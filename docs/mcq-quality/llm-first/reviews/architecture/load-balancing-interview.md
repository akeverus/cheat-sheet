# Load Balancing — авторский проход MCQ

Дата: 2026-11-09. Модель: GPT-6. Авторский статус после отдельной ревизии: **VISUAL_PARITY_REVIEWED**. [Отчёт о визуальной симметрии](load-balancing-visual-parity-2026-11-09.md).

Пара и SHA-256:

- `cheatsheets/interview/architecture/load-balancing-interview.md` — `0583846493dceaecf745baf92382ba06644d6ab1b64b0d610a0633d609351354`.
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/load-balancing-interview.json` — `40f8eb878d256494e5f9b6e14729c0b0b2ccd995bb1488df43f90244fd1d3eea`.
- `docs/mcq-quality/fact-freshness/load-balancing-interview.json` — `bf0431d8e6d8a8f86c684148d7e2280a8d4821d2908cf67afd2c4d7513ac97cb`.

Прочитаны пролог, оглавление, все видимые Q1–Q40, парная теория, 160 вариантов A–D и их пояснения. Ключи и B0 сохранены. Q1–Q20 переписаны с искажённого языка на обычный технический русский; каждый неверный вариант теперь описывает конкретную ошибочную модель. Для Q21–Q40 уточнены условия продуктов и отказов. В теории сохранены полезные схемы и конфигурации, удалён неверный пример клиентского выбора по никогда не заполняемой карте latency.

Смысловые исправления: L4 не читает HTTP path, но может завершать TLS в конкретном продукте; forwarded headers принимают только от доверенного proxy. Health check отделён от readiness и общих зависимостей. Sticky routing не реплицирует состояние; least-connections не измеряет успех ответа; локальные счётчики клиентов не равны глобальной загрузке. Nginx `max_fails`/`fail_timeout` и динамический DNS описаны с условиями версии. Уточнены retry storm, draining, gRPC `pick_first`, WebSocket, cross-zone AWS, DNS TTL, BGP Anycast, Maglev и Istio ambient. Убраны универсальные пороги метрик, обещания мгновенного failover и неверные гарантии Redis Pub/Sub.

Проверенные первичные источники:

- [Nginx upstream](https://nginx.org/en/docs/http/ngx_http_upstream_module.html), [real IP](https://nginx.org/en/docs/http/ngx_http_realip_module.html) и [WebSocket proxying](https://nginx.org/en/docs/http/websocket.html).
- [HAProxy health checks](https://www.haproxy.com/documentation/haproxy-configuration-tutorials/reliability/health-checks/) и [configuration manual](https://www.haproxy.org/download/2.8/doc/configuration.txt).
- [Spring Cloud LoadBalancer](https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/loadbalancer.html).
- [Kubernetes probes](https://kubernetes.io/docs/concepts/workloads/pods/probes/) и [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/).
- [AWS ALB cross-zone/target group](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/edit-target-group-attributes.html) и [ELB routing](https://docs.aws.amazon.com/elasticloadbalancing/latest/userguide/how-elastic-load-balancing-works.html).
- [gRPC client LB](https://grpc.io/docs/guides/custom-load-balancing/), [Envoy policies](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/upstream/load_balancing/load_balancing), [Istio DestinationRule](https://istio.io/latest/docs/reference/config/networking/destination-rule/) и [ambient mode](https://istio.io/latest/docs/ambient/overview/).
- [Google Research: Maglev](https://research.google/pubs/maglev-a-fast-and-reliable-software-network-load-balancer/).

Открытых критических сомнений по ключам нет. Примеры конфигурации не выполнялись в установленной версии Nginx/HAProxy/Spring/Kubernetes; перед использованием в сервисе надо проверить версию, режим, синтаксис и реальный отказный сценарий. Пороги latency, ошибок и время переключения задаются SLO и измерениями конкретной системы.

Структурная проверка: `PASS: files=1 questions=40 blocks=40; semantic review NOT_RUN`. `git diff --check` — без ошибок. Независимое ревью и `FINAL PASS` не проводились.
