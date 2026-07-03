# Project Pulse Android

A modern, offline-first Android dashboard for tracking your projects, their status, remaining tasks, completion scores, and logged hours.

## Features

- **Dashboard overview**: total projects, active/completed counts, tasks, remaining tasks, total hours, and average completion.
- **Project list**: see every project with status chip, completion ring, remaining tasks, and logged hours.
- **Project detail**: view project info, toggle tasks, add/delete tasks, and log hours.
- **Statistics screen**: aggregate development metrics including hours per project and completion distribution.
- **CRUD operations**: add, edit, and delete projects and tasks.
- **Modern UI**: Jetpack Compose + Material 3 with dynamic theming on Android 12+.
- **Local persistence**: Room database with seed data on first launch.

## Tech Stack

- Kotlin
- Jetpack Compose + Material 3
- MVVM + Repository pattern
- Hilt dependency injection
- Room persistence
- Navigation Compose
- Coroutines + Flow

## Build

```bash
./gradlew assembleDebug
```

## Test

```bash
./gradlew testDebugUnitTest
```

## Lint

```bash
./gradlew lint
```

## Automated Verification

Run the full verification pipeline:

```bash
./auto_verify.sh
```

The debug APK is automatically copied to `/storage/emulated/0/apps/project-pulse-debug.apk` after every successful `assembleDebug`.

## On-Device Verification with Shizuku

If Shizuku is running on the device, run the autonomous install-launch-smoke loop:

```bash
./shizuku_verify.sh debug
```

This builds the APK, installs it via `rish`, launches the app, runs a monkey stress test, verifies upgrade/reinstall, and checks logcat for crashes.

## Skills

- `.skills/android-apk-build-verify/SKILL.md` — build and verify Android APKs.
- `.skills/android-apk-shizuku-verify/SKILL.md` — autonomous on-device APK testing with Shizuku.

## Project Structure

```
app/src/main/java/com/projectpulse/android/
├── MainActivity.kt
├── ProjectPulseApp.kt
├── data/
│   ├── local/
│   │   ├── ProjectPulseDatabase.kt
│   │   ├── SeedData.kt
│   │   ├── dao/
│   │   └── entity/
│   └── repository/
├── di/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── ui/
    ├── dashboard/
    ├── detail/
    ├── list/
    ├── navigation/
    ├── stats/
    └── theme/
```

## Continuous Integration

A GitHub Actions workflow (`.github/workflows/ci.yml`) runs on every push and pull request to `main`:

- Unit tests (`./gradlew testDebugUnitTest`)
- Lint (`./gradlew lint`)
- Debug APK build (`./gradlew assembleDebug`)
- APK artifact upload (retained for 7 days)

The workflow strips the local `aapt2FromMavenOverride` line from `gradle.properties` so the project builds on standard GitHub-hosted runners.

## Testing

### Unit tests

```bash
./gradlew testDebugUnitTest
```

Covers stats use cases with `FakeProjectRepository`.

### Instrumented UI tests

```bash
./gradlew connectedDebugAndroidTest
```

Requires a connected device or emulator. Tests run through Hilt and the real app:

- `DashboardScreenTest` — overview stats and navigation to projects
- `ProjectListScreenTest` — add project and delete project flows
- `ProjectDetailScreenTest` — project info, add task, and task toggle
- `NavigationTest` — dashboard → projects → detail → back flow
- `ProjectPulseDatabaseMigrationTest` — Room 1→2 migration

The test runner is `com.projectpulse.android.HiltTestRunner`.

## Quality Gates

- `./gradlew assembleDebug` — passes
- `./gradlew testDebugUnitTest` — passes (`ProjectStatsCalculatorTest`, `GetDashboardStatsUseCaseTest`)
- `./gradlew lint` — passes
- `./gradlew connectedDebugAndroidTest` — instrumented tests (requires connected device)
- `./auto_verify.sh` — all checks pass
- `./shizuku_verify.sh debug` — install, launch, monkey stress, upgrade, and crash checks pass

## Room Migrations

The database uses explicit Room migrations. Schema JSON files are exported to `app/schemas/` and committed. When changing the schema:

1. Bump `version` in `ProjectPulseDatabase`.
2. Add a `Migration` object in `Migrations.kt`.
3. Register it in `ProjectPulseDatabase` via `addMigrations(*ALL_MIGRATIONS)`.
4. Run `./gradlew kspDebugKotlin` to generate the new schema JSON.
5. Add or update the migration test in `app/src/androidTest/java/com/projectpulse/android/data/local/ProjectPulseDatabaseMigrationTest.kt`.

Destructive migration fallback is disabled, so unmapped schema changes will fail fast instead of erasing user data.

## Notes

- The app is fully offline; no network permissions are requested.
- Seed data is inserted on first launch so the dashboard is immediately useful.
- Out-of-scope for this MVP: cloud sync, widgets, sub-tasks, timer-based tracking.
