import request from '@/request/request'

export function myExerListpage(parm) {
	return request.post('/my-exer/listpage', parm);
}
export function myExerAdd(parm) {
	return request.post('/my-exer/add', parm);
}
export function myExerGet(parm) {
	return request.post('/my-exer/get', parm);
}
export function myExerQuestionList(parm) {
	return request.post('/my-exer/question-list', parm);
}
export function myExerQuestion(parm) {
	return request.post('/my-exer/question', parm);
}
export function myExerAnswer(parm) {
	return request.post('/my-exer/answer', parm);
}
export function myExerQuestionFav(parm) {
	return request.post('/my-exer/question-fav', parm);
}
export function myExerFavQuestionList(parm) {
	return request.post('/my-exer/fav-question-list', parm);
}
export function myExerWrongQuestionReset(parm) {
	return request.post('/my-exer/wrong-question-reset', parm);
}
export function myExerWrongQuestionList(parm) {
	return request.post('/my-exer/wrong-question-list', parm);
}
export function myExerTrackList(parm) {
	return request.post('/my-exer/track-list', parm);
}
export function myExerTrack(parm) {
	return request.post('/my-exer/track', parm);
}