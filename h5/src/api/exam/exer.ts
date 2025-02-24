import request from '@/request'

export function exerListpage(parm: object) {
    return request.post('/exer/listpage', parm);
}
export function exerAdd(parm: object) {
    return request.post('/exer/add', parm);
}
export function exerEdit(parm: object) {
    return request.post('/exer/edit', parm);
}
export function exerDel(parm: object) {
    return request.post('/exer/del', parm);
}
export function exerGet(parm: object) {
    return request.post('/exer/get', parm);
}