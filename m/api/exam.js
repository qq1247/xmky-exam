import request from '@/request/request';

export function examListpage(parm) {
	return request.post('/exam/listpage', parm);
}
export function examGet(parm) {
	return request.post('exam/get', parm);
}

export function examExamGet(parm) {
	return request.post('exam/examGet', parm);
}
