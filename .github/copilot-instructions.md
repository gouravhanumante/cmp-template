# Rules for anyone changing the code

Full working agreement: [docs/GUIDE.md](../docs/GUIDE.md). Architecture: [docs/architecture.md](../docs/architecture.md).
Detailed rules: [instructions/frontend.instructions.md](instructions/frontend.instructions.md),
[instructions/web-security.instructions.md](instructions/web-security.instructions.md).

## Docs change with the code, in the same commit
A change that alters behaviour and leaves a doc saying the old thing is not finished.

| If the change touches... | Update |
|---|---|
| A screen, a rule, what the user sees | `docs/features/<feature>.md` |
| Modules, layers, platforms, DI, networking, storage | `docs/architecture.md` |
| Setup, run commands, Firebase, signing, packaging | `README.md` |
| A feature finished, a gap found or closed | `docs/PROGRESS.md` and `docs/TODO.md` |

Before committing: search `docs/` for every name and concept you changed; point each factual claim
at the file or symbol that proves it; if code and doc disagree and it isn't obvious which is right,
ask instead of changing either.

## Intent comes from the user
Order of truth: the user, then plain reasoning about what the app should obviously do, then
docs and code. Never infer intended behaviour from the implementation alone. When unsure, ask.
Discuss options before building anything with a new endpoint, storage format, dependency or
user-facing control.

## Tests
State the rule in plain words, test the rule, and watch the test fail against a plausible bug
before trusting it. A test that can't fail is a regression guard; label it.

## Checks before committing
```sh
./gradlew :androidApp:compileDebugKotlin testAndroidHostTest testDebugUnitTest jvmTest \
  :desktopApp:compileKotlin :webApp:compileKotlinWasmJs
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64   # when shared or iOS code changed
```
Don't trust IDE "no errors" for Kotlin; run the compile.
