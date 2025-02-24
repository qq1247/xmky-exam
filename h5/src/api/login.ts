import request from "@/request"

export function loginIn(parm: object) {
    return request.post('/login/in', parm);
}
export function loginEnt(parm: object) {
    return request.post('/login/ent', parm);
}
export function loginCustom(parm: object) {
    return request.post('/login/custom', parm);
}
export function loginSysTime(parm: object) {
    return request.post('/login/sysTime', parm);
}
export function loginPwd(parm: object) {
    return request.post('/login/pwd', parm);
}
