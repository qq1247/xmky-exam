import request from '@/request/request'

export function loginIn(parm: object) {
	return request.post('/login/in', parm);
}
export function loginTempIn(parm: object) {
	return request.post('/login/temp-in', parm);
}
export function loginPwd(parm: object) {
	return request.post('/login/pwd', parm);
}
export function loginParm(parm: object) {
	return request.post('/login/parm', parm);
}
export function loginSysTime(parm: object) {
	return request.post('/login/sys-time', parm);
}
export function loginOut(parm: object) {
	return request.post('/login/out', parm);
}
export function loginEncrypt(parm: object) {
	return request.post('/login/encrypt', parm);
}

