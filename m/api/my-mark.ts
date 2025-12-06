import request from '@/request';

export function myMarkListpage(parm: object) {
	return request.post('/my-mark/listpage', parm);
}
export function myMarkPaper(parm: object) {
	return request.post('/my-mark/paper', parm);
}
export function myMarkQuestionStatis(parm: object) {
	return request.post('/my-mark/question-statis', parm);
}
export function myMarkClaimInfo(parm: object) {
	return request.post('/my-mark/claim-info', parm);
}
export function myMarkClaim(parm: object) {
	return request.post('/my-mark/claim', parm);
}
export function myMarkMarkList(parm: object) {
	return request.post('/my-mark/mark-list', parm);
}
export function myMarkScore(parm: object) {
	return request.post('/my-mark/score', parm);
}
export function myMarkFinish(parm: object) {
	return request.post('/my-mark/finish', parm);
}
export function myMarkExamGet(parm: object) {
	return request.post('/my-mark/exam-get', parm);
}
