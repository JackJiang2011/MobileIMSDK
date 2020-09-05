#!/bin/sh

sudo $JAVA_HOME/bin/java -cp lib/gson-2.8.6.jar:lib/log4j-1.2.17.jar:lib/slf4j-api-1.7.21.jar:lib/slf4j-log4j12-1.7.21.jar:lib/MobileIMSDKServer.jar:lib/rabbitmq-client.jar:$JAVA_HOME/lib/tools.jar:classes/.:lib/netty-all-4.1.50.Final.jar: net.x52im.mobileimsdk.server.demo.ServerLauncherImpl %1