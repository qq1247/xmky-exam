INSERT INTO `SYS_DICT` VALUES (60, 'EXAM_SXES', '1', '试题乱序', 1);
INSERT INTO `SYS_DICT` VALUES (61, 'EXAM_SXES', '2', '选项乱序', 2);
INSERT INTO `SYS_DICT` VALUES (62, 'EXAM_SXES', '3', '禁止考试中切屏', 3);
INSERT INTO `SYS_DICT` VALUES (63, 'EXAM_SXES', '4', '禁止浏览器调试', 4);

create table `EXM_MY_SXES`
(
   ID                   int not null auto_increment comment 'id',
   EXAM_ID              int comment '考试ID',
   USER_ID              int comment '考试用户ID',
   SXES_TYPE            int comment '作弊类型（3：禁止考试中切屏；4：禁止浏览器调试）',
   CONTENT              varchar(128) comment '作弊内容',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table `EXM_MY_SXES` comment '我的作弊';

ALTER TABLE `EXM_EXAM` MODIFY COLUMN `SXES` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '防作弊（1：试题乱序；2：选项乱序；3：禁止考试中切屏；4：禁止浏览器调试）' AFTER `TOTAL_SCORE`;

INSERT INTO `SYS_VER`(`ID`, `VER`, `UPDATE_TIME`, `AUTHOR`, `REMARK`) VALUES (41, '5.1.0', '2025-03-27 17:12:00', 'zhanghc', '');