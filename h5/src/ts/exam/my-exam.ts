// 我的考试接口
export interface MyExam {
    examId: number | null; // 考试ID
    userId: number | null; // 用户ID
    answerStartTime: string; //答题开始时间（yyyy-MM-dd HH:mm:ss）
    answerEndTime: string; //答题结束时间（yyyy-MM-dd HH:mm:ss）
    markStartTime: string; //阅卷开始时间（yyyy-MM-dd HH:mm:ss）
    markEndTime: string; //阅卷结束时间
    objectiveScore: number | null; //客观题分数
    totalScore: number | null; //总分数
    state: number | null; // 状态（1：未考试；2：考试中；3：已交卷；）
    markState: number | null; //阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
    answerState: number | null; //答题状态（1：及格；2：不及格）
    no: number | null; // 排名
    ver: number | null; // 版本

}
