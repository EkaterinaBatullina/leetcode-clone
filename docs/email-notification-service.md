# Демонстрация работы notification-service


# Шаг 1: Создание пароля приложения в Google Account

На этом шаге:
- создан специальный 16-значный пароль для SMTP, чтобы notification-service мог отправлять письма через Gmail. Он используется вместо основного пароля и не требует двухэтапной аутентификации.

![](screenshot/email-notification-screenshot/step1.png)


# Шаг 2: Пароль сгенерирован

На этом шаге:
- просмотр созданного пароля по имени notification-service.

![](screenshot/email-notification-screenshot/step2.png)


# Шаг 3: Регистрация пользователя через Swagger

На этом шаге:
- создан аккаунт пользователя, на email которого notification-service отправит уведомление.

![](screenshot/email-notification-screenshot/step3.png)


# Шаг 4: Получение access и refresh tokens для Bearer аутентификации

На этом шаге:
- получены токены для аутентификации.

![](screenshot/email-notification-screenshot/step4.png)


# Шаг 5: Получение email уведомления

На этом шаге:
- получено приветственное письмо ввиду регистрации пользователя.

![](screenshot/email-notification-screenshot/step5.png)


# Шаг 6: Примение access token для входа пользователя

На этом шаге:
- осуществлен логин пользователя.

![](screenshot/email-notification-screenshot/step6.png)


# Шаг 7: Просмотр информации авторизованного пользователя

На этом шаге:
- получен id пользователя для последующего обращения к notification-service для осуществления запроса к MongoDB для просмотра сохраненного уведомления текущего пользователя.

![](screenshot/email-notification-screenshot/step7.png)


# Шаг 8: Получение отправленного уведомления из базы

На этом шаге:
- выполнен запрос к MongoDB для получения уведомлений пользователя.
