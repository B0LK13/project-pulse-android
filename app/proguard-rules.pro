# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Room entities
-keep class com.vibecoding.android.data.local.entity.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }

# Keep serialized models
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
