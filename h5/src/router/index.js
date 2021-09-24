import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout/Index.vue'

// vue-router内部错误,没有进行catch处理
const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace
// push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalPush.call(this, location, onResolve, onReject)
  }
  return originalPush.call(this, location).catch((err) => err)
}
// replace
VueRouter.prototype.replace = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalReplace.call(this, location, onResolve, onReject)
  }
  return originalReplace.call(this, location).catch((err) => err)
}

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
        component: () => import('views/Home'),
        name: 'Home',
        meta: { title: '首页' },
      },
      {
        path: 'login',
        component: () => import('views/base/Login'),
        name: 'Login',
        meta: { title: '欢迎登录' },
        hidden: true,
      },
      {
        path: '404',
        component: () => import('views/base/404'),
        name: '404',
        meta: { title: '404' },
        hidden: true,
      },
    ],
  },
  {
    path: '/my',
    component: Layout,
    redirect: '/my/index',
    name: 'My',
    meta: { title: '我的', icon: 'common common-mine' },
    children: [
      {
        path: 'index',
        component: () => import('views/my/Index.vue'),
        name: 'MyIndex',
        meta: { title: '我的', icon: 'common common-mine' },
      },
      {
        path: 'exam',
        component: () => import('views/my/List.vue'),
        name: 'ExamList',
        meta: { title: '我的考试', icon: 'common common-exam', type: 1 },
        hidden: true,
      },
      {
        path: 'mark',
        component: () => import('views/my/List.vue'),
        name: 'MarkList',
        meta: { title: '我的阅卷', icon: 'common common-mark', type: 2 },
        hidden: true,
      },
      {
        path: 'exam/index',
        component: () => import('views/my/MyExam.vue'),
        name: 'ExamIndex',
        hidden: true,
      },
      {
        path: 'mark/index',
        component: () => import('views/my/MyMarkExam.vue'),
        name: 'MarkIndex',
        hidden: true,
      },
      {
        path: 'bulletin',
        component: () => import('views/my/Bulletin.vue'),
        name: 'Bulletin',
        meta: {
          title: '我的公告',
          icon: 'common common-notice',
        },
        hidden: true,
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
        component: () => import('../views/question/Index.vue'),
        meta: {
          title: '试题管理',
        },
      },
      {
        path: 'edit',
        name: 'QuestionEdit',
        component: () => import('../views/question/Edit.vue'),
        hidden: true,
        meta: {
          title: '编辑试题',
          hideHeader: true,
          hideFooter: true,
        },
      },
      {
        path: 'comment',
        name: 'QuestionComment',
        component: () => import('../views/question/Comment.vue'),
        hidden: true,
        meta: {
          title: '查看 | 评论试题',
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
        component: () => import('../views/paper/Index.vue'),
        meta: {
          title: '试卷管理',
        },
      },
      {
        path: 'list',
        name: 'PaperList',
        component: () => import('../views/paper/List.vue'),
        hidden: true,
        meta: {
          title: '试卷列表',
        },
      },
      {
        path: 'edit',
        name: 'PaperEdit',
        component: () => import('../views/paper/Edit.vue'),
        hidden: true,
        meta: {
          title: '编辑试卷',
          hideHeader: true,
          hideFooter: true,
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
        component: () => import('../views/exam/Index.vue'),
        meta: {
          title: '考试管理',
        },
      },
      {
        path: 'list',
        name: 'SettingList',
        component: () => import('../views/exam/List.vue'),
        hidden: true,
        meta: {
          title: '考试列表',
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
      icon: 'common common-user-manage',
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
      icon: 'common common-base-manage',
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
        meta: {
          title: '定时任务',
        },
        hidden: true,
      },
      {
        path: 'dict',
        name: 'BaseDict',
        component: () => import('../views/base/Dict.vue'),
        meta: {
          title: '数据字典',
        },
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
  { path: '*', redirect: '/404', hidden: true },
]

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
