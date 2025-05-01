<template>
    <div class="question-bank-list">
        <div class="question-bank-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="question-bank-list__main">
            <el-scrollbar max-height="calc(100vh - 360px)">
                <xmks-card-add name="添加题库" @click="$router.push('/question-bank/add')"></xmks-card-add>
                <xmks-card-data v-for="questionBank in listpage.list" :key="questionBank.id" :title="questionBank.name"
                    tag-name="题库" :btns="[{
                        name: '设置',
                        icon: 'icon-liebiao-01',
                        event: () => $router.push(`/question-bank/set/${questionBank.id}`)
                    }, {
                        name: '试题列表',
                        icon: 'icon-a-16ri-05',
                        event: () => $router.push(`/question-bank/question-nav/list/${questionBank.id}`)
                    }]" class="question-bank">
                    <div class="question-bank__objsub-num">
                        <span class="question-bank__pre-txt">
                            客观题：<span class="question-bank__num">{{ questionBank.objectiveNum }}</span>道
                        </span>
                        <span class="question-bank__pre-txt">
                            主观题：<span class="question-bank__num">{{ questionBank.subjectiveNum }}</span>道
                        </span>
                    </div>
                    <div class="question-bank__question-type-num">
                        <div class="question-bank__inner">
                            <span class="question-bank__num">
                                {{ questionBank.singleNum }}<span class="question-bank__unit">道</span>
                            </span>
                            <span class="question-bank__after-txt">单选题</span>
                        </div>
                        <div class="question-bank__inner">
                            <span class="question-bank__num">
                                {{ questionBank.multipleNum }}<span class="question-bank__unit">道</span>
                            </span>

                            <span class="question-bank__after-txt">多选题</span>
                        </div>
                        <div class="question-bank__inner">
                            <span class="question-bank__num">
                                {{ questionBank.fillBlankSubNum + questionBank.fillBlankObjNum }}<span
                                    class="question-bank__unit">道</span></span>

                            <span class="question-bank__after-txt">填空题</span>
                        </div>
                        <div class="question-bank__inner">
                            <span class="question-bank__num">
                                {{ questionBank.judgeNum }}<span class="question-bank__unit">道</span>
                            </span>
                            <span class="question-bank__after-txt">判断题</span>
                        </div>
                        <div class="question-bank__inner">
                            <span class="question-bank__num">{{ questionBank.qaSubNum +
                                questionBank.qaObjNum }}<span class="question-bank__unit">道</span></span>
                            <span class="question-bank__after-txt">问答题</span>
                        </div>
                    </div>
                    <div class="question-bank__other">
                        <span class="question-bank__time">{{ questionBank.updateTime }}</span>
                        <span class="question-bank__username">{{ questionBank.createUserName }}</span>
                    </div>
                </xmks-card-data>
            </el-scrollbar>
        </div>
        <div class="question-bank-list__foot">
            <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next" :hide-on-single-page="true" size="large"
                class="pagination" @size-change="query" @current-change="query" @prev-click="query"
                @next-click="query" />
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, onMounted, } from 'vue'
import type { Listpage } from '@/ts/common/listpage'
import xmksCardData from '@/components/card/xmks-card-data.vue'
import xmksCardAdd from '@/components/card/xmks-card-add.vue'
import { questionBankListpage } from '@/api/exam/question-bank'

/************************变量定义相关***********************/
const queryForm = reactive({// 查询表单
    name: '',
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
    const { data: { code, data } } = await questionBankListpage({
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
<style lang="scss" scoped>
.question-bank-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .question-bank-list__head {
        .query {

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
    }

    :deep(.question-bank-list__main) {
        flex: 1;

        .el-scrollbar__view {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px 20px;


            .question-bank {
                display: flex;
                flex-direction: column;
                height: 220px;

                .question-bank__objsub-num {
                    display: flex;
                    justify-content: center;
                    align-items: baseline;
                    margin-top: 15px;

                    .question-bank__pre-txt {
                        font-size: 12px;
                        color: #8F939C;
                        margin-right: 20px;

                        .question-bank__num {
                            font-size: 16px;
                            color: #333333;
                        }
                    }
                }

                .question-bank__question-type-num {
                    display: grid;
                    grid-template-columns: repeat(5, 1fr);
                    height: 74px;
                    justify-content: center;
                    align-items: center;
                    margin-top: 10px;
                    background: #EFF5FA;
                    border-radius: 6px 6px 6px 6px;

                    .question-bank__inner {
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-items: center;
                        position: relative;

                        &::after {
                            content: "";
                            position: absolute;
                            display: block;
                            right: 0;
                            width: 1px;
                            height: 33px;
                            background-color: #E5E5E5;
                        }

                        &:last-child {
                            &::after {
                                display: none;
                            }
                        }

                        .question-bank__num {
                            font-size: 16px;
                            color: #333333;

                            .question-bank__unit {
                                font-size: 10px;
                                color: #8F939C;
                            }
                        }


                        .question-bank__after-txt {
                            font-size: 12px;
                            color: #8F939C;
                            flex: 1 0 100%;
                            margin-left: 6px;
                            line-height: 26px;
                        }
                    }
                }

                .question-bank__other {
                    display: flex;
                    justify-content: space-between;
                    margin-top: 20px;
                    font-size: 12px;
                    color: #8F939C;
                }
            }
        }
    }

    .question-bank-list__foot {
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
