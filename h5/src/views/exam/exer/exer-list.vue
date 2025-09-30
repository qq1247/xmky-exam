<template>
    <div class="exer-list">
        <div class="exer-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <el-scrollbar max-height="calc(100vh - 360px)" class="exer-list__main">
            <xmks-card-add name="添加练习" @click="$router.push('/exer/add')"></xmks-card-add>
            <xmks-card-data v-for="exer in listpage.list" :key="exer.id" :title="exer.name" tag-name="练习" :btns="[{
                name: '设置',
                icon: 'icon-liebiao-01',
                event: () => $router.push(`/exer/set/${exer.id}`)
            }, {
                name: '统计',
                icon: 'icon-liebiao-02',
                event: () => $router.push(`/exer/statis/${exer.id}`)
            }]" class="exer">
                <div class="exer__state">
                    <span class="exer__pre-txt">
                        发布练习：<span class="exer__num">{{ dictStore.getValue('STATE_PS', exer.state) }}</span>
                    </span>
                    <!-- <span class="exer__pre-txt">
                        允许评论：<span class="exer__num">{{ dictStore.getValue('STATE_YN', exer.rmkState) }}</span>
                    </span> -->
                </div>
                <div class="exer__outer">
                    <div class="exer__inner">
                        <span class="exer__num">
                            {{ exer.questionBankIds.length }}<span class="exer__unit">个</span>
                        </span>
                        <span class="exer__after-txt">题库已选</span>
                    </div>
                    <div class="exer__inner">
                        <span class="exer__num">
                            {{ exer.objectiveNum + exer.subjectiveNum }}<span class="exer__unit">题</span>
                        </span>
                        <span class="exer__after-txt">试题合计</span>
                    </div>
                    <div class="exer__inner">
                        <span class="exer__num">
                            {{ exer.orgIds.length }}<span class="exer__unit">个</span>
                        </span>
                        <span class="exer__after-txt">机构已选</span>
                    </div>
                    <div class="exer__inner">
                        <span class="exer__num">
                            {{ exer.userIds.length }}<span class="exer__unit">人</span>
                        </span>
                        <span class="exer__after-txt">用户已选</span>
                    </div>
                </div>
                <div class="exer__other">
                    <span class="exer__time">{{ exer.updateTime }}</span>
                    <span class="exer__username">{{ exer.createUserName }}</span>
                </div>
            </xmks-card-data>
        </el-scrollbar>
        <div class="exer-list__foot">
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
import XmksCardAdd from '@/components/card/xmks-card-add.vue'
import { useDictStore } from '@/stores/dict'
import { exerListpage } from '@/api/exam//exer'

/************************变量定义相关***********************/
const dictStore = useDictStore() // 字典缓存
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
    const { data: { code, data } } = await exerListpage({
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
.exer-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .exer-list__head {
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

    :deep(.exer-list__main) {
        flex: 1;

        .el-scrollbar__view {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px 20px;

            .exer {
                display: flex;
                flex-direction: column;
                height: 220px;

                .exer__state {
                    display: flex;
                    justify-content: center;
                    align-items: baseline;
                    margin-top: 15px;

                    .exer__pre-txt {
                        font-size: 12px;
                        color: #8F939C;
                        margin-right: 20px;

                        .exer__num {
                            font-size: 12px;
                            color: #333333;
                        }
                    }
                }

                .exer__outer {
                    display: grid;
                    grid-template-columns: repeat(4, 1fr);
                    height: 74px;
                    justify-content: center;
                    align-items: center;
                    margin-top: 10px;
                    background: #EFF5FA;
                    border-radius: 6px 6px 6px 6px;

                    .exer__inner {
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

                        .exer__num {
                            font-size: 16px;
                            color: #333333;

                            .exer__unit {
                                font-size: 10px;
                                color: #8F939C;
                            }
                        }


                        .exer__after-txt {
                            font-size: 12px;
                            color: #8F939C;
                            flex: 1 0 100%;
                            margin-left: 6px;
                            line-height: 26px;
                        }
                    }
                }

                .exer__other {
                    display: flex;
                    justify-content: space-between;
                    margin-top: 20px;
                    font-size: 12px;
                    color: #8F939C;
                }
            }
        }
    }

    .exer-list__foot {
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
