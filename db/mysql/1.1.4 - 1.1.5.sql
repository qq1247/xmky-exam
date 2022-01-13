INSERT INTO `SYS_VER` VALUES (5, '1.1.5', '2019-12-16 23:16:00', 'ZHC', '');

drop table if exists SYS_CRON;

/*==============================================================*/
/* Table: SYS_CRON                                              */
/*==============================================================*/
create table SYS_CRON
(
   ID                   int not null auto_increment comment '主键',
   NAME                 varchar(32) comment '名称',
   JOB_CLASS            varchar(64) comment '实现类',
   CRON                 varchar(64) comment 'cron表达式',
   STATE                int comment '1：启动；2：停止；',
   UPDATE_USER_ID       int comment '修改人',
   UPDATE_TIME          datetime comment '修改时间',
   primary key (ID)
);

alter table SYS_CRON comment '定时任务';

INSERT INTO `SYS_CRON` VALUES ('1', '清除临时附件', 'com.wcpdoc.exam.file.job.ClearFileJob', '0 0 1 * * ?', '1', '1', '2019-09-13 19:31:07');
INSERT INTO `SYS_CRON` VALUES ('2', '强制交卷', 'com.wcpdoc.exam.core.job.ForcePaperJob', '0 0 1 * * ?', '1', '1', '2019-09-13 19:31:07');
