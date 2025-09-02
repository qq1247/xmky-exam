import request from '@/request'

export function parmGet(parm: object) {
    return request.post('/parm/get', parm);
}
export function parmPwd(parm: object) {
    return request.post('/parm/pwd', parm);
}
export function parmSys(parm: object) {
    return request.post('/parm/sys', parm);
}
export function parmM(parm: object) {
    return request.post('/parm/m', parm);
}
export function parmSupport(parm: object) {
    return request.post('/parm/support', parm);
}
export function parmIcp(parm: object) {
    return request.post('/parm/icp', parm);
}

