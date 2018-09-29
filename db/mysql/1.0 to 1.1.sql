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
