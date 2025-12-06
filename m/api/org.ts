import request from '@/request';

export function orgListpage(parm: object) {
	return request.post('/org/listpage', parm);
}
export function orgGet(parm: object) {
	return request.post('/org/get', parm);
}
