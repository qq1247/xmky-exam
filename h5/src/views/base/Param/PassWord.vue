<template>
  <el-form ref="paramForm" :model="paramForm" label-width="100px">
    <el-form-item label="类型" prop="pwdType">
      <el-radio
        v-for="item in paramForm.typeList"
        :key="item.value"
        v-model="paramForm.pwdType"
        :label="item.value"
      >{{ item.name }}</el-radio>
    </el-form-item>
    <el-form-item v-if="paramForm.pwdType === 2" label="密码" prop="pwdValue">
      <el-input
        v-model.trim="paramForm.pwdValue"
        placeholder="请输入密码"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="setting">设置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { parmGet, parmPwd } from 'api/base'
export default {
  data() {
    return {
      paramForm: {
        pwdType: 1,
        pwdValue: '',
        typeList: [
          {
            name: '随机',
            value: 1
          },
          {
            name: '固定',
            value: 2
          }
        ]
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const { data } = await parmGet()
      this.paramForm.pwdType = data.pwdType
      this.paramForm.pwdValue = data.pwdValue
    },
    // 设置
    setting() {
      this.$refs['paramForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        if (this.paramForm.pwdType === 2 && !this.paramForm.pwdValue) {
          this.$message({
            message: '请输入密码',
            duration: 1000,
            type: 'warning'
          })
          return false
        }

        const params =
          this.paramForm.pwdType === 1
            ? {
              type: this.paramForm.pwdType
            }
            : {
              type: this.paramForm.pwdType,
              value: this.paramForm.pwdValue
            }

        const { code, msg } = await parmPwd(params)

        if (code === 200) {
          this.$message({
            message: '设置成功'
          })
        } else {
          this.$message.error(msg)
        }
      })
    }
  }
}
</script>
