<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input placeholder="请输入名称" v-model="queryForm.queryName" class="query-input"></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query()" icon="el-icon-search" type="primary">查询</el-button>
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
            <span>添加考试分类</span>
          </div>
        </div>
        <ListCard
          v-for="(item, index) in typeList"
          :key="index"
          :data="item"
          name="exam"
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
      ></el-pagination>
    </div>
    <!-- 添加 | 修改考试分类 -->
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      width="30%"
      title="考试分类"
      :close-on-click-modal="false"
      @close="resetData('examForm')"
    >
      <el-form :model="examForm" :rules="examForm.rules" ref="examForm" label-width="60px">
        <el-form-item label="名称" prop="examName">
          <el-input placeholder="请输入分类名称" v-model="examForm.examName"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="examHandler" type="primary">
          {{
            examForm.edit ? "修改" : "添加"
          }}
        </el-button>
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
            { required: true, message: "请输入考试名称", trigger: "blur" }
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
    // 查询分类信息
    async query(curPage = 1) {
      const typeList = await this.$https.examTypeListPage({
        name: this.queryForm.queryName,
        curPage,
        pageSize: this.pageSize
      })
      this.typeList = typeList.data.list
      this.total = typeList.data.total
    },
    // 添加 || 修改考试名称
    examHandler() {
      this.$refs["examForm"].validate(async (valid) => {
        if (!valid) {
          return
        }

        let res

        if (this.examForm.edit) {
          res = await this.$https.examTypeEdit({
            id: this.examForm.id,
            name: this.examForm.examName
          })
        } else {
          res = await this.$https.examTypeAdd({
            name: this.examForm.examName
          })
        }

        if (res.code == 200) {
          this.examForm.show = false
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
    // 考试子分类
    goDetail({ id }) {
      this.$router.push({
        path: "/examSetting/list",
        query: { id }
      })
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
    // 清空还原数据
    resetData(name) {
      this.$tools.resetData(this, name)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";
</style>
