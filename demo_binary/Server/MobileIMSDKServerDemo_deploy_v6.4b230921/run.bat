@echo off

@echo 欢迎使用 MobileIMSDK v6.4 服务端 (build230921)
@echo.
@echo 基于MobileIMSDK的IM服务器正在运行中 ...
@echo.

"%JAVA_HOME%/bin/java" -cp lib/gson-2.8.6.jar;lib/log4j-api-2.17.0.jar;lib/log4j-core-2.17.0.jar;lib/log4j-slf4j-impl-2.17.0.jar;lib/slf4j-api-1.7.30.jar;lib/MobileIMSDKServer.jar;lib/amqp-client-5.14.2.jar;"%JAVA_HOME%/lib/tools.jar;classes/.;lib/netty-all-4.1.50.Final.jar;" net.x52im.mobileimsdk.server.demo.ServerLauncherImpl %1

@echo.
pause