import 'package:flutter/foundation.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/ext.dart';
import 'package:stack_trace/stack_trace.dart';

class Log {
  static Function? cacheLogInfo;

  static void info(dynamic message, String? tag) {
    if (kDebugMode) {
      var info = "[info] $tag ${nowHmsStrWithMark()}--> $message";
      WLog(info, mode: WLogMode.info);
      cacheLogInfo?.call(info);
    }
  }

  static void debug(dynamic message, String? tag) {
    if (kDebugMode) {
      var info = "[debug] $tag ${nowHmsStrWithMark()}--> $message";
      WLog(info, mode: WLogMode.debug);
      cacheLogInfo?.call(info);
    }
  }

  static void warn(dynamic message, String? tag) {
    if (kDebugMode) {
      var info = "[warning] $tag ${nowHmsStrWithMark()}--> $message";
      WLog(info, mode: WLogMode.warning);
      cacheLogInfo?.call(info);
    }
  }

  static void error(dynamic message, String? tag) {
    if (kDebugMode) {
      var info = "[error] $tag ${nowHmsStrWithMark()}--> $message";
      WLog(info, mode: WLogMode.error);
      cacheLogInfo?.call(info);
    }
  }
}

enum WLogMode {
  debug, // 💚 DEBUG
  warning, // 💛 WARNING
  info, // 💙 INFO
  error, // ❤️ ERROR
}

String WLog(dynamic msg, {WLogMode mode = WLogMode.debug}) {
  if (kReleaseMode) {
    // release模式不打印
    return "";
  }
  var chain = Chain.current(); // Chain.forTrace(StackTrace.current);
  // 将 core 和 flutter 包的堆栈合起来（即相关数据只剩其中一条）
  chain =
      chain.foldFrames((frame) => frame.isCore || frame.package == "flutter");
  // 取出所有信息帧
  final frames = chain.toTrace().frames;
  // 找到当前函数的信息帧
  final idx = frames.indexWhere((element) => element.member == "WLog");
  if (idx == -1 || idx + 1 >= frames.length) {
    return "";
  }
  // 调用当前函数的函数信息帧
  final frame = frames[idx + 1];

  var modeStr = "";
  switch (mode) {
    case WLogMode.debug:
      modeStr = "💚 DEBUG";
      break;
    case WLogMode.warning:
      modeStr = "💛 WARNING";
      break;
    case WLogMode.info:
      modeStr = "💙 INFO";
      break;
    case WLogMode.error:
      modeStr = "❤️ ERROR";
      break;
  }

  final printStr =
      "$modeStr ${frame.uri.toString().split("/").last}(${frame.line}) - $msg ";
  debugPrint(printStr);
  return printStr;
}
