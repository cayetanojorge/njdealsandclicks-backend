INSERT INTO subscription (plan_name, description, price, duration_in_days, max_emails_per_week, max_tracked_products, max_tracked_categories, is_active)
VALUES
    ('Free', 'Piano gratuito con funzionalità di base', 0.00, 30, 10, 10, 5, true),
    ('Premium', 'Piano premium con funzionalità avanzate', 9.99, 30, 50, 50, 25, true),
    ('Pro', 'Piano pro con tutte le funzionalità', 29.99, 30, 100, 100, 50, true);
