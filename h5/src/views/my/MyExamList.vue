<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input placeholder="请输入考试名称" v-model="queryForm.examName" class="query-input"></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query()" icon="el-icon-search" type="primary">查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <ListCard
          v-for="(item, index) in myExamList"
          :key="index"
          :data="item"
          name="myExamList"
          @startExam="startExam"
        ></ListCard>
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
import ListCard from "@/components/ListCard.vue";
export default {
  components: {
    ListCard
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      queryForm: {
        examName: "",
      },
      myExamList: []
    }
  },
  mounted() {
    this.query(1)
  },
  methods: {
    // 我的考试列表
    async query(curPage = 1) {
      const loginSysTimeStr = await this.$https.loginSysTime({});
      const curTime = new Date(loginSysTimeStr.data)

      const myExamList = await this.$https.myExamListPage({
        name: this.queryForm.examName,
        curPage,
        pageSize: this.pageSize
      })

      myExamList.data.list.forEach((item) => {
        const examStartTime = new Date(item.examStartTime);
        const examEndTime = new Date(item.examEndTime);

        if (examStartTime.getTime() > curTime.getTime()) {
          item.btn = "unStart";
        } else if (examEndTime.getTime() < curTime.getTime()) {
          item.btn = "end";
        } else {
          item.btn = "start";
        }
      })
      this.myExamList = myExamList.data.list
      this.total = myExamList.data.total
    },
    // 删除分类
    async del({ id }) {
      const res = await this.$https.examTypeDel({
        id
      })

      if (res.code == 200) {
        this.$tools.message("删除成功！");
        this.query()
      } else {
        this.$tools.message("删除成功！", "error");
      }
    },
    // 开始考试
    startExam(data) {
      this.$router.push({
        path: "/my/exam",
        query: { id: data.id, paperId: data.paperId }
      })
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";
</style>
