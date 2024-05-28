SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for cross
-- ----------------------------
DROP TABLE IF EXISTS `crosschain`;
CREATE TABLE `crosschain` (
    `cross_id` BIGINT(200) PRIMARY KEY AUTO_INCREMENT NOT NULL,

    `cross_from` BIGINT(255) NOT NULL,
    `cross_to` BIGINT(255) NOT NULL,
    `cross_param` BIGINT(255) NOT NULL COMMENT'参数',

    `cross_type` tinyint(1) NOT NULL COMMENT'跨链类型',
    `cross_result` tinyint(1) NOT NULL COMMENT'交易结果',
    `cross_time` DATETIME NOT NULL COMMENT'交易时间'

)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跨链信息表';
