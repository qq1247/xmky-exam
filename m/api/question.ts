import request from '@/request/request';

export function questionListpage(parm: object) {
	return request.post('/question/listpage', parm);
}
export function questionGet(parm: object) {
	return request.post('/question/get', parm);
}
