import request from '@/request'
import type { AxiosRequestConfig } from 'axios';

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
    return request.post('/user/pwd-init', parm);
}
export function userImport(parm: object) {
    return request.post('user/import', parm);
}
export function userTemplate(data?: any, config?: AxiosRequestConfig<any> | undefined) {
    return request.post('user/template', data, config);
}


