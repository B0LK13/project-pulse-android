#!/usr/bin/env bash
set -euo pipefail

# auto_verify.sh
# Automated verification pipeline for Project Pulse Android.
# Runs unit tests, lint, and debug build verification.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
DEVICE_APK="/storage/emulated/0/apps/project-pulse-debug.apk"


echo "=== Project Pulse Android — Automated Verification ==="
echo "Working directory: $(pwd)"
echo

echo "[1/5] Running unit tests..."
./gradlew testDebugUnitTest --no-daemon

echo "[2/5] Running lint..."
./gradlew lint --no-daemon

echo "[3/5] Building debug APK..."
./gradlew assembleDebug --no-daemon

echo "[4/5] Verifying debug APK exists..."
if [ ! -f "$DEBUG_APK" ]; then
    echo "ERROR: Debug APK not found at $DEBUG_APK"
    exit 1
fi

echo "[5/5] Verifying device APK copy at $DEVICE_APK..."
if [ ! -f "$DEVICE_APK" ]; then
    echo "ERROR: Device APK copy not found at $DEVICE_APK"
    exit 1
fi

echo
echo "=== Verification Complete ==="
echo "Build APK: $DEBUG_APK"
echo "Device APK: $DEVICE_APK"
echo
