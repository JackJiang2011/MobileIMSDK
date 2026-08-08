import 'dart:convert';
import 'dart:core';
import 'dart:io';
import 'dart:typed_data';

import 'package:mobile_im_sdk_flutter_tcp/sdk/core/keep_alive_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';

class TcpSocketManager {
  final tag = "TcpSocketManager";

  ///用于描述消息长度的固定长度
  static int tcpFrameFixedHeaderLength = 4; // 4 bytes
  static int tcpFrameMaxBodyLength = 6 * 1024; // 6K bytes

  late String _host;
  late int _port;
  Socket? _mSocket;
  bool _cancelOnError = false;
  bool _isActive = false;
  Function? _messageReceived;
  Function? _onConnectionLost;

  void init(String host, int port,
      {bool cancelOnError = false,
      Function? messageReceived,
      Function? onConnectionLost}) {
    _host = host;
    _port = port;
    _cancelOnError = cancelOnError;
    _messageReceived = messageReceived;
    _onConnectionLost = onConnectionLost;
  }

  void connectSocket({Function? onSuccess, Function? onError}) async {
    if (_mSocket != null) return;
    try {
      await Socket.connect(_host, _port,
              timeout: Duration(
                  milliseconds: KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT))
          .then((socket) {
        _mSocket = socket;
        _mSocket?.listen(_receivedMsgHandler,
            onError: _errorHandler,
            onDone: _doneHandler,
            cancelOnError: _cancelOnError);
        Log.info('---------连接成功------------$_mSocket', tag);
        _isActive = true;
        onSuccess?.call();
      });
    } catch (e) {
      onError?.call(e);
      _mSocket?.destroy();
      _mSocket = null;
      _isActive = false;
      Log.error("连接socket出现异常，e=${e.toString()}", tag);
    }
  }

  void close() {
    _doneHandler();
  }

  bool isActive() {
    return _isActive && _mSocket != null;
  }

  void _errorHandler(error, StackTrace trace) {
    Log.error("捕获socket异常信息：error=$error，trace=${trace.toString()}", tag);
    close();
  }

  void _doneHandler() {
    _isActive = false;
    _mSocket?.destroy();
    _mSocket = null;
    _onConnectionLost?.call();
    Log.warn("socket关闭处理", tag);
  }

  void _receivedMsgHandler(Uint8List data) async {
    _decodeHandle(data);
  }

  //<editor-fold desc="解码消息">

  /// 缓存的网络数据，暂未处理（一般这里有数据，说明当前接收的数据不是一个完整的消息，需要等待其它数据的到来拼凑成一个完整的消息） */
  Uint8List _cacheData = Uint8List(0);

  /// 解码处理方法
  /// 处理服务器发过来的数据，注意，这里要处理粘包，这个data参数不一定是一个完整的包
  void _decodeHandle(Uint8List newData) async {
    //拼凑当前最新未处理的网络数据
    _cacheData = Uint8List.fromList(_cacheData + newData);

    //缓存数据长度符合最小包长度才尝试解码
    while (_cacheData.length >= tcpFrameFixedHeaderLength) {
      //读取消息长度
      var byteData = _cacheData.buffer.asByteData();
      var bodyLen = byteData.getInt32(0);

      //数据长度小于消息长度，说明不是完整的数据，暂不处理
      if (_cacheData.length < bodyLen + tcpFrameFixedHeaderLength) {
        return;
      }

      //读取 body 数据
      Uint8List pbBody;
      if (bodyLen > 0) {
        pbBody = _cacheData.sublist(
            tcpFrameFixedHeaderLength, tcpFrameFixedHeaderLength + bodyLen);
      } else {
        pbBody = Uint8List.fromList(List.empty());
      }

      //整理缓存数据
      int totalLen = tcpFrameFixedHeaderLength + pbBody.length;
      _cacheData = _cacheData.sublist(totalLen, _cacheData.length);

      var msgStr = utf8.decode(pbBody);
      Log.info("[msg] 接收到的消息：$msgStr", tag);
      //处理消息
      _messageReceived?.call(msgStr);
    }
  }

  //</editor-fold>

  ///发送数据
  bool sendMsg(String message) {
    var result = false;
    if (!isActive()) return result;

    //序列化pb对象
    Uint8List pbBody = Uint8List.fromList(utf8.encode(message));
    int dataLength = pbBody.length;

    //包头部分
    var header = ByteData(tcpFrameFixedHeaderLength);
    header.setInt32(0, dataLength);

    var msgInfoIntList = pbBody.buffer.asUint8List();
    //包头+message组合成一个完整的数据包
    var msg = header.buffer.asUint8List() + msgInfoIntList;

    //给服务器发消息
    try {
      _mSocket?.add(msg);
      Log.info("[msg] 给服务端发送消息，$message", tag);
      result = true;
    } catch (e) {
      Log.error("send捕获异常：message=$message，e=${e.toString()}", tag);
    }
    return result;
  }
}
