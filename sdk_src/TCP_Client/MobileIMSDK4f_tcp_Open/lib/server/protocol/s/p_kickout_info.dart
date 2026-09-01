// ignore_for_file: constant_identifier_names

class PKickoutInfo {
  static const KICK_OUT_FOR_DUPLICATE_LOGIN = 1;
  static const KICK_OUT_FOR_ADMIN = 2;
  int _code = -1;
  String _reason = "";

  PKickoutInfo(int code, {String reason = ""}) {
    _code = code;
    _reason = reason;
  }

  //命名式构造方法，也可以是用工厂构造方法
  PKickoutInfo.fromJson(Map<String, dynamic> json)
      : _code = json['code'],
        _reason = json['reason'];

  //如果想写成协议，归档(json和对象互转时，为了使用方便)时，可以继承协议，那么可以用普通方法，而不是构造方法
  Map<String, dynamic> toJson() => {'code': _code, 'reason': _reason};

  getCode() {
    return _code;
  }

  void setCode(int code) {
    _code = code;
  }

  getReason() {
    return _reason;
  }

  void setReason(String reason) {
    _reason = reason;
  }
}
