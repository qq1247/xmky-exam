INSERT INTO SYS_VER VALUES (21, '3.8.0', '2021-05-13 16:34:00', 'zhanghc', '');

INSERT INTO `SYS_CRON` VALUES (3, '清理无效试题', 'com.wcpdoc.exam.core.job.QuestionDelJob', '0 0 0 1/1 * ? ', 1, 1, '2022-04-28 13:00:00');