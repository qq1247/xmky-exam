<!--
 * @Description: tinymce富文本
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-12-16 15:16:59
 * @LastEditors: Che
 * @LastEditTime: 2022-01-17 09:31:05
-->
<template>
  <div class="tinymce-box">
    <editor
      :init="init"
      class="tinymce-content"
      v-model="myValue"
      :disabled="disabled"
      :placeholder="placeholder"
      @input="editorListener($event)"
    >
    </editor>
    <tinymce-image ref="customImage"></tinymce-image>
  </div>
</template>

<script>
import _ from 'lodash'
import TinymceImage from './TinymceImage'
// 文档 http://tinymce.ax-z.cn/
// 引入组件
import tinymce from 'tinymce/tinymce' // tinymce默认hidden，不引入不显示
import Editor from '@tinymce/tinymce-vue'

// 引入富文本编辑器主题的js和css
import 'tinymce/skins/content/default/content.css'
import 'tinymce/themes/silver/theme.min.js'
import 'tinymce/icons/default/icons' // 解决了icons.js 报错Unexpected token '<'
import 'tinymce/plugins/table'
import 'tinymce/plugins/imagetools'
import 'tinymce/plugins/uploadImg'
export default {
  components: {
    Editor,
    TinymceImage,
  },
  name: 'Tinymce',
  props: {
    placeholder: {
      type: String,
      default: '',
    },
    // 默认的富文本内容
    value: {
      type: String,
      default: '',
    },
    // 基本路径，默认为空根目录，如果你的项目发布后的地址为目录形式，
    // 即abc.com/tinymce，baseUrl需要配置成tinymce，不然发布后资源会找不到
    baseUrl: {
      type: String,
      default: window.location.origin ? window.location.origin : '',
    },
    // 禁用
    disabled: {
      type: Boolean,
      default: false,
    },
    plugins: {
      type: [String, Array],
      default: 'uploadImg table imagetools',
      // 'link lists image code table wordcount media preview fullscreen help',
    },
    toolbar: {
      type: [String, Array],
      default:
        'uploadImg | bold italic underline strikethrough |  alignleft aligncenter alignright alignjustify | forecolor backcolor table | fontsizeselect | formatselect',
      // 'bold italic underline strikethrough | fontsizeselect | formatselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent blockquote | undo redo | link unlink code lists table image media | removeformat | fullscreen preview',
    },
    id: {
      type: String,
      default: 'editorId',
    },
    border: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      init: {
        language_url: `${this.baseUrl}/tinymce/langs/zh_CN.js`,
        language: 'zh_CN',
        skin_url: `${this.baseUrl}/tinymce/skins/ui/oxide`,
        // skin_url: 'tinymce/skins/ui/oxide-dark', // 暗色系
        convert_urls: false,
        height: 300,
        inline: true,
        // content_css（为编辑区指定css文件）,加上就不显示字数统计了
        // content_css: `${this.baseUrl}tinymce/skins/content/default/content.css`,
        // （指定需加载的插件）
        plugins: this.plugins,
        toolbar: this.toolbar, // （自定义工具栏）
        statusbar: false, // 底部的状态栏
        menubar: '', // （1级菜单）最上方的菜单
        branding: false, // （隐藏右下角技术支持）水印“Powered by TinyMCE”
        // 此处为图片上传处理函数，这个直接用了base64的图片形式上传图片，
        // 如需ajax上传可参考https://www.tiny.cloud/docs/configure/file-image-upload/#images_upload_handler
        /* images_upload_handler: (blobInfo, success, failure) => {
          console.log(blobInfo)
          const img = 'data:image/jpeg;base64,' + blobInfo.base64()
          success(img)
        }, */
      },
      myValue: this.value,
    }
  },
  mounted() {
    tinymce.init({})
  },
  watch: {
    value(newValue) {
      this.myValue = newValue
    },
    myValue(newValue) {
      this.$emit('input', newValue)
    },
  },
  methods: {
    editorListener: _.debounce(function ($event) {
      this.$emit('editorListener', this.id, $event)
    }, 300),
  },
}
</script>
<style lang="scss" scoped>
.tinymce-box {
  width: 100%;
  flex: 1;
  position: relative;
  .tinymce-content {
    border: 1px solid #dcdfe6;
    padding: 0 10px;
    border-radius: 3px;
    &[contentEditable='true']:focus,
    &[contentEditable='true']:hover {
      border: 1px solid #c0c4cc;
      cursor: text;
    }
  }
}
/deep/
  .mce-content-body:not([dir='rtl'])[data-mce-placeholder]:not(.mce-visualblocks)::before {
  left: 10px;
  color: #c4c4cf;
}
</style>
