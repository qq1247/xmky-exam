<template>
  <el-form ref="paramForm" :model="paramForm" label-width="100px">
    <el-form-item label="名称" prop="orgName">
      <el-input v-model="paramForm.orgName" placeholder="请输入单位名称" />
    </el-form-item>
    <el-form-item label="logo" prop="orgLogo">
      <el-upload
        class="avatar-uploader"
        name="files"
        :headers="headers"
        :show-file-list="false"
        action="/api/file/upload"
        :on-success="logoSuccess"
        :before-upload="beforeLogoUpload"
      >
        <img v-if="paramForm.orgLogo" :src="paramForm.orgLogo" class="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon" />
      </el-upload>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="setting">设置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { parmGet, parmLogo } from 'api/base'
export default {
  data() {
    return {
      headers: {
        Authorization: this.$store.getters.token
      },
      paramForm: {
        orgLogo: '',
        orgName: ''
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 初始化
    async init() {
      const { data } = await parmGet()
      this.paramForm.orgName = data.orgName
      this.paramForm.orgLogo = `/api/file/download?id=${Number(data.orgLogo)}`
    },
    // 设置
    setting() {
      this.$refs['paramForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const params = {
          orgLogo:
            this.$tools.getQueryParam(this.paramForm.orgLogo, 'id') || null,
          orgName: this.paramForm.orgName
        }

        const { code, msg } = await parmLogo(params)

        if (code === 200) {
          this.$message({
            message: '设置成功',
            duration: 1000
          })
        } else {
          this.$message.error(msg)
        }
      })
    },
    logoSuccess(res, file) {
      this.paramForm.orgLogo = `/api/file/download?id=${Number(
        res.data.fileIds
      )}`
    },
    beforeLogoUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt1M = file.size / 1024 / 1024 < 1

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!')
      }
      if (!isLt1M) {
        this.$message.error('上传头像图片大小不能超过 1MB!')
      }
      return isJPG && isLt1M
    }
  }
}
</script>

<style lang="scss" scoped>
.avatar-uploader {
  /deep/.el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    &:hover {
      border-color: #409eff;
    }
    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      line-height: 178px;
      text-align: center;
    }
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }
  }
}
</style>
