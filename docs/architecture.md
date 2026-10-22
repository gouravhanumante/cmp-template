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
  feature:*  (home, devtools)              ← one module per feature, never depend on each other
            │
   core:network ─ core:datastore            ← Ktor client, API envelope, tokens, key-value store
  core:database                           ← Room 3, SQLite drivers and the example notes table
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
  share it. `RefreshResult.Refused` clears tokens and emits `SessionEvents`; `Unreachable`
  preserves tokens when offline, rate-limited or the server is unavailable. `AppViewModel`
  collects session expiry and shows a snackbar (`app_session_expired`).
- Every request sends `X-App-Platform`, and `X-App-Build` when the build number is known. A response header `X-App-Gate:
  maintenance | update-required` makes `AppGateEvents` emit, and `AppViewModel` swaps the whole UI
  for the gate screen (`shared/.../app/gate/AppGateScreen.kt`).
- JSON: `ignoreUnknownKeys`, `explicitNulls = false`, so backend additions never break the app.

## Storage

`core:database` uses Room 3.0.3 (`androidx.room3`) and SQLite 2.7.1. `AppDatabase` declares
the example `notes` table and `NoteDao`; KSP generates implementations for all four platforms.
Exported schemas live under `core/database/schemas/` and must accompany future migrations.
`CoreDatabaseModule.*` uses bundled SQLite in Android's private database directory, iOS
Application Support, and the desktop app data folder. No destructive-migration fallback is enabled.

### Web database

`CoreDatabaseModule.wasmJs.kt` connects `WebWorkerSQLiteDriver` to the local npm package
`core/database/worker/`. Its worker runs pinned SQLite Wasm 3.54.1 with the `opfs` VFS when
cross-origin isolation and `OpfsDb` are available. OPFS data is private to the browser origin;
changing host or port creates a separate storage area. It is not a server backup.

`webApp/webpack.config.d/devServer.js` sends `Cross-Origin-Opener-Policy: same-origin`
and `Cross-Origin-Embedder-Policy: require-corp`. Production must send both too. The bundled
`webApp/src/wasmJsMain/resources/_headers` supplies them for Cloudflare Pages or Netlify.
Other hosts need equivalent configuration. These policies can affect cross-origin popups
and embedded content; evaluate those integrations before adding them.

Without available OPFS, the worker uses memory. `databaseStorage()` exposes the expected
storage mode and the Database page warns when the page lacks isolation or the storage API.
An OPFS open error is reported rather than silently opening a new in-memory database.
Browser quotas, private mode and clearing site data can affect persistence. Multi-tab
contention can produce locking errors; no cross-tab retry coordination has been added.

## Navigation and appearance

`AppShell` owns Home, Test and Settings. Android/iOS use bottom navigation; desktop/web use
a sidebar from 840 dp and an icon rail below that (`navigationLayout`). Test and all its
routes are registered only for `AppBuildInfo.isDebug`. Android uses `BuildConfig.DEBUG`,
iOS uses `Platform.isDebugBinary`, desktop checks the jpackage launcher property, and web
recognizes localhost/LAN hosts. These are developer-UI switches, not security boundaries.

Settings changes the whole-app palette through `ThemeSettings`, persisted by
`StoredThemeSettings` in `KeyValueStore`. `AppViewModel.palette` feeds `AppTheme`.
`WidthClass` centralizes 600/840 dp thresholds. `AppScreen.maxContentWidth` defaults to
720 dp; pass `Dp.Infinity` for a full-width screen. `AppFormSheet` opens a bottom sheet below
600 dp and a right-side panel at larger widths. Desktop opens maximized and `SingleInstance`
prevents a second copy writing to the same local database.

## Settings storage

| Platform | Tokens (`TokenStorage`) | Settings (`KeyValueStore`) |
|---|---|---|
| Android | Keystore-encrypted DataStore | DataStore in the app's files folder |
| iOS | Keychain | DataStore in Application Support (Documents would show it in the Files app) |
| Desktop | OS credential store via java-keyring | DataStore in the per-user app data folder (below) |
| Web | Access token in memory; refresh token in an HttpOnly cookie the backend sets | `localStorage` (non-secret only) |

The app data folder on computers (`desktopAppDataPath()`, `core/common/src/jvmMain/.../AppDataFolder.jvm.kt`,
tested by `AppDataFolderTest`), named by `APP_NAME`:

| OS | Folder |
|---|---|
| macOS | `~/Library/Application Support/<AppName>` |
| Windows | `%LOCALAPPDATA%\<AppName>`, not Roaming `%APPDATA%`, which is copied between computers on company networks |
| Linux | `$XDG_DATA_HOME/<appname>`, else `~/.local/share/<appname>`; created readable by the user only |

## Strings and resources

All strings, fonts and images live in `core:resources` (`composeResources/values/strings_<area>.xml`)
so every module, including `core:common`, can use them. `UiText` carries a string resource or raw
text through ViewModels; resolve it in a composable with `resolve()`.

The app font is Montserrat, bundled as four static TTFs (Regular, Medium, SemiBold and Bold)
in `core/resources/src/commonMain/composeResources/font/`. The licence is
`core/resources/FONT-LICENSE-Montserrat.txt`. `appFontFamily()` and `appTypography()` in
`core/designsystem/.../theme/Type.kt` own font selection: the local `style()` builder sets
the family once for all 15 Material text roles. Components choose a typography role, not
a font family. `AppTheme` installs that typography for the whole app.

Resource-heavy screens can use `import com.boundless.template.core.resources.*` and
`import com.boundless.template.core.designsystem.component.*` rather than importing every
string and component separately (`ComponentsScreen.kt`, `ChecksScreen.kt`). Keep individual
imports where they improve clarity or resolve name collisions.

Button design alternatives are in `docs/design/button-options.html`. This is an interactive
comparison, not the app's selected button design; it does not change `Buttons.kt`.
The earlier button hierarchy is retained: `AppSecondaryButton` is outlined (red for
destructive actions), and `AppTonalButton` is filled. Option D's solid secondary and
destructive variants were rejected because they competed with the primary action.
See `core/designsystem/.../component/Buttons.kt` and `theme/Shape.kt` (`AppRadius.button`).

## DI

Koin. Each module declares a `val xxxModule`; `shared/.../app/di/InitKoin.kt` lists them all.
Constructor injection only; no `KoinComponent` or `get()` inside classes.
