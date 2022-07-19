/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
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
 * TestSample.java at 2022-7-16 16:53:48, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.demo.utils;

import net.x52im.mobileimsdk.server.protocal.Protocal;
import net.x52im.mobileimsdk.server.protocal.ProtocalType;

/**
 * 获得测试样本数据实用类。
 * 
 * @author 来自网络
 */
public class TestSample
{
	/**
	 * 获得一个ECHO指令测试样本（含完整的4字节包头+包体）。
	 * 
	 * @return
	 */
	public static String getECHOSample(){
		// 以发送者id=“aaa”的身份，向服务端发送内容为“A”的ECHO测试指令！
		Protocal p = new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_ECHO, "A", "aaa", "0", false, null, -1);
//		LocalUDPDataSender.getInstance().sendCommonData(p);

		// tcp数据体
		byte[] frameConcent = p.toBytes();
		// tcp包头
		byte[] header = intToBytes2(frameConcent.length);
		
		// 完成tcp包
		byte[] tcpFrameAll = byteMergerAll(header, frameConcent);
		
		// tcp完整包转16进制文本
		String hex = bytes2Hex(tcpFrameAll);

//		System.out.println("tcpFrameAll.len="+tcpFrameAll.length);
//		System.out.println("结果："+hex);
		
		return hex;
	}

//	/**
//	 * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
//	 * @param value 要转换的int值
//	 * @return byte数组
//	 */
//	public static byte[] intToBytes( int value )
//	{
//		byte[] src = new byte[4];
//		src[3] =  (byte) ((value>>24) & 0xFF);
//		src[2] =  (byte) ((value>>16) & 0xFF);
//		src[1] =  (byte) ((value>>8) & 0xFF);
//		src[0] =  (byte) (value & 0xFF);
//		return src;
//	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
	 */
	public static byte[] intToBytes2(int value){
		byte[] src = new byte[4];
		src[0] = (byte) ((value>>24) & 0xFF);
		src[1] = (byte) ((value>>16)& 0xFF);
		src[2] = (byte) ((value>>8)&0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * 合并两个byte数组。
	 * 
	 * @param values
	 * @return
	 */
	private static byte[] byteMergerAll(byte[]... values) {
		int length_byte = 0;
		for (int i = 0; i < values.length; i++) {
			length_byte += values[i].length;
		}
		byte[] all_byte = new byte[length_byte];
		int countLength = 0;
		for (int i = 0; i < values.length; i++) {
			byte[] b = values[i];
			System.arraycopy(b, 0, all_byte, countLength, b.length);
			countLength += b.length;
		}
		return all_byte;
	}

	private static final char[] HEXES = {
		'0', '1', '2', '3',
		'4', '5', '6', '7',
		'8', '9', 'a', 'b',
		'c', 'd', 'e', 'f'
	};

	/**
	 * byte数组 转换成 16进制小写字符串。
	 */
	public static String bytes2Hex(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		StringBuilder hex = new StringBuilder();
		for (byte b : bytes) {
			hex.append(HEXES[(b >> 4) & 0x0F]);
			hex.append(HEXES[b & 0x0F]);
		}

		return hex.toString();
	}
	
	public static void main(String[] args){
		System.out.println(TestSample.getECHOSample());
	}
}
