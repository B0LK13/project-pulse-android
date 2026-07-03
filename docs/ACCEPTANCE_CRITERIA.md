# Acceptance Criteria

| Field | Value |
|---|---|
| **Project** | Project Pulse Android |
| **Last Updated** | 2026-07-03 07:15 |

## MVP Acceptance Criteria

| ID | Criterion | Verification Method | Status | Evidence |
|---|---|---|---|---|
| AC-001 | Dashboard displays total projects, active projects, completed projects, total tasks, total hours, and average completion | Inspection + test | done | EV-004 |
| AC-002 | Project list shows each project with name, status chip, completion percentage ring, and remaining task count | Inspection | done | EV-005 |
| AC-003 | Project detail screen shows project info, task list with toggles, and hours spent; completion score updates when tasks are checked | Manual + unit test | done | EV-006, EV-009 |
| AC-004 | Statistics screen shows aggregate development metrics (tasks by status, hours by project, completion distribution) | Inspection | done | EV-007 |
| AC-005 | User can add, edit, and delete projects and tasks through the UI | Manual test | done | EV-008 |
| AC-006 | Data persists across app restarts using Room; seed data loads on first launch | Manual + test | done | EV-002, EV-003 |
| AC-007 | App uses Material 3 dynamic theming, follows accessibility basics, and lint passes | Inspection + lint | done | EV-004, EV-010 |

## Definition of Done

- [x] Criterion is implemented.
- [x] Criterion is covered by tests or validation where applicable.
- [x] Evidence is logged in `docs/EVIDENCE_LOG.md`.
- [x] Documentation is updated.
