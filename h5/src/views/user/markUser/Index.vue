<template>
    <template v-if="$route.path === '/markUser'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
            <el-form-item style="float: right;">
                <el-button type="success" @click="$router.push('/markUser/add')">
                    <Iconfont icon="icon-plus" color="white">&nbsp;添加</Iconfont>
                </el-button>
            </el-form-item>
        </el-form>
        <el-table :data="listpage.list" style="width: 100%" class="table" size="large" row-key="id"
            default-expand-all>
            <el-table-column prop="name" label="阅卷用户" align="center" />
            <el-table-column prop="loginName" label="登录账号" align="center" />
            <el-table-column prop="" label="拥有权限" align="center" width="400">
                <template #default="scope">
                    协助阅卷
                </template>
            </el-table-column>
            <el-table-column prop="state" label="状态" align="center">
                <template #default="scope">
                    <span>{{ dictStore.getValue('STATE_NF', scope.row.state) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="" label="操作" align="center" width="300">
                <template #default="scope">
                    <el-tooltip ffect="dark" content="修改">
                        <Iconfont icon="icon-edit" :size="18" color="#409eff"
                            @click="$router.push(`/markUser/edit/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="删除">
                        <Iconfont icon="icon-delete" :size="18" color="#409eff"
                            @click="$router.push(`/markUser/del/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="密码重置">
                        <Iconfont icon="icon-pwd-reset" :size="18" color="#409eff"
                            @click="$router.push(`/markUser/pwdInit/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip effect="dark" content="账号冻结">
                        <Iconfont icon="icon-dongjie" :size="18" color="#409eff"
                            @click="$router.push(`/markUser/frozen/${scope.row.id}`)"></Iconfont>
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

// 定义变量
const route = useRoute()
const dictStore = useDictStore()// 字典缓存
const queryForm = reactive({// 查询表单
    name: '',
    state: '',
    type: 3 // 查询阅卷用户
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
    if (n === '/markUser') {
        query()
    }
})

// 查询
async function query() { 
    const { data: { code, data } } = await http.post('user/listpage', {
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

</style>
  