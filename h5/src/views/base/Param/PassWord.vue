<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 11:01:22
 * @LastEditors: Che
 * @LastEditTime: 2021-11-12 18:05:06
-->
<template>
  <div class="param-option">
    <div class="param-title">用户密码</div>
    <el-form :model="paramForm" :label-position="labelPosition" ref="paramForm">
      <el-form-item label="类型" label-width="100px" prop="type">
        <el-radio
          v-for="item in paramForm.typeList"
          :key="item.value"
          v-model="paramForm.type"
          :label="item.value"
          >{{ item.name }}</el-radio
        >
      </el-form-item>
      <el-form-item
        label="密码"
        label-width="100px"
        prop="value"
        v-if="paramForm.type === 2"
      >
        <el-input
          placeholder="请输入密码"
          v-model.trim="paramForm.value"
        ></el-input>
      </el-form-item>
      <el-form-item label label-width="100px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { parmPwd } from 'api/base'
export default {
  props: {
    pwdType: {
      type: Number,
      default: 1,
    },
    pwdValue: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      labelPosition: 'left',
      paramForm: {
        type: this.pwdType,
        value: this.pwdValue,
        typeList: [
          {
            name: '随机',
            value: 1,
          },
          {
            name: '固定',
            value: 2,
          },
        ],
      },
    }
  },
  watch: {
    pwdType: {
      immediate: true,
      handler(n, o) {
        this.paramForm.type = n
      },
    },
    pwdValue: {
      immediate: true,
      handler(n, o) {
        this.paramForm.value = n
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

        if (this.paramForm.type === 2 && !this.paramForm.value) {
          this.$message({
            message: '请输入密码',
            duration: 1000,
            type: 'warning',
          })
          return false
        }

        const params =
          this.paramForm.type === 1
            ? {
                type: this.paramForm.type,
              }
            : {
                type: this.paramForm.type,
                value: this.paramForm.value,
              }

        const { code, msg } = await parmPwd(params)

        if (code === 200) {
          this.$message({
            message: '设置成功',
            duration: 1000,
          })
          this.$emit('init')
        } else {
          this.$message.error(msg)
        }
      })
    },
  },
}
</script>
