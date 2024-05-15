SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `user_id` bigint(200) NOT NULL AUTO_INCREMENT COMMENT '用户唯一注册序号',
    `user_name` varchar(100) NOT NULL COMMENT '用户名',
    `user_pswd` varchar(100) NOT NULL COMMENT '密码',
    `token` varchar(255) COMMENT '用户登陆token',
    `authority` tinyint(2) NOT NULL COMMENT'用户权限，0为国外加工运输企业，1为国内运输营销企业，2为农场主',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户个人信息表';