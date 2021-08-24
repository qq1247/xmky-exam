<template>
  <div class="container">
    <!-- 导航 -->
    <EditHeader :title="queryForm.name"></EditHeader>

    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline">
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
            <el-select placeholder="请输入类型" v-model="queryForm.type">
              <el-option
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
                v-for="dict in queryForm.typeList"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-select placeholder="请输入难度" v-model="queryForm.difficulty">
              <el-option
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
                v-for="dict in queryForm.difficultyList"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label>
            <el-input
              placeholder="分值大于"
              v-model="queryForm.scoreStart"
            ></el-input>
          </el-form-item>
          <el-form-item label>
            <el-input
              placeholder="分值小于"
              v-model="queryForm.scoreEnd"
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
        <div class="left-top">添加题型</div>
        <div
          :class="[
            editForm.type === btn.type
              ? 'left-center left-center-active'
              : 'left-center',
          ]"
          :key="btn.type"
          @click="updateType(btn.type)"
          v-for="btn in typeButtonGroup"
        >
          <img :src="btn.img" />
          {{ btn.name }}
          <img src="@/assets/img/icon/active-icon.png" />
        </div>
        <div class="splitLine"></div>
        <div class="left-bottom" @click="questionTemplate">
          <img src="../../assets/img/icon/template-icon.png" />
          试题模板
        </div>
        <div class="left-bottom" @click="fileForm.show = true">
          <img src="../../assets/img/icon/import-icon.png" />
          试题导入
        </div>
      </el-scrollbar>
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-center">
        <template v-if="list.questionList.length > 0">
          <el-card
            :key="question.id"
            :class="[
              'center-card',
              question.id == editForm.id ? 'center-card-active' : '',
            ]"
            shadow="hover"
            v-for="question in list.questionList"
          >
            <div
              class="center-card-top"
              v-html="`${question.id}、${question.title}`"
            ></div>
            <div class="center-card-bottom">
              <div class="card-bottom-left">
                <el-tag class="center-tag-danger" size="mini" type="danger">{{
                  question.typeName
                }}</el-tag>
                <el-tag class="center-tag-purple" effect="plain" size="mini">{{
                  question.difficultyName
                }}</el-tag>
                <el-tag effect="plain" size="mini" type="danger"
                  >{{ question.score }}分</el-tag
                >
                <el-tag effect="plain" size="mini">{{
                  question.updateUserName
                }}</el-tag>
                <el-tag
                  :type="question.state == 1 ? 'info' : 'danger'"
                  effect="dark"
                  size="mini"
                  >{{ question.state == 1 ? '发布' : '草稿' }}</el-tag
                >
              </div>
              <div class="card-bottom-right">
                <el-button
                  @click="get(question.id)"
                  class="btn"
                  icon="el-icon-document"
                  plain
                  round
                  size="mini"
                  type="primary"
                  >详细</el-button
                >
                <el-button
                  @click="copy(question.id)"
                  class="btn"
                  icon="el-icon-document-copy"
                  plain
                  round
                  size="mini"
                  type="primary"
                  >复制</el-button
                >
                <el-button
                  @click="del(question.id)"
                  class="btn"
                  icon="el-icon-delete"
                  plain
                  round
                  size="mini"
                  type="primary"
                  >删除</el-button
                >
                <el-button
                  v-if="question.state == 2"
                  @click="publish(question.id, question.state)"
                  class="btn"
                  icon="el-icon-share"
                  plain
                  round
                  size="mini"
                  type="primary"
                  >发布</el-button
                >
              </div>
            </div>
          </el-card>
          <!-- 分页 -->
          <el-pagination
            background
            layout="prev, pager, next"
            prev-text="上一页"
            next-text="下一页"
            hide-on-single-page
            :total="list.total"
            :page-size="list.pageSize"
            :current-page="list.curPage"
            @current-change="pageChange"
          ></el-pagination>
        </template>
        <el-empty v-else description="暂无试题">
          <img slot="image" src="../../assets/img/data-null.png" alt="" />
        </el-empty>
      </el-scrollbar>
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-right">
        <el-form
          :model="editForm"
          ref="editForm"
          :rules="editForm.rules"
          label-width="80px"
          size="mini"
          v-model="labelPosition"
        >
          <el-row>
            <el-col :span="11">
              <el-form-item label="类型">
                <el-select
                  disabled
                  placeholder="请选择类型"
                  v-model="editForm.type"
                >
                  <el-option
                    :key="parseInt(dict.dictKey)"
                    :label="dict.dictValue"
                    :value="parseInt(dict.dictKey)"
                    v-for="dict in queryForm.typeList"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="难度" prop="difficulty">
                <el-select
                  placeholder="请选择难度"
                  v-model="editForm.difficulty"
                >
                  <el-option
                    :key="parseInt(dict.dictKey)"
                    :label="dict.dictValue"
                    :value="parseInt(dict.dictKey)"
                    v-for="dict in queryForm.difficultyList"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="题干" prop="title">
            <Editor
              :value="editForm.title"
              @editorListener="editorListener"
              id="title"
            ></Editor>
          </el-form-item>

          <template v-if="editForm.type === 1 || editForm.type === 2">
            <el-form-item
              :key="'option' + option.lab"
              :label="'选项' + option.lab"
              :prop="`options.${index}.value`"
              :rules="editForm.rules.option"
              v-for="(option, index) in editForm.options"
            >
              <Editor
                :id="'option' + option.lab"
                :value="option.value"
                @editorListener="editorListener"
              ></Editor>
            </el-form-item>
            <!-- 选项按钮 -->
            <el-form-item>
              <el-button
                :disabled="editForm.options.length >= 7"
                @click="addOption()"
                class="btn2"
                type="primary"
                >+添加选项</el-button
              >
              <el-button
                :disabled="editForm.options.length <= 2"
                @click="delOption()"
                class="btn2"
                type="primary"
                >-删除选项</el-button
              >
            </el-form-item>
          </template>

          <el-row>
            <el-col :span="13">
              <el-form-item label="分值" prop="score">
                <el-input-number
                  :max="100"
                  :min="1"
                  :step="1"
                  controls-position="right"
                  v-model.number="editForm.score"
                ></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="9">
              <el-form-item label="智能阅卷" prop="ai">
                <el-switch
                  active-color="#0094e5"
                  :active-value="1"
                  inactive-color="#ff4949"
                  :inactive-value="2"
                  v-model="editForm.ai"
                ></el-switch>
              </el-form-item>
            </el-col>
          </el-row>

          <template v-if="editForm.ai === 1">
            <el-row v-if="editForm.type === 2">
              <el-col :span="8">
                <el-form-item>
                  <el-checkbox-group v-model="editForm.scoreOptions">
                    <el-tooltip
                      class="item"
                      content="默认全对得分"
                      effect="dark"
                      placement="top"
                    >
                      <el-checkbox label="1">漏选得分</el-checkbox>
                    </el-tooltip>
                  </el-checkbox-group>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  v-if="editForm.scoreOptions.length > 0"
                  prop="multipScore"
                  class="ai-score"
                  :show-message="editForm.ai === 1 ? true : false"
                >
                  <el-input
                    v-if="editForm.ai === 1"
                    v-model="editForm.multipScore"
                  >
                    <template slot="append">分</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item v-if="editForm.type === 3">
              <el-checkbox-group v-model="editForm.scoreOptions">
                <el-tooltip
                  class="item"
                  content="默认答案有顺序"
                  effect="dark"
                  placement="top"
                >
                  <el-checkbox label="2">答案无顺序</el-checkbox>
                </el-tooltip>
                <el-tooltip
                  class="item"
                  content="默认大小写敏感"
                  effect="dark"
                  placement="top"
                >
                  <el-checkbox label="3">大小写不敏感</el-checkbox>
                </el-tooltip>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item v-if="editForm.type === 5">
              <el-checkbox-group v-model="editForm.scoreOptions">
                <el-tooltip
                  class="item"
                  content="大小写不敏感"
                  effect="dark"
                  placement="top"
                >
                  <el-checkbox label="3">大小写不敏感</el-checkbox>
                </el-tooltip>
              </el-checkbox-group>
            </el-form-item>
          </template>

          <el-form-item label="答案" prop="answer" v-if="editForm.type === 1">
            <el-radio-group v-model="editForm.answer">
              <el-radio
                :key="answer.lab"
                :label="answer.lab"
                v-for="answer in editForm.answers"
                >{{ answer.lab }}</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="答案"
            prop="answerMultip"
            v-if="editForm.type === 2"
          >
            <el-checkbox-group v-model="editForm.answerMultip">
              <el-checkbox
                :key="answer.lab"
                :label="answer.lab"
                v-for="answer in editForm.answers"
                >{{ answer.lab }}</el-checkbox
              >
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="答案" v-if="editForm.type === 3">
            <el-card class="el-card" shadow="never">
              <el-alert
                :closable="false"
                title="单个填空有多个同义词，可添加多个标签。如：人民、公民、群众"
                type="success"
              ></el-alert>
              <el-row
                :gutter="10"
                v-for="(answer, index) in editForm.answers"
                :key="index"
              >
                <el-col :span="16">
                  <el-form-item
                    :prop="`answers.${index}.value`"
                    :rules="editForm.rules.answerMultip"
                  >
                    <span
                      style="margin: 0 15px; color: #838ee9; font-size: 12px"
                      >填空{{ answer.lab }}</span
                    >
                    <el-select
                      v-model="answer.value"
                      multiple
                      filterable
                      remote
                      allow-create
                      default-first-option
                      placeholder="请填写答案"
                    >
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :prop="`answers.${index}.score`"
                    :rules="editForm.rules.aiScore"
                    :show-message="editForm.ai === 1 ? true : false"
                  >
                    <el-input v-if="editForm.ai === 1" v-model="answer.score">
                      <template slot="append">分</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-button
                :disabled="editForm.answers.length >= 7"
                @click="addFillBlanks()"
                class="btn2"
                style="margin-left: 65px"
                type="primary"
                >+添加填空</el-button
              >
              <el-button
                :disabled="editForm.answers.length <= 1"
                @click="delFillBlanks()"
                class="btn2"
                type="primary"
                >-删除填空</el-button
              >
            </el-card>
          </el-form-item>
          <el-form-item label="答案" prop="answer" v-if="editForm.type === 4">
            <el-radio-group v-model="editForm.answer">
              <el-radio
                :key="answer.lab"
                :label="answer.lab"
                v-for="answer in editForm.judgeAnswers"
                >{{ answer.lab }}</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="答案"
            v-if="editForm.type === 5 && editForm.ai === 1"
          >
            <el-card class="el-card" shadow="never">
              <el-alert
                :closable="false"
                title="单个关键词有多个同义词，可添加多个标签。如：人民、公民、群众"
                type="success"
              ></el-alert>
              <el-row
                :gutter="10"
                v-for="(answer, index) in editForm.answers"
                :key="index"
              >
                <el-col :span="16">
                  <el-form-item
                    :prop="`answers.${index}.value`"
                    :rules="editForm.rules.answerMultip"
                  >
                    <span
                      style="margin: 0 15px; color: #838ee9; font-size: 12px"
                      >关键词</span
                    >
                    <el-select
                      v-model="answer.value"
                      multiple
                      filterable
                      remote
                      allow-create
                      default-first-option
                      placeholder="请填写答案"
                    >
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    :prop="`answers.${index}.score`"
                    :rules="editForm.rules.aiScore"
                    :show-message="editForm.ai === 1 ? true : false"
                  >
                    <el-input v-if="editForm.ai === 1" v-model="answer.score">
                      <template slot="append">分</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-button
                :disabled="editForm.answers.length >= 7"
                @click="addFillBlanks()"
                class="btn2"
                style="margin-left: 65px"
                type="primary"
                >+添加关键词</el-button
              >
              <el-button
                :disabled="editForm.answers.length <= 1"
                @click="delFillBlanks()"
                class="btn2"
                type="primary"
                >-删除关键词</el-button
              >
            </el-card>
          </el-form-item>
          <el-form-item
            label="答案"
            prop="answer"
            v-if="editForm.type === 5 && editForm.ai === 2"
          >
            <Editor
              :value="editForm.answer"
              @editorListener="editorListener"
              id="answer"
            ></Editor>
          </el-form-item>
          <el-form-item label="解析" prop="analysis">
            <Editor
              :value="editForm.analysis"
              @editorListener="editorListener"
              id="analysis"
            ></Editor>
          </el-form-item>
          <el-form-item>
            <el-button
              @click="add()"
              style="width: 164px; height: 40px"
              type="primary"
              v-if="!editForm.id"
              >添加</el-button
            >
            <el-button
              @click="edit()"
              style="width: 164px; height: 40px"
              type="primary"
              v-if="editForm.id"
              >修改</el-button
            >
          </el-form-item>
        </el-form>
      </el-scrollbar>
    </div>

    <!-- 上传试题模板 -->
    <el-dialog
      :visible.sync="fileForm.show"
      :show-close="false"
      width="40%"
      title="上传并解析试题"
      :close-on-click-modal="false"
      @close="templateClear('templateUpload')"
    >
      <el-form :model="fileForm" ref="fileForm">
        <Upload
          type="word"
          ref="templateUpload"
          @success="templateSucess"
          @remove="templateRemove"
        />
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button
          @click="questionImport"
          :loading="fileForm.isAnalysis"
          type="primary"
          >解析试题</el-button
        >
        <el-button @click="fileForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { dictListPage } from '@/api/base'
import Upload from '@/components/upload'
import {
  questionListPage,
  questionAdd,
  questionGet,
  questionCopy,
  questionDel,
  questionTemplate,
  questionImport,
  questionEdit,
  questionPublish,
} from '@/api/question'
import Editor from '@/components/Editor.vue'
import EditHeader from '@/components/EditHeader.vue'
export default {
  components: {
    Editor,
    EditHeader,
    Upload,
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
      labelPosition: 'left',
      value: '',
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
        scoreStart: '', // 得分大于
        scoreEnd: '', // 得分小于
        questionTypeName: '', // 试题分类name
        questionTypeId: 1, // 试题分类id
        difficultyList: [], // 难度列表
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
        multipScore: '',
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
        ], // 答案
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
              trigger: 'change',
            },
          ],
          answerMultip: [
            {
              type: 'array',
              required: true,
              message: '请选择或者输入答案',
              trigger: 'change',
            },
          ],
          score: [{ required: true, message: '请输入分值', trigger: 'change' }],
          aiScore: [{ validator: validateAiScore }],
          multipScore: [{ validator: validateMultipScore }],
        },
      },
      typeButtonGroup: [
        // 左侧按钮组1
        {
          type: 1,
          name: '单选题',
          img: require('@/assets/img/icon/radio-icon.png'),
        },
        {
          type: 2,
          name: '多选题',
          img: require('@/assets/img/icon/checkbox-icon.png'),
        },
        {
          type: 3,
          name: '填空题',
          img: require('@/assets/img/icon/blanks-icon.png'),
        },
        {
          type: 4,
          name: '判断题',
          img: require('@/assets/img/icon/judge-icon.png'),
        },
        {
          type: 5,
          name: '问答题',
          img: require('@/assets/img/icon/ask-icon.png'),
        },
      ],
      handlerButtonGroup: [
        // 左侧按钮组2
        {
          name: '试题模板',
          img: require('@/assets/img/icon/template-icon.png'),
        },
        {
          name: '试题导入',
          img: require('@/assets/img/icon/import-icon.png'),
        },
        /* {
          name: "试题导出",
          img: require("@/assets/img/icon/export-icon.png")
        },
        {
          name: "清空试题",
          img: require("@/assets/img/icon/clear-icon.png")
        } */
      ],
      fileForm: {
        show: false,
        questionDocIds: [],
        isAnalysis: false,
      },
    }
  },
  created() {
    const { id, name, edit } = this.$route.query
    this.queryForm.questionTypeId = id
    this.queryForm.name = name
    this.queryForm.edit = edit
    this.init()
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    // 初始化默认值
    async init() {
      this.search() // 查询列表

      const typeDictData = await dictListPage({
        dictIndex: 'QUESTION_TYPE',
      })

      this.queryForm.typeList = typeDictData.data.list // 初始化类型下拉框

      const difficultyDictData = await dictListPage({
        dictIndex: 'QUESTION_DIFFICULTY',
      })

      this.queryForm.difficultyList = difficultyDictData.data.list // 初始化难度下拉框

      this.typeButtonGroup[0].checked = true // 默认选中类型按钮
      this.editForm.type = 1 // 默认选中类型
      this.editForm.difficulty = 1 // 默认选中简单
      this.editForm.score = 1 // 默认得分为1
      this.editForm.answer = '' // 默认答案为空
    },
    // 查询
    async query() {
      const {
        data: { list, total },
      } = await questionListPage({
        id: this.queryForm.id,
        questionTypeId: this.queryForm.questionTypeId,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        type: this.queryForm.type,
        difficulty: this.queryForm.difficulty,
        scoreStart: this.queryForm.scoreStart,
        scoreEnd: this.queryForm.scoreEnd,
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
      const lab = String.fromCharCode(65 + index)
      this.editForm.answers.push({
        lab: lab,
        value: value.answer,
        score: value.score,
      })

      this.editForm.rules['answers.' + index + '.value'] = [
        { required: true, message: '请输入填空' + lab, trigger: 'change' },
      ]
    },
    // 删除填空
    delFillBlanks() {
      if (this.editForm.answers.length <= 1) {
        alert('最少1个填空')
        return
      }

      this.editForm.answers.pop()
      const index = this.editForm.answers.length
      this.editForm.rules['answers.' + index + '.value'] = [
        { required: false, message: '请输入填空' + lab, trigger: 'change' },
      ]
    },
    // 编辑器监听事件
    editorListener(id, value) {
      if (id.startsWith('option')) {
        const index = parseInt(id.substr(6).charCodeAt() - 65) // 选项赋值，特殊处理一下
        this.editForm.options[index].value = value
        return
      }

      this.editForm[id] = value
    },
    // 更新类型
    updateType(value) {
      this.$tools.resetData(this, 'editForm')
      this.editForm.type = value
      if (value == 5) this.editForm.ai = 2
    },
    // 组合添加或修改请求参数
    compistionParam(status) {
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
        params.scores = params.score
      }

      // 分值选项对应的分值（多选）
      if (params.ai == 1 && params.type == 2) {
        params.scores =
          params.scoreOptions.length > 0 ? this.editForm.multipScore : 0
      }

      // 分值选项对应的分值（填空、问答）
      if ([3, 5].includes(params.type)) {
        params.scores = this.editForm.answers.reduce((acc, cur) => {
          acc.push(params.ai == 1 ? cur.score : params.score)
          return acc
        }, [])
        const sum = params.scores.reduce((acc, cur) => acc + Number(cur), 0)
        if (sum != params.score && params.ai == 1) {
          this.$message.warning('答案分值相加应等于总分值！')
          return false
        }
      }

      return params
    },
    // 添加试题
    add() {
      const params = this.compistionParam(true)
      if (!params) return

      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await questionAdd(params)
        this.resetQuery(res, '添加')
      })
    },
    // 修改试题
    edit() {
      const params = this.compistionParam(false)
      if (!params) return

      this.$refs['editForm'].validate((valid) => {
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
    // 获取试题
    async get(id) {
      const res = await questionGet({ id })
      if (res?.code != 200) {
        this.$message.error('查询失败！')
        return
      }

      this.editForm.id = res.data.id
      this.editForm.type = res.data.type
      this.editForm.difficulty = res.data.difficulty
      this.editForm.title = res.data.title
      this.editForm.answer = res.data.answers[0].answer
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
          res.data.scoreOptions == null ? [] : res.data.scoreOptions.split(',')
        this.editForm.answerMultip = res.data.answers.reduce((acc, cur) => {
          acc.push(cur.answer)
          return acc
        }, [])
        this.editForm.multipScore = res.data.answers[0].score
      }
      if ([3, 5].includes(this.editForm.type)) {
        this.editForm.answers = [] // 重置答案列表
        const answers = res.data.answers
        for (let i = 0; i < answers.length; i++) {
          this._addFillBlanks(i, answers[i])
        }

        this.editForm.scoreOptions =
          res.data.scoreOptions == null ? [] : res.data.scoreOptions.split(',')
      }

      if (res.data.ai == 1 && res.data.type === 5) {
        this.editForm.answer = ''
      }
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
        const res = await questionDel({ id })
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
        id,
      })
      this.resetQuery(res, '发布试题')
    },
    // 获取试题模板
    async questionTemplate() {
      const template = await questionTemplate({}, 'blob')
      const blob = new Blob([template], { type: 'application/msword' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = '试题模板.docx'
      a.click()
      window.URL.revokeObjectURL(url)
    },
    // 解析试题
    async questionImport() {
      if (this.fileForm.questionDocIds.length === 0) {
        this.$message.warning('请选择需解析的文件')
        return
      }
      this.fileForm.isAnalysis = true
      const res = await questionImport({
        fileId: this.fileForm.questionDocIds[0].response.data.fileIds,
        questionTypeId: this.queryForm.questionTypeId,
      })
      if (res?.code == 200) {
        this.$message.success('解析成功！')
        this.templateClear()
        this.fileForm.isAnalysis = false
        this.fileForm.show = false
        this.query()
      } else {
        this.$message.error('解析失败！')
      }
    },
    // 还原数据并查询
    resetQuery(res, msg) {
      if (res?.code === 200) {
        this.editForm.id = null
        this.list.curPage = 1
        this.query()
        this.$message.success(`${msg}成功！`)
        this.$tools.resetData(this, 'editForm')
      } else {
        this.$message.error(res.msg || `${msg}失败！`)
      }
    },
    // 上传试题模板成功
    templateSucess(res, file, fileList) {
      this.fileForm.questionDocIds = fileList
    },
    // 上传试题模板失败
    templateClear(ref) {
      this.$refs[ref].clear()
    },
    // 删除试题模板
    templateRemove(file, fileList) {
      this.fileForm.questionDocIds = []
      console.log('upload_remove', file, fileList)
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding-bottom: 10px;
  padding-top: 110px;
  margin: 0 auto;
}

.form-inline {
  height: 60px;
  padding: 10px 20px;
  position: fixed;
  top: 50px;
  left: 0;
  right: 0;
  background: #f7f8f9;
  z-index: 1500;
  .el-form-item {
    width: 140px;
    margin-bottom: 0;
  }
}

.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 110px);
  padding: 0 20px;
  margin: 0 auto;
}

.content-left {
  width: 145px;
  background: #fff;
  border-radius: 5px;
  box-shadow: 0 0 13px 3px rgba(30, 159, 255, 0.15);
  .left-top {
    background: #0095e5;
    width: 100%;
    height: 40px;
    line-height: 40px;
    text-align: left;
    padding-left: 10px;
    font-size: 14px;
    color: #fff;
  }
  .left-center {
    width: 110px;
    height: 40px;
    border: 1px solid #eeeeff;
    margin: 7px auto;
    line-height: 40px;
    position: relative;
    cursor: pointer;
    display: flex;
    align-items: center;
    font-size: 14px;
    &:hover {
      border: 1px solid #0095e5;
      img:last-child {
        display: block;
      }
    }
    img {
      &:first-child {
        width: 22px;
        height: 22px;
        margin: 0 10px;
      }
      &:last-child {
        width: 28px;
        height: 22px;
        position: absolute;
        bottom: -1px;
        right: 0px;
        display: none;
      }
    }
  }
  .left-center-active {
    border: 1px solid #0095e5;
    img:last-child {
      display: block;
    }
  }
  .splitLine {
    background: #eee;
    width: 100%;
    height: 1px;
    margin: 16px auto;
  }
  .left-bottom {
    width: 110px;
    height: 40px;
    background: #eee;
    margin: 7px auto;
    text-align: center;
    line-height: 40px;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    justify-content: center;
    align-items: center;
    &:hover {
      border: 1px solid #0095e5;
      color: #0095e5;
    }
    img {
      width: 16px;
      height: 16px;
      margin-right: 10px;
    }
  }
}

.content-center {
  flex: 1;
  .center-card {
    cursor: pointer;
    margin: 0 10px 10px 10px;
    padding: 0 5px;
    display: flex;
    flex-direction: column;
    &:hover {
      border: 1px solid #0095e5;
      box-shadow: 0 0 7px 2px rgba(0, 149, 229, 0.15);
      .card-bottom-right {
        display: block;
      }
    }
  }
  .center-card-active {
    border: 1px solid #0095e5;
    box-shadow: 0 0 7px 2px rgba(0, 149, 229, 0.15);
  }
  .center-card-top {
    font-size: 14px;
    text-align: left;
    display: flex;
    align-items: center;
    p {
      margin: 0;
      padding: 0;
    }
  }
  .center-card-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 10px 0 5px;
    .el-tag {
      margin-right: 8px;
    }
    .center-tag-danger {
      background-color: #feeddc;
      border-color: #fddbb9;
      color: #fa8718;
      height: 20px;
      line-height: 18px;
    }
    .center-tag-purple {
      border-color: #daddf9;
      color: #838eea;
      height: 20px;
      line-height: 18px;
    }
    .card-bottom-right {
      display: none;
    }
    .btn {
      padding: 5px 10px;
    }
  }
}

.content-right {
  width: 500px;
  background: #fff;
  border-radius: 5px;
  padding: 10px 0 0 0;
  box-shadow: 0 0 13px 3px rgba(191, 198, 204, 0.15);
}

.btn2 {
  padding: 0 10px;
  height: 25px;
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

/deep/ .el-card__body {
  padding: 5px;
}

.el-alert--success.is-light {
  background-color: #ecf7fd;
  color: #0095e5;
  margin-bottom: 10px;
}

.el-alert {
  padding: 0px;
}

/deep/ .el-alert__title {
  font-size: 12px;
  color: #0095e5;
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

.el-input /deep/.el-input-group__prepend,
.el-input /deep/.el-input-group__append {
  background-color: #fff;
  padding: 0 10px;
}

/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}

/deep/.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #0095e5;
  color: #fff;
}

/deep/.el-pagination.is-background .btn-next,
/deep/.el-pagination.is-background .btn-prev,
/deep/.el-pagination.is-background .el-pager li {
  margin: 0 3px;
  min-width: 35px;
  border: 1px solid #d4dfd9;
  background-color: #fff;
  padding: 0 10px;
}
</style>
