<template>
    <div class="paper">
        <!-- 答题卡 -->
        <div class="paper-left">
            <el-card shadow="never" class="paper-left-top">
                <!-- <el-avatar size="large"/> -->
                <span class="paper-left-top-username">
                    {{ user.name }} / {{ user.orgName }}
                </span>
                <div class="paper-left-top-statis">
                    <div>
                        <Iconfont icon="icon-jiangbei" :size="20" color="#F6961E;" :width="30" :height="30" :radius="5"
                            background-color="#FDF3E7" />
                        <span class="paper-left-top-statis-value">{{ scoreShow ? myExam.totalScore + '分' : '-' }}</span>
                        <span class="paper-left-top-statis-txt">得分</span>
                    </div>
                    <div>
                        <Iconfont icon="icon-shijianxuanzhong" :size="20" color="#F6961E;" :width="30" :height="30"
                            :radius="5" background-color="#FDF3E7" />
                        <span class="paper-left-top-statis-value">
                            {{ myExam.state === 3 ? timeDiff(myExam.answerStartTime, myExam.answerEndTime) : '-' }}
                        </span><!-- 交卷就有 -->
                        <span class="paper-left-top-statis-txt">答题用时</span>
                    </div>
                    <div>
                        <Iconfont icon="icon-approval-fulll" :size="20" color="#05CAC1;" :width="30" :height="30"
                            :radius="5" background-color="#E3F3FF" />
                        <span class="paper-left-top-statis-value">
                            {{ scoreShow ? dictStore.getValue('ANSWER_STATE', myExam.answerState) : '-' }}
                        </span>
                        <span class="paper-left-top-statis-txt">成绩</span>
                    </div>
                    <div>
                        <Iconfont icon="icon-shijianxuanzhong" :size="20" color="#327EF6;" :width="30" :height="30"
                            :radius="5" background-color="#E3F3FF" />
                        <span class="paper-left-top-statis-value">
                            {{ rankShow ? `${myExam.no || '-'} / ${myExam.userNum}` : `-` }}
                        </span>
                        <span class="paper-left-top-statis-txt">排名</span>
                    </div>
                </div>
                <el-button type="primary" plain @click="$router.push(`/exam/statis/${exam.id}`)">返回</el-button>
            </el-card>
            <el-card shadow="never" class="paper-left-bottom">
                <el-divider>
                    答题卡
                </el-divider>
                <el-scrollbar height="calc(100vh - 475px)">
                    <template v-for="examQuestion in examQuestions">
                        <div v-if="examQuestion.type === 1" class="paper-left-bottom-chapter">{{ examQuestion.chapterName }}
                        </div>
                        <el-button v-else type="primary" plain @click="toNav(examQuestion)">
                            {{ examQuestion.no }}
                        </el-button>
                    </template>
                </el-scrollbar>
            </el-card>
        </div>
        <!-- 试卷 -->
        <el-scrollbar height="calc(100vh - 50px)" class="paper-right">
            <el-card shadow="never">
                <!-- 右上角显示打分 -->
                <div class="paper-right-score" v-if="scoreShow">
                    <span class="paper-right-score-value">{{ myExam.totalScore }}</span>
                    <span class="iconfont icon-fenshudixian"></span>
                </div>
                <!-- 试卷名称 -->
                <div class="paper-right-title">
                    <el-input :value="exam.name" :maxlength="16" placeholder="" :readonly="true" />
                </div>
                <!-- 试题列表 -->
                <template v-for="(examQuestion, index) in examQuestions" :key="index">
                    <div v-if="examQuestion.type === 1" class="paper-right-chapter">
                        <el-input :value="examQuestion.chapterName" maxlength="14" placeholder="请输入章节名称" :readonly="true" />
                        <el-input v-if="examQuestion.chapterTxt" :value="examQuestion.chapterTxt" type="textarea"
                            maxlength="128" :autosize="{ minRows: 1 }" resize="none" placeholder="请输入章节描述"
                            :readonly="true" />
                    </div>
                    <Question 
                        v-else :no="examQuestion.no" 
                        :type="examQuestion.questionType || 1"
                        :markType="examQuestion.markType || 1" 
                        :title="examQuestion.title || ''"
                        :score="examQuestion.score || 1" 
                        :answers="examQuestion.answers"
                        :userAnswers="examQuestion.userAnswers" 
                        :userScore="examQuestion.userScore"
                        :options="examQuestion.options" 
                        :editable="isAnswer"
                        @change="" 
                        :errShow="scoreShow">
                        <template #bottom-right
                            v-if="myExam.state === 3 || (myExam.state === 1 && myExam.markState === 3)"><!-- 已交卷或（未考试已阅卷） -->
                            <el-tooltip placement="top" effect="light" :content="answerShow(examQuestion) || '稍后查看答案'"
                                popper-class="popper-class" raw-content>
                                <el-button type="success" size="small">标准答案</el-button>
                            </el-tooltip>
                        </template>
                    </Question>
                </template>
            </el-card>
        </el-scrollbar>
    </div>
</template>
<script lang="ts" setup>
import http from '@/request';
import { computed, onMounted, onUnmounted, reactive, ref, } from 'vue';
import Question from '@/components/question/Question.vue';
import type { ExamQuestion } from '@/stores/exam';
import _ from 'lodash'
import dayjs from 'dayjs';
import { useRoute } from 'vue-router'
import { useDictStore } from '@/stores/dict';

// 定义变量
const route = useRoute()
const dictStore = useDictStore() // 字典缓存
const examQuestions = ref([] as ExamQuestion[])// 试卷信息
const exam = reactive({// 考试信息
    id: 0, // 考试ID
    name: '',// 试卷名称
    color: '', // 倒计时颜色
    markState: 0, // 阅卷状态
    scoreState: 0, // 分数状态
    rankState: 0,// 排名状态
})
const myExamEndTime = ref()// 我的考试结束时间（用reactive必须new Date()，会造成倒计时立即结束）
const myExam = reactive({// 我的考试信息
    totalScore: 0, //总分
    answerStartTime: new Date(),// 答题开始时间
    answerEndTime: new Date(),// 答题结束时间
    state: 0, // 考试状态
    markState: 0, // 阅卷状态
    answerState: 0, // 答题状态
    no: 0, // 用户排名
    userNum: 0, // 用户数量
})
const user = reactive({// 用户信息
    name: '',// 姓名
    orgName: '',// 机构名称
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    // 隐藏页头信息（模拟全屏）
    (document.getElementsByClassName('el-header')[0] as HTMLElement).style.display = 'none'

    // 获取机构信息
    let userId = parseInt(route.params.userId as string)
    let { data: { data: data2 } } = await http.post("user/get", {id: userId})
    user.name = data2.name
    user.orgName = data2.orgName

    // 获取试卷信息（先获取试卷信息，因为后台检测到，当前用户如果是第一次打开试卷，才生成当前用户的考试和结束时间
    exam.id = parseInt(route.params.id as string)
    let { data: { data: data1 } } = await http.post("myMark/paper", { examId: exam.id, userId })
    let no = 1
    examQuestions.value = data1.map((examQuestion: ExamQuestion) => {
        if (examQuestion.type === 2) {// 处理题号
            examQuestion.no = no++
        }
        return examQuestion
    })

    // 获取我的考试信息
    let { data: { data } } = await http.post("myMark/get", { examId: exam.id, userId })
    exam.name = data.examName
    exam.markState = data.examMarkState
    exam.scoreState = data.examScoreState
    exam.rankState = data.examRankState

    myExamEndTime.value = dayjs(data.examEndTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    myExam.totalScore = data.totalScore
    myExam.answerStartTime = dayjs(data.answerStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    myExam.answerEndTime = dayjs(data.answerEndTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    myExam.state = data.state
    myExam.markState = data.markState
    myExam.answerState = data.answerState
    myExam.no = data.no
    myExam.userNum = data.userNum
    
})
// 组件卸载完成后，执行如下方法
onUnmounted(() => {
    (document.getElementsByClassName('el-header')[0] as HTMLElement).style.display = 'initial'
})

// 计算属性
const isAnswer = computed(() => (myExam.state === 1 && myExam.markState != 3) || myExam.state === 2) // 是否答题。未考试且未阅卷显示（未考试且考试结束时间到，后台会处理成未考试且已阅卷）；考试中显示
const scoreShow = computed(() => {// 分数显示（后端已经数据过滤掉，这里控制只是好理解）
    return (exam.scoreState == 1 && exam.markState == 3) // 如果是考试结束后显示成绩，需要等到考试结束
        || (exam.scoreState == 3 && myExam.markState == 3);// 如果是交卷后显示成绩，需要等到该试卷阅卷完成。比如主观题没阅卷，得不到总分，得不到是否及格
})
const rankShow = computed(() => exam.rankState === 1)

// 答题导航
function toNav(examQuestion: ExamQuestion) {
    (document.querySelector(`#question-${examQuestion.no}`) as HTMLElement).scrollIntoView(true)
}

// 时间差值
function timeDiff(startTime: Date, endTime: Date) {
    let h = Math.floor((endTime.getTime() - startTime.getTime()) / 1000 / 60 / 60)
    let m = Math.floor(((endTime.getTime() - startTime.getTime()) / 1000 / 60) % 60)
    let s = Math.floor(((endTime.getTime() - startTime.getTime()) / 1000) % 60)
    return `${('0' + h).slice(-2)}:${('0' + m).slice(-2)}:${('0' + s).slice(-2)}`
}

function answerShow(examQuestion: ExamQuestion) {
    if (examQuestion.questionType === 1 // 单选
        || examQuestion.questionType === 4 // 判断
        || (examQuestion.questionType === 5 && examQuestion.markType === 2)) {// 主观问答
        return examQuestion.answers && examQuestion.answers[0] && examQuestion.answers[0].replaceAll('\n', '<br/>')
    }
    if (examQuestion.questionType === 2) {// 多选
        return examQuestion.answers?.toString().replaceAll(",", "")
    }

    if (examQuestion.questionType === 3 // 填空
        || (examQuestion.questionType === 5 && examQuestion.markType === 1)) {// 客观问答
        let answerFormat = ''
        examQuestion.answers?.forEach((answer, index) => {
            answerFormat += `${examQuestion.questionType === 3 ? '填空' : '关键词'}${index + 1}：${answer.replaceAll('\n', '、')}<br/>`
        })

        return answerFormat
    }
}
</script>

<style lang="scss" scoped>
.paper {
    flex: 1;
    display: flex;

    .paper-left {
        width: 240px;
        display: flex;
        flex-direction: column;

        .paper-left-top {
            margin-bottom: 10px;

            :deep(.el-card__body) {
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 20px 10px;

                .el-avatar {
                    border: 2px solid var(--el-color-primary);
                    margin-bottom: 10px;
                }

                .paper-left-top-username {
                    margin-bottom: 15px;
                    font-size: 13px;
                    font-weight: bold;
                    color: var(--el-text-color-regular);
                }

                .paper-left-top-statis {
                    display: flex;
                    width: 100%;

                    div {
                        flex: 1;
                        margin: auto;

                        .paper-left-top-statis-value {
                            font-size: 12px;
                            font-weight: bold;
                            color: var(--el-text-color-regular);
                            padding-top: 5px;
                            display: block;
                            text-align: center;
                        }

                        .paper-left-top-statis-txt {
                            font-size: 12px;
                            font-weight: bold;
                            color: var(--el-text-color-secondary);
                            display: block;
                            text-align: center;
                        }
                    }
                }

                .paper-left-top-time {
                    border: 1px dashed var(--el-color-primary);
                    padding: 5px;
                    margin-top: 20px;
                    width: 100%;

                    span {
                        font-weight: bold;
                        font-size: 14px;
                        color: var(--el-color-primary);
                        margin: auto;
                        display: block;
                        text-align: center;
                    }
                }

                .el-button {
                    width: 100%;
                    margin-top: 10px;
                    margin-left: 0px;
                }
            }
        }

        .paper-left-bottom {
            .el-divider--horizontal {
                margin: 15px 0px 20px 0px;
            }

            .el-button {
                height: 30px;
                width: 30px;
                padding: 0;
                border: 0;
                margin: 2px;
            }

            .paper-left-bottom-chapter {
                font-size: 13px;
                font-weight: bold;
                margin: 5px 0px;
                cursor: pointer;
            }
        }
    }

    :deep(.paper-right) {
        flex: 1;

        .el-card {
            width: 800px;
            min-height: calc(100vh - 50px);
            margin-left: 10px;
            position: relative;

            &:hover {
                .paper-right-toolbar {
                    display: block;
                }
            }

            .paper-right-toolbar {
                position: absolute;
                right: 10px;
                top: 5px;
                display: none;
                z-index: 2;
            }

            .paper-right-score {
                position: absolute;
                right: 30px;
                top: 30px;
                z-index: 2;

                display: flex;
                flex-direction: column;
                color: red;
                font-size: 26px;

                .iconfont {
                    font-size: 58px;
                    line-height: 30px;
                }

                .paper-right-score-value {
                    text-align: center;
                }
            }

            .paper-right-title {
                margin-top: 10px;

                .el-input__wrapper {
                    box-shadow: initial;

                    .el-input__inner {
                        text-align: center;
                        font-size: 18px;
                        font-weight: bold;
                    }
                }
            }

            .paper-right-chapter {
                padding: 0px 20px;
                position: relative;

                &:hover {
                    .el-button {
                        display: inline-block;
                    }
                }

                .el-input__wrapper {
                    box-shadow: initial;

                    .el-input__inner {
                        font-size: 16px;
                        font-weight: bold;
                    }
                }

                .el-textarea__inner {
                    box-shadow: initial;
                }

                .el-button {
                    position: absolute;
                    bottom: 5px;
                    right: 5px;
                    display: none;
                }

                .el-form-item {
                    // 校验通过时，恢复和章节描述的距离
                    margin-bottom: 0;

                    &.is-error {
                        margin-bottom: 18px;
                    }
                }

                .el-form-item__error {
                    margin-left: 12px;
                }
            }
        }
    }
}
</style>
<style>
.popper-class {
    max-width: 800px;
}
</style>