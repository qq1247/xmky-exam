import request from '@/request/request'

export function loginIn(parm) {
	return request.post('/login/in', parm);
}
export function loginTempIn(parm) {
	return request.post('/login/temp-in', parm);
}
export function loginPwd(parm) {
	return request.post('/login/pwd', parm);
}
export function loginParm(parm) {
	return request.post('/login/parm', parm);
}
export function loginSysTime(parm) {
	return request.post('/login/sys-time', parm);
}
export function loginOut(parm) {
	return request.post('/login/out', parm);
}
export function loginEncrypt(parm) {
	return request.post('/login/encrypt', parm);
}

