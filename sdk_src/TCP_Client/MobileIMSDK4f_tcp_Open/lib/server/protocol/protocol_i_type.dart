// ignore_for_file: constant_identifier_names

abstract class ProtocolType {}

abstract class ProtocolTypeC extends ProtocolType {
  static const int FROM_CLIENT_TYPE_OF_LOGIN = 0;
  static const int FROM_CLIENT_TYPE_OF_KEEP$ALIVE = 1;
  static const int FROM_CLIENT_TYPE_OF_COMMON$DATA = 2;
  static const int FROM_CLIENT_TYPE_OF_LOGOUT = 3;
  static const int FROM_CLIENT_TYPE_OF_RECEIVED = 4;
  static const int FROM_CLIENT_TYPE_OF_ECHO = 5;
}

abstract class ProtocolTypeS extends ProtocolType{
  static const int FROM_SERVER_TYPE_OF_RESPONSE$LOGIN = 50;
  static const int FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE = 51;
  static const int FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR = 52;
  static const int FROM_SERVER_TYPE_OF_RESPONSE$ECHO = 53;
  static const int FROM_SERVER_TYPE_OF_KICKOUT = 54;
}
