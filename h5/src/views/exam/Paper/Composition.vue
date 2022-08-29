<template>
  <div class="content-center center-drag">
    <Draggable
      v-model="questionList"
      tag="div"
      group="questionGroup"
      chosen-class="drag-question-active"
      animation="300"
    >
      <!-- @update="chapterMove" -->
      <div
        v-for="(question, index) in questionList"
        :id="`p-${question.id}`"
        :key="question.id"
        class="question-content"
      >
        <!-- <div class="chapter">
          <div class="chapter-name">
            <el-input
              v-model="item.chapter.name"
              class="chapter"
              placeholder="请输入章节名称"
              maxlength="16"
            />
          </div>
          <el-input
              v-model="item.chapter.description"
              :rows="2"
              class="chapter"
              placeholder="请输入描述"
              type="textarea"
              :autosize="true"
            />
        </div> -->
        <!-- 题目 -->
          <div v-if="question.type !== 3" class="question-title">
            <span>{{ index + 1 }}、</span>
            <div v-html="`${question.title}`" />
          </div>
          <ClozeTitle 
            v-else
            :no="index + 1 + ''"
            :title="question.title"
            :answers="question.answers"
            :id="question.id"
            :err="false"
            :preview="true"
          />
          <!-- 单选 -->
          <template v-if="question.type === 1">
            <el-radio-group v-model="question.answers" class="children-option">
              <el-radio
                v-for="(option, indexOption) in question.options"
                :key="indexOption"
                class="option-item"
                :label="String(option.no)"
              >
                <div
                  class="flex-items-center"
                  v-html="
                    `${String.fromCharCode(65 + indexOption)}、${option.option}`
                  "
                />
              </el-radio>
            </el-radio-group>
          </template>
          <!-- 多选 -->
          <template v-if="question.type === 2">
            <el-checkbox-group
              v-model="question.answers"
              class="children-option"
            >
              <el-checkbox
                v-for="(option, indexOption) in question.options"
                :key="indexOption"
                class="option-item"
                :label="String(option.no)"
              >
                <div
                  class="flex-items-center"
                  v-html="
                    `${String.fromCharCode(65 + indexOption)}、${option.option}`
                  "
                />
              </el-checkbox>
            </el-checkbox-group>
          </template>
          <!-- 判断 -->
          <template v-if="question.type === 4">
            <el-radio-group v-model="question.answers" class="children-option">
              <el-radio
                v-for="(option, indexOption) in ['对', '错']"
                :key="indexOption"
                class="option-item"
                :label="option"
              >{{ option }}</el-radio>
            </el-radio-group>
          </template>
          <!-- 问答 -->
          <template v-if="question.type === 5">
            <el-input
              v-model="question.answers"
              :rows="2"
              class="question-text"
              placeholder="请输入答案"
              type="textarea"
              :autosize="true"
            />
          </template>
          <div class="children-footer">
            <div v-if="question.analysisState" v-html="`解析：${question.analysis}`" ></div>
            <div class="lose">
              本题
              <el-input v-model="question.score" size="mini" />分
            </div>
            <div class="lose" v-if="question.type === 2">
              ，漏选得
              <el-input v-model="question.answerScores" size="mini" />分
            </div>
            <div>
              <el-button
                class="btn"
                icon="el-icon-view"
                round
                size="mini"
                @click.native.stop="
                  question.analysisShow = !question.analysisShow
                "
              >查看解析</el-button>
              <el-button
                class="btn"
                icon="el-icon-delete"
                round
                size="mini"
                @click="del(question.id)"
              >删除</el-button>
            </div>
        </div>
      <!-- <el-empty v-else description="拖拽题目到此处">
          <template slot="image">
            <i class="common common-drag" style="font-size: 35px" />
          </template>
        </el-empty> -->
      </div>
    </Draggable>

    <!-- <div v-if="paperState === 2" class="add-chapter" @click="paperChapterAdd">
      <i class="common common-click" />
      <span>点击添加章节</span>
    </div> -->

    <el-empty v-if="!paperQuestion.length" description="请添加试题" />

    <el-dialog
      :visible.sync="settingForm.show"
      :show-close="false"
      width="40%"
      title="分值选项设置"
      :close-on-click-modal="false"
      @close="resetData('settingForm')"
    >
      <el-form
        ref="settingForm"
        :model="settingForm"
        :rules="settingForm.rules"
        label-width="70px"
      >
        <el-form-item label="分值" prop="score">
          <el-input-number
            v-model.number="settingForm.score"
            :max="100"
            :min="0.5"
            :step="0.5"
            controls-position="right"
            mini
          />
        </el-form-item>

        <template v-if="settingForm.markType === 1">
          <template v-if="settingForm.type === 3 || settingForm.type === 5">
            <el-form-item
              v-for="(answer, indexAnswer) in settingForm.answers"
              :key="indexAnswer"
              :label="
                settingForm.type === 3
                  ? `填空${$tools.intToChinese(indexAnswer + 1)}`
                  : `关键词${$tools.intToChinese(indexAnswer + 1)}`
              "
              :prop="`answers.${indexAnswer}.score`"
              :rules="settingForm.rules.markTypeScore"
              :show-message="settingForm.markType === 1 ? true : false"
            >
              <el-input v-if="settingForm.markType === 1" v-model="answer.score">
                <template slot="append">分</template>
              </el-input>
            </el-form-item>
          </template>
        </template>

        <template v-if="settingForm.markType === 1">
          <div v-if="settingForm.type === 2" class="setting-checkbox">
            漏选得<el-input
              v-if="settingForm.markType === 1"
              v-model="settingForm.multipScore"
            />分
            <!-- <el-form-item
              prop="multipScore"
              class="ai-score"
              :show-message="settingForm.markType === 1 ? true : false"
            >
            </el-form-item> -->
          </div>
          <el-form-item v-if="settingForm.type === 3">
            <el-checkbox-group v-model="settingForm.markOptions">
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
          <el-form-item v-if="settingForm.type === 5">
            <el-checkbox-group v-model="settingForm.markOptions">
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="setScore">设置</el-button>
        <el-button @click="settingForm.show = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :visible.sync="batchForm.show"
      :show-close="false"
      width="40%"
      title="批量设置"
      :close-on-click-modal="false"
      @close="resetData('batchForm')"
    >
      <el-form
        ref="batchForm"
        :model="batchForm"
        :rules="batchForm.rules"
        label-width="80px"
      >
        <el-form-item label="每题得分" prop="score">
          <el-input-number
            v-model.number="batchForm.score"
            :max="100"
            :min="0.5"
            :step="0.5"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="选项设置">
          <el-checkbox-group v-model="batchForm.markOptions">
            <el-tooltip
              class="item"
              content="默认题目分数的一半"
              effect="dark"
              placement="top"
            >
              <el-checkbox :label="1">漏选得分</el-checkbox>
            </el-tooltip>
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
        <el-form-item
          v-if="batchForm.markOptions.includes(1)"
          label="漏选得分"
          prop="multipScore"
        >
          <el-input-number
            v-model.number="batchForm.multipScore"
            :max="100"
            :min="1"
            :step="1"
            controls-position="right"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="setBatchScore">设置</el-button>
        <el-button @click="batchForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import _ from 'lodash'
import { getOneDict } from '@/utils/getDict'
import { getQuick } from '@/utils/storage'
import Draggable from 'vuedraggable'
import ClozeTitle from '@/components/ClozeTitle.vue'
export default {
  components: {
    Draggable,
    ClozeTitle
  },
  props: {
    questionList: {
      type: Array,
      default: []
    }
  },
  data() {
    const validateAiScore = (rule, value, callback) => {
      if (this.settingForm.markType === 2) {
        return callback()
      }
      if (value === '') {
        return callback(new Error('请填写分数'))
      }
      if (value > this.settingForm.score || value <= 0) {
        return callback(new Error(`请填写合理分数`))
      }
      return callback()
    }

    const validateMultipScore = (rule, value, callback) => {
      if (
        this.settingForm.markType === 2 ||
        this.settingForm.markOptions.length === 0
      ) {
        this.settingForm.multipScore = ''
        return callback()
      }

      if (value === '') {
        return callback(new Error('请填写分数'))
      }
      if (value > this.settingForm.score || value <= 0) {
        return callback(new Error(`请填写合理分数`))
      }
      return callback()
    }

    return {
      paperId: 0,
      paperTypeId: 0,
      paperQuestion: [],
      chapterForm: {
        id: 0,
        name: '章节名称',
        description: '章节描述',
        rules: {
          name: [
            { required: true, message: '请输入章节名称', trigger: 'blur' }
          ]
        }
      },
      settingForm: {
        show: false,
        questionId: null,
        type: 1,
        score: 1,
        answers: [],
        markOptions: [],
        multipScore: '',
        rules: {
          score: [{ required: true, message: '请输入分值', trigger: 'change' }],
          markTypeScore: [{ validator: validateAiScore }],
          multipScore: [{ validator: validateMultipScore }]
        }
      },
      batchForm: {
        show: false,
        id: null,
        score: '',
        markOptions: [],
        multipScore: '',
        rules: {
          score: [
            { required: true, message: '请设置每题得分', trigger: 'change' }
          ]
        }
      }
    }
  },
  async created() {
    this.paperId = this.$route.params.id || getQuick().id
  },
  methods: {
    // 添加章节
    async paperChapterAdd() {
      const res = await paperChapterAdd({
        name: this.chapterForm.name,
        description: this.chapterForm.description,
        paperId: this.paperId,
        type: 1
      })
      this.refreshData(res, '添加章节')
    },
    // 编辑章节
    editorListener: _.debounce(function(id, value, chapter) {
      const chapterInfo = {}
      if (id === 'chapterName') {
        chapterInfo.name = value
        chapterInfo.description = chapter.description
      } else {
        chapterInfo.name = chapter.name
        chapterInfo.description = value
      }
      paperChapterEdit({
        chapterId: chapter.id,
        ...chapterInfo
      })
    }, 300),
    // 删除章节
    chapterDel({ id }) {
      this.$confirm(`删除章节将删除章节内的试题，是否删除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          const res = await paperChapterDel({ chapterId: id })
          this.refreshData(res, '删除章节')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 章节折叠
    chapterFold(index) {
      this.paperQuestion[index].chapter.show =
        !this.paperQuestion[index].chapter.show
    },
    // 展示批量设置
    batchSetting({ id }) {
      this.batchForm.id = id
      this.batchForm.show = true
    },
    // 清空试卷试题
    async chapterClear({ id }, index) {
      if (this.paperQuestion[index].questionList.length === 0) {
        this.$message.warning('试题已清空，请重新添加试题！')
        return
      }
      this.$confirm(`确认清空章节下的所有试题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          const res = await paperQuestionClear({ chapterId: id })
          this.refreshData(res, '清空试题')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 删除试题
    del(questionId) {
      this.$confirm(`确认删除该试题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          const res = await paperQuestionDel({ id: this.paperId, questionId })
          this.refreshData(res, '删除试题')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 更新页面数据
    refreshData(res, title) {
      res?.code === 200
        ? (this.$message(`${title}成功！`),
        this.$parent.$parent.$refs.questionDrag.queryQuestion())
        : this.$message.error(`${title}失败！`)
    },
    // 设置分数
    setting(data) {
      this.settingForm.questionId = data.id
      this.settingForm.type = data.type
      this.settingForm.markType = data.markType
      this.settingForm.score = data.score
      this.settingForm.markOptions = data.markOptions ? data.markOptions : []
      this.settingForm.answers = data.answers
      this.settingForm.multipScore =
        data.type === 2 && data.markType === 1 && data.markOptions
          ? data.answers[0].score
          : ''
      this.settingForm.show = true
    },
    // 设置分数
    setScore() {
      let paperQuestionAnswerScore = []
      if (this.settingForm.markType === 1) {
        if (this.settingForm.type === 2) {
          paperQuestionAnswerScore = this.settingForm.answers.reduce((acc) => {
            acc.push(this.settingForm.multipScore)
            return acc
          }, [])
        } else {
          paperQuestionAnswerScore = this.settingForm.answers.reduce(
            (acc, cur) => {
              acc.push(Number(cur.score))
              return acc
            },
            []
          )
        }
      }

      this.$refs['settingForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        let params = {
          id: this.paperId,
          questionId: this.settingForm.questionId,
          score: this.settingForm.score,
          subScores:
            this.settingForm.type === 2
              ? this.settingForm.markOptions.length === 0
                ? []
                : paperQuestionAnswerScore
              : paperQuestionAnswerScore
        }

        if (
          [2, 3, 5].includes(this.settingForm.type) &&
          this.settingForm.markType === 1
        ) {
          params = { ...params, markOptions: this.settingForm.markOptions }
        }

        const updateScore = await paperScoreUpdate(params)

        if (updateScore?.code === 200) {
          this.$message.success('编辑成功！')
          this.settingForm.show = false
        }
      })
    },
    // 批量设置分数
    async setBatchScore() {
      const res = await paperUpdateBatchScore({
        chapterId: this.batchForm.id,
        score: this.batchForm.score,
        subScores: this.batchForm.markOptions.includes(1)
          ? this.batchForm.multipScore
          : null,
        markOptions: this.batchForm.markOptions
      })
      if (res?.code === 200) {
        this.$message.success('编辑成功！')
        this.batchForm.show = false
      }
    },
    // 筛选数据
    filterQuestion(chapterId) {
      const list = this.paperQuestion.filter(
        (item) => item.chapter.id === chapterId
      )
      return list[0].questionList
    },
    // 重置数据
    resetData(name) {
      this.$refs[name].resetFields()
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
.drag-question-active {
  border: 1px solid #0094e5 !important;
  box-shadow: 0 0 16px 2px rgba(0, 148, 229, 0.15);
  transition: all 0.15s ease-in-out;
}

.center-drag {
  width: calc(1200px - 410px);
  padding-top: 80px;
  .drag-item {
    cursor: move;
  }
}

.add-chapter {
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 5px;
  margin-top: 10px;
  cursor: pointer;
  i {
    font-size: 25px;
    margin-right: 10px;
  }
}

.setting-checkbox {
  display: flex;
  align-items: center;
  margin-left: 80px;
  /deep/ .el-input {
    width: 80px;
    margin: 0 10px;
  }
}

.question-content {
  cursor: pointer;
  &:hover {
    transition: all 0.2s ease;
    padding: 16px 16px 50px;
    .children-footer {
      transition: all 0.2s 0.1s ease-in-out;
      opacity: 1;
    }
  }
}

/deep/.tinymce-box .tinymce-content {
  line-height: 30px;
  border: 1px solid #fff;
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
</style>
