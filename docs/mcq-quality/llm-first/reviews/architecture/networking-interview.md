# Networking — авторский проход MCQ

Дата: 2026-11-09. Модель: GPT-6. Статус: **LLM_READY**.

Пара и SHA-256:

- `cheatsheets/interview/architecture/networking-interview.md` — `02c1ab2aab449c95b0a47bd8fe0d4d4cfad5bba4733e9f5e3a8a92375c9d1bf1`.
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/networking-interview.json` — `3ae4979c9280c11176a51569e5ea408ef87054c6e52dc7dd36b4cac9d27e9cda`.
- `docs/mcq-quality/fact-freshness/networking-interview.json` — `5b200c007edfc1bed25d9c5a1d0a24070cb562d3a56c3c5880b44c9a7ebdb026`.

Прочитаны пролог, оглавление, видимые Q1–Q43, парная теория, 172 варианта A–D и пояснения. Идентификаторы B0 и ключи сохранены. Самопроверка не заменяет независимое ревью. Отдельная ревизия визуальной симметрии записана в `networking-visual-parity-2026-11-09.md`.

Исправлены безусловные формулировки о TCP keep-alive и закрытии соединения, HTTP/1.1 persistent connection, HTTP/2 HOL, HTTP/3 push, TLS 1.3 PSK-only и 0-RTT, DNS-кэше Java, CORS preflight, L4/L7 и TLS termination, сетевых политиках Kubernetes, service mesh и certificate pinning. Для UDP и QUIC разделены свойства транспорта и гарантий приложения. Убраны универсальные числа задержки и производительности без условий измерения. Дистракторы приведены к полным проверяемым ошибочным моделям, а пояснения согласованы с их новой редакцией.

Проверенные первичные источники:

- [HTTP/1.1, RFC 9112](https://www.rfc-editor.org/rfc/rfc9112.html), [HTTP/2, RFC 9113](https://www.rfc-editor.org/rfc/rfc9113.html), [HTTP/3, RFC 9114](https://www.rfc-editor.org/rfc/rfc9114.html) и [поддержка Push в Chrome](https://developer.chrome.com/blog/removing-push).
- [QUIC, RFC 9000](https://www.rfc-editor.org/rfc/rfc9000.html), [congestion control, RFC 9002](https://www.rfc-editor.org/rfc/rfc9002.html), [TCP, RFC 9293](https://www.rfc-editor.org/rfc/rfc9293.html), [TLS 1.3, RFC 8446](https://www.rfc-editor.org/rfc/rfc8446.html).
- [Fetch/CORS](https://fetch.spec.whatwg.org/), [Java InetAddress](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/InetAddress.html), [Java HttpClient](https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/java/net/http/HttpClient.html).
- [Kubernetes Service](https://kubernetes.io/docs/concepts/services-networking/service/) и [NetworkPolicy](https://kubernetes.io/docs/concepts/services-networking/network-policies/), [Istio ambient](https://istio.io/latest/docs/ambient/overview/), [Linkerd mTLS](https://linkerd.io/docs/features/automatic-mtls/), [OWASP pinning](https://community.owasp.org/controls/Certificate_and_Public_Key_Pinning).

Открытых критических сомнений по ключам нет. Примеры Java, Kubernetes, браузера и mesh не проверялись на конкретном развёртывании; перед применением нужно сверить версию и конфигурацию. Числа задержки, DNS TTL и ограничения соединений не приняты как универсальные.

Структурная проверка: `PASS: files=1 questions=43 blocks=43; semantic review NOT_RUN`. `git diff --check` — без ошибок. Независимое ревью и прежний `FINAL PASS` не проводились.
