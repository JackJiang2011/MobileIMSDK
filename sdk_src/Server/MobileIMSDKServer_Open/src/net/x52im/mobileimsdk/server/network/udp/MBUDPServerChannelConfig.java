/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v5.x Project. 
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
 * MBUDPServerChannelConfig.java at 2020-8-22 16:00:59, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.network.udp;

import java.net.SocketException;
import java.nio.channels.DatagramChannel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.ServerSocketChannelConfig;

public class MBUDPServerChannelConfig extends DefaultChannelConfig implements ServerSocketChannelConfig 
{
	private final DatagramChannel datagramChannel;

	public MBUDPServerChannelConfig(Channel channel, DatagramChannel datagramChannel) 
	{
		super(channel);
		this.datagramChannel = datagramChannel;
		setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048));
	}

	@Override
	public int getBacklog()
	{
		return 1;
	}

	@Override
	public ServerSocketChannelConfig setBacklog(int backlog)
	{
		return this;
	}

	@Override
	public ServerSocketChannelConfig setConnectTimeoutMillis(int timeout) 
	{
		return this;
	}

	@Override
	public ServerSocketChannelConfig setPerformancePreferences(int arg0, int arg1, int arg2) 
	{
		return this;
	}

	@Override
	public ServerSocketChannelConfig setAllocator(ByteBufAllocator alloc) 
	{
		super.setAllocator(alloc);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator alloc) 
	{
		super.setRecvByteBufAllocator(alloc);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setAutoRead(boolean autoread) 
	{
		super.setAutoRead(true);
		return this;
	}

	@Override
	@Deprecated
	public ServerSocketChannelConfig setMaxMessagesPerRead(int n) 
	{
		super.setMaxMessagesPerRead(n);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator est)
	{
		super.setMessageSizeEstimator(est);
		return this;
	}

	@Override
	public ServerSocketChannelConfig setWriteSpinCount(int spincount)
	{
		super.setWriteSpinCount(spincount);
		return this;
	}

	public ServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark)
	{
		return (ServerSocketChannelConfig) super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
	}

	public ServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) 
	{
		return (ServerSocketChannelConfig) super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
	}

	public ServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) 
	{
		return (ServerSocketChannelConfig) super.setWriteBufferWaterMark(writeBufferWaterMark);
	}

	@Override
	public int getReceiveBufferSize() 
	{
		try
		{
			return datagramChannel.socket().getReceiveBufferSize();
		} 
		catch (SocketException ex)
		{
			throw new ChannelException(ex);
		}
	}

	@Override
	public ServerSocketChannelConfig setReceiveBufferSize(int size)
	{
		try
		{
			datagramChannel.socket().setReceiveBufferSize(size);
		} 
		catch (SocketException ex) 
		{
			throw new ChannelException(ex);
		}
		return this;
	}

	@Override
	public boolean isReuseAddress()
	{
		try
		{
			return datagramChannel.socket().getReuseAddress();
		}
		catch (SocketException ex) 
		{
			throw new ChannelException(ex);
		}
	}

	@Override
	public ServerSocketChannelConfig setReuseAddress(boolean reuseaddr)
	{
		try 
		{
			datagramChannel.socket().setReuseAddress(true);
		} 
		catch (SocketException ex) 
		{
			throw new ChannelException(ex);
		}
		return this;
	}
}
