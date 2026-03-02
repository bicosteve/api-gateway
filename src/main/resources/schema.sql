CREATE TABLE IF NOT EXISTS `profile` (
    `profile_id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `phone_number` VARCHAR(20) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_profile_phone ON profile (profile_id, phone_number);

CREATE TABLE IF NOT EXISTS `profile_settings` (
    `id` BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `status` TINYINT DEFAULT 0,
    `is_verified` TINYINT DEFAULT 0,
    `is_deleted` TINYINT DEFAULT 0,
    `profile_id` BIGINT UNSIGNED NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_settings_status_verified_profile` (`status`, `is_verified`, `profile_id`),
    CONSTRAINT `fk_profile_settings_profile`
    FOREIGN KEY (`profile_id`)
    REFERENCES `profile`(`profile_id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_profile_settings ON profile_settings (status, is_verified, is_deleted,profile_id);