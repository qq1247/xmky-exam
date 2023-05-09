drop table if exists EXM_BULLETIN;

drop table if exists EXM_EXAM;

drop table if exists EXM_EXAM_TYPE;

drop table if exists EXM_MY_EXAM;

drop table if exists EXM_MY_EXAM_DETAIL;

drop table if exists EXM_MY_MARK;

drop table if exists EXM_PAPER;

drop table if exists EXM_PAPER_QUESTION;

drop table if exists EXM_PAPER_QUESTION_RULE;

drop table if exists EXM_PAPER_QUESTION_ANSWER;

drop table if exists EXM_PAPER_REMARK;

drop table if exists EXM_PAPER_TYPE;

drop table if exists EXM_QUESTION;

drop table if exists EXM_QUESTION_ANSWER;

drop table if exists EXM_QUESTION_COMMENT;

drop table if exists EXM_QUESTION_OPTION;

drop table if exists EXM_QUESTION_TYPE;

drop table if exists EXM_QUESTION_TYPE_OPEN;

drop table if exists SYS_CRON;

drop table if exists SYS_DICT;

drop table if exists SYS_FILE;

drop table if exists SYS_ORG;

drop table if exists SYS_PARM;

drop table if exists SYS_SENSITIVE;

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
   IMG_FILE_ID          varchar(256) comment '图片',
   VIDEO_FILE_ID        varchar(256) comment '视频',
   CONTENT              text comment '内容',
   IMGS_HEIGHT          int comment '图片高',
   IMGS_WIDTH           int comment '图片宽',
   URL                  varchar(128) comment '跳转链接',
   SHOW_TYPE            int comment '展示类型（1：正常；2：置顶；3：轮播；）',
   NO                   int comment '排序',
   STATE                int comment '0：删除；1：发布；2：草稿',
   UPDATE_TIME          datetime comment '修改时间',
   UPDATE_USER_ID       int comment '修改人',
   READ_USER_IDS        varchar(256) comment '用户读权限',
   READ_ORG_IDS         varchar(64) comment '机构读权限',
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
   START_TIME           datetime comment '考试开始',
   END_TIME             datetime comment '考试结束',
   MARK_START_TIME      datetime comment '阅卷开始',
   MARK_END_TIME        datetime comment '阅卷结束',
   MARK_STATE           int comment '阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）',
   SCORE_STATE          int comment '成绩状态（1：公开；2：不公开）',
   RANK_STATE           int comment '排名状态（1：公开；2：不公开）',
   LOGIN_TYPE           int comment '登陆方式（1：安排考试；2：免登陆考试）',
   DESCRIPTION          varchar(512) comment '描述',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '状态（0：删除；1：发布；2：草稿；3：归档）',
   PAPER_ID             int comment '试卷ID',
   EXAM_TYPE_ID         int comment '考试分类ID',
   primary key (ID)
);

alter table EXM_EXAM comment '考试';

/*==============================================================*/
/* Table: EXM_EXAM_TYPE                                         */
/*==============================================================*/
create table EXM_EXAM_TYPE
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   IMG_FILE_ID          int comment '图片ID',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常',
   primary key (ID)
);

alter table EXM_EXAM_TYPE comment '考试分类';

/*==============================================================*/
/* Table: EXM_MY_EXAM                                           */
/*==============================================================*/
create table EXM_MY_EXAM
(
   ID                   int not null auto_increment comment 'id',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '用户ID',
   ANSWER_START_TIME    datetime comment '答题时间',
   ANSWER_END_TIME      datetime comment '答题完成时间',
   MARK_USER_ID         int comment '阅卷用户',
   MARK_START_TIME      datetime comment '阅卷时间',
   MARK_END_TIME        datetime comment '阅卷完成时间',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   STATE                int comment '1：未考试；2：考试中；3：已交卷；4：强制交卷；',
   MARK_STATE           int comment '1：未阅卷；2：阅卷中；3：已阅卷；',
   ANSWER_STATE         int comment '1：及格；2：不及格',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   NO                   int comment '排序',
   primary key (ID)
);

alter table EXM_MY_EXAM comment '我的考试';

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
   MARK_USER_ID         int comment '阅卷用户ID',
   MARK_TIME            datetime comment '阅卷时间',
   ANSWER               text comment '答案',
   SCORE                decimal(5,2) comment '得分',
   QUESTION_SCORE       decimal(5,2) comment '试题分值',
   ANSWER_FILE_ID       int comment '答案附件ID',
   primary key (ID)
);

alter table EXM_MY_EXAM_DETAIL comment '我的考试详细';

/*==============================================================*/
/* Table: EXM_MY_MARK                                           */
/*==============================================================*/
create table EXM_MY_MARK
(
   ID                   int not null auto_increment comment 'id',
   MARK_USER_ID         int comment '阅卷用户ID',
   EXAM_USER_IDS        varchar(1024) comment '考试用户IDS',
   QUESTION_IDS         varchar(1024) comment '试题IDS',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   EXAM_ID              int comment '考试ID',
   primary key (ID)
);

alter table EXM_MY_MARK comment '我的阅卷';

/*==============================================================*/
/* Table: EXM_PAPER                                             */
/*==============================================================*/
create table EXM_PAPER
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   PASS_SCORE           decimal(5,2) comment '及格分数（%）',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   MARK_TYPE            int comment '阅卷方式（1：智能阅卷；2：人工阅卷；）数据字典：PAPER_MARK_TYPE',
   SHOW_TYPE            int comment '展示方式（1：整卷展示；2：章节显示；3：单题展示；）数据字典：PAPER_SHOW_TYPE',
   READ_REMARK          text comment '考前阅读',
   READ_NUM             int comment '阅读时长（分）',
   STATE                int comment '0：删除；1：发布；2：草稿；3：归档',
   PAPER_TYPE_ID        int not null comment '试卷分类ID',
   GEN_TYPE             int comment '组卷方式（1：人工组卷；2：随机组卷）',
   OPTIONS              varchar(32) comment '1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告',
   MINIMIZE_NUM         int comment '最小化警告次数（OPTIONS=5有效）',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_PAPER comment '试卷';

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
   SCORE_OPTIONS        varchar(8) comment '1：漏选得分；2：答案无顺序；3：大小写不敏感；',
   NO                   int comment '排序',
   PAPER_QUESTION_RULE_ID int comment '试卷试题规则ID',
   USER_ID              int comment '考试用户ID',
   EXAM_ID              int comment '考试ID',
   primary key (ID)
);

alter table EXM_PAPER_QUESTION comment '试卷试题';

/*==============================================================*/
/* Table: EXM_PAPER_QUESTION_RULE                               */
/*==============================================================*/
create table EXM_PAPER_QUESTION_RULE
(
   ID                   int not null auto_increment comment 'id',
   QUESTION_TYPE_ID     int comment '试题分类ID',
   TYPE                 int comment '1：单选；2：多选；3：填空；4：判断；5：问答',
   DIFFICULTYS          varchar(16) comment '1：极易；2：简单；3：适中；4：困难；5：极难',
   AIS                  varchar(16) comment '智能阅卷',
   SCORE_OPTIONS        varchar(8) comment '1：漏选得分；2：答案无顺序；3：大小写不敏感；',
   NUM                  int comment '题数',
   SCORE                decimal(5,2) comment '分数',
   PAPER_ID             int comment '试卷ID',
   PAPER_QUESTION_ID    int comment '试卷试题ID',
   NO                   int comment '排序',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_PAPER_QUESTION_RULE comment '试卷试题规则';

/*==============================================================*/
/* Table: EXM_PAPER_QUESTION_ANSWER                             */
/*==============================================================*/
create table EXM_PAPER_QUESTION_ANSWER
(
   ID                   int not null auto_increment,
   ANSWER               text comment '一个答案有多个同义词用\n分隔',
   SCORE                decimal(5,2) comment '分值',
   NO                   int comment '排序',
   PAPER_ID             int comment '试卷ID',
   QUESTION_ID          int not null comment '试题ID',
   PAPER_QUESTION_ID    int comment '试卷试题ID',
   primary key (ID)
);

alter table EXM_PAPER_QUESTION_ANSWER comment '试卷试题答案';

/*==============================================================*/
/* Table: EXM_PAPER_REMARK                                      */
/*==============================================================*/
create table EXM_PAPER_REMARK
(
   ID                   int not null auto_increment comment '主键',
   SCORE                decimal(5,2) comment '分数（百分比）',
   REMARK               varchar(32) comment '评语',
   NO                   int comment '排序',
   PAPER_ID             int not null comment '试卷ID',
   primary key (ID)
);

alter table EXM_PAPER_REMARK comment '试卷评语';

/*==============================================================*/
/* Table: EXM_PAPER_TYPE                                        */
/*==============================================================*/
create table EXM_PAPER_TYPE
(
   ID                   int not null auto_increment comment 'id',
   NAME                 varchar(32) comment '名称',
   IMG_FILE_ID          int comment '图片ID',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常',
   READ_USER_IDS        varchar(1024) comment '读权限',
   primary key (ID)
);

alter table EXM_PAPER_TYPE comment '试卷分类';

/*==============================================================*/
/* Table: EXM_QUESTION                                          */
/*==============================================================*/
create table EXM_QUESTION
(
   ID                   int not null auto_increment,
   TYPE                 int comment '1：单选；2：多选；3：填空；4：判断；5：问答',
   DIFFICULTY           int comment '1：极易；2：简单；3：适中；4：困难；5：极难',
   TITLE                text comment '题干',
   ANALYSIS             text comment '解析',
   STATE                int comment '状态（0：删除；1：发布；2：草稿）',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   QUESTION_TYPE_ID     int comment '试题分类',
   SCORE                decimal(5,2) comment '默认分值',
   AI                   int comment '智能阅卷（1：是；2：否；）',
   SCORE_OPTIONS        varchar(8) comment '分值选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)',
   primary key (ID)
);

alter table EXM_QUESTION comment '试题';

/*==============================================================*/
/* Table: EXM_QUESTION_ANSWER                                   */
/*==============================================================*/
create table EXM_QUESTION_ANSWER
(
   ID                   int not null auto_increment,
   ANSWER               text comment '一个答案有多个同义词用\n分隔',
   SCORE                decimal(5,2) comment '分值',
   NO                   int comment '排序',
   QUESTION_ID          int not null comment '试题ID',
   primary key (ID)
);

alter table EXM_QUESTION_ANSWER comment '试题答案';

/*==============================================================*/
/* Table: EXM_QUESTION_COMMENT                                  */
/*==============================================================*/
create table EXM_QUESTION_COMMENT
(
   ID                   int not null auto_increment comment '主键',
   QUESTION_ID          int comment '试题id',
   CONTENT              text comment '评论内容',
   PARENT_ID            int comment '父ID',
   PARENT_SUB           varchar(512) comment '父子关系（格式：_父ID_子ID_子子ID_... ...）',
   LEVEL                int comment '级别',
   STATE                int comment '状态(0：删除；1：正常,)',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_QUESTION_COMMENT comment '试题评论';

/*==============================================================*/
/* Table: EXM_QUESTION_OPTION                                   */
/*==============================================================*/
create table EXM_QUESTION_OPTION
(
   ID                   int not null auto_increment,
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
   ID                   int not null auto_increment comment 'id',
   NAME                 varchar(32) comment '名称',
   IMG_FILE_ID          int comment '图片ID',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常',
   WRITE_USER_IDS       varchar(1024) comment '写权限',
   primary key (ID)
);

alter table EXM_QUESTION_TYPE comment '试题分类';

/*==============================================================*/
/* Table: EXM_QUESTION_TYPE_OPEN                                */
/*==============================================================*/
create table EXM_QUESTION_TYPE_OPEN
(
   ID                   int not null auto_increment comment 'id',
   START_TIME           datetime comment '开始时间',
   END_TIME             datetime comment '结束时间',
   USER_IDS             varchar(1024) comment '开放用户',
   ORG_IDS              varchar(256) comment '开放机构',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常',
   QUESTION_TYPE_ID     int comment '试题分类ID',
   COMMENT_STATE        int comment '评论状态(0：关闭；1：只读；2：可编辑,)',
   primary key (ID)
);

alter table EXM_QUESTION_TYPE_OPEN comment '试题分类开放';

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
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   STATE                int comment '0：删除；1：正常；',
   NO                   int comment '排序',
   primary key (ID)
);

alter table SYS_ORG comment '组织机构';

/*==============================================================*/
/* Table: SYS_PARM                                              */
/*==============================================================*/
create table SYS_PARM
(
   ID                   int not null auto_increment,
   EMAIL_HOST           varchar(64) comment '邮件主机',
   EMAIL_USER_NAME      varchar(64) comment '邮件用户名',
   EMAIL_PWD            varchar(64) comment '邮件密码',
   EMAIL_PROTOCOL       varchar(16) comment '邮件协议',
   EMAIL_ENCODE         varchar(16) comment '邮件编码',
   ORG_LOGO             int comment '单位商标',
   ORG_NAME             varchar(32) comment '单位名称',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   FILE_UPLOAD_DIR      varchar(64) comment '上传目录',
   DB_BAK_DIR           varchar(64) comment '数据库备份目录',
   PWD_TYPE             int comment '密码类型（1：随机；2：固定）',
   PWD_VALUE            varchar(32) comment '密码初始化默认值',
   primary key (ID)
);

alter table SYS_PARM comment '参数';

/*==============================================================*/
/* Table: SYS_SENSITIVE                                         */
/*==============================================================*/
create table SYS_SENSITIVE
(
   ID                   int not null auto_increment,
   WHITE_LIST           text comment '白名单',
   BLACK_LIST           text comment '黑名单',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_SENSITIVE comment '敏感词';

/*==============================================================*/
/* Table: SYS_USER                                              */
/*==============================================================*/
create table SYS_USER
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(16) comment '名称',
   LOGIN_NAME           varchar(16) comment '登陆账号',
   EMAIL                varchar(64) comment '邮箱',
   PHONE                varchar(11) comment '手机号',
   PWD                  varchar(32) comment '密码',
   REGIST_TIME          datetime comment '注册时间',
   LAST_LOGIN_TIME      datetime comment '最后登陆时间',
   ORG_ID               int comment '组织机构ID',
   ROLES                varchar(64) comment '角色',
   TYPE                 int comment '类型（1：普通用户；2：管理员）',
   STATE                int comment '状态（0：删除；1：正常；2：冻结；）',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_USER comment '用户';

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

/*==============================================================*/
/* 数据															*/
/*==============================================================*/

INSERT INTO `SYS_ORG` VALUES (1, '组织机构', 'code', '0', '_1_', '1', '1', '2017-08-01 22:31:43', '1', '1');

INSERT INTO `SYS_USER` VALUES (1, '管理员', 'admin', null, null,'79nRuL+wDo42R5kPfXTR2A==', '2017-08-01 22:31:43', '2017-08-01 22:31:43', null, 'admin', '1', '1', '1', '2017-08-01 22:31:43');

INSERT INTO `SYS_DICT` VALUES (1, 'STATE', '0', '删除', 1);
INSERT INTO `SYS_DICT` VALUES (2, 'STATE', '1', '启用', 2);
INSERT INTO `SYS_DICT` VALUES (3, 'STATE', '2', '禁用', 3);
INSERT INTO `SYS_DICT` VALUES (4, 'STATE_YN', '1', '是', 1);
INSERT INTO `SYS_DICT` VALUES (5, 'STATE_YN', '2', '否', 2);
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
INSERT INTO `SYS_DICT` VALUES (34, 'EXAM_STATE', '0', '删除', 1);
INSERT INTO `SYS_DICT` VALUES (35, 'EXAM_STATE', '1', '发布', 2);
INSERT INTO `SYS_DICT` VALUES (36, 'EXAM_STATE', '2', '草稿', 3);
INSERT INTO `SYS_DICT` VALUES (37, 'STATE_OPEN', '1', '正常', 1);
INSERT INTO `SYS_DICT` VALUES (38, 'STATE_OPEN', '2', '作废', 2);
INSERT INTO `SYS_DICT` VALUES (39, 'STATE_OPEN', '0', '删除', 3);
INSERT INTO `SYS_DICT` VALUES (40, 'PAPER_MARK_TYPE', '1', '智能阅卷', 1);
INSERT INTO `SYS_DICT` VALUES (41, 'PAPER_MARK_TYPE', '2', '人工阅卷', 2);
INSERT INTO `SYS_DICT` VALUES (42, 'PAPER_SHOW_TYPE', '1', '整卷展示', 1);
INSERT INTO `SYS_DICT` VALUES (43, 'PAPER_SHOW_TYPE', '2', '章节显示', 2);
INSERT INTO `SYS_DICT` VALUES (44, 'PAPER_SHOW_TYPE', '3', '单题展示', 3);
INSERT INTO `SYS_DICT` VALUES (45, 'PAPER_GEN_TYPE', '1', '人工组卷', 1);
INSERT INTO `SYS_DICT` VALUES (46, 'PAPER_GEN_TYPE', '2', '随机组卷', 2);
INSERT INTO `SYS_DICT` VALUES (47, 'EXAM_STATE', '3', '归档', 4);
INSERT INTO `SYS_DICT` VALUES (48, 'BULLETIN_SHOW_TYPE', '1', '正常', 1);
INSERT INTO `SYS_DICT` VALUES (49, 'BULLETIN_SHOW_TYPE', '2', '置顶', 2);
INSERT INTO `SYS_DICT` VALUES (50, 'QUESTION_SCORE_OPTIONS', '1', '漏选得分', 1);
INSERT INTO `SYS_DICT` VALUES (51, 'QUESTION_SCORE_OPTIONS', '2', '答案无顺序', 2);
INSERT INTO `SYS_DICT` VALUES (52, 'QUESTION_SCORE_OPTIONS', '3', '大小写不敏感', 3);

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
INSERT INTO `SYS_VER` VALUES (16, '3.6.0', '2021-03-17 16:14:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (17, '3.4.1', '2022-03-08 13:15:00', 'zhanghc', '');
INSERT INTO `SYS_VER` VALUES (18, '3.5.1', '2022-03-08 15:32:00', 'zhanghc', '');

ALTER TABLE `EXM_PAPER_QUESTION` ADD INDEX `QUESTION_ID`(`QUESTION_ID`);