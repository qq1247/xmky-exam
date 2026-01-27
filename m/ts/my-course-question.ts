// 我的课程试题接口
export interface MyCourseQuestion {
    questionId: number | null; // 试题ID
    courseTime: string; // 课程时间
    answerTime: string; // 答题时间
    [key: string]: any; // 自定义属性
}