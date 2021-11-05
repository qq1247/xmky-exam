# 在线考试接口文档
## 默认值
Date样式：yyyy-MM-dd HH:mm:ss
curPage = 1
pageSize = 20
pageSize <= 100
code == 200 请求正常
code == 500 服务器内部错误
code == 401 无权限或登录超时
| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

## http请求头、响应头
http请求头需添加Authorization字段，
值为login/in的响应参数data.accessToken的值。
如果某次请求，http响应头有Authorization字段，请缓存该值，
之后的http请求头Authorization字段使用该缓存值，用于令牌续租。

## 接口命名规则
主业务/子业务。如：user/add

## 接口参数和返回值命名规则
不带前缀描述，默认表达主业务+字段的意思。如user/add?id=1，id表示用户ID=1
带前缀描述，表达前缀业务+字段的意思。如user/add?orgId=1，orgId表示机构ID=1

## 基础相关
### 登录进入：login/in
###### 
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|loginName      | String (16)  |  登陆名称  |  是  |
|pwd      | String (16)  |  密码  |  是  |

| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.accessToken     | String  | 访问令牌 |
|data.userId     | Integer  | 用户ID |
|data.userName     | String  | 用户名称 |
|data.roles | String[]  | 角色 |

### 登陆退出：login/out
### 登陆修改密码：login/pwd
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| oldPwd   | String   | 旧密码  | 是 |
| newPwd   | String   | 新密码  | 是 |

### 服务器时间：login/sysTime
| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data     | Date  | 服务器时间（前端每隔30秒同步一次，保证跟时间相关的业务功能正常） |

### 商标：login/logo
| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|     | Binary  | 二进制流 |

### 组织机构列表：org/listpage
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name| String(16) | 名称   |   否     |
| parentId| Integer | 父ID   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |

| 响应参数  |  类型   |  描述  |
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

### 组织机构添加：org/add
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|parentId      | Integer  | 父ID   |   否     |
|no    | Integer     |   排序   |   是   |

### 组织机构修改：org/edit
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id| Integer  | 主键   |   是     |
|name      | String (16)  | 名称   |   是     |
|parentId      | Integer  | 父ID   |   否     |
|no    | Integer     |   排序   |   是   |

### 组织机构删除：org/del
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 组织机构获取：org/get
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.id | Integer  | 主键 |
|data.name     | String  | 名称 |
|data.parentId     | Integer  | 父ID |
|data.parentName   | String  | 父名称 |
|data.no   | Integer  | 排序 |

### 组织机构移动：暂未开放
### 组织机构模板：暂未开放
### 组织机构导入：暂未开放
### 组织机构导出：暂未开放

### 用户列表：user/listpage
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name | String(16) | 名称   |   否     |
| orgName| String(16) | 组织机构名称   |   否     |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |

| 响应参数 |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name | String  | 名称 |
|data.list[].loginName | String  | 登陆名称 |
|data.list[].orgId | Integer  | 组织机构ID |
|data.list[].orgName | String  | 组织机构名称 |
|data.list[].registTime | Date | 注册时间（admin可见） |
|data.list[].lastLoginTime | Date  | 最后登陆时间（admin可见） |
|data.list[].roles[] | String  | 角色（admin可见） |
|data.list[].online| Boolean  | 是否在线（admin可见）|
|data.list[].onlineTime| Date |最后在线时间（admin可见） |

### 用户添加：user/add
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|loginName      | String (16)  | 登陆账号   |   是     |
|orgId      | Integer  | 组织机构ID   |   是     |

| 响应参数 |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.initPwd | String  | 初始化密码 |

### 用户修改：user/edit
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|loginName      | String (16)  | 登陆账号   |   是     |
|orgId      | Integer  | 组织机构ID   |   是     |

| 响应参数 |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.initPwd | String  | 初始化密码（更改loginName，此字段有效） |

### 用户删除：user/del
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 用户获取：user/get
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.id| Integer  | 主键 |
|data.name     | String  | 名称 |
|data.loginName     | String  | 登陆账号 |
|data.registTime     | Date  | 注册时间（admin可见） |
|data.lastLoginTime     | Date  | 最后登陆时间（admin可见） |
|data.orgId     | Integer  | 组织机构ID |
|data.orgName   | String  | 组织机构名称 |
|data.roles | String[]  | 角色（admin可见） |
|data.state   | Integer  | 状态（0：删除；1：正常；） |

### 用户密码初始化：user/pwdInit
| 请求参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.initPwd| String  | 初始化密码|

### 用户更新角色：user/role
| 请求参数 |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|roles    | String[]     |   角色  |   是   |

### 用户强制退出：user/out
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 用户导入模板：暂未提供
### 用户导入：暂未提供
### 用户导出：暂未提供

### 数据字典列表：dict/listpage
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|dictIndex      | String  | 索引   |  否     |
|dictKey      | String  | 键   |   否     |
|dictValue    | String     | 值 |   否   |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |

| 响应参数|  类型   |  描述  |
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

### 数据字典添加：dict/add
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|dictIndex      | String (32)  | 索引   |   是     |
|dictKey      | String (32)  | 键   |   是     |
|dictValue      | String (32)  | 值  |   是     |
|no    | Integer     |   排序   |   是   |

### 数据字典修改：dict/edit
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer  | 主键   |   是     |
|dictIndex      | String (32)  | 索引   |   是     |
|dictKey      | String (32)  | 键   |   是     |
|dictValue      | String (32)  | 值  |   是     |
|no    | Integer     |   排序   |   是   |

### 数据字典删除：dict/del
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 定时任务列表：cron/listpage
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name  | String  | 名称   |   是     |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |

| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name | String  | 名称 |
|data.list[].jobClass | String  | 实现类 |
|data.list[].cron | String  | cron表达式 |
|data.list[].state | Integer | 状态 |
|data.list[].stateName | String  | 状态名称 |
|data.list[].triggerTimes | Date[]  | 最近几次触发时间  |

### 定时任务添加：cron/add
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|name      | String (32)  | 名称   |   是     |
|jobClass      | String (32)  | 实现类   |   是     |
|cron      | String (32)  | cron表达式 |   是     |

### 定时任务修改：cron/edit
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer  | 主键   |   是     |
|name      | String (32)  | 名称   |   是     |
|jobClass      | String (32)  | 实现类   |   是     |
|cron      | String (32)  | cron表达式 |   是     |

### 定时任务删除：cron/del
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 定时任务获取：cron/get
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

| 响应参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.id| Integer  | 主键 |
|data.name     | String  | 名称 |
|data.jobClass | String  | 实现类 |triggerTimes
|data.cron | String  | cron表达式|
|data.triggerTimes | Date[]| 最近几次触发时间|

### 定时任务启动：cron/startTask
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 定时任务停止：cron/stopTask
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 定时任务执行一次：cron/runOnceTask
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |

### 附件上传：file/upload
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|files    | File     |   附件  |   是   |
|uuid| String|   通用唯一识别码。查阅file/getId接口了解详细  |   否   |

| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String[]  | 附件ID |

### 附件下载：file/download
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer   | 主键 |   是   |

| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|	| Binary| 二进制流|

### 附件获取ID：file/getId
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|uuid| String| 移动端在浏览器上扫码传图时，二维码携带自定义uuid，移动端上传图片后， 浏览器通过该uuid反向获取上传的附件ID，浏览器建议查询间隔为1秒一次 |   是   |

| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | 附件ID |

### 进度条获取：progressBar/get
###### 建议一秒查询一次
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| id   | String   | 进度条ID  | 是 |

| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.curNum    | Integer   | 当前数量 |
|data.totalNum    | Integer   | 总数量 |
|data.percent| Integer   | 百分比 |

### 公告栏列表：bulletin/listpage
| 求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
| name  | String  | 名称   |   是     |
| curPage | Integer | 当前第几页  |   否     |
| pageSize | Integer  | 每页多少条   |   否     |

| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].title | String  | 标题 |
|data.list[].content | String  | 内容 |
|data.list[].showType      | Integer  | 展示状态 |
|data.list[].showTypeName      | String  | 展示状态名称 |
|data.list[].imgFileId     | String  | 图片附件ID |
|data.list[].updateTime | String  | 更新时间 |
|data.list[].updateUserName | String  | 更新用户名称 |
|data.list[].readUserNames | String  | 用户读权限 |

### 公告栏添加：bulletin/add
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|title     | String (32)  | 标题   |   是     |
|content      | text  | 内容   |   是     |
|showType      | Integer  | 展示状态（1：正常；2：置顶；3：轮播；）   |   是     |
|imgFileId     | String (256)  | 轮播图附件ID   |  否     |
|readUserIds      | String (256)  | 用户读权限（不填表示所有人都可以读取）  |   否     |

### 公告栏修改：bulletin/edit
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id| Integer    | 主键   |   是     |
|title     | String (32)  | 标题   |   是     |
|content      | text  | 内容   |   是     |
|showType      | Integer  | 展示状态（1：正常；2：置顶；3：轮播；）   |   是     |
|imgFileId     | String (256)  | 轮播图附件ID   |  否     |
|readUserIds      | String (256)  | 用户读权限（不填表示所有人都可以读取）  |   否     |

### 公告栏删除：bulletin/del
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |


### 系统参数邮件：parm/email
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|host| String（64）| 主机   |   是     |
|userName| String (64)  | 用户名   |   是     |
|pwd| text  | String（64）   |   密码     |
|protocol| String（16）| 协议  |   是     |
|encode| String (16)  | 编码   |  是   |

### 系统参数上传附件目录：parm/file
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|uploadDir| String（64）| 上传目录   |   是    |

### 系统参数数据库备份目录：parm/db
| 请求参数|  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|bakDir| String（64）| 上传目录   |   是    |
