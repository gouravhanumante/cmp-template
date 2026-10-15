plugins {
    id("boundless.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.resources)
            api(libs.kotlinx.datetime)
        }
    }
}
