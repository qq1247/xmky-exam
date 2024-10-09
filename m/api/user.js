import request from '@/request/request'

export function userGet(parm) {
	return request.post('/user/get', parm);
}

