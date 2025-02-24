import request from '@/request'

export function orgListpage(parm: object) {
    return request.post('/org/listpage', parm);
}

export function orgAdd(parm: object) {
    return request.post('/org/add', parm);
}
export function orgEdit(parm: object) {
    return request.post('/org/edit', parm);
}
export function orgDel(parm: object) {
    return request.post('/org/del', parm);
}
export function orgGet(parm: object) {
    return request.post('/org/get', parm);
}



