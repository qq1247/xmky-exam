<template>
    <div class="my-exer-read">
        <div class="my-exer__side">
            <div class="user">
                <div class="user__avatar">
                    <span class="iconfont icon-rentouxiang user__avatar-icon"></span>
                </div>
                <span class="user__label">账号：<span class="user__value">{{ user.loginName }}</span></span>
                <span class="user__label">姓名：<span class="user__value">{{ user.name }}</span></span>
                <span class="user__label">机构：<span class="user__value">{{ user.orgName }}</span></span>
            </div>
            <div class="notes">
                <xmks-card-guide title="注意事项" icon="icon-tubiaoziti-34" class="notes__head"></xmks-card-guide>
                <div class="notes__main">
                    <span class="notes__txt">
                        1、进入此页面，会自动增量更新最新试题到自己的练习<br />
                        2、答错试题会收集到历史错题<br />
                        3、需要重新练题，请点“重新练习”按钮<br />
                    </span>
                </div>
            </div>
        </div>
        <el-scrollbar max-height="calc(100vh - 104px)" class="my-exer-read__main">
            <div class="paper">
                <xmks-card-guide title="题库信息" icon="icon-tubiaoziti-35" class="paper__head"></xmks-card-guide>
                <div class="paper__main">
                    <div class="paper-info__wrap">
                        <div class="paper-info">
                            <span class="paper-info__label">客观题</span>
                            <span class="paper-info__num">
                                {{ markTypeStatis.objective }}<span class="paper-info__unit">道</span>
                            </span>
                        </div>
                        <div class="paper-info">
                            <span class="paper-info__label">主观题</span>
                            <span class="paper-info__num">
                                {{ markTypeStatis.subjective }}<span class="paper-info__unit">道</span>
                            </span>
                        </div>
                        <div class="paper-info">
                            <span class="paper-info__label">章节</span>
                            <span class="paper-info__num">
                                {{ markTypeStatis.chapter }}<span class="paper-info__unit">节</span>
                            </span>
                        </div>
                    </div>
                    <div class="paper-statis">
                        <v-chart :option="questionStatisOpts" />
                    </div>
                </div>
            </div>
            <div class="exer">
                <xmks-card-guide title="练习信息" icon="icon-icon_xiugaishijian" class="exer__head"></xmks-card-guide>
                <div class="exer__main">
                    <!-- <span class="exer__label">
                        练习名称：<span class="exer__value">{{ exer.name }}</span>
                    </span>
                    <span class="exer__label">
                        发布状态：<span class="exer__value">{{ dictStore.getValue('STATE_PS', exer.state as number) }}</span>
                    </span> -->
                    <!-- <span class="exer__label">
                        允许评论：<span class="exer__value">{{ dictStore.getValue('STATE_YN', exer.rmkState as number)
                            }}</span>
                    </span> -->
                    <v-chart :option="exerTimeStatisOpts" class="exer__statis" />
                </div>
            </div>
            <div class="my-exer">
                <xmks-card-guide title="我的练习" icon="icon-icon_xiugaishijian" class="my-exer__head"></xmks-card-guide>
                <div class="my-exer__main">
                    <div v-if="progressBar.percent !== 100" class="create-progress">
                        <el-progress :percentage="40" class="create-progress__progress" />
                        <div class="create-progress__inner">
                            <span class="create-progress__title">{{ progressBar.title }}</span>
                            <span class="create-progress__desc">{{ progressBar.msg }}</span>
                        </div>
                    </div>
                    <el-scrollbar v-if="progressBar.percent === 100" max-height="290px" class="history">
                        <div v-if="pullInfo.questionBankUpdateNum" class="history__head">新增试题{{
                            pullInfo.questionBankUpdateNum }}道</div>
                        <div v-for="(exer, index) in listpage.list" :key="index" class="history__list">
                            <div class="history__row">
                                <span class="history__title">{{ dictStore.getValue('QUESTION_TYPE', exer.type)
                                }}题</span>
                                <span class="history__btn"
                                    @click="$router.push(`/my-exer/paper/${form.exerId}/${exer.type}`)">>>继续训练</span>
                            </div>
                            <div class="history__row">
                                <span class="history__progress">进度：{{ exer.answerNum }}/{{ exer.questionNum }}</span>
                                <span class="history__correct-rate">正确率：
                                    {{ new
                                        Decimal(exer.correctAnswerNum).dividedBy(exer.answerNum).times(100).toDecimalPlaces(0).toNumber()
                                    }}%</span>
                            </div>
                        </div>
                        <xmks-card-empty v-if="listpage.list.length === 0" name="暂无最近练习"
                            icon="icon-tubiaoziti22-22"></xmks-card-empty>
                    </el-scrollbar>
                    <div v-if="progressBar.percent === 100" class="my-exer__opt-panel">
                        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large"
                            class="form">
                            <el-form-item label="题型练习：" prop="type">
                                <el-radio-group v-model="form.type">
                                    <el-radio v-for="dict in dictStore.getList('QUESTION_TYPE')" :key="dict.dictKey"
                                        border :value="parseInt(dict.dictKey)"
                                        :disabled="typeStatis.find((item: any) => item.type == dict.dictKey)?.count === 0">
                                        {{ dict.dictValue }}题（{{ pullInfo.questionTypeStatis[dict.dictKey] || 0 }}道）
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item label="巩固练习：" prop="type">
                                <el-radio-group v-model="form.type">
                                    <el-radio border :value="11" :disabled="pullInfo.wrongAnswerNum === 0">
                                        历史错题（{{ pullInfo.wrongAnswerNum }}道）
                                    </el-radio>
                                    <el-radio border :value="12" :disabled="pullInfo.favNum === 0">
                                        我的收藏（{{ pullInfo.favNum }}道）
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" class="form__btn" @click="toExer">进入练习</el-button>
                                <el-button v-if="form.type >= 1 && form.type <= 5" type="primary"
                                    class="form__btn form__btn--secondary" @click="reset">重新练习</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive } from 'vue'
import XmksCardGuide from '@/components/card/xmks-card-guide.vue'
import { ref } from 'vue'
import { useDictStore } from '@/stores/dict'
import type { User } from '@/ts/base/user'
import { userGet } from '@/api/base/user'
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'
import { dayjs, type FormRules } from 'element-plus'
import { myExerExerGet, myExerListpage, myExerPull, myExerQuestionStatis, myExerExerReset, myExerTrackList } from '@/api/my/my-exer'
import type { Exer } from '@/ts/exam/exer'
import type { Listpage } from '@/ts/common/listpage'
import Decimal from 'decimal.js-light'
import xmksCardEmpty from '@/components/card/xmks-card-empty.vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, ToolboxComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { loginSysTime } from '@/api/login'

/************************变量定义相关***********************/
use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent, ToolboxComponent, GridComponent, LineChart, UniversalTransition]);

const dictStore = useDictStore(); // 字典缓存
const userStore = useUserStore(); // 字典缓存
const route = useRoute()// 路由
const router = useRouter()// 路由
const form = reactive<any>({
    exerId: route.params.exerId,
    type: 1,
})
const formRules = reactive<FormRules>({// 表单规则
})

const listpage = reactive<Listpage>({// 我的练习分页列表
    curPage: 1,
    pageSize: 20,
    total: 0,
    list: [],
})

const user = reactive<User>({ // 用户
    id: null,
    name: '',
    loginName: '',
    type: null,
    state: null,
    orgName: ''
})
const exer = reactive<Exer>({// 练习
    id: null,
    name: '',
    questionBankId: undefined,
    userIds: [],
    orgIds: [],
    state: null,
    rmkState: null
})

const markTypeStatis = reactive({ // 阅卷类型统计
    subjective: 0,
    objective: 0,
    chapter: 0,
})
const questionStatisOpts = ref({// 试题类型统计
    title: {
        text: '',
        left: 'center',
    },
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)',
    },
    series: [
        {
            name: '试题统计',
            type: 'pie',
            radius: '100%',
            center: ['50%', '50%'],
            data: [
                // { value: 20, name: '单选题' },
                // { value: 20, name: '多选题' },
                // { value: 10, name: '填空题' },
                // { value: 10, name: '判断题' },
                // { value: 2, name: '问答题' },
            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
            },
            color: [
                '#4692D8', '#06BCE3', '#978CEC', '#DD8CEC', '#85e3a4'
            ],
        },
    ],
})

// const typeCache: Record<number, string> = reactive({ 1: '单选题', 2: '多选题', 3: '填空题', 4: '判断题', 5: '问答题', });

const typeStatis = ref<any[]>([])

const progressBar = reactive({// 进度条
    percent: 0,// 进度
    title: '', // 响应码
    msg: '', // 响应消息
})
const pullInfo = reactive({
    questionTypeStatis: {} as any,
    questionBankUpdateNum: 0,
    wrongAnswerNum: 0,
    favNum: 0,
});// 拉取信息

/************************组件生命周期相关*********************/
onMounted(async () => {
    userQuery()
    exerQuery()
    questionStatisQuery()
    exerTimeStatisQuery()

    await pull()
    myExerQuery()
})

/************************计算属性相关*************************/
const exerTimeStatisXAxis = computed<string[]>(() => {
    return exerTimeStatisData.value.flatMap(item => Object.keys(item))
})
const exerTimeStatisYAxis = computed<number[]>(() => {
    return exerTimeStatisData.value.flatMap(item => Object.values(item))
})
const exerTimeStatisTitle = computed<string>(() => {
    return exer.name
})
const exerTimeStatisData = ref<Record<string, number>[]>([])
const exerTimeStatisOpts = ref({// 练习时长统计
    title: {
        text: exerTimeStatisTitle, // 直接exer.name不是响应式的
        left: 'center'
    },
    tooltip: {
        show: true,
        trigger: 'axis',
        formatter: '{b}<br/>练习{c}分钟'
    },
    // toolbox: {
    //     feature: {
    //         myDay: {
    //             show: true,
    //             title: '按日统计',
    //             icon: 'image://data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgZmlsbD0iIzAwMCI+CiAgPHJlY3QgeD0iMiIgeT0iNCIgd2lkdGg9IjEyIiBoZWlnaHQ9IjEyIiByeD0iMSIgcnk9IjEiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMCIgc3Ryb2tlLXdpZHRoPSIxIi8+CiAgPHJlY3QgeD0iNCIgeT0iMiIgd2lkdGg9IjIiIGhlaWdodD0iMiIgZmlsbD0iIzAwMCIvPgogIDxyZWN0IHg9IjEwIiB5PSIyIiB3aWR0aD0iMiIgaGVpZ2h0PSIyIiBmaWxsPSIjMDAwIi8+CiAgPHRleHQgeD0iOSIgeT0iMTIiIGZvbnQtc2l6ZT0iOCIgZm9udC13ZWlnaHQ9ImJvbGQiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGRvbWluYW50LWJhc2VsaW5lPSJtaWRkbGUiPuWPtzwvdGV4dD4KPC9zdmc+',
    //             onclick: function () {
    //             }
    //         },
    //         myDay2: {
    //             show: true,
    //             title: '按月统计',
    //             icon: 'image://data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgZmlsbD0iIzAwMCI+CiAgPHJlY3QgeD0iMiIgeT0iNCIgd2lkdGg9IjEyIiBoZWlnaHQ9IjEyIiByeD0iMSIgcnk9IjEiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMCIgc3Ryb2tlLXdpZHRoPSIxIi8+CiAgPHJlY3QgeD0iNCIgeT0iMiIgd2lkdGg9IjIiIGhlaWdodD0iMiIgZmlsbD0iIzAwMCIvPgogIDxyZWN0IHg9IjEwIiB5PSIyIiB3aWR0aD0iMiIgaGVpZ2h0PSIyIiBmaWxsPSIjMDAwIi8+CiAgPHRleHQgeD0iOSIgeT0iMTIiIGZvbnQtc2l6ZT0iOCIgZm9udC13ZWlnaHQ9ImJvbGQiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGRvbWluYW50LWJhc2VsaW5lPSJtaWRkbGUiPuaXnTwvdGV4dD4KPC9zdmc+',
    //             onclick: function () {
    //             }
    //         }
    //     },
    // },
    xAxis: {
        type: 'category',
        data: exerTimeStatisXAxis
    },
    yAxis: {
        type: 'value',
    },
    series: [
        {
            data: exerTimeStatisYAxis,
            type: 'line'
        }
    ],
    grid: {
        left: '10',
        right: '10',
        top: '10',
        bottom: '0',
        containLabel: true
    },
})

/************************事件相关*****************************/
// 用户查询
async function userQuery() {
    const { data: { data } } = await userGet({ id: userStore.id })
    user.id = data.id;
    user.name = data.name;
    user.loginName = data.loginName;
    user.orgName = data.orgName;
}

// 练习查询
async function exerQuery() {
    const { data: { data } } = await myExerExerGet({ exerId: route.params.exerId })
    exer.id = parseInt(route.params.exerId as string)
    exer.name = data.name
    exer.questionBankId = data.questionBankId
    exer.state = data.state
    exer.rmkState = data.rmkState
}

// 试题统计查询
async function questionStatisQuery() {
    const { data: { data: { typeStatis: _typeStatis, markTypeStatis: _markTypeStatis } } } = await myExerQuestionStatis({ exerId: route.params.exerId })
    const datas = (_typeStatis as any[]).map((d) => {
        return {
            name: dictStore.getValue('QUESTION_TYPE', d.type),
            value: d.count
        };
    })
    questionStatisOpts.value.series[0].data = datas as never[]
    markTypeStatis.subjective = _markTypeStatis.subjective;
    markTypeStatis.objective = _markTypeStatis.objective;
    markTypeStatis.chapter = _markTypeStatis.chapter;

    typeStatis.value = _typeStatis
}

// 我的练习拉取
async function pull() {
    progressBar.percent = 20;
    progressBar.title = '正在更新最新试题'
    progressBar.msg = '请等待。。。'

    const { data: { code, msg, data } } = await myExerPull({
        exerId: route.params.exerId
    })

    if (code !== 200) {
        progressBar.percent = 99;
        progressBar.title = '拉取错误'
        progressBar.msg = msg
        return
    }

    pullInfo.questionTypeStatis = data.questionTypeStatis
    pullInfo.questionBankUpdateNum = data.questionBankUpdateNum
    pullInfo.wrongAnswerNum = data.wrongAnswerNum
    pullInfo.favNum = data.favNum

    progressBar.percent = 100;
    progressBar.title = '拉取成功'
    progressBar.msg = ''
}

// 我的练习查询
async function myExerQuery() {
    const { data: { code, data } } = await myExerListpage({
        exerId: route.params.exerId,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    listpage.list = data.list
    listpage.total = data.total
}

// 去练习
async function toExer() {
    router.push(`/my-exer/paper/${form.exerId}/${form.type}`)
}

// 重新练习
async function reset() {
    if (form.type === 11 || form.type === 12) {
        return;
    }

    const { data: { code } } = await myExerExerReset({
        exerId: route.params.exerId,
        type: form.type,
    })

    if (code !== 200) {
        return
    }

    toExer()
}

// 练习时间统计（近一年）
async function exerTimeStatisQuery() {
    const { data: { data } } = await loginSysTime({})
    const startTime = dayjs(data).subtract(1, 'year').startOf('month').valueOf(); // 去年同月的第一天（去年的今天会丢数据）
    const endTime = dayjs(data).startOf('day').valueOf();// 今天

    const { data: { data: myExerTracks } } = await myExerTrackList({
        exerId: route.params.exerId,
        startDate: dayjs(startTime).format('YYYY-MM-DD'),
        endDate: dayjs(endTime).format('YYYY-MM-DD'),
    })

    const myExerTrackCache = myExerTracks.reduce((preMyExerTrack: Record<string, number>, curMyExerTrack: any) => {
        const ym = dayjs(curMyExerTrack.ymd).format('YYYY-MM');
        preMyExerTrack[ym] = (preMyExerTrack[ym] || 0) + curMyExerTrack.minuteCount;
        return preMyExerTrack;
    }, {})

    let curMonth = dayjs(startTime);
    const endMonth = dayjs(endTime).startOf('month');
    while (curMonth.isBefore(endMonth) || curMonth.isSame(endMonth, 'month')) {
        const ym = curMonth.format('YYYY-MM');
        exerTimeStatisData.value.push({
            [ym]: myExerTrackCache[ym] || 0
        });
        curMonth = curMonth.add(1, 'month');
    }
}

</script>
<style lang="scss" scoped>
.my-exer-read {
    display: flex;
    width: 1200px;
    margin: 20px 0px;

    .my-exer__side {
        display: flex;
        flex-direction: column;
        width: 270px;
        margin-right: 20px;

        .user {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            position: relative;
            background-color: #FFFFFF;
            height: 180px;
            padding: 20px 0px;
            border-radius: 15px 15px 15px 15px;
            margin-top: 30px;

            .user__avatar {
                display: flex;
                justify-content: center;
                align-items: center;
                position: absolute;
                width: 70px;
                height: 70px;
                border: 5px solid #FFFFFF;
                background-color: #F0FAFF;
                top: 0px;
                left: 50%;
                transform: translateX(-50%) translateY(-50%);
                border-radius: 50%;

                .user__avatar-icon {
                    font-size: 48px;
                    color: #04C7F2;
                }
            }

            .user__label {
                font-size: 14px;
                color: #8F939C;
                line-height: 28px;

                .user__value {
                    color: #333333;
                }
            }
        }

        .notes {
            flex: 1;
            display: flex;
            flex-direction: column;
            margin-top: 20px;

            .notes__head {
                margin-bottom: 8px;
            }

            .notes__main {
                flex: 1;
                display: flex;
                background-color: #FFFFFF;
                height: 270px;
                padding: 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .notes__txt {
                    font-size: 14px;
                    color: #E43D33;
                    line-height: 36px;
                }
            }
        }
    }

    .my-exer-read__main {
        flex: 1;

        .paper {
            .paper__head {
                margin-bottom: 8px;
            }

            .paper__main {
                display: flex;
                background-color: #FFFFFF;
                height: 180px;
                padding: 20px 0px;
                border-radius: 15px 15px 15px 15px;

                .paper-info__wrap {
                    flex: 1;
                    display: grid;
                    grid-template-columns: repeat(3, 1fr);
                    border-right: 1px solid #E5E5E5;

                    .paper-info {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;

                        .paper-info__label {
                            font-size: 14px;
                            color: #8F939C;
                        }

                        .paper-info__num {
                            margin-top: 20px;
                            font-size: 24px;
                            color: #139FF6;

                            .paper-info__unit {
                                font-size: 14px;
                                color: #8F939C;
                            }
                        }
                    }
                }

                .paper-statis {
                    flex: 1;
                    margin-left: 50px;
                }
            }
        }

        .exer {
            display: flex;
            flex-direction: column;
            margin-top: 20px;

            .exer__head {
                margin-bottom: 8px;
            }

            .exer__main {
                .exer__statis {
                    width: 850px;
                    height: 120px;
                }

                display: grid;
                grid-template-columns: repeat(3, 1fr);
                grid-template-rows: repeat(1, 1fr);
                background-color: #FFFFFF;
                padding: 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .exer__label {
                    font-size: 14px;
                    color: #8F939C;
                    line-height: 32px;

                    .exer__value {
                        color: #333333;
                    }
                }

                .exer__label-2 {
                    grid-column: span 2;
                }
            }

        }

        .my-exer {
            display: flex;
            flex-direction: column;
            margin-top: 20px;

            .my-exer__head {
                margin-bottom: 8px;
            }

            .my-exer__main {
                display: flex;
                background-color: #FFFFFF;
                height: 330px;
                padding: 20px 0px 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .create-progress {
                    display: flex;
                    flex-direction: column;
                    // width: 500px;
                    margin-top: 50px;
                    width: 100%;

                    :deep(.create-progress__progress) {
                        .el-progress-bar__inner {
                            background-image: linear-gradient(to right, #04C7F2, #259FF8);
                        }
                    }

                    .create-progress__inner {
                        display: flex;
                        flex-direction: column;
                        align-items: center;

                        .create-progress__title {
                            margin-top: 30px;
                            font-size: 16px;
                            color: #333333;
                        }

                        .create-progress__desc {
                            margin-top: 10px;
                            font-size: 14px;
                            color: #999999;
                        }
                    }
                }

                .xmks-card-empty {
                    height: 290px;
                }

                .history {
                    flex: 1;
                    padding-right: 20px;
                    border-right: 1px solid #E5E5E5;

                    .history__head {
                        font-size: 14px;
                        color: #E43D33;
                        font-weight: bold;
                    }

                    .history__list {
                        display: flex;
                        flex-direction: column;
                        border-radius: 15px 15px 15px 15px;
                        margin-top: 10px;
                        padding: 10px;
                        background-color: #efefef;
                        font-size: 14px;
                        color: #888;

                        .history__row {
                            height: 20px;
                            display: flex;
                            justify-content: space-between;

                            .history__title {
                                font-size: 14px;
                                font-weight: bold;
                                color: #303133;
                            }

                            .history__btn {
                                cursor: pointer;
                                color: #04C7F2;

                            }
                        }

                    }
                }

                .my-exer__opt-panel {
                    width: 480px;


                    .form {
                        margin-top: 20px;

                        .el-radio {
                            width: 160px;
                            margin-top: 10px;
                        }

                        .form__btn {
                            width: 114px;
                            height: 38px;
                            padding: 0px 30px;
                            border-radius: 6px;
                            border: 0px;
                            color: #FFFFFF;
                            font-size: 14px;
                            background-image: linear-gradient(to right, #04C7F2, #259FF8);

                            &.form__btn--secondary {
                                color: #04C7F2;
                                border: 1px solid #04C7F2;
                                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
                            }

                        }
                    }
                }
            }

        }
    }

}
</style>
