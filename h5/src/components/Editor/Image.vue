<template>
  <div class="upload-container">
    <el-dialog
      :visible.sync="panelShow"
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-upload
        name="files"
        :multiple="true"
        :headers="headers"
        :file-list="fileList"
        :show-file-list="true"
        :on-success="handleSuccess"
        :on-remove="handleRemove"
        :on-preview="handlePreview"
        :before-upload="beforeUpload"
        class="editor-slide-upload"
        action="/api/file/upload"
        list-type="picture-card"
      >
        <i slot="default" class="el-icon-plus" />
      </el-upload>
      <el-image-viewer
        v-if="showViewer"
        :on-close="closeViewer"
        :url-list="[url]"
        :z-index="2100"
      />
      <template slot="footer">
        <el-button type="primary" @click="hidePanel"> 取消 </el-button>
        <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import store from '@/store'
import ElImageViewer from 'element-ui/packages/image/src/image-viewer'
export default {
  name: 'EditorImageUpload',
  components: {
    ElImageViewer
  },
  props: {
    panelShow: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      headers: {
        Authorization: store.getters.token
      },
      listObj: {},
      fileList: [],
      showViewer: false,
      url: ''
    }
  },
  methods: {
    // 是否全部上传成功
    checkAllSuccess() {
      return Object.keys(this.listObj).every(
        (item) => this.listObj[item].hasSuccess
      )
    },
    // 确定事件
    handleSubmit() {
      const arr = Object.keys(this.listObj).map((v) => this.listObj[v])
      if (!this.checkAllSuccess()) {
        this.$message('请等待所有文件上传成功！')
        return
      }
      this.$emit('successUpload', arr)
      this.hidePanel()
    },
    // 上传成功
    handleSuccess(response, file) {
      const uid = file.uid
      const objKeyArr = Object.keys(this.listObj)
      for (let i = 0, len = objKeyArr.length; i < len; i++) {
        if (this.listObj[objKeyArr[i]].uid === uid) {
          this.listObj[objKeyArr[i]].url = `/api/file/download?id=${Number(
            response.data.fileIds
          )}`
          this.listObj[objKeyArr[i]].hasSuccess = true
          return
        }
      }
    },
    // 移除图片
    handleRemove(file) {
      const uid = file.uid
      const objKeyArr = Object.keys(this.listObj)
      for (let i = 0, len = objKeyArr.length; i < len; i++) {
        if (this.listObj[objKeyArr[i]].uid === uid) {
          delete this.listObj[objKeyArr[i]]
          return
        }
      }
    },
    // 上传前事件
    beforeUpload(file) {
      const _self = this
      const _URL = window.URL || window.webkitURL
      const fileName = file.uid
      this.listObj[fileName] = {}
      return new Promise((resolve, reject) => {
        const img = new Image()
        img.src = _URL.createObjectURL(file)
        img.onload = function() {
          _self.listObj[fileName] = {
            hasSuccess: false,
            uid: file.uid,
            width: img.width,
            height: img.height
          }
        }
        resolve(true)
      })
    },
    // 点击按钮预览图片
    handlePreview(file) {
      this.url = file.url
      this.showViewer = true
    },
    // 关闭查看器
    closeViewer() {
      this.showViewer = false
    },
    // 关闭弹窗
    hidePanel() {
      this.listObj = {}
      this.fileList = []
      this.$emit('hidePanel', false)
    }
  }
}
</script>

<style lang="scss" scoped>
.editor-slide-upload {
  margin-bottom: 20px;
}
</style>
