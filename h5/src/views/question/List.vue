<template>
  <div>
    <!-- 查询条件 -->
    <el-form :inline="true" :model="queryForm" class="form-inline">
      <el-row type="flex" justify="space-between">
        <el-col :span="22">
          <el-form-item label>
            <el-input v-model="queryForm.id" placeholder="编号" />
          </el-form-item>
          <el-form-item label>
            <el-input v-model="queryForm.title" placeholder="题干" />
          </el-form-item>
          <el-form-item label>
            <el-select v-model="queryForm.type" clearable placeholder="类型">
              <el-option
                v-for="dict in $tools.getDicts('QUESTION_TYPE')"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-select v-model="queryForm.markType" clearable placeholder="阅卷类型">
              <el-option
                v-for="dict in $tools.getDicts('PAPER_MARK_TYPE')"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-button icon="el-icon-search" type="primary" @click="query">查询</el-button>
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item>
            <el-button icon="el-icon-plus" type="success" @click="toAdd">添加</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 试题列表 -->
    <div class="content">
      <QuestionList :list="queryList.list" :showMode="'simple'">
        <template #question-btn="{ question }">
          <el-button
            class="btn"
            size="mini"
            type="primary"
            @click.stop="toEdit(question.id)"
          >
            <img src="@/assets/img/question/question-update.png" />修改
          </el-button>
          <el-button
            class="btn"
            size="mini"
            type="primary"
            @click.stop="copy(question.id)"
          >
            <img src="@/assets/img/question/question-copy.png" alt="" />复制
          </el-button>
          <el-button
            class="btn"
            size="mini"
            type="danger"
            @click.stop="del(question.id)"
          >
            <img src="@/assets/img/question/question-delete.png" alt="" />删除
          </el-button>
        </template>
      </QuestionList>
    </div>

    <!-- 试题编辑 -->
    <el-drawer
      title="试题编辑"
      :visible.sync="showEditForm"
      direction="rtl"
      size="500px"
      :modal-append-to-body="false"
      :modal="false"
      @close="id = null"
    >
      <QuestionEdit :id="id" @add="add" @edit="edit" />
    </el-drawer>
  </div>
</template>
<script>
import {
  questionListpage,
  questionAdd,
  questionCopy,
  questionDel,
  questionEdit,
  questionPublish,
} from 'api/question'
import QuestionList from '@/components/Question/QuestionList.vue'
import QuestionEdit from '@/components/Question/QuestionEdit.vue'

export default {
  components: {
    QuestionList,
    QuestionEdit,
  },
  data() {
    return {
      questionTypeId: null, // 题库ID
      id: null, // 试题ID
      showEditForm: false, // 显示编辑表单
      queryForm: {
        id: null, // 主键
        title: null, // 标题
        type: null, // 试题类型
        markType: null, // 阅卷类型
      },
      queryList: {
        curPage: 1, // 当前第几页
        pageSize: 5, // 每页多少条
        total: 0, // 总条数
        list: [], // 数据列表
      },
    }
  },
  created() {
    this.questionTypeId = Number(this.$route.params.questionTypeId)
    this.query()
  },
  methods: {
    // 查询
    async query() {
      let {data: { list, total }} = await questionListpage({
        ...this.queryForm,
        curPage: this.queryList.curPage, 
        pageSize: this.queryList.pageSize
      })

      this.queryList.total = total
      this.queryList.list = list
    },
    // 分页查询
    pageChange(val = 1) {
      this.queryList.curPage = val
      this.query()
    },
    // 添加试题
    toAdd(id) {
      this.showEditForm = true
    },
    // 添加试题
    async add(params) {
      params.questionTypeId = this.questionTypeId
      const res = await questionAdd(params)
      this.$message.success(res.msg)

      this.queryList.curPage = 1
      this.query()
    },
    // 修改试题
    toEdit(id) {
      this.showEditForm = true
      this.id = id
    },
    // 修改试题
    async edit(params) {
      const res = await questionEdit(params)
      this.$message.success(res.msg)

      this.queryList.curPage = 1
      this.query()
    },
    // 复制试题
    async copy(id) {
      await questionCopy({ id })
    },
    // 删除试题
    async del(id) {
      await questionDel({ ids: [`${id}`] })
    },
    // 发布试题
    async publish(id, state) {
      await questionPublish({ids: [`${id}`]})
    },
  },
}
</script>
<style lang="scss" scoped>
.form-inline {
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px 8px 0 0;
  .el-form-item {
    width: 168px;
    margin-bottom: 0;
  }
}

.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 200px);
  margin: 0 auto;
}

.content-center {
  flex: 1;
  background: #fff;
  border-left: 1px solid rgba(0, 0, 0, 0.1);
  // border-right: 1px solid rgba(0, 0, 0, 0.1);
  padding-bottom: 40px;
}
</style>
