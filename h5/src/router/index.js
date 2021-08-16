import Vue from 'vue'
import VueRouter from 'vue-router'

// vue-router内部错误,没有进行catch处理
const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace
// push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject)
    return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch((err) => err)
}
// replace
VueRouter.prototype.replace = function push(location, onResolve, onReject) {
  if (onResolve || onReject)
    return originalReplace.call(this, location, onResolve, onReject)
  return originalReplace.call(this, location).catch((err) => err)
}

/* Layout */
import Layout from '@/layout/Index.vue'

Vue.use(VueRouter)

export const constantRoutes = [
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { title: '首页', icon: 'common common-index' },
    children: [
      {
        path: 'home',
        component: () => import('@/views/Home'),
        name: 'Home',
        meta: { title: '首页' },
      },
      {
        path: 'login',
        component: () => import('@/views/base/Login'),
        name: 'Login',
        meta: { title: '欢迎登录' },
        hidden: true,
      },
    ],
  },
  {
    path: '/my',
    component: Layout,
    redirect: '/my/exam',
    name: 'My',
    meta: { title: '我的', icon: 'common common-mine' },
    children: [
      {
        path: 'exam',
        component: () => import('@/views/my/List.vue'),
        name: 'ExamList',
        meta: { title: '我的考试', icon: 'common common-exam', type: 1 },
      },
      {
        path: 'mark',
        component: () => import('@/views/my/List.vue'),
        name: 'MarkList',
        meta: { title: '我的阅卷', icon: 'common common-mark', type: 2 },
      },
      {
        path: 'bulletin',
        component: () => import('@/views/my/Bulletin.vue'),
        name: 'Bulletin',
        meta: { title: '我的公告', icon: 'common common-notice' },
      },
    ],
  },
  {
    path: '*',
    component: Layout,
    redirect: '/404',
    meta: { title: 'Error' },
    hidden: true,
    children: [
      {
        path: '404',
        component: () => import('@/views/base/404'),
        name: 'Error',
        meta: { title: '404' },
      },
    ],
  },
]

export const asyncRoutes = [
  {
    path: '/question',
    component: Layout,
    redirect: '/question/index',
    name: 'Question',
    meta: {
      title: '试题管理',
      icon: 'common common-question-manage',
      roles: ['admin', 'subAdmin'], // you can set roles in root nav
    },
    children: [
      {
        path: 'index',
        name: 'QuestionIndex',
        component: () => import('../views/examQuestion/Index.vue'),
        meta: {
          title: '试题管理',
        },
      },
      {
        path: 'edit',
        name: 'QuestionEdit',
        component: () => import('../views/examQuestion/Edit.vue'),
        hidden: true,
        meta: {
          title: '编辑试题',
        },
      },
    ],
  },
  {
    path: '/paper',
    component: Layout,
    redirect: '/paper/index',
    name: 'Paper',
    meta: {
      title: '试卷管理',
      icon: 'common common-paper-manage',
      roles: ['admin', 'subAdmin'], // you can set roles in root nav
    },
    children: [
      {
        path: 'index',
        name: 'PaperIndex',
        component: () => import('../views/examPaper/Index.vue'),
        meta: {
          title: '试卷管理',
        },
      },
      {
        path: 'list',
        name: 'PaperList',
        component: () => import('../views/examPaper/List.vue'),
        hidden: true,
        meta: {
          title: '试卷列表',
        },
      },
      {
        path: 'edit',
        name: 'PaperEdit',
        component: () => import('../views/examPaper/Edit.vue'),
        hidden: true,
        meta: {
          title: '编辑试卷',
        },
      },
    ],
  },
  {
    path: '/exam',
    component: Layout,
    redirect: '/exam/index',
    name: 'Exam',
    meta: {
      title: '考试管理',
      icon: 'common common-exam-manage',
      roles: ['admin', 'subAdmin'], // you can set roles in root nav
    },
    children: [
      {
        path: 'index',
        name: 'SettingIndex',
        component: () => import('../views/examSetting/Index.vue'),
        meta: {
          title: '考试管理',
        },
      },
      {
        path: 'list',
        name: 'SettingList',
        component: () => import('../views/examSetting/List.vue'),
        hidden: true,
        meta: {
          title: '编辑试题',
        },
      },
    ],
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/index',
    name: 'User',
    meta: {
      title: '用户管理',
      icon: 'common common-exam-manage',
      roles: ['admin'], // you can set roles in root nav
    },
    children: [
      {
        path: 'index',
        name: 'UserIndex',
        component: () => import('../views/user/User.vue'),
        meta: {
          title: '用户管理',
        },
      },
      {
        path: 'org',
        name: 'UserOrg',
        component: () => import('../views/user/Org.vue'),
        meta: {
          title: '用户组织',
        },
        hidden: true,
      },
      {
        path: 'orgExam',
        name: 'OrgExam',
        component: () => import('../views/organization/MyExam.vue'),
        meta: {
          title: '用户组织',
        },
        hidden: true,
      },
      {
        path: 'orgMark',
        name: 'OrgMark',
        component: () => import('../views/organization/MyMark.vue'),
        meta: {
          title: '用户组织',
        },
        hidden: true,
      },
      {
        path: 'orgMarkExam',
        name: 'OrgMarkExam',
        component: () => import('../views/organization/MyMarkExam.vue'),
        meta: {
          title: '用户组织',
        },
        hidden: true,
      },
    ],
  },
  {
    path: '/base',
    component: Layout,
    redirect: '/base/index',
    name: 'Base',
    meta: {
      title: '基础管理',
      icon: 'common common-exam-manage',
      roles: ['admin'], // you can set roles in root nav
    },
    children: [
      {
        path: 'index',
        name: 'BaseIndex',
        component: () => import('../views/base/Index.vue'),
        meta: {
          title: '基础管理',
        },
      },
      {
        path: 'cron',
        name: 'BaseCron',
        component: () => import('../views/base/Cron.vue'),
        hidden: true,
      },
      {
        path: 'dict',
        name: 'BaseDict',
        component: () => import('../views/base/Dict.vue'),
        hidden: true,
      },
      {
        path: 'param',
        name: 'BaseParam',
        component: () => import('../views/base/Param.vue'),
        hidden: true,
      },
    ],
  },
]

/* const routes = [
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
    component: () => import('../views/my/Index.vue'),
  },
  // 考试，阅卷列表
  {
    path: '/my/list',
    name: 'MyList',
    component: () => import('../views/my/List.vue'),
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
] */

const createRouter = () =>
  new VueRouter({
    mode: 'history',
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
  })

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
