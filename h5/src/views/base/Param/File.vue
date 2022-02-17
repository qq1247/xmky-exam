<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 11:00:29
 * @LastEditors: Che
 * @LastEditTime: 2021-11-12 16:50:17
-->
<template>
  <div class="param-option">
    <el-form :model="paramForm" ref="paramForm">
      <el-form-item label="目录名称" label-width="100px" prop="fileUploadDir">
        <el-input
          placeholder="请输入目录名称"
          v-model="paramForm.fileUploadDir"
        ></el-input>
      </el-form-item>
      <el-form-item label label-width="100px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { parmGet, parmFile } from 'api/base'
export default {
  data() {
    return {
      paramForm: {
        fileUploadDir: '',
      },
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
      this.$refs['paramForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await parmFile({
          uploadDir: this.paramForm.fileUploadDir,
        })

        if (code === 200) {
          this.$message({
            message: '设置成功',
            duration: 1000,
          })
          await this.$parent.init()
        } else {
          this.$message.error(msg)
        }
      })
    },
  },
}
</script>
