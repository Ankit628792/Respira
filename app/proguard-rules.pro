# ==============================================================================
# Respira - ProGuard & R8 Optimization and Obfuscation Rules
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. General Optimization & De-obfuscation in Google Play Console
# ------------------------------------------------------------------------------
# Preserve line numbers and source attributes for de-obfuscated crash reports in Play Console
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,Deprecated,*Annotation*
-renamesourcefileattribute SourceFile

# Enable aggressive R8 optimizations
-allowaccessmodification
-repackageclasses ''

# ------------------------------------------------------------------------------
# 2. Application Data Models
# ------------------------------------------------------------------------------
# Keep all data model classes, fields, and constructors
-keep class com.respira.data.model.** { *; }
-keepclassmembers class com.respira.data.model.** { *; }

# ------------------------------------------------------------------------------
# 3. Room Database & SQLite Persistence
# ------------------------------------------------------------------------------
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public abstract *;
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# ------------------------------------------------------------------------------
# 4. Networking: Retrofit & OkHttp
# ------------------------------------------------------------------------------
# Retrofit 2 rules
-keepattributes *Annotation*,Signature
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn retrofit2.Platform$Java8

# OkHttp 3 / 4 rules
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class okhttp3.OkHttpClient {
    *;
}
-keep interface okhttp3.Call$Factory { *; }
-keep interface okhttp3.Interceptor { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ------------------------------------------------------------------------------
# 5. JSON Parsing: Moshi
# ------------------------------------------------------------------------------
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface * { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class * extends com.squareup.moshi.JsonAdapter { *; }
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# ------------------------------------------------------------------------------
# 6. AndroidX Lifecycle, ViewModel & Compose
# ------------------------------------------------------------------------------
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    public <init>(...);
}
-keep class androidx.lifecycle.ViewModelLazy { *; }
-keep class androidx.lifecycle.ViewTreeViewModelStoreOwner { *; }

# Jetpack Compose UI
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# ------------------------------------------------------------------------------
# 7. Kotlin Coroutines & Reflection Suppression
# ------------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
