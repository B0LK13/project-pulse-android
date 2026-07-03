# Risk Register

| Field | Value |
|---|---|
| **Project** | Project Pulse Android |
| **Last Updated** | 2026-07-03 07:15 |

## Risks

| ID | Risk | Likelihood | Impact | Owner | Status | Mitigation | Evidence |
|---|---|---|---|---|---|---|---|
| R-001 | Gradle/Android SDK incompatibility on this workstation | medium | high | agent | mitigated | Use identical versions to daily-trendscape (AGP 8.6.0, Gradle 8.7, compileSdk 35) and verify `./gradlew assembleDebug`. | EV-001 |
| R-002 | Room schema mismatch during iteration | low | medium | agent | mitigated | Version database, use KSP arg for schema location, provide destructive fallback in migrations. | EV-002 |
| R-003 | UI looks cluttered on small screens | medium | medium | agent | mitigated | Use adaptive cards, scrollable screens, and Material 3 spacing guidelines. | EV-004 |
| R-004 | Feature creep beyond MVP | medium | medium | agent | mitigated | Strictly enforce out-of-scope list in MVP_DEFINITION.md. | PRP |

## Watch List

| ID | Risk | Trigger for Action |
|---|---|---|
| W-001 | Need for cloud sync | User explicitly requests multi-device access. |
| W-002 | Need for richer task hierarchy | User requests sub-tasks or recurring tasks. |
