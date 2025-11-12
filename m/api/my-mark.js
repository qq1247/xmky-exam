import request from '@/request/request';

export function myMarkListpage(parm) {
	return request.post('/my-mark/listpage', parm);
}
export function myMarkPaper(parm) {
	return request.post('/my-mark/paper', parm);
}
export function myMarkQuestionStatis(parm) {
	return request.post('/my-mark/question-statis', parm);
}
export function myMarkClaimInfo(parm) {
	return request.post('/my-mark/claimInfo', parm);
}
export function myMarkClaim(parm) {
	return request.post('/my-mark/claim', parm);
}
export function myMarkMarkList(parm) {
	return request.post('/my-mark/mark-list', parm);
}
export function myMarkScore(parm) {
	return request.post('/my-mark/score', parm);
}
export function myMarkFinish(parm) {
	return request.post('/my-mark/finish', parm);
}
