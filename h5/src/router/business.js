import Layout from '@/layout/Index.vue'
export default [
  {
    path: '/question',
    component: Layout,
    redirect: '/question/index',
    name: 'Question',
    meta: {
      title: '试题管理',
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
          title: '试题分类',
          layout: 'subAdmin'
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            name: 'QuestionIndexSetting',
            component: () => import('@/views/question/Setting/Index.vue'),
            meta: {
              title: '试题设置',
              layout: 'subAdmin'
            }
          },
          {
            path: 'edit/:id',
            name: 'QuestionIndexEdit',
            component: () => import('@/views/question/Edit.vue'),
            meta: {
              title: '试题列表',
              layout: 'subAdmin'
            }
          },
          {
            path: 'editor/:id',
            name: 'QuestionIndexTxtImport',
            component: () => import('@/views/question/Editor.vue'),
            meta: {
              title: '文本导入',
              layout: 'subAdmin'
            }
          },
          // {
          //   path: 'open/:id',
          //   name: 'QuestionIndexOpen',
          //   component: () => import('@/views/question/Open.vue'),
          //   hidden: true,
          //   meta: {
          //     title: '模拟练习',
          //     layout: 'subAdmin'
          //   }
          // },
          {
            path: 'statistics/:id',
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
    path: '/paper',
    component: Layout,
    redirect: '/paper/index',
    name: 'Paper',
    meta: {
      title: '试卷管理',
      icon: 'common common-paper-manage',
      layout: 'subAdmin',
      roles: ['subAdmin']
    },
    children: [
      {
        path: 'index',
        name: 'PaperIndex',
        component: () => import('@/views/paper/Index/Index.vue'),
        meta: {
          title: '试卷分类',
          layout: 'subAdmin'
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            name: 'PaperIndexSetting',
            component: () => import('@/views/paper/Index/Setting/Index.vue'),
            meta: {
              title: '试卷设置',
              layout: 'subAdmin'
            }
          }
        ]
      },
      {
        path: 'list/:id?',
        name: 'PaperList',
        component: () => import('@/views/paper/List/Index.vue'),
        hidden: true,
        meta: {
          title: '试卷列表',
          layout: 'subAdmin'
        },
        children: [
          {
            path: '/paper/list/setting/:id/:paperTypeId/:genType/:tab?',
            name: 'PaperListSetting',
            component: () => import('@/views/paper/List/Setting/Index.vue'),
            meta: {
              title: '列表设置',
              layout: 'subAdmin'
            }
          },
          {
            path: '/paper/list/edit/:id/:paperTypeId/:genType/:tab?',
            name: 'PaperListEdit',
            component: () => import('@/views/paper/List/Edit/Index.vue'),
            meta: {
              title: '组合试卷',
              layout: 'subAdmin'
            }
          }
        ]
      }
    ]
  },
  {
    path: '/exam',
    component: Layout,
    redirect: '/exam/index',
    name: 'Exam',
    meta: {
      title: '考试管理',
      icon: 'common common-exam-manage',
      layout: 'subAdmin',
      roles: ['subAdmin']
    },
    children: [
      {
        path: 'index',
        name: 'ExamIndex',
        component: () => import('@/views/exam/Index/Index.vue'),
        meta: {
          title: '考试分类',
          layout: 'subAdmin'
        },
        children: [
          {
            path: 'setting/:id/:tab?',
            name: 'ExamIndexSetting',
            component: () => import('@/views/exam/Index/Setting/Index.vue'),
            meta: {
              title: '考试设置',
              layout: 'subAdmin'
            }
          }
        ]
      },
      {
        path: 'list/:id',
        name: 'ExamList',
        component: () => import('@/views/exam/List/Index.vue'),
        hidden: true,
        meta: {
          title: '考试列表',
          layout: 'subAdmin'
        },
        children: [
          {
            path: '/exam/list/setting/:id/:examTypeId/:tab?',
            name: 'ExamListSetting',
            component: () => import('@/views/exam/List/Setting/Index.vue'),
            meta: {
              title: '列表设置',
              layout: 'subAdmin'
            }
          },
          {
            path: '/exam/list/markSetting/:id/:examTypeId',
            name: 'ExamListMarkSetting',
            component: () => import('@/views/exam/List/MarkSetting.vue'),
            meta: {
              title: '考试用户',
              layout: 'subAdmin'
            }
          },
          {
            path: '/exam/list/line/:id/:examTypeId',
            name: 'ExamListLine',
            component: () => import('@/views/exam/List/OnLine.vue'),
            meta: {
              title: '在线用户',
              layout: 'subAdmin'
            }
          },
          {
            path: '/exam/list/statistics/:id/:examTypeId',
            name: 'ExamListStatistics',
            component: () => import('@/views/exam/List/Statistics.vue'),
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
