import request from '@/request';

export function questionBankListpage(parm: object) {
	return request.post('/question-bank/listpage', parm);
}
export function questionBankGet(parm: object) {
	return request.post('/question-bank/get', parm);
}
