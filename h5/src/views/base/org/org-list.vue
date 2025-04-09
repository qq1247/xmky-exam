<template>
    <div class="org-list">
        <div class="org-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="org-list__main">
            <el-table :data="listpage.list" size="large" row-key="id" default-expand-all :indent="48"
                height="calc(100vh - 428px)" class="table">
                <el-table-column prop="name" label="名称" align="left" />
                <el-table-column prop="no" label="排序" align="center" />
                <el-table-column v-if="userStore.type === 0" prop="" label="操作" align="center" width="300">
                    <template #default="scope">
                        <el-tooltip effect="dark" content="添加">
                            <span class="iconfont icon-icon-01 table__btn"
                                @click="$router.push(`/org-nav/add/${scope.row.id}`)"></span>
                        </el-tooltip>
                        <el-tooltip ffect="dark" content="设置">
                            <span class="iconfont icon-liebiao-01 table__btn"
                                @click="$router.push(`/org-nav/set/${scope.row.id}`)"></span>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, onMounted, } from 'vue'
import type { Listpage } from '@/ts/common/listpage'
import { orgListpage } from '@/api/base/org'
import { useUserStore } from '@/stores/user'

/************************变量定义相关***********************/
const userStore = useUserStore()// 用户缓存
const queryForm = reactive({// 查询表单
    name: '',
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 100,
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
    const { data: { code, data } } = await orgListpage({
        name: queryForm.name,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    const list = data.list
    const treeList: object[] = []// 列表转树形列表
    const treeMap: any = {}
    const idFiled = 'id'
    const parentField = 'parentId'
    for (let i = 0; i < list.length; i++) {
        list[i]['id'] = list[i][idFiled]
        treeMap[list[i]['id']] = list[i]
    }

    for (let i = 0; i < list.length; i++) {
        if (treeMap[list[i][parentField]] && list[i]['id'] !== list[i][parentField]) {
            if (!treeMap[list[i][parentField]]['children']) {
                treeMap[list[i][parentField]]['children'] = []
            }
            treeMap[list[i][parentField]]['children'].push(list[i])
        } else {
            treeList.push(list[i])
        }
    }

    listpage.list = treeList
    listpage.total = data.total
}

</script>
<style lang="scss" scoped>
.org-list {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 30px;

    .org-list__head {
        display: flex;
        justify-content: flex-end;

        .query {
            .el-form-item {
                &:last-child {
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

    .org-list__main {
        flex: 1;

        .table {

            .table__btn {
                margin-right: 20px;
                color: #409EFF;
                cursor: pointer;
            }

            :deep(.el-table__inner-wrapper) {
                &::before {
                    height: 0px;
                }
            }
        }
    }
}
</style>
