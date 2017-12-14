package net.nettime.mobileimsdk.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.openmob.mobileimsdk.server.ServerCoreHandler;
import net.openmob.mobileimsdk.server.ServerLauncher;

public class MBUDPClientInboundHandler extends SimpleChannelInboundHandler<ByteBuf>
{
	private static Logger logger = LoggerFactory.getLogger(MBUDPClientInboundHandler.class); 
	
	private ServerCoreHandler serverCoreHandler = null;
	
	public MBUDPClientInboundHandler(ServerCoreHandler serverCoreHandler)
	{
		this.serverCoreHandler = serverCoreHandler;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		try{
			serverCoreHandler.exceptionCaught(ctx.channel(), e);
		}catch (Exception e2){
			logger.warn(e2.getMessage(), e);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		serverCoreHandler.sessionCreated(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		serverCoreHandler.sessionClosed(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bytebuf) throws Exception {
		serverCoreHandler.messageReceived(ctx.channel(), bytebuf);
	}
}