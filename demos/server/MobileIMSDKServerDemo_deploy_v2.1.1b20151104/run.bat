@echo off

@echo 欢迎使用MobileIMSDK 即时通讯核心服务器
@echo.
@echo IM核心服务器正在运行中...
@echo.

"%JAVA_HOME%/bin/java" -cp lib/gson-2.2.4.jar;lib/log4j-1.2.17.jar;lib/mina-core-2.0.7.jar;lib/slf4j-api-1.6.6.jar;lib/slf4j-log4j12-1.7.5.jar;lib/MobileIMSDKServerO.jar;lib/MobileIMSDKServer_meta.jar;lib/MobileIMSDKServer_meta.jar;"%JAVA_HOME%/lib/tools.jar;.;" net.openmob.mobileimsdk.server.demo.ServerLauncherImpl %1

@echo.
pause