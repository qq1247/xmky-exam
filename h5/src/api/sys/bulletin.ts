import request from '@/request'

export function bulletinListpage(parm: object) {
    return request.post('/bulletin/listpage', parm);
}
export function bulletinAdd(parm: object) {
    return request.post('/bulletin/add', parm);
}
export function bulletinEdit(parm: object) {
    return request.post('/bulletin/edit', parm);
}
export function bulletinDel(parm: object) {
    return request.post('/bulletin/del', parm);
}
export function bulletinGet(parm: object) {
    return request.post('/bulletin/get', parm);
}