import request from "@/request"

export function cronListpage(parm: object) {
    return request.post('/cron/listpage', parm);
}
export function cronAdd(parm: object) {
    return request.post('/cron/add', parm);
}
export function cronEdit(parm: object) {
    return request.post('/cron/edit', parm);
}
export function cronDel(parm: object) {
    return request.post('/cron/del', parm);
}
export function cronGet(parm: object) {
    return request.post('/cron/get', parm);
}
export function cronStartTask(parm: object) {
    return request.post('/cron/startTask', parm);
}
export function cronStopTask(parm: object) {
    return request.post('/cron/stopTask', parm);
}
export function cronRunOnceTask(parm: object) {
    return request.post('/cron/runOnceTask', parm);
}
