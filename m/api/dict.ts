import request from '@/request'

export function dictIndexList(parm: object) {
	return request.post('/dict/index-list', parm);
}