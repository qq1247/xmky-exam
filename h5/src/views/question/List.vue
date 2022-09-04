<template>
  <div>
    <!-- 查询条件 -->
    <el-form
      :inline="true"
      :model="queryForm"
      class="form-inline"
    >
      <el-row type="flex" justify="space-between">
        <el-col :span="22">
          <el-form-item label>
            <el-input v-model="queryForm.id" placeholder="编号" />
          </el-form-item>
          <el-form-item label>
            <el-input v-model="queryForm.title" placeholder="题干" />
          </el-form-item>
          <el-form-item label>
            <el-select
              v-model="queryForm.type"
              clearable
              placeholder="类型"
            >
              <el-option
                v-for="dict in $tools.getDicts('QUESTION_TYPE')"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-select
              v-model="queryForm.type"
              clearable
              placeholder="阅卷类型"
            >
              <el-option
                v-for="dict in $tools.getDicts('PAPER_MARK_TYPE')"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-button
              icon="el-icon-search"
              type="primary"
              @click="search"
            >查询</el-button>
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item>
            <el-button
              icon="el-icon-plus"
              type="success"
              @click="drawer = true"
            >添加</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 试题列表 -->
    <div class="content">
        <QuestionList :list="list" showMode='simple'/>
    </div>

    <!-- 试题编辑 -->
    <el-drawer
      title="试题编辑"
      :visible.sync="drawer"
      direction="rtl"
      size="500px"
      :modal-append-to-body="false"
      :modal="false"
      >
      <QuestionEdit
        @add="add"
        @edit="edit"
      />
    </el-drawer>
  </div>
</template>
<script>
import {
  questionListPage,
  questionAdd,
  questionGet,
  questionCopy,
  questionDel,
  questionEdit,
  questionPublish
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
      questionDetail: {},
      showType: 'simple', // 试题展示样式
      drawer: false,
      list: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 5, // 每页多少条
        questionList: [] // 列表数据
      },
      queryForm: {
        id: '', // 主键
        name: '', // 导航栏名称
        edit: false, // 编辑权限
        title: '', // 标题
        type: null, // 类型
        state: '', // 状态
        score: '', // 得分等于
        questionTypeName: '', // 题库name
        stateList: [
          {
            key: '0',
            value: '回收站'
          },
          {
            key: '1',
            value: '发布'
          },
          {
            key: '2',
            value: '草稿'
          }
        ], // 状态列表
      }
    }
  },
  created() {
    this.questionTypeId = Number(this.$route.params.questionTypeId)
    this.search()
  },
  methods: {
    // 查询
    async query() {
      const {
        data: { list, total }
      } = await questionListPage({
        id: this.queryForm.id,
        type: this.queryForm.type,
        state: this.queryForm.state,
        title: this.queryForm.title,
        score: this.queryForm.score,
        questionTypeId: this.questionTypeId,
        questionTypeName: this.queryForm.questionTypeName,
        curPage: this.list.curPage,
        pageSize: this.list.pageSize
      })
      this.list.total = total
      this.list.questionList = list
    },
    // 搜索
    search() {
      this.list.curPage = 1
      this.query()
    },
    // 分页查询
    pageChange(val = 1) {
      this.list.curPage = val
      this.query()
    },
    // 更新类型
    updateType(value) {
      this.questionType = value
      this.questionId = null
    },
    // 添加试题
    async add(params) {
      params.questionTypeId = this.questionTypeId
      const res = await questionAdd(params)
      if (res.code !== 200) {
        this.$message.success(res.msg)
      }

      this.search()
    },
    // 修改试题
    async edit(params) {
      const res = await questionEdit(params)
      if (res?.code === 200) {
        this.$message.success('修改成功！')
        this.questionId = null
        this.search()
      } else {
        this.$message.success('修改失败！')
      }
    },
    // 编辑试题
    questionEdit({ id, type }) {
      this.$nextTick(() => {
        this.questionId = id
        this.questionType = type
      })
    },
    // 复制试题
    async copy(id) {
      if (this.queryForm.edit === 'false') {
        this.$message.warning('暂无此项权限！')
        return
      }
      const res = await questionCopy({ id })
      this.resetQuery(res, '复制')
    },
    // 删除试题
    del(id) {
      if (this.queryForm.edit === 'false') {
        this.$message.warning('暂无此项权限！')
        return
      }
      this.$confirm(
        `确定要删除？<br/>不影响关联的试卷等，可以正常显示和使用`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          dangerouslyUseHTMLString: true
        }
      ).then(async() => {
        const res = await questionDel({ ids: [`${id}`] })
        this.resetQuery(res, '删除')
      })
    },
    // 发布试题
    async publish(id, state) {
      if (this.queryForm.edit === 'false') {
        this.$message.warning('暂无此项权限！')
        return false
      }

      if (state === 1) {
        this.$message.warning('试题已经发布！')
        return
      }
      const res = await questionPublish({
        ids: [`${id}`]
      })
      this.resetQuery(res, '发布试题')
    },
    // 还原数据并查询
    resetQuery(res, msg) {
      if (res?.code === 200) {
        this.questionId = null
        this.list.curPage = 1
        this.queryForm.state = ''
        this.query()
        this.$message.success(`${msg}成功！`)
      } else {
        this.$message.error(res.msg || `${msg}失败！`)
      }
    }
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
