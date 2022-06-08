<script>
export default {
  functional: true,
  props: {
    title: {
      type: String,
      default: ''
    },
    questionId: {
      type: Number,
      default: 0
    },
    preview: {
      type: Boolean,
      default: false
    },
    questionDetail: {
      type: Object,
      default: () => { }
    },
    myExamDetailCache: {
      type: Object,
      default: () => { }
    }
  },
  render(h, context) {
    const {
      props,
      parent: { updateAnswer }
    } = context
    let title = props.title
    const questionId = props.questionId
    const underlineList = title.match(/[_]{5,}/g)
    underlineList.map((underline, index) => {
      const titleStart = title.substring(0, title.indexOf(underline))
      const titleEnd = title.substring(
        title.indexOf(underline) + underline.length
      )

      let inputHtml
      if (props.preview) {
        let isTrue
        const selfAnswer =
          props.myExamDetailCache[props.questionId].answers[index]
        const trueAnswer = props.questionDetail.answers[index].answer

        if (selfAnswer) {
          isTrue = trueAnswer.some((answer) => selfAnswer.includes(answer))
        } else {
          isTrue = false
        }

        inputHtml = `<span class="cloze-input-preview" style="color: ${isTrue ? '#00b050' : '#ff5722'};border-color: ${isTrue ? '#00b050' : '#ff5722'}; ">${props.myExamDetailCache[questionId].answers[index] || ''}</span><span v-if="${props.preview
        }" class="cloze-answer">（${props.questionDetail.answers[
          index
        ].answer.join(',')}）</span>`
      } else {
        inputHtml = `<el-input class="cloze-input" @change='updateAnswer(${questionId})' :disabled='${props.preview}' v-model='myExamDetailCache[${questionId}].answers[${index}]'></el-input>`
      }
      title = `${titleStart}${inputHtml}${titleEnd}`
    })
    const titleTemplate = {
      template: title,
      data() {
        return props
      }
    }
    if (!props.preview) {
      Object.assign(titleTemplate, {
        methods: {
          updateAnswer
        }
      })
    }
    return h(titleTemplate)
  }
}
</script>

<style lang="scss">
.cloze-input {
  width: fit-content !important;

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
