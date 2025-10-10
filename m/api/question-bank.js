import request from '@/request/request';

export function questionBankListpage(parm) {
	return request.post('/questionBank/listpage', parm);
}
export function questionBankGet(parm) {
	return request.post('/questionBank/get', parm);
}
