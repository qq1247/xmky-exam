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
    paperQuestion: {
      type: Array,
      default: () => []
    },
    myExamDetailCache: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {}
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
      // questionId 存在则为整卷方式，否则为单题方式
      const inputHtml = `<el-input class="cloze-input" @change='updateAnswer(${questionId})' :disabled='${props.preview}' v-model='myExamDetailCache[${questionId}].answers[${index}]'></el-input>`
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
  min-width: 100px !important;
}
</style>
