# AGENTS.md

Guidance for AI agents working in this repository.

## Project Snapshot

- Android scoreboard app for the Gang Of Four card game.
- Single Gradle module: `app`.
- Main package: `rek.gofscoreboard`.
- Language and UI stack: Kotlin, classic Android XML views, Data Binding, View Binding, AppCompat, RecyclerView.
- Build baseline: Gradle wrapper 8.14.3, Android Gradle Plugin 8.12.0, Kotlin 2.2.10, Java 21.
- Supported app modes: 3 or 4 players, portrait orientation, English and French string resources.

Read `.agents/README.md` before making non-trivial changes. It contains the project map, domain rules, test commands, and common pitfalls.

## Work Rules

- Keep changes scoped to the requested behavior. This codebase is intentionally small and direct.
- Preserve the existing Kotlin/XML style unless a task explicitly asks for modernization.
- Prefer updating view models and helpers for logic changes; keep activities focused on Android UI wiring.
- Do not edit `local.properties`, generated build folders, IDE metadata, or Gradle caches.
- When adding user-facing text, update both `app/src/main/res/values/strings.xml` and `app/src/main/res/values-fr/strings.xml`.
- When touching score, save/load, player-count, or game-over behavior, add or update unit tests.
- When touching Android UI flows, IDs, menus, dialogs, or layouts, add or update instrumentation tests where practical.

## Verification

Use the Gradle wrapper from the repository root.

Windows:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

Linux/macOS:

```sh
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

Instrumentation tests require a connected emulator/device:

```powershell
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat jacocoTestDebugReport
```

CI additionally runs unit coverage, instrumented coverage, `build`, and Sonar analysis. Sonar requires `SONAR_TOKEN`, so local agents should not treat missing Sonar credentials as a source failure.

## High-Risk Areas

- Save format in `SavedData` and `ScoreboardViewModel.getContentForSaveFile()` is delimiter-based: commas split fields, `|` splits keys and values, `#` splits scores.
- Player names are constrained in XML with `android:digits`; keep delimiter safety in mind if changing name input rules.
- `ScoreboardActivity` menu handling compares localized titles because item IDs did not work reliably in this project. Be careful when changing menu labels.
- Scoreboard data is a flat string list rendered by `GridLayoutManager`; column count must match player mode.
- Game end happens once any total score reaches 100 or more; ranking sorts by lowest total score first.
