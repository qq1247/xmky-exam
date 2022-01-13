<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-16 09:46:16
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:48:32
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
          <div
            class="chapter-description"
            v-html="item.chapter.description"
          ></div>
        </div>

        <template v-if="item.questionList.length > 0">
          <div
            :id="`p-${child.id}`"
            :key="child.id"
            class="children-content"
            v-for="(child, index) in item.questionList"
          >
            <div class="question-title" v-if="child.type !== 3">
              <span>{{ index + 1 }}、</span>
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
                @change="updateAnswer(child.id)"
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
                @change="updateAnswer(child.id)"
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
import ClozeTitle from '../ClozeTitle.vue'
export default {
  components: {
    ClozeTitle,
  },
  props: {
    preview: {
      type: [String, Boolean],
      default: 'false',
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
    updateAnswer(childId) {
      this.$emit('updateAnswer', childId)
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
</style>
