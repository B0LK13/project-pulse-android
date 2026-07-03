# Product Requirements and Planning (PRP)

| Field | Value |
|---|---|
| **Project** | Project Pulse Android |
| **Version** | 1.0.0 |
| **Owner** | Pixel Lab |
| **Status** | approved |
| **Last Updated** | 2026-07-03 07:15 |

## 1. Problem Statement

Developers and makers juggle many projects and lack a quick, mobile-first way to see which projects are healthy, how many tasks remain, how much time has been invested, and what the overall completion score is. Existing tools are either too heavy or desktop-bound.

## 2. Target Users

- Primary: Solo developers and indie makers managing multiple side projects.
- Secondary: Small-team leads who want a lightweight project health dashboard.

## 3. Goals

- [x] Provide a single-screen dashboard of all projects and their status.
- [x] Surface completion score, remaining task count, and logged hours per project.
- [x] Show aggregate development statistics across projects.
- [x] Offer a modern, sleek UI using Material 3 and Jetpack Compose.
- [x] Work fully offline with local persistence.

## 4. Non-Goals

- [ ] Cloud sync or multi-device collaboration.
- [ ] Deep task hierarchy (sub-tasks) or rich project notes.
- [ ] Time tracking timer/stopwatch (manual hour logging only).

## 5. Key Features

| ID | Feature | Priority | Owner | Status | Evidence |
|---|---|---|---|---|---|
| F-001 | Project dashboard overview | P0 | agent | done | AC-001, EV-001 |
| F-002 | Project list with status and completion score | P0 | agent | done | AC-002 |
| F-003 | Project detail with tasks and hours | P0 | agent | done | AC-003 |
| F-004 | Aggregate development statistics | P0 | agent | done | AC-004 |
| F-005 | Add/edit/delete projects and tasks | P0 | agent | done | AC-005 |
| F-006 | Local Room persistence with seed data | P0 | agent | done | AC-006 |
| F-007 | Material 3 dynamic theming | P1 | agent | done | AC-007 |

## 6. User Stories

- As a developer, I want to open the app and see all my projects at a glance so that I know where to focus.
- As a user, I want to see a completion score for each project so that I can track progress visually.
- As a user, I want to view remaining tasks per project so that I know what is left to ship.
- As a user, I want to log hours spent per project so that I can measure effort investment.
- As a user, I want aggregate stats (total projects, total tasks, total hours, average completion) so that I can gauge overall productivity.

## 7. Constraints

- Time: Single autonomous development iteration.
- Budget: Local-only, no paid services.
- Technology: Android API 26+, Jetpack Compose, Room, Hilt.
- Compliance: No PII, no network permissions required.

## 8. Dependencies

- Internal: Pixel Lab Android conventions (daily-trendscape template).
- External: Android SDK, Gradle, Jetpack Compose BOM, Room, Hilt.

## 9. Open Questions

| ID | Question | Owner | Due Date | Answer |
|---|---|---|---|---|

## 10. Revision History

| Date | Author | Change |
|---|---|---|
| 2026-07-03 | agent | Initial PRP. |
