import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.boundless.template.buildlogic"

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Plugin marker artifacts, so the convention scripts can apply these plugins by id.
dependencies {
    implementation(marker("org.jetbrains.kotlin.multiplatform", libs.versions.kotlin.get()))
    implementation(marker("org.jetbrains.kotlin.plugin.serialization", libs.versions.kotlin.get()))
    implementation(marker("org.jetbrains.kotlin.plugin.compose", libs.versions.kotlin.get()))
    implementation(marker("com.android.kotlin.multiplatform.library", libs.versions.agp.get()))
    implementation(marker("org.jetbrains.compose", libs.versions.composeMultiplatform.get()))
}

fun marker(pluginId: String, version: String) = "$pluginId:$pluginId.gradle.plugin:$version"
