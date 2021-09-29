ALTER TABLE `exam_`.`exm_my_exam_detail` ADD COLUMN `ANSWER_FILE_ID` int(11) DEFAULT NULL COMMENT '答案附件ID' AFTER `QUESTION_SCORE`;

ALTER TABLE `exam_`.`exm_my_mark` MODIFY COLUMN `AUTO_STATE` int(11) DEFAULT NULL COMMENT '1：已自动阅卷；2：未自动阅卷' AFTER `EXAM_ID`;

ALTER TABLE `exam_`.`exm_paper` MODIFY COLUMN `MINIMIZE_NUM` int(11) DEFAULT NULL COMMENT '最小化警告次数' AFTER `OPTIONS`;

ALTER TABLE `exam_`.`exm_paper` MODIFY COLUMN `CREATE_USER_ID` int(11) DEFAULT NULL COMMENT '创建人' AFTER `MINIMIZE_NUM`;

ALTER TABLE `exam_`.`exm_question` ADD COLUMN `ANSWER` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '答案' AFTER `TITLE`;

ALTER TABLE `exam_`.`exm_question` MODIFY COLUMN `CREATE_USER_ID` int(11) DEFAULT NULL COMMENT '创建人' AFTER `STATE`;

ALTER TABLE `exam_`.`exm_question` MODIFY COLUMN `SCORE_OPTIONS` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '1：半对半分；2：答案无顺序；3：大小写不敏感；4：包含答案得分' AFTER `SCORE`;

ALTER TABLE `exam_`.`exm_question` MODIFY COLUMN `AI` int(2) DEFAULT NULL COMMENT '0：否；1：是' AFTER `SCORE_OPTIONS`;

drop table if exists SYS_SENSITIVE;

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

drop table if exists EXM_QUESTION_COMMENT;

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

drop table if exists SYS_PARM;

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
   primary key (ID)
);

alter table SYS_PARM comment '参数';



INSERT INTO `SYS_DICT` VALUES (37, 'STATE_OPEN', '1', '正常', 1);
INSERT INTO `SYS_DICT` VALUES (38, 'STATE_OPEN', '2', '作废', 2);
INSERT INTO `SYS_DICT` VALUES (39, 'STATE_OPEN', '0', '删除', 3);

INSERT INTO `SYS_VER` VALUES (10, '3.1.0', '2020-09-30 13:58:00', 'zhanghc', '');