import request from '@/request/request';

export function myExamListpage(parm) {
	return request.post('myExam/listpage', parm);
}
export function myExamExamGet(parm) {
	return request.post('myExam/examGet', parm);
}
export function myExamGet(parm) {
	return request.post('myExam/get', parm);
}
export function myExamPaper(parm) {
	return request.post('myExam/paper', parm);
}
export function myExamAnswer(parm) {
	return request.post('myExam/answer', parm);
}
export function myExamFinish(parm) {
	return request.post('myExam/finish', parm);
}
export function myExamQuestionStatis(parm) {
	return request.post('myExam/questionStatis', parm);
}
export function myExamGeneratePaper(parm) {
	return request.post('myExam/generatePaper', parm);
}
export function myExamRetake(parm) {
	return request.post('myExam/retake', parm);
}
