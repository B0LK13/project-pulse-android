---
name: android-apk-shizuku-verify
description: Use when you need to test and verify an Android APK on a real device without ADB, especially when Shizuku is available and you want an autonomous install-launch-smoke loop.
---

# Android APK Shizuku Verification

## Overview

A repeatable, autonomous on-device verification workflow using [Shizuku](https://shizuku.rikka.app/). It installs the APK, launches the app, runs a monkey stress test, checks for crashes, and repeats until all checks pass or no actionable fix remains.

## When to Use

- ADB is unavailable but the device has Shizuku running.
- You need to verify the APK on real hardware after a build.
- You want an autonomous loop that builds, installs, smokes, and reports.
- You need crash detection and install/upgrade preservation checks.

## When Not to Use

- ADB is available and faster (`adb install`, `adb shell monkey`).
- The device does not have Shizuku installed or running.
- You need screenshot-based UI assertions (use Espresso or UI Automator with ADB instead).

## Prerequisites

1. Shizuku installed and started on the Android device.
2. `rish` (Shizuku shell) in your PATH.
3. APK built and accessible on the workstation.

## Autonomous Loop

```
Build → Stage APK → Install → Verify package → Launch → Wait for activity
→ Monkey stress test → Check crashes → Reinstall (upgrade test) → Re-check
→ Log evidence → If failures, fix and repeat
```

The loop must continue until:
- All checks pass, or
- A failure cannot be fixed from the workstation (e.g., device offline).

## Verification Script Template

Create `shizuku_verify.sh` in the Android project root:

```bash
#!/usr/bin/env bash
set -euo pipefail

PACKAGE="com.example.android"
DEBUG_PACKAGE="${PACKAGE}.debug"
APK="app/build/outputs/apk/debug/app-debug.apk"
DEVICE_APK="/sdcard/Download/example-debug-shizuku.apk"
RISH_TMP="/data/local/tmp/example-debug-shizuku.apk"
LOGCAT_FILE="/tmp/example_shizuku_verify.log"
MONKEY_EVENTS=200
MAX_RETRIES=7

if ! command -v rish >/dev/null 2>&1; then
    echo "ERROR: rish not found in PATH. Install Shizuku and add rish."
    exit 1
fi

rish -c "logcat -c" >/dev/null 2>&1 || true

echo "[1/6] Staging APK..."
cp "$APK" "$DEVICE_APK"

echo "[2/6] Installing via Shizuku..."
cat "$DEVICE_APK" | rish -c "cat > '$RISH_TMP' && pm install --user 0 -r '$RISH_TMP'" >/dev/null 2>&1

echo "[3/6] Verifying package..."
rish -c "pm path '$DEBUG_PACKAGE'" | grep -q "package:" || { echo "Package not found"; exit 1; }

echo "[4/6] Launching app..."
COMPONENT="${DEBUG_PACKAGE}/.MainActivity"
rish -c "am start -n '$COMPONENT'" >/dev/null 2>&1
sleep 2

attempt=1
while [[ $attempt -le $MAX_RETRIES ]]; do
    if rish -c "dumpsys activity activities | grep -A2 '$COMPONENT'" | grep -q "topResumedActivity.*$COMPONENT"; then
        echo "  Activity resumed."
        break
    fi
    echo "  Waiting... ($attempt/$MAX_RETRIES)"
    sleep 1
    ((attempt++)) || true
done

if [[ $attempt -gt $MAX_RETRIES ]]; then
    echo "ERROR: Activity did not resume."
    exit 1
fi

echo "[5/6] Monkey stress test..."
rish -c "monkey -p '$DEBUG_PACKAGE' -v $MONKEY_EVENTS --throttle 200" >/tmp/example_monkey.log 2>&1 || true

echo "[6/6] Checking for crashes..."
rish -c "logcat -d" > "$LOGCAT_FILE" 2>&1
if grep -iE "fatal exception|\.Fatal|AndroidRuntime.*$DEBUG_PACKAGE" "$LOGCAT_FILE" >/dev/null; then
    echo "ERROR: Crash detected. See $LOGCAT_FILE"
    exit 1
fi

echo "=== Shizuku verification passed ==="
echo "Logcat: $LOGCAT_FILE"
```

## Self-Improvement Rules

- If a step fails, capture the full log and retry once after a clean install.
- If crashes appear, fix the code, rebuild, and restart the loop.
- If the activity never resumes, check the manifest and launcher intent filter.
- Do not stop after a successful install; always run the smoke and monkey checks.
- Do not claim release readiness without a passing Shizuku log.

## Common Failures

| Symptom | Cause | Fix |
|---|---|---|
| `rish: not found` | Shizuku shell not in PATH | Add rish to PATH or install Shizuku. |
| `INSTALL_FAILED_*` | Conflicting signature or package | Uninstall the old package first. |
| Activity never resumes | Wrong component or crash on launch | Check manifest and logcat. |
| Monkey crash | UI race or unhandled exception | Inspect logcat and reproduce manually. |
| `No activities found` | Wrong package or variant | Use the correct debug/release package suffix. |

## Quality Gate Checklist

- [ ] `rish` is available and Shizuku is running.
- [ ] APK installs without errors.
- [ ] Package is present after install.
- [ ] Main activity launches and resumes.
- [ ] Monkey stress test completes without crashes.
- [ ] Logcat is captured and reviewed.
- [ ] Loop repeats until all checks pass.
