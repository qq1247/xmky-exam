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
### 考试用户首页：report/user/home
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.examNum       | Integer | 考试数量 |
| data.exerNum       | String | 练习数量  |
| data.passExamNum        | String | 及格次数  |
| data.topRank       | Integer | 最高排名 |

### 管理员首页：report/admin/home
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.examNum        | Integer | 考试数量 |
| data.questionNum        | Integer | 试题数量  |
| data.exerNum  | Integer | 练习数量 |
| data.userNum     | Integer | 用户数量 |

### 子管理员首页：report/subAdmin/home
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.examNum        | Integer | 考试数量 |
| data.questionNum        | Integer | 试题数量  |
| data.exerNum  | Integer | 练习数量 |
| data.userNum     | Integer | 用户数量 |

### 阅卷用户首页：report/markUser/home
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.unMarkNum       | Integer | 总考试场数 |
| data.examNum        | Integer | 试题数量  |

### 试题统计：report/question/statis
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| questionTypeId | Integer | 试题分类ID   | 是   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.typeList[]               | Object[]   | 类型列表   |
| data.typeList[].name        | String | 试题类型名称  |
| data.typeList[].value       | Integer  | 试题类型值  |
| data.markTypeList[]               | Object[]   | 阅卷类型   |
| data.markTypeList[].name        | String | 阅卷类型名称  |
| data.markTypeList[].value       | Integer  | 阅卷类型值  |

### 考试统计：report/exam/statis
| 请求参数| 类型    | 描述       | 必填 |
| ---- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |

| 响应数据| 类型    | 描述                       |
| --------------------- | ------- | -------------------------- |
| code                  | Integer | 响应码                     |
| msg                   | String  | 响应消息                   |
| data.userNum       | Integer    | 考试人数                   |
| data.missUserNum   | Integer  | 未考试人数                      |
| data.failUserNum  | Integer  | 不及格人数|
| data.questionNum  | Integer  | 试题数量    |
| data.objectiveQuestionNum  | Integer  | 客观题数量    |
| data.avgScore | Double  | 平均分   |
| data.minScore | Double  | 最低分   |
| data.maxScore | Double  |最高分  |
| data.sdScore | Double  | 标准差 |
| data.questionTypeList[]             | Object[]   | 试题类型占比列表   |
| data.questionTypeList[].name        | String | 试题类型名称  |
| data.questionTypeList[].value       | String  | 试题类型值  |
| data.scoreGradeList[]               | Object[]   | 分数段占比列表   |
| data.scoreGradeList[].name        | String | 分数段名称  |
| data.scoreGradeList[].value       | String  | 分数段值  |

### 用户排名列表：report/exam/rankListpage
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
| data.list[].myExamNo        | Integer | 用户排序  |
| data.list[].userId        | Integer | 考试用户Id |
| data.list[].userName        | String | 考试用户名称  |
| data.list[].orgName        | String | 组织机构名称  |
| data.list[].myExamState        | Integer | 用户考试状态（1：未考试；2：考试中；3：已交卷；）  |
| data.list[].myExamMarkState        | Integer | 用户阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）  |
| data.list[].myExamAnswerState        | Integer | 用户答题状态（1：及格；2：不及格）  |
| data.list[].myExamStartTime        | Date | 用户答题开始时间  |
| data.list[].myExamEndTime       | Date | 用户答题结束时间  |
| data.list[].myExamMarkStartTime        | Date | 阅卷开始时间  |
| data.list[].myExamMarkEndTime       | Date | 阅卷结束时间  |
| data.list[].myExamTotalScore        | Double | 我的考试分数  |
