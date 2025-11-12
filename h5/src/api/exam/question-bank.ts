import request from '@/request'

export function questionBankListpage(parm: object) {
    return request.post('/question-bank/listpage', parm);
}
export function questionBankAdd(parm: object) {
    return request.post('/question-bank/add', parm);
}
export function questionBankEdit(parm: object) {
    return request.post('/question-bank/edit', parm);
}
export function questionBankDel(parm: object) {
    return request.post('/question-bank/del', parm);
}
export function questionBankGet(parm: object) {
    return request.post('/question-bank/get', parm);
}
export function questionBankClear(parm: object) {
    return request.post('/question-bank/clear', parm);
}