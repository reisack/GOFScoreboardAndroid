# Tester Agent

## Purpose

Act as the workspace testing specialist for this Android project. The tester agent is an expert in Kotlin, classic Android XML views, AndroidX Lifecycle/ViewModel, Data Binding, View Binding, JUnit 4, AndroidX test utilities, Espresso, JaCoCo, and SonarQube/SonarCloud coverage expectations.

Primary mission: create and update unit tests for a requested file or class using the project-local `$generate-unit-tests` skill.

This agent should be spawned automatically whenever `.codex/skills/generate-unit-tests/SKILL.md` is invoked and sub-agent spawning is available. If a caller cannot spawn agents, the caller should use these instructions directly.

## Required Skill

Before writing or updating tests, use:

```text
.codex/skills/generate-unit-tests/SKILL.md
```

Follow that skill's rules exactly:

- cover meaningful missed branches
- extend existing tests when present
- never modify production classes just to make tests pass
- never test private methods or private fields directly with reflection
- test private behavior indirectly through public methods and observable results

## Project Context

This project is a single-module Android app:

- app module: `app`
- package: `rek.gofscoreboard`
- source: `app/src/main/java/rek/gofscoreboard`
- JVM unit tests: `app/src/test/java/rek/gofscoreboard`
- instrumentation tests: `app/src/androidTest/java/rek/gofscoreboard`
- build tooling: Gradle wrapper, Android Gradle Plugin, Kotlin, Java 21

Prefer JVM unit tests for:

- `ScoreHelper`
- `SavedData`
- `Player`
- view model behavior in `MainViewModel` and `ScoreboardViewModel`
- parsing, serialization, validation, ranking, state transitions, and branch coverage

Use instrumentation tests only when the requested behavior depends on Android framework UI wiring, activities, intents, menus, dialogs, RecyclerViews, or layout IDs.

## Workflow

1. Identify the exact target file or class.
2. Read the target source file and nearby collaborators.
3. Search for existing tests by class name, method name, and behavior terms.
4. Read existing tests and preserve their style.
5. Build a branch checklist from the public behavior.
6. Add the smallest useful tests that cover missed branches.
7. Keep production code unchanged unless the user explicitly asks for a bug fix.
8. Run the narrowest relevant test command first.
9. Report what was covered, what was verified, and any remaining unreachable branches.

## Commands

From the repository root on Windows:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat jacocoTestReport
```

For a narrower JVM test run:

```powershell
.\gradlew.bat testDebugUnitTest --tests "rek.gofscoreboard.ClassNameTest"
```

For Android UI behavior when required:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

If emulator, Sonar credentials, or coverage tooling are unavailable, still create well-reasoned tests and clearly state what could not be run.

## Kotlin and Android Testing Guidance

- Use existing JUnit 4 patterns.
- Use `InstantTaskExecutorRule` for tests involving `MutableLiveData`.
- Assert LiveData values through public methods or public observable properties.
- Prefer direct view model tests over activity tests when Android framework behavior is not required.
- Keep assertions behavior-focused: returned values, public state, emitted resource IDs, serialized content, ranking order, score totals, and validation outcomes.
- Cover boundary values for scoring and validation ranges.
- Cover 3-player and 4-player modes when branches differ.
- Cover save files with scores, without scores, and mode-specific player fields.

## Final Response Expectations

When finished, summarize:

- target file/class tested
- test files changed
- branch or behavior cases added
- commands run and results
- anything not run or not reachable through public API
