<template>
    <template v-if="$route.path === '/myMark'">
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
                v-for="myMark in listpage.list" 
                :menu="[
                    {   name: `${myMark.examMarkState === 3 ? '查阅试卷' : '进入阅卷'}`, 
                        icon: `${myMark.examMarkState === 3 ? 'icon-search' : 'icon-peixunkaoshi'}`, 
                        event: () => markIn(myMark)
                    },
                    ]" 
                >
                <template #tag>
                    <CountDown v-if="myMark.examMarkState !== 3" :expireTime="dayjs(myMark.examMarkStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()" preTxt="距阅卷："></CountDown>
                    <template v-else>
                        <el-tag type="success" size="small">{{ dictStore.getValue("MARK_STATE", myMark.examMarkState) }}</el-tag>
                    </template>
                </template>
                <template #title>
                    {{ myMark.examName }}
                </template>
                <template #content>
                    <div style="margin-bottom: 5px;text-align: center;">
                        考试时间：{{ myMark.examStartTime }} - {{ myMark.examEndTime }}
                    </div>
                    <div v-if="myMark.examMarkType === 2" style="margin-bottom: 5px;text-align: center;">
                        阅卷时间：{{ myMark.examMarkStartTime }} - {{ myMark.examMarkEndTime }}
                    </div>
                    <el-row style="margin-bottom: 5px;">
                        <el-col :span="8">
                            及格分数：{{ myMark.examPassScore || '-' }} / {{ myMark.examTotalScore }}
                        </el-col>
                        <el-col :span="8">
                            <!-- 待批试卷：6 / 79 -->
                            考试人数：{{ myMark.examUserNum }}
                        </el-col>
                        <el-col :span="8">
                            协助批阅：{{ myMark.examMarkUserNum || '无'}}
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="10">
                            成绩查询：{{ dictStore.getValue('SCORE_STATE', myMark.examScoreState) }}
                        </el-col>
                        <el-col :span="7">
                            排名：{{ dictStore.getValue('STATE_ON', myMark.examRankState) }}
                        </el-col>
                        <el-col :span="7">
                            防作弊：{{ myMark.examSxes.length > 0 ? '是' : '否' }}
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
    if (n === '/myMark') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('myMark/listpage', {
        examName: queryForm.examName,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    listpage.list = data.list.map((myMark : any) => {
        return myMark
    })
    listpage.total = data.total
}

// 考试进入
async function markIn(myMark: any) {
    if (myMark.state !== 3) {
        let { data: { data } } = await http.post("login/sysTime", {  })
        let curTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
        let examStartTim = dayjs(myMark.examStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()
        if (examStartTim.getTime() > curTime.getTime()) {
            ElMessage.error('考试未开始，请等待...')
            return
        }
    }

    router.push(`/myMark/paper/${myMark.examId}`)
    // if (screenfull.isEnabled) {
    //     screenfull.request();
    //     screenfull.onchange((e) => {
    //         if(!screenfull.isFullscreen) {
    //             router.push(`/myMark`)
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
  