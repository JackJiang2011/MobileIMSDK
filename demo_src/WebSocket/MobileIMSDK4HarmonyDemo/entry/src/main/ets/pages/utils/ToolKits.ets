/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

import { promptAction, window, KeyboardAvoidMode } from "@kit.ArkUI";
import { Callback } from "@ohos.base";
import { common } from "@kit.AbilityKit";
import { ClientCoreSDK } from "mobileimsdk";

const TAG: string = '[ToolKits] ';

/**
 * 工具类。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
export default class ToolKits {

  /** 聊天界面全面屏状态栏背景色 */
  static readonly chat_page_title_background_color = '#ffffff';//'#fafafa';//#2e3746';//'#2e3746';
  /** 聊天界面全面屏状态栏文本颜色 */
  static readonly chat_page_title_color = '#000000';//#ffffff';

  /**
   * 退出系统。
   *
   * @param context UIAbilityContext上下文对象
   */
  static exitSystem(context: common.UIAbilityContext) {
    // let context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext;
    context.terminateSelf();
  }

  /**
   * 设置全面屏状态栏颜色。
   *
   * @param context 上下文对象
   * @param statusBarContentColor 状态栏文本颜色
   * @param statusBarColor 状态栏背景色
   */
  static setStatusBarColor(context: common.BaseContext, statusBarContentColor: string, statusBarColor: string) {
    // window.getLastWindow(context).then((windowClass: window.Window) => { // 这种方式获得window对象时，在page首次启动后，为啥在onPageShow里调用setStatusBarColor没效果？
      try {
        let windowClass = AppStorage.get<window.Window>('window')

        windowClass?.setWindowSystemBarProperties({
          statusBarContentColor: statusBarContentColor,//color,
          statusBarColor: statusBarColor,
          // navigationBarColor: '#0000ff',
          // navigationBarContentColor: '#fff00f'
        });
      } catch (exception) {
        ClientCoreSDK.Log.error(TAG+ 'Failed to set the system bar properties. Cause: ' + JSON.stringify(exception));
      }
    // });
  }

  /**
   * 收起软键盘。
   *
   * @param context 上下文对象
   */
  static hideSoftInputMethod(context: UIContext) {
    // 收起软键盘，官方资料：https://developer.huawei.com/consumer/cn/doc/best-practices-V5/bpta-keyboard-layout-adapt-V5#section19809195110316
    context.getFocusController().clearFocus();
  }

  /**
   * 设置输入法软键盘避让模式为RESIZE（表示当输入显示时，压缩窗体高度，而不是使用默认的OFFSET模式将窗体整体上推）。
   * 详见官方资料：https://developer.huawei.com/consumer/cn/doc/best-practices-V5/bpta-keyboard-layout-adapt-V5#section08221814182316
   *
   * @param toRESIZE true表示设置为KeyboardAvoidMode.RESIZE模式，否则还原为系统默认的 KeyboardAvoidMode.OFFSET模式
   */
  static setKeyboardAvoidModeToRESIZE(context: UIContext, toRESIZE: boolean) {
    context.setKeyboardAvoidMode(toRESIZE ? KeyboardAvoidMode.RESIZE : KeyboardAvoidMode.OFFSET);

    // window.getLastWindow(context).then(currentWindow => {
    //   currentWindow?.getUIContext().setKeyboardAvoidMode(toRESIZE ? KeyboardAvoidMode.RESIZE : KeyboardAvoidMode.OFFSET);
    // });

    // AppStorage.get<window.Window>('window')?.getUIContext().setKeyboardAvoidMode(KeyboardAvoidMode.RESIZE);
  }

  /**
   * Check obj is empty
   *
   * @param {object} obj
   * @return {boolean} true(empty)
   */
  static isEmpty(obj: object | string, trimString: boolean = false): boolean {
    return obj === undefined
      || obj === null
      || obj === ''
      || (trimString? ((typeof obj === 'string') ? (obj as string).trim() === '' : false) : false);
  }

  /**
   * 返回本地用户UID.
   *
   * @returns local uid
   */
  static getLocalUserUid(): string | undefined {
    return ClientCoreSDK.getInstance().currentLoginInfo?.loginUserId
  }

  /**
   * 显示奶油提示。
   *
   * @param message 提示内容
   * @param duration 持续时间（单位：毫秒），默认1500
   */
  static showToast(message: string | Resource, duration: number = 1500) {
    promptAction.showToast({
      message: message,
      duration: duration,
      // 软键盘弹出时，防止Toast上抬，详见: https://developer.huawei.com/consumer/cn/doc/best-practices-V5/bpta-keyboard-layout-adapt-V5#section381324419328
      showMode: promptAction.ToastShowMode.TOP_MOST,
      // backgroundBlurStyle: BlurStyle.Thin
    });
  }

  /**
   * 显示对话框.
   *
   * @param message 文本显示内容
   * @param primaryButtonText 主按钮文本
   * @param primaryButtonTextColor 主按钮文本颜色
   * @param primaryAction  主按钮Action
   * @param secondaryButtonText 次按钮文本
   * @param secondaryButtonTextColor 次按钮文本颜色
   * @param secondaryAction 次按钮Action
   */
  static showAlertDialog(message: ResourceStr
    , primaryButtonText: string, primaryButtonTextColor: ResourceColor | undefined, primaryAction: Callback<void>
    , secondaryButtonText: string, secondaryButtonTextColor: ResourceColor | undefined, secondaryAction: Callback<void>) {
    AlertDialog.show(
      {
        message: message,
        autoCancel: false,
        alignment: DialogAlignment.Center,
        offset:{ dx: 0, dy: 0 } ,
        // gridCount: 4,
        primaryButton: primaryButtonText ? {
          value: primaryButtonText,
          action: primaryAction,
          fontColor: primaryButtonTextColor
        } : undefined,
        secondaryButton: secondaryButtonText ? {
          value: secondaryButtonText,
          action: secondaryAction,
          fontColor: secondaryButtonTextColor//$r('sys.color.ohos_id_color_badge_red')
        } : undefined
      });
  }

  /**
   * 仿照微信中的消息时间显示逻辑，将时间戳（单位：毫秒）转换为友好的显示格式.
   *
   * 1）7天之内的日期显示逻辑是：今天、昨天(-1d)、前天(-2d)、星期？（只显示总计7天之内的星期数，即<=-4d）；
   * 2）7天之外（即>7天）的逻辑：直接显示完整日期时间。

   * @param timestamp 时间戳（单位：毫秒），形如：1550789954260
   * @param mustIncludeTime true表示输出的格式里一定会包含“时间:分钟”
   * ，否则不包含（参考微信，不包含时分的情况，用于首页“消息”中显示时）
   * @param timeWithSegmentStr 本参数仅在mustIncludeTime=true时有生效，表示在时间字符串前带上“上午”、“下午”、“晚上”这样的描述
   *
   * @return 输出格式形如：“刚刚”、“10:30”、“昨天 12:04”、“前天 20:51”、“星期二”、“2019/2/21 12:09”等形式
   * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
   */
  static getTimeStringAutoShort2(timestamp: number, mustIncludeTime: boolean, timeWithSegmentStr: boolean): string {
    // 当前时间
    let currentDate: Date = new Date();
    // 目标判断时间
    let srcDate: Date = new Date(timestamp);

    let currentYear: number = currentDate.getFullYear();
    let currentMonth: number = (currentDate.getMonth()+1);
    let currentDateD: number = currentDate.getDate();

    let srcYear: number = srcDate.getFullYear();
    let srcMonth: number = (srcDate.getMonth()+1);
    let srcDateD: number = srcDate.getDate();

    let ret: string = '';

    // 要额外显示的时间分钟
    let timeExtraStr = '';
    if(mustIncludeTime) {
      // let timeExtraStr = (mustIncludeTime ? " " + _formatDate(srcDate, "hh:mm") : "");
      timeExtraStr = " "+ToolKits.getTimeHH24Human(srcDate, timeWithSegmentStr);
    }

    // 当年
    if(currentYear === srcYear) {
      let currentTimestamp: number = currentDate.getTime();
      let srcTimestamp: number = timestamp;
      // 相差时间（单位：毫秒）
      let deltaTime: number = (currentTimestamp-srcTimestamp);

      // 当天（月份和日期一致才是）
      if(currentMonth === srcMonth && currentDateD === srcDateD) {
        // // 时间相差60秒以内
        // if(deltaTime < 60 * 1000)
        //     ret = "刚刚";
        // // 否则当天其它时间段的，直接显示“时:分”的形式
        // else
        //     ret = _formatDate(srcDate, "hh:mm");

        // 当天只需要显示时间分钟，且必须显示“上午”、“下午”这样的时间段描述
        ret = ToolKits.getTimeHH24Human(srcDate, true);
      }
      // 当年 && 当天之外的时间（即昨天及以前的时间）
      else {
        // 昨天（以“现在”的时候为基准-1天）
        let yesterdayDate:Date = new Date();
        yesterdayDate.setDate(yesterdayDate.getDate()-1);

        // 前天（以“现在”的时候为基准-2天）
        let beforeYesterdayDate: Date = new Date();
        beforeYesterdayDate.setDate(beforeYesterdayDate.getDate()-2);

        // 用目标日期的“月”和“天”跟上方计算出来的“昨天”进行比较，是最为准确的（如果用时间戳差值
        // 的形式，是不准确的，比如：现在时刻是2019年02月22日1:00、而srcDate是2019年02月21日23:00，
        // 这两者间只相差2小时，直接用“deltaTime/(3600 * 1000)” > 24小时来判断是否昨天，就完全是扯蛋的逻辑了）
        if(srcMonth === (yesterdayDate.getMonth()+1) && srcDateD === yesterdayDate.getDate()) {
          ret = "昨天"+timeExtraStr;// -1d
        }
        // “前天”判断逻辑同上
        else if(srcMonth === (beforeYesterdayDate.getMonth()+1) && srcDateD === beforeYesterdayDate.getDate()) {
          ret = "前天" + timeExtraStr; // -2d
        } else{
          // 跟当前时间相差的小时数
          let deltaHour: number = (deltaTime/(3600 * 1000));

          // 如果小于或等 7*24小时就显示星期几
          if (deltaHour <= 7*24){
            let weekday = new Array<string>(7);
            weekday[0]="星期日";
            weekday[1]="星期一";
            weekday[2]="星期二";
            weekday[3]="星期三";
            weekday[4]="星期四";
            weekday[5]="星期五";
            weekday[6]="星期六";

            // 取出当前是星期几
            let weedayDesc: string = weekday[srcDate.getDay()];
            ret = weedayDesc + timeExtraStr;
          }
          // 否则直接显示完整日期时间
          else
            ret = ToolKits.formatDate(srcDate, "M月d日")+timeExtraStr;
        }
      }
    }
    // 往年
    else{
      ret = ToolKits.formatDate(srcDate, "yy年M月d日")+timeExtraStr;
    }

    return ret;
  }

  /**
   * 获取仅包含“时间:分钟”部分的字符串，24小时制，且可以显示“上午”、“下午”、“晚上”这样的描述。
   *
   * @param srcDateObj 原始日期对象
   * @param timeWithSegmentStr 表示在时间字符串前带上“上午”、“下午”、“晚上”这样的描述
   * @return 如果成功则返回结果，否则返回空字符串""（不是null）
   */
  static getTimeHH24Human(srcDateObj: Date, timeWithSegmentStr: boolean): string {
    let ret: string = '';
    try {
      let timePattern: string = 'hh:mm';
      // 原始的时间分钟字符串
      let timeStr: string = ToolKits.formatDate(srcDateObj, timePattern);

      // 时间段描述（形如：“上午”、“下午”、“晚上”这样的描述），只在中文语言下生效
      let timeSegmentStr: string = '';
      if(timeWithSegmentStr)
        timeSegmentStr = ToolKits.getTimeSegmentStr(timeStr);

      // 组合成最终的人性化时间分钟字符串形式
      ret = timeSegmentStr+timeStr;
    } catch (e) {
      ClientCoreSDK.Log.warn("【DEBUG-getTimeHH24Human】计算出错："+ JSON.stringify(e) +" 【NO】");
    }

    return ret;
  }

  /**
   * 将一个两位24小时时间的转换为上午、下午这样的描述。
   *
   * @param hh24 两位的24小时制时间的小时部分
   * @return 如果成功转换则返回形如：“凌晨”、“上午”等，否则返回空字符串（不是null）
   * @since 3.1
   */
  static getTimeSegmentStr(hh24: string): string {
    let ret: string = '';
    if (hh24 && hh24.length >= 2) {
      try {
        // 取出“小时”部分
        let a = parseInt(hh24.substring(0, 2));
        if (a >= 0 && a <= 6) {
          ret = "凌晨";
        } else if (a > 6 && a <= 12) {
          ret = "上午";
        } else if (a > 12 && a <= 13) {
          ret = "中午";
        } else if (a > 13 && a <= 18) {
          ret = "下午";
        } else if (a > 18 && a <= 24) {
          ret = "晚上";
        }
      } catch (e) {
        ClientCoreSDK.Log.warn("【DEBUG-getTimeSegmentStr】计算出错：" + JSON.stringify(e) + " 【NO】");
      }
    }

    return ret;
  }

  /**
   * 对Date的扩展，将 Date 转化为指定格式的String。
   *
   *  月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
   *  年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)。
   *
   *  【示例】：
   *  common.formatDate(new Date(), 'yyyy-MM-dd hh:mm:ss.S') ==> 2006-07-02 08:09:04.423
   *  common.formatDate(new Date(), 'yyyy-M-d h:m:s.S')      ==> 2006-7-2 8:9:4.18
   *  common.formatDate(new Date(), 'hh:mm:ss.S')            ==> 08:09:04.423
   *
   *  @param date Date对象
   *  @param fmt 格式字符串
   *  @param 格式化的字符串
   */
  static formatDate(date: Date, fmt: string) {
    // let o = {
    //   "M+": date.getMonth() + 1, //月份
    //   "d+": date.getDate(), //日
    //   "h+": date.getHours(), //小时
    //   "m+": date.getMinutes(), //分
    //   "s+": date.getSeconds(), //秒
    //   "q+": Math.floor((date.getMonth() + 3) / 3), //季度
    //   "S": date.getMilliseconds() //毫秒
    // };

     let map = new Map<String, Number>();
     map.set("M+", date.getMonth() + 1); // 月份
     map.set("d+", date.getDate()); // 日
     map.set("h+", date.getHours()); // 小时
     map.set("m+", date.getMinutes()); // 分
     map.set("s+", date.getSeconds()); // 秒
     map.set("q+", Math.floor((date.getMonth() + 3) / 3)); // 季度
     map.set("S", date.getMilliseconds()); // 毫秒

    // if (/(y+)/.test(fmt))
    //   fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));

     let yearMatcher = /(y+)/.exec(fmt);
     if (yearMatcher && yearMatcher.length > 0) {
       let match = yearMatcher[0];
       fmt = fmt.replace(match, (date.getFullYear() + "").substr(4 - match.length));
     }

    // for (let k in o) {
    //   if (new RegExp("(" + k + ")").test(fmt)) {
    //     fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    //   }
    // }

     map.forEach((value, key) => {
       let matcher = new RegExp(`(${key})`).exec(fmt!);
       if (matcher && matcher.length > 0) {
         let match = matcher[0];
         let replaceValue = '';
         if ((match.length == 1)) {
           replaceValue = `${value}`;
         } else {
           replaceValue = (("00" + value).substr(("" + value).length))
         }
         fmt = fmt!.replace(match, replaceValue);
       }
     });

    return fmt;
  }
}