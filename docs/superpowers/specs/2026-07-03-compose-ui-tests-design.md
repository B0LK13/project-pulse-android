# Compose UI Tests Design

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:writing-plans` to create the implementation plan after this design is approved.

**Goal:** Add instrumented Compose UI tests that verify the dashboard, project list/detail CRUD flows, task toggle, and navigation behave correctly on a real Android runtime.

**Architecture:** Use Hilt's Android testing support. A custom `HiltTestRunner` bootstraps a generated `HiltTestApplication`. Tests are annotated with `@HiltAndroidTest`, inject the real `ProjectRepository`, seed/clear data per test, and launch `MainActivity` via `createAndroidComposeRule`. Compose test APIs (`onNodeWithText`, `onNodeWithContentDescription`, `performClick`, `assertIsDisplayed`, etc.) drive assertions.

**Tech Stack:**
- `com.google.dagger:hilt-android-testing:2.56.1`
- `androidx.compose.ui:ui-test-junit4`
- `androidx.test.ext:junit`
- JUnit 4 rules (`HiltAndroidRule`, `createAndroidComposeRule`)

## Global Constraints

- Target JVM 17, `compileSdk 35`, `minSdk 26`.
- Follow existing Kotlin conventions (4-space indentation).
- Keep tests deterministic: clear and re-seed the repository before each test.
- Do not change production behavior; only add testability hooks where Hilt requires them.
- Instrumented tests live in `app/src/androidTest/java/com/projectpulse/android/`.

## Design

### Test runner setup

1. Add `hilt-android-testing` and `hilt-compiler` (androidTest/kspAndroidTest) dependencies.
2. Define `@CustomTestApplication(Application::class)` interface `HiltTestApplication` in `app/src/androidTest/java/com/projectpulse/android/HiltTestApplication.kt`.
3. Create `HiltTestRunner : AndroidJUnitRunner` that instantiates the generated Hilt test application.
4. Set `testInstrumentationRunner` to `com.projectpulse.android.HiltTestRunner` in `app/build.gradle.kts`.

### Test utilities

Create `ComposeTestBase` or extension helpers to:
- Clear all projects, tasks, and time logs via the injected repository.
- Seed a known project with tasks.
- Wait for async data to settle before assertions.

### Test cases

1. **DashboardScreenTest**
   - `dashboardDisplaysOverviewStats`: seed one project and one task, verify "Projects", "Tasks", "Hours", "Completion" stats appear.
   - `dashboardNavigatesToProjects`: click the list icon in the top app bar, verify the Projects screen is displayed.

2. **ProjectListScreenTest**
   - `emptyStateShowsMessage`: clear repository, navigate to projects, verify "No projects yet" text.
   - `addProjectAppearsInList`: open add-project dialog, enter name/description, save, verify project appears.
   - `deleteProjectRemovesIt`: seed one project, open overflow menu, delete, verify project removed.

3. **ProjectDetailScreenTest**
   - `detailDisplaysProjectInfo`: seed one project with one task, click project in dashboard, verify project name and task title.
   - `toggleTaskUpdatesCompletion`: open detail, click task checkbox, verify task is shown as completed/strikethrough.
   - `addTaskAppearsInList`: open detail, add a new task, verify it appears.

4. **NavigationTest**
   - `dashboardToProjectsToDetailAndBack`: navigate from dashboard to projects to project detail and back, verifying each screen is shown.

### CI considerations

GitHub Actions does not currently run an emulator. The instrumented tests will be available locally and can be run with `./gradlew connectedDebugAndroidTest` when a device or emulator is attached. A future iteration can add an emulator CI job.

## Risks

- `SeedData.seedIfEmpty()` runs on application start and may insert data before a test clears it. Tests will explicitly clear after `HiltAndroidRule` injects the repository but before any UI assertions.
- Compose test synchronization depends on idling resources; standard `waitForIdle` handles Room Flow emissions.
