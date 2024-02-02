<template>
    <template v-if="$route.path === '/examUser'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.name" placeholder="请输入姓名或机构名称" />
            </el-form-item>
            <el-form-item label="">
                <el-select v-model="queryForm.state" placeholder="请选择状态" clearable size="large">
                    <el-option
                        v-for="dict in dictStore.getList('STATE_NF')"
                        :key="dict.dictKey"
                        :label="dict.dictValue"
                        :value="dict.dictKey"
                    />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
        </el-form>
        <el-table :data="listpage.list" style="width: 100%" class="table" size="large" row-key="id"
            default-expand-all>
            <el-table-column prop="name" label="姓名" align="center" />
            <el-table-column prop="loginName" label="登录账号" align="center" />
            <el-table-column prop="orgName" label="所属机构" align="center" />
            <el-table-column prop="state" label="状态" align="center">
                <template #default="scope">
                    <span v-if="scope.row.state === 2" style="color: #f56c6c">
                        {{ dictStore.getValue('STATE_NF', scope.row.state) }}
                    </span>
                    <span v-else>{{ dictStore.getValue('STATE_NF', scope.row.state) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="" label="操作" align="center" width="300">
                
            </el-table-column>
        </el-table>
        <el-pagination 
            v-model:current-page="listpage.curPage" 
            v-model:page-size="listpage.pageSize" 
            :total="listpage.total" 
            background
            layout="prev, pager, next" 
            :hide-on-single-page="true" 
            @size-change="query"
            @current-change="query"
            @prev-click="query"
            @next-click="query"
        />
    </template>
    <RouterView v-else></RouterView>
</template>
  
<script lang="ts" setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router'
import http from "@/request"
import { useDictStore } from '@/stores/dict';
import { useUserStore } from '@/stores/user';

// 定义变量
const route = useRoute()
const dictStore = useDictStore()// 字典缓存
const queryForm = reactive({// 查询表单
    name: '',
    state: ''
})
const listpage = reactive({// 分页列表
    curPage: 1,
    pageSize: 10,
    total: 0,
    list: [] as any[],
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    query()
})

// 如果是跳转到列表页，重新查询
watch(() => route.path, (n, o) => {
    if (n === '/examUser') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('user/listpage', {
        name: queryForm.name,
        state: queryForm.state,
        type: 1,
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

</style>
  