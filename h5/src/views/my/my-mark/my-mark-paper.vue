<template>
    <div v-loading="load.loading" :element-loading-text="load.text"
        element-loading-background="rgba(122, 122, 122, 0.8)" class="my-mark-paper">
        <div class="my-mark-paper__head">
            <el-form :inline="true">
                <el-form-item label="当前批阅：">
                    <el-select v-model="curMarkIndex" filterable placeholder="请选择" @change="paperQuery()">
                        <el-option v-for="(mark, index) in markList" :key="index" :label="mark.examUserName"
                            :value="index">
                            {{ mark.examUserName }}/{{ mark.markUserName }} -
                            <span>
                                {{ mark.myExamState === 1 ? dictStore.getValue('EXAM_STATE', mark.myExamState) :
                                    dictStore.getValue('MARK_STATE', mark.myExamMarkState) }}
                            </span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="批阅进度：">
                    {{ markPaperNum }}/{{ claimPaperNum }}
                </el-form-item>
                <el-form-item label="">
                    <xmks-count-down v-if="marking" :expireTime="exam.markEndTime" preTxt="距阅卷结束：" :remind="300"
                        color="#04C7F2" remind-color="#E43D33" font-size="14px">
                    </xmks-count-down>
                </el-form-item>
                <el-form-item label="">
                    <el-button class="my-mark-paper__btn" :disabled="curMarkIndex === 0" @click="paperPre">
                        <span class="iconfont icon-fanye-01 my-mark-paper__btn-icon"></span>
                        <span class="my-mark-paper__btn-txt">上一卷</span>
                    </el-button>
                    <el-button class="my-mark-paper__btn" :disabled="curMarkIndex === markList.length - 1"
                        @click="paperNext">
                        <span class="my-mark-paper__btn-txt">下一卷</span>
                        <span class="iconfont icon-fanye-02 my-mark-paper__btn-icon"></span>
                    </el-button>
                    <el-button v-if="marking" class="my-mark-paper__finish-btn"
                        :disabled="markList[curMarkIndex]?.myExamState !== 3" @click="finish">
                        <span class="iconfont icon-lianxi-61 my-mark-paper__btn-icon"></span>
                        <span class="my-mark-paper__btn-txt">{{ markList[curMarkIndex]?.myExamState !== 3 ? '无须阅卷' :
                            '完成阅卷' }}</span>
                    </el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="my-mark-paper__main">
            <div class="paper-toolbar">
                <el-popover placement="bottom" :width="400" trigger="click">
                    <template #reference>
                        <el-button type='' class="paper-toolbar__btn">
                            <span class="iconfont icon-icon-05 paper-toolbar__btn-icon"></span>
                            <span class="paper-toolbar__btn-txt">批阅题号</span>
                        </el-button>
                    </template>
                    <el-checkbox-group v-model="viewQuestions">
                        <el-checkbox label="客观试题" :value="0" />
                        <template v-for="(examQuestion, index) in examQuestions" :key="index">
                            <el-checkbox v-if="examQuestion.markType === 2" :label="examQuestion.no"
                                :value="examQuestion.no" />
                        </template>
                    </el-checkbox-group>
                </el-popover>

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
                <el-button type='' class="paper-toolbar__btn"
                    :class="{ 'paper-toolbar__btn--active': toolbars.markOptionShow }"
                    @click="toolbars.markOptionShow = !toolbars.markOptionShow">
                    <span class="iconfont icon-icon-05 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.markOptionShow ? '隐藏阅卷选项' : '显示阅卷选项' }}</span>
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
                                <div v-else-if="hasQuestionShow(examQuestion)" @click="scrollView(index)"
                                    class="answer-sheet__question" :class="{
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
                <el-scrollbar height="calc(100vh - 269px)" class="paper">
                    <div v-if="myExam.totalScore" class="total-score">
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
                        <xmks-question v-else-if="hasQuestionShow(examQuestion)" :id="`q${index}`"
                            :type="examQuestion.questionType as number" :title="examQuestion.title as string"
                            :options="examQuestion.options" :answers="examQuestion.answers"
                            :markType="examQuestion.markType as number" :score="examQuestion.score as number"
                            :scores="examQuestion.scores" :analysis="examQuestion.analysis"
                            :userAnswers="examQuestion.userAnswers" :userScore="examQuestion.userScore"
                            :answer-show="toolbars.answerShow" :user-answer-show="true"
                            :analysisShow="toolbars.analysisShow" :display="'paper'" :editable="false"
                            class="paper-question">
                            <template #title-pre>{{ examQuestion.no }}、</template>
                            <template #foot>
                                <div v-if="toolbars.markOptionShow && examQuestion.markType === 2" class="mark-option">
                                    <div class="mark-option__title">阅题</div>
                                    <span class="mark-option__txt">本题得</span>
                                    <el-input-number v-model="examQuestion.userScore" :min="0" :max="examQuestion.score"
                                        :precision="2" controls-position="right" :readonly="examQuestion.commit"
                                        class="mark-option__input-number"
                                        @blur="() => score(examQuestion)" /><!-- 用blur事件，输入字母或删除数字不触发change事件 -->
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
import xmksCountDown from '@/components/xmks-count-down.vue'
import _ from 'lodash'
import { delay } from '@/util/timeUtil'
import { myMarkFinish, myMarkMarkList, myMarkPaper, myMarkScore } from '@/api/my/my-mark'
import { useDictStore } from '@/stores/dict'
import { examGet } from '@/api/exam/exam'
import { ElMessage } from 'element-plus'

/************************变量定义相关***********************/
const route = useRoute() // 路由
const dictStore = useDictStore() // 字典缓存
const markList = ref<any[]>([])// 阅卷列表
const curMarkIndex = ref(0) // 当前批阅位置
const examQuestions = ref<ExamQuestion[]>([])// 试卷
const viewQuestions = ref<number[]>([])// 当前预览试题

const toolbars = reactive({// 工具栏
    answerShow: false,
    totalScoreShow: false,
    analysisShow: false,
    markOptionShow: true,
})

const exam = reactive<Exam>({// 考试
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
    limitMinute: null
})
const myExam = reactive<MyExam>({// 我的考试
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
})
const load = reactive({// 加载
    loading: false, // 显示加载页面
    text: '',// 显示加载页面内容
    second: 6,// 倒计时秒数
})



/************************组件生命周期相关*********************/
onMounted(async () => {
    examQuery()
    await markListQuery()
    await paperQuery();

    examQuestions.value.forEach((examQuestion: ExamQuestion) => {// 默认显示待批阅的（主观）试题
        if (examQuestion.type === 2 && examQuestion.markType === 2) {
            viewQuestions.value.push(examQuestion.no as number)
        }
    });

})

/************************计算属性相关*************************/
const marking = computed(() => (exam.markState === 2)) // 是否阅卷中
const unMarkQuestions = computed(() => examQuestions.value.filter((examQuestion) => examQuestion.markType === 2 && (examQuestion.userScore == null)).map(examQuestion => examQuestion.no)) // 未批阅试题
const claimPaperNum = computed(() => markList.value.length)// 已领取试卷数量
const markPaperNum = computed(() => markList.value.filter((user) => user.myExamMarkState === 3).length) // 已领取且已批阅试卷数量
const hasQuestionShow = computed(() => (examQuestion: ExamQuestion) => (viewQuestions.value.indexOf(0) != -1 && examQuestion.markType === 1) || viewQuestions.value.indexOf(examQuestion.no as number) != -1) // 是否可以显示
const isHalf = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore > 0 && question.userScore !== question.score); // 是否半对
const isRight = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === question.score); // 是否答对
const isWrong = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === 0); // 是否答错

/************************事件相关*****************************/

// 获取阅卷列表
async function markListQuery() {
    const { data: { data } } = await myMarkMarkList({ examId: route.params.examId })
    markList.value = data

}

// 试卷查询
async function paperQuery() {
    load.text = `正在打开${markList.value[curMarkIndex.value]?.examUserName}的试卷...`
    load.loading = true

    await delay(200)
    const { data: { data } } = await myMarkPaper({
        examId: route.params.examId,
        userId: markList.value[curMarkIndex.value]?.examUserId
    })
    let no = 1;
    examQuestions.value = data.map((examQuestion: ExamQuestion) => {
        if (examQuestion.type === 2) { // 处理题号
            examQuestion.no = no++;
        }
        return examQuestion;
    })

    await markListQuery() // 第一次打开试卷，阅卷状态就变了，重新更新数据

    load.loading = false
}

// 考试查询
async function examQuery() {
    const { data: { data } } = await examGet({ id: route.params.examId })
    exam.id = parseInt(route.params.examId as string)
    exam.name = data.name;
    exam.paperName = data.paperName;
    exam.startTime = data.startTime;
    exam.endTime = data.endTime;
    exam.markStartTime = data.markStartTime;
    exam.markEndTime = data.markEndTime;
    exam.markState = data.markState;
    exam.scoreState = data.scoreState;
    exam.rankState = data.rankState;
    exam.passScore = data.passScore;
    exam.totalScore = data.totalScore;
    exam.markType = data.markType;
    exam.genType = data.genType;
    exam.sxes = data.sxes;
    exam.state = data.state;
    exam.userNum = data.userNum;
    exam.limitMinute = data.limitMinute;
}

// 滚动预览
function scrollView(index: number) {
    (document.querySelector(`#q${index}`) as HTMLElement).scrollIntoView(true)
};

// 上一卷
async function paperPre() {
    curMarkIndex.value--
    const mark = markList.value[curMarkIndex.value]

    load.text = `正在打开${mark?.examUserName}的试卷...`
    load.loading = true

    await delay(500)
    await paperQuery()

    load.loading = false
}

// 下一卷
async function paperNext() {
    curMarkIndex.value++
    const mark = markList.value[curMarkIndex.value]

    load.text = `正在打开${mark?.examUserName}的试卷...`
    load.loading = true

    await delay(200)
    await paperQuery()

    load.loading = false
}

// 打分
async function score(examQuestion: ExamQuestion) {
    if (!examQuestion.userScore) {
        examQuestion.userScore = 0
    }

    examQuestion.commit = true
    const { data: { code } } = await myMarkScore({
        examId: route.params.examId,
        userId: markList.value[curMarkIndex.value]?.examUserId,
        questionId: examQuestion.questionId,
        userScore: examQuestion.userScore
    })
    examQuestion.commit = false

    if (code !== 200) {
        examQuestion.userScore = null// 如果打分失败，把页面的分数清空掉，重新尝试打分
        return
    }
}

async function finish() {
    if (unMarkQuestions.value.length) {
        ElMessage.error(`${unMarkQuestions.value.join('、')}题未阅`)
        return
    }

    load.text = `正在上传${markList.value[curMarkIndex.value]?.examUserName}的成绩...`
    load.loading = true

    await delay(200)
    const { data: { code } } = await myMarkFinish({
        examId: route.params.examId,
        userId: markList.value[curMarkIndex.value]?.examUserId,
    })

    if (code === 200) {
        ElMessage.success('批阅成功')
    }

    await markListQuery() // 获取最新阅卷状态
    load.loading = false
}
</script>
<style lang="scss" scoped>
.my-mark-paper {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin-top: 20px;
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
