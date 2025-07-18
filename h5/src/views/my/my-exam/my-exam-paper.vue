<template>
    <xmks-anti-cheat v-if="myExam.state === 2" :screen-switch="!exam.sxes.includes(3)" :debug="!exam.sxes.includes(4)"
        :hotkey="!exam.sxes.includes(4)" @screen-switch="(content) => sxes(3, content)"
        @hotkey="(content) => sxes(4, content)" @debug="(content) => sxes(4, content)"></xmks-anti-cheat>
    <div v-loading="load.loading" :element-loading-text="load.text"
        element-loading-background="rgba(122, 122, 122, 0.8)" class="my-exam-paper">
        <div class="my-exam-paper__head">
            <xmks-count-down v-if="examing" :expireTime="myExam.answerEndTime" preTxt="距考试结束：" :remind="300"
                color="#04C7F2" remind-color="#E43D33" font-size="14px" @end="finish">
            </xmks-count-down>
            <el-button v-if="examing" type="primary" class="my-exam-paper__btn"
                :class="{ 'my-exam-paper__btn--warn': finishConfirm }" @click="() => {
                    // 二次确认是否交卷
                    if (!finishConfirm) {
                        finishConfirm = true
                        return
                    }
                    finish()
                }">
                <span class="iconfont icon-lianxi-61 my-exam-paper__btn-icon"></span>
                <span class="editor-toolbar__btn-txt">{{ finishConfirm ? '再次确认' : '完成交卷' }}</span>
            </el-button>
        </div>
        <div class="my-exam-paper__main">
            <div v-if="toolbarShow" class="paper-toolbar">
                <el-button type='' class="editor-toolbar__btn "
                    :class="{ 'editor-toolbar__btn--active': toolbars.answerShow }"
                    @click="toolbars.answerShow = !toolbars.answerShow">
                    <span class="iconfont icon-icon-03 editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">{{ toolbars.answerShow ? '隐藏标准答案' : '显示标准答案' }}</span>
                </el-button>
                <el-button type='' class="editor-toolbar__btn"
                    :class="{ 'editor-toolbar__btn--active': toolbars.analysisShow }"
                    @click="toolbars.analysisShow = !toolbars.analysisShow">
                    <span class="iconfont icon-icon-06 editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">{{ toolbars.analysisShow ? '隐藏解析' : '显示解析' }}</span>
                </el-button>
            </div>
            <div class="paper__wrap">
                <div class="answer-sheet">
                    <el-divider class="answer-sheet_title">
                        答题卡
                    </el-divider>
                    <el-scrollbar height="calc(100vh - 300px)" class="answer-sheet__wrap">
                        <div class="answer-sheet">
                            <template v-for="(examQuestion, index) in examQuestions" :key="index">
                                <div v-if="examQuestion.type === 1" @click="scrollView(index)"
                                    class="answer-sheet__chapter">
                                    {{ examQuestion.chapterName }}
                                </div>
                                <div v-else @click="scrollView(index)" class="answer-sheet__question" :class="{
                                    'answer-sheet__question--answered': answered(examQuestion),
                                    'answer-sheet__question--right': isRight(examQuestion),
                                    'answer-sheet__question--wrong': isWrong(examQuestion),
                                    'answer-sheet__question--half': isHalf(examQuestion),
                                }">
                                    {{ examQuestion.no }}
                                </div>
                            </template>
                        </div>
                    </el-scrollbar>
                </div>
                <el-scrollbar height="calc(100vh - 226px)" class="paper">
                    <div v-if="myExam.totalScore != null" class="total-score">
                        <div class="total-score__inner">
                            <span class="total-score__value">
                                {{ myExam.totalScore }}
                            </span>
                            <span class="total-score__unit">分</span>
                        </div>
                        <span class="iconfont icon-defen total-score__icon"></span>
                    </div>
                    <div class="paper__name">
                        {{ exam.paperName }}
                    </div>
                    <template v-for="(examQuestion, index) in examQuestions" :key="index">
                        <div v-if="examQuestion.type === 1" :id="`q${index}`" class="paper-chapter">
                            <span class="paper-chapter__name">{{ examQuestion.chapterName }}</span>
                            <span v-if="examQuestion.chapterTxt" class="paper-chapter__desc">
                                {{ examQuestion.chapterTxt }}
                            </span>
                        </div>
                        <xmks-question v-else :id="`q${index}`" :type="examQuestion.questionType as number"
                            :title="examQuestion.title as string" :img-ids="examQuestion.imgFileIds"
                            :video-id="examQuestion.videoFileId" :options="examQuestion.options"
                            :answers="examQuestion.answers" :markType="examQuestion.markType as number"
                            :score="examQuestion.score as number" :scores="examQuestion.scores"
                            :analysis="examQuestion.analysis" :userAnswers="examQuestion.userAnswers"
                            :userScore="examQuestion.userScore" :answer-show="toolbars.answerShow"
                            :user-answer-show="true" :analysisShow="toolbars.analysisShow" :display="'paper'"
                            :editable="examing" class="paper-question"
                            @change="(answers: string[]) => { examQuestion.userAnswers = answers; answerUpdate(examQuestion, answers) }">
                            <template #title-pre>{{ examQuestion.no }}、</template>
                        </xmks-question>
                    </template>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { loginSysTime } from '@/api/login'
import { myExamAnswer, myExamExamGet, myExamFinish, myExamGet, myExamPaper, myExamSxe } from '@/api/my/my-exam'
import XmksQuestion from '@/components/question/xmks-question.vue'
import XmksAntiCheat from '@/components/xmks-anti-cheat.vue'
import xmksCountDown from '@/components/xmks-count-down.vue'
import type { Exam, ExamQuestion } from '@/ts/exam/exam'
import type { MyExam } from '@/ts/exam/my-exam'
import { delay } from '@/util/timeUtil'
import { ElMessage } from 'element-plus'
import _ from 'lodash'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute() // 路由
const router = useRouter()// 路由
const toolbars = reactive({// 工具栏
    answerShow: false,
    totalScoreShow: false,
    analysisShow: false,
})

const examQuestions = ref<ExamQuestion[]>([])// 试卷
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
const myExam = reactive<MyExam>({
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
    ver: null
})
const finishConfirm = ref(false) // 交卷确认
const load = reactive({// 加载
    loading: false, // 显示加载页面
    text: '',// 显示加载页面内容
    second: 6,// 倒计时秒数
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    await paperQuery() // 先获取试卷信息，因为后台检测到，当前用户如果是第一次打开试卷，才生成当前用户的考试和结束时间
    examQuery()
    myExamQuery()
})

/************************计算属性相关*************************/
const examing = computed(() => (myExam.state === 1 && myExam.markState != 3) || myExam.state === 2) // 考试中。未考试且未阅卷显示（未考试且考试结束时间到，后台会处理成未考试且已阅卷）；考试中显示
const toolbarShow = computed(() => (exam.scoreState == 1 && exam.markState == 3) || (exam.scoreState == 3 && myExam.markState as number >= 2)) // 如果是考试结束后显示成绩，需要等到考试结束。如果是交卷后显示成绩，交卷后就可以显示全部标准答案（包括主观题答案）。
const answered = computed(() => (examQuestion: ExamQuestion) => examQuestion.userAnswers?.some((userAnswer) => userAnswer.length > 0))// 是否已答
const isHalf = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore > 0 && question.userScore !== question.score); // 是否半对
const isRight = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === question.score); // 是否答对
const isWrong = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === 0); // 是否答错
const answerUpdate = _.debounce(async function (examQuestion, answers) {// // 答题  _.debounce返回的包装后的函数，所以能正常传参
    // examQuestion.userAnswers = answers; // 浏览器选题时，延时500毫秒体验不好，放到外面。

    const { data: { code } } = await myExamAnswer({
        examId: route.params.examId,
        questionId: examQuestion.questionId,
        answers: answers
    })
    if (code != 200) {// 答题失败也不要清空答案，比如问答题清空就尴尬了
        router.push('/my-exam-list')
        return
    }
}, 500) // 延时一秒体验不好，填完直接退出页面不提交

/************************事件相关*****************************/
// 试卷查询
async function paperQuery() {
    const { data: { data: data1 } } = await myExamPaper({ examId: route.params.examId })
    let no = 1;
    examQuestions.value = data1.map((examQuestion: ExamQuestion) => {
        if (examQuestion.type === 2) { // 处理题号
            examQuestion.no = no++;
        }
        return examQuestion;
    })
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

// 滚动预览
function scrollView(index: number) {
    (document.querySelector(`#q${index}`) as HTMLElement).scrollIntoView(true)
};

// 防作弊
async function sxes(type: number, content: string) {
    if (type === 3) {
        ElMessage.error(`禁止考试中切屏，请继续答题`)
    } else {
        ElMessage.error(`禁止浏览器调试，请正常答题`)
    }

    const { data: { data } } = await myExamSxe({ examId: route.params.examId, type, content })
    if (data) {
        ElMessage.error('多次检测到作弊，强制交卷')
        router.push('/my-exam-list')
    }

};

async function finish() {
    // 交卷
    load.loading = true
    load.text = `交卷中`
    await myExamFinish({ examId: route.params.examId })

    // 如果考试成绩是不公布，返回首页
    if (exam.scoreState === 2) {
        load.text = `考试已结束`
        await delay(2000)
        // console.log(`考试已结束`)
        router.push('/home')
        return
    }

    // 如果考试成绩是交卷后公布，并且是主观题试卷，提示后返回首页（需人工阅卷）
    // if (exam.scoreState === 3 && exam.markType === 2) {
    // 	情况不存在，业务上做了主观题试卷不允许交卷后公布，允许也没有意义
    // 	return;
    // }

    // 如果考试成绩是交卷后公布，并且是客观题试卷，查询成绩
    if (exam.scoreState === 3 && exam.markType === 1) {
        load.text = `成绩查询中`
        for (let i = 0; i < 6; i++) {
            if (i >= 5) {
                load.text = `查询失败，请稍后再次查询`
                await delay(1000)
                // console.log(`查询失败，请稍后再次查询`)
                router.push('/home')
                return
            }

            await delay(1000)
            await myExamQuery()

            if (myExam.markState as number === 3) { // 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
                load.text = `考试已结束`
                await delay(1000)
                // console.log(`考试已结束`)
                router.push(`/my-exam/read/${route.params.examId}`)
                return
            }
        }
        return
    }

    // 如果考试成绩是考试结束后公布，但是提前交卷，提示后返回首页
    const { data: { data } } = await loginSysTime({})
    const curTime = new Date(data)
    const examEndTime = new Date(exam.endTime)
    const remainSecond = (examEndTime.getTime() - curTime.getTime()) / 1000
    if (exam.scoreState === 1 && remainSecond > 3) {
        load.text = `整场考试未结束，请于${exam.endTime}后查询`
        await delay(1000)
        // console.log(`整场考试未结束，请于${exam.endTime}后查询`)
        router.push('/home')
        return
    }

    // 如果考试成绩是考试结束后公布，并且在接近整场考试结束时交卷，查询成绩
    if (exam.scoreState === 1 && remainSecond <= 3) {
        load.text = `成绩查询中`
        for (let i = 0; i < 6; i++) {
            if (i >= 5) {
                load.text = `查询失败，请稍后再次查询`
                await delay(1000)
                // console.log('查询失败，请稍后再次查询')
                router.push('/home')
                return
            }

            await delay(1000)
            await myExamQuery()

            if (myExam.markState as number >= 2) { // 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
                load.text = `考试已结束`
                await delay(1000)
                // console.log('如果考试成绩是考试结束后公布，并且在接近整场考试结束时交卷，查询成绩')
                router.push(`/my-exam/read/${route.params.examId}`)
                return
            }
        }
        return
    }
}

</script>
<style lang="scss" scoped>
.my-exam-paper {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .my-exam-paper__head {
        display: flex;
        justify-content: end;
        align-items: center;
        margin: 20px 20px 0px 20px;

        .my-exam-paper__btn {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 120px;
            height: 40px;
            margin-left: 10px;
            border: 0px;
            border-radius: 8px;
            color: white;

            background: linear-gradient(to right, #04c7f2, #259ff8);

            &.my-exam-paper__btn--warn {
                color: #FF5D15;
                border: 1px solid #FF5D15;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF)
            }

            .my-exam-paper__btn-icon {
                width: 18px;
                height: 18px;
                margin-right: 4px;
            }

            .my-exam-paper__btn-txt {
                font-size: 14px;
            }
        }
    }

    .my-exam-paper__main {
        flex: 1;
        display: flex;
        flex-direction: column;
        margin: 10px 20px 20px 20px;
        border: 1px solid #E5E5E5;

        .paper-toolbar {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            border-bottom: 1px solid #E5E5E5;
            height: 50px;
            padding: 0px 10px;

            .editor-toolbar__tip {
                font-size: 14px;
                color: #999999;
                line-height: 45px;

                .editor-toolbar__tip-warn {
                    color: #E43D33;
                }
            }

            .editor-toolbar__btn {
                padding: 0px 0px;
                margin: 0px 10px;
                border: 0px;
                position: relative;

                &:hover {
                    color: #999999;
                }

                &::after {
                    content: '';
                    position: absolute;
                    width: 1px;
                    height: 20px;
                    background-color: #CCCCCC;
                    right: -10px;
                    top: 50%;
                    transform: translateY(-50%)
                }

                &:last-child::after {
                    display: none;
                }

                .editor-toolbar__btn-icon {
                    font-size: 16px;
                }

                .editor-toolbar__btn-txt {
                    margin-left: 2px;
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }

                &.editor-toolbar__btn--active {
                    .editor-toolbar__btn-icon {
                        color: #1EA1EE;
                    }

                    .editor-toolbar__btn-txt {
                        color: #1EA1EE;
                    }
                }
            }

        }

        .paper__wrap {
            display: flex;

            .answer-sheet {
                display: flex;
                flex-direction: column;
                width: 220px;
                border-right: 1px solid #E5E5E5;

                .answer-sheet_title {
                    margin: 40px 0px 10px 0px;

                    .el-divider__text {
                        font-size: 16px;
                        color: #303133;
                    }
                }

                :deep(.answer-sheet__wrap) {
                    .el-scrollbar__view {
                        display: flex;
                        justify-content: center;


                        .answer-sheet {
                            display: flex;
                            flex-direction: row;
                            flex-wrap: wrap;
                            padding: 10px 0px 10px 10px;

                            .answer-sheet__chapter {
                                flex: 1 0 100%;
                                font-size: 14px;
                                color: #303133;
                                margin: 15px 0px 5px 0px;
                                cursor: pointer;
                            }

                            .answer-sheet__question {
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                position: relative;
                                width: 28px;
                                height: 28px;
                                margin: 5px 5px 5px 0px;
                                border-radius: 6px 6px 6px 6px;
                                font-size: 12px;
                                color: #8F939C;
                                background-color: #F5F5F5;
                                border: 1px solid #CCCCCC;
                                cursor: pointer;
                                z-index: 1;

                                &.answer-sheet__question--answered {
                                    background-color: #E4F6FF;
                                    border: 1px solid #1EA1EE;
                                }

                                &.answer-sheet__question--right {
                                    background-color: #D1F2D7;
                                    border: 1px solid #4FBF63;
                                }

                                &.answer-sheet__question--wrong {
                                    background-color: #FAD8D6;
                                    border: 1px solid #FF6960;
                                }

                                &.answer-sheet__question--half {
                                    border: initial;

                                    &::before {
                                        content: "";
                                        position: absolute;
                                        top: 0;
                                        right: 0;
                                        left: 0;
                                        height: 50%;
                                        background-color: #FAD8D6;
                                        border-left: 1px solid #FF6960;
                                        border-top: 1px solid #FF6960;
                                        border-right: 1px solid #FF6960;
                                        border-radius: 6px 6px 0px 0px;
                                        z-index: -1;
                                    }

                                    &::after {
                                        content: "";
                                        position: absolute;
                                        bottom: 0;
                                        right: 0;
                                        left: 0;
                                        height: 50%;
                                        background-color: #D1F2D7;
                                        border-left: 1px solid #4FBF63;
                                        border-bottom: 1px solid #4FBF63;
                                        border-right: 1px solid #4FBF63;
                                        border-radius: 0px 0px 6px 6px;
                                        z-index: -1;
                                    }
                                }
                            }

                            .answer-sheet__sort {
                                padding: 0px 10px;
                                width: 190px;
                                height: 40px;
                                line-height: 40px;
                                margin: 10px 5px;
                                background: #F1F8FF;
                                font-size: 14px;
                                border: 1px solid #CCCCCC;
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                                cursor: move;
                            }
                        }

                    }
                }

            }

            :deep(.paper) {
                flex: 1;
                margin-top: 20px;

                .el-scrollbar__view {
                    display: flex;
                    flex-direction: column;
                    padding: 10px 10px 10px 10px;
                    position: relative;

                    .paper__name {
                        display: flex;
                        justify-content: center;
                        text-align: center;
                        font-size: 24px;
                        color: #303133;
                        line-height: 45px;
                    }

                    .paper-chapter {
                        display: flex;
                        flex-direction: column;

                        .paper-chapter__name {
                            margin-top: 10px;
                            font-size: 16px;
                            font-size: 16px;
                            color: #303133;
                            line-height: 30px;
                        }

                        .paper-chapter__desc {
                            margin-top: 5px;
                            font-size: 14px;
                            color: #999999;
                            line-height: 22px;
                        }
                    }

                    .paper-question {
                        .mark-option {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;

                            margin-top: 10px;
                            padding: 10px 20px 5px 20px;
                            position: relative;
                            border: 1px solid #E5E5E5;


                            .mark-option__title {
                                position: absolute;
                                left: 15px;
                                top: -8px;
                                padding: 0px 10px;
                                background-color: #FFFFFF;
                                font-size: 14px;
                                color: #999999;
                            }

                            .mark-option__txt {
                                font-size: 14px;
                                color: #999999;
                                line-height: 45px;
                            }

                            .mark-option__input-number {
                                position: relative;
                                width: 40px;

                                .el-input-number__decrease,
                                .el-input-number__increase {
                                    display: none;
                                }

                                .el-input__wrapper {
                                    padding: 0px;
                                    box-shadow: initial;
                                    border-radius: 0;
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

                            .mark-option__btn {
                                border: 0px;
                                height: 30px;
                                border-radius: 6px 6px 6px 6px;

                                .mark-option__btn-icon {
                                    font-size: 12px;
                                }

                                .mark-option__btn-txt {
                                    margin-left: 4px;
                                    font-size: 12px;
                                }
                            }
                        }
                    }

                    .total-score {
                        display: flex;
                        flex-direction: column;
                        align-items: center;

                        position: absolute;
                        right: 50px;
                        top: 10px;
                        z-index: 1;

                        .total-score__inner {
                            display: flex;
                            align-items: flex-end;

                            .total-score__value {
                                font-weight: bold;
                                font-size: 36px;
                                color: #FF0000;
                                line-height: 45px;

                            }

                            .total-score__unit {
                                color: #FF0000;
                                font-size: 14px;
                                line-height: 30px;

                            }

                        }

                        .total-score__icon {
                            font-size: 88px;
                            text-align: center;
                            color: #FF0000;
                            line-height: 20px;
                        }
                    }
                }
            }
        }

    }

}
</style>
