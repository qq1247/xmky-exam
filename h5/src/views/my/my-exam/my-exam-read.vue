<template>
    <div class="my-exam-read">
        <div class="my-exam__side">
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
                        1、考试结束时间到，系统会自动交卷，请注意时间。<br /> 2、交卷后不可再次考试，请慎重（允许重考除外）。
                    </span>
                </div>
            </div>
        </div>
        <el-scrollbar max-height="calc(100vh - 104px)" class="my-exam-read__main">
            <div class="exam">
                <xmks-card-guide title="考试信息" icon="icon-tubiaoziti22-22" class="exam__head"></xmks-card-guide>
                <div class="exam__main">
                    <span class="exam__label">
                        考试名称：<span class="exam__value">{{ exam.name }}</span>
                    </span>
                    <span class="exam__label">
                        开始时间：<span class="exam__value">{{ exam.startTime }}</span>
                    </span>
                    <span class="exam__label">
                        结束时间：<span class="exam__value">{{ exam.endTime }}</span>
                    </span>
                    <span class="exam__label">
                        限时答题：<span class="exam__value">{{ exam.limitMinute ? `${exam.limitMinute}分钟` : '无' }}</span>
                    </span>
                    <span class="exam__label">
                        成绩查询：<span class="exam__value">{{ dictStore.getValue('SCORE_STATE', exam.scoreState as number)
                        }}</span>
                    </span>
                    <span class="exam__label">
                        公布排名：<span class="exam__value">{{ dictStore.getValue('STATE_ON', exam.rankState as number) ||
                            '-' }}</span>
                    </span>
                    <span class="exam__label">
                        考试分数：<span class="exam__value">{{ exam.passScore }}/{{ exam.totalScore }}（及格/总分）</span>
                    </span>
                    <span class="exam__label">
                        试卷类型：<span class="exam__value">{{ dictStore.getValue('PAPER_MARK_TYPE', exam.markType as number)
                        }}试卷</span>
                    </span>

                    <span class="exam__label">
                        组卷方式：<span class="exam__value">{{ dictStore.getValue('PAPER_GEN_TYPE', exam.genType as number)
                        }}</span>
                    </span>
                    <span class="exam__label exam__label-2">
                        防 作 弊：<span class="exam__value">
                            <template v-if="!exam.sxes.length">无</template>
                            <template v-else v-for="(sxe, index) in exam.sxes" :key="index">{{ index > 0 ? '、' : '' }}{{
                                dictStore.getValue('EXAM_SXES', sxe) }}</template>
                        </span>
                    </span>
                    <span class="exam__label">
                        重考次数：<span class="exam__value">{{ exam.retakeNum || '-' }}次</span>
                    </span>
                </div>
            </div>
            <div class="paper">
                <xmks-card-guide title="试卷信息" icon="icon-tubiaoziti-35" class="paper__head"></xmks-card-guide>
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
            <div class="my-exam">
                <xmks-card-guide title="我的考试" icon="icon-icon_xiugaishijian" class="my-exam__head"></xmks-card-guide>
                <div class="my-exam__main">
                    <div class="my-exam__state">
                        <div class="my-exam__state-inner">
                            <el-steps direction="vertical" :active="myExam.state || 0">
                                <el-step v-for="(dict, index) in dictStore.getList('EXAM_STATE')" :key="index"
                                    :title="dict.dictValue" class="my-exam__step">
                                    <template #icon>
                                        <span class="iconfont icon-lianxi-61 my-exam__step-icon"></span>
                                    </template>
                                </el-step>
                            </el-steps>
                            <el-steps direction="vertical" :active="myExam.markState || 0">
                                <el-step v-for="(dict, index) in dictStore.getList('MARK_STATE')" :key="index"
                                    :title="dict.dictValue" class="my-exam__step">
                                    <template #icon>
                                        <span class="iconfont icon-lianxi-61 my-exam__step-icon"></span>
                                    </template>
                                </el-step>
                            </el-steps>
                        </div>
                    </div>
                    <div class="my-exam__outer">
                        <div class="my-exam__inner">
                            <span class="myexam__label">
                                答题开始时间：<span class="myexam__value">{{ myExam.answerStartTime || '-' }}</span>
                            </span>
                            <span class="myexam__label">
                                答题结束时间：<span class="myexam__value">{{ myExam.answerEndTime || '-' }}</span>
                            </span>
                            <span class="myexam__label">
                                考试分数：<span v-if="exam.markType === 1" class="myexam__value">
                                    {{ myExam.totalScore != null ? myExam.totalScore : '-' }}分</span>
                                <span v-if="exam.markType === 2" class="myexam__value">
                                    {{ myExam.objectiveScore != null ? myExam.objectiveScore : '-' }}/{{
                                        myExam.totalScore != null ?
                                            myExam.totalScore : '-' }}（客观分/合计）</span>
                            </span>
                            <span class="myexam__label">
                                考试排名：<span class="myexam__value">{{ myExam.no == null ? '-' : myExam.no }} / {{
                                    exam.userNum == null
                                        ? '-' : exam.userNum }}</span>
                            </span>
                            <span class="myexam__label">
                                是否及格：<span class="my-exam__tag"
                                    :class="{ 'my-exam__tag--pass': myExam.answerState === 1, 'my-exam__tag--fail': myExam.answerState === 2 }">{{
                                        dictStore.getValue('ANSWER_STATE', myExam.answerState as number) || '-' }}</span>
                                <span v-if="myExam.ver as number > 1">（已重考{{ myExam.ver as number - 1 }}/{{
                                    exam.retakeNum
                                }}次）</span>
                            </span>
                            <div class="my-exam__btn-group">
                                <el-button type="primary" class="my-exam__btn"
                                    :class="{ 'my-exam__btn--active': hasStartExam, 'my-exam__btn--disable': !hasStartExam }"
                                    @click="toExam">
                                    <xmks-count-down v-if="!hasStartExam" :expireTime="exam.startTime" preTxt="距考试开始："
                                        :remind="60" color="#FFFFFF" remind-color="#E43D33" @end="hasStartExam = true">
                                    </xmks-count-down>
                                    <span v-else> {{ myExam.markState as number >= 2 ? '查阅试卷' : '进入考试' }}</span>
                                </el-button>
                                <el-button
                                    v-if="exam.markType === 1 && exam.markState !== 3 && exam.retakeNum as number > 0 && myExam.answerState === 2 && ((myExam.ver as number) <= (exam.retakeNum as number))"
                                    type="primary" class="my-exam__btn my-exam__btn--secondary" @click="toRetake">
                                    <span>重新考试</span><!-- 客观题试卷 && 考试未结束 && 允许重考 && 不及格 -->
                                </el-button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive } from 'vue'
import XmksCardGuide from '@/components/card/xmks-card-guide.vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { ref } from 'vue'
import { useDictStore } from '@/stores/dict'
import type { User } from '@/ts/base/user'
import type { Exam } from '@/ts/exam/exam'
import type { MyExam } from '@/ts/exam/my-exam'
import { userGet } from '@/api/base/user'
import { myExamExamGet, myExamGet, myExamQuestionStatis, myExamRetake } from '@/api/my/my-exam'
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'
import XmksCountDown from '@/components/xmks-count-down.vue'
import { ElMessage } from 'element-plus'

/************************变量定义相关***********************/
use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent]);
const dictStore = useDictStore(); // 字典缓存
const userStore = useUserStore(); // 字典缓存
const route = useRoute()// 路由
const router = useRouter()// 路由
const user = reactive<User>({ // 用户
    id: null,
    name: '',
    loginName: '',
    type: null,
    state: null,
    orgName: ''
})
const exam = reactive<Exam>({ // 考试
    id: null,
    name: '',
    paperName: '',
    startTime: '',
    endTime: '',
    markStartTime: '',
    markEndTime: '',
    markState: null,
    scoreState: null,
    rankState: null,
    passScore: null,
    totalScore: null,
    markType: null,
    genType: null,
    loginType: null,
    sxes: [],
    state: null,
    userNum: null,
    limitMinute: null,
    retakeNum: null,
})
const myExam = reactive<MyExam>({ // 我的考试
    examId: null,
    userId: null,
    answerStartTime: '',
    answerEndTime: '',
    markStartTime: '',
    markEndTime: '',
    objectiveScore: null,
    totalScore: null,
    state: null,
    markState: null,
    answerState: null,
    no: null,
    ver: null,
})
const markTypeStatis = reactive({ // 阅卷类型统计
    subjective: 0,
    objective: 0,
    chapter: 0,
})


const questionStatisOpts = ref({
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

const hasStartExam = ref(false); // 是否开始考试

/************************组件生命周期相关*********************/
onMounted(async () => {
    userQuery();
    examQuery();
    myExamQuery();
    questionStatisQuery();
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

// 考试查询
async function examQuery() {
    const { data: { data } } = await myExamExamGet({ examId: route.params.examId })
    exam.id = parseInt(route.params.examId as string)
    exam.name = data.name
    exam.paperName = data.paperName
    exam.startTime = data.startTime
    exam.endTime = data.endTime
    exam.markStartTime = data.markStartTime
    exam.markEndTime = data.markEndTime
    exam.markState = data.markState
    exam.scoreState = data.scoreState
    exam.rankState = data.rankState
    exam.passScore = data.passScore
    exam.totalScore = data.totalScore
    exam.markType = data.markType
    exam.genType = data.genType
    exam.sxes = data.sxes
    exam.state = data.state
    exam.userNum = data.userNum
    exam.limitMinute = data.limitMinute
    exam.retakeNum = data.retakeNum
}
// 我的考试查询
async function myExamQuery() {
    const { data: { data } } = await myExamGet({ examId: route.params.examId })
    myExam.examId = data.examId
    myExam.userId = data.userId
    myExam.answerStartTime = data.answerStartTime
    myExam.answerEndTime = data.answerEndTime
    myExam.markStartTime = data.markStartTime
    myExam.markEndTime = data.markEndTime
    myExam.objectiveScore = data.objectiveScore
    myExam.totalScore = data.totalScore
    myExam.state = data.state
    myExam.markState = data.markState
    myExam.answerState = data.answerState
    myExam.no = data.no
    myExam.ver = data.ver
}

// 试题统计查询
async function questionStatisQuery() {
    const { data: { data: { typeStatis, markTypeStatis: _markTypeStatis } } } = await myExamQuestionStatis({ examId: route.params.examId })
    const datas = (typeStatis as any[]).map((d) => {
        return {
            name: dictStore.getValue('QUESTION_TYPE', d.type),
            value: d.count
        };
    })
    questionStatisOpts.value.series[0].data = datas as never[]
    markTypeStatis.subjective = _markTypeStatis.subjective;
    markTypeStatis.objective = _markTypeStatis.objective;
    markTypeStatis.chapter = _markTypeStatis.chapter;
}

// 去考试
function toExam() {
    if (!hasStartExam.value) {
        ElMessage.error('考试未开始，请等待')
        return;
    }
    router.push(`/my-exam/paper/${route.params.examId}`)
}

// 重考
async function toRetake() {
    const { data: { code } } = await myExamRetake({ examId: route.params.examId })
    if (code !== 200) {
        return;
    }

    toExam()
}

</script>
<style lang="scss" scoped>
.my-exam-read {
    display: flex;
    width: 1200px;
    margin: 20px 0px;

    .my-exam__side {
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
            height: 200px;
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

    .my-exam-read__main {
        flex: 1;

        .paper {
            margin-top: 20px;

            .paper__head {
                margin-bottom: 8px;
            }

            .paper__main {
                display: flex;
                background-color: #FFFFFF;
                height: 200px;
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

        .exam {
            display: flex;
            flex-direction: column;

            .exam__head {
                margin-bottom: 8px;
            }

            .exam__main {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                grid-template-rows: repeat(3, 1fr);
                background-color: #FFFFFF;
                padding: 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .exam__label {
                    font-size: 14px;
                    color: #8F939C;
                    line-height: 32px;

                    .exam__value {
                        color: #333333;
                    }
                }

                .exam__label-2 {
                    grid-column: span 2;
                }
            }

        }

        .my-exam {
            display: flex;
            flex-direction: column;
            margin-top: 20px;

            .my-exam__head {
                margin-bottom: 8px;
            }

            .my-exam__main {
                display: flex;
                background-color: #FFFFFF;
                height: 270px;
                padding: 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .my-exam__state {
                    display: flex;
                    flex-direction: column;
                    width: 500px;

                    .my-exam__state-label {
                        font-size: 14px;
                        color: #8F939C;
                    }

                    .my-exam__state-inner {
                        flex: 1;
                        display: flex;
                        justify-content: space-between;
                        padding: 20px 100px;
                        background: #F6F7FC;
                        border-radius: 15px 15px 15px 15px;

                        :deep(.my-exam__step) {
                            .is-finish {
                                font-size: 14px;

                                .el-step__icon {
                                    background-color: #F6F7FC;

                                    .my-exam__step-icon {
                                        font-size: 12px;
                                        padding: 2px;
                                        border-radius: 50%;
                                        color: #FFFFFF;
                                        background-color: #0D9DF6;
                                    }
                                }
                            }

                            .is-process {
                                font-size: 14px;

                                .el-step__icon {
                                    background-color: #F6F7FC;

                                    .my-exam__step-icon {
                                        font-size: 12px;
                                        padding: 2px;
                                        border-radius: 50%;
                                        color: #FFFFFF;
                                        background-color: #E5E5E5;
                                    }
                                }
                            }

                            .is-wait {
                                font-size: 14px;

                                .el-step__icon {
                                    background-color: #F6F7FC;

                                    .my-exam__step-icon {
                                        font-size: 12px;
                                        padding: 2px;
                                        border-radius: 50%;
                                        color: #FFFFFF;
                                        background-color: #E5E5E5;
                                    }
                                }
                            }

                        }
                    }
                }

                .my-exam__outer {
                    flex: 1;
                    display: flex;
                    justify-content: center;

                    .my-exam__inner {
                        flex: 1;
                        display: flex;
                        flex-direction: column;
                        margin: 0px 10px 0px 40px;

                        .myexam__label {
                            font-size: 14px;
                            color: #8F939C;
                            line-height: 34px;

                            .myexam__value {
                                color: #333333;
                            }

                        }

                        .my-exam__tag {
                            display: inline-block;
                            margin-left: 5px;
                            padding: 0px 8px;
                            font-size: 14px;
                            border: 1px solid transparent;
                            border-radius: 4px;
                            background-clip: padding-box, border-box;
                            background-origin: padding-box, border-box;
                            height: 18px;
                            line-height: 16px;

                            &.my-exam__tag--pass {
                                background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #009554, #14EC9D);
                                color: #08B771;
                            }

                            &.my-exam__tag--fail {
                                background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #F445A0, #F99C9C);
                                color: #F66A9E;
                            }
                        }

                        .my-exam__btn-group {
                            display: flex;

                            .my-exam__btn {
                                margin-top: 10px;
                                width: 100%;
                                height: 48px;
                                border-radius: 6px;
                                border: 0px;
                                color: #FFFFFF;
                                font-size: 16px;

                                &.my-exam__btn--active {
                                    background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
                                }

                                &.my-exam__btn--disable {
                                    background: linear-gradient(to right, #dfdfdf 0%, #cacaca 100%);
                                }

                                &.my-exam__btn--secondary {
                                    border: 1px solid transparent;
                                    border-radius: 4px;
                                    background-clip: padding-box, border-box;
                                    background-origin: padding-box, border-box;
                                    background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #F445A0, #F99C9C);
                                    color: #F66A9E;
                                }
                            }
                        }
                    }

                }
            }

        }
    }

}
</style>
