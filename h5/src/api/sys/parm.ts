import request from '@/request'

export function parmGet(parm: object) {
    return request.post('/parm/get', parm);
}
export function parmPwd(parm: object) {
    return request.post('/parm/pwd', parm);
}
export function parmEnt(parm: object) {
    return request.post('/parm/ent', parm);
}
export function parmM(parm: object) {
    return request.post('/parm/m', parm);
}
export function parmCustom(parm: object) {
    return request.post('/parm/custom', parm);
}

