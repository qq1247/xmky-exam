<template>
  <el-form ref="paramForm" :model="paramForm" label-width="100px">
    <el-form-item label="目录名称" prop="dbBakDir">
      <el-input
        v-model="paramForm.dbBakDir"
        placeholder="请输入目录名称"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="setting">设置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { parmGet, parmDb } from 'api/base'
export default {
  data() {
    return {
      paramForm: {
        dbBakDir: ''
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const { data } = await parmGet()
      this.paramForm.dbBakDir = data.dbBakDir
    },
    // 设置
    setting() {
      this.$refs['paramForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await parmDb({
          bakDir: this.paramForm.dbBakDir
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
