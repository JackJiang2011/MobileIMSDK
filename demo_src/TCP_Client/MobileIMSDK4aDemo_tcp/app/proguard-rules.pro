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

# ---------------------------------------------------------------------------------
# 以下内容由最新Android Studio的R8混淆器自动生成的，如果不加，则无法编译生成release包！
# 参考资料：http://quibbler.cn/?thread-821.htm
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.barchart.udt.OptionUDT
-dontwarn com.barchart.udt.SocketUDT
-dontwarn com.barchart.udt.StatusUDT
-dontwarn com.barchart.udt.TypeUDT
-dontwarn com.barchart.udt.nio.ChannelUDT
-dontwarn com.barchart.udt.nio.KindUDT
-dontwarn com.barchart.udt.nio.NioServerSocketUDT
-dontwarn com.barchart.udt.nio.NioSocketUDT
-dontwarn com.barchart.udt.nio.RendezvousChannelUDT
-dontwarn com.barchart.udt.nio.SelectorProviderUDT
-dontwarn com.barchart.udt.nio.ServerSocketChannelUDT
-dontwarn com.barchart.udt.nio.SocketChannelUDT
-dontwarn com.fasterxml.aalto.AsyncByteArrayFeeder
-dontwarn com.fasterxml.aalto.AsyncInputFeeder
-dontwarn com.fasterxml.aalto.AsyncXMLInputFactory
-dontwarn com.fasterxml.aalto.AsyncXMLStreamReader
-dontwarn com.fasterxml.aalto.stax.InputFactoryImpl
-dontwarn com.jcraft.jzlib.Deflater
-dontwarn com.jcraft.jzlib.Inflater
-dontwarn com.jcraft.jzlib.JZlib$WrapperType
-dontwarn com.jcraft.jzlib.JZlib
-dontwarn com.ning.compress.BufferRecycler
-dontwarn com.ning.compress.lzf.ChunkDecoder
-dontwarn com.ning.compress.lzf.ChunkEncoder
-dontwarn com.ning.compress.lzf.LZFChunk
-dontwarn com.ning.compress.lzf.LZFEncoder
-dontwarn com.ning.compress.lzf.util.ChunkDecoderFactory
-dontwarn com.ning.compress.lzf.util.ChunkEncoderFactory
-dontwarn com.oracle.svm.core.annotate.Alias
-dontwarn com.oracle.svm.core.annotate.RecomputeFieldValue$Kind
-dontwarn com.oracle.svm.core.annotate.RecomputeFieldValue
-dontwarn com.oracle.svm.core.annotate.TargetClass
-dontwarn com.sun.nio.sctp.AbstractNotificationHandler
-dontwarn com.sun.nio.sctp.Association
-dontwarn com.sun.nio.sctp.AssociationChangeNotification
-dontwarn com.sun.nio.sctp.HandlerResult
-dontwarn com.sun.nio.sctp.MessageInfo
-dontwarn com.sun.nio.sctp.Notification
-dontwarn com.sun.nio.sctp.NotificationHandler
-dontwarn com.sun.nio.sctp.PeerAddressChangeNotification
-dontwarn com.sun.nio.sctp.SctpChannel
-dontwarn com.sun.nio.sctp.SctpServerChannel
-dontwarn com.sun.nio.sctp.SctpSocketOption
-dontwarn com.sun.nio.sctp.SctpStandardSocketOptions$InitMaxStreams
-dontwarn com.sun.nio.sctp.SctpStandardSocketOptions
-dontwarn com.sun.nio.sctp.SendFailedNotification
-dontwarn com.sun.nio.sctp.ShutdownNotification
-dontwarn gnu.io.CommPort
-dontwarn gnu.io.CommPortIdentifier
-dontwarn gnu.io.SerialPort
-dontwarn io.netty.internal.tcnative.Buffer
-dontwarn io.netty.internal.tcnative.CertificateCallback
-dontwarn io.netty.internal.tcnative.CertificateVerifier
-dontwarn io.netty.internal.tcnative.Library
-dontwarn io.netty.internal.tcnative.SSL
-dontwarn io.netty.internal.tcnative.SSLContext
-dontwarn io.netty.internal.tcnative.SSLPrivateKeyMethod
-dontwarn io.netty.internal.tcnative.SessionTicketKey
-dontwarn io.netty.internal.tcnative.SniHostNameMatcher
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.xml.stream.XMLStreamException
-dontwarn lzma.sdk.ICodeProgress
-dontwarn lzma.sdk.lzma.Encoder
-dontwarn net.jpountz.lz4.LZ4Compressor
-dontwarn net.jpountz.lz4.LZ4Exception
-dontwarn net.jpountz.lz4.LZ4Factory
-dontwarn net.jpountz.lz4.LZ4FastDecompressor
-dontwarn net.jpountz.xxhash.XXHash32
-dontwarn net.jpountz.xxhash.XXHashFactory
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority
-dontwarn org.apache.logging.log4j.Level
-dontwarn org.apache.logging.log4j.LogManager
-dontwarn org.apache.logging.log4j.Logger
-dontwarn org.apache.logging.log4j.message.MessageFactory
-dontwarn org.apache.logging.log4j.spi.ExtendedLogger
-dontwarn org.apache.logging.log4j.spi.ExtendedLoggerWrapper
-dontwarn org.bouncycastle.asn1.x500.X500Name
-dontwarn org.bouncycastle.cert.X509CertificateHolder
-dontwarn org.bouncycastle.cert.X509v3CertificateBuilder
-dontwarn org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
-dontwarn org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
-dontwarn org.bouncycastle.jce.provider.BouncyCastleProvider
-dontwarn org.bouncycastle.operator.ContentSigner
-dontwarn org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
-dontwarn org.conscrypt.AllocatedBuffer
-dontwarn org.conscrypt.BufferAllocator
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.HandshakeListener
-dontwarn org.eclipse.jetty.alpn.ALPN$ClientProvider
-dontwarn org.eclipse.jetty.alpn.ALPN$Provider
-dontwarn org.eclipse.jetty.alpn.ALPN$ServerProvider
-dontwarn org.eclipse.jetty.alpn.ALPN
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ClientProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$Provider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ServerProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego
-dontwarn org.jboss.marshalling.ByteInput
-dontwarn org.jboss.marshalling.ByteOutput
-dontwarn org.jboss.marshalling.Marshaller
-dontwarn org.jboss.marshalling.MarshallerFactory
-dontwarn org.jboss.marshalling.MarshallingConfiguration
-dontwarn org.jboss.marshalling.Unmarshaller
-dontwarn org.slf4j.ILoggerFactory
-dontwarn org.slf4j.Logger
-dontwarn org.slf4j.LoggerFactory
-dontwarn org.slf4j.Marker
-dontwarn org.slf4j.helpers.FormattingTuple
-dontwarn org.slf4j.helpers.MessageFormatter
-dontwarn org.slf4j.helpers.NOPLoggerFactory
-dontwarn org.slf4j.spi.LocationAwareLogger
-dontwarn reactor.blockhound.BlockHound$Builder
-dontwarn reactor.blockhound.integration.BlockHoundIntegration
-dontwarn sun.security.util.ObjectIdentifier
-dontwarn sun.security.x509.AlgorithmId
-dontwarn sun.security.x509.CertificateAlgorithmId
-dontwarn sun.security.x509.CertificateIssuerName
-dontwarn sun.security.x509.CertificateSerialNumber
-dontwarn sun.security.x509.CertificateSubjectName
-dontwarn sun.security.x509.CertificateValidity
-dontwarn sun.security.x509.CertificateVersion
-dontwarn sun.security.x509.CertificateX509Key
-dontwarn sun.security.x509.X500Name
-dontwarn sun.security.x509.X509CertImpl
-dontwarn sun.security.x509.X509CertInfo
# ---------------------------------------------------------------------------------

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


# 排除netty的混淆
-keepattributes Signature,InnerClasses
-keepclasseswithmembers class io.netty.** { *;}
-keepnames class io.netty.** { *;}

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