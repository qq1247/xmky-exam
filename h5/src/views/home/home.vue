<template>
    <div class="home">
        <div class="home__head">
            <div class="notice">
                <div class="notice__outer">
                    <span class="iconfont icon-lingdang notice__tip-icon"></span>
                    <span class="notice__tip">{{ bulletinList.length === 0 ? '暂无公告' : '通知公告' }}</span>
                    <el-carousel direction="vertical" :autoplay="true" :loop="false">
                        <el-carousel-item v-for="(bulletin, index) in bulletinList" :key="index"
                            @click="bulletinDetail(bulletin)">
                            <div class="notice__inner">
                                <span class="notice__time">{{ bulletin.title }}</span>
                                <span class="notice__content">{{ bulletin.content }}</span>
                            </div>
                        </el-carousel-item>
                    </el-carousel>
                </div>
                <el-button type="" link class="notice__more">
                    <!-- 更多>> -->
                </el-button>
            </div>
        </div>
        <div class="home__main">
            <el-scrollbar max-height="calc(100vh - 300px)" class="task-wrap">
                <div v-if="userStore.type === 0 || userStore.type === 2 || userStore.type === 3" class="task">
                    <xmks-card-guide title="阅卷任务" icon="icon-tubiaoziti22-22" class="task__head"></xmks-card-guide>
                    <div class="task__main">
                        <xmks-card-empty v-if="unMarkList.length === 0" name="暂无阅卷"
                            icon="icon-tubiaoziti22-22"></xmks-card-empty>
                        <xmks-card-data v-else v-for="(myMark, index) in unMarkList" :key="index"
                            :title="myMark.examName" tag-name="阅卷" :btns="[{
                                name: '阅卷',
                                icon: 'icon-icon-05',
                                event: () => $router.push(`/my-mark/read/${myMark.examId}`)
                            }]" class="my-mark">
                            <div class="my-mark__tag-wrap">
                                <span class="my-mark__tag my-mark__tag--gen-type">
                                    {{ dictStore.getValue("PAPER_GEN_TYPE", myMark.examGenType) }}
                                </span>
                                <span class="my-mark__tag my-mark__tag--login-type">
                                    {{ dictStore.getValue("LOGIN_TYPE", myMark.examLoginType) }}
                                </span>
                                <span class="my-mark__tag my-mark__tag--mark-state">
                                    {{ dictStore.getValue("MARK_STATE", myMark.examMarkState) }}
                                </span>
                                <span class="my-mark__tag my-mark__tag--state">
                                    {{ dictStore.getValue("STATE_PS", myMark.examState) }}
                                </span>
                            </div>
                            <div class="my-mark__row">
                                <span class="my-mark__exam-time">
                                    考试时间：<span class="my-mark__time">{{ myMark.examStartTime.substring(0, 16) }} -
                                        {{
                                            myMark.examEndTime.substring(0, 16) }}</span>
                                </span>
                            </div>
                            <div class="my-mark__row">
                                <span v-if="myMark.examMarkType === 2" class="my-mark__exam-time">
                                    阅卷时间：<span class="my-mark__time">{{ myMark.examMarkStartTime?.substring(0, 16) }}
                                        - {{ myMark.examMarkEndTime?.substring(0, 16) }}</span>
                                </span>
                                <span v-else class="my-mark__exam-time">
                                    阅卷时间：考试结束自动阅卷
                                </span>
                            </div>
                            <div class="my-mark__row1">
                                <div class="my-mark__inner">
                                    <span class="my-mark__value">
                                        {{ dictStore.getValue("PAPER_MARK_TYPE", myMark.examMarkType) }}<span
                                            class="my-mark__unit"></span>
                                    </span>
                                    <span class="my-mark__lab">试卷</span>
                                </div>
                                <div class="my-mark__inner">
                                    <span class="my-mark__value">
                                        {{ myMark.examPassScore || '-' }}/{{ myMark.examTotalScore }}<span
                                            class="my-mark__unit">分</span>
                                    </span>
                                    <span class="my-mark__lab">及格分数</span>
                                </div>
                                <div class="my-mark__inner">
                                    <span v-if="myMark.limitMinute" class="my-mark__value">
                                        60<span class="my-mark__unit">分钟</span>
                                    </span>
                                    <span v-else class="my-mark__value">无</span>
                                    <span class="my-mark__lab">限时</span>
                                </div>
                                <div class="my-mark__inner">
                                    <span class="my-mark__value">
                                        {{ myMark.examUserNum }}<span class="my-mark__unit">人</span>
                                    </span>
                                    <span class="my-mark__lab">考试人数</span>
                                </div>
                                <div class="my-mark__inner">
                                    <span class="my-mark__value">
                                        {{ myMark.examSxes.length > 0 ? '是' : '否' }}<span class="my-mark__unit"></span>
                                    </span>
                                    <span class="my-mark__lab">防作弊</span>
                                </div>
                                <div class="my-mark__inner">
                                    <span class="my-mark__value">
                                        {{ dictStore.getValue('SCORE_STATE', myMark.examScoreState) }}<span
                                            class="my-mark__unit"></span>
                                    </span>
                                    <span class="my-mark__lab">查询成绩</span>
                                </div>
                                <div class="my-mark__inner">
                                    <span class="my-mark__value">
                                        {{ dictStore.getValue('STATE_ON', myMark.examRankState) }}<span
                                            class="my-mark__unit"></span>
                                    </span>
                                    <span class="my-mark__lab">排名</span>
                                </div>
                                <div class="my-mark__inner">
                                    <span v-if="myMark.markUserNum" class="my-mark__value">
                                        {{ myMark.examMarkUserNum }}<span class="my-mark__unit">人</span>
                                    </span>
                                    <span v-else class="my-mark__value">无</span>
                                    <span class="my-mark__lab">协助阅卷</span>
                                </div>
                            </div>
                        </xmks-card-data>
                    </div>
                </div>
                <div v-if="userStore.type === 0 || userStore.type === 2" class="task">
                    <xmks-card-guide title="练习任务" icon="icon-icon_xiugaishijian" class="task__head"></xmks-card-guide>
                    <div class="task__main">
                        <xmks-card-empty v-if="exerList.length === 0" name="暂无练习"
                            icon="icon-icon_xiugaishijian"></xmks-card-empty>
                        <xmks-card-data v-for="exer in exerList" :key="exer.id" :title="exer.name" tag-name="练习" :btns="[{
                            name: '设置',
                            icon: 'icon-liebiao-01',
                            event: () => $router.push(`/exer/set/${exer.id}`)
                        }]" class="exer">
                            <div class="exer__state">
                                <span class="exer__pre-txt">
                                    发布练习：<span class="exer__num">{{ dictStore.getValue('STATE_PS', exer.state) }}</span>
                                </span>
                                <!-- <span class="exer__pre-txt">
                        允许评论：<span class="exer__num">{{ dictStore.getValue('STATE_YN', exer.rmkState) }}</span>
                    </span> -->
                            </div>
                            <div class="exer__outer">
                                <div class="exer__inner">
                                    <span class="exer__num">
                                        {{ exer.orgIds.length }}<span class="exer__unit">个</span>
                                    </span>
                                    <span class="exer__after-txt">练习机构</span>
                                </div>
                                <div class="exer__inner">
                                    <span class="exer__num">
                                        {{ exer.userIds.length }}<span class="exer__unit">人</span>
                                    </span>
                                    <span class="exer__after-txt">练习人数</span>
                                </div>
                            </div>
                            <div class="exer__other">
                                <span class="exer__time">{{ exer.updateTime }}</span>
                                <span class="exer__username">{{ exer.createUserName }}</span>
                            </div>
                        </xmks-card-data>
                    </div>
                </div>
                <div v-if="userStore.type === 1" class="task">
                    <xmks-card-guide title="我的考试" icon="icon-tubiaoziti22-22" class="task__head"></xmks-card-guide>
                    <div class="task__main">
                        <xmks-card-empty v-if="unExamList.length === 0" name="暂无考试"
                            icon="icon-tubiaoziti22-22"></xmks-card-empty>
                        <xmks-card-data v-else v-for="myExam in unExamList" :key="myExam.id" :title="myExam.examName"
                            tag-name="考试" class="my-exam">
                            <div class="my-exam__exam-time">
                                <span>
                                    {{ myExam.examStartTime }} - {{ myExam.examEndTime }}
                                </span>
                            </div>
                            <div class="my-exam__outer">
                                <div class="my-exam__inner">
                                    <span class="my-exam__num">
                                        {{ myExam.examLimitMinute || '无' }}<span v-if="myExam.examLimitMinute"
                                            class="my-exam__unit">分钟</span>
                                    </span>
                                    <span class="my-exam__after-txt">限时答题</span>
                                </div>
                                <div class="my-exam__inner">
                                    <span class="my-exam__num">
                                        {{ myExam.totalScore || '-' }}<span class="my-exam__unit">/{{
                                            myExam.examTotalScore
                                            }}</span>
                                    </span>
                                    <span class="my-exam__after-txt">我的分数</span>
                                </div>
                                <div class="my-exam__inner">
                                    <span class="my-exam__num">
                                        {{ myExam.no || '-' }}<span class="my-exam__unit">/{{ myExam.userNum || '-'
                                            }}</span>
                                    </span>
                                    <span class="my-exam__after-txt">我的排名</span>
                                </div>
                                <div class="my-exam__inner">
                                    <span class="my-exam__num">
                                        {{ diff(myExam.answerStartTime, myExam.answerEndTime) }}<span
                                            class="my-exam__unit">分钟</span>
                                    </span>
                                    <span class="my-exam__after-txt">答题用时</span>
                                </div>
                            </div>
                            <div class="my-exam__other">
                                <xmks-count-down v-if="myExam.state === 1 || myExam.state === 2"
                                    :expireTime="myExam.state === 1 ? myExam.examStartTime : myExam.answerEndTime"
                                    :preTxt="myExam.state === 1 ? '距离考试开始：' : '距离考试结束：'" class="my-exam__time">
                                </xmks-count-down>
                                <template v-else>
                                    <div>
                                        <span class="my-exam__tag my-exam__tag--state">
                                            {{ dictStore.getValue("EXAM_STATE", myExam.state) }}
                                        </span>
                                        <span class="my-exam__tag my-exam__tag--mark-state">
                                            {{ dictStore.getValue("MARK_STATE", myExam.markState) }}
                                        </span>
                                    </div>
                                </template>

                                <el-button type="primary" class="my-exam__btn"
                                    @click="$router.push(`/my-exam/read/${myExam.examId}`)">
                                    {{ myExam.markState >= 2 ? '查阅试卷' : '进入考试' }}
                                </el-button>
                            </div>
                        </xmks-card-data>
                    </div>
                </div>
                <div v-if="userStore.type === 1" class="task">
                    <xmks-card-guide title="我的练习" icon="icon-icon_xiugaishijian" class="task__head"></xmks-card-guide>
                    <div class="task__main">
                        <xmks-card-empty v-if="exerList.length === 0" name="暂无练习"
                            icon="icon-tubiaoziti22-22"></xmks-card-empty>
                        <xmks-card-data v-else v-for="exer in exerList" :key="exer.id" :title="exer.name" tag-name="练习"
                            class="my-exer">
                            <div class="my-exer__exam-time">
                                <span class="my-exer__pre-txt">
                                    客观题：<span class="my-exer__num">{{ exer.objectiveNum }}</span>道
                                </span>
                                <span class="my-exer__pre-txt">
                                    主观题：<span class="my-exer__num">{{ exer.subjectiveNum }}</span>道
                                </span>
                            </div>
                            <div class="my-exer__outer">
                                <div class="my-exer__inner">
                                    <span class="my-exer__num">
                                        {{ exer.singleNum }}<span class="my-exer__unit">道</span>
                                    </span>
                                    <span class="my-exer__after-txt">单选题</span>
                                </div>
                                <div class="my-exer__inner">
                                    <span class="my-exer__num">
                                        {{ exer.multipleNum }}<span class="my-exer__unit">道</span>
                                    </span>
                                    <span class="my-exer__after-txt">多选题</span>
                                </div>
                                <div class="my-exer__inner">
                                    <span class="my-exer__num">
                                        {{ exer.fillBlankObjNum + exer.fillBlankSubNum }}<span
                                            class="my-exer__unit">道</span>
                                    </span>
                                    <span class="my-exer__after-txt">填空题</span>
                                </div>
                                <div class="my-exer__inner">
                                    <span class="my-exer__num">
                                        {{ exer.judgeNum }}<span class="my-exer__unit">道</span>
                                    </span>
                                    <span class="my-exer__after-txt">判断题</span>
                                </div>
                                <div class="my-exer__inner">
                                    <span class="my-exer__num">
                                        {{ exer.qaObjNum + exer.qaSubNum }}<span class="my-exer__unit">道</span>
                                    </span>
                                    <span class="my-exer__after-txt">问答题</span>
                                </div>
                            </div>
                            <div class="my-exer__other">
                                <div></div>
                                <el-button type="primary" class="my-exer__btn"
                                    @click="() => router.push(`/my-exer/read/${exer.id}`)">
                                    进入练习
                                </el-button>
                            </div>
                        </xmks-card-data>
                    </div>
                </div>
            </el-scrollbar>
            <div class="calendar">
                <xmks-card-guide title="考试日程" icon="icon-bianjirili" class="calendar__head"></xmks-card-guide>
                <el-calendar ref="calendarRef" v-model="calendar" class="calendar__main">
                    <template #header="{ date }">
                        <div class="calendar__opt">
                            <el-button type="" link bg class="calendar__btn"
                                @click="calendarRef.selectDate('prev-month')">
                                <span class="iconfont icon-zuo calendar__btn-icon"></span>
                            </el-button>
                            <span class="calendar__ymd">{{ date }}</span>
                            <el-button type="" link bg class="calendar__btn"
                                @click="calendarRef.selectDate('next-month')">
                                <span class="iconfont icon-you calendar__btn-icon"></span>
                            </el-button>
                        </div>
                    </template>
                    <template #date-cell="{ data }">
                        <div class="calendar__date" :class="{
                            'calendar__date--finished': hasFinishedTask(data.day),
                            'calendar__date--ongoing': hasOngoingTask(data.day),
                        }">
                            <span class="calendar__date-txt">{{ dayjs(data.date).get('date') }}</span>
                        </div>
                    </template>
                </el-calendar>
                <el-scrollbar height="calc(100vh - 660px)" class="calendar__foot">
                    <div v-if="userStore.type === 0 || userStore.type === 2" class="calendar__task-list">
                        <div v-for="(myMark, index) in myMarkGroup[dayjs(calendar).format('YYYY-MM-DD')]" :key="index"
                            class="calendar-task">
                            <div class="calendar-task__outer">
                                <span class="calendar-task__name">{{ myMark.examName }}</span>
                                <div class="calendar-task__inner">
                                    <span class="calendar-task__type">考试</span>
                                    <span class="calendar-task__state" :class="{
                                        'calendar__date--pending': myMark.examMarkState === 1,
                                        'calendar__date--ongoing': myMark.examMarkState === 2,
                                        'calendar__date--finished': myMark.examMarkState === 3,

                                    }">
                                        {{ dictStore.getValue("MARK_STATE", myMark.examMarkState) }}
                                    </span>
                                </div>
                            </div>
                            <div class="calendar-task__content">
                                <span class="calendar-task__time">
                                    {{ myMark.examStartTime }} - {{ myMark.examEndTime }}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 1" class="calendar__task-list">
                        <div v-for="(myExam, index) in myExamGroup[dayjs(calendar).format('YYYY-MM-DD')]" :key="index"
                            class="calendar-task">
                            <div class="calendar-task__outer">
                                <span class="calendar-task__name">{{ myExam.examName }}</span>
                                <div class="calendar-task__inner">
                                    <span class="calendar-task__type">考试</span>
                                    <span class="calendar-task__state" :class="{
                                        'calendar__date--pending': myExam.state === 1,
                                        'calendar__date--ongoing': myExam.state === 2,
                                        'calendar__date--finished': myExam.state === 3,

                                    }">
                                        {{ dictStore.getValue("EXAM_STATE", myExam.state) }}
                                    </span>
                                </div>
                            </div>
                            <div class="calendar-task__content">
                                <span class="calendar-task__time">
                                    {{ myExam.examStartTime }} - {{ myExam.examEndTime }}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div v-if="userStore.type === 3" class="calendar__task-list">
                        <div v-for="(myMark, index) in myMarkGroup[dayjs(calendar).format('YYYY-MM-DD')]" :key="index"
                            class="calendar-task">
                            <div class="calendar-task__outer">
                                <span class="calendar-task__name">{{ myMark.examName }}</span>
                                <div class="calendar-task__inner">
                                    <span class="calendar-task__type">考试</span>
                                    <span class="calendar-task__state" :class="{
                                        'calendar__date--pending': myMark.examMarkState === 1,
                                        'calendar__date--ongoing': myMark.examMarkState === 2,
                                        'calendar__date--finished': myMark.examMarkState === 3,

                                    }">
                                        {{ dictStore.getValue("MARK_STATE", myMark.examMarkState) }}
                                    </span>
                                </div>
                            </div>
                            <div class="calendar-task__content">
                                <span class="calendar-task__time">
                                    {{ myMark.examStartTime }} - {{ myMark.examEndTime }}
                                </span>
                            </div>
                        </div>
                    </div>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from "dayjs";
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import XmksCardGuide from '@/components/card/xmks-card-guide.vue';
import { myMarkListpage } from '@/api/my/my-mark';
import { myExamListpage } from '@/api/my/my-exam';
import { loginSysTime } from '@/api/login';
import { exerListpage } from '@/api/exam/exer';
import { bulletinListpage } from '@/api/sys/bulletin';
import { useDictStore } from '@/stores/dict';
import xmksCardData from '@/components/card/xmks-card-data.vue';
import { diff } from '@/util/timeUtil'
import xmksCountDown from '@/components/xmks-count-down.vue';
import xmksCardEmpty from '@/components/card/xmks-card-empty.vue';
import type { Bulletin } from '@/ts/sys/bulletin';

/************************变量定义相关***********************/
const dictStore = useDictStore();// 字典缓存
const router = useRouter()
const userStore = useUserStore()
const calendarRef = ref()// 日历引用
const calendar = ref<Date>()// 日历当前日期

const myMarkList = ref<any[]>([])// 我的待阅卷列表
const exerList = ref<any[]>([])// 待练习列表
const myExamList = ref<any[]>([])// 我的考试列表
const bulletinList = ref<any[]>([])// 公告列表

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    // 如果未登录，跳转到登录页面
    if (!userStore.id) {
        router.push('/login')
    }

    // 更新日历日期为今天（不要依赖前端）
    calendarQuery()

    // 任务查询
    if (userStore.type === 0 || userStore.type === 2) {
        myMarkListQuery()
        exerListQuery();
    } else if (userStore.type === 1) {
        myExamListQuery()
        exerListQuery()
    } else if (userStore.type === 3) {
        myMarkListQuery()
    }

    // 公告查询
    bulletinListQuery()
})

/************************计算属性相关*************************/
const myMarkGroup = computed(() => {
    return myMarkList.value.reduce((groups, myMark) => {
        const date = myMark.examStartTime.substring(0, 10);
        if (!groups[date]) {
            groups[date] = [];
        }
        groups[date].push(myMark);
        return groups;
    }, {});
});
const unMarkList = computed(() => { // 阅卷任务列表
    return myMarkList.value.filter(myMark => myMark.examMarkState === 2)
})
const unExamList = computed(() => {
    return myExamList.value.filter(myExam => myExam.markState !== 3)
})
const myExamGroup = computed(() => {
    return myExamList.value.reduce((groups, myExam) => {
        const date = myExam.examStartTime.substring(0, 10);
        if (!groups[date]) {
            groups[date] = [];
        }
        groups[date].push(myExam);
        return groups;
    }, {});
});
const hasFinishedTask = computed(() => (date: string): boolean => {
    if (userStore.type === 0 || userStore.type === 2) {
        if (!myMarkGroup.value[date]) {// 当天没有任务
            return false
        }
        return (myMarkGroup.value[date] == null || myMarkGroup.value[date].every((myMark: any) => myMark.examMarkState === 3)) // 当天阅卷任务都完成
    }

    if (userStore.type === 1) {
        if (!myExamGroup.value[date]) {// 当天没有任务
            return false
        }
        return (myExamGroup.value[date] == null || myExamGroup.value[date].every((myExam: any) => myExam.markState === 3)) // 当天考试任务都完成
    }
    if (userStore.type === 3) {
        if (!myMarkGroup.value[date]) {// 当天没有任务
            return false
        }
        return (myMarkGroup.value[date] == null || myMarkGroup.value[date].every((myMark: any) => myMark.examMarkState === 3)) // 当天阅卷任务都完成
    }
    return false
})
const hasOngoingTask = computed(() => (date: string): boolean => {
    if (userStore.type === 0 || userStore.type === 2) {
        if (!myMarkGroup.value[date]) {// 当天没有任务
            return false
        }

        return (myMarkGroup.value[date] != null && myMarkGroup.value[date]?.some((myMark: any) => myMark.examMarkState !== 3)) // 只要有一个阅卷任务未完成
    }

    if (userStore.type === 1) {
        if (!myExamGroup.value[date]) {// 当天没有任务
            return false
        }

        return (myExamGroup.value[date] != null && myExamGroup.value[date]?.some((myExam: any) => myExam.markState !== 3)) // 只要有一个考试任务未完成

    }
    if (userStore.type === 3) {
        if (!myMarkGroup.value[date]) {// 当天没有任务
            return false
        }

        return (myMarkGroup.value[date] != null && myMarkGroup.value[date]?.some((myMark: any) => myMark.examMarkState !== 3)) // 只要有一个阅卷任务未完成
    }
    return false
})

/************************事件相关*****************************/
// 日历查询
async function calendarQuery() {
    const { data: { data: data } } = await loginSysTime({})
    const curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
    calendar.value = curTime
}

// 我的阅卷列表
async function myMarkListQuery() {
    let curPage = 1
    const pageSize = 100
    while (true) {
        const { data: { data } } = await myMarkListpage({
            curPage: curPage++,
            pageSize: pageSize,
        })
        myMarkList.value.push(...data.list)

        if (myMarkList.value.length >= data.total) {
            break
        }
    }
}

// 练习列表
async function exerListQuery() {
    let curPage = 1
    const pageSize = 100
    while (true) {
        const { data: { data } } = await exerListpage({
            state: 1,
            curPage: curPage++,
            pageSize: pageSize,
        })
        exerList.value.push(...data.list)
        if (exerList.value.length >= data.total) {
            break
        }
    }
}

// 我的考试列表
async function myExamListQuery() {
    let curPage = 1
    const pageSize = 100
    while (true) {
        const { data: { data } } = await myExamListpage({
            curPage: curPage++,
            pageSize: pageSize,
        })
        myExamList.value.push(...data.list)
        if (myExamList.value.length >= data.total) {
            break
        }
    }
}

// 公告查询
async function bulletinListQuery() {
    const { data: { data } } = await bulletinListpage({
        curPage: 1,
        pageSize: 10,
        notice: true,
    })

    bulletinList.value = data.list
    // .map((bulletin: any) => {
    //     bulletin.content = bulletin.content.replaceAll('\n', '<br/>')
    //     return bulletin
    // })
}

// 公告详情
function bulletinDetail(bulletin: Bulletin) {
    router.push(`/bulletin/${bulletin.id}`)
}
</script>

<style lang="scss" scoped>
.home {
    width: 1200px;
    display: flex;
    flex-direction: column;
    margin-bottom: 20px;

    .home__head {
        .notice {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0px 20px;
            height: 60px;
            margin-top: 20px;
            background: #FFF6DE;
            border-radius: 15px 15px 15px 15px;

            .notice__outer {
                flex: 1;
                display: flex;
                align-items: center;

                .notice__tip-icon {
                    font-size: 20px;
                    font-weight: bold;
                    color: #CE7D42;
                    line-height: 48px;
                }

                .notice__tip {
                    margin-left: 5px;
                    font-size: 14px;
                    font-weight: bold;
                    color: #CE7D42;
                    line-height: 48px;
                }

                :deep(.el-carousel) {
                    flex: 1;
                    height: 20px;

                    .notice__inner {
                        margin-top: 2px;
                        display: flex;
                        align-items: center;
                        cursor: pointer;

                        .notice__time {
                            margin-left: 20px;
                            font-size: 14px;
                            color: #CE7D42;
                        }

                        .notice__content {
                            display: inline-block;
                            width: 800px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            margin-left: 20px;
                            font-size: 14px;
                            color: #CE7D42;
                        }
                    }
                }
            }

            .notice__more {
                font-weight: bold;
                font-size: 14px;
                color: #1EA1EE;
            }
        }
    }

    .home__main {
        flex: 1;
        display: flex;

        .task-wrap {
            flex: 1;
            margin-right: 20px;

            .task {
                display: flex;
                flex-direction: column;
                margin-top: 20px;

                .task__head {
                    margin-bottom: 10px;
                }

                .task__main {
                    display: grid;
                    grid-template-columns: repeat(2, 1fr);
                    gap: 20px 20px;



                    .my-mark {
                        display: flex;
                        flex-direction: column;

                        .my-mark__tag-wrap {
                            display: flex;
                            justify-content: center;
                            margin-top: 15px;

                            .my-mark__tag {
                                font-size: 12px;
                                padding: 2px 4px;
                                border-radius: 4px 4px 4px 4px;
                                margin-right: 10px;

                                &.my-mark__tag--gen-type {
                                    color: #0D9DF6;
                                    background: #E4F6FF;
                                    border: 1px solid #A1E0FF;
                                }

                                &.my-mark__tag--login-type {
                                    color: #FC8113;
                                    background: #FDEDD9;
                                    border: 1px solid #FFC791;
                                }

                                &.my-mark__tag--mark-state {
                                    color: #FE7068;
                                    background: #FFE6E6;
                                    border: 1px solid #FFCAC7;
                                }

                                &.my-mark__tag--state {
                                    color: #1AC693;
                                    background: #E8F9F4;
                                    border: 1px solid #AFE7D6;
                                }
                            }

                        }

                        .my-mark__row {
                            margin-top: 10px;

                            .my-mark__exam-time {
                                font-size: 14px;
                                color: #999999;

                                .my-mark__time {
                                    font-size: 14px;
                                    color: #333333;
                                }
                            }
                        }

                        .my-mark__row1 {
                            display: grid;
                            grid-template-rows: repeat(2, 1fr);
                            grid-template-columns: repeat(4, 1fr);
                            margin-top: 15px;
                            height: 90px;
                            background: #EFF5FA;
                            border-radius: 6px 6px 6px 6px;

                            .my-mark__inner {
                                display: flex;
                                flex-direction: column;
                                align-items: center;
                                justify-content: center;
                                position: relative;

                                &::after {
                                    content: "";
                                    position: absolute;
                                    display: block;
                                    right: 0;
                                    width: 1px;
                                    height: 33px;
                                    background-color: #E5E5E5;
                                }

                                &:last-child {
                                    &::after {
                                        display: none;
                                    }
                                }

                                &:nth-last-child(5) {
                                    &::after {
                                        display: none;
                                    }
                                }


                                .my-mark__value {
                                    font-size: 14px;
                                    color: #333333;

                                    .my-mark__unit {
                                        font-size: 12px;
                                        color: #8F939C;
                                    }
                                }

                                .my-mark__lab {
                                    font-size: 12px;
                                    color: #8F939C;
                                    margin-top: 2px;
                                }
                            }
                        }
                    }

                    .my-exam {
                        display: flex;
                        flex-direction: column;
                        height: 220px;

                        .my-exam__exam-time {
                            display: flex;
                            justify-content: center;
                            margin-top: 10px;
                            font-size: 14px;
                            color: #8F939C;
                        }

                        .my-exam__outer {
                            display: grid;
                            grid-template-columns: repeat(4, 1fr);
                            height: 74px;
                            justify-content: center;
                            align-items: center;
                            margin-top: 10px;
                            background: #EFF5FA;
                            border-radius: 6px 6px 6px 6px;

                            .my-exam__inner {
                                display: flex;
                                flex-direction: column;
                                justify-content: center;
                                align-items: center;
                                position: relative;

                                &::after {
                                    content: "";
                                    position: absolute;
                                    display: block;
                                    right: 0;
                                    width: 1px;
                                    height: 33px;
                                    background-color: #E5E5E5;
                                }

                                &:last-child {
                                    &::after {
                                        display: none;
                                    }
                                }

                                .my-exam__num {
                                    font-size: 16px;
                                    color: #333333;

                                    .my-exam__unit {
                                        font-size: 10px;
                                        color: #8F939C;
                                    }
                                }


                                .my-exam__after-txt {
                                    font-size: 12px;
                                    color: #8F939C;
                                    flex: 1 0 100%;
                                    margin-left: 6px;
                                    line-height: 26px;
                                }
                            }
                        }

                        .my-exam__other {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-top: 16px;
                            font-size: 12px;
                            color: #8F939C;

                            .my-exam__tag {
                                font-size: 12px;
                                padding: 2px 4px;
                                border-radius: 4px 4px 4px 4px;
                                margin-right: 10px;

                                &.my-exam__tag--state {
                                    color: #FE7068;
                                    background: #FFE6E6;
                                    border: 1px solid #FFCAC7;
                                }

                                &.my-exam__tag--mark-state {
                                    color: #1AC693;
                                    background: #E8F9F4;
                                    border: 1px solid #AFE7D6;
                                }
                            }

                            .my-exam__btn {
                                height: 30px;
                                padding: 0px 20px;
                                border-radius: 6px;
                                border: 0px;
                                color: #FFFFFF;
                                font-size: 14px;
                                background-image: linear-gradient(to right, #04C7F2, #259FF8);
                            }
                        }
                    }

                    .exer {
                        display: flex;
                        flex-direction: column;
                        height: 220px;

                        .exer__state {
                            display: flex;
                            justify-content: center;
                            align-items: baseline;
                            margin-top: 15px;

                            .exer__pre-txt {
                                font-size: 12px;
                                color: #8F939C;
                                margin-right: 20px;

                                .exer__num {
                                    font-size: 12px;
                                    color: #333333;
                                }
                            }
                        }

                        .exer__outer {
                            display: grid;
                            grid-template-columns: repeat(2, 1fr);
                            height: 74px;
                            justify-content: center;
                            align-items: center;
                            margin-top: 10px;
                            background: #EFF5FA;
                            border-radius: 6px 6px 6px 6px;

                            .exer__inner {
                                display: flex;
                                flex-direction: column;
                                justify-content: center;
                                align-items: center;
                                position: relative;

                                &::after {
                                    content: "";
                                    position: absolute;
                                    display: block;
                                    right: 0;
                                    width: 1px;
                                    height: 33px;
                                    background-color: #E5E5E5;
                                }

                                &:last-child {
                                    &::after {
                                        display: none;
                                    }
                                }

                                .exer__num {
                                    font-size: 16px;
                                    color: #333333;

                                    .exer__unit {
                                        font-size: 10px;
                                        color: #8F939C;
                                    }
                                }


                                .exer__after-txt {
                                    font-size: 12px;
                                    color: #8F939C;
                                    flex: 1 0 100%;
                                    margin-left: 6px;
                                    line-height: 26px;
                                }
                            }
                        }

                        .exer__other {
                            display: flex;
                            justify-content: space-between;
                            margin-top: 20px;
                            font-size: 12px;
                            color: #8F939C;
                        }
                    }

                    .my-exer {
                        display: flex;
                        flex-direction: column;
                        height: 220px;

                        .my-exer__exam-time {
                            display: flex;
                            justify-content: center;
                            align-items: baseline;
                            margin-top: 10px;

                            .my-exer__pre-txt {
                                font-size: 12px;
                                color: #8F939C;
                                margin-right: 20px;

                                .my-exer__num {
                                    font-size: 16px;
                                    color: #333333;
                                }
                            }
                        }

                        .my-exer__outer {
                            display: grid;
                            grid-template-columns: repeat(5, 1fr);
                            height: 74px;
                            justify-content: center;
                            align-items: center;
                            margin-top: 10px;
                            background: #EFF5FA;
                            border-radius: 6px 6px 6px 6px;

                            .my-exer__inner {
                                display: flex;
                                flex-direction: column;
                                justify-content: center;
                                align-items: center;
                                position: relative;

                                &::after {
                                    content: "";
                                    position: absolute;
                                    display: block;
                                    right: 0;
                                    width: 1px;
                                    height: 33px;
                                    background-color: #E5E5E5;
                                }

                                &:last-child {
                                    &::after {
                                        display: none;
                                    }
                                }

                                .my-exer__num {
                                    font-size: 16px;
                                    color: #333333;

                                    .my-exer__unit {
                                        font-size: 10px;
                                        color: #8F939C;
                                    }
                                }


                                .my-exer__after-txt {
                                    font-size: 12px;
                                    color: #8F939C;
                                    flex: 1 0 100%;
                                    margin-left: 6px;
                                    line-height: 26px;
                                }
                            }
                        }

                        .my-exer__other {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-top: 16px;
                            font-size: 12px;
                            color: #8F939C;

                            .my-exer__btn {
                                height: 30px;
                                padding: 0px 20px;
                                border-radius: 6px;
                                border: 0px;
                                color: #FFFFFF;
                                font-size: 14px;
                                background-image: linear-gradient(to right, #04C7F2, #259FF8);
                            }
                        }
                    }
                }
            }
        }

        :deep(.calendar) {
            display: flex;
            flex-direction: column;
            width: 360px;
            margin-top: 20px;
            overflow: hidden;

            .calendar__head {
                margin-bottom: 10px;
            }

            .calendar__main {
                border-radius: 15px 15px 0px 0px;

                .el-calendar__header {
                    border-bottom: 0px;
                    padding: 12px 20px 0px 20px;

                    .calendar__opt {
                        flex: 1;
                        display: flex;
                        justify-content: space-between;

                        .calendar__btn {
                            width: 34px;
                            height: 24px;
                            background: #F2F6F9;
                            border-radius: 4px 4px 4px 4px;

                            .calendar__btn-icon {
                                font-size: 12px;
                                font-weight: bold;
                                color: #1EA1EE;
                            }
                        }

                        .calendar__ymd {
                            font-size: 18px;
                            color: #222222;
                        }
                    }
                }



                .el-calendar__body {
                    padding: 10px 20px 20px 20px;

                    table {
                        thead {
                            tr {
                                height: 32px;

                                th {
                                    padding: 0px;
                                    background-color: #F2F6F9;
                                    font-size: 14px;
                                    color: #333333;
                                }
                            }
                        }


                        tbody {
                            tr {

                                td {
                                    border: 0px;

                                    &.is-selected {
                                        background-color: #FFFFFF;

                                        .el-calendar-day {

                                            .calendar__date {
                                                width: 24px;
                                                height: 24px;
                                                border-radius: 50%;
                                                background: linear-gradient(to bottom, #04C7F2 0%, #259FF8 100%);

                                                .calendar__date-txt {
                                                    font-size: 14px;
                                                    color: #FFFFFF !important;
                                                }
                                            }
                                        }
                                    }

                                    &.is-today {
                                        .el-calendar-day {
                                            .calendar__date {
                                                .calendar__date-txt {
                                                    font-size: 14px;
                                                    color: #1EA1EE;
                                                }
                                            }
                                        }
                                    }

                                    .calendar__date--finished {
                                        &::after {

                                            content: '';
                                            position: absolute;
                                            bottom: 0px;
                                            transform: translateX(-50%);
                                            left: 50%;
                                            width: 4px;
                                            height: 4px;
                                            border-radius: 50%;
                                            background: #999999;
                                        }
                                    }

                                    .calendar__date--ongoing {
                                        &::after {

                                            content: '';
                                            position: absolute;
                                            bottom: 0px;
                                            transform: translateX(-50%);
                                            left: 50%;
                                            width: 4px;
                                            height: 4px;
                                            border-radius: 50%;
                                            background: #17C990;
                                        }
                                    }

                                    .calendar__date--pending {
                                        &::after {

                                            content: '';
                                            position: absolute;
                                            bottom: 0px;
                                            transform: translateX(-50%);
                                            left: 50%;
                                            width: 4px;
                                            height: 4px;
                                            border-radius: 50%;
                                            background: #F2B95D;
                                        }
                                    }

                                    &.prev,
                                    &.next {
                                        .el-calendar-day {
                                            display: flex;
                                            justify-content: center;
                                            align-items: center;
                                            position: relative;
                                            height: 36px;
                                            padding: 0px;
                                            background-color: initial;

                                            :hover {
                                                background-color: initial;
                                            }

                                            .calendar__date {
                                                display: flex;
                                                justify-content: center;
                                                align-items: center;

                                                .calendar__date-txt {
                                                    font-size: 14px;
                                                    color: #CCCCCC;
                                                }
                                            }
                                        }
                                    }

                                    &.current {
                                        .el-calendar-day {
                                            display: flex;
                                            justify-content: center;
                                            align-items: center;
                                            position: relative;
                                            padding: 0px;
                                            height: 36px;
                                            background-color: initial;

                                            :hover {
                                                background-color: initial;
                                            }

                                            .calendar__date {
                                                display: flex;
                                                justify-content: center;
                                                align-items: center;

                                                .calendar__date-txt {
                                                    font-size: 14px;
                                                    color: #777777;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

            .calendar__foot {
                border-radius: 0px 0px 15px 15px;
                padding: 0px 20px 0px 20px;
                background-color: #FFFFFF;


                .calendar__task-list {
                    border-top: 1px solid #E5E5E5;
                    padding: 15px 0px;

                    .calendar-task {
                        position: relative;
                        padding-left: 10px;

                        &::before {
                            content: '';
                            position: absolute;
                            top: 8px;
                            left: 0px;
                            width: 4px;
                            height: 4px;
                            border-radius: 50%;
                            background-color: #999999;
                        }

                        .calendar-task__outer {
                            margin-top: 12px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;

                            .calendar-task__name {
                                max-width: 190px;
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                                font-size: 14px;
                                color: #333333;
                            }

                            .calendar-task__inner {
                                display: flex;

                                .calendar-task__type {
                                    display: flex;
                                    align-items: center;
                                    height: 20px;
                                    padding: 0px 5px;
                                    font-size: 14px;
                                    border: 1px solid transparent;
                                    border-radius: 4px;
                                    background-clip: padding-box, border-box;
                                    background-origin: padding-box, border-box;
                                    background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #259ff8, #04c7f2);
                                    color: #04c7f2;
                                }

                                .calendar-task__state {
                                    display: flex;
                                    align-items: center;
                                    height: 20px;
                                    margin-left: 5px;
                                    padding: 0px 5px;
                                    border-radius: 4px;
                                    font-size: 14px;


                                    &.calendar__date--finished {
                                        border: 1px solid #E5E5E5;
                                        background-color: #F9F9F9;
                                        color: #999999;
                                    }

                                    &.calendar__date--ongoing {
                                        color: #1AC693;
                                        background: #E8F9F4;
                                        border: 1px solid #AFE7D6;
                                    }

                                    &.calendar__date--pending {
                                        color: #FC8113;
                                        background: #FDEDD9;
                                        border: 1px solid #FFC791;
                                    }
                                }
                            }
                        }



                        .calendar-task__content {
                            .calendar-task__time {
                                font-size: 12px;
                                color: #8F939C;
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>
