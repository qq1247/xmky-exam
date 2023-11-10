import { useUserStore } from '@/stores/user';
import axios, { type RawAxiosRequestHeaders } from 'axios'
import { ElMessage } from 'element-plus';
import qs from 'qs'

/**
 * http请求
 * 
 * 如果请求1个接口时，浏览器调试显示发起两次请求（OPTIONS、POST|GET），是触发了预检请求
 * https://blog.csdn.net/qq_27626333/article/details/77005911
 */
const http = axios.create({
    baseURL: import.meta.env.DEV ? import.meta.env.VITE_BASE_URL : window.domain.url,
    timeout: 6000,
    transformRequest: [function (data, headers) {
        if (headers['Content-Type'] === 'application/json') {
            return data
        }
        return qs.stringify(data, { arrayFormat: 'repeat' })
    }],
})

http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'

// 请求拦截器
http.interceptors.request.use(function (config) {
    const userStore = useUserStore()
    if (userStore.accessToken) {
        (config.headers as RawAxiosRequestHeaders).Authorization = userStore.accessToken
    }

    return config;
}, function (error) {
    return Promise.reject(error);
});

// 响应拦截器
http.interceptors.response.use(
    response => {
        if (response.data.code === 401) {// 鉴权失败，跳转到登录页
            window.location.href = "/login"
        } else if (response.data.code === 500) {// 接口错误，提示错误
            ElMessage.error(response.data.msg)
        } else if (response.headers.authorization) {// 携带刷新令牌，替代当前令牌
            const userStore = useUserStore()
            userStore.accessToken = response.headers.authorization
        }

        return response;
    }, error => {
        if (error.message.substring(0, 10) === 'timeout of') {// 请求超时，提示错误（timeout of 6000ms exceeded）
            ElMessage.error(`请求超时：${error.config.timeout / 1000}秒`)
            console.error(`请求超时：${error.config.timeout / 1000}秒 ${error.config.baseURL}${error.config.url}?${error.config.data}`)
        } else if (error.message === 'Network Error') {// 网络不通，提示错误
            ElMessage.error('连接服务器失败')
            console.error(`连接服务器失败：${error.config.baseURL}${error.config.url}?${error.config.data}`)
        } else {
            ElMessage.error(error.message)// 其他错误，提示错误
            console.error(`请求未知错误：${error.config.baseURL}${error.config.url}?${error.config.data}`)
        }
        return Promise.reject(error);
    });

export default http