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
 * MBWebsocketClientInboundHandler.java at 2021-6-29 10:15:35, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.network.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.ReadTimeoutException;
import net.x52im.mobileimsdk.server.ServerCoreHandler;
import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.network.tcp.MBTCPClientInboundHandler;
import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.utils.ServerToolKits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBWebsocketClientInboundHandler  extends SimpleChannelInboundHandler<WebSocketFrame>
{
	private static Logger logger = LoggerFactory.getLogger(MBTCPClientInboundHandler.class); 
	private ServerCoreHandler serverCoreHandler = null;
	
	public MBWebsocketClientInboundHandler(ServerCoreHandler serverCoreHandler)
	{
		this.serverCoreHandler = serverCoreHandler;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		try{
			if(e instanceof ReadTimeoutException){
				logger.info("[IMCORE-ws]客户端{}的会话已超时失效，很可能是对方非正常通出或网络故障" +
						"，即将以会话异常的方式执行关闭流程 ...", ServerToolKits.clientInfoToString(ctx.channel()));
			}
			serverCoreHandler.exceptionCaught(ctx.channel(), e);
		}catch (Exception e2){
			logger.warn(e2.getMessage(), e);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		Gateway.setSocketType(ctx.channel(), Gateway.SOCKET_TYPE_WEBSOCKET);
		serverCoreHandler.sessionCreated(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		Gateway.removeSocketType(ctx.channel());
		serverCoreHandler.sessionClosed(ctx.channel());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
        	String frameContent = ((TextWebSocketFrame) frame).text();
        	if(frameContent != null){
            	Protocal pFromClient = ServerToolKits.toProtocal(frameContent);
        		serverCoreHandler.messageReceived(ctx.channel(), pFromClient);
        	}
        	else
        		throw new UnsupportedOperationException("不支持的 frame content (is null!!)");
        }
        else 
        	throw new UnsupportedOperationException("不支持的 frame type: " + frame.getClass().getName());
	}
}
