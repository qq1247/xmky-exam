<template>
  <div class="chart-info">
    <el-card class="box-card" shadow="never">
      <div slot="header">
        <span>智能试题</span>
      </div>
      <div id="questionAi" />
    </el-card>
    <el-card class="box-card" shadow="never">
      <div slot="header">
        <span>试题类型</span>
      </div>
      <div id="questionType" />
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOneDict } from '@/utils/getDict'
import { getQuestionStatis } from 'api/report'

export default {
  data() {
    return {
      statisInfo: {},
      markTypeNames: [],
      typeNames: []
    }
  },
  async mounted() {
    this.markTypeNames = getOneDict('PAPER_MARK_TYPE').map((item) => item.dictValue)
    this.typeNames = getOneDict('QUESTION_TYPE').map((item) => item.dictValue)
    this.id = this.$route.params.id
    if (this.id) {
      await this.getQuestionStatis()
      this.renderChart()
    }
  },
  methods: {
    async getQuestionStatis() {
      const res = await getQuestionStatis({
        questionTypeId: this.id
      })
      this.statisInfo = res.data
    },
    // chart共用
    chartDom(id, data) {
      const chartDom = document.getElementById(id)
      const myChart = echarts.init(chartDom)
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: '类型',
            type: 'pie',
            radius: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '14'
              }
            },
            labelLine: {
              show: false
            },
            data
          }
        ]
      }
      option && myChart.setOption(option)
    },
    // 渲染饼图
    renderChart() {
      this.statisInfo.markTypeList.map((ai) => {
        markType.name = this.markTypeNames[ai.name - 1]
      })
      this.statisInfo.typeList.map((type) => {
        type.name = this.typeNames[type.name - 1]
      })
      this.chartDom('questionAi', this.statisInfo.markTypeList)
      this.chartDom('questionType', this.statisInfo.typeList)
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  margin: 0 auto 20px;
}

.chart-info {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}

.box-card {
  margin-bottom: 30px;
  /deep/ .el-card__header {
    position: relative;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 18px;
      left: 10px;
      width: 4px;
      height: 20px;
      background: #0094e5;
    }
  }
  /deep/.el-card__body {
    padding: 13px;
    min-height: 300px;
  }
}

#questionAi,
#questionType {
  width: 400px;
  height: 400px;
}
</style>
