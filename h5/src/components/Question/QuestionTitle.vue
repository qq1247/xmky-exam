<script>
export default {
  functional: true,
  props: {
    type: {// 类型
      type: Number,
      default: null,
    },
    no: {// 题号
      type: [Number, String],
      default: null,
    },
    title: {// 题干
      type: String,
      default: null,
    },
    score: {// 分数
      type: Number,
      default: null,
    },
    answers: {// 答案
      type: Array,
      default: [],
    },
  },
  render(h, {props}) {
    let title = props.title
    if (props.type === 3) {// 如果是填空题，下划线转输入框
      title.match(/[_]{5,}/g).forEach((value, index) => {
        title = title.replace(value, `
          <el-input class="cloze-input" 
            value='${props.answers[index].join(" ")}'
            :style="{ width: ${value.length * 18} + 'px' }"
            :disabled='true' 
            >
          </el-input>
        `)
      })
    }
    let template = `
      <div class='question-title' id='question-${props.no}'>
        <span>${props.no}、</span>
        <div>${title}</div>（${props.score}分）
      </div>`
    return h({
      template
    })
  }
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

.cloze-input-preview {
  display: inline-block;
  line-height: 16px;
  padding: 0 30px;
  color: #0c2e41;
  border-bottom: 1px solid #0c2e41;
}

.cloze-answer {
  color: #00b050;
}
</style>
