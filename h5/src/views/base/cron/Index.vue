<template>
    <template v-if="$route.path === '/cron'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.name" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
            <el-form-item style="float: right;">
                <el-button type="success" @click="$router.push('/cron/add')">
                    <Iconfont icon="icon-plus" color="white">&nbsp;添加</Iconfont>
                </el-button>
            </el-form-item>
        </el-form>
        <el-table :data="listpage.list" style="width: 100%" class="table" size="large" row-key="id">
            <el-table-column prop="name" label="名称" align="center" />
            <el-table-column prop="cronKey" label="状态" align="center">
                <template #default="scope">
                    {{ dictStore.getValue('STATE_SS', scope.row.state) }}
                </template>
            </el-table-column>
            <el-table-column prop="triggerTimes" label="最近三次触发时间" align="center">
                <template #default="scope">
                    <template v-for="triggerTime in scope.row.triggerTimes">
                        <div>{{ triggerTime }}</div>
                    </template>
                </template>
            </el-table-column>
            <el-table-column prop="" label="操作" align="center" width="300">
                <template #default="scope">
                    <el-tooltip ffect="dark" content="修改">
                        <Iconfont icon="icon-edit" :size="18" color="#409eff"
                            @click="$router.push(`/cron/edit/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="删除">
                        <Iconfont icon="icon-delete" :size="18" color="#409eff"
                            @click="$router.push(`/cron/del/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip ffect="dark" content="启动/停止">
                        <Iconfont icon="icon-start" :size="18" color="#409eff"
                            @click="$router.push(`/cron/start/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip ffect="dark" content="执行一次">
                        <Iconfont icon="icon-once" :size="18" color="#409eff"
                            @click="$router.push(`/cron/runOnce/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                </template>
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

//  定义变量
const route = useRoute()
const dictStore = useDictStore()// 字典缓存
const queryForm = reactive({// 查询表单
    name: '',
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
    if (n === '/cron') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('cron/listpage', {
        name: queryForm.name,
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
  