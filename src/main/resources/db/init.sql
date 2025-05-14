-- Таблица пользователей
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL
);

-- Таблица подписок
CREATE TABLE subscriptions (
                               id SERIAL PRIMARY KEY,
                               user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               service_name VARCHAR(100) NOT NULL,
                               start_date DATE DEFAULT CURRENT_DATE
);

-- Индексы
CREATE INDEX idx_user_id ON subscriptions(user_id);
CREATE INDEX idx_service_name ON subscriptions(service_name);
