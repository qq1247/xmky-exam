/**
 * 题干接口
 * 用于填空，按下划线拆分为文字和输入框
 */
export interface Title {
	type : String // 文本还是输入框txt、input
	index : number // 那个位置
	value : string // 内容
}

// 考试试题接口
export interface ExamQuestion {
	type : number // 类型 （1：章节；2：试题）
	no ?: number // 题号
	chapterName ?: string // 章节名称（type === 1有效）
	chapterTxt ?: string // 章节描述（type === 1有效）
	questionId ?: number// 试题编号（题库选择有；文本导入没有，后台为先保存题在使用）
	questionType ?: number// 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
	markType ?: number// （阅卷类型 1：客观题；2：主观题）
	title ?: string// 标题
	options ?: string[]// 选项
	markOptions ?: number[] //（2：答案无顺序；3：不区分大小写；)
	score ?: number// 分数
	answers ?: string[]// 答案
	scores ?: number[]// 答案分数
	userAnswers ?: string[]// 用户答案
	userScore ?: number// 用户分数
	analysis ?: string// 解析
}

// 试题接口
export interface Question {
	no ?: number // 题号
	id : number// 试题编号（题库选择有；文本导入没有，后台为先保存题在使用）
	type : number// 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
	markType : number// （阅卷类型 1：客观题；2：主观题）
	title : string// 标题
	options : string[]// 选项
	markOptions : number[] //（2：答案无顺序；3：不区分大小写；)
	score : number// 分数
	answers : string[]// 答案
	scores : number[]// 答案分数
	userAnswers ?: string[]// 用户答案
	userScore ?: number// 用户分数
	analysis : string// 解析
}

// 公告接口
export interface Bulletin {
	id : number// ID
	title : string// 标题
	content : string// 内容
	startTime : string// 开始时间
	endTime : string// 结束时间
}

// 我的考试
export interface MyExam {
	examId: number // 考试ID
	examName : string // 考试名称
	examStartTime : string// 考试开始时间
	examEndTime : string// 考试结束时间
	totalScore : string// 用户得分
	examTotalScore : string// 考试总分
	no : number// 考试排名
	userNum : number// 考试人数
	state: number // 考试状态
	markState: number // 阅卷状态
}

// 我的练习
export interface MyExer {
	id : number // 练习ID
	name : string // 练习名称
	startTime : string// 开始时间
	endTime : string// 结束时间
}