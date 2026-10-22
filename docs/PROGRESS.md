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
| `core:resources` | Compose Resources, Montserrat (four static weights, SIL OFL), `UiText`, strings for app shell, design system, errors, home |
| `core:designsystem` | Theme tokens and components; image picker/cropper and OTP field on all four platforms |
| `core:datastore` | `TokenStorage` and `KeyValueStore` on all four platforms |
| `core:network` | Ktor clients, envelope parsing, error mapping, token refresh, app gate |
| `core:database` | Room 3 on all four platforms; exported notes schema, platform drivers, SQLite Wasm worker; `NoteDaoTest` |
| `shared` | `App`, `AppViewModel` (maintenance / update gate, snackbar messages), navigation, Koin |
| `feature:home` | Welcome screen + backend health check, 4 tests |
| `feature:devtools` | Debug-only Test hub: Database, Design system and Core checks; `DevToolsScreen`, `DatabaseScreen`, `ComponentsScreen`, `ChecksScreen` |

## Verification on 2026-10-22

Passed Android compilation, Android host/unit tests, `jvmTest`, desktop compilation and
desktop UI tests, web compilation, and `:shared:linkDebugFrameworkIosSimulatorArm64`.
The web app was launched at localhost:8081 with the isolation headers and Montserrat assets.
Theme persistence was checked in the browser. The complete manual browser matrix remains
unfinished; see TODO. Android (Pixel 9a) and iOS (iPhone 17 Pro) were rebuilt, installed and
launched for the README screenshots in `docs/screenshots/`. The native desktop app also launched;
its README image uses the `RenderScreens` Home preview because macOS screen capture permission
was unavailable. The README labels the rendered preview separately from the three direct captures.

`SignInRenewalTest`, `ApiTokenRefresherTest`, `ThemeContrastTest` and `SidePanelTest` have
been seen failing under deliberately planted regressions. Other new tests currently provide
regression coverage without that mutation proof.

## Integrations
| Integration | State |
|---|---|
| Firebase Crashlytics + Analytics | Wired on Android and iOS; inactive until config files are added |
| Firebase App Distribution | Gradle plugin applied on Android when config exists |
| Heimdall | Android debug builds; no-op artifacts in release |
| Kover | All modules |
