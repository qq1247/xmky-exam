<!--
 * @Description: Info
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2022-01-07 09:51:39
 * @LastEditors: Che
 * @LastEditTime: 2022-01-07 10:26:10
-->
<template>
  <div>
    <el-dropdown trigger="hover" @command="handleCommand">
      <div class="el-dropdown-link">
        <p>
          {{ level[onlyRole[0]] }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </p>
      </div>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          icon="common common-change"
          command="change"
          v-if="roles.includes('subAdmin')"
          >切换角色</el-dropdown-item
        >
        <el-dropdown-item icon="common common-edit" command="edit"
          >修改密码</el-dropdown-item
        >
        <el-dropdown-item icon="common common-login-out" command="out"
          >退出</el-dropdown-item
        >
      </el-dropdown-menu>
    </el-dropdown>
    <el-dialog
      :visible.sync="editForm.show"
      title="修改密码"
      :show-close="false"
      width="50%"
      :close-on-click-modal="false"
      @close="resetData('editForm')"
      append-to-body
    >
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="旧密码" label-width="80px" prop="oldPwd">
          <el-input
            placeholder="请输入旧密码"
            v-model.trim="editForm.oldPwd"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" label-width="80px" prop="newPwd">
          <el-input
            placeholder="请输入新密码"
            v-model.trim="editForm.newPwd"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="pwdUpdate" type="primary">确定</el-button>
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { loginPwd } from 'api/common'
import { getInfo, setInfo } from '@/utils/storage'
export default {
  data() {
    return {
      level: {
        admin: '管理员',
        subAdmin: '子管理员',
        user: '普通用户',
      },
      editForm: {
        show: false,
        oldPwd: '',
        newPwd: '',
        rules: {
          oldPwd: [
            { required: true, message: '请填写旧密码', trigger: 'blur' },
          ],
          newPwd: [
            { required: true, message: '请填写新密码', trigger: 'blur' },
          ],
        },
      },
    }
  },
  computed: {
    ...mapGetters(['roles', 'onlyRole']),
  },
  methods: {
    async handleCommand(command) {
      if (command === 'edit') {
        this.editForm.show = true
      }

      if (command === 'out') {
        this.$store.dispatch('user/resetToken').then(() => {
          this.$message('登出成功！')
          this.$router.push('/')
        })
      }

      if (command === 'change') {
        this.$confirm(
          `确定要切换为【${
            this.onlyRole.includes('subAdmin') ? '普通用户' : '子管理员'
          }】角色吗？`,
          '角色切换',
          {
            confirmButtonText: '切换',
            cancelButtonText: '取消',
            type: 'warning',
          }
        ).then(async () => {
          const onlyRole = this.onlyRole.includes('subAdmin')
            ? ['user']
            : ['subAdmin']
          this.$store.commit('user/SET_ONLY_ROLE', onlyRole)
          const userInfo = getInfo()
          userInfo.onlyRole = onlyRole
          setInfo(userInfo)
          const accessRoutes = await this.$store.dispatch(
            'permission/generateRoutes',
            onlyRole
          )
          this.$router.addRoutes(accessRoutes)
          this.$router.replace({
            path: '/',
          })
        })
      }
    },
    async pwdUpdate() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return
        }
        const res = await loginPwd({
          oldPwd: this.editForm.oldPwd,
          newPwd: this.editForm.newPwd,
        })

        if (res.code != 200) {
          this.$message.warning(res.msg)
          return
        }

        this.editForm.show = false
      })
    },
    // 清空还原数据
    resetData(name) {
      this.$refs[name].resetFields()
    },
  },
}
</script>

<style lang="scss" scoped></style>
