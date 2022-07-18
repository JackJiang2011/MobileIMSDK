/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK4j_udp (MobileIMSDK4j v6.1 UDP版) Project. 
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
 * QoS4ReciveDaemon.java at 2021-8-4 21:37:03, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import net.x52im.mobileimsdk.java.ClientCoreSDK;
import net.x52im.mobileimsdk.java.utils.Log;
import net.x52im.mobileimsdk.server.protocal.Protocal;

public class QoS4ReciveDaemon {
	private final static String TAG = QoS4ReciveDaemon.class.getSimpleName();

	private static QoS4ReciveDaemon instance = null;

	public final static int CHECH_INTERVAL = 5 * 60 * 1000; // 5分钟
	public final static int MESSAGES_VALID_TIME = 10 * 60 * 1000;// 10分钟

	private ConcurrentHashMap<String, Long> recievedMessages = new ConcurrentHashMap<String, Long>();
	private boolean running = false;
	private boolean _excuting = false;
	private Timer timer = null;

	public static QoS4ReciveDaemon getInstance() {
		if (instance == null)
			instance = new QoS4ReciveDaemon();

		return instance;
	}

	public QoS4ReciveDaemon() {
		init();
	}

	private void init() {
		timer = new Timer(CHECH_INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
	}

	public void run() {
		if (!_excuting) {
			_excuting = true;

			if (ClientCoreSDK.DEBUG)
				Log.d(TAG, "【IMCORE-UDP】【QoS接收方】+++++ START 暂存处理线程正在运行中，当前长度" + recievedMessages.size() + ".");

			for (String key : recievedMessages.keySet()) {
				Long recievedTime = recievedMessages.get(key);
				long delta = System.currentTimeMillis() - (recievedTime == null ? 0 : recievedTime);
				if (delta >= MESSAGES_VALID_TIME) {
					if (ClientCoreSDK.DEBUG)
						Log.d(TAG, "【IMCORE-UDP】【QoS接收方】指纹为" + key + "的包已生存" + delta + "ms(最大允许" + MESSAGES_VALID_TIME + "ms), 马上将删除之.");
					recievedMessages.remove(key);
				}
			}
		}

		if (ClientCoreSDK.DEBUG)
			Log.d(TAG, "【IMCORE-UDP】【QoS接收方】+++++ END 暂存处理线程正在运行中，当前长度" + recievedMessages.size() + ".");

		_excuting = false;
	}

	public void startup(boolean immediately) {
		stop();

		if (recievedMessages != null && recievedMessages.size() > 0) {
			for (String key : recievedMessages.keySet()) {
				putImpl(key);
			}
		}

		if (immediately)
			timer.setInitialDelay(0);
		else
			timer.setInitialDelay(CHECH_INTERVAL);
		timer.start();

		running = true;
	}

	public void stop() {
		if (timer != null)
			timer.stop();
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void addRecieved(Protocal p) {
		if (p != null && p.isQoS())
			addRecieved(p.getFp());
	}

	public void addRecieved(String fingerPrintOfProtocal) {
		if (fingerPrintOfProtocal == null) {
			Log.w(TAG, "【IMCORE-UDP】无效的 fingerPrintOfProtocal==null!");
			return;
		}

		if (recievedMessages.containsKey(fingerPrintOfProtocal))
			Log.w(TAG, "【IMCORE-UDP】【QoS接收方】指纹为" + fingerPrintOfProtocal+ "的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.");

		putImpl(fingerPrintOfProtocal);
	}

	private void putImpl(String fingerPrintOfProtocal) {
		if (fingerPrintOfProtocal != null)
			recievedMessages.put(fingerPrintOfProtocal, System.currentTimeMillis());
	}

	public boolean hasRecieved(String fingerPrintOfProtocal) {
		return recievedMessages.containsKey(fingerPrintOfProtocal);
	}

	public void clear() {
		this.recievedMessages.clear();
	}

	public int size() {
		return recievedMessages.size();
	}
}
