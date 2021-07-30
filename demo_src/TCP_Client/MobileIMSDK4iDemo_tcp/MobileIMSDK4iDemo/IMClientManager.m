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

#import "IMClientManager.h"
#import "ClientCoreSDK.h"
#import "ConfigEntity.h"
#import "TCPFrameCodec.h"


///////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
///////////////////////////////////////////////////////////////////////////////////////////

@interface IMClientManager ()

/* MobileIMSDK是否已被初始化. true表示已初化完成，否则未初始化. */
@property (nonatomic) BOOL _init;
//
@property (strong, nonatomic) ChatBaseEventImpl *baseEventListener;
//
@property (strong, nonatomic) ChatMessageEventImpl *transDataListener;
//
@property (strong, nonatomic) MessageQoSEventImpl *messageQoSListener;

@end


///////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 本类的代码实现
///////////////////////////////////////////////////////////////////////////////////////////

@implementation IMClientManager

// 本类的单例对象
static IMClientManager *instance = nil;

+ (IMClientManager *)sharedInstance
{
    if (instance == nil)
    {
        instance = [[super allocWithZone:NULL] init];
    }
    return instance;
}

/*
 *  重写init实例方法实现。
 *
 *  @return
 *  @see [NSObject init:]
 */
- (id)init
{
    if (![super init])
        return nil;
    
    [self initMobileIMSDK];
    
    return self;
}

- (void)initMobileIMSDK
{
    if(!self._init)
    {
        // 设置AppKey
        [ConfigEntity registerWithAppKey:@"5418023dfd98c579b6001741"];
        
        // 设置服务器ip和服务器端口
//      [ConfigEntity setServerIp:@"rbcore.52im.net"];
//      [ConfigEntity setServerPort:7901];
        
        // 使用以下代码表示不绑定固定port（由系统自动分配），否则使用默认的7801端口
//      [ConfigEntity setLocalSendAndListeningPort:-1];
        
        // MobileIMSDK核心IM框架的敏感度模式设置
        [ConfigEntity setSenseMode:SenseMode5S];
        
        // 设置最大TCP帧内容长度（不设置则默认最大是 6 * 1024字节）
//      [TCPFrameCodec setTCP_FRAME_MAX_BODY_LENGTH:60 * 1024];
        
        // 开启DEBUG信息输出
        [ClientCoreSDK setENABLED_DEBUG:YES];
        
        // 设置事件回调
        self.baseEventListener = [[ChatBaseEventImpl alloc] init];
        self.transDataListener = [[ChatMessageEventImpl alloc] init];
        self.messageQoSListener = [[MessageQoSEventImpl alloc] init];
        [ClientCoreSDK sharedInstance].chatBaseEvent = self.baseEventListener;
        [ClientCoreSDK sharedInstance].chatMessageEvent = self.transDataListener;
        [ClientCoreSDK sharedInstance].messageQoSEvent = self.messageQoSListener;
        
        self._init = YES;
    }
}

- (void)releaseMobileIMSDK
{
    [[ClientCoreSDK sharedInstance] releaseCore];
    [self resetInitFlag];
}

- (void)resetInitFlag
{
    self._init = NO;
}

- (ChatMessageEventImpl *) getTransDataListener
{
    return self.transDataListener;
}
- (ChatBaseEventImpl *) getBaseEventListener
{
    return self.baseEventListener;
}
- (MessageQoSEventImpl *) getMessageQoSListener
{
    return self.messageQoSListener;
}

@end
