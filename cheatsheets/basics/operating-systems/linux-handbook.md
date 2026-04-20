---
title: "Linux и Bash: практический справочник"
description: "Рабочий справочник по Linux для backend/DevOps инженера: команды, права, процессы, сигналы, shell scripting, systemd, сеть и production troubleshooting."
tags:
  - basics
  - operating-systems
  - linux
  - bash
  - devops
difficulty: "intermediate"
prerequisites: ["operating-systems-basics.md"]
next: []
updated: "2026-04-20"
related:
  - "operating-systems-basics.md"
  - "../networks/network-protocols.md"
---
# Linux и Bash: практический справочник

Рабочий справочник по `Linux` для backend/DevOps инженера. Фокус — реальные задачи: диагностика, troubleshooting, systemd-сервисы, сеть, shell-скрипты. Для теории ядра и OS-концепций см. [[operating-systems-basics]].

## Полезные ссылки

### Официальная документация
- [The Linux man-pages project](https://www.kernel.org/doc/man-pages/) — man-страницы kernel API и утилит
- [GNU Bash Reference Manual](https://www.gnu.org/software/bash/manual/bash.html)
- [systemd documentation](https://www.freedesktop.org/wiki/Software/systemd/)
- [Filesystem Hierarchy Standard](https://refspecs.linuxfoundation.org/fhs.shtml)

### Учебные ресурсы
- [Linux Interview Questions — Baeldung](https://www.baeldung.com/linux/linux-interview-questions)
- [The Linux Documentation Project](https://tldp.org/)
- [ArchWiki](https://wiki.archlinux.org/) — один из лучших источников по настройке Linux

### См. также
- [[operating-systems-basics]] — теория OS, процессы, память, file systems
- [[linux-interview]] — вопросы на собеседовании по Linux
- [[docker-basics]] — контейнеры поверх Linux namespaces и cgroups
- [[kubernetes-basics]] — оркестрация Linux-хостов
- [[git-basics]] — git как Linux CLI

## Содержание

- [Базовая навигация](#базовая-навигация)
- [Файловая система и FHS](#файловая-система-и-fhs)
- [Права доступа](#права-доступа)
- [Пользователи и группы](#пользователи-и-группы)
- [Работа с процессами](#работа-с-процессами)
- [Сигналы](#сигналы)
- [Файловые дескрипторы и лимиты](#файловые-дескрипторы-и-лимиты)
- [Bash: основы скриптования](#bash-основы-скриптования)
- [Пайпы, редиректы, subshell](#пайпы-редиректы-subshell)
- [Поиск файлов и текста](#поиск-файлов-и-текста)
- [systemd](#systemd)
- [cron и systemd timers](#cron-и-systemd-timers)
- [Логи и journald](#логи-и-journald)
- [Сеть](#сеть)
- [Диск и файловые системы](#диск-и-файловые-системы)
- [Память](#память)
- [Производительность и профилирование](#производительность-и-профилирование)
- [Архивирование и передача файлов](#архивирование-и-передача-файлов)
- [Переменные окружения и конфиги shell](#переменные-окружения-и-конфиги-shell)
- [Типичные задачи на проде](#типичные-задачи-на-проде)

## Базовая навигация

| Команда | Назначение | Частые флаги |
|---|---|---|
| `pwd` | текущая директория | — |
| `cd` | сменить директорию | `cd -` (предыдущая), `cd ~` (домой) |
| `ls` | список файлов | `-la` подробно+скрытые, `-lh` human sizes, `-lt` сорт по времени |
| `tree` | дерево директорий | `-L 2` глубина |
| `cat` / `bat` | вывести файл | `bat` — с подсветкой |
| `less` | постраничный просмотр | `/` поиск, `G` конец, `q` выход |
| `head`, `tail` | первые/последние строки | `-n 50`, `tail -f` tail в real-time |
| `wc` | подсчёт строк/слов/байт | `-l` только строки |

```bash
ls -la --color=auto
tail -n 100 -f /var/log/app.log
```

## Файловая система и FHS

Стандартное дерево (Filesystem Hierarchy Standard):

| Путь | Назначение |
|---|---|
| `/` | root |
| `/bin`, `/usr/bin` | исполняемые пользовательские команды |
| `/sbin`, `/usr/sbin` | команды для администрирования |
| `/etc` | конфигурационные файлы системы |
| `/var` | изменяемые данные (`log`, `cache`, `lib`) |
| `/tmp` | временные файлы (могут очищаться при перезагрузке) |
| `/home` | домашние директории пользователей |
| `/opt` | опциональное ПО от вендоров |
| `/proc` | псевдо-FS, runtime-инфа о ядре и процессах |
| `/sys` | псевдо-FS, sysfs — устройства и kernel-параметры |
| `/dev` | файлы устройств (`/dev/null`, `/dev/sda`) |
| `/mnt`, `/media` | точки монтирования |

### Inode и ссылки

- Каждый файл описан **inode** — структурой с метаданными (права, владелец, размер, указатели на блоки). Имя файла хранится в directory entry, а не в inode.
- **Hard link** (`ln source link`) — ещё одна запись в директории, указывающая на тот же inode. Оригинал и копия равноправны, данные живут до удаления последней ссылки.
- **Symlink** (`ln -s target link`) — файл, содержащий путь к цели. Если цель удалить — симлинк становится битым.

```bash
stat file.txt         # метаданные файла, inode number
df -i                 # использование inode (может кончиться раньше места)
ls -li                # показать inode
```

Если `df -h` показывает свободное место, а запись падает с `No space left on device` — смотри `df -i`.

## Права доступа

Классическая модель — `rwx` для user/group/others:

Разбор строки `-rwxr-xr-- 1 user group 1024 Apr 20 12:00 file`:

- `-` — тип (`-` файл, `d` директория, `l` symlink)
- `rwx` — user
- `r-x` — group
- `r--` — others

Права для файла и директории интерпретируются по-разному:

| Бит | Файл | Директория |
|---|---|---|
| `r` | читать содержимое | листинг (`ls`) |
| `w` | изменять | создавать/удалять файлы |
| `x` | запускать | входить (`cd`), обращаться к файлам по имени |

**Octal:** `r=4, w=2, x=1`. Наборы: `7=rwx, 6=rw-, 5=r-x, 4=r--, 0=---`.

```bash
chmod 750 script.sh            # rwxr-x---
chmod u+x script.sh            # добавить execute для user
chmod -R g+w /var/app          # рекурсивно
chown app:devops file          # сменить владельца
chgrp devops file              # сменить группу
```

### Специальные биты

| Бит | Символ | На файле | На директории |
|---|---|---|---|
| `SUID` (4xxx) | `s` в user-x | запуск от имени owner (`passwd`) | — |
| `SGID` (2xxx) | `s` в group-x | запуск от имени group | новые файлы наследуют group директории |
| Sticky (1xxx) | `t` в other-x | — | удалять может только owner файла (`/tmp`) |

### umask

`umask` — маска, которая вычитается из default-прав при создании. Default для файла — `0666`, для директории — `0777`.

| umask | Новый файл | Новая директория |
|---|---|---|
| `022` | `644` | `755` |
| `027` | `640` | `750` |
| `077` | `600` | `700` |

Для сервисов с чувствительными данными — `umask 027` в unit-файле или `~/.bashrc`.

## Пользователи и группы

```bash
whoami                 # текущий пользователь
id                     # uid, gid, группы
groups <user>          # группы пользователя
who                    # кто залогинен
last                   # история логинов

useradd -m -s /bin/bash alice
passwd alice
usermod -aG docker alice    # добавить в группу docker (важно -a — append!)
userdel -r alice            # удалить с домашней директорией

getent passwd alice         # запись из /etc/passwd (с учётом LDAP/NSS)
```

Ключевые файлы:

- `/etc/passwd` — пользователи (не хранит пароли).
- `/etc/shadow` — хэши паролей (0600).
- `/etc/group` — группы.
- `/etc/sudoers` — sudo-политика (редактируется через `visudo`).

## Работа с процессами

| Команда | Что показывает |
|---|---|
| `ps aux` | все процессы (BSD-style) |
| `ps -ef` | все процессы (System-V style) |
| `top` | интерактивный монитор |
| `htop` | удобный top (нужно ставить) |
| `pgrep -af nginx` | PID по шаблону cmdline |
| `pidof nginx` | короткий pgrep |
| `pstree -p` | дерево процессов |
| `lsof -p <PID>` | открытые файлы процесса |
| `/proc/<pid>/` | `cmdline`, `status`, `limits`, `fd/` |

```bash
ps -eo pid,ppid,%cpu,%mem,comm --sort=-%mem | head     # top по памяти
ps -eLf | grep java | wc -l                            # число потоков Java
```

### Состояния процесса

| Код | Состояние |
|---|---|
| `R` | running / runnable |
| `S` | interruptible sleep (ждёт события) |
| `D` | uninterruptible sleep (ждёт IO — `kill` не работает) |
| `T` | stopped (SIGSTOP) |
| `Z` | zombie (exit, но родитель не вызвал `wait`) |

Если процесс в `D` — проблема с диском/NFS, нужен root-cause.

### Фон и job-control

```bash
long_task &           # запустить в фоне
jobs                  # список jobs текущего shell
fg %1                 # вывести job 1 на передний план
bg %1                 # продолжить в фоне
disown %1             # отвязать от shell, чтобы пережил выход
nohup ./script.sh &   # игнорировать SIGHUP при logout
```

Для стойкости к закрытию сессии используй `tmux` или `screen`.

## Сигналы

Сигнал — асинхронное уведомление процессу. Номера и поведение:

| № | Имя | Default | Можно поймать? | Использование |
|---|---|---|---|---|
| 1 | `SIGHUP` | terminate | да | перечитать конфиг (nginx) |
| 2 | `SIGINT` | terminate | да | `Ctrl+C` |
| 3 | `SIGQUIT` | core dump | да | `Ctrl+\`, thread dump JVM |
| 9 | `SIGKILL` | kill | нет | жёсткий kill |
| 15 | `SIGTERM` | terminate | да | graceful shutdown (по умолчанию `kill`) |
| 17 | `SIGCHLD` | ignore | да | умер child |
| 18 | `SIGCONT` | resume | да | возобновить остановленный |
| 19 | `SIGSTOP` | stop | нет | пауза |

```bash
kill 12345            # = SIGTERM
kill -HUP 12345       # перечитать конфиг
kill -9 12345         # SIGKILL
kill -l               # список сигналов
killall nginx         # по имени
pkill -f 'spring-boot' # по шаблону cmdline
```

**Правило:** сначала `SIGTERM`, жди 10–30 сек, если не вышел — `SIGKILL`. Прямой `-9` теряет данные (файлы не закроются, буферы не сбросятся).

## Файловые дескрипторы и лимиты

### fd 0/1/2

| fd | Поток |
|---|---|
| 0 | stdin |
| 1 | stdout |
| 2 | stderr |

```bash
ls /proc/<pid>/fd        # открытые fd процесса
lsof -p <pid> | wc -l    # количество
```

### Лимиты процесса (`ulimit`)

| Флаг | RLIMIT | Значение |
|---|---|---|
| `-n` | `NOFILE` | макс file descriptors |
| `-u` | `NPROC` | макс процессов/тредов |
| `-s` | `STACK` | размер стека (KB) |
| `-v` | `AS` | виртуальная память |
| `-l` | `MEMLOCK` | залоченная память |
| `-c` | `CORE` | размер core dump |

```bash
ulimit -a                     # все лимиты текущего shell
cat /proc/<pid>/limits        # лимиты конкретного процесса
```

Для сервисов выставляй в systemd:

```ini
LimitNOFILE=65536
LimitNPROC=4096
```

Для пользователей — `/etc/security/limits.conf`:

```text
app  soft  nofile  65536
app  hard  nofile  65536
```

**Боль:** «Too many open files» — первая проверка `ulimit -n`.

## Bash: основы скриптования

### Shebang и запуск

```bash
#!/usr/bin/env bash
# file: script.sh
```

```bash
chmod +x script.sh
./script.sh        # запуск в subshell
bash script.sh     # то же, но без +x
source script.sh   # выполнить в текущем shell (видны переменные)
```

### Безопасные настройки

```bash
#!/usr/bin/env bash
set -euo pipefail          # -e: fail-fast, -u: error on unset var, -o pipefail: fail при любой ошибке в pipe
IFS=$'\n\t'                # безопасный IFS
```

### Переменные

```bash
name="Alice"               # без пробелов вокруг =
echo "Hello, $name"
echo "Path: ${PATH}"
readonly PI=3.14           # константа
unset name                 # удалить

# default-значения
: "${LOG_DIR:=/var/log}"   # LOG_DIR=/var/log если не задана
: "${PORT:?PORT is required}"   # fail если не задана
```

### Специальные переменные

| Переменная | Значение |
|---|---|
| `$0` | имя скрипта |
| `$1` … `$9` | позиционные аргументы |
| `$#` | количество аргументов |
| `$@` | все аргументы (с кавычками — корректное разбиение) |
| `$*` | все аргументы как одна строка |
| `$?` | exit-code последней команды |
| `$$` | PID shell |
| `$!` | PID последнего фонового процесса |
| `$_` | последний аргумент предыдущей команды |

### Условия

```bash
if [[ "$var" == "prod" ]]; then
    echo "production"
elif [[ -z "$var" ]]; then
    echo "empty"
else
    echo "other"
fi

# арифметика
if (( count > 10 )); then ... ; fi
```

`[[ ... ]]` vs `[ ... ]`: `[[` — bash-built-in, поддерживает `==`, `=~` (regex), `&&`, `||`. `[` — POSIX, более ограниченный. **Всегда используй `[[`**.

Тесты файлов:

| Флаг | Значение |
|---|---|
| `-e file` | существует |
| `-f file` | обычный файл |
| `-d dir` | директория |
| `-r/w/x file` | права |
| `-s file` | не пустой |
| `file1 -nt file2` | file1 новее |

### Циклы

```bash
for i in 1 2 3; do echo $i; done
for f in *.log; do gzip "$f"; done
for ((i=0; i<10; i++)); do echo $i; done

while read -r line; do
    echo "$line"
done < input.txt

until condition; do ... ; done
```

### Функции

```bash
log() {
    local level="$1"
    shift
    echo "[$level] $*" >&2
}

log INFO "starting"
log ERROR "failed: $?"
```

`local` — обязателен, иначе переменная станет глобальной.

## Пайпы, редиректы, subshell

### Редиректы

| Синтаксис | Действие |
|---|---|
| `cmd > out` | stdout в файл (перезапись) |
| `cmd >> out` | stdout в файл (append) |
| `cmd < in` | stdin из файла |
| `cmd 2> err` | stderr в файл |
| `cmd > out 2>&1` | stdout+stderr в один файл |
| `cmd &> out` | то же (bash-сокращение) |
| `cmd > /dev/null` | отбросить вывод |
| `cmd <<< "string"` | here-string |
| `cmd <<EOF ... EOF` | heredoc (многострочный stdin) |

**Важно:** порядок редиректов слева направо. `cmd > out 2>&1` ≠ `cmd 2>&1 > out`.

### Пайп

```bash
cat /var/log/app.log | grep ERROR | awk '{print $4}' | sort | uniq -c | sort -rn | head
```

Каждая команда — отдельный процесс, stdout левого stdin правого. С `set -o pipefail` pipe упадёт при ошибке любой команды.

### Subshell vs source

| Что | Изменения переменных/cwd видны в родителе? |
|---|---|
| `./script.sh` / `bash script.sh` | нет (subshell) |
| `(cmd1; cmd2)` | нет (явный subshell) |
| `$(cmd)` / `` `cmd` `` | нет (command substitution) |
| `source script.sh` / `. script.sh` | да |
| `cmd1; cmd2` | да |
| `{ cmd1; cmd2; }` | да (group в текущем shell) |

### trap — cleanup при выходе

```bash
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
trap 'echo interrupted; exit 130' INT TERM

# ... использование $tmp ...
```

## Поиск файлов и текста

### find

```bash
find /var/log -name '*.log' -mtime -1 -size +1M      # свежее 1 дня, >1MB
find . -type f -name '*.java' -exec grep -l 'TODO' {} +
find / -type f -perm -u+s 2>/dev/null                # SUID-файлы
find . -type f -mtime +30 -delete                    # удалить старше 30 дней
find . -depth -name '*.bak' -print                   # depth-first
```

Типы: `f` файл, `d` директория, `l` symlink. `-mtime -N` = моложе N дней.

### grep / ripgrep

```bash
grep -rni 'ERROR' /var/log --include='*.log'         # recursive, ignore-case, line-numbers
grep -v 'DEBUG' app.log                              # инверсия
grep -A 3 -B 1 'Exception' app.log                   # контекст
grep -E 'ERROR|WARN' app.log                         # extended regex

rg 'ERROR' /var/log                                  # ripgrep — быстрее и умнее
rg -t java 'TODO'                                    # только .java
rg -g '!node_modules' 'require'                      # исключить
```

### awk, sed

```bash
awk '{print $1, $4}' access.log                       # колонки
awk '$9 == "500" {c++} END {print c}' access.log      # условие + агрегация
awk -F',' '{print $2}' data.csv                       # разделитель

sed -n '10,20p' file.txt                              # строки 10–20
sed -i.bak 's/old/new/g' file.txt                     # замена с бэкапом
sed '/^#/d' config                                    # удалить комментарии
```

### xargs

```bash
find . -name '*.tmp' | xargs rm                       # стандарт
find . -name '*.tmp' -print0 | xargs -0 rm            # с NUL-разделителем — правильно
ls *.log | xargs -I{} gzip {}                         # {} как placeholder
... | xargs -P 4 -n 1 cmd                             # 4 параллельно
```

**Правило:** всегда `-print0 | xargs -0` или `-exec ... +` — безопасно с пробелами в именах.

## systemd

`systemd` — PID 1 на большинстве современных дистрибутивов. Заменяет `SysV init`, управляет сервисами, таймерами, сокетами, монтированием.

### Управление сервисами

```bash
systemctl start nginx
systemctl stop nginx
systemctl restart nginx
systemctl reload nginx           # SIGHUP, без рестарта
systemctl enable nginx           # автозапуск
systemctl disable nginx
systemctl enable --now nginx     # enable + start
systemctl status nginx
systemctl list-units --type=service --state=failed
systemctl daemon-reload          # перечитать изменённые unit-файлы
```

### Unit-файл сервиса

`/etc/systemd/system/myapp.service`:

```ini
[Unit]
Description=My Spring Boot app
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=app
Group=app
WorkingDirectory=/opt/myapp
ExecStart=/usr/bin/java -jar /opt/myapp/app.jar
Restart=on-failure
RestartSec=5s
LimitNOFILE=65536
Environment="SPRING_PROFILES_ACTIVE=prod"
EnvironmentFile=-/etc/myapp/env
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

### Типы сервисов

| `Type=` | Когда |
|---|---|
| `simple` | процесс не демонизируется (дефолт) |
| `forking` | классический daemon с fork |
| `oneshot` | короткий скрипт |
| `notify` | процесс сообщает готовность через `sd_notify` |
| `exec` | как simple, но systemd ждёт `exec()` до «started» |

### Зависимости и порядок

- `After=` / `Before=` — порядок запуска (не зависимость).
- `Requires=` — жёсткая зависимость (если упадёт — упадёт и этот).
- `Wants=` — мягкая зависимость.
- `PartOf=` — рестарт вместе.

## cron и systemd timers

### cron

`crontab -e` пользовательский, `/etc/crontab` системный:

```cron
# min  hour  dom  mon  dow  command
  0    3     *    *    *    /opt/backup.sh
  */5  *     *    *    *    /opt/metrics.sh
  0    */2   *    *    *    /opt/cleanup.sh
```

Специальные:

- `@reboot` — при старте.
- `@daily`, `@hourly`, `@weekly`.

Логи — обычно в `/var/log/cron` или через syslog. Stdin/stdout cron-задач по умолчанию пусты — перенаправляй в файл: `>> /var/log/backup.log 2>&1`.

### systemd timer

Пара `.timer` + `.service`. Пример `/etc/systemd/system/backup.timer`:

```ini
[Unit]
Description=Daily backup

[Timer]
OnCalendar=*-*-* 03:00:00
Persistent=true
RandomizedDelaySec=300

[Install]
WantedBy=timers.target
```

`OnCalendar=` — календарные события, `OnBootSec=15min` — относительно boot, `OnUnitActiveSec=1h` — после предыдущего запуска.

Плюсы timer над cron:

- `Persistent=true` — выполнить пропущенное, если машина была выключена.
- Логи в journald.
- Resource-контроль (`MemoryMax`, `CPUQuota`).
- `RandomizedDelaySec` — разнести нагрузку.

```bash
systemctl list-timers --all
journalctl -u backup.service
```

## Логи и journald

### journalctl

```bash
journalctl -u nginx                              # логи юнита
journalctl -u nginx -f --since '10 min ago'      # follow с отсечкой
journalctl --since '2026-04-20 10:00' --until '2026-04-20 12:00'
journalctl -p err                                # только errors
journalctl -b                                    # с текущего boot
journalctl -b -1                                 # предыдущий boot
journalctl _PID=12345                            # по PID
journalctl --disk-usage
journalctl --vacuum-time=7d                      # чистка
```

Уровни (`-p`): `emerg`, `alert`, `crit`, `err`, `warning`, `notice`, `info`, `debug`.

### Традиционные логи

- `/var/log/syslog` или `/var/log/messages` — системные.
- `/var/log/auth.log` — авторизация/SSH.
- `/var/log/kern.log` — kernel.
- `/var/log/nginx/`, `/var/log/postgresql/` — сервисы.

### logrotate

`/etc/logrotate.d/myapp`:

```text
/var/log/myapp/*.log {
    daily
    rotate 14
    compress
    delaycompress
    missingok
    notifempty
    copytruncate
}
```

**Подводный камень:** если приложение держит fd на `.log`, но ротация его переименовала — пишет в удалённый файл. Используй `copytruncate` или `postrotate kill -HUP`.

## Сеть

### Интерфейсы и маршруты

```bash
ip addr                    # интерфейсы и адреса
ip link set eth0 up
ip route                   # таблица маршрутизации
ip route add default via 192.168.1.1
ip -s link                 # статистика по интерфейсам
ethtool eth0               # скорость/duplex/driver
```

### DNS

```bash
dig example.com            # подробный query
dig @8.8.8.8 example.com   # явный резолвер
dig -x 8.8.8.8             # reverse
getent hosts example.com   # как программы резолвят (NSS, /etc/hosts)
resolvectl status          # systemd-resolved
```

### Диагностика доступности

```bash
ping -c 4 host
traceroute host
mtr host                   # realtime traceroute + loss
nc -zv host 443            # проверить порт
curl -v telnet://host:443  # альтернатива
curl -I https://example.com  # только headers
```

### Слушающие порты и соединения

```bash
ss -tlnp                   # TCP listening, numeric, с процессами
ss -tunap                  # TCP+UDP, все, все процессы
ss -o state established    # активные соединения
ss -s                      # общая статистика

lsof -i :8080              # кто слушает порт 8080
lsof -i TCP:8080           # конкретно TCP
```

`netstat` устарел — используй `ss`.

### tcpdump

```bash
tcpdump -i any -n port 80
tcpdump -i eth0 -n host 10.0.0.1 and port 443
tcpdump -i any -w capture.pcap  # записать в pcap (открыть в Wireshark)
```

### Firewall

| Инструмент | Дистрибутив |
|---|---|
| `nftables` / `nft` | современный стандарт |
| `iptables` | legacy, часто alias к `iptables-nft` |
| `ufw` | Ubuntu, wrapper |
| `firewalld` | RHEL/Fedora, daemon + zones |

```bash
ufw status
ufw allow 22/tcp
ufw allow from 10.0.0.0/24 to any port 5432

nft list ruleset
```

## Диск и файловые системы

```bash
df -h                      # занятость по разделам
df -i                      # inode usage
du -h --max-depth=1 /var | sort -h
du -sh /path               # суммарно
ncdu /                     # интерактивный du

mount                      # все смонтированные FS
findmnt                    # иерархически
lsblk                      # блочные устройства
blkid                      # UUID разделов
fdisk -l                   # таблицы разделов
```

### /etc/fstab

```text
UUID=xxxx-xxxx  /data   ext4   defaults,noatime  0  2
/swapfile       none    swap   sw                0  0
```

Опции: `noatime` — не обновлять время доступа (меньше IO), `nodev`, `nosuid`, `nofail` (не падать при отсутствии).

### Типы FS

| FS | Заметка |
|---|---|
| `ext4` | default на многих дистрах, надёжный |
| `xfs` | хорош для больших файлов, RHEL-default |
| `btrfs` | snapshots, compression |
| `zfs` | снапшоты, L2ARC, dedup; не в mainline kernel |
| `tmpfs` | в RAM, `/dev/shm`, `/tmp` на некоторых дистрах |

## Память

```bash
free -h                    # общая картина
cat /proc/meminfo          # подробно
vmstat 1                   # поток по секундам
pmap -x <pid>              # разбивка памяти процесса
smem -r -k                 # с учётом shared

cat /proc/<pid>/status | grep -E 'Vm|Rss'
```

**Важно:** в Linux «свободная» память — `MemAvailable`, не `free`. Linux кэширует файлы в page cache — `free` маленький это нормально.

### OOM killer

При исчерпании памяти ядро убивает процесс по эвристике `oom_score`:

```bash
dmesg -T | grep -i 'killed process'
journalctl -k --since today | grep -i oom
cat /proc/<pid>/oom_score
cat /proc/<pid>/oom_score_adj       # -1000 = неубиваемый
```

В контейнерах: `OOMKilled` в Kubernetes = cgroup-лимит памяти исчерпан.

### Swap

```bash
swapon --show
free -h
sysctl vm.swappiness               # 0..100, 0 — избегать swap
```

Для сервисов с low-latency требованиями (БД, Kafka) swap часто отключают.

## Производительность и профилирование

### CPU

```bash
top / htop
mpstat -P ALL 1            # per-CPU
sar -u 1 5                 # CPU usage из sysstat
perf top                   # hot-функции ядра/userspace
perf record -p <pid> -- sleep 30 && perf report
```

### IO

```bash
iostat -xz 1
iotop -o                   # только процессы с IO
pidstat -d 1               # IO per-process
```

### Сеть

```bash
iftop                      # пропускная способность
nethogs                    # per-process traffic
sar -n DEV 1
```

### eBPF-инструменты

```bash
bpftrace -e 'tracepoint:syscalls:sys_enter_openat { @[comm] = count(); }'
bcc-tools: execsnoop, opensnoop, tcpconnect, biolatency, cachestat
```

### strace / ltrace

```bash
strace -p <pid> -f                          # все syscalls процесса+потомки
strace -e trace=openat,read,write -p <pid>  # фильтр
strace -c ./cmd                             # сводка по syscalls
ltrace -p <pid>                             # библиотечные вызовы
```

## Архивирование и передача файлов

```bash
tar -czf archive.tar.gz dir/               # create gzip
tar -xzf archive.tar.gz                    # extract
tar -tzf archive.tar.gz                    # list
tar --exclude='*.log' -czf a.tgz dir/

gzip file                                  # → file.gz
gunzip file.gz
zstd file                                  # быстрый современный compress
xz file                                    # лучшее сжатие, медленно

scp file user@host:/path
scp -r dir user@host:/path
rsync -avP src/ user@host:/dst/            # incremental, с progress
rsync --dry-run ...                        # проверить без реальной передачи

curl -O https://example.com/file.tgz       # скачать
curl -fsSL url | bash                      # fail-silent, follow, install-style
wget https://example.com/file.tgz
```

## Переменные окружения и конфиги shell

### Какой файл читается когда

| Контекст | Bash |
|---|---|
| Login shell (SSH, `su -`, tty) | `/etc/profile` `~/.bash_profile` `~/.bash_login` `~/.profile` |
| Interactive non-login (новый терминал) | `/etc/bash.bashrc` `~/.bashrc` |
| Non-interactive (`bash script.sh`) | `$BASH_ENV`, если задан |
| Logout | `~/.bash_logout` |

Обычно `~/.bash_profile` содержит `source ~/.bashrc`, чтобы оба типа сессий видели одинаковое окружение.

### Env variables

```bash
export JAVA_HOME=/opt/java17
export PATH=$JAVA_HOME/bin:$PATH

printenv                   # все env
printenv PATH
env -i bash                # чистое окружение
unset VAR
```

### Системные env

- `/etc/environment` — пары `KEY=VAL`, читается PAM при логине (не bash-скрипт).
- `/etc/profile.d/*.sh` — shell-скрипты, подцепляются `/etc/profile`.

## Типичные задачи на проде

### «Диск забит»

```bash
df -h                                          # где?
df -i                                          # может быть inode
du -h --max-depth=1 /var | sort -h             # кто?
find / -xdev -type f -size +500M 2>/dev/null
lsof +L1                                       # удалённые, но открытые файлы
```

Проверь, не держит ли приложение fd на ротированные логи — `lsof | grep deleted`.

### «Процесс съедает CPU/память»

```bash
ps -eo pid,ppid,%cpu,%mem,comm --sort=-%cpu | head
top -p $(pgrep -d, -f myapp)
pmap -x <pid>
cat /proc/<pid>/status
```

Для JVM: `jcmd <pid> GC.heap_info`, `jstack <pid>` или `jcmd <pid> Thread.print`.

### «Процесс завис в D»

Не убивается `kill -9`. Ищи root-cause:

```bash
cat /proc/<pid>/stack             # kernel-стек где именно застрял
cat /proc/<pid>/wchan             # wait channel
dmesg -T | tail                   # сообщения kernel
iostat -xz 1                      # проблема с диском?
```

Обычно — зависший NFS, больной диск, кривой драйвер. Лечится только перезагрузкой или восстановлением underlying ресурса.

### «Лог не крутится»

```bash
logrotate -d /etc/logrotate.d/myapp    # dry-run, показать что сделал бы
logrotate -f /etc/logrotate.d/myapp    # принудительно
```

После ротации, если приложение пишет в старый fd, добавь в conf `copytruncate` или `postrotate/endscript` с `kill -HUP`.

### «SSH не логинит»

```bash
ssh -vvv user@host                      # подробный лог на клиенте
tail -f /var/log/auth.log               # на сервере
sshd -T                                 # эффективный конфиг
systemctl status sshd
```

Проверь `/etc/ssh/sshd_config`: `PermitRootLogin`, `PasswordAuthentication`, `AllowUsers`. После правки — `systemctl reload sshd`.

### Сбор thread-dump JVM

```bash
jcmd <pid> Thread.print > thread-dump.txt
# или
kill -3 <pid>   # дамп уйдёт в stdout/stderr процесса (обычно в лог)
```

Для heap dump:

```bash
jcmd <pid> GC.heap_dump /tmp/heap.hprof
jmap -dump:live,format=b,file=/tmp/heap.hprof <pid>   # alt, legacy
```

### Проверить слушающий порт снаружи

```bash
# с клиентского хоста
nc -zv prod-host 5432
curl -I https://prod-host/health

# на сервере
ss -tlnp | grep 5432
```

### Траблшут скачка 5xx в nginx

```bash
tail -F /var/log/nginx/error.log
awk '$9 ~ /^5/ {print $7}' /var/log/nginx/access.log | sort | uniq -c | sort -rn | head
```

### Быстрый replace во многих файлах

```bash
grep -rln 'oldApi' src/ | xargs sed -i.bak 's/oldApi/newApi/g'
```

`grep -l` выдаёт только имена файлов, `xargs` подставляет в `sed -i`. `.bak` создаёт резервные копии.
