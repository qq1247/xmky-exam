<template>
    <div v-if="paperShow === 'paper'" class="paper">
        <!-- 答题卡 -->
        <el-card shadow="never" class="paper-left">
            <el-divider>
                答题卡 - 拖动排序
            </el-divider>
            <draggable v-model="form.examQuestions" item-key="no" @end="form.noUpdate()">
                <template #item="{element}">
                    <div v-if="element.type === 1" class="paper-left-chapter">{{ element.chapterName }}</div>
                    <el-button v-else type="primary" plain>{{element.no}}</el-button>
                </template>
            </draggable>
        </el-card>
        <!-- 试卷 -->
        <el-scrollbar height="calc(100vh - 200px)" class="paper-right">
            <el-card shadow="never" >
                <el-form ref="formRef" :model="form" :rules="formRules">
                    <!-- 工具条 -->
                    <div class="paper-right-toolbar">
                        <el-button type="success" size="small" round @click="questionType.queryForm.show = true; questionQuery();">
                            <span class="iconfont icon-list-row" style="font-size: 13px;"></span>&nbsp;题库选择
                        </el-button>
                        <el-button type="success" size="small" round @click="paperShow = 'editor'">
                            <span class="iconfont icon-edit" style="font-size: 13px;"></span>&nbsp;文本导入
                        </el-button>
                        <el-button type="success" size="small" round @click="chapterAdd()">
                            <span class="iconfont icon-plus" style="font-size: 14px;"></span>&nbsp;章节添加
                        </el-button>
                        <el-button type="success" size="small" round @click="paperReset()">
                            <span class="iconfont icon-clear" style="font-size: 14px;"></span>&nbsp;试卷重置
                        </el-button>
                        <el-button type="success" size="small" round @click="userAnswerShow = !userAnswerShow">
                            <span class="iconfont icon-preview" style="font-size: 17px;"></span>&nbsp;{{ userAnswerShow ? '答案隐藏' : '答案显示' }}
                        </el-button>
                    </div>
                    <!-- 右上角显示打分 -->
                    <div class="paper-right-score">
                        <span class="paper-right-score-value">{{form.totalScore}}</span>
                        <span class="iconfont icon-fenshudixian"></span>
                    </div>
                    <!-- 试卷名称
                    <div class="paper-right-title">
                        <el-input v-model="form.paperName" :maxlength="16" placeholder="请输入试卷名称" />
                    </div> -->
                    <!-- 试题列表 -->
                    <template v-for="(examQuestion, index) in form.examQuestions" :key="index">
                        <div v-if="examQuestion.type === 1" class="paper-right-chapter">
                            <el-form-item :prop="`examQuestions[${index}].chapterName`" :rules="formRules.chapterName">
                                <el-input v-model="examQuestion.chapterName" maxlength="14" placeholder="请输入章节名称" />
                            </el-form-item>
                            <el-input v-model="examQuestion.chapterTxt" type="textarea" maxlength="128" 
                                :autosize="{ minRows: 1 }" resize="none" placeholder="请输入章节描述" />
                            <el-button type="danger" @click="chapterDel(examQuestion)" size="small" circle>
                                <span class="iconfont icon-close" style="font-size:12px;font-weight: bold;"></span>
                            </el-button>
                        </div>
                        <Question 
                            v-else
                            :no="examQuestion.no" 
                            :type="examQuestion.questionType || 1"
                            :markType="examQuestion.markType || 1" 
                            :title="examQuestion.title || ''" 
                            :score="examQuestion.score || 1" 
                            :answers="examQuestion.answers" 
                            :options="examQuestion.options" 
                            :userAnswerShow="userAnswerShow"
                            >
                            <!-- 单题属性设置 -->
                            <template #bottom-right>
                                <template v-if="examQuestion.questionType === 1 || examQuestion.questionType === 2 
                                    || examQuestion.questionType === 4 || (examQuestion.questionType === 5 && examQuestion.markType === 2)">
                                    本题<el-input-number 
                                        v-model="examQuestion.score" 
                                        :min="0.5" 
                                        :max="20" 
                                        :precision="2"
                                        controls-position="right"
                                        @blur="() => scoreUpdate(examQuestion)"/>分<!-- 用blur事件，输入字母或删除数字不触发change事件 -->
                                </template>
                                <template v-if="examQuestion.questionType === 2">
                                    ，漏选<el-input-number 
                                            v-if="examQuestion.scores" 
                                            v-model="examQuestion.scores[0]" 
                                            :min="0" 
                                            :max="20" 
                                            size="small"
                                            :precision="2"
                                            @blur="() => scoresUpdate(examQuestion)"
                                            ></el-input-number>分
                                </template>
                                <template v-if="examQuestion.questionType === 3 || (examQuestion.questionType === 5 && examQuestion.markType === 1)">
                                    <template v-for="(score, index1) of examQuestion.scores" :key="index1">
                                        {{ index1 > 0 ? "，" : "" }}
                                        第{{index1 + 1}}{{examQuestion.questionType === 3 ? '空' : '关键词'}}
                                        <el-input-number 
                                            v-if="examQuestion.scores" 
                                            v-model="examQuestion.scores[index1]" 
                                            :min="0.5" 
                                            :max="10" 
                                            size="small"
                                            :precision="1"
                                            @blur="() => scoresUpdate(examQuestion)"
                                            ></el-input-number>分
                                    </template>
                                    <el-checkbox-group v-model="examQuestion.markOptions" style="width:300px">
                                        <el-tooltip v-if="examQuestion.markType === 1 && examQuestion.questionType === 3" content="默认答案有顺序">
                                            <el-checkbox :label="2" class="checkbox">答案无顺序</el-checkbox>
                                        </el-tooltip>
                                        <el-tooltip v-if="examQuestion.markType === 1" content="默认字母大小写敏感">
                                            <el-checkbox :label="3" class="checkbox">不分大小写</el-checkbox>
                                        </el-tooltip>
                                    </el-checkbox-group>
                                </template>
                                <el-button type="danger" @click="questionDel(examQuestion)" size="small" circle>
                                    <span class="iconfont icon-close" style="font-size:14px;"></span>
                                </el-button>
                            </template>
                        </Question>
                    </template>
                </el-form>
            </el-card>
        </el-scrollbar>
    </div>
    <!-- 题库 -->
    <el-drawer title="题库" v-model="questionType.queryForm.show" :size="700" :with-header="false">
        <el-form :inline="true" :model="questionType.queryForm">
            <el-form-item style="width: 150px;">
                <el-input v-model="questionType.queryForm.questionTypeName" placeholder="请输入题库名称" />
            </el-form-item>
            <el-form-item label="" style="width: 150px;">
                <el-input v-model="questionType.queryForm.id" placeholder="请输入编号" />
            </el-form-item>
            <el-form-item label="" style="width: 150px;">
                <el-input v-model="questionType.queryForm.title" placeholder="请输入题干" />
            </el-form-item>
            <el-form-item label="" style="width: 150px;">
                <el-select v-model="questionType.queryForm.type" clearable placeholder="请选择类型">
                    <el-option v-for="dict in dictStore.getList('QUESTION_TYPE')" :key="dict.dictKey"
                        :label="dict.dictValue" :value="dict.dictKey" />
                </el-select>
            </el-form-item>
            <el-form-item label="" style="width: 150px;">
                <el-select v-model="questionType.queryForm.markType" clearable placeholder="请选择阅卷类型">
                    <el-option v-for="dict in dictStore.getList('PAPER_MARK_TYPE')" :key="dict.dictKey"
                        :label="dict.dictValue" :value="dict.dictKey" />
                </el-select>
            </el-form-item>
            <el-form-item style="width: 120px;">
                <el-button type="primary" @click="questionQuery">
                    <span class="iconfont icon-search" style="font-size:14px;">&nbsp;查询</span>
                </el-button>
            </el-form-item>
            <el-form-item style="width: 100px;">
                <el-radio-group v-model="questionType.display">
                    <el-radio-button label="paper">
                        <span class="iconfont icon-menu-s"></span>
                    </el-radio-button>
                    <el-radio-button label="list">
                        <span class="iconfont icon-list-row"></span>
                    </el-radio-button>
                </el-radio-group>
            </el-form-item>
        </el-form>
        <Question 
            v-for="question in questionType.listpage.list" 
            :no="question.id" 
            :type="question.type"
            :markType="question.markType" 
            :title="question.title" 
            :score="question.score" 
            :answers="question.answers"
            :options="question.options" 
            :display="questionType.display"
            :updateUserName="question.updateUserName"
            >
            <template #bottom-right>
                <el-button type="success" @click="questionAdd(question)" size="small">
                    <span class="iconfont icon-arrow-left" style="font-size:14px;">&nbsp;导入</span>
                </el-button>
            </template>
        </Question>
        <el-pagination v-model:current-page="questionType.listpage.curPage" v-model:page-size="questionType.listpage.pageSize"
            :total="questionType.listpage.total" background layout="prev, pager, next" :hide-on-single-page="true"
            @size-change="questionQuery" @current-change="questionQuery" @prev-click="questionQuery" @next-click="questionQuery" />
    </el-drawer>
    <!-- 试题编辑器 -->
    <QuestionEditor v-if="paperShow === 'editor'" @back="paperShow = 'paper'" @txt-import="txtImport"></QuestionEditor>
</template>
<script lang="ts" setup>
import http from '@/request';
import { reactive, ref, watch, type Ref } from 'vue';
import Question from '@/components/question/Question.vue';
import { useDictStore } from '@/stores/dict';
import { useExamStore } from '@/stores/exam';
import type { ExamQuestion } from '@/stores/exam';
import draggable from 'vuedraggable'
import QuestionEditor from '@/components/question/QuestionEditor.vue';
import type { FormInstance, FormRules } from 'element-plus';

// 定义变量
defineExpose({ next });
const emit = defineEmits(['paperShowUpdate'])
const props = withDefaults(defineProps<{
    show?: string // 试卷显示（paper：空白试卷；editor：试题编辑器）
}>(), {
    show: 'paper',
})
const dictStore = useDictStore() // 字典缓存
const form = useExamStore() // 表单
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    chapterName: [{ required: true, message: '请输入章节名称', trigger: 'change' }],
})
const paperShow = ref(props.show) // 试卷显示（paper：空白试卷；editor：试题编辑器）
const userAnswerShow = ref(true) // 用户答案显示
const questionType = reactive({// 题库
    display: 'list',
    listpage: {// 分页列表
        curPage: 1,
        pageSize: 5,
        total: 0,
        list: [] as any[],
    },
    queryForm: {// 题库查询表单
        questionTypeName: '',
        id: '',
        title: '',
        type: null,
        markType: null,
        show: false,
    }
})

// 监听属性
watch(paperShow, () => {
    emit('paperShowUpdate', paperShow.value)
})

// 题库查询
async function questionQuery() {
    let exQuestionIds = [] as number[]
    form.examQuestions.forEach(examQuestion => {
        if (examQuestion.type === 2 && examQuestion.questionId) {// 文本导入没有ID
            exQuestionIds.push(examQuestion.questionId)
        }
    });

    const { data: { code, data } } = await http.post('question/listpage', {
        ...questionType.queryForm,
        exIds: exQuestionIds.join(),
        curPage: questionType.listpage.curPage,
        pageSize: questionType.listpage.pageSize,
    })
    if (code !== 200) {
        return
    }

    questionType.listpage.list = data.list
    questionType.listpage.total = data.total
}

// 章节添加
function chapterAdd() {
    form.examQuestions.push({
        no: 0,
        type: 1,
        chapterName: '单选题',
        chapterTxt: '',
    })
}

// 章节删除
function chapterDel(examQuestion: ExamQuestion) {
    form.examQuestions = form.examQuestions.filter(cur => examQuestion != cur)
}

// 试题添加
function questionAdd(question: any) {
    // 选中试题导入页面
    form.examQuestions.push({
        type: 2,
        questionId: question.id,
        questionType: question.type,
        markType: question.markType,
        title: question.title,
        score: question.score,
        answers: question.answers,
        scores: question.scores,
        options: question.options,
        markOptions: question.markOptions,
    })

    // 已导入的试题，则不在题库再次显示
    questionType.listpage.list = questionType.listpage.list.filter((cur: any) => cur != question) 
    if (!questionType.listpage.list.length) {
        questionQuery()
    }
    form.noUpdate()
}

// 试题删除
function questionDel(examQuestion: ExamQuestion) {
    form.examQuestions = form.examQuestions.filter(cur => examQuestion != cur)
    form.noUpdate()
}

// 试卷重置
function paperReset() {
    form.examQuestions = []
}

// 文本导入
function txtImport(questions: any) {
    questions._value.forEach((question: any) => {
        form.examQuestions.push({
            type: 2,
            questionId: question.id,
            questionType: question.type,
            markType: question.markType,
            title: question.title,
            score: question.score,
            answers: question.answers,
            scores: question.scores,
            options: question.options,
            markOptions: question.markOptions,
        })
    })
    form.noUpdate()
    paperShow.value = 'paper'
}

// 分数更新
function scoreUpdate(examQuestion: ExamQuestion) {
    // 如果分数被删除或填了错误的值，恢复成1分
    if (!examQuestion.score) {
        examQuestion.score = 1
    }

    // 如果是多选题，漏选超出范围，恢复为分数一半
    if (examQuestion.questionType === 2) {
        if (examQuestion.scores && examQuestion.score <= examQuestion.scores[0]) {
            examQuestion.scores[0] = examQuestion.score / 2
        }
    }
}

// 子分数更新
function scoresUpdate(examQuestion: ExamQuestion) {
    // 如果子分数被删除或填了错误的值，恢复成1分
    examQuestion.scores?.forEach((score, index) => {
        if(examQuestion.scores 
            && (examQuestion.scores[index] === null || examQuestion.scores[index] === undefined)) {// 不要用!，子分数可能为0
            examQuestion.scores[index] = 1
        }
    })

    // 如果是多选题，漏选超出范围，恢复为分数一半
    if (examQuestion.questionType === 2) {
        if (examQuestion.score && examQuestion.scores 
            && examQuestion.score <= examQuestion.scores[0]) {
            examQuestion.scores[0] = examQuestion.score / 2
        }
    } 
    // 如果是填空或客观问答，分数为子分数累加
    else if (examQuestion.questionType === 3 
        || (examQuestion.questionType === 5 && examQuestion.markType === 1)) {
        if (examQuestion.scores) {
            examQuestion.score = examQuestion.scores.reduce((pre, cur) => pre + cur)
        }
    }
}

// 下一步
async function next() {
    if (!formRef.value) return false
    return await formRef.value.validate((valid, fields) => {
        return valid
    })
}

</script>

<style lang="scss" scoped>
.paper {
    flex: 1;
    display: flex;

    .paper-left {
        width: 240px;
        min-height: 300px;

        .el-button {
            height: 30px;
            width: 30px;
            padding: 0;
            border: 0;
            margin: 2px;
        }
        :deep(.el-divider__text) {
            width: 150px;
        }
        .paper-left-chapter {
            font-size: 13px;
            font-weight: bold;
            margin: 5px 0px;
            cursor: pointer;
        }
    }

    :deep(.paper-right) {
        flex: 1;
        .el-card {
            width: 800px;
            min-height: calc(100vh - 200px);
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
                    right: 100px;
                    display: none;
                }
                .el-form-item {// 校验通过时，恢复和章节描述的距离
                    margin-bottom: 0;
                    &.is-error {
                        margin-bottom: 18px;
                    }
                }
                .el-form-item__error {
                    margin-left: 12px;
                }
            }
            .question-paper {
                &:hover {
                    .question-paper-bottom-right {
                        height: 80px;
                        line-height: 40px;
                        font-size: 12px;
                        font-weight: bold;
                        color: var(--el-text-color-secondary);
                    }
                }
                .question-paper-bottom-right {
                    width: 100%;
                    height: 0px;
                    background-color: white;
                    bottom: 0px;
                    right: initial;
                    z-index: 2;// 单选框为1，不能遮盖
                    box-shadow: var(--el-box-shadow-light);
                    padding: 5px 20px;
                    left: 0px;
                    transition: height .38s ease;

                    .el-input-number {
                        width: 40px;
                        .el-input-number__decrease, .el-input-number__increase {
                            display: none;
                        }
                        .el-input__wrapper {
                            padding: 0px;
                            box-shadow: initial;
                            border-bottom: 1px solid var(--el-border-color);
                            border-radius: 0;
                        }
                    }
                    
                    .el-button {
                        float: right;
                        margin-top: 4px;
                    }
                    .el-checkbox-group {
                        display: inline-block;
                        padding-left: 30px;
                    }
                }
            }
        }
    }
}
</style>