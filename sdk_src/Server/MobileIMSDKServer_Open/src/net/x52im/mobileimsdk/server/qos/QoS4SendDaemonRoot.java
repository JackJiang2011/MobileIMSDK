/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.x Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“【即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * QoS4SendDaemonRoot.java at 2021-6-29 10:15:35, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.qos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import net.x52im.mobileimsdk.server.ServerLauncher;
import net.x52im.mobileimsdk.server.network.MBObserver;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.utils.LocalSendHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QoS4SendDaemonRoot
{
	private static Logger logger = LoggerFactory.getLogger(QoS4SendDaemonRoot.class);  
	
	private boolean DEBUG = false;
	private ServerLauncher serverLauncher = null;
	private ConcurrentSkipListMap<String, Protocal> sentMessages = new ConcurrentSkipListMap<String, Protocal>();
	private ConcurrentMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap<String, Long>();
	private int CHECH_INTERVAL = 5000;
	private int MESSAGES_JUST$NOW_TIME = 2 * 1000;
	private int QOS_TRY_COUNT = 1;
	private boolean _excuting = false;
	private Timer timer = null;
	private String debugTag = "";
	
	public QoS4SendDaemonRoot(int CHECH_INTERVAL
			, int MESSAGES_JUST$NOW_TIME
			, int QOS_TRY_COUNT
			, boolean DEBUG, String debugTag)
	{
		if(CHECH_INTERVAL > 0)
			this.CHECH_INTERVAL = CHECH_INTERVAL;
		if(MESSAGES_JUST$NOW_TIME > 0)
			this.MESSAGES_JUST$NOW_TIME = MESSAGES_JUST$NOW_TIME;
		if(QOS_TRY_COUNT >= 0)
			this.QOS_TRY_COUNT = QOS_TRY_COUNT;
		this.DEBUG = DEBUG;
		this.debugTag = debugTag;
	}
	
	private void doTaskOnece()
	{
		if(!_excuting)
		{
			ArrayList<Protocal> lostMessages = new ArrayList<Protocal>();
			_excuting = true;
			try
			{
				if(DEBUG && sentMessages.size() > 0)
					logger.debug("【IMCORE"+this.debugTag+"】【QoS发送方】====== 消息发送质量保证线程运行中, 当前需要处理的列表长度为"+sentMessages.size()+"...");

				//** 遍历HashMap方法二（在大数据量情况下，方法二的性能要5倍优于方法一）
				Iterator<Entry<String, Protocal>> entryIt = sentMessages.entrySet().iterator();  
			    while(entryIt.hasNext())
			    {  
			        Entry<String, Protocal> entry = entryIt.next();  
			        String key = entry.getKey();  
			        final Protocal p = entry.getValue();
			        
					if(p != null && p.isQoS())
					{
						if(p.getRetryCount() >= QOS_TRY_COUNT)
						{
							if(DEBUG)
								logger.debug("【IMCORE"+this.debugTag+"】【QoS发送方】指纹为"+p.getFp()
										+"的消息包重传次数已达"+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次)上限，将判定为丢包！");

							lostMessages.add((Protocal)p.clone());
							remove(p.getFp());
						}
						else
						{
							//### 2015104 Bug Fix: 解决了无线网络延较大时，刚刚发出的消息在其应答包还在途中时被错误地进行重传
							Long sendMessageTimestamp = sendMessagesTimestamp.get(key);
							long delta = System.currentTimeMillis() - (sendMessageTimestamp == null?0 : sendMessageTimestamp);
							if(delta <= MESSAGES_JUST$NOW_TIME)
							{
								if(DEBUG)
									logger.warn("【IMCORE"+this.debugTag+"】【QoS发送方】指纹为"+key+"的包距\"刚刚\"发出才"+delta
										+"ms(<="+MESSAGES_JUST$NOW_TIME+"ms将被认定是\"刚刚\"), 本次不需要重传哦.");
							}
							//### 2015103 Bug Fix END
							else
							{
								MBObserver sendResultObserver = new MBObserver(){
									@Override
									public void update(boolean sendOK, Object extraObj)
									{
										if(sendOK)
										{
											if(DEBUG)
											{
												logger.debug("【IMCORE"+debugTag+"】【QoS发送方】指纹为"+p.getFp()
														+"的消息包已成功进行重传，此次之后重传次数已达"
														+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
											}
										}
										else
										{
											if(DEBUG)
											{
												logger.warn("【IMCORE"+debugTag+"】【QoS发送方】指纹为"+p.getFp()
														+"的消息包重传失败，它的重传次数之前已累计为"
														+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
											}
										}
									}
								};
								
								LocalSendHelper.sendData(p, sendResultObserver);
								p.increaseRetryCount();
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
				if(DEBUG)
					logger.warn("【IMCORE"+this.debugTag+"】【QoS发送方】消息发送质量保证线程运行时发生异常,"+eee.getMessage(), eee);
			}

			if(lostMessages != null && lostMessages.size() > 0)
				notifyMessageLost(lostMessages);

			_excuting = false;
		}
	}
	
	protected void notifyMessageLost(ArrayList<Protocal> lostMessages)
	{
		if(serverLauncher != null && serverLauncher.getServerMessageQoSEventListener() != null)
			serverLauncher.getServerMessageQoSEventListener().messagesLost(lostMessages);
	}
	
	public QoS4SendDaemonRoot startup(boolean immediately)
	{
		stop();
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() 
		{
			@Override
			public void run()
			{
				doTaskOnece();
			}
		}
		, immediately ? 0 : CHECH_INTERVAL
		, CHECH_INTERVAL);
		
		logger.debug("【IMCORE"+this.debugTag+"】【QoS发送方】====== 消息发送质量保证线程已成功启动");
		
		return this;
	}
	
	public void stop()
	{
		if(timer != null)
		{
			try{
				timer.cancel();
			}
			finally{
				timer = null;
			}
		}
	}
	
	public boolean isRunning()
	{
		return timer != null;
	}
	
	public boolean exist(String fingerPrint)
	{
		return sentMessages.get(fingerPrint) != null;
	}
	
	public void put(Protocal p)
	{
		if(p == null)
		{
			if(DEBUG)
				logger.warn(this.debugTag+"Invalid arg p==null.");
			return;
		}
		if(p.getFp() == null)
		{
			if(DEBUG)
				logger.warn(this.debugTag+"Invalid arg p.getFp() == null.");
			return;
		}
		
		if(!p.isQoS())
		{
			if(DEBUG)
				logger.warn(this.debugTag+"This protocal is not QoS pkg, ignore it!");
			return;
		}
		
		if(sentMessages.get(p.getFp()) != null)
		{
			if(DEBUG)
				logger.warn("【IMCORE"+this.debugTag+"】【QoS发送方】指纹为"+p.getFp()+"的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");
		}
		
		sentMessages.put(p.getFp(), p);
		sendMessagesTimestamp.put(p.getFp(), System.currentTimeMillis());
	}
	
	public void remove(final String fingerPrint)
	{
		try
		{
			// remove it
			sendMessagesTimestamp.remove(fingerPrint);
			Object result = sentMessages.remove(fingerPrint);
			if(DEBUG)
				logger.warn("【IMCORE"+this.debugTag+"】【QoS发送方】指纹为"+fingerPrint+"的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数="
						+(result != null?((Protocal)result).getRetryCount():"none呵呵."));
		}
		catch (Exception e)
		{
			if(DEBUG)
				logger.warn("【IMCORE"+this.debugTag+"】【QoS发送方】remove(fingerPrint)时出错了：", e);
		}
	}
	
	public int size()
	{
		return sentMessages.size();
	}

	public void setServerLauncher(ServerLauncher serverLauncher)
	{
		this.serverLauncher = serverLauncher;
	}

	public QoS4SendDaemonRoot setDebugable(boolean debugable)
	{
		this.DEBUG = debugable;
		return this;
	}
	
	public boolean isDebugable()
	{
		return this.DEBUG;
	}
}
