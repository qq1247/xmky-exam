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
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应数据| 类型    | 描述 |
| --------------------------- | ------- | ----- |
| code                        | Integer | 响应码   |
| msg                         | String  | 响应消息  |
| data.total                  | Integer | 总行数 |
| data.list[]                 | arr[]   | 分页列表   |
| data.list[].examName        | String  | 考试名称  |
| data.list[].examStartTime   | Date    | 考试开始时间|
| data.list[].examEndTime     | Date    | 考试结束时间 |
| data.list[].userId          | Date    | 考试用户ID |
| data.list[].userName        | Date    | 考试用户名称|
| data.list[].answerStartTime | Date    | 答题开始时间|
| data.list[].answerEndTime   | Date    | 答题结束时间|
| data.list[].markUserId      | Integer | 阅卷人ID   |
| data.list[].markUserName    | String  | 阅卷人名称  |
| data.list[].markStartTime   | Date    | 阅卷开始时间 |
| data.list[].markEndTime     | Date    | 阅卷结束时间  |
| data.list[].paperId         | Double  | 试卷ID  |
| data.list[].paperTotalScore | Double  | 试卷总分  |
| data.list[].totalScore      | Double  | 我的得分  |
| data.list[].state           | Integer | 考试状态（1：未考试；2：考试中；3：已交卷；4：强制交卷；） |
| data.list[].stateName       | String  | 考试状态名称   |
| data.list[].markState       | Integer | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；） |
| data.list[].markStateName   | String  | 阅卷状态名称   |
| data.list[].answerState     | Integer | 答题状态（1：及格；2：不及格） |
| data.list[].answerStateName | String  | 答题状态名称  |

### 我的考试答案列表：myExam/answerList
| 请求参数| 类型    | 描述       | 必填 |
| ---- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |

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

### 我的考试更新答题：myExam/answer
| 请求参数| 类型    | 描述           | 必填 |
| -------------- | ------- | -------------- | ---- |
| examId | Integer | 考试ID | 是   |
| questionId     | Integer | 试题ID | 是   |
| answers | String[]| 答案（参考question/add）      | 是   |
| fileId | Integer | 附件ID  | 否   |

### 我的考试交卷：myExam/finish
| 请求参数| 类型    | 描述       | 必填 |
| -------- | ------- | ---------- | ---- |
| examId | Integer | 考试ID | 是   |

### 我的阅卷考试列表：myMark/examListpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examName | String (32) | 考试名称   | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应数据| 类型    | 描述         |
| --------------------------- | ------- | ------------ |
| code                        | Integer | 响应码       |
| msg                         | String  | 响应消息     |
| data.total                  | Integer | 总行数       |
| data.list[]                 | arr[]   | 分页列表     |
| data.list[].id              | Integer | 主键         |
| data.list[].examName        | String  | 考试名称     |
| data.list[].examStartTime   | Date    | 考试开始时间 |
| data.list[].examEndTime     | Date    | 考试结束时间 |
| data.list[].paperPassScore  | Double  | 试卷及格分数 |
| data.list[].paperTotalScore | Date    | 试卷总分     |
| data.list[].userNum         | String  | 考试人数     |

### 我的阅卷列表：myMark/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| examId | Integer     | 考试ID   | 否   |
| examName | String (32) | 考试名称   | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |
###### 响应数据
同myExam/listpage

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





