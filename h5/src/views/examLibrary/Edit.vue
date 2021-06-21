<template>
  <div class="container">
    <!-- 导航 -->
    <div class="head">
      <el-link
        class="head-left"
        :underline="false"
        @click="goBack"
        icon="el-icon-back"
        >返回</el-link
      >
    </div>
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item label>
          <el-input placeholder="请输入编号" v-model="queryForm.id"></el-input>
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
              v-for="dict in queryForm.typeDictList"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label>
          <el-select placeholder="请输入难度" v-model="queryForm.difficulty">
            <el-option
              :key="parseInt(dict.dictKey)"
              :label="dict.dictValue"
              :value="parseInt(dict.dictKey)"
              v-for="dict in queryForm.difficultyDictList"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label>
          <el-input
            placeholder="分值大于"
            v-model="queryForm.startScore"
          ></el-input>
        </el-form-item>
        <el-form-item label>
          <el-input
            placeholder="分值小于"
            v-model="queryForm.endScore"
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
      <div class="content-left">
        <div class="left-top">添加题型</div>
        <div
          :class="[
            editForm.type === btn.type
              ? 'left-center left-center-active'
              : 'left-center'
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
      </div>
      <div class="content-center" v-if="list.questionList.length == 0">
        <div class="data-null">
          <img class="data-img" src="../../assets/img/data-null.png" alt="" />
          <span class="data-tip">抱歉！暂无信息</span>
        </div>
      </div>
      <div class="content-center" v-if="list.questionList.length > 0">
        <el-card
          :key="quesiton.id"
          class="center-card"
          shadow="hover"
          v-for="(quesiton, index) in list.questionList"
        >
          <div class="center-card-top">
            {{ index + 1 }}、{{ quesiton.title }}
          </div>
          <div class="center-card-bottom">
            <div class="card-bottom-left">
              <el-tag class="center-tag-danger" size="mini" type="danger">{{
                quesiton.typeName
              }}</el-tag>
              <el-tag class="center-tag-purple" effect="plain" size="mini">{{
                quesiton.difficultyName
              }}</el-tag>
              <el-tag effect="plain" size="mini" type="danger"
                >{{ quesiton.score }}分</el-tag
              >
              <el-tag effect="plain" size="mini">{{
                quesiton.updateUserName
              }}</el-tag>
            </div>
            <div class="card-bottom-right">
              <el-button
                @click="get(quesiton.id)"
                class="btn"
                icon="el-icon-document"
                plain
                round
                size="mini"
                type="primary"
                >详细</el-button
              >
              <el-button
                @click="copy(quesiton.id)"
                class="btn"
                icon="el-icon-document-copy"
                plain
                round
                size="mini"
                type="primary"
                >复制</el-button
              >
              <el-button
                @click="del(quesiton.id)"
                class="btn"
                icon="el-icon-delete"
                plain
                round
                size="mini"
                type="primary"
                >删除</el-button
              >
            </div>
          </div>
        </el-card>
        <!-- <el-pagination
            :total="1000"
            background
            class="pagination"
            hide-on-single-page="true"
            layout="prev, pager, next"
            page-size="5"
            small="false"
          ></el-pagination> -->
      </div>
      <div class="content-right">
        <el-form
          :model="editForm"
          :rules="editForm.rules"
          label-width="70px"
          ref="editForm"
          size="mini"
          v-model="labelPosition"
        >
          <el-row>
            <el-col :span="11">
              <el-form-item label="类型" prop="type">
                <el-select
                  disabled
                  placeholder="请选择类型"
                  v-model="editForm.type"
                >
                  <el-option
                    :key="parseInt(dict.dictKey)"
                    :label="dict.dictValue"
                    :value="parseInt(dict.dictKey)"
                    v-for="dict in queryForm.typeDictList"
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
                    v-for="dict in queryForm.difficultyDictList"
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
          <div v-if="editForm.type === 1 || editForm.type === 2">
            <el-form-item
              :key="'option' + option.lab"
              :label="'选项' + option.lab"
              :prop="'options.' + index + '.value'"
              v-for="(option, index) in editForm.options"
            >
              <Editor
                :id="'option' + option.lab"
                :value="option.value"
                @editorListener="editorListener"
              ></Editor>
            </el-form-item>
          </div>
          <el-form-item v-if="editForm.type === 1 || editForm.type === 2">
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
          <el-form-item label="答案" prop="answer" v-if="editForm.type === 2">
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
                title="单空有多个同义词，用三竖线分开。如：一般|||通常|||普遍"
                type="success"
              ></el-alert>
              <el-form-item
                :key="'answers' + index"
                :prop="'answers.' + index + '.value'"
                v-for="(answer, index) in editForm.answers"
              >
                <el-input v-model="answer.value">
                  <template slot="prepend">填空{{ answer.lab }}</template>
                </el-input>
              </el-form-item>
              <el-button
                :disabled="editForm.answers.length >= 7"
                @click="addFillBlanks()"
                class="btn2"
                style="margin-left: 78px"
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
          <el-form-item label="答案" v-if="editForm.type === 4">
            <el-radio-group v-model="editForm.answer">
              <el-radio
                :key="answer.lab"
                :label="answer.lab"
                v-for="answer in editForm.judgeAnswers"
                >{{ answer.lab }}</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item label="答案" prop="answer" v-if="editForm.type === 5">
            <Editor
              :value="editForm.answer"
              @editorListener="editorListener"
              id="answer"
            ></Editor>
          </el-form-item>
          <el-form-item label="解析">
            <Editor
              :value="editForm.analysis"
              @editorListener="editorListener"
              id="analysis"
            ></Editor>
          </el-form-item>
          <el-row>
            <el-col :span="11">
              <el-form-item label="分值" prop="score">
                <el-input-number
                  :max="100"
                  :min="1"
                  controls-position="right"
                  v-model.number="editForm.score"
                ></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="排序" prop="no">
                <el-input-number
                  :max="100000"
                  :min="1"
                  controls-position="right"
                  v-model.number="editForm.no"
                ></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="分值选项" v-if="editForm.type === 2">
            <el-checkbox-group v-model="editForm.scoreOptions">
              <el-tooltip
                class="item"
                content="默认全对得分"
                effect="dark"
                placement="top"
              >
                <el-checkbox label="1">半对半分</el-checkbox>
              </el-tooltip>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="分值选项" v-if="editForm.type === 3">
            <el-checkbox-group v-model="editForm.scoreOptions">
              <el-tooltip
                class="item"
                content="默认全对得分"
                effect="dark"
                placement="top"
              >
                <el-checkbox label="1">半对半分</el-checkbox>
              </el-tooltip>
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
              <el-tooltip
                class="item"
                content="默认等于答案得分"
                effect="dark"
                placement="top"
              >
                <el-checkbox label="4">包含答案得分</el-checkbox>
              </el-tooltip>
            </el-checkbox-group>
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
              @click="edit(false)"
              style="width: 164px; height: 40px"
              type="primary"
              v-if="editForm.id"
              >修改</el-button
            >
            <el-button
              @click="edit(true)"
              plain
              style="width: 164px; height: 40px; border-color: #fff"
              type="primary"
              v-if="editForm.id != null"
              >生成新版本</el-button
            >
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-dialog
      :visible.sync="fileForm.show"
      :show-close="false"
      width="30%"
      title="上传试题模板"
      :close-on-click-modal="false"
    >
      <el-form :model="fileForm" ref="fileForm">
        <input
          type="file"
          id="docId"
          accept=".doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        />
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="questionImport" type="primary">上传</el-button>
        <el-button @click="fileForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Editor from "@/components/Editor.vue"
export default {
  components: {
    Editor
  },
  data() {
    return {
      labelPosition: "left",
      list: {
        //列表数据
        total: 0, // 总条数
        curPage: 1, //当前第几页
        pageSize: 6, //每页多少条
        questionList: [] //列表数据
      },
      queryForm: {
        //查询表单
        id: "", //主键
        title: "", //标题
        type: null, //类型
        difficulty: null, //难度
        startScore: "", //得分大于
        endScore: "", //得分小于
        questionTypeName: "", //试题分类name
        questionTypeId: 1, //试题分类id
        difficultyDictList: [], //难度列表
        typeDictList: [] //类型列表
      },
      editForm: {
        //修改表单
        id: 0, //主键
        type: 1, //类型
        difficulty: 1, //难度
        title: "", //标题
        options: [
          {
            lab: "A",
            value: ""
          },
          {
            lab: "B",
            value: ""
          }
        ], //选项
        answer: "", //答案
        answerMultip: [],
        answers: [
          {
            lab: "A",
            value: ""
          },
          {
            lab: "B",
            value: ""
          }
        ], //答案
        judgeAnswers: [
          {
            lab: "对",
            value: ""
          },
          {
            lab: "错",
            value: ""
          }
        ],
        analysis: "", //解析
        score: 1, //分值
        no: 1, //排序
        scoreOptions: [],
        rules: {
          type: [{ required: true, message: "请选择类型", trigger: "change" }],
          difficulty: [
            { required: true, message: "请选择难度", trigger: "change" }
          ],
          title: [{ required: true, message: "请输入题干", trigger: "change" }],
          answer: [
            { required: true, message: "请选择答案", trigger: "change" }
          ],
          score: [{ required: true, message: "请输入分值", trigger: "change" }],
          no: [{ required: true, message: "请输入排序", trigger: "change" }]
        }
      },
      typeButtonGroup: [
        // 左侧按钮组1
        {
          type: 1,
          name: "单选题",
          img: require("@/assets/img/icon/radio-icon.png")
        },
        {
          type: 2,
          name: "多选题",
          img: require("@/assets/img/icon/checkbox-icon.png")
        },
        {
          type: 3,
          name: "填空题",
          img: require("@/assets/img/icon/blanks-icon.png")
        },
        {
          type: 4,
          name: "判断题",
          img: require("@/assets/img/icon/judge-icon.png")
        },
        {
          type: 5,
          name: "问答题",
          img: require("@/assets/img/icon/ask-icon.png")
        }
      ],
      handlerButtonGroup: [
        // 左侧按钮组2
        {
          name: "试题模板",
          img: require("@/assets/img/icon/template-icon.png")
        },
        {
          name: "试题导入",
          img: require("@/assets/img/icon/import-icon.png")
        }
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
        show: false
      }
    }
  },
  created() {
    const { id, name } = this.$route.query
    this.queryForm.questionTypeId = id
    this.queryForm.name = name
    this.init()
  },
  methods: {
    goBack() {
      this.$router.push("/examLibrary")
    },
    //初始化默认值
    async init() {
      this.query() //查询列表

      let typeDictData = await this.$https.dictListpage({
        dictIndex: "QUESTION_TYPE"
      })
      this.queryForm.typeDictList = typeDictData.data.list // 初始化类型下拉框

      let difficultyDictData = await this.$https.dictListpage({
        dictIndex: "QUESTION_DIFFICULTY"
      })
      this.queryForm.difficultyDictList = difficultyDictData.data.list // 初始化难度下拉框

      this.typeButtonGroup[0].checked = true //默认选中类型按钮
      this.editForm.type = 1 //默认选中类型
      this.editForm.difficulty = 1 //默认选中简单
      this.editForm.score = 1 //默认得分为1
      this.editForm.no = 1 //默认排序为1
      this.editForm.answer = "" //默认答案为空
    },
    // 查询
    async query() {
      let {
        data: { list, total }
      } = await this.$https.questionListPage({
        id: this.queryForm.id,
        questionTypeId: this.queryForm.questionTypeId,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        type: this.queryForm.type,
        difficulty: this.queryForm.difficulty,
        scoreStart: this.queryForm.scoreStart,
        scoreEnd: this.queryForm.scoreEnd,
        curPage: this.queryForm.curPage,
        pageSize: this.queryForm.pageSize
      })
      this.list.total = total
      this.list.questionList = list
    },
    //更新选项和答案
    updateOptionAndAnswer(value) {
      this.$refs.editForm.clearValidate(["answer"]) // 清理校验

      this.editForm.options = [] // 重置选项
      this.editForm.answers = [] //重置答案列表
      this.editForm.answer = "" //重置答案
      this.editForm.rules.answer = [
        { required: false, message: "请选择答案", trigger: "change" }
      ] //取消答案校验
      //如果是单选
      if (value === 1) {
        for (let i = 0; i < 2; i++) {
          this._addOption(i, "") //初始化两个选项
        }
        this.editForm.rules.answer = [
          { required: true, message: "请选择答案", trigger: "change" }
        ] //添加答案校验
        return
      }

      //如果是多选
      if (value === 2) {
        this.editForm.answer = []
        for (let i = 0; i < 2; i++) {
          this._addOption(i, "") //初始化两个选项
        }
        this.editForm.rules.answer = [
          {
            type: "array",
            required: true,
            message: "请选择答案",
            trigger: "change"
          }
        ] //添加答案校验
        return
      }

      //如果是填空
      if (value === 3) {
        for (let i = 0; i < 1; i++) {
          let lab = String.fromCharCode(65 + i)
          this.editForm.answers.push({ lab: lab, value: "" }) //初始化一个填空

          this.editForm.rules["answers." + i + ".value"] = [
            { required: true, message: "请输入填空" + lab, trigger: "change" }
          ] //添加答案校验
        }
        return
      }

      //如果是对错
      if (value === 4) {
        this.editForm.answers.push({ lab: "对", value: "" }) //初始化两个选项
        this.editForm.answers.push({ lab: "错", value: "" })
        this.editForm.rules.answer = [
          { required: true, message: "请选择答案", trigger: "change" }
        ] //添加答案校验
        return
      }

      //如果是问答
      if (value === 5) {
        this.editForm.answer = ""
        this.editForm.rules.answer = [
          { required: true, message: "请输入答案", trigger: "change" }
        ] //添加答案校验
      }
    },
    //添加选项
    addOption() {
      if (this.editForm.options.length >= 7) {
        alert("最多7个选项")
        return
      }

      let index = this.editForm.options.length
      this._addOption(index, "")
    },
    // 添加选项
    _addOption(index, value) {
      let lab = String.fromCharCode(65 + index)
      this.editForm.options.push({ lab: lab, value: value })
      this.editForm.answers.push({ lab: lab, value: value })

      this.editForm.rules["options." + index + ".value"] = [
        { required: true, message: "请输入选项" + lab, trigger: "change" }
      ]
    },
    // 删除选项
    delOption() {
      this.editForm.options.pop()
      this.editForm.answers.pop()
    },
    //添加填空
    addFillBlanks() {
      let index = this.editForm.answers.length
      this._addFillBlanks(index, "")
    },
    //添加填空
    _addFillBlanks(index, value) {
      let lab = String.fromCharCode(65 + index)
      this.editForm.answers.push({ lab: lab, value: value })

      this.editForm.rules["answers." + index + ".value"] = [
        { required: true, message: "请输入填空" + lab, trigger: "change" }
      ]
    },
    //删除填空
    delFillBlanks() {
      if (this.editForm.answers.length <= 1) {
        alert("最少1个填空")
        return
      }

      this.editForm.answers.pop()
      let index = this.editForm.answers.length
      this.editForm.rules["answers." + index + ".value"] = [
        { required: false, message: "请输入填空" + lab, trigger: "change" }
      ]
    },
    //编辑器监听事件
    editorListener(id, value) {
      if (id.startsWith("option")) {
        let index = parseInt(id.substr(6).charCodeAt() - 65) // 选项赋值，特殊处理一下
        this.editForm.options[index].value = value
        return
      }

      this.editForm[id] = value
    },
    //更新类型
    updateType(value) {
      Object.assign(this.$data.editForm, this.$options.data().editForm)
      this.editForm.type = value
    },
    //添加试题
    async add() {
      this.$refs["editForm"].validate(async valid => {
        if (!valid) {
          return false
        }

        let params = {
          type: this.editForm.type,
          difficulty: this.editForm.difficulty,
          title: this.editForm.title,
          analysis: this.editForm.analysis,
          score: this.editForm.score,
          no: this.editForm.no,
          questionTypeId: this.queryForm.questionTypeId
        }

        if (params.type === 1 || params.type === 2) {
          //如果是单选、多选，提取选项值
          let _options = []
          for (let i in this.editForm.options) {
            _options[i] = this.editForm.options[i].value
          }
          params.options = _options
        }
        if (params.type === 2 || params.type === 3) {
          //如果是多选、填空，提取分值选项
          let _scoreOptions = []
          for (let i in this.editForm.scoreOptions) {
            _scoreOptions[i] = this.editForm.scoreOptions[i]
          }
          params.scoreOptions = _scoreOptions
        }

        if (params.type === 1 || params.type === 4 || params.type === 5) {
          params.answer = this.editForm.answer
        } else if (params.type === 2) {
          //如果是多选，答案按逗号分隔
          params.answer = this.editForm.answerMultip.join(",")
        } else if (params.type === 3) {
          //如果是填空，答案之间按不可见字符回车分隔
          let _answer = []
          for (let i in this.editForm.answers) {
            _answer[i] = this.editForm.answers[i].value
          }
          params.answer = _answer.join("\n")
        }

        let res = await this.$https.questionAdd(params)
        if (res.code != 200) {
          alert(res.msg)
        }

        this.query()
      })
    },
    //修改试题
    edit(newVer) {
      this.$refs["editForm"].validate(valid => {
        if (!valid) {
          return false
        }

        let params = {
          id: this.editForm.id,
          type: this.editForm.type,
          difficulty: this.editForm.difficulty,
          title: this.editForm.title,
          analysis: this.editForm.analysis,
          score: this.editForm.score,
          no: this.editForm.no
        }

        if (params.type === 1 || params.type === 2) {
          //如果是单选、多选，提取选项值
          let _options = []
          for (let i in this.editForm.options) {
            _options[i] = this.editForm.options[i].value
          }
          params.options = _options
        }

        if (params.type === 2 || params.type === 3) {
          //如果是多选、填空，提取分值选项
          let _scoreOptions = []
          this.editForm.scoreOptions.forEach(element => {
            _scoreOptions.push(element)
          })
          params.scoreOptions = _scoreOptions
        }

        if (params.type === 1 || params.type === 4 || params.type === 5) {
          params.answer = this.editForm.answer
        } else if (params.type === 2) {
          //如果是多选，答案按逗号分隔
          params.answer = this.editForm.answer.join(",")
        } else if (params.type === 3) {
          //如果是填空，答案之间按不可见字符回车分隔
          let _answer = []
          for (let i in this.editForm.answers) {
            _answer[i] = this.editForm.answers[i].value
          }
          params.answer = _answer.join("\n")
        }

        var msg = newVer
          ? "确定要生成新版本？"
          : "当前修改会同步到引用的试卷，确定要修改？"
        this.$confirm(msg, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(async () => {
            let res = await this.$https.questionEdit(params)
            if (res.code != 200) {
              this.$message({
                type: "error",
                message: res.msg
              })
            }
            this.query()
          })
          .catch(() => {})
      })
    },
    // 获取试题
    async get(id) {
      let res = await this.$https.questionGet({ id })
      if (res.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.id = res.data.id
      this.editForm.type = res.data.type
      this.editForm.difficulty = res.data.difficulty
      this.editForm.title = res.data.title
      this.editForm.answer = res.data.answer
      this.editForm.answerMultip = res.data.answer.split(",")
      this.editForm.analysis = res.data.analysis
      this.editForm.score = res.data.score
      this.editForm.no = res.data.no

      if (this.editForm.type === 1) {
        this.editForm.options = [] // 重置选项
        this.editForm.answers = [] //重置答案列表
        for (let i = 0; i < res.data.options.length; i++) {
          this._addOption(i, res.data.options[i].options)
        }
      } else if (this.editForm.type === 2) {
        this.editForm.options = [] // 重置选项
        this.editForm.answers = [] //重置答案列表
        for (let i = 0; i < res.data.options.length; i++) {
          this._addOption(i, res.data.options[i].options)
        }

        this.editForm.answer = res.data.answer.split(",")
        this.editForm.scoreOptions =
          res.data.scoreOptions == null ? [] : res.data.scoreOptions.split(",")
      } else if (this.editForm.type === 3) {
        this.editForm.answers = [] //重置答案列表
        let answers = res.data.answer.split("\n")
        for (let i = 0; i < answers.length; i++) {
          this._addFillBlanks(i, answers[i])
        }

        this.editForm.scoreOptions =
          res.data.scoreOptions == null ? [] : res.data.scoreOptions.split(",")
      }
    },
    // 复制试题
    async copy(id) {
      await this.$https.questionCopy({ id })
      this.query()
    },
    // 删除试题
    del(id) {
      this.$confirm("确定要删除？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(async () => {
        await this.$https.questionDel({ id })
        this.query()
      })
    },
    // 下载试题模板
    async questionTemplate() {
      const template = await this.$https.questionTemplate({}, "blob")
      let blob = new Blob([template], { type: "application/msword" })
      let url = window.URL.createObjectURL(blob)
      let a = document.createElement("a")
      a.href = url
      a.download = "试题模板.doc"
      a.click()
      window.URL.revokeObjectURL(url)
    },
    // 上传试题
    async questionImport() {
      let formData = new FormData()
      formData.append("file", document.querySelector("#docId").files[0])
      formData.append("questionTypeId", this.queryForm.questionTypeId)
      const res = await this.$https.questionImport(
        formData,
        undefined,
        "multipart/form-data"
      )
      if (res.code == 200) {
        this.$tools.message("上传成功！")
        this.fileForm.show = false
        this.query()
      } else {
        this.$tools.message("上传失败！", error)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  display: flex;
  flex-direction: column;
  padding-bottom: 10px;
  padding-top: 0;
}

.head {
  width: 100%;
  height: 50px;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  .head-left {
    color: #fff;
  }
}

.search {
  height: 80px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content {
  display: flex;
  width: 100%;
  padding: 0 20px;
  margin: 0 auto;
}

.content-left {
  width: 145px;
  background: #fff;
  border-radius: 5px;
  box-shadow: 0 0 13px 3px rgba(30, 159, 255, 0.15);
  .left-top {
    background-color: #0095e5;
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;
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
    height: 70px;
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
  .center-card-top {
    height: 20px;
    font-size: 14px;
    text-align: left;
  }
  .center-card-bottom {
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;
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
  width: 460px;
  background: #fff;
  border-radius: 5px;
  padding: 10px 0 0 0;
  box-shadow: 0 0 13px 3px rgba(191, 198, 204, 0.15);
}

.btn2 {
  width: 65px;
  height: 25px;
  padding: 0px;
}

.form-inline .el-form-item {
  width: 140px;
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
.el-input /deep/.el-input-group__prepend {
  background-color: #fff;
  border: 0px;
  width: 38px;
}
/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}
</style>
