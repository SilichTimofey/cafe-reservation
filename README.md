# Система бронирования столиков в кафе

REST API для управления залом кафе, бронированием столиков, отзывами,импортом данных.  
Проект реализован на **Spring Boot 3** (Java 17) с использованием **PostgreSQL**, **JWT-аутентификации** и **Swagger UI** для интерактивного тестирования.

Задание:
1. Создать систему бронирования столиков к кафе.
2. Реализовать Импорт данных: `POST /api/entities/import` — загрузка CSV-файла для первоначального наполнения зала (только формат `.csv`).

---

### Технологический стек

* **Java 17**
* **Spring Boot 3.3.5**
* **Spring Web** (REST API)
* **Spring Data JPA / Hibernate** (ORM)
* **PostgreSQL** (База данных)
* **Spring Security / JWT** (jjwt 0.12 для авторизации)
* **Jakarta Bean Validation** (Валидация данных)
* **Apache Commons CSV 1.12** (Парсинг файлов)
* **SpringDoc OpenAPI 2.6** (Swagger UI для документации)
* **Lombok / Maven** (Инструменты сборки и кодогенерации)


## Ключевой функционал

### Беспарольная аутентификация и регистрация по номеру телефона

- Единый эндпоинт `POST /api/auth/login` выполняет **вход и автоматическую регистрацию**: если пользователь с таким номером не найден, он создаётся в БД.
- Пароль не используется — идентификация по номеру телефона и JWT-токену.
- Строгая валидация белорусских мобильных номеров: формат `+375XXXXXXXXX`, допустимые коды операторов **25, 29, 33, 44**.

### Ролевая модель доступа (Public, USER, ADMIN)

| Уровень | Описание |
|---|---|
| **Public** | Доступ без токена: просмотр столиков, свободных мест, отзывов; вход в систему |
| **USER** | Аутентифицированный пользователь: бронирование, просмотр и отмена своих броней, создание отзывов |
| **ADMIN** | Администратор: CRUD столиков, импорт CSV |

Роли хранятся в сущности `User` (`USER` / `ADMIN`). При первом входе всегда назначается `USER`. Роль `ADMIN` назначается вручную в БД.

## Назначение роли ADMIN (для демонстрации)

При первом входе пользователю всегда назначается роль `USER`. Для тестирования ADMIN-функций:

1. Выполните `POST /api/auth/login` с нужным номером телефона.
2. В PostgreSQL выполните:

```sql
UPDATE users SET role = 'ADMIN' WHERE phone_number = '+375291234567';
```

3. Повторите `POST /api/auth/login` — новый JWT будет содержать `ROLE_ADMIN`.
4. Используйте этот токен в Swagger для импорта CSV и управления столиками.

### Управление столиками и поиск свободных мест

- CRUD для столиков с полями: номер, вместимость, признак **VIP**.
- `GET /api/tables/available` — умный поиск: возвращает столики, у которых `capacity >= guests` и которые **не заняты** активной бронью (`CONFIRMED`) на указанные дату и время.

### Жизненный цикл бронирования

- Два статуса: **`CONFIRMED`** (активная бронь) и **`CANCELLED`** (отменена).
- Новая бронь сразу создаётся со статусом `CONFIRMED`.
- Защита от двойного бронирования: проверка занятости слота только по активным (`CONFIRMED`) броням.

### Система отзывов

- Публичный просмотр всех отзывов (`GET /api/reviews`).
- Создание отзыва (`POST /api/reviews`) — только для авторизованных пользователей.
- Автор отзыва берётся из **JWT** (`@AuthenticationPrincipal`), а не из тела запроса — клиент не может подделать `userId`.
- Рейтинг: от 1 до 5.

### Импорт данных из CSV

- Эндпоинт `POST /api/entities/import` (только ADMIN).
- Формат: `multipart/form-data`, поле `file` с расширением `.csv`.
- Ожидаемые колонки: `tableNumber`, `capacity`, `isVip`.
- Построчная валидация с **частичным успехом**: корректные строки сохраняются, ошибочные пропускаются.
- Ответ `ImportResult`: `totalRows`, `imported`, `rejected`, список ошибок по номерам строк.
- Дубликаты по `tableNumber` отклоняются с сообщением `Duplicate tableNumber`.

Пример файла: [`tables.csv`](tables.csv) или [`docs/sample-tables.csv`](docs/sample-tables.csv).

---

## Архитектура

Проект построен по **слоистой архитектуре**:

```
controller  →  HTTP-слой: валидация запросов, делегирование в сервисы
service     →  бизнес-логика, транзакции, проверка прав доступа
repository  →  Spring Data JPA
model       →  JPA-сущности (не покидают сервисный слой)
dto         →  контракты запросов/ответов (records)
mapper      →  преобразование entity ↔ DTO
security    →  JWT-фильтр, SecurityConfig
exception   →  единый формат ошибок (ApiError)
```

---

## Спецификация API

Всего **15 эндпоинтов**. Для защищённых методов передайте JWT в заголовке:

```
Authorization: Bearer <accessToken>
```

### AuthController — `/api/auth`

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| `POST` | `/api/auth/login` | Public | Вход / регистрация по телефону. Возвращает JWT-токен |

**Тело запроса (`LoginRequest`):**
```json
{
  "phoneNumber": "+375291234567",
  "name": "Иван"
}
```
---

### CafeTableController — `/api/tables`

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| `GET` | `/api/tables` | Public | Список всех столиков |
| `GET` | `/api/tables/available?date=&time=&guests=` | Public | Свободные столики на дату/время с учётом вместимости |
| `GET` | `/api/tables/{id}` | Public | Столик по ID |
| `POST` | `/api/tables` | ADMIN | Создание столика |
| `PUT` | `/api/tables/{id}` | ADMIN | Обновление столика |
| `DELETE` | `/api/tables/{id}` | ADMIN | Удаление столика |

**Параметры `/available`:**
- `date` — дата в формате `YYYY-MM-DD` (например, `2026-06-15`)
- `time` — время в формате `HH:mm` (например, `19:00`)
- `guests` — количество гостей (целое число)

---

### ReservationController — `/api/reservations`

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| `GET` | `/api/reservations/my` | USER | Активные (`CONFIRMED`) брони текущего пользователя |
| `GET` | `/api/reservations/{id}` | USER (владелец) / ADMIN | Бронь по ID с проверкой владельца |
| `POST` | `/api/reservations` | USER | Создание брони (статус сразу `CONFIRMED`) |
| `POST` | `/api/reservations/{id}/cancel` | USER (владелец) / ADMIN | Отмена брони (статус → `CANCELLED`) |

**Тело запроса на создание (`ReservationRequestDTO`):**
```json
{
  "tableId": 1,
  "guestsCount": 2,
  "reservationDate": "2026-07-01",
  "reservationTime": "19:00"
}
```

---

### ReviewController — `/api/reviews`

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| `GET` | `/api/reviews` | Public | Список всех отзывов |
| `POST` | `/api/reviews` | USER | Создание отзыва (автор из JWT) |

**Тело запроса (`ReviewRequestDTO`):**
```json
{
  "rating": 5,
  "comment": "Отличное место, уютная атмосфера"
}
```

---

### ImportController — `/api/entities`

| Метод | Путь | Доступ | Описание |
|---|---|---|---|
| `POST` | `/api/entities/import` | ADMIN | Импорт столиков из CSV-файла (`multipart/form-data`, поле `file`) |

**Формат CSV:**

```csv
tableNumber,capacity,isVip
T1,2,false
T2,4,false
T3,6,true
```

**Пример ответа (`ImportResult`):**
```json
{
  "totalRows": 3,
  "imported": 2,
  "rejected": 1,
  "errors": [
    { "rowNumber": 4, "reason": "Duplicate tableNumber: T1" }
  ]
}
```

---

### Все GET-методы (6 штук)

| № | Метод | Путь | Доступ |
|---|---|---|---|
| 1 | `GET` | `/api/tables` | Public |
| 2 | `GET` | `/api/tables/available` | Public |
| 3 | `GET` | `/api/tables/{id}` | Public |
| 4 | `GET` | `/api/reviews` | Public |
| 5 | `GET` | `/api/reservations/my` | USER |
| 6 | `GET` | `/api/reservations/{id}` | USER / ADMIN |

---

## Запуск приложения

### Общие требования

- **JDK 17+**
- **Maven 3.9+**
- **PostgreSQL 14+** (локально или в Docker-контейнере)

> Spring Boot-приложение всегда запускается локально через Maven. Docker используется только для PostgreSQL.

При первом запуске Hibernate автоматически создаёт таблицы (`ddl-auto: update`).

### Переменные окружения (опционально)

| Переменная | Значение по умолчанию | Описание |
|---|---|---|
| `DB_HOST` | `localhost` | Хост PostgreSQL |
| `DB_PORT` | `5432` | Порт |
| `DB_NAME` | `cafe_reservation` | Имя БД |
| `DB_USER` | `postgres` | Пользователь БД |
| `DB_PASSWORD` | `1111` | Пароль БД |
| `JWT_SECRET` | (dev-ключ в `application.yml`) | Base64-ключ для подписи JWT |

---

### Вариант 1 — с Docker (PostgreSQL в контейнере)

**1. Клонировать репозиторий**

```bash
git clone https://github.com/SilichTimofey/cafe-reservation.git
cd cafe-reservation
```

**2. Запустить PostgreSQL в Docker**

```bash
docker run --name cafe-db -e POSTGRES_DB=cafe_reservation -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=1111 -p 5432:5432 -d postgres:16
```

> Пароль `1111` совпадает с настройками по умолчанию в `application.yml`.

**3. Запустить приложение**

```bash
mvn clean spring-boot:run
```

**Полезные команды Docker:**

```bash
docker start cafe-db   # запустить уже созданный контейнер
docker stop cafe-db    # остановить
docker rm cafe-db      # удалить контейнер
```

---

### Вариант 2 — локально без Docker

**1. Установить PostgreSQL**

Скачать и установить с https://www.postgresql.org/download/  
При установке задать пароль пользователя `postgres` (рекомендуется `1111` — как в `application.yml`).

**2. Создать базу данных**

Через pgAdmin или `psql`:

```sql
CREATE DATABASE cafe_reservation;
```

**3. Клонировать репозиторий (если ещё не клонировали)**

```bash
git clone https://github.com/SilichTimofey/cafe-reservation.git
cd cafe-reservation
```

**4. Настроить пароль (если отличается от `1111`)**

PowerShell:

```powershell
$env:DB_PASSWORD="ваш_пароль"
```

**5. Запустить приложение**

```bash
mvn clean spring-boot:run
```

---

### Проверка работы

Приложение доступно по адресу **http://localhost:8080**.

**Swagger UI:** http://localhost:8080/swagger-ui.html

Для защищённых эндпоинтов:
1. Выполните `POST /api/auth/login` и скопируйте `accessToken`.
2. Нажмите **Authorize** в Swagger UI.
3. Введите токен (без префикса `Bearer` — Swagger добавит его автоматически).

**Быстрый тест:**

```json
POST /api/auth/login
{
  "phoneNumber": "+375291234567",
  "name": "Тест"
}
```

Если при старте ошибка подключения к БД — проверьте, что PostgreSQL запущен и пароль совпадает с `DB_PASSWORD`.

---

## Назначение роли ADMIN (для демонстрации)

При первом входе пользователю всегда назначается роль `USER`. Для тестирования ADMIN-функций:

1. Выполните `POST /api/auth/login` с нужным номером телефона.
2. В PostgreSQL выполните:

```sql
UPDATE users SET role = 'ADMIN' WHERE phone_number = '+375291234567';
```

3. Повторите `POST /api/auth/login` — новый JWT будет содержать `ROLE_ADMIN`.
4. Используйте этот токен в Swagger для импорта CSV и управления столиками.

---

## Сценарий демонстрации (краткий)

1. **Public:** `GET /api/tables`, `GET /api/reviews` — без токена.
2. **Регистрация:** `POST /api/auth/login` → получить JWT.
3. **USER:** `GET /api/tables/available` → `POST /api/reservations` → `GET /api/reservations/my` → `POST /api/reviews`.
4. **ADMIN:** назначить роль в БД → перелогиниться → `POST /api/entities/import` (файл `tables.csv`) → `POST /api/tables`.


