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
          <MyCard
            v-for="(item, index) in myExamList"
            :key="index"
            :data="item"
            name="myExamList"
            @exam="examHandler"
          ></MyCard>
        </div>
        <div class="exam-list" v-if="type === 2">
          <MyCard
            v-for="(item, index) in myExamList"
            :key="index"
            :data="item"
            name="myMarkExamList"
            @mark="markHandler"
          ></MyCard>
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
import { myExamListPage, myMarkListPage } from 'api/my'
import MyCard from 'components/ListCard/MyCard.vue'
import { loginSysTime } from 'api/common'
export default {
  components: {
    MyCard,
  },
  data() {
    return {
      pageSize: 6,
      curPage: 1,
      total: 1,
      type: 1,
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
    examHandler({
      id,
      examId,
      paperId,
      paperShowType,
      examStartTime,
      examEndTime,
    }) {
      const _examStartTime = new Date(examStartTime).getTime()
      const _examEndTime = new Date(examEndTime).getTime()
      const now = new Date().getTime()
      if (now < _examStartTime) {
        this.$message.warning('考试未开始，请等待...')
        return
      }

      this.$router.push({
        path: '/my/exam/index',
        query: {
          id,
          examId,
          paperId,
          examEndTime,
          showType: paperShowType,
          preview: _examStartTime < now && now > _examEndTime,
        },
      })
    },
    // 我的阅卷操作
    markHandler({ id, examId, paperId, markStartTime, markEndTime }) {
      this.markId = examId
      const _markStartTime = new Date(markStartTime).getTime()
      const _markEndTime = new Date(markEndTime).getTime()
      const now = new Date().getTime()
      if (now < _markStartTime) {
        this.$message.warning('阅卷未开始，请等待...')
        return
      }

      this.$router.push({
        path: '/my/mark/index',
        query: {
          markId: id,
          examId,
          paperId,
          markEndTime: _markEndTime,
          markStartTime: _markStartTime,
        },
      })
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
