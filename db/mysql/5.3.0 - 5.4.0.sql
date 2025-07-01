-- 记录版本
INSERT INTO `SYS_VER` VALUES (46, '5.4.0', '2025-07-01 21:07:00', 'zhanghc', '');
UPDATE SYS_PARM SET APP_VER = '5.4.0';

-- 练习全局只能关联一个题库，多余的清除
DELETE E1 FROM EXM_EXER E1
INNER JOIN EXM_EXER E2 ON E1.QUESTION_BANK_ID = E2.QUESTION_BANK_ID 
WHERE E1.ID < E2.ID;

-- 练习支持按机构筛选用户
ALTER TABLE `EXM_EXER` ADD COLUMN `ORG_IDS` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '机构IDS' AFTER `USER_IDS`;

-- 练习删除开始时间和结束时间，由人工控制
ALTER TABLE `EXM_EXER` DROP COLUMN `START_TIME`;
ALTER TABLE `EXM_EXER` DROP COLUMN `END_TIME`;

-- 练习删除创建用户，使用题库的创建用户ID
ALTER TABLE `EXM_EXER` DROP COLUMN `CREATE_USER_ID`;

-- 练习变更字段注释
ALTER TABLE `EXM_EXER` MODIFY COLUMN `USER_IDS` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '用户IDS' AFTER `QUESTION_BANK_ID`;
ALTER TABLE `EXM_EXER` MODIFY COLUMN `STATE` int(11) DEFAULT NULL COMMENT '状态（0：删除；1：发布；2：暂停）' AFTER `ORG_IDS`;

-- 我的练习
create table EXM_MY_EXER
(
   ID                   int not null auto_increment comment '主键',
   EXER_ID              int comment '练习ID',
   USER_ID              int comment '用户ID',
   TYPE                 int comment '类型（1：单选题；2多选题；3：填空题；4：判断题；5：问答题）',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_EXER comment '我的练习';

-- 我的练习试题
create table EXM_MY_EXER_QUESTION
(
   ID                   int not null auto_increment comment '主键',
   EXER_ID              int comment '练习ID',
   USER_ID              int comment '用户ID',
   TYPE                 int comment '类型（1：单选题；2多选题；3：填空题；4：判断题；5：问答题；6：主观题）',
   QUESTION_ID          int comment '试题ID',
   NO                   int comment '排序',
   SHUFFLE_NO           int comment '随机排序',
   SCORE                decimal(5,2) comment '分数',
   SCORES               varchar(64) comment '子分数',
   MARK_OPTIONS         varchar(8) comment '阅卷选项（2：答案无顺序；3：不分大小写；)',
   ANSWER_TIME          datetime comment '答题时间',
   USER_ANSWER          text comment '用户答案',
   USER_SCORE           decimal(5,2) comment '用户得分',
   FAV                  int comment '收藏（1：是；2：否）',
   WRONG_ANSWER_NUM     int comment '答错次数',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_EXER_QUESTION comment '我的练习试题';

-- 加速练习查询
ALTER TABLE `EXM_MY_EXER` ADD INDEX `MY_EXER_EU` ( `EXER_ID`,`USER_ID` );
ALTER TABLE `EXM_MY_EXER_QUESTION` ADD INDEX `MY_EXER_QUESTION_EUQ` ( `EXER_ID`,`USER_ID`, `QUESTION_ID` );