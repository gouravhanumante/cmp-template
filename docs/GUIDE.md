# How to work on an app built from this template

This is the working agreement. It applies to people and to AI assistants alike; the short version
for tools lives in [.github/copilot-instructions.md](../.github/copilot-instructions.md).

## 1. Starting a new app

1. Create the new repo from this one (GitHub "Use this template", or copy without `.git`).
2. Rename the package `com.boundless.template` → `com.yourco.yourapp`:
   - Android Studio: right-click `com.boundless.template` in any module → Refactor → Rename
     (package), with "Rename directory" and "Search in comments and strings".
   - Then search the whole repo for `com.boundless.template` and `boundless.template`. Check
     `build-logic/convention/.../boundless.kmp.library.gradle.kts` (namespace prefix),
     `core/resources/build.gradle.kts` (`packageOfResClass`), `androidApp/build.gradle.kts`
     (`namespace`), `androidApp/proguard-rules.pro`, `desktopApp/build.gradle.kts` (`mainClass`).
3. `gradle.properties`: set `app.name`, `app.applicationId`, and `app.apiBaseUrl.prod/debug`.
4. iOS: `iosApp/Configuration/Config.xcconfig` → `TEAM_ID`, `PRODUCT_NAME`,
   `PRODUCT_BUNDLE_IDENTIFIER`; the `releaseDebug` bundle id in the Xcode project.
5. Search for `TODO(template)`. Each one is a value that must change per app: store URLs, the
   hosted API URL, the desktop data folder and keychain service name.
6. Firebase: create the project, add `google-services.json` and `GoogleService-Info.plist`
   (see README → Firebase).
7. Replace the icons: `androidApp/src/main/res/` and `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/`.
8. Rewrite `README.md`'s first section for your app, clear [PROGRESS.md](PROGRESS.md) and
   [TODO.md](TODO.md) down to what is true for it, and update `rootProject.name`.

## 2. Adding a feature

Copy the shape of `feature:home`. In order:

1. **Write the feature doc first**: `docs/features/<name>.md` from
   [features/README.md](features/README.md). Screens, states, rules, API calls.
   If something is unclear, ask before coding it.
2. **Module**: `feature/<name>/build.gradle.kts` (copy home's), then `include(":feature:<name>")`
   in `settings.gradle.kts` and `kover(project(":feature:<name>"))` in the root build file.
3. **Domain**: models and the `Repository` interface. Pure Kotlin.
4. **Data**: `@Serializable` DTOs, the `Api` class using `apiRequest { }`, mappers, `RepositoryImpl`.
5. **Presentation**: `UiState` + `Event` (+ `Effect`), the ViewModel, then `Screen` + `Content`.
   Strings go in `core/resources/.../values/strings_<name>.xml`; colours, spacing and components
   come from `core:designsystem`.
6. **DI**: `di/<Name>Module.kt`, then add it to `shared/.../app/di/InitKoin.kt`.
7. **Navigation**: a `@Serializable` route + `composable<Route> { }` in
   `shared/.../app/navigation/AppNavHost.kt`, and `api(projects.feature.<name>)` in
   `shared/build.gradle.kts`.
8. **Tests** in `commonTest` (see section 4).
9. **Docs**: update the feature doc, [PROGRESS.md](PROGRESS.md), and [TODO.md](TODO.md) for anything
   left out on purpose.
10. Run the checks (section 5) and try it on at least Android and one other platform.

If a feature needs a platform API (camera, location, payments, push), put the `expect` in the
smallest `core:*` module that makes sense and give every platform an `actual`. A platform that
can't do it says so explicitly (an error result or a hidden control), never a silent no-op.

## 3. Docs: what lives where

Docs describe what the code does **now**, not a history. A doc that contradicts the code is worse
than no doc. **Every change that alters behaviour updates the owning doc in the same commit.**

| If the change touches... | Update |
|---|---|
| A screen, a rule, what the user sees | `docs/features/<feature>.md` |
| Modules, layers, platforms, DI, networking, storage | `docs/architecture.md` |
| Setup, run commands, Firebase, signing, packaging | `README.md` |
| A feature finished or a gap closed | `docs/PROGRESS.md` and `docs/TODO.md` |
| A known gap, risk or decision deliberately postponed | `docs/TODO.md` (what, why, options) |
| How we work | this file |

Before finishing a change:
1. Search `docs/` for the concept you changed (a screen name, an endpoint, a rule), not just the
   symbol.
2. Fix anything the change made wrong, and check the sentences around it. Stale text clusters.
3. Point each factual claim at the file or symbol that proves it.
4. If code and doc disagree and it isn't obvious which is right, ask. Don't silently change either.

Don't create a new doc per change. Update the one that owns the topic.

## 4. Tests

- Tests live in `commonTest`, so they run on every target (Android host tests and desktop `jvmTest`
  are the fast ones).
- Test a **rule**, stated in plain words first, e.g. "an unreachable backend ends in a retryable
  error, never an endless spinner" (`HomeViewModelTest`). Don't assert the implementation back at
  itself.
- Prove each new test can fail: break the code in the most plausible way, watch the test go red,
  restore it. A test nobody has seen fail proves nothing. If no realistic bug can make it fail,
  it's a regression guard, so say so.
- Fake the domain interfaces (`FakeHealthRepository`); use Ktor `MockEngine` for the data layer.
  Never call a real backend from a test.
- ViewModel tests: `Dispatchers.setMain(StandardTestDispatcher())`, `runTest`, `advanceUntilIdle()`.

## 5. Checks before committing

```sh
./gradlew :androidApp:compileDebugKotlin testAndroidHostTest testDebugUnitTest jvmTest \
  :desktopApp:compileKotlin :webApp:compileKotlinWasmJs
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64   # when shared or iOS code changed
```

Don't trust the IDE's "no errors" for Kotlin; run the compile.

## 6. Commits

- One concern per commit. Code and its docs go together.
- Plain ASCII commit messages (no backticks, no `\u` escapes).
- Never commit secrets: `google-services.json`, `GoogleService-Info*.plist`, keystores, API keys.

## 7. Making decisions

For anything with a new endpoint, a new storage format, a new user-facing control or a new
dependency: write down the options and the trade-off, decide, then build. Record the decision in
the feature doc (or `architecture.md` if it is app-wide). If a fix turns out much bigger than it
looked, stop and re-decide rather than pushing through.
