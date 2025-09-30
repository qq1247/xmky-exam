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
                        1、自组练习抽题：优先题库中未抽取到的试题，题数不足再补练习中未答试题，再补练习中已答试题，确保练习内容覆盖全面<br />
                        2、单次练习5分钟无操作，将视为挂机，不计入有效练习时长<br />
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
                <xmks-card-guide title="练习时长" icon="icon-icon_xiugaishijian" class="exer__head"></xmks-card-guide>
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
                    <el-scrollbar max-height="290px" class="history">
                        <div v-for="(myExer, index) in listpage.list" :key="index" class="history__list">
                            <div class="history__row">
                                <span class="history__title">{{ myExer.name }}</span>
                                <span class="history__btn"
                                    @click="$router.push(`/my-exer/paper/${form.exerId}/${myExer.id}`)">>>继续训练</span>
                            </div>
                            <div class="history__row">
                                <span class="history__progress">进度：{{ myExer.answerNum }}/{{ myExer.questionNum
                                    }}</span>
                                <span class="history__correct-rate">正确率：
                                    {{ new
                                        Decimal(myExer.correctAnswerNum).dividedBy(myExer.answerNum).times(100).toDecimalPlaces(0).toNumber()
                                    }}%</span>
                            </div>
                        </div>
                        <xmks-card-empty v-if="listpage.list.length === 0" name="暂无最近练习"
                            icon="icon-tubiaoziti22-22"></xmks-card-empty>
                    </el-scrollbar>
                    <div class="opt-panel">
                        <el-form ref="formRef" :model="form" :rules="formRules" label-width="" size="large"
                            class="form">
                            <el-form-item label="" prop="type">
                                <el-radio-group v-model="form.type" size="large" fill="#04C7F2" @change="toggleExer">
                                    <el-radio-button
                                        :label="`自组（${(myExerStatis.free?.[0]?.count ?? 0) + (myExerStatis.free?.[1]?.count ?? 0) + (myExerStatis.free?.[2]?.count ?? 0) + (myExerStatis.free?.[3]?.count ?? 0) + (myExerStatis.free?.[4]?.count ?? 0)}题）`"
                                        :value="1" />
                                    <el-radio-button
                                        :label="`未练（${(myExerStatis.unExer?.[0]?.count ?? 0) + (myExerStatis.unExer?.[1]?.count ?? 0) + (myExerStatis.unExer?.[2]?.count ?? 0) + (myExerStatis.unExer?.[3]?.count ?? 0) + (myExerStatis.unExer?.[4]?.count ?? 0)}题）`"
                                        :value="2" />
                                    <el-radio-button
                                        :label="`错题（${(myExerStatis.wrong?.[0]?.count ?? 0) + (myExerStatis.wrong?.[1]?.count ?? 0) + (myExerStatis.wrong?.[2]?.count ?? 0) + (myExerStatis.wrong?.[3]?.count ?? 0) + (myExerStatis.wrong?.[4]?.count ?? 0)}题）`"
                                        :value="3" />
                                    <el-radio-button
                                        :label="`收藏（${(myExerStatis.fav?.[0]?.count ?? 0) + (myExerStatis.fav?.[1]?.count ?? 0) + (myExerStatis.fav?.[2]?.count ?? 0) + (myExerStatis.fav?.[3]?.count ?? 0) + (myExerStatis.fav?.[4]?.count ?? 0)}题）`"
                                        :value="4" />
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item label="单选题：" prop="singleNum">
                                <div class="form__slider">
                                    <el-slider v-model="form.singleNum" show-stops :max="questionMaxNum.singleNum"
                                        class="form__slider-bar form__slider-bar--single-choice"
                                        :format-tooltip="(value: number) => `本次选择【${value || 0}】题`"
                                        :disabled="questionMaxNum.singleNum === 0"
                                        @input="nameUpdate" /><!--max变小，不触发change，改用input-->
                                    <span class="form__slider-unit form__slider-unit--single-choice">
                                        共{{ questionMaxNum.singleNum }}题
                                    </span>
                                </div>
                            </el-form-item>
                            <el-form-item label="多选题：" prop="multipleNum">
                                <div class="form__slider">
                                    <el-slider v-model="form.multipleNum" show-stops :max="questionMaxNum.multipleNum"
                                        class="form__slider-bar form__slider-bar--form__multiple-choice"
                                        :format-tooltip="(value: number) => `本次选择【${value || 0}】题`"
                                        :disabled="questionMaxNum.multipleNum === 0" @input="nameUpdate" />
                                    <span class="form__slider-unit form__slider-unit--multiple-choice">
                                        共{{ questionMaxNum.multipleNum }}题
                                    </span>
                                </div>
                            </el-form-item>
                            <el-form-item label="填空题：" prop="fillBlankNum">
                                <div class="form__slider">
                                    <el-slider v-model="form.fillBlankNum" show-stops :max="questionMaxNum.fillBlankNum"
                                        class="form__slider-bar form__slider-bar--fill-blank"
                                        :format-tooltip="(value: number) => `本次选择【${value || 0}】题`"
                                        :disabled="questionMaxNum.fillBlankNum === 0" @input="nameUpdate" />
                                    <span class="form__slider-unit form__slider-unit--fill-blank">
                                        共{{ questionMaxNum.fillBlankNum }}题
                                    </span>
                                </div>
                            </el-form-item>
                            <el-form-item label="判断题：" prop="judgeNum">
                                <div class="form__slider">
                                    <el-slider v-model="form.judgeNum" show-stops :max="questionMaxNum.judgeNum"
                                        class="form__slider-bar form__slider-bar--judge"
                                        :format-tooltip="(value: number) => `本次选择【${value || 0}】题`"
                                        :disabled="questionMaxNum.judgeNum === 0" @input="nameUpdate" />
                                    <span class="form__slider-unit form__slider-unit--judge">
                                        共{{ questionMaxNum.judgeNum }}题
                                    </span>
                                </div>
                            </el-form-item>
                            <el-form-item label="问答题：" prop="qaNum">
                                <div class="form__slider">
                                    <el-slider v-model="form.qaNum" show-stops :max="questionMaxNum.qaNum"
                                        class="form__slider-bar form__slider-bar--qa"
                                        :format-tooltip="(value: number) => `本次选择【${value || 0}】题`"
                                        :disabled="questionMaxNum.qaNum === 0" @input="nameUpdate" />
                                    <span class="form__slider-unit form__slider-unit--qa">
                                        共{{ questionMaxNum.qaNum }}题
                                    </span>
                                </div>
                            </el-form-item>
                            <el-form-item label="名 称：" prop="name" class="form__submit">
                                <el-input v-model="form.name" placeholder="请输入名称">
                                    <template #append>
                                        <el-button type="primary" :loading="loading" :disabled="loading"
                                            class="form__btn" @click="toExer">创建练习</el-button>
                                    </template>
                                </el-input>
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
import type { User } from '@/ts/base/user'
import { userGet } from '@/api/base/user'
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'
import { dayjs, type FormRules } from 'element-plus'
import { myExerAdd, myExerGet, myExerListpage, myExerTrackList } from '@/api/my/my-exer'
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
import { exerListpage } from '@/api/exam/exer'

/************************变量定义相关***********************/
use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent, ToolboxComponent, GridComponent, LineChart, UniversalTransition]);

const userStore = useUserStore(); // 用户缓存
const route = useRoute()// 路由
const router = useRouter()// 路由
const form = reactive<any>({
    exerId: route.params.exerId,
    type: 1,
    singleNum: 0,
    multipleNum: 0,
    fillBlankNum: 0,
    judgeNum: 0,
    qaNum: 0,
})
const questionMaxNum = reactive({
    singleNum: 0,
    multipleNum: 0,
    fillBlankNum: 0,
    judgeNum: 0,
    qaNum: 0,
})

const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
    ],
})

const listpage = reactive<Listpage>({// 我的练习分页列表
    curPage: 1,
    pageSize: 100,
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
    questionBankIds: [],
    userIds: [],
    orgIds: [],
    state: null,
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
            data: [],
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

const myExerStatis = reactive({
    free: [],
    unExer: [],
    wrong: [],
    fav: [],
})

const loading = ref(false)

/************************组件生命周期相关*********************/
onMounted(async () => {
    userQuery()
    questionBankStatisQuery()
    exerTimeStatisQuery()
    myExerQuery()

    await myExerStatisQuery()
    toggleExer()
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

// 我的练习统计查询
async function myExerStatisQuery() {
    const { data: { data } } = await myExerGet({ exerId: route.params.exerId })
    myExerStatis.free = data.free
    myExerStatis.unExer = data.unExer
    myExerStatis.wrong = data.wrong
    myExerStatis.fav = data.fav
}

// 题库统计查询
async function questionBankStatisQuery() {
    const { data: { code, data } } = await exerListpage({
        id: route.params.exerId,
        curPage: 1,
        pageSize: 1,
    })

    if (code !== 200) {
        return
    }
    questionStatisOpts.value.series[0].data.push({
        name: '单选题',
        value: data.list[0].singleNum
    } as never)
    questionStatisOpts.value.series[0].data.push({
        name: '多选题',
        value: data.list[0].multipleNum
    } as never)
    questionStatisOpts.value.series[0].data.push({
        name: '填空题',
        value: data.list[0].fillBlankObjNum + data.list[0].fillBlankSubNum
    } as never)
    questionStatisOpts.value.series[0].data.push({
        name: '判断题',
        value: data.list[0].judgeNum
    } as never)
    questionStatisOpts.value.series[0].data.push({
        name: '问答题',
        value: data.list[0].qaObjNum + data.list[0].qaSubNum
    } as never)


    markTypeStatis.subjective = data.list[0].subjectiveNum
    markTypeStatis.objective = data.list[0].objectiveNum
    markTypeStatis.chapter = 0
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

// 切换练习
function toggleExer() {
    if (form.type === 1) {
        questionMaxNum.singleNum = myExerStatis.free?.[0]?.count ?? 0
        questionMaxNum.multipleNum = myExerStatis.free?.[1]?.count ?? 0
        questionMaxNum.fillBlankNum = myExerStatis.free?.[2]?.count ?? 0
        questionMaxNum.judgeNum = myExerStatis.free?.[3]?.count ?? 0
        questionMaxNum.qaNum = myExerStatis.free?.[4]?.count ?? 0
    } else if (form.type === 2) {
        questionMaxNum.singleNum = myExerStatis.unExer?.[0]?.count ?? 0
        questionMaxNum.multipleNum = myExerStatis.unExer?.[1]?.count ?? 0
        questionMaxNum.fillBlankNum = myExerStatis.unExer?.[2]?.count ?? 0
        questionMaxNum.judgeNum = myExerStatis.unExer?.[3]?.count ?? 0
        questionMaxNum.qaNum = myExerStatis.unExer?.[4]?.count ?? 0
    } else if (form.type === 3) {
        questionMaxNum.singleNum = myExerStatis.wrong?.[0]?.count ?? 0
        questionMaxNum.multipleNum = myExerStatis.wrong?.[1]?.count ?? 0
        questionMaxNum.fillBlankNum = myExerStatis.wrong?.[2]?.count ?? 0
        questionMaxNum.judgeNum = myExerStatis.wrong?.[3]?.count ?? 0
        questionMaxNum.qaNum = myExerStatis.wrong?.[4]?.count ?? 0
    } else if (form.type === 4) {
        questionMaxNum.singleNum = myExerStatis.fav?.[0]?.count ?? 0
        questionMaxNum.multipleNum = myExerStatis.fav?.[1]?.count ?? 0
        questionMaxNum.fillBlankNum = myExerStatis.fav?.[2]?.count ?? 0
        questionMaxNum.judgeNum = myExerStatis.fav?.[3]?.count ?? 0
        questionMaxNum.qaNum = myExerStatis.fav?.[4]?.count ?? 0
    }

    nameUpdate()
}

// 名称更新
function nameUpdate() {
    const typeName = form.type === 1 ? '自组' : form.type === 2 ? '未练' : form.type === 3 ? '错题' : '收藏'
    form.name = `${typeName}_单${form.singleNum || 0}多${form.multipleNum || 0}填${form.fillBlankNum || 0}判${form.judgeNum || 0}问${form.qaNum || 0}_${dayjs().format('YYYYMMDD')}`
}

// 去练习
async function toExer() {
    loading.value = true
    const { data: { code, data } } = await myExerAdd({ ...form })
    if (code !== 200) {
        loading.value = false // 错误显示，正确就跳转了
        return
    }

    router.push(`/my-exer/paper/${route.params.exerId}/${data}`)
}

// 练习时长统计（近一年）
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

                .opt-panel {
                    width: 480px;

                    .form {
                        padding: 10px 20px 0px 20px;

                        // .el-radio {
                        //     width: 160px;
                        //     margin-top: 10px;
                        // }

                        .el-form-item {
                            margin-bottom: 0px;

                            // :deep(.el-form-item__label) {
                            //     height: 36px;
                            //     line-height: 36px;
                            // }

                            // :deep(.el-form-item__content) {
                            //     line-height: 36px;

                            //     .el-slider {
                            //         height: 36px;
                            //     }
                            // }
                            :deep(.form__slider) {
                                flex: 1;
                                display: flex;
                                justify-content: space-between;

                                .form__slider-bar {
                                    width: 300px;

                                    .el-slider__stop {
                                        background-color: #e4e7ed;
                                    }

                                    &.form__slider-bar--single-choice {
                                        .el-slider__bar {
                                            background-color: #4692D8;
                                        }

                                        .el-slider__button {
                                            border: 2px solid #4692D8;
                                        }
                                    }

                                    &.form__slider-bar--multiple-choice {
                                        .el-slider__bar {
                                            background-color: #06BCE3;
                                        }

                                        .el-slider__button {
                                            border: 2px solid #06BCE3;
                                        }
                                    }

                                    &.form__slider-bar--fill-blank {
                                        .el-slider__bar {
                                            background-color: #978CEC;
                                        }

                                        .el-slider__button {
                                            border: 2px solid #978CEC;
                                        }
                                    }

                                    &.form__slider-bar--judge {
                                        .el-slider__bar {
                                            background-color: #DD8CEC;
                                        }

                                        .el-slider__button {
                                            border: 2px solid #DD8CEC;
                                        }
                                    }

                                    &.form__slider-bar--qa {
                                        .el-slider__bar {
                                            background-color: #85E3A4;
                                        }

                                        .el-slider__button {
                                            border: 2px solid #85E3A4;
                                        }
                                    }
                                }

                                .form__slider-unit {
                                    flex: 1;
                                    text-align: right;

                                    &.form__slider-unit--single-choice {
                                        color: #4692D8;
                                    }

                                    &.form__slider-unit--multiple-choice {
                                        color: #06BCE3;
                                    }

                                    &.form__slider-unit--fill-blank {
                                        color: #978CEC;
                                    }

                                    &.form__slider-unit--judge {
                                        color: #DD8CEC;
                                    }

                                    &.form__slider-unit--qa {
                                        color: #85E3A4;
                                    }
                                }
                            }

                            .el-radio-button {
                                :deep(.el-radio-button__inner) {
                                    width: 110px;
                                    height: 38px;
                                    line-height: 38px;
                                    padding: 0px;
                                }
                            }

                        }

                        // .form__submit {
                        //     margin-top: 4px;
                        // }

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
