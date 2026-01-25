// 课程资料接口
export interface CourseMaterial {
    id: number | null // ID
    name: string; // 名称
    content: string; // 内容
    videoFileId: number | null // 视频文件ID
    courseQuestions: CourseQuestion[] // 课程时间
    no: number // 排序
}
export interface CourseQuestion {
    courseTime: string // 课程时间（小时分秒）
    questionId: number | string; // 试题ID
}
