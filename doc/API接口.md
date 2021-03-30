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
|data.sort   | int  | 排序 |

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
|loginName      | String (16)  | 登陆账号   |   是     |
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
|postId    | Integer[]     |   角色id   |   是   |
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

####用户列表：dict/list
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

##考试相关
####试题分类列表：questionType/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   否     |
|curPage      | int | 当前第几页  |   否     |
|pageSize      | int  | 每页多少条，最大100条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].NAME | String  | 名称 |
|data.list[].PW_STATE | int  | 1：开启；2：禁用 |
|data.list[].READ_USER_NAME | String  | 读权限 |
|data.list[].WRITE_USER_NAME | String  | 写权限 |

####试题分类详情：questionType/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | int  | 主键 |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.id     | int  | 分类id |
|data.name      | String  | 分类名称 |
|data.img   | int  | 图片 |
|data.createUserId   | int  | 创建人 |
|data.createTime   | String  | 创建时间 |
|data.rwState   | int  | 读写状态【1：开启；2：禁用'】 |
|data.readUserIds   | int  | 读权限 |
|data.writeUserIds   | int  | 写权限 |

####试题分类添加：questionType/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|imgId      | int  | 图片ID |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类修改：questionType/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | int | 主键 |   是     |
|name      | String (16)  | 名称   |   是     |
|imgId      | int  | 图片ID   |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类删除：questionType/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类权限：questionType/auth
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
|readUserIds    | String     |   读权限   |   否   |
|writeUserIds    | String |  写权限 |  否  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类合并：questionType/move
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
|sourceId    | String     |   源ID   |   否   |
|targetId    | String | 目标ID |  否  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类开放添加：questionTypeOpen/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
|startTime    | Date | 开始时间 | 是  |
|endTime    | Date | 结束时间 |  是  |
|userIds    | String(1024) | 授权用户 |  否  |
|orgIds  | String(1024) | 授权机构 |  是  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类开放删除：questionTypeOpen/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题列表：question/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|questionTypeId| int | 题库id（试题分类id）   |   否     |
|id| int | 试卷id   |   否     |
|title      | String  | 题干 |   否     |
|type      | int | 类型 |   否     |
|difficulty    | int | 难度 |   否     |
|scoreStart    | double | 分值大于等于 |   否     |
|scoreEnd    | double | 分值小于等于 |   否     |
|curPage      | int | 当前第几页  |   否     |
|pageSize      | int  | 每页多少条，最大100条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].TYPE | int | 类型 |
|data.list[].TYPE_NAME | String  | 类型名称 |
|data.list[].DIFFICULTY | int | 难度 |
|data.list[].DIFFICULTY_NAME | String  | 难度名称 |
|data.list[].PW_STATE | int  | 1：开启；2：禁用 |
|data.list[].READ_USER_NAME | String  | 读权限 |
|data.list[].WRITE_USER_NAME | String  | 写权限 |

####试题添加：question/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|type    | int     | 1：单选；2：多选；3：填空；4：判断；5：问答 |   是   |
|difficulty    | int     |   难度   |   是   |
|title    | String（65535）     | 题干 |   是   |
|options[]    | arr     |   选项A、B、C、D、E、F、G  |   否   |
|answer    | String(65535)     |   答案   |   是   |
|analysis    | String(65535)     |   解析   |   是   |
|state    | int  |   1：启用；2：禁用   |   是   |
|questionTypeId    | int  |   试题分类ID   |   是   |
|score    | double  | 默认分值  |  否   |
|scoreOptions    | String(8)  | 1：半对半分；2：答案无顺序；3：大小写不敏感；4：包含答案得分 |  否   |
|no    | int  | 排序 | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题删除：question/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     | 主键 |   是   |
|type    | int     | 1：单选；2：多选；3：填空；4：判断；5：问答 |   是   |
|difficulty    | int     |   难度   |   是   |
|title    | String（65535）     | 题干 |   是   |
|options[]    | arr     |   选项A、B、C、D、E、F、G  |   否   |
|answer    | String(65535)     |   答案   |   是   |
|analysis    | String(65535)     |   解析   |   是   |
|state    | int  |   1：启用；2：禁用   |   是   |
|questionTypeId    | int  |   试题分类ID   |   是   |
|score    | double  | 默认分值  |  否   |
|scoreOptions    | String(8)  | 1：半对半分；2：答案无顺序；3：大小写不敏感；4：包含答案得分 |  否   |
|no    | int  | 排序 | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题删除：question/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     | 主键 |   是   |

######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题获取：question/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     | 主键 |   是   |
|type    | int     | 1：单选；2：多选；3：填空；4：判断；5：问答 |   是   |
|difficulty    | int     |   难度   |   是   |
|title    | String（65535）     | 题干 |   是   |
|options[]    | arr     |   选项A、B、C、D、E、F、G  |   否   |
|answer    | String(65535)     |   答案   |   是   |
|analysis    | String(65535)     |   解析   |   是   |
|state    | int  |   1：启用；2：禁用   |   是   |
|questionTypeId    | int  |   试题分类ID   |   是   |
|score    | double  | 默认分值  |  否   |
|scoreOptions    | String(8)  | 1：半对半分；2：全对得分 3：答案无顺序；4：大小写不敏感；5：包含答案得分 |  否   |
|no    | int  | 排序 | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题获取：question/copy
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷列表：paper/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperTypeId| int | 试卷分类ID   |   否     |
|name| String | 试卷名称   |   否     |
|curPage      | int | 当前第几页  |   否     |
|pageSize      | int  | 每页多少条，最大100条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].NAME   | String  | 名称 |
|data.list[].STATE   | int  | 状态 |
|data.list[].STATE_NAME   | String  | 状态名称 |
|data.list[].UPDATE_USER_ID   | int  | 修改人 |
|data.list[].UPDATE_TIME   | DateTime  | 修改时间 |
|data.list[].PASS_SCORE   | double  | 及格分数（百分比） |
|data.list[].TOTAL_SCORE   | double  | 总分数 |
|data.list[].PAPER_TYPE_ID   | int  | 试卷分类id |
|data.list[].PAPER_TYPE_NAME   | String  | 试卷分类名称 |
|data.list[].DESCRIPTION   | String  | 描述 |
|data.list[].SHOW_TYPE   | int  | 1：整卷展示；2：章节显示；3：单题展示；数据字典：PAPER_SHOW_TYPE |
|data.list[].GEN_TYPE   | int  | 1：人工组卷；2：随机组卷 |

####试卷添加：paper/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| genType | int  | 1：人工组卷；2：随机组卷 |   是   |
|name    | String(32)     | 名称 |   是   |
|passScore    | double     | 及格分数（百分比） |   是   |
|readRemark    | String(65535)  | 考前阅读 |   是   |
|readNum    | int  | 阅读时长【分钟】 |   是   |
|showType    | int | 1：整卷展示；2：章节显示；3：单题展示；数据字典：PAPER_SHOW_TYPE |   否   |
|question    | int | 试题乱序 【1：正常；2：乱序】|   否   |
|questionOption    | int | 选项乱序 【1：正常；2：乱序】|   否   |
|rightClick    | int | 禁用右键 【1：正常；2：禁用】|   否  |
|rightCopy    | int | 禁止复制 【1：正常；2：禁用】|   否   |
|minimize    | int | 最小化 【1：正常；2：禁用】|  否   |
|minimizeNum    | int | 次数 |   否   |
|scoreA    | double | 占分比 |   否   |
|scoreARemark    | String(32) | 评语 |  否   |
|scoreB    | double | 占分比 |   否   |
|scoreBRemark    | String(32) | 评语 |   否   |
|scoreC    | double | 占分比 |   否  |
|scoreCRemark    | String(32) | 评语 |   否   |
|scoreD    | double | 占分比 |   否   |
|scoreDRemark    | String(32) | 评语 |  否   |
|scoreE    | double | 备注 |   否   |
|scoreERemark    | String(32) | 评语 |   否   |

######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷修改回显：paper/echo
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperId| int | 试卷ID   |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.paperRemark.id     | int  | 试卷评语主键 |
|data.paperRemark.scoreA     | double  | 分数A（%） |
|data.paperRemark.scoreARemark     | int  | 分数A评语 |
|data.paperRemark.scoreB     | int  | 分数B（%）|
|data.paperRemark.scoreBRemark     | int  | 分数B评语 |
|data.paperRemark.scoreC     | int  | 分数C（%）|
|data.paperRemark.scoreCRemark     | int  | 分数C评语 |
|data.paperRemark.scoreD     | int  | 分数D（%） |
|data.paperRemark.scoreDRemark     | int  | 分数D评语|
|data.paperRemark.scoreE     | int  | 分数E（%） |
|data.paperRemark.scoreERemark     | int  | 分数E评语 |
|data.paperRemark.paperId     | int  | 试卷id |
|data.paper.id | int  | 试卷主键 |
|data.paper.name | String  | 试卷名称 |
|data.paper.passScore | double  | 试卷及格分数（%） |
|data.paper.totalScore | double  | 试卷总分 |
|data.paper.showType | int  | 展示方式 |
|data.paper.readRemark | String  | 考前阅读 |
|data.paper.readNum | int  | 阅读时长 |
|data.paper.state | int  | 试卷状态 |
|data.paper.paperTypeId | int  | 试卷分类id |
|data.paper.genType | int  | 组卷方式 |
|data.paperOption.id | int  | 防作弊主键 |
|data.paperOption.question | int  | 试题乱序 |
|data.paperOption.questionOption | int  | 试题选项乱序 |
|data.paperOption.rightClick | int  | 禁用右键 |
|data.paperOption.rightCopy | int  | 禁用复制 |
|data.paperOption.minimize | int  | 最小化警告 |
|data.paperOption.minimizeNum | int  | 最小化警告次数 |
|data.paperOption.paperId | int  | 试卷id |

####试卷修改：paper/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperId    | int     | 试卷id |   是   |
|genType | int  | 1：人工组卷；2：随机组卷 |   是   |
|name    | String(32)     | 名称 |   是   |
|passScore    | double     | 及格分数（百分比） |   是   |
|readRemark    | String(65535)  | 考前阅读 |   是   |
|readNum    | int  | 阅读时长【分钟】 |   是   |
|showType    | int | 1：整卷展示；2：章节显示；3：单题展示；数据字典：PAPER_SHOW_TYPE |   否   |
|question    | int | 试题乱序 【1：正常；2：乱序】|   否   |
|questionOption    | int | 选项乱序 【1：正常；2：乱序】|   否   |
|rightClick    | int | 禁用右键 【1：正常；2：禁用】|   否  |
|rightCopy    | int | 禁止复制 【1：正常；2：禁用】|   否   |
|minimize    | int | 最小化 【1：正常；2：禁用】|  否   |
|minimizeNum    | int | 次数 |   否   |
|scoreA    | double | 占分比 |   否   |
|scoreARemark    | String(32) | 评语 |  否   |
|scoreB    | double | 占分比 |   否   |
|scoreBRemark    | String(32) | 评语 |   否   |
|scoreC    | double | 占分比 |   否  |
|scoreCRemark    | String(32) | 评语 |   否   |
|scoreD    | double | 占分比 |   否   |
|scoreDRemark    | String(32) | 评语 |  否   |
|scoreE    | double | 备注 |   否   |
|scoreERemark    | String(32) | 评语 |   否   |

######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷删除：paper/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     | 主键 |   是   |

######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷获取：paper/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.id     | int  | 主键 |
|data.name     | String  | 名称 |
|data.state     | int  | 状态 |
|data.paperTypeId     | int  | 试卷分类id |
|data.readRemark     | String  | 靠前阅读 |
|data.readNum     | int  | 阅读时间【分钟】 |
|data.passScore     | int  | 及格分数（百分比） |
|data.totalScore     | int  | 总分 |

####试卷拷贝：paper/copy
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试题选项获取：questionOption/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|questionId    | int     | 试题id |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.id   | int  | 主键 |
|data.optionA   | String  | 选项A |
|data.optionB   | String | 选项B |
|data.optionC   | String  | 选项C |
|data.optionD   | String  | 选项D |
|data.optionE   | String  | 选项E |
|data.optionF	| String  | 选项F |
|data.optionG   | String  | 选项G |
|data.questionId   | int  | 主键 |

####试卷选项添加：questionOption/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|optionA   | String  | 选项A |   否   |
|optionB   | String | 选项B |  否   |
|optionC   | String  | 选项C |   否   |
|optionD   | String  | 选项D |   否   |
|optionE   | String  | 选项E |   否   |
|optionF	| String  | 选项F |   否   |
|optionG   | String  | 选项G |   否   |
|questionId   | int  | 试题主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷选项修改：questionOption/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id   | int  | 主键 |   是   |
|optionA   | String  | 选项A |   否   |
|optionB   | String | 选项B |  否   |
|optionC   | String  | 选项C |   否   |
|optionD   | String  | 选项D |   否   |
|optionE   | String  | 选项E |   否   |
|optionF	| String  | 选项F |   否   |
|optionG   | String  | 选项G |   否   |
|questionId   | int  | 试题主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷选项删除：questionOption/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id   | int  | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷分类列表：paperType/list
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   否     |
|userId     | int  | 发布人Id  |   否     |
|curPage      | int | 当前第几页  |   否     |
|pageSize      | int  | 每页多少条，最大100条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | int  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].ID   | int  | 主键 |
|data.list[].NAME | String  | 名称 |
|data.list[].PW_STATE | int  | 1：开启；2：禁用 |
|data.list[].READ_USER_NAME | String  | 读权限 |
|data.list[].WRITE_USER_NAME | String  | 写权限 |

####试卷分类详情：paperType/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | int  | 主键 |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |
|data.id     | int  | 分类id |
|data.name      | String  | 分类名称 |
|data.img   | int  | 图片 |
|data.createUserId   | int  | 创建人 |
|data.createTime   | String  | 创建时间 |
|data.rwState   | int  | 读写状态【1：开启；2：禁用'】 |
|data.readUserIds   | int  | 读权限 |
|data.writeUserIds   | int  | 写权限 |

####试卷分类添加：paperType/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|imgId      | int  | 图片ID |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷分类修改：paperType/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | int | 主键 |   是     |
|name      | String (16)  | 名称   |   是     |
|imgId      | int  | 图片ID   |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷分类删除：paperType/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |

####试卷分类权限：paperType/auth
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | int     |   主键   |   是   |
|readUserIds    | String     |   读权限   |   否   |
|writeUserIds    | String |  写权限 |  否  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | int  | 响应码 |
|msg     | String  | 响应消息 |