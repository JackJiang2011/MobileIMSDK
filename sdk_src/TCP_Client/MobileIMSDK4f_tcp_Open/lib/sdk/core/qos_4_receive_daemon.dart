// ignore_for_file: constant_identifier_names

import 'dart:collection';
import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Ext.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Handler.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol.dart';

class QoS4ReceiveDaemon {
  final String _tag = "QoS4ReceiveDaemon";

  //<editor-fold desc="单例">

  factory QoS4ReceiveDaemon.getInstance() => _instance;

  static late final QoS4ReceiveDaemon _instance = QoS4ReceiveDaemon._internal();

  QoS4ReceiveDaemon._internal() {
    _initM();
  }

  //</editor-fold>

  static const CHECK_INTERVAL = 5 * 60 * 1000; // 5分钟
  static const MESSAGES_VALID_TIME = 10 * 60 * 1000; // 10分钟

  final LinkedHashMap<String, int> _receivedMessages =
      LinkedHashMap<String, int>();

  bool _running = false;
  bool _executing = false;
  bool _init = false;

  late Handler _handler;
  late Runnable _runnable;

  void _initM() {
    if (_init) return;

    _handler = Handler();
    _runnable = () async {
      if (!_executing) {
        _executing = true;

        Log.info(
            "【IMCORE-TCP】【QoS接收方】+++++ START 暂存处理线程正在运行中，当前长度 ${_receivedMessages.length} .",
            _tag);

        for (String key in _receivedMessages.keys) {
          int receivedTime = _receivedMessages[key] ?? 0;
          int delta = ClientCoreSDK.getCurrentTimeStamp() - receivedTime;
          if (delta >= MESSAGES_VALID_TIME) {
            _receivedMessages.remove(key);
            Log.info(
                "【IMCORE-TCP】【QoS接收方】指纹为 $key 的包已生存 $delta ms(最大允许${MESSAGES_VALID_TIME}ms), 马上将删除之.",
                _tag);
          }
        }
      }
    };

    Log.info(
        "【IMCORE-TCP】【QoS接收方】+++++ END 暂存处理线程正在运行中，当前长度 ${_receivedMessages.length}.",
        _tag);

    _executing = false;

    _handler.postDelayed(_runnable, CHECK_INTERVAL);

    _init = true;
  }

  void startup(bool immediately) {
    stop();

    if (_receivedMessages.isNotEmpty) {
      for (var key in _receivedMessages.keys) {
        putImpl(key);
      }
    }

    _handler.postDelayed(_runnable, immediately ? 0 : CHECK_INTERVAL);
    _running = true;
  }

  void stop() {
    _handler.removeCallbacks(_runnable);
    _running = false;
  }

  bool isRunning() {
    return _running;
  }

  bool isInit() {
    return _init;
  }

  void addReceived(Protocol p) {
    if (!p.isQoS()) return;

    var fingerPrintOfProtocol = p.toJson().toJsonStr();

    if (fingerPrintOfProtocol.isEmpty) {
      Log.info("【IMCORE-TCP】无效的 fingerPrintOfProtocol isEmpty!", _tag);
      return;
    }

    if (_receivedMessages.containsKey(fingerPrintOfProtocol)) {
      Log.info(
          "【IMCORE-TCP】【QoS接收方】指纹为 $fingerPrintOfProtocol 的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.",
          _tag);
    }

    putImpl(fingerPrintOfProtocol);
  }

  void putImpl(String fingerPrintOfProtocol) {
    _receivedMessages[fingerPrintOfProtocol] =
        DateTime.now().millisecondsSinceEpoch;
  }

  bool hasReceived(String fingerPrintOfProtocol) {
    return _receivedMessages.containsKey(fingerPrintOfProtocol);
  }

  void clear() {
    _receivedMessages.clear();
  }

  int size() {
    return _receivedMessages.length;
  }
}
