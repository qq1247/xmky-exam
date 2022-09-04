INSERT INTO SYS_VER VALUES (26, '4.0.0', '2022-08-30 10:18:00', 'zhanghc', '');

DELETE FROM SYS_DICT WHERE ID = 13;
DELETE FROM SYS_DICT WHERE ID = 14;
DELETE FROM SYS_DICT WHERE ID = 15;
DELETE FROM SYS_DICT WHERE ID = 16;
DELETE FROM SYS_DICT WHERE ID = 17;

UPDATE SYS_DICT SET DICT_INDEX = 'STATE', DICT_KEY = '0', DICT_VALUE = '回收站', NO = 1 WHERE ID = 1;
UPDATE SYS_DICT SET DICT_INDEX = 'STATE', DICT_KEY = '1', DICT_VALUE = '发布', NO = 2 WHERE ID = 2;
UPDATE SYS_DICT SET DICT_INDEX = 'STATE', DICT_KEY = '2', DICT_VALUE = '暂停', NO = 3 WHERE ID = 3;

UPDATE SYS_DICT SET DICT_INDEX = 'PAPER_MARK_TYPE', DICT_KEY = '1', DICT_VALUE = '客观题', NO = 1 WHERE ID = 40;
UPDATE SYS_DICT SET DICT_INDEX = 'PAPER_MARK_TYPE', DICT_KEY = '2', DICT_VALUE = '主观题', NO = 2 WHERE ID = 41;

UPDATE SYS_DICT SET DICT_INDEX = 'QUESTION_MARK_OPTIONS', DICT_KEY = '1', DICT_VALUE = '漏选得分', NO = 1 WHERE ID = 50;
UPDATE SYS_DICT SET DICT_INDEX = 'QUESTION_MARK_OPTIONS', DICT_KEY = '2', DICT_VALUE = '答案无顺序', NO = 2 WHERE ID = 51;
UPDATE SYS_DICT SET DICT_INDEX = 'QUESTION_MARK_OPTIONS', DICT_KEY = '3', DICT_VALUE = '大小写不敏感', NO = 3 WHERE ID = 52;

drop table if exists EXM_PAPER_TYPE;

drop table if exists EXM_EXAM_TYPE;

drop table if exists EXM_PAPER;

drop table if exists EXM_PAPER_REMARK;

drop table if exists EXM_EXAM_ANSWER;


drop table if exists EXM_QUESTION;

drop table if exists EXM_EXAM;

drop table if exists EXM_EXAM_RULE;

drop table if exists EXM_EXAM_QUESTION;

drop table if exists EXM_MY_EXAM_DETAIL;

drop table if exists EXM_EXAM_QUESTION_NO;

/*==============================================================*/
/* Table: EXM_QUESTION                                          */
/*==============================================================*/
create table EXM_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   TYPE                 int comment '类型（1：单选；2：多选；3：填空；4：判断；5：问答）',
   TITLE                text comment '题干',
   ANALYSIS             text comment '解析',
   SCORE                decimal(5,2) comment '默认分值',
   MARK_TYPE            int comment ' 阅卷类型（1：客观题；2：主观题）',
   MARK_OPTIONS         varchar(8) comment ' 阅卷选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)',
   WRITE_USER_IDS       varchar(1024) comment '写权限',
   STATE                int comment '状态（0：删除；1：发布；2：草稿）',
   QUESTION_TYPE_ID     int comment '试题分类',
   CREATE_USER_ID       int comment '创建人',
   CREATE_TIME          datetime comment '创建时间',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_QUESTION comment '试题';

/*==============================================================*/
/* Table: EXM_EXAM                                              */
/*==============================================================*/
create table EXM_EXAM
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   TIME_TYPE            int comment '时间类型（1：限时；2：不限时）',
   START_TIME           datetime comment '考试开始',
   END_TIME             datetime comment '考试结束',
   MARK_START_TIME      datetime comment '阅卷开始',
   MARK_END_TIME        datetime comment '阅卷结束',
   MARK_STATE           int comment '阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）',
   SCORE_STATE          int comment '成绩状态（1：公开；2：不公开）',
   RANK_STATE           int comment '排名状态（1：公开；2：不公开）',
   ANON_STATE           int comment '匿名阅卷状态（1：公开；2：不公开）',
   LOGIN_TYPE           int comment '登陆方式（1：安排考试；2：免登陆考试）',
   PASS_SCORE           decimal(5,2) comment '及格分数（%）',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   MARK_TYPE            int comment '阅卷方式（1：智能阅卷；2：人工阅卷；）数据字典：PAPER_MARK_TYPE',
   SHOW_TYPE            int comment '展示方式（1：整卷展示；2：章节显示；3：单题展示；）数据字典：PAPER_SHOW_TYPE',
   GEN_TYPE             int comment '组卷方式（1：人工组卷；2：随机组卷）',
   SXES                 varchar(32) comment '反作弊（1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告）',
   MINIMIZE_NUM         int comment '最小化警告次数（SXES=5有效）',
   STATE                int comment '状态（0：删除；1：发布；2：暂停；3：归档）',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXAM comment '考试';

/*==============================================================*/
/* Table: EXM_EXAM_RULE                                         */
/*==============================================================*/
create table EXM_EXAM_RULE
(
   ID                   int not null auto_increment comment '主键',
   QUESTION_TYPE_ID     int comment '题库ID',
   TYPE                 int comment '类型（1：单选；2：多选；3：填空；4：判断；5：问答）',
   MARK_TYPES           varchar(8) comment '阅卷类型（1：客观题；2：主观题）',
   MARK_OPTIONS         varchar(8) comment '阅卷选项   （1：漏选得分；2：答案无顺序；3：大小写不敏感；）',
   NUM                  int comment '题数',
   SCORE                decimal(5,2) comment '分数',
   EXAM_ID              int comment '考试ID',
   NO                   int comment '排序',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXAM_RULE comment '考试规则';

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
   SCORES               varchar(16) comment '子分数',
   MARK_OPTIONS         varchar(8) comment '阅卷选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；)',
   EXAM_ID              int comment '考试ID',
   QUESTION_ID          int comment '试题ID',
   USER_ID              int comment '用户ID（随机试卷有效）',
   NO                   int comment '排序',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXAM_QUESTION comment '考试试题';

/*==============================================================*/
/* Table: EXM_MY_EXAM_DETAIL                                    */
/*==============================================================*/
create table EXM_MY_EXAM_DETAIL
(
   ID                   int not null auto_increment comment '主键',
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
   MY_EXAM_ID           int comment '我的考试ID',
   primary key (ID)
);

alter table EXM_MY_EXAM_DETAIL comment '我的考试详细';

/*==============================================================*/
/* Table: EXM_EXAM_QUESTION_NO                                  */
/*==============================================================*/
create table EXM_EXAM_QUESTION_NO
(
   ID                   int not null auto_increment comment '主键',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '用户ID',
   NO                   text comment '排序（格式：[ {questionId:options} ]）',
   primary key (ID)
);

alter table EXM_EXAM_QUESTION_NO comment '考试试题排序';


