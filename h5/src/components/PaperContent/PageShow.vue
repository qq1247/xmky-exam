<template>
  <div>
    <template v-if="paperQuestion.length">
      <div v-for="(item, index) in paperQuestion" :key="index">
        <div class="chapter">
          <div class="chapter-name">{{ item.chapter.name }}</div>
          <div class="chapter-description" v-html="item.chapter.description" />
        </div>

        <template v-if="item.questionList.length > 0">
          <div
            v-for="(question, indexQuestion) in item.questionList"
            :id="`p-${question.id}`"
            :key="question.id"
            :class="[
              'question-content',
              question.type === 5 ? 'ask-content' : '',
            ]"
          >
            <div v-if="question.type !== 3" class="question-title">
              <div>{{ indexQuestion + 1 }}、</div>
              <div v-html="`${question.title}`" />
            </div>

            <div
              v-if="question.type === 3 && myExamDetailCache[question.id]"
              class="question-title"
            >
              <span>{{ indexQuestion + 1 }}、</span>
              <ClozeTitle
                :preview="preview"
                :title="question.title"
                :question-id="question.id"
                :question-detail="question"
                :my-exam-detail-cache="myExamDetailCache"
              />
            </div>

            <!-- 单选 -->
            <template v-if="question.type === 1">
              <el-radio-group
                v-if="myExamDetailCache[question.id]"
                v-model="myExamDetailCache[question.id].answers[0]"
                class="children-option"
                @change="updateAnswer(question.id)"
              >
                <el-radio
                  v-for="(option, indexOption) in question.options"
                  :key="indexOption"
                  :disabled="preview"
                  :label="String(option.no)"
                  class="option-item"
                >
                  <div
                    :style="{
                      color:
                        preview && scoreState
                          ? optionColor(indexOption, question)
                          : '',
                    }"
                    class="flex-items-center"
                    v-html="
                      `${option.no}、${
                        option.option
                      }`
                    "
                  />
                </el-radio>
              </el-radio-group>
            </template>

            <!-- 多选 -->
            <template v-if="question.type === 2">
              <el-checkbox-group
                v-if="myExamDetailCache[question.id]"
                v-model="myExamDetailCache[question.id].answers"
                class="children-option"
                @change="updateAnswer(question.id)"
              >
                <el-checkbox
                  v-for="(option, indexOption) in question.options"
                  :key="indexOption"
                  :label="String(option.no)"
                  class="option-item"
                  :disabled="preview"
                >
                  <div
                    :style="{
                      color:
                        preview && scoreState
                          ? optionColor(indexOption, question)
                          : '',
                    }"
                    class="flex-items-center"
                    v-html="
                      `${option.no}、${
                        option.option
                      }`
                    "
                  />
                </el-checkbox>
              </el-checkbox-group>
            </template>

            <!-- 判断 -->
            <template v-if="question.type === 4">
              <el-radio-group
                v-if="myExamDetailCache[question.id]"
                v-model="myExamDetailCache[question.id].answers[0]"
                class="children-option"
                @change="updateAnswer(question.id)"
              >
                <el-radio
                  v-for="(option, indexOption) in ['对', '错']"
                  :key="indexOption"
                  :label="option"
                  class="option-item"
                  :disabled="preview"
                ><span
                  :style="{
                    color:
                      preview && scoreState
                        ? optionColor(indexOption, question)
                        : '',
                  }"
                >{{ option }}</span></el-radio>
              </el-radio-group>
            </template>

            <!-- 问答 -->
            <template v-if="question.type === 5">
              <el-input
                v-if="myExamDetailCache[question.id]"
                v-model="myExamDetailCache[question.id].answers[0]"
                :rows="2"
                type="textarea"
                :disabled="preview"
                class="question-text"
                placeholder="请输入内容"
                @change="updateAnswer(question.id)"
              />
              <div v-if="preview && scoreState" class="children-analysis">
                <div class="analysis-content">
                  <span>答案：</span>
                  <div v-if="question.ai === 1">
                    <div class="cloze-answers">
                      <span
                        v-for="answer in question.answers"
                        :key="answer.id"
                        class="answers-items"
                      >
                        {{ answer.answer.join(' | ') }}
                      </span>
                    </div>
                  </div>
                  <div
                    v-if="question.ai === 2"
                    v-html="`${question.answers[0].answer}`"
                  />
                </div>
              </div>
            </template>
          </div>
        </template>
        <el-empty v-else description="暂无试题" />
      </div>
    </template>
    <el-empty v-else description="暂无试卷" />
  </div>
</template>

<script>
import ClozeTitle from '../ClozeTitle.vue'
export default {
  components: {
    ClozeTitle
  },
  props: {
    preview: {
      type: Boolean,
      default: false
    },
    scoreState: {
      type: Boolean,
      default: false
    },
    paperQuestion: {
      type: Array,
      default: () => []
    },
    myExamDetailCache: {
      type: Object,
      default: () => {}
    }
  },
  computed: {
    optionColor(index, item) {
      return (index, item) => {
        // 单选，多选
        console.log(index, '=====', item)
        if ([1, 2].includes(item.type)) {
          // 选择完毕且与正确答案不匹配
          if (
            this.myExamDetailCache[item.id].answers.includes(
              item.options[index].no
            ) &&
            !item.answers[0].answer.includes(item.options[index].no)
          ) {
            return '#FF5722'
          }

          // 正确答案
          if (
            item.answers[0].answer.includes(item.options[index].no)
          ) {
            return '#00B050'
          }
          // if (this.myExamDetailCache[item.id].answers != item.answers[0].answer)
        }

        // 判断
        if (item.type === 4) {
          // 选择完毕且与正确答案不匹配
          if (
            this.myExamDetailCache[item.id].answers[0] ===
              ['对', '错'][index] &&
            ['对', '错'][index] !== item.answers[0].answer[0]
          ) {
            return '#FF5722'
          }

          // 正确答案
          if (item.answers[0].answer[0] === ['对', '错'][index]) {
            return '#00B050'
          }
        }
      }
    }
  },
  methods: {
    updateAnswer(childId) {
      this.$emit('updateAnswer', childId)
    },
    isShowAnalysis(index, indexQuestion) {
      this.$emit('isShowAnalysis', { index, indexQuestion })
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
</style>
