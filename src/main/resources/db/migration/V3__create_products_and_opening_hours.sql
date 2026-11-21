-- Socle M2: articles (US-S03, S04, S15, C07) and opening hours (US-S10, C05) of the shops.

CREATE TABLE products (
    id                  BIGSERIAL     PRIMARY KEY,
    shop_id             BIGINT        NOT NULL REFERENCES shops (id),
    name                VARCHAR(120)  NOT NULL,
    description         TEXT,
    price               NUMERIC(8, 2) NOT NULL,
    stock               INTEGER       NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER       NOT NULL DEFAULT 0,
    available           BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT products_price_positive        CHECK (price > 0),
    CONSTRAINT products_stock_not_negative    CHECK (stock >= 0),
    CONSTRAINT products_threshold_not_negative CHECK (low_stock_threshold >= 0)
);
CREATE INDEX idx_products_shop ON products (shop_id);

-- A day can have two slots (lunch break): no unique constraint on (shop_id, day_of_week).
-- day_of_week: 1 = Monday ... 7 = Sunday (ISO-8601).
CREATE TABLE opening_hours (
    id          BIGSERIAL PRIMARY KEY,
    shop_id     BIGINT    NOT NULL REFERENCES shops (id),
    day_of_week SMALLINT  NOT NULL,
    opens_at    TIME      NOT NULL,
    closes_at   TIME      NOT NULL,
    CONSTRAINT opening_hours_day_valid  CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT opening_hours_slot_valid CHECK (opens_at < closes_at)
);
CREATE INDEX idx_opening_hours_shop ON opening_hours (shop_id);

-- Demo data for the three partner shops created by V1 (matched by name, not by id).
INSERT INTO products (shop_id, name, description, price, stock, low_stock_threshold)
SELECT s.id, p.name, p.description, p.price, p.stock, p.low_stock_threshold
FROM shops s
JOIN (VALUES
    ('Le Fournil',         'Baguette tradition',          'Pétrie et cuite sur place chaque matin', 1.30, 40, 10),
    ('Le Fournil',         'Pain de campagne',            'Levain naturel, 500 g',                  3.20, 15,  5),
    ('Le Fournil',         'Croissant pur beurre',        NULL,                                     1.10, 30, 10),
    ('Boucherie Flamande', 'Carbonade flamande (500 g)',  'Prête à mijoter',                        9.90,  8,  3),
    ('Boucherie Flamande', 'Potjevleesch (300 g)',        'Spécialité flamande',                    7.80,  2,  3),
    ('Café des Sports',    'Café moulu (250 g)',          'Torréfié à Lille',                       5.50, 20,  5),
    ('Café des Sports',    'Gaufre au sucre',             NULL,                                     2.50,  0,  5)
) AS p (shop_name, name, description, price, stock, low_stock_threshold) ON p.shop_name = s.name;

INSERT INTO opening_hours (shop_id, day_of_week, opens_at, closes_at)
SELECT s.id, d.day, h.opens_at, h.closes_at
FROM shops s
JOIN (VALUES
    ('Le Fournil',         2, 6, TIME '07:00', TIME '13:00'),
    ('Le Fournil',         2, 6, TIME '15:00', TIME '19:30'),
    ('Le Fournil',         7, 7, TIME '07:00', TIME '13:00'),
    ('Boucherie Flamande', 2, 6, TIME '08:30', TIME '12:30'),
    ('Boucherie Flamande', 2, 6, TIME '14:30', TIME '19:00'),
    ('Café des Sports',    1, 6, TIME '08:00', TIME '20:00')
) AS h (shop_name, first_day, last_day, opens_at, closes_at) ON h.shop_name = s.name
CROSS JOIN LATERAL generate_series(h.first_day, h.last_day) AS d (day);
