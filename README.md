# 快捷目录
> #### 相关资料
* [有关MobileIMSDK的疑问及解答](http://openmob.net/forum.php?mod=viewthread&tid=60&extra=page%3D1) :point_left:
* [MobileIMSDK性能测试报告](http://openmob.net/forum.php?mod=viewthread&tid=57)
* [客户端Demo安装和使用帮助(Android)](http://openmob.net/forum.php?mod=viewthread&tid=55&extra=page%3D1)
* [客户端Demo安装和使用帮助(iOS)](http://openmob.net/forum.php?mod=viewthread&tid=54&extra=page%3D1)
* [客户端Demo安装和使用帮助(Java)](http://openmob.net/forum.php?mod=viewthread&tid=56&extra=page%3D1)
* [应用案例RainbowChat体验版](http://openmob.net/forum.php?mod=viewthread&tid=19&extra=page%3D1) :point_left:
* [应用案例RainbowChat体验版截图预览](http://openmob.net/forum.php?mod=viewthread&tid=20&extra=page%3D1)
* [应用案例某Chat的部分非敏感运营数据](http://openmob.net/forum.php?mod=viewthread&tid=21&page=1&extra=#pid35)

> #### 开发文档
* [客户端开发指南(Android)](http://openmob.net/forum.php?mod=viewthread&tid=61)
* [客户端开发指南(iOS)](http://openmob.net/forum.php?mod=viewthread&tid=62)
* [客户端开发指南(Java)](http://openmob.net/forum.php?mod=viewthread&tid=59)
* [服务端开发指南](http://openmob.net/forum.php?mod=viewthread&tid=63)
* [客户端SDK API文档(Android)](http://openmob.net/extend/docs/api/mobileimsdk/android/)
* [客户端SDK API文档(iOS)](http://openmob.net/extend/docs/api/mobileimsdk/ios/)
* [客户端SDK API文档(Java)](http://openmob.net/extend/docs/api/mobileimsdk/java/)
* [服务端SDK API文档](http://openmob.net/extend/docs/api/mobileimsdk/server/)

> #### 资源下载
* [MobileIMSDK最新版打包下载](https://github.com/JackJiang2011/MobileIMSDK/releases/latest) :point_left:
* [MobileIMSDK的Github地址](https://github.com/JackJiang2011/MobileIMSDK)

> #### 学习交流
* 讨论学习和资料区：[点此进入](http://openmob.net/forum.php?mod=forumdisplay&fid=89) :point_left:
* 移动端即时通讯交流群：![](https://raw.githubusercontent.com/JackJiang2011/MobileIMSDK/master/preview/more_screenshots/others/qq_group_icon_16-16.png) `215891622` :point_left:
* bug/建议发送至：`jb2011@163.com`
* 技术支持/合作/咨询请联系作者QQ：`413980957`

# 一、简介
![](https://raw.githubusercontent.com/JackJiang2011/MobileIMSDK/master/preview/more_screenshots/others/github_header_logo_h.png)

<b>MobileIMSDK是一套专为移动端开发的原创即时通讯高可重用框架：</b> 
* 超轻量级、高度提炼，lib包50KB以内；
* 完全基于UDP协议实现；
* 客户端支持iOS、Android、标准Java平台；
* 可应用于跨设备、跨网络的聊天APP、企业OA、消息推送等各种场景。

> MobileIMSDK工程始于2013年10月，起初用作某产品的即时通讯底层实现，完全从零开发。<br>
MobileIMSDK现已公开并免费供开发者使用，希望对需要的人有所启发和帮助。

:point_right: 您可能需要：[查看更多关于MobileIMSDK的疑问及解答](http://openmob.net/forum.php?mod=viewthread&tid=60&extra=page%3D1)。

# 二、代码托管同步更新

**GitHub.com**

* 代码托管：  https://github.com/JackJiang2011/MobileIMSDK 
* 项目资料：  [点击查看更多资料](http://openmob.net/forum.php?mod=forumdisplay&fid=89)

**OsChina.net**

* 代码托管：  http://git.oschina.net/jackjiang/MobileIMSDK 
* 项目资料：  [点击查看更多资料](http://openmob.net/forum.php?mod=forumdisplay&fid=89)

# 三、设计目标
让开发者专注于应用逻辑的开发，底层<code>复杂的即时通讯算法交由SDK开发人员</code>，从而<code>解偶即时通讯应用开发的复杂性</code>。

# 四、框架组成
<b>整套MobileIMSDK框架由以下4部分组成：</b>

1. <b>Android客户端SDK：</b>用于Android版即时通讯客户端，支持Android 2\.3及以上，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/android/)；
2. <b>iOS客户端SDK：</b>用于开发iOS版即时通讯客户端，支持iOS 6\.0及以上，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/ios/)；
3. <b>Java客户端SDK：</b>用于开发跨平台的PC端即时通讯客户端，支持Java 1\.5及以上，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/java/)；
4. <b>服务端SDK：</b>用于开发即时通讯服和端，支持Java 1\.5及以上版本，[查看API文档](http://openmob.net/extend/docs/api/mobileimsdk/server/)。

# 五、技术特征
* <b>超轻量级：</b>高度提炼，lib包50KB以内；
* <b>UDP实现：</b>更好的适应现今的无线网络环境；
* <b>高效费比：</b>UDP的无连接特性，同等条件下可实现更高的网络负载和吞吐能力；
* <b>消息走向：</b>支持即时通讯技术中消息的所有可能走向，共3种（即C2C、C2S、S2C）；
* <b>QoS机制：</b>完善的消息送达保证机制，不漏过每一条消息；
* <b>健壮可靠：</b>实践表明，非常适于在高延迟、跨洲际、不同网络制式环境中稳定、可靠地运行；
* <b>断网恢复：</b>拥有网络状况自动检测、断网自动治愈的能力；
* <b>原创算法：</b>核心算法和实现均为原创，保证了持续改进和提升的空间；
* <b>多种模式：</b>预设多种实时灵敏度模式，可根据不同场景控制即时性、流量和客户端电量消耗；
* <b>数据压缩：</b>自有协议实现，未来可自主定制数据压缩，灵活控制客户端的流量、服务端网络吞吐；
* <b>高度封装：</b>高度封装的API接口，保证了调用的简易性，也使得可应用于更多的应用场景。

> <b>IMMobileSDK 所支持的全部3种即时通讯消息走向分别是：</b><br>
  (1) Client to Client (C2C)：即由某客户端主动发起，接收者是另一客户端；<br>
  (2) Client to Server (C2S)：即由某客户端主动发起，接收者是服务端；<br>
  (3) Server to Client (S2C)：即由服务端主动发起，接收者是某客户端。
  
:point_right: 您可能需要：[查看更多关于MobileIMSDK的疑问及解答](http://openmob.net/forum.php?mod=viewthread&tid=60&extra=page%3D1)。

# 六、性能测试
压力测试表明，MobileIMSDK用于推送场景时，理论单机负载可接近千万级。用于聊天应用时，单机负载也可达数十万。

> 当然，每款应用都有各自的特点和差异，请视具体场景具体评估之，测试数据仅供参考。

:point_right: 性能测试报告：[点此查看](http://openmob.net/forum.php?mod=viewthread&tid=57)。

# 七、演示程序
1. <b>Android客户端 Demo：</b>[点此安装和使用](http://openmob.net/forum.php?mod=viewthread&tid=55&extra=page%3D1)；
2. <b>iOS客户端 Demo：</b>[点此安装和使用](http://openmob.net/forum.php?mod=viewthread&tid=54&extra=page%3D1)；
3. <b>Java客户端 Demo：</b>[点此安装和使用](http://openmob.net/forum.php?mod=viewthread&tid=56&extra=page%3D1)。

# 八、应用案例
#### ① 基于MobileIMSDK的产品级聊天APP：
> 目前仅作演示之用：[点击下载体验](http://openmob.net/forum.php?mod=viewthread&tid=19&extra=page%3D1) 或 [查看运行截图](http://openmob.net/forum.php?mod=viewthread&tid=20&extra=page%3D1)。

#### ② MobileIMSDK在高网络延迟下的案例：
> 某款基于MobileIMSDK的商业商品，曾运营于跨洲际的复杂网络环境下，端到端通信延迟在洲际网络繁忙时可高达600ms以上（与服务端的单向延迟约为300ms左右，而通常大家访问国内主流门户的延迟约为20~50ms），某段时期的非敏感运营数据 [点此查看](http://openmob.net/forum.php?mod=viewthread&tid=21&page=1&extra=#pid35)。

# 九、资源下载
:paperclip: 最新发布版：[点击下载](https://github.com/JackJiang2011/MobileIMSDK/releases/latest)（<code>内含完整demo、api文档、编译分发包等</code>）。

# 十、典型应用场景
### :triangular_flag_on_post: 场景1：聊天APP
* <b>应用说明：</b>可用于开发类似于微信、QQ等聊天工具。<br>
* <b>消息走向：</b>需使用C2C、C2S、S2C全部类型。<br>
* <b>特别说明：</b>MobileIMSDK并未定义聊天应用的应用层逻辑和协议，开发者可自行定义并实现之。

### :triangular_flag_on_post: 场景2：消息推送
* <b>应用说明：</b>可用于需要向客户端实时推送信息的各种类型APP。<br>
* <b>消息走向：</b>仅需使用S2C 1种消息走向，属MobileIMSDK的最简单应用场景。

### :triangular_flag_on_post: 场景3：企业OA
* <b>应用说明：</b>可用于实现企业OA的指令、公文、申请等各种消息实时推送，极大提升用户体验，并可延伸至移动设备。<br>
* <b>消息走向：</b>仅需使用S2C 1种消息走向，属MobileIMSDK的最简单应用场景。

### :triangular_flag_on_post: 场景4：企业OA的增强型
* <b>应用说明：</b>可用于实现企业OA中各种系统级、用户级消息的实时互动，充分利用即时通讯技术提升传统OA的价值。<br>
* <b>消息走向：</b>可使用C2C、C2S、S2C全部类型，这与聊天APP在很多方面已无差别，但企业OA有自已的用户关系管理模型和逻辑，较之全功能聊天APP要简单的多。

# 十一、开发指南
1. <b>Android客户端开发指南：</b>[点此查看](http://openmob.net/forum.php?mod=viewthread&tid=61)；
2. <b>iOS客户端开发指南：</b>[点此查看](http://openmob.net/forum.php?mod=viewthread&tid=62)；
3. <b>Java客户端开发指南：</b>[点此查看](http://openmob.net/forum.php?mod=viewthread&tid=59)；
4. <b>Server端开发指南：</b>[点此查看](http://openmob.net/forum.php?mod=viewthread&tid=63)。

# 十二、授权方式
你可永久免费且自由地使用MobileIMSDK，如：用于研究、学习、甚至商业用途，
但禁止在超越License约束内容的情况下用于商业用途等，请尊重知识产权。

> <b>补充说明：</b>如需获得更多技术支持或技术合作请联系作者，QQ：<code>413980957</code>。

# 十三、联系方式
* 讨论学习和资料区：:earth_americas: [点此进入](http://openmob.net/forum.php?mod=forumdisplay&fid=89)；
* 移动端即时通讯学习交流群：`215891622` <a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=4cc788473d261129ab3ded26fbb22168d0fa52c799d28f92a8f193dc36865bcb"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="移动端即时通讯/IM开发" title="移动端即时通讯/IM开发"></a> （[更多QQ群点此进入](http://openmob.net/portal.php?mod=topic&topicid=2)）；
* bug和建议请发送至：:love_letter: `jb2011@163.com`；
* 技术支持、技术合作或咨询请联系作者QQ：:penguin: `413980957`、微信：`hellojackjiang`。

# 十四、关注作者
* 推荐关注：[BeautyEye工程](https://github.com/JackJiang2011/beautyeye)
* 博客地址：[点击入进](http://openmob.net/home.php?mod=space&uid=1&do=thread&view=me&from=space)
* Github主页：[点击进入](https://github.com/JackJiang2011)

# 附录1：Demo截图
### :triangular_flag_on_post: Android和iOS运行效果：
> <code>安装和使用：</code>[进入Android版Demo帮助页](http://openmob.net/forum.php?mod=viewthread&tid=55)、[进入iOS版Demo帮助页](http://openmob.net/forum.php?mod=viewthread&tid=54)。

![](https://github.com/JackJiang2011/MobileIMSDK/raw/master/preview/more_screenshots/others/ios_android_real_run.jpg)

### :triangular_flag_on_post: Windows 运行效果：
> <code>安装和使用：</code>[进入Java版Demo帮助页](http://openmob.net/forum.php?mod=viewthread&tid=56)。

![](https://github.com/JackJiang2011/MobileIMSDK/raw/master/preview/more_screenshots/others/windows_real_run.png)

### :triangular_flag_on_post: Mac OS X 运行效果：
> <code>安装和使用：</code>[进入Java版Demo帮助页](http://openmob.net/forum.php?mod=viewthread&tid=56)。

![](https://github.com/JackJiang2011/MobileIMSDK/raw/master/preview/more_screenshots/others/mac_real_run.png)