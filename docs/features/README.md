# Feature docs

One file per feature module: `docs/features/<feature>.md`. Write it before the code, keep it true
after. It describes what the feature does now, not how it got there.

## Template

```markdown
# <Feature name>

Module: `feature:<name>` · Entry: `<Name>Screen` (`feature/<name>/.../presentation/<Name>Screen.kt`)

## What it is for
One or two sentences, in the user's words.

## Screens and states
| Screen | Loading | Empty | Error | Content |
|---|---|---|---|---|

## Rules
Plain sentences a test can check, each pointing at the code and the test:
- <rule> — `<File.kt>`, tested by `<TestClass.testName>`

## API
| Call | Request | Response `data` | Errors handled |
|---|---|---|---|

## Platform differences
Anything that behaves differently on Android / iOS / desktop / web, and why.

## Not done yet
Link to the docs/TODO.md entries for this feature.
```

## Features

| Feature | Doc |
|---|---|
| Home (sample) | [home.md](home.md) |
