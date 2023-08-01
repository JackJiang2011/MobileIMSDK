// ignore_for_file: non_constant_identifier_names

import 'dart:async';
import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_sender.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Handler.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/mb_observer.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/mb_simple_timer.dart';

class KeepAliveDaemon {
  static const String _tag = "KeepAliveDaemon";

  /// Keep Alive 心跳发送时间间隔（单位：毫秒），默认15000毫秒（即15秒）
  ///
  /// 心跳间隔越短则保持会话活性的健康度更佳，但将使得在大量客户端连接情况下服务端因此而增加负载，
  /// 且手机将消耗更多电量和流量，所以此间隔需要权衡（建议为：大于3秒 且 小于270秒(即4分半钟) ）！
  ///
  /// 说明：此参数用于设定客户端发送到服务端的心跳间隔，心跳包的作用是用来保持与服务端的会话活性
  /// （更准确的说是为了避免客户端因路由器的NAT算法而导致路由器端口老化，相关知识见此文：http://www.52im.net/thread-281-1-1.html）.
  /// 参定此参数的同时，也需要相应设置服务端的ServerLauncher.SESION_RECYCLER_EXPIRE参数。
  ///
  static int KEEP_ALIVE_INTERVAL = 15 * 1000;

  /// 收到服务端响应心跳包的超时间时间（单位：毫秒），默认（15 * 1000 + 5000）＝ 20000 毫秒（即20秒）
  ///
  /// 超过这个时间客户端将判定与服务端的网络连接已断开（此间隔建议为(KEEP_ALIVE_INTERVAL * 1) + 5 秒），
  /// 没有上限，但不可太长，否则将不能即时反映出与服务器端的连接断开（比如掉掉线时），请从 能忍受的反应时长和即时性上做出权衡。
  ///
  /// 本参数除与 [KEEP_ALIVE_INTERVAL] 有关联外，不受其它设置影响。
  ///
  static int NETWORK_CONNECTION_TIME_OUT = KEEP_ALIVE_INTERVAL + 5000;

  /// 心跳包超时检查定时器的运行间隔时间（单位：毫秒），默认（2 * 1000）＝ 2000 毫秒（即2秒）
  ///
  /// 此时间将决定断网感应灵敏度。建议设置值的范围为1~5秒内。
  ///
  static int NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL = 2 * 1000;

  bool _keepAliveRunning = false;
  int _lastGetKeepAliveResponseFromServerTimestamp = 0;
  MBObserver? _networkConnectionLostObserver;

  Timer? _keepAliveHandler;
  late Runnable _keepAliveRunnable;

  bool _keepAliveTaskExecuting = false;
  bool _keepAliveWillStop = false;

  late MBSimpleTimer _keepAliveTimeoutTimer;

  bool _init = false;

  //<editor-fold desc="单例">

  factory KeepAliveDaemon.getInstance() => _instance;

  static late final KeepAliveDaemon _instance = KeepAliveDaemon._internal();

  KeepAliveDaemon._internal() {
    _initFunc();
  }

  //</editor-fold>

  void _initFunc() {
    if (_init) return;

    _keepAliveRunnable = () async {
      if (!_keepAliveTaskExecuting) {
        final int code = _doKeepAlive();
        _onKeepAlive(code);
      }
    };

    _keepAliveTimeoutTimer =
        MBSimpleTimer(NETWORK_CONNECTION_TIME_OUT_CHECK_INTERVAL, () {
      Log.info("【IMCORE-TCP】心跳[超时检查]线程执行中... $hashCode ", _tag);
      _doTimeoutCheck();
    });

    _init = true;
  }

  int _doKeepAlive() {
    _keepAliveTaskExecuting = true;
    Log.info("【IMCORE-TCP】心跳包[发送]线程执行中...", _tag);
    int code = LocalDataSender.getInstance().sendKeepAlive();

    return code;
  }

  void _onKeepAlive(int code) {
    bool isInitialedForKeepAlive = _isInitialedForKeepAlive();
    if (isInitialedForKeepAlive) {
      updateGetKeepAliveResponseFromServerTimestamp();
    }

    _keepAliveTaskExecuting = false;
    if (!_keepAliveWillStop) {
      _keepAliveHandler =
          Timer(Duration(milliseconds: KEEP_ALIVE_INTERVAL), () {
        _keepAliveRunnable.call();
      });
    }
  }

  void _doTimeoutCheck() {
    bool isInitialedForKeepAlive = _isInitialedForKeepAlive();
    if (!isInitialedForKeepAlive) {
      int now = ClientCoreSDK.getCurrentTimeStamp();
      if (now - _lastGetKeepAliveResponseFromServerTimestamp >=
          NETWORK_CONNECTION_TIME_OUT) {
        Log.info("【IMCORE-TCP】心跳机制已判定网络断开，将进入断网通知和重连处理逻辑 ...", _tag);
        notifyConnectionLost();
        _keepAliveWillStop = true;
      }
    }
  }

  bool _isInitialedForKeepAlive() {
    return _lastGetKeepAliveResponseFromServerTimestamp == 0;
  }

  /// 心跳线程算法已判定需要与服务器的“通信通道”断开，调用此方法将进入框架的“通信通道”断开处理逻辑
  /// 本方法，目前属于 MobileIMSDK 框架算法的一部分，暂时无需也不建议由应用层开发者自行调用。
  void notifyConnectionLost() {
    stop();
    _networkConnectionLostObserver?.update(true, null);
  }

  /// 是否正在运行中
  bool isKeepAliveRunning() {
    return _keepAliveRunning;
  }

  /// 本类对象是否已补初始化过
  bool isInit() {
    return _init;
  }

  /// 收到服务端反馈的心跳包时调用此方法：作用是更新服务端最背后的响应时间戳
  /// 本方法的调用，目前属于 MobileIMSDK 算法的一部分，暂时无需也不建议由应用层自行调用。
  void updateGetKeepAliveResponseFromServerTimestamp() {
    _lastGetKeepAliveResponseFromServerTimestamp =
        ClientCoreSDK.getCurrentTimeStamp();
  }

  /// 设置网络断开事件观察者
  /// 本方法的调用，目前属于 MobileIMSDK 算法的一部分，暂时无需也不建议由应用层自行调用。
  void setNetworkConnectionLostObserver(
      MBObserver networkConnectionLostObserver) {
    _networkConnectionLostObserver = networkConnectionLostObserver;
  }

  ///会尝试首先调用 stop()方法，以便确保实例被启动前是真正处 于停止状态，这也意味着可无害调用本方法。
  ///
  /// 目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
  ///
  /// 参数:
  /// [immediately] - true 表示立即执行线程作业，否则直到 [KEEP_ALIVE_INTERVAL] 执行间隔的到来才 进行首次作业的执行。
  ///
  void start(bool immediately) {
    stop();
    var time = immediately ? 0 : KEEP_ALIVE_INTERVAL;
    _keepAliveHandler = Timer(Duration(milliseconds: time), () {
      _keepAliveRunnable.call();
    });
    _keepAliveRunning = true;
    _keepAliveWillStop = false;
    Log.info("【IMCORE-TCP】心跳 start()  time:$time ... ", _tag);
    _keepAliveTimeoutTimer.start(immediately);
  }

  /// 无条件停止本实例，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。
  void stop() {
    _keepAliveTimeoutTimer.stop();

    _keepAliveHandler?.cancel();
    _keepAliveRunning = false;
    _keepAliveWillStop = false;
    _lastGetKeepAliveResponseFromServerTimestamp = 0;
  }
}
