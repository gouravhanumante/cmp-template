plugins {
    id("boundless.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.okio)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        jvmMain.dependencies {
            implementation(libs.java.keyring)
        }
    }
}
