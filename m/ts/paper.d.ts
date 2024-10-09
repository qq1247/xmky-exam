// 试卷统计接口
export interface PaperStatis {
	typeStatis: TypeStatis[];
	markTypeStatis: MarkTypeStatis;
}
// 试题类型统计接口
export interface TypeStatis {
	name: String; // 名称
	value: string; // 数量
}
// 阅卷类型统计接口
export interface MarkTypeStatis {
	subjective: number;
	objective: number;
}

// 考试试题接口
export interface ExamQuestion {
	type: number; // 类型 （1：章节；2：试题）
	no?: number; // 题号
	chapterName?: string; // 章节名称（type === 1有效）
	chapterTxt?: string; // 章节描述（type === 1有效）
	questionId?: number; // 试题编号（题库选择有；文本导入没有，后台为先保存题在使用）
	questionType?: number; // 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
	markType?: number; // （阅卷类型 1：客观题；2：主观题）
	title?: string; // 标题
	options?: string[]; // 选项
	markOptions?: number[]; //（2：答案无顺序；3：不区分大小写；)
	score?: number; // 分数
	answers?: string[]; // 答案
	scores?: number[]; // 答案分数
	userAnswers?: string[]; // 用户答案
	userScore?: number; // 用户分数
	analysis?: string; // 解析
}
