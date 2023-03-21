/**
 * axios封装
 * 请求拦截、响应拦截、错误统一处理
 */
import axios from 'axios'
import router from '@/router'
import store from '@/store'
import { Message, Notification } from 'element-ui'
/**
 * 提示函数
 */
const message = (msg) => {
  if (msg.length > 60) {
    Notification({
      title: '提示',
      message: msg,
      type: 'warning'
    })
  } else {
    Message({
      message: msg,
      duration: 1500,
      type: 'warning'
    })
  }
}

/**
 * 跳转登录页
 */
const toLogin = () => {
  router.replace({
    path: '/login',
    query: {
      redirect: '/'
    }
  })
}

/**
 * 请求失败后的错误统一处理
 * @param {Number} status 请求失败的状态码
 * @param {Number} msg 请求失败返回的message
 */
const errorHandle = (status, msg) => {
  // 状态码判断
  switch (status) {
    case 500:
      message(msg)
      break
    case 401:
    case 403:
      // message(`请重新登录`)
      store.dispatch('user/resetToken').then(() => {
        toLogin()
      })
      break
    case 404:
      message('请求的资源不存在')
      break
    default:
      message(msg)
  }
}

// 创建axios实例
const instance = axios.create({
  baseURL:
    process.env.NODE_ENV === 'development'
      ? process.env.VUE_APP_BASE_URL
      : window.domain.url,
  timeout: 6 * 1000
})

/**
 * 请求拦截器
 * 每次请求前，如果存在token则在请求头中携带token
 */
instance.interceptors.request.use(
  (config) => {
    const accessToken = store.state.user.token
    accessToken && (config.headers.Authorization = accessToken)
    return config
  },
  (error) => Promise.error(error)
)

// 响应拦截器
instance.interceptors.response.use(
  // 请求成功
  (res) => {
    const {
      data: { code, msg },
      headers,
      config
    } = res
    headers?.authorization &&
      store.commit('user/SET_TOKEN', headers.authorization)
    if (config.responseType === 'blob') {
      return Promise.resolve(res.data)
    } else {
      if (code === 200) {
        return Promise.resolve(res.data)
      } else {
        errorHandle(code, msg)
        return Promise.reject(res)
      }
    }
  },
  // 请求失败
  (error) => {
    const { response, config } = error
    if (
      error.code === 'ECONNABORTED' &&
      error.message.indexOf('timeout') !== -1 &&
      !config._retry
    ) {
      Message.error('请求超时')
      console.error(`请求超时：${error.config.baseURL}${error.config.url}?${error.config.data}`)
      return Promise.reject(error)
    }
    if (response) {
      errorHandle(response.status, response.data.message)
      console.error(`未知错误：${error.config.baseURL}${error.config.url}?${error.config.data}`)
      return Promise.reject(response)
    } else {
      message('连接服务器失败')
      console.error(`连接服务器失败：${error.config.baseURL}${error.config.url}?${error.config.data}`)
      return Promise.reject(error)
    }
  }
)

export default instance
