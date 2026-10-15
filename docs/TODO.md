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

## Session end is not shown to the user
`SessionEvents` emits when a refresh fails, but nothing collects it. An app with login should
observe it in `AppViewModel` and route back to sign-in.

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
