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

# 实体类
-keep class com.wazing.gank.bean.CommonBean.** { *; }
# 监听接口
-keep class com.wazing.gank.listener.** { *; }

# Glide
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** { **[] $VALUES;public *; }

# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
# Retrofit ApiService
-keep class com.wazing.gank.respository.net.ApiService {*;}
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
long producerIndex;
long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Jsoup
-dontwarn org.jsoup.**
-keep class org.jsoup.**{*;}


# 混淆等级，默认值
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers
# 这句话能够使我们的项目混淆后产生映射文件# 包含有类名->混淆后类名的映射关系
-verbose
-printmapping proguardMapping.txt
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses
# 避免混淆泛型
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 忽略警告，这一行挺重要，不加的话某些情况下打包会报错中断
-ignorewarnings
#---------------------------------------------------------------------------
#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keep class com.google.**{*;}
# 保留继承的
-keep public class * extends androidx.appcompat.app.**
-keep public class * extends android.support.annotation.**
-keepclasseswithmembernames class * {
native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
 public void *(android.view.View);
}
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
 *** get*();
 void set*(***);
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
 static final long serialVersionUID;
 private static final java.io.ObjectStreamField[] serialPersistentFields;
 private void writeObject(java.io.ObjectOutputStream);
 private void readObject(java.io.ObjectInputStream);
 java.lang.Object writeReplace();
 java.lang.Object readResolve();
}
-keep class **.R$* {
*;
}
-keepclassmembers class * {
 void *(**On*Event);
}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
 native <methods>;
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
 public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
 public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
 public void *(android.webkit.WebView, jav.lang.String);
}