/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * QoS4SendDaemon.java at 2016-2-20 11:25:50, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.core;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class QoS4SendDaemon
{
	private static final String TAG = QoS4SendDaemon.class.getSimpleName();

	// 并发Hash，因为本类中可能存在不同的线程同时remove或遍历之
	private ConcurrentHashMap<String, Protocal> sentMessages = new ConcurrentHashMap<String, Protocal>();
	// 关发Hash，因为本类中可能存在不同的线程同时remove或遍历之
	private ConcurrentHashMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap<String, Long>();

	public static final int CHECH_INTERVAL = 5000;
	public static final int MESSAGES_JUST$NOW_TIME = 3000;
	public static final int QOS_TRY_COUNT = 3;

	private Handler handler = null;
	private Runnable runnable = null;
	private boolean running = false;
	private boolean _excuting = false;
	private Context context = null;

	private static QoS4SendDaemon instance = null;

	public static QoS4SendDaemon getInstance(Context context)
	{
		if (instance == null) {
			instance = new QoS4SendDaemon(context);
		}
		return instance;
	}

	private QoS4SendDaemon(Context context)
	{
		this.context = context;

		init();
	}

	private void init()
	{
		this.handler = new Handler();
		this.runnable = new Runnable()
		{
			public void run()
			{
				// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
				// 次还没有运行完的情况下又重复执行，从而出现无法预知的错误
				if (!QoS4SendDaemon.this._excuting)
				{
					new AsyncTask()
					{
						private ArrayList<Protocal> lostMessages = new ArrayList();

						protected ArrayList<Protocal> doInBackground(Object[] params)
						{
							QoS4SendDaemon.this._excuting = true;
							try
							{
								if (ClientCoreSDK.DEBUG) {
									Log.d(QoS4SendDaemon.TAG
											, "【IMCORE】【QoS】=========== 消息发送质量保证线程运行中" +
													", 当前需要处理的列表长度为" 
													+ QoS4SendDaemon.this.sentMessages.size() + "...");
								}

								for (String key : QoS4SendDaemon.this.sentMessages.keySet())
								{
									Protocal p = (Protocal)QoS4SendDaemon.this.sentMessages.get(key);
									if ((p != null) && (p.isQoS()))
									{
										if (p.getRetryCount() >= QOS_TRY_COUNT)
										{
											if (ClientCoreSDK.DEBUG) {
												Log.d(QoS4SendDaemon.TAG
														, "【IMCORE】【QoS】指纹为" + p.getFp() + 
														"的消息包重传次数已达" + p.getRetryCount() + "(最多" + QOS_TRY_COUNT + "次)上限，将判定为丢包！");
											}

											this.lostMessages.add((Protocal)p.clone());
											QoS4SendDaemon.this.remove(p.getFp());
										}
										else
										{
											long delta = System.currentTimeMillis() - ((Long)QoS4SendDaemon.this.sendMessagesTimestamp.get(key)).longValue();

											if (delta <= MESSAGES_JUST$NOW_TIME)
											{
												if (ClientCoreSDK.DEBUG) {
													Log.w(QoS4SendDaemon.TAG, "【IMCORE】【QoS】指纹为"
															+ key + "的包距\"刚刚\"发出才" + delta 
															+ "ms(<=" + MESSAGES_JUST$NOW_TIME 
															+ "ms将被认定是\"刚刚\"), 本次不需要重传哦.");
												}
											}
											else
											{
												new LocalUDPDataSender.SendCommonDataAsync(QoS4SendDaemon.this.context, p)
												{
													protected void onPostExecute(Integer code)
													{
														if (code.intValue() == 0)
														{
															this.p.increaseRetryCount();

															if (ClientCoreSDK.DEBUG)
																Log.d(QoS4SendDaemon.TAG, "【IMCORE】【QoS】指纹为" + this.p.getFp() + 
																		"的消息包已成功进行重传，此次之后重传次数已达" + 
																		this.p.getRetryCount() + "(最多" + QOS_TRY_COUNT + "次).");
														}
														else
														{
															Log.w(QoS4SendDaemon.TAG, "【IMCORE】【QoS】指纹为" + this.p.getFp() + 
																	"的消息包重传失败，它的重传次数之前已累计为" + 
																	this.p.getRetryCount() + "(最多" + QOS_TRY_COUNT + "次).");
														}
													}
												}
												.execute(new Object[0]);
											}
										}
									}
									else
									{
										QoS4SendDaemon.this.remove(key);
									}
								}
							}
							catch (Exception eee)
							{
								Log.w(QoS4SendDaemon.TAG, "【IMCORE】【QoS】消息发送质量保证线程运行时发生异常," + eee.getMessage(), eee);
							}

							return this.lostMessages;
						}

						protected void onPostExecute(ArrayList<Protocal> al)
						{
							if ((al != null) && (al.size() > 0))
							{
								QoS4SendDaemon.this.notifyMessageLost(al);
							}

							QoS4SendDaemon.this._excuting = false;
							QoS4SendDaemon.this.handler.postDelayed(QoS4SendDaemon.this.runnable, 5000L);
						}
					}
					.execute(new Object[0]);
				}
			}
		};
	}

	protected void notifyMessageLost(ArrayList<Protocal> lostMessages)
	{
		if (ClientCoreSDK.getInstance().getMessageQoSEvent() != null)
			ClientCoreSDK.getInstance().getMessageQoSEvent().messagesLost(lostMessages);
	}

	public void startup(boolean immediately)
	{
		stop();

		this.handler.postDelayed(this.runnable, immediately ? 0 : CHECH_INTERVAL);
		this.running = true;
	}

	public void stop()
	{
		this.handler.removeCallbacks(this.runnable);
		this.running = false;
	}

	public boolean isRunning()
	{
		return this.running;
	}

	boolean exist(String fingerPrint)
	{
		return this.sentMessages.get(fingerPrint) != null;
	}

	public void put(Protocal p)
	{
		if (p == null)
		{
			Log.w(TAG, "Invalid arg p==null.");
			return;
		}
		if (p.getFp() == null)
		{
			Log.w(TAG, "Invalid arg p.getFp() == null.");
			return;
		}

		if (!p.isQoS())
		{
			Log.w(TAG, "This protocal is not QoS pkg, ignore it!");
			return;
		}

		if (this.sentMessages.get(p.getFp()) != null) {
			Log.w(TAG, "【IMCORE】【QoS】指纹为" + p.getFp() + "的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");
		}

		// save it
		sentMessages.put(p.getFp(), p);
		// 同时保存时间戳
		sendMessagesTimestamp.put(p.getFp(), System.currentTimeMillis());
	}

	public void remove(final String fingerPrint)
	{
		new AsyncTask(){
			@Override
			protected Object doInBackground(Object... params)
			{
				sendMessagesTimestamp.remove(fingerPrint);
				return sentMessages.remove(fingerPrint);
			}

			protected void onPostExecute(Object result) 
			{
				Log.w(TAG, "【IMCORE】【QoS】指纹为"+fingerPrint+"的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数="
						+(result != null?((Protocal)result).getRetryCount():"none呵呵."));
			}
		}.execute();
	}

	public int size()
	{
		return this.sentMessages.size();
	}
}