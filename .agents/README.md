# AI Agent Project Notes

This directory is for agent-facing documentation. It is not user help text and it should stay practical: what to inspect, what to change, and how to verify it.

## Repository Map

```text
.
|-- build.gradle                         Root Gradle buildscript and plugin versions
|-- settings.gradle                      Includes the single :app module
|-- gradle/wrapper/                      Pinned Gradle wrapper
|-- app/build.gradle                     Android app config, dependencies, JaCoCo, Sonar
|-- app/src/main/AndroidManifest.xml     Two-activity Android manifest
|-- app/src/main/java/rek/gofscoreboard/ App Kotlin source
|-- app/src/main/res/layout/             Phone XML layouts
|-- app/src/main/res/layout-sw600dp/     Tablet scoreboard layout
|-- app/src/main/res/values*/            Styles, colors, strings, API-specific resources
|-- app/src/test/                        JVM unit tests
|-- app/src/androidTest/                 Espresso instrumentation tests
`-- .github/workflows/build.yml          CI: tests, coverage, Sonar
```

## Runtime Flow

`MainActivity` is the launch screen. It lets the user choose 3 or 4 players, validates non-empty unique player names through `MainViewModel`, and starts `ScoreboardActivity` with intent extras. It also opens the privacy URL and loads an existing internal save file when requested.

`ScoreboardActivity` owns the active game UI. It initializes `ScoreboardViewModel`, builds the scoreboard RecyclerViews, handles adding scores, undoing the last round, saving the current game, loading a saved game, game-over dialogs, and back/new-game confirmations.

The score table has one direction column plus one column per player. `ScoreboardViewModel.getScoreboardColumnsCount()` must stay aligned with the flat `scoreboard` list and the header list.

## Core Domain Rules

- A round records the number of cards left for each player.
- Valid card counts are `0..16`.
- Exactly one player must have `0` cards left in each round.
- Score conversion is in `ScoreHelper.calculateScore()`:
  - `0` cards: `0`
  - `1..7`: face value
  - `8..10`: cards times 2
  - `11..13`: cards times 3
  - `14..15`: cards times 4
  - `16`: `80`
- The game finishes when at least one player total is `>= 100`.
- Final ranking is sorted by lowest total score first.
- Undo removes the last direction marker and the most recent score for every player.

## Important Files

- `MainViewModel.kt`: player-count state, player-name validation, delayed clearing after game start/load.
- `ScoreboardViewModel.kt`: game initialization, score validation, scoreboard state, undo, finish detection, save serialization.
- `ScoreHelper.kt`: score calculation and validation rules. This is the first place to look for rule changes.
- `SavedData.kt`: parser for the delimiter-based save file.
- `Player.kt`: player index/name plus `Stack<Int>` score history and `MutableLiveData<String>` input state.
- `EdgeToEdgeInsets.kt`: system bar padding/status bar behavior.
- `activity_main.xml`: Data Binding launch form.
- `activity_scoreboard.xml` and `layout-sw600dp/activity_scoreboard.xml`: score entry and scoreboard table layouts.
- `values/styles.xml`, `values-sw600dp/styles.xml`, `values-v35/styles.xml`, `values-sw600dp-v35/styles.xml`: duplicated style variants. Keep related changes synchronized.

## Save Format

Saves are stored internally as `savefile.txt` (`SavedData.FILENAME`) with comma-separated key/value pairs:

```text
isFourPlayersMode|true,name1|Stan,score1|4#0#6,name2|Kyle,score2|6#2#5,name3|Kenny,score3|16#3#0,name4|Clyde,score4|0#7#1
```

The parser assumes every field contains a `|`. Scores are optional; an empty score value means no score history for that player. Player names are currently constrained in XML to alphanumeric characters, which prevents delimiter collisions. If changing input filtering or save syntax, update `SavedDataUnitTests` and save/load instrumentation coverage.

## Testing Strategy

Fast local checks:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

Coverage for JVM tests:

```powershell
.\gradlew.bat jacocoTestReport
```

Emulator/device checks:

```powershell
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat jacocoTestDebugReport
```

Use the equivalent `./gradlew` commands on Linux/macOS.

Test locations:

- `ScoreHelperUnitTests.kt`: scoring, valid card ranges, one-winner rule, final ranking.
- `SavedDataUnitTests.kt`: save parser for 3/4-player and with/without-score cases.
- `MainViewModelTests.kt`: launch form state and validation.
- `MainInstrumentedTests.kt`: launch UI, player-name restrictions, activity navigation, privacy intent.
- `Scoreboard*InstrumentedTests.kt`: scoring flows for 3 and 4 players.
- `MultiActivitiesInstrumentedTests.kt`: cross-activity scenarios and saved-game behavior.
- `ScoreboardInstrumentedTestsHelper.kt`: shared Espresso actions.

Prefer unit tests for pure rule changes and instrumentation tests for UI behavior, intents, dialogs, menus, or layout IDs.

## CI Notes

`.github/workflows/build.yml` runs on pushes to `master` and pull requests. It uses JDK 21, Gradle caches, `testDebugUnitTest`, `jacocoTestReport`, an Android emulator for `jacocoTestDebugReport`, coverage file checks, then `./gradlew build sonar --info`.

Local agents may not have `SONAR_TOKEN` or emulator access. Report that clearly instead of claiming full CI parity.

## Implementation Conventions

- Kotlin files use direct, simple classes rather than layered architecture.
- Activities use `DataBindingUtil.setContentView()` and also call `setContentView(binding.root)`.
- View state commonly uses `MutableLiveData`.
- Layout event handlers are declared with XML `android:onClick` or Data Binding expressions.
- RecyclerView adapters are recreated on refresh; do not introduce broad state-management changes unless asked.
- The app is portrait-only in `AndroidManifest.xml`.
- Resource names and test IDs are relied on by Espresso tests. Rename IDs only with test updates.
- Keep new comments short and useful. Existing comments are descriptive but not a reason to add boilerplate.

## Localization and Resources

When adding or renaming strings:

- Update `app/src/main/res/values/strings.xml`.
- Update `app/src/main/res/values-fr/strings.xml`.
- Update tests that click menu/dialog text by string resource.

When changing styles:

- Check the base phone styles and `sw600dp` styles.
- Check API 35 style variants (`values-v35`, `values-sw600dp-v35`) when theme items are involved.
- Tablet-specific layout exists for the scoreboard screen only.

## Common Pitfalls

- `ScoreHelper.areScoresValid()` calls `toInt()` on nullable string values. Existing validation calls `canAddScoresRound()` first; preserve that order or make parsing safer.
- `MainViewModel.delayedClearPlayersNames()` uses `Looper.myLooper()!!`, so it expects to run on a thread with a Looper.
- `ScoreboardActivity.launchNewGame()` reuses the current intent after removing `LOAD_SAVED_GAME`.
- The French strings file may display incorrectly in some terminals depending on encoding. Avoid unrelated encoding churn.
- `local.properties` is machine-specific and should not be committed or modified by agents.
