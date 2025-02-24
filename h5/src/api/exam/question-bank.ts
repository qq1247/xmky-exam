import request from '@/request'

export function questionBankListpage(parm: object) {
    return request.post('/questionBank/listpage', parm);
}
export function questionBankAdd(parm: object) {
    return request.post('/questionBank/add', parm);
}
export function questionBankEdit(parm: object) {
    return request.post('/questionBank/edit', parm);
}
export function questionBankDel(parm: object) {
    return request.post('/questionBank/del', parm);
}
export function questionBankGet(parm: object) {
    return request.post('/questionBank/get', parm);
}
export function questionBankClear(parm: object) {
    return request.post('/questionBank/clear', parm);
}