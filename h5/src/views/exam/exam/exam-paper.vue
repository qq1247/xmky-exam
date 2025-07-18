<template>
    <div v-loading="load.loading" :element-loading-text="load.text"
        element-loading-background="rgba(122, 122, 122, 0.8)" class="my-mark-paper">
        <div class="my-mark-paper__main">
            <div class="paper-toolbar">
                <el-button type='' class="paper-toolbar__btn"
                    :class="{ 'paper-toolbar__btn--active': toolbars.answerShow }"
                    @click="toolbars.answerShow = !toolbars.answerShow">
                    <span class="iconfont icon-icon-03 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.answerShow ? '隐藏标准答案' : '显示标准答案' }}</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn"
                    :class="{ 'paper-toolbar__btn--active': toolbars.analysisShow }"
                    @click="toolbars.analysisShow = !toolbars.analysisShow">
                    <span class="iconfont icon-icon-06 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.analysisShow ? '隐藏解析' : '显示解析' }}</span>
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
                <el-scrollbar height="calc(100vh - 155px)" class="paper">
                    <div class="total-score">
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
                            :editable="false" class="paper-question">
                            <template #title-pre>{{ examQuestion.no }}、</template>
                            <template #foot>
                                <div v-if="toolbars.markOptionShow && examQuestion.markType === 2" class="mark-option">
                                    <div class="mark-option__title">阅题</div>
                                    <span class="mark-option__txt">本题得</span>
                                    <el-input-number v-model="examQuestion.userScore" :min="0" :max="examQuestion.score"
                                        :precision="2" controls-position="right" :readonly="examQuestion.commit"
                                        class="mark-option__input-number" /><!-- 用blur事件，输入字母或删除数字不触发change事件 -->
                                    <span class="mark-option__txt">分</span>
                                </div>
                            </template>
                        </xmks-question>
                    </template>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import type { Exam, ExamQuestion } from '@/ts/exam/exam'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import XmksQuestion from '@/components/question/xmks-question.vue'
import type { MyExam } from '@/ts/exam/my-exam'
import { delay } from '@/util/timeUtil'
import { myMarkGet, myMarkPaper } from '@/api/my/my-mark'
import { examGet } from '@/api/exam/exam'
import type { User } from '@/ts/base/user'
import { userGet } from '@/api/base/user'
import http from '@/request'

/************************变量定义相关***********************/
const route = useRoute() // 路由
const examQuestions = ref<ExamQuestion[]>([])// 试卷
const viewQuestions = ref<number[]>([])// 当前预览试题
const logoUrl = ref(`${http.defaults.baseURL}login/logo`)// logo链接

const toolbars = reactive({// 工具栏
    answerShow: false,
    totalScoreShow: false,
    analysisShow: false,
    markOptionShow: false,
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
const user = reactive<User>({
    id: null,
    orgId: 1,
    name: '',
    loginName: '',
    type: 1,
    state: 0,
})
const load = reactive({// 加载
    loading: false, // 显示加载页面
    text: '',// 显示加载页面内容
    second: 6,// 倒计时秒数
})



/************************组件生命周期相关*********************/
onMounted(async () => {
    examQuery()
    await userQuery()
    await paperQuery();
    myExamQuery()

    document.title = user.name + '的试卷'
    const favicon = document.querySelector('link[rel="icon"]') as HTMLLinkElement;
    favicon.href = logoUrl.value;

    examQuestions.value.forEach((examQuestion: ExamQuestion) => {// 默认显示待批阅的（主观）试题
        if (examQuestion.type === 2 && examQuestion.markType === 2) {
            viewQuestions.value.push(examQuestion.no as number)
        }
    });

})

/************************计算属性相关*************************/
const isHalf = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore > 0 && question.userScore !== question.score); // 是否半对
const isRight = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === question.score); // 是否答对
const isWrong = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === 0); // 是否答错


/************************事件相关*****************************/

// 试卷查询
async function paperQuery() {
    load.text = `正在打开${user.name}的试卷...`
    load.loading = true

    await delay(200)
    const { data: { data: data1 } } = await myMarkPaper({
        examId: route.params.examId,
        userId: route.params.userId
    })
    let no = 1;
    examQuestions.value = data1.map((examQuestion: ExamQuestion) => {
        if (examQuestion.type === 2) { // 处理题号
            examQuestion.no = no++;
        }
        return examQuestion;
    })

    load.loading = false
}

// 我的考试查询
async function myExamQuery() {
    const { data: { data } } = await myMarkGet({ examId: route.params.examId, userId: route.params.userId })
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
// 用户查询
async function userQuery() {
    const { data: { data } } = await userGet({ id: route.params.userId })
    user.id = data.id
    user.name = data.name
    user.loginName = data.loginName
    user.orgId = data.orgId
    user.state = data.state
}

// 滚动预览
function scrollView(index: number) {
    (document.querySelector(`#q${index}`) as HTMLElement).scrollIntoView(true)
};


</script>
<style lang="scss" scoped>
.my-mark-paper {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px auto 20px auto;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .my-mark-paper__head {
        display: flex;
        justify-content: end;
        align-items: center;
        margin: 20px 20px 0px 20px;

        .my-mark-paper__btn {
            padding: 0px 0px;
            margin: 0px 10px;
            border: 0px;
            position: relative;

            &:hover {
                background-color: initial;
            }

            .my-mark-paper__btn-icon {
                font-size: 12px;
            }

            .my-mark-paper__btn-txt {
                padding: 0px 5px;
                font-size: 14px;
                color: #999999;
                line-height: 45px;
            }
        }

        .my-mark-paper__finish-btn {
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

            &.is-disabled {
                background: linear-gradient(to right, #dfdfdf 0%, #cacaca 100%);
            }

            .my-mark-paper__btn-icon {
                width: 18px;
                height: 18px;
                margin-right: 4px;
            }

            .my-mark-paper__btn-txt {
                font-size: 14px;
            }
        }
    }

    .my-mark-paper__main {
        flex: 1;
        display: flex;
        flex-direction: column;
        margin: 20px 20px 20px 20px;
        border: 1px solid #E5E5E5;

        .paper-toolbar {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            border-bottom: 1px solid #E5E5E5;
            height: 50px;
            padding: 0px 10px;

            .paper-toolbar__tip {
                font-size: 14px;
                color: #999999;
                line-height: 45px;

                .paper-toolbar__tip-warn {
                    color: #E43D33;
                }
            }

            .paper-toolbar__btn {
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

                .paper-toolbar__btn-icon {
                    font-size: 16px;
                }

                .paper-toolbar__btn-txt {
                    margin-left: 2px;
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }

                &.paper-toolbar__btn--active {
                    .paper-toolbar__btn-icon {
                        color: #1EA1EE;
                    }

                    .paper-toolbar__btn-txt {
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
                                width: 60px;

                                .el-input-number__decrease,
                                .el-input-number__increase {
                                    display: none;
                                }

                                .el-input__wrapper {
                                    padding: 0px;
                                    box-shadow: initial;
                                    border-radius: 0;

                                    .el-input__inner {
                                        font-size: 18px;
                                        color: #FF0000;
                                    }
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
