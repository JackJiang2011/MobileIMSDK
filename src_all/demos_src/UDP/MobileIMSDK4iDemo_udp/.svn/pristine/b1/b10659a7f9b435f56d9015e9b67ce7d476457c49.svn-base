//  ----------------------------------------------------------------------
//  Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_X (MobileIMSDK v3.x) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址: http://www.52im.net/forum-89-1.html
//  > 即时通讯技术社区：http://www.52im.net/
//  > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//
//  如需联系作者，请发邮件至 jack.jiang@52im.net 或 jb2011@163.com.
//  ----------------------------------------------------------------------
//
//  Protocal.h
//  MibileIMSDK4i_X (MobileIMSDK v3.0 at Summer 2017)
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2017年 52im.net. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 * 协议报文对象.
 * <p>
 * <b>重要说明：</b>因本类中的属性retryCount仅用于本地（不需要通过网络把值传给接收方），因为在对象转JSON时应去掉此属性
 * ，那么接收方收到本对象并反序列化时该属性正好就是默认值0.
 *
 * @author Jack Jiang, 2014-10-22
 * @version 1.0
 */
@interface Protocal : NSObject

/**
 * 意义：是否来自跨服务器的消息，true表示是、否则不是。本字段是为跨服务器或集群准备的。
 * 默认：false
 *
 * @since 3.0
 */
@property (nonatomic, assign)  bool bridge;

/*!
 * 意义：协议类型。
 * 注意：本字段为框架专用字段，本字段的使用涉及IM核心层算法的表现，如无必要请避免应用层使用此字段。
 * 补充：理论上应用层不参与本字段的定义，可将其视为透明，如需定义应用层的消息类型，请使用 {@link typeu} 字
 *      段并配合dataContent一起使用。
 * 默认：0
 *
 * @see @link ProtocalType @/link
 */
@property (nonatomic, assign)  int type;
/*! 意义：协议数据内容。
 * 说明：本字段用于MobileIMSDK_X框架中时，可能会存放一些指令内容。当本字段用于应用层时，由用户自行
 *      定义和使用其内容 */
@property (nonatomic, retain)  NSString *dataContent;

/*! 
 * 意义：消息发出方的id（当用户登陆时，此值可不设置）
 * 说明：为“-1”表示未设定、为“0”表示来自Server。
 * 默认："-1" */
@property (nonatomic, retain)  NSString *from;
/*! 
 * 意义：消息接收方的id（当用户退出时，此值可不设置）
 * 说明：为“-1”表示未设定、为“0”表示发给Server。
 * 默认："-1" */
@property (nonatomic, retain)  NSString *to;

/*! 
 * 意义：用于QoS消息包的质量保证时作为消息的指纹特征码（理论上全局唯一）。
 * 注意：本字段为框架专用字段，请勿用作其它用途。 */
@property (nonatomic, retain)  NSString *fp;

/*!
 * 意义：true表示本包需要进行QoS质量保证，否则不需要.
 * 默认：false
 *
 * @warning 当本属性申明为BOOL类型时，在模拟器iPhone 4s、iPhone 5时，利用官方的NSJSONSerialization
 * 类转成JSON时，会被解析成0或1，而在iPhone 5s和iPhone 6上会被解析成true或false，符合JSON规范的应
 * 是true和false. 经过实验，把申明改成bool型时，在4s、5、5s、6上都能正常解析成true和false，暂时原因不明！
 */
@property (nonatomic, assign)  bool QoS;
//@property (nonatomic)  BOOL QoS;

/**
 * 意义：应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型。
 * 注意：此值为-1时表示未定义。MobileIMSDK_X框架中，本字段为保留字段，不参与框架的核心算法，专留用应用
 *      层自行定义和使用。
 * 默认：-1
 *
 * @since 3.0, at 20161021 */
@property (nonatomic, assign)  int typeu;

/*!
 * 本字段仅用于QoS时：表示丢包重试次数。
 *
 * @return
 */
- (int) getRetryCount;

/*!
 * 本方法仅用于QoS时：选出包重试次数+1。
 * <p>
 * <b>本方法理论上由MobileIMSDK内部调用，应用层无需额外调用。</b>
 */
- (void) increaseRetryCount;

/*!
 * 将本对象转换成JSON字符串.
 *
 * @return
 */
- (NSString *) toGsonString;

/*!
 * 将本对象转换成JSON表示的byte数组（以便网络传输）.
 *
 * @return
 * @see toMutableDictionary:
 * @see toGsonString:
 */
- (NSData *) toBytes;

/*!
 * 克隆本对象.
 * <p>
 * 克隆一个Protocal对象（该对象已重置retryCount数值为0）.
 *
 * @return 本对象新的复制体
 */
- (Protocal *) clone;

/*!
 *  创建Protocal对象的快捷方法（QoS标记默认为true、typeu默认=-1）。
 *
 *  @param type        协议类型
 *  @param dataContent 协议数据内容
 *  @param from        消息发出方的id（当用户登陆时，此值可不设置）
 *  @param to          消息接收方的id（当用户退出时，此值可不设置）
 *
 *  @return 新建的Protocal对象
 */
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to;

/*!
 *  创建Protocal对象的快捷方法（QoS标记默认为true）。
 *
 *  @param type        协议类型
 *  @param dataContent 协议数据内容
 *  @param from        消息发出方的id（当用户登陆时，此值可不设置）
 *  @param to          消息接收方的id（当用户退出时，此值可不设置）
 *  @param typeu       应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型，不需要设置时请填-1即可
 *
 *  @return 新建的Protocal对象
 */
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to tu:(int)typeu;

/*!
 *  创建Protocal对象的快捷方法.
 *
 *  @param type        协议类型
 *  @param dataContent 协议数据内容
 *  @param from        消息发出方的id（当用户登陆时，此值可不设置）
 *  @param to          消息接收方的id（当用户退出时，此值可不设置）
 *  @param QoS         是否需要QoS支持，true表示是，否则不需要
 *  @param fingerPrint 协议包的指纹特征码，当 QoS字段=true时且本字段为null时方法中将自动生成指纹码否则使用本参数指定的指纹码
 *  @param typeu       应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型，不需要设置时请填-1即可
 *
 *  @return 新建的Protocal对象
 */
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to qos:(bool)QoS fp:(NSString *)fingerPrint tu:(int)typeu;

/*!
 *  创建Protocal对象的快捷方法.
 *
 *  @param type        协议类型
 *  @param dataContent 协议数据内容
 *  @param from        消息发出方的id（当用户登陆时，此值可不设置）
 *  @param to          消息接收方的id（当用户退出时，此值可不设置）
 *  @param QoS         是否需要QoS支持，true表示是，否则不需要
 *  @param fingerPrint 协议包的指纹特征码，当 QoS字段=true时且本字段为null时方法中将自动生成指纹码否则使用本参数指定的指纹码
 *  @param bridge      是否来自跨服务器的消息，true表示是、否则不是。本字段是为跨服务器或集群准备的
 *  @param typeu       应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型，不需要设置时请填-1即可
 *
 *  @return 新建的Protocal对象
 */
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(NSString *)from to:(NSString *)to qos:(bool)QoS fp:(NSString *)fingerPrint bg:(bool)bridge tu:(int)typeu;

/*!
 * 返回QoS需要的消息包的指纹特征码.
 * <p>
 * <b>重要说明：</b>使用系统时间戳作为指纹码，则意味着只在Protocal生成的环境中可能唯一.
 * 它存在重复的可能性有2种：
 * <ul>
 * 		<li>1) 比如在客户端生成时如果生成过快的话（时间戳最小单位是1毫秒，如1毫秒内生成
 * 		多个指纹码），理论上是有重复可能性；</li>
 * 		<li>2) 不同的客户端因为系统时间并不完全一致，理论上也是可能存在重复的，所以唯一性应是：好友+指纹码才对.</li>
 * </ul>
 *
 * <p>
 * * 目前使用的UUID基本能保证全局唯一，但它有36位长（加上分隔符32+4），目前为了保持框架的算法可读性
 * 暂时不进行优化，以后可考虑使用2进制方式或者Protobuffer实现。
 *
 * @return 指纹特征码实际上就是系统的当时时间戳
 * @see ToolKits.generateUUID()
 */
+ (NSString *) genFingerPrint;

//+ (Protocal *) fromBytes_JSON:(NSData *) fullProtocalJASOnBytes;

@end
