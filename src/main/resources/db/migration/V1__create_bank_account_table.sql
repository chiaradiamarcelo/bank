CREATE TABLE IF NOT EXISTS bank_account
(
    id          UUID primary key,
    balance     NUMERIC not null
);