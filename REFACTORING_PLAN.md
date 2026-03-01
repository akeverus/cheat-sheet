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
- [x] Introduced `QuestionGenerationPolicy` with configurable minimum quality threshold for generated questions.
- [x] Added quality scoring to `QuestionValidationService` and integrated score-based acceptance in `QuestionGenerationService`.
- [x] Added retry-path test coverage for low-quality-score rejection in question generation.
- [x] Sanitized AI and global exception logging to reduce sensitive payload leakage risk.
- [x] Unified admin endpoint auth-guard flow in `AdminController` with shared `forbiddenIfUnauthorized` helper.
- [x] Removed duplicated validation-error mapping in `AdminController` via centralized `validationError` response builder.
- [x] Refactored senior-rules help payload to static constant for cleaner controller method bodies.
- [x] Kept admin controller MockMvc regression suite green after auth/error-handler refactor.
- [x] Reduced `AdminSeniorRulesService` duplication by unifying PUT/PATCH payload validation into a single strategy-aware validator.
- [x] Hardened `AdminSeniorRulesService` runtime safety by normalizing non-concurrent override maps into `ConcurrentHashMap`.
- [x] Added dedicated unit coverage for `AdminSeniorRulesService` (replace/patch/delete validation and concurrent-map migration path).
- [x] Standardized sensitive endpoint authorization checks via shared `forbiddenIfUnauthorized` guard in `SensitiveEndpointAccessService`.
- [x] Updated `/api/regenerate` and `/export` to use centralized auth-guard flow with preserved API contracts.
- [x] Added unit tests for `SensitiveEndpointAccessService` (forbidden flow + trusted/untrusted client key selection).
- [x] Added defensive property `app.trust-forwarded-for-header` (default false) for safer rate-limit key derivation.
- [x] Kept `InterviewApiControllerUnitTest` and `ExportControllerErrorTest` green after security-guard refactor.
- [x] Extended `QuestionGenerationPolicy` with cognitive-load constraints (question/options verbosity limits).
- [x] Added anti-duplication guard for question generation retries using candidate fingerprinting across attempts.
- [x] Integrated policy-level violation enrichment into `QuestionGenerationService` retry pipeline and quality logging.
- [x] Expanded question-engine tests with `QuestionGenerationPolicyTest` and duplicate-retry regression coverage.
- [x] Moved admin senior-rules validation exception mapping out of `AdminController` into global `@ControllerAdvice`.
- [x] Removed controller-local `try/catch` blocks from admin senior-rules endpoints (PUT/PATCH/DELETE), preserving API error contracts via centralized handler.
- [x] Kept admin `MockMvc` regression suite green after centralized exception mapping refactor.
- [x] Removed remaining auth-guard duplication from `AdminController` by delegating all checks to `SensitiveEndpointAccessService.forbiddenIfUnauthorized`.
- [x] Updated admin controller tests to use centralized forbidden-guard stubbing instead of direct `isAuthorized` stubs.

## In Progress
- [ ] Continue thin-controller refactor for remaining controllers with direct persistence access.
- [ ] Standardize service-level precondition checks and keep MVC controllers as HTTP adapters only.
- [ ] Expand quality policy to topic-level uniqueness constraints in generation flow.

## Backlog
- [ ] Split oversized classes (`OptionQualityValidator`, `AbstractAiClient`) into smaller cohesive components.
- [ ] Add broader exception-handling and security hardening pass (endpoint guard consistency, authz invariants).
- [ ] Expand regression coverage for refactored service boundaries and LLM failure paths.
