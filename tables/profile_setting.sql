CREATE TABLE IF NOT EXISTS `profile_settings` (
    `id` BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `status` INTEGER UNSIGNED DEFAULT 0,
    `is_verified` INTEGER UNSIGNED DEFAULT 0,
    `is_deleted` INTEGER UNSIGNED DEFAULT 0,
    `profile_id` BIGINT UNSIGNED NOT NULL,
     KEY `idx_settings_status_verified_profile` (`status`, `is_verified`, `profile_id`),
     CONSTRAINT `fk_profile_settings_profile`
     FOREIGN KEY (`profile_id`)
     REFERENCES `profileDto`(`profile_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_profile_settings ON profile_settings (status, is_verified, is_deleted,profile_id);