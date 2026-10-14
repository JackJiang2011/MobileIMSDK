import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/auto_relogin_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/keep_alive_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_sender.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_socket_provider.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/qos_4_receive_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/qos_4_send_daemon.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/mb_observer.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/error_code.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol_i_type.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/protocol_factory.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_error_response.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_kickout_info.dart';
import 'package:mobile_im_sdk_flutter_tcp/server/protocol/s/p_login_info_response.dart';

class LocalDataReceiver {
  final String _tag = "LocalDataReceiver";

  //<editor-fold desc="单例">

  factory LocalDataReceiver.getInstance() => _instance;

  static late final LocalDataReceiver _instance = LocalDataReceiver._internal();

  LocalDataReceiver._internal() {
    _init();
  }

  //</editor-fold>

  void _init() {}

  void handleProtocolJson(String fullProtocolOfBodyJson) {
    _handleProtocolImpl(fullProtocolOfBodyJson);
  }

  void _handleProtocolImpl(String? fullProtocolOfBodyJson) {
    if (fullProtocolOfBodyJson == null || fullProtocolOfBodyJson.isEmpty) {
      Log.info("【IMCORE-TCP】无效的 fullProtocolOfBodyJson（.length == 0）！", _tag);
      return;
    }

    final Protocol pFromServer;
    try {
      pFromServer = ProtocolFactory.parseFromString(fullProtocolOfBodyJson);

      if (pFromServer.isQoS()) {
        Log.info("pb预处理：$pFromServer", _tag);
        if (pFromServer.getType() ==
                ProtocolTypeS.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN &&
            ProtocolFactory.parsePLoginInfoResponse(
                        pFromServer.getDataContent())
                    .getCode() !=
                0) {
          Log.info("IMCORE-TCP】这是服务端的登陆返回响应包，且服务端判定登陆失败(即code!=0)，本次无需发送ACK应答包！",
              _tag);
        } else {
          if (QoS4ReceiveDaemon.getInstance()
              .hasReceived(pFromServer.getFp())) {
            Log.info(
                "【IMCORE-TCP】【QoS机制】${pFromServer.getFp()} 已经存在于发送列表中，这是重复包，通知应用层收到该包！",
                _tag);
            QoS4ReceiveDaemon.getInstance().addReceived(pFromServer);
            _sendReceivedBack(pFromServer);
            return;
          }

          QoS4ReceiveDaemon.getInstance().addReceived(pFromServer);
          _sendReceivedBack(pFromServer);
        }
      }
    } catch (e) {
      Log.error("pb预处理错误：${e.toString()}", _tag);
      return;
    }

    try {
      switch (pFromServer.getType()) {
        case ProtocolTypeC.FROM_CLIENT_TYPE_OF_COMMON$DATA:
          {
            _onReceivedCommonData(pFromServer);
            break;
          }
        case ProtocolTypeS.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE:
          {
            _onServerResponseKeepAlive();
            break;
          }
        case ProtocolTypeC.FROM_CLIENT_TYPE_OF_RECEIVED:
          {
            _onMessageRecievedACK(pFromServer);
            break;
          }
        case ProtocolTypeS.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN:
          {
            _onServerResponseLogin(pFromServer);
            break;
          }
        case ProtocolTypeS.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR:
          {
            _onServerResponseError(pFromServer);
            break;
          }
        case ProtocolTypeS.FROM_SERVER_TYPE_OF_KICKOUT:
          {
            _onKickOut(pFromServer);
            break;
          }
        default:
          Log.info(
              "【IMCORE-TCP】收到的服务端消息类型：${pFromServer.getType()}，但目前该类型客户端不支持解析和处理！",
              _tag);
          break;
      }
    } catch (e) {
      Log.info(
          "【IMCORE-TCP】处理消息的过程中发生了错误. ${e.toString()},${pFromServer.toJson()}",
          _tag);
    }
  }

  void _onReceivedCommonData(Protocol pFromServer) {
    Log.info(
        ">>>>>>>>>>>>>>>>>>>>>>>>>>>>收到${pFromServer.getFrom()}发过来的消息：${pFromServer.getDataContent()},${pFromServer.getTo()}",
        _tag);

    ClientCoreSDK.getInstance().getChatMessageEvent()?.onReceiveMessage(
        pFromServer.getFp(),
        pFromServer.getFrom(),
        pFromServer.getDataContent(),
        pFromServer.getTypeu());
  }

  void _onServerResponseKeepAlive() {
    Log.info("【IMCORE-TCP】收到服务端回过来的Keep Alive心跳响应包.", _tag);
    KeepAliveDaemon.getInstance()
        .updateGetKeepAliveResponseFromServerTimestamp();
  }

  void _onMessageRecievedACK(Protocol pFromServer) {
    String theFingerPrint = pFromServer.getDataContent();

    Log.info(
        "【IMCORE-TCP】【QoS】收到 ${pFromServer.getFrom()}发过来的指纹为$theFingerPrint的应答包.",
        _tag);

    ClientCoreSDK.getInstance()
        .getMessageQoSEvent()
        ?.messagesBeReceived(theFingerPrint);

    QoS4SendDaemon.getInstance().removeByFp(theFingerPrint);
  }

  void _onServerResponseLogin(Protocol pFromServer) {
    PLoginInfoResponse loginInfoRes =
        ProtocolFactory.parsePLoginInfoResponse(pFromServer.getDataContent());
    if (loginInfoRes.getCode() == 0) {
      if (!ClientCoreSDK.getInstance().isLoginHasInit()) {
        ClientCoreSDK.getInstance()
            .saveFirstLoginTime(loginInfoRes.getFirstLoginTime());
      }
      _fireConnectedToServer();
    } else {
      Log.info("【IMCORE-TCP】登陆验证失败，错误码=${loginInfoRes.getCode()}！", _tag);
      LocalSocketProvider.getInstance().closeLocalSocket();
      ClientCoreSDK.getInstance().setConnectedToServer(false);
    }

    ClientCoreSDK.getInstance()
        .getChatBaseEvent()
        ?.onLoginResponse(loginInfoRes.getCode());
  }

  void _onServerResponseError(Protocol pFromServer) {
    PErrorResponse errorRes =
        ProtocolFactory.parsePErrorResponse(pFromServer.getDataContent());
    if (errorRes.getErrorCode() == ErrorCodeForS.RESPONSE_FOR_UN_LOGIN) {
      ClientCoreSDK.getInstance().setLoginHasInit(false);

      Log.info("【IMCORE-TCP】收到服务端的“尚未登陆”的错误消息，心跳线程将停止，请应用层重新登陆.", _tag);
      KeepAliveDaemon.getInstance().stop();
      AutoReLoginDaemon.getInstance().start(false);
    }

    ClientCoreSDK.getInstance()
        .getChatMessageEvent()
        ?.onErrorResponse(errorRes.getErrorCode(), errorRes.getErrorMsg());
  }

  void _onKickOut(Protocol pFromServer) {
    Log.info("【IMCORE-TCP】收到服务端发过来的“被踢”指令.", _tag);

    ClientCoreSDK.getInstance().release();

    PKickoutInfo kickOutInfo =
        ProtocolFactory.parsePKickoutInfo(pFromServer.getDataContent());
    ClientCoreSDK.getInstance().getChatBaseEvent()?.onKickOut(kickOutInfo);

    ClientCoreSDK.getInstance().getChatBaseEvent()?.onLinkClose(-1);
  }

  void _fireConnectedToServer() {
    Log.info("【IMCORE-TCP】 取得和服务器的连接.", _tag);

    ClientCoreSDK.getInstance().setLoginHasInit(true);
    AutoReLoginDaemon.getInstance().stop();

    KeepAliveDaemon.getInstance()
        .setNetworkConnectionLostObserver(MBObserver((success, extraObj) {
      _fireDisconnectedToServer();
    }));

    KeepAliveDaemon.getInstance().start(false);

    QoS4SendDaemon.getInstance().startup(true);
    QoS4ReceiveDaemon.getInstance().startup(true);
    ClientCoreSDK.getInstance().setConnectedToServer(true);
  }

  void _fireDisconnectedToServer() {
    Log.info("【IMCORE-TCP】 失去和服务器的连接.", _tag);

    ClientCoreSDK.getInstance().setConnectedToServer(false);
    LocalSocketProvider.getInstance().closeLocalSocket();

    QoS4SendDaemon.getInstance().stop();
    QoS4ReceiveDaemon.getInstance().stop();

    // 建议：此参数可由true改为false，防止服务端重启等情况下，客户端立即重连等
    AutoReLoginDaemon.getInstance().start(true);

    ClientCoreSDK.getInstance().getChatBaseEvent()?.onLinkClose(-1);
  }

  void _sendReceivedBack(final Protocol pFromServer) async {
    if (pFromServer.getFp() != null) {
      var pb = ProtocolFactory.createRecivedBack(
          pFromServer.getTo(), pFromServer.getFrom(), pFromServer.getFp(),
          bridge: pFromServer.isBridge());
      var i = LocalDataSender.getInstance().sendCommonDataObj(pb);
      Log.info(
          "【IMCORE-TCP】【QoS】向 ${pFromServer.getFrom()} 发送 ${pFromServer.getFp()} 包的应答包成功,from= ${pFromServer.getTo()}, resultCode = $i!",
          _tag);
    } else {
      Log.info(
          "【IMCORE-TCP】【QoS】收到 ${pFromServer.getFrom()} 发过来需要QoS的包，但它的指纹码却为null！无法发应答包！",
          _tag);
    }
  }
}
