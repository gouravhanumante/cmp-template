import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("boundless.kmp.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
    sourceSets.getByName("commonMain").dependencies {
        implementation(libs.findLibrary("compose-runtime").get())
        implementation(libs.findLibrary("compose-foundation").get())
        implementation(libs.findLibrary("compose-material3").get())
        implementation(libs.findLibrary("compose-ui").get())
        implementation(libs.findLibrary("compose-components-resources").get())
        implementation(libs.findLibrary("androidx-lifecycle-viewmodel").get())
        implementation(libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
        implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
        implementation(libs.findLibrary("koin-compose").get())
        implementation(libs.findLibrary("koin-compose-viewmodel").get())
    }
}
