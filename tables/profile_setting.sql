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

--CREATE INDEX IF NOT EXISTS idx_profile_settings ON profile_settings (status, is_verified, is_deleted,profile_id);