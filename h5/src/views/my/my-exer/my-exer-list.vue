<template>
    <div class="my-exer-list">
        <div class="my-exer-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <el-scrollbar max-height="calc(100vh - 360px)" class="my-exer-list__main">
            <xmks-card-empty v-if="listpage.total === 0" name="暂无练习" icon="icon-tubiaoziti22-22"></xmks-card-empty>
            <xmks-card-data v-else v-for="exer in listpage.list" :key="exer.id" :title="exer.name" tag-name="练习"
                class="my-exer">
                <div class="my-exer__exam-time">
                    <span class="my-exer__pre-txt">
                        客观题：<span class="my-exer__num">{{ exer.objectiveNum }}</span>道
                    </span>
                    <span class="my-exer__pre-txt">
                        主观题：<span class="my-exer__num">{{ exer.subjectiveNum }}</span>道
                    </span>
                </div>
                <div class="my-exer__outer">
                    <div class="my-exer__inner">
                        <span class="my-exer__num">
                            {{ exer.singleNum }}<span class="my-exer__unit">道</span>
                        </span>
                        <span class="my-exer__after-txt">单选题</span>
                    </div>
                    <div class="my-exer__inner">
                        <span class="my-exer__num">
                            {{ exer.multipleNum }}<span class="my-exer__unit">道</span>
                        </span>
                        <span class="my-exer__after-txt">多选题</span>
                    </div>
                    <div class="my-exer__inner">
                        <span class="my-exer__num">
                            {{ exer.fillBlankObjNum + exer.fillBlankSubNum }}<span class="my-exer__unit">道</span>
                        </span>
                        <span class="my-exer__after-txt">填空题</span>
                    </div>
                    <div class="my-exer__inner">
                        <span class="my-exer__num">
                            {{ exer.judgeNum }}<span class="my-exer__unit">道</span>
                        </span>
                        <span class="my-exer__after-txt">判断题</span>
                    </div>
                    <div class="my-exer__inner">
                        <span class="my-exer__num">
                            {{ exer.qaObjNum + exer.qaSubNum }}<span class="my-exer__unit">道</span>
                        </span>
                        <span class="my-exer__after-txt">问答题</span>
                    </div>
                </div>
                <div class="my-exer__other">
                    <div></div>
                    <el-button type="primary" class="my-exer__btn" @click="toExer(exer)">
                        进入练习
                    </el-button>
                </div>
            </xmks-card-data>
        </el-scrollbar>
        <div class="my-exer-list__foot">
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
import { useRouter } from 'vue-router'
import { exerListpage } from '@/api/exam/exer'

/************************变量定义相关***********************/
const router = useRouter()
const queryForm = reactive({// 查询表单
    name: '', // 练习名称
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 6,
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
    const { data: { code, data } } = await exerListpage({
        ...queryForm,
        state: 1,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    listpage.list = data.list
    listpage.total = data.total
}

// 练习进入
async function toExer(exer: any) {
    router.push(`/my-exer/read/${exer.id}`)
}

</script>
<style lang="scss" scoped>
.my-exer-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .my-exer-list__head {
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

    :deep(.my-exer-list__main) {
        flex: 1;

        .el-scrollbar__view {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px 20px;

            .my-exer {
                display: flex;
                flex-direction: column;
                height: 220px;

                .my-exer__exam-time {
                    display: flex;
                    justify-content: center;
                    align-items: baseline;
                    margin-top: 10px;

                    .my-exer__pre-txt {
                        font-size: 12px;
                        color: #8F939C;
                        margin-right: 20px;

                        .my-exer__num {
                            font-size: 16px;
                            color: #333333;
                        }
                    }
                }

                .my-exer__outer {
                    display: grid;
                    grid-template-columns: repeat(5, 1fr);
                    height: 74px;
                    justify-content: center;
                    align-items: center;
                    margin-top: 10px;
                    background: #EFF5FA;
                    border-radius: 6px 6px 6px 6px;

                    .my-exer__inner {
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

                        .my-exer__num {
                            font-size: 16px;
                            color: #333333;

                            .my-exer__unit {
                                font-size: 10px;
                                color: #8F939C;
                            }
                        }


                        .my-exer__after-txt {
                            font-size: 12px;
                            color: #8F939C;
                            flex: 1 0 100%;
                            margin-left: 6px;
                            line-height: 26px;
                        }
                    }
                }

                .my-exer__other {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-top: 16px;
                    font-size: 12px;
                    color: #8F939C;

                    .my-exer__btn {
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

    .my-exer-list__foot {
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
