<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-16 09:46:28
 * @LastEditors: Che
 * @LastEditTime: 2021-09-16 14:40:07
-->
<template>
  <div class="content-center">
    <template v-if="paperQuestion.length">
      <div :id="`p-${paperQuestion[routerIndex].id}`" class="children-content">
        <p
          class="question-title"
          v-html="`${routerIndex + 1}、${paperQuestion[routerIndex].title}`"
        ></p>

        <!-- 单选 -->
        <template v-if="paperQuestion[routerIndex].type === 1">
          <el-radio-group
            @change="
              (val) => {
                updateAnswer(paperQuestion[routerIndex].id, val)
              }
            "
            class="children-option"
            v-if="myExamDetailCache[paperQuestion[routerIndex].id]"
            v-model="
              myExamDetailCache[paperQuestion[routerIndex].id].answers[0]
            "
          >
            <el-radio
              :key="index"
              :disabled="preview === 'true' ? true : false"
              :label="String.fromCharCode(65 + index)"
              class="option-item"
              v-for="(option, index) in paperQuestion[routerIndex].options"
            >
              <div
                class="flex-items-center"
                v-html="`${String.fromCharCode(65 + index)}、${option}`"
              ></div>
            </el-radio>
          </el-radio-group>
        </template>

        <!-- 多选 -->
        <template v-if="paperQuestion[routerIndex].type === 2">
          <el-checkbox-group
            @change="
              (val) => {
                updateAnswer(paperQuestion[routerIndex].id, val)
              }
            "
            class="children-option"
            v-if="myExamDetailCache[paperQuestion[routerIndex].id]"
            v-model="myExamDetailCache[paperQuestion[routerIndex].id].answers"
          >
            <el-checkbox
              :key="index"
              :label="String.fromCharCode(65 + index)"
              class="option-item"
              :disabled="preview === 'true' ? true : false"
              v-for="(option, index) in paperQuestion[routerIndex].options"
            >
              <div
                class="flex-items-center"
                v-html="`${String.fromCharCode(65 + index)}、${option}`"
              ></div>
            </el-checkbox>
          </el-checkbox-group>
        </template>

        <!-- 填空 -->
        <template
          v-if="
            paperQuestion[routerIndex].type === 3 &&
            myExamDetailCache[paperQuestion[routerIndex].id]
          "
        >
          <el-input
            class="question-text"
            @change="
              (val) => {
                updateClozeAnswer(
                  paperQuestion[routerIndex].id,
                  val,
                  paperQuestion[routerIndex].answers,
                  index
                )
              }
            "
            placeholder="请输入内容"
            :key="index"
            :disabled="preview === 'true' ? true : false"
            v-for="(answer, index) in myExamDetailCache[
              paperQuestion[routerIndex].id
            ].answers"
            v-model="
              myExamDetailCache[paperQuestion[routerIndex].id].answers[index]
            "
          >
            <template slot="prepend">第{{ index + 1 }}空</template>
          </el-input>
        </template>

        <!-- 判断 -->
        <template v-if="paperQuestion[routerIndex].type === 4">
          <el-radio-group
            @change="
              (val) => {
                updateAnswer(paperQuestion[routerIndex].id, val)
              }
            "
            class="children-option"
            v-if="myExamDetailCache[paperQuestion[routerIndex].id]"
            v-model="
              myExamDetailCache[paperQuestion[routerIndex].id].answers[0]
            "
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
        <template v-if="paperQuestion[routerIndex].type === 5">
          <el-input
            :rows="2"
            class="question-text"
            @change="
              (val) => {
                updateAnswer(paperQuestion[routerIndex].id, val)
              }
            "
            placeholder="请输入内容"
            type="textarea"
            v-if="myExamDetailCache[paperQuestion[routerIndex].id]"
            :disabled="preview === 'true' ? true : false"
            v-model="
              myExamDetailCache[paperQuestion[routerIndex].id].answers[0]
            "
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
    routerIndex: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {}
  },
  created() {},
  methods: {
    updateAnswer(itemId, value) {
      this.$emit('updateAnswer', itemId, value)
    },
    updateClozeAnswer(itemId, value, itemAnswers, index) {
      this.$emit('updateClozeAnswer', itemId, value, itemAnswers, index)
    },
    prevQuestion() {
      this.$emit('prevQuestion')
    },
    nextQuestion() {
      this.$emit('nextQuestion')
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
