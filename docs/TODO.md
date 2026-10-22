# TODO and known gaps

Each entry: what is missing, why it isn't done, and the options. Remove an entry in the same
commit that closes it.

## Per-app values left as placeholders
Search `TODO(template)`. Store URLs (`shared/.../gate/AppGateScreen.kt`), hosted API URL and LAN IP
(`core/network/.../ApiConfig.kt`), desktop data folder and keychain service
(`core/datastore/src/jvmMain/.../CoreDatastoreModule.jvm.kt`).

## No login feature
The network layer is ready for bearer tokens (`AuthenticatedClient`, `TokenStorage`,
`POST auth/refresh`), but there is no sign-in screen. Options: Supabase Auth, phone OTP against
your own backend, or email/password. Decide per app.

## Session end has no sign-in route
`AppViewModel` shows a session-expired snackbar. Apps adding login must also route to their
sign-in screen; the template has no login feature or per-user cache to clear.

## Tester distribution workflows
Android release App Distribution options are configured in `androidApp/build.gradle.kts`.
The requested Android/iOS tester workflows and their signing/identity setup are not yet added.

## Developer tools follow-up
`feature:devtools` exercises notes, settings, token storage, health, formatting and pickers.
Complete the manual browser matrix: notes across reloads, two-tab writes, picker selection and
cancellation, gate simulations, all gallery components, and narrow-window navigation. The gallery
does not yet include every exported design-system component or a force-update simulation.
The notes/settings ViewModels and theme persistence need focused unit tests; remaining copied
DAO/formatting/single-instance tests have not all been mutation-proven.

## Web storage reporting
The page's `databaseStorage()` checks isolation and the browser storage API, while the worker
checks whether SQLite actually registered `OpfsDb`. A browser advertising storage but failing VFS
initialization can therefore disagree with the UI's persistence label. Report the worker's actual
storage mode before treating the label as a production persistence guarantee.

## Documentation structure
The detailed Rehearsal-style documentation reorganization is still pending. Current owning docs
remain `docs/architecture.md` and `docs/features/`; do not assume `docs/frontend/` exists yet.

## Force update on desktop and web does nothing
`storeUrl()` returns null for `DESKTOP` and `WEB`, so the "Update" button on the update-required
screen has no action there. Options: web reloads the page; desktop opens a download page.

## Web refresh token needs backend support
`BrowserTokenStorage` (`core/datastore/src/wasmJsMain/`) expects the backend to set the refresh
token as an HttpOnly cookie and to accept `auth/refresh` with no body token. Rules:
[.github/instructions/web-security.instructions.md](../.github/instructions/web-security.instructions.md).

## Desktop and web have no crash reporting or analytics
Firebase has no desktop SDK, and the web build doesn't load the JS SDK. Options: Sentry (supports
JVM and JS), or the Firebase JS SDK through interop on web.

## Native desktop screenshot
The README uses `RenderScreens` Home content with sample data, not the running desktop window.
macOS screen capture permission was unavailable. Replace `docs/screenshots/desktop.png` with
a window-only capture once permission is granted and update its README caption. Windows and
Linux screenshots have not been captured.

## Desktop installers are not built in CI
`.dmg` builds only on macOS and `.msi` only on Windows. CI compiles desktop but doesn't package.
Notarisation (macOS) and code signing (Windows) are not set up.

## iOS is not built in CI
CI runs on Linux. Add a `macos-latest` job running `:shared:linkDebugFrameworkIosSimulatorArm64`
when iOS breakage starts slipping through.

## Web tests are not run
`wasmJsTest` needs a headless browser. Shared logic is covered by `jvmTest` and Android host tests.

## Deprecation warnings
`TextFields.kt` uses the `BasicTextField(value, onValueChange)` overload that Compose now
deprecates in favour of `TextFieldState`. It still works; migrate when touching those fields.
