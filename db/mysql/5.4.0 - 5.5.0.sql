-- 记录版本
INSERT INTO `SYS_VER` VALUES (47, '5.5.0', '2025-07-31 09:15:00', 'zhanghc', '');
UPDATE SYS_PARM SET APP_VER = '5.5.0', APP_REL_TIME = '2025-07-31 09:15:00';

-- 试题题干支持视频
ALTER TABLE `EXM_QUESTION` ADD COLUMN `VIDEO_FILE_ID` int(11) DEFAULT NULL COMMENT '视频ID' AFTER `IMG_FILE_IDS`;

-- 考试不及格时支持重考
ALTER TABLE `EXM_EXAM` ADD COLUMN `RETAKE_NUM` int(11) DEFAULT NULL COMMENT '重考次数' AFTER `MARK_USER_IDS`;
ALTER TABLE `EXM_MY_EXAM` ADD COLUMN `VER` int(11) DEFAULT NULL COMMENT '版本' AFTER `ANSWER_STATE`;
ALTER TABLE `EXM_MY_QUESTION` ADD COLUMN `VER` int(11) DEFAULT NULL COMMENT '版本' AFTER `MARK_TIME`;
create table EXM_MY_EXAM_HIS
(
   ID                   int not null auto_increment comment '主键',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '用户ID',
   MARK_USER_ID         int comment '阅卷用户ID',
   ANSWER_START_TIME    datetime comment '考试开始时间',
   ANSWER_END_TIME      datetime comment '考试结束时间',
   MARK_START_TIME      datetime comment '阅卷开始时间',
   MARK_END_TIME        datetime comment '阅卷结束时间',
   OBJECTIVE_SCORE      decimal(5,2) comment '客观题分数',
   TOTAL_SCORE          decimal(5,2) comment '总分数',
   STATE                int comment '状态（1：未考试；2：考试中；3：已交卷；）',
   MARK_STATE           int comment '阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）',
   ANSWER_STATE         int comment '答题状态（1：及格；2：不及格）',
   NO                   int comment '排序',
   VER                  int comment '版本',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_EXAM_HIS comment '我的考试历史';

create table EXM_MY_QUESTION_HIS
(
   ID                   int not null auto_increment comment '主键',
   EXAM_ID              int comment '考试ID',
   QUESTION_ID          int comment '试题ID',
   USER_ID              int comment '用户ID',
   CHAPTER_NAME         varchar(32) comment '章节名称',
   CHAPTER_TXT          varchar(512) comment '章节描述',
   TYPE                 int comment '类型 （1：章节；2：试题）',
   SCORE                decimal(5,2) comment '分数',
   SCORES               varchar(64) comment '子分数',
   MARK_OPTIONS         varchar(8) comment '阅卷选项（2：答案无顺序；3：不区分大小写；)',
   NO                   int comment '排序',
   OPTIONS_NO           varchar(16) comment '选项排序，如：2,1,4,3',
   ANSWER_TIME          datetime comment '答题时间',
   USER_ANSWER          text comment '用户答案',
   USER_SCORE           decimal(5,2) comment '用户得分',
   MARK_USER_ID         int comment '阅卷用户ID',
   MARK_TIME            datetime comment '阅卷时间',
   VER                  int comment '版本',
   IMG_FILE_IDS         varchar(32) comment '图片附件IDS',
   VIDEO_FILE_IDS       varchar(32) comment '视频附件IDS',
   REMARK               varchar(512) comment '批语',
   REMARK_USER_ID       int comment '批语用户ID',
   REMARK_TIME          datetime comment '批语时间',
   UPDATE_USER_ID       int comment '修改用户ID',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table EXM_MY_QUESTION_HIS comment '我的试题历史';

-- 修改我的试题的顺序，方便查看
ALTER TABLE `EXM_MY_QUESTION` MODIFY COLUMN `EXAM_ID` int(11) DEFAULT NULL COMMENT '考试ID' AFTER `ID`;
ALTER TABLE `EXM_MY_QUESTION` MODIFY COLUMN `QUESTION_ID` int(11) DEFAULT NULL COMMENT '试题ID' AFTER `EXAM_ID`;
ALTER TABLE `EXM_MY_QUESTION` MODIFY COLUMN `USER_ID` int(11) DEFAULT NULL COMMENT '用户ID' AFTER `QUESTION_ID`;

-- 防作弊表名错误，导致不记录
ALTER TABLE `EXM_MY_SXES` RENAME TO `EXM_MY_SXE`;
ALTER TABLE `EXM_MY_SXE` CHANGE COLUMN `SXES_TYPE` `TYPE` int(11) DEFAULT NULL COMMENT '作弊类型（3：禁止考试中切屏；4：禁止浏览器调试）';

-- 答题支持上传图片和视频
ALTER TABLE `EXM_MY_QUESTION` ADD COLUMN `IMG_FILE_IDS` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '图片附件IDS' AFTER `VER`;
ALTER TABLE `EXM_MY_QUESTION` ADD COLUMN `VIDEO_FILE_IDS` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '视频附件IDS' AFTER `IMG_FILE_IDS`;

-- 阅卷增加批语
ALTER TABLE `EXM_MY_QUESTION` ADD COLUMN `REMARK` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '批语' AFTER `VIDEO_FILE_IDS`;
ALTER TABLE `EXM_MY_QUESTION` ADD COLUMN `REMARK_USER_ID` int(11) DEFAULT NULL COMMENT '批语用户ID' AFTER `REMARK`;
ALTER TABLE `EXM_MY_QUESTION` ADD COLUMN `REMARK_TIME` datetime(0) DEFAULT NULL COMMENT '批语时间' AFTER `REMARK_USER_ID`;