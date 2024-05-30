import { createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: () => import('../views/layout/Index.vue'),
            redirect: '/home',
            meta: { title: '首页' },
            children: [
                {
                    path: 'home',
                    component: () => import('../views/home/Index.vue'),
                },
                {
                    path: 'org',
                    component: () => import('../views/user/org/Index.vue'),
                    meta: { title: '机构' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/user/org/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add/:parentId',
                                    component: () => import('../views/user/org/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/user/org/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/user/org/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'user',
                    component: () => import('../views/user/user/Index.vue'),
                    meta: { title: '用户' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/user/user/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/user/user/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/user/user/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'pwdInit/:id',
                                    component: () => import('../views/user/user/setting/PwdInit.vue'),
                                    meta: { title: '密码初始化' },
                                },
                                {
                                    path: 'frozen/:id',
                                    component: () => import('../views/user/user/setting/Frozen.vue'),
                                    meta: { title: '冻结' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/user/user/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'subAdmin',
                    component: () => import('../views/user/subAdmin/Index.vue'),
                    meta: { title: '子管理员' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/user/subAdmin/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/user/subAdmin/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/user/subAdmin/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'pwdInit/:id',
                                    component: () => import('../views/user/subAdmin/setting/PwdInit.vue'),
                                    meta: { title: '密码初始化' },
                                },
                                {
                                    path: 'frozen/:id',
                                    component: () => import('../views/user/subAdmin/setting/Frozen.vue'),
                                    meta: { title: '冻结' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/user/subAdmin/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'markUser',
                    component: () => import('../views/user/markUser/Index.vue'),
                    meta: { title: '阅卷用户' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/user/markUser/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/user/markUser/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/user/markUser/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'pwdInit/:id',
                                    component: () => import('../views/user/markUser/setting/PwdInit.vue'),
                                    meta: { title: '密码初始化' },
                                },
                                {
                                    path: 'frozen/:id',
                                    component: () => import('../views/user/markUser/setting/Frozen.vue'),
                                    meta: { title: '冻结' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/user/markUser/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'examUser',
                    component: () => import('../views/user/examUser/Index.vue'),
                    meta: { title: '考试用户' },
                    children: [
                    ]
                },
                {
                    path: 'dict',
                    component: () => import('../views/base/dict/Index.vue'),
                    meta: { title: '数据字典' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/base/dict/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/base/dict/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/base/dict/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/base/dict/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'cron',
                    component: () => import('../views/base/cron/Index.vue'),
                    meta: { title: '定时任务' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/base/cron/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/base/cron/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/base/cron/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/base/cron/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                                {
                                    path: 'start/:id',
                                    component: () => import('../views/base/cron/setting/Start.vue'),
                                    meta: { title: '启动/停止' },
                                },
                                {
                                    path: 'runOnce/:id',
                                    component: () => import('../views/base/cron/setting/RunOnce.vue'),
                                    meta: { title: '执行一次' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'bulletin',
                    component: () => import('../views/base/bulletin/Index.vue'),
                    meta: { title: '公告' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/base/bulletin/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/base/bulletin/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/base/bulletin/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/base/bulletin/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'parm',
                    component: () => import('../views/base/parm/Index.vue'),
                    meta: { title: '系统配置' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/base/parm/setting/Index.vue'),
                            redirect: '/parm/pwd',
                            children: [
                                {
                                    path: 'pwd',
                                    component: () => import('../views/base/parm/setting/Pwd.vue'),
                                    meta: { title: '默认密码' },
                                },
                                {
                                    path: 'ent',
                                    component: () => import('../views/base/parm/setting/Ent.vue'),
                                    meta: { title: '企业信息' },
                                },
                                {
                                    path: 'custom',
                                    component: () => import('../views/base/parm/setting/Custom.vue'),
                                    meta: { title: '自定义' },
                                },
                                // {
                                //     path: 'email',
                                //     component: () => import('../views/base/parm/setting/Email.vue'),
                                //     meta: { title: '邮箱设置' },
                                // },
                                // {
                                //     path: 'file',
                                //     component: () => import('../views/base/parm/setting/File.vue'),
                                //     meta: { title: '上传目录' },
                                // },
                                // {
                                //     path: 'db',
                                //     component: () => import('../views/base/parm/setting/DB.vue'),
                                //     meta: { title: '备份目录' },
                                // },
                            ]
                        },
                    ]
                },
                {
                    path: 'cache',
                    component: () => import('../views/base/cache/Index.vue'),
                    meta: { title: '缓存服务' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/base/cache/setting/Index.vue'),
                            redirect: '/cache/cache',
                            children: [
                                {
                                    path: 'cache',
                                    component: () => import('../views/base/cache/setting/Cache.vue'),
                                    meta: { title: '缓存服务' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'questionType',
                    component: () => import('../views/questionType/Index.vue'),
                    meta: { title: '题库' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/questionType/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/questionType/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/questionType/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'clear/:id',
                                    component: () => import('../views/questionType/setting/Clear.vue'),
                                    meta: { title: '清空' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/questionType/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                        {
                            path: 'question/:questionTypeId',
                            component: () => import('../views/questionType/question/Index.vue'),
                            meta: { title: '列表' },
                        },
                    ]
                },
                {
                    path: 'exer',
                    component: () => import('../views/exer/Index.vue'),
                    meta: { title: '练习' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/exer/setting/Index.vue'),
                            children: [
                                {
                                    path: 'add',
                                    component: () => import('../views/exer/setting/Edit.vue'),
                                    meta: { title: '添加' },
                                },
                                {
                                    path: 'edit/:id',
                                    component: () => import('../views/exer/setting/Edit.vue'),
                                    meta: { title: '修改' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/exer/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                            ]
                        },
                    ]
                },
                {
                    path: 'exam',
                    component: () => import('../views/exam/Index.vue'),
                    meta: { title: '考试' },
                    children: [
                        {
                            path: '',
                            component: () => import('../views/exam/setting/Index.vue'),
                            children: [
                                {
                                    path: 'pause/:id',
                                    component: () => import('../views/exam/setting/Pause.vue'),
                                    meta: { title: '暂停' },
                                },
                                {
                                    path: 'del/:id',
                                    component: () => import('../views/exam/setting/Del.vue'),
                                    meta: { title: '删除' },
                                },
                                {
                                    path: 'time/:id',
                                    component: () => import('../views/exam/setting/Time.vue'),
                                    meta: { title: '时间变更' },
                                },
                                {
                                    path: 'markUser/:id',
                                    component: () => import('../views/exam/setting/MarkUser.vue'),
                                    meta: { title: '协助阅卷' },
                                },
                            ]
                        },
                        {
                            path: 'add',
                            component: () => import('../views/exam/edit/Index.vue'),
                            meta: { title: '添加' },
                        },
                        {
                            path: 'edit/:id',
                            component: () => import('../views/exam/edit/Index.vue'),
                            meta: { title: '组卷' },
                        },
                        {
                            path: 'mark/:id',
                            component: () => import('../views/exam/Mark.vue'),
                            meta: { title: '阅卷' },
                        },
                        {
                            path: 'statis/:id',
                            component: () => import('../views/exam/Statis.vue'),
                            meta: { title: '统计' },
                        },
                        {
                            path: 'paper/:id/:userId',
                            component: () => import('../views/exam/Paper.vue'),
                            meta: { title: '统计' },
                        },
                    ]
                },
                {
                    path: 'myExam',
                    component: () => import('../views/my/myExam/Index.vue'),
                    meta: { title: '我的考试' },
                    children: [
                        {
                            path: 'paper/:examId',
                            component: () => import('../views/my/myExam/Paper.vue'),
                            meta: { title: '试卷' },
                        },
                    ]
                },
                {
                    path: 'myMark',
                    component: () => import('../views/my/myMark/Index.vue'),
                    meta: { title: '我的阅卷' },
                    children: [
                        {
                            path: 'paper/:examId',
                            component: () => import('../views/my/myMark/Paper.vue'),
                            meta: { title: '批阅' },
                        },
                    ]
                },
                {
                    path: 'myExer',
                    component: () => import('../views/my/myExer/Index.vue'),
                    meta: { title: '我的练习' },
                    children: [
                        {
                            path: 'paper/:exerId',
                            component: () => import('../views/my/myExer/Paper.vue'),
                            meta: { title: '试题' },
                        },
                    ]
                },
            ],
        },
        {
            path: '/login',
            component: () => import('../views/login/Index.vue'),
        },
    ]
})

export default router
