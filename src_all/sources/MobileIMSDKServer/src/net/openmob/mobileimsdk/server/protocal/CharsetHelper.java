/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * CharsetHelper.java at 2016-2-20 11:26:02, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.server.protocal;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CharsetHelper
{
	public static final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	public static final String ENCODE_CHARSET = "UTF-8";
	public static final String DECODE_CHARSET = "UTF-8";

	public static String getString(byte[] b, int len)
	{
		try
		{
			return new String(b, 0 , len, DECODE_CHARSET);
		}
		// 如果是不支持的字符类型则按默认字符集进行解码
		catch (UnsupportedEncodingException e)
		{
			return new String(b, 0 , len);
		}
	}

	public static String getString(byte[] b, int start, int len)
	{
		try
		{
			return new String(b, start , len, DECODE_CHARSET);
		}
		// 如果是不支持的字符类型则按默认字符集进行解码
		catch (UnsupportedEncodingException e)
		{
			return new String(b, start , len);
		}
	}

	public static byte[] getBytes(String str)
	{
		if(str != null)
		{
			try
			{
				return str.getBytes(ENCODE_CHARSET);
			}
			// 如果是不支持的字符类型则按默认字符集进行编码
			catch (UnsupportedEncodingException e)
			{
				return str.getBytes();
			}
		}
		else
			return new byte[0];
	}
}