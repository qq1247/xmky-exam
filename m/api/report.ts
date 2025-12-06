import request from '@/request';

export function reportExamStatis(parm: object) {
	return request.post('/report/exam/statis', parm);
}
export function reportExamRankListpage(parm: object) {
	return request.post('/report/exam/rank-listpage', parm);
}
export function reportExerTrackListpage(parm: object) {
	return request.post('/report/exer/track-listpage', parm);
}
export function reportExerWrongQuestionListpage(parm: object) {
	return request.post('/report/exer/wrong-question-listpage', parm);
}