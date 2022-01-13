<!--
 * @Description: 封装上传组件
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-18 16:50:04
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 14:51:53
-->
<template>
  <el-upload
    name="files"
    ref="upload"
    :multiple="true"
    :headers="headers"
    :file-list="files"
    action="/api/file/upload"
    :limit="limit"
    :accept="types[type].accept"
    :list-type="type === 'image' ? 'picture-card' : 'text'"
    :on-error="error"
    :on-exceed="exceed"
    :on-remove="remove"
    :on-success="success"
    :before-upload="beforeUpload"
  >
    <div slot="default">
      <el-button type="primary" v-if="type !== 'image'"
        ><i class="common common-line-upload"></i
        >&nbsp;&nbsp;点击上传</el-button
      >
      <i class="el-icon-plus" v-else></i>
      <div slot="tip" class="upload-tip">
        可以上传{{ limit ? limit : 'N' }}个{{
          type === '*' ? '任意' : this.type
        }}文件，且不超过{{ size }}M
      </div>
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
    limit: {
      type: [Number, String],
      default: 1,
    },
    files: {
      type: Array,
      default: () => [],
    },
    size: {
      type: [String, Number],
      default: 20,
    },
  },
  data() {
    return {
      headers: {
        Authorization: this.$store.getters.token,
      },
      fileList: this.files,
      totalSize: 0,
      types: {
        '*': {
          accept: '*',
        },
        image: {
          accept: 'image/*',
          suffix: ['jpeg', 'jpg', 'png', 'gif'],
        },
        audio: {
          accept: 'audio/*',
          suffix: [
            'mp4',
            'avi',
            'mov',
            'm4v',
            'wmv',
            '3gp',
            'flv',
            'asf',
            'asx',
            'amr',
            'flac',
            'rm',
            'rmvb',
            'mkv',
            'dat',
            'dts',
            'ts',
            'tp',
            'vob',
            'swf',
          ],
        },
        video: {
          accept: 'video/*',
          suffix: ['mp3', 'wma', 'amr', 'mp4', 'flac', 'aac', 'ape', 'ogg'],
        },
        word: {
          accept:
            '.docx, .doc, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          suffix: ['docx', 'doc'],
        },
        excel: {
          accept:
            '.xlsx, .xls, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          suffix: ['xlsx', 'xls'],
        },
        ppt: {
          accept:
            '.pptx, .ppt, application/vnd.openxmlformats-officedocument.presentationml.presentation',
          suffix: ['pptx', 'ppt'],
        },
      },
    }
  },
  methods: {
    exceed() {
      this.$message.warning(`最多选择${this.limit}个文件！`)
    },
    beforeUpload({ name, size }) {
      const _suffix = name.slice(name.lastIndexOf('.') + 1).toLowerCase()
      const isSuffix =
        this.type !== '*' && this.types[this.type].suffix.includes(_suffix)
      // 部分火狐浏览器获取不到word类型，根据后缀名特殊处理下
      if (!isSuffix) {
        this.$message.warning('文件格式错误')
        return false
      }

      this.totalSize += size
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
