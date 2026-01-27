import type { MyCourseQuestion } from './my-course-question';

// 我的课程资料接口
export interface MyCourseMaterial {
	courseId: number | null; // 课程ID
	courseMaterialId: number | null; // 课程资料ID
	userId: number | null; // 用户ID
	name: string; // 名称
	content: string; // 简介
	videoFileId: number | null; // 视频附件ID
	videoTime: string; // 视频时长
	no: number | null; // 第几小节
	watchTime: string; // 观看时间
	state: number | null; // 状态（0：删除；1：完成；2：未开始；3：进行中）
	questions: MyCourseQuestion[];
}
