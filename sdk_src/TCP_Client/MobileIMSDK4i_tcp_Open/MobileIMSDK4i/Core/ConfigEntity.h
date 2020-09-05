//  ----------------------------------------------------------------------
//  Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址:    http://www.52im.net/forum-89-1.html
//  > 技术社区：   http://www.52im.net/
//  > 技术交流群： 320837163 (http://www.52im.net/topic-qqgroup.html)
//  > 作者公众号： “即时通讯技术圈】”，欢迎关注！
//  > 联系作者：   http://www.52im.net/thread-2792-1-1.html
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//  ----------------------------------------------------------------------

#import <Foundation/Foundation.h>

@interface ConfigEntity : NSObject

typedef enum
{
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为3秒；<br>
     * * 5秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大6秒延迟后)后仍未收到服务端反馈）。
     */
    SenseMode3S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为10秒；<br>
     * * 15秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大20秒延迟后)后仍未收到服务端反馈）。
     */
    SenseMode10S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为15秒；<br>
     * * 20秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大30秒延迟后)后仍未收到服务端反馈）。
     * @since 5.0
     */
    SenseMode15S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为30秒；<br>
     * * 35秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大60秒延迟后)后仍未收到服务端反馈）。
     */
    SenseMode30S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为60秒；<br>
     * * 65秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大120秒延迟后)后仍未收到服务端反馈）。
     */
    SenseMode60S,
    
    /*!
     * 此模式下：<br>
     * * KeepAlive心跳问隔为120秒；<br>
     * *125秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大240秒延迟后)后仍未收到服务端反馈）。
     */
    SenseMode120S
} SenseMode;


+ (void)registerWithAppKey:(NSString *)key;
+ (void) setServerIp:(NSString*)sIp;
+ (NSString *)getServerIp;
+ (void) setServerPort:(int)sPort;
+ (int) getServerPort;
+ (void) setLocalSendAndListeningPort:(int)lPort;
+ (int) getLocalSendAndListeningPort;
+ (void) setSenseMode:(SenseMode)mode;

@end
