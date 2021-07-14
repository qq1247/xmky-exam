#在线考试接口文档
######默认值
Date样式：yyyy-MM-dd HH:mm:ss
curPage = 1
pageSize = 20
pageSize <= 100
code == 200 请求正常
code == 500 服务器内部错误
code == 401 无权限或登录超时

######默认响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

######http请求头、响应头
除login/*接口外，http请求头需添加Authorization字段，
值为login/in的响应数据data.accessToken的值。
如果某次请求，http响应头有Authorization字段，请缓存该值，
之后的http请求头Authorization字段使用该缓存值，用于令牌续租。

##基础相关
####登录进入：login/in
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|loginName      | String (16)  |  登陆名称  |  是  |
|pwd      | String (16)  |  密码  |  是  |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.accessToken     | String  | 访问令牌 |
|data.userId     | name  | 用户ID |
|data.userName     | name  | 用户名称 |
|data.roles | String[]  | 角色 |

####登陆退出：login/out
######请求参数
无

####登陆修改密码：login/updatePwd
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| oldPwd   | String   | 旧密码  | 是 |
| newPwd   | String   | 新密码  | 是 |

####系统时间：login/sysTime
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data     | Date  | 系统时间 |

####组织机构列表：org/listpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name| String(16) | 名称   |   否     |
| parentId| Integer | 父ID   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name | String  | 名称 |
|data.list[].parentId | Integer  | 父ID |
|data.list[].parentName | String  | 父名称 |
|data.list[].no | Integer  | 排序 |

####组织机构添加：org/add
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|parentId      | Integer  | 父ID   |   否     |
|no    | Integer     |   排序   |   是   |

####组织机构修改：org/edit
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|no    | Integer     |   排序   |   是   |

####组织机构删除：org/del
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

####组织机构获取：org/get
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.name     | String  | 名称 |
|data.parentId     | Integer  | 父ID |
|data.parentName   | String  | 父名称 |
|data.no   | Integer  | 排序 |

####组织机构模板：org/template
######请求参数
无
######响应数据
字节流

####组织机构导入：org/import
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|file    | file     |   附件   |   是   |

####组织机构导出：org/export
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|ids    | String     |   逗号分隔的ids   |   否   |

####用户列表：user/listpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name | String(16) | 名称   |   否     |
| orgName| String(16) | 组织机构名称   |   否     |
| parentOrgId| String(16) | 父组织机构ID   |   否     |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name | String  | 名称 |
|data.list[].loginName | String  | 登陆名称 |
|data.list[].phone | String  | 手机号 |
|data.list[].email | String  | 邮箱 |
|data.list[].orgName | String  | 组织机构名称 |
|data.list[].registTime | Date | 注册时间 |
|data.list[].lastLoginTime | Date  | 登陆时间 |
|data.list[].roles | String  | 角色 |

####用户添加：user/add
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 昵称   |   是     |
|loginName      | String (16)  | 登陆账号   |   否     |
|phone      | String (11)  | 手机号   |   否     |
|email      | String (64)  | 邮箱   |   是     |
|orgId      | Integer  | 组织机构ID   |   是     |

####用户修改：user/edit
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|loginName      | String (16)  | 登陆账号   |   是     |
|phone      | String (11)  | 手机号   |   否     |
|email      | String (64)  | 邮箱   |   是     |
|no    | Integer     |   排序   |   是   |

####用户删除：user/del
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

####用户获取：user/get
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.name     | String  | 名称 |
|data.loginName     | String  | 登陆账号 |
|data.phone | String (11)  | 手机号   |  
|data.email | String (64)  | 邮箱   |   
|data.registTime     | Date  | 注册时间 |
|data.lastLoginTime     | Date  | 最后登陆时间 |
|data.orgId     | Integer  | 组织机构ID |
|data.orgName   | String  | 组织机构名称 |
|data.state   | Integer  | 0：删除；1：正常；2：冻结； |

####用户更新角色：user/updateRoles
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|roles    | String[]     |   角色  |   是   |

####用户更新机构：user/updateOrg
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|orgId    | Integer     |   组织机构ID   |   是   |

####用户模板：user/template
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|    |      |      |      ||
######响应数据
字节流

####用户导入：user/import
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|file    | file     |   附件   |   是   |

####用户导出：user/export
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|ids    | String     |   逗号分隔的ids   |   否   |

####数据字典列表：dict/listpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|dictIndex      | String  | 索引   |  否     |
|dictKey      | String  | 键   |   否     |
|dictValue    | String     | 值 |   否   |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].dictIndex | String  | 索引 |
|data.list[].dictKey | String  | 键 |
|data.list[].dictValue | String  | 值 |
|data.list[].no | Integer  | 排序 |

####数据字典添加：dict/add
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|dictIndex      | String (32)  | 索引   |   是     |
|dictKey      | String (32)  | 键   |   是     |
|dictValue      | String (32)  | 值  |   是     |
|no    | Integer     |   排序   |   是   |

####数据字典修改：dict/edit
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer  | 主键   |   是     |
|dictIndex      | String (32)  | 索引   |   是     |
|dictKey      | String (32)  | 键   |   是     |
|dictValue      | String (32)  | 值  |   是     |
|no    | Integer     |   排序   |   是   |

####数据字典删除：dict/del
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

####定时任务列表：cron/listpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name  | String  | 名称   |   是     |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name | String  | 名称 |
|data.list[].jobClass | String  | 实现类 |
|data.list[].cron | String  | cron表达式 |
|data.list[].state | String  | 状态 |
|data.list[].stateName | String  | 状态名称 |
|data.list[].recentTriggerTime | String  | 最近三次运行时间  |

####定时任务添加：cron/add
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (32)  | 名称   |   是     |
|jobClass      | String (32)  | 实现类   |   是     |
|cron      | String (32)  | cron表达式 |   是     |

####定时任务修改：cron/edit
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer  | 主键   |   是     |
|name      | String (32)  | 名称   |   是     |
|jobClass      | String (32)  | 实现类   |   是     |
|cron      | String (32)  | cron表达式 |   是     |
|no    | Integer     |   排序   |   是   |

####定时任务删除：cron/del
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

####附件上传：file/upload
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|files    | file     |   附件  |   是   |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | 附件IDS |

####附件下载：file/download
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer   | 主键 |   是   |
######响应数据
字节流

####系统参数修改：pram/edit
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer   | 主键 |   否   |
|emailHost    | String   | 邮箱主机 |   是   |
|emailUserName    | 邮件用户名   | 主键 |   是   |
|emailpwd    | String   | 邮件密码 |   是   |
|emailProtocol    | String   | 邮件协议 |   是   |
|emailEncode    | String   | 邮件编码 |   是   |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | 附件IDS |

####系统参数获取：pram/get
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.emailHost    | String   | 邮箱主机 |
|data.emailUserName    | String   | 邮件用户名 |
|data.emailpwd    | String   | 邮件密码 |
|data.emailProtocol    | String   | 邮件协议 |
|data.emailEncode    | String   | 邮件编码 |

####进度条获取：progressBar/get
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| id   | String   | 进度条ID  | 是 |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.curNum    | Integer   | 当前数量 |
|data.totalNum    | Integer   | 总数量 |
|data.msg    | String   | 消息 |

####公告栏列表：bulletinBoard/listpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name  | String  | 名称   |   是     |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].title | String  | 标题 |
|data.list[].imgId | String  | 图片ID |
|data.list[].videoId | String  | 视频ID |
|data.list[].content | String  | 内容 |
|data.list[].imgHeight | Integer | 图片高 |
|data.list[].imgWidth | Integer | 图片宽 |
|data.list[].url | Integer | 跳转链接 |
|data.list[].topState | Integer  | 置顶状态 |
|data.list[].topStateName | String  | 置顶状态名称 |
|data.list[].no | Integer | 排序 |
|data.list[].updateTime | String  | 更新时间 |
|data.list[].updateUserName | String  | 更新用户名称 |
|data.list[].readUserNames | String  | 用户读权限 |
|data.list[].readOrgNames | String  | 机构读权限 |

####公告栏添加：bulletinBoard/add
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|title     | String (32)  | 标题   |   是     |
|imgId     | String (256)  | 图片ID   |  否     |
|videoId     | String (256)  | 视频ID   |   否     |
|content      | text  | 内容   |   否     |
|imgHeight      | Integer  | 图片高   |   否     |
|imgWidth      | Integer  | 图片宽  |   否     |
|url      | String (128)  | 跳转链接   |   否     |
|topState      | Integer  | 置顶状态(1：是；2：否)   |   是     |
|no      | Integer  | 排序  |   否     |
|readUserIds      | String (256)  | 用户读权限  |   否     |
|readOrgIds      | String (256)  | 机构读权限  |   否     |

####公告栏修改：bulletinBoard/edit
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer  | 主键   |   是     |
其他参数同

####公告栏删除：bulletinBoard/del
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |