<template>
    <div class="exam-statis">
        <!-- <div class="exam-statis__head">
        </div> -->
        <el-scrollbar max-height="calc(100vh - 110px)" class="exam-statis__main">
            <div class="statis-data">
                <div class="statis-data__head">考试统计</div>
                <div class="statis-data__main">
                    <span v-if="exam.markState !== 3" class="statis-data__msg">考试未结束</span>
                    <div v-if="exam.markState === 3" class="statis-num">
                        <div class="statis-num__head">统计信息</div>
                        <div class="statis-num__main">
                            <div class="statis-num__inner">
                                <span class="statis-num__label statis-num__label--green">应考人数</span>
                                <span class="statis-num__value statis-num__value--green">
                                    {{ statis.userNum }}<span class="statis-num__unit">人</span>
                                </span>
                            </div>
                            <div class="statis-num__inner">
                                <span class="statis-num__label statis-num__label--blue">未考人数</span>
                                <span class="statis-num__value statis-num__value--blue">
                                    {{ statis.missUserNum }}<span class="statis-num__unit">人</span>
                                </span>
                            </div>
                            <div class="statis-num__inner">
                                <span class="statis-num__label statis-num__label--red">挂科人数</span>
                                <span class="statis-num__value statis-num__value--red">
                                    {{ statis.failUserNum }}<span class="statis-num__unit">人</span>
                                </span>
                            </div>
                            <div class="statis-num__inner">
                                <span class="statis-num__label statis-num__label--green">最高分数</span>
                                <span class="statis-num__value statis-num__value--green">
                                    {{ statis.maxScore }}<span class="statis-num__unit">分</span>
                                </span>
                            </div>
                            <div class="statis-num__inner">
                                <span class="statis-num__label statis-num__label--blue">平均分数</span>
                                <span class="statis-num__value statis-num__value--blue">
                                    {{ statis.avgScore }}<span class="statis-num__unit">分</span>
                                </span>
                            </div>
                            <div class="statis-num__inner">
                                <span class="statis-num__label statis-num__label--red">最低分数</span>
                                <span class="statis-num__value statis-num__value--red">
                                    {{ statis.minScore }}<span class="statis-num__unit">分</span>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div v-if="exam.markState === 3" class="statis-question">
                        <div class="statis-question__head">试题类型统计</div>
                        <div class="statis-question__main">
                            <div class="statis-question__inner">
                                <!-- <span class="statis-question__label">主观题：<span
                                        class="statis-question__value">13</span>道</span>
                                <span class="statis-question__label">客观题：<span
                                        class="statis-question__value">13</span>道</span> -->
                            </div>
                            <div class="statis-question__chart">
                                <v-chart :option="questionStatisOpts" />
                            </div>
                        </div>
                    </div>
                    <div v-if="exam.markState === 3" class="statis-score">
                        <div class="statis-score__head">分数段统计</div>
                        <div class="statis-score__main">
                            <v-chart :option="scoreStatisOpts" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="list-data">
                <div class="list-data__head">考试数据</div>
                <div class="list-data__main">
                    <el-tabs v-model="curTab" class="tabs">
                        <el-form :inline="true" size="large" class="query">
                            <el-form-item label="">
                                <el-input v-model="examRankForm.userName" placeholder="请输入姓名" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" class="query__btn" @click="examRankQuery">查询</el-button>
                                <el-button v-if="exam.markState === 3" type="success"
                                    class="query__btn query__btn--secondary" @click="examRankExport">
                                    <span class="iconfont icon-xiazaimoban query__btn-icon"></span>
                                    <span class="query__btn-txt">排名导出</span>
                                </el-button>
                            </el-form-item>
                        </el-form>
                        <el-tab-pane label="考试排名" name="examRank">
                            <el-table :data="examRankListpage.list" size="small" row-key="id" height="450px"
                                :row-class-name="setRowClassName" class="table">
                                <el-table-column prop="" label="排名" align="center">
                                    <template #default="scope">
                                        {{ scope.row.myExamNo || '-' }}
                                    </template>
                                </el-table-column>
                                <el-table-column prop="userName" label="姓名" align="center" />
                                <el-table-column prop="orgName" label="机构" align="center" />
                                <el-table-column prop="" label="分数" align="center">
                                    <template #default="scope">
                                        <span
                                            :style="{ color: scope.row.myExamAnswerState === 2 ? '#f56c6c' : '#67c23a' }">
                                            {{ scope.row.myExamTotalScore != null ? scope.row.myExamTotalScore : '-' }}
                                        </span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="" label="考试状态" align="center">
                                    <template #default="scope">{{ dictStore.getValue('EXAM_STATE',
                                        scope.row.myExamState) }}</template>
                                </el-table-column>
                                <el-table-column prop="" label="阅卷状态" align="center">
                                    <template #default="scope">{{ dictStore.getValue('MARK_STATE',
                                        scope.row.myExamMarkState) }}</template>
                                </el-table-column>
                                <el-table-column prop="" label="答题用时" align="center">
                                    <template #default="scope">
                                        {{ diff(scope.row.myExamAnswerStartTime, scope.row.myExamAnswerEndTime) }}分钟
                                    </template>
                                </el-table-column>
                                <el-table-column prop="no" label="阅卷用时" align="center">
                                    <template #default="scope">
                                        {{ diff(scope.row.myExamStartMarkime, scope.row.myExamMarkEndTTime) }}分钟
                                    </template>
                                </el-table-column>
                                <el-table-column prop="" label="操作" align="center" width="300">
                                    <template #default="scope">
                                        <el-button v-if="scope.row.myExamMarkState === 3" type="" link
                                            @click="toPaper(scope.row.examId, scope.row.userId)"
                                            class="table__link">预览</el-button>
                                        <el-button v-if="scope.row.myExamMarkState === 3" type="" link
                                            @click="toPDF(scope.row.examId, scope.row.userId)"
                                            class="table__link">导出PDF</el-button>
                                    </template>
                                </el-table-column>
                            </el-table>
                            <el-pagination v-model:current-page="examRankListpage.curPage"
                                v-model:page-size="examRankListpage.pageSize" :total="examRankListpage.total" background
                                layout="prev, pager, next" :hide-on-single-page="false" size="large" class="pagination"
                                @size-change="examRankQuery" @current-change="examRankQuery" @prev-click="examRankQuery"
                                @next-click="examRankQuery" />
                        </el-tab-pane>
                    </el-tabs>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import type { Exam } from '@/ts/exam/exam'
import { examGet } from '@/api/exam/exam'
import { useDictStore } from '@/stores/dict'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts';
import { GridComponent } from 'echarts/components';
import { reportExamRankListpage, reportExamStatis } from '@/api/report/report'
import { diff } from '@/util/timeUtil'
import type { Listpage } from '@/ts/common/listpage'
import http from "@/request"
import { ElMessage } from 'element-plus'

/************************变量定义相关***********************/
use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent, BarChart, GridComponent]);
const route = useRoute()// 路由
const dictStore = useDictStore()// 字典缓存
const curTab = ref('examRank') // 当前标签页（默认考试排名）
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
})

const examRankForm = reactive({// 考试排名查询表单
    examId: route.params.id,
    userName: '',
})

const examRankListpage = reactive<Listpage>({// 考试排名分页列表
    curPage: 1,
    pageSize: 100,
    total: 0,
    list: [],
})
const exam = reactive<Exam>({ // 考试
    id: null,
    name: '',
    paperName: '',
    startTime: '',
    endTime: '',
    markStartTime: '',
    markEndTime: '',
    markState: null,
    scoreState: null,
    rankState: null,
    passScore: null,
    totalScore: null,
    markType: null,
    genType: null,
    loginType: null,
    sxes: [],
    state: null,
    userNum: null,
    limitMinute: null
})
const questionStatisOpts = ref({// 试题统计数据
    title: {
        text: '',
        left: 'center',
    },
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)',
    },
    series: [
        {
            name: '试题统计',
            type: 'pie',
            radius: '100%',
            center: ['50%', '50%'],
            data: [

            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
            },
            color: [
                '#4692D8', '#06BCE3', '#978CEC', '#DD8CEC', '#85e3a4'
            ],
        },
    ],
})
const scoreStatisOpts = ref({// 分数统计数据
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        },
        formatter: '{a} <br/>{b}分 ({c}人)',
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
            data: ['<20', '<40', '<60', '<80', '<=100'],
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
            name: '分数段统计',
            type: 'bar',
            barWidth: '8',
            data: [

            ],
            itemStyle: {
                color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 1,
                    y2: 1,
                    colorStops: [{
                        offset: 0, color: '#04C7F2'
                    }, {
                        offset: 1, color: '#259FF8'
                    }],
                },
                borderRadius: [4, 4, 0, 0]
            }
        }
    ]
})


/************************组件生命周期相关*********************/
onMounted(async () => {
    await examQuery() // 这里保持同步，下面用到
    await examRankQuery()// 考试未结束也允许查看
    if (exam.markState === 3) {
        await examStatisQuery()
    }
})

/************************事件相关*****************************/

// 考试查询
async function examQuery() {
    const { data: { data } } = await examGet({ id: route.params.id })
    exam.id = data.id
    exam.name = data.name
    exam.genType = data.genType
    exam.markType = data.markType
    exam.loginType = data.loginType
    exam.startTime = data.startTime
    exam.endTime = data.endTime
    exam.limitMinute = data.limitMinute
    exam.markStartTime = data.markStartTime
    exam.markEndTime = data.markEndTime
    exam.markState = data.markState
    exam.scoreState = data.scoreState
    exam.rankState = data.rankState
    exam.passScore = data.passScore
    exam.totalScore = data.totalScore
    exam.sxes = data.sxes
    exam.userNum = data.userNum
    exam.state = data.state
}
// 考试统计查询
async function examStatisQuery() {
    const { data: { data } } = await reportExamStatis({
        examId: route.params.id
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

    questionStatisOpts.value.series[0].data = data.questionTypeList
    scoreStatisOpts.value.xAxis[0].data = data.scoreGradeList.map((scoreGrade: any) => scoreGrade.name)
    scoreStatisOpts.value.series[0].data = data.scoreGradeList.map((scoreGrade: any) => scoreGrade.value)
}

// 排名查询
async function examRankQuery() {
    const { data: { code, data } } = await reportExamRankListpage({
        ...examRankForm,
        curPage: examRankListpage.curPage,
        pageSize: examRankListpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    examRankListpage.list = data.list
    examRankListpage.total = data.total
}

// 考试排名导出
async function examRankExport() {
    let downloadLink = null;
    let objectUrl = null;
    try {
        ElMessage.info('正在生成PDF，请稍后...')
        const data = await http.post('report/rank/exportPDF', { examId: examRankForm.examId }, { responseType: 'blob' })
        objectUrl = URL.createObjectURL(data.data)

        const downloadLink = document.createElement('a');
        downloadLink.download = decodeURIComponent(data.headers['content-disposition'].substr(20)) // attachment; filename="example.pdf
        downloadLink.style.display = 'none'
        downloadLink.href = objectUrl;
        downloadLink.click();
        URL.revokeObjectURL(downloadLink.href);
    } catch (error) {
        ElMessage.error('生成PDF失败：' + error,)
    } finally {
        ElMessage.success('下载完成')
        if (downloadLink) {
            document.removeChild(downloadLink);
            downloadLink = null;
        }
        if (objectUrl) {
            URL.revokeObjectURL(objectUrl);
            objectUrl = null;
        }
    }

}

function toPaper(examId: number, examUserId: number) {
    window.open(`/paper/${examId}/${examUserId}`, '_blank')
}

async function toPDF(examId: number, userId: number) {
    let downloadLink = null;
    let objectUrl = null;
    try {
        ElMessage.info('正在生成PDF，请稍后...')
        const data = await http.post('report/paper/exportPDF', { examId, userId }, { responseType: 'blob' })
        objectUrl = URL.createObjectURL(data.data)

        const downloadLink = document.createElement('a');
        downloadLink.download = decodeURIComponent(data.headers['content-disposition'].substr(20))
        downloadLink.style.display = 'none'
        downloadLink.href = objectUrl;
        downloadLink.click();
        URL.revokeObjectURL(downloadLink.href);
    } catch (error) {
        ElMessage.error('生成PDF失败：' + error,)
    } finally {
        ElMessage.success('下载完成')
        if (downloadLink) {
            document.removeChild(downloadLink);
            downloadLink = null;
        }
        if (objectUrl) {
            URL.revokeObjectURL(objectUrl);
            objectUrl = null;
        }
    }
}

function setRowClassName({ rowIndex }: { rowIndex: number }) {
    if (rowIndex < 3 && examRankListpage.curPage === 1) {
        console.log(rowIndex, examRankListpage.curPage)
        return `row${rowIndex + 1}`;
    }
    return '';
}
</script>

<style lang="scss" scoped>
.exam-statis {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .exam-statis__main {
        border-radius: 15px 15px 15px 15px;

        .statis-data {
            padding: 30px;
            border-radius: 15px 15px 15px 15px;
            background-color: #FFFFFF;

            .statis-data__head {
                font-size: 20px;
                color: #333333;
                line-height: 45px;
                border-bottom: 1px solid #F2F2F2;
            }

            .statis-data__main {
                display: flex;

                .statis-data__msg {
                    line-height: 50px;
                }

                .statis-num {
                    display: flex;
                    flex-direction: column;
                    width: 400px;

                    .statis-num__head {
                        font-size: 16px;
                        color: #333333;
                        line-height: 45px;
                        position: relative;
                        padding-left: 15px;

                        &::before {
                            content: '';
                            position: absolute;
                            display: inline-block;
                            width: 4px;
                            height: 16px;
                            background: #1EA1EE;
                            border-radius: 2px 2px 2px 2px;
                            left: 0px;
                            top: 50%;
                            transform: translateY(-50%);

                        }
                    }

                    .statis-num__main {
                        display: grid;
                        grid-template-columns: repeat(3, 1fr);
                        grid-template-rows: repeat(2, 1fr);
                        border-radius: 15px 15px 15px 15px;
                        overflow: hidden;
                        gap: 10px 0px;
                        height: 234px;

                        .statis-num__inner {
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;

                            background: #F2F6F9;
                            position: relative;

                            &::after {
                                content: "";
                                position: absolute;
                                display: block;
                                right: 0;
                                width: 1px;
                                height: 50px;
                                background-color: #E5E5E5;
                            }

                            &:last-child {
                                &::after {
                                    display: none;
                                }
                            }

                            &:nth-last-child(4) {
                                &::after {
                                    display: none;
                                }
                            }

                            .statis-num__label {
                                font-size: 12px;
                                color: #999999;
                                position: relative;
                                margin: 10px 0px;

                                &::before {
                                    content: "";
                                    position: absolute;
                                    display: block;
                                    left: -12px;
                                    top: 50%;
                                    transform: translateY(-50%);
                                    width: 6px;
                                    height: 6px;
                                    border-radius: 50%;
                                }

                                &.statis-num__label--green {
                                    &::before {
                                        background-color: #18BC37;
                                    }
                                }

                                &.statis-num__label--blue {
                                    &::before {
                                        background-color: #1EA1EE;
                                    }
                                }

                                &.statis-num__label--red {
                                    &::before {
                                        background-color: #E43D33;
                                    }
                                }
                            }

                            .statis-num__value {
                                font-weight: bold;
                                font-size: 24px;
                                color: #18BC37;

                                &.statis-num__value--green {
                                    color: #18BC37
                                }

                                &.statis-num__value--blue {
                                    color: #1EA1EE
                                }

                                &.statis-num__value--red {
                                    color: #E43D33
                                }


                                .statis-num__unit {
                                    color: #999999;
                                    font-size: 10px;
                                }
                            }
                        }
                    }
                }

                .statis-question {
                    display: flex;
                    flex-direction: column;
                    width: 400px;
                    padding: 0px 40px 0px 40px;

                    .statis-question__head {
                        font-size: 16px;
                        color: #333333;
                        line-height: 45px;
                        position: relative;
                        padding-left: 15px;

                        &::before {
                            content: '';
                            position: absolute;
                            display: inline-block;
                            width: 4px;
                            height: 16px;
                            background: #1EA1EE;
                            border-radius: 2px 2px 2px 2px;
                            left: 0px;
                            top: 50%;
                            transform: translateY(-50%);

                        }
                    }

                    .statis-question__main {
                        flex: 1;
                        display: flex;
                        flex-direction: column;

                        .statis-question__inner {
                            display: flex;
                            margin-bottom: 20px;

                            .statis-question__label {
                                font-size: 12px;
                                color: #999999;
                                margin-right: 50px;

                                .statis-question__value {
                                    margin-right: 2px;
                                    font-size: 16px;
                                    color: #1EA1EE;
                                }
                            }
                        }

                        .statis-question__chart {
                            padding: 10px;
                            flex: 1;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                        }
                    }
                }

                .statis-score {
                    flex: 1;
                    display: flex;
                    flex-direction: column;

                    .statis-score__head {
                        font-size: 16px;
                        color: #333333;
                        line-height: 45px;
                        position: relative;
                        padding-left: 15px;

                        &::before {
                            content: '';
                            position: absolute;
                            display: inline-block;
                            width: 4px;
                            height: 16px;
                            background: #1EA1EE;
                            border-radius: 2px 2px 2px 2px;
                            left: 0px;
                            top: 50%;
                            transform: translateY(-50%);

                        }
                    }

                    .statis-score__main {
                        flex: 1;
                    }

                }
            }
        }

        .list-data {
            padding: 30px;
            border-radius: 15px 15px 15px 15px;
            background-color: #FFFFFF;
            margin-top: 20px;

            .list-data__head {
                font-size: 16px;
                color: #333333;
                line-height: 45px;
                position: relative;
                padding-left: 15px;

                &::before {
                    content: '';
                    position: absolute;
                    display: inline-block;
                    width: 4px;
                    height: 16px;
                    background: #1EA1EE;
                    border-radius: 2px 2px 2px 2px;
                    left: 0px;
                    top: 50%;
                    transform: translateY(-50%);

                }
            }

            .list-data__main {
                :deep(.tabs) {
                    .el-tab-pane {
                        display: flex;
                        flex-direction: column;
                        align-items: center;


                        .el-tabs__item {
                            &.is-active {
                                color: #1EA1EE;
                            }
                        }

                        .el-tabs__active-bar {
                            background-color: #1EA1EE;
                        }

                        .el-tabs__nav-wrap {
                            &:after {
                                width: initial;
                            }
                        }

                        .table {
                            .el-table__header-wrapper {
                                border-radius: 6px 6px 6px 6px;
                                border: initial;

                                th {
                                    height: 48px;
                                    background: linear-gradient(to bottom, #D4EEFE 0%, #E8F4FC 100%);
                                }
                            }

                            .el-table__body-wrapper {
                                .el-table__row {
                                    height: 38px;

                                    &.row1 {
                                        background: #FFFBED;
                                    }

                                    &.row2 {
                                        background: #E7F8F9;
                                    }

                                    &.row3 {
                                        background: #FEF0EA;
                                    }

                                    .table__link {
                                        font-size: 14px;
                                        color: #1EA1EE;
                                    }
                                }
                            }
                        }

                        .pagination {
                            margin-top: 20px;

                            .btn-prev,
                            .btn-next {
                                width: 38px;
                                height: 38px;

                                .el-icon {
                                    font-size: 20px;
                                }
                            }

                            .el-pager {
                                li {
                                    width: 38px;
                                    height: 38px;
                                    border-radius: 4px 4px 4px 4px;
                                    font-size: 16px;

                                    &.is-active {
                                        background-image: linear-gradient(to right, #04C7F2, #259FF8);
                                    }
                                }
                            }

                        }
                    }

                    .query {
                        .query__btn {
                            height: 40px;
                            padding: 0px 30px;
                            border-radius: 6px;
                            border: 0px;
                            font-size: 16px;
                            color: #FFFFFF;
                            background-image: linear-gradient(to right, #04C7F2, #259FF8);
                        }

                        .query__btn--secondary {
                            padding: 0px 25px;
                            border: 1px solid #04C7F2;
                            background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
                            margin-left: 10px;

                            .query__btn-icon {
                                color: #04C7F2;
                            }

                            .query__btn-txt {
                                color: #04C7F2;
                            }
                        }
                    }
                }
            }
        }
    }

}
</style>