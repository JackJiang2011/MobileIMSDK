class MBObserver {
  final Function(bool success, Object? extraObj) _cb;

  MBObserver(this._cb);

  void update(bool success, Object? extraObj) {
    _cb.call(success, extraObj);
  }
}
