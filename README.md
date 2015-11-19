# ![](https://raw.githubusercontent.com/JackJiang2011/MobileIMSDK/master/preview/more_screenshots/others/github_header_logo_h.png)
MobileIMSDK工程始于2013年10月，起初用作某产品的即时通讯底层实现，完全从零开发。<br>
MobileIMSDK现已公开并免费供开发者使用，希望对需要的人有所启发和帮助。

:point_right: 您可能需要：[查看更多关于MobileIMSDK的疑问及解答](http://openmob.net/forum.php?mod=viewthread&tid=60&extra=page%3D1)。

# 一、简介
<b>MobileIMSDK是一套专为移动端开发的原创即时通讯高可重用框架：</b> 
* 完全基于UDP协议实现；
* 客户端支持iOS、Android、标准Java平台；
* 可应用于跨设备、跨网络的聊天APP、企业OA、消息推送等各种场景。

:point_right: 您可能需要：[查看更多关于MobileIMSDK的疑问及解答](http://openmob.net/forum.php?mod=viewthread&tid=60&extra=page%3D1)。

# 二、设计目标
让开发者专注于应用逻辑的开发，底层<code>复杂的即时通讯算法交由SDK开发人员</code>，从而<code>解偶即时通讯应用开发的复杂性</code>。

# 三、框架组成
<b>整套MobileIMSDK框架由以下4部分组成：</b>
* <b>[1] Android客户端SDK：</b>用于开发Android版即时通讯客户端，支持Android 2.3及以上版本，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/android/)；
* <b>[2] iOS客户端SDK：</b>用于开发iOS版即时通讯客户端，支持iOS 6.0及以上版本，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/ios/)；
* <b>[3] Java客户端SDK：</b>用于开发跨平台的PC端即时通讯客户端，支持标准Java 1.5及以上版本，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/java/)；
* <b>[4] 服务端SDK：</b>用于开发即时通讯服和端，支持Java 1.5及以上版本，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/server/)。

# 四、技术特征
* <b>UDP实现：</b>更好的适应现今的无线网络环境；
* <b>高效费比：</b>UDP的无连接特性，同等条件下可实现更高的网络负载和吞吐能力；
* <b>消息走向：</b>支持即时通讯技术中消息的所有可能走向，共3种（即C2C、C2S、S2C）；
* <b>QoS机制：</b>完善的消息送达保证机制，不漏过每一条消息；
* <b>健壮可靠：</b>实际产品的运营表明，非常适于在高延迟、跨洲际、不同网络制式环境中稳定、可靠地运行；
* <b>断网恢复：</b>拥有网络状况自动检测、断网自动治愈的能力；
* <b>原创算法：</b>核心算法和实现均为原创，保证了持续改进和提升的空间；
* <b>多种模式：</b>预设多种实时灵敏度模式，可根据不同场景控制即时性、流量和客户端电量消耗；
* <b>数据压缩：</b>自有协议实现，未来可自主定制数据压缩，灵活控制客户端的流量、服务端网络吞吐；
* <b>高度封装：</b>高度封装的API接口，保证了调用的简易性，也使得可应用于更多的应用场景。
> IMMobileSDK 所支持的全部3种即时通讯消息走向分别是：   
  (1) Client to Client (C2C)：即由某客户端主动发起，接收者是另一客端；
  (2) Client to Server (C2S)：即由某客户端主动发起，接收者是服务端；
  (3) Server to Client (S2C)：即由服务端主动发起，接收者是某客户端。

<b>MobileIMSDK在高网络延迟下的真实应用案例：</b>
某款基于MobileIMSDK的商业商品，曾运营于跨洲际的复杂网络环境下，端到端通信延迟在洲际网络繁忙时可高达600ms以上（与服务端的单向延迟约为300ms左右，而通常大家访问国内主流门户的延迟约为20~50ms），某段时期的非敏感运营数据[点此查看](http://openmob.net/forum.php?mod=viewthread&tid=21&page=1&extra=#pid35)。
