<script>
export default {
  functional: true,
  props: {
    title: {
      type: String,
      default: '',
    },
    answers: {
      type: Array,
      default: [],
    },
    id: {
      type: Number,
      default: 0,
    },
    err: {
      type: Boolean,
      default: false,
    },
    preview: {
      type: Boolean,
      default: false,
    },
  },
  render(h, {props, parent: { updateAnswer }}) {
    let title = props.title
    let id = props.id

    title.match(/[_]{5,}/g).forEach((fillBlanks, index) => {
      const titleStart = title.substring(0, title.indexOf(fillBlanks))
      const titleEnd = title.substring(
        title.indexOf(fillBlanks) + fillBlanks.length
      )

      let html
      if (props.preview && props.scoreState) {
        const { score, questionScore } =
          props.myExamDetailCache[props.id]

        html = `<span class="cloze-input-preview" style="color: ${
          score === questionScore ? '#00b050' : '#ff5722'
        };border-color: ${score === questionScore ? '#00b050' : '#ff5722'}; ">${
          props.myExamDetailCache[id].answers[index] || ''
        }</span><span v-if="${
          props.preview
        }" class="cloze-answer">（${props.questionDetail.answers[
          index
        ].answer.join(',')}）</span>`
      } else {
        html = `
        <el-input class="cloze-input" 
          @change='updateAnswer(${id})' 
          :disabled='${props.preview}' 
          v-model='answers[${index}]'
          v-bind:style="{ width: ${fillBlanks.length * 18} + 'px' }"
          >
        </el-input>`
      }
      title = `${titleStart}${html}${titleEnd}`
    })
    const titleTemplate = {
      template: `<div>${title}</div>`, // 最外出需要包裹一层（控制台报没有根节点）
      data() {
        return props
      },
    }
    if (!props.preview) {
      Object.assign(titleTemplate, {
        methods: {
          updateAnswer,
        },
      })
    }
    return h(titleTemplate)
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
