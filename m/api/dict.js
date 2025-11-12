import request from '@/request/request'

export function dictIndexList(parm) {
	return request.post('/dict/index-list', parm);
}