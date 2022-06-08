<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            v-model="queryForm.examName"
            placeholder="请输入考试名称"
            class="query-input"
          />
        </el-form-item>
      </div>
      <el-form-item>
        <el-button
          icon="el-icon-search"
          type="primary"
          @click="search"
        >查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <template v-if="myExamList.length > 0">
        <div class="exam-list">
          <MyCard
            v-for="(item, index) in myExamList"
            :key="index"
            :data="item"
            name="myExamList"
            @exam="examHandler"
          />
        </div>
      </template>
      <el-empty v-else description="暂无信息" />
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
      />
    </div>
  </div>
</template>

<script>
import { myExamListPage } from 'api/my'
import MyCard from 'components/ListCard/MyCard.vue'
export default {
  components: {
    MyCard
  },
  data() {
    return {
      pageSize: 6,
      curPage: 1,
      total: 0,
      queryForm: {
        examName: ''
      },
      myExamList: []
    }
  },
  mounted() {
    this.query()
  },
  methods: {
    // 我的考试列表
    async query() {
      const myExamList = await myExamListPage({
        examName: this.queryForm.examName,
        curPage: this.curPage,
        pageSize: this.pageSize
      })

      this.myExamList = myExamList.data?.list || []
      this.total = myExamList.data?.total || 0
    },
    search() {
      this.curPage = 1
      this.query()
    },
    // 我的考试操作
    examHandler({
      examId,
      paperId,
      paperShowType,
      examStartTime,
      examEndTime,
      examMarkState,
      examMarkEndTime,
      state,
      markState
    }) {
      const _examStartTime = new Date(examStartTime).getTime()
      const _examEndTime = new Date(examEndTime).getTime()
      const _examMarkEndTime = new Date(examMarkEndTime).getTime()
      const now = new Date().getTime()
      if (now < _examStartTime) {
        this.$message.warning('考试暂未开始！')
        return
      }

      // 考试时间内和阅卷结束后的逻辑
      if (
        (_examStartTime < now && now < _examEndTime) ||
        (now > _examMarkEndTime && examMarkState === 3)
      ) {
        this.$router.push({
          name: 'MyExamDetail',
          params: {
            examId,
            paperId,
            examEndTime,
            showType: paperShowType,
            preview: state === 3 || (state === 1 && markState === 3),
            userId: null
          }
        })
      }
    },
    // 分页切换
    pageChange(val) {
      this.curPage = val
      this.query()
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
.container {
  margin: 0 auto 20px;
}
</style>
