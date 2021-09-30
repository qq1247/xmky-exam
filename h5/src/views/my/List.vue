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
        <el-button @click="search" icon="el-icon-search" type="primary"
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
      <el-empty v-else description="暂无信息"> </el-empty>
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
} from 'api/my'
import ListCard from 'components/ListCard.vue'
import { loginSysTime } from 'api/common'
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
          examName: this.queryForm.examName,
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
          examName: this.queryForm.examName,
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
    search() {
      this.curPage = 1
      this.query()
    },
    // 我的考试操作
    examHandler(data) {
      const examStartTime = new Date(data.examStartTime).getTime()
      const examEndTime = new Date(data.examEndTime).getTime()
      const now = new Date().getTime()
      if (now < examStartTime) {
        this.$message.warning('考试未开始，请等待...')
        return
      }

      this.$router.push({
        path: '/my/exam/index',
        query: {
          id: data.id,
          paperId: data.paperId,
          preview: examStartTime < now && now > examEndTime,
          examEndTime: data.examEndTime,
          showType: data.paperShowType,
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
        this.$message.warning('阅卷未开始，请等待...')
        return
      }
      if (markStartTime < now && now < markEndTime && data.autoState !== 1) {
        const res = await myExamAutoScore({
          id: data.id,
          examId: data.examId,
        })
        if (res?.code === 200) {
          this.percentage = 1
          const isAiEnd = await this.getProgress(res.data)
          if (isAiEnd) {
            this.$router.push({
              path: '/my/mark/index',
              query: {
                markId: data.id,
                examId: data.examId,
                paperId: data.paperId,
                markEndTime: markEndTime,
                markStartTime: markStartTime,
              },
            })
            this.$tools.delay().then(() => {
              this.percentage = 0
              this.markId = null
            })
            return
          }
        } else {
          this.$message.error(res.msg || '智能阅卷失败！请重试！')
        }
      }

      this.$router.push({
        path: '/my/mark/index',
        query: {
          markId: data.id,
          examId: data.examId,
          paperId: data.paperId,
          markEndTime: markEndTime,
          markStartTime: markStartTime,
        },
      })
    },
    // 获取进度
    async getProgress(id) {
      const percentage = await this.$tools.delay().then(() => {
        return myExamAiProgress({
          id,
        })
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
