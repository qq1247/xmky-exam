<template>
    <div class="my-exer-paper">
        <div class="my-exer-paper__head">
            <xmks-count-down :expireTime="myExer.endTime" preTxt="距练习结束：" :remind="300" color="#04C7F2"
                remind-color="#E43D33" font-size="14px" @end="$router.push('/my-exer-list')">
            </xmks-count-down>
        </div>
        <div class="my-exer-paper__main">
            <div class="paper-toolbar">
                <el-button type='' class="paper-toolbar__btn "
                    :class="{ 'paper-toolbar__btn--active': toolbars.answerShow }"
                    @click="toolbars.answerShow = !toolbars.answerShow; toolbars.analysisShow = !toolbars.analysisShow">
                    <span class="iconfont icon-icon_xiugaishijian paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.answerShow ? '答题' : '背题' }}</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn "
                    :class="{ 'paper-toolbar__btn--active': toolbars.randShow }"
                    @click="toolbars.randShow = !toolbars.randShow">
                    <span class="iconfont icon-a-icon_huaban1 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.randShow ? '顺序' : '随机' }}</span>
                </el-button>
            </div>
            <div class="paper__wrap">
                <div class="answer-sheet">
                    <el-divider class="answer-sheet_title">
                        答题卡
                    </el-divider>
                    <el-scrollbar height="calc(100vh - 300px)" class="answer-sheet__wrap">
                        <div class="answer-sheet">
                            <template v-for="(questionId, index) in questionIds" :key="index">
                                <div @click="questionView(index)" class="answer-sheet__question" :class="{
                                    'answer-sheet__question--answered': answered(index),
                                    'answer-sheet__question--right': isRight(index),
                                    'answer-sheet__question--wrong': isWrong(index),
                                    'answer-sheet__question--half': isHalf(index),
                                }">
                                    {{ index + 1 }}
                                </div>
                            </template>
                        </div>
                    </el-scrollbar>
                </div>
                <el-scrollbar height="calc(100vh - 269px)" class="_paper">
                    <div class="paper__head">
                        <el-button class="paper__btn" :disabled="curQuestion.index <= 0" @click="questionNext(false)">
                            <span class="iconfont icon-fanye-01 paper__btn-icon"></span>
                            <span class="paper__btn-txt">上一题</span>
                        </el-button>
                        <el-button class="paper__btn" :disabled="curQuestion.index >= questionIds.length - 1"
                            @click="questionNext(true)">
                            <span class="paper__btn-txt">下一题</span>
                            <span class="iconfont icon-fanye-02 paper__btn-icon"></span>
                        </el-button>
                    </div>
                    <xmks-question v-if="curQuestion.index >= 0" :id="`q${curQuestion.index}`"
                        :type="curQuestion.question.type as number" :title="curQuestion.question.title as string"
                        :options="curQuestion.question.options" :answers="curQuestion.question.answers"
                        :markType="curQuestion.question.markType as number"
                        :score="curQuestion.question.score as number" :scores="[]"
                        :analysis="curQuestion.question.analysis" :userAnswers="curQuestion.question.userAnswers"
                        :userScore="curQuestion.question.userScore" :answer-show="toolbars.answerShow"
                        :user-answer-show="true" :analysisShow="toolbars.analysisShow" :display="'paper'"
                        :editable="!toolbars.answerShow && curQuestion.question.userScore == null"
                        class="paper-question" @change="(answers: string[]) => {
                            curQuestion.question.userAnswers = answers
                            if (curQuestion.question.type === 1 || curQuestion.question.type === 4) {// 单选或判断，选完答案，自动下一题
                                questionNext(true)
                            }
                        }">
                        <template #title-pre>{{ curQuestion.index + 1 }}、</template>
                    </xmks-question>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { myExerGet, myExerQuestion, myExerQuestionList } from '@/api/my/my-exer'
import XmksQuestion from '@/components/question/xmks-question.vue'
import xmksCountDown from '@/components/xmks-count-down.vue'
import type { ExamQuestion } from '@/ts/exam/exam'
import type { Exer } from '@/ts/exam/exer'
import type { Question } from '@/ts/exam/question'
import { delay } from '@/util/timeUtil'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import _ from 'lodash'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'


/************************变量定义相关***********************/
const route = useRoute() // 路由
const toolbars = reactive({// 工具栏
    randShow: false,
    answerShow: false,
    totalScoreShow: false,
    analysisShow: false,
})

const questionIds = ref<number[]>([])// 试题IDS
const myExer = reactive<Exer>({// 我的练习
    id: null,
    name: '',
    startTime: '',
    endTime: '',
    questionBankId: undefined,
    userIds: [],
    rmkState: null,
})
const curQuestion = reactive({// 当前试题
    index: -1,// 索引
    question: {} as ExamQuestion,// 内容
    cache: {} as { [questionId: number]: ExamQuestion }// 缓存
})


/************************组件生命周期相关*********************/
onMounted(async () => {
    myExerQuery()
    await questionListQuery()
    await questionView(0)
})

/************************计算属性相关*************************/
const questionIdsOfShuffle = computed(() => _.shuffle(questionIds.value))// 点随机的时候用
const answered = computed(() => (index: number) => {
    const questionId = toolbars.randShow ? questionIdsOfShuffle.value[index] : questionIds.value[index]
    const question = curQuestion.cache[questionId as number]
    if (!question) {// 如果没有查看过这道题
        return false
    }
    if (!question.userAnswers?.length) {// 如果没有作答
        return false
    }
    if (question.type === 3 || question.type === 5) {// 如果是填空问答，一个字都没有
        if (question.userAnswers.every(userAnswer => userAnswer.length === 0)) {
            return false
        }
    }
    return true
})// 是否已答
const isRight = computed(() => (index: number) => {
    const questionId = toolbars.randShow ? questionIdsOfShuffle.value[index] : questionIds.value[index]
    const question = curQuestion.cache[questionId as number]
    if (!question) {// 如果没有查看过这道题
        return false
    }
    if (!question.userAnswers?.length) {// 如果没有作答
        return false
    }
    if (question.type === 3 || question.type === 5) {// 如果是填空问答，一个字都没有
        if (question.userAnswers.every(userAnswer => userAnswer.length === 0)) {
            return false
        }
    }
    if (!(question.type === 1 || question.type === 2 || question.type === 4)) {// 如果不是单选多选判断
        return false
    }

    return question.userScore != null && question.userScore === question.score
}); // 是否答对
const isHalf = computed(() => (index: number) => {
    const questionId = toolbars.randShow ? questionIdsOfShuffle.value[index] : questionIds.value[index]
    const question = curQuestion.cache[questionId as number]
    if (!question) {// 如果没有查看过这道题
        return false
    }
    if (!question.userAnswers?.length) {// 如果没有作答
        return false
    }
    if (question.type === 3 || question.type === 5) {// 如果是填空问答，一个字都没有
        if (question.userAnswers.every(userAnswer => userAnswer.length === 0)) {
            return false
        }
    }
    if (!(question.type === 1 || question.type === 2 || question.type === 4)) {// 如果不是单选多选判断
        return false
    }

    return question.userScore != null && question.userScore > 0 && question.userScore !== question.score
}); // 是否半对
const isWrong = computed(() => (index: number) => {
    const questionId = toolbars.randShow ? questionIdsOfShuffle.value[index] : questionIds.value[index]
    const question = curQuestion.cache[questionId as number]
    if (!question) {// 如果没有查看过这道题
        return false
    }
    if (!question.userAnswers?.length) {// 如果没有作答
        return false
    }
    if (question.type === 3 || question.type === 5) {// 如果是填空问答，一个字都没有
        if (question.userAnswers.every(userAnswer => userAnswer.length === 0)) {
            return false
        }
    }
    if (!(question.type === 1 || question.type === 2 || question.type === 4)) {// 如果不是单选多选判断
        return false
    }

    return question.userScore != null && question.userScore === 0
}); // 是否答错


/************************事件相关*****************************/
// 练习查询
async function myExerQuery() {
    const { data: { data } } = await myExerGet({ exerId: route.params.exerId })
    myExer.startTime = data.startTime
    myExer.endTime = data.endTime
    myExer.rmkState = data.rmkState
}

// 试题列表查询
async function questionListQuery() {
    const { data: { data } } = await myExerQuestionList({ exerId: route.params.exerId })
    questionIds.value.push(...data)
}

// 试题查询
async function questionView(index: number) {
    const questionId = toolbars.randShow ? questionIdsOfShuffle.value[index] : questionIds.value[index]
    if (curQuestion.cache[questionId as number]) {
        curQuestion.index = index
        curQuestion.question = curQuestion.cache[questionId as number]
    } else {
        const { data: { data: data } } = await myExerQuestion({ exerId: route.params.exerId, questionId })
        data.userAnswers = [] // 接口没有
        data.userScore = null // 接口没有
        curQuestion.question = data
        curQuestion.cache[questionId as number] = data

        curQuestion.index = index
    }
}

/**
 * 下一题
 *
 * @param hasNext true：下一题；false：上一题
 */
async function questionNext(hasNext: boolean) {
    // 数据校验
    if (hasNext) {
        if (curQuestion.index >= questionIds.value.length - 1) {
            ElMessage.success('最后一题')
            return
        }
    }
    if (!hasNext) {
        if (curQuestion.index < 0) {
            ElMessage.success('第一题')
            return
        }
    }

    // 如果是答题模式，选择错误时，标记错误
    await delay(500) // 切换太快体验差
    if (!toolbars.answerShow) {
        if (curQuestion.question.type === 1 || curQuestion.question.type === 4) {// 如果是单选或判断
            if (curQuestion.question.userScore == null) {// 如果没打分
                if (curQuestion.question.userAnswers && curQuestion.question.userAnswers[0]) {// 如果有答案
                    curQuestion.question.userScore = 0
                    if (curQuestion.question.userAnswers[0] === (curQuestion.question.answers as string[])[0]) {// 打分
                        curQuestion.question.userScore = curQuestion.question.score
                    }
                    if (curQuestion.question.userScore === 0) {// 如果答错，则停留当前题，不进入下一题
                        return
                    }
                }
            }
        }
        if (curQuestion.question.type === 2) {// 如果是多选
            if (curQuestion.question.userScore == null) {// 如果没打分
                if (curQuestion.question.userAnswers && curQuestion.question.userAnswers[0]) {// 如果有答案
                    curQuestion.question.userScore = 0
                    const include = curQuestion.question.userAnswers.every((userAnswer: string) => (curQuestion.question.answers as string[]).includes(userAnswer))
                    if (include && (curQuestion.question.answers as string[]).length === curQuestion.question.userAnswers.length) {// 打分
                        curQuestion.question.userScore = curQuestion.question.score
                    } else if (include) {
                        curQuestion.question.userScore = (curQuestion.question.scores as number[])[0]
                    }

                    if ((curQuestion.question.userScore as number) < (curQuestion.question.score as number)) {// 如果答错，则停留当前题，不进入下一题
                        return
                    }
                }
            }
        }
    }

    // 下一题
    if (hasNext) {
        questionView(curQuestion.index + 1)
    } else {
        questionView(curQuestion.index - 1)
    }
}
</script>
<style lang="scss" scoped>
.my-exer-paper {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin-top: 20px;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .my-exer-paper__head {
        display: flex;
        justify-content: end;
        align-items: center;
        margin: 20px 20px 0px 20px;

        .my-exer-paper__btn {
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

            &.my-exer-paper__btn--warn {
                color: #FF5D15;
                border: 1px solid #FF5D15;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF)
            }

            .my-exer-paper__btn-icon {
                width: 18px;
                height: 18px;
                margin-right: 4px;
            }

            .my-exer-paper__btn-txt {
                font-size: 14px;
            }
        }
    }

    .my-exer-paper__main {
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

            :deep(._paper) {
                // 和组件的样式冲突
                flex: 1;
                margin: 20px 20px 10px 20px;

                .paper__head {

                    border-bottom: 1px solid #E5E5E5;

                    .paper__btn {
                        padding: 0px 0px;
                        margin: 0px 10px;
                        border: 0px;
                        position: relative;

                        &:hover {
                            background-color: initial;
                        }

                        .paper__btn-icon {
                            font-size: 12px;
                        }

                        .paper__btn-txt {
                            padding: 0px 5px;
                            font-size: 14px;
                            color: #999999;
                            line-height: 45px;
                        }
                    }
                }
            }
        }

    }

}
</style>
