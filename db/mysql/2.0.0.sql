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

drop table if exists EXM_QUESTION;

/*==============================================================*/
/* Table: EXM_QUESTION                                          */
/*==============================================================*/
create table EXM_QUESTION
(
   ID                   int not null auto_increment,
   TYPE                 int comment '1：单选；2：多选；3：填空；4：判断；5：问答',
   DIFFICULTY           int comment '1：极易；2：简单；3：适中；4：困难；5：极难',
   TITLE                text comment '题干',
   OPTION_A             text comment '选项A',
   OPTION_B             text comment '选项B',
   OPTION_C             text comment '选项C',
   OPTION_D             text comment '选项D',
   OPTION_E             text comment '选项E',
   OPTION_F             text comment '选项F',
   OPTION_G             text comment '选项G',
   ANSWER               text comment '答案',
   ANALYSIS             text comment '解析',
   STATE                int comment '0：删除；1：启用；2：禁用',
   UPDATE_USER_ID       int comment '更新人',
   UPDATE_TIME          datetime comment '更新时间',
   QUESTION_TYPE_ID     int comment '试题分类',
   SCORE                decimal(5,2) comment '默认分值',
   SCORE_OPTIONS        varchar(8) comment '1：半对半分（默认全对得分）；2：答案无顺序（默认答案有前后顺序）；3：大小写不敏感（默认大小写敏感）；4：包含答案得分（默认等于答案得分）',
   VER                  int comment '版本',
   SRC_ID               int comment '源ID',
   NO                   int comment '排序',
   primary key (ID)
);

alter table EXM_QUESTION comment '试题';

alter table EXM_QUESTION add constraint FK_Reference_15 foreign key (QUESTION_TYPE_ID)
      references EXM_QUESTION_TYPE (ID) on delete restrict on update restrict;

drop table if exists EXM_PAPER_TYPE;

/*==============================================================*/
/* Table: EXM_PAPER_TYPE                                        */
/*==============================================================*/
create table EXM_PAPER_TYPE
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

alter table EXM_PAPER_TYPE comment '试卷分类';

drop table if exists EXM_PAPER;

/*==============================================================*/
/* Table: EXM_PAPER                                             */
/*==============================================================*/
create table EXM_PAPER
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   PREVIEW_TYPE         int comment '1：整卷展示；2：单题展示；数据字典：PAPER_PREVIEW_TYPE',
   PASS_SCORE           decimal(5,2) comment '及格分数',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   SCORE_A              decimal(5,2) comment '分数A',
   SCORE_A_REMARK       varchar(32) comment '分数A评语',
   SCORE_B              decimal(5,2) comment '分数B',
   SCORE_B_REMARK       varchar(32) comment '分数B评语',
   SCORE_C              decimal(5,2) comment '分数C',
   SCORE_C_REMARK       varchar(32) comment '分数C评语',
   SCORE_D              decimal(5,2) comment '分数D',
   SCORE_D_REMARK       varchar(32) comment '分数D评语',
   SCORE_E              decimal(5,2) comment '分数E',
   SCORE_E_REMARK       varchar(32) comment '分数E评语',
   DESCRIPTION          text comment '描述',
   PAPER_TYPE_ID        int comment '试卷分类',
   STATE                int comment '0：删除；1：启用；2：禁用',
   UPDATE_USER_ID       int comment '更新人',
   UPDATE_TIME          datetime comment '更新时间',
   primary key (ID)
);

alter table EXM_PAPER comment '试卷';

alter table EXM_PAPER add constraint FK_Reference_16 foreign key (PAPER_TYPE_ID)
      references EXM_PAPER_TYPE (ID) on delete restrict on update restrict;

drop table if exists EXM_PAPER_QUESTION;

/*==============================================================*/
/* Table: EXM_PAPER_QUESTION                                    */
/*==============================================================*/
create table EXM_PAPER_QUESTION
(
   ID                   int not null auto_increment comment 'id',
   NAME                 varchar(32) comment '章节名称',
   DESCRIPTION          varchar(512) comment '章节描述',
   PARENT_ID            int comment '父ID',
   PARENT_SUB           varchar(512) comment '父子关系（格式：_父ID_子ID_子子ID_... ...）',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   PAPER_ID             int comment '试卷ID',
   QUESTION_ID          int comment '试题ID',
   TYPE                 int comment '1：章节；2：固定试题；3：随机试题',
   SCORE                decimal(5,2) comment '分数',
   SCORE_OPTIONS        varchar(8) comment '1：半对半分（默认全对得分）；2：答案无顺序（默认答案有前后顺序）；3：大小写不敏感（默认大小写敏感）；4：包含答案得分（默认等于答案得分）',
   NO                   int comment '排序',
   primary key (ID)
);

alter table EXM_PAPER_QUESTION comment '试卷试题';

alter table EXM_PAPER_QUESTION add constraint FK_Reference_17 foreign key (QUESTION_ID)
      references EXM_QUESTION (ID) on delete restrict on update restrict;

alter table EXM_PAPER_QUESTION add constraint FK_Reference_18 foreign key (PAPER_ID)
      references EXM_PAPER (ID) on delete restrict on update restrict;

drop table if exists EXM_EXAM_TYPE;

/*==============================================================*/
/* Table: EXM_EXAM_TYPE                                         */
/*==============================================================*/
create table EXM_EXAM_TYPE
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

alter table EXM_EXAM_TYPE comment '考试分类';

drop table if exists EXM_EXAM;

/*==============================================================*/
/* Table: EXM_EXAM                                              */
/*==============================================================*/
create table EXM_EXAM
(
   ID                   int not null auto_increment comment 'id',
   NAME                 varchar(32) comment '名称',
   PASS_SCORE           decimal(5,2) comment '及格分数',
   START_TIME           datetime comment '考试开始',
   END_TIME             datetime comment '考试结束',
   MARK_START_TIME      datetime comment '判卷开始',
   MARK_END_TIME        datetime comment '判卷结束',
   GRADE_TYPE           int comment '1：展示；2：不展示',
   SCORE_A              decimal(5,2) comment '分数A',
   SCORE_A_REMARK       varchar(32) comment '分数A评语',
   SCORE_B              decimal(5,2) comment '分数B',
   SCORE_B_REMARK       varchar(32) comment '分数B评语',
   SCORE_C              decimal(5,2) comment '分数C',
   SCORE_C_REMARK       varchar(32) comment '分数C评语',
   SCORE_D              decimal(5,2) comment '分数D',
   SCORE_D_REMARK       varchar(32) comment '分数D评语',
   SCORE_E              decimal(5,2) comment '分数E',
   SCORE_E_REMARK       varchar(32) comment '分数E评语',
   DESCRIPTION          varchar(512) comment '描述',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：发布；2：草稿',
   PAPER_ID             int comment '试卷ID',
   EXAM_TYPE_ID         int comment '考试分类',
   primary key (ID)
);

alter table EXM_EXAM comment '考试';

alter table EXM_EXAM add constraint FK_Reference_19 foreign key (PAPER_ID)
      references EXM_PAPER (ID) on delete restrict on update restrict;

alter table EXM_EXAM add constraint FK_Reference_22 foreign key (EXAM_TYPE_ID)
      references EXM_EXAM_TYPE (ID) on delete restrict on update restrict;

drop table if exists EXM_MY_MARK;

/*==============================================================*/
/* Table: EXM_MY_MARK                                           */
/*==============================================================*/
create table EXM_MY_MARK
(
   ID                   int not null auto_increment comment 'id',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '用户ID',
   primary key (ID)
);

alter table EXM_MY_MARK comment '我的阅卷';

alter table EXM_MY_MARK add constraint FK_Reference_37 foreign key (EXAM_ID)
      references EXM_EXAM (ID) on delete restrict on update restrict;

drop table if exists EXM_MY_EXAM;

/*==============================================================*/
/* Table: EXM_MY_EXAM                                           */
/*==============================================================*/
create table EXM_MY_EXAM
(
   ID                   int not null auto_increment comment 'id',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '用户ID',
   ANSWER_TIME          datetime comment '答题时间',
   ANSWER_FINISH_TIME   datetime comment '答题完成时间',
   MARK_USER_ID         int comment '阅卷人',
   MARK_TIME            datetime comment '阅卷时间',
   MARK_FINISH_TIME     datetime comment '阅卷完成时间',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   STATE                int comment '1：未考试；2：考试中；3：已交卷；4：强制交卷；',
   MARK_STATE           int comment '1：未阅卷；2：阅卷中；3：已阅卷；',
   ANSWER_STATE         int comment '1：及格；2：不及格',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_EXAM comment '我的考试';

alter table EXM_MY_EXAM add constraint FK_Reference_23 foreign key (EXAM_ID)
      references EXM_EXAM (ID) on delete restrict on update restrict;

drop table if exists EXM_MY_EXAM_DETAIL;

/*==============================================================*/
/* Table: EXM_MY_EXAM_DETAIL                                    */
/*==============================================================*/
create table EXM_MY_EXAM_DETAIL
(
   ID                   int not null auto_increment comment 'id',
   MY_EXAM_ID           int comment '考试用户ID',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '用户ID',
   QUESTION_ID          int comment '试题ID',
   ANSWER_TIME          datetime comment '答题时间',
   MARK_USER_ID         int comment '阅卷人ID',
   MARK_TIME            datetime comment '阅卷时间',
   ANSWER               text comment '答案',
   SCORE                decimal(5,2) comment '得分',
   primary key (ID)
);

alter table EXM_MY_EXAM_DETAIL comment '我的考试详细';

alter table EXM_MY_EXAM_DETAIL add constraint FK_Reference_24 foreign key (MY_EXAM_ID)
      references EXM_MY_EXAM (ID) on delete restrict on update restrict;

alter table EXM_MY_EXAM_DETAIL add constraint FK_Reference_25 foreign key (QUESTION_ID)
      references EXM_QUESTION (ID) on delete restrict on update restrict;


/*==============================================================*/
/* 数据								*/
/*==============================================================*/

INSERT INTO `SYS_ORG` VALUES ('1', '组织机构', 'code', '0', '_1_', '1', '1', '2017-08-01 22:31:43', '1', '1');

INSERT INTO `SYS_USER` VALUES ('1', '管理员', 'admin', null,'79nRuL+wDo42R5kPfXTR2A==', '2017-08-01 22:31:43', '2017-08-01 22:31:43', null, null, '1', '2017-08-01 22:31:43', '1');

INSERT INTO `SYS_RES` VALUES (1, '系统资源', 'xtzy', 0, '_1_', 1, 1, '2017-08-15 18:41:02', 1, 1, 1, '', NULL);
INSERT INTO `SYS_RES` VALUES (2, '系统管理', 'xtgl', 1, '_1_2_', 2, 1, '2017-08-01 23:02:52', 1, 1, 2, '', 1);
INSERT INTO `SYS_RES` VALUES (3, '基础管理', 'jcgl', 2, '_1_2_3_', 3, 1, '2017-08-01 23:04:09', 1, 1, 4, '', 1);
INSERT INTO `SYS_RES` VALUES (4, '数据字典', 'dict/toList', 3, '_1_2_3_4_', 4, 1, '2017-08-02 09:26:06', 3, 1, 8, '', 1);
INSERT INTO `SYS_RES` VALUES (5, '列表', 'dict/list', 4, '_1_2_3_4_5_', 5, 1, '2017-08-01 23:06:54', 1, 1, 16, '', 1);
INSERT INTO `SYS_RES` VALUES (6, '添加', 'dict/toAdd|dict/doAdd', 4, '_1_2_3_4_6_', 5, 1, '2017-08-01 23:08:51', 2, 1, 32, '', 1);
INSERT INTO `SYS_RES` VALUES (7, '修改', 'dict/toEdit|dict/doEdit', 4, '_1_2_3_4_7_', 5, 1, '2017-08-01 23:45:35', 3, 1, 64, '', 1);
INSERT INTO `SYS_RES` VALUES (8, '删除', 'dict/doDel', 4, '_1_2_3_4_8_', 5, 1, '2017-08-01 23:10:11', 4, 1, 128, '', 1);
INSERT INTO `SYS_RES` VALUES (9, '移动', 'dict/toMove|dict/doMove', 4, '_1_2_3_4_9_', 5, 1, '2017-08-01 23:10:51', 5, 1, 256, '', 1);
INSERT INTO `SYS_RES` VALUES (10, '资源管理', 'res/toList', 3, '_1_2_3_10_', 4, 1, '2017-08-02 09:25:07', 1, 1, 512, '', 1);
INSERT INTO `SYS_RES` VALUES (11, '列表', 'res/toList|res/treeList|res/list', 10, '_1_2_3_10_11_', 5, 1, '2017-08-01 23:44:54', 1, 1, 1024, '', 1);
INSERT INTO `SYS_RES` VALUES (12, '添加', 'res/toAdd|res/doAdd', 10, '_1_2_3_10_12_', 5, 1, '2017-08-01 23:45:25', 2, 1, 2048, '', 1);
INSERT INTO `SYS_RES` VALUES (13, '修改', 'res/toEdit|res/doEdit', 10, '_1_2_3_10_13_', 5, 1, '2017-08-01 23:46:06', 3, 1, 4096, '', 1);
INSERT INTO `SYS_RES` VALUES (14, '删除', 'res/doDel', 10, '_1_2_3_10_14_', 5, 1, '2017-08-01 23:46:27', 4, 1, 8192, '', 1);
INSERT INTO `SYS_RES` VALUES (15, '移动', 'res/toMove|res/doMove', 10, '_1_2_3_10_15_', 5, 1, '2017-08-01 23:47:13', 5, 1, 16384, '', 1);
INSERT INTO `SYS_RES` VALUES (16, '附件管理', 'file/toList', 3, '_1_2_3_16_', 4, 1, '2017-08-01 23:49:46', 5, 1, 32768, '', 1);
INSERT INTO `SYS_RES` VALUES (17, '列表', 'file/list', 16, '_1_2_3_16_17_', 5, 1, '2017-08-01 23:52:34', 1, 1, 65536, '', 1);
INSERT INTO `SYS_RES` VALUES (18, '上传', 'file/toUpload|file/doTempUpload|file/doUpload', 16, '_1_2_3_16_18_', 5, 1, '2017-08-01 23:51:08', 2, 1, 131072, '', 1);
INSERT INTO `SYS_RES` VALUES (19, '下载', 'file/doDownload', 16, '_1_2_3_16_19_', 5, 1, '2017-08-01 23:51:45', 3, 1, 262144, '', 1);
INSERT INTO `SYS_RES` VALUES (20, '删除', 'file/doDel', 16, '_1_2_3_16_20_', 5, 1, '2017-08-01 23:52:23', 4, 1, 524288, '', 1);
INSERT INTO `SYS_RES` VALUES (21, '在线用户', 'onlineUser/toList', 3, '_1_2_3_21_', 4, 1, '2017-08-02 09:26:10', 7, 1, 1048576, '', 1);
INSERT INTO `SYS_RES` VALUES (22, '列表', 'onlineUser/list', 21, '_1_2_3_21_22_', 5, 1, '2020-09-08 14:40:08', 1, 1, 2097152, '', 1);
INSERT INTO `SYS_RES` VALUES (23, '强制退出', 'onlineUser/doDel', 21, '_1_2_3_21_23_', 5, 1, '2017-08-02 09:17:55', 2, 1, 4194304, '', 1);
INSERT INTO `SYS_RES` VALUES (24, '实时日志', 'log/toList', 3, '_1_2_3_24_', 4, 1, '2017-08-02 09:20:53', 9, 1, 8388608, '', 1);
INSERT INTO `SYS_RES` VALUES (25, '列表', 'log/list', 24, '_1_2_3_24_25_', 5, 1, '2020-09-08 14:38:22', 1, 1, 16777216, '', 1);
INSERT INTO `SYS_RES` VALUES (26, '用户管理', 'yhgl', 2, '_1_2_26_', 3, 1, '2017-08-02 09:27:15', 3, 1, 33554432, '', 1);
INSERT INTO `SYS_RES` VALUES (27, '组织机构', 'org/toList', 26, '_1_2_26_27_', 4, 1, '2017-08-02 09:29:33', 1, 1, 67108864, '', 1);
INSERT INTO `SYS_RES` VALUES (28, '列表', 'org/treeList|org/list', 27, '_1_2_26_27_28_', 5, 1, '2017-08-02 09:29:50', 1, 1, 134217728, '', 1);
INSERT INTO `SYS_RES` VALUES (29, '添加', 'org/toAdd|org/doAdd', 27, '_1_2_26_27_29_', 5, 1, '2017-08-02 09:30:03', 2, 1, 268435456, '', 1);
INSERT INTO `SYS_RES` VALUES (30, '修改', 'org/toEdit|org/doEdit', 27, '_1_2_26_27_30_', 5, 1, '2017-08-02 09:30:52', 3, 1, 536870912, '', 1);
INSERT INTO `SYS_RES` VALUES (31, '删除', 'org/doDel', 27, '_1_2_26_27_31_', 5, 1, '2017-08-02 09:30:47', 4, 1, 1073741824, '', 1);
INSERT INTO `SYS_RES` VALUES (32, '移动', 'org/toMove|org/doMove', 27, '_1_2_26_27_32_', 5, 1, '2017-08-02 09:30:38', 5, 2, 1, '', 1);
INSERT INTO `SYS_RES` VALUES (33, '岗位管理', 'post/toList', 26, '_1_2_26_33_', 4, 1, '2017-08-02 09:42:58', 3, 2, 2, '', 1);
INSERT INTO `SYS_RES` VALUES (34, '列表', 'post/orgTreeList|post/resTreeList|post/list', 33, '_1_2_26_33_34_', 5, 1, '2017-08-02 09:35:07', 1, 2, 4, '', 1);
INSERT INTO `SYS_RES` VALUES (35, '添加', 'post/toAdd|post/doAdd', 33, '_1_2_26_33_35_', 5, 1, '2017-08-02 09:35:23', 2, 2, 8, '', 1);
INSERT INTO `SYS_RES` VALUES (36, '修改', 'post/toEdit|post/doEdit', 33, '_1_2_26_33_36_', 5, 1, '2017-08-02 09:35:32', 3, 2, 16, '', 1);
INSERT INTO `SYS_RES` VALUES (37, '删除', 'post/doDel', 33, '_1_2_26_33_37_', 5, 1, '2017-08-02 09:35:41', 4, 2, 32, '', 1);
INSERT INTO `SYS_RES` VALUES (38, '设置权限', 'post/toResUpdate|post/doResUpdate', 33, '_1_2_26_33_38_', 5, 1, '2017-08-02 09:35:57', 5, 2, 64, '', 1);
INSERT INTO `SYS_RES` VALUES (39, '用户管理', 'user/toList', 26, '_1_2_26_39_', 4, 1, '2017-08-02 09:43:19', 5, 2, 128, '', 1);
INSERT INTO `SYS_RES` VALUES (40, '列表', 'user/orgTreeList|user/postTreeList|user/list', 39, '_1_2_26_39_40_', 5, 1, '2017-08-02 09:45:22', 1, 2, 256, '', 1);
INSERT INTO `SYS_RES` VALUES (41, '添加', 'user/toAdd|user/doAdd', 39, '_1_2_26_39_41_', 5, 1, '2017-08-02 09:45:30', 2, 2, 512, '', 1);
INSERT INTO `SYS_RES` VALUES (42, '修改', 'user/toEdit|user/doEdit', 39, '_1_2_26_39_42_', 5, 1, '2017-08-02 09:45:41', 3, 2, 1024, '', 1);
INSERT INTO `SYS_RES` VALUES (43, '删除', 'user/doDel', 39, '_1_2_26_39_43_', 5, 1, '2017-08-02 09:45:50', 4, 2, 2048, '', 1);
INSERT INTO `SYS_RES` VALUES (44, '设置机构', 'user/toOrgUpdate|user/doOrgUpdate', 39, '_1_2_26_39_44_', 5, 1, '2017-08-02 09:46:15', 5, 2, 4096, '', 1);
INSERT INTO `SYS_RES` VALUES (45, '设置岗位', 'user/toPostUpdate|user/doPostUpdate', 39, '_1_2_26_39_45_', 5, 1, '2017-08-02 09:46:26', 6, 2, 8192, '', 1);
INSERT INTO `SYS_RES` VALUES (46, '初始密码', 'user/initPwd', 39, '_1_2_26_39_46_', 5, 1, '2017-08-02 09:46:41', 7, 2, 16384, '', 1);
INSERT INTO `SYS_RES` VALUES (47, '定时任务', 'cron/toList', 3, '_1_2_3_47_', 4, 1, '2020-08-25 14:28:55', 11, 2, 32768, '', 1);
INSERT INTO `SYS_RES` VALUES (48, '列表', 'cron/list', 47, '_1_2_3_47_48_', 5, 1, '2020-08-25 14:29:20', 1, 2, 65536, '', 1);
INSERT INTO `SYS_RES` VALUES (49, '添加', 'cron/toAdd|cron/doAdd', 47, '_1_2_3_47_49_', 5, 1, '2020-08-25 14:29:57', 2, 2, 131072, '', 1);
INSERT INTO `SYS_RES` VALUES (50, '修改', 'cron/toEdit|cron/doEdit', 47, '_1_2_3_47_50_', 5, 1, '2020-08-25 14:30:17', 3, 2, 262144, '', 1);
INSERT INTO `SYS_RES` VALUES (51, '删除', 'cron/doDel', 47, '_1_2_3_47_51_', 5, 1, '2020-08-25 14:30:34', 4, 2, 524288, '', 1);
INSERT INTO `SYS_RES` VALUES (52, '启动', 'cron/startTask', 47, '_1_2_3_47_52_', 5, 1, '2020-08-25 14:31:29', 5, 2, 1048576, '', 1);
INSERT INTO `SYS_RES` VALUES (53, '停止', 'cron/stopTask', 47, '_1_2_3_47_53_', 5, 1, '2020-08-25 14:31:51', 6, 2, 2097152, '', 1);
INSERT INTO `SYS_RES` VALUES (54, '执行一次', 'cron/runOnceTask', 47, '_1_2_3_47_54_', 5, 1, '2020-08-25 14:32:13', 7, 2, 4194304, '', 1);
INSERT INTO `SYS_RES` VALUES (55, '考试管理', 'ksgl', 2, '_1_2_55_', 3, 1, '2020-09-07 09:48:58', 5, 2, 8388608, '', 1);
INSERT INTO `SYS_RES` VALUES (56, '试题分类', 'questionType/toList', 55, '_1_2_55_56_', 4, 1, '2020-09-07 09:50:33', 1, 2, 16777216, '', 1);
INSERT INTO `SYS_RES` VALUES (59, '列表', 'questionType/treeList|questionType/list', 56, '_1_2_55_56_59_', 5, 2, '2020-09-07 13:22:45', 1, 2, 33554432, '', 1);
INSERT INTO `SYS_RES` VALUES (60, '添加', 'questionType/toAdd|questionType/doAdd', 56, '_1_2_55_56_60_', 5, 2, '2020-09-07 13:25:32', 2, 2, 67108864, '', 1);
INSERT INTO `SYS_RES` VALUES (61, '修改', 'questionType/toEdit|questionType/doEdit', 56, '_1_2_55_56_61_', 5, 2, '2020-09-07 13:25:43', 3, 2, 134217728, '', 1);
INSERT INTO `SYS_RES` VALUES (62, '删除', 'questionType/doDel', 56, '_1_2_55_56_62_', 5, 2, '2020-09-07 13:25:59', 4, 2, 268435456, '', 1);
INSERT INTO `SYS_RES` VALUES (63, '操作权限', 'questionType/toAuth|questionType/authUserList|questionType/authPostList|questionType/authOrgList|questionType/doAuth', 56, '_1_2_55_56_63_', 5, 1, '2020-09-12 14:48:07', 5, 2, 536870912, '', 1);
INSERT INTO `SYS_RES` VALUES (64, '试题管理', 'question/toList', 55, '_1_2_55_64_', 4, 2, '2020-09-07 13:58:08', 3, 2, 1073741824, '', 1);
INSERT INTO `SYS_RES` VALUES (65, '列表', 'question/questionTypeTreeList|question/list', 64, '_1_2_55_64_65_', 5, 1, '2020-09-08 13:26:15', 1, 3, 1, '', 1);
INSERT INTO `SYS_RES` VALUES (66, '添加', 'question/toAdd|question/doAdd', 64, '_1_2_55_64_66_', 5, 1, '2020-09-08 13:26:37', 2, 3, 2, '', 1);
INSERT INTO `SYS_RES` VALUES (67, '修改', 'question/toEdit|question/doEdit', 64, '_1_2_55_64_67_', 5, 1, '2020-09-08 13:26:57', 3, 3, 4, '', 1);
INSERT INTO `SYS_RES` VALUES (68, '删除', 'question/doDel', 64, '_1_2_55_64_68_', 5, 1, '2020-09-08 13:27:16', 4, 3, 8, '', 1);
INSERT INTO `SYS_RES` VALUES (69, '导入试题', 'question/wordImp', 64, '_1_2_55_64_69_', 5, 2, '2020-10-19 15:24:18', 6, 3, 16, '', 1);
INSERT INTO `SYS_RES` VALUES (70, '下载模板', 'question/wordTemplateExport', 64, '_1_2_55_64_70_', 5, 2, '2020-10-19 15:24:13', 7, 3, 32, '', 1);
INSERT INTO `SYS_RES` VALUES (71, '试卷分类', 'paperType/toList', 55, '_1_2_55_71_', 4, 1, '2020-09-12 14:54:19', 5, 3, 64, '', 1);
INSERT INTO `SYS_RES` VALUES (72, '列表', 'paperType/treeList|paperType/list', 71, '_1_2_55_71_72_', 5, 1, '2020-09-12 14:45:10', 1, 3, 128, '', 1);
INSERT INTO `SYS_RES` VALUES (73, '添加', 'paperType/toAdd|paperType/doAdd', 71, '_1_2_55_71_73_', 5, 1, '2020-09-12 14:45:29', 2, 3, 256, '', 1);
INSERT INTO `SYS_RES` VALUES (74, '修改', 'paperType/toEdit|paperType/doEdit', 71, '_1_2_55_71_74_', 5, 1, '2020-09-12 14:45:45', 3, 3, 512, '', 1);
INSERT INTO `SYS_RES` VALUES (75, '删除', 'paperType/doDel', 71, '_1_2_55_71_75_', 5, 1, '2020-09-12 14:45:57', 4, 3, 1024, '', 1);
INSERT INTO `SYS_RES` VALUES (76, '操作权限', 'paperType/toAuth|paperType/authUserList|paperType/authPostList|paperType/authOrgList|paperType/doAuth', 71, '_1_2_55_71_76_', 5, 1, '2020-09-12 14:47:46', 5, 3, 2048, '', 1);
INSERT INTO `SYS_RES` VALUES (77, '试卷管理', 'paper/toList', 55, '_1_2_55_77_', 4, 1, '2020-09-14 10:55:01', 7, 3, 4096, '', 1);
INSERT INTO `SYS_RES` VALUES (78, '列表', 'paper/paperTypeTreeList|paper/list', 77, '_1_2_55_77_78_', 5, 1, '2020-09-14 10:55:39', 1, 3, 8192, '', 1);
INSERT INTO `SYS_RES` VALUES (79, '添加', 'paper/toAdd|paper/doAdd', 77, '_1_2_55_77_79_', 5, 1, '2020-09-14 10:55:54', 2, 3, 16384, '', 1);
INSERT INTO `SYS_RES` VALUES (80, '修改', 'paper/toEdit|paper/doEdit', 77, '_1_2_55_77_80_', 5, 1, '2020-09-14 11:00:19', 3, 3, 32768, '', 1);
INSERT INTO `SYS_RES` VALUES (81, '删除', 'paper/doDel', 77, '_1_2_55_77_81_', 5, 1, '2020-09-14 11:00:33', 4, 3, 65536, '', 1);
INSERT INTO `SYS_RES` VALUES (82, '配置试卷', 'paper/toCfg|paper/toChapterAdd|paper/doChapterAdd|paper/toChapterEdit|paper/doChapterEdit|paper/doChapterDel|paper/doChapterUp|paper/doChapterDown|paper/toQuestionAdd|paper/questionTypeTreeList|paper/questionList|paper/doQuestionAdd|paper/doScoreUpdate|paper/doOptionsUpdate|paper/doQuestionUp|paper/doQuestionDown|paper/doQuestionDel|paper/doQuestionClear', 77, '_1_2_55_77_82_', 5, 1, '2020-09-28 13:30:06', 5, 3, 131072, '', 1);
INSERT INTO `SYS_RES` VALUES (83, '考试分类', 'examType/toList', 55, '_1_2_55_83_', 4, 1, '2020-09-28 13:32:35', 9, 3, 262144, '', 1);
INSERT INTO `SYS_RES` VALUES (84, '列表', 'examType/treeList|examType/list', 83, '_1_2_55_83_84_', 5, 1, '2020-09-28 13:33:17', 1, 3, 524288, '', 1);
INSERT INTO `SYS_RES` VALUES (85, '添加', 'examType/toAdd|examType/doAdd', 83, '_1_2_55_83_85_', 5, 1, '2020-09-28 13:33:27', 2, 3, 1048576, '', 1);
INSERT INTO `SYS_RES` VALUES (86, '修改', 'examType/toEdit|examType/doEdit', 83, '_1_2_55_83_86_', 5, 1, '2020-09-28 13:33:45', 3, 3, 2097152, '', 1);
INSERT INTO `SYS_RES` VALUES (87, '删除', 'examType/doDel', 83, '_1_2_55_83_87_', 5, 1, '2020-09-28 13:33:57', 4, 3, 4194304, '', 1);
INSERT INTO `SYS_RES` VALUES (88, '操作权限', 'examType/toAuth|examType/authUserList|examType/authPostList|paperType/authOrgList|examType/doAuth', 83, '_1_2_55_83_88_', 5, 1, '2020-09-28 13:35:26', 5, 3, 8388608, '', 1);
INSERT INTO `SYS_RES` VALUES (89, '考试管理', 'exam/toList', 55, '_1_2_55_89_', 4, 1, '2020-09-28 13:36:34', 11, 3, 16777216, '', 1);
INSERT INTO `SYS_RES` VALUES (90, '列表', 'exam/examTypeTreeList|exam/paperList|exam/userList|exam/list', 89, '_1_2_55_89_90_', 5, 1, '2020-09-30 14:44:01', 1, 3, 33554432, '', 1);
INSERT INTO `SYS_RES` VALUES (91, '添加', 'exam/toAdd|exam/doAdd', 89, '_1_2_55_89_91_', 5, 1, '2020-09-30 14:44:27', 2, 3, 67108864, '', 1);
INSERT INTO `SYS_RES` VALUES (92, '修改', 'exam/toEdit|exam/doEdit', 89, '_1_2_55_89_92_', 5, 1, '2020-09-30 14:44:52', 3, 3, 134217728, '', 1);
INSERT INTO `SYS_RES` VALUES (93, '删除', 'exam/doDel', 89, '_1_2_55_89_93_', 5, 1, '2020-09-30 14:45:15', 4, 3, 268435456, '', 1);
INSERT INTO `SYS_RES` VALUES (94, '考试配置', 'exam/toCfg|exam/doCfg', 89, '_1_2_55_89_94_', 5, 1, '2020-09-30 15:55:53', 5, 3, 536870912, '', 1);
INSERT INTO `SYS_RES` VALUES (95, '发布', 'exam/doPublish', 89, '_1_2_55_89_95_', 5, 1, '2020-09-30 15:56:15', 6, 3, 1073741824, '', 1);
INSERT INTO `SYS_RES` VALUES (96, '我的考试', 'wdks', 2, '_1_2_96_', 3, 1, '2020-10-09 09:28:59', 7, 4, 1, '', 1);
INSERT INTO `SYS_RES` VALUES (97, '我的考试', 'myExam/toList', 96, '_1_2_96_97_', 4, 1, '2020-10-09 09:29:44', 1, 4, 2, '', 1);
INSERT INTO `SYS_RES` VALUES (98, '列表', 'myExam/list', 97, '_1_2_96_97_98_', 5, 1, '2020-10-09 09:30:21', 1, 4, 4, '', 1);
INSERT INTO `SYS_RES` VALUES (99, '考试', 'myExam/toExam|myExam/updateAnswer|myExam/doExam', 97, '_1_2_96_97_99_', 5, 2, '2020-10-15 15:55:24', 2, 4, 8, '', 1);
INSERT INTO `SYS_RES` VALUES (100, '在线阅卷', 'myMark/toList', 96, '_1_2_96_100_', 4, 2, '2020-10-12 15:43:08', 3, 4, 16, '', 1);
INSERT INTO `SYS_RES` VALUES (101, '列表', 'myMark/list|myMark/toDetailList|myMark/detailList', 100, '_1_2_96_100_101_', 5, 2, '2020-10-12 17:14:17', 1, 4, 32, '', 1);
INSERT INTO `SYS_RES` VALUES (102, '阅卷', 'myMark/toMark|myMark/doScoreUpdate|myMark/doMark', 100, '_1_2_96_100_102_', 5, 1, '2020-10-22 13:43:28', 2, 4, 64, '', 1);
INSERT INTO `SYS_RES` VALUES (103, '发布', 'question/doPublish', 64, '_1_2_55_64_103_', 5, 2, '2020-10-19 15:24:03', 5, 4, 128, '', 1);
INSERT INTO `SYS_RES` VALUES (104, '自动阅卷', 'myMark/doAutoMark', 100, '_1_2_96_100_104_', 5, 1, '2020-10-22 13:43:41', 3, 4, 256, '', 1);

INSERT INTO `SYS_DICT` VALUES (1, 'STATE', '0', '删除', 1);
INSERT INTO `SYS_DICT` VALUES (2, 'STATE', '1', '启用', 2);
INSERT INTO `SYS_DICT` VALUES (3, 'STATE', '2', '禁用', 3);
INSERT INTO `SYS_DICT` VALUES (4, 'RES_TYPE', '1', '后台', 1);
INSERT INTO `SYS_DICT` VALUES (5, 'RES_TYPE', '2', '前台', 2);
INSERT INTO `SYS_DICT` VALUES (6, 'CRON_TYPE', '1', '启动', 1);
INSERT INTO `SYS_DICT` VALUES (7, 'CRON_TYPE', '2', '停止', 2);
INSERT INTO `SYS_DICT` VALUES (8, 'QUESTION_TYPE', '1', '单选', 1);
INSERT INTO `SYS_DICT` VALUES (9, 'QUESTION_TYPE', '2', '多选', 2);
INSERT INTO `SYS_DICT` VALUES (10, 'QUESTION_TYPE', '3', '填空', 3);
INSERT INTO `SYS_DICT` VALUES (11, 'QUESTION_TYPE', '4', '判断', 4);
INSERT INTO `SYS_DICT` VALUES (12, 'QUESTION_TYPE', '5', '问答', 5);
INSERT INTO `SYS_DICT` VALUES (13, 'QUESTION_DIFFICULTY', '1', '极易', 1);
INSERT INTO `SYS_DICT` VALUES (14, 'QUESTION_DIFFICULTY', '2', '简单', 2);
INSERT INTO `SYS_DICT` VALUES (15, 'QUESTION_DIFFICULTY', '3', '适中', 3);
INSERT INTO `SYS_DICT` VALUES (16, 'QUESTION_DIFFICULTY', '4', '困难', 4);
INSERT INTO `SYS_DICT` VALUES (17, 'QUESTION_DIFFICULTY', '5', '极难', 5);
INSERT INTO `SYS_DICT` VALUES (18, 'QUESTION_OPTIONS', '1', 'A', 1);
INSERT INTO `SYS_DICT` VALUES (19, 'QUESTION_OPTIONS', '2', 'B', 2);
INSERT INTO `SYS_DICT` VALUES (20, 'QUESTION_OPTIONS', '3', 'C', 3);
INSERT INTO `SYS_DICT` VALUES (21, 'QUESTION_OPTIONS', '4', 'D', 4);
INSERT INTO `SYS_DICT` VALUES (22, 'QUESTION_OPTIONS', '5', 'E', 5);
INSERT INTO `SYS_DICT` VALUES (23, 'QUESTION_OPTIONS', '6', 'F', 6);
INSERT INTO `SYS_DICT` VALUES (24, 'QUESTION_OPTIONS', '7', 'G', 7);
INSERT INTO `SYS_DICT` VALUES (25, 'MY_EXAM_STATE', '1', '未考试', 1);
INSERT INTO `SYS_DICT` VALUES (26, 'MY_EXAM_STATE', '2', '考试中', 2);
INSERT INTO `SYS_DICT` VALUES (27, 'MY_EXAM_STATE', '3', '已交卷', 3);
INSERT INTO `SYS_DICT` VALUES (28, 'MY_EXAM_STATE', '4', '强制交卷', 4);
INSERT INTO `SYS_DICT` VALUES (29, 'MY_EXAM_MARK_STATE', '1', '未阅卷', 1);
INSERT INTO `SYS_DICT` VALUES (30, 'MY_EXAM_MARK_STATE', '2', '阅卷中', 2);
INSERT INTO `SYS_DICT` VALUES (31, 'MY_EXAM_MARK_STATE', '3', '已阅卷', 3);
INSERT INTO `SYS_DICT` VALUES (32, 'MY_EXAM_ANSWER_STATE', '1', '及格', 1);
INSERT INTO `SYS_DICT` VALUES (33, 'MY_EXAM_ANSWER_STATE', '2', '不及格', 2);

INSERT INTO `SYS_CRON` VALUES ('1', '清理临时附件', 'com.wcpdoc.exam.file.job.ClearFileJob', '0 0 0 1/1 * ? ', '1', '1', '2020-08-26 18:42:08');

INSERT INTO `SYS_VER` VALUES (1, '1.0.0', '2017-09-07 15:06:00', 'zhanghc', '初始版本');
INSERT INTO `SYS_VER` VALUES (2, '1.1.0', '2018-11-27 22:47:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (3, '1.1.1', '2019-02-23 15:35:21', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (4, '1.1.2', '2019-03-03 13:20:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (5, '1.1.3', '2019-08-14 18:49:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (6, '1.1.4', '2019-09-05 09:58:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (7, '1.1.5', '2019-12-16 23:16:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (8, '2.0.0', '2020-10-15 00:00:00', 'zhanghc', '');

INSERT INTO `EXM_QUESTION_TYPE` VALUES ('1', '试题分类', '0', '_1_', '1', '1', '2017-08-01 22:31:43', '1', '1', null, null, null);
INSERT INTO `EXM_PAPER_TYPE` VALUES ('1', '试卷分类', '0', '_1_', '1', '1', '2017-08-01 22:31:43', '1', '1', null, null, null);
INSERT INTO `EXM_EXAM_TYPE` VALUES ('1', '考试分类', '0', '_1_', '1', '1', '2017-08-01 22:31:43', '1', '1', null, null, null);