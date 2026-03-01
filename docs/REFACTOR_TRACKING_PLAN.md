# Refactor & Upgrade Plan

## Phase 1 – Architecture
- [x] Package refactor
- [x] Controller cleanup
- [x] Service separation
- [x] DTO layer introduced

## Phase 2 – Question Engine
- [x] Upgrade Question model
- [x] Add QuestionOption
- [x] Add Difficulty enum
- [x] Add QuestionType enum
- [x] Implement ValidationService
- [x] Implement AdaptiveDifficultyService

## Phase 3 – LLM Prompt Upgrade
- [x] Rewrite generation prompt
- [x] Add reasoning enforcement
- [x] Add distractor quality rules
- [x] Add self-validation step

## Phase 4 – UI/UX
- [x] Implement Focus Mode
- [x] Add ProgressBar
- [x] Refactor post-answer flow
- [x] Improve accessibility

## Phase 5 – Documentation
- [x] ARCHITECTURE.md
- [x] DOMAIN.md
- [x] LLM_PIPELINE.md
- [x] API.md
- [x] Update README.md

## Phase 6 – Tests
- [x] Validation tests
- [x] AdaptiveDifficulty tests
- [x] LLM parsing tests

## Current Iteration Notes
- [x] Adaptive topic prioritization by mastery in `TrainingSessionService`
- [x] Dedicated review filtering service (`ReviewModeService`)
- [x] Collapsible settings sidebar with keyboard/ARIA support
- [x] Focus page orchestration moved from `InterviewMvcController` to `FocusTrainingPageService`
- [x] Answer/stats page orchestration moved from MVC controller to `AnswerPageService` and `StatsPageService`
- [x] Session lifecycle endpoints moved to `SessionFlowService`
- [x] HTTP session state access centralized in `HttpSessionStateService`
- [x] MVC model attribute mapping centralized in `MvcModelAttributeMapper`
- [x] Request parsing and filter normalization centralized in `MvcRequestMapper`
- [x] MVC navigation routes/view names centralized in `MvcNavigationService`
- [x] Answer request DTO unpacking centralized in `MvcAnswerRequestMapper`
- [x] Settings/stats request context centralized in `MvcRequestMapper` records
- [x] `LLM_PIPELINE.md` synchronized with strict JSON + Question V2 quality gate
- [x] Start-session mode parsing centralized in `MvcRequestMapper`
- [x] MVC regression tests added for `/settings` mode/weak-topics and `/stats` search echo
- [x] `API.md` updated with MVC endpoints (`/settings`, `/stats`, `/review`) and request context notes
- [x] Added MVC regression tests for `/review` onlyWrong contract and `/stats` filter-link preservation
- [x] `SESSION_LOGIC.md` updated with `/review` flow and MVC request-orchestration details
- [x] `ARCHITECTURE.md` expanded with thin-MVC orchestration map and dependency boundaries
- [x] `CONTRIBUTING.md` updated with MVC refactor workflow and mandatory tracking/test steps
- [x] LLM contract tests expanded: `LlmRequestBuilderTest` + new `QuestionGenerationServiceTest` (regen + strict JSON failure path)
- [x] Mocked LLM parsing hardened: fenced/noisy JSON cases in `QuestionGenerationServiceTest` and `AiResponseParserTest`
- [x] `OptionGenerationServiceTest` expanded for noisy/fenced LLM option payloads and insufficient-unique-options guard (`AiGenerationException`)
- [x] API integration tests expanded for validation contract (`VALIDATION_ERROR`) on `/api/hint` and `/api/answer`
- [x] `/api/regenerate` contract covered by integration tests (`403`, `429`, `Retry-After`) and documented in `API.md`
- [x] `/api/regenerate` error-matrix completed with `404 QUESTION_NOT_FOUND` integration test
- [x] `/api/next` contract extended with integration test for invalid `excludeQuestionId` (`VALIDATION_ERROR`) and API docs update
- [x] `/api/confidence` validation contract covered: invalid `grade` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/wrong-feedback` validation contract covered: invalid `optionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/comparison` validation contract covered: invalid `selectedOptionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/takeaway` validation contract covered: invalid `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/code-trace` validation contract covered: invalid `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/comparison` validation contract covered: invalid `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/takeaway` validation contract covered: non-numeric `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/code-trace` validation contract covered: non-numeric `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/comparison` validation contract covered: non-numeric `selectedOptionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/comparison` validation contract covered: non-numeric `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/confidence` validation contract covered: non-numeric `grade` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/confidence` validation contract covered: non-numeric `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/confidence` validation contract covered: invalid `questionId` (`<=0`) -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/wrong-feedback` validation contract covered: non-numeric `optionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/wrong-feedback` validation contract covered: non-numeric `questionId` -> `400 VALIDATION_ERROR` (+ API docs)
- [x] `/api/wrong-feedback` error-matrix extended: non-existent `questionId` -> `404 QUESTION_NOT_FOUND` (+ API docs)
- [x] `/api/wrong-feedback` error-matrix extended: non-existent `optionId` -> `200 available=false` (+ API docs)
- [x] `LLM_PIPELINE.md` doc-sync: strict JSON injection in `AbstractAiClient`, quality-gate logging, noisy/fenced JSON parsing
- [x] `InterviewApiController` Javadocs added for key public endpoints (`/api/regenerate`, `/api/confidence`, `/api/wrong-feedback`, `/api/next`)
- [x] Added `InterviewApiControllerUnitTest`: `/api/wrong-feedback` branches (`available=true/false`) covered at controller unit level
- [x] Added `InterviewApiControllerUnitTest`: `/api/confidence` happy-path covered (`success=true`, facade delegation)
- [x] Added `InterviewApiControllerUnitTest`: `/api/regenerate` unauthorized branch covered (`403`, no regenerate call)
- [x] Added `InterviewApiControllerUnitTest`: `/api/regenerate` rate-limit branch covered (`429`, `Retry-After`, no regenerate call)
- [x] Added `InterviewApiControllerUnitTest`: `/api/regenerate` happy-path covered (`200`, `success=true`, regenerate delegation)
- [x] Added `InterviewApiControllerUnitTest`: `/api/next` empty branch covered (`204 No Content` when facade returns empty)
- [x] Added `InterviewApiControllerUnitTest`: `/api/next` happy-path covered (`200`, payload mapping for question/options)
- [x] Added `InterviewApiControllerUnitTest`: `/api/next` type fallback covered (`questionType=null` -> `"TEXT"`)
- [x] Added `InterviewApiControllerUnitTest`: `/api/next` review-state mapping covered (`repetitions/correctCount/wrongCount`)
- [x] Added `InterviewApiControllerUnitTest`: `/api/next` code/diagram passthrough covered (`codeSnippet` mapped, nullable `diagramMermaid`)
- [x] Added `InterviewApiControllerUnitTest`: `/api/next` filter normalization + flags forwarding covered (`topic/group trim`, `weakTopics`, `excludeQuestionId`)
- [x] Added `InterviewApiControllerUnitTest`: `/api/hint` unit coverage for available/unavailable payload branches
- [x] Added `InterviewApiControllerUnitTest`: `/api/answer` unit coverage for response mapping (`answerHtml`, `optionExplanations`, `session`, `relatedQuestions`)
- [x] Added `InterviewApiControllerUnitTest`: precondition guard for `/api/hint` (`QuestionNotFoundException` when `questionId` missing)
- [x] Added `InterviewApiControllerUnitTest`: precondition guard for `/api/wrong-feedback` (`QuestionNotFoundException` when `questionId` missing)
- [x] Added `InterviewApiControllerUnitTest`: `/api/takeaway` unit coverage for present/empty payload and missing-question precondition
- [x] Added `InterviewApiControllerUnitTest`: `/api/comparison` unit coverage for present/empty payload and missing-question precondition
- [x] Added `InterviewApiControllerUnitTest`: `/api/code-trace` unit coverage for present/empty payload and missing-question precondition
- [x] Added `InterviewApiControllerUnitTest`: `/api/stats` filter normalization + mapper delegation coverage
- [x] Added `InterviewApiControllerUnitTest`: `/api/topic-stats` facade-to-mapper passthrough coverage
- [x] Added `InterviewApiControllerUnitTest`: `/api/streak` service passthrough coverage
- [x] Added `InterviewApiControllerUnitTest`: `/api/favorite` result mapping + exception propagation coverage
- [x] `API.md` synchronized with `InterviewApiControllerUnitTest` thin-controller coverage matrix
- [x] `InterviewControllerApiTest` smoke strengthened: `/api/streak` includes `correct` field assertion
- [x] `InterviewControllerApiTest` smoke strengthened: `/api/topic-stats` validates topic-item JSON shape when payload is non-empty
- [x] `ExportController` decoupled from `QuestionStatsRepository` (rows loading moved to `ExportService`)
- [x] `ExportControllerErrorTest` updated to new controller-service boundary (no direct repository wiring in controller)
- [x] `InterviewApiController` decoupled from `QuestionRepository` (`ensureQuestionExists` moved to `InterviewFacade`/`InterviewService`)
- [x] `InterviewApiControllerUnitTest` updated to facade-level precondition stubbing (`doThrow` on `facade.ensureQuestionExists`)
- [x] `QuestionGenerationService` retry upgraded with `QUALITY_FEEDBACK_FROM_PREVIOUS_ATTEMPT` block built from validation violations
- [x] `QuestionGenerationServiceTest` expanded: retry prompt now asserts inclusion of previous validation violations
- [x] Introduced `QuestionPromptBuilder` for deterministic Question V2 prompt assembly with optional quality-feedback section
- [x] Added `QuestionPromptBuilderTest` (base prompt, feedback block, null-safe defaults)
- [x] `OptionGenerationService` now applies quality-aware retry (`optionQualityRetryAttempts/backoff/maxElapsed`) using `OptionQualityValidator` report
- [x] `OptionGenerationService` now appends `QUALITY_FIX_REQUIRED` context on retry and enforces hard-block failure guard
- [x] `OptionGenerationServiceTest` expanded: critical-quality retry path verifies second-attempt fix context injection
- [x] Added `AdminMaintenanceService` and moved `/api/admin/options/clear` + `/api/admin/reset-all` data operations out of `AdminController`
- [x] `AdminControllerTest` and `AdminControllerNoTokenTest` migrated from repository/cache mocks to `AdminMaintenanceService` mock boundary
- [x] Added `AdminSeniorRulesService` and moved senior-rules filtering/validation/patch/delete logic out of `AdminController`
- [x] `AdminController` now delegates senior-rules operations to service and keeps only HTTP/auth + response mapping
- [x] Admin controller tests kept green with real `AdminSeniorRulesService` via `@Import` and mocked `AppProperties`
