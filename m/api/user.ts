import request from '@/request';

export function userListpage(parm: object) {
	return request.post('/user/listpage', parm);
}
export function userGet(parm: object) {
	return request.post('/user/get', parm);
}
