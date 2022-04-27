<template>
  <div class="content-center center-preview">
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
            <div class="question-title">
              <span>{{ index + 1 }}、</span>
              <div v-html="`${child.title}`"></div>
            </div>

            <!-- 单选 -->
            <template v-if="child.type === 1">
              <el-radio-group class="children-option" v-model="child.answers">
                <el-radio
                  class="option-item"
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

            <!-- 判断 -->
            <template v-if="child.type === 4">
              <el-radio-group class="children-option" v-model="child.answer">
                <el-radio
                  class="option-item"
                  :key="index"
                  :label="option"
                  v-for="(option, index) in ['对', '错']"
                  >{{ option }}</el-radio
                >
              </el-radio-group>
            </template>

            <!-- 问答 -->
            <template v-if="child.type === 5">
              <el-input
                :rows="2"
                class="question-text"
                placeholder="请输入内容"
                type="textarea"
                v-model="child.answer"
              ></el-input>
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
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';

.center-preview {
  width: 100%;
  padding-top: 50px;
}
</style>
