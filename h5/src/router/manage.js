/*
 * @Description: 管理路由
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-12-29 09:57:44
 * @LastEditors: Che
 * @LastEditTime: 2022-01-06 17:13:23
 */
import Layout from '@/layout/Index.vue'
export default [
  {
    path: '/user',
    component: Layout,
    redirect: '/user/index',
    name: 'User',
    meta: {
      title: '用户管理',
      icon: 'common common-wo',
      layout: 'admin',
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/User/Index.vue'),
        name: 'UserIndex',
        meta: {
          title: '用户信息',
          icon: 'common common-wo',
          layout: 'admin',
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/User/Setting/Index.vue'),
            name: 'UserIndexSetting',
            meta: {
              title: '用户设置',
              icon: 'common common-setting',
              layout: 'admin',
            },
            hidden: true,
          },
        ],
        hidden: true,
      },
    ],
  },
  {
    path: '/org',
    component: Layout,
    redirect: '/org/index',
    name: 'Org',
    meta: {
      title: '组织机构',
      icon: 'common common-org',
      layout: 'admin',
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Org/Index.vue'),
        name: 'OrgIndex',
        meta: {
          title: '组织机构',
          icon: 'common common-org',
          layout: 'admin',
        },
        children: [
          {
            path: 'setting/:id/:tab?/:parentId?',
            component: () => import('views/base/Org/Setting/Index.vue'),
            name: 'OrgIndexSetting',
            meta: {
              title: '机构设置',
              icon: 'common common-setting',
              layout: 'admin',
            },
            hidden: true,
          },
        ],
        hidden: true,
      },
    ],
  },
  {
    path: '/bulletin',
    component: Layout,
    redirect: '/bulletin/index',
    name: 'Bulletin',
    meta: {
      layout: 'admin',
      title: '我的公告',
      icon: 'common common-notice',
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Bulletin/Index.vue'),
        name: 'BulletinIndex',
        meta: {
          layout: 'admin',
          title: '我的公告',
          icon: 'common common-notice',
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/Bulletin/Setting/Index.vue'),
            name: 'BulletinIndexSetting',
            meta: {
              layout: 'admin',
              title: '公告设置',
              icon: 'common common-setting',
            },
            hidden: true,
          },
        ],
        hidden: true,
      },
    ],
  },
  {
    path: '/dict',
    component: Layout,
    redirect: '/dict/index',
    name: 'Dict',
    meta: {
      layout: 'admin',
      title: '数据字典',
      icon: 'common common-data-library',
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Dict/Index.vue'),
        name: 'DictIndex',
        meta: {
          layout: 'admin',
          title: '数据字典',
          icon: 'common common--data-library',
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/Dict/Setting/Index.vue'),
            name: 'DictIndexSetting',
            meta: {
              layout: 'admin',
              title: '数据字典设置',
              icon: 'common common-setting',
            },
            hidden: true,
          },
        ],
        hidden: true,
      },
    ],
  },
  {
    path: '/cron',
    component: Layout,
    redirect: '/cron/index',
    name: 'Cron',
    meta: {
      layout: 'admin',
      title: '定时任务',
      icon: 'common common-time',
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Cron/Index.vue'),
        name: 'CronIndex',
        meta: {
          layout: 'admin',
          title: '定时任务',
          icon: 'common common-time',
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/Cron/Setting/Index.vue'),
            name: 'CronIndexSetting',
            meta: {
              layout: 'admin',
              title: '定时任务设置',
              icon: 'common common-setting',
            },
            hidden: true,
          },
        ],
        hidden: true,
      },
    ],
  },
  {
    path: '/param',
    component: Layout,
    redirect: '/param/index',
    name: 'Param',
    meta: {
      layout: 'admin',
      title: '系统参数',
      icon: 'common common-setting',
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Param/Index.vue'),
        name: 'ParamIndex',
        meta: {
          layout: 'admin',
          title: '参数设置',
          icon: 'common common-setting',
        },
        hidden: true,
      },
    ],
  },
  {
    path: '/sensitive',
    component: Layout,
    redirect: '/sensitive/index',
    name: 'Sensitive',
    meta: {
      layout: 'admin',
      title: '敏感词',
      icon: 'common common-sensitive',
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Sensitive/Index.vue'),
        name: 'SensitiveIndex',
        meta: {
          layout: 'admin',
          title: '敏感词设置',
          icon: 'common common-sensitive',
        },
        hidden: true,
      },
    ],
  },
  { path: '*', redirect: '/404', hidden: true },
]
