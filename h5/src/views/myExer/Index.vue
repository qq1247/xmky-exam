<template>
    <template v-if="$route.path === '/myExer'">
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
            <Gridadd v-if="listpage.list.length === 0" name="暂无练习" icon="icon-dongjie"/>
            <Griddata 
                v-for="myExer in listpage.list" 
                :menu="[
                    { name: `去练习`, icon: `icon-peixunkaoshi`, event: () => toExer(myExer) },
                    ]"
                >
                <template #title>
                    {{ myExer.name }}
                </template>
                <template #content>
                    <div style="margin-bottom: 5px;text-align: center;">
                        题库名称：{{ myExer.questionTypeName }}
                    </div>
                    <div style="margin-bottom: 5px;text-align: center;">
                        练习时间：{{ myExer.startTime }} - {{ myExer.endTime }}
                    </div>
                    <el-row>
                        <el-col :span="24">
                            允许评论：{{ dictStore.getValue('STATE_YN', myExer.rmkState) }}
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
import http from "@/request";
import { useDictStore } from '@/stores/dict';
import dayjs from 'dayjs';
import { ElMessage } from "element-plus";
import { onMounted, reactive, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router'
import Griddata from "@/components/Griddata.vue";
import Gridadd from "@/components/Gridadd.vue";

//  定义变量
const dictStore = useDictStore()// 字典缓存
const route = useRoute()
const router = useRouter()
const queryForm = reactive({// 查询表单
    name: '',
})
const listpage = reactive({// 分页列表
    curPage: 1,
    pageSize: 6,
    total: 0,
    list: [] as any[],
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    query()
})

// 如果是跳转到列表页，重新查询
watch(() => route.path, (n, o) => {
    if (n === '/myExer') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('myExer/listpage', {
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

// 练习进入
async function toExer(exer: any) {
    let { data: { data } } = await http.post("login/sysTime", {  })
        let curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
        let exerStartTim = dayjs(exer.startTime, 'YYYY-MM-DD HH:mm:ss').toDate()
        if (exerStartTim.getTime() > curTime.getTime()) {
            ElMessage.error('练习未开始，请等待...')
            return
        }

        let exerEndTime = dayjs(exer.endTime, 'YYYY-MM-DD HH:mm:ss').toDate()
        if (curTime.getTime() > exerEndTime.getTime()) {
            ElMessage.error('练习已结束...')
            return
        }

        router.push(`/myExer/paper/${ exer.id }`)
}
</script>

<style lang="scss" scoped>
.list {
    display: flex;
    flex-wrap: wrap;
    align-content: flex-start;
}
</style>
  