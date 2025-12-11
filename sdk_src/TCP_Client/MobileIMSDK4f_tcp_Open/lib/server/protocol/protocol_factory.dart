import 'dart:convert';
import 'dart:typed_data';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Ext.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_error_response.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_keep_alive_response.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_kickout_info.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_login_info_response.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/c/p_keep_alive.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/c/p_login_info.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol_i_type.dart';

class ProtocolFactory {
  static const serverUserId = "0";

  static String create(Object c) {
    if (c.runtimeType == Map<String, dynamic>) {
      return (c as Map<String, dynamic>).toJsonStr();
    }
    return json.encode(c);
  }

  static T parseByteArray<T>(Uint8List fullProtocolJSONBytes, int len,
      T Function(Uint8List byteArray, int len) doParse) {
    return doParse(fullProtocolJSONBytes, len);
  }

  static T parseString<T>(String dataContentOfProtocol,
      T Function(String dataContentOfProtocol) doParse) {
    return doParse(dataContentOfProtocol);
  }

  static Protocol parseFromString(String dataContentOfProtocol) {
    return Protocol.fromJson(jsonDecode(dataContentOfProtocol));
  }

  static Protocol createPKeepAliveResponse(String to_user_id) {
    return Protocol(ProtocolTypeS.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE,
        create(PKeepAliveResponse()), serverUserId, to_user_id);
  }

  static PKeepAliveResponse parsePKeepAliveResponse(
      String dataContentOfProtocol) {
    return PKeepAliveResponse();
  }

  static Protocol createPKeepAlive(String from_user_id) {
    return Protocol(ProtocolTypeC.FROM_CLIENT_TYPE_OF_KEEP$ALIVE,
        create(PKeepAlive()), from_user_id, serverUserId);
  }

  static PKeepAlive parsePKeepAlive(String dataContentOfProtocol) {
    return PKeepAlive();
  }

  static Protocol createPErrorResponse(
      int errorCode, String errorMsg, String user_id) {
    return Protocol(ProtocolTypeS.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR,
        create(PErrorResponse(errorCode, errorMsg)), serverUserId, user_id);
  }

  static PErrorResponse parsePErrorResponse(String dataContentOfProtocol) {
    return PErrorResponse.fromJson(json.decode(dataContentOfProtocol));
  }

  static Protocol createPLogoutInfo(PLoginInfo pLoginInfo) {
    return Protocol(
        ProtocolTypeC.FROM_CLIENT_TYPE_OF_LOGOUT, null, pLoginInfo.getLoginUserId(), serverUserId);
  }

  static Protocol createPLoginInfo(PLoginInfo loginInfo) {
    return Protocol(
        ProtocolTypeC.FROM_CLIENT_TYPE_OF_LOGIN,
        loginInfo.toJson().toJsonStr(),
        loginInfo.getLoginUserId(),
        serverUserId);
  }

  static PLoginInfo parsePLoginInfo(String dataContentOfProtocol) {
    return PLoginInfo.fromJson(json.decode(dataContentOfProtocol));
  }

  static Protocol createPLoginInfoResponse(
      int code, int firstLoginTime, String user_id) {
    return Protocol(ProtocolTypeS.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN,
        create(PLoginInfoResponse(code, firstLoginTime)), serverUserId, user_id,
        QoS: true, fingerPrint: Protocol.genFingerPrint());
  }

  static PLoginInfoResponse parsePLoginInfoResponse(
      String dataContentOfProtocol) {
    return PLoginInfoResponse.fromJson(json.decode(dataContentOfProtocol));
  }

  static Protocol createCommonData(
      String dataContent, String from_user_id, String to_user_id, bool QoS,
      {String? fingerPrint, int typeu = -1}) {
    return Protocol(ProtocolTypeC.FROM_CLIENT_TYPE_OF_COMMON$DATA, dataContent,
        from_user_id, to_user_id,
        QoS: QoS, fingerPrint: fingerPrint, typeu: typeu);
  }

  static Protocol createRecivedBack(
      String from_user_id, String to_user_id, String recievedMessageFingerPrint,
      {bool bridge = false}) {
    Protocol p = Protocol(ProtocolTypeC.FROM_CLIENT_TYPE_OF_RECEIVED,
        recievedMessageFingerPrint, from_user_id, to_user_id);
    p.setBridge(bridge);
    return p;
  }

  static Protocol createPKickout(String to_user_id, int code, String reason) {
    return Protocol(ProtocolTypeS.FROM_SERVER_TYPE_OF_KICKOUT,
        create(PKickoutInfo(code, reason: reason)), serverUserId, to_user_id);
  }

  static PKickoutInfo parsePKickoutInfo(String dataContentOfProtocol) {
    return PKickoutInfo.fromJson(json.decode(dataContentOfProtocol));
  }
}
