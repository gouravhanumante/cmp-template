plugins {
    // Declared once here so each subproject's classloader doesn't load them again.
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseAppDistribution) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room3) apply false
    alias(libs.plugins.kover)
}

// Coverage: read the report as a map of what is untested, not as a number to raise.
subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
}

// -PuseSystemNode=true uses the installed Node.js instead of downloading one, for networks
// whose TLS proxy blocks Gradle's download.
allprojects {
    plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin> {
        if (providers.gradleProperty("useSystemNode").orNull == "true") {
            project.the<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec>().download = false
        }
    }
}

dependencies {
    kover(project(":shared"))
    kover(project(":core:common"))
    kover(project(":core:designsystem"))
    kover(project(":core:datastore"))
    kover(project(":core:network"))
    kover(project(":core:database"))
    kover(project(":feature:home"))
    kover(project(":feature:devtools"))
}

kover {
    reports {
        filters {
            excludes {
                packages("com.boundless.template.core.resources")
                classes("*.di.*Module*", "*ComposableSingletons*")
            }
        }
    }
}
