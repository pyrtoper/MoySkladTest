# Тестовой задание МойСклад

Для запуска тестов нужен предустановленный Docker (для работы Testcontainers)

Варианты запуска:

1. Получить все файлы из finale ветки (например, скачать zip-архив с github) -> открыть папку с проектом с помощью Intellij IDEA Ultimate -> запустить MoySkladApplication. 
2. (Сборка с помощью Apache Maven) Получить все файлы из finale ветки (например, скачать zip-архив с github) -> с помощью командной строки зайти в папку с проектом -> ввести команду mvn clean install (запустятся тесты) -> после прохождения тестов ввести команду mvn spring-boot:run. 
3. Скачать .jar файл по следующей ссылке: https://drive.google.com/drive/folders/1vcwcuwsNneNbuU0gpShWQ23P_4AB-Mkd?usp=sharing -> затем в командной строке ввести: java -jar <путь/до/jar-файла> src/main/java/com/moysklad/demo/MoySkladApplication.java

Приложение запустится на порту 8082, Swagger UI после запуска приложения будет доступен по ссылке: http://localhost:8082/swagger-ui/index.html

