import 'dart:math';

class Protocol {
  bool _bridge = false;
  int _type = 0;
  String? _dataContent;
  String _from = "-1";
  String _to = "-1";
  String? _fp;
  bool _QoS = false;
  int? _retryCount;
  int _sm = -1;

  ///意义：应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型。
  ///注意：此值为-1时表示未定义。
  ///本字段为保留字段，不参与框架的核心算法，专留用应用 层自行定义和使用。 默认：-1
  int _typeu = -1;

  Protocol(this._type, this._dataContent, this._from, this._to,
      {bool? QoS, String? fingerPrint, int typeu = -1}) {
    _QoS = QoS ?? false;
    _typeu = typeu;
    _sm = -1;

    if (_QoS && fingerPrint == null) {
      _fp = genFingerPrint();
    } else {
      _fp = fingerPrint;
    }
  }

  //命名式构造方法，也可以是用工厂构造方法
  Protocol.fromJson(Map<String, dynamic> json)
      : _bridge = json['bridge'],
        _type = json['type'],
        _dataContent = json['dataContent'],
        _from = json['from'],
        _to = json['to'],
        _fp = json['fp'],
        _QoS = json['QoS'],
        _retryCount = json['retryCount'],
        _sm = json['sm'],
        _typeu = json['typeu'];

  //如果想写成协议，归档(json和对象互转时，为了使用方便)时，可以继承协议，那么可以用普通方法，而不是构造方法
  Map<String, dynamic> toJson() => {
        'bridge': _bridge,
        'type': _type,
        'dataContent': _dataContent,
        'from': _from,
        'to': _to,
        'fp': _fp,
        'QoS': _QoS,
        'retryCount': _retryCount,
        'sm': _sm,
        'typeu': _typeu,
      };

  getType() {
    return _type;
  }

  void setType(int type) {
    _type = type;
  }

  getDataContent() {
    return _dataContent;
  }

  void setDataContent(String dataContent) {
    _dataContent = dataContent;
  }

  getFrom() {
    return _from;
  }

  void setFrom(String from) {
    _from = from;
  }

  getTo() {
    return _to;
  }

  void setTo(String to) {
    _to = to;
  }

  getFp() {
    return _fp;
  }

  getRetryCount() {
    return _retryCount ?? 0;
  }

  void increaseRetryCount() {
    int count = _retryCount ?? 0;
    ++count;
    _retryCount = count;
  }

  isQoS() {
    return _QoS;
  }

  void setQoS(bool qoS) {
    _QoS = qoS;
  }

  isBridge() {
    return _bridge;
  }

  void setBridge(bool bridge) {
    _bridge = bridge;
  }

  getTypeu() {
    return _typeu;
  }

  void setTypeu(int typeu) {
    _typeu = typeu;
  }

  getSm() {
    return _sm;
  }

  void setSm(int sm) {
    _sm = sm;
  }

  static int genServerTimestamp() {
    return DateTime.now().millisecondsSinceEpoch;
  }

  static String genFingerPrint() {
    return (genServerTimestamp()).toString() + Random().nextDouble().toString();
  }
}
