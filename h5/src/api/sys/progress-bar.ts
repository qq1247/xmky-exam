import request from '@/request'

export function progressBarGet(parm: object) {
    return request.post('/progressBar/get', parm);
}

