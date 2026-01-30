<template>
    <div class="course-list">
        <div class="course-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <el-scrollbar max-height="calc(100vh - 360px)" class="course-list__main">
            <xmks-card-add name="添加课程" @click="$router.push('/course/add')"></xmks-card-add>
            <xmks-card-data v-for="course in listpage.list" :key="course.id" :title="course.name" tag-name="课程" :btns="[{
                name: '设置',
                icon: 'icon-liebiao-01',
                event: () => $router.push(`/course/set/${course.id}`)
            }, {
                name: '资料列表',
                icon: 'icon-a-16ri-05',
                event: () => $router.push(`/course/course-nav/list/${course.id}`)
            }]" class="course">
                <div class="course__state">
                    <span class="course__pre-txt">
                        发布课程：<span class="course__num">{{ dictStore.getValue('STATE_PS', course.state) }}</span>
                    </span>
                    <!-- <span class="course__pre-txt">
                        允许评论：<span class="course__num">{{ dictStore.getValue('STATE_YN', course.commentState) }}</span>
                    </span> -->
                </div>
                <div class="course__outer">
                    <div class="course__inner">
                        <span class="course__num">
                            {{ course.courseMaterialNum }}<span class="course__unit">个</span>
                        </span>
                        <span class="course__after-txt">资料合计</span>
                    </div>
                    <div class="course__inner">
                        <span class="course__num">
                            {{ course.questionNum }}<span class="course__unit">道</span>
                        </span>
                        <span class="course__after-txt">试题合计</span>
                    </div>
                    <div class="course__inner">
                        <span class="course__num">
                            {{ course.orgIds.length }}<span class="course__unit">个</span>
                        </span>
                        <span class="course__after-txt">机构已选</span>
                    </div>
                    <div class="course__inner">
                        <span class="course__num">
                            {{ course.userIds.length }}<span class="course__unit">人</span>
                        </span>
                        <span class="course__after-txt">用户已选</span>
                    </div>
                </div>
                <div class="course__other">
                    <span class="course__time">{{ course.updateTime }}</span>
                    <span class="course__username">
                        {{ course.createUserName }} /
                        {{ dictStore.getValue('SHARE_AUTH', course.shareAuth) }}权限</span>
                </div>
            </xmks-card-data>
        </el-scrollbar>
        <div class="course-list__foot">
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
import { courseListpage } from '@/api/course/course'

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
    const { data: { code, data } } = await courseListpage({
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
.course-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .course-list__head {
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

    :deep(.course-list__main) {
        flex: 1;

        .el-scrollbar__view {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px 20px;

            .course {
                display: flex;
                flex-direction: column;
                height: 220px;

                .course__state {
                    display: flex;
                    justify-content: center;
                    align-items: baseline;
                    margin-top: 15px;

                    .course__pre-txt {
                        font-size: 12px;
                        color: #8F939C;
                        margin-right: 20px;

                        .course__num {
                            font-size: 12px;
                            color: #333333;
                        }
                    }
                }

                .course__outer {
                    display: grid;
                    grid-template-columns: repeat(4, 1fr);
                    height: 74px;
                    justify-content: center;
                    align-items: center;
                    margin-top: 10px;
                    background: #EFF5FA;
                    border-radius: 6px 6px 6px 6px;

                    .course__inner {
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

                        .course__num {
                            font-size: 16px;
                            color: #333333;

                            .course__unit {
                                font-size: 10px;
                                color: #8F939C;
                            }
                        }


                        .course__after-txt {
                            font-size: 12px;
                            color: #8F939C;
                            flex: 1 0 100%;
                            margin-left: 6px;
                            line-height: 26px;
                        }
                    }
                }

                .course__other {
                    display: flex;
                    justify-content: space-between;
                    margin-top: 20px;
                    font-size: 12px;
                    color: #8F939C;
                }
            }
        }
    }

    .course-list__foot {
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
