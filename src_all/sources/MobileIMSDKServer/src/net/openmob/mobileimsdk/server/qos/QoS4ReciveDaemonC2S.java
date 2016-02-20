/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * QoS4ReciveDaemonC2S.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.qos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.Timer;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QoS4ReciveDaemonC2S implements ActionListener
{
	private static Logger logger = LoggerFactory.getLogger(QoS4ReciveDaemonC2S.class);

	public static boolean DEBUG = false;
	public static final int CHECH_INTERVAL = 300000;
	public static final int MESSAGES_VALID_TIME = 600000;
	
	private ConcurrentHashMap<String, Long> recievedMessages = new ConcurrentHashMap<String, Long>();
	private Timer timer = null;
	private Runnable runnable = null;
	private boolean _excuting = false;

	private static QoS4ReciveDaemonC2S instance = null;

	public static QoS4ReciveDaemonC2S getInstance()
	{
		if (instance == null) {
			instance = new QoS4ReciveDaemonC2S();
		}

		return instance;
	}

	public QoS4ReciveDaemonC2S()
	{
		init();
	}

	private void init()
	{
		this.timer = new Timer(CHECH_INTERVAL, this);
		this.runnable = new Runnable()
		{
			public void run()
			{
				// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
				// 次还没有运行完的情况下又重复过劲行，从而出现无法预知的错误
				if (!QoS4ReciveDaemonC2S.this._excuting)
				{
					QoS4ReciveDaemonC2S.this._excuting = true;

					if (QoS4ReciveDaemonC2S.DEBUG) {
						QoS4ReciveDaemonC2S.logger.debug("【IMCORE】【QoS接收方】++++++++++ START 暂存处理线程正在运行中，当前长度" + QoS4ReciveDaemonC2S.this.recievedMessages.size() + ".");
					}

					for (String key : QoS4ReciveDaemonC2S.this.recievedMessages.keySet())
					{
						long delta = System.currentTimeMillis() - ((Long)QoS4ReciveDaemonC2S.this.recievedMessages.get(key)).longValue();

						if (delta < MESSAGES_VALID_TIME)
							continue;
						if (QoS4ReciveDaemonC2S.DEBUG)
							QoS4ReciveDaemonC2S.logger.debug("【IMCORE】【QoS接收方】指纹为" + key + "的包已生存" + delta + 
									"ms(最大允许" + MESSAGES_VALID_TIME + "ms), 马上将删除之.");
						QoS4ReciveDaemonC2S.this.recievedMessages.remove(key);
					}
				}

				if (QoS4ReciveDaemonC2S.DEBUG) {
					QoS4ReciveDaemonC2S.logger.debug("【IMCORE】【QoS接收方】++++++++++ END 暂存处理线程正在运行中，当前长度" + QoS4ReciveDaemonC2S.this.recievedMessages.size() + ".");
				}

				QoS4ReciveDaemonC2S.this._excuting = false;
			}
		};
	}

	public void actionPerformed(ActionEvent e)
	{
		this.runnable.run();
	}

	public void startup()
	{
		stop();

		if ((this.recievedMessages != null) && (this.recievedMessages.size() > 0))
		{
			for (String key : this.recievedMessages.keySet())
			{
				putImpl(key);
			}

		}

		this.timer.start();
	}

	public void stop()
	{
		if (this.timer.isRunning())
			this.timer.stop();
	}

	public boolean isRunning()
	{
		return this.timer.isRunning();
	}

	public void addRecieved(Protocal p)
	{
		if ((p != null) && (p.isQoS()))
			addRecieved(p.getFp());
	}

	public void addRecieved(String fingerPrintOfProtocal)
	{
		if (fingerPrintOfProtocal == null)
		{
			logger.debug("【IMCORE】无效的 fingerPrintOfProtocal==null!");
			return;
		}

		if (this.recievedMessages.containsKey(fingerPrintOfProtocal)) {
			logger.debug("【IMCORE】【QoS接收方】指纹为" + fingerPrintOfProtocal + 
					"的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.");
		}

		putImpl(fingerPrintOfProtocal);
	}

	private void putImpl(String fingerPrintOfProtocal)
	{
		if (fingerPrintOfProtocal != null)
			this.recievedMessages.put(fingerPrintOfProtocal, Long.valueOf(System.currentTimeMillis()));
	}

	public boolean hasRecieved(String fingerPrintOfProtocal)
	{
		return this.recievedMessages.containsKey(fingerPrintOfProtocal);
	}

	public int size()
	{
		return this.recievedMessages.size();
	}
}