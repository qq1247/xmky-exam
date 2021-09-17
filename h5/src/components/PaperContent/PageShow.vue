<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-16 09:46:16
 * @LastEditors: Che
 * @LastEditTime: 2021-09-16 13:52:02
-->

<template>
  <div class="content-center">
    <template v-if="paperQuestion.length">
      <div :key="index" v-for="(item, index) in paperQuestion">
        <div class="chapter">
          <div class="chapter-item">
            <div class="item-title">{{ item.chapter.name }}</div>
            <div></div>
          </div>
          <div class="chapter-description">
            {{ item.chapter.description }}
          </div>
        </div>

        <template v-if="item.questionList.length > 0">
          <div
            :id="`p-${child.id}`"
            :key="child.id"
            class="children-content"
            v-for="(child, index) in item.questionList"
          >
            <p
              class="question-title"
              v-html="`${index + 1}、${child.title}`"
            ></p>

            <!-- 单选 -->
            <template v-if="child.type === 1">
              <el-radio-group
                @change="
                  (val) => {
                    updateAnswer(child.id, val)
                  }
                "
                class="children-option"
                v-if="myExamDetailCache[child.id]"
                v-model="myExamDetailCache[child.id].answers[0]"
              >
                <el-radio
                  :key="index"
                  :disabled="preview === 'true' ? true : false"
                  :label="String.fromCharCode(65 + index)"
                  class="option-item"
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
                @change="
                  (val) => {
                    updateAnswer(child.id, val)
                  }
                "
                class="children-option"
                v-if="myExamDetailCache[child.id]"
                v-model="myExamDetailCache[child.id].answers"
              >
                <el-checkbox
                  :key="index"
                  :label="String.fromCharCode(65 + index)"
                  class="option-item"
                  :disabled="preview === 'true' ? true : false"
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
            <template v-if="child.type === 3 && myExamDetailCache[child.id]">
              <el-input
                class="question-text"
                @change="
                  (val) => {
                    updateClozeAnswer(child.id, val, child.answers, index)
                  }
                "
                placeholder="请输入内容"
                :key="index"
                :disabled="preview === 'true' ? true : false"
                v-for="(answer, index) in myExamDetailCache[child.id].answers"
                v-model="myExamDetailCache[child.id].answers[index]"
              >
                <template slot="prepend">第{{ index + 1 }}空</template>
              </el-input>
            </template>

            <!-- 判断 -->
            <template v-if="child.type === 4">
              <el-radio-group
                @change="
                  (val) => {
                    updateAnswer(child.id, val)
                  }
                "
                class="children-option"
                v-if="myExamDetailCache[child.id]"
                v-model="myExamDetailCache[child.id].answers[0]"
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
            <template v-if="child.type === 5">
              <el-input
                :rows="2"
                class="question-text"
                @change="
                  (val) => {
                    updateAnswer(child.id, val)
                  }
                "
                placeholder="请输入内容"
                type="textarea"
                v-if="myExamDetailCache[child.id]"
                :disabled="preview === 'true' ? true : false"
                v-model="myExamDetailCache[child.id].answers[0]"
              ></el-input>
            </template>
          </div>
        </template>
      </div>
    </template>
    <el-empty v-else description="暂无试卷"> </el-empty>
  </div>
</template>

<script>
export default {
  props: {
    preview: {
      type: [String, Boolean],
      default: '',
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
  data() {
    return {}
  },
  methods: {
    updateAnswer(childId, value) {
      this.$emit('updateAnswer', childId, value)
    },
    updateClozeAnswer(childId, value, childAnswers, index) {
      this.$emit('updateClozeAnswer', childId, value, childAnswers, index)
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
</style>
