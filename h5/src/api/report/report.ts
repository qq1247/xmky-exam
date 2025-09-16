import request from "@/request"

export function reportExamStatis(parm: object) {
    return request.post('/report/exam/statis', parm);
}
export function reportExamRankListpage(parm: object) {
    return request.post('/report/exam/rankListpage', parm);
}
export function reportExerTrackList(parm: object) {
    return request.post('/report/exer/trackList', parm);
}
export function reportExerWrongAnswerNumList(parm: object) {
    return request.post('/report/exer/wrongAnswerNumList', parm);
}
export function reportExerQuestionListpage(parm: object) {
    return request.post('/report/exer/questionListpage', parm);
}