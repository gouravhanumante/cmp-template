import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

// ":core:network" -> "com.boundless.template.core.network"
val moduleNamespace = "com.boundless.template." +
    path.removePrefix(":").replace(':', '.').replace("-", "")

kotlin {
    iosArm64()
    iosSimulatorArm64()
    // Desktop (macOS, Windows, Linux).
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
        namespace = moduleNamespace
        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        // Compose Resources compiles composeResources/values into a .cvr that only reaches the
        // APK through the Android assets pipeline, which this target leaves off by default.
        androidResources.enable = true
        // Host tests exercise pure Kotlin; stubbed android.* calls must return defaults, not throw.
        withHostTest { isReturnDefaultValues = true }
    }

    sourceSets.getByName("commonMain").dependencies {
        api(libs.findLibrary("kotlinx-coroutines-core").get())
        api(libs.findLibrary("koin-core").get())
        implementation(libs.findLibrary("kotlinx-serialization-json").get())
    }
    sourceSets.getByName("commonTest").dependencies {
        implementation(libs.findLibrary("kotlin-test").get())
        implementation(libs.findLibrary("kotlinx-coroutines-test").get())
    }
}
