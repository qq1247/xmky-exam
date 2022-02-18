<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 11:01:02
 * @LastEditors: Che
 * @LastEditTime: 2021-11-12 16:51:51
-->
<template>
  <div class="param-option">
    <el-form :model="paramForm" ref="paramForm">
      <el-form-item label="备份目录" label-width="100px" prop="dbBakDir">
        <el-input
          placeholder="请输入备份目录"
          v-model="paramForm.dbBakDir"
        ></el-input>
      </el-form-item>
      <el-form-item label label-width="100px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { parmGet, parmDb } from 'api/base'
export default {
  data() {
    return {
      paramForm: {
        dbBakDir: '',
      },
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
      this.$refs['paramForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await parmDb({
          bakDir: this.paramForm.dbBakDir,
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
