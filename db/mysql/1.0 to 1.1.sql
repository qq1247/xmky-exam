drop table if exists ZE_QUESTION_AUTH;

/*==============================================================*/
/* Table: ZE_QUESTION_AUTH                                      */
/*==============================================================*/
create table ZE_QUESTION_AUTH
(
   ID                   int not null auto_increment comment 'id',
   QUESTION_TYPE_ID     int comment '试题分类ID',
   TYPE                 int comment '1：可选择；2：不可选择',
   USER_IDS             varchar(4096) comment '用户权限。多用户用逗号分隔，如：,2,45,66,57,',
   ORG_IDS              varchar(2048) comment '机构权限',
   POST_IDS             varchar(2048) comment '岗位权限',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

drop table if exists ZE_VERSION;

/*==============================================================*/
/* Table: ZE_VERSION                                            */
/*==============================================================*/
create table ZE_VERSION
(
   ID                   int not null auto_increment comment 'id',
   NAME                 varchar(16) comment '版本',
   primary key (ID)
);

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