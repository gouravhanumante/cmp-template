# Architecture

## Platforms

| Platform | Entry point | Notes |
|---|---|---|
| Android | `androidApp/.../MainApplication.kt`, `MainActivity.kt` | Starts Koin with `androidContext`, splash held until `AppReadyState` is ready, Heimdall overlay in debug |
| iOS | `iosApp/iosApp/iOSApp.swift` → `ContentView.swift` → `shared/.../MainViewController.kt` | One static `Shared` framework; `KoinStarter.kt` starts Koin once |
| Desktop | `desktopApp/.../desktop/Main.kt` | Compose `Window`; packaged as `.dmg` / `.msi` / `.deb` |
| Web | `webApp/.../web/Main.kt` + `webApp/src/wasmJsMain/resources/index.html` | `ComposeViewport` mounted in `#app`; Wasm, so a modern browser is required |

Everything else is written once in `commonMain`. Drop to `expect`/`actual` only where the
platform API really differs. The current `expect`s:

| Declaration | Module | Why it differs |
|---|---|---|
| `currentPlatform`, `platformIoDispatcher()` | `core:common` | Platform identity; the browser has no IO dispatcher |
| `coreDatastorePlatformModule` | `core:datastore` | Secure store: Android Keystore, iOS Keychain, desktop OS credential store (java-keyring), browser memory + HttpOnly cookie |
| `httpClientEngine()`, `defaultApiBaseUrl()`, `networkLog()` | `core:network` | OkHttp / Darwin / JS engine; emulator vs simulator vs origin |
| `rememberImagePicker`, `decodeToImageBitmapOrNull`, `cropSquareToJpeg`, `AppOtpField` | `core:designsystem` | Camera/gallery vs file chooser vs `<input type=file>`; the web OTP field needs a native input |
| `app*Transition` | `shared` | Each platform's native navigation feel |

## Modules

```
androidApp / iosApp / desktopApp / webApp   ← entry points, depend only on :shared
            │
          shared                            ← app shell: App.kt, AppViewModel, nav graph, DI, iOS framework
            │
   feature:*  (feature:home)                ← one module per feature, never depend on each other
            │
   core:network ─ core:datastore            ← Ktor client, API envelope, tokens, key-value store
   core:designsystem                        ← theme tokens + components
   core:common ─ core:resources             ← AppResult, DomainError, dispatchers / strings, fonts, UiText
```

Dependency rules:
- `core:*` never depends on `feature:*` or `:shared`.
- Features never depend on each other. Anything two features need goes into a `core:*` module or
  is composed in `:shared`.
- The entry-point modules depend only on `:shared`.
- Every library module applies `boundless.kmp.library` or `boundless.kmp.compose`
  (`build-logic/convention/`). Those plugins set the targets (Android, iOS, JVM desktop, Wasm),
  SDK levels, JVM target, the Android namespace (`com.boundless.template.<module path>`) and the
  common dependencies. Don't copy target or compiler config into a module.

## Inside a feature

```
feature/<name>/src/commonMain/kotlin/com/boundless/template/feature/<name>/
├── domain/         pure Kotlin: models, Repository interfaces, use cases (no Ktor, Compose, platform)
├── data/           Api (Ktor), RepositoryImpl, dto/ (@Serializable), mappers. DTOs never leave here.
├── presentation/   <Screen>Screen.kt, <Screen>ViewModel.kt, <Screen>UiState.kt (UiState + Event [+ Effect])
└── di/             <name>Module: Koin bindings for the above
```

Direction: `presentation → domain ← data`. `feature:home` is the worked example.

## State (MVVM)

- One ViewModel per screen. It exposes one `StateFlow<UiState>` and one `onEvent(event)`.
- `UiState` is an immutable data class, updated with `_uiState.update { ... }`.
- One-shot effects (navigate, snackbar) go through a `Channel`, never into `UiState`.
- Composables are stateless: the `XxxScreen` collects state with `collectAsStateWithLifecycle()`
  and passes it to an `XxxContent(state, onEvent)` that previews and tests can call directly.
- Every screen handles loading, error and content (and empty, where a list can be empty).

## Data flow

```
Screen ─events→ ViewModel ─→ (UseCase) ─→ Repository (interface, domain)
                   ↑                              │
               StateFlow                 RepositoryImpl (data) ─→ Api ─→ HttpClient (core:network)
                                                                            │
                                         AppResult<T> ← apiRequest { } ← ApiEnvelope { success, data, error, meta }
```

- `apiRequest`, `apiRequestNullable`, `apiRequestPaged` (`core/network/.../ApiRequest.kt`) unwrap
  the envelope and turn every transport or parse failure into a `DomainError`. Nothing throws
  across a layer.
- `DomainError.toUserMessage()` (`core/designsystem/.../error/DomainErrorMessages.kt`) is the only
  place an error becomes user-visible text.

## Networking

- Two clients (`core/network/.../di/CoreNetworkModule.kt`): `UnauthenticatedClient` (public
  endpoints, token refresh) and `AuthenticatedClient` (bearer token from `TokenStorage`).
- A 401 triggers one refresh through `POST auth/refresh` (`ApiTokenRefresher.kt`); concurrent 401s
  share it. A failed refresh clears tokens and emits `SessionEvents`.
- Every request sends `X-App-Platform`, and `X-App-Build` when the build number is known. A response header `X-App-Gate:
  maintenance | update-required` makes `AppGateEvents` emit, and `AppViewModel` swaps the whole UI
  for the gate screen (`shared/.../app/gate/AppGateScreen.kt`).
- JSON: `ignoreUnknownKeys`, `explicitNulls = false`, so backend additions never break the app.

## Storage

| Platform | Tokens (`TokenStorage`) | Settings (`KeyValueStore`) |
|---|---|---|
| Android | Keystore-encrypted DataStore | DataStore |
| iOS | Keychain | DataStore in Documents |
| Desktop | OS credential store via java-keyring | DataStore in the per-user app data folder |
| Web | Access token in memory; refresh token in an HttpOnly cookie the backend sets | `localStorage` (non-secret only) |

## Strings and resources

All strings, fonts and images live in `core:resources` (`composeResources/values/strings_<area>.xml`)
so every module, including `core:common`, can use them. `UiText` carries a string resource or raw
text through ViewModels; resolve it in a composable with `resolve()`.

## DI

Koin. Each module declares a `val xxxModule`; `shared/.../app/di/InitKoin.kt` lists them all.
Constructor injection only; no `KoinComponent` or `get()` inside classes.
