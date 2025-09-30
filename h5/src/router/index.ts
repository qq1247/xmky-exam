import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            component: () => import('@/views/login/login.vue'),
        },
        {
            path: '/',
            component: () => import('@/views/layout/layout.vue'),
            redirect: '/home',
            meta: { title: '首页' },
            children: [
                {
                    path: 'home',
                    component: () => import('@/views/home/home.vue'),
                    meta: { title: '首页' },
                },
                {
                    path: 'pwd',
                    component: () => import('@/views/home/home/pwd.vue'),
                    meta: { title: '密码修改' },
                },
                {
                    path: 'bulletin/:id',
                    component: () => import('@/views/home/home/bulletin-detail.vue'),
                    meta: { title: '公告' },
                },
                {
                    path: 'question-bank-list',
                    component: () => import('@/views/exam/question-bank/question-bank-list.vue'),
                    meta: { title: '题库列表' },
                },
                {
                    path: 'exam-list',
                    component: () => import('@/views/exam/exam/exam-list.vue'),
                    meta: { title: '考试列表' },
                },
                {
                    path: 'exer-list',
                    component: () => import('@/views/exam/exer/exer-list.vue'),
                    meta: { title: '练习列表' },
                },
                {
                    path: 'my-exam-list',
                    component: () => import('@/views/my/my-exam/my-exam-list.vue'),
                    meta: { title: '我的考试列表' },
                },
                {
                    path: 'my-exer-list',
                    component: () => import('@/views/my/my-exer/my-exer-list.vue'),
                    meta: { title: '我的练习列表' },
                },
                {
                    path: 'my-mark-list',
                    component: () => import('@/views/my/my-mark/my-mark-list.vue'),
                    meta: { title: '我的阅卷列表' },
                },
                {
                    path: 'base-nav',
                    meta: { title: '用户导航' },
                    component: () => import('@/views/base/base-nav.vue'),
                    children: [
                        {
                            path: 'org-list',
                            component: () => import('@/views/base/org/org-list.vue'),
                            meta: { title: '机构管理' },
                        },
                        {
                            path: 'exam-user-list',
                            component: () => import('@/views/base/exam-user/exam-user-list.vue'),
                            meta: { title: '考试用户管理' },
                        },
                        {
                            path: 'sub-admin-list',
                            component: () => import('@/views/base/sub-admin/sub-admin-list.vue'),
                            meta: { title: '子管理员管理' },
                        },
                        {
                            path: 'mark-user-list',
                            component: () => import('@/views/base/mark-user/mark-user-list.vue'),
                            meta: { title: '阅卷用户管理' },
                        },
                    ]
                },
                {
                    path: 'sys-nav',
                    meta: { title: '系统导航' },
                    component: () => import('@/views/sys/sys-nav.vue'),
                    children: [
                        {
                            path: 'bulletin-list',
                            component: () => import('@/views/sys/bulletin/bulletin-list.vue'),
                            meta: { title: '公告管理' },
                        },
                        {
                            path: 'cron-list',
                            component: () => import('@/views/sys/cron/cron-list.vue'),
                            meta: { title: '定时任务' },
                        },
                        {
                            path: 'dict-list',
                            component: () => import('@/views/sys/dict/dict-list.vue'),
                            meta: { title: '字典' },
                        },
                        {
                            path: 'parm-list',
                            component: () => import('@/views/sys/parm/parm-list.vue'),
                            meta: { title: '系统配置' },
                        },
                    ]
                },
            ]
        },

        {
            path: '/org-nav',
            meta: { title: '机构导航' },
            component: () => import('@/views/base/org/org-nav.vue'),
            children: [
                {
                    path: 'add/:parentId',
                    component: () => import('@/views/base/org/org-set.vue'),
                    meta: { title: '机构添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/base/org/org-set.vue'),
                    meta: { title: '机构设置' },
                },
            ]
        },
        {
            path: '/exam-user-nav',
            meta: { title: '考试用户导航' },
            component: () => import('@/views/base/exam-user/exam-user-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/base/exam-user/exam-user-set.vue'),
                    meta: { title: '考试用户添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/base/exam-user/exam-user-set.vue'),
                    meta: { title: '考试用户设置' },
                },
            ]
        },
        {
            path: '/sub-admin-nav',
            meta: { title: '子管理员导航' },
            component: () => import('@/views/base/sub-admin/sub-admin-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/base/sub-admin/sub-admin-set.vue'),
                    meta: { title: '子管理员添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/base/sub-admin/sub-admin-set.vue'),
                    meta: { title: '子管理员设置' },
                },
            ]
        },
        {
            path: '/mark-user-nav',
            meta: { title: '阅卷用户导航' },
            component: () => import('@/views/base/mark-user/mark-user-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/base/mark-user/mark-user-set.vue'),
                    meta: { title: '阅卷用户添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/base/mark-user/mark-user-set.vue'),
                    meta: { title: '阅卷用户设置' },
                },
            ]
        },
        {
            path: '/bulletin-nav',
            meta: { title: '公告导航' },
            component: () => import('@/views/sys/bulletin/bulletin-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/sys/bulletin/bulletin-set.vue'),
                    meta: { title: '公告添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/sys/bulletin/bulletin-set.vue'),
                    meta: { title: '公告设置' },
                },
            ]
        },
        {
            path: '/cron-nav',
            meta: { title: '公告导航' },
            component: () => import('@/views/sys/cron/cron-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/sys/cron/cron-set.vue'),
                    meta: { title: '公告添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/sys/cron/cron-set.vue'),
                    meta: { title: '公告设置' },
                },
            ]
        },
        {
            path: '/dict-nav',
            meta: { title: '字典导航' },
            component: () => import('@/views/sys/dict/dict-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/sys/dict/dict-set.vue'),
                    meta: { title: '字典添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/sys/dict/dict-set.vue'),
                    meta: { title: '字典设置' },
                },
            ]
        },
        {
            path: '/parm-nav',
            meta: { title: '系统配置导航' },
            component: () => import('@/views/sys/parm/parm-nav.vue'),
            children: [
                {
                    path: 'set/:id',
                    component: () => import('@/views/sys/parm/parm-set.vue'),
                    meta: { title: '系统配置设置' },
                },
            ]
        },
        {
            path: '/question-bank',
            meta: { title: '题库导航' },
            component: () => import('@/views/exam/question-bank/question-bank-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/exam/question-bank/question-bank-set.vue'),
                    meta: { title: '题库添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/exam/question-bank/question-bank-set.vue'),
                    meta: { title: '题库设置' },
                },
                {
                    path: 'question-nav',
                    component: () => import('@/views/exam/question-bank/question/question-nav.vue'),
                    meta: { title: '试题' },
                    children: [
                        {
                            path: 'list/:questionBankId',
                            component: () => import('@/views/exam/question-bank/question/question-list.vue'),
                            meta: { title: '试题列表' },
                        },
                        {
                            path: 'add/:questionBankId',
                            component: () => import('@/views/exam/question-bank/question/question-set.vue'),
                            meta: { title: '试题添加' },
                        },
                        {
                            path: 'set/:id',
                            component: () => import('@/views/exam/question-bank/question/question-set.vue'),
                            meta: { title: '试题设置' },
                        },
                        {
                            path: 'import/:questionBankId',
                            component: () => import('@/views/exam/question-bank/question/question-import.vue'),
                            meta: { title: '试题设置' },
                        },
                    ]
                },
            ]
        },
        {
            path: '/exam',
            meta: { title: '考试导航' },
            component: () => import('@/views/exam/exam/exam-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/exam/exam/exam-add.vue'),
                    meta: { title: '考试添加' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/exam/exam/exam-set.vue'),
                    meta: { title: '考试设置' },
                },
                {
                    path: 'statis/:id',
                    component: () => import('@/views/exam/exam/exam-statis.vue'),
                    meta: { title: '考试统计' },
                },
            ]
        },
        {
            path: '/exer',
            meta: { title: '练习导航' },
            component: () => import('@/views/exam/exer/exer-nav.vue'),
            children: [
                {
                    path: 'add',
                    component: () => import('@/views/exam/exer/exer-set.vue'),
                    meta: { title: '练习设置' },
                },
                {
                    path: 'set/:id',
                    component: () => import('@/views/exam/exer/exer-set.vue'),
                    meta: { title: '练习设置' },
                },
                {
                    path: 'statis/:id',
                    component: () => import('@/views/exam/exer/exer-statis.vue'),
                    meta: { title: '练习统计' },
                },
            ]
        },
        {
            path: '/paper/:examId/:userId',
            meta: { title: '试卷预览' },
            component: () => import('@/views/exam/exam/exam-paper.vue'),

        },
        {
            path: '/my-exam',
            meta: { title: '我的考试导航' },
            component: () => import('@/views/my/my-exam/my-exam-nav.vue'),
            children: [
                {
                    path: 'read/:examId',
                    component: () => import('@/views/my/my-exam/my-exam-read.vue'),
                    meta: { title: '考前阅读' },
                },
                {
                    path: 'paper/:examId',
                    component: () => import('@/views/my/my-exam/my-exam-paper.vue'),
                    meta: { title: '我的试卷' },
                },
            ]
        },
        {
            path: '/my-exer',
            meta: { title: '我的练习导航' },
            component: () => import('@/views/my/my-exer/my-exer-nav.vue'),
            children: [
                {
                    path: 'read/:exerId',
                    component: () => import('@/views/my/my-exer/my-exer-read.vue'),
                    meta: { title: '练前阅读' },
                },
                {
                    path: 'paper/:exerId/:id',
                    component: () => import('@/views/my/my-exer/my-exer-paper.vue'),
                    meta: { title: '我的练习' },
                },
            ]
        },
        {
            path: '/my-mark',
            meta: { title: '我的阅卷导航' },
            component: () => import('@/views/my/my-mark/my-mark-nav.vue'),
            children: [
                {
                    path: 'read/:examId',
                    component: () => import('@/views/my/my-mark/my-mark-read.vue'),
                    meta: { title: '阅前阅读' },
                },
                {
                    path: 'paper/:examId',
                    component: () => import('@/views/my/my-mark/my-mark-paper.vue'),
                    meta: { title: '我的阅卷' },
                },
            ]
        },
    ],
})

export default router
