---
description: Security rules for the web build (Compose Wasm in webApp and every wasmJsMain source set) and the backend endpoints it relies on. Based on the OWASP Cheat Sheet Series.
applyTo: "webApp/**,**/wasmJsMain/**"
---

# Web security rules

The web build runs in a browser, where any script that gets onto the page (an XSS bug, a
compromised dependency, a hostile extension) has the page's full access. Android and iOS have the
Keystore and Keychain; the browser has nothing equivalent. Assume some script on the page is
hostile, and keep anything that would let it act as the user out of its reach.

## 1. Sessions and tokens
1. **No credential in browser storage.** No access token, refresh token, OTP or session id in
   `localStorage`, `sessionStorage`, IndexedDB or a JS-readable cookie. *(OWASP Session Management; HTML5 Security)*
2. **The refresh token travels only in an `HttpOnly` cookie** set by the backend. Web code never
   sees it. `BrowserTokenStorage` (`core/datastore/src/wasmJsMain/`) is built on this.
3. **The access token lives in memory only**; a reload renews it with the cookie.
4. Production cookie: `__Host-` prefix, `Secure`, `HttpOnly`, `SameSite=Strict`, `Path=/`, no
   `Domain`, `Max-Age` equal to the server-side refresh lifetime.
5. Only local HTTP testing may drop `Secure`/`__Host-`, gated on a non-production environment.
6. Expiry, rotation (on every renew and privilege change) and revocation happen on the server.
7. Logout revokes server-side first, then clears the cookie, then in-memory tokens and our
   browser storage.

## 2. Browser storage (non-secrets)
1. `localStorage` holds only data that would be harmless if read or rewritten by a hostile script.
2. Parse everything read back defensively and fall back to defaults.
3. Ask before storing personal data; clear it on logout.
4. Keep one key prefix (`app.` in `BrowserStorage.kt`) so logout can clear exactly our data.

## 3. CSRF (cookie-authenticated endpoints: refresh, logout)
1. No state change on `GET`/`HEAD`/`OPTIONS`.
2. Require a custom header (the client already sends `X-App-Platform`).
3. Check `Sec-Fetch-Site` / `Origin` on the server; never allow `Origin: null`.
4. Accept only `application/json` bodies there.

## 4. CORS
Serve the site and `/api` from the **same origin** (reverse proxy), as
`webApp/webpack.config.d/devServer.js` does in development. If cross-origin is unavoidable, use an
exact origin list; never `*` or a reflected origin.

## 5. Headers on the page serving the Wasm bundle
| Header | Rule |
|---|---|
| `Content-Security-Policy` | Start from `default-src 'self'; script-src 'self' 'wasm-unsafe-eval'; connect-src 'self'; img-src 'self' data: blob:; object-src 'none'; base-uri 'none'; frame-ancestors 'none'; form-action 'self'`. Add third-party origins by exact name only. Never `'unsafe-eval'` or `'unsafe-inline'` for scripts. |
| `Strict-Transport-Security` | `max-age=63072000; includeSubDomains`, production only |
| `X-Content-Type-Options` | `nosniff` |
| `Referrer-Policy` | `strict-origin-when-cross-origin` or stricter |
| `Permissions-Policy` | Deny everything not used |
| `Cross-Origin-Opener-Policy` | `same-origin` |
| `Cache-Control` | `no-store` on auth responses and anything carrying personal data |

## 6. Code running in the page
1. The Wasm/JS bundle is public. Never compile a secret into it.
2. No `eval`, `new Function` or `innerHTML` with anything from user input, the URL, storage or the
   network. Keep `js("…")` snippets small and pass data as arguments.
3. Never build a request URL or method from the page URL, hash or query.
4. New-tab links use `noopener,noreferrer`.
5. `postMessage`: explicit target origin; check `event.origin` exactly on receive.
6. Third-party scripts load from their official origin, are listed in the CSP, and use SRI where
   possible.

## 7. Before calling a web change done
Search the change for `localStorage`, `sessionStorage`, `document.cookie`, `js(`, `eval`,
`innerHTML`, `window.open`, `postMessage`, and check each hit against these rules. A new cookie,
header, CORS origin or third-party domain is a design decision: raise it before building and
record it in `docs/architecture.md`. A rule broken temporarily goes in `docs/TODO.md` with a date.

Sources: OWASP Session Management, HTML5 Security, CSRF Prevention, Content Security Policy and
HTTP Headers cheat sheets (https://cheatsheetseries.owasp.org).
