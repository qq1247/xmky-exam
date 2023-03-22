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
      layout: 'admin'
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/User/Index.vue'),
        name: 'UserIndex',
        meta: {
          title: '用户信息',
          icon: 'common common-wo',
          layout: 'admin'
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/User/Setting/Index.vue'),
            name: 'UserIndexSetting',
            meta: {
              title: '用户设置',
              icon: 'common common-setting',
              layout: 'admin'
            },
            hidden: true
          }
        ],
        hidden: true
      }
    ]
  },
  {
    path: '/org',
    component: Layout,
    redirect: '/org/index',
    name: 'Org',
    meta: {
      title: '组织机构',
      icon: 'common common-org',
      layout: 'admin'
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Org/Index.vue'),
        name: 'OrgIndex',
        meta: {
          title: '组织机构',
          icon: 'common common-org',
          layout: 'admin'
        },
        children: [
          {
            path: 'setting/:id/:tab?/:parentId?',
            component: () => import('views/base/Org/Setting/Index.vue'),
            name: 'OrgIndexSetting',
            meta: {
              title: '机构设置',
              icon: 'common common-setting',
              layout: 'admin'
            },
            hidden: true
          }
        ],
        hidden: true
      }
    ]
  },
  {
    path: '/bulletin',
    component: Layout,
    redirect: '/bulletin/index',
    name: 'Bulletin',
    meta: {
      layout: 'admin',
      title: '公告管理',
      icon: 'common common-notice'
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Bulletin/Index.vue'),
        name: 'BulletinIndex',
        meta: {
          layout: 'admin',
          title: '公告管理',
          icon: 'common common-notice'
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/Bulletin/Setting/Index.vue'),
            name: 'BulletinIndexSetting',
            meta: {
              layout: 'admin',
              title: '公告设置',
              icon: 'common common-setting'
            },
            hidden: true
          }
        ],
        hidden: true
      }
    ]
  },
  {
    path: '/dict',
    component: Layout,
    redirect: '/dict/index',
    name: 'Dict',
    meta: {
      layout: 'admin',
      title: '数据字典',
      icon: 'common common-data-library'
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Dict/Index.vue'),
        name: 'DictIndex',
        meta: {
          layout: 'admin',
          title: '数据字典',
          icon: 'common common--data-library'
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/Dict/Setting/Index.vue'),
            name: 'DictIndexSetting',
            meta: {
              layout: 'admin',
              title: '数据字典设置',
              icon: 'common common-setting'
            },
            hidden: true
          }
        ],
        hidden: true
      }
    ]
  },
  {
    path: '/cron',
    component: Layout,
    redirect: '/cron/index',
    name: 'Cron',
    meta: {
      layout: 'admin',
      title: '定时任务',
      icon: 'common common-time'
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Cron/Index.vue'),
        name: 'CronIndex',
        meta: {
          layout: 'admin',
          title: '定时任务',
          icon: 'common common-time'
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            component: () => import('views/base/Cron/Setting/Index.vue'),
            name: 'CronIndexSetting',
            meta: {
              layout: 'admin',
              title: '定时任务设置',
              icon: 'common common-setting'
            },
            hidden: true
          }
        ],
        hidden: true
      }
    ]
  },
  {
    path: '/param',
    component: Layout,
    redirect: '/param/index',
    name: 'Param',
    meta: {
      layout: 'admin',
      title: '系统参数',
      icon: 'common common-setting'
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Param/Index.vue'),
        name: 'ParamIndex',
        meta: {
          layout: 'admin',
          title: '参数设置',
          icon: 'common common-setting'
        },
        hidden: true
      }
    ]
  },
  {
    path: '/sensitive',
    component: Layout,
    redirect: '/sensitive/index',
    name: 'Sensitive',
    meta: {
      layout: 'admin',
      title: '敏感词',
      icon: 'common common-sensitive'
    },
    children: [
      {
        path: 'index',
        component: () => import('views/base/Sensitive/Index.vue'),
        name: 'SensitiveIndex',
        meta: {
          layout: 'admin',
          title: '敏感词设置',
          icon: 'common common-sensitive'
        },
        hidden: true
      }
    ]
  },
  {
    name: 'Question',
    path: '/question',
    component: Layout,
    redirect: '/question/index',
    meta: {
      icon: 'common common-question-manage',
      layout: 'admin',
      roles: ['admin']
    },
    children: [
      {
        path: 'index',
        name: 'QuestionIndex',
        component: () => import('@/views/question/Index.vue'),
        meta: {
          title: '题库',
          layout: 'admin'
        },
        children: [
          {
            path: 'setting/:questionTypeId/:tab?',
            name: 'QuestionIndexSetting',
            component: () => import('@/views/question/Setting/Index.vue'),
            meta: {
              title: '题库设置',
              layout: 'admin'
            }
          },
          {
            path: 'list/:questionTypeId',
            name: 'QuestionIndexList',
            component: () => import('@/views/question/List.vue'),
            meta: {
              title: '试题列表',
              layout: 'admin'
            }
          },
          {
            path: 'txtImport/:questionTypeId',
            name: 'QuestionIndexTxtImport',
            component: () => import('@/views/question/txtImport.vue'),
            meta: {
              title: '文本导入',
              layout: 'admin'
            }
          },
          {
            path: 'statistics/:questionTypeId',
            name: 'QuestionIndexStatistics',
            component: () => import('@/views/question/Statistics.vue'),
            meta: {
              title: '试题统计',
              layout: 'admin'
            }
          }
        ]
      }
    ]
  },
  {
    name: 'Exam',
    path: '/exam',
    component: Layout,
    redirect: '/exam/index',
    meta: {
      icon: 'common common-exam-manage',
      layout: 'admin',
      roles: ['admin']
    },
    children: [
      {
        path: 'index',
        name: 'ExamIndex',
        component: () => import('@/views/exam/Index.vue'),
        hidden: true,
        meta: {
          title: '考试列表',
          layout: 'admin'
        },
        children: [
          {
            path: 'nav/:id',
            name: 'ExamNav',
            component: () => import('@/views/exam/Nav/Index.vue'),
            meta: {
              title: '考试导航',
              layout: 'admin'
            }
          },
          {
            path: 'setting/:id/:tab?',
            name: 'ExamListSetting',
            component: () => import('@/views/exam/Setting/Index.vue'),
            meta: {
              title: '考试设置',
              layout: 'admin'
            }
          },
        ]
      }
    ]
  },
  { path: '*', redirect: '/404', hidden: true }
]
