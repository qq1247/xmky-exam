<template>
    <template v-if="$route.path === '/dict'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.dictIndex" placeholder="请输入索引" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
            <el-form-item style="float: right;">
                <el-button type="success" @click="$router.push('/dict/add')">
                    <Iconfont icon="icon-plus" color="white">&nbsp;添加</Iconfont>
                </el-button>
            </el-form-item>
        </el-form>
        <el-table :data="listpage.list" style="width: 100%" class="table" size="large" row-key="id">
            <el-table-column prop="dictIndex" label="索引" align="center" />
            <el-table-column prop="dictKey" label="键" align="center" />
            <el-table-column prop="dictValue" label="值" align="center" />
            <el-table-column prop="no" label="排序" align="center" />
            <el-table-column prop="" label="操作" align="center" width="300">
                <template #default="scope">
                    <el-tooltip ffect="dark" content="修改">
                        <Iconfont icon="icon-edit" :size="18" color="#409eff"
                            @click="$router.push(`/dict/edit/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="删除">
                        <Iconfont icon="icon-delete" :size="18" color="#409eff"
                            @click="$router.push(`/dict/del/${scope.row.id}`)"></Iconfont>
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

//  定义变量
const route = useRoute()
const queryForm = reactive({// 查询表单
    dictIndex: '',
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
    if (n === '/dict') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('dict/listpage', {
        dictIndex: queryForm.dictIndex,
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
  