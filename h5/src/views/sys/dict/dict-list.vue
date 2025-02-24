<template>
    <div class="dict">
        <div class="dict__head">
            <div class="opt">
                <el-button type="success" class="opt__btn" @click="$router.push('/dict-nav/add')">
                    <span class="iconfont icon-tubiaoziti2-02 opt__btn-icon"></span>
                    <span class="opt__btn-txt">添加</span>
                </el-button>
            </div>
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.dictIndex" placeholder="请输入索引" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="dict__main">
            <el-table :data="listpage.list" size="large" row-key="id" height="calc(100vh - 460px)" class="table">
                <el-table-column prop="dictIndex" label="索引" align="center" />
                <el-table-column prop="dictKey" label="键" align="center" />
                <el-table-column prop="dictValue" label="值" align="center" />
                <el-table-column prop="no" label="排序" align="center" />
                <el-table-column prop="" label="操作" align="center" width="300">
                    <template #default="scope">
                        <el-tooltip ffect="dark" content="设置">
                            <span class="iconfont icon-liebiao-01 table__btn"
                                @click="$router.push(`/dict-nav/set/${scope.row.id}`)"></span>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next" :hide-on-single-page="true" size="large"
                class="pagination" @size-change="query" @current-change="query" @prev-click="query"
                @next-click="query" />
        </div>
    </div>
</template>
<script setup lang="ts">
import { dictListpage } from '@/api/sys/dict'
import type { Listpage } from '@/ts/common/listpage'
import { onMounted, reactive, } from 'vue'

/************************变量定义相关***********************/
const queryForm = reactive({// 查询表单
    dictIndex: '',
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 8,
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
    const { data: { code, data } } = await dictListpage({
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
.dict {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 30px 30px 30px 30px;

    .dict__head {
        display: flex;
        justify-content: space-between;

        .opt {
            flex: 1;
            display: flex;

            .opt__btn {
                height: 40px;
                padding: 0px 30px;
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
        }

        .query {
            .el-form-item {
                &:last-child {
                    margin-right: 0px;
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
    }

    .dict__main {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-between;

        .table {

            .table__btn {
                color: #409EFF;
                cursor: pointer;
            }

            .table__btn--warn {
                color: #FF5D15;
            }

            :deep(.el-table__inner-wrapper) {
                &::before {
                    height: 0px;
                }
            }
        }

        :deep(.pagination) {

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
</style>
