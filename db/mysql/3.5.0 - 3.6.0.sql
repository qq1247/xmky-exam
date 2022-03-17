INSERT INTO `SYS_VER` VALUES (16, '3.6.0', '2021-03-17 16:14:00', 'zhanghc', '');
DELETE FROM `SYS_CRON` WHERE ID IN (3, 4);

drop table if exists EXM_PAPER_QUESTION_RULE;

/*==============================================================*/
/* Table: EXM_PAPER_QUESTION_RULE                               */
/*==============================================================*/
create table EXM_PAPER_QUESTION_RULE
(
   ID                   int not null auto_increment comment 'id',
   QUESTION_TYPE_ID     int comment '试题分类ID',
   TYPE                 int comment '1：单选；2：多选；3：填空；4：判断；5：问答',
   DIFFICULTYS          varchar(16) comment '1：极易；2：简单；3：适中；4：困难；5：极难',
   AIS                  varchar(16) comment '智能阅卷',
   SCORE_OPTIONS        varchar(8) comment '1：漏选得分；2：答案无顺序；3：大小写不敏感；',
   NUM                  int comment '题数',
   SCORE                decimal(5,2) comment '分数',
   PAPER_ID             int comment '试卷ID',
   PAPER_QUESTION_ID    int comment '试卷试题ID',
   NO                   int comment '排序',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_PAPER_QUESTION_RULE comment '试卷试题规则';

ALTER TABLE `EXM_PAPER_QUESTION` ADD COLUMN `PAPER_QUESTION_RULE_ID` INT(11) DEFAULT NULL COMMENT '试卷试题规则ID' AFTER `NO`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD COLUMN `EXAM_ID` INT(11) DEFAULT NULL COMMENT '考试ID' AFTER `PAPER_QUESTION_RULE_ID`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD COLUMN `USER_ID` INT(11) DEFAULT NULL COMMENT '考试用户ID' AFTER `EXAM_ID`;
ALTER TABLE `EXM_PAPER_QUESTION` ADD INDEX `QUESTION_ID`(`QUESTION_ID`);