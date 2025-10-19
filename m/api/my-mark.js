import request from '@/request/request';

export function myMarkListpage(parm) {
	return request.post('/myMark/listpage', parm);
}
export function myMarkPaper(parm) {
	return request.post('/myMark/paper', parm);
}
export function myMarkQuestionStatis(parm) {
	return request.post('/myMark/questionStatis', parm);
}
export function myMarkClaimInfo(parm) {
	return request.post('/myMark/claimInfo', parm);
}
export function myMarkClaim(parm) {
	return request.post('/myMark/claim', parm);
}
export function myMarkMarkList(parm) {
	return request.post('/myMark/markList', parm);
}
export function myMarkScore(parm) {
	return request.post('/myMark/score', parm);
}
export function myMarkFinish(parm) {
	return request.post('/myMark/finish', parm);
}
