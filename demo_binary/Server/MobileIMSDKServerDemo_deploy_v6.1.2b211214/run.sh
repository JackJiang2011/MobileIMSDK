#!/bin/sh

sudo $JAVA_HOME/bin/java -cp lib/gson-2.8.6.jar:lib/log4j-api-2.15.015.0.jar:lib/log4j-core-2.15.0.jar:lib/log4j-slf4j-impl-2.15.0.jar:lib/slf4j-api-1.7.30.jar:lib/MobileIMSDKServer.jar:lib/rabbitmq-client.jar:$JAVA_HOME/lib/tools.jar:classes/.:lib/netty-all-4.1.50.Final.jar: net.x52im.mobileimsdk.server.demo.ServerLauncherImpl %1