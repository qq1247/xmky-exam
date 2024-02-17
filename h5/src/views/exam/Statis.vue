<template>
    <div class="statis">
        <div class="statis-top">
            <el-card class="statis-top-base" shadow="never">
                <template #header>基础信息</template>
                <p>考试名称：<span>{{exam.name}}</span></p>
                <p>试卷类型：<span>{{dictStore.getValue('PAPER_GEN_TYPE', exam.genType)}} / {{ dictStore.getValue('PAPER_MARK_TYPE', exam.markType) }}</span></p>
                <p>考试时间：<span>{{dayjs(exam.startTime).format('YY-MM-DD HH:mm')}}（{{ examMinutes }}分钟）</span></p>
                <p>阅卷时间：<span>{{exam.markType === 2 ? dayjs(exam.markStartTime).format('YY-MM-DD HH:mm') : '-'}} （{{ markMinutes }}分钟）</span></p>
                <p>及格分数：<span>{{exam.passScore}}分 / {{exam.totalScore}}分</span></p>
            </el-card>
            <el-card class="statis-top-statis" shadow="never">
                <template #header>统计信息</template>
                <div v-if="exam.markState === 3">
                    <p>应考人数：<span>{{ `${ statis.userNum }人`}}</span></p>
                    <p>未考人数：<span>{{ `${ statis.missUserNum }人`}}</span></p>
                    <p>挂科人数：<span>{{ `${ statis.failUserNum }人`}}</span></p>
                    <p>试题数量：<span>{{ `${ statis.questionNum }道`}}</span></p>
                    <p>客观题数：<span>{{ `${ statis.objectiveQuestionNum }道`}}</span></p>
                </div>
                <div v-if="exam.markState === 3">
                    <p>最高分数：<span>{{ exam.markState === 3 ? statis.maxScore + '分' : '-' }}</span></p>
                    <p>最低分数：<span>{{ exam.markState === 3 ? statis.minScore + '分' : '-' }}</span></p>
                    <p>平均分数：<span>{{ exam.markState === 3 ? statis.avgScore + '分' : '-' }}</span></p>
                    <p>标准偏差：<span>{{ exam.markState === 3 ? statis.sdScore : '-' }}</span></p>
                </div>
                <span class="errs" v-else>
                    {{exam.markState === 1 ? '考试未结束' :  exam.markState === 2 ? '阅卷未结束' :  ''}}
                </span>
            </el-card>
            <el-card class="statis-top-charts" shadow="never">
                <template #header>试题类型统计</template>
                <div v-if="exam.markState === 3" id="statis-type" style="width: 280px; height: 150px;"></div>
                <span class="errs" v-else>
                    {{exam.markState === 1 ? '考试未结束' :  exam.markState === 2 ? '阅卷未结束' :  ''}}
                </span>
            </el-card>
            <el-card class="statis-top-charts" shadow="never">
                <template #header>分数段统计</template>
                <div v-if="exam.markState === 3" id="statis-score" style="width: 280px; height: 140px;"></div>
                <span class="errs" v-else>
                    {{exam.markState === 1 ? '考试未结束' :  exam.markState === 2 ? '阅卷未结束' :  ''}}
                </span>
            </el-card>
        </div>
        <el-card class="statis-buttom" shadow="never">
            <el-tabs v-model="curTab" >
                <el-tab-pane label="考试排名" name="examRank">
                    <el-table :data="examRankListpage.list" size="small" row-key="id" stripe max-height="calc(100vh - 500px)">
                        <el-table-column prop="" label="排名" align="center">
                            <template #default="scope">
                                {{ scope.row.myExamNo || '-' }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="userName" label="姓名" align="center"/>
                        <el-table-column prop="orgName" label="机构" align="center"/>
                        <el-table-column prop="" label="分数" align="center">
                            <template #default="scope">
                                <span :style="{ color: scope.row.myExamAnswerState === 2 ? '#f56c6c' : '#67c23a'}">
                                    {{ scope.row.myExamTotalScore != null ? scope.row.myExamTotalScore : '-'}}
                                </span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="" label="考试状态" align="center">
                            <template #default="scope">{{ dictStore.getValue('EXAM_STATE', scope.row.myExamState)}}</template>
                        </el-table-column>
                        <el-table-column prop="" label="阅卷状态" align="center">
                            <template #default="scope">{{ dictStore.getValue('MARK_STATE', scope.row.myExamMarkState)}}</template>
                        </el-table-column>
                        <el-table-column prop="" label="答题用时" align="center">
                            <template #default="scope">
                                {{ scope.row.myExamEndTime &&  scope.row.myExamStartTime
                                    ? Math.ceil((dayjs(scope.row.myExamEndTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime() 
                                        - dayjs(scope.row.myExamStartTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime()) / (60 * 1000)) + '分钟'
                                    : '-'}}
                            </template>
                        </el-table-column>
                        <el-table-column prop="no" label="阅卷用时" align="center">
                            <template #default="scope">
                                {{ scope.row.myExamMarkEndTime &&  scope.row.myExamMarkStartTime
                                    ? Math.ceil((dayjs(scope.row.myExamMarkEndTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime() 
                                        - dayjs(scope.row.myExamMarkStartTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime()) / (60 * 1000)) + '分钟'
                                    : '-'}}
                            </template>
                        </el-table-column>
                        <el-table-column prop="" label="操作" align="center" width="300">
                            <template #default="scope">
                                <el-tooltip ffect="dark" content="预览">
                                    <Iconfont icon="icon-search" :size="18" color="#409eff"
                                        @click="$router.push(`/exam/paper/${scope.row.examId}/${scope.row.userId}`)"></Iconfont>
                                </el-tooltip>
                            </template>
                        </el-table-column> 
                    </el-table>
                    <el-pagination 
                        v-model:current-page="examRankListpage.curPage" 
                        v-model:page-size="examRankListpage.pageSize" 
                        :total="examRankListpage.total" 
                        background
                        layout="prev, pager, next" 
                        :hide-on-single-page="true" 
                        @size-change="examRankQuery"
                        @current-change="examRankQuery"
                        @prev-click="examRankQuery"
                        @next-click="examRankQuery"
                    />
                </el-tab-pane>
                <!-- <el-tab-pane label="错题分析" name="questionErr">
                    <el-table :data="questionErrList" size="small" row-key="id" stripe max-height="calc(100vh - 500px)">
                        <el-table-column prop="questionId" label="编号" align="center" width="100"/>
                        <el-table-column prop="questionTitle" label="题干" align="center"/>
                        <el-table-column prop="no" label="阅卷用时" align="center" width="100">
                            <template #default="scope">
                                {{ Math.round((scope.row.succUserNum / scope.row.userNum) * 100) }}%
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane> -->
            </el-tabs>
        </el-card>
    </div>
</template>
  
<script lang="ts" setup>
import * as echarts from 'echarts/core';
import { computed, onMounted, reactive, ref } from 'vue';
import http from '@/request';
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import { useDictStore } from '@/stores/dict';

// 定义变量
const route = useRoute()
const dictStore = useDictStore()// 字典缓存
const curTab = ref('examRank') // 当前标签页（默认考试排名）
const exam = reactive({// 考试信息
    id: 0, // 考试ID
    name: '',// 考试名称
    markType: 0,// 阅卷类型
    genType: 0,// 生成类型
    markState: 0,// 阅卷状态
    startTime: new Date(),// 考试开始时间
    endTime: new Date(),// 考试结束时间
    markStartTime: new Date(),// 阅卷开始时间
    markEndTime: new Date(),// 阅卷结束时间
    passScore: null,// 及格分数
    totalScore: null,// 试卷总分
})
const statis = reactive({// 统计信息
    userNum: 0,// 应考人数
    missUserNum: 0,// 未考人数
    failUserNum: 0,// 挂科人数
    questionNum: 0,// 试题数量
    objectiveQuestionNum: 0,// 客观题数
    avgScore: 0,// 最高分数
    minScore: 0,// 最低分数
    maxScore: 0,// 平均分数
    sdScore: 0,// 标准差值
    questionTypeList: [] as any[], // 试题类型数据
    scoreGradeList: [] as any[], // 分数段数据
})

const examRankListpage = reactive({// 考试排名分页列表
    curPage: 1,
    pageSize: 10,
    total: 0,
    list: [] as any[],
})
const questionErrList = ref([] as any)// 错题分析

// 计算属性
const examMinutes = computed(() => {// 考试分钟数
    return Math.ceil((exam.endTime.getTime() - exam.startTime.getTime()) / ( 60 * 1000))
})
const markMinutes = computed(() => {// 阅卷分钟数
    if (exam.markType === 1) {
        return null
    }
    return Math.ceil((exam.markEndTime.getTime() - exam.markStartTime.getTime()) / ( 60 * 1000))
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    exam.id = parseInt(route.params.id as string)
    await examQuery() // 这里保持同步，下面用到
    await examRankQuery()// 考试未结束也允许查看
    if (exam.markState === 3) {
        await examStatisQuery()
        statisForTypeInit()
        statisForScoreInit()
        // uestionErrListQuery()
    }
})

// 考试查询
async function examQuery() {
    const { data: { data } } = await http.post('exam/get', {
        id: exam.id
    })
    exam.name = data.name
    exam.markType = data.markType
    exam.genType = data.genType
    exam.markState = data.markState
    exam.startTime = dayjs(data.startTime, "YYYY-MM-DD HH:mm:ss").toDate()
    exam.endTime = dayjs(data.endTime, "YYYY-MM-DD HH:mm:ss").toDate()
    exam.markStartTime = exam.markType === 2 ? dayjs(data.markStartTime, "YYYY-MM-DD HH:mm:ss").toDate() : new Date()
    exam.markEndTime = exam.markType === 2 ? dayjs(data.markEndTime, "YYYY-MM-DD HH:mm:ss").toDate() : new Date()
    exam.passScore = data.passScore
    exam.totalScore = data.totalScore
}

// 考试统计查询
async function examStatisQuery() {
    const { data: { data } } = await http.post('report/exam/statis', {
        examId: exam.id
    })
    statis.userNum = data.userNum
    statis.missUserNum = data.missUserNum
    statis.failUserNum = data.failUserNum
    statis.questionNum = data.questionNum
    statis.objectiveQuestionNum = data.objectiveQuestionNum
    statis.avgScore = data.avgScore
    statis.minScore = data.minScore
    statis.maxScore = data.maxScore
    statis.sdScore = data.sdScore
    statis.questionTypeList = data.questionTypeList
    statis.scoreGradeList = data.scoreGradeList
}

// 初始化试题占比图
function statisForTypeInit() {
    let chartDom = document.getElementById('statis-type') as HTMLElement;
    let myChart = echarts.init(chartDom);
    let option = {
        title: {
            text: '',
            subtext: '',
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        series: [
            {
                name: '试题数量',
                type: 'pie',
                radius: '50%',
                data: statis.questionTypeList,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    myChart.setOption(option);
}

// 初始化分数段图
function statisForScoreInit() {
    var chartDom = document.getElementById('statis-score') as HTMLElement;
    var myChart = echarts.init(chartDom);
    var option;

    option = {
        title: {
            text: '',
            subtext: '',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: statis.scoreGradeList.map(scoreGrade => scoreGrade.name),
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: '人数',
                type: 'bar',
                barWidth: '60%',
                data: statis.scoreGradeList.map(scoreGrade => scoreGrade.value),
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#83bff6' },
                        { offset: 0.5, color: '#188df0' },
                        { offset: 1, color: '#188df0' }
                    ])
                },
                emphasis: {
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#2378f7' },
                            { offset: 0.7, color: '#2378f7' },
                            { offset: 1, color: '#83bff6' }
                        ])
                    }
                },
            }
        ]
    };

    option && myChart.setOption(option);
}

// 排名查询
async function examRankQuery() {
    const { data: { code, data } } = await http.post('report/exam/rankListpage', {
        examId: exam.id,
        curPage: examRankListpage.curPage,
        pageSize: examRankListpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    examRankListpage.list = data.list
    examRankListpage.total = data.total
}

// 查询错题
async function uestionErrListQuery() {
    const { data: { code, data } } = await http.post('report/exam/questionErrList', {
        examId: exam.id
    })

    if (code !== 200) {
        return
    }

    questionErrList.value = data
}
</script>
  
<style lang="scss" scoped>
.statis {
    display: flex;
    flex-direction: column;
    height: 100%;
    .statis-top {
        display: flex;
        .errs {
            padding: 15px 0px 0px 15px;
            font-size: 12px;
            font-weight: bold;
            color: var(--el-text-color-regular);
            display: inline-block;
        }
        :deep(.el-card) {
            flex: 1;
            margin: 0px 5px 5px 5px;
            position: relative;// 用于header左侧的竖条

            .el-card__header {
                padding: 15px 15px 15px 30px;
                font-size: 14px;
                font-weight: bold;
            }

            .el-card__header::after {
                content: "";
                display: block;
                width: 4px;
                height: 16px;
                background: #409EFF;
                position: absolute;
                top: 15px;
                left: 15px;
            }
            .el-card__body { 
                padding: 0px;
            }
        }
        :deep(.statis-top-base) {
            .el-card__body {
                font-size: 12px;
                font-weight: bold;
                color: var(--el-text-color-regular);
                p {
                    margin: 8px 15px;
                    span {
                        font-size: 13px;
                        color: var(--el-color-primary);
                        margin-left: 5px;
                        display: inline-block;
                        width: 180px;
                        word-break: keep-all;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                        overflow: hidden;
                        vertical-align: middle;
                    }
                }
                p:first-child {
                    margin-top: 15px;
                }
            }
        }
        :deep(.statis-top-statis) {
            .el-card__body {
                display: flex;
                font-size: 12px;
                font-weight: bold;
                color: var(--el-text-color-regular);
                div {
                    flex: 1;
                    p {
                        margin: 8px 15px;
                        span {
                            font-size: 13px;
                            color: var(--el-color-primary);
                            margin-left: 5px;
                            vertical-align: middle;
                        }
                    }
                    p:first-child {
                        margin-top: 15px;
                    }
                }
            }
        }
    }
    :deep(.statis-buttom) {
        margin: 5px 5px 15px 5px;
        .errs {
            padding: 15px 0px 0px 15px;
            font-size: 12px;
            font-weight: bold;
            color: var(--el-text-color-regular);
            display: inline-block;
        }
        .el-card__body {
            padding-top: 10px;
            .el-pagination {
                margin-top: 10px;
            }
        }
    }
}
</style>