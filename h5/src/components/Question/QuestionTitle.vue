<script>
export default {
  functional: true,
  props: {
    id: {// 主键
      type: Number,
      default: 0,
    },
    no: {// 题号
      type: [Number, String],
      default: '',
    },
    title: {// 题干
      type: String,
      default: '',
    },
    answers: {// 答案
      type: Array,
      default: [],
    },
    readOnly: {// 只读
      type: Boolean,
      default: false,
    },
  },
  render(h, {props}) {
    let title = props.title
    if (props.type === 3) {// 如果是填空题，下划线转输入框
      title.match(/[_]{5,}/g).forEach((value, index) => {
        title = title.replace(value, `
          <el-input class="cloze-input" 
            :disabled='${props.readOnly}' 
            v-model='${props.answers[index]}'
            v-bind:style="{ width: ${value.length * 18} + 'px' }"
            >
          </el-input>
        `)
      })
    }
    let template = `
      <div class='question-title'>
        <span>${props.no}、</span>
        <div>${title}</div>
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
