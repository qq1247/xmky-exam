<template>
  <div class="container">
    <!-- 导航 -->
    <div class="head">
      <div class="left">
        <el-link class="back" href icon="el-icon-back">返回</el-link>
      </div>
      <div class="right">
        <el-link class="back" href icon>
          返回
          <i class="el-icon-right el-icon--right"></i>
        </el-link>
      </div>
    </div>
    <!-- 搜索 -->
    <div class="search">
      <el-form :inline="true" :model="queryForm" class="form-inline">
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
              :key="parseInt(dict.DICT_KEY)"
              :label="dict.DICT_VALUE"
              :value="parseInt(dict.DICT_KEY)"
              v-for="dict in queryForm.typeDictList"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label>
          <el-select placeholder="请输入难度" v-model="queryForm.difficulty">
            <el-option
              :key="parseInt(dict.DICT_KEY)"
              :label="dict.DICT_VALUE"
              :value="parseInt(dict.DICT_KEY)"
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
        <el-form-item style="width: 200px">
          <el-button @click="query" icon="el-icon-search" type="primary"
            >查询</el-button
          >
        </el-form-item>
        <el-form-item></el-form-item>
      </el-form>
    </div>
    <!-- 内容 -->
    <div class="content">
      <div class="opt">
        <div class="left">
          <div class="left_inner_top">添加题型</div>
          <div
            :class="[
              editForm.type === btn.type
                ? 'left_inner_center left_inner_center1'
                : 'left_inner_center'
            ]"
            :key="btn.type"
            @click="updateType(btn.type)"
            v-for="btn in btnGroup1"
          >
            <img :src="btn.img" />
            <span>{{ btn.name }}</span>
            <img src="@/assets/img/icon/icon6.png" />
          </div>
          <hr class="splitLine" />
          <div
            :key="btn.type"
            class="left_inner_bottom"
            v-for="btn in btnGroup2"
          >
            <img :src="btn.img" />
            <span>{{ btn.name }}</span>
          </div>
        </div>
        <div class="center">
          <el-card
            :key="quesiton.ID"
            class="center_inner_card"
            shadow="hover"
            v-for="quesiton in list.questionList"
          >
            <div class="center_inner_card1">
              <span>{{ quesiton.ID }}、</span>
              {{ quesiton.TITLE }}
            </div>
            <div class="center_inner_card2">
              <el-tag class="center_inner_card21" size="mini" type="danger">{{
                quesiton.TYPE_NAME
              }}</el-tag>
              <el-tag class="center_inner_card22" effect="plain" size="mini">{{
                quesiton.DIFFICULTY_NAME
              }}</el-tag>
              <el-tag effect="plain" size="mini" type="danger"
                >{{ quesiton.SCORE }}分</el-tag
              >
              <el-tag effect="plain" size="mini">{{
                quesiton.UPDATE_USER_NAME
              }}</el-tag>
            </div>
            <div class="center_inner_card3">
              <el-button
                @click="get(quesiton.ID)"
                class="btn"
                icon="el-icon-edit"
                plain
                round
                size="mini"
                type="primary"
                >详细</el-button
              >
              <el-button
                class="btn"
                icon="el-icon-edit"
                plain
                round
                size="mini"
                type="primary"
                >复制</el-button
              >
              <el-button
                @click="del(quesiton.ID)"
                class="btn"
                icon="el-icon-edit"
                plain
                round
                size="mini"
                type="primary"
                >删除</el-button
              >
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
          ></el-pagination>-->
        </div>
        <div class="right">
          <el-form
            :model="editForm"
            :rules="editForm.rules"
            label-width="70px"
            ref="editForm"
            size="mini"
          >
            <el-row>
              <el-col :span="12">
                <el-form-item label="类型" prop="type">
                  <el-select
                    @change="updateOptionAndAnswer"
                    disabled
                    placeholder="请选择类型"
                    v-model="editForm.type"
                  >
                    <el-option
                      :key="parseInt(dict.DICT_KEY)"
                      :label="dict.DICT_VALUE"
                      :value="parseInt(dict.DICT_KEY)"
                      v-for="dict in queryForm.typeDictList"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="难度" prop="difficulty">
                  <el-select
                    placeholder="请选择难度"
                    v-model="editForm.difficulty"
                  >
                    <el-option
                      :key="parseInt(dict.DICT_KEY)"
                      :label="dict.DICT_VALUE"
                      :value="parseInt(dict.DICT_KEY)"
                      v-for="dict in queryForm.difficultyDictList"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="题干" prop="title">
              <Editor
                :height="70"
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
            <el-form-item
              label
              v-if="editForm.type === 1 || editForm.type === 2"
            >
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
            <el-form-item
              label="答案"
              prop="answer"
              v-if="editForm.type === 1 || editForm.type === 4"
            >
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
              <el-checkbox-group v-model="editForm.answer">
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
              <el-col :span="12">
                <el-form-item label="分值" prop="score">
                  <el-input-number
                    :max="100"
                    :min="1"
                    controls-position="right"
                    v-model.number="editForm.score"
                  ></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
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
            <el-form-item label>
              <el-button
                @click="add()"
                style="width: 164px; height: 40px"
                type="primary"
                v-if="editForm.id === null"
                >添加</el-button
              >
              <el-button
                @click="edit(false)"
                style="width: 164px; height: 40px"
                type="primary"
                v-if="editForm.id != null"
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
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      value: "",
      list: {
        //列表数据
        total: 0, // 总条数
        curPage: 1, //当前第几页
        pageSize: 6, //每页多少条
        questionList: [] //列表数据
      },
      queryForm: {
        //查询表单
        id: null, //主键
        title: null, //标题
        type: null, //类型
        difficulty: null, //难度
        startScore: null, //得分大于
        endScore: null, //得分小于
        difficultyDictList: [], //难度列表
        typeDictList: [] //类型列表
      },
      editForm: {
        //修改表单
        id: null, //主键
        type: null, //类型
        difficulty: null, //难度
        title: null, //标题
        options: [], //选项
        answer: null, //答案
        answers: [], //答案
        analysis: null, //解析
        score: null, //分值
        no: null, //排序
        scoreOptions: [],
        rules: {
          //校验
        }
      },
      btnGroup1: [
        // 左侧按钮组1
        {
          type: 1,
          name: "单选题",
          img: require("@/assets/img/icon/icon1.png")
        },
        {
          type: 2,
          name: "多选题",
          img: require("@/assets/img/icon/icon2.png")
        },
        {
          type: 3,
          name: "填空题",
          img: require("@/assets/img/icon/icon3.png")
        },
        {
          type: 4,
          name: "判断题",
          img: require("@/assets/img/icon/icon4.png")
        },
        {
          type: 5,
          name: "问答题",
          img: require("@/assets/img/icon/icon5.png")
        }
      ],
      btnGroup2: [
        // 左侧按钮组2
        {
          name: "试题模板",
          img: require("@/assets/img/icon/icon7.png")
        },
        {
          name: "试题导入",
          img: require("@/assets/img/icon/icon8.png")
        },
        {
          name: "试题导出",
          img: require("@/assets/img/icon/icon9.png")
        },
        {
          name: "清空试题",
          img: require("@/assets/img/icon/icon10.png")
        }
      ]
    }
  },
  created() {
    this.init()
  },
  methods: {
    //初始化默认值
    async init() {
      this.query() //查询列表

      let typeDictData = await this.$https.dictList({
        dictIndex: "QUESTION_TYPE"
      })
      this.queryForm.typeDictList = typeDictData.rows // 初始化类型下拉框

      let difficultyDictData = await this.$https.dictList({
        dictIndex: "QUESTION_DIFFICULTY"
      })
      this.queryForm.difficultyDictList = difficultyDictData.rows // 初始化难度下拉框

      this.btnGroup1[0].checked = true //默认选中类型按钮
      this.editForm.type = 1 //默认选中类型
      this.editForm.difficulty = 1 //默认选中简单
      this.editForm.score = 1 //默认得分为1
      this.editForm.no = 1 //默认排序为1
      this.editForm.answer = "" //默认答案为空

      for (let i = 0; i < 2; i++) {
        this._addOption(i, "") //默认添加两个选项
      }

      // 初始化校验
      this.editForm.rules = {
        type: [{ required: true, message: "请选择类型", trigger: "change" }],
        difficulty: [
          { required: true, message: "请选择难度", trigger: "change" }
        ],
        title: [{ required: true, message: "请输入题干", trigger: "change" }],
        answer: [{ required: true, message: "请选择答案", trigger: "change" }],
        score: [{ required: true, message: "请输入分值", trigger: "change" }],
        no: [{ required: true, message: "请输入排序", trigger: "change" }]
      }
    },
    // 查询
    async query() {
      let { rows, total } = await this.$https.questionList({
        id: this.id,
        title: this.title,
        type: this.type,
        difficulty: this.difficulty,
        scoreStart: this.scoreStart,
        scoreEnd: this.scoreEnd,
        curPage: this.curPage,
        pageSize: this.pageSize
      })
      this.list.total = total
      this.list.questionList = rows
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
      if (this.editForm.options.length <= 2) {
        alert("最少2个选项")
        return
      }

      this.editForm.options.pop()
      this.editForm.answers.pop()

      let index = this.editForm.options.length
      this.editForm.rules["options." + index + ".value"] = [
        { required: false, message: "请输入选项" + lab, trigger: "change" }
      ]
    },
    //添加填空
    addFillBlanks() {
      if (this.editForm.answers.length >= 7) {
        alert("最多7个填空")
        return
      }

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
      this.editForm = {
        //修改表单
        id: null, //主键
        type: value, //类型
        difficulty: null, //难度
        title: value, //标题
        options: [], //选项
        answer: null, //答案
        answers: [], //答案
        analysis: null, //解析
        score: null, //分值
        no: null, //排序
        scoreOptions: [],
        rules: {
          //校验
        }
      }
      this.updateOptionAndAnswer(value)
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
          for (let i in this.editForm.scoreOptions) {
            _scoreOptions[i] = this.editForm.scoreOptions[i]
          }
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

        let res = await questionAdd(params)
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
            let res = await questionEdit(params)
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
      let res = await this.$https.questionGet({ id: id })
      if (res.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.id = res.id
      this.editForm.type = res.type
      this.editForm.difficulty = res.difficulty
      this.editForm.title = res.title
      this.editForm.answer = res.answer
      this.editForm.analysis = res.analysis
      this.editForm.score = res.score
      this.editForm.no = res.no

      if (this.editForm.type === 1) {
        this.editForm.options = [] // 重置选项
        this.editForm.answers = [] //重置答案列表
        for (let i = 0; i < res.data.options.length; i++) {
          this._addOption(i, res.data.options[i])
        }
      } else if (this.editForm.type === 2) {
        this.editForm.options = [] // 重置选项
        this.editForm.answers = [] //重置答案列表
        for (let i = 0; i < res.data.options.length; i++) {
          this._addOption(i, res.data.options[i])
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
    async del(id) {
      this.$confirm("确定要删除？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(async () => {
        let res = await questionDel({ id })
        if (res.code != 200) {
          this.$message({
            type: "error",
            message: res.msg
          })
        }

        this.query()
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  flex-direction: column;
  background-color: #f2f4f5;
}

.head {
  width: 100%;
  height: 50px;
  background: url("../assets/img/head.png");
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-sizing: border-box;
  .back {
    color: #fff;
  }
}

.search {
  height: 60px;
  padding-top: 20px;
  .opt {
    flex-grow: 1;
    display: flex;
  }
  .left {
    width: 145px;
    background-color: #fff;
  }
}

.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  width: 98%;
  margin: 0 auto;
}
.content .opt .content .opt .center {
  flex-grow: 1;
  background-color: #fff;
  margin: 0 4px;
  padding: 10px;
  border-color: #f2f3f5;
  width: 0px;
}
.content .opt .right {
  width: 460px;
  background-color: #fff;
  padding: 10px;
  height: calc(100% - 60px);
  overflow-y: scroll;
}

.content .opt .left_inner_top {
  background-color: #1e9fff;
  width: 145px;
  height: 45px;
  line-height: 45px;
  text-align: center;
  font-size: 16px;
  color: #fff;
}

.content .opt .left_inner_center {
  box-sizing: border-box;
  width: 110px;
  height: 38px;
  border: 1px solid #eeeeeeff;
  margin: 7px auto;
  text-align: center;
  line-height: 38px;
  position: relative;
  cursor: pointer;
}
.content .opt .left_inner_center:hover {
  border: 1px solid #1e9fff;
}

.content .left_inner_center img:first-child {
  width: 22px;
  height: 22px;
  position: absolute;
  left: 10px;
  top: 8px;
}
.content .left_inner_center img:last-child {
  width: 28px;
  height: 22px;
  position: absolute;
  bottom: 0px;
  right: 0px;
  display: none;
}
.content .left_inner_center:hover img:last-child {
  display: block;
}
.content .left_inner_center span {
  position: absolute;
  top: 0px;
  left: 40px;
  font-size: 14px;
}
.content .left_inner_center1 {
  border: 1px solid #1e9fff !important;
}
.content .left_inner_center1 img {
  display: inline-block !important;
}

.splitLine {
  border-color: #eee;
  border-top: 0px;
  margin: 18px 0px;
}
.content .left_inner_bottom {
  box-sizing: border-box;
  width: 110px;
  height: 38px;
  border: 1px solid #eeeeeeff;
  background-color: #eee;
  margin: 7px auto;
  text-align: center;
  line-height: 38px;
  position: relative;
  cursor: pointer;
}
.content .left_inner_bottom img {
  position: absolute;
  top: 10px;
  left: 10px;
}
.content .left_inner_bottom span {
  position: absolute;
  top: 0px;
  left: 35px;
  font-size: 12px;
}
.content .center_inner_card {
  height: 70px;
  position: relative;
  cursor: pointer;
  margin-bottom: 10px;
}
.content .center_inner_card:hover {
  border: 1px solid #1e9fff;
}
.content .center_inner_card .center_inner_card1 {
  height: 20px;
  width: 90%;
  font-size: 14px;
  overflow: hidden;
}
.content .center_inner_card .center_inner_card1 span {
  margin-right: 10px;
}
.content .center_inner_card .center_inner_card2 {
  position: absolute;
  bottom: 8px;
  left: 20px;
}
.content .center_inner_card .center_inner_card21 {
  background-color: #feeddc;
  border-color: #fddbb9;
  color: #fa8718;
  height: 20px;
  line-height: 18px;
}
.content .center_inner_card .center_inner_card22 {
  border-color: #daddf9;
  color: #838eea;
  height: 20px;
  line-height: 18px;
}
.content .center_inner_card .center_inner_card3 {
  position: absolute;
  right: 20px;
  bottom: 8px;
  display: none;
}
.content .center_inner_card:hover .center_inner_card3 {
  display: block;
}
.btn {
  padding: 2px 4px;
  color: #c2c2c2;
  background: transparent;
  border-color: #c2c2c2;
  border: 0px;
}
.btn2 {
  width: 65px;
  height: 25px;
  padding: 0px;
}

.back {
  /* width: 100px; */
  height: 50px;
  display: inline-block;
  line-height: 50px;
  color: #fff;
}

.form-inline .el-form-item {
  width: 140px;
}

.pagination {
  font-weight: 400;
  text-align: center;
}

// .el-form-item {
//   margin-bottom: 7px;
// }

.el-checkbox {
  margin-right: 10px;
}
.el-radio {
  margin-right: 10px;
}
.box-card-no-border {
  border: 0px solid #ebeef5;
}

/deep/ .el-card__body {
  padding: 5px;
}
.el-alert--success.is-light {
  background-color: #ecf7fd;
  color: #409eff;
  margin-bottom: 10px;
}
.el-alert {
  padding: 0px;
}
/deep/ .el-alert__title {
  font-size: 12px;
  color: #409eff;
}
.el-input /deep/.el-input-group__prepend {
  background-color: #fff;
  border: 0px;
  width: 38px;
}
</style>
