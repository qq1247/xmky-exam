import Layout from '@/layout/Index.vue'
export default [
  {
    path: '/question',
    component: Layout,
    redirect: '/question/index',
    name: 'Question',
    meta: {
      title: '题库管理',
      icon: 'common common-question-manage',
      layout: 'subAdmin',
      roles: ['subAdmin']
    },
    children: [
      {
        path: 'index',
        name: 'QuestionIndex',
        component: () => import('@/views/question/Index.vue'),
        meta: {
          title: '题库',
          layout: 'subAdmin'
        },
        children: [
          {
            path: 'setting/:questionTypeId/:tab?',
            name: 'QuestionIndexSetting',
            component: () => import('@/views/question/Setting/Index.vue'),
            meta: {
              title: '题库设置',
              layout: 'subAdmin'
            }
          },
          {
            path: 'list/:questionTypeId',
            name: 'QuestionIndexList',
            component: () => import('@/views/question/List.vue'),
            meta: {
              title: '试题列表',
              layout: 'subAdmin'
            }
          },
          {
            path: 'txtImport/:questionTypeId',
            name: 'QuestionIndexTxtImport',
            component: () => import('@/views/question/txtImport.vue'),
            meta: {
              title: '文本导入',
              layout: 'subAdmin'
            }
          },
          {
            path: 'statistics/:questionTypeId',
            name: 'QuestionIndexStatistics',
            component: () => import('@/views/question/Statistics.vue'),
            meta: {
              title: '试题统计',
              layout: 'subAdmin'
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
      title: '考试管理',
      icon: 'common common-exam-manage',
      layout: 'subAdmin',
      roles: ['subAdmin']
    },
    children: [
      {
        path: '/exam/nav',
        name: 'ExamNav',
        component: () => import('@/views/exam/Nav/Index.vue'),
        meta: {
          title: '考试导航',
          layout: 'subAdmin'
        },
        children: [
          {
            path: 'setting',
            name: 'ExamNavSetting',
            component: () => import('@/views/exam/Nav/PaperSetting.vue'),
            meta: {
              title: '试卷设置',
              layout: 'subAdmin'
            }
          }
        ]
      },
      {
        path: 'index',
        name: 'ExamIndex',
        component: () => import('@/views/exam/Index.vue'),
        hidden: true,
        meta: {
          title: '考试列表',
          layout: 'subAdmin'
        },
        children: [
          {
            path: '/exam/list/setting/:id/:examTypeId/:tab?',
            name: 'ExamListSetting',
            component: () => import('@/views/exam/Setting/Index.vue'),
            meta: {
              title: '列表设置',
              layout: 'subAdmin'
            }
          },
          {
            path: '/exam/list/markSetting/:id/:examTypeId',
            name: 'ExamListMarkSetting',
            component: () => import('@/views/exam/MarkSetting.vue'),
            meta: {
              title: '考试用户',
              layout: 'subAdmin'
            }
          },
          {
            path: '/exam/list/line/:id/:examTypeId',
            name: 'ExamListLine',
            component: () => import('@/views/exam/OnLine.vue'),
            meta: {
              title: '在线用户',
              layout: 'subAdmin'
            }
          },
          {
            path: '/exam/list/statistics/:id/:examTypeId',
            name: 'ExamListStatistics',
            component: () => import('@/views/exam/Statistics.vue'),
            meta: {
              title: '考试统计',
              layout: 'subAdmin'
            }
          }
        ]
      }
    ]
  },
  {
    path: '/myExam',
    component: Layout,
    redirect: '/myExam/index',
    name: 'MyExam',
    meta: {
      title: '考试管理',
      layout: 'user',
      icon: 'common common-question-manage',
      roles: ['user', 'subAdmin']
    },
    children: [
      {
        path: 'index',
        component: () => import('views/my/Exam/Index.vue'),
        name: 'MyExamIndex',
        meta: {
          title: '我的考试',
          icon: 'common common-exam',
          layout: 'user'
        },
        hidden: true
      },
      {
        path: 'detail/:examId/:examEndTime/:showType/:preview/:userId?',
        component: () => import('views/my/Exam/Detail.vue'),
        name: 'MyExamDetail',
        meta: {
          layout: 'common'
        },
        hidden: true
      },
      {
        path: 'result/:examId/:scoreState',
        component: () => import('views/my/Exam/Result.vue'),
        name: 'MyExamResult',
        meta: {
          layout: 'common'
        },
        hidden: true
      }
    ]
  },
  {
    path: '/myMark',
    component: Layout,
    redirect: '/myMark/index',
    name: 'MyMark',
    meta: {
      title: '阅卷管理',
      layout: 'subAdmin',
      icon: 'common common-mark-paper',
      roles: ['user', 'subAdmin']
    },
    children: [
      {
        path: 'index',
        component: () => import('views/my/Mark/Index.vue'),
        name: 'MyMarkIndex',
        meta: {
          title: '阅卷分类',
          icon: 'common common-exam',
          layout: 'subAdmin'
        },
        children: [
          {
            path: 'user/:examId/:paperId/:preview/:userId?',
            component: () => import('views/my/Mark/User.vue'),
            name: 'MyMarkIndexUser',
            meta: {
              title: '考生列表',
              layout: 'subAdmin'
            },
            hidden: true
          },
          {
            path: 'detail/:examId/:paperId/:preview/:userId?',
            component: () => import('views/my/Mark/Detail.vue'),
            name: 'MyMarkIndexDetail',
            meta: {
              title: '阅卷',
              layout: 'subAdmin'
            },
            hidden: true
          }
        ],
        hidden: true
      }
    ]
  },
  {
    path: '/simulate',
    component: Layout,
    redirect: '/simulate/index',
    name: 'Simulate',
    meta: {
      title: '模拟练习',
      icon: 'common common-simulate',
      layout: 'user',
      roles: ['user']
    },
    children: [
      {
        path: 'index',
        name: 'SimulateIndex',
        component: () => import('@/views/simulate/Index.vue'),
        hidden: true,
        meta: {
          title: '模拟试题',
          layout: 'user'
        }
      },
      {
        path: 'test/:questionTypeId/:commentState?',
        name: 'SimulateTest',
        component: () => import('@/views/simulate/Test/Index.vue'),
        hidden: true,
        meta: {
          title: '背题 | 模拟测试',
          layout: 'user'
        }
      }
    ]
  },
  {
    path: '/quick',
    component: Layout,
    redirect: '/quick/index',
    name: 'Quick',
    meta: {
      title: '快捷考试',
      icon: 'common common-quick',
      layout: 'subAdmin',
      roles: ['subAdmin'],
      hidden: true
    },
    children: [
      {
        path: 'index',
        name: 'QuickIndex',
        component: () => import('@/views/quick/Index.vue'),
        hidden: true,
        meta: {
          title: '快捷考试',
          layout: 'subAdmin'
        }
      }
    ]
  },
  { path: '*', redirect: '/404', hidden: true }
]
