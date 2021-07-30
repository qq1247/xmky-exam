import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store/index'
import { Message } from 'element-ui'
import Home from '../views/Home'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/base/Login.vue'),
  },
  // 试卷相关
  {
    path: '/examPaper',
    name: 'ExamPaper',
    component: () => import('../views/examPaper/Index.vue'),
  },
  {
    path: '/examPaper/list',
    name: 'ExamPaperList',
    component: () => import('../views/examPaper/List.vue'),
  },
  {
    path: '/examPaper/edit',
    name: 'ExamPaperEdit',
    component: () => import('../views/examPaper/Edit.vue'),
  },
  // 考试相关
  {
    path: '/examSetting',
    name: 'ExamSetting',
    component: () => import('../views/examSetting/Index.vue'),
  },
  {
    path: '/examSetting/list',
    name: 'ExamSettingList',
    component: () => import('../views/examSetting/List.vue'),
  },
  // 试题相关
  {
    path: '/examQuestion',
    name: 'ExamQuestion',
    component: () => import('../views/examQuestion/Index.vue'),
  },
  {
    path: '/examQuestion/edit',
    name: 'ExamQuestionEdit',
    component: () => import('../views/examQuestion/Edit.vue'),
  },
  // 基础相关
  {
    path: '/base',
    name: 'Base',
    component: () => import('../views/base/Index.vue'),
  },
  {
    path: '/base/cron',
    name: 'BaseCron',
    component: () => import('../views/base/Cron.vue'),
  },
  {
    path: '/base/dict',
    name: 'BaseDict',
    component: () => import('../views/base/Dict.vue'),
  },
  {
    path: '/base/Param',
    name: 'BaseParam',
    component: () => import('../views/base/Param.vue'),
  },
  // 用户相关
  {
    path: '/user',
    name: 'User',
    component: () => import('../views/user/User.vue'),
  },
  {
    path: '/user/org',
    name: 'UserOrg',
    component: () => import('../views/user/Org.vue'),
  },
  {
    path: '/my/bulletin',
    name: 'MyBulletin',
    component: () => import('../views/my/Bulletin.vue'),
  },
  {
    path: '/organization/myExam',
    name: 'OrgMyExam',
    component: () => import('../views/organization/MyExam.vue'),
  },
  {
    path: '/organization/myMark',
    name: 'OrgMyMark',
    component: () => import('../views/organization/MyMark.vue'),
  },
  {
    path: '/organization/myMarkExam',
    name: 'OrgMyMarkExam',
    component: () => import('../views/organization/MyMarkExam.vue'),
  },
  // 我的
  {
    path: '/my',
    name: 'My',
    component: () => import('../views/my/index.vue'),
  },
  {
    path: '/my/exam',
    name: 'MyExam',
    component: () => import('../views/my/MyExam.vue'),
  },
  {
    path: '/my/markExam',
    name: 'MyMarkExam',
    component: () => import('../views/my/MyMarkExam.vue'),
  },
  { path: '*', component: () => import('../views/base/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
})

// 使用 router.beforeEach 注册一个全局前置守卫，判断用户是否登陆
router.beforeEach((to, from, next) => {
  if (to.path != '/login') {
    const token = store.state.userInfo
      ? JSON.parse(store.state.userInfo).accessToken
      : ''
    if (!token) {
      Message({
        message: '请您重新登录',
        duration: 2000,
        type: 'warning',
      })
      next({
        path: '/login',
        query: {
          redirect: to.fullPath,
        },
      })
    }
  }
  next()
})

// Loading chunk * failed 错误
router.onError((error) => {
  const pattern = /Loading chunk (\d)+ failed/g
  const isChunkLoadFailed = error.message.match(pattern)
  const targetPath = router.history.pending.fullPath
  if (isChunkLoadFailed) {
    router.replace(targetPath)
  }
})

export default router
