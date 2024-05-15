SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for cross
-- ----------------------------
DROP TABLE IF EXISTS `crosschain`;
CREATE TABLE `crosschain` (
    `cross_id` BIGINT(200) PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `ox_id` bigint(200) NOT NULL,
    `beef_key` VARCHAR(255) NOT NULL,
    `cross_from` VARCHAR(255) NOT NULL,
    `cross_to` VARCHAR(255) NOT NULL,
    `tx_from` VARCHAR(255) NOT NULL,
    `tx_to` VARCHAR(255) NOT NULL,
    `tx_back` VARCHAR(255) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跨链信息表';
