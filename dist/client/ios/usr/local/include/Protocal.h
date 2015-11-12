//  ----------------------------------------------------------------------
//  Copyright (C) 2015 Jack Jiang The MobileIMSDK Project.
//  All rights reserved.
//  Project URL:  https://github.com/JackJiang2011/MobileIMSDK
//
//  openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
//
//  You can contact author with jack.jiang@openmob.net or jb2011@163.com.
//  ----------------------------------------------------------------------
//
//  Protocal.h
//  MobileIMSDK4i
//
//  Created by JackJiang on 14/10/22.
//  Copyright (c) 2014年 openmob.net. All rights reserved.
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

/*! 
 * 协议类型.
 *
 * @see @link ProtocalType @/link
 */
@property (nonatomic, assign)  int type;
/*! 协议数据内容 */
@property (nonatomic, retain)  NSString *dataContent;

/*! 消息发出方的id（当用户登陆时，此值可不设置） */
@property (nonatomic, assign)  int from;
/*! 消息接收方的id（当用户退出时，此值可不设置） */
@property (nonatomic, assign)  int to;

/*! 用于QoS消息包的质量保证时作为消息的指纹特征码（理论上全局唯一） */
@property (nonatomic, retain)  NSString *fp;

/*!
 * true表示本包需要进行QoS质量保证，否则不需要.
 * <p>
 * <b>注：</b>目前只支持客户发给客户端的质量保证，暂不支持服务端主动发起的包哦（比如上下线通知等）。
 *
 * @warning 当本属性申明为BOOL类型时，在模拟器iPhone 4s、iPhone 5时，利用官方的NSJSONSerialization
 * 类转成JSON时，会被解析成0或1，而在iPhone 5s和iPhone 6上会被解析成true或false，符合JSON规范的应
 * 是true和false. 经过实验，把申明改成bool型时，在4s、5、5s、6上都能正常解析成true和false，暂时原因不明！
 */
@property (nonatomic, assign)  bool QoS;
//@property (nonatomic)  BOOL QoS;

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
 *  创建Protocal对象的快捷方法（QoS标记默认为false）。
 *
 *  @param type        协议类型
 *  @param dataContent 协议数据内容
 *  @param from        消息发出方的id（当用户登陆时，此值可不设置）
 *  @param to          消息接收方的id（当用户退出时，此值可不设置）
 *
 *  @return 新建的Protocal对象
 */
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(int)from to:(int)to;

/*!
 *  创建Protocal对象的快捷方法.
 *
 *  @param type        协议类型
 *  @param dataContent 协议数据内容
 *  @param from        消息发出方的id（当用户登陆时，此值可不设置）
 *  @param to          消息接收方的id（当用户退出时，此值可不设置）
 *  @param QoS         是否需要QoS支持，true表示是，否则不需要
 *  @param fingerPrint 协议包的指纹特征码，当 QoS字段=true时且本字段为null时方法中将自动生成指纹码否则使用本参数指定的指纹码
 *
 *  @return 新建的Protocal对象
 */
+ (Protocal *) initWithType:(int)type content:(NSString *)dataContent from:(int)from to:(int)to qos:(BOOL)QoS fp:(NSString *)fingerPrint;

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
