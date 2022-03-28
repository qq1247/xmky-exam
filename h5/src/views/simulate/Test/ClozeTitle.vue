<script>
export default {
  functional: true,
  props: {
    detail: {
      type: Object,
      default: () => {},
    },
    recite: {
      type: Boolean,
      default: false,
    },
  },
  render(h, context) {
    const { props } = context
    let title = props.detail.title
    let underlineList = title.match(/[_]{5,}/g)
    underlineList.map((underline, index) => {
      const titleStart = title.substring(0, title.indexOf(underline))
      const titleEnd = title.substring(
        title.indexOf(underline) + underline.length
      )
      const inputHtml = props.recite
        ? `<el-input class="cloze-input" disabled v-model='detail.answers[${index}].answer.join(" | ")'></el-input>`
        : `<el-input class="cloze-input" v-model='detail.selected[${index}]'></el-input>`
      title = `${titleStart}${inputHtml}${titleEnd}`
    })
    let titleTemplate = {
      template: title,
      data() {
        return props
      },
    }
    return h(titleTemplate)
  },
}
</script>

<style lang="scss">
.cloze-input {
  width: fit-content !important;
  min-width: 100px !important;
}
</style>
