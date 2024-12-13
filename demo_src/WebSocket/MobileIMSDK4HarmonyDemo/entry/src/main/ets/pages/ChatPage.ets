/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import {ErrorCode, ClientCoreSDK, PKickoutInfo} from 'mobileimsdk/Index'
import { ChatInputView } from './components/ChatInputView';
import { CustomeTitleBar } from './components/ChatTitleBar'
import { MsgView_normal } from './components/msg_view/NormalMsgView';
import { MsgView_system } from './components/msg_view/SystemMsgView';
import Message from './model/Message';
import MsgType from './constants/MsgType';
import MessagesDataSource from './model/MessagesDataSource';
import IMClientManager from '../IMClientManager';
import UIEvent from './constants/UIEvent';
import {router } from '@kit.ArkUI';
import ToolKits from './utils/ToolKits';
import { common } from '@kit.AbilityKit';
import { MsgTimeView } from './components/msg_view/MsgTimeView';

const TAG: string = '[ChatPage] '

/**
 * 主界面（聊天单页）。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Entry
@Component
struct ChatPage {
  /** 聊天列表的滚动条组件 */
  private scroller: Scroller = new Scroller();

  /** 连接状态：false 连接断开、true 连接正常 */
  @State connectStatus: boolean = ClientCoreSDK.getInstance().connectedToServer;
  /** 心跳状态：true表示刚刚已发送心跳包，本心跳状态仅仅是为了在ui界面上实现心跳呼吸灯效果 */
  @State keepAliveStatus: boolean = false;

  /** 聊天消息列表对应的数据源对象（本对象与MessageProvider配合，用于实现UI界面与数据组的解偶）*/
  messagesDataSource: MessagesDataSource = new MessagesDataSource();

  /** 事件回调：增加了聊天消息 */
  meessageAddedEventCallback?: Function;
  /** 事件回调：更新了聊天消息（单条聊天消息） */
  meessageUpdateEventCallback?: Function;
  /** 事件回调：更新了聊天消息（多条聊天消息） */
  meessagesUpdateEventCallback?: Function;
  /** 事件回调：掉线重连完成 */
  loginCompleteEventCallback?: Function;
  /** 事件回调：掉线了 */
  disconnectedEventCallback?: Function;
  /** 事件回调：心跳了一次 */
  keepAliveEventCallback?: Function;
  /** 事件回调：你被踢了 */
  kickoutEventCallback?: Function;

  /**
   * 生命周期中只被调用一次，类似于iOS中的viewDidLoad、Android中的onCreate.
   */
  aboutToAppear() {
    // 添加事件通知
    this.addEvents();
    // 添加一条预定义的系统消息在列表列表里
    IMClientManager.getInstance().getMessageProvider().addSystemMessage('当前通信层，基于鸿蒙NEXT原生WebSocket');
  }

  /**
   * 界面被销毁时被调用。
   */
  aboutToDisappear() {
    // 取消事件通知
    this.removeEvents();
  }

  /**
   * 界面显示时被调用。
   */
  onPageShow(): void {
    // 本界面显示时，将输入法软键盘避让模式设置为KeyboardAvoidMode.RESIZE（即压缩窗体，而不是将窗体整体上移）
    ToolKits.setKeyboardAvoidModeToRESIZE(this.getUIContext(), true);
    // 设置全面屏状态栏颜色
    ToolKits.setStatusBarColor(getContext(this), ToolKits.chat_page_title_color, ToolKits.chat_page_title_background_color);
  }

  /**
   * 界面隐藏时被调用。
   */
  onPageHide(): void {
    // 本界面不显示时，将输入法软键盘避让模式还原为系统默认的KeyboardAvoidMode.OFFSET
    ToolKits.setKeyboardAvoidModeToRESIZE(this.getUIContext(), false);
  }

  /**
   * 添加事件监听。
   */
  addEvents() {
    // 【1】增加了聊天消息
    IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_messageAdded, this.meessageAddedEventCallback = (index: number) => {
      // 通知ListView的数据源，刷新消息显示
      this.messagesDataSource.notifyDataAdd(index);
      // 滚动消息列表至底部（以备显示最新消息）
      this.scrollToBottom();
    });

    // 【2】更新了聊天消息（单条聊天消息）
    IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_messageUpdate, this.meessageUpdateEventCallback = (index: number) => {
      // 通知ListView的数据源，刷新消息显示
      this.messagesDataSource.notifyDataChange(index);
    });

    // 【3】更新了聊天消息（多条聊天消息）
    IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_messagesUpdate, this.meessagesUpdateEventCallback = (updateIndexes: number[]) => {
      // 通知ListView的数据源，刷新消息显示（多条消息就直接reload，这样简单直接）
      this.messagesDataSource.notifyDataReload();
    });

    // 【4】掉线重连完成
    IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_onIMAfterLoginComplete, this.loginCompleteEventCallback = (code: number) => {
      // 登陆或重连成功
      if(code == ErrorCode.COMMON_CODE_OK) {
        this.refreshConnecteStatus();
      }
    });

    // 【5】掉线了
    IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_onIMDisconnected, this.disconnectedEventCallback = () => {
      this.refreshConnecteStatus();
    });

    // 【6】心跳了一次
    IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_onIMPing, this.keepAliveEventCallback = () => {
      this.keepAliveStatus = true;
      setTimeout(() => {
        this.keepAliveStatus = false;
      }, 500);
    });

    // 【7】你被踢了
    IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_onKickout, this.kickoutEventCallback = (kickoutInfo: PKickoutInfo) => {
      this.beKickout(kickoutInfo);
    });
  }

  /**
   * 取消事件监听。
   */
  removeEvents() {
    // 【1】
    IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_messageAdded, this.meessageAddedEventCallback);
    // 【2】
    IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_messageUpdate, this.meessageUpdateEventCallback);
    // 【3】
    IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_messagesUpdate, this.meessagesUpdateEventCallback);
    // 【4】
    IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_onIMAfterLoginComplete, this.loginCompleteEventCallback);
    // 【5】
    IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_onIMDisconnected, this.disconnectedEventCallback);
    // 【6】
    IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_onIMPing, this.keepAliveEventCallback);
    // 【7】
    IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_onKickout, this.kickoutEventCallback);
  }

  /**
   * 滚动消息列表到最底部，以备显示最新消息。
   */
  scrollToBottom() {
    this.scroller.scrollEdge(Edge.Bottom);
  }

  /**
   * 刷新连接状态。
   */
  refreshConnecteStatus() {
    this.connectStatus = ClientCoreSDK.getInstance().connectedToServer;
  }

  /**
   * 被踢的处理逻辑。
   */
  beKickout(kickoutInfo: PKickoutInfo) {
    // 首先释放IM所占资源
    IMClientManager.getInstance().releaseMobileIMSDK();

    // 提示信息
    let alertContent: string = '';
    if (kickoutInfo.code === PKickoutInfo.KICKOUT_FOR_DUPLICATE_LOGIN) {
      alertContent = '账号已在其它地方登陆，当前会话已断开，请退出后重新登陆！';
    } else if (kickoutInfo.code === PKickoutInfo.KICKOUT_FOR_ADMIN) {
      alertContent = '已被管理员强行踢出聊天，当前会话已断开！';
    } else {
      alertContent = '你已被踢出聊天，当前会话已断开（kickoutInfo=' + JSON.stringify(kickoutInfo) + '）！';
    }

    ClientCoreSDK.Log.error(TAG + alertContent);

    // 确认Action
    let primaryAction: Callback<void> = () => {
      this.switchToLoginPage();
    };

    // 取消Action
    let secondaryAction: Callback<void> = () => {
      let context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext;
      ToolKits.exitSystem(context);
    };

    // 显示对话框
    ToolKits.showAlertDialog(alertContent, '重新登录', undefined, primaryAction,'退出应用', $r('sys.color.ohos_id_color_badge_red'), secondaryAction);
  }

  /**
   * 切换到登陆界面。
   */
  switchToLoginPage() {
    // init MobileIMSDK first（必须保证此代码被调用，否则IM框架无法完成IM服务器的连接等工作）
    IMClientManager.getInstance().initMobileIMSDK(getContext(this).getApplicationContext(), getContext(this).eventHub);
    // goto login page!
    router.replaceUrl({ url: 'pages/LoginPage', });
  }


  build() {
    Column() {
      // 聊天界面自定义标题栏
      CustomeTitleBar({ connectStatus: this.connectStatus, keepAliveStatus: this.keepAliveStatus })

      // 聊天界面内容区
      Column() {
        // List性能需极致优化的情况下，还可使用“组件复用”技术，详见官方资料：
        // https://developer.huawei.com/consumer/cn/doc/best-practices-V5/bpta-best-practices-long-list-V5#section36781044162218
        List({ scroller: this.scroller }) {
          LazyForEach(this.messagesDataSource, (m: Message, index: number) => {
            ListItem() {
              // 消息item的整体布局
              Column() {
                /* 显示消息时间 */
                MsgTimeView({ message: m })

                /* 显示消息内容 */
                if (m.msgType === MsgType.TYPE_SYSTEAM$INFO) {
                  MsgView_system({ message: m })
                } else if (m.msgType === MsgType.TYPE_TEXT) {
                  MsgView_normal({ message: m })
                } else {
                  // TODO: 更多消息类型支持。。。
                }
              }
            }
            // 列表顶部和底部的的空白间隔
            .margin({top: index === 0 ? 15 : 0, bottom: 15})
          }, (m: Message) => MessagesDataSource.messageItemKey(m))// 提示：在List中使用LazyForEach时，响应式ui需要key变化才会更新
        }
        // 监听组件触屏事件，确保触碰聊天列表时，能自动收起输入法软键盘，提升用户体验
        .onTouch((event: TouchEvent) => {
          // 收起软键盘
          ToolKits.hideSoftInputMethod(this.getUIContext());
        })
        // 监听组件大小变化事件（用于实现当软键盘显示时，列表高度被压缩后，消息不能自动显示最后一条的问题）
        .onSizeChange((oldValue: SizeOptions, newValue: SizeOptions) => {
          let oldHeight = oldValue.height, newHeight = newValue.height;
          oldHeight = (oldHeight === undefined ? 0 : oldHeight);
          newHeight = (newHeight === undefined ? 0 : newHeight);
          // 列表高度变小，可以认为是软键盘显示了
          let toBeSmall = (newHeight >0 && newHeight < oldHeight);
          if(toBeSmall) {
            // 消息列表滚动到底部，确保最新一条消息被显示
            this.scrollToBottom();
          }
        })
        .scrollBar(BarState.Off)
        .edgeEffect(EdgeEffect.Spring)
        .width("100%")
        .height('100%')
        .layoutWeight(1)
        .listDirection(Axis.Vertical)
      }
      .backgroundColor("#f0f2f6")
      .layoutWeight(1)

      // 底部消息输入区
      ChatInputView()
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
    // 适配流海屏（顶部状态栏透明并穿过，并透过底部上划操作区）
    .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP, SafeAreaEdge.BOTTOM])
  }
}

