import request from "@/request"
import type { AxiosRequestConfig } from "axios";

export function reportExamStatis(parm: object) {
    return request.post('/report/exam/statis', parm);
}
export function reportExamRankListpage(parm: object) {
    return request.post('/report/exam/rank-listpage', parm);
}
export function reportExerTrackListpage(parm: object) {
    return request.post('/report/exer/track-listpage', parm);
}
export function reportExerWrongQuestionListpage(parm: object) {
    return request.post('/report/exer/wrong-question-listpage', parm);
}
export function reportRankExportPdf(data?: object | undefined, config?: AxiosRequestConfig<object> | undefined) {
    return request.post('/report/rank/export-pdf', data, config);
}
export function reportPaperExportPdf(data?: object | undefined, config?: AxiosRequestConfig<object> | undefined) {
    return request.post('/report/paper/export-pdf', data, config);
}