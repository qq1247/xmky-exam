<template>
  <div class="header-info">
    <el-avatar
      v-if="$store.getters.userAvatar"
      :size="32"
      :src="`/api/file/download?id=${Number($store.getters.userAvatar)}`"
    ><i
      class="common common-wo"
    /></el-avatar>
    <el-avatar v-else :size="32"><i class="common common-wo" /></el-avatar>
    <el-dropdown @command="handleCommand">
      <span class="el-dropdown-link">
        {{ level[onlyRole[0]] }}<i class="el-icon-arrow-down el-icon--right" />
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="edit">修改密码</el-dropdown-item>
        <el-dropdown-item command="out">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <div
      v-if="roles.includes('subAdmin')"
      class="change-role"
      @click="changeRole"
    >
      切换角色
    </div>
    <el-dialog
      :visible.sync="editForm.show"
      title="修改密码"
      :show-close="false"
      width="50%"
      :close-on-click-modal="false"
      append-to-body
      @close="resetData('editForm')"
    >
      <el-form ref="editForm" :model="editForm" :rules="editForm.rules">
        <el-form-item label="旧密码" label-width="80px" prop="oldPwd">
          <el-input v-model.trim="editForm.oldPwd" placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" label-width="80px" prop="newPwd">
          <el-input v-model.trim="editForm.newPwd" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="pwdUpdate">确定</el-button>
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getInfo, setInfo } from '@/utils/storage'
export default {
  data() {
    return {
      level: {
        admin: '管理员',
        subAdmin: '子管理员',
        user: '普通用户'
      },
      editForm: {
        show: false,
        oldPwd: '',
        newPwd: '',
        rules: {
          oldPwd: [
            { required: true, message: '请填写旧密码', trigger: 'blur' }
          ],
          newPwd: [
            { required: true, message: '请填写新密码', trigger: 'blur' }
          ]
        }
      }
    }
  },
  computed: {
    ...mapGetters(['roles', 'onlyRole'])
  },
  methods: {
    handleCommand(command) {
      if (command === 'edit') {
        this.editForm.show = true
      }
      if (command === 'out') {
        this.$store.dispatch('user/resetToken').then(() => {
          this.$message('登出成功！')
          this.$router.push('/')
        })
      }
    },
    async pwdUpdate() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return
        }

        import('api/common.js').then(async(common) => {
          const res = await common.loginPwd({
            oldPwd: this.editForm.oldPwd,
            newPwd: this.editForm.newPwd
          })
          if (res.code !== 200) {
            this.$message.warning(res.msg)
            return
          }
          this.editForm.show = false
        })
      })
    },
    // 切换角色
    changeRole() {
      this.$confirm(
        `确定要切换为【${
          this.onlyRole.includes('subAdmin') ? '普通用户' : '子管理员'
        }】角色吗？`,
        '角色切换',
        {
          confirmButtonText: '切换',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async() => {
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
          path: '/'
        })
      })
    },
    // 清空还原数据
    resetData(name) {
      this.$refs[name].resetFields()
    }
  }
}
</script>

<style lang="scss" scoped>
.header-info {
  display: flex;
  align-items: center;
  .change-role {
    width: 72px;
    height: 32px;
    font-size: 14px;
    text-align: center;
    color: #fff;
    background: #0094e5;
    line-height: 32px;
    border-radius: 4px;
    cursor: pointer;
  }
}

/deep/ .el-avatar {
  background: #fff;
  border: 1px solid rgba(#000, 0.1);
  .common {
    color: rgba(#000, 0.3);
  }
}

.el-dropdown-link {
  cursor: pointer;
  margin: 0 35px 0 10px;
}
.el-icon-arrow-down {
  font-size: 12px;
}
</style>
