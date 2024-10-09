import request from '@/request/request'

export function loginIn(parm) {
	return request.post('/login/in', parm);
}
export function loginPwd(parm) {
	return request.post('/login/pwd', parm);
}
export function loginEnt(parm) {
	return request.post('/login/ent', parm);
}
export function loginCustom(parm) {
	return request.post('/login/custom', parm);
}
export function loginSysTime(parm) {
	return request.post('/login/sysTime', parm);
}
export function loginOut(parm) {
	return request.post('/login/out', parm);
}

