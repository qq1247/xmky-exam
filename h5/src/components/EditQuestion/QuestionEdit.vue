<!--
 * @Description: 编辑试题
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-19 14:24:48
 * @LastEditors: Che
 * @LastEditTime: 2021-10-22 16:51:50
-->
<template>
  <div>
    <QuestionDetail
      v-if="detailStatus"
      :data="questionDetail"
      :type="queryForm.typeList"
      :difficulty="queryForm.difficultyList"
    ></QuestionDetail>
    <template v-else>
      <el-form
        :model="editForm"
        ref="editForm"
        :rules="editForm.rules"
        label-width="80px"
        size="mini"
        label-position="labelPosition"
        id="question_driver_editor"
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
              <el-select placeholder="请选择难度" v-model="editForm.difficulty">
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

        <div v-if="editForm.type === 3" class="cloze-tip">
          Tip：请在题干中用英文状态下连续五个的下划线（ _ ）来表示填空位。
        </div>

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
                  <span style="margin: 0 10px; color: #838ee9; font-size: 12px"
                    >填空{{ $tools.intToChinese(index + 1) }}</span
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
    </template>
  </div>
</template>

<script>
import Editor from 'components/Editor.vue'
import QuestionDetail from 'components/QuestionDetail/Index.vue'
export default {
  components: {
    Editor,
    QuestionDetail,
  },
  props: {
    queryForm: {
      type: Object,
      default: () => {},
    },
    editForm: {
      type: Object,
      default: () => {},
    },
    detailStatus: {
      type: Boolean,
      default: false,
    },
    questionDetail: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      labelPosition: 'left',
    }
  },
  methods: {
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
.add-del-btn {
  padding: 0 10px;
  height: 25px;
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

.cloze-tip {
  color: #f84040;
  padding-left: 80px;
  margin-bottom: 10px;
  font-size: 13px;
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

/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}
</style>
