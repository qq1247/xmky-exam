<template>
  <div>
    <!-- 搜索 -->
    <el-form
      id="question_driver_query"
      :inline="true"
      :model="queryForm"
      class="form-inline"
    >
      <el-row type="flex" justify="space-between">
        <el-col :span="22">
          <el-form-item label>
            <el-input v-model="queryForm.id" placeholder="请输入编号" />
          </el-form-item>
          <el-form-item label>
            <el-input v-model="queryForm.title" placeholder="请输入题干" />
          </el-form-item>
          <el-form-item label>
            <el-select
              v-model="queryForm.type"
              clearable
              placeholder="请选择类型"
            >
              <el-option
                v-for="dict in queryForm.typeList"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-select
              v-model="queryForm.difficulty"
              clearable
              placeholder="请选择难度"
            >
              <el-option
                v-for="dict in queryForm.difficultyList"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-select
              v-model="queryForm.state"
              clearable
              placeholder="请选择状态"
            >
              <el-option
                v-for="state in queryForm.stateList"
                :key="parseInt(state.key)"
                :label="state.value"
                :value="parseInt(state.key)"
              />
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-input v-model="queryForm.score" placeholder="分值等于" />
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item>
            <el-button
              icon="el-icon-search"
              type="primary"
              @click="search"
            >查询</el-button>
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
        />
      </el-scrollbar>
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-center">
        <template v-if="questionTemplate">
          <div class="top">试题模板操作</div>
          <QuestionTemplate
            ref="questionTemplate"
            :question-type-id="questionTypeId"
            @showTemplate="showTemplate"
          />
        </template>
        <QuestionList
          v-else
          :id="questionId"
          ref="questionList"
          :list="list"
          @del="del"
          @copy="copy"
          @publish="publish"
          @pageChange="pageChange"
          @showDetails="showDetails"
          @questionEdit="questionEdit"
        />
      </el-scrollbar>
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-right">
        <!-- 试题参数详情 -->
        <QuestionDetail
          v-show="detailStatus"
          :data="questionDetail"
          :comment="false"
        />

        <!-- 编辑试题 -->
        <div v-show="!detailStatus">
          <div class="top">设置</div>
          <EditModule
            key="editModule"
            ref="editModule"
            style="padding-top: 50px"
            :question-id="questionId"
            :question-type="questionType"
            :question-type-id="questionTypeId"
            @add="add"
            @edit="edit"
          />
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
  questionPublish
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
    QuestionTemplate
  },
  data() {
    return {
      questionTypeId: null, // 试题分类id
      questionId: null, // 试题id
      questionType: 1, // 试题类型
      detailStatus: false,
      questionDetail: {},
      questionTemplate: false,
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
        difficulty: null, // 难度
        state: '', // 状态
        score: '', // 得分等于
        questionTypeName: '', // 试题分类name
        difficultyList: [], // 难度列表
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
        typeList: [] // 类型列表
      }
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
        data: { list, total }
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
      if (res?.code !== 200) {
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
    }
  }
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

.content-left {
  width: 136px;
  background: #fff;
  position: relative;
  border-bottom-left-radius: 8px;
}

.content-center {
  flex: 1;
  background: #fff;
  border-left: 1px solid rgba(0, 0, 0, 0.1);
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  padding-bottom: 40px;
}

.content-right {
  width: 400px;
  background: #fff;
  padding: 10px 20px 0 0;
  border-bottom-right-radius: 8px;
}

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
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  &::before {
    content: '';
    display: inline-block;
    position: relative;
    top: 2px;
    left: -10px;
    width: 2px;
    height: 14px;
    background: #0094e5;
  }
}
</style>
