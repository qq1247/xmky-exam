import Vue from "vue"
import VueRouter from "vue-router"
import { Message } from "element-ui"
import Home from "../views/Home"

Vue.use(VueRouter)

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/base/Login.vue")
  },
  {
    path: "/examPaper/classify",
    name: "PaperClassify",
    component: () => import("../views/examPaper/Classify.vue")
  },
  {
    path: "/examPaper/add",
    name: "PaperAdd",
    component: () => import("../views/examPaper/Add.vue")
  },
  {
    path: "/examPaper/edit",
    name: "PaperEdit",
    component: () => import("../views/examPaper/Edit.vue")
  },
  {
    path: "/organization/dict",
    name: "Dict",
    component: () => import("../views/organization/Dict.vue")
  },
  {
    path: "/organization/org",
    name: "Org",
    component: () => import("../views/organization/Org.vue")
  },
  {
    path: "/examLibrary/classify",
    name: "LibraryClassify",
    component: () => import("../views/examLibrary/Classify.vue")
  },
  {
    path: "/examLibrary/edit",
    name: "LibraryEdit",
    component: () => import("../views/examLibrary/Edit.vue")
  },
  { path: "*", component: () => import("../views/base/404.vue") }
]

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
})

// 使用 router.beforeEach 注册一个全局前置守卫，判断用户是否登陆
/* router.beforeEach((to, from, next) => {
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
}) */

export default router
