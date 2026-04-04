CREATE TABLE IF NOT EXISTS profile(
    profile_id      BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    phone_number    VARCHAR(20) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_profile_phone (profile_id,phone_number),
    INDEX idx_profile_id(profile_id),
    INDEX idx_phone_number(phone_number)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS profile_settings(
    id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    status      TINYINT DEFAULT 0,
    is_verified TINYINT DEFAULT 0,
    is_deleted  TINYINT DEFAULT 0,
    profile_id  BIGINT UNSIGNED NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_settings_status(status),
    INDEX idx_settings_is_verified(is_verified),
    INDEX idx_settings_is_deleted(is_deleted),
    INDEX idx_profile_settings(status, is_verified, is_deleted,profile_id),
    FOREIGN KEY (profile_id) REFERENCES profile(profile_id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS bets(
    bet_id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    event_id        VARCHAR(255) NOT NULL,
    profile_id      BIGINT UNSIGNED NOT NULL,
    stake           DECIMAL(12,2) NOT NULL,
    is_bonus        TINYINT NOT NULL DEFAULT 0,
    status          TINYINT NOT NULL,
    total_odds      DECIMAL(12,2) CHECK (total_odds > 1.2),
    possible_win    DECIMAL(12,2) NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX           idx_bet_id(bet_id),
    INDEX           idx_event_id(event_id),
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
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (bet_id) REFERENCES bets(bet_id),
    FOREIGN KEY (event_id) REFERENCES bets(event_id),
    INDEX               idx_bet_slip_id(bet_slip_id),
    INDEX               idx_bet_id_bet_slips(bet_id),
    INDEX               idx_sport_id(sport_id),
    INDEX               idx_team_id(team_id),
    INDEX               idx_market_id(market_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;