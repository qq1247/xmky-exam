export interface Question {
    id?: number | null // 试题ID
    type: number// 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
    title: string// 标题
    options: string[]// 选项
    markType: number// （阅卷类型 1：客观题；2：主观题）
    markOptions: number[] //（2：答案无顺序；3：不区分大小写；)
    score: number// 分数
    answers: string[]// 答案
    scores: number[]// 答案分数（单选判断主观问答，只有第0项有效）
    analysis: string// 解析
    questionBankId?: number | null // 题库ID
    [key: string]: any; // 扩展字段
}
