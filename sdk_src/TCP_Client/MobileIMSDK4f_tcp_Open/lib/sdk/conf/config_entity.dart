// ignore_for_file: constant_identifier_names

import 'package:mobile_im_sdk_flutter_tcp/sdk/core/keep_alive_daemon.dart';

class ConfigEntity {
  static String serverIP = "192.168.31.108"; //
  static int serverPort = 8901;

  static void setSenseMode(SenseMode mode) {
    int keepAliveInterval = 0;
    int networkConnectionTimeout = 0;
    switch (mode) {
      case SenseMode.MODE_3S:
        {
          keepAliveInterval = 3000; // 3s
          networkConnectionTimeout = keepAliveInterval * 1 + 2000; // 5s
          break;
        }
      case SenseMode.MODE_5S:
        {
          keepAliveInterval = 5000; // 3s
          networkConnectionTimeout = keepAliveInterval * 1 + 3000; // 8s
          break;
        }
      case SenseMode.MODE_10S:
        keepAliveInterval = 10000; // 10s
        networkConnectionTimeout = keepAliveInterval * 1 + 5000; // 15s
        break;
      case SenseMode.MODE_15S:
        keepAliveInterval = 15000; // 15s
        networkConnectionTimeout = keepAliveInterval * 1 + 5000; // 20s
        break;
      case SenseMode.MODE_30S:
        keepAliveInterval = 30000; // 30s
        networkConnectionTimeout = keepAliveInterval * 1 + 5000; // 35s
        break;
      case SenseMode.MODE_60S:
        keepAliveInterval = 60000; // 60s
        networkConnectionTimeout = keepAliveInterval * 1 + 5000; // 65s
        break;
      case SenseMode.MODE_120S:
        keepAliveInterval = 120000; // 120s
        networkConnectionTimeout = keepAliveInterval * 1 + 5000; // 125s
        break;
    }

    if (keepAliveInterval > 0) {
      KeepAliveDaemon.KEEP_ALIVE_INTERVAL = keepAliveInterval;
    }
    if (networkConnectionTimeout > 0) {
      KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT = networkConnectionTimeout;
    }
  }
}

/// 即时通讯核心框架预设的敏感度模式.
///
/// <p>
/// 对于客户端而言，此模式决定了用户与服务端网络会话的健康模式，原则上越敏感客户端的体验越好。
///
/// <p>
/// <b>重要说明：</b><u>客户端本模式的设定必须要与服务端的模式设制保持一致</u>，否则可能因参数的不一致而导致
/// IM算法的不匹配，进而出现不可预知的问题。
///
enum SenseMode {
  /// 此模式下：<br>
  /// * KeepAlive心跳问隔为3秒；<br>
  /// * 5秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大6秒延迟后)后仍未收到服务端反馈）。
  MODE_3S,

  /// 此模式下：<br>
  /// * KeepAlive心跳问隔为5秒；<br>
  /// * 8秒后未收到服务端心跳反馈即认为连接已断开（相当于连续1个心跳间隔+3秒链路延迟容忍时间后仍未收到服务端反馈）。
  MODE_5S,

  /// 此模式下：<br>
  /// * KeepAlive心跳问隔为10秒；<br>
  /// * 15秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大20秒延迟后)后仍未收到服务端反馈）。
  MODE_10S,

  /// 此模式下：<br>
  /// * KeepAlive心跳问隔为15秒；<br>
  /// * 20秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大30秒延迟后)后仍未收到服务端反馈）。
  ///
  /// @since 5.0
  MODE_15S,

  /// 此模式下：<br>
  /// * KeepAlive心跳问隔为30秒；<br>
  /// * 35秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大60秒延迟后)后仍未收到服务端反馈）。
  MODE_30S,

  /// 此模式下：<br>
  /// * KeepAlive心跳问隔为60秒；<br>
  /// * 65秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大120秒延迟后)后仍未收到服务端反馈）。
  MODE_60S,

  /// 此模式下：<br>
  /// * KeepAlive心跳问隔为120秒；<br>
  /// * 125秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2个心跳间隔(即算法最大240秒延迟后)后仍未收到服务端反馈）。
  MODE_120S
}
