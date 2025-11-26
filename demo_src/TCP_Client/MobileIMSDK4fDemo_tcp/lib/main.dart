import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:mobile_im_sdk_flutter_tcp_example/test_login_page.dart';
import 'package:oktoast/oktoast.dart';

void main() async {
  // 初始化持久化
  await GetStorage.init();

  runApp(const TestApp());
}

class TestApp extends StatelessWidget {
  const TestApp({super.key});

  @override
  Widget build(BuildContext context) {
    return OKToast(
      radius: 15,
      textPadding:
          const EdgeInsets.only(left: 15, right: 15, top: 6, bottom: 6),
      position: ToastPosition.bottom,
      backgroundColor: Colors.black.withOpacity(0.7),
      child: GetMaterialApp(
        title: 'Chat Demo',
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        debugShowCheckedModeBanner: true,
        home: const TestLoginPage(),
      ),
    );
  }
}
