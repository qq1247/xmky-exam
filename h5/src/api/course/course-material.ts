import request from '@/request'

export function courseMaterialListpage(parm: object) {
    return request.post('/course-material/listpage', parm);
}
export function courseMaterialAdd(parm: object) {
    return request.post('/course-material/add', parm);
}
export function courseMaterialEdit(parm: object) {
    return request.post('/course-material/edit', parm);
}
export function courseMaterialDel(parm: object) {
    return request.post('/course-material/del', parm);
}
export function courseMaterialGet(parm: object) {
    return request.post('/course-material/get', parm);
}