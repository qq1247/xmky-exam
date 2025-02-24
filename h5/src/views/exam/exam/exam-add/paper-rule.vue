<template>
    <div class="paper-rule">
        <div class="paper-rule__main">
            <div class="paper-toolbar">
                <el-button type='' class="paper-toolbar__btn "
                    :class="{ 'paper-toolbar__btn--active': toolbars.questionSort }"
                    @click="toolbars.questionSort = !toolbars.questionSort">
                    <span class="iconfont icon-a-icon_huaban1 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.questionSort ? '完成排序' : '规则排序' }}</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn " @click="chapterAdd">
                    <span class="iconfont icon-icon-01 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">添加章节</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn "
                    :class="{ 'paper-toolbar__btn--active': toolbars.chapterEditShow }"
                    @click="toolbars.chapterEditShow = !toolbars.chapterEditShow">
                    <span class="iconfont icon-bianjibanli-93 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.chapterEditShow ? '查看章节' : '修改章节' }}</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn " @click="ruleAdd">
                    <span class="iconfont icon-icon-01 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">添加规则</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn " @click="paperReset">
                    <span class="iconfont icon-icon-02 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">清空规则</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn "
                    :class="{ 'paper-toolbar__btn--active': toolbars.paperNameEditShow }"
                    @click="toolbars.paperNameEditShow = !toolbars.paperNameEditShow">
                    <span class="iconfont icon-bianjibanli-93 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.paperNameEditShow ? '查看试卷名称' : '修改试卷名称' }}</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn "
                    :class="{ 'paper-toolbar__btn--active': toolbars.totalScoreShow }"
                    @click="toolbars.totalScoreShow = !toolbars.totalScoreShow">
                    <span class="iconfont icon-icon-07 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.totalScoreShow ? '隐藏总分' : '显示总分' }}</span>
                </el-button>
                <el-button type='' class="paper-toolbar__btn"
                    :class="{ 'paper-toolbar__btn--active': toolbars.markOptionShow }"
                    @click="toolbars.markOptionShow = !toolbars.markOptionShow">
                    <span class="iconfont icon-icon-05 paper-toolbar__btn-icon"></span>
                    <span class="paper-toolbar__btn-txt">{{ toolbars.markOptionShow ? '隐藏删除' : '显示删除'
                    }}</span>
                </el-button>
            </div>
            <div class="paper__wrap">
                <div class="answer-sheet">
                    <el-divider class="answer-sheet_title">
                        答题卡
                    </el-divider>
                    <el-scrollbar height="calc(100vh - 400px)" class="answer-sheet__wrap">
                        <div class="answer-sheet">
                            <template v-if="toolbars.questionSort">
                                <vue-draggable v-model="form.examRules" @end="form.questionNoUpdate()">
                                    <div v-for="(examRule, index) in form.examRules" :key="index"
                                        class="answer-sheet__sort">
                                        {{ examRule.type === 1
                                            ? examRule.chapterName
                                            : `规则${examRule.no}` }}
                                    </div>
                                </vue-draggable>
                            </template>
                            <template v-else>
                                <template v-for="(examRule, index) in form.examRules" :key="index">
                                    <div v-if="examRule.type === 1" @click="scrollView(index)"
                                        class="answer-sheet__chapter">
                                        {{ examRule.chapterName }}
                                    </div>
                                    <div v-else @click="scrollView(index)" class="answer-sheet__rule">
                                        {{ examRule.no }}
                                    </div>
                                </template>
                            </template>
                        </div>
                    </el-scrollbar>
                </div>
                <el-scrollbar height="calc(100vh - 400px)" class="paper">
                    <div v-if="toolbars.totalScoreShow" class="total-score">
                        <div class="total-score__inner">
                            <span class="total-score__txt">{{ form.totalScore }}</span>
                            <span class="total-score__desc">分</span>
                        </div>
                        <span class="iconfont icon-defen total-score__icon"></span>
                    </div>
                    <el-form ref="formRef" :model="form" :rules="formRules">
                        <el-form-item v-if="toolbars.paperNameEditShow" prop="paperName" :rules="formRules.paperName">
                            <el-input v-model="form.paperName" :maxlength="16" placeholder="请输入试卷名称"
                                class="paper__name--edit" />
                        </el-form-item>
                        <div v-else class="paper__name--view">
                            {{ form.paperName }}
                        </div>
                        <template v-for="(examRule, index) in form.examRules" :key="index">
                            <div v-if="examRule.type === 1" :id="`q${index}`" class="paper-chapter">
                                <el-form-item v-if="toolbars.chapterEditShow" :prop="`examRules[${index}].chapterName`"
                                    :rules="formRules.chapterName">
                                    <el-input v-model="examRule.chapterName" maxlength="14" placeholder="请输入章节名称"
                                        class="paper-chapter__name--edit" />
                                </el-form-item>
                                <el-form-item v-if="toolbars.chapterEditShow" :prop="`examRules[${index}].chapterTxt`"
                                    :rules="formRules.chapterTxt">
                                    <el-input v-model="examRule.chapterTxt" type="textarea" maxlength="256"
                                        :autosize="{ minRows: 2 }" resize="none" placeholder="请输入章节描述"
                                        class="paper-chapter__desc--edit" />
                                </el-form-item>
                                <div v-if="!toolbars.chapterEditShow" class="paper-chapter__wrap">
                                    <span class="paper-chapter__name--view">
                                        {{ examRule.chapterName }}
                                    </span>
                                    <el-button v-if="toolbars.markOptionShow" type="" class="paper-chapter__btn"
                                        @click="chapterDel(examRule)">
                                        <span class="iconfont icon-shanchu paper-chapter__btn-icon"></span>
                                        <span class="paper-chapter__btn-txt">删除</span>
                                    </el-button>
                                </div>
                                <span v-if="!toolbars.chapterEditShow && examRule.chapterTxt"
                                    class="paper-chapter__desc--view">
                                    {{ examRule.chapterTxt }}
                                </span>
                            </div>
                            <div v-else :id="`q${index}`" class="paper-question">
                                <!-- 题库 -->
                                <el-form-item :prop="`examRules[${index}].questionBankId`"
                                    :rules="formRules.questionBankId">
                                    <xmks-select v-model="examRule.questionBankId" url="questionBank/listpage"
                                        :params="{}" search-parm-name="name" option-value="id" option-label="name"
                                        :multiple="false" search-placeholder="请输入题库名称进行筛选" :options="questionBanks"
                                        placeholder="请选择题库" class="paper-question__question-bank">
                                        <template #default="{ option }">
                                            {{ option.name }} （单选{{ option.singleNum }} / 多选{{ option.multipleNum }} /
                                            客观填空{{ option.multipleNum }} / 判断{{ option.judgeNum }} / 客观问答{{
                                                option.qaObjNum }}）
                                        </template>
                                    </xmks-select>
                                </el-form-item>
                                <!-- 试题类型 -->
                                <el-form-item :prop="`examRules[${index}].questionType`"
                                    :rules="formRules.questionType">
                                    <el-select v-model="examRule.questionType" clearable placeholder="请选择试题类型"
                                        class="paper-question__question-type" @change="() => {
                                            if (examRule.questionType === 2) {// 如果是多选，设置漏选分数为分数一半
                                                if (examRule.scores && examRule.score) {
                                                    examRule.scores = examRule.score / 2
                                                }
                                            } else {// 否则清空子分数
                                                examRule.scores = undefined
                                            }
                                            examRule.markOptions = [] // 清空阅卷选项
                                        }">
                                        <el-option v-for="dict in dictStore.getList('QUESTION_TYPE')"
                                            :key="dict.dictKey" :label="dict.dictValue"
                                            :value="parseInt(dict.dictKey)" />
                                    </el-select>
                                </el-form-item>
                                <!-- 阅卷类型 -->
                                <el-form-item v-if="examRule.questionType === 3 || examRule.questionType === 5"
                                    :prop="`examRules[${index}].markType`" :rules="formRules.markType" :disabled="true"
                                    style="width: 100px;">
                                    <el-select v-model="examRule.markType" clearable placeholder="阅卷类型" disabled>
                                        <el-option v-for="dict in dictStore.getList('PAPER_MARK_TYPE')"
                                            :key="dict.dictKey" :label="dict.dictValue"
                                            :value="parseInt(dict.dictKey)" />
                                    </el-select>
                                </el-form-item>
                                <!-- 阅卷选项（智能填空或智能问答时显示） -->
                                <el-form-item
                                    v-if="examRule.markType === 1 && (examRule.questionType === 3 || examRule.questionType === 5)"
                                    :prop="`examRules[${index}].markOptions`" :rules="formRules.markOptions"
                                    style="width: 100px;">
                                    <el-select v-model="examRule.markOptions" clearable placeholder="阅卷选项" multiple
                                        collapse-tags>
                                        <template v-for="dict in dictStore.getList('QUESTION_MARK_OPTIONS')"
                                            :key="dict.dictKey">
                                            <el-option
                                                v-if="dict.dictKey === '3' || (dict.dictKey === '2' && examRule.questionType === 3)"
                                                :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                                        </template>
                                    </el-select>
                                </el-form-item>
                                <!-- 题数 -->
                                <el-form-item :prop="`examRules[${index}].num`" :rules="formRules.num">
                                    <span class="paper-question__txt">添加</span>
                                    <el-input-number v-model="examRule.num" :min="1" :max="100" :step="10"
                                        :precision="0" controls-position="right" class="paper-question__input-number" />
                                    <span class="paper-question__txt">题，</span>
                                </el-form-item>
                                <!-- 分数 -->
                                <el-form-item :prop="`examRules[${index}].score`" :rules="formRules.score">
                                    <span class="paper-question__txt">每题</span>
                                    <el-input-number v-model="examRule.score" :min="0.5" :max="20" :step="0.5"
                                        :precision="1" controls-position="right" class="paper-question__input-number"
                                        @change="() => {// 如果是多选，修改时默认漏选分数为分数的一半
                                            if (examRule.questionType === 2) {
                                                if (examRule.scores && examRule.score) {
                                                    examRule.scores = examRule.score / 2
                                                }
                                            }
                                        }" />
                                    <span class="paper-question__txt">分</span>
                                </el-form-item>
                                <!-- 漏选分数 -->
                                <el-form-item v-if="examRule.questionType === 2" :prop="`examRules[${index}]`"
                                    :rules="formRules.scores">
                                    <span class="paper-question__txt"> ，漏选</span>
                                    <el-input-number v-model="examRule.scores" :min="0" :max="20" :step="0.5"
                                        :precision="1" controls-position="right" class="paper-question__input-number"
                                        @change="() => {// 如果漏选分数大于等于分数，漏选分数恢复成分数一半
                                            if (examRule.questionType === 2) {
                                                if (examRule.scores && examRule.score) {
                                                    if (examRule.scores >= examRule.score) {
                                                        examRule.scores = examRule.score / 2
                                                    }
                                                }
                                            }
                                        }" />
                                    分
                                </el-form-item>
                                <el-form-item>
                                    <el-button v-if="toolbars.markOptionShow" type="" class="paper-question__btn"
                                        @click="ruleDel(examRule as ExamRule)">
                                        <span class="iconfont icon-shanchu paper-question__btn-icon"></span>
                                        <span class="paper-question__btn-txt">删除</span>
                                    </el-button>
                                </el-form-item>
                            </div>
                        </template>
                    </el-form>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref, } from 'vue'
import { useDictStore } from '@/stores/dict'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import XmksSelect from '@/components/xmks-select.vue'
import { useExamStore } from '@/stores/exam'
import type { ExamRule } from '@/ts/exam/exam'
import { VueDraggable } from 'vue-draggable-plus'
import { questionBankListpage } from '@/api/exam/question-bank'

// 定义变量
defineExpose({ next })

const dictStore = useDictStore() // 字典储存
const form = useExamStore() // 表单
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    chapterName: [{ required: true, message: '请输入章节名称', trigger: 'change' }],
    questionBankId: [{ required: true, message: '请选择题库', trigger: 'change' }],
    questionType: [{ required: true, message: '请选择试题类型', trigger: 'change' }],
    markType: [{ required: true, message: '请选择阅卷类型', trigger: 'change' }],
    markOptions: [{ required: false, message: '请选择阅卷选项', trigger: 'change' }],
    num: [{
        trigger: 'change',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入题数'))
            }
            if (!/^[0-9]*$/.test(value)) {
                return callback(new Error('请输入正整数'))
            }
            if (value > 100) {
                return callback(new Error('最大100题'))
            }
            return callback()
        }
    }],
    score: [{
        trigger: 'change',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
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
    scores: [{
        trigger: 'change',
        validator: (rule: any, value: ExamRule, callback: any) => {
            if (value.scores == null) {
                return callback(new Error('请输入分数'))
            }
            if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value.scores.toString())) {
                return callback(new Error('正数，最多两位小数'))
            }
            if (value.score && value.score <= value.scores) {
                return callback(new Error('不能大于分数'))
            }
            if (value.scores > 20) {
                return callback(new Error('最大20分'))
            }
            return callback()
        }
    }],
})

const toolbars = reactive({// 工具条
    analysisShow: false,
    markOptionShow: false,
    questionSort: false,
    chapterEditShow: false,
    paperNameEditShow: false,
    totalScoreShow: false,
})

const questionBanks = ref([]) // 题库列表


// 组件挂载完成后，执行如下方法
onMounted(async () => {
    let curPage = 1
    while (true) {
        const { data: { data } } = await questionBankListpage({
            curPage: curPage++,
            pageSize: 100,
        })

        questionBanks.value.push(...data.list as never[])
        if (questionBanks.value.length >= data.total) {// 分批次取出
            break
        }
    }
})

// 章节添加
function chapterAdd() {
    form.examRules.unshift({
        no: 0,
        type: 1,
        chapterName: '第一章节',
        chapterTxt: '在此输入章节描述。。。',
    })
}

// 章节删除
function chapterDel(examRule: ExamRule) {
    ruleDel(examRule)
}

// 规则添加
function ruleAdd() {
    form.examRules.push({
        no: 0,
        type: 2,
        questionBankId: undefined,
        questionType: 1,
        markType: 1,
        markOptions: [],
        num: 10,
        score: 1,
        scores: undefined,
    })
    form.questionNoUpdate()
}

// 规则删除
function ruleDel(examRule: ExamRule) {
    form.examRules = form.examRules.filter(cur => examRule != cur)
    form.questionNoUpdate()
}

// 试卷重置
function paperReset() {
    form.examRules = []
}

// 滚动预览
function scrollView(index: number) {
    (document.querySelector(`#q${index}`) as HTMLElement).scrollIntoView(true)
};

// 下一步
async function next() {
    if ((form.subjectiveQuestionNum + form.objectiveQuestionNum) === 0) {
        ElMessage.error(`最少添加一条规则`)
        return false
    }

    try {
        await formRef.value?.validate()
        return true
    } catch (e) {
        ElMessage.error(`部分信息填写错误，请检查`)
        return false
    }
}
</script>
<style scoped lang="scss">
.paper-rule {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .paper-rule__main {
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

                            .answer-sheet__rule {
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                width: 28px;
                                height: 28px;
                                margin: 5px 5px 5px 0px;
                                background: #F5F5F5;
                                border-radius: 6px 6px 6px 6px;
                                border: 1px solid #CCCCCC;
                                font-size: 12px;
                                color: #999999;
                                cursor: pointer;
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

                    .paper__name--edit {
                        .el-input__wrapper {
                            // box-shadow: initial;

                            .el-input__inner {
                                text-align: center;
                                font-size: 24px;
                                color: #303133;
                                line-height: 45px;
                            }
                        }
                    }

                    .paper__name--view {
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
                        padding: 0px 0px 10px 0px;
                        border-bottom: 1px dashed #E5E5E5;

                        .paper-chapter__name--edit {
                            margin-top: 20px;

                            .el-input__wrapper {
                                // box-shadow: initial;

                                .el-input__inner {
                                    font-size: 16px;
                                    color: #303133;
                                    line-height: 45px;
                                }
                            }
                        }

                        .paper-chapter__desc--edit {
                            .el-textarea__inner {
                                height: 40px;
                                // box-shadow: initial;
                                font-size: 14px;
                                color: #999999;
                            }
                        }

                        .paper-chapter__wrap {
                            .paper-chapter__name--view {
                                margin-top: 10px;
                                font-size: 16px;
                                font-size: 16px;
                                color: #303133;
                                line-height: 30px;
                            }

                            .paper-chapter__btn {
                                padding: 0px 0px;
                                margin: 0px 10px;
                                border: 0px;
                                position: relative;

                                &:hover {
                                    background-color: initial;
                                    color: #FE7068;

                                    .paper-chapter__btn-txt {
                                        color: #FE7068;
                                    }
                                }

                                .paper-chapter__btn-icon {
                                    font-size: 14px;
                                }

                                .paper-chapter__btn-txt {
                                    margin-left: 2px;
                                    font-size: 14px;
                                    color: #999999;
                                    line-height: 45px;
                                }
                            }
                        }

                        .paper-chapter__desc--view {
                            margin-top: 5px;
                            font-size: 14px;
                            color: #999999;
                            line-height: 22px;
                        }


                    }

                    .paper-question {
                        display: flex;
                        flex-direction: row;
                        margin-top: 20px;
                        border-bottom: 1px dashed #E5E5E5;

                        .paper-question__question-bank {
                            .el-select__wrapper {
                                width: 260px;
                                height: 38px;
                            }
                        }

                        .paper-question__question-type {
                            .el-select__wrapper {
                                margin-left: 10px;
                                width: 170px;
                                height: 38px;
                            }
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

                        .paper-question__btn {
                            padding: 0px 0px;
                            margin: 0px 10px;
                            border: 0px;
                            position: relative;

                            &:hover {
                                color: #FE7068;

                                .paper-question__btn-txt {
                                    color: #FE7068;
                                }
                            }

                            .paper-question__btn-icon {
                                font-size: 14px;
                            }

                            .paper-question__btn-txt {
                                margin-left: 2px;
                                font-size: 14px;
                                color: #999999;
                                line-height: 45px;
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

                            .total-score__txt {
                                font-weight: bold;
                                font-size: 36px;
                                color: #FF0000;
                                line-height: 45px;
                            }

                            .total-score__desc {
                                color: #FF0000;
                                font-size: 16px;
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
