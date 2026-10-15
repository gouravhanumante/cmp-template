import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseAppDistribution) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
}

// Firebase is wired only once google-services.json exists, so a fresh clone builds without it.
val hasFirebaseConfig = file("google-services.json").exists()
if (hasFirebaseConfig) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
    apply(plugin = "com.google.firebase.appdistribution")
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

// Backend per variant, in priority order: -PapiBaseUrl=..., then the variant's gradle.properties
// entry, then blank (MainApplication falls back to core:network's defaultApiBaseUrl()).
fun apiBaseUrl(variantProperty: String): String =
    (findProperty("apiBaseUrl") as String?)?.takeIf { it.isNotBlank() }
        ?: (findProperty(variantProperty) as String?).orEmpty()

val appName = providers.gradleProperty("app.name").get()

dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.android)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    debugImplementation(libs.heimdall.core)
    debugImplementation(libs.heimdall.network.ktor)
    debugImplementation(libs.heimdall.ui)
}

android {
    namespace = "com.boundless.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    // Nothing is committed: set these in ~/.gradle/gradle.properties or pass them from CI.
    // Absent = the release variant falls back to the debug signing config.
    val releaseStoreFile = (findProperty("releaseStoreFile") as String?)?.let(::file)
    signingConfigs {
        if (releaseStoreFile?.exists() == true) {
            create("release") {
                storeFile = releaseStoreFile
                storePassword = findProperty("releaseStorePassword") as String?
                keyAlias = findProperty("releaseKeyAlias") as String?
                keyPassword = findProperty("releaseKeyPassword") as String?
            }
        }
    }

    defaultConfig {
        applicationId = providers.gradleProperty("app.applicationId").get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // CI passes -PbuildVersionCode/-PbuildVersionName; local builds fall back to 1 / "1.0-dev".
        versionCode = (findProperty("buildVersionCode") as String?)?.toIntOrNull() ?: 1
        versionName = (findProperty("buildVersionName") as String?) ?: "1.0-dev"
        buildConfigField("boolean", "ENABLE_NETWORK_LOGGING", "false")
        manifestPlaceholders["crashlyticsCollectionEnabled"] = "false"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfigs.findByName("release")?.let { signingConfig = it }
            resValue("string", "app_name", appName)
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl("app.apiBaseUrl.prod")}\"")
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "true"
            if (hasFirebaseConfig) {
                // R8 renames everything, so a release crash is unreadable without this build's mapping.
                configure<CrashlyticsExtension> { mappingFileUploadEnabled = true }
            }
        }
        // Production backend with logging on and no minification, for chasing release-only bugs.
        create("releaseDebug") {
            initWith(getByName("release"))
            applicationIdSuffix = ".debug"
            buildConfigField("boolean", "ENABLE_NETWORK_LOGGING", "true")
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            versionNameSuffix = "-release-debug"
            matchingFallbacks += "release"
            resValue("string", "app_name", "$appName Prod Debug")
            if (hasFirebaseConfig) {
                configure<CrashlyticsExtension> { mappingFileUploadEnabled = false }
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "$appName Debug")
            buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl("app.apiBaseUrl.debug")}\"")
            if (hasFirebaseConfig) {
                configure<CrashlyticsExtension> { mappingFileUploadEnabled = false }
            }
        }
    }
    sourceSets {
        getByName("releaseDebug") {
            manifest.srcFile("src/debug/AndroidManifest.xml")
            res.srcDir("src/debug/res")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }
}

dependencies {
    add("releaseDebugImplementation", libs.heimdall.core.noop)
    add("releaseDebugImplementation", libs.heimdall.network.ktor.noop)
    add("releaseDebugImplementation", libs.heimdall.ui.noop)
    add("releaseImplementation", libs.heimdall.core.noop)
    add("releaseImplementation", libs.heimdall.network.ktor.noop)
    add("releaseImplementation", libs.heimdall.ui.noop)
}
