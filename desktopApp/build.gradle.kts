import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
}

compose.desktop {
    application {
        mainClass = "com.boundless.template.desktop.MainKt"

        nativeDistributions {
            // A .dmg builds only on macOS and a .msi only on Windows; CI needs one runner per OS.
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = providers.gradleProperty("app.name").get()
            packageVersion = "1.0.0"
            macOS {
                bundleID = providers.gradleProperty("app.applicationId").get()
            }
        }
    }
}
