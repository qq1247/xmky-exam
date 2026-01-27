import request from '@/request';

export function courseListpage(parm: object) {
	return request.post('/course/listpage', parm);
}
export function courseAdd(parm: object) {
	return request.post('/course/add', parm);
}
export function courseEdit(parm: object) {
	return request.post('/course/edit', parm);
}
export function courseDel(parm: object) {
	return request.post('/course/del', parm);
}
export function courseGet(parm: object) {
	return request.post('/course/get', parm);
}
export function courseState(parm: object) {
	return request.post('/course/state', parm);
}
export function courseShare(parm: object) {
	return request.post('/course/share', parm);
}
