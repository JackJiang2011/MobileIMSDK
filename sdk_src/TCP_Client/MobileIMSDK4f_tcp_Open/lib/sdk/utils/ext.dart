import 'dart:convert';

extension MapExt on Map<String, dynamic> {
  String toJsonStr() {
    var newMap = <String, dynamic>{};
    for (var element in entries) {
      if (element.value != null) newMap[element.key] = element.value;
    }
    return json.encode(newMap);
  }
}

extension Tm on DateTime {
  String hms() {
    return "$hour:$minute:$second";
  }
}

String nowHmsStrWithMark() => "[${DateTime.now().hms()}]";
