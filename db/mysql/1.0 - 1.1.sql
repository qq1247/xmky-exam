ALTER TABLE EX_EXAM RENAME TO EXM_EXAM;
ALTER TABLE EX_EXAM_TYPE RENAME TO EXM_EXAM_TYPE;
ALTER TABLE EX_EXAM_USER RENAME TO EXM_EXAM_USER;
ALTER TABLE EX_EXAM_USER_QUESTION RENAME TO EXM_EXAM_USER_QUESTION;
ALTER TABLE EX_MARK_USER RENAME TO EXM_MARK_USER;
ALTER TABLE EX_PAPER RENAME TO EXM_PAPER;
ALTER TABLE EX_PAPER_QUESTION RENAME TO EXM_PAPER_QUESTION;
ALTER TABLE EX_PAPER_TYPE RENAME TO EXM_PAPER_TYPE;
ALTER TABLE EX_QUESTION RENAME TO EXM_QUESTION;
ALTER TABLE EX_QUESTION_TYPE RENAME TO EXM_QUESTION_TYPE;

ALTER TABLE `EXM_EXAM` MODIFY COLUMN `DESCRIPTION` text NULL COMMENT '描述' AFTER `PASS_SCORE`;
ALTER TABLE `EXM_EXAM` ADD COLUMN `MARK_START_TIME` datetime NULL DEFAULT NULL COMMENT '判卷开始' AFTER `END_TIME`;
ALTER TABLE `EXM_EXAM` ADD COLUMN `MARK_END_TIME` datetime NULL DEFAULT NULL COMMENT '判卷结束' AFTER `MARK_START_TIME`;

ALTER TABLE `EXM_EXAM_TYPE` ADD COLUMN `USER_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '用户权限。多用户用逗号分隔，如：,2,45,66,57,' AFTER `NO`;
ALTER TABLE `EXM_EXAM_TYPE` ADD COLUMN `ORG_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '机构权限' AFTER `USER_IDS`;
ALTER TABLE `EXM_EXAM_TYPE` ADD COLUMN `POST_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '岗位权限' AFTER `ORG_IDS`;

ALTER TABLE `EXM_PAPER` MODIFY COLUMN `DESCRIPTION` text NULL COMMENT '描述' AFTER `NAME`;

ALTER TABLE `EXM_PAPER_QUESTION` MODIFY COLUMN `DESCRIPTION` text NULL COMMENT '章节描述' AFTER `NAME`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD COLUMN `OPTIONS` varchar(8) NULL DEFAULT NULL AFTER `NO`;

ALTER TABLE `EXM_PAPER_TYPE` ADD COLUMN `USER_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '用户权限。多用户用逗号分隔，如：,2,45,66,57,' AFTER `NO`;
ALTER TABLE `EXM_PAPER_TYPE` ADD COLUMN `ORG_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '机构权限' AFTER `USER_IDS`;
ALTER TABLE `EXM_PAPER_TYPE` ADD COLUMN `POST_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '岗位权限' AFTER `ORG_IDS`;

ALTER TABLE `EXM_QUESTION_TYPE` ADD COLUMN `USER_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '用户权限。多用户用逗号分隔，如：,2,45,66,57,' AFTER `NO`;
ALTER TABLE `EXM_QUESTION_TYPE` ADD COLUMN `ORG_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '机构权限' AFTER `USER_IDS`;
ALTER TABLE `EXM_QUESTION_TYPE` ADD COLUMN `POST_IDS` varchar(1024) NULL DEFAULT NULL COMMENT '岗位权限' AFTER `ORG_IDS`;

ALTER TABLE `EXM_EXAM_USER` DROP FOREIGN KEY `FK_Reference_23`;
ALTER TABLE `EXM_EXAM_USER` ADD CONSTRAINT `FK_Reference_23` FOREIGN KEY (`EXAM_ID`) REFERENCES `EXM_EXAM` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `EXM_EXAM_USER_QUESTION` DROP FOREIGN KEY `FK_Reference_24`;
ALTER TABLE `EXM_EXAM_USER_QUESTION` ADD CONSTRAINT `FK_Reference_24` FOREIGN KEY (`EXAM_USER_ID`) REFERENCES `EXM_EXAM_USER` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `EXM_MARK_USER` DROP FOREIGN KEY `FK_Reference_37`;
ALTER TABLE `EXM_MARK_USER` ADD CONSTRAINT `FK_Reference_37` FOREIGN KEY (`EXAM_ID`) REFERENCES `EXM_EXAM` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `EXM_PAPER` DROP FOREIGN KEY `FK_Reference_16`;
ALTER TABLE `EXM_PAPER` ADD CONSTRAINT `FK_Reference_16` FOREIGN KEY (`PAPER_TYPE_ID`) REFERENCES `EXM_PAPER_TYPE` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `EXM_EXAM_USER_QUESTION` DROP FOREIGN KEY `FK_Reference_25`;
ALTER TABLE `EXM_EXAM_USER_QUESTION` ADD CONSTRAINT `FK_Reference_25` FOREIGN KEY (`QUESTION_ID`) REFERENCES `EXM_QUESTION` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `EXM_PAPER_QUESTION` DROP FOREIGN KEY `FK_Reference_18`;
ALTER TABLE `EXM_PAPER_QUESTION` DROP FOREIGN KEY `FK_Reference_17`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD CONSTRAINT `FK_Reference_17` FOREIGN KEY (`QUESTION_ID`) REFERENCES `EXM_QUESTION` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE `EXM_PAPER_QUESTION` ADD CONSTRAINT `FK_Reference_18` FOREIGN KEY (`PAPER_ID`) REFERENCES `EXM_PAPER` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `EXM_QUESTION` DROP FOREIGN KEY `FK_Reference_15`;
ALTER TABLE `EXM_QUESTION` ADD CONSTRAINT `FK_Reference_15` FOREIGN KEY (`QUESTION_TYPE_ID`) REFERENCES `EXM_QUESTION_TYPE` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `EXM_EXAM` DROP FOREIGN KEY `FK_Reference_19`;
ALTER TABLE `EXM_EXAM` DROP FOREIGN KEY `FK_Reference_22`;
ALTER TABLE `EXM_EXAM` ADD CONSTRAINT `FK_Reference_19` FOREIGN KEY (`PAPER_ID`) REFERENCES `EXM_PAPER` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE `EXM_EXAM` ADD CONSTRAINT `FK_Reference_22` FOREIGN KEY (`EXAM_TYPE_ID`) REFERENCES `EXM_EXAM_TYPE` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `SYS_ORG_POST` DROP FOREIGN KEY `FK_Reference_2`;
ALTER TABLE `SYS_ORG_POST` ADD CONSTRAINT `FK_Reference_2` FOREIGN KEY (`POST_ID`) REFERENCES `SYS_POST` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `SYS_POST_RES` DROP FOREIGN KEY `FK_Reference_7`;
ALTER TABLE `SYS_POST_RES` ADD CONSTRAINT `FK_Reference_7` FOREIGN KEY (`RES_ID`) REFERENCES `SYS_RES` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `SYS_POST_USER` DROP FOREIGN KEY `FK_Reference_4`;
ALTER TABLE `SYS_POST_USER` ADD CONSTRAINT `FK_Reference_4` FOREIGN KEY (`USER_ID`) REFERENCES `SYS_USER` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

INSERT INTO `SYS_DICT` VALUES ('30', 'EXAM_STATE', '1', '发布', '1');
INSERT INTO `SYS_DICT` VALUES ('31', 'EXAM_STATE', '2', '草稿', '2');

DELETE FROM SYS_RES WHERE PARENT_SUB LIKE '_1_47_48_55_%';
DELETE FROM SYS_RES WHERE PARENT_SUB LIKE '_1_47_61_68_%';
DELETE FROM SYS_RES WHERE PARENT_SUB LIKE '_1_47_75_82_%';
DELETE FROM SYS_RES WHERE PARENT_SUB LIKE '_1_89_90_%';

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
