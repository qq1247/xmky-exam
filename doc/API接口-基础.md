#在线考试接口文档

##基础权限相关

####用户登录：login/in
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|loginName      | String (16)  |  名称  |  是  |
|pwd      | String (16)  |  名称  |  是  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | jwt格式的令牌 |

####系统时间：login/sysTime
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | yyyy-MM-dd HH:mm:ss |

####组织机构列表：org/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].NAME | String  | 名称 |
|data.list[].PARENT_ID | int  | 父ID |
|data.list[].PARENT_NAME | String  | 父名称 |
|data.list[].NO | int  | 排序 |

####组织机构添加：org/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|parentId      | int  | 父ID   |   否     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####组织机构修改：org/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####组织机构删除：org/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####组织机构查询：org/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.name     | String  | 名称 |
|data.parentId     | int  | 父ID |
|data.parentName   | String  | 父名称 |
|data.no   | int  | 排序 |

####组织机构模板：org/template
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|    |      |      |      ||
######响应数据
字节流

####组织机构导入：org/import
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|file    | file     |   附件   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####组织机构导出：org/export
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|ids    | String     |   逗号分隔的ids   |   否   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户列表：user/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|parentId      | int  | 父ID   |   否     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].NAME | String  | 名称 |
|data.list[].PARENT_ID | int  | 父ID |
|data.list[].PARENT_NAME | String  | 父名称 |
|data.list[].NO | int  | 排序 |

####用户添加：user/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 昵称   |   是     |
|loginName      | String (16)  | 登陆账号   |   否     |
|phone      | String (11)  | 手机号   |   否     |
|email      | String (64)  | 邮箱   |   是     |
|orgId      | int  | 组织机构ID   |   是     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户修改：org/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|loginName      | String (16)  | 登陆账号   |   是     |
|phone      | String (11)  | 手机号   |   否     |
|email      | String (64)  | 邮箱   |   是     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户删除：user/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户更新角色：user/updatePost
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
|postIds    | Integer[]     |   角色id   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户更新机构：user/updateOrg
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
|orgId    | Integer     |   组织机构id   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户查询：user/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.name     | String  | 名称 |
|data.loginName     | String  | 登陆账号 |
|data.registTime     | Date  | 注册时间 |
|data.lastLoginTime     | Date  | 最后登陆时间 |
|data.orgId     | int  | 组织机构ID |
|data.orgName   | String  | 组织机构名称 |
|data.state   | int  | 0：删除；1：正常；2：冻结； |
|data.sort   | int  | 排序 |


####用户模板：user/template
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|    |      |      |      ||
######响应数据
字节流

####用户导入：user/import
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|file    | file     |   附件   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户导出：user/export
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|ids    | String     |   逗号分隔的ids   |   否   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####数据字典列表：dict/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|dictIndex      | String  | 索引   |   是     |
|dictKey      | String  | 键   |   否     |
|dictValue    | String     | 值 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].DICT_INDEX | String  | 索引 |
|data.list[].DICT_KEY | String  | 键 |
|data.list[].DICT_VALUE | String  | 值 |
|data.list[].NO | int  | 排序 |

####数据字典添加：dict/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|dictIndex      | String (32)  | 索引   |   是     |
|dictKey      | String (32)  | 键   |   是     |
|dictValue      | String (32)  | 值  |   是     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####数据字典修改：dict/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | int  | 主键   |   是     |
|dictIndex      | String (32)  | 索引   |   是     |
|dictKey      | String (32)  | 键   |   是     |
|dictValue      | String (32)  | 值  |   是     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####数据字典删除：dict/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####用户列表：cron/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|cronIndex      | String  | 索引   |   是     |
|cronKey      | String  | 键   |   否     |
|cronValue    | String     | 值 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].NAME | String  | 名称 |
|data.list[].JOB_CLASS | String  | 实现类 |
|data.list[].CRON | String  | cron表达式 |
|data.list[].STATE | String  | 状态 |
|data.list[].STATE_NAME | String  | 状态名称 |
|data.list[].RECENT_TRIGGER_TIME | String  | 最近三次运行时间  |

####定时任务添加：cron/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (32)  | 名称   |   是     |
|jobClass      | String (32)  | 实现类   |   是     |
|cron      | String (32)  | cron表达式 |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####定时任务修改：cron/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | int  | 主键   |   是     |
|name      | String (32)  | 名称   |   是     |
|jobClass      | String (32)  | 实现类   |   是     |
|cron      | String (32)  | cron表达式 |   是     |
|no    | int     |   排序   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####定时任务删除：cron/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####附件上传：file/upload
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|files    | file     |   附件  |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | 附件IDS |

####附件下载：file/download
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int   | 主键 |   是   |
######响应数据
字节流

####系统参数修改：pram/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int   | 主键 |   否   |
|emailHost    | String   | 邮箱主机 |   是   |
|emailUserName    | 邮件用户名   | 主键 |   是   |
|emailpwd    | String   | 邮件密码 |   是   |
|emailProtocol    | String   | 邮件协议 |   是   |
|emailEncode    | String   | 邮件编码 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | 附件IDS |

####系统参数获取：pram/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |

######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.emailHost    | String   | 邮箱主机 |
|data.emailUserName    | String   | 邮件用户名 |
|data.emailpwd    | String   | 邮件密码 |
|data.emailProtocol    | String   | 邮件协议 |
|data.emailEncode    | String   | 邮件编码 |