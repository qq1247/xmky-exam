# 统计接口文档
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

## 统计相关
### 试题统计：report/question/statis
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| questionTypeId | Integer | 试题分类ID   | 是   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.typeList[]               | Object[]   | 类型列表   |
| data.typeList[].name        | String | 类型名称  |
| data.typeList[].value       | Integer  | 类型值  |
| data.difficultyList[]               | Object[]   | 难度列表   |
| data.difficultyList[].name        | String | 难度名称  |
| data.difficultyList[].value       | Integer  | 难度值  |
| data.aiList[]               | Object[]   | 智能列表   |
| data.aiList[].name        | String | 智能名称  |
| data.aiList[].value       | Integer  | 智能值  |

### 考试统计：report/exam/statis
| 请求参数| 类型    | 描述       | 必填 |
| ---- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |

| 响应数据| 类型    | 描述                       |
| --------------------- | ------- | -------------------------- |
| code                  | Integer | 响应码                     |
| msg                   | String  | 响应消息                   |
| data.exam.name     | String |  考试名称       |
| data.exam.startTime     | Date    | 考试开始时间           |
| data.exam.endTime        | Date   | 考试结束时间           |
| data.exam.markStartTime    | Date | 阅卷开始时间               |
| data.exam.markEndTime   | Date   | 阅卷结束时间                 |
| data.exam.userNum       | Integer    | 考试用户数量                   |
| data.exam.missUserNum   | Integer  | 缺考用户数量                       |
| data.exam.succUserNum  | Integer  | 成绩合格用户数量       |
| data.myExam.answerStartTime  | Date  | 答题开始时间（用于统计最短交卷时间）暂不使用      |
| data.myExam.answerEndTime  | Date  | 答题结束时间  暂不使用   |
| data.score.total  | Double  | 总分数  |
| data.score.avg | Double  | 平均分   |
| data.score.min  | Double  | 最低分   |
| data.score.max | Double  |最高分  |
| data.score.sd | Double  | 标准差 |
| data.typeList[]               | Object[]   | 类型列表   |
| data.typeList[].name        | String | 类型名称  |
| data.typeList[].value       | String  | 类型值  |
| data.scoreGradeList[]               | Object[]   | 分数段列表   |
| data.scoreGradeList[].name        | String | 分数段名称  |
| data.scoreGradeList[].value       | String  | 分数段值  |

### 用户考试列表：report/myExam/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |
| questionId | Integer | 试题ID | 否 |
| state | Integer | 是否正确【1：正确；2：错误 试题ID不为空时有效】 | 否 |
| curPage   | Integer | 当前第几页 | 否   |
| pageSize  | Integer | 每页多少条 | 否   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.total                | Integer | 总行数  |
| data.list[]               | Object[]   | 分页列表|
| data.list[].myExamNo        | Integer | 考试排序  |
| data.list[].userId        | Integer | 考试用户Id |
| data.list[].userName        | String | 考试用户名称  |
| data.list[].orgId        | Integer | 组织机构Id |
| data.list[].orgName        | String | 组织机构名称  |
| data.list[].myExamState        | Integer | 考试状态  |
| data.list[].myExamMarkState        | Integer | 阅卷状态  |
| data.list[].myExamAnswerState        | Integer | 答题状态  |
| data.list[].paperTotalScore        | Double | 试卷总分数 |
| data.list[].paperPassScore        | Double | 试卷及格分数  |
| data.list[].myExamScore        | Double | 我的考试分数  |
| data.list[].myExamStartTime        | Date | 我的考试开始时间  |
| data.list[].myExamEndTime       | Date | 我的考试结束时间  |
| data.list[].myExamMarkStartTime        | Date | 我的阅卷开始时间  |
| data.list[].myExamMarkEndTime       | Date | 我的阅卷结束时间  |
| data.list[].myMarkUserId        | Integer | 阅卷用户Id  |
| data.list[].myMarkUserName       | String | 阅卷用户名称 |

### 试题列表：report/question/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |
| curPage   | Integer | 当前第几页 | 否   |
| pageSize  | Integer | 每页多少条 | 否   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.total                | Integer | 总行数  |
| data.list[]               | Object[]   | 分页列表|
| data.list[].questionId        | Integer | 试题ID  |
| data.list[].questionTitle        | Integer | 试题标题 |
| data.list[].userNum        | Integer | 考试用户数量 |
| data.list[].succUserNum        | Integer | 答对用户数量 |

### 首页统计（普通用户）：report/home/user
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.user.id        | Integer | 考试用户Id |
| data.user.name        | String | 考试用户名称  |
| data.user.headFileId        | String | 考试用户头像  |
| data.org.id        | Integer | 组织机构Id |
| data.org.name        | String | 组织机构名称  |
| data.user.type        | Integer | 用户类型 |
| data.exam.num       | Integer    | 考试次数     |
| data.exam.missNum   | Integer  | 缺考次数     |
| data.exam.succNum  | Integer  | 通过次数    |
| data.exam.top      | Double | 最高排名 |
| data.score.avg | Double  | 平均分   |
| data.score.min  | Double  | 最低分   |
| data.score.max | Double  |最高分  |
| data.score.sd | Double  | 标准差 |

### 首页统计(子管理)：report/home/subAdmin
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.user.id        | Integer | 用户Id |
| data.user.name        | String | 用户名称  |
| data.user.headFileId  | String | 用户头像  |
| data.org.id        | Integer | 组织机构Id |
| data.org.name        | String | 组织机构名称  |
| data.user.type        | Integer | 用户类型 |
| data.exam.num       | Integer    | 创建考试数量     |
| data.paper.num   | Integer  | 创建试卷数量     |
| data.question.num  | Integer  | 创建试题数量    |
| data.myMark.num      | Integer | 待阅试卷 |

### 首页统计(超级管理员)：report/home/admin
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                    | Integer | 响应码    |
| msg                     | String  | 响应消息  |
| data.user.num            | Integer | 创建用户数   |
| data.subAdmin.num        | Integer | 创建子管理数 |
| data.bulletin.num        | Integer | 创建公告数   |
| data.onlineUser.num          | Integer | 在线用户     |

### 服务器参数：report/server/parm
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | -----  |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息 |
| data[]                  | Object[]   | 对象列表     |
| data[].name               | String | 名称  |
| data[].value             | String | 值 （部分字段为数组，如c盘d盘） |

### 慢接口日志：report/server/log
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data                 | String[]   | 字符串列表     |