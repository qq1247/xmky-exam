<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入考试名称"
            v-model="queryForm.examName"
            class="query-input"
          ></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query" icon="el-icon-search" type="primary"
          >查询</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <template v-if="myExamList.length > 0">
        <div class="exam-list" v-if="type === 1">
          <ListCard
            v-for="(item, index) in myExamList"
            :key="index"
            :data="item"
            name="myExamList"
            @exam="examHandler"
          ></ListCard>
        </div>
        <div class="exam-list" v-if="type === 2">
          <ListCard
            v-for="(item, index) in myExamList"
            :key="index"
            :data="item"
            :markId="markId"
            :percentage="percentage"
            name="myMarkExamList"
            @mark="markHandler"
          ></ListCard>
        </div>
      </template>
      <div class="data-null" v-else>
        <img class="data-img" src="../../assets/img/data-null.png" alt />
        <span class="data-tip">抱歉！暂无信息</span>
      </div>
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="total"
        :page-size="pageSize"
        :current-page="1"
        @current-change="pageChange"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import {
  myExamListPage,
  myMarkListPage,
  myExamAutoScore,
  myExamAiProgress,
} from '@/api/my'
import ListCard from '@/components/ListCard.vue'
import { loginSysTime } from '@/api/common'
export default {
  components: {
    ListCard,
  },
  data() {
    return {
      pageSize: 6,
      curPage: 1,
      total: 1,
      type: 1,
      percentage: 0,
      markId: null,
      queryForm: {
        examName: '',
      },
      myExamList: [],
    }
  },
  mounted() {
    console.log(this.$route)
    const { type } = this.$route.meta

    this.type = type
    this.query()
  },
  methods: {
    // 我的考试列表
    async query() {
      const loginSysTimeStr = await loginSysTime({})
      const curTime = new Date(loginSysTimeStr.data).getTime()
      let myExamList

      if (this.type === 1) {
        myExamList = await myExamListPage({
          name: this.queryForm.examName,
          curPage: this.curPage,
          pageSize: this.pageSize,
        })
        myExamList.data.list.forEach((item) => {
          const examStartTime = new Date(item.examStartTime).getTime()
          const examEndTime = new Date(item.examEndTime).getTime()

          if (curTime < examStartTime) {
            item.exam = 'unStart'
          } else if (curTime > examEndTime) {
            item.exam = 'end'
          } else {
            item.exam = 'start'
          }
        })
      }

      if (this.type === 2) {
        myExamList = await myMarkListPage({
          name: this.queryForm.examName,
          curPage: this.curPage,
          pageSize: this.pageSize,
        })
        myExamList.data.list.forEach((item) => {
          const markStartTime = new Date(item.markStartTime).getTime()
          const markEndTime = new Date(item.markEndTime).getTime()
          const examStartTime = new Date(item.examStartTime).getTime()
          const examEndTime = new Date(item.examEndTime).getTime()

          if (curTime < examStartTime) {
            item.state = 1
            item.stateName = '待考试'
          } else if (curTime > examEndTime) {
            item.state = 3
            item.stateName = '已考试'
          } else {
            item.state = 2
            item.stateName = '考试中'
          }

          if (curTime < markStartTime) {
            item.mark = 'unStart'
            item.markState = 1
            item.markStateName = '待阅卷'
          } else if (curTime > markEndTime) {
            item.mark = 'end'
            item.markState = 3
            item.markStateName = '已阅卷'
          } else {
            item.mark = 'start'
            item.markState = 2
            item.markStateName = '阅卷中'
          }
        })
      }

      this.myExamList = myExamList.data.list
      this.total = myExamList.data.total
    },
    // 我的考试操作
    examHandler(data) {
      const examStartTime = new Date(data.examStartTime).getTime()
      const now = new Date().getTime()
      if (now < examStartTime) {
        this.$tools.message('考试未开始，请等待...', 'warning')
        return
      }
      this.$router.push({
        path: '/my/exam',
        query: {
          id: data.id,
          paperId: data.paperId,
          preview: data.exam !== 'start',
          examEndTime: data.exam === 'start' ? data.examEndTime : '',
        },
      })
    },
    // 我的阅卷操作
    async markHandler(data) {
      this.markId = data.examId
      const markStartTime = new Date(data.markStartTime).getTime()
      const markEndTime = new Date(data.markEndTime).getTime()
      const now = new Date().getTime()
      if (now < markStartTime) {
        this.$tools.message('阅卷未开始，请等待...', 'warning')
        return
      }

      if (now > markEndTime) {
        this.$router.push({
          path: '/my/markExam',
          query: {
            examId: data.examId,
            paperId: data.paperId,
            markId: data.id,
            preview: true,
          },
        })
        return
      }

      if (markStartTime < now < markEndTime && data.autoState === 1) {
        this.$router.push({
          path: '/my/markExam',
          query: {
            examId: data.examId,
            paperId: data.paperId,
            markId: data.id,
            preview: false,
          },
        })
        return
      }

      const res = await myExamAutoScore({
        id: data.id,
        examId: data.examId,
      }).catch((err) => {
        console.log(err)
      })
      if (res?.code === 200) {
        this.percentage = 1
        const isAiEnd = await this.getProgress(res.data)
        if (isAiEnd) {
          this.$router.push({
            path: '/my/markExam',
            query: {
              examId: data.examId,
              paperId: data.paperId,
              markId: data.id,
              preview: false,
            },
          })
          this.$tools.delay().then(() => {
            this.percentage = 0
            this.markId = null
          })
        }
      } else {
        this.$tools.message(res.msg || '智能阅卷失败！请重试！', 'erroe')
      }
    },
    // 获取进度
    async getProgress(id) {
      const percentage = await this.$tools.delay().then(() => {
        return myExamAiProgress({
          id,
        }).catch((err) => {})
      })

      if (!percentage?.data?.totalNum) {
        this.percentage = 0
        this.markId = null
        return false
      }

      this.percentage +=
        Math.ceil(
          Math.abs(percentage.data.curNum / percentage.data.totalNum) * 100
        ) - this.percentage

      if (percentage.data.curNum === percentage.data.totalNum) {
        return true
      } else {
        this.getProgress(id)
      }
    },
    // 分页切换
    pageChange(val) {
      this.curPage = val
      this.query()
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
</style>
