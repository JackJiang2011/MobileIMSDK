import 'dart:io';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/core/local_data_sender.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/Ext.dart';
import 'package:mobile_im_sdk_flutter_tcp/sdk/utils/log.dart';
import 'package:oktoast/oktoast.dart';

import 'chat_controller.dart';
import 'loading_button.dart';

class TestHomeController extends GetxController {
  final accountController = TextEditingController();
  final msgController = TextEditingController();
  bool loading = false;

  final ChatController chatController = Get.find();

  final storage = GetStorage();

  @override
  void onInit() {
    super.onInit();
    String name = storage.read("rec_name") ?? "";
    String info = storage.read("msg") ?? "{消息[doge]} ";
    accountController.text = name;
    msgController.text = info;
  }

  void sendMsg() {
    var info = msgController.text ?? "";
    var name = accountController.text ?? "";
    if (info.isEmpty || name.isEmpty) {
      showToast("消息 和 用户名 不可为空");
      return;
    }

    storage.write("rec_name", name);
    storage.write("msg", info);

    var finalMsg = nowHmsStrWithMark() + info;

    chatController.msgList.add(finalMsg);

    LocalDataSender.getInstance().sendCommonData(finalMsg, name);
  }
}

class TestHomePage extends StatefulWidget {
  const TestHomePage({super.key});

  @override
  State<TestHomePage> createState() => _TestHomePageState();
}

class _TestHomePageState extends State<TestHomePage>
    with WidgetsBindingObserver, RouteAware {
  bool _inCurrentPage = true;

  /// 由子页面 返回时刷新
  @override
  void didPopNext() {
    _inCurrentPage = true;
    super.didPopNext();
  }

  /// 进入子页面
  @override
  void didPushNext() {
    _inCurrentPage = false;
    super.didPushNext();
  }

  @override
  void initState() {
    super.initState();
    Log.cacheLogInfo = (str) {
      var finalStr = str.toString();
      var flag = finalStr.contains("[error]") || finalStr.contains("[warning]");
      var notMsg = !finalStr.contains("[msg]");
      if (flag && notMsg) {
        chatController.msgList.add(finalStr);
      }
    };

    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    // App 的生命周期回调
    switch (state) {
      case AppLifecycleState.inactive:
        print('app -> inactive');
        break;
      case AppLifecycleState.resumed:
        print('app -> resumed 恢复');
        // 连接
        break;
      case AppLifecycleState.paused:
        print('app -> paused 挂起');
        break;
      case AppLifecycleState.detached:
        print('app -> detached 已经退出(进程已杀死)');
        // 关闭连接
        break;
    }
  }

  TestHomeController controller = Get.find();
  ChatController chatController = Get.find();

  ScrollController scrollController = ScrollController();

  Color getBgColor() => Color.fromARGB(Random().nextInt(125),
      Random().nextInt(255), Random().nextInt(255), Random().nextInt(255));

  late Color bgColor = Colors.lime;

  final autoScroll = false.obs;

  @override
  Widget build(BuildContext context) {
    chatController.msgList.listen((p0) {
      if (!autoScroll.value) return;

      /// 延迟 300 毫秒，再进行滑动
      Future.delayed(const Duration(milliseconds: 300), () {
        scrollController.jumpTo(scrollController.position.maxScrollExtent);
      });
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('主页'),
      ),
      body: SizedBox.expand(
        child: Padding(
          padding: const EdgeInsets.only(top: 20, left: 15, right: 15),
          child: Column(
            children: [
              SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      Obx(() => Text(
                            "网络 ${chatController.linkState.value ? "连接" : "断开"}",
                            style: TextStyle(
                                fontSize: 8,
                                color: chatController.linkState.value
                                    ? Colors.green
                                    : Colors.red),
                          )),
                      const SizedBox(width: 10),
                      Obx(() => Text(
                            "重连 ${chatController.reLinkState.value ? "连接" : "断开"}",
                            style: TextStyle(
                                fontSize: 8,
                                color: chatController.reLinkState.value
                                    ? Colors.green
                                    : Colors.red),
                          )),
                      const SizedBox(width: 10),
                      Obx(() => Text(
                            "心跳 ${chatController.heartbeatState.value ? "连接" : "断开"}",
                            style: TextStyle(
                                fontSize: 8,
                                color: chatController.heartbeatState.value
                                    ? Colors.green
                                    : Colors.red),
                          )),
                      const SizedBox(width: 10),
                      Obx(() => Text(
                            "QoS 发送 ${chatController.qosSendState.value ? "连接" : "断开"}",
                            style: TextStyle(
                                fontSize: 8,
                                color: chatController.qosSendState.value
                                    ? Colors.green
                                    : Colors.red),
                          )),
                      const SizedBox(width: 10),
                      Obx(() => Text(
                            "QoS 接收 ${chatController.qosReceiveState.value ? "连接" : "断开"}",
                            style: TextStyle(
                                fontSize: 8,
                                color: chatController.qosReceiveState.value
                                    ? Colors.green
                                    : Colors.red),
                          )),
                    ],
                  )),
              const SizedBox(height: 15),
              Obx(() {
                return Row(
                  mainAxisSize: MainAxisSize.max,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text("自动滚动到底部"),
                    const SizedBox(width: 5),
                    CupertinoSwitch(
                        value: autoScroll.value,
                        onChanged: (v) {
                          autoScroll.value = v;
                        }),
                  ],
                );
              }),
              const SizedBox(height: 15),
              CupertinoTextField(
                placeholder: '请输入接收者...',
                controller: controller.accountController,
                clearButtonMode: OverlayVisibilityMode.editing,
              ),
              const SizedBox(height: 15),
              CupertinoTextField(
                placeholder: '请输入消息...',
                controller: controller.msgController,
                keyboardType: TextInputType.text,
                clearButtonMode: OverlayVisibilityMode.editing,
              ),
              const SizedBox(height: 15),
              Row(
                mainAxisSize: MainAxisSize.max,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Expanded(
                    child: SizedBox(
                      width: 100,
                      height: 30,
                      child: LoadingButton(
                        title: '发送',
                        loadingText: '发送中',
                        titleStyle: const TextStyle(
                          fontSize: 8,
                          color: Colors.white,
                        ),
                        radius: 17.5,
                        loading: controller.loading,
                        onTap: () {
                          controller.sendMsg();
                        },
                      ),
                    ),
                  ),
                  const SizedBox(width: 50),
                  Expanded(
                    child: SizedBox(
                      width: 100,
                      height: 30,
                      child: LoadingButton(
                        title: '批量发送 ($_multiCount)',
                        loadingText: '发送中',
                        titleStyle: const TextStyle(
                          fontSize: 8,
                          color: Colors.white,
                        ),
                        radius: 17.5,
                        loading: controller.loading,
                        onTap: () {
                          doMultiSend();
                        },
                      ),
                    ),
                  ),
                  const SizedBox(width: 50),
                  Expanded(
                    child: SizedBox(
                      height: 30,
                      child: LoadingButton(
                        title: '清屏',
                        loadingText: '清屏中',
                        titleStyle: const TextStyle(
                          fontSize: 8,
                          color: Colors.white,
                        ),
                        radius: 17.5,
                        loading: controller.loading,
                        onTap: () {
                          chatController.msgList.clear();
                        },
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 15),
              Obx(() => Text(
                    chatController.newestReceiveMsg.value,
                    style: const TextStyle(color: Colors.blue),
                  )),
              const SizedBox(height: 15),
              Obx(() => Text("${chatController.msgList.length}")),
              const SizedBox(height: 5),
              Expanded(
                  child: Obx(() => ListView.builder(
                        controller: scrollController,
                        itemCount: chatController.msgList.length,
                        reverse: false,
                        itemBuilder: (context, index) {
                          return Container(
                            color: bgColor,
                            padding: const EdgeInsets.all(8.0),
                            margin: const EdgeInsets.symmetric(
                                horizontal: 3.0, vertical: 5),
                            child: Text(
                                "$index  ${chatController.msgList[index]}"),
                          );
                        },
                      )))
            ],
          ),
        ),
      ),
    );
  }

  doMultiSend() async {
    for (int i = 0; i < _multiCount; i++) {
      controller.sendMsg();
      sleep(const Duration(milliseconds: 100));
    }
  }

  @override
  void dispose() {
    super.dispose();
    chatController.logout();
  }
}

const int _multiCount = 10;
