import request from '@/request'

export function userListpage(parm: object) {
    return request.post('/user/listpage', parm);
}
export function userAdd(parm: object) {
    return request.post('/user/add', parm);
}
export function userEdit(parm: object) {
    return request.post('/user/edit', parm);
}
export function userDel(parm: object) {
    return request.post('/user/del', parm);
}
export function userGet(parm: object) {
    return request.post('/user/get', parm);
}
export function userFrozen(parm: object) {
    return request.post('/user/frozen', parm);
}
export function userPwdInit(parm: object) {
    return request.post('/user/pwdInit', parm);
}


