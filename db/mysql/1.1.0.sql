/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50561
Source Host           : localhost:3306
Source Database       : exam

Target Server Type    : MYSQL
Target Server Version : 50561
File Encoding         : 65001

Date: 2018-11-27 15:39:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for EXM_EXAM
-- ----------------------------
DROP TABLE IF EXISTS `EXM_EXAM`;
CREATE TABLE `EXM_EXAM` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `NAME` varchar(32) DEFAULT NULL COMMENT '名称',
  `PASS_SCORE` decimal(5,2) DEFAULT NULL COMMENT '及格分数',
  `DESCRIPTION` text COMMENT '描述',
  `START_TIME` datetime DEFAULT NULL COMMENT '开始时间',
  `END_TIME` datetime DEFAULT NULL COMMENT '结束时间',
  `MARK_START_TIME` datetime DEFAULT NULL COMMENT '判卷开始',
  `MARK_END_TIME` datetime DEFAULT NULL COMMENT '判卷结束',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：启用；2：禁用',
  `PAPER_ID` int(11) DEFAULT NULL COMMENT '试卷ID',
  `EXAM_TYPE_ID` int(11) DEFAULT NULL COMMENT '考试分类',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_19` (`PAPER_ID`),
  KEY `FK_Reference_22` (`EXAM_TYPE_ID`),
  CONSTRAINT `FK_Reference_22` FOREIGN KEY (`EXAM_TYPE_ID`) REFERENCES `EXM_EXAM_TYPE` (`ID`),
  CONSTRAINT `FK_Reference_19` FOREIGN KEY (`PAPER_ID`) REFERENCES `EXM_PAPER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_EXAM
-- ----------------------------

-- ----------------------------
-- Table structure for EXM_EXAM_TYPE
-- ----------------------------
DROP TABLE IF EXISTS `EXM_EXAM_TYPE`;
CREATE TABLE `EXM_EXAM_TYPE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `NAME` varchar(32) DEFAULT NULL COMMENT '名称',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父ID',
  `PARENT_SUB` varchar(512) DEFAULT NULL COMMENT '父子关系（格式：_父ID_子ID_子子ID_... ...）',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：正常',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  `USER_IDS` varchar(1024) DEFAULT NULL COMMENT '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
  `ORG_IDS` varchar(1024) DEFAULT NULL COMMENT '机构权限',
  `POST_IDS` varchar(1024) DEFAULT NULL COMMENT '岗位权限',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_EXAM_TYPE
-- ----------------------------
INSERT INTO `EXM_EXAM_TYPE` VALUES ('1', '考试分类', '0', '_1_', '1', '2017-08-01 22:31:43', '1', '1', null, null, null);

-- ----------------------------
-- Table structure for EXM_EXAM_USER
-- ----------------------------
DROP TABLE IF EXISTS `EXM_EXAM_USER`;
CREATE TABLE `EXM_EXAM_USER` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `EXAM_ID` int(11) DEFAULT NULL COMMENT '考试ID',
  `USER_ID` int(11) DEFAULT NULL COMMENT '用户ID',
  `UPDATE_USER_ID` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_MARK_USER_ID` int(11) DEFAULT NULL,
  `UPDATE_MARK_TIME` datetime DEFAULT NULL,
  `TOTAL_SCORE` decimal(5,2) DEFAULT NULL COMMENT '总分',
  `STATE` int(11) DEFAULT NULL COMMENT '1：未考试；2：考试中；3：已交卷；4：强制交卷；5：判卷中；6：未通过；7：已通过',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_23` (`EXAM_ID`),
  CONSTRAINT `FK_Reference_23` FOREIGN KEY (`EXAM_ID`) REFERENCES `EXM_EXAM` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_EXAM_USER
-- ----------------------------

-- ----------------------------
-- Table structure for EXM_EXAM_USER_QUESTION
-- ----------------------------
DROP TABLE IF EXISTS `EXM_EXAM_USER_QUESTION`;
CREATE TABLE `EXM_EXAM_USER_QUESTION` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `EXAM_USER_ID` int(11) DEFAULT NULL COMMENT '考试用户ID',
  `EXAM_ID` int(11) DEFAULT NULL COMMENT '考试ID',
  `USER_ID` int(11) DEFAULT NULL COMMENT '用户ID',
  `QUESTION_ID` int(11) DEFAULT NULL COMMENT '试题ID',
  `UPDATE_USER_ID` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_MARK_USER_ID` int(11) DEFAULT NULL,
  `UPDATE_MARK_TIME` datetime DEFAULT NULL,
  `ANSWER` text COMMENT '答案',
  `SCORE` decimal(5,2) DEFAULT NULL COMMENT '得分',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_24` (`EXAM_USER_ID`),
  KEY `FK_Reference_25` (`QUESTION_ID`),
  CONSTRAINT `FK_Reference_25` FOREIGN KEY (`QUESTION_ID`) REFERENCES `EXM_QUESTION` (`ID`),
  CONSTRAINT `FK_Reference_24` FOREIGN KEY (`EXAM_USER_ID`) REFERENCES `EXM_EXAM_USER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_EXAM_USER_QUESTION
-- ----------------------------

-- ----------------------------
-- Table structure for EXM_MARK_USER
-- ----------------------------
DROP TABLE IF EXISTS `EXM_MARK_USER`;
CREATE TABLE `EXM_MARK_USER` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `EXAM_ID` int(11) DEFAULT NULL COMMENT '考试ID',
  `USER_ID` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_37` (`EXAM_ID`),
  CONSTRAINT `FK_Reference_37` FOREIGN KEY (`EXAM_ID`) REFERENCES `EXM_EXAM` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_MARK_USER
-- ----------------------------

-- ----------------------------
-- Table structure for EXM_PAPER
-- ----------------------------
DROP TABLE IF EXISTS `EXM_PAPER`;
CREATE TABLE `EXM_PAPER` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(32) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` text COMMENT '描述',
  `TOTLE_SCORE` decimal(5,2) DEFAULT NULL COMMENT '总分数',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：启用；2：禁用',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '更新人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `PAPER_TYPE_ID` int(11) DEFAULT NULL COMMENT '试卷分类',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_16` (`PAPER_TYPE_ID`),
  CONSTRAINT `FK_Reference_16` FOREIGN KEY (`PAPER_TYPE_ID`) REFERENCES `EXM_PAPER_TYPE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_PAPER
-- ----------------------------

-- ----------------------------
-- Table structure for EXM_PAPER_QUESTION
-- ----------------------------
DROP TABLE IF EXISTS `EXM_PAPER_QUESTION`;
CREATE TABLE `EXM_PAPER_QUESTION` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `NAME` varchar(32) DEFAULT NULL COMMENT '章节名称',
  `DESCRIPTION` text COMMENT '章节描述',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父ID',
  `PARENT_SUB` varchar(512) DEFAULT NULL COMMENT '父子关系（格式：_父ID_子ID_子子ID_... ...）',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `PAPER_ID` int(11) DEFAULT NULL COMMENT '试卷ID',
  `QUESTION_ID` int(11) DEFAULT NULL COMMENT '试题ID',
  `TYPE` int(11) DEFAULT NULL COMMENT '1：章节；2：试题',
  `SCORE` decimal(5,2) DEFAULT NULL COMMENT '分数',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  `OPTIONS` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_17` (`QUESTION_ID`),
  KEY `FK_Reference_18` (`PAPER_ID`),
  CONSTRAINT `FK_Reference_18` FOREIGN KEY (`PAPER_ID`) REFERENCES `EXM_PAPER` (`ID`),
  CONSTRAINT `FK_Reference_17` FOREIGN KEY (`QUESTION_ID`) REFERENCES `EXM_QUESTION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_PAPER_QUESTION
-- ----------------------------

-- ----------------------------
-- Table structure for EXM_PAPER_TYPE
-- ----------------------------
DROP TABLE IF EXISTS `EXM_PAPER_TYPE`;
CREATE TABLE `EXM_PAPER_TYPE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `NAME` varchar(32) DEFAULT NULL COMMENT '名称',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父ID',
  `PARENT_SUB` varchar(512) DEFAULT NULL COMMENT '父子关系（格式：_父ID_子ID_子子ID_... ...）',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：正常',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  `USER_IDS` varchar(1024) DEFAULT NULL COMMENT '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
  `ORG_IDS` varchar(1024) DEFAULT NULL COMMENT '机构权限',
  `POST_IDS` varchar(1024) DEFAULT NULL COMMENT '岗位权限',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_PAPER_TYPE
-- ----------------------------
INSERT INTO `EXM_PAPER_TYPE` VALUES ('1', '试卷分类', '0', '_1_', '1', '2017-08-01 22:31:43', '1', '1', null, null, null);

-- ----------------------------
-- Table structure for EXM_QUESTION
-- ----------------------------
DROP TABLE IF EXISTS `EXM_QUESTION`;
CREATE TABLE `EXM_QUESTION` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TYPE` int(11) DEFAULT NULL COMMENT '1：单选；2：多选；3：填空；4：判断；5：问答',
  `DIFFICULTY` int(11) DEFAULT NULL COMMENT '1：极易；2：简单；3：适中；4：困难；5：极难',
  `TITLE` text COMMENT '题干',
  `OPTION_A` text COMMENT '选项A',
  `OPTION_B` text COMMENT '选项B',
  `OPTION_C` text COMMENT '选项C',
  `OPTION_D` text COMMENT '选项D',
  `OPTION_E` text COMMENT '选项E',
  `OPTION_F` text COMMENT '选项F',
  `OPTION_G` text COMMENT '选项G',
  `ANSWER` text COMMENT '答案',
  `ANALYSIS` text COMMENT '解析',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：启用；2：禁用',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '更新人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `QUESTION_TYPE_ID` int(11) DEFAULT NULL COMMENT '试题分类',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_15` (`QUESTION_TYPE_ID`),
  CONSTRAINT `FK_Reference_15` FOREIGN KEY (`QUESTION_TYPE_ID`) REFERENCES `EXM_QUESTION_TYPE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_QUESTION
-- ----------------------------

-- ----------------------------
-- Table structure for EXM_QUESTION_TYPE
-- ----------------------------
DROP TABLE IF EXISTS `EXM_QUESTION_TYPE`;
CREATE TABLE `EXM_QUESTION_TYPE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `NAME` varchar(32) DEFAULT NULL COMMENT '名称',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父ID',
  `PARENT_SUB` varchar(512) DEFAULT NULL COMMENT '父子关系（格式：_父ID_子ID_子子ID_... ...）',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：正常',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  `USER_IDS` varchar(1024) DEFAULT NULL COMMENT '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
  `ORG_IDS` varchar(1024) DEFAULT NULL COMMENT '机构权限',
  `POST_IDS` varchar(1024) DEFAULT NULL COMMENT '岗位权限',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of EXM_QUESTION_TYPE
-- ----------------------------
INSERT INTO `EXM_QUESTION_TYPE` VALUES ('1', '试题分类', '0', '_1_', '1', '2017-08-01 22:31:43', '1', '1', null, null, null);

-- ----------------------------
-- Table structure for SYS_DICT
-- ----------------------------
DROP TABLE IF EXISTS `SYS_DICT`;
CREATE TABLE `SYS_DICT` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DICT_INDEX` varchar(32) DEFAULT NULL COMMENT '索引',
  `DICT_KEY` varchar(32) DEFAULT NULL COMMENT '键',
  `DICT_VALUE` varchar(32) DEFAULT NULL COMMENT '值',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5000 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_DICT
-- ----------------------------
INSERT INTO `SYS_DICT` VALUES ('1', 'QUESTION_TYPE', '1', '单选', '1');
INSERT INTO `SYS_DICT` VALUES ('2', 'QUESTION_TYPE', '2', '多选', '2');
INSERT INTO `SYS_DICT` VALUES ('3', 'QUESTION_TYPE', '3', '填空', '3');
INSERT INTO `SYS_DICT` VALUES ('4', 'QUESTION_TYPE', '4', '判断', '4');
INSERT INTO `SYS_DICT` VALUES ('5', 'QUESTION_TYPE', '5', '问答', '5');
INSERT INTO `SYS_DICT` VALUES ('6', 'QUESTION_DIFFICULTY', '1', '极易', '1');
INSERT INTO `SYS_DICT` VALUES ('7', 'QUESTION_DIFFICULTY', '2', '简单', '2');
INSERT INTO `SYS_DICT` VALUES ('8', 'QUESTION_DIFFICULTY', '3', '适中', '3');
INSERT INTO `SYS_DICT` VALUES ('9', 'QUESTION_DIFFICULTY', '4', '困难', '4');
INSERT INTO `SYS_DICT` VALUES ('10', 'QUESTION_DIFFICULTY', '5', '极难', '5');
INSERT INTO `SYS_DICT` VALUES ('11', 'QUESTION_OPTIONS', '1', 'A', '1');
INSERT INTO `SYS_DICT` VALUES ('12', 'QUESTION_OPTIONS', '2', 'B', '2');
INSERT INTO `SYS_DICT` VALUES ('13', 'QUESTION_OPTIONS', '3', 'C', '3');
INSERT INTO `SYS_DICT` VALUES ('14', 'QUESTION_OPTIONS', '4', 'D', '4');
INSERT INTO `SYS_DICT` VALUES ('15', 'QUESTION_OPTIONS', '5', 'E', '5');
INSERT INTO `SYS_DICT` VALUES ('16', 'QUESTION_OPTIONS', '6', 'F', '6');
INSERT INTO `SYS_DICT` VALUES ('17', 'QUESTION_OPTIONS', '7', 'G', '7');
INSERT INTO `SYS_DICT` VALUES ('18', 'STATE', '1', '启用', '1');
INSERT INTO `SYS_DICT` VALUES ('19', 'STATE', '2', '禁用', '2');
INSERT INTO `SYS_DICT` VALUES ('20', 'EXAM_USER_STATE', '1', '未考试', '1');
INSERT INTO `SYS_DICT` VALUES ('21', 'EXAM_USER_STATE', '2', '考试中', '2');
INSERT INTO `SYS_DICT` VALUES ('22', 'EXAM_USER_STATE', '3', '已交卷', '3');
INSERT INTO `SYS_DICT` VALUES ('23', 'EXAM_USER_STATE', '4', '强制交卷', '4');
INSERT INTO `SYS_DICT` VALUES ('24', 'EXAM_USER_STATE', '5', '判卷中', '5');
INSERT INTO `SYS_DICT` VALUES ('25', 'EXAM_USER_STATE', '6', '已判卷', '6');
INSERT INTO `SYS_DICT` VALUES ('26', 'EXAM_USER_STATE', '7', '不及格', '7');
INSERT INTO `SYS_DICT` VALUES ('27', 'EXAM_USER_STATE', '8', '及格', '8');
INSERT INTO `SYS_DICT` VALUES ('28', 'RES_TYPE', '1', '后台', '1');
INSERT INTO `SYS_DICT` VALUES ('29', 'RES_TYPE', '2', '前台', '2');
INSERT INTO `SYS_DICT` VALUES ('30', 'EXAM_STATE', '1', '发布', '1');
INSERT INTO `SYS_DICT` VALUES ('31', 'EXAM_STATE', '2', '草稿', '2');

-- ----------------------------
-- Table structure for SYS_FILE
-- ----------------------------
DROP TABLE IF EXISTS `SYS_FILE`;
CREATE TABLE `SYS_FILE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(32) DEFAULT NULL COMMENT '前缀',
  `EXT_NAME` varchar(32) DEFAULT NULL COMMENT '后缀',
  `FILE_TYPE` varchar(128) DEFAULT NULL COMMENT '类型',
  `PATH` varchar(64) DEFAULT NULL COMMENT '路径',
  `IP` varchar(16) DEFAULT NULL COMMENT '上传ip',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：正常',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '更新人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_FILE
-- ----------------------------

-- ----------------------------
-- Table structure for SYS_ORG
-- ----------------------------
DROP TABLE IF EXISTS `SYS_ORG`;
CREATE TABLE `SYS_ORG` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `NAME` varchar(32) DEFAULT NULL COMMENT '名称',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父ID',
  `PARENT_SUB` varchar(512) DEFAULT NULL COMMENT '父子关系（格式：_父ID_子ID_子子ID_... ...）',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：正常',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_ORG
-- ----------------------------
INSERT INTO `SYS_ORG` VALUES ('1', '组织机构', '0', '_1_', '1', '2017-08-01 22:31:43', '1', '1');

-- ----------------------------
-- Table structure for SYS_ORG_POST
-- ----------------------------
DROP TABLE IF EXISTS `SYS_ORG_POST`;
CREATE TABLE `SYS_ORG_POST` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ORG_ID` int(11) DEFAULT NULL,
  `POST_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_1` (`ORG_ID`),
  KEY `FK_Reference_2` (`POST_ID`),
  CONSTRAINT `FK_Reference_2` FOREIGN KEY (`POST_ID`) REFERENCES `SYS_POST` (`ID`),
  CONSTRAINT `FK_Reference_1` FOREIGN KEY (`ORG_ID`) REFERENCES `SYS_ORG` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_ORG_POST
-- ----------------------------

-- ----------------------------
-- Table structure for SYS_POST
-- ----------------------------
DROP TABLE IF EXISTS `SYS_POST`;
CREATE TABLE `SYS_POST` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(32) DEFAULT NULL,
  `UPDATE_USER_ID` int(11) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：正常',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_POST
-- ----------------------------

-- ----------------------------
-- Table structure for SYS_POST_RES
-- ----------------------------
DROP TABLE IF EXISTS `SYS_POST_RES`;
CREATE TABLE `SYS_POST_RES` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `POST_ID` int(11) DEFAULT NULL COMMENT '岗位ID',
  `RES_ID` int(11) DEFAULT NULL COMMENT '资源ID',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_6` (`POST_ID`),
  KEY `FK_Reference_7` (`RES_ID`),
  CONSTRAINT `FK_Reference_7` FOREIGN KEY (`RES_ID`) REFERENCES `SYS_RES` (`ID`),
  CONSTRAINT `FK_Reference_6` FOREIGN KEY (`POST_ID`) REFERENCES `SYS_POST` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_POST_RES
-- ----------------------------

-- ----------------------------
-- Table structure for SYS_POST_USER
-- ----------------------------
DROP TABLE IF EXISTS `SYS_POST_USER`;
CREATE TABLE `SYS_POST_USER` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `POST_ID` int(11) DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_4` (`USER_ID`),
  KEY `FK_Reference_5` (`POST_ID`),
  CONSTRAINT `FK_Reference_4` FOREIGN KEY (`USER_ID`) REFERENCES `SYS_USER` (`ID`),
  CONSTRAINT `FK_Reference_5` FOREIGN KEY (`POST_ID`) REFERENCES `SYS_POST` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_POST_USER
-- ----------------------------

-- ----------------------------
-- Table structure for SYS_RES
-- ----------------------------
DROP TABLE IF EXISTS `SYS_RES`;
CREATE TABLE `SYS_RES` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(32) DEFAULT NULL COMMENT '名称',
  `URL` varchar(512) DEFAULT NULL COMMENT 'URL',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父ID',
  `PARENT_SUB` varchar(128) DEFAULT NULL COMMENT '父子关系（格式：_父ID_子ID_子子ID_... ...）',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `NO` int(11) DEFAULT NULL COMMENT '排序',
  `AUTH_POS` int(11) DEFAULT NULL COMMENT '权限位',
  `AUTH_CODE` int(11) DEFAULT NULL COMMENT '权限码',
  `ICON` varchar(32) DEFAULT NULL COMMENT '图标',
  `TYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5000 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_RES
-- ----------------------------
INSERT INTO `SYS_RES` VALUES ('1', '系统资源', 'xtzy', '0', '_1_', '1', '2017-08-15 18:41:02', '1', '1', '1', '', null);
INSERT INTO `SYS_RES` VALUES ('2', '系统管理', 'xtgl', '1', '_1_2_', '1', '2017-08-01 23:02:52', '1', '1', '2', '', '1');
INSERT INTO `SYS_RES` VALUES ('3', '基础管理', 'jcgl', '2', '_1_2_3_', '1', '2017-08-01 23:04:09', '1', '1', '4', '', '1');
INSERT INTO `SYS_RES` VALUES ('4', '数据字典', 'dict/toList', '3', '_1_2_3_4_', '1', '2017-08-02 09:26:06', '3', '1', '8', '', '1');
INSERT INTO `SYS_RES` VALUES ('5', '列表', 'dict/list', '4', '_1_2_3_4_5_', '1', '2017-08-01 23:06:54', '1', '1', '16', '', '1');
INSERT INTO `SYS_RES` VALUES ('6', '添加', 'dict/toAdd|dict/doAdd', '4', '_1_2_3_4_6_', '1', '2017-08-01 23:08:51', '2', '1', '32', '', '1');
INSERT INTO `SYS_RES` VALUES ('7', '修改', 'dict/toEdit|dict/doEdit', '4', '_1_2_3_4_7_', '1', '2017-08-01 23:45:35', '3', '1', '64', '', '1');
INSERT INTO `SYS_RES` VALUES ('8', '删除', 'dict/doDel', '4', '_1_2_3_4_8_', '1', '2017-08-01 23:10:11', '4', '1', '128', '', '1');
INSERT INTO `SYS_RES` VALUES ('9', '移动', 'dict/toMove|dict/moveResTreeList|dict/doMove', '4', '_1_2_3_4_9_', '1', '2017-08-01 23:10:51', '5', '1', '256', '', '1');
INSERT INTO `SYS_RES` VALUES ('10', '资源管理', 'res/toList', '3', '_1_2_3_10_', '1', '2017-08-02 09:25:07', '1', '1', '512', '', '1');
INSERT INTO `SYS_RES` VALUES ('11', '列表', 'res/toList|res/treeList|res/list', '10', '_1_2_3_10_11_', '1', '2017-08-01 23:44:54', '1', '1', '1024', '', '1');
INSERT INTO `SYS_RES` VALUES ('12', '添加', 'res/toAdd|res/doAdd', '10', '_1_2_3_10_12_', '1', '2017-08-01 23:45:25', '2', '1', '2048', '', '1');
INSERT INTO `SYS_RES` VALUES ('13', '修改', 'res/toEdit|res/doEdit', '10', '_1_2_3_10_13_', '1', '2017-08-01 23:46:06', '3', '1', '4096', '', '1');
INSERT INTO `SYS_RES` VALUES ('14', '删除', 'res/doDel', '10', '_1_2_3_10_14_', '1', '2017-08-01 23:46:27', '4', '1', '8192', '', '1');
INSERT INTO `SYS_RES` VALUES ('15', '移动', 'res/toMove|res/moveResTreeList|res/doMove', '10', '_1_2_3_10_15_', '1', '2017-08-01 23:47:13', '5', '1', '16384', '', '1');
INSERT INTO `SYS_RES` VALUES ('16', '附件管理', 'file/toList', '3', '_1_2_3_16_', '1', '2017-08-01 23:49:46', '5', '1', '32768', '', '1');
INSERT INTO `SYS_RES` VALUES ('17', '列表', 'file/list', '16', '_1_2_3_16_17_', '1', '2017-08-01 23:52:34', '1', '1', '65536', '', '1');
INSERT INTO `SYS_RES` VALUES ('18', '上传', 'file/toUpload|file/doTempUpload|file/doUpload', '16', '_1_2_3_16_18_', '1', '2017-08-01 23:51:08', '2', '1', '131072', '', '1');
INSERT INTO `SYS_RES` VALUES ('19', '下载', 'file/doDownload', '16', '_1_2_3_16_19_', '1', '2017-08-01 23:51:45', '3', '1', '262144', '', '1');
INSERT INTO `SYS_RES` VALUES ('20', '删除', 'file/doDel', '16', '_1_2_3_16_20_', '1', '2017-08-01 23:52:23', '4', '1', '524288', '', '1');
INSERT INTO `SYS_RES` VALUES ('21', '在线用户', 'onlineUser/toList', '3', '_1_2_3_21_', '1', '2017-08-02 09:26:10', '7', '1', '1048576', '', '1');
INSERT INTO `SYS_RES` VALUES ('22', '列表', 'onlineUser/list', '21', '_1_2_3_21_22_', '1', '2017-08-02 09:17:19', '1', '1', '2097152', '', '1');
INSERT INTO `SYS_RES` VALUES ('23', '强制退出', 'onlineUser/doDel', '21', '_1_2_3_21_23_', '1', '2017-08-02 09:17:55', '2', '1', '4194304', '', '1');
INSERT INTO `SYS_RES` VALUES ('24', '实时日志', 'log/toList', '3', '_1_2_3_24_', '1', '2017-08-02 09:20:53', '9', '1', '8388608', '', '1');
INSERT INTO `SYS_RES` VALUES ('25', '列表', 'log/list', '24', '_1_2_3_24_25_', '1', '2017-08-02 09:20:01', '1', '1', '16777216', '', '1');
INSERT INTO `SYS_RES` VALUES ('26', '用户管理', 'yhgl', '2', '_1_2_26_', '1', '2017-08-02 09:27:15', '3', '1', '33554432', '', '1');
INSERT INTO `SYS_RES` VALUES ('27', '组织机构', 'org/toList', '26', '_1_2_26_27_', '1', '2017-08-02 09:29:33', '1', '1', '67108864', '', '1');
INSERT INTO `SYS_RES` VALUES ('28', '列表', 'org/treeList|org/list', '27', '_1_2_26_27_28_', '1', '2017-08-02 09:29:50', '1', '1', '134217728', '', '1');
INSERT INTO `SYS_RES` VALUES ('29', '添加', 'org/toAdd|org/doAdd', '27', '_1_2_26_27_29_', '1', '2017-08-02 09:30:03', '2', '1', '268435456', '', '1');
INSERT INTO `SYS_RES` VALUES ('30', '修改', 'org/toEdit|org/doEdit', '27', '_1_2_26_27_30_', '1', '2017-08-02 09:30:52', '3', '1', '536870912', '', '1');
INSERT INTO `SYS_RES` VALUES ('31', '删除', 'org/doDel', '27', '_1_2_26_27_31_', '1', '2017-08-02 09:30:47', '4', '1', '1073741824', '', '1');
INSERT INTO `SYS_RES` VALUES ('32', '移动', 'org/toMove|org/moveOrgTreeList|org/doMove', '27', '_1_2_26_27_32_', '1', '2017-08-02 09:30:38', '5', '2', '1', '', '1');
INSERT INTO `SYS_RES` VALUES ('33', '岗位管理', 'post/toList', '26', '_1_2_26_33_', '1', '2017-08-02 09:42:58', '3', '2', '2', '', '1');
INSERT INTO `SYS_RES` VALUES ('34', '列表', 'post/orgPostTreeList|post/list', '33', '_1_2_26_33_34_', '1', '2017-08-02 09:35:07', '1', '2', '4', '', '1');
INSERT INTO `SYS_RES` VALUES ('35', '添加', 'post/toAdd|post/doAdd', '33', '_1_2_26_33_35_', '1', '2017-08-02 09:35:23', '2', '2', '8', '', '1');
INSERT INTO `SYS_RES` VALUES ('36', '修改', 'post/toEdit|post/doEdit', '33', '_1_2_26_33_36_', '1', '2017-08-02 09:35:32', '3', '2', '16', '', '1');
INSERT INTO `SYS_RES` VALUES ('37', '删除', 'post/doDel', '33', '_1_2_26_33_37_', '1', '2017-08-02 09:35:41', '4', '2', '32', '', '1');
INSERT INTO `SYS_RES` VALUES ('38', '设置权限', 'post/toResUpdate|post/resUpdateResTreeList|post/doResUpdate', '33', '_1_2_26_33_38_', '1', '2017-08-02 09:35:57', '5', '2', '64', '', '1');
INSERT INTO `SYS_RES` VALUES ('39', '用户管理', 'user/toList', '26', '_1_2_26_39_', '1', '2017-08-02 09:43:19', '5', '2', '128', '', '1');
INSERT INTO `SYS_RES` VALUES ('40', '列表', 'user/orgTreeList|user/list', '39', '_1_2_26_39_40_', '1', '2017-08-02 09:45:22', '1', '2', '256', '', '1');
INSERT INTO `SYS_RES` VALUES ('41', '添加', 'user/toAdd|user/doAdd', '39', '_1_2_26_39_41_', '1', '2017-08-02 09:45:30', '2', '2', '512', '', '1');
INSERT INTO `SYS_RES` VALUES ('42', '修改', 'user/toEdit|user/doEdit', '39', '_1_2_26_39_42_', '1', '2017-08-02 09:45:41', '3', '2', '1024', '', '1');
INSERT INTO `SYS_RES` VALUES ('43', '删除', 'user/doDel', '39', '_1_2_26_39_43_', '1', '2017-08-02 09:45:50', '4', '2', '2048', '', '1');
INSERT INTO `SYS_RES` VALUES ('44', '设置机构', 'user/toOrgUpdate|user/orgUpdateOrgTreeList|user/doOrgUpdate', '39', '_1_2_26_39_44_', '1', '2017-08-02 09:46:15', '5', '2', '4096', '', '1');
INSERT INTO `SYS_RES` VALUES ('45', '设置岗位', 'user/toPostUpdate|user/postUpdatePostTreeList|user/doPostUpdate', '39', '_1_2_26_39_45_', '1', '2017-08-02 09:46:26', '6', '2', '8192', '', '1');
INSERT INTO `SYS_RES` VALUES ('46', '初始密码', 'user/initPwd', '39', '_1_2_26_39_46_', '1', '2017-08-02 09:46:41', '7', '2', '16384', '', '1');
INSERT INTO `SYS_RES` VALUES ('47', '考试管理', 'ksgl', '1', '_1_47_', '1', '2017-08-02 09:50:01', '3', '2', '32768', '', '1');
INSERT INTO `SYS_RES` VALUES ('48', '试题管理', 'stgl', '47', '_1_47_48_', '1', '2017-08-02 09:50:28', '1', '2', '65536', '', '1');
INSERT INTO `SYS_RES` VALUES ('49', '试题分类', 'questionType/toList', '48', '_1_47_48_49_', '1', '2017-08-02 09:52:59', '1', '2', '131072', '', '1');
INSERT INTO `SYS_RES` VALUES ('50', '列表', 'questionType/treeList|questionType/list', '49', '_1_47_48_49_50_', '1', '2017-08-02 09:53:16', '1', '2', '262144', '', '1');
INSERT INTO `SYS_RES` VALUES ('51', '添加', 'questionType/toAdd|questionType/doAdd', '49', '_1_47_48_49_51_', '1', '2017-08-02 09:53:26', '2', '2', '524288', '', '1');
INSERT INTO `SYS_RES` VALUES ('52', '修改', 'questionType/toEdit|questionType/doEdit', '49', '_1_47_48_49_52_', '1', '2017-08-02 09:53:34', '3', '2', '1048576', '', '1');
INSERT INTO `SYS_RES` VALUES ('53', '删除', 'questionType/doDel', '49', '_1_47_48_49_53_', '1', '2017-08-02 09:53:43', '4', '2', '2097152', '', '1');
INSERT INTO `SYS_RES` VALUES ('54', '移动', 'questionType/toMove|questionType/moveQuestionTypeTreeList|questionType/doMove', '49', '_1_47_48_49_54_', '1', '2017-08-02 09:55:27', '5', '2', '4194304', '', '1');
INSERT INTO `SYS_RES` VALUES ('61', '试卷管理', 'sjgl', '47', '_1_47_61_', '1', '2017-08-02 10:00:35', '3', '2', '536870912', '', '1');
INSERT INTO `SYS_RES` VALUES ('62', '试卷分类', 'paperType/toList', '61', '_1_47_61_62_', '1', '2017-08-02 10:03:05', '1', '2', '1073741824', '', '1');
INSERT INTO `SYS_RES` VALUES ('63', '列表', 'paperType/treeList|paperType/list', '62', '_1_47_61_62_63_', '1', '2017-08-02 10:03:18', '1', '3', '1', '', '1');
INSERT INTO `SYS_RES` VALUES ('64', '添加', 'paperType/toAdd|paperType/doAdd', '62', '_1_47_61_62_64_', '1', '2017-08-02 10:03:31', '2', '3', '2', '', '1');
INSERT INTO `SYS_RES` VALUES ('65', '修改', 'paperType/toEdit|paperType/doEdit', '62', '_1_47_61_62_65_', '1', '2017-08-02 10:03:39', '3', '3', '4', '', '1');
INSERT INTO `SYS_RES` VALUES ('66', '删除', 'paperType/doDel', '62', '_1_47_61_62_66_', '1', '2017-08-02 10:03:48', '4', '3', '8', '', '1');
INSERT INTO `SYS_RES` VALUES ('67', '移动', 'paperType/toMove|paperType/movePaperTypeTreeList|paperType/doMove', '62', '_1_47_61_62_67_', '1', '2017-08-02 10:04:01', '5', '3', '16', '', '1');
INSERT INTO `SYS_RES` VALUES ('75', '考试管理', 'ksgl2', '47', '_1_47_75_', '1', '2017-08-02 10:22:06', '5', '3', '4096', '', '1');
INSERT INTO `SYS_RES` VALUES ('76', '考试分类', 'examType/toList', '75', '_1_47_75_76_', '2', '2017-08-02 16:55:56', '1', '3', '8192', '', '1');
INSERT INTO `SYS_RES` VALUES ('77', '列表', 'examType/treeList|examType/list', '76', '_1_47_75_76_77_', '1', '2017-08-02 10:23:12', '1', '3', '16384', '', '1');
INSERT INTO `SYS_RES` VALUES ('78', '添加', 'examType/toAdd|examType/doAdd', '76', '_1_47_75_76_78_', '1', '2017-08-02 10:23:22', '2', '3', '32768', '', '1');
INSERT INTO `SYS_RES` VALUES ('79', '修改', 'examType/toEdit|examType/doEdit', '76', '_1_47_75_76_79_', '1', '2017-08-02 10:23:30', '3', '3', '65536', '', '1');
INSERT INTO `SYS_RES` VALUES ('80', '删除', 'examType/doDel', '76', '_1_47_75_76_80_', '1', '2017-08-02 10:23:39', '4', '3', '131072', '', '1');
INSERT INTO `SYS_RES` VALUES ('81', '移动', 'examType/toMove|examType/moveExamTypeTreeList|examType/doMove', '76', '_1_47_75_76_81_', '1', '2017-08-02 10:23:54', '5', '3', '262144', '', '1');
INSERT INTO `SYS_RES` VALUES ('89', '前台首页', 'qtsy', '1', '_1_89_', '1', '2017-08-16 08:51:40', '1', '3', '67108864', '', '2');
INSERT INTO `SYS_RES` VALUES ('99', '报表管理', 'bbgl', '1', '_1_99_', '1', '2017-08-29 14:19:59', '5', '4', '2', '', '1');
INSERT INTO `SYS_RES` VALUES ('100', '报表管理', 'bbgl2', '99', '_1_99_100_', '1', '2017-08-29 14:19:32', '1', '4', '4', '', '1');
INSERT INTO `SYS_RES` VALUES ('101', '成绩查询', 'gradeReport/toList', '100', '_1_99_100_101_', '1', '2017-08-29 15:34:19', '1', '4', '8', '', '1');
INSERT INTO `SYS_RES` VALUES ('102', '列表', 'gradeReport/list', '101', '_1_99_100_101_102_', '1', '2017-08-29 15:34:04', '1', '4', '16', '', '1');
INSERT INTO `SYS_RES` VALUES ('103', '考试管理', 'qtsy_ksgl', '89', '_1_89_103_', '1', '2018-11-24 17:03:57', '1', '4', '32', '', '2');
INSERT INTO `SYS_RES` VALUES ('104', '我的考试', 'qtsy_wdks', '89', '_1_89_104_', '1', '2018-11-24 17:04:19', '3', '4', '64', '', '2');
INSERT INTO `SYS_RES` VALUES ('105', '我的判卷', 'qtsy_wdpj', '89', '_1_89_105_', '1', '2018-11-24 17:04:37', '5', '4', '128', '', '2');
INSERT INTO `SYS_RES` VALUES ('106', '试题管理', 'home/question/toList', '103', '_1_89_103_106_', '1', '2018-11-24 17:05:21', '1', '4', '256', '', '2');
INSERT INTO `SYS_RES` VALUES ('107', '试卷管理', 'home/paper/toList', '103', '_1_89_103_107_', '1', '2018-11-24 17:05:46', '3', '4', '512', '', '2');
INSERT INTO `SYS_RES` VALUES ('108', '考试管理', 'home/exam/toList', '103', '_1_89_103_108_', '1', '2018-11-24 17:06:00', '5', '4', '1024', '', '2');
INSERT INTO `SYS_RES` VALUES ('109', '列表', 'home/question/questionTypeTreeList|home/question/list|home/question/doTempUpload|home/question/doDownload', '106', '_1_89_103_106_109_', '1', '2018-11-24 17:13:47', '1', '4', '2048', '', '2');
INSERT INTO `SYS_RES` VALUES ('110', '添加', 'home/question/toAdd|home/question/doAdd', '106', '_1_89_103_106_110_', '1', '2018-11-24 17:10:19', '2', '4', '4096', '', '2');
INSERT INTO `SYS_RES` VALUES ('111', '修改', 'home/question/toEdit|home/question/doEdit', '106', '_1_89_103_106_111_', '1', '2018-11-24 17:10:53', '3', '4', '8192', '', '2');
INSERT INTO `SYS_RES` VALUES ('112', '删除', 'home/question/doDel', '106', '_1_89_103_106_112_', '1', '2018-11-24 17:11:25', '4', '4', '16384', '', '2');
INSERT INTO `SYS_RES` VALUES ('113', '列表', 'home/paper/paperTypeTreeList|home/paper/list', '107', '_1_89_103_107_113_', '1', '2018-11-24 17:19:23', '1', '4', '32768', '', '2');
INSERT INTO `SYS_RES` VALUES ('114', '添加', 'home/paper/toAdd|home/paper/doAdd', '107', '_1_89_103_107_114_', '1', '2018-11-24 17:19:34', '2', '4', '65536', '', '2');
INSERT INTO `SYS_RES` VALUES ('115', '修改', 'home/paper/toEdit|home/paper/doEdit', '107', '_1_89_103_107_115_', '1', '2018-11-24 17:29:19', '3', '4', '131072', '', '2');
INSERT INTO `SYS_RES` VALUES ('116', '删除', 'home/paper/doDel', '107', '_1_89_103_107_116_', '1', '2018-11-24 17:29:33', '4', '4', '262144', '', '2');
INSERT INTO `SYS_RES` VALUES ('117', '配置试卷', 'home/paper/toCfg|home/paper/toChapterAdd|home/paper/doChapterAdd|home/paper/toChapterEdit|home/paper/doChapterEdit|home/paper/doChapterDel|home/paper/toQuestionAdd|home/paper/questionTypeTreeList|home/paper/questionList|home/paper/doQuestionAdd|home/paper/doQuestionClear|home/paper/toScoresUpdate|home/paper/doScoresUpdate|home/paper/doChapterUp|home/paper/doChapterDown|home/paper/doScoreUpdate|home/paper/doQuestionUp|home/paper/doQuestionDown|home/paper/doQuestionDel', '107', '_1_89_103_107_117_', '1', '2018-11-24 17:30:08', '5', '4', '524288', '', '2');
INSERT INTO `SYS_RES` VALUES ('118', '列表', 'home/exam/examTypeTreeList|home/exam/toPaperAdd|home/exam/paperTypeTreeList|home/exam/paperList|home/exam/list', '108', '_1_89_103_108_118_', '1', '2018-11-24 17:36:51', '1', '4', '1048576', '', '2');
INSERT INTO `SYS_RES` VALUES ('119', '添加', 'home/exam/toAdd|home/exam/doAdd', '108', '_1_89_103_108_119_', '1', '2018-11-24 17:37:05', '2', '4', '2097152', '', '2');
INSERT INTO `SYS_RES` VALUES ('120', '修改', 'home/exam/toEdit|home/exam/doEdit', '108', '_1_89_103_108_120_', '1', '2018-11-24 17:37:16', '3', '4', '4194304', '', '2');
INSERT INTO `SYS_RES` VALUES ('121', '删除', 'home/exam/doDel', '108', '_1_89_103_108_121_', '1', '2018-11-24 17:37:29', '4', '4', '8388608', '', '2');
INSERT INTO `SYS_RES` VALUES ('122', '发布', 'home/exam/doPublish', '108', '_1_89_103_108_122_', '1', '2018-11-24 17:37:45', '5', '4', '16777216', '', '2');
INSERT INTO `SYS_RES` VALUES ('123', '考试用户', 'home/exam/toExamUserList|home/exam/examUserOrgTreeList|home/exam/examUserList|home/exam/toExamUserAdd|home/exam/examUserAddList|home/exam/doExamUserAdd|home/exam/doExamUserDel', '108', '_1_89_103_108_123_', '1', '2018-11-24 17:38:14', '6', '4', '33554432', '', '2');
INSERT INTO `SYS_RES` VALUES ('124', '判卷用户', 'home/exam/toMarkUserList|home/exam/markUserOrgTreeList|home/exam/markUserList|home/exam/toMarkUserAdd|home/exam/markUserAddList|home/exam/doMarkUserAdd|home/exam/doMarkUserDel', '108', '_1_89_103_108_124_', '1', '2018-11-24 17:38:32', '7', '4', '67108864', '', '2');
INSERT INTO `SYS_RES` VALUES ('125', '我的考试', 'home/myExam/toList', '104', '_1_89_104_125_', '1', '2018-11-24 18:20:52', '3', '5', '1', '', '2');
INSERT INTO `SYS_RES` VALUES ('126', '我的判卷', 'home/myMark/toExamList', '105', '_1_89_105_126_', '1', '2018-11-24 18:20:40', '5', '5', '2', '', '2');
INSERT INTO `SYS_RES` VALUES ('127', '列表', 'home/myExam/list', '125', '_1_89_104_125_127_', '1', '2018-11-24 17:45:50', '1', '4', '134217728', '', '2');
INSERT INTO `SYS_RES` VALUES ('128', '答题', 'home/myExam/toPaper|home/myExam/updateAnswer|home/myExam/doPaper', '125', '_1_89_104_125_128_', '1', '2018-11-24 17:43:27', '2', '4', '268435456', '', '2');
INSERT INTO `SYS_RES` VALUES ('129', '列表', 'home/myMark/examList|home/myMark/toList|home/myMark/list', '126', '_1_89_105_126_129_', '1', '2018-11-24 17:45:07', '1', '4', '536870912', '', '2');
INSERT INTO `SYS_RES` VALUES ('130', '判卷', 'home/myMark/toMark|home/myMark/updateScore|home/myMark/doMark', '126', '_1_89_105_126_130_', '1', '2018-11-24 17:45:29', '2', '4', '1073741824', '', '2');
INSERT INTO `SYS_RES` VALUES ('133', '设置权限', 'questionType/toAuth|questionType/authUserOrgTreeList|questionType/authUserList|questionType/toAuthUserAddList|questionType/authUserAddList|questionType/doAuthUserAdd|questionType/doAuthUserDel|questionType/doAuthOrgUpdate|questionType/authOrgOrgTreeList|questionType/authPostOrgTreeList|questionType/doAuthPostUpdate', '49', '_1_47_48_49_133_', '1', '2018-11-25 20:52:32', '6', '5', '16', '', '1');
INSERT INTO `SYS_RES` VALUES ('134', '设置权限', 'paperType/toAuth|paperType/authUserOrgTreeList|paperType/authUserList|paperType/toAuthUserAddList|paperType/authUserAddList|paperType/doAuthUserAdd|paperType/doAuthUserDel|paperType/doAuthOrgUpdate|paperType/authOrgOrgTreeList|paperType/authPostOrgTreeList|paperType/doAuthPostUpdate', '62', '_1_47_61_62_134_', '1', '2018-11-25 20:51:23', '6', '5', '32', '', '1');
INSERT INTO `SYS_RES` VALUES ('135', '设置权限', 'examType/toAuth|examType/authUserOrgTreeList|examType/authUserList|examType/toAuthUserAddList|examType/authUserAddList|examType/doAuthUserAdd|examType/doAuthUserDel|examType/doAuthOrgUpdate|examType/authOrgOrgTreeList|examType/authPostOrgTreeList|examType/doAuthPostUpdate', '76', '_1_47_75_76_135_', '1', '2018-11-25 20:53:04', '6', '5', '64', '', '1');

-- ----------------------------
-- Table structure for SYS_USER
-- ----------------------------
DROP TABLE IF EXISTS `SYS_USER`;
CREATE TABLE `SYS_USER` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `NAME` varchar(32) DEFAULT NULL COMMENT '用户名',
  `PWD` varchar(32) DEFAULT NULL COMMENT '密码',
  `LOGIN_NAME` varchar(32) DEFAULT NULL COMMENT '登陆名称',
  `LAST_LOGIN_TIME` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `UPDATE_USER_ID` int(11) DEFAULT NULL COMMENT '修改人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `ORG_ID` int(11) DEFAULT NULL COMMENT '组织机构',
  `STATE` int(11) DEFAULT NULL COMMENT '0：删除；1：正常',
  PRIMARY KEY (`ID`),
  KEY `FK_Reference_3` (`ORG_ID`),
  CONSTRAINT `FK_Reference_3` FOREIGN KEY (`ORG_ID`) REFERENCES `SYS_ORG` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of SYS_USER
-- ----------------------------
INSERT INTO `SYS_USER` VALUES ('1', '系统管理员', '5HK/W3IuS8X734A5JMigPg==', 'sysadmin', '2017-09-06 11:03:17', '1', '2017-08-01 22:33:19', '1', '1');
