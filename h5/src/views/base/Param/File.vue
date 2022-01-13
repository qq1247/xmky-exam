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
    <div class="param-title">附件目录设置</div>
    <el-form :model="paramForm" :label-position="labelPosition" ref="paramForm">
      <el-form-item label="目录名称" label-width="100px" prop="uploadDir">
        <el-input
          placeholder="请输入目录名称"
          v-model="paramForm.uploadDir"
        ></el-input>
      </el-form-item>
      <el-form-item label label-width="100px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { parmFile } from 'api/base'
export default {
  props: {
    dir: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      labelPosition: 'left',
      paramForm: {
        uploadDir: this.dir,
      },
    }
  },
  watch: {
    dir: {
      immediate: true,
      handler() {
        this.paramForm.uploadDir = this.dir
      },
    },
  },
  methods: {
    // 设置
    setting() {
      this.$refs['paramForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await parmFile({
          uploadDir: this.paramForm.uploadDir,
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
