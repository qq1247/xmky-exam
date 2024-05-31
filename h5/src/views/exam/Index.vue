<template>
    <template v-if="$route.path === '/exam'">
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
            <Gridadd name="考试添加" @click="$router.push('/exam/add')"/>
            <Griddata 
                v-for="exam in listpage.list" 
                :menu="generateMenu(exam)" 
                >
                <template #tag>
                    <el-tag size="small">{{ dictStore.getValue("PAPER_GEN_TYPE", exam.genType) }}</el-tag>
                    &nbsp;<el-tag size="small">{{ dictStore.getValue("MARK_STATE", exam.markState) }}</el-tag>
                    &nbsp;<el-tag size="small">{{ exam.state === 1 ? "发布" : "暂停" }}</el-tag>
                </template>
                <template #title>
                    {{ exam.name }}
                </template>
                <template #content>
                    <div style="margin-bottom: 5px;text-align: center;">
                        考试时间：{{ exam.startTime }} - {{ exam.endTime }}
                    </div>
                    <div v-if="exam.markType === 2" style="margin-bottom: 5px;text-align: center;">
                        阅卷时间：{{ exam.markStartTime }} - {{ exam.markEndTime }}
                    </div>
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="10">
                            及格分数：{{ exam.passScore || '-' }} / {{ exam.totalScore }}
                        </el-col>
                        <el-col :span="7">
                            <!-- 待批试卷：6 / 79 -->
                            考试人数：{{ exam.userNum }}
                        </el-col>
                        <el-col :span="7">
                            协助批阅：{{ exam.markUserNum || '无'}}
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="10">
                            成绩查询：{{ dictStore.getValue('SCORE_STATE', exam.scoreState) }}
                        </el-col>
                        <el-col :span="7">
                            排名：{{ dictStore.getValue('STATE_ON', exam.rankState) }}
                        </el-col>
                        <el-col :span="7">
                            防作弊：{{ exam.sxes.length > 0 ? '是' : '否' }}
                        </el-col>
                        <!-- <el-col :span="7">
                            匿名阅卷：{{ dictStore.getValue('STATE_YN', exam.anonState) }}
                        </el-col> -->
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
import Griddata from '@/components/Griddata.vue';
import Gridadd from '@/components/Gridadd.vue';
import { useDictStore } from '@/stores/dict';
import type { Menu } from '@/stores/exam';
import dayjs from 'dayjs';
import { ElMessage } from 'element-plus';

//  定义变量
const route = useRoute()
const router = useRouter()
const dictStore = useDictStore()// 字典缓存
const queryForm = reactive({// 查询表单
    name: '',
})
const listpage = reactive({// 分页列表
    curPage: 1,
    pageSize: 5,
    total: 0,
    list: [] as any[],
})
const curTime = ref()// 根据时间显示不同的按钮

// 组件挂载完成后，执行如下方法
onMounted(() => {
    query()
})

// 如果是跳转到列表页，重新查询
watch(() => route.path, (n, o) => {
    if (n === '/exam') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('exam/listpage', {
        name: queryForm.name,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }


    let { data: { data: data2 } } = await http.post("login/sysTime", {  })
    curTime.value = dayjs(data2, 'YYYY-MM-DD HH:mm:ss').toDate()

    listpage.list = data.list
    listpage.total = data.total
}

// 生成菜单
function generateMenu(exam: any) {
    let menu: Menu[] = [
        { name: '组卷', icon: 'icon-edit', event: () => router.push(`/exam/edit/${exam.id}`) },
        { name: '暂停', icon: 'icon-end', event: () => router.push(`/exam/pause/${exam.id}`) },
        { name: '删除', icon: 'icon-delete', event: () => router.push(`/exam/del/${exam.id}`) },
        { name: '变更时间', icon: 'icon-time', event: () => router.push(`/exam/time/${exam.id}`) },
        { name: '阅卷', icon: 'icon-peixunkaoshi', event: () => router.push(`/exam/mark/${exam.id}`) },
        { name: '统计', icon: 'icon-statistics', event: () => router.push(`/exam/statis/${exam.id}`) },
    ]

    if (exam.markState === 3) {// 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
        return menu
    }
    
    let startTime = dayjs(exam.startTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    let endTime = dayjs(exam.endTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    if (startTime.getTime() > curTime.value.getTime()) {
        menu.push({ name: '立即开始考试', icon: 'icon-count-down', event: () => setTime(exam, 1) }) 
        menu.push({ name: '协助阅卷', icon: 'icon-persons', event: () => router.push(`/exam/markUser/${exam.id}`) }) 
        return menu
    }
    if (endTime.getTime() > curTime.value.getTime()) {
        menu.push({ name: '立即完成考试', icon: 'icon-count-down', event: () => setTime(exam, 2) }) 
        menu.push({ name: '协助阅卷', icon: 'icon-persons', event: () => router.push(`/exam/markUser/${exam.id}`) }) 
        return menu
    }

    if (exam.markType === 2) {// 阅卷方式（1：客观题；2：主观题；）
        let markStartTime = dayjs(exam.markStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()
        let markEndTime = dayjs(exam.markEndTime, 'YYYY-MM-DD HH:mm:ss').toDate()
        if (markStartTime.getTime() > curTime.value.getTime()) {
            menu.push({ name: '立即开始阅卷', icon: 'icon-count-down', event: () => setTime(exam, 3) }) 
            menu.push({ name: '协助阅卷', icon: 'icon-persons', event: () => router.push(`/exam/markUser/${exam.id}`) }) 
            return menu
        }
        if (markEndTime.getTime() > curTime.value.getTime()) {
            menu.push({ name: '立即完成阅卷', icon: 'icon-count-down', event: () => setTime(exam, 4) }) 
            menu.push({ name: '协助阅卷', icon: 'icon-persons', event: () => router.push(`/exam/markUser/${exam.id}`) }) 
            return menu
        }
    }

    return menu
}

// 设置考试时间
async function setTime(exam: any, timeType: number) {
    let { data: { code } } = await http.post("exam/time", {
        id: exam.id,
        timeType: timeType,
        minute: -525600//-0x7fffffff int最小值边界有bug，后端时间计算错误，改成最多减一年
    })

    if (code !== 200) {
        return
    }
    if (timeType === 2 || timeType === 4) {
        ElMessage.success('后台正在阅卷，请稍后查询')
    } else {
        ElMessage.success('设置成功')
    }

    setTimeout(query, 2000) 
}
</script>

<style lang="scss" scoped>
.list {
    display: flex;
    flex-wrap: wrap;
    align-content: flex-start;
    .grid {
        height: 200px;
    }
}
</style>
  