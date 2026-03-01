# Refactoring Plan

## Completed
- [x] Strengthened API integration smoke checks for `/api/streak` and `/api/topic-stats`.
- [x] Synchronized `docs/API.md` with thin-controller unit coverage matrix.
- [x] Removed repository access from `ExportController` by moving export row loading behind `ExportService`.
- [x] Updated export tests (`ExportControllerErrorTest`, `ExportIntegrationTest` target run) for the new controller-service boundary.
- [x] Removed `QuestionRepository` dependency from `InterviewApiController`; question existence precondition moved into `InterviewFacade`/`InterviewService`.
- [x] Updated `InterviewApiControllerUnitTest` to verify facade-driven precondition guards instead of repository stubbing.
- [x] Upgraded `QuestionGenerationService` retry flow to include structured validation feedback from previous attempt.
- [x] Added unit test verifying retry prompt enrichment with prior validation violations.
- [x] Introduced dedicated `QuestionPromptBuilder` component for deterministic prompt assembly.
- [x] Added dedicated `QuestionPromptBuilderTest` coverage for feedback and null-safe defaults.
- [x] Implemented quality-aware retry loop in `OptionGenerationService` using existing interview retry limits.
- [x] Added hard-block quality guard for option generation failure and contextual retry hints.
- [x] Expanded `OptionGenerationServiceTest` to validate critical-quality retry with `QUALITY_FIX_REQUIRED` context.
- [x] Added `AdminMaintenanceService` and moved admin artifact maintenance operations out of `AdminController`.
- [x] Updated admin controller tests to validate behavior through service boundary instead of direct repository/cache coupling.
- [x] Added `AdminSeniorRulesService` and extracted senior-rules business logic from `AdminController`.
- [x] Switched `AdminController` senior-rules endpoints to service delegation with preserved API responses.
- [x] Kept admin endpoint regression tests green after service extraction.

## In Progress
- [ ] Continue thin-controller refactor for remaining controllers with direct persistence access.
- [ ] Standardize service-level precondition checks and keep controllers as HTTP adapters only.
- [ ] Harden LLM/question-engine quality pipeline (policy, scoring, retry) in service layer.

## Backlog
- [ ] Split oversized classes (`OptionQualityValidator`, `AbstractAiClient`) into smaller cohesive components.
- [ ] Add broader exception-handling and security hardening pass (sanitized logging, endpoint guard consistency).
- [ ] Expand regression coverage for refactored service boundaries and LLM failure paths.
