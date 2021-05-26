<template>
  <div>
    <div :id="id"></div>
  </div>
</template>
<script>
import ClassicEditor from "@ckeditor/ckeditor5-build-classic"
export default {
  name: "Editor",
  props: {
    id: { type: String, default: "editorId" },
    value: {
      type: String,
      default: ""
    },
    height: {
      type: Number,
      default: 40
    }
  },
  data() {
    return {
      cfg: {
        toolbar: [["Styles", "Format", "Font", "FontSize"]],
        removePlugins: "elementspath",
        height: 0
      }
    }
  },
  watch: {
    value: function () {
      this.$emit("editorListener", this.id, this.value)
    }
  },
  created() {
    this.cfg.height = this.height
    ClassicEditor.create(document.querySelector(`#${this.id}`))
      .then(editor => {
        console.log(editor)
      })
      .catch(error => {
        console.error(error)
      })
  }
}
</script>
<style lang="scss" scoped>
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
