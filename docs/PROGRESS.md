# Progress

What is built and verified in the template. Update when something is finished; move gaps to
[TODO.md](TODO.md).

## Platforms
All four were run on 2026-10-15 and showed the home screen; with no backend on port 8080 the
Backend card correctly showed "Offline" with a retry.

| Platform | Ran on | Build check |
|---|---|---|
| Android | Pixel 9a emulator (debug, with Heimdall) | `./gradlew :androidApp:compileDebugKotlin testAndroidHostTest` |
| iOS | iPhone 17 Pro simulator (`releaseDebug`, no Firebase config) | `xcodebuild -scheme iosApp` / `:shared:linkDebugFrameworkIosSimulatorArm64` |
| Desktop (JVM) | macOS (`:desktopApp:run`) | `./gradlew :desktopApp:compileKotlin` |
| Web (Wasm) | Browser via `:webApp:wasmJsBrowserDevelopmentRun` | `./gradlew :webApp:compileKotlinWasmJs` |

## Modules
| Module | State |
|---|---|
| `build-logic` | `boundless.kmp.library` (Android, iOS, JVM, Wasm targets) and `boundless.kmp.compose` |
| `core:common` | `AppResult`, `DomainError`, `DispatcherProvider`, `TodayProvider`, `AppMessenger`, `Paged`, `TimeOfDay` |
| `core:resources` | Compose Resources, Plus Jakarta Sans, `UiText`, strings for app shell, design system, errors, home |
| `core:designsystem` | Theme tokens and components; image picker/cropper and OTP field on all four platforms |
| `core:datastore` | `TokenStorage` and `KeyValueStore` on all four platforms |
| `core:network` | Ktor clients, envelope parsing, error mapping, token refresh, app gate |
| `shared` | `App`, `AppViewModel` (maintenance / update gate, snackbar messages), navigation, Koin |
| `feature:home` | Welcome screen + backend health check, 4 tests |

## Integrations
| Integration | State |
|---|---|
| Firebase Crashlytics + Analytics | Wired on Android and iOS; inactive until config files are added |
| Firebase App Distribution | Gradle plugin applied on Android when config exists |
| Heimdall | Android debug builds; no-op artifacts in release |
| Kover | All modules |
