---
name: android-apk-build-verify
description: Use when building or verifying an Android APK in a Gradle project, including debug builds, release builds, lint, unit tests, and APK integrity checks.
---

# Android APK Build and Verify

## Overview

A repeatable workflow for producing and validating Android APKs. Covers debug builds, release builds, tests, lint, and APK existence/signature verification.

## When to Use

- Before claiming an Android feature is complete.
- Before declaring release readiness.
- After changing Gradle dependencies, build types, or signing config.
- When CI for an Android project needs a verification script.
- When you need to confirm an APK was actually produced and is valid.

## Build Commands

Run from the Android project root (the directory containing `gradlew`):

```bash
# Debug build
./gradlew assembleDebug --no-daemon

# Release build (requires signing config)
./gradlew assembleRelease --no-daemon

# Full clean build
./gradlew clean assembleDebug --no-daemon
```

## Test and Lint Commands

```bash
# Unit tests
./gradlew testDebugUnitTest --no-daemon

# Lint
./gradlew lint --no-daemon
```

## Verification Steps

1. **Confirm the APK exists.**
   ```bash
   ls -lh app/build/outputs/apk/debug/app-debug.apk
   ls -lh app/build/outputs/apk/release/app-release.apk
   ```

2. **Verify release APK signature (release only).**
   ```bash
   APKSIGNER="${ANDROID_SDK_ROOT:-$ANDROID_HOME}/build-tools/35.0.0/apksigner"
   "$APKSIGNER" verify --verbose app/build/outputs/apk/release/app-release.apk
   ```

3. **Check lint and test reports.**
   - Lint: `app/build/reports/lint-results-debug.html`
   - Tests: `app/build/reports/tests/testDebugUnitTest/index.html`

## Example Verification Script

Create `auto_verify.sh` in the project root:

```bash
#!/usr/bin/env bash
set -euo pipefail

./gradlew testDebugUnitTest --no-daemon
./gradlew lint --no-daemon
./gradlew assembleDebug --no-daemon

if [ ! -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "ERROR: Debug APK not found"
    exit 1
fi

echo "Verification complete. APK ready."
```

## Copying the APK to Shared Device Storage

If the APK must also be written to a device path such as `/storage/emulated/0/apps`, add to `app/build.gradle.kts`:

```kotlin
tasks.register<Copy>("copyDebugApkToDeviceStorage") {
    description = "Copies the debug APK to /storage/emulated/0/apps"
    group = "verification"
    from(layout.buildDirectory.file("outputs/apk/debug/app-debug.apk"))
    into("/storage/emulated/0/apps")
    rename { "project-pulse-debug.apk" }
}

afterEvaluate {
    tasks.named("assembleDebug").configure {
        finalizedBy("copyDebugApkToDeviceStorage")
    }
}
```

Then verify the copied file in `auto_verify.sh`:

```bash
DEVICE_APK="/storage/emulated/0/apps/project-pulse-debug.apk"
if [ ! -f "$DEVICE_APK" ]; then
    echo "ERROR: Device APK copy not found at $DEVICE_APK"
    exit 1
fi
```

## Common Failures

| Symptom | Cause | Fix |
|---|---|---|
| `Unable to find valid certification` | JDK trust store issue | Check `JAVA_HOME` and use a compatible JDK (17 for AGP 8.x). |
| `Could not find com.android.tools.build:gradle` | Offline or missing repository | Confirm `google()` and `mavenCentral()` in `settings.gradle.kts`. |
| `minSdk 26 cannot be smaller than version 21` | Library conflict | Exclude or upgrade conflicting dependency. |
| APK missing after successful build | Wrong build variant path | Check `app/build/outputs/apk/<variant>/`. |
| `apksigner verify` fails | Unsigned or wrongly signed release APK | Verify signing config in `build.gradle.kts`. |

## Quality Gate Checklist

- [ ] `./gradlew assembleDebug` succeeds.
- [ ] Unit tests pass (`testDebugUnitTest`).
- [ ] Lint passes (`lint`).
- [ ] Debug APK exists at expected path.
- [ ] Release APK is signed and verifies (if release build is in scope).
