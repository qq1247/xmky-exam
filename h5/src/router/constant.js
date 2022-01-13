/*
 * @Description: 基础路由
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-12-29 09:55:10
 * @LastEditors: Che
 * @LastEditTime: 2021-12-29 10:28:34
 */
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
]
