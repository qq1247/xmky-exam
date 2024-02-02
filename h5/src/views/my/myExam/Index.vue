<template>
    <template v-if="$route.path === '/myExam'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.examName" placeholder="请输入考试名称" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
        </el-form>
        <div class="list">
            <Gridadd v-if="listpage.list.length === 0" name="暂无考试" icon="icon-dongjie"/>
            <Griddata 
                v-for="myExam in listpage.list" 
                :menu="[
                    {   name: `${myExam.state === 3 ? '查阅试卷' : '进入考试'}`, 
                        icon: `${myExam.state === 3 ? 'icon-search' : 'icon-peixunkaoshi'}`, 
                        event: () => examIn(myExam)
                    },
                    ]" 
                >
                <template #tag>
                    <CountDown v-if="myExam.state === 1 && myExam.markState === 1" :expireTime="dayjs(myExam.examStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()" preTxt="距考试："></CountDown>
                    <template v-else>
                        <el-tag size="small">{{ dictStore.getValue("EXAM_STATE", myExam.state) }}</el-tag>
                        &nbsp;<el-tag type="success" size="small">{{ dictStore.getValue("MARK_STATE", myExam.markState) }}</el-tag>
                    </template>
                </template>
                <template #title>
                    {{ myExam.examName }}
                </template>
                <template #content>
                    <div style="margin-bottom: 10px;text-align: center;">
                        考试时间：{{ myExam.examStartTime }}（{{ Math.ceil((dayjs(myExam.examEndTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime() 
                                        - dayjs(myExam.examStartTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime()) / (60 * 1000)) + '分钟' }}）
                    </div>
                    <el-row>
                        <el-col :span="8">
                            答题：{{ myExam.state === 3 
                                    ? Math.ceil((dayjs(myExam.answerEndTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime() 
                                        - dayjs(myExam.answerStartTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime()) / (60 * 1000)) + '分钟'
                                    : '-' }}
                        </el-col>
                        <el-col :span="8">
                            分数：{{ myExam.totalScore || '-' }} / {{ myExam.examTotalScore }}
                        </el-col>
                        <el-col :span="8">
                            排名：{{ myExam.no || '-' }} / {{ myExam.userNum || '-' }}
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
import { reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router'
import http from "@/request"
import dayjs from "dayjs"
import Griddata from '@/components/Griddata.vue';
import { ElMessage } from 'element-plus';
import Gridadd from '@/components/Gridadd.vue';
import { useDictStore } from '@/stores/dict';
import CountDown from '@/components/CountDown.vue';

//  定义变量
const dictStore = useDictStore() // 字典缓存
const route = useRoute()
const router = useRouter()
const queryForm = reactive({// 查询表单
    examName: '', // 考试名称
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
    if (n === '/myExam') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('myExam/listpage', {
        examName: queryForm.examName,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    listpage.list = data.list.map((myExam : any) => {
        return myExam
    })
    listpage.total = data.total
}

// 考试进入
async function examIn(myExam: any) {
    if (myExam.state !== 3) {
        let { data: { data } } = await http.post("login/sysTime", {  })
        let curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
        let examStartTim = dayjs(myExam.examStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()
        if (examStartTim.getTime() > curTime.getTime()) {
            ElMessage.error('考试未开始，请等待...')
            return
        }
    }

    router.push(`/myExam/paper/${myExam.examId}`)
    // if (screenfull.isEnabled) {
    //     screenfull.request();
    //     screenfull.onchange((e) => {
    //         if(!screenfull.isFullscreen) {
    //             router.push(`/myExam`)
    //         }
    //     })
    // }
}
</script>

<style lang="scss">
.el-header {
    display: initial;
}
</style>
<style lang="scss" scoped>
.list {
    display: flex;
    flex-wrap: wrap;
    align-content: flex-start;
    :deep(.grid-content) {
        width: 100%;
        .el-col {
            text-align: center;
        }
    }
}
</style>
  