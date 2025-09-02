import request from "@/request"

export function loginIn(parm: object) {
    return request.post('/login/in', parm);
}
export function loginParm(parm: object) {
    return request.post('/login/parm', parm);
}
export function loginSysTime(parm: object) {
    return request.post('/login/sysTime', parm);
}
export function loginPwd(parm: object) {
    return request.post('/login/pwd', parm);
}
export function loginNoLogin(parm: object) {
    return request.post('/login/noLogin', parm);
}
