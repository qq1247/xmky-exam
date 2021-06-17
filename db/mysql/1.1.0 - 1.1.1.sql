DELETE FROM SYS_POST_RES
WHERE RES_ID IN (
		SELECT ID FROM SYS_RES
		WHERE PARENT_SUB LIKE '_1_47_%'
);
DELETE FROM SYS_RES WHERE PARENT_SUB LIKE '_1_47_%';

INSERT INTO `SYS_RES` VALUES ('131', '试题分类', 'home/questionType/toList', '103', '_1_89_103_131_', '1', '2019-02-22 23:42:01', '1', '5', '128', '', '2');
INSERT INTO `SYS_RES` VALUES ('132', '列表', 'questionType/treeList|questionType/list', '131', '_1_89_103_131_132_', '1', '2019-01-02 21:46:25', '1', '5', '256', '', '2');
INSERT INTO `SYS_RES` VALUES ('133', '添加', 'questionType/toAdd|questionType/doAdd', '131', '_1_89_103_131_133_', '1', '2019-02-22 23:45:36', '2', '5', '512', '', '2');
INSERT INTO `SYS_RES` VALUES ('134', '修改', 'questionType/toEdit|questionType/doEdit', '131', '_1_89_103_131_134_', '1', '2019-02-22 23:45:52', '3', '5', '1024', '', '2');
INSERT INTO `SYS_RES` VALUES ('135', '删除', 'questionType/doDel', '131', '_1_89_103_131_135_', '1', '2019-02-22 23:46:06', '4', '5', '2048', '', '2');
INSERT INTO `SYS_RES` VALUES ('136', '授权', 'questionType/toAuth|questionType/authUserOrgTreeList|questionType/authUserList|questionType/toAuthUserAddList|questionType/authUserAddList|questionType/doAuthUserAdd|questionType/doAuthUserDel|questionType/doAuthOrgUpdate|questionType/authOrgOrgTreeList|questionType/authPostOrgTreeList|questionType/doAuthPostUpdate', '131', '_1_89_103_131_136_', '1', '2019-02-22 23:46:23', '5', '5', '4096', '', '2');
INSERT INTO `SYS_RES` VALUES ('137', '试卷分类', 'home/paperType/toList', '103', '_1_89_103_137_', '1', '2019-02-22 23:51:36', '3', '5', '8192', '', '2');
INSERT INTO `SYS_RES` VALUES ('138', '列表', 'paperType/treeList|paperType/list', '137', '_1_89_103_137_138_', '1', '2019-02-22 23:52:27', '1', '5', '16384', '', '2');
INSERT INTO `SYS_RES` VALUES ('139', '添加', 'paperType/toAdd|paperType/doAdd', '137', '_1_89_103_137_139_', '1', '2019-02-22 23:52:39', '2', '5', '32768', '', '2');
INSERT INTO `SYS_RES` VALUES ('140', '修改', 'paperType/toEdit|paperType/doEdit', '137', '_1_89_103_137_140_', '1', '2019-02-22 23:52:52', '3', '5', '65536', '', '2');
INSERT INTO `SYS_RES` VALUES ('141', '删除', 'paperType/doDel', '137', '_1_89_103_137_141_', '1', '2019-02-22 23:53:02', '4', '5', '131072', '', '2');
INSERT INTO `SYS_RES` VALUES ('142', '授权', 'paperType/toAuth|paperType/authUserOrgTreeList|paperType/authUserList|paperType/toAuthUserAddList|paperType/authUserAddList|paperType/doAuthUserAdd|paperType/doAuthUserDel|paperType/doAuthOrgUpdate|paperType/authOrgOrgTreeList|paperType/authPostOrgTreeList|paperType/doAuthPostUpdate', '137', '_1_89_103_137_142_', '1', '2019-02-22 23:53:13', '5', '5', '262144', '', '2');
INSERT INTO `SYS_RES` VALUES ('143', '考试分类', 'home/examType/toList', '103', '_1_89_103_143_', '1', '2019-02-22 23:53:59', '5', '5', '524288', '', '2');
INSERT INTO `SYS_RES` VALUES ('144', '列表', 'examType/treeList|examType/list', '143', '_1_89_103_143_144_', '1', '2019-02-22 23:54:21', '1', '5', '1048576', '', '2');
INSERT INTO `SYS_RES` VALUES ('145', '添加', 'examType/toAdd|examType/doAdd', '143', '_1_89_103_143_145_', '1', '2019-02-22 23:54:34', '2', '5', '2097152', '', '2');
INSERT INTO `SYS_RES` VALUES ('146', '修改', 'examType/toEdit|examType/doEdit', '143', '_1_89_103_143_146_', '1', '2019-02-22 23:54:44', '3', '5', '4194304', '', '2');
INSERT INTO `SYS_RES` VALUES ('147', '删除', 'examType/doDel', '143', '_1_89_103_143_147_', '1', '2019-02-22 23:54:55', '4', '5', '8388608', '', '2');
INSERT INTO `SYS_RES` VALUES ('148', '授权', 'examType/toAuth|examType/authUserOrgTreeList|examType/authUserList|examType/toAuthUserAddList|examType/authUserAddList|examType/doAuthUserAdd|examType/doAuthUserDel|examType/doAuthOrgUpdate|examType/authOrgOrgTreeList|examType/authPostOrgTreeList|examType/doAuthPostUpdate', '143', '_1_89_103_143_148_', '1', '2019-02-22 23:55:09', '5', '5', '16777216', '', '2');

drop table if exists SYS_VER;

/*==============================================================*/
/* Table: SYS_VER                                               */
/*==============================================================*/
create table SYS_VER
(
   ID                   int not null auto_increment comment 'id',
   VER                  varchar(16) comment '版本',
   UPDATE_TIME          datetime comment '修改时间',
   AUTHOR               varchar(16) comment '作者',
   REMARK               text comment '备注',
   primary key (ID)
);

alter table SYS_VER comment '版本';

INSERT INTO `SYS_VER` VALUES (1, '1.1.1', '2019-02-23 15:35:21', 'ZHC', '');
