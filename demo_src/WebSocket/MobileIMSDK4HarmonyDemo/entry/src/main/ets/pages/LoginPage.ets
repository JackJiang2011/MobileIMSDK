/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { common, Want } from '@kit.AbilityKit';
import { router, window, KeyboardAvoidMode } from '@kit.ArkUI';
import { LoginInputView } from './components/LoginInputView';
import ToolKits from './utils/ToolKits';
import IMClientManager from '../IMClientManager';
import UIEvent from './constants/UIEvent';
import { LocalDataSender, PLoginInfo, ErrorCode } from 'mobileimsdk/Index';

const TAG: string = '[LoginPage] ';

/**
 * 登陆界面。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
@Entry({ routeName: 'LoginPage' })
@Component
struct LoginPage {

  /** 登陆超时时间定义（单位：毫秒）*/
  readonly RETRY_DELAY: number = 6000;

  /** 密码输入框是否处于焦点状态（用于优化当输入法软键盘处于显示状态时，输入法软键盘会档住“登录”按钮的问题，优化方法就是自动缩小界面上方的Logo图片大小）*/
  @State passwordFocused: boolean = false;

  /** 登录名 */
  @State loginName: string = '';
  /** 登录密码 */
  @State loginPassword: string = '';

  /** 是否正在登录中（true表示是，否则不是） */
  @State @Watch('watchLoginingStatus') isLogining: boolean = false;

  /** 保存登录超时定时器id（此id方便用于关闭定时器时使用） */
  private loginingTimerId: number = -1;

  /** 登录成功后的回调 */
  private onLoginSucessObserver: Function = (code: number) => {};

  /**
   * 生命周期中只被调用一次，类似于iOS中的viewDidLoad、Android中的onCreate.
   */
  aboutToAppear() {
    this.onLoginSucessObserver = (code: number) => {
      // * 已收到服务端登陆反馈则当然应立即取消显示登陆进度提示等
      this.isLogining = false;

      // 服务端返回的登陆结果值：登陆成功
      if(code == ErrorCode.COMMON_CODE_OK) {

        // TODO: -> 友情提示：登陆MobileIMSDK服务器成功后的事情在此实现即可！

        // 进入主界面
        router.replaceUrl({ url: 'pages/ChatPage'});
      }
      // 登陆失败
      else {
        // 显示提示框
        ToolKits.showAlertDialog('Sorry，登录失败，错误码='+ code, '知道了', undefined, () =>{}, '', undefined, () =>{});
      }
    };
  }

  /**
   * 登陆。
   */
  doLogin(): void {
    // 输入检查
    if (ToolKits.isEmpty(this.loginName, true)) {
      ToolKits.showToast('登录账号不能为空!');
      return;
    }

    // 开始登录
    this.doLoginImpl(this.loginName, this.loginPassword);
  }

  /*
   * 真正的登陆信息发送实现方法。
   */
  doLoginImpl(loginUserId: string, loginToken: string) {
    // * 立即显示登陆处理进度提示等（并将同时启动超时检查线程）
    this.isLogining = true;

    // * 将要提交的登陆信息对象
    let loginInfo: PLoginInfo = new PLoginInfo();
    loginInfo.loginUserId = loginUserId;
    loginInfo.loginToken = loginToken;

    // * 发送登陆数据包(提交登陆名和密码)
    let code: number = LocalDataSender.getInstance().sendLogin(loginInfo);
    if(code == ErrorCode.COMMON_CODE_OK) {
      ToolKits.showToast('登录请求已发出。。。');
    } else {
      ToolKits.showToast('登录请求发送失败，错误码：'+ code);
      // * 登陆信息没有成功发出时当然无条件取消显示登陆进度条
      this.isLogining = false;
    }
  }

  /**
   * Watch登录状态的改变。
   */
  watchLoginingStatus(): void {
    if(this.isLogining) {
      // * 设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）【2】
      IMClientManager.getInstance().getEmitter().on(UIEvent.UIEVENT_onIMAfterLoginComplete, this.onLoginSucessObserver);

      // 开启登录超时计时器
      this.startLoginTimer();
    } else {
      //## try to bug FIX ! 20160810：此observer本身执行完成才设置为nil，解决之前过早被nil而导致有时怎么也无法跳过登陆界面的问题
      // * 取消设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）【1】
      IMClientManager.getInstance().getEmitter().off(UIEvent.UIEVENT_onIMAfterLoginComplete)

      // 关闭登录超时计时器
      this.stopLoginTimer();
    }
  }

  /**
   * 开启登录超时计时器。
   */
  startLoginTimer() {
    // 先确保清除之前的计时器
    this.stopLoginTimer();
    // 登陆超时计时器
    this.loginingTimerId = setTimeout(() => {
      this.onLoginTimeout();
    }, this.RETRY_DELAY);
  }

  /**
   * 关闭登录超时计时器。
   */
  stopLoginTimer() {
    if(this.loginingTimerId != -1) {
      clearTimeout(this.loginingTimerId);
      this.loginingTimerId = -1;
    }
  }

  /**
   * 登录超时后要执行的逻辑。
   */
  onLoginTimeout() {
    // * 清除登录状态等
    this.isLogining = false;

    // 确认Action
    let confrimAction: Callback<void> = () => {
      // 重试
      this.doLogin();
    };

    // 退出Action
    let cancelAction: Callback<void> = () => {
      // 取消登陆中的状态
      this.isLogining = false;
    };

    // 显示超时提示窗
    ToolKits.showAlertDialog('登录超时，可能是网络故障或服务器无法连接，是否重试？', '重试！', undefined, confrimAction, '取消', $r('sys.color.ohos_id_color_badge_red'), cancelAction);
  }

  /**
   * 联系作者。
   */
  doContactAuthor() {
    try {
      let want: Want = {
        action: 'ohos.want.action.viewData',
        entities: ['entity.system.browsable'],
        uri: 'http://www.52im.net/'
      }
      let context = getContext(this) as common.UIAbilityContext;
      context.startAbility(want)
      console.info(`explicit start ability succeed`)
    } catch (error) {
      console.info(`explicit start ability failed with ${error.code}`)
    }
  }

  build() {
    Stack() {
      GridRow({ columns: 4 }) {
        GridCol({ span: 4, offset: 0  }) {
          Column() {
            Image($r('app.media.app_icon'))
              .width(this.passwordFocused? 45 : 96)
              .aspectRatio(1)
              .margin({ top: 56 })
              .border({width: 1, radius: 22, color: '#eceef1'})
              .animation({ duration: 250, curve: Curve.EaseOut, iterations: 1, playMode: PlayMode.Normal })
            Row() {
              Text('MobileIMSDK')
                .fontSize(30)
                .fontColor($r('sys.color.ohos_id_color_text_primary'))
                .lineHeight(40)
                .fontWeight(FontWeight.Medium)
              Text('鸿蒙')
                .fontFamily('HarmonyHeiTi')
                .fontColor(Color.White)
                .fontSize(13)
                .fontWeight(500)
                .backgroundColor(Color.Black)
                .padding({top:4, bottom:4, left: 6, right: 6})
                .borderRadius(6)
                .margin({left: 5})
            }
            .margin({ top: 20 })

            Row() {
              Text('欢迎使用MobileIMSDK鸿蒙端演示程序')
                .fontSize(14)
                .fontColor($r('sys.color.ohos_id_color_text_secondary'))
                .lineHeight(19)
              Text('v2.0')
                .fontSize(12)
                .fontColor('#a0a2a6')
                .lineHeight(19)
                .margin({left: 5})
            }
            .margin({ top: 4 })

            // 输入区组件
            LoginInputView({
              loginName: this.loginName,
              loginPassword: this.loginPassword,
              passwordFocused: this.passwordFocused
            })

            // 登录按钮
            Button({
              type: ButtonType.Normal,
              stateEffect: true
            }) {
              Row() {
                // 正在登陆则显示一个进度条
                if(this.isLogining) {
                  LoadingProgress().width(24).height(24).margin({ right: 5 }).color(0xFFFFFF)
                }

                Text(this.isLogining  ? '正在登录 ...' : '登录')
                  .fontSize(16)
                  .fontWeight(FontWeight.Bold)
                  .fontColor(Color.White)
              }
            }
            .borderRadius(20)
            .width('100%')
            .backgroundColor('#0A59F7')
            .height(40)
            .margin({ top: 30 })
            .enabled(!this.isLogining)
            .onClick(() => {
              // 收起软键盘
              ToolKits.hideSoftInputMethod(this.getUIContext());
              // 开始登录
              this.doLogin();
            })

            Blank()

            Column() {
              Text() {
                Span('Developed by ')
                  .fontSize(12)
                  .fontColor('#a0a2a6')
                  .lineHeight(14)
                Span('JackJiang')
                  .fontSize(12)
                  .fontColor('#0A59F7')
                  .fontWeight(FontWeight.Medium)
                  .lineHeight(14)
                  .onClick(() => {
                    this.doContactAuthor();
                  })
              }
              .width('100%')
              .align(Alignment.Center)
              .textAlign(TextAlign.Center)
              .margin({bottom: 5})

              Text('Copyright © 2024 即时通讯网 / 52im.net')
                .fontSize(12)
                .fontColor('#a0a2a6')
                .lineHeight(14)
            }
            .margin({ bottom: 32 })
          }
          .width('100%')
          .height('100%')
          .padding({ left: 12, right: 12 })
        }
      }
      .padding({ left: 16, right: 16 })
      .width('100%')
      .height('100%')
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('sys.color.ohos_id_color_sub_background'))
    // 适配流海屏（顶部状态栏透明并穿过，并透过底部上划操作区）
    .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP, SafeAreaEdge.BOTTOM])
  }
}
