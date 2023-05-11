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
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_EXER comment '模拟练习';

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

alter table `SYS_USER` DROP COLUMN `TYPE`;

drop table `EXM_QUESTION_COMMENT`;

drop table `EXM_QUESTION_TYPE_OPEN`;

drop table `SYS_SENSITIVE`;

INSERT INTO `SYS_VER` VALUES (30, '4.1.0', '2023-05-10 14:17:00', 'zhanghc', '');