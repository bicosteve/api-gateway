CREATE TABLE IF NOT EXISTS profile(
    profile_id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    phone_number            VARCHAR(20) NOT NULL UNIQUE,
    password_hash           VARCHAR(255) NOT NULL,
    created_at              DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_at             DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_profile_phone (profile_id,phone_number),
    INDEX idx_profile_id(profile_id),
    INDEX idx_phone_number(phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS profile_settings(
    id                      BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    status                  TINYINT DEFAULT 0,
    is_verified             TINYINT DEFAULT 0,
    is_deleted              TINYINT DEFAULT 0,
    profile_id              BIGINT UNSIGNED NOT NULL,
    created_at              DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_at             DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_settings_status(status),
    INDEX idx_settings_is_verified(is_verified),
    INDEX idx_settings_is_deleted(is_deleted),
    INDEX idx_profile_settings(status, is_verified, is_deleted,profile_id),
    FOREIGN KEY (profile_id) REFERENCES profile(profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS bets(
    bet_id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    profile_id      BIGINT UNSIGNED NOT NULL,
    stake           DECIMAL(12,2) NOT NULL,
    is_bonus        TINYINT NOT NULL DEFAULT 0,
    status          TINYINT NOT NULL,
    total_odds      DECIMAL(12,2) CHECK (total_odds > 1.2),
    possible_win    DECIMAL(12,2) NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX           idx_bet_id(bet_id),
    INDEX           idx_profile_id(profile_id),
    INDEX           idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS bet_slips(
    bet_slip_id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    bet_id              BIGINT UNSIGNED NOT NULL,
    event_id            VARCHAR(255) NOT NULL,
    sport_id            INT UNSIGNED NOT NULL,
    team_id             INT UNSIGNED NOT NULL,
    market_id           INT UNSIGNED NOT NULL,
    market_name         VARCHAR(255) NOT NULL,
    participant_name    VARCHAR(255) NOT NULL,
    odds                DECIMAL(6,2) NOT NULL,
    special_bet_value   VARCHAR(255),
    status              INT UNSIGNED NOT NULL DEFAULT 1,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (bet_id) REFERENCES bets(bet_id),
    INDEX               idx_bet_slip_id(bet_slip_id),
    INDEX               idx_bet_id_bet_slips(bet_id),
    INDEX               idx_sport_id(sport_id),
    INDEX               idx_team_id(team_id),
    INDEX               idx_market_id(market_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS transactions (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_id          BIGINT UNSIGNED NOT NULL,
    reference           VARCHAR(100) NOT NULL UNIQUE,
    type                TINYINT NOT NULL DEFAULT 0 CHECK (type IN (0,1,2,3)),
    amount              DECIMAL(10, 2) NOT NULL,
    status              TINYINT NOT NULL DEFAULT 0 CHECK (status IN (0,1,2,3,4,5,7,8)),
    created_by          VARCHAR(100) NOT NULL,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profile(profile_id),
    INDEX idx_transaction_profile_id (profile_id),
    INDEX idx_transaction_type (type),
    INDEX idx_transaction_status (status),
    INDEX idx_transaction_created_at (created_at),
    INDEX idx_transaction_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS wallets(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_id          BIGINT UNSIGNED NOT NULL,
    balance             DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    bonus               DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_by          VARCHAR(100) NOT NULL,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE
    CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profile(profile_id),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0),
    INDEX idx_wallet_id (id),
    INDEX idx_wallet_profile_id (profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS deposits (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_id      BIGINT UNSIGNED NOT NULL,
    tx_ref          VARCHAR(100) NOT NULL UNIQUE,   -- your unique reference
    amount          DECIMAL(10,2) NOT NULL,
    currency        VARCHAR(10) DEFAULT 'ETB',
    checkout_url    TEXT,                           -- Chapa hosted page URL
    chapa_ref       VARCHAR(100),                   -- Chapa's own reference
    status          TINYINT DEFAULT 0,              -- 0=pending, 1=success, 2=failed
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profile(profile_id),
    INDEX idx_deposits_profile_id (profile_id),
    INDEX idx_deposits_tx_ref (tx_ref),
    INDEX idx_deposits_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS withdrawals (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_id      BIGINT UNSIGNED NOT NULL,
    tx_ref          VARCHAR(100) NOT NULL UNIQUE,
    amount          DECIMAL(10,2) NOT NULL,
    currency        VARCHAR(10) DEFAULT 'ETB',
    account_number  VARCHAR(20) NOT NULL,
    account_name    VARCHAR(100) NOT NULL,
    bank_code       VARCHAR(20),
    channel         VARCHAR(20) NOT NULL,           -- telebirr, mpesa, bank
    status          TINYINT DEFAULT 0,              -- 0=pending, 1=approved, 2=processing, 3=completed, 4=rejected, 5=failed
    chapa_ref       VARCHAR(100),
    reason          VARCHAR(255),                   -- rejection reason
    approved_by     BIGINT,                         -- admin profileId
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profile(profile_id),
    INDEX idx_withdrawals_profile_id (profile_id),
    INDEX idx_withdrawals_status (status),
    INDEX idx_withdrawals_tx_ref (tx_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;