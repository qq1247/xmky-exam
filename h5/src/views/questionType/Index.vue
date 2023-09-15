<template>
    <template v-if="$route.path === '/questionType'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.name" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
            <!-- <el-form-item style="float: right;">
                <el-button type="success" @click="$router.push('/questionType/add')">
                    <Iconfont icon="icon-plus" color="white">&nbsp;添加</Iconfont>
                </el-button>
            </el-form-item> -->
        </el-form>
        <div class="list">
            <Gridadd name="题库添加" @click="$router.push('/questionType/add')"/>
            <Griddata 
                v-for="questionType in listpage.list" 
                :menu="[
                    { name: '修改', icon: 'icon-edit', event: () => $router.push(`/questionType/edit/${questionType.id}`) },
                    { name: '删除', icon: 'icon-delete', event: () => $router.push(`/questionType/del/${questionType.id}`) },
                    { name: '清空试题', icon: 'icon-clear', event: () => $router.push(`/questionType/clear/${questionType.id}`) },
                    { name: '试题列表', icon: 'icon-list-row', event: () => $router.push(`/questionType/question/${questionType.id}`) },
                    ]" 
                >
                <template #title>
                    {{ questionType.name }}
                </template>
                <template #content>
                    <div style="margin-bottom: 10px;">试题数量：{{questionType.questionNum}}道</div>
                    <div v-if="userStore.type === 0" style="margin-bottom: 10px;">创建用户：{{questionType.createUserName}}</div>
                </template>
            </Griddata>
        </div>
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
import Griddata from '@/components/Griddata.vue';
import Gridadd from '@/components/Gridadd.vue';
import { useUserStore } from '@/stores/user';

//  定义变量
const userStore = useUserStore()
const route = useRoute()
const queryForm = reactive({// 查询表单
    name: '',
})
const listpage = reactive({// 分页列表
    curPage: 1,
    pageSize: 5,
    total: 0,
    list: [] as any[],
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    query()
})

// 如果是跳转到列表页，重新查询
watch(() => route.path, (n, o) => {
    if (n === '/questionType') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('questionType/listpage', {
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
.list {
    display: flex;
    flex-wrap: wrap;
    align-content: flex-start;
}
</style>
  