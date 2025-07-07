// 考试接口
export interface Exam {
    id?: number | null // 考试ID
    name: ''// 考试名称
    paperName: ''// 试卷名称
    genType: number | null // 组卷方式（1：人工组卷；2：随机组卷）
    markType: number | null // 阅卷类型（1：客观题；2：主观题）
    loginType: number | null // 登录方式（1：正常登录；2：免登录；）
    startTime: string // 考试开始时间
    endTime: string // 考试结束时间
    limitMinute: number | null // 限制分钟
    markStartTime: string // 阅卷开始时间
    markEndTime: string // 阅卷结束时间
    markState: number | null // 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
    scoreState: number | null // 成绩查询状态（1：考试结束后；2：不公布；3：交卷后）
    rankState: number | null // 排名状态（1：公布；2：不公布）
    passScore: number | null // 及格分数
    totalScore: number | null // 总分数
    sxes: number[] // 防作弊
    userNum: number | null // 考试用户数量
    state: number | null  // 状态（1：发布；2：暂停；）
}

// 试卷接口
export interface ExamQuestion {
    type: number | null// 类型 （1：章节；2：试题）
    no?: number | null// 序号
    chapterName?: string // 章节名称（type === 1有效）
    chapterTxt?: string // 章节描述（type === 1有效）
    questionId?: number | null// 试题编号（题库选择有；在线导入没有，后台为先保存题在使用）
    questionType?: number | null// 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
    markType?: number | null// （阅卷类型 1：客观题；2：主观题）
    title?: string// 标题
    imgFileIds?: number[]// 图片附件IDS
    videoFileId?: number | null// 视频附件ID
    options?: string[]// 选项
    markOptions?: number[] //（2：答案无顺序；3：不区分大小写；)
    score?: number | null// 分数
    answers?: string[]// 答案
    scores?: number[]// 答案分数
    userAnswers?: string[]// 用户答案
    userScore?: number | null// 用户分数
    analysis?: string// 解析
    [key: string]: any // 扩展字段
}

// 抽题规则接口
export interface ExamRule {
    type: number // 类型 （1：章节；2：试题）
    no?: number // 序号
    chapterName?: string // 章节名称（type === 1有效）
    chapterTxt?: string // 章节描述（type === 1有效）
    questionBankId?: number // 题库ID
    questionType?: number // 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
    markType?: number// （阅卷类型 1：客观题；2：主观题）
    markOptions?: number[] //（2：答案无顺序；3：不区分大小写；)
    num?: number // 题数
    score?: number // 分数
    scores?: number // 子分数，用于表示多选漏选分值，客观填空问答每空分值
}