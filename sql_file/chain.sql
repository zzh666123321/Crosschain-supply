SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ox
-- ----------------------------
DROP TABLE IF EXISTS `chain`;
CREATE TABLE `chain` (
    `chain_id` bigint(200) NOT NULL AUTO_INCREMENT,
    `chain_ip` VARCHAR(255) NOT NULL,
    `chain_status` TINYINT(2) DEFAULT 0,
    `chain_type` TINYINT(2) DEFAULT 0,
    `owner_id` VARCHAR(255) NOT NULL,


    PRIMARY KEY (`chain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链信息表';