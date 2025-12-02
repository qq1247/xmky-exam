import request from '@/request'

export function registUserListpage(parm: object) {
    return request.post('/regist-user/listpage', parm);
}
export function registUserGet(parm: object) {
    return request.post('/regist-user/get', parm);
}
export function registUserApprove(parm: object) {
    return request.post('/regist-user/approve', parm);
}
export function registUserReject(parm: object) {
    return request.post('/regist-user/reject', parm);
}


