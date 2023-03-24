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
| examId | Integer | 考试ID（查分数用）   | 否   |
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
| data.list[].examMarkStartTime   | Date    | （考试的）阅卷开始时间 |
| data.list[].examMarkEndTime     | Date    | （考试的）阅卷结束时间  |
| data.list[].examMarkState     | Date    | （考试的 )阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；） |
| data.list[].paperId         | Double  | 试卷ID  |
| data.list[].paperPassScore | Double  | 试卷及格分数  |
| data.list[].paperTotalScore | Double  | 试卷总分  |
| data.list[].paperShowType | String  | 试卷展示类型  |
| data.list[].userId          | Date    | 考试用户ID |
| data.list[].userName        | Date    | 考试用户名称|
| data.list[].answerStartTime | Date    | 答题开始时间|
| data.list[].answerEndTime   | Date    | 答题结束时间|
| data.list[].markUserId      | Integer | 阅卷人ID   |
| data.list[].markUserName    | String  | 阅卷人名称  |
| data.list[].totalScore      | Double  | 我的得分 （考试不显示成绩返回null） |
| data.list[].state           | Integer | 考试状态（1：未考试；2：考试中；3：已交卷；4：强制交卷；） |
| data.list[].markState       | Integer | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；） |
| data.list[].answerState     | Integer | 答题状态（1：及格；2：不及格） |
| data.list[].no     | Integer | 排名（考试不显示排名返回null） |


### 我的试卷：myExam/paper
| 请求参数| 类型    | 描述       | 必填 |
| ---- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |

| 响应数据| 类型    | 描述                       |
| --------------------- | ------- | -------------------------- |
| code                  | Integer | 响应码                     |
| msg                   | String  | 响应消息                   |
| data[].type		| Integer | 类型类型 （1：章节；2：试题）  |
| data[].chapterName    | String  | 章节名称  （type==1有效）     |
| data[].chapterTxt     | String  | 章节描述 （type==1有效）|
| data[].questionId     | Integer | 试题ID                 |
| data[].questionType   | Integer  | 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）                 |
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

### 我的考试交卷：myExam/finish
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
###### 响应数据
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
| data.list[].examMarkState     | Date    | 考试阅卷状态（考试的总阅卷状态） |
| data.list[].paperId         | Double  | 试卷ID  |
| data.list[].paperPassScore | Double  | 试卷通过  |
| data.list[].paperTotalScore | Double  | 试卷总分  |
| data.list[].paperShowType | String  | 试卷展示类型  |
| data.list[].markUserId      | Integer | 阅卷人ID   |
| data.list[].markUserName    | String  | 阅卷人名称  |

### 我的阅卷阅题：myMark/score
| 请求参数| 类型    | 描述           | 必填 |
| -------------- | ------- | -------------- | ---- |
| examId| Integer| 考试ID| 是   |
| userId| Integer| 考试用户ID| 是   |
| questionId| Integer| 试题ID| 是   |
| score          | Double  | 得分           | 是   |

### 我的阅卷交卷：myMark/finish
| 请求参数| 类型    | 描述       | 必填 |
| -------- | ------- | ---------- | ---- |
| examId| Integer | 考试ID | 是   |
| userId| Integer | 考试用户ID | 是   |

### 我的阅卷用户列表：myMark/userList
| 请求参数| 类型    | 描述       | 必填 |
| -------- | ------- | ---------- | ---- |
| examId| Integer | 考试ID | 是   |
###### 响应数据
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data                 | arr[]   | 数组列表   |
| data[].userId        | Integer | 用户ID  |
| data[].userName        | String  | 用户名称（匿名阅卷时不显示）  |
| data[].userHeadFileId        | Integer  | 用户头像（匿名阅卷时不显示）  |
| data[].orgId   | Integer    | 机构ID|
| data[].orgName     | String    | 机构名称（匿名阅卷时不显示） |
| data[].answerStartTime   | Date    | 答题开始时间 |
| data[].answerEndTime     | Date    | 答题完成时间  |
| data[].markStartTime   | Date    | 阅卷用户开始阅卷时间 |
| data[].markEndTime     | Date    | 阅卷用户最后阅卷时间  |
| data[].state       | Integer | 我的考试状态（（1：未考试；2：考试中；3：已交卷；4：强制交卷；））  |
| data[].markState       | Integer | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）  |
| data[].answerState       | Integer | 答题状态（1：及格；2：不及格） （成绩不公开时不显示） |
| data[].totalScore | Double  | 个人总分数（成绩不公开时不显示）  |
| data[].paperTotalScore | Double  | 试卷总分数  |
| data[].paperPassScore | Double  | 试卷通过分数  |

### 我的阅卷用户：myMark/user
| 请求参数| 类型    | 描述       | 必填 |
| -------- | ------- | ---------- | ---- |
| examId| Integer | 考试ID | 是   |
| userId| Integer | 用户ID | 是   |
###### 响应数据
| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.userId        | Integer | 用户ID  |
| data.userName        | String  | 用户名称（匿名阅卷时不显示）  |
| data.userHeadFileId        | Integer  | 用户头像（匿名阅卷时不显示）  |
| data.orgId   | Integer    | 机构ID |
| data.orgName     | String    | 机构名称（匿名阅卷时不显示） |
| data.answerStartTime   | Date    | 答题开始时间 |
| data.answerEndTime     | Date    | 答题完成时间  |
| data.markStartTime   | Date    | 阅卷用户开始阅卷时间 |
| data.markEndTime     | Date    | 阅卷用户最后阅卷时间  |
| data.state       | Integer | 我的考试状态（（1：未考试；2：考试中；3：已交卷；4：强制交卷；））  |
| data.markState       | Integer | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）  |
| data.answerState       | Integer | 答题状态（1：及格；2：不及格） （成绩不公开时不显示） |
| data.totalScore | Double  | 个人总分数 （成绩不公开时不显示） |
| data.paperTotalScore | Double  | 试卷总分数  |
| data.paperPassScore | Double  | 试卷通过分数  |

### 我的考试答案列表：myMark/answerList
| 请求参数| 类型    | 描述       | 必填 |
| ---- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |
| userId | Integer | 用户ID | 是   |

| 响应数据| 类型    | 描述                       |
| --------------------- | ------- | -------------------------- |
| code                  | Integer | 响应码                     |
| msg                   | String  | 响应消息                   |
| data[].questionId     | Integer | 试题ID                     |
| data[].answerTime     | Date    | 答题时间                   |
| data[].answers        | String[]| 我的答案，参考question/add |
| data[].markUserId     | Integer | 阅卷人ID                   |
| data[].markUserName   | String  | 阅卷人名称                 |
| data[].markTime       | Date    | 阅卷时间                   |
| data[].score          | Double  | 得分                       |
| data[].questionScore  | Double  | 试卷分数                   |
| data[].answerFileId  | Integer  | 答案附件ID    |