/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.4 Project. 
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
 * ServerLauncherImpl.java at 2023-9-22 11:44:58, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.demo;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.IOException;
import java.io.InputStream;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static Logger logger = LoggerFactory.getLogger(ServerLauncherImpl.class);  
	
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
		
		SslContext sslContext = createSslContext();
		// 开启TCP协议的SSL/TLS加密传输（请确保客户端也已开发SSL）
//		GatewayTCP.sslContext = sslContext;
		// 开启WebSocket协议的SSL/TLS加密传输（请确保SSL证书是正规CA签发，否则浏览器是不允许的）
//		GatewayWebsocket.sslContext = sslContext;
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
	
	/**
	 * 创建SslContext对象，用于开启SSL/TLS加密传输。
	 * 
	 * @return 如果成功创建则返回SslContext对象，否则返回null
	 */
	private static SslContext createSslContext()
	{		
		try {
			/** 示例 1：使用证书（证书位于绝对路径）*/
//			// 证书文件
//			File certChainFile = new File("c:/certs/netty-cert2.crt");
//			// 证书文件
//			File keyFile = new File("c:/certs/netty-key2.pk8");
//			// 私钥密码（注意：Netty只支持.pk8格式，如何生成，见JackJiang文章：）
//			String keyPassword = "123456";
//			// 生成SslContext对象（为了方便理解，此处使用的是单向认证）
//			SslContext sslCtx = SslContextBuilder.forServer(certChainFile, keyFile, keyPassword).clientAuth(ClientAuth.NONE).build();
				
			/** 示例 2：使用证书（证书位于相对路径）*/
			// TODO: 注意：请使用自已的证书，Demo中带的证书为自签名证书且已绑定域名，不安全！！！
			// 证书文件
			InputStream certChainFile = ServerLauncherImpl.class.getResourceAsStream("certs/netty-cert2.crt");
			// 私钥文件（注意：Netty只支持.pk8格式，如何生成，见JackJiang文章：）
			InputStream keyFile = ServerLauncherImpl.class.getResourceAsStream("certs/netty-key2.pk8");
			// 私钥密码（注意：Netty只支持.pk8格式，如何生成，见JackJiang文章：）
			String keyPassword = "123456";
			// 生成SslContext对象（为了方便理解，此处使用的是单向认证）
			SslContext sslCtx = SslContextBuilder.forServer(certChainFile, keyFile, keyPassword).clientAuth(ClientAuth.NONE).build();
				
			/** 示例 3：使用Netty自带的自签名证书（建议该证书仅用于测试使用）*/
//			SelfSignedCertificate ssc = new SelfSignedCertificate();
//			SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
				
			return sslCtx;
		} catch (Exception e) {
		 	logger.warn("createSslContext()时出错了，原因："+e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Demo程序主入口函数。
	 * 
	 * @param args
	 * @throws Exception
	 */
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
