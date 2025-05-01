-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1) PROFILE SERVICE: customer profiles
CREATE TABLE IF NOT EXISTS profiles (
                                        id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username      VARCHAR(50) UNIQUE NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20) NOT NULL,        -- e.g. 'CUSTOMER', 'ADMIN'
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

-- 2) STORE OF VALUE SERVICE: bank accounts
CREATE TABLE IF NOT EXISTS accounts (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id    UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    balance       NUMERIC(19,4) NOT NULL DEFAULT 0.00,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- e.g. ACTIVE, SUSPENDED
    account_type   VARCHAR(50) NOT NULL DEFAULT 'WALLET',  -- e.g. WALLET, SAVINGS, CURRENT
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

-- 3) PAYMENT SERVICE: transactions (top-ups, withdrawals, transfers)
CREATE TABLE IF NOT EXISTS transactions (
                                            id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id       UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    type             VARCHAR(20) NOT NULL,    -- 'TOPUP', 'WITHDRAWAL', 'TRANSFER'
    amount           NUMERIC(19,4) NOT NULL CHECK (amount > 0),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    related_account  UUID,                    -- for transfers, the other account
    CONSTRAINT fk_related_account FOREIGN KEY (related_account) REFERENCES accounts(id)
    );

-- 4) EVENTS SERVICE: notifications after payments
CREATE TABLE IF NOT EXISTS events (
                                      id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    profile_id     UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    event_type     VARCHAR(50) NOT NULL,      -- e.g. 'EMAIL_SENT', 'SMS_SENT'
    payload        JSONB,                     -- store notification details
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

-- Indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_profiles_username ON profiles(username);
CREATE INDEX IF NOT EXISTS idx_accounts_profile ON accounts(profile_id);
CREATE INDEX IF NOT EXISTS idx_transactions_account ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_events_profile ON events(profile_id);
