import Vue from "vue"
import VueRouter from "vue-router"
import { Message } from "element-ui"
import Question from "../views/Question"

Vue.use(VueRouter)

const routes = [
  {
    path: "/",
    name: "Question",
    component: Question
  },
  {
    path: "/dict",
    name: "Dict",
    component: () => import("../views/Dict.vue")
  },
  {
    path: "/org",
    name: "Org",
    component: () => import("../views/Org.vue")
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login.vue")
  },
  { path: "*", component: () => import("../views/404.vue") }
]

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
})

// 使用 router.beforeEach 注册一个全局前置守卫，判断用户是否登陆
router.beforeEach((to, from, next) => {
  if (to.path == "/login") {
    next()
  } else {
    const token = localStorage.getItem("token")
    if (!token) {
      Message({
        message: "请您重新登录",
        duration: 2000,
        type: "warning"
      })
      next({
        path: "/login",
        query: {
          redirect: to.fullPath
        }
      })
    } else {
      next()
    }
  }
})

export default router
