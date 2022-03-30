<template>
  <div class="container">
    <template v-if="hashChildren">
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
        <template v-if="myMarkList.length > 0">
          <div class="exam-list">
            <MyCard
              v-for="(item, index) in myMarkList"
              :key="index"
              :data="item"
              name="myMarkList"
              @mark="markHandler"
              @markUser="markUserHandler"
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
    </template>

    <router-view v-else></router-view>
  </div>
</template>

<script>
import { myMarkListPage } from 'api/my'
import MyCard from 'components/ListCard/MyCard.vue'
export default {
  components: {
    MyCard,
  },
  data() {
    return {
      pageSize: 6,
      curPage: 1,
      total: 0,
      queryForm: {
        examName: '',
      },
      myMarkList: [],
    }
  },
  computed: {
    hashChildren() {
      return this.$route.matched.length > 2 ? false : true
    },
  },
  mounted() {
    this.query()
  },
  methods: {
    // 我的考试列表
    async query() {
      const myMarkList = await myMarkListPage({
        examName: this.queryForm.examName,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })

      this.myMarkList = myMarkList.data?.list || []
      this.total = myMarkList.data?.total || 0
    },
    search() {
      this.curPage = 1
      this.query()
    },
    // 我的阅卷操作
    markHandler({ examId, paperId, examMarkStartTime, examMarkEndTime }) {
      const _markStartTime = new Date(examMarkStartTime).getTime()
      const _markEndTime = new Date(examMarkEndTime).getTime()
      const now = new Date().getTime()
      if (now < _markStartTime) {
        this.$message.warning('阅卷未开始，请等待...')
        return
      }

      this.$router.push({
        name: 'MyMarkIndexDetail',
        params: {
          examId,
          paperId,
          preview: _markStartTime < now && now > _markEndTime,
        },
      })
    },
    // 考生列表
    markUserHandler({ examId, paperId, examMarkStartTime, examMarkEndTime }) {
      const _markStartTime = new Date(examMarkStartTime).getTime()
      const _markEndTime = new Date(examMarkEndTime).getTime()
      const now = new Date().getTime()
      this.$router.push({
        name: 'MyMarkIndexUser',
        params: {
          examId,
          paperId,
          preview: _markStartTime < now && now > _markEndTime,
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
@import 'assets/style/list-card.scss';
</style>
