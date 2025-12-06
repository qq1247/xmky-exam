import request from '@/request';

export function myCommentList(parm: object) {
	return request.post('/my-comment/list', parm);
}
export function myCommentAdd(parm: object) {
	return request.post('/my-comment/add', parm);
}
export function myCommentLike(parm: object) {
	return request.post('/my-comment/like', parm);
}
export function myCommentReply(parm: object) {
	return request.post('/my-comment/reply', parm);
}
