-- US-S13: a merchant registers their shop, US-A01: the ShopLoc administrator approves it.
-- The three shops created by V1 are already partners: they become APPROVED.
ALTER TABLE shops ADD COLUMN description      VARCHAR(1000);
ALTER TABLE shops ADD COLUMN owner_account_id BIGINT;          -- account of the merchant, owned by service-accounts
ALTER TABLE shops ADD COLUMN status           VARCHAR(20) NOT NULL DEFAULT 'APPROVED';
ALTER TABLE shops ADD COLUMN created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- From now on, a new shop waits for the administrator
ALTER TABLE shops ALTER COLUMN status SET DEFAULT 'PENDING';
ALTER TABLE shops ADD CONSTRAINT shops_status_valid CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));

CREATE INDEX idx_shops_status ON shops (status);
