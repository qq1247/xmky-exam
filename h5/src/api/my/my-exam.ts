import request from '@/request'

export function myExamListpage(parm: object) {
    return request.post('/myExam/listpage', parm);
}
export function myExamGet(parm: object) {
    return request.post('/myExam/get', parm);
}
export function myExamExamGet(parm: object) {
    return request.post('/myExam/examGet', parm);
}
export function myExamQuestionStatis(parm: object) {
    return request.post('/myExam/questionStatis', parm);
}
export function myExamPaper(parm: object) {
    return request.post('/myExam/paper', parm);
}
export function myExamAnswer(parm: object) {
    return request.post('/myExam/answer', parm);
}
export function myExamFinish(parm: object) {
    return request.post('/myExam/finish', parm);
}
export function myExamSxe(parm: object) {
    return request.post('/myExam/sxe', parm);
}
