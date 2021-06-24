<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.queryName"
            class="query-input"
          ></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query()" icon="el-icon-search"
type="primary"
          >查询</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <div class="exam-item">
          <div
            class="exam-content exam-add"
            @click="
              examForm.show = true;
              examForm.edit = false;
            "
          >
            <i class="common common-plus"></i>
            <span>添加试卷分类</span>
          </div>
        </div>
        <ListCard
          v-for="(item, index) in typeList"
          :key="index"
          :data="item"
          name="paper"
          @edit="edit"
          @del="del"
          @detail="goDetail"
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
      >
      </el-pagination>
    </div>
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      width="30%"
      title="试卷分类"
      :close-on-click-modal="false"
    >
      <el-form
        :model="examForm"
        :rules="examForm.rules"
        ref="examForm"
        label-width="60px"
      >
        <el-form-item label="名称" prop="examName">
          <el-input
            placeholder="请输入分类名称"
            v-model="examForm.examName"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="addOrEdit" type="primary">{{
          examForm.edit ? "修改" : "添加"
        }}</el-button>
        <el-button @click="examForm.show = false">取消</el-button>
      </div>
    </el-dialog>
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
        queryName: "",
      },
      examForm: {
        show: false,
        edit: false,
        id: 0,
        examName: "",
        rules: {
          examName: [
            { required: true, message: "请输入试卷名称", trigger: "blur" }
          ]
        }
      },
      typeList: []
    }
  },
  mounted() {
    this.query()
  },
  methods: {
    // 查询分类信息
    async query(curPage = 1) {
      const typeList = await this.$https.paperTypeListPage({
        name: this.queryForm.queryName,
        curPage,
        pageSize: this.pageSize
      })
      this.typeList = typeList.data.list
      this.total = typeList.data.total
    },
    // 添加 || 修改试卷名称
    addOrEdit() {
      this.$refs["examForm"].validate(async(valid) => {
        if (!valid) {
          return
        }

        let res

        if (this.examForm.edit) {
          res = await this.$https.paperTypeEdit({
            id: this.examForm.id,
            name: this.examForm.examName
          })
        } else {
          res = await this.$https.paperTypeAdd({
            name: this.examForm.examName
          })
        }

        if (res.code == 200) {
          this.examForm.show = false
          this.examForm.edit = false
          this.examForm.examName = "";
          this.$tools.message(
            !this.examForm.edit ? '添加成功！' : '修改成功！'
          )
          this.query()
        } else {
          this.$tools.message(
            !this.examForm.edit ? "添加失败！" : "修改失败！",
            "error"
          )
        }
      })
    },
    // 编辑分类
    edit({ id, name }) {
      this.examForm.edit = true
      this.examForm.id = id
      this.examForm.examName = name
      this.examForm.show = true
    },
    // 删除分类
    del({ id }) {
      this.$https
        .paperTypeDel({
          id
        })
        .then((res) => {
          if (res.code == 200) {
            this.$tools.message("删除成功！");
            this.query()
          } else {
            this.$tools.message("删除成功！", "error");
          }
        })
        .catch((error) => {})
    },
    // 试卷子分类
    goDetail({ id }) {
      this.$router.push({
        path: "/examPaper/list",
        query: { id }
      })
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";
</style>
