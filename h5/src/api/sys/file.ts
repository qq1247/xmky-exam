import request from '@/request'
import type { AxiosRequestConfig } from 'axios';

export function fileUpload(data?: object | undefined, config?: AxiosRequestConfig<any> | undefined) {
    return request.post('/file/upload', data, config);
}

