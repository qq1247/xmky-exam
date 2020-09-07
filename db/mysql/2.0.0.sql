drop table if exists SYS_RES;

/*==============================================================*/
/* Table: SYS_RES                                               */
/*==============================================================*/
create table SYS_RES
(
   ID                   int not null auto_increment,
   NAME                 varchar(32) comment '名称',
   URL                  varchar(512) comment '多链接用|分割',
   PARENT_ID            int comment '父ID',
   PARENT_SUB           varchar(128) comment '父子关系（格式：_父ID_子ID_子子ID_... ...）',
   LEVEL                int comment '级别',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   NO                   int comment '排序',
   AUTH_POS             int comment '权限位',
   AUTH_CODE            int comment '权限码',
   ICON                 varchar(32) comment '图标',
   TYPE                 int comment '1：后端；2：前端',
   primary key (ID)
);

alter table SYS_RES comment '资源';

drop table if exists SYS_ORG;

/*==============================================================*/
/* Table: SYS_ORG                                               */
/*==============================================================*/
create table SYS_ORG
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   CODE                 varchar(32) comment '编码唯一',
   PARENT_ID            int comment '父ID',
   PARENT_SUB           varchar(128) comment '父子关系（格式：_父ID_子ID_子子ID_... ...）',
   LEVEL                int comment '级别',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常；',
   NO                   int comment '排序',
   primary key (ID)
);

alter table SYS_ORG comment '组织机构';

drop table if exists SYS_POST;

/*==============================================================*/
/* Table: SYS_POST                                              */
/*==============================================================*/
create table SYS_POST
(
   ID                   int not null auto_increment,
   NAME                 varchar(32),
   CODE                 varchar(32) comment '编码唯一',
   ORG_ID               int comment '组织机构ID',
   RES_IDS              varchar(512) comment '资源IDS',
   UPDATE_USER_ID       int,
   UPDATE_TIME          datetime,
   STATE                int comment '0：删除；1：正常',
   primary key (ID)
);

alter table SYS_POST comment '岗位';

drop table if exists SYS_USER;

/*==============================================================*/
/* Table: SYS_USER                                              */
/*==============================================================*/
create table SYS_USER
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(16) comment '昵称',
   LOGIN_NAME           varchar(16) comment '登陆名称',
   PHONE                varchar(11) comment '手机号',
   PWD                  varchar(32) comment '密码',
   REGIST_TIME          datetime comment '注册时间',
   LAST_LOGIN_TIME      datetime comment '最后登陆时间',
   ORG_ID               int comment '组织机构ID',
   POST_IDS             varchar(64) comment '岗位',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常；2：冻结；',
   primary key (ID)
);

alter table SYS_USER comment '用户';

drop table if exists SYS_DICT;

/*==============================================================*/
/* Table: SYS_DICT                                              */
/*==============================================================*/
create table SYS_DICT
(
   ID                   int not null auto_increment,
   DICT_INDEX           varchar(32) comment '索引',
   DICT_KEY             varchar(32) comment '键',
   DICT_VALUE           varchar(32) comment '值',
   NO                   int comment '排序',
   primary key (ID)
);

alter table SYS_DICT comment '数据字典';

drop table if exists SYS_FILE;

/*==============================================================*/
/* Table: SYS_FILE                                              */
/*==============================================================*/
create table SYS_FILE
(
   ID                   int not null auto_increment,
   NAME                 varchar(64) comment '前缀',
   EXT_NAME             varchar(32) comment '后缀',
   FILE_TYPE            varchar(128) comment '类型',
   PATH                 varchar(64) comment '路径',
   IP                   varchar(16) comment '上传IP',
   STATE                int comment '0：删除；1：正常',
   UPDATE_USER_ID       int comment '更新人',
   UPDATE_TIME          datetime comment '更新时间',
   primary key (ID)
);

alter table SYS_FILE comment '附件';

drop table if exists SYS_CRON;

/*==============================================================*/
/* Table: SYS_CRON                                              */
/*==============================================================*/
create table SYS_CRON
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   JOB_CLASS            varchar(64) comment '实现类',
   CRON                 varchar(64) comment 'cron表达式',
   STATE                int comment '1：启动；2：停止；',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_CRON comment '定时任务';

drop table if exists SYS_DISTRICT;

/*==============================================================*/
/* Table: SYS_DISTRICT                                          */
/*==============================================================*/
create table SYS_DISTRICT
(
   ID                   int not null auto_increment comment 'id',
   NAME                 varchar(32) comment '名称',
   CODE                 int comment '编号',
   X                    varchar(16) comment 'X坐标',
   Y                    varchar(16) comment 'Y坐标',
   TYPE                 int comment '1：省；2：市；3：县（区）4：街道',
   PARENT_ID            int comment '父ID',
   PARENT_SUB           varchar(512) comment '父子关系（格式：_父ID_子ID_子子ID_... ...）',
   LEVEL                int comment '级别',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常',
   NO                   int comment '排序',
   primary key (ID)
);

alter table SYS_DISTRICT comment '行政区域';

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

drop table if exists EXM_QUESTION_TYPE;

/*==============================================================*/
/* Table: EXM_QUESTION_TYPE                                     */
/*==============================================================*/
create table EXM_QUESTION_TYPE
(
   ID                   int not null auto_increment comment 'id',
   NAME                 varchar(32) comment '名称',
   PARENT_ID            int comment '父ID',
   PARENT_SUB           varchar(512) comment '父子关系（格式：_父ID_子ID_子子ID_... ...）',
   LEVEL                int comment '级别',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常',
   NO                   int comment '排序',
   USER_IDS             varchar(1024) comment '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
   ORG_IDS              varchar(1024) comment '机构权限',
   POST_IDS             varchar(1024) comment '岗位权限',
   primary key (ID)
);

alter table EXM_QUESTION_TYPE comment '试题分类';



/*==============================================================*/
/* 数据                                            */
/*==============================================================*/

INSERT INTO `SYS_ORG` VALUES ('1', '组织机构', 'code', '0', '_1_', '1', '1', '2017-08-01 22:31:43', '1', '1');

INSERT INTO `SYS_USER` VALUES ('1', '管理员', 'admin', null,'79nRuL+wDo42R5kPfXTR2A==', '2017-08-01 22:31:43', '2017-08-01 22:31:43', null, null, '1', '2017-08-01 22:31:43', '1');

INSERT INTO `SYS_RES` VALUES ('1', '系统资源', 'xtzy', '0', '_1_', '1', '1', '2017-08-15 18:41:02', '1', '1', '1', '', null);
INSERT INTO `SYS_RES` VALUES ('2', '系统管理', 'xtgl', '1', '_1_2_', '2', '1', '2017-08-01 23:02:52', '1', '1', '2', '', '1');
INSERT INTO `SYS_RES` VALUES ('3', '基础管理', 'jcgl', '2', '_1_2_3_', '3', '1', '2017-08-01 23:04:09', '1', '1', '4', '', '1');
INSERT INTO `SYS_RES` VALUES ('4', '数据字典', 'dict/toList', '3', '_1_2_3_4_', '4', '1', '2017-08-02 09:26:06', '3', '1', '8', '', '1');
INSERT INTO `SYS_RES` VALUES ('5', '列表', 'dict/list', '4', '_1_2_3_4_5_', '5', '1', '2017-08-01 23:06:54', '1', '1', '16', '', '1');
INSERT INTO `SYS_RES` VALUES ('6', '添加', 'dict/toAdd|dict/doAdd', '4', '_1_2_3_4_6_', '5', '1', '2017-08-01 23:08:51', '2', '1', '32', '', '1');
INSERT INTO `SYS_RES` VALUES ('7', '修改', 'dict/toEdit|dict/doEdit', '4', '_1_2_3_4_7_', '5', '1', '2017-08-01 23:45:35', '3', '1', '64', '', '1');
INSERT INTO `SYS_RES` VALUES ('8', '删除', 'dict/doDel', '4', '_1_2_3_4_8_', '5', '1', '2017-08-01 23:10:11', '4', '1', '128', '', '1');
INSERT INTO `SYS_RES` VALUES ('9', '移动', 'dict/toMove|dict/doMove', '4', '_1_2_3_4_9_', '5', '1', '2017-08-01 23:10:51', '5', '1', '256', '', '1');
INSERT INTO `SYS_RES` VALUES ('10', '资源管理', 'res/toList', '3', '_1_2_3_10_', '4', '1', '2017-08-02 09:25:07', '1', '1', '512', '', '1');
INSERT INTO `SYS_RES` VALUES ('11', '列表', 'res/toList|res/treeList|res/list', '10', '_1_2_3_10_11_', '5', '1', '2017-08-01 23:44:54', '1', '1', '1024', '', '1');
INSERT INTO `SYS_RES` VALUES ('12', '添加', 'res/toAdd|res/doAdd', '10', '_1_2_3_10_12_', '5', '1', '2017-08-01 23:45:25', '2', '1', '2048', '', '1');
INSERT INTO `SYS_RES` VALUES ('13', '修改', 'res/toEdit|res/doEdit', '10', '_1_2_3_10_13_', '5', '1', '2017-08-01 23:46:06', '3', '1', '4096', '', '1');
INSERT INTO `SYS_RES` VALUES ('14', '删除', 'res/doDel', '10', '_1_2_3_10_14_', '5', '1', '2017-08-01 23:46:27', '4', '1', '8192', '', '1');
INSERT INTO `SYS_RES` VALUES ('15', '移动', 'res/toMove|res/doMove', '10', '_1_2_3_10_15_', '5', '1', '2017-08-01 23:47:13', '5', '1', '16384', '', '1');
INSERT INTO `SYS_RES` VALUES ('16', '附件管理', 'file/toList', '3', '_1_2_3_16_', '4', '1', '2017-08-01 23:49:46', '5', '1', '32768', '', '1');
INSERT INTO `SYS_RES` VALUES ('17', '列表', 'file/list', '16', '_1_2_3_16_17_', '5', '1', '2017-08-01 23:52:34', '1', '1', '65536', '', '1');
INSERT INTO `SYS_RES` VALUES ('18', '上传', 'file/toUpload|file/doTempUpload|file/doUpload', '16', '_1_2_3_16_18_', '5', '1', '2017-08-01 23:51:08', '2', '1', '131072', '', '1');
INSERT INTO `SYS_RES` VALUES ('19', '下载', 'file/doDownload', '16', '_1_2_3_16_19_', '5', '1', '2017-08-01 23:51:45', '3', '1', '262144', '', '1');
INSERT INTO `SYS_RES` VALUES ('20', '删除', 'file/doDel', '16', '_1_2_3_16_20_', '5', '1', '2017-08-01 23:52:23', '4', '1', '524288', '', '1');
INSERT INTO `SYS_RES` VALUES ('21', '在线用户', 'onlineUser/toList', '3', '_1_2_3_21_', '4', '1', '2017-08-02 09:26:10', '7', '1', '1048576', '', '1');
INSERT INTO `SYS_RES` VALUES ('22', '列表', 'onlineUser/list', '21', '_1_2_3_21_22_', '5', '1', '2017-08-02 09:17:19', '1', '1', '2097152', '', '1');
INSERT INTO `SYS_RES` VALUES ('23', '强制退出', 'onlineUser/doDel', '21', '_1_2_3_21_23_', '5', '1', '2017-08-02 09:17:55', '2', '1', '4194304', '', '1');
INSERT INTO `SYS_RES` VALUES ('24', '实时日志', 'log/toList', '3', '_1_2_3_24_', '4', '1', '2017-08-02 09:20:53', '9', '1', '8388608', '', '1');
INSERT INTO `SYS_RES` VALUES ('25', '列表', 'log/list', '24', '_1_2_3_24_25_', '5', '1', '2017-08-02 09:20:01', '1', '1', '16777216', '', '1');
INSERT INTO `SYS_RES` VALUES ('26', '用户管理', 'yhgl', '2', '_1_2_26_', '3', '1', '2017-08-02 09:27:15', '3', '1', '33554432', '', '1');
INSERT INTO `SYS_RES` VALUES ('27', '组织机构', 'org/toList', '26', '_1_2_26_27_', '4', '1', '2017-08-02 09:29:33', '1', '1', '67108864', '', '1');
INSERT INTO `SYS_RES` VALUES ('28', '列表', 'org/treeList|org/list', '27', '_1_2_26_27_28_', '5', '1', '2017-08-02 09:29:50', '1', '1', '134217728', '', '1');
INSERT INTO `SYS_RES` VALUES ('29', '添加', 'org/toAdd|org/doAdd', '27', '_1_2_26_27_29_', '5', '1', '2017-08-02 09:30:03', '2', '1', '268435456', '', '1');
INSERT INTO `SYS_RES` VALUES ('30', '修改', 'org/toEdit|org/doEdit', '27', '_1_2_26_27_30_', '5', '1', '2017-08-02 09:30:52', '3', '1', '536870912', '', '1');
INSERT INTO `SYS_RES` VALUES ('31', '删除', 'org/doDel', '27', '_1_2_26_27_31_', '5', '1', '2017-08-02 09:30:47', '4', '1', '1073741824', '', '1');
INSERT INTO `SYS_RES` VALUES ('32', '移动', 'org/toMove|org/doMove', '27', '_1_2_26_27_32_', '5', '1', '2017-08-02 09:30:38', '5', '2', '1', '', '1');
INSERT INTO `SYS_RES` VALUES ('33', '岗位管理', 'post/toList', '26', '_1_2_26_33_', '4', '1', '2017-08-02 09:42:58', '3', '2', '2', '', '1');
INSERT INTO `SYS_RES` VALUES ('34', '列表', 'post/orgTreeList|post/resTreeList|post/list', '33', '_1_2_26_33_34_', '5', '1', '2017-08-02 09:35:07', '1', '2', '4', '', '1');
INSERT INTO `SYS_RES` VALUES ('35', '添加', 'post/toAdd|post/doAdd', '33', '_1_2_26_33_35_', '5', '1', '2017-08-02 09:35:23', '2', '2', '8', '', '1');
INSERT INTO `SYS_RES` VALUES ('36', '修改', 'post/toEdit|post/doEdit', '33', '_1_2_26_33_36_', '5', '1', '2017-08-02 09:35:32', '3', '2', '16', '', '1');
INSERT INTO `SYS_RES` VALUES ('37', '删除', 'post/doDel', '33', '_1_2_26_33_37_', '5', '1', '2017-08-02 09:35:41', '4', '2', '32', '', '1');
INSERT INTO `SYS_RES` VALUES ('38', '设置权限', 'post/toResUpdate|post/doResUpdate', '33', '_1_2_26_33_38_', '5', '1', '2017-08-02 09:35:57', '5', '2', '64', '', '1');
INSERT INTO `SYS_RES` VALUES ('39', '用户管理', 'user/toList', '26', '_1_2_26_39_', '4', '1', '2017-08-02 09:43:19', '5', '2', '128', '', '1');
INSERT INTO `SYS_RES` VALUES ('40', '列表', 'user/orgTreeList|user/postTreeList|user/list', '39', '_1_2_26_39_40_', '5', '1', '2017-08-02 09:45:22', '1', '2', '256', '', '1');
INSERT INTO `SYS_RES` VALUES ('41', '添加', 'user/toAdd|user/doAdd', '39', '_1_2_26_39_41_', '5', '1', '2017-08-02 09:45:30', '2', '2', '512', '', '1');
INSERT INTO `SYS_RES` VALUES ('42', '修改', 'user/toEdit|user/doEdit', '39', '_1_2_26_39_42_', '5', '1', '2017-08-02 09:45:41', '3', '2', '1024', '', '1');
INSERT INTO `SYS_RES` VALUES ('43', '删除', 'user/doDel', '39', '_1_2_26_39_43_', '5', '1', '2017-08-02 09:45:50', '4', '2', '2048', '', '1');
INSERT INTO `SYS_RES` VALUES ('44', '设置机构', 'user/toOrgUpdate|user/doOrgUpdate', '39', '_1_2_26_39_44_', '5', '1', '2017-08-02 09:46:15', '5', '2', '4096', '', '1');
INSERT INTO `SYS_RES` VALUES ('45', '设置岗位', 'user/toPostUpdate|user/doPostUpdate', '39', '_1_2_26_39_45_', '5', '1', '2017-08-02 09:46:26', '6', '2', '8192', '', '1');
INSERT INTO `SYS_RES` VALUES ('46', '初始密码', 'user/initPwd', '39', '_1_2_26_39_46_', '5', '1', '2017-08-02 09:46:41', '7', '2', '16384', '', '1');
INSERT INTO `sys_res` VALUES ('47', '定时任务', 'cron/toList', '3', '_1_2_3_47_', '4', '1', '2020-08-25 14:28:55', '11', '2', '32768', '', '1');
INSERT INTO `sys_res` VALUES ('48', '列表', 'cron/list', '47', '_1_2_3_47_48_', '5', '1', '2020-08-25 14:29:20', '1', '2', '65536', '', '1');
INSERT INTO `sys_res` VALUES ('49', '添加', 'cron/toAdd|cron/doAdd', '47', '_1_2_3_47_49_', '5', '1', '2020-08-25 14:29:57', '2', '2', '131072', '', '1');
INSERT INTO `sys_res` VALUES ('50', '修改', 'cron/toEdit|cron/doEdit', '47', '_1_2_3_47_50_', '5', '1', '2020-08-25 14:30:17', '3', '2', '262144', '', '1');
INSERT INTO `sys_res` VALUES ('51', '删除', 'cron/doDel', '47', '_1_2_3_47_51_', '5', '1', '2020-08-25 14:30:34', '4', '2', '524288', '', '1');
INSERT INTO `sys_res` VALUES ('52', '启动', 'cron/startTask', '47', '_1_2_3_47_52_', '5', '1', '2020-08-25 14:31:29', '5', '2', '1048576', '', '1');
INSERT INTO `sys_res` VALUES ('53', '停止', 'cron/stopTask', '47', '_1_2_3_47_53_', '5', '1', '2020-08-25 14:31:51', '6', '2', '2097152', '', '1');
INSERT INTO `sys_res` VALUES ('54', '执行一次', 'cron/runOnceTask', '47', '_1_2_3_47_54_', '5', '1', '2020-08-25 14:32:13', '7', '2', '4194304', '', '1');

INSERT INTO `SYS_DICT` VALUES ('1', 'STATE', '0', '删除', '1');
INSERT INTO `SYS_DICT` VALUES ('2', 'STATE', '1', '正常', '2');
INSERT INTO `SYS_DICT` VALUES ('3', 'RES_TYPE', '1', '后台', '1');
INSERT INTO `SYS_DICT` VALUES ('4', 'RES_TYPE', '2', '前台', '2');
INSERT INTO `SYS_DICT` VALUES ('5', 'CRON_TYPE', '1', '启动', '1');
INSERT INTO `SYS_DICT` VALUES ('6', 'CRON_TYPE', '2', '停止', '2');

INSERT INTO `sys_cron` VALUES ('1', '清理临时附件', 'com.wcpdoc.exam.file.job.ClearFileJob', '0 0 0 1/1 * ? ', '1', '1', '2020-08-26 18:42:08');

INSERT INTO `SYS_VER` VALUES (1, '1.1.1', '2019-02-23 15:35:21', 'zhanghc', '初始版本');
INSERT INTO `SYS_VER` VALUES (2, '1.1.2', '2019-03-03 13:20:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (3, '1.1.3', '2019-08-14 18:49:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (4, '1.1.4', '2019-09-05 09:58:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (5, '1.1.5', '2019-12-16 23:16:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (6, '2.0.0', '2020-09-04 16:37:00', 'zhanghc', '');