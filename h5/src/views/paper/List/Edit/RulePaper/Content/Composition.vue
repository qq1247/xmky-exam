<template>
  <div class="center-drag">
    <Draggable
      tag="div"
      v-model="paperQuestion"
      group="paperParent"
      chosenClass="drag-question-active"
      animation="300"
      v-if="paperQuestion.length"
      @update="chapterMove"
    >
      <div
        class="drag-item drag-content drag-parent"
        v-for="(item, index) in paperQuestion"
        :key="index"
      >
        <div class="chapter">
          <div class="chapter-item">
            <el-input
              class="chapter-name"
              placeholder="请输入章节名称（最多16个字符）"
              v-model="item.chapter.name"
              maxlength="16"
              @blur="
                (e) =>
                  editorListener('chapterName', e.target.value, item.chapter)
              "
            ></el-input>
            <!-- <div class="item-title">{{ item.chapter.name }}</div> -->
            <div class="chapter-handler">
              <el-button
                @click="chapterDel(item.chapter)"
                class="btn"
                icon="common common-delete"
                round
                size="mini"
                >删除</el-button
              >
              <el-button
                @click="chapterClear(item.chapter, index)"
                class="btn"
                icon="common common-clear"
                round
                size="mini"
                >清空试题</el-button
              >
              <el-button
                @click="batchSetting(item.chapter, index)"
                class="btn"
                icon="common common-setting"
                round
                size="mini"
                >批量设置</el-button
              >
              <el-button
                @click="chapterFold(index)"
                class="btn"
                icon="common common-fold"
                round
                size="mini"
                >折叠</el-button
              >
            </div>
          </div>
          <TinymceEditor
            class="chapter-description"
            placeholder="请输入章节描述"
            :value="item.chapter.description"
            @editorListener="
              (id, value) => editorListener(id, value, item.chapter)
            "
            id="chapterDesc"
          ></TinymceEditor>
        </div>

        <Draggable
          tag="div"
          v-if="item.chapter.show"
          :class="[
            'drag-content',
            item.questionList.length != 0 ? 'drag-children' : '',
          ]"
          v-model="item.questionList"
          group="paper"
          chosenClass="drag-question-active"
          animation="300"
          :data-id="item.chapter.id"
          @end="questionMove"
        >
          <template v-if="item.questionList.length > 0">
            <div
              class="children-content"
              v-for="(child, index) in item.questionList"
              :key="child.id"
              :id="`p-${child.id}`"
            >
              <div class="item-title">
                <span>{{ index + 1 }}、</span>
                <div v-html="`${child.title}`"></div>
              </div>
              <!-- 单选 -->
              <template v-if="child.type === 1">
                <el-radio-group class="children-option" v-model="child.answers">
                  <el-radio
                    class="option-item"
                    disabled
                    :key="index"
                    :label="String.fromCharCode(65 + index)"
                    v-for="(option, index) in child.options"
                  >
                    <div
                      class="flex-items-center"
                      v-html="`${String.fromCharCode(65 + index)}、${option}`"
                    ></div>
                  </el-radio>
                </el-radio-group>
              </template>

              <!-- 多选 -->
              <template v-if="child.type === 2">
                <el-checkbox-group
                  class="children-option"
                  v-model="child.answers"
                >
                  <el-checkbox
                    class="option-item"
                    disabled
                    :key="index"
                    :label="String.fromCharCode(65 + index)"
                    v-for="(option, index) in child.options"
                  >
                    <div
                      class="flex-items-center"
                      v-html="`${String.fromCharCode(65 + index)}、${option}`"
                    ></div>
                  </el-checkbox>
                </el-checkbox-group>
              </template>

              <!-- 填空 -->
              <!-- <template v-if="child.type === 3">
                      <div
                        class="option-item-text"
                        :key="index"
                        v-for="(option, index) in child.answers"
                      ></div>
                    </template> -->

              <!-- 判断 -->
              <template v-if="child.type === 4">
                <el-radio-group class="children-option" v-model="child.answer">
                  <el-radio
                    class="option-item"
                    disabled
                    :key="index"
                    :label="option"
                    v-for="(option, index) in ['对', '错']"
                    >{{ option }}</el-radio
                  >
                </el-radio-group>
              </template>

              <!-- 问答 -->
              <!-- <template v-if="child.type === 5">
                      <div class="option-item-text">{{ child.answer }}</div>
                    </template> -->

              <div class="children-analysis">
                <el-row :gutter="10">
                  <template v-if="[1, 4].includes(child.type)">
                    <el-col :span="2.5"> 【答案】： </el-col>
                    <el-col :span="21">
                      <div
                        v-if="child.answers && child.answers.length > 0"
                        v-html="`${child.answers[0].answer}`"
                      ></div>
                    </el-col>
                  </template>
                  <template v-if="child.type === 2">
                    <el-col :span="2.5"> 【答案】： </el-col>
                    <el-col :span="21">
                      <div v-if="child.answers && child.answers.length > 0">
                        <span
                          v-for="answer in child.answers"
                          :key="answer.id"
                          >{{ answer.answer.join('，') }}</span
                        >
                      </div>
                    </el-col>
                  </template>
                  <template v-if="child.type === 3">
                    <el-col :span="2.5">【答案】：</el-col>
                    <el-col :span="21">
                      <div
                        v-for="(answer, index) in child.answers"
                        :key="answer.id"
                        class="answers-item"
                      >
                        <span>{{
                          `填空${$tools.intToChinese(index + 1)}、`
                        }}</span>
                        <span
                          class="answers-tag"
                          v-for="(ans, index) in answer.answer"
                          :key="index"
                          >{{ ans }}</span
                        >
                      </div>
                    </el-col>
                  </template>
                  <template v-if="child.type === 5">
                    <el-col :span="2.5"> 【答案】： </el-col>
                    <el-col :span="21">
                      <template v-if="child.ai === 1">
                        <div
                          v-for="(answer, index) in child.answers"
                          :key="answer.id"
                          class="answers-item"
                        >
                          <span>{{
                            `关键词${$tools.intToChinese(index + 1)}、`
                          }}</span>
                          <span
                            class="answers-tag"
                            v-for="(ans, index) in answer.answer"
                            :key="index"
                            >{{ ans }}</span
                          >
                        </div>
                      </template>
                      <div
                        v-if="
                          child.ai === 2 &&
                          child.answers &&
                          child.answers.length > 0
                        "
                        v-html="`${child.answers[0].answer}`"
                      ></div>
                    </el-col>
                  </template>
                </el-row>
                <el-row :gutter="10">
                  <el-col :span="2.5"> 【解析】： </el-col>
                  <el-col :span="21">
                    <div v-html="`${child.analysis}`"></div>
                  </el-col>
                </el-row>
              </div>

              <div class="children-footer">
                <div class="children-tags">
                  <el-tag effect="dark" size="mini" type="warning">
                    {{ child.type | typeName }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="danger">
                    {{ child.difficulty | difficultyName }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="warning"
                    >{{ child.score }}分</el-tag
                  >
                </div>
                <div class="children-buts">
                  <el-button
                    @click="setting(child)"
                    class="btn"
                    icon="el-icon-setting"
                    round
                    size="mini"
                    >设置</el-button
                  >
                  <el-button
                    @click="del(child.id)"
                    class="btn"
                    icon="el-icon-delete"
                    round
                    size="mini"
                    >删除</el-button
                  >
                </div>
              </div>
            </div>
          </template>
          <el-empty v-else description="拖拽题目到此处">
            <template slot="image">
              <i class="common common-drag" style="font-size: 35px"></i>
            </template>
          </el-empty>
        </Draggable>
      </div>
    </Draggable>

    <div class="add-chapter" @click="paperChapterAdd" v-if="paperState === 2">
      <i class="common common-click"></i>
      <span>点击添加章节</span>
    </div>

    <el-empty v-if="!paperQuestion.length" description="暂无试卷"> </el-empty>

    <el-dialog
      :visible.sync="settingForm.show"
      :show-close="false"
      width="40%"
      title="分值选项设置"
      :close-on-click-modal="false"
      @close="resetData('settingForm')"
    >
      <el-form
        :model="settingForm"
        :rules="settingForm.rules"
        ref="settingForm"
        label-width="70px"
      >
        <el-form-item label="分值" prop="score">
          <el-input-number
            :max="100"
            :min="1"
            :step="1"
            controls-position="right"
            v-model.number="settingForm.score"
            mini
          ></el-input-number>
        </el-form-item>

        <template v-if="settingForm.ai === 1">
          <template v-if="settingForm.type === 3 || settingForm.type === 5">
            <el-form-item
              v-for="(answer, index) in settingForm.answers"
              :key="index"
              :label="
                settingForm.type === 3
                  ? `填空${$tools.intToChinese(index + 1)}`
                  : `关键词${$tools.intToChinese(index + 1)}`
              "
              :prop="`answers.${index}.score`"
              :rules="settingForm.rules.aiScore"
              :show-message="settingForm.ai === 1 ? true : false"
            >
              <el-input v-if="settingForm.ai === 1" v-model="answer.score">
                <template slot="append">分</template>
              </el-input>
            </el-form-item>
          </template>
        </template>

        <template v-if="settingForm.ai === 1">
          <div class="setting-checkbox" v-if="settingForm.type === 2">
            漏选得<el-input
              v-if="settingForm.ai === 1"
              v-model="settingForm.multipScore"
            >
            </el-input
            >分
            <!-- <el-form-item
              prop="multipScore"
              class="ai-score"
              :show-message="settingForm.ai === 1 ? true : false"
            >
            </el-form-item> -->
          </div>
          <el-form-item v-if="settingForm.type === 3">
            <el-checkbox-group v-model="settingForm.aiOptions">
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
            <el-checkbox-group v-model="settingForm.aiOptions">
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
      <div class="dialog-footer" slot="footer">
        <el-button @click="setScore" type="primary">设置</el-button>
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
        :model="batchForm"
        :rules="batchForm.rules"
        ref="batchForm"
        label-width="80px"
      >
        <el-form-item label="每题得分" prop="score">
          <el-input-number
            :max="100"
            :min="1"
            :step="1"
            controls-position="right"
            v-model.number="batchForm.score"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="选项设置">
          <el-checkbox-group v-model="batchForm.aiOptions">
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
          label="漏选得分"
          prop="multipScore"
          v-if="batchForm.aiOptions.includes(1)"
        >
          <el-input-number
            :max="100"
            :min="1"
            :step="1"
            controls-position="right"
            v-model.number="batchForm.multipScore"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="setBatchScore" type="primary">设置</el-button>
        <el-button @click="batchForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import _ from 'lodash'
import { getOneDict } from '@/utils/getDict'
import { getQuick } from '@/utils/storage'
import {
  paperQuestionList,
  paperChapterAdd,
  paperChapterEdit,
  paperChapterDel,
  paperQuestionClear,
  paperQuestionDel,
  paperScoreUpdate,
  paperChapterMove,
  paperQuestionMove,
  paperUpdateBatchScore,
} from 'api/paper'
import TinymceEditor from 'components/TinymceEditor/Index.vue'
import Draggable from 'vuedraggable'
export default {
  components: { TinymceEditor, Draggable },
  props: {
    paperState: {
      type: Number,
      default: 2,
    },
  },
  data() {
    const validateAiScore = (rule, value, callback) => {
      if (this.settingForm.ai === 2) {
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
        this.settingForm.ai === 2 ||
        this.settingForm.aiOptions.length === 0
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
            { required: true, message: '请输入章节名称', trigger: 'blur' },
          ],
        },
      },
      settingForm: {
        show: false,
        questionId: null,
        type: 1,
        score: 1,
        answers: [],
        aiOptions: [],
        multipScore: '',
        rules: {
          score: [{ required: true, message: '请输入分值', trigger: 'change' }],
          aiScore: [{ validator: validateAiScore }],
          multipScore: [{ validator: validateMultipScore }],
        },
      },
      batchForm: {
        show: false,
        id: null,
        score: '',
        aiOptions: [],
        multipScore: '',
        rules: {
          score: [
            { required: true, message: '请设置每题得分', trigger: 'change' },
          ],
        },
      },
    }
  },
  filters: {
    typeName(data) {
      return getOneDict('QUESTION_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    difficultyName(data) {
      return getOneDict('QUESTION_DIFFICULTY').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
  },
  async created() {
    this.paperId = this.$route.params.id || getQuick().id
    this.query()
  },
  activated() {
    this.query()
  },
  methods: {
    // 查询试卷信息
    async query() {
      try {
        const res = await paperQuestionList({
          id: this.paperId,
        })
        res.data.map((item) => {
          item.chapter.show = true
        })
        this.paperQuestion = [...res.data]
      } catch (error) {
        this.$message.error(error.msg)
      }
    },
    // 添加章节
    async paperChapterAdd() {
      const res = await paperChapterAdd({
        name: this.chapterForm.name,
        description: this.chapterForm.description,
        paperId: this.paperId,
        type: 1,
      })
      this.refreshData(res, '添加章节')
    },
    // 编辑章节
    editorListener: _.debounce(function (id, value, chapter) {
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
        ...chapterInfo,
      })
    }, 300),
    // 删除章节
    chapterDel({ id }) {
      this.$confirm(`删除章节将删除章节内的试题，是否删除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
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
      if (this.paperQuestion[index].questionList.length == 0) {
        this.$message.warning('试题已清空，请重新添加试题！')
        return
      }
      this.$confirm(`确认清空章节下的所有试题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
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
        type: 'warning',
      })
        .then(async () => {
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
          this.query(),
          this.$parent.$refs.questionDrag.queryQuestion())
        : this.$message.error(`${title}失败！`)
    },
    // 设置分数
    setting(data) {
      this.settingForm.questionId = data.id
      this.settingForm.type = data.type
      this.settingForm.ai = data.ai
      this.settingForm.score = data.score
      this.settingForm.aiOptions = data.aiOptions ? data.aiOptions : []
      this.settingForm.answers = data.answers
      this.settingForm.multipScore =
        data.type === 2 && data.ai === 1 && data.aiOptions
          ? data.answers[0].score
          : ''
      this.settingForm.show = true
    },
    // 设置分数
    setScore() {
      let paperQuestionAnswerScore = []
      if (this.settingForm.ai === 1) {
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

      this.$refs['settingForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        let params = {
          id: this.paperId,
          questionId: this.settingForm.questionId,
          score: this.settingForm.score,
          subScores:
            this.settingForm.type === 2
              ? this.settingForm.aiOptions.length === 0
                ? []
                : paperQuestionAnswerScore
              : paperQuestionAnswerScore,
        }

        if (
          [2, 3, 5].includes(this.settingForm.type) &&
          this.settingForm.ai === 1
        ) {
          params = { ...params, aiOptions: this.settingForm.aiOptions }
        }

        const updateScore = await paperScoreUpdate(params)

        if (updateScore?.code === 200) {
          this.$message.success('编辑成功！')
          this.settingForm.show = false
          this.query()
        }
      })
    },
    // 批量设置分数
    async setBatchScore() {
      const res = await paperUpdateBatchScore({
        chapterId: this.batchForm.id,
        score: this.batchForm.score,
        subScores: this.batchForm.aiOptions.includes(1)
          ? this.batchForm.multipScore
          : null,
        aiOptions: this.batchForm.aiOptions,
      })
      if (res?.code === 200) {
        this.$message.success('编辑成功！')
        this.batchForm.show = false
        this.query()
      }
    },
    // 章节移动
    async chapterMove({ newIndex, oldIndex }) {
      const sourceId = this.paperQuestion[newIndex].chapter.id
      const targetId = this.paperQuestion[oldIndex].chapter.id
      const res = await paperChapterMove({
        sourceId,
        targetId,
      })

      if (res?.code === 200) {
        this.query()
      }
    },
    // 试题移动
    async questionMove({ to, from, newIndex, oldIndex }) {
      const toChapterId = to.dataset.id
      const sourceQuestion = this.filterQuestion(toChapterId)
      const sourceId = sourceQuestion[newIndex].id
      const targetId = sourceQuestion[oldIndex].id

      const res = await paperQuestionMove({
        id: this.paperId,
        sourceId,
        targetId,
      })

      if (res?.code === 200) {
        this.query()
      }
    },
    // 筛选数据
    filterQuestion(chapterId) {
      const list = this.paperQuestion.filter(
        (item) => item.chapter.id == chapterId
      )
      return list[0].questionList
    },
    // 重置数据
    resetData(name) {
      this.$refs[name].resetFields()
    },
  },
}
</script>

<style lang="scss" scoped>
.drag-content {
  width: 100%;
  border: 1px solid #d8d8d8;
  border-radius: 5px;
}

.drag-active {
  transform: scale(0.98);
  border: 1px solid #0094e5;
  border-radius: 5px;
  border-bottom: 1px solid #0094e5 !important;
  transition: all 0.15s ease-in-out;
}

.drag-question-active {
  border: 1px solid #0094e5 !important;
  box-shadow: 0 0 16px 2px rgba(0, 148, 229, 0.15);
  transition: all 0.15s ease-in-out;
}

.paper-content {
  background: #fff;
  width: calc(100% - 500px);
  overflow: scroll;
  .center-drag {
    width: 100%;
    padding: 10px;
    padding-top: 50px;
    .drag-item {
      cursor: move;
      margin-bottom: 10px;
      border-radius: 5px;
    }
  }
  .center-preview {
    width: 100%;
    padding: 50px 10px 10px;
    .chapter {
      line-height: 40px;
    }
    .drag-content {
      border: none;
    }
  }
  .chapter {
    display: flex;
    flex-direction: column;
    padding: 10px;
    .chapter-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .chapter-name {
        flex: 1;
        margin-right: 20px;
        /deep/ .el-input__inner {
          border: 1px solid #fff;
          padding: 0 10px;
        }
        /deep/ .el-input__inner:hover,
        /deep/ .el-input__inner:focus {
          border: 1px solid #c0c4cc;
        }
      }
      /deep/.el-button {
        opacity: 0;
        .common {
          font-size: 12px;
          margin-right: 5px;
        }
      }
      &:hover {
        .el-button {
          opacity: 1;
        }
      }
    }
  }
  .drag-parent {
    padding: 10px;
  }
  .drag-children {
    border: none;
  }
  .paper-title {
    font-size: 16px;
    color: #333;
    padding: 20px 0 10px 10px;
  }
  .paper-intro {
    font-size: 12px;
    color: #666;
    padding: 0 10px 15px;
    border-bottom: 1px solid #d8d8d8;
  }
  .children-content {
    border-left: 1px solid #f3f3f3;
    border-right: 1px solid #f3f3f3;
    font-size: 14px;
    box-sizing: border-box;
    &:not(:last-child) {
      border-bottom: none;
    }
    .item-title {
      background: #e5f4fc;
    }
  }
  .children-option {
    padding: 10px 0 0 25px;
  }
  .option-item,
  .flex-items-center {
    display: flex;
    justify-items: center;
    line-height: 30px;
    /deep/ .el-radio__input,
    /deep/ .el-checkbox__input {
      padding-top: 9px;
    }
  }
  .option-item-text {
    border-bottom: 1px solid #d8d8d8;
    padding: 20px 10px 5px;
    color: #333;
    margin: 0 25px 0;
  }
  .children-analysis {
    line-height: 30px;
    padding-left: 20px;
    margin: 15px 0;
    font-size: 13px;
    color: #666;
  }
  .answers-item {
    width: 100%;
    span {
      width: 100%;
      display: inline;
      word-wrap: break-word;
      word-break: normal;
    }
    .answers-tag {
      background: #cdd2f6;
      color: #fff;
      padding: 3px 10px;
      border-radius: 3px;
      &:not(:last-child) {
        margin-right: 10px;
      }
    }
  }
  .el-tag {
    margin-right: 6px;
  }
  .children-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
  }
  .btn {
    padding: 5px 10px;
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

.item-title {
  font-size: 14px;
  text-align: left;
  line-height: 50px;
  display: flex;
  padding-left: 7px;
  p {
    margin: 0;
    padding: 0;
  }
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

.setting-checkbox {
  display: flex;
  align-items: center;
  margin-left: 80px;
  /deep/ .el-input {
    width: 80px;
    margin: 0 10px;
  }
}

/deep/.tinymce-box .tinymce-content {
  line-height: 30px;
  border: 1px solid #fff;
}
</style>
