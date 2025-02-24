import request from '@/request'

export function myExerListpage(parm: object) {
    return request.post('/myExer/listpage', parm);
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