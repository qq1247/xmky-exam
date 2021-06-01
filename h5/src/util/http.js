/**
 * axios封装
 * 请求拦截、响应拦截、错误统一处理
 */
import axios from "axios"
import router from "@/router"
import { Message } from "element-ui"
/**
 * 提示函数
 */
const message = msg => {
  Message({
    message: msg,
    duration: 2000,
    type: "warning"
  })
}

/**
 * 跳转登录页
 * 携带当前页面路由，以期在登录页面完成登录后返回当前页面
 */
const toLogin = () => {
  router.replace({
    path: "/login",
    query: {
      redirect: router.currentRoute.fullPath
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
      message(`${msg}`)
      break
    case 401:
    case 403:
      message(`${msg}请重新登录`)
      localStorage.removeItem("token")
      toLogin()
      break
    case 404:
      message("请求的资源不存在")
      break
    default:
      message(msg)
  }
}

// 创建axios实例
var instance = axios.create({
  baseURL:
    process.env.NODE_ENV == "development"
      ? "http://192.168.110.86:8080/api/"
      : "http://192.168.110.86:8080/api/",
  timeout: 1000 * 3
})

/**
 * 请求拦截器
 * 每次请求前，如果存在token则在请求头中携带token
 */
instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem("token")
    token && (config.headers.Authorization = token)
    return config
  },
  error => Promise.error(error)
)

// 响应拦截器
instance.interceptors.response.use(
  // 请求成功
  res => {
    const {
      data: { code, msg },
      headers
    } = res
    if (code == 200) {
      headers?.Authorization &&
        localStorage.setItem("token", headers.Authorization)
      return Promise.resolve(res.data)
    } else {
      errorHandle(code, msg)
      return Promise.reject(res)
    }
  },
  // 请求失败
  error => {
    const { response, config } = error
    if (
      error.code === "ECONNABORTED" &&
      error.message.indexOf("timeout") !== -1 &&
      !config._retry
    ) {
      Message.error("请求超时")
      return Promise.reject(error)
    }
    if (response) {
      errorHandle(response.status, response.data.message)
      return Promise.reject(response)
    } else {
      return Promise.reject(error)
    }
  }
)

export default instance
