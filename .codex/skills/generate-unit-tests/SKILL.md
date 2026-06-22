---
name: generate-unit-tests
description: Generate or improve local JVM unit tests only for a specific source file, class, function, or module with emphasis on branch coverage suitable for SonarQube/SonarCloud validation. Use when the user asks to create unit tests, add missing tests, increase unit coverage, cover uncovered or missed branches, satisfy Sonar unit coverage gates, or write tests for a named file/class. Do not create, update, or run Android instrumentation/UI tests with this skill. In this workspace, using this skill should spawn or delegate to the tester agent defined in .agents/tester.md when agent spawning is available.
---

# Generate Unit Tests

Use this skill to add high-signal local JVM unit tests for a target file or class while preserving production behavior. Optimize for meaningful branch coverage, not superficial line coverage. Keep all test changes in unit-test sources such as `app/src/test`; do not touch instrumentation sources such as `app/src/androidTest`.

## Workspace Delegation

In this repository, the test-generation work belongs to the workspace tester agent:

```text
.agents/tester.md
```

When this skill is invoked and sub-agent spawning is available, immediately spawn or delegate to a worker sub-agent using `.agents/tester.md` as the role instructions and this skill as the required workflow. Give the spawned agent ownership of the relevant test files for the requested target. The spawned agent should edit tests directly and report changed files, coverage cases, and verification results.

If sub-agent spawning is unavailable in the current environment, follow `.agents/tester.md` yourself before applying the rest of this skill.

## Core Rules

- Do not modify production classes to please tests. Change production code only when the user requested a bug fix or the tests reveal a real defect and the user agrees.
- Do not test private elements directly with reflection or visibility hacks. Cover private branches indirectly through public methods, constructors, callbacks, or observable outputs.
- Prefer existing test style, framework, naming, fixtures, and helper patterns in the repository.
- If tests already exist for the target, extend them to cover missed branches instead of duplicating covered cases.
- Keep tests deterministic and isolated. Avoid network, real clocks, random data, shared filesystem state, and external services unless the existing test suite already provides safe fakes.
- Assert behavior, state, emitted events, thrown errors, interactions, or persisted output that matter to users or callers.
- Do not add or modify Android instrumentation, Espresso, emulator-backed, or UI automation tests. If a requested branch is only practical through instrumentation, report that as out of scope for this skill.

## Workflow

1. Identify the exact target file/class and its public surface.
2. Find existing tests for the target using repository search by class name, method name, package/module path, and feature terms.
3. Read the target implementation and existing tests before editing.
4. Map branches from public behavior:
   - `if`/`else`, `when`/`switch`, ternaries, early returns, null/empty paths
   - exception paths and validation failures
   - loops with zero, one, and many elements when behavior differs
   - boundary values around comparisons and ranges
   - success/failure callbacks, async completion, event emission
   - parser branches, serialization branches, feature flags, mode flags
5. Compare the map to existing tests and list only missing behavior cases.
6. Add focused tests using the smallest public setup that reaches each missing branch.
7. Run the narrow relevant JVM unit test command first, then broader JVM unit test commands if risk or project norms call for it. In this repository, prefer `.\gradlew.bat testDebugUnitTest` or an equivalent targeted unit-test task.
8. If JVM unit coverage tooling is available, run or inspect it and iterate on genuine missed branches. Do not run connected-device or emulator-backed coverage tasks for this skill.

## Existing Coverage and Sonar

When SonarQube/SonarCloud or a local coverage report is available:

- Prefer the report's missed branch details over guessing.
- Locate the source line, then identify the public input or state that exercises each missing decision outcome.
- Add one test per distinct behavior where possible; combine cases only when it stays readable.
- Re-run the JVM unit coverage-producing task if practical.
- If a missed branch is unreachable through the public API, document that clearly rather than using reflection or changing access modifiers.

If no coverage report is available, derive a branch checklist from source code and existing tests. State what could and could not be verified.

## Test Design

- Use Arrange/Act/Assert or the dominant local pattern.
- Name tests after behavior and condition, not implementation trivia.
- Use boundary values for ranges: below minimum, minimum, middle, maximum, above maximum.
- For validation code, cover valid input, missing input, malformed input, and conflicting input when applicable.
- For stateful classes, cover initial state, state transition, repeated operation, and undo/reset behavior if present.
- For parsers/serializers, cover empty values, optional fields, all supported modes, malformed input only if the public contract handles it.
- For UI-adjacent view models, test view model behavior directly through local unit tests. Do not add instrumentation/UI tests for platform wiring as part of this skill.

## Production Code Discipline

Never make these changes only for test convenience:

- changing private methods/properties to public/internal
- adding test-only getters or flags
- weakening validation
- changing timing, threading, or error handling only to simplify assertions
- altering resource text, IDs, serialization format, or public API unless required by the requested behavior

Acceptable production edits are limited to real bug fixes, dependency injection already consistent with project patterns, or small safety improvements explicitly justified by behavior. Keep those separate from pure test additions in the final summary.

## When Blocked

If a private branch cannot be reached through public behavior, do not force it. Report the unreachable branch and why it is not testable without changing the design.

If tests expose a likely production bug, stop treating it as a test-generation task. Explain the failing behavior, the public scenario that exposes it, and whether the user wants the production fix included.

If required unit-test tooling, services, or credentials are unavailable, still add the tests when confident and clearly report the unrun verification. Do not require emulator/device availability for this skill.
