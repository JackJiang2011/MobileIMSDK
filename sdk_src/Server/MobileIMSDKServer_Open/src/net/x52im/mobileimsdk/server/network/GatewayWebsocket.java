/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.4 Project. 
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
 * GatewayWebsocket.java at 2023-9-21 15:24:55, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.x52im.mobileimsdk.server.ServerCoreHandler;
import net.x52im.mobileimsdk.server.network.websocket.MBWebsocketClientInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayWebsocket extends Gateway
{
	private static Logger logger = LoggerFactory.getLogger(GatewayWebsocket.class); 
	
	public static String WEBSOCKET_PATH = "/websocket";
	public static boolean SSL = false;
    public static int PORT = 3000;
    public static int SESION_RECYCLER_EXPIRE = 20;
    
    public static SslContext sslContext = null;
    
	protected final EventLoopGroup __bossGroup4Netty = new NioEventLoopGroup(1);
 	protected final EventLoopGroup __workerGroup4Netty = new NioEventLoopGroup();
 	protected Channel __serverChannel4Netty = null;
 	protected ServerBootstrap bootstrap = null;
 	
 	@Override
 	public void init(ServerCoreHandler serverCoreHandler)
    {
        SslContext sslCtx = null;
        try{
        	if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }
		}
		catch (Exception e){
			logger.error("[IMCORE-ws] SSL证书准备失败：", e);
		}
        
        bootstrap = new ServerBootstrap()
			.group(__bossGroup4Netty, __workerGroup4Netty)
			.channel(NioServerSocketChannel.class)
			.childHandler(initChildChannelHandler(serverCoreHandler));
    }
	
 	@Override
 	public void bind() throws Exception
    {
        ChannelFuture cf = bootstrap.bind(PORT).sync();
        if (cf.isSuccess()) {
        	logger.info("[IMCORE-ws] 基于MobileIMSDK的WebSocket服务绑定端口"+PORT+"成功 √ "+(isSsl()?"(已开启SSL/TLS加密传输)":""));
        } else{
        	logger.info("[IMCORE-ws] 基于MobileIMSDK的WebSocket服务绑定端口"+PORT+"失败 ×");
        }
        
		__serverChannel4Netty = cf.channel();
		__serverChannel4Netty.closeFuture().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				__bossGroup4Netty.shutdownGracefully();
				__workerGroup4Netty.shutdownGracefully();
			}
		});
		
		logger.info("[IMCORE-ws] .... continue ...");
		logger.info("[IMCORE-ws] 基于MobileIMSDK的WebSocket服务正在端口"+ PORT +"上监听中"+(SSL?"(已开启SSL)":"")+"...");
    }
	
 	@Override
	public void shutdown()
	{
    	if (__serverChannel4Netty != null) 
    		__serverChannel4Netty.close();
	}
 	
    protected ChannelHandler initChildChannelHandler(final ServerCoreHandler serverCoreHandler)
	{
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();   

				if(sslContext != null) {
					pipeline.addLast(sslContext.newHandler(channel.alloc()));
				}
				
		        pipeline.addLast(new HttpServerCodec());
		        pipeline.addLast(new HttpObjectAggregator(65536));
		        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
				pipeline.addLast(new ReadTimeoutHandler(SESION_RECYCLER_EXPIRE));
		        pipeline.addLast(new MBWebsocketClientInboundHandler(serverCoreHandler));
			}
		};
	}
    
    public static boolean isSsl() 
    {
    	return sslContext != null;
    }
}
