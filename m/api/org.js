import request from '@/request/request';

export function orgListpage(parm) {
	return request.post('/org/listpage', parm);
}
export function orgGet(parm) {
	return request.post('/org/get', parm);
}
