/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2024/4/13 16:32:35                           */
/*==============================================================*/


drop table if exists EXM_BULLETIN;

drop table if exists EXM_EXAM;

drop table if exists EXM_EXAM_QUESTION;

drop table if exists EXM_EXAM_RULE;

drop table if exists EXM_EXER;

drop table if exists EXM_EXER_RMK;

drop table if exists EXM_MY_EXAM;

drop table if exists EXM_MY_MARK;

drop table if exists EXM_MY_QUESTION;

drop table if exists EXM_QUESTION;

drop table if exists EXM_QUESTION_ANSWER;

drop table if exists EXM_QUESTION_OPTION;

drop table if exists EXM_QUESTION_TYPE;

drop table if exists SYS_CRON;

drop table if exists SYS_DICT;

drop table if exists SYS_FILE;

drop table if exists SYS_ORG;

drop table if exists SYS_PARM;

drop table if exists SYS_USER;

drop table if exists SYS_VER;

/*==============================================================*/
/* Table: EXM_BULLETIN                                          */
/*==============================================================*/
create table EXM_BULLETIN
(
   ID                   int not null auto_increment comment '主键',
   START_TIME           datetime comment '开始时间',
   END_TIME             datetime comment '结束时间',
   TITLE                varchar(32) comment '标题',
   CONTENT              text comment '内容',
   STATE                int comment '状态（0：删除；1：发布；）数据字典：STATE',
   UPDATE_TIME          datetime comment '修改时间',
   UPDATE_USER_ID       int comment '修改用户ID',
   primary key (ID)
);

alter table EXM_BULLETIN comment '公告';

/*==============================================================*/
/* Table: EXM_EXAM                                              */
/*==============================================================*/
create table EXM_EXAM
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   PAPER_NAME           varchar(32) comment '试卷名称',
   GEN_TYPE             int comment '组卷方式（1：人工组卷；2：随机组卷）数据字典：PAPER_GEN_TYPE',
   MARK_TYPE            int comment '阅卷方式（1：客观题；2：主观题；）数据字典：PAPER_MARK_TYPE',
   SHOW_TYPE            int comment '展示方式（1：整卷展示；3：单题展示；）数据字典：PAPER_SHOW_TYPE',
   START_TIME           datetime comment '考试开始时间',
   END_TIME             datetime comment '考试结束时间',
   LIMIT_MINUTE         int comment '限制分钟（考试开始时间由用户第一次打开试卷时计时）',
   MARK_START_TIME      datetime comment '阅卷开始时间',
   MARK_END_TIME        datetime comment '阅卷结束时间',
   MARK_STATE           int comment '阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）',
   SCORE_STATE          int comment '成绩查询状态（1：考试结束后；2：不公布；3：交卷后）',
   RANK_STATE           int comment '排名状态（1：公布；2：不公布）',
   ANON_STATE           int comment '匿名阅卷状态（1：是；2：否）',
   PASS_SCORE           decimal(5,2) comment '及格分数',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   SXES                 varchar(32) comment '反作弊（1：试题乱序；2：选项乱序；）',
   USER_NUM             int comment '考试用户数量',
   STATE                int comment '状态（0：删除；1：发布；2：暂停；）',
   CREATE_USER_ID       int comment '创建用户ID',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXAM comment '考试';

/*==============================================================*/
/* Table: EXM_EXAM_QUESTION                                     */
/*==============================================================*/
create table EXM_EXAM_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   CHAPTER_NAME         varchar(32) comment '章节名称',
   CHAPTER_TXT          varchar(512) comment '章节描述',
   TYPE                 int comment '类型 （1：章节；2：试题）',
   SCORE                decimal(5,2) comment '分数',
   SCORES               varchar(64) comment '子分数',
   MARK_OPTIONS         varchar(8) comment '阅卷选项（2：答案无顺序；3：不分大小写；)',
   EXAM_ID              int comment '考试ID',
   QUESTION_ID          int comment '试题ID',
   NO                   int comment '排序',
   OPTIONS_NO           varchar(16) comment '选项排序',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXAM_QUESTION comment '考试试题（固定试卷有效）';

/*==============================================================*/
/* Table: EXM_EXAM_RULE                                         */
/*==============================================================*/
create table EXM_EXAM_RULE
(
   ID                   int not null auto_increment comment '主键',
   CHAPTER_NAME         varchar(32) comment '章节名称',
   CHAPTER_TXT          varchar(512) comment '章节描述',
   TYPE                 int comment '类型 （1：章节；2：试题）',
   QUESTION_TYPE_ID     int comment '题库ID',
   QUESTION_TYPE        int comment '类型（1：单选；2：多选；3：填空；4：判断；5：问答）',
   MARK_TYPE            int comment '阅卷类型（1：客观题；2：主观题）',
   MARK_OPTIONS         varchar(8) comment '阅卷选项   （2：答案无顺序；3：不分大小写；）',
   NUM                  int comment '题数',
   SCORE                decimal(5,2) comment '分数',
   SCORES               decimal(5,2) comment '子分数（多选有效；主观填空问答按空平均分配分数）',
   EXAM_ID              int comment '考试ID',
   NO                   int comment '排序',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXAM_RULE comment '考试抽题规则（随机试卷有效）';

/*==============================================================*/
/* Table: EXM_EXER                                              */
/*==============================================================*/
create table EXM_EXER
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(16) comment '名称',
   QUESTION_TYPE_ID     int comment '题库ID',
   START_TIME           datetime comment '开始时间',
   END_TIME             datetime comment '结束时间',
   USER_IDS             text comment '练习用户IDS',
   STATE                int comment '状态（0：删除；1：正常）',
   RMK_STATE            int comment '评论状态（1：是；2：否）',
   CREATE_USER_ID       int comment '创建用户ID',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXER comment '模拟练习';

/*==============================================================*/
/* Table: EXM_EXER_RMK                                          */
/*==============================================================*/
create table EXM_EXER_RMK
(
   ID                   int not null auto_increment comment '主键',
   EXER_ID              int comment '练习ID',
   QUESTION_ID          int comment '试题ID',
   CONTENT              varchar(256) comment '评论内容',
   LIKE_USER_IDS        text comment '点赞用户IDS',
   LIKE_NUM             int comment '点赞数量',
   STATE                int comment '状态（0：删除；1：正常）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXER_RMK comment '练习评论';

/*==============================================================*/
/* Table: EXM_MY_EXAM                                           */
/*==============================================================*/
create table EXM_MY_EXAM
(
   ID                   int not null auto_increment comment '主键',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '考试用户ID',
   MARK_USER_ID         int comment '阅卷用户ID',
   EXAM_START_TIME      datetime comment '考试开始时间',
   EXAM_END_TIME        datetime comment '考试结束时间',
   MARK_START_TIME      datetime comment '阅卷开始时间',
   MARK_END_TIME        datetime comment '阅卷结束时间',
   OBJECTIVE_SCORE      decimal(5,2) comment '客观题分数',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   STATE                int comment '状态（1：未考试；2：考试中；3：已交卷；）',
   MARK_STATE           int comment '阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）',
   ANSWER_STATE         int comment '答题状态（1：及格；2：不及格）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   NO                   int comment '排序',
   primary key (ID)
);

alter table EXM_MY_EXAM comment '我的考试';

/*==============================================================*/
/* Table: EXM_MY_MARK                                           */
/*==============================================================*/
create table EXM_MY_MARK
(
   ID                   int not null auto_increment comment 'id',
   EXAM_ID              int comment '考试ID',
   MARK_USER_ID         int comment '阅卷用户ID',
   QUESTION_IDS         varchar(1024) comment '试题IDS（按题阅卷有效）',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_MARK comment '我的阅卷';

/*==============================================================*/
/* Table: EXM_MY_QUESTION                                       */
/*==============================================================*/
create table EXM_MY_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   CHAPTER_NAME         varchar(32) comment '章节名称',
   CHAPTER_TXT          varchar(512) comment '章节描述',
   TYPE                 int comment '类型 （1：章节；2：试题）',
   SCORE                decimal(5,2) comment '分数',
   SCORES               varchar(64) comment '子分数',
   MARK_OPTIONS         varchar(8) comment '阅卷选项（2：答案无顺序；3：不区分大小写；)',
   EXAM_ID              int comment '考试ID',
   QUESTION_ID          int comment '试题ID',
   USER_ID              int comment '用户ID',
   NO                   int comment '排序',
   OPTIONS_NO           varchar(16) comment '选项排序，如：2,1,4,3',
   ANSWER_TIME          datetime comment '答题时间',
   USER_ANSWER          text comment '用户答案',
   USER_SCORE           decimal(5,2) comment '用户得分',
   MARK_USER_ID         int comment '阅卷用户ID',
   MARK_TIME            datetime comment '阅卷时间',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_QUESTION comment '我的试题';

/*==============================================================*/
/* Table: EXM_QUESTION                                          */
/*==============================================================*/
create table EXM_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   TYPE                 int comment '类型（1：单选；2：多选；3：填空；4：判断；5：问答）',
   TITLE                text comment '题干',
   MARK_TYPE            int comment ' 阅卷类型（1：客观题；2：主观题）',
   MARK_OPTIONS         varchar(8) comment ' 阅卷选项（2：答案无顺序；3：不分大小写；)',
   ANALYSIS             text comment '解析',
   SCORE                decimal(5,2) comment '默认分值',
   STATE                int comment '状态（0：删除；1：发布；）',
   QUESTION_TYPE_ID     int comment '试题分类ID',
   CREATE_USER_ID       int comment '创建用户ID',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_QUESTION comment '试题';

/*==============================================================*/
/* Table: EXM_QUESTION_ANSWER                                   */
/*==============================================================*/
create table EXM_QUESTION_ANSWER
(
   ID                   int not null auto_increment comment '主键',
   ANSWER               text comment '一个答案有多个同义词用\n分隔',
   SCORE                decimal(5,2) comment '分值',
   QUESTION_ID          int not null comment '试题ID',
   NO                   int comment '排序',
   primary key (ID)
);

alter table EXM_QUESTION_ANSWER comment '试题答案';

/*==============================================================*/
/* Table: EXM_QUESTION_OPTION                                   */
/*==============================================================*/
create table EXM_QUESTION_OPTION
(
   ID                   int not null auto_increment comment '主键',
   OPTIONS              text comment '选项（option是关键字）',
   NO                   int comment '排序',
   QUESTION_ID          int not null comment '试题ID',
   primary key (ID)
);

alter table EXM_QUESTION_OPTION comment '试题选项';

/*==============================================================*/
/* Table: EXM_QUESTION_TYPE                                     */
/*==============================================================*/
create table EXM_QUESTION_TYPE
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   CREATE_USER_ID       int comment '创建用户ID',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_QUESTION_TYPE comment '题库';

/*==============================================================*/
/* Table: SYS_CRON                                              */
/*==============================================================*/
create table SYS_CRON
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   JOB_CLASS            varchar(64) comment '实现类',
   CRON                 varchar(64) comment 'cron表达式',
   STATE                int comment '状态（1：启动；2：停止；）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_CRON comment '定时任务';

/*==============================================================*/
/* Table: SYS_DICT                                              */
/*==============================================================*/
create table SYS_DICT
(
   ID                   int not null auto_increment comment '主键',
   DICT_INDEX           varchar(32) comment '索引',
   DICT_KEY             varchar(32) comment '键',
   DICT_VALUE           varchar(32) comment '值',
   NO                   int comment '排序',
   primary key (ID)
);

alter table SYS_DICT comment '数据字典';

/*==============================================================*/
/* Table: SYS_FILE                                              */
/*==============================================================*/
create table SYS_FILE
(
   ID                   int not null auto_increment comment '主键',
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

/*==============================================================*/
/* Table: SYS_ORG                                               */
/*==============================================================*/
create table SYS_ORG
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   CODE                 varchar(32) comment '编码唯一',
   PARENT_ID            int comment '父ID',
   PARENT_IDS           varchar(128) comment '父级IDS',
   LEVEL                int comment '级别',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常；',
   NO                   int comment '排序',
   primary key (ID)
);

alter table SYS_ORG comment '机构';

/*==============================================================*/
/* Table: SYS_PARM                                              */
/*==============================================================*/
create table SYS_PARM
(
   ID                   int not null comment '主键',
   EMAIL_HOST           varchar(64) comment '邮件主机',
   EMAIL_USER_NAME      varchar(64) comment '邮件用户名',
   EMAIL_PWD            varchar(64) comment '邮件密码',
   EMAIL_PROTOCOL       varchar(16) comment '邮件协议',
   EMAIL_ENCODE         varchar(16) comment '邮件编码',
   ENT_NAME             varchar(32) comment '企业名称',
   FILE_UPLOAD_DIR      varchar(64) comment '上传目录',
   DB_BAK_DIR           varchar(64) comment '数据库备份目录',
   PWD_TYPE             int comment '密码类型（1：随机；2：固定）',
   PWD_VALUE            varchar(32) comment '密码初始化默认值',
   CUSTOM_TITLE         varchar(16) comment '自定义标题',
   CUSTOM_CONTENT       varchar(128) comment '自定义内容',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_PARM comment '参数';

/*==============================================================*/
/* Table: SYS_USER                                              */
/*==============================================================*/
create table SYS_USER
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(16) comment '姓名',
   LOGIN_NAME           varchar(16) comment '登陆账号',
   PWD                  varchar(32) comment '密码',
   TYPE                 int comment '类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）',
   PARENT_ID            int comment '父级ID（TYPE == 3有效）',
   USER_IDS             text comment '可管理用户IDS（子管理员有效）',
   ORG_IDS              text comment '可管理机构IDS（子管理员有效）',
   EMAIL                varchar(64) comment '邮箱',
   PHONE                varchar(11) comment '手机号',
   HEAD_FILE_ID         int comment '头像',
   REGIST_TIME          datetime comment '注册时间',
   LAST_LOGIN_TIME      datetime comment '最后登陆时间',
   ORG_ID               int comment '机构ID',
   STATE                int comment '状态（0：删除；1：正常；2：冻结；）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_USER comment '用户';

/*==============================================================*/
/* Table: SYS_VER                                               */
/*==============================================================*/
create table SYS_VER
(
   ID                   int not null auto_increment comment '主键',
   VER                  varchar(16) comment '版本',
   UPDATE_TIME          datetime comment '修改时间',
   AUTHOR               varchar(16) comment '作者',
   REMARK               text comment '备注',
   primary key (ID)
);

alter table SYS_VER comment '版本';

/*==============================================================*/
/* 数据															*/
/*==============================================================*/

INSERT INTO `SYS_ORG` VALUES (1, '企业', 'code', '0', '_1_', '1', '1', '2017-08-01 22:31:43', '1', '1');
INSERT INTO `SYS_USER` VALUES (1, '管理员', 'admin', '79nRuL+wDo42R5kPfXTR2A==', 0, NULL, NULL, NULL, NULL, NULL, NULL, '2017-08-01 22:31:43', '2017-08-01 22:31:43', 0, 1, 1, '2017-08-01 22:31:43');
INSERT INTO `SYS_PARM` VALUES (1, NULL, NULL, NULL, NULL, NULL, '小猫考试', 'bak\\file', 'bak\\db', 2, '111111', '服务支持', '开源项目：小猫考试\r\n在线支持： qq（811189776） ', 1, '2017-08-01 22:31:43');

INSERT INTO `SYS_DICT` VALUES (1, 'STATE', '0', '删除', 1);
INSERT INTO `SYS_DICT` VALUES (2, 'STATE', '1', '正常', 2);
INSERT INTO `SYS_DICT` VALUES (3, 'STATE', '2', '禁用', 3);

INSERT INTO `SYS_DICT` VALUES (4, 'STATE_NF', '1', '正常', 1);
INSERT INTO `SYS_DICT` VALUES (5, 'STATE_NF', '2', '冻结', 2);

INSERT INTO `SYS_DICT` VALUES (6, 'STATE_SS', '1', '启动', 1);
INSERT INTO `SYS_DICT` VALUES (7, 'STATE_SS', '2', '停止', 2);

INSERT INTO `SYS_DICT` VALUES (8, 'QUESTION_TYPE', '1', '单选', 1);
INSERT INTO `SYS_DICT` VALUES (9, 'QUESTION_TYPE', '2', '多选', 2);
INSERT INTO `SYS_DICT` VALUES (10, 'QUESTION_TYPE', '3', '填空', 3);
INSERT INTO `SYS_DICT` VALUES (11, 'QUESTION_TYPE', '4', '判断', 4);
INSERT INTO `SYS_DICT` VALUES (12, 'QUESTION_TYPE', '5', '问答', 5);

INSERT INTO `SYS_DICT` VALUES (13, 'STATE_ON', '1', '公布', 1);
INSERT INTO `SYS_DICT` VALUES (14, 'STATE_ON', '2', '不公布', 2);
INSERT INTO `SYS_DICT` VALUES (15, 'STATE_YN', '1', '是', 1);
INSERT INTO `SYS_DICT` VALUES (16, 'STATE_YN', '2', '否', 2);

INSERT INTO `SYS_DICT` VALUES (18, 'QUESTION_OPTIONS', '1', 'A', 1);
INSERT INTO `SYS_DICT` VALUES (19, 'QUESTION_OPTIONS', '2', 'B', 2);
INSERT INTO `SYS_DICT` VALUES (20, 'QUESTION_OPTIONS', '3', 'C', 3);
INSERT INTO `SYS_DICT` VALUES (21, 'QUESTION_OPTIONS', '4', 'D', 4);
INSERT INTO `SYS_DICT` VALUES (22, 'QUESTION_OPTIONS', '5', 'E', 5);
INSERT INTO `SYS_DICT` VALUES (23, 'QUESTION_OPTIONS', '6', 'F', 6);
INSERT INTO `SYS_DICT` VALUES (24, 'QUESTION_OPTIONS', '7', 'G', 7);

INSERT INTO `SYS_DICT` VALUES (25, 'EXAM_STATE', '1', '未考试', 1);
INSERT INTO `SYS_DICT` VALUES (26, 'EXAM_STATE', '2', '考试中', 2);
INSERT INTO `SYS_DICT` VALUES (27, 'EXAM_STATE', '3', '已交卷', 3);

INSERT INTO `SYS_DICT` VALUES (29, 'MARK_STATE', '1', '未阅卷', 1);
INSERT INTO `SYS_DICT` VALUES (30, 'MARK_STATE', '2', '阅卷中', 2);
INSERT INTO `SYS_DICT` VALUES (31, 'MARK_STATE', '3', '已阅卷', 3);

INSERT INTO `SYS_DICT` VALUES (32, 'ANSWER_STATE', '1', '及格', 1);
INSERT INTO `SYS_DICT` VALUES (33, 'ANSWER_STATE', '2', '不及格', 2);

INSERT INTO `SYS_DICT` VALUES (40, 'PAPER_MARK_TYPE', '1', '客观题', 1);
INSERT INTO `SYS_DICT` VALUES (41, 'PAPER_MARK_TYPE', '2', '主观题', 2);

INSERT INTO `SYS_DICT` VALUES (42, 'PAPER_SHOW_TYPE', '1', '整卷展示', 1);
INSERT INTO `SYS_DICT` VALUES (44, 'PAPER_SHOW_TYPE', '3', '单题展示', 3);

INSERT INTO `SYS_DICT` VALUES (45, 'PAPER_GEN_TYPE', '1', '人工组卷', 1);
INSERT INTO `SYS_DICT` VALUES (46, 'PAPER_GEN_TYPE', '2', '随机组卷', 2);

INSERT INTO `SYS_DICT` VALUES (48, 'BULLETIN_SHOW_TYPE', '1', '正常', 1);
INSERT INTO `SYS_DICT` VALUES (49, 'BULLETIN_SHOW_TYPE', '2', '置顶', 2);

INSERT INTO `SYS_DICT` VALUES (51, 'QUESTION_MARK_OPTIONS', '2', '答案无顺序', 2);
INSERT INTO `SYS_DICT` VALUES (52, 'QUESTION_MARK_OPTIONS', '3', '不分大小写', 3);

INSERT INTO `SYS_DICT` VALUES (53, 'SCORE_STATE', '1', '考试结束后', 1);
INSERT INTO `SYS_DICT` VALUES (54, 'SCORE_STATE', '2', '不公布', 2);
INSERT INTO `SYS_DICT` VALUES (55, 'SCORE_STATE', '3', '交卷后', 3);

INSERT INTO `SYS_CRON` VALUES (1, '清理临时附件', 'com.wcpdoc.file.job.ClearFileJob', '0 0 0 1/1 * ? ', '1', '1', '2020-08-26 18:42:08');
INSERT INTO `SYS_CRON` VALUES (2, '数据库备份', 'com.wcpdoc.quartz.job.DbBackJob', '0 0 0 1/1 * ? ', 1, 1, '2020-08-26 18:42:08');

INSERT INTO `SYS_VER` VALUES (1, '1.0.0', '2017-09-07 15:06:00', 'zhanghc', '初始版本');
INSERT INTO `SYS_VER` VALUES (2, '1.1.0', '2018-11-27 22:47:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (3, '1.1.1', '2019-02-23 15:35:21', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (4, '1.1.2', '2019-03-03 13:20:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (5, '1.1.3', '2019-08-14 18:49:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (6, '1.1.4', '2019-09-05 09:58:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (7, '1.1.5', '2019-12-16 23:16:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (8, '2.0.0', '2020-10-15 00:00:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (9, '3.0.0', '2020-08-06 13:58:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (10, '3.1.0', '2020-09-30 13:58:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (11, '3.2.0', '2020-10-31 16:43:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (12, '3.2.1', '2021-11-25 16:26:01', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (13, '3.3.0', '2020-11-30 11:11:11', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (14, '3.4.0', '2021-12-31 12:00:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (15, '3.5.0', '2022-01-28 15:36:51', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (16, '3.6.0', '2022-03-17 16:14:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (17, '3.4.1', '2022-03-08 13:15:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (18, '3.5.1', '2022-03-08 15:32:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (19, '3.6.1', '2022-03-23 13:52:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (20, '3.7.0', '2022-03-30 13:06:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (21, '3.8.0', '2022-05-13 16:34:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (22, '3.8.1', '2022-05-20 17:13:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (23, '3.8.2', '2022-06-29 10:18:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (24, '3.9.0', '2022-07-07 15:34:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (25, '3.9.1', '2022-07-29 16:47:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (26, '3.9.2', '2023-03-21 18:51:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (27, '4.0.0', '2023-04-01 14:38:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (28, '3.9.3', '2023-05-08 18:13:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (29, '4.0.1', '2023-05-09 16:22:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (30, '4.1.0', '2023-05-10 14:17:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (31, '4.2.0', '2023-08-31 13:16:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (32, '4.3.0', '2023-10-09 10:51:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (33, '4.3.1', '2023-11-13 17:35:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (34, '4.4.0', '2024-02-09 09:14:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (35, '4.4.1', '2024-02-17 15:14:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (36, '4.4.2', '2024-02-28 16:04:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (37, '4.5.0', '2024-05-31 12:16:00', 'zhanghc', '');

ALTER TABLE `EXM_MY_QUESTION` ADD INDEX `MY_QUESTION_EUQ` ( `EXAM_ID`,`USER_ID`,`QUESTION_ID` );
ALTER TABLE `EXM_MY_EXAM` ADD INDEX `MY_EXAM_EU` ( `EXAM_ID`,`USER_ID` );