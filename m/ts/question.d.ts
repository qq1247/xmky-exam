/**
 * 题干接口
 * 用于填空，按下划线拆分为文字和输入框
 */
export interface Title {
	type: String; // 文本还是输入框（txt、input）
	index: number; // 位置
	value: string; // 内容
}

// 试题接口
export interface Question {
	no?: number; // 题号
	id: number; // 试题编号（题库选择有；文本导入没有，后台为先保存题在使用）
	type: number; // 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
	markType: number; // （阅卷类型 1：客观题；2：主观题）
	title: string; // 标题
	options: string[]; // 选项
	markOptions: number[]; //（2：答案无顺序；3：不区分大小写；)
	score: number; // 分数
	answers: string[]; // 答案
	scores: number[]; // 答案分数
	userAnswers?: string[]; // 用户答案
	userScore?: number; // 用户分数
	analysis: string; // 解析
}
