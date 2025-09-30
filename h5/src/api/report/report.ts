import request from "@/request"

export function reportExamStatis(parm: object) {
    return request.post('/report/exam/statis', parm);
}
export function reportExamRankListpage(parm: object) {
    return request.post('/report/exam/rankListpage', parm);
}
export function reportExerTrackListpage(parm: object) {
    return request.post('/report/exer/trackListpage', parm);
}
export function reportExerWrongQuestionListpage(parm: object) {
    return request.post('/report/exer/wrongQuestionListpage', parm);
}