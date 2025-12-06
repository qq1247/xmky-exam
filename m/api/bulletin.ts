import request from '@/request'

export function bulletinListpage(parm: object) {
	return request.post('/bulletin/listpage', parm);
}
export function bulletinGet(parm: object) {
	return request.post('/bulletin/get', parm);
}