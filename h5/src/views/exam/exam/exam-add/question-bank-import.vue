<template>
    <div class="question-bank-import">
        <div class="question-bank-import__head">
            <el-button type="primary" class="question-bank-import__btn" @click="back">
                <span class="iconfont icon-zuo question-bank-import__btn-icon"></span>
                <span class="question-bank-import__btn-txt">返回</span>
            </el-button>
        </div>
        <div class="question-bank-import__main">
            <div class="question-list">
                <div class="question-list__head">
                    <el-form :model="queryForm" :inline="true" size="large" class="query">
                        <el-form-item label="">
                            <xmks-select v-model="queryForm.questionBankId" url="questionBank/listpage" :params="{}"
                                search-parm-name="name" option-value="id" option-label="name" :multiple="false"
                                search-placeholder="请输入题库名称进行筛选" placeholder="请选择题库"
                                class="paper-question__question-bank">
                                <template #default="{ option }">
                                    {{ option.name }} （单选{{ option.singleNum }} / 多选{{ option.multipleNum }} /
                                    客观填空{{ option.multipleNum }} / 判断{{ option.judgeNum }} / 客观问答{{
                                        option.qaObjNum }}）
                                </template>
                            </xmks-select>
                        </el-form-item>
                        <el-form-item label="">
                            <el-input v-model="queryForm.id" placeholder="请输入编号" />
                        </el-form-item>
                        <el-form-item label="">
                            <el-input v-model="queryForm.title" placeholder="请输入题干" />
                        </el-form-item>
                        <el-form-item label="">
                            <el-select v-model="queryForm.type" clearable placeholder="请选择类型">
                                <el-option v-for="dict in dictStore.getList('QUESTION_TYPE')" :key="dict.dictKey"
                                    :label="dict.dictValue" :value="dict.dictKey" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="">
                            <el-select v-model="queryForm.markType" clearable placeholder="请选择阅卷类型">
                                <el-option v-for="dict in dictStore.getList('PAPER_MARK_TYPE')" :key="dict.dictKey"
                                    :label="dict.dictValue" :value="dict.dictKey" />
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                        </el-form-item>
                    </el-form>
                    <div class="opt">
                        <el-radio-group v-model="display" size="large" class="opt__radio">
                            <el-radio-button value="list">
                                <span class="iconfont icon-a-16ri-05 opt__radio-icon"></span>
                            </el-radio-button>
                            <el-radio-button value="paper">
                                <span class="iconfont icon-a-16ri-04 opt__radio-icon"></span>
                            </el-radio-button>
                            <el-radio-button value="paper-with-detail">
                                <span class="iconfont icon-a-16ri-03 opt__radio-icon"></span>
                            </el-radio-button>
                        </el-radio-group>
                    </div>
                </div>
                <div class="question-list__main">
                    <el-scrollbar height="calc(100vh - 475px)">
                        <el-empty v-if="listpage.list.length === 0" description="暂无数据" class="question-list__empty" />
                        <xmks-question v-else v-for="question in listpage.list" :key="question.id" :type="question.type"
                            :title="question.title" :img-ids="question.imgFileIds" :video-id="question.videoFileId"
                            :options="question.options" :answers="question.answers" :markType="question.markType"
                            :score="question.score" :scores="question.scores" :analysis="question.analysis"
                            :userAnswers="[]" :userScore="0" :answerShow="display === 'paper-with-detail'"
                            :userAnswerShow="false" :analysisShow="display === 'paper-with-detail'"
                            :updateUserName="question.updateUserName" :display="display === 'list' ? 'list' : 'paper'"
                            :editable="false" :btns="[{
                                name: '导入',
                                icon: 'icon-daoru',
                                event: () => questionAdd(question)
                            }]">
                            <template #title-pre>{{ question.id }}、</template>
                        </xmks-question>
                    </el-scrollbar>
                </div>
                <div class="question-list__foot">
                    <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                        :total="listpage.total" background layout="prev, pager, next" :hide-on-single-page="false"
                        size="large" class="pagination" @size-change="query" @current-change="query" @prev-click="query"
                        @next-click="query" />
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { useExamStore } from '@/stores/exam'
import { useDictStore } from '@/stores/dict'
import type { Listpage } from '@/ts/common/listpage'
import { questionListpage } from '@/api/exam/question'
import type { Question } from '@/ts/exam/question'
import XmksQuestion from '@/components/question/xmks-question.vue'
import XmksSelect from '@/components/xmks-select.vue'

/************************变量定义相关***********************/
const emit = defineEmits(['back'])

const form = useExamStore() // 考试表单
const dictStore = useDictStore()// 字典缓存
const display = ref('list') // 显示样式
const queryForm = reactive({// 查询表单
    questionBankId: '',
    id: '',
    title: '',
    type: null,
    markType: null,
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 5,
    total: 0,
    list: [],
})

/************************组件生命周期相关*********************/
onMounted(() => {
    query()
})

/************************事件相关*****************************/
// 查询
async function query() {
    const exQuestionIds = [] as number[]
    form.examQuestions.forEach(examQuestion => {
        if (examQuestion.type === 2 && examQuestion.questionId) {// 在线导入没有ID
            exQuestionIds.push(examQuestion.questionId)
        }
    })

    const { data: { code, data } } = await questionListpage({
        ...queryForm,
        exIds: exQuestionIds.join(),
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    listpage.list = data.list
    listpage.total = data.total
}

// 试题添加
function questionAdd(question: Question) {
    // 选中导入试题页面
    form.examQuestions.push({
        type: 2,
        questionId: question.id,
        questionType: question.type,
        markType: question.markType,
        title: question.title,
        imgFileIds: question.imgFileIds,
        videoFileId: question.videoFileId,
        score: question.score,
        answers: question.answers,
        scores: question.scores,
        options: question.options,
        markOptions: question.markOptions,
    })

    // 已导入的试题，则不在题库再次显示
    listpage.list = listpage.list.filter((cur: Question) => cur != question)
    if (!listpage.list.length) {
        query()
    }
    form.questionNoUpdate()
}

// 返回
function back() {
    emit('back')
}

</script>
<style scoped lang="scss">
.question-bank-import {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: #FFFFFF;
    border-radius: 15px;

    .question-bank-import__head {
        display: flex;
        justify-content: space-between;
        padding-bottom: 10px;
        border-bottom: 1px solid #E5E5E5;

        .question-bank-import__btn {
            height: 38px;
            padding: 8px 30px;
            border: 0px;
            color: #FFFFFF;
            border-radius: 6px 6px 6px 6px;
            background-image: linear-gradient(to right, #04C7F2, #259FF8);

            .question-bank-import__btn-icon {
                font-size: 14px;
            }

            .question-bank-import__btn-txt {
                margin-left: 2px;
            }
        }
    }

    .question-bank-import__main {
        flex: 1;

        .question-list {
            display: flex;
            flex-direction: column;
            margin: 20px 0px;

            .question-list__head {
                display: flex;
                justify-content: space-between;

                .query {
                    .el-form-item {
                        width: 150px;
                        margin-right: 10px;
                    }

                    .query__btn {
                        height: 40px;
                        padding: 0px 30px;
                        border-radius: 6px;
                        border: 0px;
                        color: #FFFFFF;
                        font-size: 16px;
                        background-image: linear-gradient(to right, #04C7F2, #259FF8);
                    }
                }

                .opt {
                    flex: 1;
                    display: flex;
                    justify-content: flex-end;
                    align-items: flex-start;

                    :deep(.opt__radio) {
                        margin-left: 12px;

                        .el-radio-button {
                            &.is-active {
                                .el-radio-button__inner {
                                    background: #1EA1EE;
                                }
                            }

                            .el-radio-button__inner {
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                width: 38px;
                                height: 38px;
                                padding: 0px;

                                .opt__radio-icon {
                                    font-size: 18px;
                                    color: #E5E5E5;
                                }
                            }

                        }
                    }
                }
            }

            :deep(.question-list__main) {
                background-color: #FFFFFF;
                border-radius: 15px 15px 15px 15px;

                .el-scrollbar__view {
                    height: 100%;

                    .question-list__empty {
                        height: 100%;
                    }
                }
            }

            .question-list__foot {
                display: flex;
                justify-content: center;

                :deep(.pagination) {
                    margin-top: 10px;

                    .btn-prev,
                    .btn-next {
                        width: 38px;
                        height: 38px;

                        .el-icon {
                            font-size: 20px;
                        }
                    }

                    .el-pager {
                        li {
                            width: 38px;
                            height: 38px;
                            border-radius: 4px 4px 4px 4px;
                            font-size: 16px;

                            &.is-active {
                                background-image: linear-gradient(to right, #04C7F2, #259FF8);
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>
