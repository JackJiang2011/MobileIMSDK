/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project. 
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
 * RawCacheRoot.java at 2022-7-16 17:02:11, code by Jack Jiang.
 */
package org.jb2011.swing9patch.utils;

import java.util.HashMap;

/**
 * 本地磁盘资源文件缓存中心超类，子类可继承本类以实现磁盘资源的集中缓存.
 *
 * @param <T> the generic type
 * @author Jack Jiang(jb2011@163.com), 2010-09-11
 * @version 1.0
 */
public abstract class RawCacheRoot<T>
{
	
	/** 本地磁盘资源缓存中心（key=path,value=image对象）. */
	private HashMap<String,T> rawCache = new HashMap<String,T>();
	
	/**
	 * 本地磁盘资源（如果缓存中已存在，则从中取之，否则从磁盘读取并缓存之）。.
	 *
	 * @param relativePath 本地磁盘资源相对于baseClass类的相对路径，比如它如果在/res/imgs/pic/下，baseClass在
	 * /res下，则本地磁盘资源此处传过来的相对路径应该是/imgs/pic/some.png
	 * @param baseClass 基准类，指定此类则获取本地磁盘资源时会以此类为基准取本地磁盘资源的相对物理目录
	 * @return T
	 */
	public T getRaw(String relativePath,Class baseClass)
	{
		T ic=null;
		
		String key = relativePath+baseClass.getCanonicalName();
		if(rawCache.containsKey(key))
			ic = rawCache.get(key);
		else
		{
			try
			{
				ic = getResource(relativePath, baseClass);
				rawCache.put(key, ic);
			}
			catch (Exception e)
			{
				System.out.println("取本地磁盘资源文件出错,path="+key+","+e.getMessage());
				e.printStackTrace();
			}
		}
		return ic;
	}
	
	/**
	 * 本地资源获取方法实现.
	 *
	 * @param relativePath 相对路径
	 * @param baseClass 基准类
	 * @return the resource
	 */
	protected abstract T getResource(String relativePath,Class baseClass);
}
