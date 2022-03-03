INSERT INTO `SYS_VER` VALUES (16, '3.6.0', '2022-02-28 13:58:02', 'zhanghc', '');
DELETE FROM `SYS_CRON` WHERE ID IN (3, 4);

drop table if exists EXM_RAND_CHAPTER_RULES;

/*==============================================================*/
/* Table: EXM_RAND_CHAPTER_RULES                                */
/*==============================================================*/
create table EXM_RAND_CHAPTER_RULES
(
   ID                   int not null auto_increment comment 'id',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   PAPER_ID             int comment '试卷ID',
   QUESTION_TYPE_ID     int comment '试题分类ID',
   TYPE                 int comment '1：单选；2：多选；3：填空；4：判断；5：问答',
   DIFFICULTY           varchar(10) comment '1：极易；2：简单；3：适中；4：困难；5：极难',
   AI                   varchar(10) comment '智能阅卷',
   SCORE_OPTIONS        varchar(8) comment '1：漏选得分；2：答案无顺序；3：大小写不敏感；',
   TOTAL_NUMBER         int comment '几道题',
   SCORE                decimal(5,2) comment '分数',
   NO                   int comment '排序',
   PAPER_QUESTION_ID    int comment '试卷试题ID',
   primary key (ID)
);

alter table EXM_RAND_CHAPTER_RULES comment '随机章节规则';

ALTER TABLE `EXM_PAPER_QUESTION` ADD COLUMN `RAND_CHAPTER_RULES_ID` INT(11) DEFAULT NULL COMMENT '随机章节ID' AFTER `NO`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD COLUMN `EXAM_ID` INT(11) DEFAULT NULL COMMENT '考试ID' AFTER `RAND_CHAPTER_RULES_ID`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD COLUMN `USER_ID` INT(11) DEFAULT NULL COMMENT '考试用户ID' AFTER `EXAM_ID`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD INDEX `QUESTION_ID`(`QUESTION_ID`) USING BTREE;