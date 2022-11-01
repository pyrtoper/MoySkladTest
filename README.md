# Тестовое задание МойСклад

Использованные технологии: Spring Boot, PostgreSQL, Spring Data, JUnit 5, Mockito, Testcontainers, Spring Validation, Swagger, Apache Maven. 

Для запуска тестов нужен предустановленный Docker (для работы Testcontainers)
База данных PostgreSQL лежит в облаке Railway.app, необходимые параметры для доступа к ней лежат в файле application.properties, если нужно будет подключиться к ней извне. Скрипт с DDL командами лежит в src/test/resources/db/final_schema.sql (для тестов)

Варианты запуска приложения:

1. Получить все файлы из master ветки (например, скачать zip-архив с github) -> открыть папку с проектом с помощью Intellij IDEA Ultimate -> запустить MoySkladApplication.java. 
2. (Сборка с помощью Apache Maven) Получить все файлы из master ветки (например, скачать zip-архив с github) -> с помощью командной строки зайти в папку с проектом -> ввести команду mvn clean install (сбилдится проект, запустятся тесты) -> после прохождения тестов ввести команду mvn spring-boot:run. 
3. Скачать .jar файл по следующей ссылке: https://drive.google.com/drive/folders/1vcwcuwsNneNbuU0gpShWQ23P_4AB-Mkd?usp=sharing -> затем в командной строке ввести: java -jar <путь/до/jar-файла> src/main/java/com/moysklad/demo/MoySkladApplication.java

Приложение запустится на порту 8080, Swagger UI после запуска приложения будет доступен по ссылке: http://localhost:8080/swagger-ui/index.html

