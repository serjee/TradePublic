CREATE DATABASE tradestaff DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_tick`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_tick` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_m1`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_m1` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_m5`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_m5` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_m15`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_m15` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_m30`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_m30` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_h1`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_h1` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_h4`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_h4` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `tradestaff`.`td_d1`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_d1` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `tradestaff`.`td_w1`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_w1` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `tradestaff`.`td_mn`;
CREATE TABLE IF NOT EXISTS `tradestaff`.`td_mn` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `symbol` INT NOT NULL COMMENT 'symbol',
  `time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'time tick',
  `open` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'open',
  `high` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'high',
  `low` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'low',
  `close` DECIMAL(8,5) NOT NULL DEFAULT 0.00000 COMMENT 'close',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;