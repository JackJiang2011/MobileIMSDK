/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * Archived at 2015-11-27 14:02:01, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.qos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import net.openmob.mobileimsdk.server.ServerLauncher;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QoS4SendDaemonS2C
{
	private static Logger logger = LoggerFactory.getLogger(QoS4SendDaemonS2C.class);

	public static boolean DEBUG = false;

	public static final int CHECH_INTERVAL = 5000;
	public static final int MESSAGES_JUST$NOW_TIME = 2000;
	public static final int QOS_TRY_COUNT = 1;

	private ServerLauncher serverLauncher = null;
	private ConcurrentHashMap<String, Protocal> sentMessages = new ConcurrentHashMap<String, Protocal>();
	private ConcurrentHashMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap<String, Long>();

	private boolean running = false;
	private boolean _excuting = false;
	private Timer timer = null;

	private static QoS4SendDaemonS2C instance = null;

	public static QoS4SendDaemonS2C getInstance()
	{
		if (instance == null)
			instance = new QoS4SendDaemonS2C();
		return instance;
	}

	private QoS4SendDaemonS2C()
	{
		init();
	}

	private void init()
	{
		this.timer = new Timer(CHECH_INTERVAL, new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				QoS4SendDaemonS2C.this.run();
			}
		});
	}

	public void run()
	{
		// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
		// 次还没有运行完的情况下又重复执行，从而出现无法预知的错误
		if (!this._excuting)
		{
			ArrayList<Protocal> lostMessages = new ArrayList<Protocal>();
			this._excuting = true;
			try
			{
				if (DEBUG) {
					logger.debug("【IMCORE】【QoS发送方】=========== 消息发送质量保证线程运行中, 当前需要处理的列表长度为" + this.sentMessages.size() + "...");
				}

				for (String key : this.sentMessages.keySet())
				{
					Protocal p = (Protocal)this.sentMessages.get(key);
					if ((p != null) && (p.isQoS()))
					{
						if (p.getRetryCount() >= QOS_TRY_COUNT)
						{
							if (DEBUG) {
								logger.debug("【IMCORE】【QoS发送方】指纹为" + p.getFp() + 
										"的消息包重传次数已达" + p.getRetryCount() + "(最多" + QOS_TRY_COUNT + "次)上限，将判定为丢包！");
							}

							lostMessages.add((Protocal)p.clone());

							remove(p.getFp());
						}
						else
						{
							//### 2015104 Bug Fix: 解决了无线网络延较大时，刚刚发出的消息在其应答包还在途中时被错误地进行重传
							long delta = System.currentTimeMillis() - sendMessagesTimestamp.get(key);
							// 该消息包是“刚刚”发出的，本次不需要重传它哦
							if(delta <= MESSAGES_JUST$NOW_TIME)
							{
								if(DEBUG)
									logger.warn("【IMCORE】【QoS发送方】指纹为"+key+"的包距\"刚刚\"发出才"+delta
										+"ms(<="+MESSAGES_JUST$NOW_TIME+"ms将被认定是\"刚刚\"), 本次不需要重传哦.");
							}
							//### 2015103 Bug Fix END
							else
							{
								boolean sendOK = ServerLauncher.sendData(p);

								p.increaseRetryCount();

								if (sendOK)
								{
									if (DEBUG) {
										logger.debug("【IMCORE】【QoS发送方】指纹为" + p.getFp() + 
												"的消息包已成功进行重传，此次之后重传次数已达" + 
												p.getRetryCount() + "(最多" + 1 + "次).");
									}

								}
								else if (DEBUG) {
									logger.warn("【IMCORE】【QoS发送方】指纹为" + p.getFp() + 
											"的消息包重传失败，它的重传次数之前已累计为" + 
											p.getRetryCount() + "(最多" + 1 + "次).");
								}
							}

						}
					}
					else
					{
						remove(key);
					}
				}
			}
			catch (Exception eee)
			{
				if (DEBUG) {
					logger.warn("【IMCORE】【QoS发送方】消息发送质量保证线程运行时发生异常," + eee.getMessage(), eee);
				}
			}
			if ((lostMessages != null) && (lostMessages.size() > 0))
			{
				notifyMessageLost(lostMessages);
			}

			this._excuting = false;
		}
	}

	protected void notifyMessageLost(ArrayList<Protocal> lostMessages)
	{
		if ((this.serverLauncher != null) && (this.serverLauncher.getServerMessageQoSEventListener() != null))
			this.serverLauncher.getServerMessageQoSEventListener().messagesLost(lostMessages);
	}

	public QoS4SendDaemonS2C startup(boolean immediately)
	{
		stop();

		if (immediately)
			this.timer.setInitialDelay(0);
		else
			this.timer.setInitialDelay(CHECH_INTERVAL);
		
		this.timer.start();
		this.running = true;

		return this;
	}

	public void stop()
	{
		if (this.timer != null) {
			this.timer.stop();
		}

		this.running = false;
	}

	public boolean isRunning()
	{
		return this.running;
	}

	public boolean exist(String fingerPrint)
	{
		return this.sentMessages.get(fingerPrint) != null;
	}

	public void put(Protocal p)
	{
		if (p == null)
		{
			if (DEBUG)
				logger.warn("Invalid arg p==null.");
			return;
		}
		if (p.getFp() == null)
		{
			if (DEBUG)
				logger.warn("Invalid arg p.getFp() == null.");
			return;
		}

		if (!p.isQoS())
		{
			if (DEBUG)
				logger.warn("This protocal is not QoS pkg, ignore it!");
			return;
		}

		if (this.sentMessages.get(p.getFp()) != null)
		{
			if (DEBUG) {
				logger.warn("【IMCORE】【QoS发送方】指纹为" + p.getFp() + "的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");
			}
		}

		// save it
		sentMessages.put(p.getFp(), p);
		// 同时保存时间戳
		sendMessagesTimestamp.put(p.getFp(), System.currentTimeMillis());
	}

	public void remove(String fingerPrint)
	{
		//### 20151129 Bug Fix: 解决了之前错误地在服务端实现本remove方法时
		//	使用了SwingWorker而导致一段时间后一定几率下整个Timer不能正常工作了（OOM）
		try
		{
			// remove it
			sendMessagesTimestamp.remove(fingerPrint);
			Object result = sentMessages.remove(fingerPrint);
			if(DEBUG)
				logger.warn("【IMCORE】【QoS发送方】指纹为"+fingerPrint+"的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数="
						+(result != null?((Protocal)result).getRetryCount():"none呵呵."));
		}
		catch (Exception e)
		{
			if(DEBUG)
				logger.warn("【IMCORE】【QoS发送方】remove(fingerPrint)时出错了：", e);
		}
		//### 20151129 Bug Fix END 
	}

	public int size()
	{
		return this.sentMessages.size();
	}

	public void setServerLauncher(ServerLauncher serverLauncher)
	{
		this.serverLauncher = serverLauncher;
	}
}