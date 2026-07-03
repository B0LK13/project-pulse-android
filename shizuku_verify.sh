#!/usr/bin/env bash
set -euo pipefail

# shizuku_verify.sh
# On-device autonomous verification loop for Project Pulse Android using Shizuku (rish).
# Builds the debug APK, installs it via Shizuku, launches the app, runs monkey,
# and checks for crashes. Repeats until all checks pass.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

PACKAGE="com.projectpulse.android"
DEBUG_PACKAGE="${PACKAGE}.debug"
APK="app/build/outputs/apk/debug/app-debug.apk"
APK_BASENAME="project-pulse-debug-shizuku.apk"
DEVICE_APK="/sdcard/Download/${APK_BASENAME}"
RISH_TMP="/data/local/tmp/${APK_BASENAME}"
LOGCAT_FILE="/tmp/project_pulse_shizuku_verify.log"
MONKEY_EVENTS=200
MAX_RETRIES=7

usage() {
    echo "Usage: $0 [debug]"
    echo
    echo "Autonomous on-device verification loop via Shizuku (rish)."
    echo "Defaults to the debug APK."
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
    usage
    exit 0
fi

FLAVOR="${1:-debug}"
case "$FLAVOR" in
    debug)
        TARGET_PACKAGE="$DEBUG_PACKAGE"
        COMPONENT="${DEBUG_PACKAGE}/com.projectpulse.android.MainActivity"
        ;;
    *)
        echo "ERROR: Unknown flavor '$FLAVOR'. Use 'debug'."
        usage
        exit 1
        ;;
esac

if ! command -v rish >/dev/null 2>&1; then
    echo "ERROR: rish (Shizuku shell) not found in PATH."
    echo "Install Shizuku and make sure rish is available."
    exit 1
fi

echo "=== Project Pulse Android — Shizuku Verification Loop ==="
echo "Flavor: $FLAVOR"
echo "Package: $TARGET_PACKAGE"
echo

# Ensure a clean build first.
echo "[1/7] Building debug APK..."
./gradlew assembleDebug --no-daemon

if [ ! -f "$APK" ]; then
    echo "ERROR: APK not found at $APK"
    exit 1
fi

echo "[2/7] Staging APK on device..."
cp "$APK" "$DEVICE_APK"

install_apk() {
    local reinstall="${1:-}"
    local flag=""
    [[ "$reinstall" == "reinstall" ]] && flag="-r"

    if [ ! -f "$DEVICE_APK" ]; then
        echo "ERROR: Staged APK not found at $DEVICE_APK"
        return 1
    fi

    echo "  Installing ${APK_BASENAME}..."
    local install_output
    install_output=$(cat "$DEVICE_APK" | rish -c "cat > '$RISH_TMP' && pm install --user 0 $flag '$RISH_TMP'" 2>&1) || true

    if ! echo "$install_output" | grep -q "Success"; then
        echo "ERROR: Failed to install APK via Shizuku."
        echo "Install output: $install_output"
        return 1
    fi
    echo "  Install successful."
}

launch_app() {
    echo "  Launching $COMPONENT..."
    rish -c "am start -n '$COMPONENT'" >/dev/null 2>&1
}

wait_for_activity() {
    local attempt=1
    while [[ $attempt -le $MAX_RETRIES ]]; do
        local state
        state=$(rish -c "dumpsys activity activities | grep -A2 '$COMPONENT' | head -5" 2>/dev/null || true)
        if echo "$state" | grep -q "topResumedActivity.*$COMPONENT"; then
            echo "  Activity is top resumed."
            return 0
        fi
        echo "  Waiting for activity to resume... ($attempt/$MAX_RETRIES)"
        sleep 1
        ((attempt++)) || true
    done
    return 1
}

check_crashes() {
    if rish -c "logcat -d" 2>/dev/null | grep -iE "fatal exception|\.Fatal|AndroidRuntime.*$TARGET_PACKAGE" >/dev/null; then
        echo "ERROR: Crash detected in logcat."
        return 1
    fi
    return 0
}

run_monkey() {
    echo "  Running monkey stress test ($MONKEY_EVENTS events)..."
    rish -c "monkey -p '$TARGET_PACKAGE' -v $MONKEY_EVENTS --throttle 200" >"/tmp/project_pulse_shizuku_monkey.log" 2>&1 || true
    echo "  Monkey log saved to /tmp/project_pulse_shizuku_monkey.log"
}

# Clear logcat for a clean run.
rish -c "logcat -c" >/dev/null 2>&1 || true

echo "[3/7] Fresh install..."
install_apk

echo "[4/7] Verifying package..."
PM_PATH_OUTPUT=$(rish -c "pm path '$TARGET_PACKAGE'" 2>&1)
echo "  pm path output: $PM_PATH_OUTPUT"
if ! echo "$PM_PATH_OUTPUT" | grep -q "package:"; then
    echo "ERROR: Package $TARGET_PACKAGE not found after install."
    exit 1
fi

echo "[5/7] Launch and smoke test..."
launch_app
sleep 2
if ! wait_for_activity; then
    echo "ERROR: Activity did not become top resumed after fresh install."
    exit 1
fi

if ! check_crashes; then
    exit 1
fi

echo "[6/7] Stress test with monkey..."
rish -c "logcat -c" >/dev/null 2>&1 || true
run_monkey
sleep 2
if ! check_crashes; then
    echo "ERROR: Crash detected during monkey stress test."
    exit 1
fi

echo "[7/7] Upgrade / reinstall preservation test..."
rish -c "am force-stop '$TARGET_PACKAGE'" >/dev/null 2>&1 || true
rish -c "logcat -c" >/dev/null 2>&1 || true
install_apk reinstall
sleep 1
launch_app
sleep 2
if ! wait_for_activity; then
    echo "ERROR: Activity did not become top resumed after upgrade/reinstall."
    exit 1
fi

if ! check_crashes; then
    echo "ERROR: Crash detected after upgrade/reinstall."
    exit 1
fi

echo "[8/7] Capturing final logcat..."
rish -c "logcat -d" > "$LOGCAT_FILE" 2>&1

echo
if check_crashes; then
    echo "=== Shizuku Verification Loop Complete ==="
    echo "All checks passed for $FLAVOR build."
    echo "Logcat saved to: $LOGCAT_FILE"
    echo
else
    echo "=== Shizuku Verification Loop Failed ==="
    echo "Check logcat: $LOGCAT_FILE"
    exit 1
fi
