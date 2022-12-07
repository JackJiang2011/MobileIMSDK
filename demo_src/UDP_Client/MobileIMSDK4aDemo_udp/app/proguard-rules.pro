# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# 忽略警告
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
# 忽略警告(针对Android X)
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
# 忽略google相关的混淆警告（比如google分析的包等）
-dontwarn com.google.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.**
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe


-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


# 排除google相关的混淆（比如google分析的包等）
-keep class com.google.**
# 保留Google GSON相关API:
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *;}

# 保留fastjson相关API:
-keep class com.alibaba.fastjson.** { *;}

# 保留Apache http相关API:
-keep class org.apache.http.** { *; }
-keep class android.net.http.** { *; }

# 保留okhttp3相关API:
-keep class okio.** { *;}
-keep class okhttp3.** { *;}
-keep interface okhttp3.** { *; }

# 【重要】以下3行可排除R资源文件及其及类的混淆，否则运行时将报找不到资源
-keepattributes InnerClasses
-keep class **.R
-keep class **.R$* {
    <fields>;
}

# 保留MobileIMSDK的Protocal相关类
-keep class net.x52im.mobileimsdk.server.protocal.** { *; }

# 保留Android的一些默认API
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep class android.support.annotation.** { *; }
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keepattributes *Annotation*
# 避免使用泛型的位置混淆后出现类型转换错误:
-keepattributes Signature
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# 保留Android的一些默认API(针对Android x)
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}

# 保留JNI的native接口方法
-keepclasseswithmembernames class * {
  native <methods>;
}

# 保留所有自定义View的相关API（这些View可能用于了layout的xml文件里了）
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留所有Acvitity子类
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 保留所有枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 保留JSON、Parcelable、Serailizable等对象（否则代码中序列化、反序列化会失败）:
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}