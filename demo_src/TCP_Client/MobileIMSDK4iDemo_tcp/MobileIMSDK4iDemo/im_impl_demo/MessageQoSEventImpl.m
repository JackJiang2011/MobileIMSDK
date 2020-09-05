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

#import "MessageQoSEventImpl.h"
#import "AppDelegate.h"
#import "MainViewController.h"


/**
 * 消息送达相关事件（由QoS机制通知上来的）在此MessageQoSEvent子类中实现即可。
 *
 * @author Jack Jiang, 20170501
 * @version.1.1
 */
@implementation MessageQoSEventImpl

/*!
 * 消息未送达的回调事件通知.
 *
 * @param lostMessages 由MobileIMSDK QoS算法判定出来的未送达消息列表（此列表中的Protocal对象是原对象的clone（即原对象的深拷贝），请放心使用哦
 * ），应用层可通过指纹特征码找到原消息并可以UI上将其标记为”发送失败“以便即时告之用户
 */
- (void) messagesLost:(NSMutableArray*)lostMessages
{
    NSLog(@"【DEBUG_UI】收到系统的未实时送达事件通知，当前共有%li个包QoS保证机制结束，判定为【无法实时送达】！", (unsigned long)[lostMessages count]);
    
    // UI显示
    [[CurAppDelegate getMainViewController] showIMInfo_brightred:[NSString stringWithFormat:@"[消息未成功送达]共%li条!(网络状况不佳或对方id不存在)", [lostMessages count]]];
}

/*!
 * 消息已被对方收到的回调事件通知.
 * <p>
 * <b>目前，判定消息被对方收到是有两种可能：</b>
 * <br>
 * 1) 对方确实是在线并且实时收到了；<br>
 * 2) 对方不在线或者服务端转发过程中出错了，由服务端进行离线存储成功后的反馈（此种情况严格来讲不能算是“已被收到”，但对于应用层来说，离线存储了的消息
 * 原则上就是已送达了的消息：因为用户下次登陆时肯定能通过HTTP协议取到）。
 *
 * @param theFingerPrint 已被收到的消息的指纹特征码（唯一ID），应用层可据此ID来找到原先已发生的消息并可在UI是将其标记为”已送达“或”已读“以便提升用户体验
 */
- (void) messagesBeReceived:(NSString *)theFingerPrint
{
    if(theFingerPrint != nil)
    {
        NSLog(@"【DEBUG_UI】收到对方已收到消息事件的通知，fp=%@", theFingerPrint);
        
        // UI显示
        [[CurAppDelegate getMainViewController] showIMInfo_blue:[NSString stringWithFormat:@"[收到应答]%@", theFingerPrint]];
    }
}

@end
