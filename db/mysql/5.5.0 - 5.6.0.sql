-- 记录版本
INSERT INTO `SYS_VER` VALUES (48, '5.6.0', '2025-09-30 15:53:00', 'zhanghc', '');
UPDATE SYS_PARM SET APP_VER = '5.6.0';

-- 支持登录页底部显示的备案、版权或自定义信息
ALTER TABLE `SYS_PARM` ADD COLUMN `ICP` varchar(512) DEFAULT NULL COMMENT '备案信息' AFTER `CUSTOM_CONTENT`;
ALTER TABLE `SYS_PARM` CHANGE COLUMN `ENT_NAME` `SYS_NAME` varchar(32) DEFAULT NULL COMMENT '系统名称' AFTER `EMAIL_ENCODE`;

-- 去掉无效定时任务
DELETE FROM SYS_CRON WHERE ID = 3;

-- 添加我的练习跟踪
create table EXM_MY_EXER_TRACK
(
   ID                   int not null auto_increment comment '主键',
   USER_ID              int comment '用户ID',
   EXER_ID              int comment '练习ID',
   YMD                  int comment '年月日',
   MINUTE_TICKS         varchar(8192) comment '分钟刻度（统计用户在当天的第几分钟正在练习，最小1，最大1440，多个值用英文逗号分割）',
   MINUTE_COUNT         int comment '分钟累计',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

ALTER TABLE EXM_MY_EXER_TRACK comment '我的练习跟踪';
ALTER TABLE `EXM_MY_EXER_TRACK` ADD INDEX `MY_EXER_TRACK_UE` ( `USER_ID`,`EXER_ID` );

-- 添加我的练习跟踪月度
create table EXM_MY_EXER_TRACK_MONTHLY
(
   ID                   int not null auto_increment comment '主键',
   USER_ID              int comment '用户ID',
   EXER_ID              int comment '练习ID',
   YM                   int comment '年月',
   MINUTE_COUNT         int comment '分钟累计',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

ALTER TABLE EXM_MY_EXER_TRACK_MONTHLY comment '我的练习跟踪月度';
ALTER TABLE `EXM_MY_EXER_TRACK_MONTHLY` ADD INDEX `EXM_MY_EXER_TRACK_MONTHLY_UE` ( `USER_ID`,`EXER_ID` );

-- 管理员创建练习支持选择多个题库，用户练习可以自由组合试题进行练习
ALTER TABLE `EXM_EXER` ADD COLUMN `CREATE_USER_ID` int(11) DEFAULT NULL COMMENT '创建用户ID' AFTER `RMK_STATE`;
UPDATE EXM_EXER EE JOIN EXM_QUESTION_BANK EQB ON EE.QUESTION_BANK_ID = EQB.ID SET EE.CREATE_USER_ID = EQB.CREATE_USER_ID;
ALTER TABLE `EXM_EXER` CHANGE COLUMN `QUESTION_BANK_ID` `QUESTION_BANK_IDS` VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '题库IDS';
UPDATE `EXM_EXER` SET `QUESTION_BANK_IDS` = CONCAT(',', `QUESTION_BANK_IDS`, ',');
ALTER TABLE `EXM_EXER` MODIFY COLUMN `ORG_IDS` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '机构IDS' AFTER `QUESTION_BANK_IDS`;

-- 练习支持用户自由组合试题，方便模拟考试等情景（会丢之前的数据，写兼容sql太复杂，非核心业务丢之前的数据也影响不大）
drop table if exists EXM_MY_EXER;
create table EXM_MY_EXER
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(64) comment '名称',
   USER_ID              int comment '用户ID',
   EXER_ID              int comment '练习ID',
   TYPE                 int comment '类型（1：自组；2：未练；3：错题；4：收藏）',
   SINGLE_NUM           int comment '单选题数量',
   MULTIPLE_NUM         int comment '多选题数量',
   FILL_BLANK_NUM       int comment '填空题数量',
   JUDGE_NUM            int comment '判断题数量',
   QA_NUM               int comment '问答题数量',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_EXER comment '我的练习';
ALTER TABLE `EXM_MY_EXER` ADD INDEX `MY_EXER_UE` ( `USER_ID`,`EXER_ID` );

drop table if exists EXM_MY_EXER_QUESTION;
create table EXM_MY_EXER_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   MY_EXER_ID           int comment '我的练习ID',
   QUESTION_ID          int comment '试题ID',
   QUESTION_TYPE        int comment '试题类型（1：单选题；2多选题；3：填空题；4：判断题；5：问答题；）',
   NO                   int comment '排序',
   SCORE                decimal(5,2) comment '分数',
   SCORES               varchar(64) comment '子分数',
   MARK_OPTIONS         varchar(8) comment '阅卷选项（2：答案无顺序；3：不分大小写；)',
   ANSWER_TIME          datetime comment '答题时间',
   USER_ANSWER          text comment '用户答案',
   USER_SCORE           decimal(5,2) comment '用户得分',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_EXER_QUESTION comment '我的练习试题';
ALTER TABLE `EXM_MY_EXER_QUESTION` ADD INDEX `MY_EXER_QUESTION_MQ` ( `MY_EXER_ID`, `QUESTION_ID` );

-- 练习的错题集和收藏从练习中独立出来，以适应业务变更
create table EXM_MY_WRONG_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   USER_ID              int comment '用户ID',
   QUESTION_ID          int comment '试题ID',
   QUESTION_TYPE        int comment '试题类型',
   WRONG_NUM            int comment '答错次数',
   FIRST_WRONG_TIME     datetime comment '首次答错时间',
   FIRST_WRONG_SOURCE   varchar(32) comment '首次答错来源',
   LAST_WRONG_TIME      datetime comment '末次答错时间',
   LAST_WRONG_SOURCE    varchar(32) comment '末次答错来源',
   STATE                int comment '状态（1：已掌握；2：未掌握）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_WRONG_QUESTION comment '我的错题';
ALTER TABLE `EXM_MY_WRONG_QUESTION` ADD INDEX `MY_WRONG_QUESTION_U` ( `USER_ID` );

create table EXM_MY_FAV_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   USER_ID              int comment '用户ID',
   QUESTION_ID          int comment '试题ID',
   QUESTION_TYPE        int comment '试题类型',
   FAV_TIME             datetime comment '收藏时间',
   FAV_SOURCE           varchar(32) comment '收藏来源',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_FAV_QUESTION comment '我的收藏试题';
ALTER TABLE `EXM_MY_FAV_QUESTION` ADD INDEX `MY_FAV_QUESTION_U` ( `USER_ID` );