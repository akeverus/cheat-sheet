---
title: "Nginx Advanced"
description: "Nginx - это высокопроизводительный веб-сервер, reverse proxy, load balancer и HTTP cache с открытым исходным кодом. Этот документ охватывает продвинутые конфигурации, оптимизацию производительности и enterprise-grade паттерны использования Nginx."
tags:
  - platform
  - infrastructure-tools
  - nginx-advanced
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Nginx Advanced

**Nginx** — это высокопроизводительный веб-сервер, **reverse proxy**, **load balancer** и **HTTP cache** с открытым исходным кодом. Этот документ охватывает продвинутые конфигурации, оптимизацию производительности и **enterprise-grade** паттерны использования **Nginx**.

## Полезные ссылки
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Nginx Admin Guide](https://nginx.org/en/docs/http/ngx_http_core_module.html)
- [Nginx Modules Reference](https://nginx.org/en/docs/)
- [Nginx Load Balancing](https://nginx.org/en/docs/http/load_balancing.html)
- [Nginx Security Controls](https://nginx.org/en/docs/http/ngx_http_ssl_module.html)
- [Nginx Performance Tuning](https://nginx.org/en/docs/)

## Содержание

- [Продвинутая архитектура](#продвинутая-архитектура)
  - [Много-процессная модель](#много-процессная-модель)
  - [Dynamic modules](#dynamic-modules)
- [Load Balancing стратегии](#load-balancing-стратегии)
  - [Advanced upstream конфигурации](#advanced-upstream-конфигурации)
  - [Health checks и failover](#health-checks-и-failover)
- [Кэширование и оптимизация](#кэширование-и-оптимизация)
  - [HTTP caching](#http-caching)
  - [FastCGI caching](#fastcgi-caching)
  - [Microcaching](#microcaching)
- [Security hardening](#security-hardening)
  - [SSL/TLS конфигурация](#ssltls-конфигурация)
  - [Rate limiting и DDoS защита](#rate-limiting-и-ddos-защита)
  - [WAF (Web Application Firewall)](#waf-web-application-firewall)
- [API Gateway паттерны](#api-gateway-паттерны)
  - [API versioning и routing](#api-versioning-и-routing)
  - [Circuit breaker pattern](#circuit-breaker-pattern)
  - [API rate limiting per user](#api-rate-limiting-per-user)
- [Microservices паттерны](#microservices-паттерны)
  - [Service mesh с Nginx](#service-mesh-с-nginx)
  - [Blue-Green deployment](#blue-green-deployment)
- [Performance optimization](#performance-optimization)
  - [TCP optimizations](#tcp-optimizations)
  - [Memory и CPU optimization](#memory-и-cpu-optimization)
  - [Monitoring и metrics](#monitoring-и-metrics)
- [Enterprise patterns](#enterprise-patterns)
  - [Multi-tenant architecture](#multi-tenant-architecture)
  - [Content delivery optimization](#content-delivery-optimization)
- [Решение проблем](#решение-проблем)
  - [Debug logging](#debug-logging)
  - [Performance monitoring](#performance-monitoring)
  - [Common issues resolution](#common-issues-resolution)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## Продвинутая архитектура

### Много-процессная модель
```nginx
# nginx.conf — оптимизированная конфигурация (worker_processes, events, буферы, логи)
user nginx;
worker_processes auto;  # Автоматическое определение количества ядер
worker_cpu_affinity auto;  # Автоматическое распределение по ядрам

error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

worker_rlimit_nofile 65536;  # Увеличение лимита открытых файлов

events {
    worker_connections 2048;  # Максимум соединений на worker
    multi_accept on;          # Принимать несколько соединений сразу
    use epoll;                # Оптимизированный event loop для Linux

    # Оптимизация для большого количества соединений
    accept_mutex off;         # Отключение accept mutex в многопроцессном режиме
    accept_mutex_delay 500ms;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Логирование
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for" '
                    'rt=$request_time uct="$upstream_connect_time" '
                    'uht="$upstream_header_time" urt="$upstream_response_time"';

    access_log /var/log/nginx/access.log main buffer=32k flush=5m;

    # Буферы
    client_body_buffer_size 128k;
    client_max_body_size 100m;
    client_header_buffer_size 1k;
    large_client_header_buffers 4 4k;

    # Таймауты
    client_body_timeout 12;
    client_header_timeout 12;
    keepalive_timeout 65;
    send_timeout 10;

    # Компрессия
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied expired no-cache no-store private must-revalidate auth;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # Кэширование
    open_file_cache max=10000 inactive=30s;
    open_file_cache_valid 30s;
    open_file_cache_min_uses 2;
    open_file_cache_errors on;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=3r/m;

    # Upstream группы
    upstream backend {
        least_conn;  # Least connections алгоритм
        server backend1.example.com:8080 weight=3 max_fails=3 fail_timeout=30s;
        server backend2.example.com:8080 weight=2 max_fails=3 fail_timeout=30s;
        server backend3.example.com:8080 weight=1 max_fails=3 fail_timeout=30s;
        keepalive 32;  # HTTP keepalive
    }

    upstream websocket_backend {
        ip_hash;  # IP hash для WebSocket сессий
        server ws1.example.com:8080;
        server ws2.example.com:8080;
    }

    include /etc/nginx/conf.d/*.conf;
}
```

### Dynamic modules
```bash
# Сборка Nginx с дополнительными модулями
./configure \
    --prefix=/usr/local/nginx \
    --with-http_ssl_module \
    --with-http_v2_module \
    --with-http_realip_module \
    --with-http_addition_module \
    --with-http_xslt_module \
    --with-http_image_filter_module \
    --with-http_geoip_module \
    --with-http_sub_module \
    --with-http_dav_module \
    --with-http_flv_module \
    --with-http_mp4_module \
    --with-http_gunzip_module \
    --with-http_gzip_static_module \
    --with-http_auth_request_module \
    --with-http_random_index_module \
    --with-http_secure_link_module \
    --with-http_degradation_module \
    --with-http_slice_module \
    --with-http_stub_status_module \
    --with-mail \
    --with-mail_ssl_module \
    --with-stream \
    --with-stream_realip_module \
    --with-stream_ssl_module \
    --with-stream_ssl_preread_module \
    --with-google_perftools_module \
    --with-cpp_test_module \
    --with-debug

make && make install
```

## Load Balancing стратегии

### Advanced upstream конфигурации
```nginx
# Weighted Round Robin
upstream api_backend {
    server api1.example.com:8080 weight=5;
    server api2.example.com:8080 weight=3;
    server api3.example.com:8080 weight=1;
}

# Least Connections
upstream db_backend {
    least_conn;
    server db1.example.com:3306 max_fails=3 fail_timeout=30s;
    server db2.example.com:3306 max_fails=3 fail_timeout=30s;
}

# IP Hash для сессионности
upstream session_backend {
    ip_hash;
    server app1.example.com:8080;
    server app2.example.com:8080;
    server app3.example.com:8080;
}

# Hash based on URI
upstream cache_backend {
    hash $request_uri consistent;
    server cache1.example.com:8080;
    server cache2.example.com:8080;
}

# Random с весами
upstream random_backend {
    random two;
    server backend1.example.com:8080 weight=2;
    server backend2.example.com:8080 weight=1;
}

# Least Time (Nginx Plus)
upstream fastest_backend {
    least_time header;
    server backend1.example.com:8080;
    server backend2.example.com:8080;
}
```

### Health checks и failover
```nginx
# Passive health checks
upstream backend {
    server backend1.example.com:8080 max_fails=3 fail_timeout=30s;
    server backend2.example.com:8080 max_fails=3 fail_timeout=30s;
    server backup1.example.com:8080 backup;  # Резервный сервер
    server backup2.example.com:8080 backup;
}

# Active health checks (Nginx Plus)
upstream backend {
    zone backend 64k;
    server backend1.example.com:8080;
    server backend2.example.com:8080;

    health_check interval=5s fails=3 passes=2 uri=/health
                  match=health_check;
}

# Health check match
match health_check {
    status 200;
    header Content-Type = text/plain;
    body ~ "healthy";
}

# Sticky sessions
upstream backend {
    sticky cookie srv_id expires=1h domain=.example.com path=/;
    server backend1.example.com:8080;
    server backend2.example.com:8080;
}
```

## Кэширование и оптимизация

### HTTP caching
```nginx
# Proxy caching
proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:10m
                 max_size=10g inactive=60m use_temp_path=off;

server {
    listen 80;
    server_name api.example.com;

    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;

        # Кэширование
        proxy_cache api_cache;
        proxy_cache_valid 200 302 10m;
        proxy_cache_valid 404 1m;
        proxy_cache_valid any 1m;
        proxy_cache_use_stale error timeout invalid_header updating;

        # Cache key
        proxy_cache_key "$scheme$request_method$host$request_uri";

        # Cache bypass
        proxy_cache_bypass $http_upgrade $http_x_force_refresh;

        # Cache lock
        proxy_cache_lock on;
        proxy_cache_lock_timeout 5s;
    }
}
```

### FastCGI caching
```nginx
# FastCGI cache для PHP/Python приложений
fastcgi_cache_path /var/cache/nginx/fastcgi levels=1:2 keys_zone=fastcgi_cache:10m
                  max_size=1g inactive=60m;

server {
    listen 80;
    server_name php-app.example.com;

    location ~ \.php$ {
        fastcgi_pass php_backend;
        fastcgi_index index.php;

        # FastCGI cache
        fastcgi_cache fastcgi_cache;
        fastcgi_cache_valid 200 301 302 10m;
        fastcgi_cache_valid 404 1m;
        fastcgi_cache_key "$scheme$request_method$host$request_uri";
        fastcgi_cache_use_stale error timeout invalid_header updating;

        # Cache bypass
        fastcgi_cache_bypass $no_cache $http_upgrade;
        fastcgi_no_cache $no_cache;

        # Cache methods
        fastcgi_cache_methods GET HEAD;
    }
}
```

### Microcaching
```nginx
# Microcaching для динамического контента
proxy_cache_path /tmp/nginx-cache levels=1:2 keys_zone=microcache:10m
                 max_size=1g inactive=1m use_temp_path=off;

server {
    location /api/dynamic/ {
        proxy_pass http://backend;

        # Microcaching - кэширование на 1 секунду
        proxy_cache microcache;
        proxy_cache_valid 200 1s;
        proxy_cache_key "$scheme$request_method$host$request_uri$is_args$args";
        proxy_cache_use_stale updating;

        # Не кэшировать для аутентифицированных пользователей
        proxy_cache_bypass $cookie_session $http_authorization;
    }
}
```

## Security hardening

### SSL/TLS конфигурация
```nginx
# Современная SSL конфигурация
server {
    listen 443 ssl http2;
    server_name secure.example.com;

    # SSL сертификаты
    ssl_certificate /etc/ssl/certs/example.com.crt;
    ssl_certificate_key /etc/ssl/private/example.com.key;
    ssl_trusted_certificate /etc/ssl/certs/ca-bundle.crt;

    # SSL/TLS протоколы и шифры
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-CHACHA20-POLY1305;
    ssl_prefer_server_ciphers off;

    # HSTS
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload" always;

    # Security headers
    add_header X-Frame-Options DENY always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # OCSP Stapling
    ssl_stapling on;
    ssl_stapling_verify on;
    resolver 8.8.8.8 8.8.4.4 valid=300s;
    resolver_timeout 5s;

    # Session tickets
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    ssl_session_tickets off;

    location / {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Rate limiting и DDoS защита
```nginx
# Rate limiting zones
limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
limit_req_zone $binary_remote_addr zone=login:10m rate=3r/m;
limit_req_zone $binary_remote_addr zone=upload:10m rate=1r/m;

# Connection limiting
limit_conn_zone $binary_remote_addr zone=conn_limit_per_ip:10m;
limit_conn conn_limit_per_ip 10;

# Geo blocking
geo $blocked_country {
    default 0;
    RU 1;  # Блокировка России
    CN 1;  # Блокировка Китая
}

map $blocked_country $block_message {
    1 "Access denied from your country";
    0 "";
}

server {
    listen 80;
    server_name api.example.com;

    # Geo blocking
    if ($blocked_country) {
        return 403 $block_message;
    }

    # Rate limiting
    location /api/ {
        limit_req zone=api burst=20 nodelay;

        proxy_pass http://backend;
        proxy_set_header Host $host;
    }

    location /login/ {
        limit_req zone=login burst=5 nodelay;

        proxy_pass http://auth_backend;
    }

    location /upload/ {
        limit_req zone=upload burst=1 nodelay;
        client_max_body_size 100m;

        proxy_pass http://upload_backend;
    }
}
```

### WAF (`Web Application Firewall`)
```nginx
# ModSecurity integration
load_module modules/ngx_http_modsecurity_module.so;

http {
    modsecurity on;
    modsecurity_rules_file /etc/nginx/modsec/main.conf;
}

server {
    listen 80;
    server_name app.example.com;

    location / {
        modsecurity_rules '
            SecRule REQUEST_URI "@contains /admin" "id:1001,phase:1,t:lowercase,deny,status:403,msg:\'Admin access denied\'"
            SecRule ARGS "@contains <script>" "id:1002,phase:2,t:lowercase,deny,status:403,msg:\'XSS attempt blocked\'"
            SecRule REQUEST_HEADERS:User-Agent "@pm apachebench wget curl" "id:1003,phase:1,t:lowercase,deny,status:403,msg:\'Bot detected\'"
        ';

        proxy_pass http://backend;
    }
}
```

## API Gateway паттерны

### API versioning и routing
```nginx
# API Gateway с версионированием
map $request_uri $api_version {
    ~^/api/v1/(.*) v1;
    ~^/api/v2/(.*) v2;
    default legacy;
}

upstream api_v1 {
    server api-v1-1:8080;
    server api-v1-2:8080;
}

upstream api_v2 {
    server api-v2-1:8080;
    server api-v2-2:8080;
}

server {
    listen 80;
    server_name api.example.com;

    # API versioning
    location ~ ^/api/v1/(.*) {
        proxy_pass http://api_v1;
        proxy_set_header X-API-Version v1;
        proxy_set_header X-Original-URI $request_uri;
    }

    location ~ ^/api/v2/(.*) {
        proxy_pass http://api_v2;
        proxy_set_header X-API-Version v2;
        proxy_set_header X-Original-URI $request_uri;
    }

    # Legacy API
    location /api/ {
        proxy_pass http://legacy_api;
        proxy_set_header X-API-Version legacy;
    }

    # Health checks
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

### Circuit breaker pattern
```nginx
# Circuit breaker с помощью Lua
lua_package_path "/etc/nginx/lua/?.lua;;";

lua_shared_dict circuit_breakers 10m;

init_worker_by_lua_block {
    local circuit_breaker = require "circuit_breaker"
    circuit_breaker.init()
}

server {
    location /api/ {
        access_by_lua_block {
            local circuit_breaker = require "circuit_breaker"
            if not circuit_breaker.allow_request("api_backend") then
                ngx.exit(ngx.HTTP_SERVICE_UNAVAILABLE)
            end
        }

        proxy_pass http://backend;
        proxy_next_upstream error timeout http_500 http_502 http_503 http_504;

        # Circuit breaker logic
        body_filter_by_lua_block {
            local circuit_breaker = require "circuit_breaker"
            circuit_breaker.record_result("api_backend", ngx.status)
        }
    }
}
```

### API rate limiting per user
```nginx
# Redis для distributed rate limiting
lua_shared_dict locks 1m;

lua_package_path "/etc/nginx/lua/?.lua;;";

init_by_lua_block {
    local redis = require "resty.redis"
    redis.add_commands("incr", "expire")
}

server {
    location /api/ {
        access_by_lua_block {
            local redis = require "resty.redis"
            local red = redis:new()

            red:set_timeout(1000)
            local ok, err = red:connect("127.0.0.1", 6379)
            if not ok then
                ngx.log(ngx.ERR, "failed to connect to redis: ", err)
                ngx.exit(ngx.HTTP_INTERNAL_SERVER_ERROR)
            end

            -- Rate limiting per user (API key or user ID)
            local user_key = ngx.var.http_x_api_key or ngx.var.remote_addr
            local key = "rate_limit:" .. user_key

            local current = red:incr(key)
            if current == 1 then
                red:expire(key, 60)  -- 1 minute window
            end

            if current > 100 then  -- 100 requests per minute
                ngx.exit(ngx.HTTP_TOO_MANY_REQUESTS)
            end

            red:set_keepalive(10000, 100)
        }

        proxy_pass http://backend;
    }
}
```

## Microservices паттерны

### Service mesh с Nginx
```nginx
# Service discovery через DNS
upstream auth-service {
    server auth-service.default.svc.cluster.local:8080 resolve;
    server auth-service-staging.default.svc.cluster.local:8080 resolve backup;
}

upstream user-service {
    server user-service.default.svc.cluster.local:8080 resolve;
    server user-service-staging.default.svc.cluster.local:8080 resolve backup;
}

# Circuit breaker через health checks
server {
    location /auth/ {
        proxy_pass http://auth-service;
        proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
        proxy_next_upstream_timeout 10s;
        proxy_next_upstream_tries 3;
    }
}

# Distributed tracing headers
server {
    location /api/ {
        # Добавление tracing headers
        proxy_set_header X-Request-ID $request_id;
        proxy_set_header X-B3-TraceId $request_id;
        proxy_set_header X-B3-SpanId $request_id;
        proxy_set_header X-B3-ParentSpanId $request_id;

        proxy_pass http://api-service;
    }
}
```

### Blue-Green deployment
```nginx
# Blue-Green deployment configuration
upstream blue_backend {
    server blue-app-1:8080;
    server blue-app-2:8080;
}

upstream green_backend {
    server green-app-1:8080;
    server green-app-2:8080;
}

# Traffic switching via variable
map $cookie_env $backend {
    blue blue_backend;
    default green_backend;
}

server {
    listen 80;
    server_name app.example.com;

    location / {
        proxy_pass http://$backend;

        # Health check for canary releases
        health_check uri=/health interval=5s;
    }

    # Admin endpoint for switching
    location /switch {
        allow 10.0.0.0/8;
        deny all;

        content_by_lua_block {
            local backend = ngx.var.arg_backend
            if backend == "blue" or backend == "green" then
                -- Switch backend (in shared memory)
                ngx.shared.backends:set("active", backend)
                ngx.say("Switched to " .. backend)
            else
                ngx.exit(ngx.HTTP_BAD_REQUEST)
            end
        }
    }
}
```

## Performance optimization

### TCP optimizations
```nginx
# TCP optimizations
server {
    listen 80 default_server reuseport backlog=65535;
    server_name _;

    # TCP optimizations
    tcp_nopush on;
    tcp_nodelay on;

    # Socket options
    so_keepalive on;
    so_keepalive_requests 100;
    so_keepalive_timeout 60s;

    location / {
        # FastCGI optimizations
        fastcgi_buffering on;
        fastcgi_buffer_size 128k;
        fastcgi_buffers 256 16k;
        fastcgi_busy_buffers_size 256k;
        fastcgi_temp_file_write_size 256k;
        fastcgi_max_temp_file_size 0;

        fastcgi_pass unix:/var/run/php-fpm.sock;
    }
}
```

### Memory и CPU optimization
```nginx
# Worker process optimizations
worker_processes auto;
worker_cpu_affinity auto;

# Worker connection optimizations
events {
    worker_connections 4096;
    use epoll;
    multi_accept on;
}

http {
    # Buffer optimizations
    client_body_buffer_size 128k;
    client_header_buffer_size 1k;
    large_client_header_buffers 4 8k;

    # Output buffers
    output_buffers 1 32k;
    postpone_output 1460;

    # File operations
    sendfile on;
    sendfile_max_chunk 512k;
    aio threads;

    # Direct IO for large files
    directio 4m;
    directio_alignment 512;

    # Gzip optimizations
    gzip_comp_level 6;
    gzip_buffers 16 8k;
    gzip_http_version 1.1;
}
```

### Monitoring и metrics
```nginx
# Stub status module
server {
    listen 8080;
    server_name status.example.com;

    location /nginx_status {
        stub_status on;
        access_log off;
        allow 10.0.0.0/8;
        deny all;
    }
}

# Prometheus metrics (nginx-lua-prometheus)
lua_shared_dict prometheus_metrics 10M;

lua_package_path "/etc/nginx/lua/?.lua;;";

init_worker_by_lua_block {
    prometheus = require("prometheus").init("prometheus_metrics")
    metric_requests = prometheus:counter("nginx_http_requests_total", "Number of HTTP requests", {"host", "status"})
    metric_latency = prometheus:histogram("nginx_http_request_duration_seconds", "HTTP request latency", {"host"})
}

log_by_lua_block {
    metric_requests:inc(1, {ngx.var.host, ngx.var.status})
    metric_latency:observe(ngx.now() - ngx.req.start_time(), {ngx.var.host})
}

server {
    listen 9145;
    server_name metrics.example.com;

    location /metrics {
        content_by_lua_block {
            prometheus:collect()
        }
    }
}
```

## Enterprise patterns

### Multi-tenant architecture
```nginx
# Multi-tenant configuration
map $host $tenant_id {
    tenant1.example.com tenant1;
    tenant2.example.com tenant2;
    default default;
}

map $tenant_id $db_host {
    tenant1 db1.example.com;
    tenant2 db2.example.com;
    default db-default.example.com;
}

server {
    listen 80;
    server_name ~^(?<tenant>.+)\.example\.com$;

    location /api/ {
        # Tenant-specific routing
        proxy_pass http://backend-$tenant;
        proxy_set_header X-Tenant-ID $tenant;
        proxy_set_header X-Database-Host $db_host;

        # Rate limiting per tenant
        limit_req zone=tenant:$binary_remote_addr rate=10r/s burst=20;
    }

    # Tenant-specific static files
    location /static/ {
        alias /var/www/$tenant/static/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

### Content delivery optimization
```nginx
# CDN-like behavior
server {
    listen 80;
    server_name cdn.example.com;

    # Static content optimization
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        add_header X-Content-Type-Options nosniff;

        # WebP conversion if supported
        location ~* \.(jpg|jpeg|png)$ {
            try_files $uri$webp_suffix $uri =404;
            add_header Vary Accept;
        }

        # Gzip compression
        gzip_static on;
        gunzip on;
    }

    # API proxy with caching
    location /api/ {
        proxy_pass http://api_backend;
        proxy_cache api_cache;
        proxy_cache_valid 200 10m;
        proxy_cache_valid 404 1m;

        # CORS headers
        add_header Access-Control-Allow-Origin *;
        add_header Access-Control-Allow-Methods "GET, POST, OPTIONS";
        add_header Access-Control-Allow-Headers "Content-Type, Authorization";
    }
}
```

## Решение проблем

### Debug logging
```nginx
# Debug logging configuration
error_log /var/log/nginx/error.log debug;

http {
    # Debug для конкретных соединений
    debug_connection 192.168.1.100;  # IP для debug
    debug_connection 10.0.0.0/8;     # Subnet для debug

    server {
        location /debug {
            # Echo module for debugging
            echo "Request URI: $request_uri";
            echo "Request method: $request_method";
            echo "Remote addr: $remote_addr";
            echo "Args: $args";
            echo "Headers:";
            echo -n "Host: "; echo $host;
            echo -n "User-Agent: "; echo $http_user_agent;
        }
    }
}
```

### Performance monitoring
```bash
# Nginx status monitoring
curl http://localhost:8080/nginx_status

# Active connections: 1
# server accepts handled requests
#  10 10 10
# Reading: 0 Writing: 1 Waiting: 0

# Connection statistics
ss -tlnp | grep :80
netstat -tlnp | grep :80

# Worker process monitoring
ps aux | grep nginx
top -p $(pgrep -f nginx | tr '\n' ',' | sed 's/,$//')

# Cache statistics
du -sh /var/cache/nginx/
find /var/cache/nginx/ -type f | wc -l

# Log analysis
tail -f /var/log/nginx/access.log | awk '{print $9}' | sort | uniq -c | sort -nr

# Memory usage
smem -k -P nginx
```

### Common issues resolution
```nginx
# 499 Client Closed Request
server {
    location / {
        proxy_connect_timeout 10s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
        proxy_ignore_client_abort on;  # Игнорировать закрытие соединения клиентом
    }
}

# 502 Bad Gateway
server {
    location / {
        proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
        proxy_next_upstream_timeout 10s;
        proxy_next_upstream_tries 3;
        proxy_connect_timeout 5s;
        proxy_send_timeout 10s;
        proxy_read_timeout 10s;
    }
}

# 504 Gateway Timeout
server {
    location / {
        proxy_connect_timeout 10s;
        proxy_send_timeout 60s;    # Увеличение таймаута отправки
        proxy_read_timeout 60s;    # Увеличение таймаута чтения
        fastcgi_read_timeout 60s;  # Для FastCGI
    }
}

# Memory issues
http {
    # Ограничение памяти на worker
    worker_rlimit_core 500m;
    working_directory /tmp;

    # Буферы для предотвращения OOM
    client_body_buffer_size 128k;
    client_max_body_size 50m;
}
```

## Лучшие практики

- **Worker processes и connections:** `worker_processes auto`; `worker_connections` с учётом лимитов ОС (`ulimit -n`); `worker_rlimit_nofile` не меньше `worker_connections`.
- **Буферы и таймауты:** настройте `client_body_buffer_size`, `proxy_buffer_size`, `proxy_connect_timeout` под размер запросов/ответов и латентность бэкендов; избегайте избыточных буферов (OOM).
- **Безопасность:** скрывайте версию **Nginx**; ограничивайте методы и размер тела; используйте **rate limiting** и **WAF** при необходимости; **TLS** для всех публичных сервисов.
- **Кэширование:** задавайте `proxy_cache_path` и ключи кэша; используйте `proxy_cache_valid` и условное кэширование; не кэшируйте персональные данные без учёта ключа.
- **Мониторинг:** логируйте `request_time`, `upstream_*`; используйте **stub_status** или **OpenResty**/**nginx-plus** для метрик; интегрируйте с **Prometheus**/**Grafana**.
## См. также
- [[docker-basics|Docker]] — контейнеризация
- [[kubernetes-basics|Kubernetes]] — оркестрация контейнеров
- [[terraform-basics|Terraform]] — **Infrastructure as Code**
- [[prometheus|Prometheus]] — мониторинг
