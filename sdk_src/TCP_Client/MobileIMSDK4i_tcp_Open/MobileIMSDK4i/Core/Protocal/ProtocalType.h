//  ----------------------------------------------------------------------
//  Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址:    http://www.52im.net/forum-89-1.html
//  > 技术社区：   http://www.52im.net/
//  > 技术交流群： 215477170 (http://www.52im.net/topic-qqgroup.html)
//  > 作者公众号： “即时通讯技术圈】”，欢迎关注！
//  > 联系作者：   http://www.52im.net/thread-2792-1-1.html
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//  ----------------------------------------------------------------------

/*! @define ProtocalType
 * 协议类型.
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 */

#ifndef MobileIMSDK4i_ProtocalType_h
#define MobileIMSDK4i_ProtocalType_h


#endif

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - from client
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*! 由客户端发出 - 协议类型：客户端登陆 */
#define FROM_CLIENT_TYPE_OF_LOGIN       0
/*! 由客户端发出 - 协议类型：心跳包 */
#define FROM_CLIENT_TYPE_OF_KEEP_ALIVE  1
/*! 由客户端发出 - 协议类型：发送通用数据 */
#define FROM_CLIENT_TYPE_OF_COMMON_DATA 2
/*! 由客户端发出 - 协议类型：客户端退出登陆 */
#define FROM_CLIENT_TYPE_OF_LOGOUT      3
/*! 由客户端发出 - 协议类型：QoS保证机制中的消息应答包（目前只支持客户端间的QoS机制哦） */
#define FROM_CLIENT_TYPE_OF_RECIVED     4
/*! 由客户端发出 - 协议类型：C2S时的回显指令（此指令目前仅用于测试时） */
#define FROM_CLIENT_TYPE_OF_ECHO        5


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - from server
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*! 由服务端发出 - 协议类型：响应客户端的登陆 */
#define FROM_SERVER_TYPE_OF_RESPONSE_LOGIN      50
/*! 由服务端发出 - 协议类型：响应客户端的心跳包 */
#define FROM_SERVER_TYPE_OF_RESPONSE_KEEP_ALIVE 51
/*! 由服务端发出 - 协议类型：反馈给客户端的错误信息 */
#define FROM_SERVER_TYPE_OF_RESPONSE_FOR_ERROR  52
/*! 由服务端发出 - 协议类型：反馈回显指令给客户端 */
#define FROM_SERVER_TYPE_OF_RESPONSE_ECHO       53

/*! 由服务端发出 - 协议类型：向客户端发出“被踢”指令 */
#define FROM_SERVER_TYPE_OF_KICKOUT             54
