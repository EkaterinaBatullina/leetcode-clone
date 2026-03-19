# Демонстрация работы notification-service

Регистрация пользователя запускает асинхронный event-driven процесс:

## Системный поток 

Пользователь регистрируется через API → 
User Service создаёт пользователя →
User Service публикует событие в Kafka →
Notification Service потребляет событие →
Отправляется email-уведомление пользователю →
Уведомление сохраняется в MongoDB и доступно через API

# Шаг 1: Создание пароля приложения в Google Account

На этом шаге:
- создан специальный 16-значный пароль для SMTP для отправки notification-service писем через Gmail. Он используется вместо основного пароля, не требуя двухэтапной аутентификации.

![](screenshot/email-notification-screenshot/step1.png)

---

# Шаг 2: Пароль сгенерирован

На этом шаге:
- просмотрен созданный для notification-service пароль.

![](screenshot/email-notification-screenshot/step2.png)

---

# Шаг 3: Регистрация пользователя через Swagger

На этом шаге:
- создан аккаунт пользователя, на email которого notification-service отправит уведомление.

![](screenshot/email-notification-screenshot/step3.png)

---

# Шаг 4: Получение access и refresh tokens

На этом шаге:
- после успешной регистрации пользователя получены access и refresh tokens, используемые для Bearer-аутентификации при обращении к защищённым API.

![](screenshot/email-notification-screenshot/step4.png)

---

# Шаг 5: Получение email уведомления

На этом шаге:
- после регистрации пользователя автоматически отправлено приветственное email-уведомление;
- письмо доставлено на адрес зарегистрированного пользователя.

![](screenshot/email-notification-screenshot/step5.png)

---

# Шаг 6: Применение access token для входа пользователя

На этом шаге:
- скопирован полученный access token (Bearer) из ответа регистрации.

![](screenshot/email-notification-screenshot/step6.png)

---

# Шаг 7: Просмотр информации авторизованного пользователя

На этом шаге:
- получена информация о текущем пользователе с помощью access token;
- извлечён userId, использующийся для запроса уведомлений пользователя в notification-service (MongoDB).

![](screenshot/email-notification-screenshot/step7.png)

---

# Шаг 8: Получение отправленного уведомления из MongoDB

На этом шаге:
- выполнен GET-запрос к notification-service для получения уведомлений пользователя по его userId;
- получены username, email, статус отправки и дата создания.

![](screenshot/email-notification-screenshot/step8.png)