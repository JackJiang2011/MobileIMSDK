/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.1 Project. 
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
 * OnlineProcessor.java at 2022-7-12 16:35:57, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.processor;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.x52im.mobileimsdk.server.network.Gateway;
import net.x52im.mobileimsdk.server.protocal.s.PKickoutInfo;
import net.x52im.mobileimsdk.server.utils.LocalSendHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlineProcessor
{
	public static final AttributeKey<String> ATTRIBUTE_KEY_USER_ID = AttributeKey.newInstance("__user_id__");
	public static final AttributeKey<Long> ATTRIBUTE_KEY_FIRST_LOGIN_TIME = AttributeKey.newInstance("__first_login_time__");
	public static final AttributeKey<Integer> ATTRIBUTE_KEY_BE_KICKOUT_CODE = AttributeKey.newInstance("__be_keickout_code__");
	
	public static boolean DEBUG = false;
	private static Logger logger = LoggerFactory.getLogger(OnlineProcessor.class); 
	private static volatile OnlineProcessor instance = null;
	
	private ConcurrentMap<String, Channel> onlineSessions = new ConcurrentHashMap<String, Channel>();
	
	public static OnlineProcessor getInstance()
	{
		if (instance == null) {
			synchronized (OnlineProcessor.class) {
				if (instance == null) {
					instance = new OnlineProcessor();
				}
			}
		}
		return instance;
	}
	
	private OnlineProcessor()
	{
	}
	
	public boolean putUser(String user_id, long firstLoginTime, Channel newSession)
	{
		boolean putOk = true;
		final Channel oldSession = onlineSessions.get(user_id);
		if(oldSession != null)
		{

			boolean isTheSame = (oldSession.compareTo(newSession) == 0);
			
			logger.debug("[IMCORE-{}]【注意】用户id={}已经在在线列表中了，session也是同一个吗？{}", Gateway.$(newSession), user_id, isTheSame);

			/************* 以下将展开同一账号重复登陆情况的处理逻辑 *************/

			if(!isTheSame)
			{

				if(firstLoginTime <= 0)
				{
					logger.debug("[IMCORE-{}]【注意】用户id={}提交过来的firstLoginTime未设置(值={}, 应该是真的首次登陆？！)，将无条件踢出前面的会话！"
							, Gateway.$(newSession), user_id, firstLoginTime);
					sendKickoutDuplicateLogin(oldSession, user_id);	
					onlineSessions.put(user_id, newSession);
				}
				else
				{
					long firstLoginTimeForOld = OnlineProcessor.getFirstLoginTimeFromChannel(oldSession);
					if(firstLoginTime >= firstLoginTimeForOld)
					{
						logger.debug("[IMCORE-{}]【提示】用户id={}提交过来的firstLoginTime为{}、firstLoginTimeForOld为{}，新的“首次登陆时间”【晚于】列表中的“老的”、正常踢出老的即可！"
								, Gateway.$(newSession), user_id, firstLoginTime, firstLoginTimeForOld);
						sendKickoutDuplicateLogin(oldSession, user_id);		
						onlineSessions.put(user_id, newSession);
					}
					else
					{
						logger.debug("[IMCORE-{}]【注意】用户id={}提交过来的firstLoginTime为{}、firstLoginTimeForOld为{}，新的“首次登陆时间”【早于】列表中的“老的”，表示“新”的会话应该是未被正常通知的“已踢”会话，应再次向“新”会话发出被踢通知！！"
								, Gateway.$(newSession), user_id, firstLoginTime, firstLoginTimeForOld);
						sendKickoutDuplicateLogin(newSession, user_id);	
						putOk = false;
					}
				}
			}
			else
			{
				onlineSessions.put(user_id, newSession);
			}
		}
		else
		{
			onlineSessions.put(user_id, newSession);
		}

		__printOnline();// just for debug
		
		return putOk;
	}

private void sendKickoutDuplicateLogin(final Channel sessionBeKick, String to_user_id)
	{
		try{
			LocalSendHelper.sendKickout(sessionBeKick, to_user_id, PKickoutInfo.KICKOUT_FOR_DUPLICATE_LOGIN, null);
			logger.debug("[IMCORE-{}]【提示】服务端正在向用户id={}发送被踢指令！", Gateway.$(sessionBeKick), to_user_id);
		}
		catch (Exception e){
			logger.warn("[IMCORE-"+Gateway.$(sessionBeKick)+"] sendKickoutDuplicate的过程中发生了异常：", e);
		}
	}
	
	public void __printOnline()
	{
		logger.debug("【@】当前在线用户共("+onlineSessions.size()+")人------------------->");
		if(DEBUG)
		{
			for(String key : onlineSessions.keySet())
				logger.debug("      > user_id="+key+",session="+onlineSessions.get(key).remoteAddress());
		}
	}
	
	public boolean removeUser(String user_id)
	{
		synchronized(onlineSessions)
		{
			if(!onlineSessions.containsKey(user_id))
			{
				logger.warn("[IMCORE]！用户id={}不存在在线列表中，本次removeUser没有继续.", user_id);
				__printOnline();// just for debug
				return false;
			}
			else
				return (onlineSessions.remove(user_id) != null);
		}
	}
	
	public Channel getOnlineSession(String user_id)
	{
		if(user_id == null)
		{
			logger.warn("[IMCORE][CAUTION] getOnlineSession时，作为key的user_id== null.");
			return null;
		}
		
		return onlineSessions.get(user_id);
	}
	
	public ConcurrentMap<String, Channel> getOnlineSessions()
	{
		return onlineSessions;
	}

	public static boolean isLogined(Channel session)
	{
		return session != null && getUserIdFromChannel(session) != null;
	}
	
	public static boolean isOnline(String userId)
	{
		return OnlineProcessor.getInstance().getOnlineSession(userId) != null;
	}
	
	public static void setUserIdForChannel(Channel session, String userId)
	{
		session.attr(OnlineProcessor.ATTRIBUTE_KEY_USER_ID).set(userId);
	}
	
	public static void setFirstLoginTimeForChannel(Channel session, long firstLoginTime)
	{
		session.attr(OnlineProcessor.ATTRIBUTE_KEY_FIRST_LOGIN_TIME).set(firstLoginTime);
	}
	
	public static void setBeKickoutCodeForChannel(Channel session, int beKickoutCode)
	{
		session.attr(OnlineProcessor.ATTRIBUTE_KEY_BE_KICKOUT_CODE).set(beKickoutCode);
	}
	
	public static String getUserIdFromChannel(Channel session)
	{
		return (session != null ? session.attr(ATTRIBUTE_KEY_USER_ID).get() : null);
	}
	
	public static long getFirstLoginTimeFromChannel(Channel session)
	{
		if(session != null){
			Long attr = session.attr(ATTRIBUTE_KEY_FIRST_LOGIN_TIME).get();
			return attr != null ? attr : -1;
		}
		return -1;
	}
	
	public static int getBeKickoutCodeFromChannel(Channel session)
	{
		if(session != null){
			Integer attr = session.attr(ATTRIBUTE_KEY_BE_KICKOUT_CODE).get();
			return attr != null ? attr : -1;
		}
		return -1;
	}
	
	public static void removeAttributesForChannel(Channel session)
	{
		session.attr(OnlineProcessor.ATTRIBUTE_KEY_USER_ID).set(null);
		session.attr(OnlineProcessor.ATTRIBUTE_KEY_FIRST_LOGIN_TIME).set(null);
		session.attr(OnlineProcessor.ATTRIBUTE_KEY_BE_KICKOUT_CODE).set(null);
	}
}
