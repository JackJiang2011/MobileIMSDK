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

/*!
 * 数据接收辅助处理类（因GCDAsyncSocket的数据接收不需要像经典的BIO那样需开发者自已启动单独的线程，所以本类就简化成了一个辅助类，只是类命名为了兼容性，作了保留，请勿困惑）。
 * <br>
 * 主要工作是将收到的数据进行解析并按MobileIMSDK框架的协议进行调度和处理。本类是MobileIMSDK框架数据接收处理的唯一实现类，也是整个框架算法最为关键的部分。
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.0
 * @since 2.1
 */
@interface LocalDataReciever : NSObject

/// 获取本类的单例。使用单例访问本类的所有资源是唯一的合法途径。
+ (LocalDataReciever *)sharedInstance;

/*!
 *  解析收到的原始消息数据并按照MobileIMSDK定义的协议进行调度和处理。
 *  <p>
 *  本方法目前由 LocalUDPSocketProvider 自动调用。
 *
 *  @param originalProtocalJSONData 收到的MobileIMSDK框架原始通信报文数据内容
 *  @see LocalUDPSocketProvider
 */
- (void) handleProtocal:(NSData *)originalProtocalJSONData;

@end
