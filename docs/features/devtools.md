# Developer Tools

## Entry and scope

The debug-only Test destination in `AppShell` opens `DevToolsScreen`, which groups Database,
Design system and Core checks. `AppNavHost` does not register these routes in release mode.
The host/build switches are described in [architecture](../architecture.md#navigation-and-appearance).
This feature is a template diagnostic surface, not an authentication or access-control mechanism.

## Database

`DatabaseScreen` collects `DatabaseViewModel.uiState`; events edit a draft, save a note,
delete one note or delete all notes. Blank drafts cannot be saved. Saving shows progress;
failures retain the draft and show a translated domain error. The list has loading and empty states.
`RoomNotesRepository` maps `NoteDao` rows into domain `Note` objects and uses `WallClock`
for creation timestamps. Notes are listed newest first by timestamp and ID.

The table is a generic example, not app-specific business data. Device databases are persistent;
web persistence depends on OPFS and its headers. The memory-only warning is driven by `NoteStorage`.
See [Web database](../architecture.md#web-database) and the reporting gap in [TODO](../TODO.md).
The sample Delete all action has no undo or confirmation; do not copy it into a real user-data
workflow without deciding the appropriate safeguards.

## Core checks

`ChecksViewModel` dispatches `CheckId` actions to `CoreChecksRepositoryImpl` and exposes
Not run, Running, Passed or Failed for each check:

- Settings: write/read a timestamp under `devtools.settingsCheck`. The previous run's value is
  loaded on screen creation so it can be inspected after a restart.
- Token storage: save/read a synthetic access token, then restore the prior stored tokens in
  `finally`. Web refresh tokens remain outside JavaScript in HttpOnly cookies.
- Database: insert/read/delete a temporary note through Room.
- Backend: GET `health` through the unauthenticated core client and API-envelope parsing.

The screen also shows platform/build/storage information, three locale formatting samples,
file and image pickers, a maintenance-screen simulation and a session-expiry notification.
Simulations emit local events; they do not call the server. Backend failure is expected if no
backend is running. File cancellation does not count as an error.

## Design system and settings

`ComponentsScreen` previews button states, choices, text fields, banners, badges, rows, tiles,
dates, times, sliders, a responsive form sheet, confetti and icons. It is not yet an exhaustive
catalogue; remaining coverage is recorded in TODO. The original button hierarchy is retained,
not the solid-secondary option D explored in the HTML comparison.

`SettingsScreen` owns palette selection through `ThemeSettings`. `StoredThemeSettings` saves
the palette name in `KeyValueStore`; `AppTheme` applies it app-wide. Montserrat is assigned to
every Material text role by the shared typography builder. Themes are device-local, not synced.

## Checks and known limits

`NoteDaoTest` covers ordering, deletion and reopening a file-backed database.
`SidePanelTest` covers typing, Enter-to-save and dismissal. `ThemeContrastTest` checks palette
contrast. The full browser/manual check list and missing ViewModel tests remain in TODO.
Do not infer production readiness from a diagnostic check passing once.