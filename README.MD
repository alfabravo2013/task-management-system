# Система управления задачами

Реализация [тестового задания](https://docs.yandex.ru/docs/view?url=ya-disk-public%3A%2F%2FzSjJn%2BolC8zZAWTbGeRIkTBCYzscJ0AKViVuhL%2FU%2F6kHb7HLtcsOrKwkaYV9B9jHq%2FJ6bpmRyOJonT3VoXnDag%3D%3D%3A%2F%D0%A2%D0%97%20%D0%B4%D0%B6%D0%B0%D0%B2%D0%B0%20(1).docx&name=%D0%A2%D0%97%20%D0%B4%D0%B6%D0%B0%D0%B2%D0%B0%20(1).docx&nosw=1).

## Использованные инструменты:
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL 14.8
- Testcontainers для интеграционных тестов

## Запуск проекта

Среда разработки использует docker образ `postgres:14.8-alpine`. При желании его можно заменить на другой, отредактировав файл `compose.yml`.

Для запуска контейнера нужно в корневой директории проекта выполнить команду
```shell
docker compose up -d
```
Для запуска проекта нужно в корне проекта выполнить команду
```shell
./gradlew bootRun
```
Для прогона тестов следует выполнить команду
```shell
./gradlew clean test
```

Для остановки контейнера нужно выполнить команду
```shell
docker stop tms-pg
```
Данные контейнера `tms-pg` хранятся в каталоге `/pgdata`, чтобы удалить данные, нужно выполнить команду
```shell
sudo rm -f /pgdata
```
