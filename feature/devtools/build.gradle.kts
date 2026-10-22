plugins {
    id("boundless.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.designsystem)
            api(projects.core.network)
            api(projects.core.resources)
            implementation(projects.core.database)
            implementation(projects.core.datastore)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
        }
    }
}
