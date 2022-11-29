<template>
  <div
    class="question-title"
    id="question-${props.no}"
    style="display: inline-block"
  >
    <span>{{ question.no }}、</span>
    <template v-for="(title, index) in titles">
      <template v-if="title.type === 'txt'">{{ title.value }}</template>
      <el-input
        v-else
        :key="index"
        class="cloze-input"
        :value="answers[title.index]"
        :style="{ width: title.value.length * 18 + 'px' }"
        @change="$emit('change', answers)"
        :disabled="preview"
      ></el-input>
    </template>
    <span>（{{ question.score }}分）</span>
  </div>
</template>

<script>
export default {
  props: {
    question: {// 试题
      type: Object,
      default: {},
    },
    preview: {// 预览
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      answers: []
    }
  },
  mounted() {
    if (this.question.answers.length === 0) {// 没有答过题，初始化
      this.titles.forEach(title => {
        if (title.type === "input") {
          this.answers.push("")
        }
      })
    } else {// 否则回显填过的答案
      this.answers = [...this.question.answers]
    }
  },
  computed: {
    titles: function () {
      // 如果不是填空题，正常返回
      if (this.question.type !== 3) {
        return [
          {
            type: 'txt',
            value: this.question.title,
          },
        ]
      }
      // 如果是填空题，按下划线分割，用于转化为输入框
      let title = this.question.title // '______AAAAA_____BBBBB____________CCCCC__DDDDD______'
      let titles = []
      let pos = 0
      title.match(/[_]{5,}/g).forEach((value) => {
        let index = title.indexOf(value)
        if (index > 0) {
          titles.push({
            type: 'txt',
            value: title.substring(0, index),
          })
        }
        titles.push({
          type: 'input',
          value,
          index: pos++
        })
        title = title.substring(index + value.length)
      })
      if (title) {
        titles.push({
          type: 'txt',
          value: title,
        })
      }
      return titles
    },
  },
}
</script>

<style lang="scss">
.cloze-input {
  .el-input__inner {
    height: 24px;
    border: none;
    border-radius: 0;
    background-color: transparent;
    border-bottom: 1px solid #0c2e41;
  }

  &.is-disabled .el-input__inner {
    background-color: transparent;
    border-color: #0c2e41;
    color: #0c2e41;
    cursor: default;
  }
}
</style>
