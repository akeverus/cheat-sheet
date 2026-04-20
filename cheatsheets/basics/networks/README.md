---
title: "Компьютерные сети"
description: "Точка входа в раздел сетей: OSI, TCP/IP, адресация, прикладные протоколы, безопасность сетей и маршруты чтения к прикладным темам."
tags:
  - meta
  - index
  - networks
type: "index"
updated: "2026-04-20"
---
# Компьютерные сети

Раздел охватывает базу сетей: модели OSI и TCP/IP, IPv4/IPv6 и CIDR, транспорт (TCP/UDP/QUIC), прикладные протоколы (HTTP, DNS, TLS, SSH), беспроводные сети и сетевую безопасность. Используется как фундамент для разделов по REST/gRPC, контейнерам, IaC и мониторингу.

Для кого: бэкенд-инженеры, которые хотят понимать, что происходит «под HTTP», и уметь диагностировать latency, потери пакетов, проблемы с DNS/TLS.

## Полезные ссылки

### Основной документ
- [Компьютерные сети: основы](networks-basics.md)

### Соседние разделы
- [Computer Science](../README.md)
- [Операционные системы](../README.md)
- [REST API](../README.md)
- [gRPC](../README.md)
- [Kubernetes](../README.md)
- [Безопасность данных](../README.md)
- [Трассировка и APM](../README.md)

### Внешние ресурсы
- [RFC Editor](https://www.rfc-editor.org/) — стандарты интернет-протоколов
- [High Performance Browser Networking (Ilya Grigorik)](https://hpbn.co/) — книга о сетях для веб-инженеров
- [Cloudflare Learning: Networking](https://www.cloudflare.com/learning/network-layer/)

## Содержание

- [Карта тем](#карта-тем)
- [Что внутри документа](#что-внутри-документа)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Слой | Ключевые понятия | Файл |
|------|-----------------|------|
| L1-L2 (канальный) | Ethernet, MAC, switch, VLAN | [networks-basics](networks-basics.md#модель-osi) |
| L3 (сетевой) | IPv4/IPv6, CIDR, ARP, ICMP, маршрутизация | [networks-basics](networks-basics.md#ip-адресация) |
| L4 (транспорт) | TCP handshake, состояния, UDP, QUIC | [networks-basics](networks-basics.md#протоколы-транспортного-уровня) |
| L7 (прикладной) | HTTP/1.1-HTTP/3, DNS, TLS, SSH, WebSocket | [networks-basics](networks-basics.md#протоколы-прикладного-уровня) |
| Беспроводные | Wi-Fi, стандарты 802.11 | [networks-basics](networks-basics.md#беспроводные-сети) |
| Безопасность | NAT, Firewall, VPN, TLS | [networks-basics](networks-basics.md#сетевая-безопасность) |

## Что внутри документа

[networks-basics](networks-basics.md) содержит:

- Полная модель OSI с примерами на каждом уровне
- TCP/IP: three-way handshake, flow/congestion control, keepalive
- IPv4 подсети, CIDR-нотация, NAT, RFC 1918 приватные диапазоны
- IPv6: формат адреса, SLAAC, dual-stack
- HTTP-методы и статусы, keep-alive, chunked encoding
- DNS: типы записей, resolver flow, TTL, DoH/DoT
- TLS handshake, сертификаты, HSTS
- Инструменты диагностики: `ping`, `traceroute`, `tcpdump`, `ss`, `nslookup`, `dig`

## Маршруты чтения

- **Fundamentals (45 мин):** `OSI TCP handshake IP-адресация HTTP DNS`.
- **Подготовка к собесу по системному дизайну (2 ч):** вся карта тем + разделы в `architecture/system-design/`.
- **Диагностика prod-инцидента:** `Решение проблем` инструменты `monitoring/tracing/` для корреляции.

## Куда идти дальше

- HTTP в деталях и REST-практики — [development/api/rest/](../README.md)
- Балансировка нагрузки — [architecture/system-design/](../README.md)
- Сетевые политики K8s и service mesh — [platform/containers/kubernetes/](../README.md)
- TLS, сертификаты, шифрование — [security/data/](../README.md)
