#在线考试接口文档
数据类型
Date：yyyy-MM-dd HH:mm:ss
参数
pageSize <=100
code == 200 请求正常
code == 500 服务器内部错误
code == 401 无权限或登录超时

##考试相关
####试题分类列表：questionType/listpage
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name | String  | 名称 |
|data.list[].rwState | Integer  | 1：开启；2：禁用 |
|data.list[].readUserNames | String  | 读权限 |
|data.list[].writeUserNames | String  | 写权限 |

####试题分类添加：questionType/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类修改：questionType/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer | 主键 |   是     |
|name      | String (16)  | 名称   |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类删除：questionType/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类权限：questionType/auth
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|readUserIds    | String     |   读权限   |   否   |
|writeUserIds    | String |  写权限 |  否  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类详情：questionType/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer  | 主键 |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.id     | Integer  | 分类id |
|data.name      | String  | 分类名称 |
|data.createUserId   | Integer  | 创建人ID |
|data.createUserName   | String  | 创建人名称 |
|data.createTime   | Date  | 创建时间 |
|data.rwState   | Integer  | 读写状态【1：开启；2：禁用'】 |
|data.readUserIds   | Integer  | 读权限 |
|data.writeUserIds   | Integer  | 写权限 |

####试题分类合并：questionType/move
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|sourceId    | String     |   源ID   |   否   |
|targetId    | String | 目标ID |  否  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类开放列表：questionTypeOpen/listpage
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|startTime| Date | 开始时间   |   否     |
|endTime| Date | 结束时间  |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].startTime | Date | 类型 |
|data.list[].endTime | Date  | 类型名称 |
|data.list[].userIds | String | 用户IDS |
|data.list[].userNames | String | 用户名称 |
|data.list[].orgIds | String  | 组织机构IDS |
|data.list[].orgNames | String  | 组织机构名称 |

####试题分类开放添加：questionTypeOpen/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|startTime    | Date | 开始时间 | 是  |
|endTime    | Date | 结束时间 |  是  |
|userIds    | String(1024) | 授权用户 |  否  |
|orgIds  | String(1024) | 授权机构 |  是  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题分类开放删除：questionTypeOpen/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题列表：question/listpage
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id| Integer | 试卷id   |   否     |
|name| String | 试题分类名称   |   否     |
|title      | String  | 题干 |   否     |
|type      | Integer | 类型 |   否     |
|difficulty    | Integer | 难度 |   否     |
|scoreStart    | double | 分值大于 |   否     |
|scoreEnd    | double | 分值小于 |   否     |
|questionTypeId| Integer | 试题分类id   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].type | Integer | 类型 |
|data.list[].typeName | String  | 类型名称 |
|data.list[].difficulty | Integer | 难度 |
|data.list[].difficultyName | String  | 难度名称 |
|data.list[].title | String  | 题干 |
|data.list[].options[] | String  | 选项 |
|data.list[].answer | String  | 答案 |
|data.list[].analysis | String  | 解析 |
|data.list[].state | Integer  | 状态 |
|data.list[].questionTypeId | Integer  | 试题分类ID |
|data.list[].questionTypeName | Integer  | 试题分类名称 |
|data.list[].score | Double  | 分值 |
|data.list[].scoreOptions | String  | 分值选项 |
|data.list[].no | Integer  | 排序 |

####试题添加：question/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|type    | Integer     | 类型（1：单选；2：多选；3：填空；4：判断；5：问答 |   是   |
|difficulty    | Integer     |   难度（1：极易；2：简单；3：适中；4：困难；5：极难 ）|   是   |
|title    | String（65535）     | 题干 |   是   |
|options[]    | String[]     |   选项，type为1,2时有效，len <= 7  |   否   |
|answer    | String(65535)     |   答案   |   是   |
|analysis    | String(65535)     |   解析   |   是   |
|state    | Integer  |   状态（1：启用；2：禁用 ）  |   是   |
|questionTypeId    | Integer  |   试题分类ID   |   是   |
|score    | double  | 分值  |  否   |
|scoreOptions    | String(8)  | 分值选项，type为2时1有效，type为3时1,2,3,4有效，多选用英文逗号分隔。1：半对半分；2：答案无顺序；3：大小写不敏感；4：包含答案得分 |  否   |
|no    | Integer  | 排序 | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题删除：question/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |
|type    | Integer     | 类型 |  无效   |
|state    | Integer     | 状态 |   无效   |
|其他字段同question/add   |      |  |      |   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题删除：question/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |

######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题状态：question/state
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |

######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题获取：question/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |
|其他字段同question/add    |      |  |      |   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试题获取：question/copy
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷列表：paper/listpage
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| name| String(16) | 试卷名称   |   否     |
|userName| String(16) | 用户名称   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name   | String  | 名称 |
|data.list[].state   | Integer  | 状态 |
|data.list[].stateName   | String  | 状态名称 |
|data.list[].updateUserId   | Integer  | 修改人 |
|data.list[].updateTime   | Date  | 修改时间 |
|data.list[].passScore  | double  | 及格分数（百分比） |
|data.list[].totalScore   | double  | 总分数 |
|data.list[].paperTypeId   | Integer  | 试卷分类id |
|data.list[].paperTypeName  | String  | 试卷分类名称 |
|data.list[].description   | String  | 描述 |
|data.list[].showType  | Integer  | 1：整卷展示；2：章节显示；3：单题展示；数据字典：PAPER_SHOW_TYPE |
|data.list[].genType  | Integer  | 1：人工组卷；2：随机组卷 |

####试卷添加：paper/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| genType | Integer  | 1：人工组卷；2：随机组卷 |   是   |
|name    | String(32)     | 名称 |   是   |
|passScore    | double     | 及格分数（百分比） |   是   |
|readRemark    | String(65535)  | 考前阅读 |   是   |
|readNum    | Integer  | 阅读时长【分钟】 |   是   |
|showType    | Integer | 1：整卷展示；2：章节显示；3：单题展示；数据字典：PAPER_SHOW_TYPE |   否   |
|options    | String(32) | 1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告；数据字典：PAPER_OPTIONS|   否   |
|minimizeNum    | Integer | 最小化警告次数 |   否   |
|scoreRemark[]    | String[] | 分数评语 |   否   |
|scoreRemark[].score    | double | 分数（%） |  否   |
|scoreRemark[].remark    | String(32) | 评语 |  否   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷修改：paper/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| id | Integer  | 主键 |   是   |
| 其他字段同paper/add |   |  |      ||
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷删除：paper/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷详细：paper/get
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id| Integer | 试卷ID   |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.id | Integer  | 试卷主键 |
|其他字段同paper/add |   |  | |

####试卷拷贝：paper/copy
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷归档：paper/archive
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     | 主键 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####章节添加：paper/chapterAdd
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| name | String  | 章节名称 |   是   |
| description | String  | 描述 |   是   |
| paperId | Integer  | 试卷id |   是   |
| type | Integer  | 1：章节；2：固定试题；3：随机试题 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####章节修改：paper/chapterEdit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| id | Integer  | 章节id |   是   |
| name | String  | 章节名称 |   是   |
| description | String  | 描述 |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####章节删除：paper/chapterDel
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| id | Integer  | 章节id |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题列表：paper/paperQuestionList
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| id | Integer | 试卷id   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].chapter.id  | Integer  | 章节id |
|data.list[].chapter.name  | String  | 章节名称 |
|data.list[].chapter.description  | String  | 章节描述 |
|data.list[].chapter.parentId  | Integer  | 父id（树形结构的ID） |
|data.list[].questionsList[].id  | Integer  | 试题id |
|data.list[].questionsList[].type  | Integer  | 试题类型，参考question/add |
|data.list[].questionsList[].difficulty  | Integer  | 试题难度，难易度参考question/add|
|data.list[].questionsList[].title  | String  | 试题标题 |
|data.list[].questionsList[].answer  | String  | 试题答案 |
|data.list[].questionsList[].analysis  | String  | 试题解析 |
|data.list[].questionsList[].score  | double  | 试题分数 |
|data.list[].questionsList[].scoreOptions  | String  | 试题分数选项，参考question/add |
|data.list[].questionsList[].options[]  | Integer  | 试题选项，参考question/add |

####试卷试题添加：paper/questionAdd
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|chapterId | Integer |章节id  | 是 |
|questionIds | Integer[]  | 试题id | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题删除：paper/questionDel
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperQuestionId | Integer | 试卷试题id  | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题清空：paper/questionClear
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|chapterId | Integer | 章节id  | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题设置分值：paper/scoreUpdate
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperQuestionId | Integer | 试题试卷id  | 是 |
|score | double | 分值 | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题设置分数选项：paper/scoreOptionsUpdate
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperQuestionId | Integer | 试题试卷id  | 是 |
|scoreOptions | Integer[] | 选项 | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题批量设置分数：paper/batchScoreUpdate
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|chapterId | Integer | 章节id  | 是 |
|score | double | 分数 | 是 |
|options | String | 选项 | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题上移：paper/questionUp
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperQuestionId | Integer | 试卷试题id  | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题下移：paper/questionDown
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|paperQuestionId | Integer | 试卷试题id  | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####试卷试题发布：paper/publish
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
| id | Integer | 试卷id  | 是 |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####考试列表：exam/listpage
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].name | String  | 名称 |

####考试添加：exam/add
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|name      | String (16)  | 名称   |   是     |
|startTime      | Date  | 考试开始时间 |   是     |
|endTime      | Date  | 考试结束时间 |   是     |
|markStartTime      | Date  | 阅卷开始时间 |   是     |
|markEndTime      | Date  | 阅卷结束时间 |   是     |
|scoreState      | Integer  | 成绩状态：1：公开；2：不公开  |   是     |
|rankState      | Integer  | 排名状态：1：公开；2：不公开  |   是     |
|paperId      | Integer  | 试卷ID   |   是     |
|examTypeId      | Integer  | 考试分类ID   |   是     |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####考试修改：exam/edit
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id      | Integer  | 主键   |   是     |
|其他字段参考exam/add      |   |    |       | |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####考试删除：exam/del
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |


####考试更新考试用户：exam/updateExamUser
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|userIds    | Integer[]  |  考试用户IDS |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####考试更新判卷用户：exam/updateMarkUser
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
|markUserIds    | Integer[]     |   阅卷用户IDS   |   是   |
|examUserIds    | Integer[]     |   考试用户IDS   |   否   |
|questionIds    | Integer[]     |   试题IDS  |   否  |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |

####考试用户列表：exam/userList
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |


| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.id     | Integer  | 主键 |
|data.name     | String  | 名称 |

####考试试题列表：exam/questionList
######请求参数
| 参数     |  数据类型   |  描述  |  是否必填 |
| --------   | -----   | -----  | ---- |
|id    | Integer     |   主键   |   是   |
######响应数据
| 参数  |  数据类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.id     | Integer  | 主键 |
|data.name     | String  | 名称 |