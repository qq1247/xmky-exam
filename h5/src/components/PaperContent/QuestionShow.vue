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
  <div class="content-center">
    <template v-if="paperQuestion.length">
      <div :id="`p-${paperQuestion[routerIndex].id}`" class="children-content">
        <p
          v-if="paperQuestion[routerIndex].type !== 3"
          class="question-title"
          v-html="`${routerIndex + 1}、${paperQuestion[routerIndex].title}`"
        ></p>
        <div class="question-title" v-else>
          <span>{{ routerIndex + 1 }}、</span>
          <ClozeTitle
            :title="paperQuestion[routerIndex].title"
            :preview="preview"
            :paperQuestion="paperQuestion"
            :myExamDetailCache="myExamDetailCache"
            :routerIndex="routerIndex"
          ></ClozeTitle>
        </div>
        <!-- 单选 -->
        <template v-if="paperQuestion[routerIndex].type === 1">
          <el-radio-group
            @change="updateAnswer(paperQuestion[routerIndex].id)"
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
            @change="updateAnswer(paperQuestion[routerIndex].id)"
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

        <!-- 判断 -->
        <template v-if="paperQuestion[routerIndex].type === 4">
          <el-radio-group
            @change="updateAnswer(paperQuestion[routerIndex].id)"
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
            @change="updateAnswer(paperQuestion[routerIndex].id)"
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
import ClozeTitle from '../ClozeTitle.vue'
export default {
  components: {
    ClozeTitle,
  },
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
    updateAnswer(itemId) {
      this.$emit('updateAnswer', itemId)
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
