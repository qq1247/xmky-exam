import Layout from '@/layout/Index.vue'
export default [
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { title: '首页', icon: 'common common-index' },
    children: [
      {
        path: 'home',
        component: () => import('views/home/Index'),
        name: 'Home',
        meta: { title: '首页' }
      },
      {
        path: 'login',
        component: () => import('views/base/Login'),
        name: 'Login',
        meta: { title: '欢迎登录' },
        hidden: true
      },
      {
        path: '404',
        component: () => import('views/base/404'),
        name: '404',
        meta: { title: '404' },
        hidden: true
      }
    ]
  }
]
