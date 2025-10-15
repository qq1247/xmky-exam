import request from '@/request/request';

export function userListpage(parm) {
	return request.post('/user/listpage', parm);
}
export function userGet(parm) {
	return request.post('/user/get', parm);
}
