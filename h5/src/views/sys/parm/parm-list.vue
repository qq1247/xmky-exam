<template>
    <div class="parm">
        <div class="parm__head">
            <div class="opt">

            </div>
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input placeholder="" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="parm__main">
            <el-table :data="listpage.list" size="large" row-key="id" height="calc(100vh - 460px)" class="table">
                <el-table-column prop="type" label="密码生成策略" align="center">
                    <template #default="scope">
                        {{ scope.row.type === 1 ? '随机密码' : '固定密码' }}
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="企业名称" align="center" />
                <el-table-column prop="logo" label="企业logo" align="center">
                    <img :src="logoUrl" class="table__logo" />
                </el-table-column>
                <el-table-column prop="host" label="移动端主机" align="center" />
                <el-table-column prop="title" label="自定义标题" align="center" />
                <el-table-column prop="" label="操作" align="center" width="300">
                    <template #default="scope">
                        <el-tooltip ffect="dark" content="设置">
                            <span class="iconfont icon-liebiao-01 table__btn"
                                @click="$router.push(`/parm-nav/set/${scope.row.id}`)"></span>
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
import { parmGet } from '@/api/sys/parm'
import http from '@/request'
import type { Listpage } from '@/ts/common/listpage'
import { onMounted, reactive, ref, } from 'vue'

/************************变量定义相关***********************/
const queryForm = reactive({// 查询表单
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 8,
    total: 0,
    list: [],
})
const logoUrl = ref(`${http.defaults.baseURL}login/logo?t=${new Date().getTime()}`)

/************************组件生命周期相关*********************/
onMounted(() => {
    query()
})

/************************事件相关*****************************/
// 查询
async function query() {
    const { data: { data } } = await parmGet({ id: 1 })
    const row: any = {}
    row.id = 1
    row.type = data.pwdType
    row.value = data.pwdValue
    row.name = data.entName
    row.host = data.mHost
    row.title = data.customTitle
    row.content = data.customContent

    listpage.list = [row]
    listpage.total = 1
}
</script>
<style lang="scss" scoped>
.parm {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 30px 30px 30px 30px;

    .parm__head {
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

    .parm__main {
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

            .table__logo {
                width: 50px;
                height: 50px;
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
