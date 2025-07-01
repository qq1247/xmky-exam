<template>
    <div class="my-exer-paper">
        <div class="my-exer-paper__head">
            <el-button type="primary" class="my-exer-paper__btn"
                @click="$router.push(`/my-exer/read/${route.params.exerId}`)">
                <span class="iconfont icon-lianxi-61 my-exer-paper__btn-icon"></span>
                <span class="editor-toolbar__btn-txt">完成练习</span>
            </el-button>
        </div>
        <div class="my-exer-paper__main">
            <div class="paper-toolbar">
                <el-button type='' class="paper-toolbar__btn"
                    :class="{ 'paper-toolbar__btn--active': toolbars.answerShow }"
                    @click="toolbars.answerShow = !toolbars.answerShow; toolbars.analysisShow = !toolbars.analysisShow">
                    <span class="iconfont icon-icon_xiugaishijian paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.answerShow ? '答题' : '背题' }}</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn"
                    :class="{ 'paper-toolbar__btn--active': toolbars.randShow }"
                    @click="toolbars.randShow = !toolbars.randShow; questionView()">
                    <span class="iconfont icon-a-icon_huaban1 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.randShow ? '顺序' : '随机' }}</span>
                </el-button>
            </div>
            <div class="paper__wrap">
                <div class="answer-sheet">
                    <el-divider class="answer-sheet__title">
                        答题卡
                    </el-divider>
                    <el-scrollbar height="calc(100vh - 300px)" class="answer-sheet__wrap">
                        <div class="answer-sheet">
                            <template v-for="(myExerQuestion, index) in _myExerQuestions" :key="index">
                                <div @click="curQuestionIndex = index; questionView()" class="answer-sheet__question"
                                    :class="{
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
                <el-scrollbar height="calc(100vh - 269px)" class="paper">
                    <div class="paper__head">
                        <el-form ref="formRef" :model="form" :rules="formRules" class="form">
                            <el-form-item prop="score" :rules="formRules.score">
                                <el-button :disabled="curQuestionIndex <= 0" text class="paper__btn"
                                    @click="curQuestionIndex--; questionView()">
                                    <span class="iconfont icon-fanye-01 paper__btn-icon"></span>
                                    <span class="paper__btn-txt">上一题</span>
                                </el-button>
                                <el-button :disabled="curQuestionIndex >= _myExerQuestions.length - 1" text
                                    class="paper__btn" @click="curQuestionIndex++; questionView()">
                                    <span class="paper__btn-txt paper__btn-txt-next">下一题</span>
                                    <span class="iconfont icon-fanye-02 paper__btn-icon"></span>
                                </el-button>
                                <template
                                    v-if="!toolbars.answerShow && curExamQuestion?.userScore == null && curExamQuestion?.markType === 2"><!--  如果是主观题，需要自评 -->
                                    <span class="paper-question__txt">自评</span>
                                    <el-input-number v-model="form.score" :min="0"
                                        :max="_myExerQuestions[curQuestionIndex]?.score" :step="0.5" :precision="1"
                                        controls-position="right" class="paper-question__input-number" />
                                    <span class="paper-question__txt">分</span>
                                </template>
                                <el-button v-if="!toolbars.answerShow && curExamQuestion?.userScore == null"
                                    class="paper__btn paper__btn--highlight" @click="answer()">
                                    <span class="paper__btn-txt paper__btn-txt-next">确认作答</span>
                                    <span class="iconfont icon-lianxi-61 paper__btn-icon"></span>
                                </el-button>
                            </el-form-item>
                        </el-form>
                        <div class="opt">
                            <el-button type='' class="opt__btn"
                                :class="{ 'opt__btn--active': _myExerQuestions[curQuestionIndex]?.fav === 1 }"
                                @click="fav">
                                <span class="iconfont opt__btn-icon" :class="{
                                    'icon-lianxi-66': _myExerQuestions[curQuestionIndex]?.fav === 1,
                                    'icon-lianxi-64': _myExerQuestions[curQuestionIndex]?.fav === 2,
                                }"></span>
                                <span class="opt__btn-txt">{{ _myExerQuestions[curQuestionIndex]?.fav === 1 ? '已收藏' :
                                    '未收藏' }}</span>
                            </el-button>

                            <el-button v-if="_myExerQuestions[curQuestionIndex]?.wrongAnswerNum" type=''
                                class="opt__btn opt__btn--active" @click="wrongReset">
                                <span class="opt__btn-txt opt__btn-txt--wrong">答错{{
                                    _myExerQuestions[curQuestionIndex]?.wrongAnswerNum
                                    }}次&nbsp;&nbsp;</span>
                                <span class="iconfont icon-lianxi-61 opt__btn-icon"></span>
                                <span class="opt__btn-txt">已掌握</span>
                            </el-button>
                        </div>
                    </div>
                    <xmks-question v-if="curExamQuestion" :id="`q${curQuestionIndex}`"
                        :type="curExamQuestion.questionType as number" :title="curExamQuestion.title as string"
                        :img-ids="curExamQuestion.imgFileIds" :options="curExamQuestion.options"
                        :answers="curExamQuestion.answers" :markType="curExamQuestion.markType as number"
                        :score="curExamQuestion.score as number" :scores="curExamQuestion.scores"
                        :analysis="curExamQuestion.analysis" :userAnswers="curExamQuestion.userAnswers"
                        :userScore="curExamQuestion.userScore"
                        :answer-show="toolbars.answerShow || curExamQuestion?.userScore != null"
                        :user-answer-show="!toolbars.answerShow || curExamQuestion?.userScore != null"
                        :analysisShow="toolbars.analysisShow || curExamQuestion?.userScore != null" :display="'paper'"
                        :editable="!toolbars.answerShow && curExamQuestion.userScore == null" class="paper-question"
                        @change="(userAnswers: string[]) => (curExamQuestion as ExamQuestion).userAnswers = userAnswers">
                        <template #title-pre>{{ curQuestionIndex + 1 }}、</template>
                    </xmks-question>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';
import XmksQuestion from '@/components/question/xmks-question.vue'
import type { MyExerQuestion } from '@/ts/exam/my-exer-question';
import { myExerAnswer, myExerFav, myExerGenerate, myExerQuestion, myExerWrongReset } from '@/api/my/my-exer';
import { useRoute } from 'vue-router';
import type { ExamQuestion } from '@/ts/exam/exam';
import type { FormInstance, FormRules } from 'element-plus';

/************************变量定义相关***********************/
const route = useRoute() // 路由
const toolbars = reactive({// 工具栏
    randShow: false,
    answerShow: false,
    analysisShow: false,
})
const myExerQuestions = ref<MyExerQuestion[]>([])// 我的练习试题
const questionCache = ref<Map<number, ExamQuestion>>(new Map()) // 试题缓存
const windowSize = ref<number>(10) // 滑动窗口大小（一次性加载当前试题前后十道题，使练习时页面切换更加顺畅）
const curQuestionIndex = ref<number>(-1) // 当前试题索引
const curExamQuestion = ref<ExamQuestion>()

const form = reactive<any>({
    score: 1.0
})// 表单
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    score: [{
        trigger: 'change',
        validator: (rule: any, value: any, callback: any) => {
            if (value == null || value === '') {
                return callback(new Error('请输入分数'))
            }
            if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
            }
            if (value > 20) {
                return callback(new Error('最大20分'))
            }
            return callback()
        }
    }],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    // 加载试题列表
    await questionQuery()

    // 显示第一题
    curQuestionIndex.value = 0
    questionView()
})

/************************计算属性相关*************************/
const _myExerQuestions = computed(() => {// 我的练习试题列表（随机或顺序）
    return [...myExerQuestions.value].sort((a, b) => {
        if (toolbars.randShow) {
            return (a.shuffleNo || 0) - (b.shuffleNo || 0)
        } else {
            return (a.no || 0) - (b.no || 0)
        }
    })
})
const answered = computed(() => (index: number) => {
    const questionId = _myExerQuestions.value[index].questionId as number
    const examQuestion = questionCache.value.get(questionId)
    if (!examQuestion) {// 如果没有查看过这道题
        return false
    }
    if (!examQuestion.userAnswers?.length) {// 如果没有作答
        return false
    }
    if (examQuestion.questionType === 3 || examQuestion.questionType === 5) {// 如果是填空问答，一个字都没有
        if (examQuestion.userAnswers.every(userAnswer => userAnswer.length === 0)) {
            return false
        }
    }
    return true
})// 是否已答
const isRight = computed(() => (index: number) => {
    const myExerQuestion = _myExerQuestions.value[index]
    if (myExerQuestion.userScore == null) {// 如果没有作答
        return false
    }

    return myExerQuestion.userScore === myExerQuestion.score
}); // 是否答对
const isHalf = computed(() => (index: number) => {
    const myExerQuestion = _myExerQuestions.value[index]
    if (myExerQuestion.userScore == null) {// 如果没有作答
        return false
    }

    return myExerQuestion.userScore > 0 && myExerQuestion.userScore !== myExerQuestion.score
}); // 是否半对
const isWrong = computed(() => (index: number) => {
    const myExerQuestion = _myExerQuestions.value[index]
    if (myExerQuestion.userScore == null) {// 如果没有作答
        return false
    }

    return myExerQuestion.userScore === 0
}); // 是否答错

/************************事件相关*****************************/
// 试题列表查询
async function questionQuery() {
    const { data: { data } } = await myExerGenerate({ exerId: route.params.exerId, type: route.params.type })
    myExerQuestions.value.push(...data)
}

/**
 * 试题预览
 * 
 * 利用滑动窗口策略，提前加载部分数据，使练习时页面切换更加顺畅
 */
async function questionView() {
    const curQuestionId = _myExerQuestions.value[curQuestionIndex.value]?.questionId as number;
    if (questionCache.value.has(curQuestionId)) {
        curExamQuestion.value = questionCache.value.get(curQuestionId)
        return
    }

    const startIndex = Math.max(0, curQuestionIndex.value - windowSize.value)
    const endIndex = Math.min(_myExerQuestions.value.length - 1, curQuestionIndex.value + windowSize.value)
    const uncacheQuestionId = _myExerQuestions.value
        .slice(startIndex, endIndex + 1)
        .map(q => q.questionId)
        .filter(questionId => !questionCache.value.has(questionId as number))

    Promise.all(uncacheQuestionId.map(questionId =>
        myExerQuestion({
            exerId: route.params.exerId,
            questionId
        }).then(res => {
            questionCache.value.set(questionId as number, res.data.data)
        })
    )).then(() => {
        curExamQuestion.value = questionCache.value.get(curQuestionId)
    });
}

// 答题
async function answer() {
    const { data: { code, data } } = await myExerAnswer({
        exerId: route.params.exerId,
        questionId: curExamQuestion.value?.questionId,
        userAnswers: curExamQuestion.value?.userAnswers,
        userScore: curExamQuestion.value?.markType === 2 ? form.score : null,// 主观题有效
    })
    if (code !== 200) {
        return
    }

    curExamQuestion.value!.userScore = data
    _myExerQuestions.value[curQuestionIndex.value].userScore = data

    if (_myExerQuestions.value[curQuestionIndex.value].userScore !== _myExerQuestions.value[curQuestionIndex.value].score) {
        _myExerQuestions.value[curQuestionIndex.value].wrongAnswerNum = _myExerQuestions.value[curQuestionIndex.value].wrongAnswerNum as number + 1
    }
}

// 收藏试题
async function fav() {
    const { data: { code, data } } = await myExerFav({
        exerId: route.params.exerId,
        questionId: curExamQuestion.value?.questionId,
    })
    if (code !== 200) {
        return
    }

    _myExerQuestions.value[curQuestionIndex.value].fav = _myExerQuestions.value[curQuestionIndex.value].fav === 1 ? 2 : 1
}
// 错题重置（从历史错题中移除）
async function wrongReset() {
    const { data: { code, data } } = await myExerWrongReset({
        exerId: route.params.exerId,
        questionId: curExamQuestion.value?.questionId,
    })
    if (code !== 200) {
        return
    }

    _myExerQuestions.value[curQuestionIndex.value].wrongAnswerNum = 0
    _myExerQuestions.value[curQuestionIndex.value].userScore = null
    curExamQuestion.value!.userAnswers = []
    curExamQuestion.value!.userScore = null
}
</script>
<style scoped lang="scss">
.my-exer-paper {
    display: flex;
    width: 1200px;
    margin-top: 20px;
    flex-direction: column;
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
                    transform: translateY(-50%);
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

                .answer-sheet__title {
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
                                background: #F5F5F5;
                                border-radius: 6px 6px 6px 6px;
                                border: 1px solid #CCCCCC;
                                font-size: 12px;
                                color: #999999;
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
                margin: 20px;

                .paper__head {
                    display: flex;
                    justify-content: space-between;

                    border-bottom: 1px solid #E5E5E5;

                    .form {
                        .paper__btn {
                            padding: 0px 0px;
                            margin: 0px 10px;
                            border: 0px;
                            position: relative;

                            &:hover {
                                background-color: initial;

                                &:hover {
                                    color: #1EA1EE;
                                }
                            }

                            .paper__btn-icon {
                                font-size: 12px;
                            }

                            .paper__btn-txt {
                                padding: 0px 5px;
                                font-size: 14px;
                                color: #999999;
                                line-height: 45px;

                                &:hover {
                                    color: #1EA1EE;
                                }
                            }
                        }

                        .paper__btn--highlight {
                            font-weight: bold;
                        }

                        .paper-question__txt {
                            margin-left: 10px;
                            font-size: 14px;
                            color: #303133;
                            line-height: 45px;
                        }

                        .paper-question__input-number {
                            position: relative;
                            width: 50px;

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
                    }

                    .opt {
                        margin-right: 10px;

                        .opt__btn {
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
                                transform: translateY(-50%);
                            }

                            &:last-child::after {
                                display: none;
                            }

                            .opt__btn-icon {
                                font-size: 16px;
                            }

                            .opt__btn-txt {
                                margin-left: 2px;
                                font-size: 14px;
                                color: #999999;
                                line-height: 45px;

                            }

                            &.opt__btn--active {
                                .opt__btn-icon {
                                    color: #1EA1EE;
                                }

                                .opt__btn-txt {
                                    color: #1EA1EE;

                                    &.opt__btn-txt--wrong {
                                        color: #FF6960;
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
</style>
