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

# Room Database
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Moshi & JSON Data Models
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keep class com.example.data.model.** { *; }
-keepclassmembers class com.example.data.model.** { *; }

# Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
