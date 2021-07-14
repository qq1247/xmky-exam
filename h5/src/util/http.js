/**
 * axios封装
 * 请求拦截、响应拦截、错误统一处理
 */
import axios from 'axios'
import router from '@/router'
import store from '@/store'
import { Message } from 'element-ui'
/**
 * 提示函数
 */
const message = (msg) => {
  Message({
    message: msg,
    duration: 3000,
    type: 'warning',
  })
}

/**
 * 跳转登录页
 * 携带当前页面路由，以期在登录页面完成登录后返回当前页面
 */
const toLogin = () => {
  router.replace({
    path: '/login',
    query: {
      redirect: router.currentRoute.fullPath,
    },
  })
}

/**
 * 根据刷新的token(headers?.Authorization)替换vuex中userInfo的accessToken
 *
 */
const replaceToken = async (token) => {
  const userInfo = JSON.parse(store.state.userInfo)
  userInfo.accessToken = token
  return JSON.stringify(userInfo)
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
      message(`${msg}`)
      break
    case 401:
    case 403:
      message(`请重新登录`)
      store.dispatch('delUserInfo')
      toLogin()
      break
    case 404:
      message('请求的资源不存在')
      break
    default:
      message(`${msg}`)
  }
}

/*
 *根据打包环境配置baseURL
 */
let baseUrl
switch (process.env.VUE_APP_MODE) {
  case 'development':
    baseUrl = 'http://192.168.110.198:8080/api/'
    break
  case 'production':
    baseUrl = 'http://prod.com/api/'
    break
  case 'test':
    baseUrl = 'http://test.com/api/'
    break
  default:
    baseUrl = 'http://192.168.110.198:8080/api/'
}

// 创建axios实例
var instance = axios.create({
  baseURL: baseUrl,
  timeout: 1000 * 1000,
})

/**
 * 请求拦截器
 * 每次请求前，如果存在token则在请求头中携带token
 */
instance.interceptors.request.use(
  (config) => {
    const userInfo = store.state.userInfo
      ? JSON.parse(store.state.userInfo)
      : ''
    userInfo?.accessToken &&
      (config.headers.Authorization = userInfo.accessToken)
    return config
  },
  (error) => Promise.error(error)
)

// 响应拦截器
instance.interceptors.response.use(
  // 请求成功
  async (res) => {
    const {
      data: { code, msg },
      headers,
      config,
    } = res
    headers?.authorization &&
      store.dispatch('setUserInfo', await replaceToken(headers.authorization))
    if (config.responseType == 'blob') {
      return Promise.resolve(res.data)
    } else {
      if (code == 200) {
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
      return Promise.reject(error)
    }
    if (response) {
      errorHandle(response.status, response.data.message)
      return Promise.reject(response)
    } else {
      message('请求错误或网站服务器异常！请联系管理员')
      return Promise.reject(error)
    }
  }
)

export default instance
