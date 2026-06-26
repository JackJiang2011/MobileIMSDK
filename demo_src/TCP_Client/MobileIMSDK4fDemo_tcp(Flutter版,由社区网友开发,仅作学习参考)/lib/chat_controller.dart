import 'dart:async';

import 'package:get/get.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/conf/config_entity.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/auto_relogin_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/keep_alive_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_sender.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/qos_4_receive_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/qos_4_send_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/event/chat_base_event.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/event/chat_message_event.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/event/message_qoS_event.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Ext.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/c/p_login_info.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_kickout_info.dart';
import 'package:oktoast/oktoast.dart';

import 'test_home_page.dart';

class ChatBaseEventImpl extends ChatBaseEvent {
  final _tag = "登录";

  late ChatController _chat;

  void setChatController(ChatController chatController) {
    _chat = chatController;
  }

  void _refreshState() {
    _chat.refreshState();
  }


  @override
  void onLinkClose(int errorCode) {
    _refreshState();
    final info =
        "[error] $_tag ${nowHmsStrWithMark()} 与服务端的通信断开的回调事件通知:$errorCode";
    Log.error(info, _tag);
    _chat.msgList.add(info);
  }

  @override
  void onLoginResponse(int errorCode) {
    _refreshState();
    final info = "$_tag ${nowHmsStrWithMark()} 结果:$errorCode";
    Log.info(info, _tag);
    _chat.msgList.add(info);
    ClientCoreSDK.getInstance().setLoginHasInit(errorCode == 0);
    if (errorCode == 0) {
      Get.to(const TestHomePage());
    } else {
      showToast("登录失败:$errorCode");
    }
  }

  @override
  void onKickOut(PKickoutInfo kickOutInfo) {
    _refreshState();
    final info = "[error] $_tag ${nowHmsStrWithMark()} 被踢:$kickOutInfo";
    Log.error(info, _tag);
    _chat.msgList.add(info);
  }

}

class ChatMessageEventImpl extends ChatMessageEvent {
  final _tag = "消息";

  late ChatController _chat;

  void setChatController(ChatController chatController) {
    _chat = chatController;
  }

  void _refreshMsg() {}

  @override
  void onErrorResponse(int errorCode, String errorMsg) {
    final info =
        "$_tag ${nowHmsStrWithMark()} 【DEBUG_UI】收到服务端错误消息，errorCode=$errorCode errorMsg:$errorMsg";
    Log.warn(info, _tag);
    _chat.msgList.add(info);
  }

  @override
  void onReceiveMessage(String fingerPrintOfProtocol, String userid,
      String dataContent, int typeu) {
    final info =
        "$_tag ${nowHmsStrWithMark()} 【DEBUG_UI】[typeu=$typeu]收到来自用户 $userid 的消息: $dataContent";
    Log.info(info, _tag);
    _chat.msgList.add(info);
    _chat.newestReceiveMsg.value = info;
  }
}

class MessageQoSEventImpl extends MessageQoSEvent {
  final _tag = "消息QoS";

  late ChatController _chat;

  void setChatController(ChatController chatController) {
    _chat = chatController;
  }

  void _refreshState() {}

  @override
  void messagesBeReceived(String theFingerPrint) {
    final info =
        "$_tag ${nowHmsStrWithMark()} 【DEBUG_UI】收到对方已收到消息事件的通知，fp=$theFingerPrint";
    Log.info(info, _tag);
    _chat.msgList.add(info);
  }

  @override
  void messagesLost(List<Protocol> lostMessages) {
    final info =
        "$_tag ${nowHmsStrWithMark()} 【DEBUG_UI】收到系统的未实时送达事件通知，当前共有 ${lostMessages.length} 个包QoS保证机制结束，判定为【无法实时送达】！";
    Log.warn(info, _tag);
    _chat.msgList.add(info);
  }
}

class ChatController extends GetxController {
  final sdk = ClientCoreSDK.getInstance();

  final linkState = false.obs;

  /// 重连
  final reLinkState = false.obs;

  /// 心跳
  final heartbeatState = false.obs;

  /// 送达（发）
  final qosSendState = false.obs;

  /// 送达（收）
  final qosReceiveState = false.obs;

  final msgList = RxList();

  final newestReceiveMsg = "".obs;

  Timer? _periodicSecTimer;

  @override
  void onInit() {
    super.onInit();
    _initSdk();
    _periodicSecTimer = Timer.periodic(const Duration(seconds: 3), (timer) {
      refreshState();
    });
  }

  @override
  void dispose() {
    super.dispose();
    _periodicSecTimer?.cancel();
    sdk.release();
  }

  void _initSdk() {
    msgList.clear();
    sdk
      ..init()
      ..setChatBaseEvent(ChatBaseEventImpl()..setChatController(this))
      ..setChatMessageEvent(ChatMessageEventImpl()..setChatController(this))
      ..setMessageQoSEvent(MessageQoSEventImpl()..setChatController(this));
  }

  void refreshState() {
    linkState.value = sdk.isConnectedToServer();
    reLinkState.value = AutoReLoginDaemon.getInstance().isAutoReLoginRunning();
    heartbeatState.value = KeepAliveDaemon.getInstance().isKeepAliveRunning();
    qosSendState.value = QoS4SendDaemon.getInstance().isRunning();
    qosReceiveState.value = QoS4ReceiveDaemon.getInstance().isRunning();
  }

  void doLogin(String ip, int port, String name, String pwd) {
    _initSdk();

    ConfigEntity.serverIP = ip;
    ConfigEntity.serverPort = port;

    LocalDataSender.getInstance().sendLogin(PLoginInfo(name, loginToken: pwd));
  }

  void logout() {
    var i = LocalDataSender.getInstance().sendLogout();
    if (i == 0) {
      showToast("退出登录成功");
      sdk.release();
    }
    refreshState();
  }
}
