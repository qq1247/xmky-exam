<template>
    <div class="my-exam-list">
        <div class="my-exam-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.examName" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <el-scrollbar max-height="calc(100vh - 360px)" class="my-exam-list__main">
            <xmks-card-empty v-if="listpage.total === 0" name="暂无考试" icon="icon-tubiaoziti22-22"></xmks-card-empty>
            <xmks-card-data v-else v-for="myExam in listpage.list" :key="myExam.id" :title="myExam.examName"
                tag-name="考试" class="my-exam">
                <div class="my-exam__exam-time">
                    <span>
                        {{ myExam.examStartTime }} - {{ myExam.examEndTime }}
                    </span>
                </div>
                <div class="my-exam__outer">
                    <!-- <div class="my-exam__inner">
                        <span class="my-exam__num">
                            <span class="my-exam__unit">客观试卷</span>
                        </span>
                        <span class="my-exam__after-txt">试卷</span>
                    </div> -->
                    <div class="my-exam__inner">
                        <span class="my-exam__num">
                            {{ myExam.examLimitMinute || '无' }}<span v-if="myExam.examLimitMinute"
                                class="my-exam__unit">分钟</span>
                        </span>
                        <span class="my-exam__after-txt">限时答题</span>
                    </div>
                    <div class="my-exam__inner">
                        <span class="my-exam__num">
                            {{ myExam.totalScore || '-' }}<span class="my-exam__unit">/{{ myExam.examTotalScore
                                }}</span>
                        </span>
                        <span class="my-exam__after-txt">我的分数</span>
                    </div>
                    <div class="my-exam__inner">
                        <span class="my-exam__num">
                            {{ myExam.no || '-' }}<span class="my-exam__unit">/{{ myExam.userNum || '-' }}</span>
                        </span>
                        <span class="my-exam__after-txt">我的排名</span>
                    </div>
                    <div class="my-exam__inner">
                        <span class="my-exam__num">
                            {{ diff(myExam.answerStartTime, myExam.answerEndTime) }}<span
                                class="my-exam__unit">分钟</span>
                        </span>
                        <span class="my-exam__after-txt">答题用时</span>
                    </div>
                </div>
                <div class="my-exam__other">
                    <xmks-count-down v-if="myExam.state === 1 || myExam.state === 2"
                        :expireTime="myExam.state === 1 ? myExam.examStartTime : myExam.answerEndTime"
                        :preTxt="myExam.state === 1 ? '距离考试开始：' : '距离考试结束：'" class="my-exam__time">
                    </xmks-count-down>
                    <template v-else>
                        <div>
                            <span class="my-exam__tag my-exam__tag--state">
                                {{ dictStore.getValue("EXAM_STATE", myExam.state) }}
                            </span>
                            <span class="my-exam__tag my-exam__tag--mark-state">
                                {{ dictStore.getValue("MARK_STATE", myExam.markState) }}
                            </span>
                        </div>
                    </template>

                    <el-button type="primary" class="my-exam__btn"
                        @click="$router.push(`/my-exam/read/${myExam.examId}`)">
                        {{ myExam.markState >= 2 ? '查阅试卷' : '进入考试' }}
                    </el-button>
                </div>
            </xmks-card-data>
        </el-scrollbar>
        <div class="my-exam-list__foot">
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
import XmksCardData from '@/components/card/xmks-card-data.vue'
import XmksCardEmpty from '@/components/card/xmks-card-empty.vue'
import { useDictStore } from '@/stores/dict'
import XmksCountDown from '@/components/xmks-count-down.vue'
import { myExamListpage } from '@/api/my/my-exam'
import { diff } from '@/util/timeUtil'

/************************变量定义相关***********************/
const dictStore = useDictStore() // 字典缓存
const queryForm = reactive({// 查询表单
    examName: '', // 考试名称
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
    const { data: { code, data } } = await myExamListpage({
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
.my-exam-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .my-exam-list__head {
        .query {
            .el-form-item {
                width: 260px;

                &:last-child {
                    width: initial;
                    margin-right: 0px;
                }
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
    }

    :deep(.my-exam-list__main) {
        flex: 1;

        .el-scrollbar__view {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px 20px;

            .my-exam {
                display: flex;
                flex-direction: column;
                height: 220px;

                .my-exam__exam-time {
                    display: flex;
                    justify-content: center;
                    margin-top: 10px;
                    font-size: 14px;
                    color: #8F939C;
                }

                .my-exam__outer {
                    display: grid;
                    grid-template-columns: repeat(4, 1fr);
                    height: 74px;
                    justify-content: center;
                    align-items: center;
                    margin-top: 10px;
                    background: #EFF5FA;
                    border-radius: 6px 6px 6px 6px;

                    .my-exam__inner {
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

                        .my-exam__num {
                            font-size: 16px;
                            color: #333333;

                            .my-exam__unit {
                                font-size: 10px;
                                color: #8F939C;
                            }
                        }


                        .my-exam__after-txt {
                            font-size: 12px;
                            color: #8F939C;
                            flex: 1 0 100%;
                            margin-left: 6px;
                            line-height: 26px;
                        }
                    }
                }

                .my-exam__other {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-top: 16px;
                    font-size: 12px;
                    color: #8F939C;

                    .my-exam__tag {
                        font-size: 12px;
                        padding: 2px 4px;
                        border-radius: 4px 4px 4px 4px;
                        margin-right: 10px;

                        &.my-exam__tag--state {
                            color: #FE7068;
                            background: #FFE6E6;
                            border: 1px solid #FFCAC7;
                        }

                        &.my-exam__tag--mark-state {
                            color: #1AC693;
                            background: #E8F9F4;
                            border: 1px solid #AFE7D6;
                        }
                    }

                    .my-exam__btn {
                        height: 30px;
                        padding: 0px 20px;
                        border-radius: 6px;
                        border: 0px;
                        color: #FFFFFF;
                        font-size: 14px;
                        background-image: linear-gradient(to right, #04C7F2, #259FF8);
                    }
                }
            }
        }
    }

    .my-exam-list__foot {
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
