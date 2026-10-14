// ignore_for_file: constant_identifier_names

import 'dart:async';
import 'package:mobile_im_sdk_flutter_tcp/sdk/client_core_sdk.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_sender.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';

class AutoReLoginDaemon {
  static const String _tag = "AutoReLoginDaemon";
  static const int AUTO_RE$LOGIN_INTERVAL = 3000;

  //<editor-fold desc="单例">

  factory AutoReLoginDaemon.getInstance() => _instance;

  static late final AutoReLoginDaemon _instance = AutoReLoginDaemon._internal();

  AutoReLoginDaemon._internal() {
    init();
  }

  //</editor-fold>

  bool _autoReLoginRunning = false;
  bool _init = false;
  bool _executing = false;
  late void Function() _callback;
  Timer? _timer;

  void init() {
    if (_init) return;

    _callback = () async {
      if (!_executing) {
        final int code = await _doSendLogin();
        _onSendLogin(code);
      }
    };

    _init = true;
  }

  Future<int> _doSendLogin() async {
    // todo 重连有时会进行两次？ 第一次会被关掉， 倒是也可正常使用，但是怕高并发时会白白多一倍的连接数
    //[INFO] - [15:37:48.748][IMCORE-tcp]与{uid:null}/10.10.2.61:55978的会话建立(channelActive)了... | (ServerCoreHandler^sessionCreated:396)
    // [DEBUG] - [15:37:48.754]-Dio.netty.recycler.maxCapacityPerThread: 4096 | (Recycler^<clinit>:100)
    // [DEBUG] - [15:37:48.754]-Dio.netty.recycler.maxSharedCapacityFactor: 2 | (Recycler^<clinit>:101)
    // [DEBUG] - [15:37:48.754]-Dio.netty.recycler.linkCapacity: 16 | (Recycler^<clinit>:102)
    // [DEBUG] - [15:37:48.755]-Dio.netty.recycler.ratio: 8 | (Recycler^<clinit>:103)
    // [DEBUG] - [15:37:48.755]-Dio.netty.recycler.delayedQueue.ratio: 8 | (Recycler^<clinit>:104)
    // [INFO] - [15:37:48.771][IMCORE-tcp]与{uid:null}/10.10.2.61:55976的会话建立(channelActive)了... | (ServerCoreHandler^sessionCreated:396)
    // [INFO] - [15:37:48.791][IMCORE-tcp]>> 客户端{uid:null}/10.10.2.61:55978发过来的登陆信息内容是：uid=b、token=b、firstLoginTime=1690443347718 | (LogicProcessor^processLogin:161)
    // [DEBUG] - [15:37:48.792]【DEBUG_回调通知】正在调用回调方法：OnVerifyUserCallBack...(extra=null) | (ServerEventListenerImpl^onUserLoginVerify:60)
    // [DEBUG] - [15:37:48.801]【@】当前在线用户共(1)人-------------------> | (OnlineProcessor^__printOnline:212)
    // [DEBUG] - [15:37:48.801]【IM_回调通知onUserLoginSucess】用户：b 上线了！ | (ServerEventListenerImpl^onUserLoginSucess:78)
    // [WARN] - [15:37:53.822][IMCORE-tcp]>> 客户端{uid:null}/10.10.2.61:55976尚未登陆，{}处理未继续. | (LocalSendHelper^replyDataForUnlogined:368)
    // [WARN] - [15:37:53.824][IMCORE-tcp]>> 客户端{uid:null}/10.10.2.61:55976未登陆，服务端反馈发送成功？true（会话即将关闭） | (LocalSendHelper$2^update:379)
    // [WARN] - [15:37:53.825][IMCORE-unknow]【注意】会话{uid:null}/10.10.2.61:55976被系统close了，但它里面没有存放user_id，它 很可能是没有成功合法认证而被提前关闭，从而正常释放资源。 | (ServerCoreHandler^sessionClosed:378)
    // [INFO] - [15:37:59.815][IMCORE-tcp]客户端{uid:b}/10.10.2.61:55978的会话已超时失效，很可能是对方非正常通出或网络故障，即 将以会话异常的方式执行关闭流程 ... | (MBTCPClientInboundHandler^exceptionCaught:58)
    // [DEBUG] - [15:37:59.815][IMCORE-tcp]此客户端的Channel抛出了exceptionCaught，原因是：null，可以提前close掉了哦！ | (ServerCoreHandler^exceptionCaught:154)
    // io.netty.handler.timeout.ReadTimeoutException: null
    // [INFO] - [15:37:59.816][IMCORE-unknow]{uid:b}/10.10.2.61:55978的会话已关闭(user_id=b, firstLoginTime=1690443347718)了... | (ServerCoreHandler^sessionClosed:321)
    // [INFO] - [15:37:59.816].......... 【0】[当前正在被关闭的session] session.hashCode=79431750, session.ip+port=/10.10.2.61:55978 | (ServerCoreHandler^sessionClosed:327)
    // [INFO] - [15:37:59.817].......... 【1】[处于在线列表中的session] session.hashCode=79431750, session.ip+port= | (ServerCoreHandler^sessionClosed:332)
    // [DEBUG] - [15:37:59.817]【DEBUG_回调通知onUserLogout】用户：b 离线了（beKickoutCode=-1）！ | (ServerEventListenerImpl^onUserLogout:94)
    // [INFO] - [15:37:59.892][IMCORE-tcp]与{uid:null}/10.10.2.61:55980的会话建立(channelActive)了... | (ServerCoreHandler^sessionCreated:396)
    // [INFO] - [15:37:59.896][IMCORE-tcp]>> 客户端{uid:null}/10.10.2.61:55980发过来的登陆信息内容是：uid=b、token=b、firstLoginTime=1690443347718 | (LogicProcessor^processLogin:161)
    // [DEBUG] - [15:37:59.897]【DEBUG_回调通知】正在调用回调方法：OnVerifyUserCallBack...(extra=null) | (ServerEventListenerImpl^onUserLoginVerify:60)
    // [DEBUG] - [15:37:59.898]【@】当前在线用户共(1)人-------------------> | (OnlineProcessor^__printOnline:212)
    // [DEBUG] - [15:37:59.898]【IM_回调通知onUserLoginSucess】用户：b 上线了！ | (ServerEventListenerImpl^onUserLoginSucess:78)
    _executing = true;
    Log.info(
        "$_tag【IMCORE-TCP】自动重新登陆线程执行中, autoReLogin=${ClientCoreSDK.autoReLogin} ...",
        _tag);
    int code = -1;
    if (ClientCoreSDK.autoReLogin) {
      code = LocalDataSender.getInstance()
          .sendLogin(ClientCoreSDK.getInstance().getCurrentLoginInfo()!);
    }
    return code;
  }

  void _onSendLogin(int result) {
    if (result == 0) {
      _executing = false;
    }
  }

  void stop() {
    _timer?.cancel();
    _autoReLoginRunning = false;
    _executing = false;
  }

  void start(bool immediately) {
    stop();

    int time = immediately ? 0 : AUTO_RE$LOGIN_INTERVAL;

    _timer = Timer(Duration(milliseconds: time), () {
      _timer = Timer.periodic(
          const Duration(milliseconds: AUTO_RE$LOGIN_INTERVAL), (timer) {
        if (_autoReLoginRunning) _callback();
      });
    });

    Log.info("自动重登启动", _tag);
    _autoReLoginRunning = true;
  }

  bool isAutoReLoginRunning() {
    return _autoReLoginRunning;
  }

  bool isInit() {
    return _init;
  }
}
