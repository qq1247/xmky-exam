-- 记录版本
INSERT INTO `SYS_VER` VALUES (51, '6.1.0', '2026-01-29 14:37:00', 'zhanghc', '');
UPDATE SYS_PARM SET APP_VER = '6.1.0';

-- 基于企业内部权威资料，在用户练题时提供相关内容参考，帮助其快速理解题目背景和知识依据。
ALTER TABLE EXM_QUESTION_BANK ADD COLUMN FILE_IDS varchar(64) COMMENT '附件IDS' AFTER STATE;

-- 单题填空数量由上限7个改为20个（已满足部分业务场景）
ALTER TABLE `EXM_MY_EXAM_QUESTION` COMMENT = '我的考试试题';
ALTER TABLE `EXM_MY_EXAM_QUESTION_HIS` COMMENT = '我的考试试题历史';
ALTER TABLE `EXM_MY_EXAM_QUESTION` MODIFY COLUMN `SCORES` varchar(128) COMMENT '子分数' AFTER `SCORE`;
ALTER TABLE `EXM_MY_EXAM_QUESTION_HIS` MODIFY COLUMN `SCORES` varchar(128) COMMENT '子分数' AFTER `SCORE`;
ALTER TABLE `EXM_MY_EXER_QUESTION` MODIFY COLUMN `SCORES` varchar(128) COMMENT '子分数' AFTER `SCORE`;

-- 支持课程学习
create table EXM_COURSE
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(16) comment '名称',
   CONTENT              varchar(128) comment '简介',
   ORG_IDS              text comment '机构IDS',
   USER_IDS             text comment '用户IDS',
   SHARE_AUTH           int comment '共享权限（1：私有；2：只读；3：读写；）',
   STATE                int comment '状态（0：删除；1：正常；2：暂停）',
   CREATE_USER_ID       int comment '创建用户ID',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_COURSE comment '课程';

create table EXM_COURSE_MATERIAL
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(16) comment '名称',
   CONTENT              varchar(128) comment '简介',
   VIDEO_FILE_ID        int comment '视频附件ID',
   VIDEO_TIME           time comment '视频时间',
   QUESTION_NUM         int comment '试题数量',
   COURSE_ID            int comment '课程ID',
   NO                   int comment '排序',
   PARENT_ID            int comment '父ID（用于追溯历史）',
   STATE                int comment '状态（0：删除；1：正常；）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_COURSE_MATERIAL comment '课程资料';

create table EXM_COURSE_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   COURSE_ID            int comment '课程ID',
   COURSE_MATERIAL_ID   int comment '课程资料ID',
   QUESTION_ID          int comment '试题ID',
   COURSE_TIME          time comment '课程时间',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_COURSE_QUESTION comment '课程试题';

create table EXM_MY_COURSE_MATERIAL
(
   ID                   int not null auto_increment comment '主键',
   USER_ID              int comment '用户ID',
   COURSE_MATERIAL_ID   int comment '课程资料ID',
   COURSE_ID            int comment '课程ID',
   NAME                 varchar(16) comment '名称',
   CONTENT              varchar(128) comment '简介',
   VIDEO_FILE_ID        int comment '视频附件ID',
   VIDEO_TIME           time comment '视频时间',
   QUESTION_NUM         int comment '试题数量',
   NO                   int comment '排序',
   WATCH_TIME           time comment '观看时间',
   ACTIVE_TIME          datetime comment '活跃时间',
   STATE                int comment '状态（0：删除；1：完成；2：未开始；3：进行中）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_COURSE_MATERIAL comment '我的课程资料';

create table EXM_MY_COURSE_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   USER_ID              int comment '用户ID',
   COURSE_MATERIAL_ID   int comment '课程资料ID',
   QUESTION_ID          int comment '试题ID',
   COURSE_ID            int comment '课程ID',
   COURSE_TIME          time comment '课程时间',
   ANSWER_TIME          datetime comment '答题时间',
   STATE                int comment '状态（0：删除；1：正常；）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_COURSE_QUESTION comment '我的课程试题';
