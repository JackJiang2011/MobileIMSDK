import 'dart:async';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';

class MBSimpleTimer {
  late final _tag = "MBSimpleTimer:$hashCode";

  Timer? _timer;
  int _interval = -1;
  late Function _doAction;

  MBSimpleTimer(int interval, Function action) {
    _interval = interval;
    _doAction = action;
  }

  void setInterval(int interval) {
    _interval = interval;
  }

  void _doCallback() {
    try {
      _doAction.call();
    } catch (e) {
      Log.error(e, _tag);
    }
  }

  void start(bool immediately) {
    stop();

    _timer = Timer.periodic(Duration(milliseconds: _interval), (timer) {
      _doCallback();
    });

    if (immediately) {
      _doCallback();
    }
  }

  void stop() {
    _timer?.cancel();
  }
}
