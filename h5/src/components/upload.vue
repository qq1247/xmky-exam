<!--
 * @Description: 封装上传组件
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-08-18 16:50:04
 * @LastEditors: Che
 * @LastEditTime: 2021-08-20 15:11:38
-->
<template>
  <el-upload
    name="files"
    ref="upload"
    :multiple="true"
    :headers="headers"
    :file-list="fileList"
    action="/api/file/upload"
    :limit="types[type].limit"
    :accept="types[type].accept"
    :list-type="type === 'image' ? 'picture-card' : 'text'"
    :on-error="error"
    :on-exceed="exceed"
    :on-remove="remove"
    :on-success="success"
    :before-upload="beforeUpload"
  >
    <el-button size="small" type="primary" v-if="type !== 'image'"
      >点击上传</el-button
    >
    <i class="el-icon-plus" v-else></i>
    <div slot="tip" class="upload-tip">
      可以上传{{ types[type].limit ? types[type].limit : 'N' }}个{{
        type === '*' ? '任意' : this.type
      }}文件，且不超过{{ size }}M
    </div>
  </el-upload>
</template>

<script>
export default {
  name: 'Upload',
  props: {
    type: {
      type: String,
      default: '*',
    },
  },
  data() {
    return {
      headers: {
        Authorization: this.$store.getters.token,
      },
      fileList: [],
      size: 20,
      totalSize: 0,
      types: {
        '*': {
          accept: '*',
        },
        image: {
          accept: 'image/*',
          type: 'image/',
          limit: 10,
        },
        audio: {
          accept: 'audio/*',
          type: 'audio/',
          limit: 10,
        },
        video: {
          accept: 'video/*',
          type: 'video/',
          limit: 10,
        },
        word: {
          accept:
            '.docx, .doc, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          limit: 1,
        },
        excel: {
          accept:
            '.xlsx, .xls, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          limit: 10,
        },
        ppt: {
          accept:
            '.pptx, .ppt, application/vnd.openxmlformats-officedocument.presentationml.presentation',
          type: 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
          limit: 10,
        },
      },
    }
  },
  methods: {
    exceed(files, fileList) {
      this.$message.warning(`最多选择${this.types[this.type].limit}个文件！`)
    },
    beforeUpload(file) {
      const isType =
        this.type !== '*' &&
        file.type.indexOf(this.types[this.type].type) === -1
      if (isType) {
        this.$message.warning('文件格式错误')
        return false
      }

      this.totalSize += file.size
      if (this.totalSize > this.size * 1024 * 1024) {
        this.$message.warning(`上传文件容量最大${this.size}M`)
        return false
      }
    },
    remove(file, fileList) {
      this.setFileList(fileList)
      this.$emit('remove', file, fileList)
    },
    success(response, file, fileList) {
      this.setFileList(fileList)
      this.$emit('success', response, file, fileList)
    },
    error() {
      this.$message.error('上传失败，请重试！')
    },
    clear() {
      this.$refs.upload.clearFiles()
      this.fileList = []
      this.totalSize = 0
    },
    setFileList(fileList) {
      this.fileList = fileList
      this.totalSize = this.fileList.reduce((acc, cur) => {
        return acc + cur.size
      }, 0)
    },
  },
}
</script>

<style lang="scss" scoped>
.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
