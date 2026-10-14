import 'dart:async';
import 'dart:collection';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';

class Handler {
  final HashMap<String, Timer> _mRunnableMap = HashMap();

  void postDelayed(Runnable runnable, int delay) {
    var key = runnable.getKey();
    _mRunnableMap[key]?.cancel();
    _mRunnableMap[key] = Timer(Duration(milliseconds: delay), () {
      runnable.call();
    });
  }

  void removeCallbacks(Runnable runnable) {
    var key = runnable.getKey();
    _mRunnableMap[key]?.cancel();
    _mRunnableMap.remove(key);
  }

  void clearAll() {
    _mRunnableMap.clear();
  }
}

typedef Runnable = Function;

extension RunnableExt on Runnable {
  String getKey() {
    var key = hashCode.toString();
    Log.info("Runnable Handler key:$key ","RunnableExt getKey");
    return key;
  }
}
