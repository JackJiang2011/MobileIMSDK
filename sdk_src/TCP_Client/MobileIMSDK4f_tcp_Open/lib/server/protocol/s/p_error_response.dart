import 'dart:core';

class PErrorResponse {
  int _errorCode = -1;
  String? _errorMsg;

  PErrorResponse(this._errorCode,this._errorMsg);

  //命名式构造方法，也可以是用工厂构造方法
  PErrorResponse.fromJson(Map<String, dynamic> json)
      : _errorCode = json['errorCode'],
        _errorMsg = json['errorMsg'];

  //如果想写成协议，归档(json和对象互转时，为了使用方便)时，可以继承协议，那么可以用普通方法，而不是构造方法
  Map<String, dynamic> toJson() => {
    'errorCode': _errorCode,
    'errorMsg': _errorMsg
  };

  getErrorCode() {
    return _errorCode;
  }

  void setErrorCode(int errorCode) {
    _errorCode = errorCode;
  }

  getErrorMsg() {
    return _errorMsg;
  }

  void setErrorMsg(String errorMsg) {
    _errorMsg = errorMsg;
  }
}
