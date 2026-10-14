// ignore_for_file: constant_identifier_names

import 'dart:collection';
import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_sender.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Handler.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol.dart';

class QoS4SendDaemon {
  final _tag = "QoS4SendDaemon";

  //<editor-fold desc="单例">

  factory QoS4SendDaemon.getInstance() => _instance;

  static late final QoS4SendDaemon _instance = QoS4SendDaemon._internal();

  QoS4SendDaemon._internal() {
    _initM();
  }

  //</editor-fold>

  static const CHECK_INTERVAL = 5000;
  static const MESSAGES_JUST$NOW_TIME = 3 * 1000;
  static const QOS_TRY_COUNT = 2;

  final HashMap<String, Protocol> _sentMessages = HashMap();
  final HashMap<String, int> _sendMessagesTimestamp = HashMap();

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
        List<Protocol> lostMessages = [];
        final List<Protocol> ret = _doRetryCheck(lostMessages);
        _onRetryCheck(ret);
      }
    };

    _init = true;
  }

  List<Protocol> _doRetryCheck(List<Protocol> lostMessages) {
    _executing = true;

    try {
      if (_sentMessages.isNotEmpty) {
        Log.info(
            "【IMCORE-TCP】【QoS】====== 消息发送质量保证线程运行中, 当前需要处理的列表长度为 ${_sentMessages.length} ...",
            _tag);
      }

      final needRemoveKeyList = List.empty(growable: true);

      for (String key in _sentMessages.keys) {
        final p = _sentMessages[key];
        if (p != null && p.isQoS()) {
          if (p.getRetryCount() >= QOS_TRY_COUNT) {
            Log.info(
                "【IMCORE-TCP】【QoS】指纹为 ${p.getFp()} 的消息包重传次数已达 ${p.getRetryCount()}(最多 $QOS_TRY_COUNT 次)上限，将判定为丢包！",
                _tag);
            lostMessages.add(p);
            needRemoveKeyList.add(p.getFp());
          } else {
            // Bug Fix: 解决了无线网络延较大时，刚刚发出的消息在其应答包还在途中时被错误地进行重传
            final sendMessageTimestamp = _sendMessagesTimestamp[key];
            final delta = ClientCoreSDK.getCurrentTimeStamp() -
                (sendMessageTimestamp ?? 0);
            if (delta <= MESSAGES_JUST$NOW_TIME) {
              Log.info(
                  "【IMCORE-TCP】【QoS】指纹为 $key 的包距\"刚刚\"发出才 $delta ms(<=${MESSAGES_JUST$NOW_TIME} ms将被认定是\"刚刚\"), 本次不需要重传哦.",
                  _tag);
            }
            // Bug Fix END
            else {
              var code = LocalDataSender.getInstance().sendCommonDataObj(p);
              p.increaseRetryCount();
              if (code == 0) {
                Log.info(
                    "【IMCORE-TCP】【QoS】指纹为 ${p.getFp()} 的消息包已成功进行重传，此次之后重传次数已达 ${p.getRetryCount()} (最多 $QOS_TRY_COUNT 次).",
                    _tag);
              } else {
                Log.info(
                    "【IMCORE-TCP】【QoS】指纹为 ${p.getFp()} 的消息包重传失败，它的重传次数之前已累计为 ${p.getRetryCount()} (最多 $QOS_TRY_COUNT 次).",
                    _tag);
              }
            }
          }
        } else {
          needRemoveKeyList.add(key);
        }
      }

      for (var element in needRemoveKeyList) {
        removeByFp(element);
      }
    } catch (eee) {
      Log.error("【IMCORE-TCP】【QoS】消息发送质量保证线程运行时发生异常,$eee", _tag);
    }
    return lostMessages;
  }

  void _onRetryCheck(List<Protocol> al) {
    if (al.isNotEmpty) notifyMessageLost(al);
    _executing = false;
    _handler.postDelayed(_runnable, CHECK_INTERVAL);
  }

  void notifyMessageLost(List<Protocol> lostMessages) {
    ClientCoreSDK.getInstance()
        .getMessageQoSEvent()
        ?.messagesLost(lostMessages);
  }

  void startup(bool immediately) {
    stop();
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

  bool exist(String fingerPrint) {
    return _sentMessages[fingerPrint] != null;
  }

  void put(Protocol? p) {
    if (p == null) {
      Log.info("Invalid arg p==null.", _tag);
      return;
    }
    if (p.getFp() == null) {
      Log.info("Invalid arg p.getFp() == null.", _tag);
      return;
    }

    if (!p.isQoS()) {
      Log.info("This protocol is not QoS pkg, ignore it!", _tag);
      return;
    }

    if (_sentMessages[p.getFp()] != null) {
      Log.info(
          "【IMCORE-TCP】【QoS】指纹为 ${p.getFp()} 的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）",
          _tag);
    }

    var key = p.getFp();
    _sentMessages[key] = p;
    _sendMessagesTimestamp[key] = ClientCoreSDK.getCurrentTimeStamp();
  }

  void removeByFp(final String fingerPrint) {
    _sendMessagesTimestamp.remove(fingerPrint);
    var removedObj = _sentMessages.remove(fingerPrint);
    Log.info(
        "【IMCORE-TCP】【QoS】指纹为 $fingerPrint 的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数= ${removedObj?.getRetryCount()}",
        _tag);
  }

  void clear() {
    _sentMessages.clear();
    _sendMessagesTimestamp.clear();
  }

  int size() {
    return _sentMessages.length;
  }
}
