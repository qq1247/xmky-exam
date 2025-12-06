import request from '@/request'

export function exerListpage(parm: object) {
	return request.post('/exer/listpage', parm);
}