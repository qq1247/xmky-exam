import request from '@/request'

export function questionListpage(parm: object) {
    return request.post('/question/listpage', parm);
}
export function questionAdd(parm: object) {
    return request.post('/question/add', parm);
}
export function questionEdit(parm: object) {
    return request.post('/question/edit', parm);
}
export function questionDel(parm: object) {
    return request.post('/question/del', parm);
}
export function questionGet(parm: object) {
    return request.post('/question/get', parm);
}
export function questionCopy(parm: object) {
    return request.post('/question/copy', parm);
}