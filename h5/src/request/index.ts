import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import qs from 'qs'
import router from '@/router'

// http请求
const http = axios.create({
    baseURL: (window as any).domain.url,
    timeout: 6000,
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function (data, headers) {
        if (headers['Content-Type'] === 'application/json') return data
        if (typeof data === 'string') return data;
        return qs.stringify(data, { arrayFormat: 'repeat' })
    }],
})

// 请求拦截器
const userStore = useUserStore()
http.interceptors.request.use(config => {
    if (userStore.accessToken) {
        config.headers.Authorization = userStore.accessToken
    }

    return config;
}, function (err) {
    return Promise.reject(err);
})

// 响应拦截器
let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []
function onAccessTokenFetched(accessToken: string) {
    refreshSubscribers.forEach(callback => callback(accessToken))
    refreshSubscribers = []
}
function addRefreshSubscriber(callback: (token: string) => void) {
    refreshSubscribers.push(callback)
}
http.interceptors.response.use(response => {
    if (response.config.responseType === 'blob') { // 下载附件不要提示
        return response;
    }
    if (response.data.code !== 200) {// 成功静默，失败反馈
        ElMessage.error(`${response.data.msg}`)
    }
    return response
}, async error => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
        if (!userStore.refreshToken) {
            userStore.reset()
            router.replace('/login')
            ElMessage.error('登录已过期，请重新登录..')
            return Promise.reject(error)
        }

        if (isRefreshing) {
            return new Promise(resolve => {
                addRefreshSubscriber((accessToken: string) => {
                    originalRequest.headers.Authorization = accessToken
                    resolve(http(originalRequest))
                })
            })
        }

        originalRequest._retry = true
        isRefreshing = true

        try {
            const response = await http.post(`/login/refresh`, { refreshToken: userStore.refreshToken })

            if (response.data.code !== 200) throw new Error(response.data.msg)

            userStore.accessToken = response.data.data.accessToken
            onAccessTokenFetched(userStore.accessToken)
            originalRequest.headers.Authorization = userStore.accessToken
            return http(originalRequest)
        } catch (err) {
            userStore.reset()
            router.replace('/login')
            return Promise.reject(err)
        } finally {
            isRefreshing = false
        }
    }

    if (error.response?.status === 401 || error.response?.status === 403) {
        ElMessage.error(error.response.data.msg)
    } else if (error.code === 'ECONNABORTED') {
        ElMessage.error(`请求服务器超时：${error.config.timeout / 1000}秒`)
    } else if (error.code === 'ERR_NETWORK') {
        ElMessage.error('连接服务器失败')
    } else {
        ElMessage.error(`未知错误：${error}`)
    }
    return Promise.reject(error);
})

export default http
