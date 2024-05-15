SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for beef
-- ----------------------------
DROP TABLE IF EXISTS `beef`;
CREATE TABLE beef (
    `beef_id` BIGINT(200) PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `beef_key` VARCHAR(255) NOT NULL,
    `ox_id` VARCHAR(255) NOT NULL,
    `breed` VARCHAR(255) NOT NULL,
    `process_time` DATETIME NOT NULL,
    `process_place` VARCHAR(255) NOT NULL,
    `process_person` VARCHAR(255) NOT NULL,
    `transport_time` DATETIME NOT NULL,
    `transport_place` VARCHAR(255) NOT NULL,
    `transport_person` VARCHAR(255) NOT NULL,
    `transfer_time` DATETIME,
    `transfer_place` VARCHAR(255),
    `transfer_person` VARCHAR(255),
    `sell_place` VARCHAR(255),
    `price` INT(100),
    `weight` INT(100),
    `quality_guarantee_time` DATETIME,
    `transaction_id_c0` VARCHAR(255) NOT NULL,
    `transaction_id_c1` VARCHAR(255)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牛肉信息表';
