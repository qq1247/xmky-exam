import request from "@/request"

export function dictListpage(parm: object) {
    return request.post('/dict/listpage', parm);
}
export function dictAdd(parm: object) {
    return request.post('/dict/add', parm);
}
export function dictEdit(parm: object) {
    return request.post('/dict/edit', parm);
}
export function dictDel(parm: object) {
    return request.post('/dict/del', parm);
}
export function dictGet(parm: object) {
    return request.post('/dict/get', parm);
}
export function dictIndexList(parm: object) {
    return request.post('/dict/indexList', parm);
}