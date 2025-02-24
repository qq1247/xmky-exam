import request from '@/request'

export function myMarkListpage(parm: object) {
    return request.post('/myMark/listpage', parm);
}
export function myMarkQuestionStatis(parm: object) {
    return request.post('/myMark/questionStatis', parm);
}
export function myMarkMarkList(parm: object) {
    return request.post('/myMark/markList', parm);
}
export function myMarkClaimInfo(parm: object) {
    return request.post('/myMark/claimInfo', parm);
}
export function myMarkClaim(parm: object) {
    return request.post('/myMark/claim', parm);
}
export function myMarkPaper(parm: object) {
    return request.post('/myMark/paper', parm);
}
export function myMarkGet(parm: object) {
    return request.post('/myMark/get', parm);
}
export function myMarkScore(parm: object) {
    return request.post('/myMark/score', parm);
}
export function myMarkFinish(parm: object) {
    return request.post('/myMark/finish', parm);
}