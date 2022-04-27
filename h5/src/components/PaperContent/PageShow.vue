<template>
  <div>
    <template v-if="paperQuestion.length">
      <div v-for="(item, index) in paperQuestion" :key="index">
        <div class="chapter">
          <div class="chapter-item">
            <div class="item-title">{{ item.chapter.name }}</div>
          </div>
          <div
            class="chapter-description"
            v-html="item.chapter.description"
          ></div>
        </div>

        <template v-if="item.questionList.length > 0">
          <div
            :id="`p-${child.id}`"
            :class="['children-content', child.type === 5 ? 'ask-content' : '']"
            v-for="(child, index) in item.questionList"
            :key="child.id"
          >
            <div class="question-title" v-if="child.type !== 3">
              <div>{{ index + 1 }}、</div>
              <div v-html="`${child.title}`"></div>
            </div>

            <div
              class="question-title"
              v-if="child.type === 3 && myExamDetailCache[child.id]"
            >
              <span>{{ index + 1 }}、</span>
              <ClozeTitle
                :title="child.title"
                :questionId="child.id"
                :preview="preview"
                :paperQuestion="paperQuestion"
                :myExamDetailCache="myExamDetailCache"
              ></ClozeTitle>
            </div>

            <!-- 单选 -->
            <template v-if="child.type === 1">
              <el-radio-group
                @change="updateAnswer(child.id)"
                class="children-option"
                v-if="myExamDetailCache[child.id]"
                v-model="myExamDetailCache[child.id].answers[0]"
              >
                <el-radio
                  :key="index"
                  :disabled="preview"
                  :label="String.fromCharCode(65 + index)"
                  class="option-item"
                  v-for="(option, index) in child.options"
                >
                  <div
                    :style="{
                      color: scoreState ? optionColor(index, child) : '',
                    }"
                    class="flex-items-center"
                    v-html="`${String.fromCharCode(65 + index)}、${option}`"
                  ></div>
                </el-radio>
              </el-radio-group>
            </template>

            <!-- 多选 -->
            <template v-if="child.type === 2">
              <el-checkbox-group
                @change="updateAnswer(child.id)"
                class="children-option"
                v-if="myExamDetailCache[child.id]"
                v-model="myExamDetailCache[child.id].answers"
              >
                <el-checkbox
                  :key="index"
                  :label="String.fromCharCode(65 + index)"
                  class="option-item"
                  :disabled="preview"
                  v-for="(option, index) in child.options"
                >
                  <div
                    :style="{
                      color: scoreState ? optionColor(index, child) : '',
                    }"
                    class="flex-items-center"
                    v-html="`${String.fromCharCode(65 + index)}、${option}`"
                  ></div>
                </el-checkbox>
              </el-checkbox-group>
            </template>

            <!-- 填空 -->
            <template v-if="child.type === 3 && scoreState">
              <div class="children-analysis">
                <span class="analysis-tip">查看解析</span>
                <div class="analysis-content">
                  <div>答案：</div>
                  <div>
                    <div
                      v-for="(answer, indexAnswers) in child.answers"
                      :key="answer.id"
                      class="answers-item"
                    >
                      <span>{{
                        `填空${$tools.intToChinese(indexAnswers + 1)}、`
                      }}</span>
                      <span
                        class="answers-tag"
                        v-for="(ans, indexAnswer) in answer.answer"
                        :key="indexAnswer"
                        >{{ ans }}</span
                      >
                    </div>
                  </div>
                </div>
                <div class="analysis-content">
                  <div>解析：</div>
                  <div v-html="child.analysis"></div>
                </div>
              </div>
            </template>

            <!-- 判断 -->
            <template v-if="child.type === 4">
              <el-radio-group
                @change="updateAnswer(child.id)"
                class="children-option"
                v-if="myExamDetailCache[child.id]"
                v-model="myExamDetailCache[child.id].answers[0]"
              >
                <el-radio
                  :key="index"
                  :label="option"
                  class="option-item"
                  v-for="(option, index) in ['对', '错']"
                  :disabled="preview"
                  ><span
                    :style="{
                      color: scoreState ? optionColor(index, child) : '',
                    }"
                    >{{ option }}</span
                  ></el-radio
                >
              </el-radio-group>
            </template>

            <!-- 问答 -->
            <template v-if="child.type === 5">
              <el-input
                :rows="2"
                class="question-text"
                @change="updateAnswer(child.id)"
                placeholder="请输入内容"
                type="textarea"
                v-if="myExamDetailCache[child.id]"
                :disabled="preview"
                v-model="myExamDetailCache[child.id].answers[0]"
              ></el-input>
              <div class="children-analysis" v-if="scoreState">
                <span class="analysis-tip">查看解析</span>
                <div class="analysis-content">
                  <span>答案：</span>
                  <div v-if="child.ai === 1">
                    <div
                      v-for="(answer, indexAnswers) in child.answers"
                      :key="answer.id"
                      class="answers-item"
                    >
                      <span>{{
                        `关键词${$tools.intToChinese(indexAnswers + 1)}、`
                      }}</span>
                      <span
                        class="answers-tag"
                        v-for="(ans, indexAnswer) in answer.answer"
                        :key="indexAnswer"
                        >{{ ans }}</span
                      >
                    </div>
                  </div>
                  <div
                    v-if="child.ai === 2"
                    v-html="`${child.answers[0].answer}`"
                  ></div>
                </div>
                <div class="analysis-content">
                  <div>解析：</div>
                  <div v-html="child.analysis"></div>
                </div>
              </div>
            </template>
          </div>
        </template>
        <el-empty v-else description="暂无试题"> </el-empty>
      </div>
    </template>
    <el-empty v-else description="暂无试卷"> </el-empty>
  </div>
</template>

<script>
import ClozeTitle from '../ClozeTitle.vue'
export default {
  components: {
    ClozeTitle,
  },
  props: {
    preview: {
      type: Boolean,
      default: false,
    },
    scoreState: {
      type: Boolean,
      default: false,
    },
    paperQuestion: {
      type: Array,
      default: () => [],
    },
    myExamDetailCache: {
      type: Object,
      default: () => {},
    },
  },
  computed: {
    optionColor(index, item) {
      return (index, item) => {
        // 单选，多选
        if ([1, 2].includes(item.type)) {
          // 选择完毕且与正确答案不匹配
          if (
            this.myExamDetailCache[item.id].answers.includes(
              String.fromCharCode(65 + index)
            ) &&
            !item.answers[0].answer.includes(String.fromCharCode(65 + index))
          ) {
            return '#FF5722'
          }

          // 正确答案
          if (
            item.answers[0].answer.includes(String.fromCharCode(65 + index))
          ) {
            return '#00B050'
          }
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
    },
  },
  methods: {
    updateAnswer(childId) {
      this.$emit('updateAnswer', childId)
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
</style>
