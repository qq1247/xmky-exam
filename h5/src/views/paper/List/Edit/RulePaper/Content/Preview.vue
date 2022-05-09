<template>
  <div class="content-center center-preview">
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
            <div class="question-title">
              <span>{{ indexQuestion + 1 }}、</span>
              <div v-html="`${question.title}`" />
            </div>

            <!-- 单选 -->
            <template v-if="question.type === 1">
              <el-radio-group
                v-model="question.answers"
                class="children-option"
              >
                <el-radio
                  v-for="(option, indexOption) in question.options"
                  :key="indexOption"
                  class="option-item"
                  :label="String.fromCharCode(65 + indexOption)"
                >
                  <div
                    class="flex-items-center"
                    v-html="
                      `${String.fromCharCode(65 + indexOption)}、${option}`
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
                  :label="String.fromCharCode(65 + indexOption)"
                >
                  <div
                    class="flex-items-center"
                    v-html="
                      `${String.fromCharCode(65 + indexOption)}、${option}`
                    "
                  />
                </el-checkbox>
              </el-checkbox-group>
            </template>

            <!-- 判断 -->
            <template v-if="question.type === 4">
              <el-radio-group v-model="question.answer" class="children-option">
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
                v-model="question.answer"
                :rows="2"
                class="question-text"
                placeholder="请输入内容"
                type="textarea"
              />
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
import { paperQuestionList } from 'api/paper'
import { getQuick } from '@/utils/storage'
export default {
  data() {
    return { paperQuestion: [], paperName: '' }
  },
  async created() {
    this.paperId = this.$route.params.id || getQuick().id
    this.query()
  },
  methods: {
    // 查询试卷信息
    async query() {
      try {
        const res = await paperQuestionList({
          id: this.paperId
        })
        res.data.map((item) => {
          item.chapter.show = true
        })
        this.paperQuestion = [...res.data]
      } catch (error) {
        this.$message.error(error.msg)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';

.center-preview {
  width: 100%;
  padding-top: 50px;
}
</style>
