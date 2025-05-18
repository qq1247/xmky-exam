import request from '@/request'

export function examListpage(parm: object) {
    return request.post('/exam/listpage', parm);
}
export function examAdd(parm: object) {
    return request.post('/exam/add', parm);
}
export function examEdit(parm: object) {
    return request.post('/exam/edit', parm);
}
export function examDel(parm: object) {
    return request.post('/exam/del', parm);
}
export function examGet(parm: object) {
    return request.post('/exam/get', parm);
}
export function examCopy(parm: object) {
    return request.post('/exam/copy', parm);
}
export function examPublish(parm: object) {
    return request.post('/exam/publish', parm);
}
export function examPause(parm: object) {
    return request.post('/exam/pause', parm);
}
export function examTime(parm: object) {
    return request.post('/exam/time', parm);
}
export function examScore(parm: object) {
    return request.post('/exam/score', parm);
}
export function examRank(parm: object) {
    return request.post('/exam/rank', parm);
}
export function examMarkUserList(parm: object) {
    return request.post('/exam/markUserList', parm);
}
export function examAssist(parm: object) {
    return request.post('/exam/assist', parm);
}
export function examPaper(parm: object) {
    return request.post('/exam/paper', parm);
}
export function examUserAdd(parm: object) {
    return request.post('/exam/userAdd', parm);
}
export function examExamGet(parm: object) {
    return request.post('/exam/examGet', parm);
}
export function examUser(parm: object) {
    return request.post('/exam/user', parm);
}