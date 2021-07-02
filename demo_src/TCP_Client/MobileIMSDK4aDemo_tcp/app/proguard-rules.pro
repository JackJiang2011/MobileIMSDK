
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保留Google GSON相关API:
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *;}

# 保留MobileIMSDK的Protocal相关类
-keep class net.x52im.mobileimsdk.server.protocal.** { *; }
