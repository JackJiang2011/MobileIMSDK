import 'package:flutter/foundation.dart';
import 'package:logger/logger.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/ext.dart';

class Log {
  static Function? cacheLogInfo;

  static final Logger _logger = Logger(
    printer: PrefixPrinter(PrettyPrinter()),
  );

  static void i(dynamic message, String? tag) {
    if (kDebugMode) {
      var info = "$tag ${nowHmsStrWithMark()}--> $message";
      print(info);
      cacheLogInfo?.call(info);
    }
  }

  static void d(dynamic message, String? tag) {
    if (kDebugMode) {
      _logger.d("$tag --> $message", time: DateTime.now());
    }
  }

  static void w(dynamic message, String? tag) {
    if (kDebugMode) {
      _logger.w("$tag --> $message", time: DateTime.now());
    }
  }

  static void e(dynamic message, String? tag) {
    if (kDebugMode) {
      _logger.e("$tag --> $message", time: DateTime.now());
    }
  }

  static void f(dynamic message, String? tag) {
    if (kDebugMode) {
      _logger.f("$tag --> $message", time: DateTime.now());
    }
  }
}
