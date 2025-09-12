import request from '@/request'

export function myExerListpage(parm: object) {
    return request.post('/myExer/listpage', parm);
}
export function myExerQuestionStatis(parm: object) {
    return request.post('/myExer/questionStatis', parm);
}
export function myExerExerGet(parm: object) {
    return request.post('/myExer/exerGet', parm);
}
export function myExerPull(parm: object) {
    return request.post('/myExer/pull', parm);
}
export function myExerGenerate(parm: object) {
    return request.post('/myExer/generate', parm);
}
export function myExerQuestion(parm: object) {
    return request.post('/myExer/question', parm);
}
export function myExerAnswer(parm: object) {
    return request.post('/myExer/answer', parm);
}
export function myExerExerReset(parm: object) {
    return request.post('/myExer/exerReset', parm);
}
export function myExerFav(parm: object) {
    return request.post('/myExer/fav', parm);
}
export function myExerWrongReset(parm: object) {
    return request.post('/myExer/wrongReset', parm);
}
export function myExerTrack(parm: object) {
    return request.post('/myExer/track', parm);
}
export function myExerTrackList(parm: object) {
    return request.post('/myExer/trackList', parm);
}