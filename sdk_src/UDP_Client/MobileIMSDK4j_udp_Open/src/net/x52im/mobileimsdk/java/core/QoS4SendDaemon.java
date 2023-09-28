/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK4j_udp (MobileIMSDK4j v6.4 UDP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * QoS4SendDaemon.java at 2023-9-21 15:30:48, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import net.x52im.mobileimsdk.java.ClientCoreSDK;
import net.x52im.mobileimsdk.java.utils.Log;
import net.x52im.mobileimsdk.server.protocal.Protocal;

import org.jdesktop.swingworker.SwingWorker;

public class QoS4SendDaemon
{
	private final static String TAG = QoS4SendDaemon.class.getSimpleName();

	private static QoS4SendDaemon instance = null;
		
	public final static int CHECH_INTERVAL = 5000;
	public final static int MESSAGES_JUST$NOW_TIME = 3 * 1000;
	public final static int QOS_TRY_COUNT = 2;// since 3.0 (20160918): 为了降低服务端负载，本参数由原3调整为2
	
	private ConcurrentHashMap<String, Protocal> sentMessages = new ConcurrentHashMap<String, Protocal>();
	private ConcurrentHashMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap<String, Long>();
	private boolean running = false;
	private boolean _excuting = false;
	private Timer timer = null;
	
	public static QoS4SendDaemon getInstance()
	{
		if (instance == null) {
			synchronized (QoS4SendDaemon.class) {
				if (instance == null) {
					instance = new QoS4SendDaemon();
				}
			}
		}
		return instance;
	}
	
	private QoS4SendDaemon()
	{
		init();
	}
	
	private void init()
	{
		timer = new Timer(CHECH_INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				run();
			}
		});
	}
	
	public void run()
	{
		if(!_excuting)
		{
			ArrayList<Protocal> lostMessages = new ArrayList<Protocal>();
			_excuting = true;
			
			try
			{
				if(ClientCoreSDK.DEBUG)
					Log.d(TAG, "【IMCORE-UDP】【QoS】====== 消息发送质量保证线程运行中, 当前需要处理的列表长度为"+sentMessages.size()+"...");

				for(String key : sentMessages.keySet())
				{
					final Protocal p = sentMessages.get(key);
					if(p != null && p.isQoS())
					{
						if(p.getRetryCount() >= QOS_TRY_COUNT)
						{
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "【IMCORE-UDP】【QoS】指纹为"+p.getFp() +"的消息包重传次数已达"+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次)上限，将判定为丢包！");

							lostMessages.add((Protocal)p.clone());
							remove(p.getFp());
						}
						else
						{
							Long sendMessageTimestamp = sendMessagesTimestamp.get(key);
							long delta = System.currentTimeMillis() - (sendMessageTimestamp == null?0 : sendMessageTimestamp);
							if(delta <= MESSAGES_JUST$NOW_TIME)
							{
								if(ClientCoreSDK.DEBUG)
									Log.w(TAG, "【IMCORE-UDP】【QoS】指纹为"+key+"的包距\"刚刚\"发出才"+delta +"ms(<="+MESSAGES_JUST$NOW_TIME+"ms将被认定是\"刚刚\"), 本次不需要重传哦.");
							}
							else
							{
								new LocalDataSender.SendCommonDataAsync(p){
									@Override
									protected void onPostExecute(Integer code){
										if(code == 0){
											p.increaseRetryCount();

											if(ClientCoreSDK.DEBUG)
												Log.d(TAG, "【IMCORE-UDP】【QoS】指纹为"+p.getFp()+"的消息包已成功进行重传，此次之后重传次数已达"+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
										}
										else{
											Log.w(TAG, "【IMCORE-UDP】【QoS】指纹为"+p.getFp()+"的消息包重传失败，它的重传次数之前已累计为"+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
										}
									}
								}.execute();
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
				Log.w(TAG, "【IMCORE-UDP】【QoS】消息发送质量保证线程运行时发生异常,"+eee.getMessage(), eee);
			}

			if(lostMessages != null && lostMessages.size() > 0)
				notifyMessageLost(lostMessages);

			_excuting = false;
		}
	}
	
	protected void notifyMessageLost(ArrayList<Protocal> lostMessages)
	{
		if(ClientCoreSDK.getInstance().getMessageQoSEvent() != null)
		{
			ClientCoreSDK.getInstance().getMessageQoSEvent().messagesLost(lostMessages);
		}
	}
	
	public void startup(boolean immediately)
	{
		stop();
		
		if(immediately)
			timer.setInitialDelay(0);
		else
			timer.setInitialDelay(CHECH_INTERVAL);
		timer.start();
		
		running = true;
	}
	
	public void stop()
	{
		if(timer != null)
			timer.stop();
		running = false;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	boolean exist(String fingerPrint)
	{
		return sentMessages.get(fingerPrint) != null;
	}
	
	public void put(Protocal p)
	{
		if(p == null)
		{
			Log.w(TAG, "Invalid arg p==null.");
			return;
		}
		
		if(p.getFp() == null)
		{
			Log.w(TAG, "Invalid arg p.getFp() == null.");
			return;
		}
		
		if(!p.isQoS())
		{
			Log.w(TAG, "This protocal is not QoS pkg, ignore it!");
			return;
		}
		
		if(sentMessages.get(p.getFp()) != null)
			Log.w(TAG, "【IMCORE-UDP】【QoS】指纹为"+p.getFp()+"的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");
		
		sentMessages.put(p.getFp(), p);
		sendMessagesTimestamp.put(p.getFp(), System.currentTimeMillis());
	}
	
	public void remove(final String fingerPrint)
	{
		new SwingWorker<Protocal, Object>(){
			@Override
			protected Protocal doInBackground(){
				sendMessagesTimestamp.remove(fingerPrint);
				return sentMessages.remove(fingerPrint);
			}
			
			@Override
			protected void done() {
				Protocal result = null;
				try{
					result = get();
				}
				catch (Exception e){
					Log.w(TAG, e.getMessage());
				}
				
				Log.w(TAG, "【IMCORE-UDP】【QoS】指纹为"+fingerPrint+"的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数="+(result != null?((Protocal)result).getRetryCount():"none呵呵."));
		    }
		}.execute();
	}
	
	public void clear()
	{
		this.sentMessages.clear();
		this.sendMessagesTimestamp.clear();
	}
	
	public int size()
	{
		return sentMessages.size();
	}
}
