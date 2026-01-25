import request from '@/request'

export function myCourseListpage(parm: object) {
    return request.post('/my-course/listpage', parm);
}
export function myCourseCourseListpage(parm: object) {
    return request.post('/my-course/course-listpage', parm);
}
export function myCourseGenerate(parm: object) {
    return request.post('/my-course/generate', parm);
}
export function myCourseList(parm: object) {
    return request.post('/my-course/list', parm);
}
export function myCourseQuestion(parm: object) {
    return request.post('/my-course/question', parm);
}
export function myCourseAnswer(parm: object) {
    return request.post('/my-course/answer', parm);
}
export function myCourseFinish(parm: object) {
    return request.post('/my-course/finish', parm);
}