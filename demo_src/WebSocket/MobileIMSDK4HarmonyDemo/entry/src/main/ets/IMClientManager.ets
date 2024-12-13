/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { ClientCoreSDK, LogLevel, SenseMode, LogUtils, SocketEvent, PLoginInfoResponse, Logger, Protocal, PErrorResponse, ErrorCode, PKickoutInfo, AutoReLoginDaemon, KeepAliveDaemon, LocalDataReciever, LocalDataSender, LocalSocketProvider, QoS4ReciveDaemon, QoS4SendDaemon } from "mobileimsdk/Index";
import { common } from "@kit.AbilityKit";
import UIEvent from "./pages/constants/UIEvent";
import Message from "./pages/model/Message";
import MessagesProvider from "./pages/model/MessagesProvider";
import MsgSendStatus from "./pages/constants/MsgSendStatus";

const TAG: string = '[IMClientManager] ';

// MobileIMSDK服务端的WebSocket服务地址（如服务端已开启SSL，此处请使用wss）
const IM_SERVER_URL: string = 'ws://rbcore.52im.net:3000/websocket';// TODO: 默认地址是官方演示服务，需要改成连接您自已的服务端

/**
 * MobileIMSDK的管理类。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
export default class IMClientManager {

  /** 本类的全局单例 */
  private static instance: IMClientManager;

  /** MobileIMSDK是否已被初始化. true表示已初化完成，否则未初始化. */
  private init: boolean = false;

  /** application对象引用（暂时没用到，先留着吧） */
  private application?: common.ApplicationContext;

  /** 聊天消息的缓存数据提供者对象（集中管理所有的聊天消息和指令，消息的UI层显示将通过MessagesDataSource与UI界面解偶）*/
  private messageProvider: MessagesProvider = new MessagesProvider();

  /**
   * 获得本类的全局单例。
   *
   * @returns IMClientManager单例
   */
  static getInstance(): IMClientManager {
    if (!IMClientManager.instance) {
      IMClientManager.instance = new IMClientManager();
    }
    return IMClientManager.instance;
  }

  private constructor() {
  }

  /**
   * MobileIMSDK的初始化方法。在使用MobileIMSDK请务必确保先调用一次本方法。
   *
   * @param application application对象引用
   * @param eventHub EventHub订阅对象（用于事件分发）
   */
  initMobileIMSDK(application: common.ApplicationContext, eventHub: common.EventHub): void {
    if (!this.init) {
      this.application = application;

      //* 【特别注意】请确保首先进行核心库的初始化（这是不同于iOS和Java端的地方？)
      ClientCoreSDK.getInstance().init(eventHub);

      //* 开启Debug开关将输出相关的log，方便调试，平时建议关闭之
      ClientCoreSDK.DEBUG = true;
      AutoReLoginDaemon.DEBUG = true;
      KeepAliveDaemon.DEBUG = true;
      LocalDataReciever.DEBUG = true;
      LocalDataSender.DEBUG = true;
      LocalSocketProvider.DEBUG = true;
      QoS4ReciveDaemon.DEBUG = true;
      QoS4SendDaemon.DEBUG = true;

      //* 设置服务器的websocket连接地址
      ClientCoreSDK.websocketUrl = IM_SERVER_URL;

      //* SDK核心IM框架的敏感度模式设置（默认MBSenseMode.MODE_5S，此设置必须与服务端保持一致！！）
      SenseMode.setSenseMode(SenseMode.MODE_3S);

      //* 设置默认的日志输出级别
      LogUtils.setLevel(LogLevel.DEBUG);//(LogLevel.WARN);//

      //* 注册MobileIMSDK框架开放各种事件监听
      this.registerSocketEvent();

      //* 设置已被初始化标识
      this.init = true;
    }
  }

  /**
   * 释放IM框架所占用的资源，在退出登陆时请务必调用本方法，否则重新登陆将不能正常实现（指APP进程不退出时切换账号这种情况）。
   */
  releaseMobileIMSDK(): void {
    // 释放IM核心库资源
    ClientCoreSDK.getInstance().release();
    // 重置本类的初始化标识
    this.resetInitFlag();
    // 取消注册事件监听
    this.unregisterSocketEvent();
    // 清空聊天数据
    this.messageProvider.clear();
  }

  /**
   * 注册MobileIMSDK框架开放各种事件监听。
   */
  private registerSocketEvent() {
    let emiter: common.EventHub = this.getEmitter();


    /*
     * 本地用户的登陆结果回调事件通知（此事件发生时表示客户端已登陆/连接或重连完成）。
     *
     * @param loginInfoRes 即PLoginInfoResponse对象，API文档地址：http://docs.52im.net/extend/docs/api/mobileimsdk/server_tcp/net/x52im/mobileimsdk/server/protocal/s/PLoginInfoResponse.html
     */
    emiter.on(SocketEvent.SOCKET_EVENT_ON_LOGIN_RESPONSE, (loginInfoRes: PLoginInfoResponse) => {
      if (loginInfoRes) {
        // 返回码（用于获知被服务端是否正确登陆证的关键返回码）
        let code = loginInfoRes.code;
        this.getLog().info('[应用层]  本客户端 SOCKET_EVENT_ON_LOGIN_RESPONSE 事件已经触发✅（code=' + code + '）');

        //  认证成功
        if (0 === code) { // 每次掉线都会重新走一遍认证过程
          // // 通知应用层对登陆结果处理展现或处理
          // emiter.emit(UIEvent.UIEVENT_onIMAfterLoginSucess, code);

          this.getLog().info("[应用层]  ● ● ● ● ●【已成功登录或重连至 MobileIMSDK 服务器】● ● ● ● ●");
        }
        // 未认证成功（认证失败了）
        else {
          // // 通知应用层对登陆结果处理展现或处理
          // emiter.emit(UIEvent.UIEVENT_onIMAfterLoginFailed, code);

          this.getLog().error('[应用层]  本客户端的 SOCKET_EVENT_ON_LOGIN_RESPONSE 事件中：登陆认证失败❎【code=' + code + '】');
        }

        // 通知应用层对登陆结果进行展现或其它处理
        emiter.emit(UIEvent.UIEVENT_onIMAfterLoginComplete, code);
      } else {
        this.getLog().error('[应用层]  本客户端的 SOCKET_EVENT_ON_LOGIN_RESPONSE 事件成功返回，但返回的loginInfoRes是空的✴️！');
      }
    });

    /*
     * 与服务端的通信断开的回调事件通知（此事件发生时表示客户端已掉线）。
     * 该消息只有在客户端连接服务器成功之后网络异常中断之时触发。<br>
     * 导致与与服务端的通信断开的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G/5G等同开情况下的网络切换、手机系统的省电策略等。
     *
     * @param code 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
     */
    emiter.on(SocketEvent.SOCKET_EVENT_ON_LINK_CLOSE, (code: number) => {
      this.getLog().warn('[应用层]  本客户端 SOCKET_EVENT_ON_LINK_CLOSE 事件已经触发❌【END】，(对不起，你与IM服务器的网络连接断开了(掉线罗))!');
      // 通知应用层网络掉线了
      emiter.emit(UIEvent.UIEVENT_onIMDisconnected);
    });

    /*
     * 收到普通消息的回调事件通知（此事件表示收到IM消息了）。
     * 应用层可以将此消息进一步按自已的IM协议进行定义，从而实现完整的即时通信软件逻辑。
     *
     * @param protocal Protocal对象，见：http://docs.52im.net/extend/docs/api/mobileimsdk/server_netty/net/openmob/mobileimsdk/server/protocal/Protocal.html
     */
    emiter.on(SocketEvent.SOCKET_EVENT_ON_RECIEVE_MESSAGE, (protocal: Protocal) =>{

      this.getLog().info('[应用层]  >> 收到[聊天消息]📩：' + JSON.stringify(protocal));

      let m = Message.createChatMsgEntity_INCOME_TEXT(protocal.from, 'UID: '+protocal.from, protocal.dataContent, 0);
      m.fingerPrintOfProtocal = protocal.fp;
      // 将收到的消息放入全局消息缓存集合中（MessageProvider.putMessage()方法中会同时通知ui层进行刷新显示）
      this.messageProvider.putMessage(m);
    });

    /*
     * 服务端反馈的出错信息回调事件通知。
     *
     * @param errorCode 错误码，定义在常量表 ErrorCode.ForS 类中，见：http://docs.52im.net/extend/docs/api/mobileimsdk/server/net/openmob/mobileimsdk/server/protocal/ErrorCode.ForS.html
     * @param errorMsg 描述错误内容的文本信息
     */
    emiter.on(SocketEvent.SOCKET_EVENT_ON_ERROR_RESPONSE, (errorRes: PErrorResponse) =>{
      this.getLog().error("[应用层]  收到服务端错误消息🛑，errorCode=" + errorRes.errorCode + ", errorMsg=" + errorRes.errorMsg);
      if (errorRes.errorCode === ErrorCode.RESPONSE_FOR_UNLOGIN) {
        //this.getLog().error(TAG+ "服务端会话已失效，自动登陆/重连将启动! ("+errorCode+")");
      } else {
        //this.getLog().error(TAG+ "Server反馈错误码：" + errorCode + ",errorMsg=" + errorMsg);
      }
    });

    /*
     * 消息未送达的回调事件通知.
     *
     * @param lostMessages 由MobileIMSDK QoS算法判定出来的未送达消息列表，应用层可通过指纹特征码找到原消息并可以UI上将其标记为”发送失败“以便即时告之用户
     */
    emiter.on(SocketEvent.SOCKET_EVENT_MESSAGE_LOST, (lostMessages: Protocal[]) => {
      if (lostMessages && lostMessages.length > 0) {
        this.getLog().info('[应用层]  收到系统的未实时送达事件通知，当前共有' + lostMessages.length +'个包QoS保证机制结束，判定为【无法实时送达😡】！');

        // 更新未送达消息的发送状态（MessageProvider.updateMessagesSendStatus()方法中会同时通知ui层进行刷新显示）
        this.messageProvider.updateMessagesSendStatus(lostMessages, MsgSendStatus.sendFaild);
      }
    });

    /*
     * 消息已被对方收到的回调事件通知.
     *
     * 目前，判定消息被对方收到是有两种可能：
     *  1) 对方确实是在线并且实时收到了；
     *  2) 对方不在线或者服务端转发过程中出错了，由服务端进行离线存储成功后的反馈（此种情况严格来讲不能算是“已被
     * 		收到”，但对于应用层来说，离线存储了的消息原则上就是已送达了的消息：因为用户下次登陆时肯定能通过HTTP协议取到）。
     *
     * @param theFingerPrint {String} 已被收到的消息的指纹特征码（唯一ID），应用层可据此ID来找到原先已发生的消息并可在UI是将其标记为”已送达“或”已读“以便提升用户体验
     */
    emiter.on(SocketEvent.SOCKET_EVENT_MESSAGE_BE_RECIEVED, (theFingerPrint: string) =>{
      if (theFingerPrint) {
        this.getLog().info('[应用层]  收到对方已收到消息事件的通知哦😁，fp=' + theFingerPrint);

        // 更新已送达消息的发送状态（MessageProvider.updateMessageSendStatus()方法中会同时通知ui层进行刷新显示）
        this.messageProvider.updateMessageSendStatus(theFingerPrint, MsgSendStatus.beReceived);
      }
    });

    /* “自动重连尝试中”事件 */
    emiter.on(SocketEvent.SOCKET_EVENT_RECONNECT_ATTEMPT, (code: number) =>{
      if(code === 2) {
        this.getLog().error('[应用层]  本客户端 SOCKET_EVENT_RECONNECT_ATTEMPT 事件已触发🔗 (code=' + code + ')');
      }
    });

    /* 心跳包已发出事件 */
    emiter.on(SocketEvent.SOCKET_EVENT_PING, () =>{
      // this.getLog().info('[应用层]  心跳请求已发出 😁→');
      emiter.emit(UIEvent.UIEVENT_onIMPing);
    });

    /* 心跳响应已收到事件 */
    emiter.on(SocketEvent.SOCKET_EVENT_PONG, () =>{
      // this.getLog().info('[应用层]  心跳响应已收到 ← 😁');
      emiter.emit(UIEvent.UIEVENT_onIMPong);
    });

    /* “被踢”事件（此事件由服务端检测到当前loginUserId已经别处登陆时发出） */
    emiter.on(SocketEvent.SOCKET_EVENT_KICKOUT, (kickoutInfo: PKickoutInfo) => {
      this.getLog().error('[应用层]  本客户端 SOCKET_EVENT_KICKOUT 事件已触发🈲，kickoutInfo=' + JSON.stringify(kickoutInfo));

      // UI层去实现这个被踢事件后的处理逻辑（比如弹提示框，确认后退出到登陆界面等）
      emiter.emit(UIEvent.UIEVENT_onKickout, kickoutInfo);
    });
  }

  /**
   * 取消注册MobileIMSDK框架开放各种事件监听。
   */
  private unregisterSocketEvent() {
    let emiter: common.EventHub = this.getEmitter();
    // 清空设置的各种回调
    emiter.off(SocketEvent.SOCKET_EVENT_ON_LOGIN_RESPONSE);
    emiter.off(SocketEvent.SOCKET_EVENT_ON_LINK_CLOSE);
    emiter.off(SocketEvent.SOCKET_EVENT_ON_RECIEVE_MESSAGE);
    emiter.off(SocketEvent.SOCKET_EVENT_ON_ERROR_RESPONSE);
    emiter.off(SocketEvent.SOCKET_EVENT_MESSAGE_LOST);
    emiter.off(SocketEvent.SOCKET_EVENT_MESSAGE_BE_RECIEVED);
    emiter.off(SocketEvent.SOCKET_EVENT_RECONNECT_ATTEMPT);
    emiter.off(SocketEvent.SOCKET_EVENT_PING);
    emiter.off(SocketEvent.SOCKET_EVENT_PONG);
    emiter.off(SocketEvent.SOCKET_EVENT_KICKOUT);
  }

  /**
   * 重置本类的初始化标识。
   */
  resetInitFlag(): void {
    this.init = false;
  }

  /**
   * 返回本类持有的事件订阅对象引用。
   *
   * @returns 事件订阅对象引用
   */
  getEmitter(): common.EventHub {
    return ClientCoreSDK.getInstance().getEventHub();
  }

  /**
   * 返回日志对象。
   *
   * @returns Logger对象
   */
  getLog() :Logger {
    return ClientCoreSDK.Log;
  }

  /**
   * 返回聊天消息的缓存数据提供者对象。
   *
   * @returns
   */
  getMessageProvider() {
    return this.messageProvider;
  }
}