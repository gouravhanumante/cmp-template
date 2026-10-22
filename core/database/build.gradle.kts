plugins {
    id("boundless.kmp.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room3)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.room3.runtime)
        }
        androidMain.dependencies {
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.sqlite.bundled)
        }
        jvmMain.dependencies {
            implementation(libs.sqlite.bundled)
        }
        wasmJsMain.dependencies {
            implementation(libs.sqlite.web)
            implementation(libs.kotlinx.browser)
            // The browser has no SQLite: this local npm package runs SQLite's own Wasm build in a Web Worker.
            implementation(npm("app-sqlite-worker", layout.projectDirectory.dir("worker").asFile))
        }
    }
}

dependencies {
    listOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64", "kspJvm", "kspWasmJs").forEach { target ->
        add(target, libs.room3.compiler)
    }
}

// Exported schemas are checked in: they are what a future migration is checked against.
room3 {
    schemaDirectory("$projectDir/schemas")
}
