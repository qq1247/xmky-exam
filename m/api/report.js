import request from '@/request/request';

export function reportExamStatis(parm) {
	return request.post('/report/exam/statis', parm);
}
export function reportExamRankListpage(parm) {
	return request.post('/report/exam/rankListpage', parm);
}
export function reportExerTrackListpage(parm) {
	return request.post('/report/exer/trackListpage', parm);
}
export function reportExerWrongQuestionListpage(parm) {
	return request.post('/report/exer/wrongQuestionListpage', parm);
}
