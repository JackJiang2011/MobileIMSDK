// ignore_for_file: constant_identifier_names

class ErrorCode {
  static const int COMMON_CODE_OK = 0;
  static const int COMMON_NO_LOGIN = 1;
  static const int COMMON_UNKNOWN_ERROR = 2;
  static const int COMMON_DATA_SEND_FAILED = 3;
  static const int COMMON_INVALID_Protocol = 4;
}

class ErrorCodeForC extends ErrorCode {
  static const int BROKEN_CONNECT_TO_SERVER = 201;
  static const int BAD_CONNECT_TO_SERVER = 202;
  static const int CLIENT_SDK_NO_INITIALED = 203;
  static const int LOCAL_NETWORK_NOT_WORKING = 204;
  static const int TO_SERVER_NET_INFO_NOT_SETUP = 205;
}

class ErrorCodeForS extends ErrorCode {
  static const int RESPONSE_FOR_UN_LOGIN = 301;
}
