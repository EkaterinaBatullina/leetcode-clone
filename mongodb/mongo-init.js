db = db.getSiblingDB('notification_service_db');

db.createUser({
    user: 'ekaterina',
    pwd: 'ekaterina',
    roles: [
        { role: 'readWrite', db: 'notification_service_db' },
        { role: 'dbAdmin', db: 'notification_service_db' }
    ]
});