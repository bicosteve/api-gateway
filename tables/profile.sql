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

--CREATE INDEX IF NOT EXISTS idx_profile_phone ON profile (profile_id, phone_number);