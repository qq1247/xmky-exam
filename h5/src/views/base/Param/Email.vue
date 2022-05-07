<template>
  <el-form
    ref="paramForm"
    :model="paramForm"
    :rules="paramForm.rules"
    label-width="100px"
  >
    <el-form-item label="账号" prop="emailUserName">
      <el-input
        v-model="paramForm.emailUserName"
        placeholder="请输入账号"
      />
    </el-form-item>
    <el-form-item label="授权码" prop="emailPwd">
      <el-input
        v-model="paramForm.emailPwd"
        placeholder="请输入授权码"
      />
    </el-form-item>
    <el-form-item label="主机" prop="emailHost">
      <el-input
        v-model="paramForm.emailHost"
        placeholder="请输入主机"
      />
    </el-form-item>
    <el-form-item label="协议" prop="emailProtocol">
      <el-input
        v-model="paramForm.emailProtocol"
        placeholder="请输入协议"
      />
    </el-form-item>
    <el-form-item label="编码" prop="emailEncode">
      <el-input
        v-model="paramForm.emailEncode"
        placeholder="请输入编码"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="setting">设置</el-button>
    </el-form-item>
  </el-form>
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
            { required: true, message: '请输入密码', trigger: 'blur' }
          ],
          emailHost: [
            { required: true, message: '请输入主机', trigger: 'blur' }
          ],
          emailEncode: [
            { required: true, message: '请输入编码', trigger: 'blur' }
          ],
          emailUserName: [
            { required: true, message: '请输入用户名', trigger: 'blur' }
          ],
          emailProtocol: [
            { required: true, message: '请输入协议', trigger: 'blur' }
          ]
        }
      }
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
      this.$refs['paramForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const params = {
          pwd: this.paramForm.emailPwd,
          host: this.paramForm.emailHost,
          encode: this.paramForm.emailEncode,
          userName: this.paramForm.emailUserName,
          protocol: this.paramForm.emailProtocol
        }

        const { code, msg } = await parmEmail(params)

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
