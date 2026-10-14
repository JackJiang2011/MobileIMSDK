import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_kickout_info.dart';

abstract class ChatBaseEvent {

  /// 本地用户的登陆结果回调事件通知。
  ///
  /// @param [errorCode] 服务端反馈的登录结果：0 表示登陆成功，否则为服务端自定义的出错代码（按照约定通常为>=1025的数）
  void onLoginResponse(int errorCode);

  /// 与服务端的通信断开的回调事件通知。
  /// <br>
  /// 该消息只有在客户端连接服务器成功之后网络异常中断之时触发。<br>
  /// 导致与与服务端的通信断开的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情况下的网络切换、手机系统的省电策略等。
  ///
  /// @param [errorCode] 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
  void onLinkClose(int errorCode);

  /// 本的用户被服务端踢出的回调事件通知。
  ///
  /// @param [kickOutInfo] 被踢信息对象，[PKickoutInfo] 对象中的 code字段定义了被踢原因代码
  void onKickOut(PKickoutInfo kickOutInfo);
}
