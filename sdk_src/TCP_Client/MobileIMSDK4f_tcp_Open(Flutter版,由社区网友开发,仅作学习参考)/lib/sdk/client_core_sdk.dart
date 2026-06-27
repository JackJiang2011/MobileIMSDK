import 'package:mobile_im_sdk_flutter_tcp/sdk/conf/config_entity.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/auto_relogin_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/keep_alive_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_receiver.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_socket_provider.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/qos_4_receive_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/qos_4_send_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/event/chat_base_event.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/event/chat_message_event.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/event/message_qoS_event.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/c/p_login_info.dart';

class ClientCoreSDK {
  static const String TAG = "ClientCoreSDK";
  static bool autoReLogin = true;

  static int getCurrentTimeStamp() {
    return DateTime.now().millisecondsSinceEpoch;
  }

  //<editor-fold desc="单例">

  factory ClientCoreSDK.getInstance() => _instance;

  static late final ClientCoreSDK _instance = ClientCoreSDK._internal();

  ClientCoreSDK._internal();

  //</editor-fold>

  bool _init = false;

  bool _connectedToServer = true;
  bool _loginHasInit = false;

  ChatBaseEvent? _chatBaseEvent;
  ChatMessageEvent? _chatMessageEvent;
  MessageQoSEvent? _messageQoSEvent;

  PLoginInfo? _currentLoginInfo;

  void init({SenseMode senseMode = SenseMode.MODE_10S}) {
    if (!_init) {
      ConfigEntity.setSenseMode(senseMode);
      AutoReLoginDaemon.getInstance();
      KeepAliveDaemon.getInstance();
      LocalDataReceiver.getInstance();
      QoS4SendDaemon.getInstance();
      QoS4ReceiveDaemon.getInstance();

      _init = true;
    }
  }

  void release() {
    setConnectedToServer(false);

    LocalSocketProvider.getInstance().closeLocalSocket();
    AutoReLoginDaemon.getInstance().stop();
    KeepAliveDaemon.getInstance().stop();

    QoS4SendDaemon.getInstance().stop();
    QoS4ReceiveDaemon.getInstance().stop();

    QoS4SendDaemon.getInstance().clear();
    QoS4ReceiveDaemon.getInstance().clear();

    _init = false;
    setLoginHasInit(false);
    setCurrentLoginInfo(null);
  }

  void setCurrentLoginInfo(PLoginInfo? currentLoginInfo) {
    _currentLoginInfo = currentLoginInfo;
  }

  PLoginInfo? getCurrentLoginInfo() {
    return _currentLoginInfo;
  }

  void saveFirstLoginTime(int firstLoginTime) {
    _currentLoginInfo?.setFirstLoginTime(firstLoginTime);
  }

  bool isLoginHasInit() {
    return _loginHasInit;
  }

  ClientCoreSDK setLoginHasInit(bool loginHasInit) {
    _loginHasInit = loginHasInit;
    return this;
  }

  bool isConnectedToServer() {
    return _connectedToServer;
  }

  void setConnectedToServer(bool connectedToServer) {
    _connectedToServer = connectedToServer;
  }

  bool isInitialed() {
    return _init;
  }

  void setChatBaseEvent(ChatBaseEvent chatBaseEvent) {
    _chatBaseEvent = chatBaseEvent;
  }

  ChatBaseEvent? getChatBaseEvent() {
    return _chatBaseEvent;
  }

  void setChatMessageEvent(ChatMessageEvent chatMessageEvent) {
    _chatMessageEvent = chatMessageEvent;
  }

  ChatMessageEvent? getChatMessageEvent() {
    return _chatMessageEvent;
  }

  void setMessageQoSEvent(MessageQoSEvent messageQoSEvent) {
    _messageQoSEvent = messageQoSEvent;
  }

  MessageQoSEvent? getMessageQoSEvent() {
    return _messageQoSEvent;
  }
}
