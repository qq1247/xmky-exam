/**
 * axios封装
 * 请求拦截、响应拦截、错误统一处理
 */
import axios from "axios"
import router from "@/router"
// import store from "@/store";
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
    case 401:
    case 403:
      message(`${msg}请重新登录`)
      localStorage.removeItem("token")
      setTimeout(() => {
        toLogin()
      }, 1000)
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
  baseURL: "http://192.168.110.85:8080/api/",
  timeout: 1000 * 10
})

/**
 * 请求拦截器
 * 每次请求前，如果存在token则在请求头中携带token
 */
instance.interceptors.request.use(
  config => {
    // 登录流程控制中，根据本地是否存在token判断用户的登录情况
    // 但是即使token存在，也有可能token是过期的，所以在每次的请求头中携带token
    // 后台根据携带的token判断用户的登录情况，并返回给我们对应的状态码
    // 而后我们可以在响应拦截器中，根据状态码进行一些统一的操作。
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
    const { response } = error
    if (response) {
      // 请求已发出，但是不在2xx的范围
      errorHandle(response.status, response.data.message)
      return Promise.reject(response)
    } else {
      // 处理断网的情况
      // eg:请求超时或断网时，更新state的network状态
      // network状态在app.vue中控制着一个全局的断网提示组件的显示隐藏
      // 关于断网组件中的刷新重新获取数据，会在断网组件中说明
      if (!window.navigator.onLine) {
        // store.commit("changeNetwork", false)
      } else {
        return Promise.reject(error)
      }
    }
  }
)

export default instance
