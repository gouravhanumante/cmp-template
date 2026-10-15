plugins {
    id("boundless.kmp.compose")
}

// Every user-facing string and font lives here, NOT in core:designsystem, so that core:common can
// reference string resources too — designsystem depends on core:common, so owning the resources
// there would make that cycle impossible to close. This module depends on no other project module.
compose.resources {
    publicResClass = true
    packageOfResClass = "com.boundless.template.core.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // api, not implementation: StringResource and UiText appear in dependents' public API
            // (e.g. BookingStatus.label, XxxUiState.errorMessage), so they must leak through.
            api(libs.compose.components.resources)
            api(libs.compose.runtime)
        }
    }
}
