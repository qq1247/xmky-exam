import request from '@/request/request'

export function bulletinListpage(parm) {
	return request.post('bulletin/listpage', parm);
}
export function bulletinGet(parm) {
	return request.post('bulletin/get', parm);
}