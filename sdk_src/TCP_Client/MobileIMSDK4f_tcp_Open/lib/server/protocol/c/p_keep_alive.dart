class PKeepAlive {
  PKeepAlive();

  //命名式构造方法，也可以是用工厂构造方法
  PKeepAlive.fromJson(Map<String, dynamic> json);

  //如果想写成协议，归档(json和对象互转时，为了使用方便)时，可以继承协议，那么可以用普通方法，而不是构造方法
  Map<String, dynamic> toJson() => {};

}
