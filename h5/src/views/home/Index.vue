<template>
    <div class="home-content">
        <div class="home-left">
            <el-card class="home-left-top" shadow="never">
                <template #header>
                    <span>考试概览</span>
                </template>
                <div class="home-left-top-content">
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="home-left-top-content-item" @click="$router.push('/exam')">
                        <Iconfont icon="icon-diannao" :size="24" color="#09c8bd;" :width="48" :height="48"
                            background-color="#e5faf8" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.examNum }}</div>
                            <div class="home-left-top-content-item-desc">创建考试（场）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="home-left-top-content-item" @click="$router.push('/questionType')">
                        <Iconfont icon="icon-shiti" :size="28" color="#fb901b;" :width="48" :height="48"
                            background-color="#fff4e7" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.questionNum }}</div>
                            <div class="home-left-top-content-item-desc">创建试题（道）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="home-left-top-content-item" @click="$router.push('/exer')">
                        <Iconfont icon="icon-mark-paper" :size="27" color="#0094e5;" :width="48" :height="48"
                            background-color="#e5f4fd" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.exerNum }}</div>
                            <div class="home-left-top-content-item-desc">创建练习（场）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="home-left-top-content-item" @click="$router.push(userStore.type === 0 ? '/user' : '/examUser')">
                        <Iconfont icon="icon-ai-users" :size="29" color="#eb5b5b;" :width="48" :height="48"
                            background-color="#fdeeee" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.userNum }}</div>
                            <div class="home-left-top-content-item-desc">{{ userStore.type === 0 ? '创建' : '管理' }}用户（个）</div>
                        </div>
                    </div>

                    <div v-if="userStore.type === 1" class="home-left-top-content-item" @click="$router.push('/myExam')">
                        <Iconfont icon="icon-diannao" :size="24" color="#09c8bd;" :width="48" :height="48"
                            background-color="#e5faf8" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.examNum }}</div>
                            <div class="home-left-top-content-item-desc">参与考试（场）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 1" class="home-left-top-content-item" @click="$router.push('/myExer')">
                        <Iconfont icon="icon-shiti" :size="28" color="#fb901b;" :width="48" :height="48"
                            background-color="#fff4e7" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.exerNum }}</div>
                            <div class="home-left-top-content-item-desc">参与练习（场）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 1" class="home-left-top-content-item" @click="$router.push('/myExam')">
                        <Iconfont icon="icon-mark-paper" :size="27" color="#0094e5;" :width="48" :height="48"
                            background-color="#e5f4fd" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.passExamNum }}</div>
                            <div class="home-left-top-content-item-desc">及格次数（次）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 1" class="home-left-top-content-item" @click="$router.push('/myExam')">
                        <Iconfont icon="icon-ai-users" :size="29" color="#eb5b5b;" :width="48" :height="48"
                            background-color="#fdeeee" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.topRank }}</div>
                            <div class="home-left-top-content-item-desc">最高排名（名）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 3" class="home-left-top-content-item" @click="$router.push('/myMark')">
                        <Iconfont icon="icon-diannao" :size="24" color="#09c8bd;" :width="48" :height="48"
                            background-color="#e5faf8" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.examNum }}</div>
                            <div class="home-left-top-content-item-desc">总阅考试（场）</div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 3" class="home-left-top-content-item" @click="$router.push('/myMark')">
                        <Iconfont icon="icon-shiti" :size="28" color="#fb901b;" :width="48" :height="48"
                            background-color="#fff4e7" />
                        <div>
                            <div class="home-left-top-content-item-num">{{ statis.unMarkNum }}</div>
                            <div class="home-left-top-content-item-desc">待阅考试（场）</div>
                        </div>
                    </div>
                </div>
            </el-card>
            <div class="home-left-bottom">
                <el-card class="home-left-bottom-left" shadow="never">
                    <template #header>
                        <span>考试日程</span>
                    </template>
                    <el-calendar ref="calendarRef" v-model="calendar" class="home-left-bottom-left-calendar">
                        <template #header="{ date }">
                            <span>{{ date }}</span>
                            <el-button-group>
                                <el-button size="small" @click="calendarUpdate('prev-month')">
                                    上一月
                                </el-button>
                                <el-button size="small" @click="calendarUpdate('today')">今天</el-button>
                                <el-button size="small" @click="calendarUpdate('next-month')">
                                    下一月
                                </el-button>
                            </el-button-group>
                        </template>
                        <template #date-cell="{ data }">
                            <el-popover
                                v-if="hasCurMonth(data.date) && getTaskList(data.date).length"
                                title=""
                                :width="580"
                                trigger="hover"
                                >
                                <template #reference>
                                    <span>{{ hasCurMonth(data.date) ? dayjs(data.date).get('date') : '' }}</span>
                                </template>
                                <el-table :data="getTaskList(data.date)">
                                    <el-table-column width="200" property="name" label="名称" align="">
                                        <template #default="scope">
                                            <span v-if="scope.row.type === 1" class="iconfont icon-diannao"></span>
                                            <span v-if="scope.row.type === 2" class="iconfont icon-piyue"></span>
                                            {{ scope.row.name }}
                                        </template>
                                    </el-table-column>
                                    <el-table-column width="180" property="" label="考试 / 练习时间" align="center">
                                        <template #default="scope">{{ scope.row.startTime }}<br/>{{ scope.row.endTime }}</template>
                                    </el-table-column>
                                    <el-table-column width="180" property="" label="阅卷时间" align="center">
                                        <template #default="scope">{{ scope.row.examMarkStartTime }}<br/>{{ scope.row.examMarkEndTime }}</template>
                                    </el-table-column>
                                </el-table>
                            </el-popover>
                            <span v-else>{{ hasCurMonth(data.date) ? dayjs(data.date).get('date') : '' }}</span>
                            <div v-if="hasCurMonth(data.date) && getTaskList(data.date).length" class="home-left-bottom-left-calendar-task"></div>
                        </template>
                    </el-calendar>
                </el-card>
                <div class="home-left-bottom-right">
                    <el-card shadow="never">
                        <template #header>
                            <span>{{ userStore.type === 1 ? '考试任务' : '阅卷任务' }}</span>
                        </template>
                        <el-scrollbar max-height="calc((100vh - 380px) / 2)">
                            <div v-for="todoExam in todoExamList" class="home-left-bottom-right-row">
                                <div>
                                    <el-text tag="b">{{ todoExam.examName }}</el-text><br/>
                                    <el-text type="info" size="small">{{ todoExam.examStartTime }} - {{ todoExam.examEndTime }}</el-text>
                                    <el-text v-if="todoExam.examMarkType === 2" type="info" size="small"><br/>{{ todoExam.examMarkStartTime }} - {{ todoExam.examMarkEndTime }}（阅卷时间）</el-text>
                                </div>
                                <el-button v-if="(userStore.type === 0 || userStore.type === 2 || userStore.type === 3 ) && todoExam.examMarkType === 2" type="primary" plain @click="toMark(todoExam)">开始阅卷</el-button>
                                <el-button v-else-if="userStore.type === 1" type="primary" plain @click="toExam(todoExam)">开始考试</el-button>
                            </div>
                            <el-empty v-if="!todoExamList.length" description="暂无考试"/>
                        </el-scrollbar>
                    </el-card>
                    <el-card v-if="userStore.type !== 3" shadow="never">
                        <template #header>
                            <span>练习任务</span>
                        </template>
                        <el-scrollbar max-height="calc((100vh - 380px) / 2)">
                            <div v-for="todoExer in todoExerList" class="home-left-bottom-right-row">
                                <div>
                                    <el-text tag="b">{{ todoExer.name }}</el-text><br/>
                                    <el-text type="info" size="small">{{ todoExer.startTime }} - {{ todoExer.endTime }}</el-text>
                                </div>
                                <el-button v-if="userStore.type === 1" type="primary" plain @click="toExer(todoExer)">开始练习</el-button>
                            </div>
                            <el-empty v-if="!todoExerList.length" description="暂无练习"/>
                        </el-scrollbar>
                    </el-card>
                </div>
            </div>
        </div>
        <div class="home-right">
            <el-card class="home-right-menu" shadow="never">
                <template #header>
                    <span>快捷菜单</span>
                </template>
                <div class="home-right-menu-content">
                    <div v-if="userStore.type === 0" class="home-right-menu-content-item" @click="$router.push('/user')">
                        <Iconfont icon="icon-user-manage" :size="20" color="white" :width="40" :height="40" :radius="10"
                            background-color="#0094e5" />
                        <span>用户管理</span>
                    </div>
                    <div v-if="userStore.type === 0" class="home-right-menu-content-item" @click="$router.push('/org')">
                        <Iconfont icon="icon-base-manage" :size="20" color="white" :width="40" :height="40" :radius="10"
                            background-color="#6b77f9" />
                        <span>机构管理</span>
                    </div>
                    <div v-if="userStore.type === 0" class="home-right-menu-content-item" @click="$router.push('/bulletin')">
                        <Iconfont icon="icon-mark-paper" :size="20" color="white" :width="40" :height="40" :radius="10"
                            background-color="#eb5b5b" />
                        <span>公告管理</span>
                    </div>
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="home-right-menu-content-item" @click="$router.push('/exam')">
                        <Iconfont icon="icon-diannao" :size="20" color="white" :width="40" :height="40" :radius="10"
                            background-color="#09c8bd" />
                        <span>考试管理</span>
                    </div>
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="home-right-menu-content-item" @click="$router.push('/questionType')">
                        <Iconfont icon="icon-shiti" :size="22" color="white" :width="40" :height="40" :radius="10"
                            background-color="#fb901b" />
                        <span>题库管理</span>
                    </div>
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="home-right-menu-content-item" @click="$router.push('/exer')">
                        <Iconfont icon="icon-piyue" :size="22" color="white" :width="40" :height="40" :radius="10"
                            background-color="#67C23A" />
                        <span>模拟练习</span>
                    </div>
                    <div v-if="userStore.type === 1" class="home-right-menu-content-item" @click="$router.push('/myExam')">
                        <Iconfont icon="icon-diannao" :size="20" color="white" :width="40" :height="40" :radius="10"
                            background-color="#09c8bd" />
                        <span>我的考试</span>
                    </div>
                    <div v-if="userStore.type === 1" class="home-right-menu-content-item" @click="$router.push('/myExer')">
                        <Iconfont icon="icon-piyue" :size="22" color="white" :width="40" :height="40" :radius="10"
                            background-color="#67C23A" />
                        <span>我的练习</span>
                    </div>
                    <div v-if="userStore.type === 3" class="home-right-menu-content-item" @click="$router.push('/myMark')">
                        <Iconfont icon="icon-diannao" :size="20" color="white" :width="40" :height="40" :radius="10"
                            background-color="#09c8bd" />
                        <span>我的阅卷</span>
                    </div>
                </div>
            </el-card>
            <el-card class="home-right-bulletin" shadow="never">
                <template #header>
                    <span>公告列表</span>
                </template>
                <div v-for="bulletin in bulletinListpage.list" class="home-right-bulletin-content" @click="bulletinShow(bulletin)">
                    <span class="home-right-bulletin-content-name">{{ bulletin.title }}</span>
                    <span>{{ dayjs(bulletin.startTime, 'YYYY-MM-DD HH:mm:ss').format('YYYY-MM-DD') }}</span>
                </div>
                <el-empty v-if="bulletinListpage.total === 0" description="暂无公告"/>
            </el-card>
            <el-card class="home-right-custom" shadow="never" id="q1">
                <template #header>
                    <span>{{ custom.title }}</span>
                </template>
                <span v-html="`${custom.content}`"/>
            </el-card>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, watch } from 'vue'
import dayjs from "dayjs";
import { useUserStore } from '@/stores/user';
import http from '@/request';
import isBetween from 'dayjs/plugin/isBetween'
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

//  定义变量
dayjs.extend(isBetween)
const router = useRouter()
const userStore = useUserStore()
const calendarRef = ref()// 日历引用
const calendar = ref()// 日历当前日期
const statis = reactive({// 首页统计
    examNum: 0,// 考试数量
    questionNum: 0,// 试题数量
    exerNum: 0,// 练习数量
    userNum: 0,// 用户数量
    unExamNum: 0,// 待考数量
    unMarkNum: 0,// 待阅数量
    passExamNum: 0,// 及格次数
    topRank: 0,// 最高排名
})
const taskList = ref([] as any[])// 任务列表（日历悬浮展示）
const todoExamList = ref([] as any[])// 未完成考试列表
const todoExerList = ref([] as any[])// 未完成练习列表
const bulletinListpage = reactive({// 公告分页列表
    curPage: 1,
    pageSize: 100,
    total: 0,
    list: [] as any[],
})
const custom = reactive({// 自定义内容
    title: '',
    content: '',
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (!userStore.id) {
        router.push('/login')
    }

    // 更新日历日期
    let { data: { data: data } } = await http.post("login/sysTime", {  })
    let curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
    calendar.value = curTime

    // 任务查询
    if (userStore.type === 0) {// 如果是admin登录
        let { data: { data } } = await http.post("report/admin/home", {  })// 首页统计
        statis.examNum = data.examNum
        statis.questionNum = data.questionNum
        statis.exerNum = data.exerNum
        statis.userNum = data.userNum

        let { data: { data: data2 } } = await http.post("myMark/listpage", { pageSize: 100, todo: true })// 未完成的考试列表
        todoExamList.value.push(...data2.list) 

        let { data: { data: data3 } } = await http.post("exer/listpage", { pageSize: 100, todo: true })// 未完成的练习列表
        todoExerList.value.push(...data3.list) 
    }
    if (userStore.type === 2) {// 如果是子管理员登录
        let { data: { data } } = await http.post("report/subAdmin/home", {  })// 首页统计
        statis.examNum = data.examNum
        statis.questionNum = data.questionNum
        statis.exerNum = data.exerNum
        statis.userNum = data.userNum

        let { data: { data: data2 } } = await http.post("myMark/listpage", { pageSize: 100, todo: true })// 未完成的考试列表
        todoExamList.value.push(...data2.list) 

        let { data: { data: data3 } } = await http.post("exer/listpage", { pageSize: 100, todo: true })// 未完成的练习列表
        todoExerList.value.push(...data3.list) 
    }
    if (userStore.type === 1) {// 如果是用户登录
        let { data: { data } } = await http.post("report/user/home", {  })// 首页统计
        statis.examNum = data.examNum
        statis.exerNum = data.exerNum
        statis.passExamNum = data.passExamNum
        statis.topRank = data.topRank

        let { data: { data: data2 } } = await http.post("myExam/listpage", { pageSize: 100, todo: true })// 未完成的考试列表
        todoExamList.value.push(...data2.list) 

        let { data: { data: data3 } } = await http.post("myExer/listpage", { pageSize: 100, todo: true })// 未完成的练习列表
        todoExerList.value.push(...data3.list)
    }
    if (userStore.type === 3) {// 如果是阅卷用户登录
        let { data: { data } } = await http.post("report/markUser/home", {  })// 首页统计
        statis.examNum = data.examNum
        statis.unMarkNum = data.unMarkNum

        let { data: { data: data2 } } = await http.post("myMark/listpage", { pageSize: 100, todo: true })// 未完成的考试列表
        todoExamList.value.push(...data2.list) 
    }

    // 公告查询
    let { data: { code, data: data2 } } = await http.post('bulletin/listpage', {
        notice: true,
        curPage: 1,
        pageSize: 10,
    })

    bulletinListpage.list = data2.list.map((bulletin: any) => {
        bulletin.content = bulletin.content.replaceAll('\n', '<br/>')
        return bulletin
    })
    bulletinListpage.total = data2.total

    // 自定义信息查询
    let { data: { data:data3 } } = await http.post('login/custom', { })
    custom.title = data3.title
    custom.content = data3.content.replaceAll('\n', '<br/>')
})

// 监听属性
watch(() => calendar.value, async (n, o) => {
    // 日历翻页时，查询指定月的考试列表和练习列表
    if (n && o && dayjs(n).format('YYYY-MM') === dayjs(o).format('YYYY-MM')) {// 如果是同一个月份不处理
        return
    }

    let startTime = dayjs(n).startOf('month').format('YYYY-MM-DD HH:mm:ss')
    let endTime = dayjs(n).endOf('month').format('YYYY-MM-DD HH:mm:ss')
    taskList.value.length = 0
    if (userStore.type === 0) {
        let { data: { data } } = await http.post("myMark/listpage", { pageSize: 100, startTime, endTime })
        taskList.value.push(...data.list.map((task: any) => {
            task.startTime = task.examStartTime
            task.endTime = task.examEndTime
            task.name = task.examName
            task.type = 1
            return task
        })) 

        let { data: { data: data2 } } = await http.post("exer/listpage", { pageSize: 100, startTime, endTime })
        taskList.value.push(...data2.list.map((task: any) => {
            task.type = 2
            return task
        })) 
    } else if (userStore.type === 1) {
        let { data: { data } } = await http.post("myExam/listpage", { pageSize: 100, startTime, endTime })
        taskList.value.push(...data.list.map((task: any) => {
            task.startTime = task.examStartTime
            task.endTime = task.examEndTime
            task.name = task.examName
            task.type = 1
            return task
        })) 

        let { data: { data: data2 } } = await http.post("myExer/listpage", { pageSize: 100, startTime, endTime })
        taskList.value.push(...data2.list.map((task: any) => {
            task.type = 2
            return task
        })) 
    }
})

// 日历变更时间
function calendarUpdate(curDate: string) {
    calendarRef.value.selectDate(curDate)
}

// 是否当月日期
function hasCurMonth(curDate: Date) {
    return dayjs(curDate).isBetween(
        dayjs(calendar.value).startOf('month').toDate(),// 当月-01 00:00:00
        dayjs(calendar.value).endOf('month').toDate(),// 当月-31 23:59:59
        null, 
        '[]')// 包含边界
}

// 任务列表获取
function getTaskList(curDate: Date) {
    return taskList.value.filter(task => {
        return (dayjs(curDate).isBetween(
                dayjs(task.startTime, 'YYYY-MM-DD HH:mm:ss').startOf('day').toDate(),// 考试开始时间的00:00:00
                dayjs(task.startTime, 'YYYY-MM-DD HH:mm:ss').endOf('day').toDate(),// 考试开始时间的23:59:59
                null, 
                '[]'))// 包含边界
    })
}

// 公告显示
function bulletinShow(bulletin: any) {
    ElMessageBox.alert(bulletin.content, bulletin.title, {
        customClass: 'el-message-box-ex',
        dangerouslyUseHTMLString: true,
    })
}

// 去考试
async function toExam(myExam: any) {
    let examStartTime = dayjs(myExam.examStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    let { data: { data } } = await http.post("login/sysTime", {  })
    let curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
    if (curTime.getTime() < examStartTime.getTime()) {
        ElMessage.error('考试未开始，请等待...')
        return
    }

    router.push(`/myExam/paper/${myExam.examId}`)
}

// 去阅卷
async function toMark(myMark: any) {
    let examMarkStartTime = dayjs(myMark.examMarkStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    let { data: { data } } = await http.post("login/sysTime", {  })
    let curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
    if (curTime.getTime() < examMarkStartTime.getTime()) {
        ElMessage.error('阅卷未开始，请等待...')
        return
    }

    if (userStore.type === 3) {
        router.push(`/myMark/paper/${ myMark.examId }`)
    } else {
        router.push(`/exam/mark/${ myMark.examId }`)
    }
}

// 去练习
async function toExer(myExer: any) {
    let startTime = dayjs(myExer.startTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    let { data: { data } } = await http.post("login/sysTime", {  })
    let curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
    if (curTime.getTime() < startTime.getTime()) {
        ElMessage.error('练习未开始，请等待...')
        return
    }

    router.push(`/myExer/paper/${ myExer.id }`)
}
</script>

<style lang="scss" scoped>
.home-content {
    width: 1200px;
    height: 100%;
    display: flex;

    :deep(.el-card) {
        margin-bottom: 15px;
        position: relative;

        .el-card__header {
            padding: 15px 15px 15px 30px;
            font-size: 14px;
            font-weight: bold;
        }

        .el-card__header::after {
            content: "";
            display: block;
            width: 4px;
            height: 16px;
            background: #409EFF;
            position: absolute;
            top: 15px;
            left: 15px;
        }

        .el-card__body {
            font-size: 12px;
            color: var(--el-text-color-regular);
        }
    }

    .home-left {
        width: 900px;
        display: flex;
        flex-direction: column;
        padding-right: 15px;

        .home-left-top {
            overflow: initial;
            .home-left-top-content {
                display: flex;

                .home-left-top-content-item {
                    flex: 1;
                    display: flex;
                    justify-content: center;
                    cursor: pointer;

                    .home-left-top-content-item-num {
                        font-size: 28px;
                        height: 30px;
                        padding-left: 15px;
                    }

                    .home-left-top-content-item-desc {
                        font-weight: bold;
                        padding-left: 15px;
                        color: var(--el-text-color-secondary);
                    }
                }
            }
        }

        .home-left-bottom {
            flex: 1;
            display: flex;
            :deep(.home-left-bottom-left) {
                width: 330px;
                .home-left-bottom-left-calendar {
                    width: 300px;
                    height: 330px;
                    font-size: 14px;

                    .el-calendar__header {
                        padding: 12px 20px 8px 20px;

                        span {
                            line-height: 28px;
                        }
                    }

                    td[class*="is-selected"] {
                        .el-calendar-day {
                            background-color: #409EFF;
                            border-radius: 5px;
                            position: relative;
                            span {
                                color: white;
                            }
                            .home-left-bottom-left-calendar-task {
                                background-color: var(--el-color-white);
                            }
                        }
                    }

                    td {
                        border: 0;
                        border-radius: 5px;
                        position: relative;

                        .el-calendar-day {
                            height: 35px;
                            width: 35px;
                            margin: 1px 2px;

                            span {
                                text-align: center;
                                display: inline-block;
                                width: 100%;
                                height: 100%;
                                line-height: 24px;
                            }
                            .home-left-bottom-left-calendar-task {
                                display: block;
                                position: absolute;
                                bottom: 2px;
                                transform: translateX(-50%);
                                left: 50%;
                                width: 6px;
                                height: 6px;
                                background-color: var(--el-color-primary);
                                border-radius: 50%;
                                font-size: 12px;
                            }
                            &:hover {
                                background-color: var(--el-color-primary);
                                border-radius: 5px;

                                span {
                                    color: var(--el-color-white);
                                }
                                .home-left-bottom-left-calendar-task {
                                    background-color: var(--el-color-white);
                                }
                            }
                        }
                    }
                }
            }
            :deep(.home-left-bottom-right) {
                flex: 1;
                display: flex;
                flex-direction: column;
                margin-left: 15px;
                .el-card {
                    flex: 1;
                    .el-card__body {
                        padding-top: 0px;
                        .home-left-bottom-right-row {
                            display: flex;
                            justify-content: space-between;
                            align-items: flex-end;
                            border-bottom: 1px solid var(--el-border-color);
                            padding: 20px 10px 10px 10px;
                            div {
                                .el-text {
                                    font-size: 13px;
                                }
                            }
                            .el-button {
                                border: 0;
                            }
                        }
                    }
                }
            }
        }
    }

    .home-right {
        flex: 1;
        width: 0;
        display: flex;
        flex-direction: column;

        .home-right-menu {

            .home-right-menu-content {
                display: flex;
                flex-wrap: wrap;
                color: var(--el-text-color-secondary);
                font-weight: bold;

                .home-right-menu-content-item {
                    width: 33%;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    cursor: pointer;

                    span {
                        display: block;
                        padding-top: 10px;
                        padding-bottom: 20px;
                    }
                }
            }
        }

        .home-right-bulletin {
            flex: 1;
            font-weight: bold;

            .home-right-bulletin-content {
                display: flex;
                justify-content: space-between;
                cursor: pointer;
                padding-bottom: 10px;

                .home-right-bulletin-content-name {
                    word-break: keep-all;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    overflow: hidden;
                }
            }

        }

        .home-right-custom {
            font-weight: bold;
        }
    }
}
</style>
<style lang="scss">
.el-message-box-ex {
    max-width: 600px;
}
</style>