<template>
    <div class="exer-statis">
        <el-scrollbar max-height="calc(100vh - 110px)" class="exer-statis__main">
            <div class="list-data">
                <div class="list-data__head">练习数据</div>
                <div class="list-data__main">
                    <el-tabs v-model="curTab" class="tabs">
                        <el-form :inline="true" size="large" class="query">
                            <!-- <el-form-item label="">
                                <el-input v-model="exerTrackForm.userName" placeholder="请输入姓名" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" class="query__btn" @click="exerTrackQuery">查询</el-button>
                            </el-form-item> -->
                        </el-form>
                        <el-tab-pane label="练习时长" name="exerTrack">
                            <el-table :data="exerTrackListpage.list" size="small" row-key="userId" height="450px"
                                class="table">
                                <el-table-column prop="userName" label="姓名" align="center" />
                                <el-table-column prop="orgName" label="机构" align="center" />
                                <el-table-column prop="" label="当月练习" align="center">
                                    <template #default="scope">{{ scope.row.tracks[scope.row.tracks.length -
                                        1].minuteCount
                                        }}分钟</template>
                                </el-table-column>
                                <el-table-column prop="" label="上月练习" align="center">
                                    <template #default="scope">{{ scope.row.tracks[scope.row.tracks.length -
                                        2].minuteCount
                                        }}分钟</template>
                                </el-table-column>
                                <el-table-column prop="" label="近一年练习" align="center">
                                    <template #default="scope">
                                        {{scope.row.tracks.reduce((sum: number, track: any) => sum + (track.minuteCount
                                            || 0), 0)}}分钟
                                    </template>
                                </el-table-column>
                                <el-table-column type="expand">
                                    <template #default="props">
                                        <v-chart :option="generateChartOption(props.row.tracks)"
                                            style="height: 100px;" />
                                    </template>
                                </el-table-column>
                            </el-table>
                            <el-pagination v-model:current-page="exerTrackListpage.curPage"
                                v-model:page-size="exerTrackListpage.pageSize" :total="exerTrackListpage.total"
                                background layout="prev, pager, next" :hide-on-single-page="true" size="large"
                                class="pagination" @size-change="exerTrackQuery" @current-change="exerTrackQuery"
                                @prev-click="exerTrackQuery" @next-click="exerTrackQuery" />
                        </el-tab-pane>
                        <el-tab-pane label="答错数量" name="wrongAnswerNumList">
                            <el-table :data="exerWrongQuestionListpage.list" size="small" row-key="userId"
                                height="450px" class="table">
                                <el-table-column prop="userName" label="姓名" align="center" />
                                <el-table-column prop="orgName" label="机构" align="center" />
                                <el-table-column prop="" label="已答错数量" align="center">
                                    <template #default="scope">{{ scope.row.wrongAnswerNum }}道</template>
                                </el-table-column>
                                <el-table-column prop="" label="已练习数量" align="center">
                                    <template #default="scope">{{ scope.row.answeredNum }}道</template>
                                </el-table-column>
                                <el-table-column prop="" label="正确率" align="center">
                                    <template #default="scope">
                                        {{
                                            scope.row.answeredNum
                                                ? _.round(((scope.row.answeredNum - scope.row.wrongAnswerNum) /
                                                    scope.row.answeredNum) * 100) + '%'
                                                : '0%'
                                        }}
                                    </template>
                                </el-table-column>
                                <el-table-column prop="" label="总题数" align="center">
                                    <template #default="scope">{{ scope.row.totalQuestionNum }}道</template>
                                </el-table-column>
                                <el-table-column prop="" label="完成率" align="center">
                                    <template #default="scope">
                                        {{
                                            scope.row.totalQuestionNum
                                                ? _.round((scope.row.answeredNum / scope.row.totalQuestionNum) * 100) + '%'
                                                : '0%'
                                        }}
                                    </template>
                                </el-table-column>
                                <el-table-column type="expand">
                                    <template #default="props">
                                        <el-table :data="props.row.questions" size="small" row-key="userId"
                                            class="sub_table">
                                            <el-table-column prop="title" label="试题题干" :show-overflow-tooltip="true" />
                                            <el-table-column prop="wrongAnswerNum" label="答错次数" align="center"
                                                width="100" />
                                        </el-table>
                                    </template>
                                </el-table-column>
                            </el-table>
                            <el-pagination v-model:current-page="exerWrongQuestionListpage.curPage"
                                v-model:page-size="exerWrongQuestionListpage.pageSize"
                                :total="exerWrongQuestionListpage.total" background layout="prev, pager, next"
                                :hide-on-single-page="true" size="large" class="pagination"
                                @size-change="exerWrongQuestionQuery" @current-change="exerWrongQuestionQuery"
                                @prev-click="exerWrongQuestionQuery" @next-click="exerWrongQuestionQuery" />
                        </el-tab-pane>
                    </el-tabs>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { reportExerTrackListpage, reportExerWrongQuestionListpage } from '@/api/report/report'
import { dayjs } from 'element-plus'
import { loginSysTime } from '@/api/login'
import { TitleComponent, TooltipComponent, LegendComponent, ToolboxComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { CanvasRenderer } from 'echarts/renderers'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import _ from 'lodash'
import type { Listpage } from '@/ts/common/listpage'

/************************变量定义相关***********************/
use([CanvasRenderer, TitleComponent, TooltipComponent, LegendComponent, ToolboxComponent, GridComponent, LineChart, UniversalTransition]);
const route = useRoute()// 路由
const curTab = ref('exerTrack') // 当前标签页（默认考试排名）

const exerTrackListpage = reactive<Listpage>({// 练习跟踪分页列表
    curPage: 1,
    pageSize: 10,
    total: 0,
    list: [],
})
const exerWrongQuestionListpage = reactive<Listpage>({// 练习跟踪分页列表
    curPage: 1,
    pageSize: 10,
    total: 0,
    list: [],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    exerTrackQuery()
    exerWrongQuestionQuery()
})

/************************事件相关*****************************/

// 练习跟踪查询
async function exerTrackQuery() {
    const { data: { data } } = await loginSysTime({})
    const startTime = dayjs(data).subtract(1, 'year').startOf('month').valueOf(); // 去年同月的第一天（去年的今天会丢数据）
    const endTime = dayjs(data).startOf('day').valueOf();// 今天

    const { data: { code, data: data1 } } = await reportExerTrackListpage({
        exerId: route.params.id,
        startYm: dayjs(startTime).format('YYYY-MM'),
        endYm: dayjs(endTime).format('YYYY-MM'),
        curPage: exerTrackListpage.curPage,
        pageSize: exerTrackListpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    exerTrackListpage.list = data1.list
    exerTrackListpage.total = data1.total
}
// 练习错题排行查询
async function exerWrongQuestionQuery() {
    const { data: { code, data } } = await reportExerWrongQuestionListpage({
        exerId: route.params.id,
        curPage: exerWrongQuestionListpage.curPage,
        pageSize: exerWrongQuestionListpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    exerWrongQuestionListpage.list = data.list
    exerWrongQuestionListpage.total = data.total
}

// 生成折线图选项
function generateChartOption(tracks: any[]) {
    const xAxisData = tracks.map(track => track.ym);
    const seriesData = tracks.map(track => track.minuteCount || 0);
    return {// 练习时长统计
        title: {
            text: '练习时长',
            left: 'center'
        },
        tooltip: {
            show: true,
            trigger: 'axis',
            formatter: '{b}<br/>练习{c}分钟'
        },
        // toolbox: {
        //     feature: {
        //         myDay: {
        //             show: true,
        //             title: '按日统计',
        //             icon: 'image://data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgZmlsbD0iIzAwMCI+CiAgPHJlY3QgeD0iMiIgeT0iNCIgd2lkdGg9IjEyIiBoZWlnaHQ9IjEyIiByeD0iMSIgcnk9IjEiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMCIgc3Ryb2tlLXdpZHRoPSIxIi8+CiAgPHJlY3QgeD0iNCIgeT0iMiIgd2lkdGg9IjIiIGhlaWdodD0iMiIgZmlsbD0iIzAwMCIvPgogIDxyZWN0IHg9IjEwIiB5PSIyIiB3aWR0aD0iMiIgaGVpZ2h0PSIyIiBmaWxsPSIjMDAwIi8+CiAgPHRleHQgeD0iOSIgeT0iMTIiIGZvbnQtc2l6ZT0iOCIgZm9udC13ZWlnaHQ9ImJvbGQiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGRvbWluYW50LWJhc2VsaW5lPSJtaWRkbGUiPuWPtzwvdGV4dD4KPC9zdmc+',
        //             onclick: function () {
        //             }
        //         },
        //         myDay2: {
        //             show: true,
        //             title: '按月统计',
        //             icon: 'image://data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgZmlsbD0iIzAwMCI+CiAgPHJlY3QgeD0iMiIgeT0iNCIgd2lkdGg9IjEyIiBoZWlnaHQ9IjEyIiByeD0iMSIgcnk9IjEiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMCIgc3Ryb2tlLXdpZHRoPSIxIi8+CiAgPHJlY3QgeD0iNCIgeT0iMiIgd2lkdGg9IjIiIGhlaWdodD0iMiIgZmlsbD0iIzAwMCIvPgogIDxyZWN0IHg9IjEwIiB5PSIyIiB3aWR0aD0iMiIgaGVpZ2h0PSIyIiBmaWxsPSIjMDAwIi8+CiAgPHRleHQgeD0iOSIgeT0iMTIiIGZvbnQtc2l6ZT0iOCIgZm9udC13ZWlnaHQ9ImJvbGQiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGRvbWluYW50LWJhc2VsaW5lPSJtaWRkbGUiPuaXnTwvdGV4dD4KPC9zdmc+',
        //             onclick: function () {
        //             }
        //         }
        //     },
        // },
        xAxis: {
            type: 'category',
            data: xAxisData
        },
        yAxis: {
            type: 'value',
        },
        series: [
            {
                data: seriesData,
                type: 'line'
            }
        ],
        grid: {
            left: '10',
            right: '10',
            top: '10',
            bottom: '0',
            containLabel: true
        },
    }

}
</script>

<style lang="scss" scoped>
.exer-statis {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .exer-statis__main {
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

                        .sub_table {
                            .el-table__header-wrapper {
                                height: 24px;
                                border-radius: 6px 6px 6px 6px;
                                border: initial;

                                th {
                                    height: 24px;
                                    background: linear-gradient(to bottom, #F5F5F5 0%, #E8F4FC 100%);
                                }
                            }

                            .el-table__body-wrapper {
                                .el-table__row {
                                    height: 19px;

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