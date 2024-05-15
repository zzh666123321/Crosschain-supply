SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ox
-- ----------------------------
DROP TABLE IF EXISTS `ox`;
CREATE TABLE `ox` (
    `ox_id` bigint(200) NOT NULL AUTO_INCREMENT,
    `ox_key` VARCHAR(255) NOT NULL,
    `is_processed` TINYINT(1) DEFAULT 0,
    `breed` VARCHAR(255) NOT NULL,
    `end_time` DATETIME NOT NULL,
    `feeding_period` INT(100) NOT NULL,
    `weight` INT(100) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `feed_person` VARCHAR(255) NOT NULL,
    `transaction_id` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`ox_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牛信息表';