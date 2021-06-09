<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.examName"
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
            <span>添加试题</span>
          </div>
        </div>
        <div v-for="(item, index) in examList" :key="index" class="exam-item">
          <div class="exam-content">
            <div class="title">{{ item.name }}</div>
            <div class="content-info">
              <span>读取权限：张三</span>
            </div>
            <div class="content-info">
              <span>使用权限：张三、张三、张三</span>
            </div>
            <div class="handler">
              <span data-title="编辑" @click="examEdit">
                <i class="common common-edit"></i>
              </span>
              <span data-title="删除" @click="examDel">
                <i class="common common-delete"></i>
              </span>
              <span data-title="权限" @click="examRole">
                <i class="common common-role"></i>
              </span>
              <span data-title="开放" @click="examOpen">
                <i class="common common-share"></i>
              </span>
              <span data-title="试题列表" @click="goDetail">
                <i class="common common-list-row"></i>
              </span>
            </div>
          </div>
        </div>
      </div>
      <el-pagination background layout="prev, pager, next" :total="1000">
      </el-pagination>
    </div>
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      center
      width="30%"
      title="试题分类"
    >
      <el-form
        :model="examForm"
        :rules="examForm.rules"
        ref="examForm"
        label-width="60px"
      >
        <el-form-item label="名称" prop="examName">
          <el-input
            placeholder="请输入试题名称"
            v-model="examForm.examName"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="examAdd" type="primary">{{
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
      queryForm: {
        examName: ""
      },
      examForm: {
        show: false,
        edit: false,
        examName: "",
        examThumbnail: "",
        rules: {
          examName: [
            { required: true, message: "请输入试题名称", trigger: "blur" }
          ]
        }
      },
      examList: [
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        }
      ]
    }
  },
  methods: {
    // 查询
    query() {},
    // 添加试卷信息
    examAdd() {
      this.$refs["examForm"].validate(valid => {
        if (!valid) {
          console.log("error")
          return
        }
      })
    },
    examEdit() {
      this.examForm.edit = true
      this.examForm.show = true
    },
    examDel() {},
    examRole() {},
    examOpen() {},
    // 试题详情
    goDetail() {
      this.$router.push("/examLibrary/edit")
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";
</style>
