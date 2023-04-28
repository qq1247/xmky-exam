<template>
    <template v-if="$route.path === '/exer'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.name" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
        </el-form>
        <div class="list">
            <Gridadd name="练习添加" @click="$router.push('/exer/add')"/>
            <Griddata 
                v-for="exer in listpage.list" 
                :menu="[
                    { name: '修改', icon: 'icon-edit', event: () => $router.push(`/exer/edit/${exer.id}`) },
                    { name: '删除', icon: 'icon-delete', event: () => $router.push(`/exer/del/${exer.id}`) },
                    ]" 
                >
                <template #title>
                    {{ exer.name }}
                </template>
                <template #content>
                    <div style="margin-bottom: 5px;text-align: center;">
                        题库名称：{{ exer.questionTypeName }}
                    </div>
                    <div style="margin-bottom: 5px;text-align: center;">
                        练习时间：{{ exer.startTime }} - {{ exer.endTime }}
                    </div>
                    <el-row>
                        <el-col :span="12">
                            允许评论：{{ dictStore.getValue('STATE_YN', exer.rmkState) }}
                        </el-col>
                        <el-col :span="12">
                            人数：{{ exer.userIds.length == 0 ? '全部' : exer.userIds.length }}
                        </el-col>
                    </el-row>
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
import { useDictStore } from '@/stores/dict';
import Griddata from '@/components/Griddata.vue';
import Gridadd from '@/components/Gridadd.vue';

//  定义变量
const dictStore = useDictStore()// 字典缓存
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
    if (n === '/exer') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('exer/listpage', {
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
  