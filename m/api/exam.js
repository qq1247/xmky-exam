import request from '@/request/request';

export function examGet(parm) {
	return request.post('exam/get', parm);
}
