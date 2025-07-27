<template>
    <div class="my-mark-read">
        <div class="my-mark__side">
            <div class="user">
                <div class="user__avatar">
                    <span class="iconfont icon-rentouxiang user__avatar-icon"></span>
                </div>
                <span class="user__label">账号：<span class="user__value">{{ user.loginName }}</span></span>
                <span class="user__label">姓名：<span class="user__value">{{ user.name }}</span></span>
                <span class="user__label">角色：<span class="user__value">阅卷用户</span></span>
            </div>
            <div class="notes">
                <xmks-card-guide title="注意事项" icon="icon-tubiaoziti-34" class="notes__head"></xmks-card-guide>
                <div class="notes__main">
                    <span class="notes__txt">
                        1、阅卷结束时间到，系统会自动结束阅卷。请注意把握时间。<br />
                    </span>
                </div>
            </div>
        </div>
        <el-scrollbar max-height="calc(100vh - 104px)" class="my-mark-read__main">
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
                    <span class="exam__label">
                        防 作 弊：<span class="exam__value">
                            <template v-if="!exam.sxes.length">无</template>
                            <template v-else v-for="(sxe, index) in exam.sxes" :key="index">{{ index > 0 ? '、' : '' }}{{
                                ['', '试题乱序', '选项乱序'][sxe] }}</template>
                        </span>
                    </span>
                    <span class="exam__label">
                        考试人数：<span class="exam__value">{{ exam.userNum || '-' }}</span>
                    </span>
                </div>
            </div>
            <div class="my-mark">
                <xmks-card-guide title="我的阅卷" icon="icon-icon_xiugaishijian" class="my-mark__head"></xmks-card-guide>
                <div class="my-mark__main">
                    <el-form ref="formRef" :model="form" :rules="formRules" class="my-task">
                        <el-form-item label="" prop="">
                            <span class="my-task__label">
                                本次考试一共<span class="my-task__value my-task__value--total">{{ claimInfo.paperNum
                                }}</span>份试卷，已被领取<span class="my-task__value my-task__value--total-claim">{{
                                        claimInfo.markNum }}</span>份，剩余<span
                                    class="my-task__value my-task__value--total-remaining">{{
                                        claimInfo.paperNum - claimInfo.markNum }}</span>份。
                            </span>
                        </el-form-item>
                        <el-form-item label="" prop="">
                            <span class="my-task__label">
                                你已领取<span class="my-task__value my-task__value--my-total">{{ claimInfo.myClaimNum
                                }}</span>份试卷，未批阅<span class="my-task__value my-task__value--my-unmark">{{
                                        claimInfo.myClaimNum - claimInfo.myMarkNum }}</span>份。
                            </span>
                        </el-form-item>
                        <el-form-item label="" prop="num">
                            <span class="my-task__label">现在新领取：</span>
                            <el-input-number v-model="form.num" :min="1" :max="100" :step="10" :precision="0"
                                controls-position="right" class="my-task__input-number" />
                            <span class="my-task__label">份</span>
                            <el-button :disabled="exam.markState !== 2" class="my-task__btn"
                                :class="{ 'my-task__btn--disable': exam.markState !== 2 }"
                                @click="claim">点击领取</el-button>
                        </el-form-item>
                    </el-form>
                    <div class="my-mark__outer">
                        <div class="my-mark__inner">
                            <span class="myexam__label">
                                阅卷开始时间：<span class="myexam__value">{{ exam.markStartTime }}</span>
                            </span>
                            <span class="myexam__label">
                                阅卷结束时间：<span class="myexam__value">{{ exam.markEndTime }}</span>
                            </span>
                            <span class="myexam__label">
                                阅卷时长：<span class="myexam__value">-</span>
                            </span>
                            <span class="myexam__label">
                                参与阅卷：<span class="my-mark__tag">2人</span>
                            </span>
                            <el-button type="primary" class="my-mark__btn"
                                :class="{ 'my-mark__btn--active': hasStartMark, 'my-mark__btn--disable': !hasStartMark }"
                                @click="markIn">
                                <xmks-count-down v-if="!hasStartMark" :expireTime="exam.markStartTime" preTxt="距考试开始："
                                    :remind="60" color="#FFFFFF" remind-color="#E43D33" @end="hasStartMark = true">
                                </xmks-count-down>
                                <span v-else> {{ exam.markState as number === 2 ? '进入阅卷' : '查阅试卷' }}</span>
                            </el-button>
                        </div>
                    </div>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>
<script lang="ts" setup>
import { userGet } from '@/api/base/user'
import { examGet } from '@/api/exam/exam'
import { myMarkClaim, myMarkQuestionStatis, myMarkClaimInfo } from '@/api/my/my-mark'
import XmksCardGuide from '@/components/card/xmks-card-guide.vue'
import XmksCountDown from '@/components/xmks-count-down.vue'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import type { User } from '@/ts/base/user'
import type { Exam } from '@/ts/exam/exam'
import { PieChart } from 'echarts/charts'
import { LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import VChart from 'vue-echarts'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent]);
const router = useRouter()// 路由
const route = useRoute()// 路由
const dictStore = useDictStore() // 字典缓存
const userStore = useUserStore() // 字典缓存
const form = reactive({// 表单
    num: 10// 新批阅份数
})
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    num: [
        { required: true, message: '请输入批领取数', trigger: 'blur' },
    ],
})
const user = reactive<User>({ // 用户
    id: null,
    name: '',
    loginName: '',
    type: null,
    state: null,
    orgName: ''
})
const exam = reactive<Exam>({
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
    retakeNum: null
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

const hasStartMark = ref(false) // 是否开始考试
const claimInfo = reactive({// 领取信息
    paperNum: 0,
    markNum: 0,
    myClaimNum: 0,
    myMarkNum: 0
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    userQuery()
    examQuery()
    questionStatisQuery()
    claimInfoQuery()
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
    const { data: { data } } = await examGet({ id: route.params.examId })
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

// 试题统计查询
async function questionStatisQuery() {
    const { data: { data: { typeStatis, markTypeStatis: _markTypeStatis } } } = await myMarkQuestionStatis({ examId: route.params.examId })
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

// 获取领取信息
async function claimInfoQuery() {
    const { data: { data } } = await myMarkClaimInfo({ examId: route.params.examId })
    claimInfo.paperNum = data.paperNum
    claimInfo.markNum = data.markNum
    claimInfo.myClaimNum = data.myClaimNum
    claimInfo.myMarkNum = data.myMarkNum
}

// 分配试卷
async function claim() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 领取试卷
    await myMarkClaim({
        examId: route.params.examId,
        num: form.num
    })

    // 刷新领取信息
    claimInfoQuery()
}

// 进入阅卷
function markIn() {
    if (!hasStartMark.value) {
        ElMessage.error('阅卷未开始，请等待')
        return;
    }
    if (!claimInfo.myClaimNum) {
        ElMessage.error('未领取试卷')
        return;
    }

    router.push(`/my-mark/paper/${route.params.examId}`)
}

</script>
<style lang="scss" scoped>
.my-mark-read {
    display: flex;
    width: 1200px;
    margin: 20px 0px;

    .my-mark__side {
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

    .my-mark-read__main {
        flex: 1;

        .paper {
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
            margin-top: 20px;

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
            }

        }

        .my-mark {
            display: flex;
            flex-direction: column;
            margin-top: 20px;

            .my-mark__head {
                margin-bottom: 8px;
            }

            .my-mark__main {
                display: flex;
                background-color: #FFFFFF;
                height: 270px;
                padding: 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .my-task {
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    width: 500px;
                    margin-top: 10px;
                    background: #F6F7FC;
                    border-radius: 15px 15px 15px 15px;

                    .my-task__label {
                        font-size: 14px;
                        color: #999999;
                    }

                    .my-task__value {
                        padding: 0px 5px;
                        font-size: 16px;
                        color: #333333;
                        font-weight: bold;

                        &.my-task__value--total {
                            color: #4692D8;
                        }

                        &.my-task__value--total-claim {
                            color: #06BCE3;
                        }

                        &.my-task__value--total-remaining {
                            color: #978CEC;
                        }

                        &.my-task__value--my-total {
                            color: #85e3a4;
                        }

                        &.my-task__value--my-unmark {
                            color: #E43D33;
                        }

                    }

                    :deep(.my-task__input-number) {
                        position: relative;
                        height: 30px;
                        width: 50px;

                        .el-input-number__decrease,
                        .el-input-number__increase {
                            display: none;
                        }

                        .el-input__wrapper {
                            padding: 0px;
                            box-shadow: initial;
                            border-radius: 0;
                            background-color: #F6F7FC;
                        }

                        &::after {
                            content: "";
                            position: absolute;
                            left: 0;
                            bottom: 5px;
                            width: 100%;
                            height: 1px;
                            background-color: #333333;
                        }
                    }

                    .my-task__btn {
                        height: 22px;
                        margin-left: 10px;
                        border-radius: 6px;
                        padding: 0px 10px;
                        border: 0px;
                        color: #FFFFFF;
                        font-size: 12px;
                        background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);

                        &.my-task__btn--disable {
                            background: linear-gradient(to right, #dfdfdf 0%, #cacaca 100%);
                        }
                    }
                }

                .my-mark__outer {
                    flex: 1;
                    display: flex;
                    justify-content: center;

                    .my-mark__inner {
                        flex: 1;
                        display: flex;
                        flex-direction: column;
                        margin: 25px 10px 0px 40px;

                        .myexam__label {
                            font-size: 14px;
                            color: #8F939C;
                            line-height: 34px;

                            .myexam__value {
                                color: #333333;
                            }

                        }

                        .my-mark__tag {
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

                            &.my-mark__tag--pass {
                                background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #009554, #14EC9D);
                                color: #08B771;
                            }

                            &.my-mark__tag--fail {
                                background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #F445A0, #F99C9C);
                                color: #F66A9E;
                            }
                        }

                        .my-mark__btn {
                            margin-top: 20px;
                            width: 100%;
                            height: 48px;
                            border-radius: 6px;
                            border: 0px;
                            color: #FFFFFF;
                            font-size: 16px;

                            &.my-mark__btn--active {
                                background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
                            }

                            &.my-mark__btn--disable {
                                background: linear-gradient(to right, #dfdfdf 0%, #cacaca 100%);
                            }
                        }
                    }

                }
            }

        }
    }

}
</style>
