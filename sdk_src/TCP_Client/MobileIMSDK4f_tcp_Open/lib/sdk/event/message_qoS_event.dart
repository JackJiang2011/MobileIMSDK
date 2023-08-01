import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol.dart';

/// 消息送达相关事件（由QoS机制通知上来的）在此 [MessageQoSEvent]子类中实现即可。
abstract class MessageQoSEvent {

  /// 消息未送达的回调事件通知.
  ///
  /// @param [lostMessages] 由 MobileIMSDK QoS算法判定出来的未送达消息列表（此列表中的[Protocol]对象是原对象的
  ///                     clone（即原对象的深拷贝），请放心使用哦），应用层可通过指纹特征码找到原消息并可
  ///
  void messagesLost(List<Protocol> lostMessages);

  /// 消息已被对方收到的回调事件通知.
  /// <p>
  /// <b>目前，判定消息被对方收到是有两种可能：</b><br>
  /// <ul>
  /// <li>1) 对方确实是在线并且实时收到了；</li>
  /// <li>2) 对方不在线或者服务端转发过程中出错了，由服务端进行离线存储成功后的反馈（此种情况严格来讲不能算是“已被
  /// 		收到”，但对于应用层来说，离线存储了的消息原则上就是已送达了的消息：因为用户下次登陆时肯定能通过HTTP协议取到）。</li>
  /// </ul>
  ///
  /// @param [theFingerPrint] 已被收到的消息的指纹特征码（唯一ID），应用层可据此ID来找到原先已发生的消息并可在
  ///                       UI是将其标记为”已送达“或”已读“以便提升用户体验
  void messagesBeReceived(String theFingerPrint);
}
