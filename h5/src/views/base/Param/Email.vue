<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 10:59:48
 * @LastEditors: Che
 * @LastEditTime: 2021-11-12 17:05:58
-->
<template>
  <div class="param-option">
    <el-form :model="paramForm" :rules="paramForm.rules" ref="paramForm">
      <el-form-item label="用户名" label-width="100px" prop="emailUserName">
        <el-input
          placeholder="请输入用户名"
          v-model="paramForm.emailUserName"
        ></el-input>
      </el-form-item>
      <el-form-item label="密码" label-width="100px" prop="emailPwd">
        <el-input
          placeholder="请输入密码"
          v-model="paramForm.emailPwd"
        ></el-input>
      </el-form-item>
      <el-form-item label="主机" label-width="100px" prop="emailHost">
        <el-input
          placeholder="请输入主机"
          v-model="paramForm.emailHost"
        ></el-input>
      </el-form-item>
      <el-form-item label="协议" label-width="100px" prop="emailProtocol">
        <el-input
          placeholder="请输入协议"
          v-model="paramForm.emailProtocol"
        ></el-input>
      </el-form-item>
      <el-form-item label="编码" label-width="100px" prop="emailEncode">
        <el-input
          placeholder="请输入编码"
          v-model="paramForm.emailEncode"
        ></el-input>
      </el-form-item>
      <el-form-item label label-width="100px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { parmGet, parmEmail } from 'api/base'
export default {
  data() {
    return {
      paramForm: {
        emailPwd: '',
        emailHost: '',
        emailEncode: '',
        emailUserName: '',
        emailProtocol: '',
        rules: {
          emailPwd: [
            { required: true, message: '请输入密码', trigger: 'blur' },
          ],
          emailHost: [
            { required: true, message: '请输入主机', trigger: 'blur' },
          ],
          emailEncode: [
            { required: true, message: '请输入编码', trigger: 'blur' },
          ],
          emailUserName: [
            { required: true, message: '请输入用户名', trigger: 'blur' },
          ],
          emailProtocol: [
            { required: true, message: '请输入协议', trigger: 'blur' },
          ],
        },
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      const { data } = await parmGet()
      this.paramForm.emailPwd = data.emailPwd
      this.paramForm.emailHost = data.emailHost
      this.paramForm.emailProtocol = data.emailProtocol
      this.paramForm.emailEncode = data.emailEncode
      this.paramForm.emailUserName = data.emailUserName
    },
    // 设置
    setting() {
      this.$refs['paramForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const params = {
          pwd: this.paramForm.emailPwd,
          host: this.paramForm.emailHost,
          encode: this.paramForm.emailEncode,
          userName: this.paramForm.emailUserName,
          protocol: this.paramForm.emailProtocol,
        }

        const { code, msg } = await parmEmail(params)

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
