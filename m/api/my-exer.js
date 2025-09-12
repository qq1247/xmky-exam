import request from '@/request/request'

export function myExerListpage(parm) {
	return request.post('myExer/listpage', parm);
}
export function myExerExerGet(parm) {
	return request.post('myExer/exerGet', parm);
}
export function myExerPull(parm) {
	return request.post('myExer/pull', parm);
}
export function myExerQuestionStatis(parm) {
	return request.post('myExer/questionStatis', parm);
}
export function myExerExerReset(parm) {
	return request.post('myExer/exerReset', parm);
}
export function myExerGenerate(parm) {
	return request.post('myExer/generate', parm);
}
export function myExerQuestion(parm) {
	return request.post('myExer/question', parm);
}
export function myExerAnswer(parm) {
	return request.post('myExer/answer', parm);
}
export function myExerFav(parm) {
	return request.post('myExer/fav', parm);
}
export function myExerWrongReset(parm) {
	return request.post('myExer/wrongReset', parm);
}
export function myExerTrackList(parm) {
	return request.post('myExer/trackList', parm);
}
export function myExerTrack(parm) {
	return request.post('myExer/track', parm);
}