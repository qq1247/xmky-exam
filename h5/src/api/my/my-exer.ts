import request from '@/request'

export function myExerListpage(parm: object) {
    return request.post('/my-exer/listpage', parm);
}
export function myExerExerListpage(parm: object) {
    return request.post('/my-exer/exer-listpage', parm);
}
export function myExerAdd(parm: object) {
    return request.post('/my-exer/add', parm);
}
export function myExerGet(parm: object) {
    return request.post('/my-exer/get', parm);
}
export function myExerQuestionList(parm: object) {
    return request.post('/my-exer/question-list', parm);
}
export function myExerQuestion(parm: object) {
    return request.post('/my-exer/question', parm);
}
export function myExerAnswer(parm: object) {
    return request.post('/my-exer/answer', parm);
}
export function myExerQuestionFav(parm: object) {
    return request.post('/my-exer/question-fav', parm);
}
export function myExerWrongQuestionReset(parm: object) {
    return request.post('/my-exer/wrong-question-reset', parm);
}
export function myExerTrack(parm: object) {
    return request.post('/my-exer/track', parm);
}
export function myExerTrackList(parm: object) {
    return request.post('/my-exer/track-list', parm);
}
export function myExerFavQuestionList(parm: object) {
    return request.post('/my-exer/fav-question-list', parm);
}
export function myExerWrongQuestionList(parm: object) {
    return request.post('/my-exer/wrong-question-list', parm);
}