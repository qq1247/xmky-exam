# 在线考试接口文档

## 默认值

Date样式：yyyy-MM-dd HH:mm:ss
curPage = 1
pageSize = 20
pageSize &lt;= 100
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

## 考试相关

### 试题分类列表：questionType/listpage

| 请求参数     | 类型          | 描述    | 必填  |
| -------- | ----------- | ----- | --- |
| id       | Integer     | 主键    | 否   |
| name     | String (16) | 名称    | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应参数                    | 类型       | 描述   |
| ----------------------- | -------- | ---- |
| code                    | Integer  | 响应码  |
| msg                     | String   | 响应消息 |
| data.total              | Integer  | 总行数  |
| data.list[]             | Object[] | 分页列表 |
| data.list[].id          | Integer  | 主键   |
| data.list[].name        | String   | 名称   |
| data.list[].questionNum | String   | 试题数量 |

### 试题分类添加：questionType/add

| 请求参数 | 类型          | 描述  | 必填  |
| ---- | ----------- | --- | --- |
| name | String (16) | 名称  | 是   |

| 响应参数 | 类型      | 描述     |
| ---- | ------- | ------ |
| code | Integer | 响应码    |
| msg  | String  | 响应消息   |
| data | Integer | 试题分类ID |

### 试题分类修改：questionType/edit

| 请求参数 | 类型          | 描述  | 必填  |
| ---- | ----------- | --- | --- |
| id   | Integer     | 主键  | 是   |
| name | String (16) | 名称  | 是   |

### 试题分类删除：questionType/del

| 请求参数 | 类型      | 描述  | 必填  |
| ---- | ------- | --- | --- |
| id   | Integer | 主键  | 是   |

### 试题分类详情：questionType/get

| 请求参数 | 类型      | 描述  | 必填  |
| ---- | ------- | --- | --- |
| id   | Integer | 主键  | 是   |

| 响应参数      | 类型      | 描述     |
| --------- | ------- | ------ |
| code      | Integer | 响应码    |
| msg       | String  | 响应消息   |
| data.id   | Integer | 试题分类id |
| data.name | String  | 试题分类名称 |

### 试题列表：question/listpage

| 请求参数             | 类型      | 描述                                     | 必填  |
| ---------------- | ------- | -------------------------------------- | --- |
| questionTypeId   | Integer | 试题分类id                                 | 否   |
| questionTypeName | String  | 试题分类名称                                 | 否   |
| id               | Integer | 编号（主键）                                 | 否   |
| title            | String  | 题干                                     | 否   |
| type             | Integer | 类型（1：单选；2：多选；3：填空；4：判断；5：问答）           | 否   |
| score            | Double  | 分值                                     | 否   |
| markType         | Integer | 阅卷类型（1：客观题；2：主观题）                      | 否   |
| exIds            | String  | 排除的试题IDS（英文逗号分割）                       | 否   |
| state            | Integer | 状态（0：删除；1：发布；）；默认查询1；参数为0则查询最近7天已删除的试题 | 否   |
| rand             | String  | 随机查询 （任意字符串）                           | 否   |
| curPage          | Integer | 当前第几页                                  | 否   |
| pageSize         | Integer | 每页多少条                                  | 否   |

| 响应参数                         | 类型        | 描述                                   |
| ---------------------------- | --------- | ------------------------------------ |
| code                         | Integer   | 响应码                                  |
| msg                          | String    | 响应消息                                 |
| data.total                   | Integer   | 总行数                                  |
| data.list[]                  | Object[]  | 分页列表                                 |
| data.list[].id               | Integer   | 主键                                   |
| data.list[].type             | Integer   | 类型（1：单选；2：多选；3：填空；4：判断；5：问答          |
| data.list[].title            | String    | 题干                                   |
| data.list[].markType         | Integer   | 阅卷类型（1：客观题；2：主观题）                    |
| data.list[].state            | Integer   | 状态（0：删除；1：发布；）                       |
| data.list[].analysis         | String    | 解析                                   |
| data.list[].questionTypeId   | Integer   | 试题分类ID                               |
| data.list[].questionTypeName | Integer   | 试题分类名称                               |
| data.list[].score            | Double    | 分值                                   |
| data.list[].markOptions      | Integer[] | 分值选项                                 |
| data.list[].options[]        | Object[]  | 单多选选项（type=1,2时有效）                   |
| data.list[].answers[]        | String[]  | 答案（如果是填空或智能问答，会有多个答案）                |
| data.list[].scores[]         | Double[]  | 答案分值（如果是填空或智能问答，表示每空分值；如果是多选，表示漏选分值） |
| data.list[].updateUserName   | String    | 更新用户名称                               |

### 试题添加：question/add

| 请求参数           | 类型        | 描述                                   | 必填  |
| -------------- | --------- | ------------------------------------ | --- |
| type           | Integer   | 类型（1：单选；2：多选；3：填空；4：判断；5：问答          | 是   |
| title          | Text      | 题干                                   | 是   |
| markType       | Integer   | 阅卷类型（1：客观题；2：主观题）                    | 是   |
| analysis       | Text      | 解析                                   | 是   |
| questionTypeId | Integer   | 试题分类ID                               | 是   |
| score          | Double    | 分数                                   | 是   |
| markOptions[]  | Integer[] | 分数选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；）       | 否   |
| options[]      | String[]  | 选项，type为1,2时有效，len &lt;= 7           | 否   |
| answers[]      | String[]  | 答案（如果是填空或智能问答，会有多个答案）                | 是   |
| scores[]       | Double[]  | 答案分值（如果是填空或智能问答，表示每空分值；如果是多选，表示漏选分值） | 否   |

### 试题修改：question/edit

| 请求参数              | 类型      | 描述       | 必填  |
| ----------------- | ------- | -------- | --- |
| id                | Integer | 主键       | 是   |
| type              | Integer | 类型（修改无效） | 否   |
| 其他字段同question/add |         |          |     |

### 试题删除：question/del

| 请求参数 | 类型        | 描述   | 必填  |
| ---- | --------- | ---- | --- |
| ids  | Integer[] | 主键数组 | 是   |

### 试题获取：question/get

| 请求参数 | 类型      | 描述  | 必填  |
| ---- | ------- | --- | --- |
| id   | Integer | 主键  | 是   |

| 响应参数                | 类型        | 描述                                   |
| ------------------- | --------- | ------------------------------------ |
| code                | Integer   | 响应码                                  |
| msg                 | String    | 响应消息                                 |
| data.id             | Integer   | 主键                                   |
| data.type           | Integer   | 类型（1：单选；2：多选；3：填空；4：判断；5：问答          |
| data.title          | Text      | 题干                                   |
| data.options[]      | String[]  | 选项，type为1,2时有效，len <= 7              |
| data.markType       | Integer   | 阅卷类型（1：客观题；2：主观题）                    |
| data.analysis       | Text      | 解析                                   |
| data.questionTypeId | Integer   | 试题分类ID                               |
| data.score          | Double    | 分数                                   |
| data.markOptions[]  | Integer[] | 分数选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；）       |
| data.answers[]      | String[]  | 答案（如果是填空或智能问答，会有多个答案）                |
| data.scores[]       | Double[]  | 答案分值（如果是填空或智能问答，表示每空分值；如果是多选，表示漏选分值） |
| data.state          | Integer   | 状态（0：删除；1：发布；）                       |

### 试题复制：question/copy

| 请求参数 | 类型      | 描述  | 必填  |
| ---- | ------- | --- | --- |
| id   | Integer | 主键  | 是   |

### 考试列表：exam/listpage

| 请求参数     | 类型          | 描述    | 必填  |
| -------- | ----------- | ----- | --- |
| name     | String (16) | 名称    | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应参数                      | 类型        | 描述                          |
| ------------------------- | --------- | --------------------------- |
| code                      | Integer   | 响应码                         |
| msg                       | String    | 响应消息                        |
| data.total                | Integer   | 总行数                         |
| data.list[]               | Object[]  | 分页列表                        |
| data.list[].id            | Integer   | 主键                          |
| data.list[].name          | String    | 名称                          |
| data.list[].startTime     | Date      | 开始时间                        |
| data.list[].endTime       | Date      | 结束时间                        |
| data.list[].markStartTime | Date      | 阅卷开始时间                      |
| data.list[].markEndTime   | Date      | 阅卷结束时间                      |
| data.list[].passScore     | Double    | 及格分数                        |
| data.list[].totalScore    | Double    | 总分数                         |
| data.list[].markState     | Integer   | 阅卷方式（1：客观题；2：主观题；）          |
| data.list[].scoreState    | Integer   | 成绩查询状态（1：考试结束后；2：不公布；3：交卷后） |
| data.list[].rankState     | Integer   | 排名状态（1：公布；2：不公布）            |
| data.list[].genType       | Integer   | 组卷方式（1：人工组卷；2：随机组卷）         |
| data.list[].markType      | Integer   | 阅卷方式（1：客观题；2：主观题；）          |
| data.list[].sxes          | Integer[] | 反作弊（1：试题乱序；2：选项乱序；）         |
| data.list[].anonState     | Integer[] | 匿名阅卷（1：是；2：否）               |
| data.list[].userNum       | Integer   | 用户数量                        |
| data.list[].markUserNum   | Integer   | 阅卷用户数量                      |

### 考试发布：exam/publish

| 请求参数 | 类型     | 描述      | 必填  |
| ---- | ------ | ------- | --- |
| 看下文  | String | json字符串 | 是   |

```json
{
    "id": null, // 考试ID（修改时用）
    "name": "考试-2023-04-01",// 考试名称
    "paperName": "试卷-2023-04-01",// 试卷试卷名称
    "genType": 1, // 组卷方式（1：人工组卷；2：随机组卷）
    "passScore": 14,// 及格分数
    "sxes": [2, 1],// genType == 1有效
    "scoreState": 3,// 成绩查询状态（1：考试结束后；2：不公布；3：交卷后）
    "rankState": 1,// 排名状态（1：公布；2：不公布）
    "examQuestions": [{// genType == 1有效（固定组卷）
        "type": 1,// 类型 （1：章节；2：试题）
        "chapterName": "单选题",// 章节名称
        "chapterTxt": ""// 章节描述（非必填）
    }, {
        "type": 2,// 类型 （1：章节；2：试题） 试题数据格式参考question/add接口
        "questionType": 1,// 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）
        "markType": 1,//  阅卷类型（1：客观题；2：主观题）
        "title": "这是一道单选题的题干，简单写法",// 题干
        "score": 1,// 分数
        "answers": ["B"],// 答案
        "scores": [],// 分数（非必填，默认为1分）
        "options": ["单选题的A选项", "单选题的B选项", "单选题的C选项", "单选题的D选项"],// 选项
    },],
    "examRules": [{// genType == 1有效（随机组卷）
        "type": 1,// 类型 （1：章节；2：试题）
        "chapterName": "单选题",// 章节名称
        "chapterTxt": ""// 章节描述
    }, {
        "type": 2,// 类型 （1：章节；2：试题）
        "questionTypeId": 5,// 题库ID
        "questionType": 1,// 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）
        "markType": 1,//  阅卷类型（1：客观题；2：主观题），1有效
        "markOptions": [],//  阅卷选项（2：答案无顺序；3：不分大小写；) questionType == 3，23有效；questionType == 5,3有效
        "num": 10,// 题数
        "score": 1,// 分数
        "scores": []// questionType == 2有效，漏选分数
    }],
    "examUserIds": [1229, 1228, 1193, 1201, 1209],// 用户IDS
    "totalScore": 24,// 总分
    "markType": 2,// 阅卷类型（1：客观题；2：主观题）
    "startTime": "2023-04-01 08:00:00",// 考试开始时间
    "endTime": "2023-04-01 10:00:00",// 考试结束时间
    "markStartTime": "2023-04-01 14:00:00",// 阅卷开始时间
    "markEndTime": "2023-04-01 18:00:00",// 阅卷结束时间
    "limitMinute": 60 // 限制分钟（考试开始时间由用户第一次打开试卷时计时））
}
```

| 响应参数 | 类型      | 描述                                         |
| ---- | ------- | ------------------------------------------ |
| code | Integer | 响应码                                        |
| msg  | String  | 响应消息                                       |
| data | Integer | 进度条ID，每隔1秒请求一次获取发布进度，参考：progressBar/get 接口 |

### 考试试卷：exam/paper

| 请求参数 | 类型      | 描述  |
| ---- | ------- | --- |
| id   | Integer | 主键  |

| 响应参数               | 类型        | 描述                          |
| ------------------ | --------- | --------------------------- |
| code               | Integer   | 响应码                         |
| msg                | String    | 响应消息                        |
| data.id            | Integer   | 主键                          |
| data.name          | String    | 考试名称                        |
| data.paperName     | String    | 试卷名称                        |
| data.markType      | Integer   | 阅卷类型                        |
| data.startTime     | Date      | 考试开始时间                      |
| data.endTime       | Date      | 考试结束时间                      |
| data.markStartTime | Date      | 阅卷开始时间                      |
| data.markEndTime   | Date      | 阅卷结束时间                      |
| data.genType       | Integer   | 组卷方式（1：人工组卷；2：随机组卷）         |
| data.passScore     | Integer   | 及格分数                        |
| data.anonState     | Integer   | 匿名阅卷状态（1：是；2：否）             |
| data.scoreState    | Integer   | 成绩查询状态（1：考试结束后；2：不公布；3：交卷后） |
| data.rankState     | Integer   | 排名状态（1：公布；2：不公布）            |
| data.sxes          | Integer[] | 反作弊（1：试题乱序；2：选项乱序；）         |
| data.examQuestions | Object[]  | 参考 exam/publish 接口          |
| data.examRules     | Object[]  | 参考 exam/publish 接口          |
| data.examUserIds   | Integer[] | 考试用户IDS                     |

### 考试删除：exam/del

| 请求参数 | 类型      | 描述  | 必填  |
| ---- | ------- | --- | --- |
| id   | Integer | 主键  | 是   |

### 考试添加：exam/get

| 请求参数 | 类型      | 描述  |
| ---- | ------- | --- |
| id   | Integer | 主键  |

| 响应参数               | 类型        | 描述                          |
| ------------------ | --------- | --------------------------- |
| code               | Integer   | 响应码                         |
| msg                | String    | 响应消息                        |
| data.id            | Integer   | 主键                          |
| data.name          | String    | 考试名称                        |
| data.paperName     | String    | 试卷名称                        |
| data.startTime     | Date      | 考试开始时间                      |
| data.endTime       | Date      | 考试结束时间                      |
| data.markStartTime | Date      | 阅卷开始时间                      |
| data.markEndTime   | Date      | 阅卷结束时间                      |
| data.markState     | Integer   | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）    |
| data.scoreState    | Integer   | 成绩查询状态（1：考试结束后；2：不公布；3：交卷后） |
| data.rankState     | Integer   | 排名状态（1：公布；2：不公布）            |
| data.anonState     | Integer   | 匿名阅卷状态（1：是；2：否）             |
| data.passScore     | Double    | 及格分数                        |
| data.totalScore    | Double    | 总分数                         |
| data.markType      | Integer   | 阅卷方式（1：客观题；2：主观题；）          |
| data.genType       | Integer   | 组卷方式（1：人工组卷；2：随机组卷）         |
| data.sxes          | Integer[] | 反作弊（1：试题乱序；2：选项乱序；）         |
| data.state         | Integer   | 状态（0：删除；1：发布；）              |

### 考试变更时间：exam/time

| 请求参数     | 类型      | 描述                                       | 必填  |
| -------- | ------- | ---------------------------------------- | --- |
| id       | Integer | 考试id                                     | 是   |
| timeType | Integer | 状态 【1：考试开始时间；2：考试结束时间；3：阅卷开始时间；4：阅卷结束时间】 | 是   |
| minute   | Integer | 分钟数                                      | 是   |

### 练习列表：exer/listpage

| 请求参数           | 类型          | 描述           | 必填  |
| -------------- | ----------- | ------------ | --- |
| questionTypeId | Integer     | 题库ID         | 否   |
| name           | String (16) | 名称           | 否   |
| todo           | Boolean     | 未完成的（true有效） | 否   |
| startTime      | Date        | 开始时间         | 否   |
| endTime        | Date        | 结束时间         | 否   |
| curPage        | Integer     | 当前第几页        | 否   |
| pageSize       | Integer     | 每页多少条        | 否   |

| 响应参数                         | 类型        | 描述            |
| ---------------------------- | --------- | ------------- |
| code                         | Integer   | 响应码           |
| msg                          | String    | 响应消息          |
| data.total                   | Integer   | 总行数           |
| data.list[]                  | Object[]  | 分页列表          |
| data.list[].id               | Integer   | 主键            |
| data.list[].name             | String    | 名称            |
| data.list[].startTime        | Date      | 开始时间          |
| data.list[].endTime          | Date      | 结束时间          |
| data.list[].rmkState         | Integer   | 允许评论（1：是；2：否） |
| data.list[].userIds          | Integer[] | 用户IDS         |
| data.list[].questionTypeName | String    | 题库名称          |

### 练习添加：exer/add

| 请求参数           | 类型        | 描述            | 必填  |
| -------------- | --------- | ------------- | --- |
| name           | String    | 练习名称          |     |
| questionTypeId | Integer   | 题库ID          | 是   |
| startTime      | Integer   | 开始时间          | 是   |
| endTime        | Integer   | 结束时间          | 是   |
| rmkState       | Integer   | 允许评论（1：是；2：否） | 是   |
| userIds        | Integer[] | 没有默认表示所有用户    | 否   |

### 练习添加：exer/add

| 请求参数           | 类型        | 描述            | 必填  |
| -------------- | --------- | ------------- | --- |
| id             | Integer   | 练习ID          | 是   |
| name           | String    | 练习名称          |     |
| questionTypeId | Integer   | 题库ID          | 是   |
| startTime      | Integer   | 开始时间          | 是   |
| endTime        | Integer   | 结束时间          | 是   |
| rmkState       | Integer   | 允许评论（1：是；2：否） | 是   |
| userIds        | Integer[] | 没有默认表示所有用户    | 否   |

### 练习添加：exer/del

| 请求参数 | 类型      | 描述   | 必填  |
| ---- | ------- | ---- | --- |
| id   | Integer | 练习ID | 是   |