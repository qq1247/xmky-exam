import request from '@/request/request'

export function myExerListpage(parm) {
	return request.post('myExer/listpage', parm);
}
export function myExerGet(parm) {
	return request.post('myExer/get', parm);
}
export function myExerQuestionList(parm) {
	return request.post('myExer/questionList', parm);
}
export function myExerQuestionList2(parm) {
	return request.post('myExer/questionList2', parm);
}