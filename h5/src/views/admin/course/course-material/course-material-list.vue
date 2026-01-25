<template>
    <div class="course-material-list">
        <div class="course-material-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
            <div class="opt">
                <el-button v-if="route.params.courseId !== '0'" type="success" class="opt__btn"
                    @click="$router.push(`/course/course-nav/add/${queryForm.courseId}`)">
                    <span class="iconfont icon-tubiaoziti2-02 opt__btn-icon"></span>
                    <span class="opt__btn-txt">添加</span>
                </el-button>
            </div>
        </div>
        <div class="course-material-list__main">
            <el-scrollbar height="calc(100vh - 275px)">
                <div v-for="(courseMaterial, index) in listpage.list" :key="index" class="list">
                    <div class="list__title">
                        <slot name="title-pre"></slot>
                        <span>{{ courseMaterial.content }}</span>
                    </div>
                    <div class="list__tags">
                        <el-tag class="list__tag list__tag--type">
                            {{ courseMaterial.name }}
                        </el-tag>
                        <el-tag class="list__tag list__tag--mark-type">
                            {{ courseMaterial.videoTime }}
                        </el-tag>
                        <el-tag class="list__tag list__tag--score">
                            试题{{ courseMaterial.questionNum }}道
                        </el-tag>
                        <el-tag class="list__tag list__tag--username">
                            第{{ courseMaterial.no }}小节
                        </el-tag>
                    </div>
                    <div class="list__opt">
                        <span data-name="设置" class="list__btn"
                            @click="$router.push(`/course/course-nav/set/${courseMaterial.id}`)">
                            <i :class="`iconfont icon-liebiao-01`"></i>
                        </span>
                    </div>
                </div>
                <el-empty v-if="listpage.list.length === 0" description="暂无数据" class="course-material-list__empty" />
            </el-scrollbar>
        </div>
        <div class="course-material-list__foot">
            <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next" :hide-on-single-page="false" size="large"
                class="pagination" @size-change="query" @current-change="query" @prev-click="query"
                @next-click="query" />
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, onMounted, } from 'vue'
import type { Listpage } from '@/ts/common/listpage'
import { courseMaterialListpage } from '@/api/course/course-material'
import { useRoute } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()
const queryForm = reactive({// 查询表单
    courseId: null as number | null,
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
    queryForm.courseId = parseInt(route.params.courseId as string) || null

    query()
})

/************************事件相关*****************************/
// 查询
async function query() {
    const { data: { code, data } } = await courseMaterialListpage({
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
.course-material-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .course-material-list__head {
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

    :deep(.course-material-list__main) {
        background-color: #FFFFFF;
        border-radius: 15px 15px 15px 15px;
        padding: 30px;

        .el-scrollbar__view {
            height: 100%;

            .list {
                display: flex;
                flex-direction: column;
                justify-content: center;
                position: relative;
                height: 85px;
                border-bottom: 1px dashed #E5E5E5;
                cursor: pointer;
                padding-left: 10px;

                &:hover {
                    background-color: #F2F6F9;

                    .list__opt {
                        display: block;
                    }
                }

                .list__title {
                    width: 900px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    font-size: 14px;
                    line-height: 30px;
                    color: #303133;
                }

                .list__tags {
                    .list__tag {
                        height: 22px;
                        padding: 0px 10px;
                        font-size: 12px;
                        margin-right: 10px;

                        &.list__tag--type {
                            color: #1EA1EE;
                            background-color: #E4F6FF;
                            border: 1px solid #C0EAFF;
                        }

                        &.list__tag--mark-type {
                            color: #FC8113;
                            background-color: #FDEDD9;
                            border: 1px solid #FED9B3;
                        }

                        &.list__tag--score {
                            color: #FE7068;
                            background-color: #FFE6E6;
                            border: 1px solid #FFCAC7;
                        }

                        &.list__tag--username {
                            color: #1AC693;
                            background-color: #E8F9F4;
                            border: 1px solid #AFE7D6;
                        }
                    }
                }

                .list__opt {
                    display: none;
                    position: absolute;
                    top: 16px;
                    right: 30px;

                    .list__btn {
                        display: inline-block;
                        width: 28px;
                        height: 28px;
                        line-height: 28px;
                        margin: 0px 10px;
                        text-align: center;
                        border-radius: 50%;
                        border: 1px solid #E5E5E5;
                        color: #8F939C;
                        position: relative;
                        cursor: pointer;
                        z-index: 1;

                        &::before {
                            content: "";
                            display: block;
                            position: absolute;
                            bottom: -10px;
                            left: 50%;
                            transform: translateX(-50%);
                            border-width: 0 5px 10px 5px;
                            border-style: solid;
                            border-color: transparent transparent #04C7F2;
                            opacity: 0;
                        }

                        &::after {
                            content: attr(data-name);
                            display: block;
                            position: absolute;
                            bottom: -40px;
                            transform: translateX(-50%);
                            left: 50%;
                            height: 30px;
                            width: 80px;
                            line-height: 30px;
                            background-image: linear-gradient(to right, #04C7F2, #259FF8);
                            color: white;
                            border-radius: 6px;
                            font-size: 14px;
                            opacity: 0;
                        }

                        &:hover {
                            border: 1px solid #04C7F2;
                            background-image: linear-gradient(to right, #04C7F2, #259FF8);
                            color: white;

                            &::before,
                            &::after {
                                opacity: 1;
                            }
                        }
                    }
                }
            }

            .course-material-list__empty {
                height: 100%;
            }
        }
    }

    .course-material-list__foot {
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
