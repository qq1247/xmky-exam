import request from '@/request';

export function examListpage(parm: object) {
	return request.post('/exam/listpage', parm);
}
export function examGet(parm: object) {
	return request.post('/exam/get', parm);
}

export function examExamGet(parm: object) {
	return request.post('/exam/exam-get', parm);
}
