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
除api/login/*接口外，http请求头需添加Authorization字段，
值为api/login/in的响应数据data.accessToken的值。
如果某次请求，http响应头有Authorization字段，请缓存该值，
之后的http请求头Authorization字段使用该缓存值，用于令牌续租。

##我的考试相关
####我的考试列表：myExam/listpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|examName      | String (32)  | 考试名称   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].examName | String  | 考试名次 |
|data.list[].examStartTime | Date  | 考试开始时间 |
|data.list[].examEndTime | Date  | 考试结束时间 |
|data.list[].userId | Date  | 考试用户ID |
|data.list[].userName | Date  | 考试用户名称 |
|data.list[].answerStartTime | Date  | 答题开始时间 |
|data.list[].answerEndTime | Date  | 答题结束时间 |
|data.list[].markUserId | Integer  | 阅卷人ID |
|data.list[].markUserName | String  | 阅卷人名称 |
|data.list[].markStartTime | Date  | 阅卷开始时间 |
|data.list[].markEndTime | Date  | 阅卷结束时间 |
|data.list[].paperTotalScore | Double  | 试卷总分 |
|data.list[].totalScore | Double  | 我的得分 |
|data.list[].state | Integer  | 考试状态（1：未考试；2：考试中；3：已交卷；4：强制交卷；） |
|data.list[].stateName | String  | 考试状态 |
|data.list[].markState | Integer  | 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；） |
|data.list[].markStateName | String  | 阅卷状态 |
|data.list[].answerState | Integer  | 答题状态（1：及格；2：不及格） |
|data.list[].answerStateName | String  | 答题状态 |

####我的考试更新答案：myExam/updateAnswer
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|myExamDetailId      | Integer  | 我的考试详细ID   |   是     |
|answer      | text  | 答案  |   是     |

####我的考试交卷：myExam/doAnswer
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|myExamId      | Integer | 我的考试ID |   是     |

####我的阅卷考试列表：myMark/examListpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|examName      | String (32)  | 考试名称   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data.total     | Integer  | 总行数 |
|data.list[]      | arr[]  | 分页列表 |
|data.list[].id   | Integer  | 主键 |
|data.list[].examName | String  | 考试名次 |
|data.list[].examStartTime | Date  | 考试开始时间 |
|data.list[].examEndTime | Date  | 考试结束时间 |
|data.list[].paperPassScore | Double  | 试卷及格分数 |
|data.list[].paperTotalScore | Date  | 试卷总分 |
|data.list[].userNum | String  | 考试人数 |

####我的阅卷列表：myMark/listpage
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|examName      | String (32)  | 考试名称   |   否     |
|curPage      | Integer | 当前第几页  |   否     |
|pageSize      | Integer  | 每页多少条   |   否     |
######响应数据
同myExam/listpage

####我的阅卷更新分数：myMark/updateScore
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|myExamDetailId      | Integer  | 我的考试详细ID   |   是     |
|score      | Double  | 得分  |   是     |

####我的阅卷阅卷完成：myMark/doScore
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|myExamId      | Integer | 我的考试ID |   是     |

####我的阅卷自动阅卷：myMark/autoScore
######请求参数
| 参数     |  类型   |  描述  |  必填 |
| --------   | -----   | -----  | ---- |
|myExamId      | Integer | 我的考试ID |   是     |
######响应数据
| 参数  |  类型   |  描述  |
| --------   | -----   | -----  |
|code     | Integer  | 响应码 |
|msg     | String  | 响应消息 |
|data     | String  | 进度条ID |




