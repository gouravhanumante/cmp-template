# R8 rules for the release build. Add a rule only when a specific library needs it;
# a blanket "-keep class **" defeats the point of shrinking.

# Readable crash reports from an obfuscated build.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Firebase discovers registrars by class name and invokes their no-argument constructors reflectively.
-keep class * implements com.google.firebase.components.ComponentRegistrar {
    public <init>();
}

# --- kotlinx.serialization ---------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.boundless.template.**$$serializer { *; }
-keepclassmembers class com.boundless.template.** { *** Companion; }
-keepclasseswithmembers class com.boundless.template.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Koin ViewModels ---------------------------------------------------------
-keep class com.boundless.template.** extends androidx.lifecycle.ViewModel { *; }

# --- Android image picker ----------------------------------------------------
-keep class com.boundless.template.core.designsystem.component.** { *; }

# --- Ktor / OkHttp -----------------------------------------------------------
-dontwarn org.slf4j.**
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# --- Coroutines --------------------------------------------------------------
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
