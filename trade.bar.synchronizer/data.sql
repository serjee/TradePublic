CREATE DATABASE strade DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `strade`.`si_user`;
CREATE TABLE IF NOT EXISTS `strade`.`si_user` (
  `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `email` VARCHAR(50) NOT NULL COMMENT 'Login (email)',
  `phone` VARCHAR(20) NULL COMMENT 'Phone',
  `password` CHAR(32) NOT NULL COMMENT 'Password',
  `salt` CHAR(32) NOT NULL COMMENT 'Secure Code',
  `balance` DECIMAL (8,2) NOT NULL DEFAULT 0.00 COMMENT 'Balance',
  `first_name` VARCHAR(20) NULL COMMENT 'First name',
  `second_name` VARCHAR(20) NULL COMMENT 'Second name',
  `role` ENUM('ADMIN','MODERATOR','USER') NOT NULL DEFAULT 'USER' COMMENT 'Role',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1 - active и 0 - no active',
  `last_ip` VARCHAR(15) NOT NULL DEFAULT '0' COMMENT 'IP address',
  `time_create` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `time_update` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Last Time',
  PRIMARY KEY (`id`),
  UNIQUE `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `strade`.`si_phonecheck`;
CREATE TABLE IF NOT EXISTS `strade`.`si_phonecheck` (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id INT(11) UNSIGNED NOT NULL COMMENT 'User ID',
  code VARCHAR(10) NOT NULL,
  status tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 - no sent и 1 - sent',
  checked tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 - no и 1 - yes',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
UNIQUE KEY `user_id` (`user_id`),
KEY `status` (`status`),
KEY `checked` (`checked`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/* Table for save signal from different monitors */
DROP TABLE IF EXISTS `strade`.`si_signal`;
CREATE TABLE IF NOT EXISTS `strade`.`si_signal` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `signal_id` INT UNSIGNED NOT NULL COMMENT 'signal id',
  `signal_type_id` SMALLINT UNSIGNED NOT NULL COMMENT 'signal type id',
  `symbol_id` TINYINT UNSIGNED NOT NULL COMMENT 'symbol id',
  `timeframe` SMALLINT UNSIGNED NOT NULL COMMENT 'time frame',
  `way` TINYINT NOT NULL COMMENT 'way',
  `confirm` TINYINT NOT NULL DEFAULT 0 COMMENT 'confirm',
  `bar_id` INT NOT NULL COMMENT 'barId link',
  `wr_pr` DECIMAL(6,2) NOT NULL DEFAULT -1.00 COMMENT 'winrate %',
  `wr_succ` INT NOT NULL DEFAULT -1 COMMENT 'winrate count success',
  `wr_fail` INT NOT NULL DEFAULT -1 COMMENT 'winrate count fail',
  `wr_exp` INT NOT NULL DEFAULT -1 COMMENT 'winrate expiration count',
  `result` TINYINT NOT NULL DEFAULT 0 COMMENT 'signal result', /* 1=true, -1=false */
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/* Table for save subscription from site (get using site api) */
DROP TABLE IF EXISTS `tradestaff`.`td_subscription`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_subscription` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` INT NOT NULL COMMENT 'user id',
  `signal_type` SMALLINT UNSIGNED NOT NULL COMMENT 'signal type', /* unique for user */
  `symbol` TINYINT UNSIGNED NOT NULL COMMENT 'symbol id', /* unique for user */
  `confirmed` TINYINT NOT NULL DEFAULT 0 COMMENT '1=confirmed',
  `timeframe` VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'timeframe by delimiter',
  `need_sms` TINYINT NOT NULL DEFAULT 0 COMMENT 'send sms',
  `need_email` TINYINT NOT NULL DEFAULT 0 COMMENT 'send email',
  `need_push` TINYINT NOT NULL DEFAULT 0 COMMENT 'send push',
  `min_winrate` TINYINT NOT NULL DEFAULT 0 COMMENT 'minimum winrate',
  `priority` TINYINT NOT NULL DEFAULT 0 COMMENT 'priority status for send sms',
  `start_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'start date of subscribing',
  `end_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'end date of subscribing',
  `payed` TINYINT NOT NULL DEFAULT 0 COMMENT '1=payed',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;