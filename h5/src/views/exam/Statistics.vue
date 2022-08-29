<template>
  <div class="container">
    <div class="info-content">
      <el-card v-if="statisInfo.typeList" class="box-card" shadow="never">
        <div slot="header">
          <span>统计信息</span>
        </div>
        <div class="content-item">
          <template v-if="statisInfo.exam">
            <p><span>考试名称：</span>{{ statisInfo.exam.name }}</p>
            <p><span>开始时间：</span>{{ statisInfo.exam.startTime }}</p>
            <p>
              <span>考试时长：</span>{{
                timeRange(statisInfo.exam.startTime, statisInfo.exam.endTime)
              }}
            </p>
            <p><span>考试人数：</span>{{ statisInfo.exam.userNum }}</p>
            <p><span>缺考人数：</span>{{ statisInfo.exam.missUserNum }}</p>
            <p><span>合格人数：</span>{{ statisInfo.exam.succUserNum }}</p>
          </template>
          <template v-if="statisInfo.score">
            <p><span>总分数：</span>{{ statisInfo.score.total }}</p>
            <p><span>平均分：</span>{{ statisInfo.score.avg }}</p>
            <p><span>最高分：</span>{{ statisInfo.score.max }}</p>
            <p><span>最低分：</span>{{ statisInfo.score.min }}</p>
          </template>
        </div>
      </el-card>
      <el-card v-if="statisInfo.typeList" class="box-card" shadow="never">
        <div slot="header">
          <span>试题类型</span>
        </div>
        <div id="questionType" />
      </el-card>
      <el-card v-if="statisInfo.scoreGradeList" class="box-card" shadow="never">
        <div slot="header">
          <span>分数区间</span>
        </div>
        <div id="scoreRange" />
      </el-card>
    </div>
    <div class="ranking-error">
      <el-tabs v-model="tabIndex" @tab-click="handleClick">
        <el-tab-pane
          v-for="item in tabs"
          :key="item.index"
          :label="item.name"
          :name="item.index"
        />
      </el-tabs>
      <div v-show="tabIndex === '0'">
        <el-table :data="rankingList" style="width: 100%">
          <el-table-column label="排名" width="100">
            <template slot-scope="scope">
              <span>{{ scope.row.myExamNo || '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="姓名" width="100" />
          <el-table-column prop="orgName" label="组织机构" width="100" />
          <el-table-column label="考试状态" width="100">
            <template slot-scope="scope">
              <span>{{ scope.row.myExamState | examState }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷状态" width="100">
            <template slot-scope="scope">
              <span>{{ scope.row.myExamMarkState | markState }}</span>
            </template>
          </el-table-column>
          <el-table-column label="及格状态">
            <template slot-scope="scope">
              <span
                :style="{
                  color: scope.row.myExamAnswerState === 2 ? 'red' : 'black',
                }"
              >{{ scope.row.myExamAnswerState | passState }}</span>
            </template>
          </el-table-column>
          <el-table-column label="分数">
            <template slot-scope="scope">
              <span>{{ scope.row.myExamScore }}&nbsp;/&nbsp;{{
                scope.row.paperTotalScore
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="答题用时">
            <template slot-scope="scope">
              <span>{{
                timeRange(scope.row.myExamStartTime, scope.row.myExamEndTime)
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷用时">
            <template slot-scope="scope">
              <span>{{
                timeRange(
                  scope.row.myExamMarkStartTime,
                  scope.row.myExamMarkEndTime
                )
              }}</span>
            </template>
          </el-table-column>
          <el-table-column>
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="viewPaper(scope.row.userId)"
              >查看试卷</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-show="tabIndex === '1'">
        <el-table
          :data="errorList"
          style="width: 100%"
          row-key="questionId"
          :expand-row-keys="expandRowList"
          @expand-change="expandChange"
          :lazy="true"
          :tree-props="{children: 'children', hasChildren: true}">
        >
          <el-table-column type="expand">
            <template slot-scope="props">
              <div style="padding: 5px 30px 10px 50px">
                <el-table :data="props.row.children" style="width: 100%">
                  <el-table-column label="排名" width="100">
                    <template slot-scope="scope">
                      <span>{{ scope.row.myExamNo || '--' }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="userName" label="姓名" width="100" />
                  <el-table-column
                    prop="orgName"
                    label="组织机构"
                    width="100"
                  />
                  <el-table-column label="考试状态" width="100">
                    <template slot-scope="scope">
                      <span>{{ scope.row.myExamState | examState }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="阅卷状态" width="100">
                    <template slot-scope="scope">
                      <span>{{ scope.row.myExamMarkState | markState }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="及格状态">
                    <template slot-scope="scope">
                      <span
                        :style="{
                          color:
                            scope.row.myExamAnswerState === 2 ? 'red' : 'black',
                        }"
                      >{{ scope.row.myExamAnswerState | passState }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="分数">
                    <template slot-scope="scope">
                      <span>{{ scope.row.myExamScore }}&nbsp;/&nbsp;{{
                        scope.row.paperTotalScore
                      }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="答题用时">
                    <template slot-scope="scope">
                      <span>{{
                        timeRange(
                          scope.row.myExamStartTime,
                          scope.row.myExamEndTime
                        )
                      }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="阅卷用时">
                    <template slot-scope="scope">
                      <span>{{
                        timeRange(
                          scope.row.myExamMarkStartTime,
                          scope.row.myExamMarkEndTime
                        )
                      }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column>
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        @click="viewPaper(scope.row.userId)"
                      >查看试卷</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="questionId" label="编号" width="100" />
          <el-table-column label="题干">
            <template slot-scope="scope">
              <span v-html="scope.row.questionTitle" />
            </template>
          </el-table-column>
          <el-table-column width="100" label="正确率">
            <template slot-scope="scope">
              <span>{{
                Math.round((scope.row.succUserNum / scope.row.userNum) * 100)
              }}%</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="total"
        :page-size="pageSize"
        :current-page="curPage"
        @current-change="pageChange"
      />
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getExamStatis, getRanking, getQuestionError } from 'api/report'
import { getOneDict } from '@/utils/getDict'
import * as dayjs from 'dayjs'
export default {
  filters: {
    examState(data) {
      return getOneDict('MY_EXAM_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    markState(data) {
      return getOneDict('MY_EXAM_MARK_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    passState(data) {
      if (!data) {
        return '--'
      }
      return getOneDict('MY_EXAM_ANSWER_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    }
  },
  data() {
    return {
      id: null,
      total: 0,
      curPage: 1,
      pageSize: 10,
      statisInfo: {},
      errorList: [],
      rankingList: [],
      expandRowList: [],
      tabs: [
        {
          name: '用户排名',
          index: '0'
        },
        {
          name: '错题分析',
          index: '1'
        }
      ],
      tabIndex: '0'
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      await this.getExamStatis(this.id)
      this.getRanking(this.id)
      this.renderChart()
    }
  },
  methods: {
    // 考试基础信息
    async getExamStatis(examId) {
      const res = await getExamStatis({
        examId
      })
      this.statisInfo = res.data
    },
    // 用户排名
    async getRanking(examId) {
      const res = await getRanking({
        examId,
        curPage: this.curPage,
        pageSize: this.pageSize
      })
      this.rankingList = res.data.list
      this.total = res.data.total
    },
    // 错题列表
    async getQuestionError(examId) {
      const res = await getQuestionError({
        examId,
        curPage: this.curPage,
        pageSize: this.pageSize
      })
      this.errorList = res.data.list
      this.total = res.data.total
    },
    // chart共用
    chartDom(id, data) {
      const chartDom = document.getElementById(id)
      const myChart = echarts.init(chartDom)

      const legendData = data.map((item) => item.name)
      const seriesData = data.map((item) => item.value)

      const option = {
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
            data: legendData,
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
            name: '数值',
            type: 'bar',
            barWidth: '50%',
            data: seriesData
          }
        ]
      }
      option && myChart.setOption(option)
    },
    // 渲染饼图
    renderChart() {
      this.chartDom('questionType', this.statisInfo.typeList || [])
      this.chartDom('scoreRange', this.statisInfo.scoreGradeList || [])
    },
    // 标签切换
    handleClick() {
      this.curPage = 1
      this.total = 0
      this.tabIndex === '0'
        ? this.getRanking(this.id)
        : this.getQuestionError(this.id)
    },
    // 计算分钟数
    timeRange(startTime, endTime) {
      if (!startTime || !endTime) {
        return '--'
      }
      const diffTime =
        new Date(endTime).getTime() - new Date(startTime).getTime()
      const minutes = Math.ceil(diffTime / (60 * 1000))
      return `${minutes}分钟`
    },
    // 分页查询
    pageChange(val) {
      val && (this.curPage = val)
      this.tabIndex === '0'
        ? this.getRanking(this.id)
        : this.getQuestionError(this.id)
    },
    // 预览试卷
    viewPaper(userId) {
      const routes = this.$router.resolve({
        name: 'MyExamDetail',
        params: {
          userId,
          examId: this.id,
          examEndTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          showType: 1,
          preview: true
        }
      })
      window.open(routes.href, '_blank')
    },
    // 获取展开行信息
    async expandChange(row) {
      const currentId = this.expandRowList[0] ? this.expandRowList[0] : ''

      if (currentId === row.questionId) {
        return
      }
      const index = this.errorList.findIndex(
        (item) => row.questionId === item.questionId
      )

      const res = await getRanking({
        examId: this.id,
        questionId: row.questionId,
        curPage: this.curPage,
        pageSize: this.pageSize
      })
      res.data.list.map(item => {
        item.questionId = row.questionId
      })
      this.expandRowList = [row.questionId]
      this.errorList[index]['children'] = res.data.list
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  margin: 0 auto 20px;
}

.info-content {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: stretch;
  line-height: 30px;
  .content-item {
    span {
      display: inline-block;
      text-align: right;
      width: 120px;
    }
  }
}

.box-card {
  margin-right: 20px;
  flex: 1;
  &:first-child {
    margin-left: 20px;
  }
  /deep/ .el-card__header {
    position: relative;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 22px;
      left: 10px;
      width: 4px;
      height: 20px;
      background: #0094e5;
    }
  }
  /deep/.el-card__body {
    padding: 13px;
  }
}

#questionType,
#scoreRange {
  width: 100%;
  height: 200px;
}

.ranking-error {
  padding: 20px;
}
</style>
