-- 记录版本
INSERT INTO `SYS_VER` VALUES (50, '6.0.0', '2025-12-27 09:30:00', 'zhanghc', '');
UPDATE SYS_PARM SET APP_VER = '6.0.0';

-- 原先表示角色的type改成role，更直观
ALTER TABLE SYS_USER CHANGE TYPE ROLE VARCHAR(16);
ALTER TABLE SYS_USER MODIFY COLUMN ROLE varchar(16) COMMENT '角色（ADMIN：管理员；EXAM_USER：考试用户；SUB_ADMIN：子管理员；MARK_USER：阅卷用户；TEMP_USER：临时用户）' AFTER `PWD`;
UPDATE SYS_USER SET ROLE = 
    CASE ROLE
        WHEN '0' THEN 'ADMIN'
        WHEN '1' THEN 'EXAM_USER'
        WHEN '2' THEN 'SUB_ADMIN'
        WHEN '3' THEN 'MARK_USER'
        WHEN '4' THEN 'TEMP_USER'
        ELSE NULL
    END;

-- 匿名账号登录使用uuid
ALTER TABLE SYS_USER MODIFY COLUMN LOGIN_NAME VARCHAR(32) COMMENT '登陆账号';

-- 支持用户注册，以适配不同的用户群体
ALTER TABLE SYS_PARM ADD COLUMN USER_REGIST INT COMMENT '用户注册（1：是；2：否）' AFTER APP_REL_TIME;
UPDATE SYS_PARM SET USER_REGIST = 2;

ALTER TABLE SYS_USER ADD COLUMN SOURCE VARCHAR(32) COMMENT '用户来源' AFTER ORG_ID;
UPDATE SYS_USER SET SOURCE = '管理员添加';

create table SYS_REGIST_USER
(
   ID                   int not null auto_increment comment '主键',
   LOGIN_NAME           varchar(32) comment '登陆账号',
   PWD                  varchar(32) comment '密码',
   NAME                 varchar(16) comment '姓名',
   ORG_ID               int comment '机构ID',
   REGIST_TIME          datetime comment '注册时间',
   STATE                int comment '状态（1：通过；2：拒绝；3：待审核）',
   REMARK               varchar(64) comment '审批意见',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_REGIST_USER comment '注册用户';

INSERT INTO `SYS_DICT` VALUES (64, 'APPROVE_STATE', '1', '通过', 1);
INSERT INTO `SYS_DICT` VALUES (65, 'APPROVE_STATE', '2', '拒绝', 2);
INSERT INTO `SYS_DICT` VALUES (66, 'APPROVE_STATE', '3', '待审核', 3);

-- 恢复试题评论
ALTER TABLE EXM_EXER CHANGE COLUMN RMK_STATE COMMENT_STATE INT COMMENT '评论状态（1：是；2：否）';
UPDATE EXM_EXER SET COMMENT_STATE = 2;

drop table if exists EXM_EXER_RMK;
create table EXM_MY_COMMENT
(
   ID                   int not null auto_increment comment '主键',
   USER_ID              int comment '用户ID',
   QUESTION_ID          int comment '试题ID',
   CONTENT              varchar(128) comment '内容',
   LIKE_USER_IDS        text comment '点赞用户IDS',
   LIKE_NUM             int comment '点赞数量',
   STATE                int comment '状态（0：删除；1：正常）',
   REPLY_USER_ID        int comment '回复用户ID',
   PARENT_ID            int comment '父评论ID（多层级）',
   ROOT_ID              int comment '根评论ID',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_COMMENT comment '我的评论';
ALTER TABLE EXM_MY_COMMENT ADD INDEX `EXM_MY_COMMENT_Q` ( `QUESTION_ID` );

-- 支持用户头像
ALTER TABLE SYS_USER CHANGE COLUMN HEAD_FILE_ID AVATAR_FILE_ID INT COMMENT '头像附件ID';

-- 支持删除考试用户练习记录（用户个人数据不删除，如错题集、收藏、评论）
ALTER TABLE EXM_MY_EXER ADD COLUMN `STATE` int COMMENT '状态（0：删除；1：正常；）' AFTER QA_NUM;
UPDATE EXM_MY_EXER SET STATE = 1;

-- 支持题库设置权限
ALTER TABLE EXM_QUESTION_BANK ADD COLUMN SHARE_AUTH INT COMMENT '共享权限（1：私有；2：只读；3：读写；）' AFTER QUESTION_NUM;
UPDATE EXM_QUESTION_BANK SET SHARE_AUTH = 1;
INSERT INTO `SYS_DICT` VALUES (67, 'SHARE_AUTH', '1', '私有', 1);
INSERT INTO `SYS_DICT` VALUES (68, 'SHARE_AUTH', '2', '只读', 2);
INSERT INTO `SYS_DICT` VALUES (69, 'SHARE_AUTH', '3', '读写', 3);