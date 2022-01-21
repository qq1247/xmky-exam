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
          :questionType="editForm.type"
          @updateType="updateType"
          @showTemplate="showTemplate"
        ></QuestionType>
      </el-scrollbar>
      <el-scrollbar
        wrap-style="overflow-x:hidden;"
        class="content-center"
        id="question_driver_content"
      >
        <QuestionTemplate
          ref="questionTemplate"
          v-if="questionTemplate"
          @showTemplate="showTemplate"
        ></QuestionTemplate>
        <QuestionList
          v-else
          ref="questionList"
          :list="list"
          :id="editForm.id"
          @del="del"
          @copy="copy"
          @publish="publish"
          @pageChange="pageChange"
          @showDetails="showDetails"
          @questionEdit="questionEdit"
        ></QuestionList>
      </el-scrollbar>
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-right">
        <QuestionEdit
          ref="questionDetail"
          :edit-form="editForm"
          :detailStatus="detailStatus"
          :id="editForm.id"
          @add="add"
          @edit="edit"
          @addOption="addOption"
          @delOption="delOption"
          @addFillBlanks="addFillBlanks"
          @delFillBlanks="delFillBlanks"
          @editorListener="editorListener"
        ></QuestionEdit>
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
import QuestionEdit from '@/components/EditQuestion/QuestionEdit.vue'
import QuestionTemplate from '@/components/EditQuestion/QuestionTemplate.vue'
export default {
  components: {
    QuestionType,
    QuestionList,
    QuestionEdit,
    QuestionTemplate,
  },
  data() {
    const validateAiScore = (rule, value, callback) => {
      if (this.editForm.ai === 2) {
        return callback()
      }
      if (value === '') {
        return callback(new Error('请填写分数'))
      }
      if (value > this.editForm.score || value <= 0) {
        return callback(new Error(`请填写合理分数`))
      }
      return callback()
    }

    const validateMultipScore = (rule, value, callback) => {
      if (this.editForm.ai === 2 || this.editForm.scoreOptions.length === 0) {
        this.editForm.multipScore = ''
        return callback()
      }
      if (value === '') {
        return callback(new Error('请填写分数'))
      }
      if (value > this.editForm.score || value <= 0) {
        return callback(new Error(`请填写合理分数`))
      }
      return callback()
    }

    return {
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
        // 查询表单
        id: '', // 主键
        name: '', // 导航栏名称
        edit: false, // 编辑权限
        title: '', // 标题
        type: null, // 类型
        difficulty: null, // 难度
        state: '', //状态
        score: '', // 得分等于
        questionTypeName: '', // 试题分类name
        questionTypeId: 1, // 试题分类id
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
      editForm: {
        // 修改表单
        id: null, // 主键
        type: 1, // 类型
        difficulty: 1, // 难度
        title: '', // 标题
        ai: 1, // AI阅卷
        state: 2,
        options: [
          {
            lab: 'A',
            value: '',
          },
          {
            lab: 'B',
            value: '',
          },
        ], // 选项
        answer: '', // 答案
        answerMultip: [],
        answers: [
          {
            lab: 'A',
            value: [],
            score: '',
          },
          {
            lab: 'B',
            value: [],
            score: '',
          },
        ],
        // 答案
        judgeAnswers: [
          {
            lab: '对',
            value: '',
          },
          {
            lab: '错',
            value: '',
          },
        ],
        analysis: '', // 解析
        score: 1, // 分值
        multipScore: 0.5,
        scoreOptions: [],
        rules: {
          type: [{ required: true, message: '请选择类型', trigger: 'change' }],
          difficulty: [
            { required: true, message: '请选择难度', trigger: 'change' },
          ],
          title: [{ required: true, message: '请输入题干', trigger: 'blur' }],
          option: [
            { required: true, message: '请输入选项内容', trigger: 'change' },
          ],
          answer: [
            {
              required: true,
              message: '请选择或者输入答案',
              trigger: 'blur',
            },
          ],
          answerMultip: [
            {
              type: 'array',
              required: true,
              message: '请选择或者输入答案',
              trigger: 'blur',
            },
          ],
          aiScore: [{ validator: validateAiScore }],
          multipScore: [
            { required: true, trigger: 'blur', validator: validateMultipScore },
          ],
        },
      },
    }
  },
  watch: {
    'editForm.score': {
      deep: true,
      immediate: true,
      handler(newValue) {
        if (this.editForm.type === 2) {
          this.editForm.multipScore = newValue / 2
        }
      },
    },
  },
  created() {
    this.queryForm.questionTypeId = this.$route.params.id
  },
  mounted() {
    this.init()
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    // 初始化默认值
    async init() {
      this.search() // 查询列表
      this.queryForm.typeList = getOneDict('QUESTION_TYPE')
      this.queryForm.difficultyList = getOneDict('QUESTION_DIFFICULTY')
      this.editForm.type = 1 // 默认选中类型
      this.editForm.difficulty = 1 // 默认选中简单
      this.editForm.score = 1 // 默认得分为 1
      this.editForm.answer = '' // 默认答案为空
    },
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
        questionTypeId: this.queryForm.questionTypeId,
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
    // 添加选项
    addOption() {
      if (this.editForm.options.length >= 7) {
        alert('最多7个选项')
        return
      }

      const index = this.editForm.options.length
      this._addOption(index)
    },
    // 添加选项
    _addOption(index, value) {
      const lab = String.fromCharCode(65 + index)
      this.editForm.options.push({ lab: lab, value: value })
      this.editForm.answers.push({ lab: lab, value: value })

      this.editForm.rules['options.' + index + '.value'] = [
        { required: true, message: '请输入选项' + lab, trigger: 'change' },
      ]
    },
    // 删除选项
    delOption() {
      this.editForm.options.pop()
      this.editForm.answers.pop()
    },
    // 添加填空
    addFillBlanks() {
      const index = this.editForm.answers.length
      this._addFillBlanks(index)
    },
    // 添加填空
    _addFillBlanks(index, value) {
      let lab
      if ([3, 5].includes(this.editForm.type)) {
        lab = this.$tools.intToChinese(index + 1)
      } else {
        lab = String.fromCharCode(65 + index)
      }

      this.editForm.answers.push({
        lab: lab,
        value: !value ? '' : value.answer,
        score: !value ? '' : value.score,
      })
    },
    // 删除填空
    delFillBlanks() {
      this.editForm.answers.pop()
    },
    // 编辑器监听事件
    editorListener(id, value) {
      if (id.startsWith('option')) {
        const index = parseInt(id.substr(6).charCodeAt() - 65) // 选项赋值，特殊处理一下
        this.editForm.options[index].value = value
        return
      }
      this.editForm[id] = value

      if (this.editForm.type === 3 && id === 'title') {
        let underlineNum = value.match(/[_]{5,}/g)

        // 下划线不存在，则答案选项置空
        if (!underlineNum) {
          this.editForm.answers = []
          return false
        }

        // 下划线数量大于现有答案选项数量，则进行补充填空
        if (underlineNum.length > this.editForm.answers.length) {
          for (
            let index = this.editForm.answers.length;
            index < underlineNum.length;
            index++
          ) {
            const lab = this.$tools.intToChinese(index + 1)
            this.editForm.answers.push({
              lab,
              value: [],
              score: '',
            })
          }
        }

        // 下划线数量小于现有答案选项数量，则删除填空
        if (underlineNum.length < this.editForm.answers.length) {
          this.editForm.answers.length = underlineNum.length
        }
      }
    },
    // 更新类型
    updateType(value) {
      this.detailStatus = false
      this.$tools.resetData(this, 'editForm')
      this.editForm.type = value
      if (value == 5) this.editForm.ai = 2
    },
    // 组合添加或修改请求参数
    compositionParam(status) {
      if (this.queryForm.edit == 'false') {
        this.$message.warning('暂无此项权限！')
        return false
      }

      const params = {
        type: this.editForm.type,
        difficulty: this.editForm.difficulty,
        title: this.editForm.title,
        analysis: this.editForm.analysis,
        score: this.editForm.score,
        ai: this.editForm.ai,
      }

      status
        ? (params.questionTypeId = this.queryForm.questionTypeId)
        : (params.id = this.editForm.id)

      // 选项值(单选、多选)
      if ([1, 2].includes(params.type)) {
        params.options = this.editForm.options.reduce((acc, cur) => {
          acc.push(cur.value)
          return acc
        }, [])
      }

      // 分值选项 (多选【漏选得分】 填空【漏答得分、大小写不敏感】  问答【大小写不敏感】)
      if ([2, 3, 5].includes(params.type)) {
        params.scoreOptions = this.editForm.scoreOptions.reduce((acc, cur) => {
          acc.push(cur)
          return acc
        }, [])
      }

      // 答案（单选、判断、问答【非智能 ai=2】）
      if ([1, 4, 5].includes(params.type)) {
        params.answers = this.editForm.answer
      }

      // 答案（多选）
      if (params.type === 2) {
        params.answers = this.editForm.answerMultip
      }

      // 答案（填空、问答【智能 ai=1】）
      if (params.type === 3 || (params.ai === 1 && params.type === 5)) {
        params.answers = this.editForm.answers.reduce((acc, cur) => {
          acc.push(cur.value.join('\n'))
          return acc
        }, [])
      }

      // 分值选项对应的分值（非智能 ai=2 || 单选、判断）
      if (params.ai == 2 || [1, 4].includes(params.type)) {
        params.answerScores = params.score
      }

      // 分值选项对应的分值（多选）
      if (params.type == 2) {
        params.answerScores = this.editForm.multipScore
      }

      // 分值选项对应的分值（填空、问答）
      if ([3, 5].includes(params.type)) {
        params.answerScores = this.editForm.answers.reduce((acc, cur) => {
          acc.push(params.ai == 1 ? cur.score : params.score)
          return acc
        }, [])
        const sum = params.answerScores.reduce(
          (acc, cur) => acc + Number(cur),
          0
        )
        if (sum != params.score && params.ai == 1) {
          this.$message.warning('答案分值相加应等于总分值！')
          return false
        }
      }

      return params
    },
    // 添加试题
    add() {
      const params = this.compositionParam(true)
      if (!params) return

      this.$refs.questionDetail.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await questionAdd(params)
        this.resetQuery(res, '添加')
      })
    },
    // 修改试题
    edit() {
      const params = this.compositionParam(false)
      if (!params) return

      this.$refs.questionDetail.$refs['editForm'].validate((valid) => {
        if (!valid) {
          return false
        }
        this.$confirm('确定要修改？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(async () => {
            const res = await questionEdit(params)
            this.resetQuery(res, '修改')
          })
          .catch((err) => {
            console.log(err)
          })
      })
    },
    // 编辑试题的详细信息
    async questionEdit(id) {
      this.detailStatus = false
      const res = await questionGet({ id })
      if (res?.code != 200) {
        this.$message.error('查询失败！')
        return
      }

      this.editForm.id = res.data.id
      this.editForm.type = res.data.type
      this.editForm.difficulty = res.data.difficulty
      this.editForm.title = res.data.title
      this.editForm.answer = res.data.answers[0].answer[0]
      this.editForm.analysis = res.data.analysis
      this.editForm.score = res.data.score
      this.editForm.ai = res.data.ai
      this.editForm.state = res.data.state

      if (this.editForm.type === 1) {
        this.editForm.options = [] // 重置选项
        this.editForm.answers = [] // 重置答案列表
        for (let i = 0; i < res.data.options.length; i++) {
          this._addOption(i, res.data.options[i])
        }
      }

      if (this.editForm.type === 2) {
        this.editForm.answerMultip = []
        this.editForm.multipScore = ''
        this.editForm.options = [] // 重置选项
        this.editForm.answers = [] // 重置答案列表
        for (let i = 0; i < res.data.options.length; i++) {
          this._addOption(i, res.data.options[i])
        }
        this.editForm.scoreOptions =
          res.data.scoreOptions == null ? [] : res.data.scoreOptions
        this.editForm.answerMultip = res.data.answers.reduce((acc, cur) => {
          acc.push(...cur.answer)
          return acc
        }, [])
        this.editForm.multipScore = res.data.answers[0].score
      }

      if (this.editForm.type === 5 && this.editForm.ai === 2) {
        this.editForm.answers = [] // 重置答案列表
      }

      if (
        this.editForm.type === 3 ||
        (this.editForm.type === 5 && this.editForm.ai === 1)
      ) {
        this.$nextTick(() => {
          const answers = res.data.answers
          this.editForm.answers = [] // 重置答案列表
          for (let i = 0; i < answers.length; i++) {
            this._addFillBlanks(i, answers[i])
          }
          this.editForm.scoreOptions =
            res.data.scoreOptions == null ? [] : res.data.scoreOptions
        })
      }

      if (res.data.ai == 1 && res.data.type === 5) {
        this.editForm.answer = ''
      }
    },
    // 获取试题详情
    async showDetails(id) {
      this.detailStatus = true
      this.editForm.id = id
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
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
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
        this.editForm.id = null
        this.list.curPage = 1
        this.queryForm.state = ''
        this.query()
        this.$message.success(`${msg}成功！`)
        this.$tools.resetData(this, 'editForm')
      } else {
        this.$message.error(res.msg || `${msg}失败！`)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
}

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
  height: calc(100vh - 120px);
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
}

.pagination {
  font-weight: 400;
  text-align: center;
}

.el-radio,
.el-checkbox {
  margin-right: 10px;
}

.box-card-no-border {
  border: none;
}

.ai-answers {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  .ai-answer {
    width: 75%;
  }
  .ai-score {
    width: 20%;
  }
}
</style>
