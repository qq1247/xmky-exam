import request from '@/request/request';

export function reportExamStatis(parm) {
	return request.post('/report/exam/statis', parm);
}
export function reportExamRankListpage(parm) {
	return request.post('/report/exam/rank-listpage', parm);
}
export function reportExerTrackListpage(parm) {
	return request.post('/report/exer/track-listpage', parm);
}
export function reportExerWrongQuestionListpage(parm) {
	return request.post('/report/exer/wrong-question-listpage', parm);
}