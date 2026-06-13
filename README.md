# Bank Cards REST API

Backend-приложение для управления банковскими картами: JWT-аутентификация, ролевой доступ, CRUD карт, пользовательский просмотр своих карт, запрос блокировки и переводы между собственными картами.

## Технологии

- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Liquibase
- Swagger UI / OpenAPI
- Docker Compose
- JUnit 5 / Mockito

## Быстрый запуск

```bash
docker compose up --build
```

Приложение будет доступно на `http://localhost:8080`.

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI-файл:

```text
docs/openapi.yaml
```

## Demo-пользователи

При первом запуске создаются два пользователя:

| Роль | Логин | Пароль |
| --- | --- | --- |
| ADMIN | `admin` | `admin12345` |
| USER | `user` | `user12345` |

В production-сценарии значения `JWT_SECRET` и `CARD_ENCRYPTION_KEY` нужно обязательно заменить через переменные окружения.

## Локальный запуск без Docker app-сервиса

Поднять только PostgreSQL:

```bash
docker compose up postgres
```

Запустить приложение:

```bash
mvn spring-boot:run
```

## Аутентификация

Получить JWT:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin12345"}'
```

Дальше передавать токен:

```text
Authorization: Bearer <token>
```

## Основные endpoints

### Auth

- `POST /api/auth/login` - получение JWT

### ADMIN

- `POST /api/admin/users` - создать пользователя
- `GET /api/admin/users` - список пользователей с поиском и пагинацией
- `GET /api/admin/users/{id}` - получить пользователя
- `PATCH /api/admin/users/{id}` - обновить пользователя
- `DELETE /api/admin/users/{id}` - удалить пользователя
- `POST /api/admin/cards` - создать карту
- `GET /api/admin/cards` - список всех карт с фильтрами
- `GET /api/admin/cards/{id}` - получить карту
- `PATCH /api/admin/cards/{id}/status` - изменить статус
- `PATCH /api/admin/cards/{id}/block` - заблокировать карту
- `PATCH /api/admin/cards/{id}/activate` - активировать карту
- `DELETE /api/admin/cards/{id}` - удалить карту

### USER

- `GET /api/cards` - список своих карт с поиском, фильтром статуса и пагинацией
- `GET /api/cards/{id}` - получить свою карту
- `GET /api/cards/{id}/balance` - посмотреть баланс
- `PATCH /api/cards/{id}/request-block` - запросить блокировку карты
- `POST /api/transfers` - перевод между своими активными картами

## Безопасность

- Полный номер карты не возвращается из API.
- Номер карты хранится в БД в зашифрованном виде через AES-GCM.
- Для уникальности номера используется SHA-256 hash.
- В API отображается только маска вида `**** **** **** 1234`.
- Переводы разрешены только между картами текущего пользователя.
- Административные endpoints доступны только роли `ADMIN`.

## Тесты

```bash
mvn test
```

Покрыта ключевая бизнес-логика переводов, маскирование и шифрование номеров карт.
