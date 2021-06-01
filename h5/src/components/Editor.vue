<template>
  <div class="editor">
    <ckeditor
      :editor="editor"
      v-model="value"
      :config="editorConfig"
      style="border: 1px solid #ececec"
    ></ckeditor>
  </div>
</template>
<script>
import InlineEditor from "@ckeditor/ckeditor5-build-inline"
import "@ckeditor/ckeditor5-build-inline/build/translations/zh-cn"
export default {
  name: "Editor",
  props: {
    id: { type: String, default: "editorId" },
    value: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      editor: InlineEditor,
      editorConfig: {
        language: "zh-cn"
      }
    }
  },
  watch: {
    value: function () {
      this.$emit("editorListener", this.id, this.value)
    }
  }
}
</script>
<style lang="scss" scoped>
.editor {
  margin-right: 15px;
}
/deep/ .cke_bottom {
  user-select: none;
  margin: 0px;
  padding: 0px;
  height: 2px;
}
/deep/ .cke_resizer {
  margin-top: -6px;
  margin-right: 0px;
}
/deep/ .cke_top {
  padding: 0;
}
/deep/ .cke_toolbar {
  height: 28px;
}
/deep/ .cke_editable {
  margin: 10px;
}
</style>
