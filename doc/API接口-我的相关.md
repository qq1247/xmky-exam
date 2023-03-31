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

## 我的考试相关
### 我的考试列表：myExam/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examName | String (32) | 考试名称   | 否   |
| startTime | Date | 考试开始时间   | 否   |
| endTime | Date | 考试结束时间  | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.total                  | Integer | 总行数 |
| data.list[]                 | arr[]   | 分页列表   |
| data.list[].examId        | Integer | 考试Id  |
| data.list[].examName        | String  | 考试名称  |
| data.list[].examStartTime   | Date    | 考试开始时间|
| data.list[].examEndTime     | Date    | 考试结束时间 |
| data.list[].examMarkStartTime   | Date    | 阅卷开始时间 |
| data.list[].examMarkEndTime     | Date    | 阅卷结束时间  |
| data.list[].examMarkState     | Date    | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；） |
| data.list[].examTotalScore | Double  | 考试总分  |
| data.list[].examPassScore | Double  | 考试及格分数  |
| data.list[].userId          | Date    | 考试用户ID |
| data.list[].userName        | Date    | 考试用户名称|
| data.list[].markUserId      | Integer | 阅卷人ID   |
| data.list[].markUserName    | String  | 阅卷人名称  |
| data.list[].answerStartTime | Date    | 答题开始时间|
| data.list[].answerEndTime   | Date    | 答题结束时间|
| data.list[].totalScore      | Double  | 我的得分 （考试成绩不显示返回null） |
| data.list[].state           | Integer | 考试状态（1：未考试；2：考试中；3：已交卷；） |
| data.list[].markState       | Integer | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；） |
| data.list[].answerState     | Integer | 答题状态（1：及格；2：不及格；）（考试成绩不显示返回null） |
| data.list[].no     | Integer | 排名（考试排名不显示返回null） |
| data.list[].userNum     | Integer | 用户数量 |

### 我的考试获取：myExam/get
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examId | Integer| 考试ID   | 是   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.examMarkState                  | Integer | 考试阅卷状态 |
| data.examScoreState                  | Integer | 考试分数状态 |
| data.examRankState                  | Integer | 考试排名状态 |
| data.examStartTime                  | Integer | 考试开始时间 |
| data.examEndTime                  | Integer | 考试结束时间 |
| data.examPaperName                  | Integer | 考试试卷名称 |
| data.answerStartTime                  | Integer | 按题开始时间 |
| data.answerEndTime                  | Integer | 按题结束时间 |
| data.markStartTime                  | Integer | 阅卷开始时间 |
| data.markEndTime                  | Integer | 阅卷结束时间 |
| data.objectiveScore                  | Integer | 客观题分数 |
| data.totalScore                  | Integer | 总分（成绩状态控制是否显示） |
| data.answerState                  | Integer | 答题状态（成绩状态控制是否显示） |
| data.state                  | Integer | 答题状态 |
| data.markState                  | Integer | 阅卷状态 |
| data.no                  | Integer | 排名（排名状态控制是否显示） |
| data.userNum                  | Integer | 用户数量 |

### 我的试卷：myExam/paper
| 请求参数| 类型    | 描述       | 必填 |
| ---- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |

| 响应数据| 类型    | 描述                       |
| --------------------- | ------- | -------------------------- |
| code                  | Integer | 响应码                     |
| msg                   | String  | 响应消息                   |
| data[].type		| Integer | 类型 （1：章节；2：试题）  |
| data[].chapterName    | String  | 章节名称  （type==1有效）     |
| data[].chapterTxt     | String  | 章节描述 （type==1有效）|
| data[].questionId     | Integer | 试题ID                 |
| data[].questionType   | Integer  | 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）|
| data[].markType       | Integer    | 阅卷方式（1：客观题；2：主观题；）  |
| data[].title          | String  | 题干                 |
| data[].markOptions    | Integer[]  | 阅卷选项（2：答案无顺序；3：不区分大小写；) |
| data[].score		| Double  | 试题分数                   |
| data[].analysis	| String  | 解析    |
| data[].userScore	| Double  | 用户分数    |
| data[].options	| String[]  | 单多选项    |
| data[].userAnswers	| String[]   | 用户答案    |
| data[].answers	| String[]  | 标准答案    |

### 我的答题：myExam/answer
| 请求参数| 类型    | 描述           | 必填 |
| -------------- | ------- | -------------- | ---- |
| examId | Integer | 考试ID | 是   |
| questionId     | Integer | 试题ID | 是   |
| answers | String[]| 答案（参考question/add）      | 是   |

### 我的交卷：myExam/finish
| 请求参数| 类型    | 描述       | 必填 |
| -------- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |

### 我的阅卷列表：myMark/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examName | String (32) | 考试名称   | 否   |
| startTime | Date | 阅卷开始时间   | 否   |
| endTime | Date | 阅卷结束时间  | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.total                  | Integer | 总行数 |
| data.list[]                 | arr[]   | 分页列表   |
| data.list[].examId        | Integer | 考试Id  |
| data.list[].examName        | String  | 考试名称  |
| data.list[].examMarkStartTime   | Date    | 阅卷开始时间 |
| data.list[].examMarkEndTime     | Date    | 阅卷结束时间  |
| data.list[].examMarkState     | Date    | 考试阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；） |

### 我的阅卷用户列表：myMark/userListpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examId | Integer | 考试ID   | 否   |
| userName | String | 用户名称   | 否   |
| state | Date | 考试用户状态（1：未考试；2：考试中；3：已交卷；）  | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.total                  | Integer | 总行数 |
| data.list[]                 | arr[]   | 分页列表   |
| data.list[].userId        | Integer | 用户ID  |
| data.list[].userName        | String  | 用户名称  |
| data.list[].orgId        | Integer | 机构ID  |
| data.list[].orgName        | String  | 机构名称  |
| data.list[].myExamState   | Date    | 我的考试状态（1：未考试；2：考试中；3：已交卷；）|
| data.list[].myExamMarkState     | Date    | 我的阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）  |

### 我的阅卷获取：myMark/get
| 请求参数| 类型    | 描述           | 必填 |
| -------------- | ------- | -------------- | ---- |
| examId| Integer| 考试ID| 是   |
| userId| Integer| 考试用户ID| 是   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.answerStartTime                  | Date | 答题开始时间 |
| data.answerEndTime                  | Date | 答题结束时间 |
| data.markStartTime                  | Date | 阅卷开始时间 |
| data.markEndTime                  | Date | 阅卷结束时间 |
| data.objectiveScore                  | Double | 客观题分数 |
| data.totalScore                  | Double | 总分数 |
| data.state                  | Integer | 用户考试状态 |
| data.markState                  | Integer | 用户阅卷状态 |
| data.answerState                  | Integer | 答题状态（1：及格；2：不及格） |

### 我的阅卷试卷：myMark/paper
| 请求参数| 类型    | 描述           | 必填 |
| -------------- | ------- | -------------- | ---- |
| examId| Integer| 考试ID| 是   |
| userId| Integer| 考试用户ID| 是   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
参考myExam/paper


### 我的阅卷打分：myMark/score
| 请求参数| 类型    | 描述           | 必填 |
| -------------- | ------- | -------------- | ---- |
| examId| Integer| 考试ID| 是   |
| userId| Integer| 考试用户ID| 是   |
| questionId| Integer| 试题ID| 是   |
| score          | Double  | 得分           | 是   |

### 我的阅卷阅卷：myMark/finish
| 请求参数| 类型    | 描述       | 必填 |
| -------- | ------- | ---------- | ---- |
| examId| Integer | 考试ID | 是   |
| userId| Integer | 考试用户ID | 是   |
