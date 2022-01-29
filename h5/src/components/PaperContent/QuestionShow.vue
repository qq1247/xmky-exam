<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-16 09:46:28
 * @LastEditors: Che
 * @LastEditTime: 2021-11-02 16:34:09
-->
<template>
  <div>
    <template v-if="paperQuestion.length">
      <div class="chapter">
        <div class="chapter-item">
          <div class="item-title">{{ chapter.name }}</div>
          <div></div>
        </div>
        <div class="chapter-description" v-html="chapter.description"></div>
      </div>

      <div class="children-content">
        <div class="question-title" v-if="questionList[index].type !== 3">
          <div>{{ index + 1 }}、</div>
          <div v-html="`${questionList[index].title}`"></div>
        </div>
        <div class="question-title" v-else>
          <span>{{ index + 1 }}、</span>
          <ClozeTitle
            :title="questionList[index].title"
            :preview="preview"
            :paperQuestion="paperQuestion"
            :myExamDetailCache="myExamDetailCache"
            :questionId="routerIndex"
          ></ClozeTitle>
        </div>
        <!-- 单选 -->
        <template v-if="questionList[index].type === 1">
          <el-radio-group
            @change="updateAnswer(questionList[index].id)"
            class="children-option"
            v-if="myExamDetailCache[questionList[index].id]"
            v-model="myExamDetailCache[questionList[index].id].answers[0]"
          >
            <el-radio
              :key="index"
              :disabled="preview === 'true' ? true : false"
              :label="String.fromCharCode(65 + index)"
              class="option-item"
              v-for="(option, index) in questionList[index].options"
            >
              <div
                class="flex-items-center"
                v-html="`${String.fromCharCode(65 + index)}、${option}`"
              ></div>
            </el-radio>
          </el-radio-group>
        </template>

        <!-- 多选 -->
        <template v-if="questionList[index].type === 2">
          <el-checkbox-group
            @change="updateAnswer(questionList[index].id)"
            class="children-option"
            v-if="myExamDetailCache[questionList[index].id]"
            v-model="myExamDetailCache[questionList[index].id].answers"
          >
            <el-checkbox
              :key="index"
              :label="String.fromCharCode(65 + index)"
              class="option-item"
              :disabled="preview === 'true' ? true : false"
              v-for="(option, index) in questionList[index].options"
            >
              <div
                class="flex-items-center"
                v-html="`${String.fromCharCode(65 + index)}、${option}`"
              ></div>
            </el-checkbox>
          </el-checkbox-group>
        </template>

        <!-- 判断 -->
        <template v-if="questionList[index].type === 4">
          <el-radio-group
            @change="updateAnswer(questionList[index].id)"
            class="children-option"
            v-if="myExamDetailCache[questionList[index].id]"
            v-model="myExamDetailCache[questionList[index].id].answers[0]"
          >
            <el-radio
              :key="index"
              :label="option"
              class="option-item"
              v-for="(option, index) in ['对', '错']"
              :disabled="preview === 'true' ? true : false"
              >{{ option }}</el-radio
            >
          </el-radio-group>
        </template>

        <!-- 问答 -->
        <template v-if="questionList[index].type === 5">
          <el-input
            :rows="2"
            class="question-text"
            @change="updateAnswer(questionList[index].id)"
            placeholder="请输入内容"
            type="textarea"
            v-if="myExamDetailCache[questionList[index].id]"
            :disabled="preview === 'true' ? true : false"
            v-model="myExamDetailCache[questionList[index].id].answers[0]"
          ></el-input>
        </template>
      </div>

      <div class="question-pages">
        <el-button @click="prevQuestion">上一题</el-button>
        <el-button @click="nextQuestion">下一题</el-button>
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
    paperQuestion: {
      type: Array,
      default: () => [],
    },
    myExamDetailCache: {
      type: Object,
      default: () => {},
    },
    routerIndex: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      chapter: {},
      questionList: [],
      index: 0,
    }
  },
  watch: {
    routerIndex: {
      deep: true,
      immediate: true,
      handler(n) {
        this.questionList.length && this.getQuestion(n)
      },
    },
  },
  mounted() {
    this.getQuestion()
  },
  methods: {
    getQuestion(routerIndex) {
      this.questionList = this.paperQuestion.reduce((acc, cur) => {
        acc.push(...cur.questionList)
        return acc
      }, [])

      this.routerIndex = Number(routerIndex || this.questionList[0].id)

      this.index = this.questionList.findIndex(
        (question) => question.id === this.routerIndex
      )
      // 查找试题所在的章节信息
      this.paperQuestion.some((item) => {
        const question = item.questionList.some(
          (question) => question.id === this.routerIndex
        )
        if (question) {
          this.chapter = item.chapter
          return true
        }
      })
    },
    updateAnswer(itemId) {
      this.$emit('updateAnswer', itemId)
    },
    // 上一题
    prevQuestion() {
      if (this.index === 0) {
        this.$message.warning('请您继续作答！')
        return
      }
      this.index -= 1
      this.$emit('prevQuestion', this.questionList[this.index].id)
    },
    // 下一题
    nextQuestion() {
      if (this.index === this.questionList.length - 1) {
        this.$message.warning('恭喜您已经作答完毕！')
        return
      }
      this.index += 1
      this.$emit('nextQuestion', this.questionList[this.index].id)
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
.question-pages {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}
</style>
