import request from '@/request/request';

export function myExamListpage(parm: object) {
	return request.post('/my-exam/listpage', parm);
}
export function myExamExamGet(parm: object) {
	return request.post('/my-exam/exam-get', parm);
}
export function myExamGet(parm: object) {
	return request.post('/my-exam/get', parm);
}
export function myExamPaper(parm: object) {
	return request.post('/my-exam/paper', parm);
}
export function myExamAnswer(parm: object) {
	return request.post('/my-exam/answer', parm);
}
export function myExamFinish(parm: object) {
	return request.post('/my-exam/finish', parm);
}
export function myExamQuestionStatis(parm: object) {
	return request.post('/my-exam/question-statis', parm);
}
export function myExamGeneratePaper(parm: object) {
	return request.post('/my-exam/generate-paper', parm);
}
export function myExamRetake(parm: object) {
	return request.post('/my-exam/retake', parm);
}
