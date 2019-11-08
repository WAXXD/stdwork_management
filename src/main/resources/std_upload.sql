/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : std_upload

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 08/11/2019 09:26:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `admin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员账户',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员密码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `admin_name_index`(`admin_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES ('1', 'admin', 'E10ADC3949BA59ABBE56E057F20F883E', '2019-11-04 16:15:34', '2019-11-04 16:34:09');
INSERT INTO `admin_user` VALUES ('2', 'root', '96E79218965EB72C92A549DD5A330112', '2019-11-04 16:15:54', NULL);

-- ----------------------------
-- Table structure for local_file_sys
-- ----------------------------
DROP TABLE IF EXISTS `local_file_sys`;
CREATE TABLE `local_file_sys`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `pid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父id',
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件存储在服务器上的名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件全路径',
  `parent_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级路径',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '1表示文件 0表示目录',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '覆盖时间',
  `level` tinyint(4) NULL DEFAULT NULL COMMENT '层级，第一级模块是1，  第二级模块是2， 上传文件夹是3',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `path`(`path`) USING BTREE,
  INDEX `ppath_path_frk`(`parent_path`) USING BTREE,
  INDEX `filename_idx`(`filename`) USING BTREE,
  CONSTRAINT `ppath_path_frk` FOREIGN KEY (`parent_path`) REFERENCES `local_file_sys` (`path`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of local_file_sys
-- ----------------------------
INSERT INTO `local_file_sys` VALUES ('1', NULL, NULL, 'E:/z04/', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `local_file_sys` VALUES ('53ddcb2120a34463a10a21d9ec46ddd7', '1', '釜式停留时间测定装置', 'E:/z04/釜式停留时间测定装置', 'E:/z04/', 0, '2019-11-08 09:17:56', NULL, 1);

-- ----------------------------
-- Table structure for std_account
-- ----------------------------
DROP TABLE IF EXISTS `std_account`;
CREATE TABLE `std_account`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学生姓名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `std_no` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `graduation_time` datetime(0) NULL DEFAULT NULL COMMENT '毕业时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `graduated` tinyint(1) NULL DEFAULT NULL COMMENT '1已毕业0未毕业',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `std_no_index`(`std_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of std_account
-- ----------------------------
INSERT INTO `std_account` VALUES ('0e53dd7e974742f6b572433df920cbbc', '1234', '6C30734811916B0F0F24A4630B08036F', '12343', '2019-12-13 00:00:00', '2019-11-06 16:23:02', NULL, 1);
INSERT INTO `std_account` VALUES ('24e6f613da134801b811e8e970c85948', '1234', '6C30734811916B0F0F24A4630B08036F', '1234', '2019-12-13 09:00:00', '2019-11-06 16:20:12', NULL, 1);
INSERT INTO `std_account` VALUES ('d1b6a8e77460468692fc135e20fbfa01', 'dda', '96E79218965EB72C92A549DD5A330112', '234324', '2019-09-11 00:00:00', '2019-11-05 16:23:50', NULL, 0);

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test`  (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test
-- ----------------------------
INSERT INTO `test` VALUES (31, '1324', '2019-11-01 15:46:00');
INSERT INTO `test` VALUES (32, 'klkl', NULL);
INSERT INTO `test` VALUES (33, 'klkl', NULL);
INSERT INTO `test` VALUES (34, 'klkl', NULL);
INSERT INTO `test` VALUES (35, 'klkl', NULL);
INSERT INTO `test` VALUES (36, 'klkl', NULL);
INSERT INTO `test` VALUES (37, 'klkl', NULL);
INSERT INTO `test` VALUES (38, 'klkl', NULL);
INSERT INTO `test` VALUES (39, 'klkl', NULL);
INSERT INTO `test` VALUES (40, 'klkl', NULL);
INSERT INTO `test` VALUES (41, 'klkl', NULL);

-- ----------------------------
-- Event structure for check_graduated_time
-- ----------------------------
DROP EVENT IF EXISTS `check_graduated_time`;
delimiter ;;
CREATE DEFINER = `root`@`%` EVENT `check_graduated_time`
ON SCHEDULE
EVERY '3' SECOND STARTS '2019-11-06 15:46:55'
DO update test set name='1324' where time < curdate()
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
