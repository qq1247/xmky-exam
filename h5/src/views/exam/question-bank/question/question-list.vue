<template>
    <div class="question-list">
        <div class="question-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="" style="width: 150px;">
                    <el-input v-model="queryForm.id" placeholder="请输入编号" />
                </el-form-item>
                <el-form-item label="" style="width: 150px;">
                    <el-input v-model="queryForm.title" placeholder="请输入题干" />
                </el-form-item>
                <el-form-item label="" style="width: 150px;">
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
                <el-button type="success" class="opt__btn"
                    @click="$router.push(`/question-bank/question-nav/add/${queryForm.questionBankId}`)">
                    <span class="iconfont icon-tubiaoziti2-02 opt__btn-icon"></span>
                    <span class="opt__btn-txt">添加</span>
                </el-button>
                <el-button type="success" class="opt__btn"
                    @click="$router.push(`/question-bank/question-nav/import/${queryForm.questionBankId}`)">
                    <span class="iconfont icon-daoru opt__btn-icon"></span>
                    <span class="opt__btn-txt">在线导入</span>
                </el-button>
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
            <el-scrollbar height="calc(100vh - 275px)">
                <el-empty v-if="listpage.list.length === 0" description="暂无数据" class="question-list__empty" />
                <xmks-question v-else v-for="question in listpage.list" :key="question.id" :type="question.type"
                    :title="question.title" :options="question.options" :answers="question.answers"
                    :markType="question.markType" :score="question.score" :scores="question.scores"
                    :analysis="question.analysis" :userAnswers="[]" :userScore="0"
                    :answerShow="display === 'paper-with-detail'" :userAnswerShow="false"
                    :analysisShow="display === 'paper-with-detail'" :updateUserName="question.updateUserName"
                    :display="display === 'list' ? 'list' : 'paper'" :editable="false" :btns="[{
                        name: '设置',
                        icon: 'icon-liebiao-01',
                        event: () => $router.push(`/question-bank/question-nav/set/${question.id}`)
                    }]">
                    <template #title-pre>{{ question.id }}、</template>
                </xmks-question>
            </el-scrollbar>
        </div>
        <div class="question-list__foot">
            <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next" :hide-on-single-page="false" size="large"
                class="pagination" @size-change="query" @current-change="query" @prev-click="query"
                @next-click="query" />
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, onMounted, ref, } from 'vue'
import type { Listpage } from '@/ts/common/listpage'
import { questionListpage } from '@/api/exam/question'
import { useDictStore } from '@/stores/dict'
import { useRoute } from 'vue-router'
import XmksQuestion from '@/components/question/xmks-question.vue'

/************************变量定义相关***********************/
const route = useRoute()
const dictStore = useDictStore()// 字典缓存
const display = ref('list') // 显示样式
const queryForm = reactive({// 查询表单
    questionBankId: 0,
    id: '',
    title: '',
    type: null,
    markType: null,
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 8,
    total: 0,
    list: [],
})

/************************组件生命周期相关*********************/
onMounted(() => {
    queryForm.questionBankId = parseInt(route.params.questionBankId as string)

    query()
})

/************************事件相关*****************************/
// 查询
async function query() {
    const { data: { code, data } } = await questionListpage({
        ...queryForm,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    listpage.list = data.list
    listpage.total = data.total
}

</script>
<style></style>
<style lang="scss" scoped>
.question-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
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

            .opt__btn {
                height: 40px;
                padding: 0px 20px;
                border-radius: 6px;
                border: 0px;
                background-image: linear-gradient(to right, #04C7F2, #259FF8);

                .opt__btn-icon {
                    color: #FFFFFF;
                    font-size: 16px;
                    margin-right: 4px;
                }

                .opt__btn-txt {
                    color: #FFFFFF;
                    font-size: 14px;
                }
            }

            .opt__btn--secondary {
                padding: 0px 25px;
                border: 1px solid #04C7F2;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
                margin-left: 10px;

                .opt__btn-icon {
                    color: #04C7F2;
                }

                .opt__btn-txt {
                    color: #04C7F2;
                }
            }

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
                        border: 0px;
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
        padding: 30px;

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
                background-color: #FFFFFF;
                border-radius: 4px 4px 4px 4px;

                .el-icon {
                    font-size: 20px;
                }
            }

            .number {
                background-color: #FFFFFF;
                border-radius: 4px 4px 4px 4px;
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
</style>
