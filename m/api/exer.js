import request from '@/request/request'

export function exerListpage(parm) {
	return request.post('exer/listpage', parm);
}