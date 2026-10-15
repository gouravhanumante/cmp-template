# Home (sample feature)

Module: `feature:home` · Entry: `HomeScreen` (`feature/home/.../presentation/HomeScreen.kt`)

## What it is for
The first screen of a fresh template: says what the template already contains and whether the
backend is reachable. It is also the worked example of a feature module. Replace it with your
app's first real feature.

## Screens and states
| Screen | Loading | Error | Content |
|---|---|---|---|
| Home | "Checking…" with a small spinner in the Backend card; the welcome text stays visible | "Offline" pill, the `DomainError` message, "Check again" | "Online" pill, "Version x", "Check again" |

The welcome text and the "What's already set up" list are static (`strings_home.xml`).

## Rules
- An unreachable backend ends in an error the user can retry, never an endless spinner —
  `HomeViewModel.load()`, tested by `HomeViewModelTest.unreachableBackendShowsRetryableErrorInsteadOfLoading`.
- Retrying after a failure replaces the old error with the result —
  `HomeViewModel.load()`, tested by `HomeViewModelTest.retryAfterFailureClearsTheErrorAndShowsTheVersion`.
- A backend error arrives as a `DomainError`, never as an exception —
  `HealthApi` via `apiRequest`, tested by `HealthRepositoryImplTest.errorEnvelopeBecomesADomainErrorNotAnException`
  (regression guard for `core:network`'s `apiRequest`).

## API
| Call | Client | Response `data` | Errors handled |
|---|---|---|---|
| `GET health` | `UnauthenticatedClient` | `{ "status": "UP", "version": "1.0.0" }` (`HealthDto`) | All, via `DomainError.toUserMessage()` |

## Platform differences
None. On web the request goes to the page's own origin (`/api/v1/health`), proxied by the dev server.

## Not done yet
Nothing; this feature is meant to be deleted.
