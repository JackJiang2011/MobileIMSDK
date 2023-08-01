class PLoginInfoResponse {
  int _code = 0;
  int _firstLoginTime = 0;

  PLoginInfoResponse(int code, int firstLoginTime) {
    _code = code;
    _firstLoginTime = firstLoginTime;
  }

  //命名式构造方法，也可以是用工厂构造方法
  PLoginInfoResponse.fromJson(Map<String, dynamic> json)
      : _code = json['code'],
        _firstLoginTime = json['firstLoginTime'];

  //如果想写成协议，归档(json和对象互转时，为了使用方便)时，可以继承协议，那么可以用普通方法，而不是构造方法
  Map<String, dynamic> toJson() =>
      {'code': _code, 'firstLoginTime': _firstLoginTime};

  int getCode() {
    return _code;
  }

  void setCode(int code) {
    _code = code;
  }

  getFirstLoginTime() {
    return _firstLoginTime;
  }

  void setFirstLoginTime(int firstLoginTime) {
    _firstLoginTime = firstLoginTime;
  }
}
