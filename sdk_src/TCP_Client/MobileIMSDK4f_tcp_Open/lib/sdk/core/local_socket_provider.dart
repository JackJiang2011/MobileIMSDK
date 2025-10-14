import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/conf/config_entity.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_receiver.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/mb_observer.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/tcp_socket_manager.dart';
import 'keep_alive_daemon.dart';

class LocalSocketProvider {
  final String _tag = "LocalSocketProvider";

  //<editor-fold desc="单例">

  factory LocalSocketProvider.getInstance() => _instance;

  static late final LocalSocketProvider _instance =
      LocalSocketProvider._internal();

  LocalSocketProvider._internal() {
    _localSocket = TcpSocketManager();
    _localSocket.init(ConfigEntity.serverIP, ConfigEntity.serverPort,
        onConnectionLost: () {
      Log.info(
          "【IMCORE】连接已断开。。。。(isLocalSocketReady=${isLocalSocketReady()}, ClientCoreSDK.connectedToServer=${ClientCoreSDK.getInstance().isConnectedToServer()})",
          _tag);

      // 快速响应tcp连接断开事件，第一时间反馈给上层
      if (ClientCoreSDK.getInstance().isConnectedToServer()) {
        Log.info(
            "【IMCORE】连接已断开，立即提前进入框架的“通信通道”断开处理逻辑(而不是等心跳线程探测到，那就已经比较迟了)......",
            _tag);
        KeepAliveDaemon.getInstance().notifyConnectionLost();
      }
    }, messageReceived: (msg) {
      LocalDataReceiver.getInstance().handleProtocolJson(msg);
    });
  }

  //</editor-fold>

  late TcpSocketManager _localSocket;

  MBObserver? _connectionDoneObserver;

  void setConnectionDoneObserver(MBObserver connectionDoneObserver) {
    _connectionDoneObserver = connectionDoneObserver;
  }

  TcpSocketManager? resetLocalSocket() {
    try {
      closeLocalSocket();
      tryConnectToHost();
      return _localSocket;
    } catch (e) {
      Log.error("【IMCORE-TCP】重置localSocket时出错，原因是：${e.toString()}", _tag);
      return null;
    }
  }

  tryConnectToHost() {
    try {
      Log.info("【IMCORE-TCP】tryConnectToHost并获取connection开始了...", _tag);

      _localSocket.connectSocket(onSuccess: () {
        Log.info(
            "【IMCORE-tryConnectToHost-异步回调】Connection established successfully",
            _tag);
        _connectionDoneObserver?.update(true, null);
        _connectionDoneObserver = null;
      }, onError: (e) {
        Log.error(
            "【IMCORE-tryConnectToHost-异步回调】连接失败，原因是：${e.toString()}", _tag);
      });
    } catch (e) {
      Log.error(
          "【IMCORE-TCP】连接Server(IP[${ConfigEntity.serverIP}],PORT[${ConfigEntity.serverPort}])失败  ${e.toString()}",
          _tag);
    }
  }

  bool isLocalSocketReady() {
    return _localSocket.isActive();
  }

  TcpSocketManager? getLocalSocket() {
    if (isLocalSocketReady()) {
      return _localSocket;
    } else {
      return resetLocalSocket();
    }
  }

  void closeLocalSocket({bool silent = true}) {
    if (!silent) {
      Log.info("【IMCORE-TCP】正在closeLocalSocket()...", _tag);
    }
    try {
      _localSocket.close();
    } catch (e) {
      Log.error(
          "【IMCORE-TCP】在closeLocalSocket方法中试图释放localSocket资源时：${e.toString()}",
          _tag);
    }

    if (!silent) {
      Log.info("【IMCORE-TCP】Socket处于未初化状态（可能是您还未登陆），无需关闭。", _tag);
    }
  }
}
