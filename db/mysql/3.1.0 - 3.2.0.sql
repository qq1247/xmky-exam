INSERT INTO `SYS_CRON` VALUES (2, '数据库备份', 'com.wcpdoc.exam.quartz.job.DbBackJob', '0 0 0 1/1 * ? ', 1, 1, '2020-08-26 18:42:08');

INSERT INTO `SYS_VER` VALUES (10, '3.2.0', '2020-10-31 16:43:00', 'zhanghc', '');

INSERT INTO `SYS_DICT` VALUES (40, 'PAPER_MARK_TYPE', '1', '智能阅卷', 1);
INSERT INTO `SYS_DICT` VALUES (41, 'PAPER_MARK_TYPE', '2', '人工阅卷', 2);