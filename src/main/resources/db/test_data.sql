-- Тестовые данные для таблицы пользователей
INSERT INTO users (name, email) VALUES
    ('Иван Иванов', 'ivan@example.com'),
    ('Мария Петрова', 'maria@example.com'),
    ('Алексей Сидоров', 'alex@example.com'),
    ('Елена Смирнова', 'elena@example.com'),
    ('Дмитрий Козлов', 'dmitry@example.com');

-- Тестовые данные для таблицы подписок
INSERT INTO subscriptions (user_id, service_name, start_date) VALUES
    (1, 'Netflix', '2023-01-15'),
    (1, 'Spotify', '2023-02-20'),
    (2, 'Netflix', '2023-03-10'),
    (2, 'YouTube Premium', '2023-01-05'),
    (3, 'Spotify', '2023-02-15'),
    (3, 'Kinopoisk', '2023-04-01'),
    (4, 'Netflix', '2023-01-25'),
    (4, 'Amediateka', '2023-03-15'),
    (5, 'YouTube Premium', '2023-02-10'),
    (5, 'Spotify', '2023-04-05');