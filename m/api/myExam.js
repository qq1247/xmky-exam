import request from '@/request/request';

export function myExamListpage(parm) {
	return request.post('/my-exam/listpage', parm);
}
export function myExamExamGet(parm) {
	return request.post('/my-exam/exam-get', parm);
}
export function myExamGet(parm) {
	return request.post('/my-exam/get', parm);
}
export function myExamPaper(parm) {
	return request.post('/my-exam/paper', parm);
}
export function myExamAnswer(parm) {
	return request.post('/my-exam/answer', parm);
}
export function myExamFinish(parm) {
	return request.post('/my-exam/finish', parm);
}
export function myExamQuestionStatis(parm) {
	return request.post('/my-exam/question-statis', parm);
}
export function myExamGeneratePaper(parm) {
	return request.post('/my-exam/generate-paper', parm);
}
export function myExamRetake(parm) {
	return request.post('/my-exam/retake', parm);
}
