import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:oktoast/oktoast.dart';
import 'chat_controller.dart';
import 'loading_button.dart';
import 'test_home_page.dart';

class TestLoginController extends GetxController {
  final ipController = TextEditingController();
  final accountController = TextEditingController();
  bool loading = false;
  final storage = GetStorage();

  @override
  void onInit() {
    super.onInit();
    String ip = storage.read("ip") ?? "rbcore.52im.net";
    String name = storage.read("name") ?? "f";
    ipController.text = ip;
    accountController.text = name;
  }

  final ChatController chatController = Get.find();

  void doLogin() {
    final ip = ipController.text ?? '';
    final name = accountController.text ?? '';
    if (ip.isEmpty || name.isEmpty) {
      showToast("Ip 和 用户名 不可为空");
      return;
    }
    storage.write("ip", ip);
    storage.write("name", name);

    chatController.doLogin(ip, 8901, name, name);
  }
}

class TestLoginPage extends StatelessWidget {
  const TestLoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    var chatController = Get.put(ChatController());
    var homeController = Get.put(TestHomeController());
    TestLoginController controller = Get.put(TestLoginController());

    return Scaffold(
      appBar: AppBar(
        title: const Text('登陆'),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.only(top: 100, left: 15, right: 15),
          child: Column(
            children: [
              CupertinoTextField(
                placeholder: '请输入ip...',
                controller: controller.ipController,
                clearButtonMode: OverlayVisibilityMode.editing,
              ),
              const SizedBox(height: 15),
              CupertinoTextField(
                placeholder: '请输入用户...',
                controller: controller.accountController,
                keyboardType: TextInputType.visiblePassword,
                clearButtonMode: OverlayVisibilityMode.editing,
              ),
              const SizedBox(height: 15),
              SizedBox(
                width: 100,
                height: 50,
                child: LoadingButton(
                  title: '登陆',
                  loadingText: '登陆中',
                  radius: 17.5,
                  loading: controller.loading,
                  onTap: () {
                    // 登陆
                    controller.doLogin();
                  },
                ),
              ),
              const SizedBox(height: 15),
              const SizedBox(height: 5),
              Expanded(
                  child: Obx(() => ListView.builder(
                        itemCount: chatController.msgList.length,
                        reverse: false,
                        itemBuilder: (context, index) {
                          return Container(
                            color: Colors.lightGreen,
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
}
