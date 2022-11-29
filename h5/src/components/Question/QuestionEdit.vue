<template>
  <el-form
    ref="editForm"
    size="mini"
    label-width="80px"
    :model="editForm"
    :rules="editForm.rules"
    class="edit-form"
  >
    <!-- 选择类型区域（单选、多选、填空、判断、问答） -->
    <el-form-item label="类型">
      <el-radio-group v-model="editForm.type" size="small" >
        <el-radio-button
          v-for="dict in $tools.getDicts('QUESTION_TYPE')"
          :key="`type${dict.dictKey}`"
          :label="Number(dict.dictKey)"
          :disabled="(id && editForm.type == dict.dictKey) ? true : false"
        >{{ dict.dictValue }}</el-radio-button>
      </el-radio-group>
    </el-form-item>

    <!-- 题干区域（富文本编辑框） -->
    <el-form-item label="题干" prop="title">
      <Editor
        :id="'title'"
        :value="editForm.title"
        @editorListener="editorListener"
      />
    </el-form-item>

    <!-- 选项区域（单多选有效） -->
    <div v-if="[1, 2].includes(editForm.type)">
      <el-form-item
        v-for="(option, index) in editForm.options"
        :key="`option${optionLabs[index]}`"
        :label="`选项${optionLabs[index]}`"
        :prop="`options[${index}]`"
        :rules="editForm.rules.options"
      >
        <Editor
          :id="`option${optionLabs[index]}`"
          :value="editForm.options[index]"
          @editorListener="editorListener"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          :disabled="editForm.options.length >= 7"
          class="option-btn option-btn-primary"
          type="primary"
          @click="addOption"
        ><img
          src="@/assets/img/question/question-plus.png"
          alt=""
        >新增选项</el-button>
        <el-button
          :disabled="editForm.options.length <= 2"
          class="option-btn option-btn-danger"
          type="danger"
          @click="delOption"
        ><img
          src="@/assets/img/question/question-minus.png"
          alt=""
        >删除选项</el-button>
      </el-form-item>
    </div>

    <!-- 阅卷类型区域（填空问答有效，用于智能阅卷） -->
    <el-row v-if="[3, 5].includes(editForm.type)">
      <el-col :span="2.5">
          <el-form-item label="阅卷类型" prop="markType">
            <el-switch
              v-model="editForm.markType"
              active-color="#0094e5"
              :active-value="1"
              active-text="客观题"
              inactive-color="#ff4949"
              :inactive-value="2"
              inactive-text="主观题"
            />
          </el-form-item>
      </el-col>
      <el-col :span="18">
        <!-- 如果是客观题 -->
        <div v-if="editForm.markType === 1">
          <el-form-item prop="markOptions">
            <el-checkbox-group v-model="editForm.markOptions" style="width:300px">
              <!-- 显示答案顺序选项（填空有效） -->
              <el-tooltip
                v-if="editForm.type === 3"
                class="item"
                content="默认答案有顺序"
                effect="dark"
                placement="top"
              >
                <el-checkbox :label="2">答案无顺序</el-checkbox>
              </el-tooltip>
              <!-- 显示区分大小写选项 -->
              <el-tooltip
                class="item"
                content="默认区分大小写"
                effect="dark"
                placement="top"
              >
                <el-checkbox :label="3">不区分大小写</el-checkbox>
              </el-tooltip>
            </el-checkbox-group>
          </el-form-item>
        </div>
      </el-col>
    </el-row>

    <!-- 分值区域（填空为客观题，问答为主观题时，分数不可设置，由子分数合计生成） -->
    <el-row>
      <el-col :span="12">
        <el-form-item label="分值" prop="score">
          <el-input-number
            v-model.number="editForm.score"
            :min="0.5"
            :step="0.5"
            :disabled="editForm.type === 3 || (editForm.type === 5 && editForm.markType === 1)"
            controls-position="right"
          />
          <!-- :max="20"，多选合计分值会超出20，用自定义校验处理 -->
        </el-form-item>
      </el-col>
      <!-- 如果是多选，显示漏选分值 -->
      <el-col v-if="editForm.type === 2" :span="12">
        <el-form-item prop="answerScores[0]" :rules="editForm.rules.answerScores">
          <div class="lose">
            漏选得
            <el-input 
              v-model="editForm.answerScores[0]" 
              size="mini" 
              />分
          </div>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 答案区域 -->
    <el-form-item
      v-if="editForm.type === 1"
      label="答案"
      prop="answers"
    >
      <el-radio-group v-model="editForm.answers[0]">
        <el-radio
          v-for="(option, index) in editForm.options"
          :key="`answers${optionLabs[index]}`"
          :label="`${optionLabs[index]}`"
        >{{optionLabs[index]}}</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item
      v-else-if="editForm.type === 2"
      label="答案"
      prop="answers"
    >
      <el-checkbox-group v-model="editForm.answers">
        <el-checkbox
          v-for="(option, index) in editForm.options"
          :key="`answers${optionLabs[index]}`"
          :label="`${optionLabs[index]}`"
        >{{optionLabs[index]}}</el-checkbox>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item 
      v-else-if="editForm.type === 3 || (editForm.type === 5 && editForm.markType === 1)" 
      label="答案">
      <el-card class="el-card" shadow="never">
        <el-alert
          :closable="false"
          title="单填有多个备选答案，用多个标签分开。如：老婆、媳妇"
          type="success"
        />
        <el-row
          v-for="(answer, index) in editForm.answers"
          :key="index"
          :gutter="10"
        >
          <el-col :span="16" class="blank-col">
            <el-form-item
              :prop="`answers[${index}]`"
              :rules="editForm.rules.answers"
            >
              <span style="margin: 0 10px; color: #838ee9; font-size: 12px">
                {{editForm.type === 3 ? '第' + $tools.intToChinese(index + 1) + '空' : '关键词'}}
              </span>
              <el-select
                v-model="editForm.answers[index]"
                remote
                multiple
                clearable
                filterable
                allow-create
                default-first-option
                placeholder="请填写答案"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8" >
            <el-form-item
              :prop="`answerScores[${index}]`"
              :rules="editForm.rules.answerScores"
            >
              <el-input 
                v-model="editForm.answerScores[index]">
                <div slot="append">分</div>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-button
          v-if="editForm.type === 5"
          :disabled="editForm.answers.length >= 7"
          class="option-btn option-btn-primary"
          style="margin-left: 65px"
          type="primary"
          @click="addKeyword"
        >
          <img src="@/assets/img/question/question-plus.png" alt="">添加关键词
        </el-button>
        <el-button
          v-if="editForm.type === 5"
          :disabled="editForm.answers.length <= 1"
          class="option-btn option-btn-danger"
          type="danger"
          @click="delKeyword"
        >
          <img src="@/assets/img/question/question-minus.png" alt="">删除关键词
        </el-button>
      </el-card>
    </el-form-item>
    <el-form-item
      v-else-if="editForm.type === 4"
      label="答案"
      prop="answers"
    >
      <el-radio-group v-model="editForm.answers[0]">
        <el-radio
          v-for="judge in judgeLabs"
          :key="judge"
          :label="judge"
        >{{ judge }}</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item
      v-else-if="editForm.type === 5 && editForm.markType === 2"
      label="答案"
      prop="answers"
    >
      <el-input type="textarea" v-model="editForm.answers[0]"></el-input>
    </el-form-item>

    <!-- 解析区域 -->
    <el-form-item label="解析" prop="analysis">
      <Editor
        :id="'analysis'"
        :value="editForm.analysis"
        @editorListener="editorListener"
      />
    </el-form-item>
    
    <!-- 添加修改按钮区域 -->
    <el-form-item>
      <el-button
        v-if="!id"
        style="width: 164px; height: 40px"
        type="primary"
        @click="add()"
      >添加</el-button>
      <el-button
        v-if="id"
        style="width: 164px; height: 40px"
        type="primary"
        @click="edit()"
      >修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import Editor from 'components/Editor/Index.vue'
import { questionGet } from 'api/question'
export default {
  components: {
    Editor
  },
  props: {
    id : {
      type: Number,
      default: null,
    }
  },
  data() {
    return {
      editForm: {
        type: null, // 类型
        title: '', // 题干
        options: [], // 选项
        markType: 1, // 阅卷类型（1：客观题；2：主观题）
        markOptions: [], // 阅卷选项（2：答案无顺序；3：不区分大小写）
        score: 1, // 分值，默认1分
        answerScores: [], // 答案分数
        answers: [], // 答案，统一数组格式
        analysis: '', // 解析
        rules: { // 校验规则
          title: [{
            trigger: 'blur', 
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请填写题干'))
              }
              if (this.editForm.type === 3 && !/[_]{5,}/.test(value)) {
                return callback(new Error('最少一个填空（连续五个或五个以上下划线（_____）表示一个填空）'))
              }
              return callback()
          }}],
          options: [{ required: true, message: '请输入选项内容', trigger: 'blur' }],
          score: [{
            trigger: 'blur', 
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请填写分值'))
              }
              if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
              }
              if (this.editForm.score > 20) {
                return callback(new Error('最大20分'))
              }
              return callback()
          }}],
          answers: [{
            /** 
             * 答案格式为数组，
             * 单选、判断：['A']
             * 多选：['A', 'B']
             * 填空或客观问答：[['老婆', [媳妇]], ['我们', '咱们']]
             * 主观问答：['老婆'] // 
             */
            trigger: 'blur', 
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请填写答案'))
              }
              if (!value instanceof Array) {
                return callback(new Error('答案格式错误'))
              }
              if (value.length === 0) {
                return callback(new Error('请填写答案'))
              }
              value.forEach(cur => {
                if (cur.length === 0) {
                  return callback(new Error('请填写答案')) // 主观问答，值为空字符串会通过校验，特殊处理一下
                }
              });
              return callback()
          }}],
          answerScores: [{
            trigger: 'blur', 
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请填写分值'))
              }
              if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
              }
              if (this.editForm.type === 2 && value >= this.editForm.score) {// 填空漏选分数校验
                return callback(new Error(`应小于${this.editForm.score}分`))
              }
              return callback()
          }}],
        }
      },
      optionLabs: ['A','B','C','D','E','F','G'],
      judgeLabs: ['对','错']
    }
  },
  mounted() {
    this.editForm.type = 1
  },
  created: {
  },
  watch: {
    // 如果有id，则查询并回显数据
    'id': {
      deep: true,
      immediate: true,
      async handler(id, id2) {
        if (!id) {
          return
        }

        const res = await questionGet({ id })
        if (res?.code !== 200) {
          this.$message.error(res.msg)
          return
        }

        this.editForm.type = res.data.type
        this.$nextTick(function () {
          this.editForm.title = res.data.title
          this.$nextTick(function () {
            this.editForm.markType = res.data.markType
            this.$nextTick(function () {
              this.editForm.score = res.data.score
              this.$nextTick(function () {
                this.editForm.answerScores = res.data.answerScores
                
                this.editForm.markOptions = res.data.markOptions
                this.editForm.answers = res.data.answers
                this.editForm.options = res.data.options;
                this.editForm.analysis = res.data.analysis
              })
            })
          })
        })
      }
    },
    // 如果是主观题，清除阅卷选项
    // 如果是客观问答题，添加一个关键词、分数
    'editForm.markType': {
      deep: true,
      handler(n) {
        this.$refs['editForm']?.clearValidate(); // 切换阅卷类型时，清空校验。如主观问答没填答案，切换到客观问答时，保留了校验文字

        if (n === 2) { 
          this.editForm.markOptions.splice(0)
        }

        if (this.editForm.type === 5) {
          this.editForm.answers.splice(0)
          this.editForm.answerScores.splice(0)

          this.editForm.answers.push('')
          if (n == 1) {
            this.editForm.answerScores.push(1)
          }
        }
      }
    },
    // 如果是切换类型，初始化数据，清除校验（显示了多余的校验文字）
    'editForm.type': {
      deep: true,
      handler(n) {
        this.$refs['editForm']?.clearValidate(); // 第一次还没有生成clearValidate方法，用?处理一下

        this.editForm.title = ''
        this.editForm.options.splice(0)
        this.editForm.markType = 1
        this.editForm.markOptions.splice(0)
        this.editForm.score = 1
        this.editForm.answerScores.splice(0)
        this.editForm.answers.splice(0)
        this.editForm.analysis = ''
        if (n === 1) {
          this.editForm.title = '这是一道单选题的题干'
          this.addOption() 
          this.addOption()
        } else if (n === 2) {
          this.editForm.title = '这是一道多选题的题干'
          this.editForm.answerScores.push(0.5)
          this.addOption()
          this.addOption()
        } else if (n === 3) {
          this.editForm.title = '这是一道填空题的题干，连续五个或五个以上下划线（_____）表示一个填空'
        } else if (n === 4) {
          this.editForm.title = '这是一道判断题的题干'
        } else if (n === 5) {
          this.editForm.markType = 2 // 问答默认为主观题
          this.editForm.title = '这是一道问答题的题干'
        }
      }
    },
    // 如果是填空题干，更新答案
    'editForm.title': {
      deep: true,
      handler(n) {
        if (this.editForm.type !== 3) {
          return;
        }

        // 填空和答案对齐（多退少补）
        let fillblanksNum = n.match(/[_]{5,}/g).length
        fillblanksNum = fillblanksNum > 7 ? 7 : fillblanksNum // 最多7个填空
        while (fillblanksNum > this.editForm.answers.length) {
          this.editForm.answers.push('')
          this.editForm.answerScores.push(1)
        }
        while (fillblanksNum < this.editForm.answers.length) {
          this.editForm.answers.pop()
          this.editForm.answerScores.pop()
        }
      }
    },
    // 如果是多选题，默认漏选分值为分数一半
    'editForm.score': {
      deep: true,
      handler(n) {
        if (this.editForm.type === 2) {
          this.editForm.answerScores.splice(0, 1, n / 2)
        }
      }
    },
    // 如果是填空题或客观问答题，分数为各子项的分数总和
    'editForm.answerScores': {
      deep: true,
      handler(n) {
        if (this.editForm.type === 3 || (this.editForm.type === 5 && this.editForm.markType === 1)) {
          this.editForm.score = this.editForm.answerScores.reduce((pre, cur) => Number(pre) + Number(cur))
        }
      }
    },
  },
  methods: {
    // 编辑器监听事件
    editorListener(id, value) {
      if (id.startsWith('option')) {// 如果是选项，把值赋给options对应的位置
        this.$set(this.editForm.options, this.optionLabs.indexOf(id.substr(6)), value)
        return
      }

      this.editForm[id] = value
    },
    // 添加选项
    addOption() {
      this.editForm.options.push('')
    },
    // 删除选项
    delOption() {
      this.editForm.options.pop()
    },
    // 添加关键词
    addKeyword() {
      this.editForm.answers.push('')
      this.editForm.answerScores.push(1)
    },
    // 删除关键词
    delKeyword() {
      this.editForm.answers.pop()
      this.editForm.answerScores.pop()
    },
    // 添加试题
    add() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        let params = JSON.parse(JSON.stringify(this.editForm)) // 深度拷贝，不要改变子属性
        Reflect.deleteProperty(params, 'rules')
        if (params.type === 3 || (params.type === 5 && params.markType === 1)) {// 如果是填空或主观问答
          params.answers.map((item, index, self) => {// 答案处理成接口各式
            return self[index] = item.join('\n')
          })
        }

        this.$emit('add', params)
      })
    },
    // 修改试题
    edit() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        let params = JSON.parse(JSON.stringify(this.editForm)) // 深度拷贝，不要改变子属性
        params.id = this.id
        Reflect.deleteProperty(params, 'rules')
        if (params.type === 3 || (params.type === 5 && params.markType === 1)) {// 如果是填空或主观问答
          params.answers.map((item, index, self) => {// 答案处理成接口各式
            return self[index] = item.join('\n')
          })
        }

        this.$emit('edit', params)
      })
    },
  }
}
</script>

<style lang="scss" scoped>
.edit-form {
  padding: 20px;
}
.option-btn {
  border: none;
  padding: 8px;
  img {
    vertical-align: -3px;
    margin-right: 4px;
  }
}

.option-btn-primary {
  background: rgba(#0094e5, 0.1);
  color: #0094e5;
}

.option-btn-danger {
  background: rgba(#eb5b5b, 0.1);
  color: #eb5b5b;
}

/deep/ .el-card__body {
  padding: 5px;
}

.lose {
  display: flex;
  align-items: flex-end;
  height: 30px;
  /deep/ .el-input {
    width: 45px;
  }
  /deep/ .el-input__inner {
    border: none;
    border-bottom: 1px solid #dcdfe6;
    text-align: center;
    padding: 0 7px;
    height: 20px;
    line-height: 20px;
    border-radius: 0;
  }
}

.el-alert--success.is-light {
  background-color: #ecf7fd;
  color: #0094e5;
  margin-bottom: 10px;
}

.el-alert {
  padding: 0px;
}

/deep/ .el-alert__title {
  font-size: 12px;
  color: #0094e5;
}

.cloze-tip {
  color: #0094e5;
  padding-left: 80px;
  margin-bottom: 10px;
  font-size: 12px;
}

.blank-col {
  /deep/ .el-form-item__content {
    position: relative;
    font-size: 14px;
    display: flex;  
  }
}

/deep/.el-select__tags .el-tag {
  width: fit-content;
  height: auto;
  border-radius: 5px;
}
/deep/.el-select__tags-text {
  display: inline;
  white-space: normal;
  word-break: break-word;
}

.el-input /deep/.el-input-group__prepend,
.el-input /deep/.el-input-group__append {
  background-color: #fff;
  padding: 0 10px;
}
// 太小会遮挡提示
/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}

/deep/ .el-checkbox,
/deep/ .el-radio {
  margin-right: 20px;
}
</style>
