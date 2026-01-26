# Docker: работа с контейнерами

Полный перенос раздела Docker Containers: список контейнеров, изучение файловой системы, передача переменных окружения, вход в оболочку, копирование файлов, получение контейнера через API, attach/detach, разница между expose и publish. Без сокращений.



**Дата последнего обновления:** 2026-01-11

## Полезные ссылки

### Официальная документация

- [Docker Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)

### Baeldung

- [Docker Tutorial](https://www.baeldung.com/ops/docker)


## Содержание
- [Список контейнеров](#список-контейнеров)
- [Изучение файловой системы контейнера](#изучение-файловой-системы-контейнера)
- [Передача переменных среды](#передача-переменных-среды)
- [Как попасть в оболочку контейнера](#как-попасть-в-оболочку-контейнера)
- [Копирование файлов в контейнеры и из них](#копирование-файлов-в-контейнеры-и-из-них)
- [Получение контейнера из API Docker Engine](#получение-контейнера-из-api-docker-engine)
- [Присоединение (attach) и отсоединение (detach)](#присоединение-attach-и-отсоединение-detach)
- [Разница между expose и publish](#разница-между-expose-и-publish)

## Список контейнеров

Чтобы вывести список контейнеров Docker, можно использовать `docker ps` или `docker container ls`. Все псевдонимы имеют одинаковые опции; рекомендуется новый синтаксис `docker container ls`.

Запущенные контейнеры:

```bash
docker container ls
```

Пример вывода:

```
CONTAINER ID   IMAGE                 COMMAND        CREATED          STATUS          PORTS                              NAMES
1addfea727b3   mysql:5.6             "docker-en.."  2 seconds ago    Up 1 second     0.0.0.0:32801->3306/tcp           dazzling_hellman
09c4105cb356   nats:2.1.0-scratch    "/nats-…"      17 minutes ago   Up 17 minutes   4222/tcp, 6222/tcp, 8222/tcp      nats-1
443fc0c41710   rabbitmq:3.7          "docker-…"     17 minutes ago   Up 17 minutes   4369/tcp,5671-5672/tcp,25672/tcp  rabbit-1
b06cfe3053e5   postgres:11           "docker-…"     29 minutes ago   Up 29 minutes   0.0.0.0:32789->5432/tcp           pg-2
4cf774b9e4a4   redis:5               "docker-…"     30 minutes ago   Up 30 minutes   0.0.0.0:32787->6379/tcp           redis-2
```

Колонки: CONTAINER ID, IMAGE:TAG, COMMAND, CREATED, STATUS, PORTS (маппинги вида `0.0.0.0:32789->5432/tcp`), NAMES.

Показать все (запущенные и остановленные):

```bash
docker container ls -a
```

Пример:

```
CONTAINER ID   IMAGE       STATUS
1addfea727b3   mysql:5.6   Up 4 hours
32928d81a65f   mysql:5.6   Exited (1) 4 hours ago
...
```

Последние n контейнеров:

```bash
docker container ls -n 2
docker container ls --latest
```

Полные значения без усечения:

```bash
docker container ls --latest --no-trunc
```

Только ID:

```bash
docker container ls -q
docker container ls --quiet --no-trunc
```

Удалить все контейнеры (осторожно):

```bash
docker container rm -f $(docker container ls -aq)
```

Размер контейнера и образа:

```bash
docker container ls --latest -s
```

Пример: `mysql:5.6 2B (virtual 256MB)` — 2B слой RW, 256MB размер образа.

Кастомный вывод (Go templates):

```bash
docker container ls --format "{{.ID}}-> Based on {{.Image}}, named {{.Names}}, ({{.Status}})"
docker container ls --format "table{{.ID}}\t{{.Image}}\t{{.Names}}"
```

Доступные плейсхолдеры: .ID, .Image, .Command, .CreatedAt, .Running, .Ports, .Status, .Size, .Names, .Labels, .Mounts, .Networks.

Фильтрация `--filter` (`-f`), формат `key=value`:

- По статусу: `docker container ls --filter "status=exited"`; `--filter "status=paused"`.
- Несколько фильтров: `--filter "status=exited" --filter "exited=1"`.
- По имени: `--filter "name=pg"`.
- По образу: `--filter "ancestor=postgres"`.
- По времени создания: `--filter "before=nats-1"` или `--filter "since=nats-1"`.

## Изучение файловой системы контейнера

Интерактивный запуск с оболочкой:

```bash
docker run -it alpine
/# ls -all
```

Если образ стартует не в shell (например, cassandra), можно переопределить команду:

```bash
docker run -it cassandra /bin/bash
```

Недостаток: приложение не стартует автоматически, надо запускать вручную.

Лучше подключаться к уже работающему контейнеру через `docker exec`:

```bash
docker run cassandra   # запустить в фоне/форграунд по умолчанию
docker ps              # взять CONTAINER ID
docker exec -it <id> /bin/bash   # или /bin/sh для Alpine
```

Пример для Alpine без bash:

```bash
docker exec -it 8408c85b3c57 /bin/sh
```

Если контейнер остановлен или без shell (hello-world), можно сделать дамп ФС:

```bash
docker run hello-world
docker ps -a   # взять ID остановленного контейнера
docker export -o hello.tar <id>
tar -tvf hello.tar
```

Копирование всей ФС через `docker cp`:

```bash
docker cp <id>:/ ./test
ls -all test/
```

## Передача переменных среды

Лучше отделять конфигурацию от кода (12-factor). Переменные окружения передаются через `-e/--env` или `--env-file`.

Передача пары ключ=значение:

```bash
docker pull alpine:3
docker run --env VARIABLE1=foobar alpine:3 env
# VARIABLE1=foobar
```

Использование значения из локальной среды:

```bash
export VARIABLE2=foobar2
docker run --env VARIABLE2 alpine:3 env
# VARIABLE2=foobar2
```

Файл со списком переменных (`key=value`):

```bash
echo VARIABLE1=foobar1 > my-env.txt
echo VARIABLE2=foobar2 >> my-env.txt
echo VARIABLE3=foobar3 >> my-env.txt
docker run --env-file my-env.txt alpine:3 env
```

Вывод:

```
VARIABLE1=foobar1
VARIABLE2=foobar2
VARIABLE3=foobar3
```

Безопасность: передавать секреты через командную строку — риск утечки (история shell, список процессов). Лучше из окружения или файла с ограничениями доступа. Любой, у кого есть доступ к Docker, может увидеть переменные через `docker inspect`:

```bash
docker inspect <id>
# "Env": ["VARIABLE1=foobar1", ...]
```

Для секретов использовать Docker Secrets или механизмы оркестраторов (Kubernetes Secrets, AWS/Azure).

## Как попасть в оболочку контейнера

Проверяем контейнеры:

```bash
docker ps -a
```

Пример вывода:

```
CONTAINER ID   IMAGE                 COMMAND                  CREATED          STATUS                    PORTS                    NAMES
a0f1b12edc3c   mysql                 "docker-entrypoint.s…"   5 minutes ago    Up 5 minutes              3306/tcp, 33060/tcp      mysql
bb5e34287020   nginxdemos/hello      "nginx -g 'daemon of…"   39 minutes ago   Up 5 minutes              0.0.0.0:8088->80/tcp     nginx
b8b17f1f5e28   hello-world           "/hello"                 10 minutes ago   Exited (0) 9 minutes ago                           flamboyant_golick
```

`hello-world` завершился, поэтому подключаться к нему нельзя. Подключимся к `nginxdemos/hello`.

Перед attach важно знать, что будет происходить при `Ctrl+C`/выходе: можно остановить контейнер или оставить работающим.

1) `docker attach` (старая форма `docker container attach`):

```bash
docker attach nginx
```

Остановка контейнера после выхода (можно отменить `Ctrl+C`):

```bash
docker stop nginx
```

2) `docker exec` — безопаснее для запуска shell без остановки сервиса:

```bash
docker exec -it nginx bash
# если bash недоступен — использовать /bin/sh
exit
```

3) Если shell недоступен (hello-world), использовать `docker cp` или `docker export` (см. выше).

## Копирование файлов в контейнеры и из них

`docker cp` работает похоже на `cp`/`scp`, принимает контейнер:путь и локальный путь.

Копирование в контейнер:

```bash
docker cp ./test.txt nginx:/usr/share/nginx/html/test.txt
```

Копирование из контейнера:

```bash
docker cp nginx:/usr/share/nginx/html/index.html ./index.html
```

Копирование всей ФС остановленного контейнера (пример hello-world):

```bash
docker cp a0af60c72d93:/ ./test
ls -all test/
```

## Получение контейнера из API Docker Engine

Нужно ID или имя контейнера. Берём через `docker ps` или `docker container ls`. Далее API `/containers/{id}/json`.

Если Docker слушает UNIX-сокет (по умолчанию), можно обращаться через curl с `--unix-socket`:

```bash
CID=$(docker ps -q | head -n1)
curl --unix-socket /var/run/docker.sock http://localhost/containers/$CID/json
```

Если включён TCP API (`-H tcp://0.0.0.0:2375` для незащищённого или HTTPS 2376), обращаемся по HTTP(S):

```bash
curl http://127.0.0.1:2375/containers/$CID/json
```

Пример структур (Config, State, NetworkSettings и т.д.) — использовать по назначению.

Безопасность: не открывать незашифрованный API наружу; использовать TLS, авторизацию, firewall.

## Присоединение (attach) и отсоединение (detach)

`docker attach` присоединяет STDIN/STDOUT/STDERR к основному процессу контейнера:

```bash
docker attach nginx
```

Обычный `Ctrl+C` может остановить процесс. Чтобы отсоединиться, не убивая контейнер, используйте escape-последовательность `Ctrl+P`, затем `Ctrl+Q`.

Если нужно просто войти в контейнер без рисков остановить основной процесс, используйте `docker exec -it <id> bash` / `sh`.

## Разница между expose и publish

В Dockerfile или compose:

- `EXPOSE <port>` — документирует/делает порт доступным другим контейнерам в одной сети, но не публикует на хост.
- Publish (`-p hostPort:containerPort` или `ports:` в compose) — пробрасывает порт на хост.

Пример publish:

```bash
docker run -d -p 8080:80 nginxdemos/hello
curl http://localhost:8080
```

Пример expose в compose:

```yaml
services:
  app:
    image: myapp
    expose:
      - "8080"
```

Здесь порт доступен другим контейнерам по имени сервиса `app:8080`, но не на хосте. Чтобы опубликовать наружу, использовать `ports: ["8080:8080"]`.

