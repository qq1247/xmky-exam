import request from '@/request/request';

export function questionBankListpage(parm) {
	return request.post('/question-bank/listpage', parm);
}
export function questionBankGet(parm) {
	return request.post('/question-bank/get', parm);
}
