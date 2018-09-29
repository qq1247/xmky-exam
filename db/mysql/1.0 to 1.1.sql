ALTER  TABLE EX_EXAM RENAME TO EXM_EXAM;
ALTER  TABLE EX_EXAM_TYPE RENAME TO EXM_EXAM_TYPE;
ALTER  TABLE EX_EXAM_USER RENAME TO EXM_EXAM_USER;
ALTER  TABLE EX_EXAM_USER_QUESTION RENAME TO EXM_EXAM_USER_QUESTION;
ALTER  TABLE EX_MARK_USER RENAME TO EXM_MARK_USER;
ALTER  TABLE EX_PAPER RENAME TO EXM_PAPER;
ALTER  TABLE EX_PAPER_QUESTION RENAME TO EXM_PAPER_QUESTION;
ALTER  TABLE EX_PAPER_TYPE RENAME TO EXM_PAPER_TYPE;
ALTER  TABLE EX_QUESTION RENAME TO EXM_QUESTION;
ALTER  TABLE EX_QUESTION_TYPE RENAME TO EXM_QUESTION_TYPE;

create table EXM_QUESTION_TYPE_AUTH
(
   ID                   int not null comment '试题分类ID',
   USER_IDS             varchar(512) comment '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
   ORG_IDS              varchar(512) comment '机构权限',
   POST_IDS             varchar(512) comment '岗位权限',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

create table EXM_PAPER_TYPE_AUTH
(
   ID                   int not null comment '试题分类ID',
   USER_IDS             varchar(512) comment '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
   ORG_IDS              varchar(512) comment '机构权限',
   POST_IDS             varchar(512) comment '岗位权限',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

create table EXM_EXAM_TYPE_AUTH
(
   ID                   int not null comment '试题分类ID',
   USER_IDS             varchar(512) comment '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
   ORG_IDS              varchar(512) comment '机构权限',
   POST_IDS             varchar(512) comment '岗位权限',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

INSERT INTO `sys_res` VALUES ('103', '设置权限', 'questionType/toAuth|questionType/authUserOrgTreeList|questionType/authUserList|questionType/toAuthUserAddList|questionType/authUserAddList|questionType/doAuthUserAdd|questionType/doAuthUserDel|questionType/doAuthOrgUpdate|questionType/authOrgOrgTreeList|questionType/authPostOrgTreeList|questionType/doAuthPostUpdate', '49', '_1_47_48_49_103_', '1', '2018-09-29 22:42:31', '6', '4', '32', '', '1');
INSERT INTO `sys_res` VALUES ('104', '设置权限', 'paperType/toAuth|paperType/authUserOrgTreeList|paperType/authUserList|paperType/toAuthUserAddList|paperType/authUserAddList|paperType/doAuthUserAdd|paperType/doAuthUserDel|paperType/doAuthOrgUpdate|paperType/authOrgOrgTreeList|paperType/authPostOrgTreeList|paperType/doAuthPostUpdate', '62', '_1_47_61_62_104_', '1', '2018-09-29 22:44:36', '6', '4', '64', '', '1');
INSERT INTO `sys_res` VALUES ('105', '设置权限', 'examType/toAuth|examType/authUserOrgTreeList|examType/authUserList|examType/toAuthUserAddList|examType/authUserAddList|examType/doAuthUserAdd|examType/doAuthUserDel|examType/doAuthOrgUpdate|examType/authOrgOrgTreeList|examType/authPostOrgTreeList|examType/doAuthPostUpdate', '76', '_1_47_75_76_105_', '1', '2018-09-29 22:43:30', '6', '4', '128', '', '1');
