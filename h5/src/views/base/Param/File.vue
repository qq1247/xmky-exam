<template>
  <el-form ref="paramForm" :model="paramForm" label-width="100px">
    <el-form-item label="目录名称" prop="fileUploadDir">
      <el-input
        v-model="paramForm.fileUploadDir"
        placeholder="请输入目录名称"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="setting">设置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { parmGet, parmFile } from 'api/base'
export default {
  data() {
    return {
      paramForm: {
        fileUploadDir: ''
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const { data } = await parmGet()
      this.paramForm.fileUploadDir = data.fileUploadDir
    },
    // 设置
    setting() {
      this.$refs['paramForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await parmFile({
          uploadDir: this.paramForm.fileUploadDir
        })

        if (code === 200) {
          this.$message({
            message: '设置成功',
            duration: 1000
          })
          await this.$parent.init()
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>
