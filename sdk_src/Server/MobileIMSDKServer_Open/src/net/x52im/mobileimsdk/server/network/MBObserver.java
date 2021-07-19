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
 * MBObserver.java at 2021-6-29 10:15:34, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.network;

/**
 * 一个类似于观察者模式中Observer的接口。
 * <p>
 * 因为Netty中的异步特性，诸如发送数据等等操作的最佳实践都是通过
 * 异步通知来获得操作结果（比如数据发送成功还是失败），而本类的目
 * 的就是起到在上述异步通知的回调作用，从而让本类的实例拥有者能得
 * 到异步结果通知。
 * 
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 * @since 3.1
 */
public interface MBObserver
{
	/**
	 * 调用者通过此方法来通知异步结果的观察者。
	 * 
	 * @param sucess true表示任务执行成功，否则表示执行失败
	 * @param extraObj 任务执行完成时通知给观察者的额外数据，本参数可为null
	 */
	void update(boolean sucess, Object extraObj);
}
