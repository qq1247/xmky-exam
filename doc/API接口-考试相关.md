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
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| name     | String (16) | 名称       | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应参数| 类型    | 描述     |
| -------------------------- | ------- | -------- |
| code                       | Integer | 响应码   |
| msg                        | String  | 响应消息 |
| data.total                 | Integer | 总行数   |
| data.list[]                | Object[]   | 分页列表 |
| data.list[].id             | Integer | 主键     |
| data.list[].name           | String  | 名称     |
| data.list[].createUserId| String  | 创建用户ID   |
| data.list[].createUserName| String  | 创建用户名称  |
| data.list[].writeUserIds[] | Integer  | 组用户ID |
| data.list[].writeUserNames[] | String[] | 组用户 |

### 试题分类添加：questionType/add
| 请求参数| 类型        | 描述 | 必填 |
| ---- | ----------- | ---- | ---- |
| name | String (16) | 名称 | 是   |

### 试题分类修改：questionType/edit
| 请求参数| 类型        | 描述 | 必填 |
| ---- | ----------- | ---- | ---- |
| id   | Integer     | 主键 | 是   |
| name | String (16) | 名称 | 是   |

### 试题分类删除：questionType/del
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 试题分类详情：questionType/get
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

| 响应参数| 类型    | 描述       |
| ------------------- | ------- | ---------- |
| code                | Integer | 响应码     |
| msg                 | String  | 响应消息   |
| data.id             | Integer | 分类id     |
| data.name           | String  | 分类名称   |
| data.createUserId   | Integer | 创建人ID   |
| data.createUserName | String  | 创建人名称 |
| data.createTime     | Date    | 创建时间   |
| data.writeUserIds[]   | String[] | 组用户ID  |
| data.writeUserNames[]   | String[] | 组用户名称  |

### 试题分类权限：questionType/auth
| 请求参数 | 类型    | 描述   | 必填 |
| ------------ | ------- | ------ | ---- |
| id           | Integer | 主键   | 是   |
| writeUserIds | String[]  | 组用户 | 是   |

### 试题分类合并：questionType/move
| 请求参数| 类型    | 描述   | 必填 |
| -------- | ------- | ------ | ---- |
| sourceId | String  | 源ID   | 是   |
| targetId | String  | 目标ID | 是   |

### 试卷分类列表：paperType/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| name     | String (16) | 名称       | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应参数| 类型    | 描述     |
| -------------------------- | ------- | -------- |
| code                       | Integer | 响应码   |
| msg                        | String  | 响应消息 |
| data.total                 | Integer | 总行数   |
| data.list[]                | Object[]   | 分页列表 |
| data.list[].id             | Integer | 主键     |
| data.list[].name           | String  | 名称     |
| data.list[].createUserId| String  | 创建用户ID   |
| data.list[].createUserName| String  | 创建用户名称  |
| data.list[].readUserIds[] | Integer  | 组用户ID |
| data.list[].readUserNames[] | String[] | 组用户 |

### 试卷分类添加：paperType/add
#### 参考questionType/add

### 试卷分类修改：paperType/edit
#### 参考questionType/edit

### 试卷分类删除：paperType/del
#### 参考questionType/del

### 试卷分类详情：paperType/get
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

| 响应参数| 类型    | 描述       |
| ------------------- | ------- | ---------- |
| code                | Integer | 响应码     |
| msg                 | String  | 响应消息   |
| data.id             | Integer | 分类id     |
| data.name           | String  | 分类名称   |
| data.createUserId   | Integer | 创建人ID   |
| data.createUserName | String  | 创建人名称 |
| data.createTime     | Date    | 创建时间   |
| data.readUserIds[]   | String[] | 组用户ID  |
| data.readUserNames[]   | String[] | 组用户名称  |

### 试卷分类权限：paperType/auth
| 请求参数 | 类型    | 描述   | 必填 |
| ------------ | ------- | ------ | ---- |
| id           | Integer | 主键   | 是   |
| readUserIds | String[]  | 组用户 | 是   |

### 试卷分类合并：paperType/move
#### 参考questionType/move

### 考试分类列表：examType/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| name     | String (16) | 名称       | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应参数| 类型    | 描述     |
| -------------------------- | ------- | -------- |
| code                       | Integer | 响应码   |
| msg                        | String  | 响应消息 |
| data.total                 | Integer | 总行数   |
| data.list[]                | Object[]   | 分页列表 |
| data.list[].id             | Integer | 主键     |
| data.list[].name           | String  | 名称     |

### 考试分类添加：examType/add
#### 参考questionType/add

### 考试分类修改：examType/edit
#### 参考questionType/edit

### 考试分类删除：examType/del
#### 参考questionType/del

### 考试分类详情：examType/get
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

| 响应参数| 类型    | 描述       |
| ------------------- | ------- | ---------- |
| code                | Integer | 响应码     |
| msg                 | String  | 响应消息   |
| data.id             | Integer | 分类id     |
| data.name           | String  | 分类名称   |

### 试题分类开放列表：questionTypeOpen/listpage
| 请求参数| 类型    | 描述       | 必填 |
| --------- | ------- | ---------- | ---- |
| startTime | Date    | 开始时间   | 否   |
| endTime   | Date    | 结束时间   | 否   |
| curPage   | Integer | 当前第几页 | 否   |
| pageSize  | Integer | 每页多少条 | 否   |

| 响应参数| 类型    | 描述         |
| --------------------- | ------- | ------------ |
| code                  | Integer | 响应码       |
| msg                   | String  | 响应消息     |
| data.total            | Integer | 总行数       |
| data.list[]           | Object[]   | 分页列表     |
| data.list[].id        | Integer | 主键         |
| data.list[].startTime | Date    | 开始时间         |
| data.list[].endTime   | Date    | 结束时间     |
| data.list[].userIds   | String  | 授权用户IDS      |
| data.list[].userNames | String  | 授权用户名称     |
| data.list[].orgIds    | String  | 授权组织机构IDS  |
| data.list[].orgNames  | String  | 授权组织机构名称 |

### 试题分类开放添加：questionTypeOpen/add
| 请求参数| 类型         | 描述     | 必填 |
| --------- | ------------ | -------- | ---- |
| id        | Integer      | 主键     | 是   |
| startTime | Date         | 开始时间 | 是   |
| endTime   | Date         | 结束时间 | 是   |
| userIds   | String(1024) | 授权用户 | 否   |
| orgIds    | String(1024) | 授权机构 | 否   |

### 试题分类开放删除：questionTypeOpen/del
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 试题列表：question/listpage
| 请求参数| 类型    | 描述         | 必填 |
| -------------- | ------- | ------------ | ---- |
| questionTypeId | Integer | 试题分类id   | 否   |
| questionTypeName| String  | 试题分类名称 | 否   |
| id             | Integer | 编号（主键）      | 否   |
| title          | String  | 题干         | 否   |
| type           | Integer | 类型         | 否   |
| difficulty     | Integer | 难度         | 否   |
| score     | Double  | 分值     | 否   |
| ai		 | Integer | 智能阅卷（1：是；2：否） | 否   |
| paperId      | Integer | 试卷ID   | 否   |
| exPaperId      | Integer | 排除试卷ID（用于组卷时过滤掉已添加过的试题）   | 否   |
| state | Integer | 状态（0：删除；1：发布；2：草稿）；默认查询1,2；参数为0则查询最近7天已删除的试题 | 否   |
| rand        | Object | 随机查询 （任意字符串）  | 否   |
| curPage        | Integer | 当前第几页   | 否   |
| pageSize       | Integer | 每页多少条   | 否   |

| 响应参数| 类型    | 描述         |
| ---------------------------- | ------- | ------------ |
| code                         | Integer | 响应码       |
| msg                          | String  | 响应消息     |
| data.total                   | Integer | 总行数       |
| data.list[]                  | Object[]   | 分页列表     |
| data.list[].id               | Integer | 主键         |
| data.list[].type             | Integer | 类型         |
| data.list[].difficulty       | Integer | 难度         |
| data.list[].title            | String  | 题干         |
| data.list[].options            | String[]  | 选项（type=1,2时有效）         |
| data.list[].state            | Integer | 状态         |
| data.list[].questionTypeId   | Integer | 试题分类ID   |
| data.list[].score            | Double  | 分值         |
| data.list[].scoreOptions     | Integer[]  | 分值选项     |
| data.list[].createUserName     | String  | 创建人     |

### 试题添加：question/add
| 请求参数| 类型  | 描述    | 必填 |
| ------ | ----- | ------- | ---- |
| type           | Integer         | 类型（1：单选；2：多选；3：填空；4：判断；5：问答 | 是   |
| difficulty     | Integer         | 难度（1：极易；2：简单；3：适中；4：困难；5：极难 ） | 是   |
| title          | Text | 题干 | 是   |
| options[]      | String[]        | 选项，type为1,2时有效，len &lt;= 7  | 否   |
| ai| Integer    | 智能阅卷（1：是；2：否；）  | 是   |
| analysis       | Text    | 解析  | 是   |
| questionTypeId | Integer         | 试题分类ID      | 是   |
| score          | Double          | 分数   | 是   |
| scoreOptions[] | Integer[] | 分数选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；）|否|
| answers[]      | String[]	   | 答案（如果是填空或智能问答，会有多个答案）  | 是   |
| answerScores[] | Double[] | 答案分值（如果是填空或智能问答，表示每空分值；如果是多选，表示漏选分值）  | 是   |

### 试题修改：question/edit
| 请求参数| 类型    | 描述 | 必填 |
| ---------------------- | ------- | ---- | ---- |
| id                     | Integer | 主键 | 是   |
| type                   | Integer | 类型（修改无效） | 否 |
| 其他字段同question/add |         |      |      |  |

### 试题删除：question/del
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 试题获取：question/get
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

| 响应参数| 类型    | 描述 | 
| -------- | ------- | ---- |
| code                | Integer | 响应码     |
| msg                 | String  | 响应消息   |
| data.id           | Integer         | 主键 |
| data.type           | Integer         | 类型（1：单选；2：多选；3：填空；4：判断；5：问答 |
| data.difficulty     | Integer         | 难度（1：极易；2：简单；3：适中；4：困难；5：极难 ） | 
| data.title          | Text | 题干 |
| data.options[]      | String[]        | 选项，type为1,2时有效，len &lt;= 7  |
| data.ai| Integer    | 智能阅卷（1：是；2：否；）  | 
| data.analysis       | Text    | 解析  | 
| data.questionTypeId | Integer         | 试题分类ID      |
| data.score          | Double          | 分数   | 是
| data.scoreOptions[] | Integer[] | 分数选项（1：漏选得分；2：答案无顺序；3：大小写不敏感；）|
| data.state| Integer | 状态（0：删除；1：发布；2：草稿）|
| data.answers[]      | Object[]   | 答案数组   | 
| data.answers[].answer      | String   | 答案   | 
| data.answers[].score     | Double    | 得分   | 

### 试题复制：question/copy
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 试题导入：question/wordImp
| 请求参数| 类型    | 描述       | 必填 |
| --------------- | ------- | ---------- | ---- |
| fileId            | fileId    | 附件       | 是   |
| questionTypeId | Integer | 试题分类ID | 是   |

### 试题模板导出：question/wordTemplateExport
| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|	| Binary| 二进制流|

### 试题发布：question/publish
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| questionTypeId| Integer | 试题分类ID（全部发布） | 否   |
| ids| Integer[] | 试题ID（部分试题发布） | 否   |

### 试题导出：question/wordExport 暂未实现
### 试卷列表：paper/listpage
| 请求参数| 类型       | 描述       | 必填 |
| -------- | ---------- | ---------- | ---- |
| paperTypeId| Integer     | 试卷分类ID| 否   |
| name     | String(16) | 试卷名称   | 否   |
| state     | Integer | 状态（默认查询草稿和已发布） | 否   |
| curPage  | Integer    | 当前第几页 | 否   |
| pageSize | Integer    | 每页多少条 | 否   |

| 响应参数| 类型    | 描述 |
| ------------------------- | ------- | ---------------- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.total                | Integer | 总行数  |
| data.list[]               | Object[]   | 分页列表|
| data.list[].id            | Integer | 主键    |
| data.list[].name          | String  | 名称   |
| data.list[].state         | Integer | 状态   |
| data.list[].stateName     | String  | 状态名称 |
| data.list[].passScore     | Double  | 及格分数（百分比） |
| data.list[].totalScore    | Double  | 总分数 |
| data.list[].paperTypeId   | Integer | 试卷分类id  |
| data.list[].markType      | Integer | 阅卷方式（1：智能阅卷；2：人工阅卷；） |
| data.list[].showType      | Integer | 展示方式（1：整卷展示；2：章节显示；3：单题展示；） |
| data.list[].genType       | Integer | 组卷方式（1：人工组卷；2：随机组卷） |

### 试卷添加：paper/add
| 请求参数             | 类型          | 描述   | 必填 |
| ---------| ------------- | -------- | ---- |
| genType | Integer       | 组卷方式（1：人工组卷；2：随机组卷）   | 是   |
| markType | Integer       | 阅卷方式（1：智能阅卷；2：人工阅卷；）  | 是   |
| name  | String(32)    | 名称  | 是   |
| passScore | Double        | 及格分数（百分比） | 是   |
| showType | Integer       | 展示方式（1：整卷展示；3：单题展示；）| 否   |
| paperTypeId | Integer       | 试卷分类ID | 是  |

### 试卷修改：paper/edit
| 请求参数| 类型    | 描述 | 必填 |
| ------------------- | ------- | ---- | ---- |
| id                  | Integer | 主键 | 是   |
| 其他字段同paper/add |         |      |      |  |

### 试卷删除：paper/del
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 试卷详细：paper/get
| 请求参数| 类型    | 描述   | 必填 |
| ---- | ------- | ------ | ---- |
| id   | Integer | 试卷ID | 是   |

| 响应参数| 类型    | 描述     |
| ------------------- | ------- | -------- |
| code                | Integer | 响应码   |
| msg                 | String  | 响应消息 |
| data.id             | Integer | 试卷主键 |
| data.name  | String  | 名称 |
| data.passScore  | Double| 及格分数 |
| data.totalScore | Double| 总分数 |
| data.genType  | Integer| 组卷方式 |
| data.markType  | Integer| 阅卷方式 |
| data.showType  | Integer| 展示方式 |
| data.state  | Integer| 状态 |
| data.paperTypeId  | Integer| 试卷分类ID |

### 试卷拷贝：paper/copy
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 试卷归档：paper/archive
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 章节添加：paper/chapterAdd
| 请求参数| 类型    | 描述                              | 必填 |
| ----------- | ------- | --------------------------------- | ---- |
| name        | String  | 章节名称                          | 是   |
| description | String  | 描述                              | 是   |
| paperId     | Integer | 试卷ID                            | 是   |

### 章节修改：paper/chapterEdit
| 请求参数| 类型    | 描述     | 必填 |
| ----------- | ------- | -------- | ---- |
| chapterId| Integer | 章节id   | 是   |
| name        | String  | 章节名称 | 是   |
| description | String  | 描述     | 是   |

### 章节删除：paper/chapterDel
| 请求参数| 类型    | 描述   | 必填 |
| ---- | ------- | ------ | ---- |
| chapterId| Integer | 章节id | 是   |

### 章节移动：paper/chapterMove
| 请求参数| 类型    | 描述       | 必填 |
| --------------- | ------- | ---------- | ---- |
| sourceId | Integer | 源章节ID | 是   |
| targetId | Integer | 目标章节ID | 是   |

### 试题移动：paper/questionMove
###### 只支持同章节下试题移动
| 请求参数| 类型    | 描述       | 必填 |
| --------------- | ------- | ---------- | ---- |
| id | Integer | 主键【试卷】 | 是   |
| sourceId | Integer | 源试题ID | 是   |
| targetId | Integer | 目标试题ID | 是   |

### 试卷试题列表：paper/paperQuestionList
| 请求参数| 类型    | 描述   | 必填 |
| ---- | ------- | ------ | ---- |
| id   | Integer | 试卷id | 否   |

| 响应参数|  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | Object[]  | 分页列表 |
|data.list[].chapter.id  | Integer  | 章节id |
|data.list[].chapter.name  | String  | 章节名称 |
|data.list[].chapter.description  | String  | 章节描述 |
|data.list[].questionList[].id  | Integer  | 试题id |
|data.list[].questionList[].type  | Integer  | 试题类型，参考question/add |
|data.list[].questionList[].difficulty  | Integer  | 试题难度，难易度参考question/add|
|data.list[].questionList[].title  | String  | 试题标题 |
|data.list[].questionList[].ai  | Integer  | 是否智能阅卷|
|data.list[].questionList[].analysis  | String  | 试题解析 |
|data.list[].questionList[].score  | Double  | 试题分数 |
|data.list[].questionList[].scoreOptions  | String  | 试题分数选项，参考question/add |
|data.list[].questionList[].options[]  | String[]  | 试题选项，参考question/add |
|data.list[].questionList[].answers[]  | Object[]  | 试题答案，参考question/add |
|data.list[].questionList[].answers[].score  | Double  | 试题分数，参考question/add |
|data.list[].questionList[].answers[].answer  | String | 试题答案，参考question/add |

### 试卷试题添加：paper/questionAdd
| 请求参数| 类型      | 描述   | 必填 |
| ----------- | --------- | ------ | ---- |
| chapterId   | Integer   | 章节id | 是   |
| questionIds | Integer[] | 试题id | 是   |

### 试卷试题删除：paper/questionDel
| 请求参数| 类型    | 描述       | 必填 |
| --------------- | ------- | ---------- | ---- |
| id| Integer | 主键| 是   |
| questionId| Integer | 试题ID | 是   |

### 试卷试题清空：paper/questionClear
| 请求参数| 类型    | 描述   | 必填 |
| --------- | ------- | ------ | ---- |
| chapterId | Integer | 章节id | 是   |

### 试卷试题设置分值：paper/scoreUpdate
| 请求参数| 类型    | 描述       | 必填 |
| --------------- | ------- | ---------- | ---- |
| id| Integer | 主键 | 是   |
| questionId| Integer  | 试题ID       | 是   |
| score | Double  | 分数 | 是 |
| subScores| Double  | 每空分数（试题为智能阅卷，并且是填空或问答时有效） | 是   |
| scoreOptions    | Integer[] | 分数选项     | 否  |

### 试卷设置分数选项：paper/scoreOptionUpdate
| 请求参数| 类型      | 描述       | 必填 |
| --------------- | --------- | ---------- | ---- |
| id | Integer   | 主键 | 是   |
| questionId | Integer   | 试题ID | 是   |
| scoreOptions    | Integer[] | 分数选项     | 是   |

### 试卷发布：paper/publish
| 请求参数| 类型    | 描述   | 必填 |
| ---- | ------- | ------ | ---- |
| id   | Integer | 试卷id | 是   |

### 试卷更新分数：paper/totalScoreUpdate
###### 组卷时，总分数由前端计算并显示（加速响应提高用户体验）
###### 在关闭浏览器，或在组卷页面点击返回按钮时，调用该接口来更新试卷总分数。
###### 浏览器崩溃或前端异常等原因，不能保证一定能调用到该接口，最终由paper/publish接口保证结果的一致性和正确性。
| 请求参数| 类型    | 描述   | 必填 |
| ---- | ------- | ------ | ---- |
| id   | Integer | 试卷id | 是   |

### 考试列表：exam/listpage
| 请求参数| 类型        | 描述       | 必填 |
| -------- | ----------- | ---------- | ---- |
| name     | String (16) | 名称       | 否   |
| examTypeId| Integer      | 考试分类ID| 否   |
| state| Integer      | 状态 | 否   |
| curPage  | Integer     | 当前第几页 | 否   |
| pageSize | Integer     | 每页多少条 | 否   |

| 响应参数| 类型    | 描述     |
| ---------------- | ------- | ------- |
| code             | Integer | 响应码   |
| msg              | String  | 响应消息 |
| data.total       | Integer | 总行数   |
| data.list[]      | Object[]   | 分页列表 |
| data.list[].id   | Integer | 主键     |
| data.list[].name | String  | 名称     |
| data.list[].startTime| Date| 开始时间|
| data.list[].endTime| Date| 结束时间|
| data.list[].markStartTime| Date| 阅卷开始时间|
| data.list[].markEndTime| Date| 阅卷结束时间|
| data.list[].state   | Integer | 考试状态     |
| data.list[].paperMarkType  | Integer |  试卷阅卷类型   |
| data.list[].paperId| Integer| 试卷Id    |
| data.list[].paperName|String| 试卷名称    |
| data.list[].paperTotleScore| Double| 试卷总分    |
| data.list[].paperPassScore| Double  | 试卷及格分数   |

### 考试添加：exam/add
| 请求参数| 类型        | 描述                         		| 必填 |
| ------------- | ----------- | ------------------------------------| ---- |
| name          | String (16) | 名称  | 是   |
| startTime     | Date        | 考试开始时间 | 是   |
| endTime       | Date        | 考试结束时间  | 是   |
| markStartTime | Date        | 阅卷开始时间 （试卷为智能阅卷时有效）| 否   |
| markEndTime   | Date        | 阅卷结束时间 （试卷为智能阅卷时有效）| 否   |
| paperId       | Integer     | 试卷ID  | 是   |
| examTypeId    | Integer     | 考试分类ID | 是   |

### 考试修改：exam/edit
| 请求参数| 类型    | 描述 | 必填 |
| -------------------- | ------- | ---- | ---- |
| id                   | Integer | 主键 | 是   |
| examTypeId    | Integer     | 修改无效 | 是   |
| 其他字段参考exam/add |         |      |      | 

### 考试删除：exam/del
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 考试添加：exam/get
| 请求参数| 类型        | 描述   |
| ------------- | ----------- | ------|
| id          | Integer | 主键  |

| 响应参数  | 类型    | 描述     |
| --------| ------- | ------- |
| code     | Integer | 响应码   |
| msg     | String  | 响应消息 |
| data.id  | Integer | 主键  |
| data.name          | String | 名称  |
| data.startTime     | Date        | 考试开始时间 |
| data.endTime       | Date        | 考试结束时间  |
| data.markStartTime | Date        | 阅卷开始时间 |
| data.markEndTime   | Date        | 阅卷结束时间 |
| data.paperId       | Integer     | 试卷ID  |
| data.paperName       | String     | 试卷名称  |
| data.examTypeId    | Integer     | 考试分类ID |
| data.paperMarkType    | Integer     | 试卷阅卷类型 |

### 考试发布：exam/publish
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 考试归档：exam/archive
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

### 考试阅卷用户列表：exam/markUserList
| 请求参数| 类型    | 描述 | 必填 |
| ---- | ------- | ---- | ---- |
| id   | Integer | 主键 | 是   |

| 响应参数| 类型    | 描述     |
| --------- | ------- | -------- |
| code      | Integer | 响应码   |
| msg       | String  | 响应消息 |
| data[].markUserId   | Integer | 阅卷用户ID（智能阅卷时有效）     |
| data[].markUserName | String  | 阅卷用户名称（智能阅卷时有效） |
| data[].examUserList[].id | Integer  | 考试用户ID |
| data[].examUserList[].name | String  | 考试用户名称 |
| data[].examUserList[].orgName | String  | 考试用户组织机构 |

### 考试阅卷设置：exam/updateMarkSet
| 请求参数| 类型      | 描述        | 必填 |
| ----------- | --------- | ----------- | ---- |
| id          | Integer   | 主键        | 是   |
| markUserIds | Integer[] | 阅卷用户IDS | 是   |
| examUserIds | Integer[] | 考试用户IDS | 否   |
| questionIds | Integer[] | 试题IDS examUserIds和questionIds都有效时，默认使用examUserIds  | 否   |


### 考试在线用户：exam/onlineUser
| 请求参数| 类型      | 描述        | 必填 |
| ----------- | --------- | ----------- | ---- |
| id          | Integer   | 主键        | 是   |

| 响应参数| 类型    | 描述     |
| --------- | ------- | -------- |
| code      | Integer | 响应码   |
| msg       | String  | 响应消息 |
| data.userId| Integer | 用户ID  |
| data.userName| String  | 用户名称  |
| data.online| Boolean| 在线状态（true：在线；false：离线）  |
| data.onlineTime| Date| 离线时间  |

### 试题评论列表：questionComment/listpage
| 请求参数| 类型       | 描述       | 必填 |
| -------- | ---------- | ---------- | ---- |
| questionId| Integer | 试题ID | 是   |
| parentId | Integer  | 父评论ID | 否   |
| curPage  | Integer    | 当前第几页 | 否   |
| pageSize | Integer    | 每页多少条 | 否   |

| 响应参数| 类型    | 描述 |
| ------------------------- | ------- | ---------------- |
| code                      | Integer | 响应码    |
| msg                       | String  | 响应消息 |
| data.total                | Integer | 总行数  |
| data.list[]               | Object[]   | 分页列表|
| data.list[].id            | Integer | 主键    |
| data.list[].content | Text  | 内容   |
| data.list[].createUserId| Integer | 评论用户ID  |
| data.list[].createUserName| String  | 评论用户名称 |
| data.list[].createTime| Date | 评论时间   |

### 试题评论添加：questionComment/add
######
| 请求参数             | 类型          | 描述   | 必填 |
| ---------| ------------- | -------- | ---- |
| questionId| Integer       | 试题ID | 是   |
| parentId| Integer       | 评论父ID（二级节点是有效） | 否   |
| content | Integer       | 评论内容  | 是   |
| anon | Integer       | 匿名（1：是；2：否） | 是   |