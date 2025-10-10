import request from '@/request/request';

export function questionListpage(parm) {
	return request.post('/question/listpage', parm);
}
export function questionGet(parm) {
	return request.post('/question/get', parm);
}
