Необходимо реализовать REST API, которое взаимодействует с файловым хранилищем AWS S3 и предоставляет возможность получать доступ к файлам и истории загрузок. Логика безопасности должна быть реализована средствами JWT токена. Приложение должно быть докеризировано и готового к развертыванию в виде Docker контейнера.
Сущности:
User (List<Event> events,  Status status, …)
Event (User user, File file, Status status)
File (id, location, Status status ...)
User -> … List<Events> events ...
Взаимодействие с S3 должно быть реализовано с помощью AWS SDK.
Уровни доступа:
ADMIN - полный доступ к приложению
MODERATOR - права USER + чтение всех User + чтение/изменение/удаление всех Events + чтение/изменение/удаление всех Files
USER - только чтение всех своих данных + загрузка файлов для себя

Технологии: Java, MySQL, Spring (IoC, Data, Security), AWS SDK, MySQL, Docker, JUnit, Mockito, Gradle.

# Запуск maven clean package с application-local.yml : `mvn clean package -Dspring.profiles.active=local`

# Запуск maven run с application-local.yml : `mvn spring-boot:run -Dspring-boot.run.profiles=local`


# Для создания docker:

# -image : `docker build -t spring-boot-rest-api-v1 .`

# -container : `docker run -p 8080:8080 --name spring-boot-rest-api-v1 --restart always -d spring-boot-rest-api-v1`

# -stop container : `docker stop <ID container>`

# -remove container : `docker container rm <container_id>`

# -remove image : `docker image rm <image_id>`
