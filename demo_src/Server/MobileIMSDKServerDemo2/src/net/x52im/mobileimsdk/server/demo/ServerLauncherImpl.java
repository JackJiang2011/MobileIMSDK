/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.x Project. 
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
 * ServerLauncherImpl.java at 2022-7-12 16:35:42, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.demo;

import java.io.IOException;

import net.x52im.mobileimsdk.server.ServerLauncher;
import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.network.GatewayTCP;
import net.x52im.mobileimsdk.server.network.GatewayUDP;
import net.x52im.mobileimsdk.server.network.GatewayWebsocket;
import net.x52im.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import net.x52im.mobileimsdk.server.qos.QoS4SendDaemonS2C;
import net.x52im.mobileimsdk.server.utils.ServerToolKits;
import net.x52im.mobileimsdk.server.utils.ServerToolKits.SenseModeTCP;
import net.x52im.mobileimsdk.server.utils.ServerToolKits.SenseModeWebsocket;

/**
 * IM服务的启动主类。
 * <p>
 * <b>友情提示：</b>其实MobileIMSDK的服务端并非只能以main的主类方式独立启
 * 动，你完全可以把它放到诸如java的Web工程里作为子模块运行，不会有任何问题！
 * 
 * @author Jack Jiang
 * @version 1.0
 * @since 3.1
 */
public class ServerLauncherImpl extends ServerLauncher
{
	/**
	 * 静态类方法：进行一些全局配置设置。
	 */
	static
	{
		// 设置MobileIMSDK服务端的UDP网络监听端口
		GatewayUDP.PORT       = 7901;
		// 设置MobileIMSDK服务端的TCP网络监听端口
		GatewayTCP.PORT       = 8901;
		// 设置MobileIMSDK服务端的WebSocket网络监听端口
		GatewayWebsocket.PORT = 3000;
		
		// 设置MobileIMSDK服务端仅支持UDP协议
//		ServerLauncher.supportedGateways = Gateway.SOCKET_TYPE_UDP;
		// 设置MobileIMSDK服务端仅支持TCP协议
//		ServerLauncher.supportedGateways = Gateway.SOCKET_TYPE_TCP;
		// 设置MobileIMSDK服务端仅支持WebSocket协议
//		ServerLauncher.supportedGateways = Gateway.SOCKET_TYPE_WEBSOCKET;
		// 设置MobileIMSDK服务端同时支持UDP、TCP、WebSocket三种协议
		ServerLauncher.supportedGateways = Gateway.SOCKET_TYPE_UDP | Gateway.SOCKET_TYPE_TCP | Gateway.SOCKET_TYPE_WEBSOCKET;
		
		// 开/关Demog日志的输出
		QoS4SendDaemonS2C.getInstance().setDebugable(true);
		QoS4ReciveDaemonC2S.getInstance().setDebugable(true);
		
		// 与客户端协商一致的心跳频率模式设置
//		ServerToolKits.setSenseModeUDP(SenseModeUDP.MODE_15S);
		ServerToolKits.setSenseModeTCP(SenseModeTCP.MODE_5S);
		ServerToolKits.setSenseModeWebsocket(SenseModeWebsocket.MODE_5S);
//		ServerToolKits.setSenseModeWebsocket(SenseModeWebsocket.MODE_30S);

		// 关闭与Web端的消息互通桥接器（其实SDK中默认就是false）
		ServerLauncher.bridgeEnabled = false;
		// TODO 跨服桥接器MQ的URI（本参数只在ServerLauncher.bridgeEnabled为true时有意义）
//		BridgeProcessor.IMMQ_URI = "amqp://js:19844713@192.168.0.190";
		
		// 设置最大TCP帧内容长度（不设置则默认最大是 6 * 1024字节）
//		GatewayTCP.TCP_FRAME_MAX_BODY_LENGTH = 60 * 1024;
	}
	
	/**
	 * 实例构造方法。
	 * 
	 * @throws IOException
	 */
	public ServerLauncherImpl() throws IOException
	{
		super();
	}
	
    /**
     * 初始化消息处理事件监听者.
     */
	@Override
    protected void initListeners()
    {
    	// ** 设置各种回调事件处理实现类
		this.setServerEventListener(new ServerEventListenerImpl());
		this.setServerMessageQoSEventListener(new MessageQoSEventS2CListnerImpl());
    }
	
    public static void main(String[] args) throws Exception 
    {
    	// 实例化后记得startup哦，单独startup()的目的是让调用者可以延迟决定何时真正启动IM服务
    	final ServerLauncherImpl sli = new ServerLauncherImpl();
    	
    	// 启动MobileIMSDK服务端的Demo
    	sli.startup();
    	
    	// 加一个钩子，确保在JVM退出时释放netty的资源
    	Runtime.getRuntime().addShutdownHook(new Thread() {
    		@Override
    		public void run() {
    			sli.shutdown();
    		}
    	});
    }
}
