<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form
      :inline="true"
      :model="queryForm"
      class="form-inline"
      id="question_driver_query"
    >
      <el-row type="flex" justify="space-between">
        <el-col :span="22">
          <el-form-item label>
            <el-input
              placeholder="请输入编号"
              v-model="queryForm.id"
            ></el-input>
          </el-form-item>
          <el-form-item label>
            <el-input
              placeholder="请输入题干"
              v-model="queryForm.title"
            ></el-input>
          </el-form-item>
          <el-form-item label>
            <el-select
              clearable
              placeholder="请选择类型"
              v-model="queryForm.type"
            >
              <el-option
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
                v-for="dict in queryForm.typeList"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-select
              clearable
              placeholder="请选择难度"
              v-model="queryForm.difficulty"
            >
              <el-option
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
                v-for="dict in queryForm.difficultyList"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-select
              clearable
              placeholder="请选择状态"
              v-model="queryForm.state"
            >
              <el-option
                :key="parseInt(state.key)"
                :label="state.value"
                :value="parseInt(state.key)"
                v-for="state in queryForm.stateList"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-input
              placeholder="分值等于"
              v-model="queryForm.score"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item>
            <el-button @click="search" icon="el-icon-search" type="primary"
              >查询</el-button
            >
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 内容 -->
    <div class="content">
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-left">
        <QuestionType
          ref="questionType"
          @updateType="updateType"
          @showTemplate="showTemplate"
        ></QuestionType>
      </el-scrollbar>
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-center">
        <template v-if="questionTemplate">
          <div class="top">
            <div class="top-title">试题模板操作</div>
          </div>
          <QuestionTemplate
            ref="questionTemplate"
            :questionTypeId="questionTypeId"
            @showTemplate="showTemplate"
          ></QuestionTemplate>
        </template>
        <QuestionList
          v-else
          ref="questionList"
          :list="list"
          :id="questionId"
          @del="del"
          @copy="copy"
          @publish="publish"
          @pageChange="pageChange"
          @showDetails="showDetails"
          @questionEdit="questionEdit"
        ></QuestionList>
      </el-scrollbar>
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-right">
        <!-- 试题参数详情 -->
        <QuestionDetail
          v-show="detailStatus"
          :data="questionDetail"
          :comment="false"
        ></QuestionDetail>

        <!-- 编辑试题 -->
        <div v-show="!detailStatus">
          <div class="top">设置</div>
          <EditModule
            key="editModule"
            ref="editModule"
            style="padding-top: 50px"
            :questionId="questionId"
            :questionType="questionType"
            :questionTypeId="questionTypeId"
            @add="add"
            @edit="edit"
          ></EditModule>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>
<script>
import { getOneDict } from '@/utils/getDict'
import {
  questionListPage,
  questionAdd,
  questionGet,
  questionCopy,
  questionDel,
  questionEdit,
  questionPublish,
} from 'api/question'
import QuestionType from '@/components/EditQuestion/QuestionType.vue'
import QuestionList from '@/components/EditQuestion/QuestionList.vue'
import EditModule from 'components/EditQuestion/EditModule.vue'
import QuestionDetail from 'components/QuestionDetail/Index.vue'
import QuestionTemplate from '@/components/EditQuestion/QuestionTemplate.vue'
export default {
  components: {
    QuestionType,
    QuestionList,
    EditModule,
    QuestionDetail,
    QuestionTemplate,
  },
  data() {
    return {
      questionTypeId: null, // 试题分类id
      questionId: null, //试题id
      questionType: 1, //试题类型
      detailStatus: false,
      questionDetail: {},
      questionTemplate: false,
      list: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 6, // 每页多少条
        questionList: [], // 列表数据
      },
      queryForm: {
        id: '', // 主键
        name: '', // 导航栏名称
        edit: false, // 编辑权限
        title: '', // 标题
        type: null, // 类型
        difficulty: null, // 难度
        state: '', //状态
        score: '', // 得分等于
        questionTypeName: '', // 试题分类name
        difficultyList: [], // 难度列表
        stateList: [
          {
            key: '0',
            value: '回收站',
          },
          {
            key: '1',
            value: '发布',
          },
          {
            key: '2',
            value: '草稿',
          },
        ], //状态列表
        typeList: [], // 类型列表
      },
    }
  },
  created() {
    this.questionTypeId = Number(this.$route.params.id)
    this.queryForm.typeList = getOneDict('QUESTION_TYPE')
    this.queryForm.difficultyList = getOneDict('QUESTION_DIFFICULTY')
    this.search()
  },
  methods: {
    // 查询
    async query() {
      const {
        data: { list, total },
      } = await questionListPage({
        id: this.queryForm.id,
        type: this.queryForm.type,
        state: this.queryForm.state,
        title: this.queryForm.title,
        score: this.queryForm.score,
        difficulty: this.queryForm.difficulty,
        questionTypeId: this.questionTypeId,
        questionTypeName: this.queryForm.questionTypeName,
        curPage: this.list.curPage,
        pageSize: this.list.pageSize,
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
      this.detailStatus = false
      this.questionType = value
      this.questionId = null
    },
    // 添加试题
    async add(params) {
      const res = await questionAdd(params)
      if (res?.code === 200) {
        this.$message.success('添加成功！')
        this.$refs.editModule.resetData()
        this.search()
      } else {
        this.$message.success('添加失败！')
      }
    },
    // 修改试题
    async edit(params) {
      const res = await questionEdit(params)
      if (res?.code === 200) {
        this.$message.success('修改成功！')
        this.$refs.editModule.resetData()
        this.questionId = null
        this.search()
      } else {
        this.$message.success('修改失败！')
      }
    },
    // 获取试题详情
    async showDetails(id) {
      this.detailStatus = true
      const res = await questionGet({ id })
      if (res?.code != 200) {
        this.$message.error('获取详情失败！请重试')
        this.questionDetail = {}
        return
      }
      this.questionDetail = res.data
    },
    // 编辑试题
    questionEdit({ id, type }) {
      this.detailStatus = false
      this.$nextTick(() => {
        this.questionId = id
        this.questionType = type
      })
    },
    // 复制试题
    async copy(id) {
      if (this.queryForm.edit == 'false') {
        this.$message.warning('暂无此项权限！')
        return
      }
      const res = await questionCopy({ id })
      this.resetQuery(res, '复制')
    },
    // 删除试题
    del(id) {
      if (this.queryForm.edit == 'false') {
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
          dangerouslyUseHTMLString: true,
        }
      ).then(async () => {
        const res = await questionDel({ ids: [`${id}`] })
        this.resetQuery(res, '删除')
      })
    },
    // 发布试题
    async publish(id, state) {
      if (this.queryForm.edit == 'false') {
        this.$message.warning('暂无此项权限！')
        return false
      }

      if (state == 1) {
        this.$message.warning('试题已经发布！')
        return
      }
      const res = await questionPublish({
        ids: [`${id}`],
      })
      this.resetQuery(res, '发布试题')
    },
    // 显示试题操作模板
    showTemplate(e) {
      this.questionTemplate = e
      this.search()
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
    },
  },
}
</script>
<style lang="scss" scoped>
.form-inline {
  height: 60px;
  padding: 10px 20px 10px 70px;
  position: fixed;
  top: 50px;
  left: 0;
  right: 0;
  background: #fff;
  z-index: 1500;
  .el-form-item {
    width: 140px;
    margin-bottom: 0;
  }
}

.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 70px);
  padding: 45px 0 0;
  margin: 0 auto;
}

.content-left {
  width: 145px;
  background: #fff;
  position: relative;
}

.content-center {
  flex: 1;
}

.content-right {
  width: 500px;
  background: #fff;
  padding: 10px 20px 0 0;
  .top {
    background: #fff;
    width: 100%;
    height: 40px;
    line-height: 40px;
    color: #333;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    font-weight: 600;
    padding-left: 30px;
    border-bottom: 1px solid #eee;
    &::before {
      content: '';
      display: inline-block;
      position: relative;
      top: 2px;
      left: -10px;
      width: 2px;
      height: 14px;
      background: #0095e5;
    }
  }
}
</style>
