import request from '@/request'

export function myExerListpage(parm: object) {
    return request.post('/myExer/listpage', parm);
}
export function myExerAdd(parm: object) {
    return request.post('/myExer/add', parm);
}
export function myExerGet(parm: object) {
    return request.post('/myExer/get', parm);
}
export function myExerQuestionList(parm: object) {
    return request.post('/myExer/questionList', parm);
}
export function myExerQuestion(parm: object) {
    return request.post('/myExer/question', parm);
}
export function myExerAnswer(parm: object) {
    return request.post('/myExer/answer', parm);
}
export function myExerQuestionFav(parm: object) {
    return request.post('/myExer/questionFav', parm);
}
export function myExerWrongQuestionReset(parm: object) {
    return request.post('/myExer/wrongQuestionReset', parm);
}
export function myExerTrack(parm: object) {
    return request.post('/myExer/track', parm);
}
export function myExerTrackList(parm: object) {
    return request.post('/myExer/trackList', parm);
}
export function myExerFavQuestionList(parm: object) {
    return request.post('/myExer/favQuestionList', parm);
}
export function myExerWrongQuestionList(parm: object) {
    return request.post('/myExer/wrongQuestionList', parm);
}