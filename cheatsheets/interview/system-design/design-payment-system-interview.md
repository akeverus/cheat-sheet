---
title: "Вопросы на собеседовании: Design Payment System"
description: "System design payment: Stripe-like, idempotency, consistency, double-entry ledger, PCI-DSS, 3DS, reconciliation, webhooks, saga, distributed transactions"
tags:
  - interview
  - system-design
  - design-payment-system-interview
aliases:
  - "Payment System design"
  - "Stripe architecture"
  - "Payment processing"
  - "Payment System собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Design Payment System`

`Payment System` (Stripe, PayPal, internal billing) — **один из самых сложных system design**. Требует: **strong consistency, idempotency, durability (money!), PCI-DSS compliance, reconciliation**. Fault tolerance критична — no lost payments ever.

Дата последнего обновления: 2026-04-19

## Полезные ссылки

- [Stripe engineering blog](https://stripe.com/blog/engineering)
- [How Stripe handles idempotency](https://stripe.com/docs/idempotency)
- [PCI-DSS standard](https://www.pcisecuritystandards.org/)
- [Double-entry accounting](https://en.wikipedia.org/wiki/Double-entry_bookkeeping)
- [Uber payments platform](https://eng.uber.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Requirements**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Capacity estimation?](#q2--capacity-estimation)

**Flow**
- [Q3. (!) End-to-end payment flow?](#q3--end-to-end-payment-flow)
- [Q4. (!) 3D Secure и challenge flow?](#q4--3d-secure-и-challenge-flow)
- [Q5. Authorization vs capture?](#q5-authorization-vs-capture)

**Architecture**
- [Q6. (!) High-level architecture?](#q6--high-level-architecture)
- [Q7. (!) Double-entry ledger?](#q7--double-entry-ledger)
- [Q8. Integration с payment providers?](#q8-integration-с-payment-providers)

**Consistency**
- [Q9. (!) Idempotency ключи — зачем и как?](#q9--idempotency-ключи--зачем-и-как)
- [Q10. (!) Saga pattern для distributed flow?](#q10--saga-pattern-для-distributed-flow)
- [Q11. (!) Eventually consistent vs strong consistency?](#q11--eventually-consistent-vs-strong-consistency)

**Security**
- [Q12. (!) PCI-DSS compliance?](#q12--pci-dss-compliance)
- [Q13. (!) Tokenization карт?](#q13--tokenization-карт)
- [Q14. Fraud detection?](#q14-fraud-detection)

**Webhooks и integration**
- [Q15. (!) Webhook delivery?](#q15--webhook-delivery)
- [Q16. Reconciliation?](#q16-reconciliation)

**Production**
- [Q17. (!) Retry strategy?](#q17--retry-strategy)
- [Q18. Refunds, disputes, chargebacks?](#q18-refunds-disputes-chargebacks)
- [Q19. (!) Testing payments (sandbox, mock)?](#q19--testing-payments-sandbox-mock)
- [Q20. Observability для payments?](#q20-observability-для-payments)

## Q1. (!) Functional и non-functional requirements?

**Functional:**
- Charge card (one-time)
- Refund
- Subscription billing
- Multiple payment methods (card, ACH, wallet)
- Multi-currency
- Webhooks для merchants
- Dashboard (transactions, metrics)

**Non-functional:**
- **Correctness:** никаких lost / duplicate transactions (money!)
- **Idempotency:** duplicate API calls = one charge
- **High availability** (99.99%+)
- **Low latency** для authorization (< 2s)
- **Auditability:** every event logged, immutable
- **PCI-DSS compliance**
- **Scalability** (100k+ tx/sec peak — Black Friday)

**Financial regulations:**
- Money handling has legal/accounting requirements
- Can't delete records
- Must reconcile daily

## Q2. (!) Capacity estimation?

**Assumptions (mid-size like Stripe):**
- 10M merchants
- 500M transactions/day = ~6K tps average
- Peak (Black Friday): 100K tps
- Transaction ~1KB metadata

**Storage:**
- 500M × 1KB = 500 GB/day raw
- 5 years retention: ~900 TB (keep forever for compliance)
- Indexes + replicas: 3-5x

**Bandwidth:**
- Avg: 6K × 1KB = 6 MB/s
- Peak: 100K × 1KB = 100 MB/s — modest

**Read:**
- Merchants dashboards (analytics): lower QPS, но heavy queries
- Reporting pipelines: batch

**Latency budget:**
- Authorization: 1-2s end-to-end
- Includes: external network к PSP (Visa/Mastercard), 3DS redirects
- Internal: < 500ms

## Q3. (!) End-to-end payment flow?

```
1. Customer initiates payment (Checkout page)
2. Frontend → tokenize card (via Stripe.js / SDK) → token
3. Backend POST /charges with token + amount + idempotency key
4. Payment Service:
   a. Validate request
   b. Check idempotency (repeat?)
   c. Risk/fraud screening
   d. Route to payment processor (Stripe/Adyen/direct)
5. Processor → card network (Visa/MC) → issuing bank
6. Bank approves/declines
7. Response propagates back
8. Payment Service:
   a. Write to ledger (double-entry)
   b. Mark transaction status
   c. Fire events (webhook, analytics)
9. Return to frontend
10. Confirmation page
```

**Time:** typically 1-3 seconds total.

**Asynchronous parts:**
- Webhook delivery
- Settlement (T+2 typically)
- Payout к merchant bank

**Failure modes:**
- Network timeout между steps → retry с idempotency
- Bank declines → user sees error, can retry другой card
- PSP outage → fallback (если have multiple)

## Q4. (!) 3D Secure и challenge flow?

**3D Secure (3DS):** additional auth layer from card network (Visa Secure, Mastercard SecureCode).

**Flow:**
1. Payment request submitted
2. Issuing bank decides: "frictionless" (no user action) или "challenge"
3. Challenge: user redirected к bank's page (OTP, biometric)
4. User authenticates → return with authorization
5. Payment proceeds

**3DS 2.0:**
- Risk-based: data shared with bank for decision
- Often frictionless (80%+ cases)
- Challenge only high-risk

**Regulatory:**
- **PSD2 (EU):** Strong Customer Authentication (SCA) mandatory for most transactions
- Exemptions (low value, trusted merchant)

**Implementation:**
- Stripe PaymentIntents handle 3DS automatically
- Merchant shows returned action (e.g., `requires_action` → redirect user)

**Impact:**
- Higher cart abandonment (extra step)
- Liability shift: if 3DS completed, issuer liable for chargeback (not merchant)

## Q5. Authorization vs capture?

**Auth (authorization):**
- Bank reserves amount on card
- Merchant не получает money yet
- Can cancel (void) before capture

**Capture:**
- Actually move money к merchant
- Usually within 7 days of auth

**Why separate:**
- E-commerce: auth at order, capture at shipment (only charge for what ships)
- Hotels: auth at booking, capture at check-out (incidentals)
- Subscriptions: auth + immediate capture

**Flow:**
```
POST /charges  (amount=100, capture=false)
→ auth only, holds $100

# Later
POST /charges/{id}/capture  (amount=80)
→ capture $80, release remaining $20

# Or void entirely
POST /charges/{id}/void
→ release all, bank un-holds
```

**Expiry:**
- Auth holds expire (5-30 days depending on card type)
- Must capture before expiry or re-auth

## Q6. (!) High-level architecture?

```
Clients (merchants)
    ↓
[API Gateway] — auth, rate limit
    ↓
[Payment Service] — main orchestrator
    ↓ ↑         ↓        ↓
[Fraud]    [Ledger]  [Vault (PCI)]
    ↓
[Payment Processors] — Stripe, Adyen, direct card networks
    ↓ (Async)
[Event Bus (Kafka)]
    ↓
[Webhook Service] — deliver to merchants
[Analytics] — reporting
[Reconciliation] — daily settlement
    ↓
[Databases]
- Postgres (strong consistency для ledger)
- Redis (idempotency cache)
- S3 (audit logs, documents)
```

**Responsibilities:**
- API: HTTPS termination, auth, rate-limit, route
- Payment Service: orchestrate flow, state machine
- Fraud: score transactions, block/allow
- Ledger: immutable accounting
- Vault: PCI-compliant card data store
- Processor integration: REST/SFTP к bank/PSP

## Q7. (!) Double-entry ledger?

**Core accounting principle:** every transaction touches two accounts, total = 0.

**Example: $100 payment from user A к merchant B.**

```
Transaction 1 (user pays):
  User A balance: -$100 (debit)
  Processor holding: +$100 (credit)

Transaction 2 (fees):
  Processor holding: -$2.90 (debit)
  Platform fees: +$2.90 (credit)

Transaction 3 (payout to merchant):
  Processor holding: -$97.10 (debit)
  Merchant B balance: +$97.10 (credit)
```

**Sum of all sides = 0** always.

**Storage:**
```sql
CREATE TABLE ledger_entries (
    id BIGSERIAL PRIMARY KEY,
    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,
    amount BIGINT NOT NULL,  -- positive or negative, cents
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Invariant (checked):
-- SUM(amount) GROUP BY transaction_id = 0 always
```

**Immutability:**
- Never update row; only append
- Reversals = new entries (opposite sign)
- History auditable

**Balance:**
- `SELECT SUM(amount) FROM ledger WHERE account_id=X` — but slow для many entries
- Materialized: `account_balance` table, updated atomically с ledger entries
- Eventually consistent or transactional

**Enforces correctness:**
- Can't "disappear" money
- Easier reconciliation

## Q8. Integration с payment providers?

**Options:**

**1. Use aggregator (Stripe, Adyen, Braintree):**
- Single API to many payment methods
- They handle card networks
- We pay fee per transaction
- Fast to integrate

**2. Direct integration (card networks):**
- Lower fees
- Custom logic
- Huge compliance/dev cost
- Need relationships with issuing/acquiring banks

**3. Multi-provider:**
- Primary + fallback
- Routing по cost/success rate
- Complex но resilient

**Adapter pattern:**
```java
interface PaymentProcessor {
    AuthResult authorize(Card card, Money amount);
    void capture(String authId, Money amount);
    void refund(String chargeId, Money amount);
}

class StripeProcessor implements PaymentProcessor { ... }
class AdyenProcessor implements PaymentProcessor { ... }
```

Router picks provider по rules (currency, cost, health).

**Idempotency across providers:**
- Each has own idempotency key format
- We translate / maintain map

## Q9. (!) Idempotency ключи — зачем и как?

**Problem:** client times out → retries → two charges for same order.

**Solution: idempotency key:**
- Client sends `Idempotency-Key: <uuid>`
- Server records key → result mapping
- Retry with same key returns cached result (no double charge)

**Implementation:**

```sql
CREATE TABLE idempotency (
    key TEXT PRIMARY KEY,
    request_hash TEXT,
    response JSONB,
    created_at TIMESTAMP,
    expires_at TIMESTAMP  -- 24h typical
);
```

**Flow:**
```python
def charge(idempotency_key, request):
    existing = db.select(idempotency_key)
    if existing:
        if existing.request_hash != hash(request):
            raise IdempotencyError("Key used with different request")
        return existing.response
    
    # New request
    response = process_charge(request)
    db.insert(idempotency_key, hash(request), response)
    return response
```

**Concurrent requests (same key):**
- DB unique constraint prevents duplicate
- Second request waits or fails; retries see cached result
- Or use advisory lock

**Stripe:** keys valid 24h; key unique per API key.

**Critical:** include idempotency key в ALL money-moving endpoints.

## Q10. (!) Saga pattern для distributed flow?

**Payment involves multiple services:** inventory reserve, payment charge, shipping arrange.

**Atomicity impossible** в distributed setting → saga.

**Saga:** sequence of local transactions; compensating actions on failure.

**Example (order):**
```
1. Reserve inventory (local tx)
2. Charge card (call payment service)
3. Arrange shipping (call shipping service)
4. Mark order complete

On failure:
- Step 3 fails → compensate: refund payment (2), release inventory (1)
- Step 2 fails → compensate: release inventory (1)
```

**Implementations:**

**Orchestration:**
- Central coordinator calls services + handles failures
- Easier to reason about
- Tools: Temporal, Camunda, AWS Step Functions

**Choreography:**
- Services emit events; others react
- Decentralized
- Harder to track workflow

**Compensating actions:**
- Not always truly atomic (funds moved can't literally "un-move" — just counteract)
- Refund = new transaction opposite direction

**State machine per order:**
```
CREATED → INVENTORY_RESERVED → PAYMENT_CHARGED → SHIPPED → COMPLETED
                                       ↓ fail
                                       REFUNDED
```

Log state transitions; can resume после crash.

## Q11. (!) Eventually consistent vs strong consistency?

**Where strong consistency required:**
- Ledger balance (must not show false positive)
- Idempotency check
- Account state

**Where eventual OK:**
- Analytics / reporting
- Search index
- Dashboards

**Hybrid approach:**
- Core transactions: ACID DB (Postgres)
- Asynchronously replicate to read stores (analytics DB, search)
- Accept lag на non-critical views

**Why not всё strongly consistent:**
- Strong consistency = slow, expensive
- Some data replicated для scaling reads

**Example:**
- Charge succeeds → write ledger (sync), return to user immediately
- Analytics updated via Kafka → Flink → BigQuery (минуты лаг)
- Dashboard shows eventually

**CAP trade-off:**
- Payments tend CP (consistency + partition tolerance)
- Availability not 100% (decline transactions если DB partition)
- Acceptable: lose few seconds of transactions vs corrupt ledger

## Q12. (!) PCI-DSS compliance?

**PCI-DSS:** Payment Card Industry Data Security Standard.

**Applies to any system handling card data** (CHD: number, CVV, expiry).

**12 requirements:**
1. Firewall config
2. No default passwords
3. Protect stored CHD
4. Encrypt transmission
5. Anti-malware
6. Secure development
7. Access by need-to-know
8. Unique IDs per user
9. Physical access restrictions
10. Track/monitor access
11. Regular testing
12. Information security policy

**Levels:**
- Level 1: > 6M tx/year (most strict, annual audit)
- Level 2-4: lower volume; SAQ (self-assessment questionnaire)

**Reducing scope:**
- Store as little CHD as possible
- **Tokenization:** token instead of real PAN (см. Q13)
- Offload к PCI-certified vendor (Stripe) → your scope minimal

**Common:**
- Separate PCI zone (network) from rest
- Encrypted DB для CHD (AES-256)
- Strict RBAC
- Logs in tamper-evident store
- Annual pen-test

## Q13. (!) Tokenization карт?

**Tokenization:** replace real PAN (Primary Account Number) с surrogate token.

**Example:**
- Real card: `4242-4242-4242-4242`
- Token: `tok_1A2B3C4D5E6F...`

**Vault:**
- Stores PAN ↔ token mapping
- Extremely locked down (PCI-DSS level 1)
- HSM (Hardware Security Module) for encryption

**App flow:**
- Frontend: Stripe.js tokenizes (PAN never touches our server)
- Backend receives token; stores token (PCI scope = token store only)
- Charge API: send token to Stripe → Stripe detokenizes in their vault → charge

**Re-usable tokens (card saved):**
- `customer_id` + `card_id` (token)
- Recurring charges use these

**Format-preserving tokens:**
- Token looks like PAN format (passes Luhn check)
- Systems unchanged; less intrusive

**Benefits:**
- If app DB breached → attacker gets tokens, not cards
- Scope reduction (PCI audits)

## Q14. Fraud detection?

**Goal:** identify fraudulent transactions before approval.

**Signals:**
- Device fingerprint (browser, IP, screen size)
- Behavior (velocity — X charges in Y minutes)
- Geo mismatch (card issued in US, IP в Russia)
- Card BIN (issuing bank)
- Past history (user/device/card)
- Amount anomaly

**Layers:**

**1. Rules engine:**
- "Decline if > $1000 AND new IP AND no 3DS"
- Simple, transparent

**2. ML model:**
- Features → probability of fraud
- Gradient boosting / neural net
- Retrained on recent labeled data

**3. Network data (Stripe Radar, Riskified):**
- Aggregate signals across many merchants
- "Card X used on 20 merchants in last hour" = suspicious

**Actions:**
- Block
- Require 3DS (step-up auth)
- Allow с review flag
- Allow + monitor

**Feedback loop:**
- Chargebacks labeled "fraud"
- Retrain model

**Trade-off:**
- False positives = lost revenue (legit customer blocked)
- False negatives = chargeback cost + reputation

## Q15. (!) Webhook delivery?

**Webhooks:** notify merchant about events (payment succeeded, refund processed).

**Requirements:**
- **At-least-once delivery** (retry на failure)
- **Signed** (prove it's from us)
- **Ordered per object** (sometimes; easier unordered)

**Implementation:**

**Producer side:**
- Event → Kafka topic
- Webhook worker reads, POSTs к merchant URL
- On failure: retry с backoff

**Retry policy:**
- Exponential: 1min, 5min, 30min, 2h, 12h, 1d
- Give up after ~3 days
- Alert merchant dashboard

**Signing:**
- HMAC with shared secret
- Header `Stripe-Signature: t=1234,v1=abc123...`
- Merchant verifies to reject fakes

**Idempotency:**
- Each event has unique ID
- Merchant processes once (idempotency key = event ID)

**Consumer (merchant) must:**
- Return 2xx quickly (within 30s); do work async
- Handle duplicates (retry scenarios)
- Verify signature

**Dead-letter:**
- Exhausted retries → DLQ
- Alert merchant via dashboard / email

## Q16. Reconciliation?

**Daily process:** compare our records vs bank/processor records.

**Why:**
- Detect discrepancies (missed transaction, amount mismatch)
- Legal requirement (audit)
- Catch bugs early

**Process:**
1. Download settlement file from PSP (SFTP, API)
2. For each reported transaction → lookup in our DB
3. Match amount, date, status
4. Flag mismatches для investigation
5. Report summary (total settled, fees, discrepancies)

**Automation:**
- Scheduled job (nightly)
- Ticket created automatically для unmatched
- Dashboard for finance team

**Common discrepancies:**
- Timing (we captured 23:59 UTC; bank files за следующий день)
- Fees different (вендор adjusted)
- Refunds queued but not processed

**Tools:**
- Custom batch jobs (Spark, Airflow)
- Vendors (Modern Treasury, Lockstep)

## Q17. (!) Retry strategy?

**Scenarios:**
- Network timeout к PSP
- PSP 5xx error
- Internal error после partial success

**Rules:**

**Retry safe (idempotent) operations:**
- GET (read status)
- POST с idempotency key

**DON'T retry without idempotency:**
- Double charge risk

**Exponential backoff + jitter:**
```python
for attempt in range(5):
    try:
        return call_psp(idempotency_key)
    except NetworkError as e:
        sleep(2**attempt + random.random())
```

**Circuit breaker:**
- PSP failing 50% → break → fall to backup PSP
- Resilience4j, Hystrix

**Async retries:**
- Queue (SQS, Kafka) → worker retries
- Long retries (hours) without blocking client

**Limits:**
- Max 5-10 attempts
- After → manual review

**User-visible:**
- Short sync retries (1-2) while user waits
- Longer async (background)

## Q18. Refunds, disputes, chargebacks?

**Refund:** merchant initiates return money to customer.
- Full or partial
- API: `POST /refunds`
- Ledger: reverse entries
- Usually 5-10 business days к customer card

**Dispute (chargeback):** customer disputes charge via bank.
- Bank returns funds, starts investigation
- Merchant: submits evidence (receipts, shipping, logs) in response
- Bank decides: charge reinstated OR refund stands
- Lost dispute = additional fee ($15-25)

**Process:**
- Merchant receives dispute webhook
- Provides evidence by deadline (10-30 days)
- Outcome: accepted or represented

**Prevention:**
- Clear billing descriptor ("ACME CORP ONLINE")
- Good customer service (refund proactively)
- Fraud prevention (reduce fraud-related chargebacks)

**Metrics:**
- Chargeback ratio (< 0.75% warning, > 1% high-risk)
- Card networks penalize high ratios

**Refund vs chargeback:**
- Refund: merchant voluntary, cheap
- Chargeback: forced by bank, expensive + damaging

## Q19. (!) Testing payments (sandbox, mock)?

**Challenges:**
- Can't test real transactions в production
- Need to test rare cases (declined, fraud, 3DS challenge)

**Strategies:**

**1. Sandbox:**
- PSP offers test environment
- Stripe: test mode с test cards
  - `4242 4242 4242 4242` (successful)
  - `4000 0000 0000 0002` (declined)
  - `4000 0025 0000 3155` (3DS required)
- Same API, isolated data

**2. Unit tests:**
- Mock PSP client at interface
- Test business logic (idempotency, ledger updates)

**3. Integration tests:**
- Real HTTP calls к sandbox
- Slower; use для critical paths

**4. E2E tests:**
- Full flow in staging environment
- Synthetic test merchant + test cards

**5. Chaos testing:**
- Inject failures (network, slow PSP)
- Ensure retries/compensation работают

**Prod safety:**
- Feature flags (dark launch new payment methods)
- Canary (1% traffic → monitor)
- Gradual rollout

## Q20. Observability для payments?

**Metrics:**
- Authorization success rate
- Decline rate by reason
- Latency (auth, capture) p50/p99
- Fraud score distribution
- Retry count
- PSP error rate (by provider)

**Logs:**
- Every state transition with transaction_id
- Immutable, structured (JSON)
- Retention: years (compliance)

**Traces (OpenTelemetry):**
- Distributed tracing across services
- Debug "which step slow/failed"

**Alerts:**
- Auth success rate drops > 5%
- PSP error rate > 1%
- Ledger balance check fails (invariant broken)
- Webhook delivery lag > threshold

**Audit log:**
- Who accessed what (internal tool access)
- Customer PII access logged

**Dashboards:**
- Finance: daily settlements, reconciliation status
- Eng: latencies, errors
- Product: conversion, 3DS impact

**Synthetic monitoring:**
- Continuously test sandbox payments
- Alert if flow breaks

---

## See also

- [System Design](system-design-interview.md) — общие принципы
- [Saga Pattern](../architecture/saga-pattern-interview.md) — distributed transactions
- [Distributed Systems](../architecture/distributed-systems-interview.md) — consistency, partition
- [Consistency Patterns](../architecture/consistency-patterns-interview.md) — strong vs eventual
- [CAP Theorem](../architecture/cap-theorem-interview.md) — trade-offs
- [Application Security](../security/application-security-interview.md) — PCI, encryption
- [Secrets Management](../security/secrets-management-interview.md) — vault, keys
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — retries, circuit breaker
- [Observability](../monitoring/observability-interview.md) — tracing, logs
- [Event-Driven Patterns](../architecture/event-driven-patterns-interview.md) — webhooks, sagas
