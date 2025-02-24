import request from '@/request'

export function cacheRefresh(parm: object) {
    return request.post('/cache/refresh', parm);
}

