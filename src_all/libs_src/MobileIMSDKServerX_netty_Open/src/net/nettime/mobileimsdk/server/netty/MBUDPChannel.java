/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v3.x Netty版) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * MBUDPChannel.java at 2017-12-9 11:24:33, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.nettime.mobileimsdk.server.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;

public class MBUDPChannel extends AbstractChannel 
{
	protected final ChannelMetadata metadata = new ChannelMetadata(false);
	protected final DefaultChannelConfig config = new DefaultChannelConfig(this);
	
	private final ConcurrentLinkedQueue<ByteBuf> buffers = new ConcurrentLinkedQueue<ByteBuf>();
	
	protected final MBUDPServerChannel serverchannel;
	protected final InetSocketAddress remote;
	
	private volatile boolean open = true;
	
	private boolean reading = false;
	
	protected MBUDPChannel(MBUDPServerChannel serverchannel, InetSocketAddress remote) 
	{
		super(serverchannel);
		this.serverchannel = serverchannel;
		this.remote = remote;
	}

	@Override
	public ChannelMetadata metadata()
	{
		return metadata;
	}

	@Override
	public ChannelConfig config()
	{
		return config;
	}

	@Override
	public boolean isActive()
	{
		return open;
	}

	@Override
	public boolean isOpen() 
	{
		return isActive();
	}

	@Override
	protected void doClose() throws Exception 
	{
		open = false;
		serverchannel.removeChannel(this);
	}

	@Override
	protected void doDisconnect() throws Exception
	{
		doClose();
	}

	protected void addBuffer(ByteBuf buffer) 
	{
		buffers.add(buffer);
	}

	@Override
	protected void doBeginRead() throws Exception
	{
		if (reading)
			return;
		
		reading = true;
		
		try 
		{
			ByteBuf buffer = null;
			while ((buffer = buffers.poll()) != null) 
			{
				pipeline().fireChannelRead(buffer);
			}
			pipeline().fireChannelReadComplete();
		} 
		finally 
		{
			reading = false;
		}
	}

	@Override
	protected void doWrite(ChannelOutboundBuffer buffer) throws Exception 
	{
		final RecyclableArrayList list = RecyclableArrayList.newInstance();
		boolean freeList = true;
		
		try 
		{
			ByteBuf buf = null;
			while ((buf = (ByteBuf) buffer.current()) != null) 
			{
				list.add(buf.retain());
				buffer.remove();
			}
			
			freeList = false;
		} 
		finally 
		{
			if (freeList)
			{
				for (Object obj : list) 
				{
					ReferenceCountUtil.safeRelease(obj);
				}
				
				list.recycle();
			}
		}
		
		serverchannel.eventLoop().execute(new Runnable() {
			@Override
			public void run() 
			{
				try 
				{
					for (Object buf : list) 
					{
						serverchannel.unsafe()
							.write(new DatagramPacket((ByteBuf) buf, remote), voidPromise());
					}
					
					serverchannel.unsafe().flush();
				} 
				finally 
				{
					list.recycle();
				}
			}
		});
	}

	@Override
	protected boolean isCompatible(EventLoop eventloop) 
	{
		return eventloop instanceof DefaultEventLoop;
	}

	@Override
	protected AbstractUnsafe newUnsafe()
	{
		return new UdpChannelUnsafe();
	}

	@Override
	protected SocketAddress localAddress0()
	{
		return serverchannel.localAddress0();
	}

	@Override
	protected SocketAddress remoteAddress0() 
	{
		return remote;
	}

	@Override
	protected void doBind(SocketAddress addr) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	private class UdpChannelUnsafe extends AbstractUnsafe 
	{
		@Override
		public void connect(SocketAddress addr1, SocketAddress addr2, ChannelPromise pr)
		{
			throw new UnsupportedOperationException();
		}
	}
}
