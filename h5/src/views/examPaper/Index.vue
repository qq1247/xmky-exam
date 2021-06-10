<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.queryName"
          ></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query(1)" icon="el-icon-search" type="primary"
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
              examForm.show = true
              examForm.edit = false
            "
          >
            <i class="common common-plus"></i>
            <span>添加试卷分类</span>
          </div>
        </div>
        <div v-for="(item, index) in typeList" :key="index" class="exam-item">
          <div class="exam-content">
            <div class="title">{{ item.name }}</div>
            <div class="content-info">
              <span>读取权限：张三</span>
            </div>
            <div class="content-info">
              <span>使用权限：张三、张三、张三</span>
            </div>
            <div class="handler">
              <span data-title="编辑" @click="examEdit(item)">
                <i class="common common-edit"></i>
              </span>
              <span data-title="删除" @click="examDel(item.id)">
                <i class="common common-delete"></i>
              </span>
              <span data-title="试卷列表" @click="goDetail">
                <i class="common common-list-row"></i>
              </span>
            </div>
          </div>
        </div>
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
        @current-change="handleCurrentChange"
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
        <el-button @click="examHandler" type="primary">{{
          examForm.edit ? "修改" : "添加"
        }}</el-button>
        <el-button @click="examForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      pageSize: 5,
      total: 1,
      queryForm: {
        queryName: ""
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
    this.query(1)
  },
  methods: {
    // 查询
    async query(curPage) {
      const typeList = await this.$https.paperTypeListPage({
        name: this.queryForm.queryName,
        curPage,
        pageSize: this.pageSize
      })
      this.typeList = typeList.data.rows
      this.total = typeList.data.total
    },
    // 添加试卷信息
    examHandler() {
      this.$refs["examForm"].validate(async valid => {
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
          this.examForm.examName = ""
          this.$tools.message(!this.examForm.edit ? "添加成功！" : "修改成功！")
          this.query()
        } else {
          this.$tools.message(
            !this.examForm.edit ? "添加失败！" : "修改失败！",
            "error"
          )
        }
      })
    },
    examEdit({ id, name }) {
      this.examForm.edit = true
      this.examForm.id = id
      this.examForm.examName = name
      this.examForm.show = true
    },
    async examDel(id) {
      const res = await this.$https.paperTypeDel({
        id
      })

      if (res.code == 200) {
        this.$tools.message("删除成功！")
        this.query()
      } else {
        this.$tools.message("删除成功！", "error")
      }
    },
    examRole() {},
    examOpen() {},
    handleCurrentChange(val) {
      this.query(val)
    },
    // 试题详情
    goDetail() {
      this.$router.push("/examPaper/classify")
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";
</style>
