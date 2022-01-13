<!--
 * @Description: 编辑试题
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-19 14:24:48
 * @LastTinymceEditors: Che
 * @LastEditTime: 2021-12-28 19:09:05
-->
<template>
  <div>
    <!-- 试题参数详情 -->
    <QuestionDetail
      v-if="detailStatus"
      :data="questionDetail"
      :comment="false"
    ></QuestionDetail>

    <!-- 编辑试题 -->
    <template v-else>
      <div class="top">设置</div>
      <el-form
        class="edit-form"
        :model="editForm"
        size="mini"
        ref="editForm"
        :rules="editForm.rules"
        label-width="80px"
        label-position="labelPosition"
        id="question_driver_editor"
      >
        <el-row>
          <el-col :span="11">
            <el-form-item label="难度" prop="difficulty">
              <el-select
                clearable
                placeholder="请选择难度"
                v-model="editForm.difficulty"
              >
                <el-option
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                  v-for="dict in difficultyList"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="题干" prop="title">
          <TinymceEditor
            :value="editForm.title"
            @editorListener="editorListener"
            id="title"
          ></TinymceEditor>
        </el-form-item>

        <div v-if="editForm.type === 3" class="cloze-tip">
          五个下划线（_____ ）表示一个填空
        </div>

        <template v-if="editForm.type === 1 || editForm.type === 2">
          <el-form-item
            :key="'option' + option.lab"
            :label="'选项' + option.lab"
            :prop="`options.${index}.value`"
            :rules="editForm.rules.option"
            v-for="(option, index) in editForm.options"
          >
            <TinymceEditor
              :id="'option' + option.lab"
              :value="option.value"
              @editorListener="editorListener"
            ></TinymceEditor>
          </el-form-item>
          <!-- 选项按钮 -->
          <el-form-item>
            <el-button
              :disabled="editForm.options.length >= 7"
              @click="addOption()"
              class="add-del-btn"
              type="primary"
              >+添加选项</el-button
            >
            <el-button
              :disabled="editForm.options.length <= 2"
              @click="delOption()"
              class="add-del-btn"
              type="primary"
              >-删除选项</el-button
            >
          </el-form-item>
        </template>

        <el-row>
          <el-col :span="2.5">
            <el-form-item
              label="智能阅卷"
              prop="ai"
              v-if="[3, 5].includes(editForm.type)"
            >
              <el-switch
                active-color="#0094e5"
                :active-value="1"
                inactive-color="#ff4949"
                :inactive-value="2"
                v-model="editForm.ai"
              ></el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="18">
            <template v-if="editForm.ai === 1">
              <el-form-item v-if="editForm.type === 3">
                <el-checkbox-group v-model="editForm.scoreOptions">
                  <el-tooltip
                    class="item"
                    content="默认答案有顺序"
                    effect="dark"
                    placement="top"
                  >
                    <el-checkbox :label="2">答案无顺序</el-checkbox>
                  </el-tooltip>
                  <el-tooltip
                    class="item"
                    content="默认大小写敏感"
                    effect="dark"
                    placement="top"
                  >
                    <el-checkbox :label="3">大小写不敏感</el-checkbox>
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
                    <el-checkbox :label="3">大小写不敏感</el-checkbox>
                  </el-tooltip>
                </el-checkbox-group>
              </el-form-item>
            </template>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
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
          <el-col :span="12" v-if="editForm.type === 2">
            <div class="lose">
              漏选得
              <el-input size="mini" v-model="editForm.multipScore"> </el-input
              >分
            </div>
          </el-col>
        </el-row>

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
                  <span style="margin: 0 10px; color: #838ee9; font-size: 12px"
                    >填空{{ $tools.intToChinese(index + 1) }}</span
                  >
                  <el-select
                    remote
                    multiple
                    clearable
                    filterable
                    allow-create
                    default-first-option
                    v-model="answer.value"
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
                  <span style="margin: 0 10px; color: #838ee9; font-size: 12px"
                    >关键词{{ $tools.intToChinese(index + 1) }}</span
                  >
                  <el-select
                    remote
                    multiple
                    clearable
                    filterable
                    allow-create
                    default-first-option
                    v-model="answer.value"
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
              class="add-del-btn"
              style="margin-left: 65px"
              type="primary"
              >+添加关键词</el-button
            >
            <el-button
              :disabled="editForm.answers.length <= 1"
              @click="delFillBlanks()"
              class="add-del-btn"
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
          <TinymceEditor
            :value="editForm.answer"
            @editorListener="editorListener"
            id="answer"
          ></TinymceEditor>
        </el-form-item>

        <el-form-item label="解析" prop="analysis">
          <TinymceEditor
            :value="editForm.analysis"
            @editorListener="editorListener"
            id="analysis"
          ></TinymceEditor>
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
    </template>
  </div>
</template>

<script>
import { questionGet } from 'api/question'
import { getOneDict } from '@/utils/getDict'
import TinymceEditor from 'components/TinymceEditor/Index.vue'
import QuestionDetail from 'components/QuestionDetail/Index.vue'
export default {
  components: {
    TinymceEditor,
    QuestionDetail,
  },
  props: {
    editForm: {
      type: Object,
      default: () => {},
    },
    detailStatus: {
      type: Boolean,
      default: false,
    },
    id: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      difficultyList: [],
      questionDetail: {},
    }
  },
  watch: {
    id: {
      immediate: true,
      handler(n) {
        if (n) {
          this.getQuestionDetail(n)
        }
      },
    },
  },
  created() {
    this.difficultyList = getOneDict('QUESTION_DIFFICULTY')
  },
  methods: {
    async getQuestionDetail(id) {
      const res = await questionGet({ id })
      if (res?.code != 200) {
        this.$message.error('获取详情失败！请重试')
        this.questionDetail = {}
        return
      }
      this.questionDetail = res.data
    },
    editorListener(id, value) {
      this.$emit('editorListener', id, value)
    },
    addOption() {
      this.$emit('addOption')
    },
    delOption() {
      this.$emit('delOption')
    },
    addFillBlanks() {
      this.$emit('addFillBlanks')
    },
    delFillBlanks() {
      this.$emit('delFillBlanks')
    },
    add() {
      this.$emit('add')
    },
    edit() {
      this.$emit('edit')
    },
  },
}
</script>

<style lang="scss" scoped>
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

.edit-form {
  padding-top: 40px;
}

.add-del-btn {
  padding: 0 10px;
  height: 25px;
}

/deep/ .el-card__body {
  padding: 5px;
}

.lose {
  display: flex;
  align-items: flex-end;
  height: 30px;
  /deep/ .el-input {
    width: 40px;
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

.cloze-tip {
  color: #0095e5;
  padding-left: 80px;
  margin-bottom: 10px;
  font-size: 12px;
}

/deep/.el-select__tags .el-tag {
  width: fit-content;
  height: auto;
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
</style>
