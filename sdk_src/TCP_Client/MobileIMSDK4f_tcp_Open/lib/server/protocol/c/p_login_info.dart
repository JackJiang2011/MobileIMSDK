import 'dart:core';

class PLoginInfo {
  String? _loginUserId;
  String? _loginToken;
  String? _extra;
  int _firstLoginTime = 0;

  PLoginInfo(String this._loginUserId, {String? loginToken, String? extra}) {
    _loginToken = loginToken;
    _extra = extra;
  }

  //命名式构造方法，也可以是用工厂构造方法
  PLoginInfo.fromJson(Map<String, dynamic> json)
      : _loginUserId = json['loginUserId'],
        _loginToken = json['loginToken'],
        _extra = json['extra'],
        _firstLoginTime = json['firstLoginTime'];

  //如果想写成协议，归档(json和对象互转时，为了使用方便)时，可以继承协议，那么可以用普通方法，而不是构造方法
  Map<String, dynamic> toJson() => {
        'loginUserId': _loginUserId,
        'loginToken': _loginToken,
        'extra': _extra,
        'firstLoginTime': _firstLoginTime
      };

  getLoginUserId() {
    return _loginUserId;
  }

  void setLoginUserId(String loginUserId) {
    _loginUserId = loginUserId;
  }

  getLoginToken() {
    return _loginToken;
  }

  void setLoginToken(String loginToken) {
    _loginToken = loginToken;
  }

  getExtra() {
    return _extra;
  }

  void setExtra(String extra) {
    _extra = extra;
  }

  getFirstLoginTime() {
    return _firstLoginTime;
  }

  void setFirstLoginTime(int firstLoginTime) {
    _firstLoginTime = firstLoginTime;
  }

  bool isFirstLogin() {
    return _firstLoginTime <= 0;
  }
}
