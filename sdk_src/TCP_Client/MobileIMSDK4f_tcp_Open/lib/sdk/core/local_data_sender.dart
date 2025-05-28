import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/qos_4_send_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Ext.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/mb_observer.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/c/p_login_info.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/error_code.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/Protocol_factory.dart';
import 'local_socket_provider.dart';

class LocalDataSender {
  final String _tag = "LocalDataSender";

  //<editor-fold desc="单例">

  factory LocalDataSender.getInstance() => _instance;

  static late final LocalDataSender _instance = LocalDataSender._internal();

  LocalDataSender._internal();

  //</editor-fold>

  int sendLogout() {
    int code = 0;
    if (ClientCoreSDK.getInstance().isLoginHasInit()) {
      final loginInfo = ClientCoreSDK.getInstance().getCurrentLoginInfo()!;
      code = _send(ProtocolFactory.createPLogoutInfo(loginInfo));
    }

    if (code == 0) {
      ClientCoreSDK.getInstance().setLoginHasInit(false);
      ClientCoreSDK.getInstance().setCurrentLoginInfo(null);
      ClientCoreSDK.getInstance().setConnectedToServer(false);
    }

    return code;
  }

  int sendLogin(PLoginInfo loginInfo) {
    int codeForCheck = _checkBeforeSend();
    if (codeForCheck != ErrorCode.COMMON_CODE_OK) return codeForCheck;

    if (!LocalSocketProvider.getInstance().isLocalSocketReady()) {
      Log.info(
          "【IMCORE-TCP】发送登陆指令时，socket连接未就绪，首先开始尝试发起连接（登陆指令将在连接成功后的回调中自动发出）。。。。",
          _tag);

      MBObserver connectionDoneObserver = MBObserver((success, extraObj) {
        Log.info("【IMCORE-TCP】[来自 Tcp 的连接结果回调观察者通知]socket连接：$success ,$extraObj",
            _tag);
        if (success) {
          _sendLoginImpl(loginInfo);
        }
      });

      LocalSocketProvider.getInstance()
          .setConnectionDoneObserver(connectionDoneObserver);

      return LocalSocketProvider.getInstance().resetLocalSocket() != null
          ? ErrorCode.COMMON_CODE_OK
          : ErrorCodeForC.BAD_CONNECT_TO_SERVER;
    } else {
      return _sendLoginImpl(loginInfo);
    }
  }

  int _sendLoginImpl(PLoginInfo loginInfo) {
    int code = _send(ProtocolFactory.createPLoginInfo(loginInfo));
    if (code == 0) {
      ClientCoreSDK.getInstance().setCurrentLoginInfo(loginInfo);
    }
    return code;
  }

  int sendKeepAlive() {
    return _send(ProtocolFactory.createPKeepAlive(
        ClientCoreSDK.getInstance().getCurrentLoginInfo()!.getLoginUserId()));
  }

  int sendCommonData(String dataContentWidthStr, String to_user_id,
      {int typeu = -1, String? fingerPrint, bool QoS = true}) {
    return sendCommonDataObj(ProtocolFactory.createCommonData(
        dataContentWidthStr,
        ClientCoreSDK.getInstance().getCurrentLoginInfo()!.getLoginUserId(),
        to_user_id,
        QoS,
        fingerPrint: fingerPrint,
        typeu: typeu));
  }

  int sendCommonDataObj(Protocol? p) {
    if (p != null) {
      int code = _send(p);
      if (code == 0) {
        if (p.isQoS() && !QoS4SendDaemon.getInstance().exist(p.getFp())) {
          QoS4SendDaemon.getInstance().put(p);
        }
      }
      return code;
    } else {
      return ErrorCode.COMMON_INVALID_Protocol;
    }
  }

  int _send(Protocol Protocol) {
    int codeForCheck = _checkBeforeSend();
    if (codeForCheck != ErrorCode.COMMON_CODE_OK) {
      return codeForCheck;
    }

    var dataStr = Protocol.toJson().toJsonStr();
    Log.info(" _send 发送消息:$dataStr", _tag);

    var tcpSm = LocalSocketProvider.getInstance().getLocalSocket();
    Log.info(" _send tcpSm:${tcpSm?.isActive()}", _tag);
    if (tcpSm != null && tcpSm.isActive()) {
      return tcpSm.sendMsg(dataStr)
          ? ErrorCode.COMMON_CODE_OK
          : ErrorCode.COMMON_DATA_SEND_FAILED;
    } else {
      Log.info("【IMCORE-TCP】socket 未连接，无法发送，本条将被忽略（dataStr=$dataStr）!", _tag);
      return ErrorCode.COMMON_CODE_OK;
    }
  }

  int _checkBeforeSend() {
    if (!ClientCoreSDK.getInstance().isInitialed()) {
      return ErrorCodeForC.CLIENT_SDK_NO_INITIALED;
    }
    return ErrorCode.COMMON_CODE_OK;
  }
}
