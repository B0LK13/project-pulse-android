# MVP Definition

| Field | Value |
|---|---|
| **Project** | Project Pulse Android |
| **Version** | 1.0.0 |
| **Last Updated** | 2026-07-03 07:15 |

## 1. Purpose

A sleek, offline-first Android dashboard that gives developers an at-a-glance overview of their projects, health status, remaining tasks, completion scores, and logged hours.

## 2. Target Users

- Solo developers and indie makers.
- Small-team leads needing a lightweight project health view.

## 3. In Scope

- [x] Local project and task storage with Room.
- [x] Dashboard with summary cards and quick stats.
- [x] Project list with status chips, progress rings, and remaining tasks.
- [x] Project detail screen with task list and hours.
- [x] Statistics screen with aggregate metrics.
- [x] CRUD operations for projects and tasks.
- [x] Material 3 dynamic theming.
- [x] Debug APK build passing all quality gates.

## 4. Out of Scope

- [ ] Cloud sync, accounts, or backends.
- [ ] Sub-tasks, attachments, or rich notes.
- [ ] Timer-based time tracking.
- [ ] Widgets or notifications.
- [ ] Release signing or Play Store publishing.

## 5. Success Criteria

| ID | Criterion | Measurement | Status | Evidence |
|---|---|---|---|---|
| SC-001 | App builds and installs cleanly | `./gradlew assembleDebug` | done | EV-001 |
| SC-002 | All screens render without crashes | Manual + Compose test | done | EV-002 |
| SC-003 | Project stats calculate correctly | Unit tests | done | EV-003 |
| SC-004 | UI follows Material 3 guidelines | Visual inspection | done | EV-004 |
| SC-005 | Lint passes with zero errors | `./gradlew lint` | done | EV-005 |

## 6. Release Blockers

| ID | Blocker | Impact | Mitigation | Status |
|---|---|---|---|---|

## 7. Revision History

| Date | Author | Change |
|---|---|---|
| 2026-07-03 | agent | Initial MVP definition. |
