import request from '@/request/request'

export function myExerListpage(parm) {
	return request.post('myExer/listpage', parm);
}
export function myExerAdd(parm) {
	return request.post('myExer/add', parm);
}
export function myExerGet(parm) {
	return request.post('myExer/get', parm);
}
export function myExerQuestionList(parm) {
	return request.post('myExer/questionList', parm);
}
export function myExerQuestion(parm) {
	return request.post('myExer/question', parm);
}
export function myExerAnswer(parm) {
	return request.post('myExer/answer', parm);
}
export function myExerQuestionFav(parm) {
	return request.post('myExer/questionFav', parm);
}
export function myExerFavQuestionList(parm) {
	return request.post('myExer/favQuestionList', parm);
}
export function myExerWrongQuestionReset(parm) {
	return request.post('myExer/wrongQuestionReset', parm);
}
export function myExerWrongQuestionList(parm) {
	return request.post('myExer/wrongQuestionList', parm);
}
export function myExerTrackList(parm) {
	return request.post('myExer/trackList', parm);
}
export function myExerTrack(parm) {
	return request.post('myExer/track', parm);
}