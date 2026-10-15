---
description: Engineering rules for this Kotlin Multiplatform + Compose Multiplatform app (Clean Architecture + MVVM, Ktor + Koin). Follow these when generating or editing any Kotlin code.
applyTo: "**/*.kt,**/*.kts"
---

# Kotlin Multiplatform rules

Architecture reference: `docs/architecture.md`. Workflow: `docs/GUIDE.md`.

## Platforms and modules
- Android, iOS, desktop (JVM) and web (Wasm) from one Compose Multiplatform codebase. No
  platform-specific UI.
- Write everything in `commonMain`. Use `expect`/`actual` only where the platform API really
  differs, and give **every** target an `actual`. A platform that can't support something says so
  (error result or hidden control), never a silent no-op.
- `core:*` never depends on `feature:*` or `:shared`. Features never depend on each other.
  `:shared` aggregates features, owns navigation and DI, and produces the iOS framework.
  `androidApp`, `iosApp`, `desktopApp`, `webApp` depend only on `:shared`.
- Library modules apply `boundless.kmp.library` or `boundless.kmp.compose`. Never copy target or
  compiler config into a module.

## Layers (per feature)
- **domain**: pure Kotlin. Models, `Repository` interfaces, use cases. No Ktor, Compose, DataStore
  or platform imports.
- **data**: repository implementations, Ktor APIs, DTOs and mappers. DTOs never leave this layer.
- **presentation**: ViewModel + Compose screens.
- Direction: `presentation → domain ← data`. Never expose a DTO or `HttpResponse` to presentation.

## MVVM and state
- One ViewModel per screen with exactly one `StateFlow<XxxUiState>` and one `onEvent(event)`.
- `UiState` is an immutable data class updated with `_uiState.update { it.copy(...) }`. One-shot
  effects go through a `Channel`, never in `UiState`.
- Composables are stateless and side-effect free: they receive state and emit events. No
  repository, network or DI lookups in a composable body except the screen's `koinViewModel()`.
- Collect with `collectAsStateWithLifecycle()`.
- Inject `DispatcherProvider`; never hard-code `Dispatchers.IO` in a repository or use case.

## Dependency injection
- Koin, one `val xxxModule` per module, all listed in `shared/.../app/di/InitKoin.kt`.
- Constructor injection; depend on interfaces. No `KoinComponent` / `get()` inside classes.

## Networking
- All API access goes through `core:network` and `apiRequest { }` / `apiRequestPaged { }`.
- Responses are the envelope `{ success, data, error, meta }`; `error.code` maps to a `DomainError`.
  Never show a raw HTTP status or server message to the user; use `DomainError.toUserMessage()`.
- DTOs are `@Serializable`, with `@SerialName` when the wire name differs.
- Repositories return `AppResult<T>` and never throw across a layer boundary.
- Auth is handled once by the Ktor `Auth` plugin. Never add token headers or retry logic in a feature.
- Base URLs come from `ApiConfig`, never a hard-coded string in a feature.

## Security
- Tokens live only in `TokenStorage` (Keystore, Keychain, OS credential store, browser memory +
  HttpOnly cookie). Never in `KeyValueStore`, preferences, logs or Compose state.
- Never log tokens, OTPs, phone numbers or full bodies in release builds. HTTP logging is debug-only.
- No secrets in the repo or in the web bundle.
- Client-side validation is UX only; the backend is the authority.
- Clear tokens and per-user cached data on logout.

## UI and design system
- Colours, typography, spacing, shapes and shared components come from `core:designsystem`. No hex
  colours, raw `dp` spacing or ad-hoc text styles in feature screens.
- All user-visible strings live in `core:resources` (`strings_<feature>.xml`). Whole sentences only;
  never build copy by concatenation.
- Every screen handles loading, empty (where it applies), error and content.
- Respect safe areas (`safeDrawingPadding()` / `AppScreen`).

## Naming and style
- Suffixes: `...Screen`, `...Content`, `...ViewModel`, `...UiState`, `...Event`, `...Effect`,
  `...Repository` / `...RepositoryImpl`, `...Api`, `...Dto`, `...UseCase`, `...Module`.
- Prefer `val` and immutable collections. Sealed interfaces for closed hierarchies; exhaustive
  `when` without `else`.
- No `!!`. No `runBlocking` outside tests. No `GlobalScope`.

## Testing
- Unit tests in `commonTest`: ViewModel state transitions, mappers, envelope/error parsing, rules.
- Fakes over mocking frameworks. Ktor `MockEngine` for the data layer. Never hit a live backend.
- Name tests after the rule they check; Arrange-Act-Assert. See `docs/GUIDE.md` section 4.

## Definition of done (per feature)
Feature doc → domain → data → ViewModel + screens → DI registered → route in `AppNavHost` →
`commonTest` passes → builds on all four platforms → `docs/PROGRESS.md` and `docs/TODO.md` updated.
