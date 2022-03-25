<!--
 * @Description: Info
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2022-01-07 09:51:39
 * @LastEditors: Che
 * @LastEditTime: 2022-01-18 09:53:05
-->
<template>
  <div class="header-info">
    <span class="info-role">{{ level[onlyRole[0]] }}</span>
    <i
      class="common common-change"
      v-if="roles.includes('subAdmin')"
      @click="handleCommand('change')"
      title="切换角色"
    ></i>
    <i
      class="common common-setting"
      @click="handleCommand('edit')"
      title="修改密码"
    ></i>
    <Screenfull class="screenfull" />
    <i
      class="common common-login-out"
      @click="handleCommand('out')"
      title="退出登录"
    ></i>
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
import Screenfull from '@/components/Screenfull/index.vue'
export default {
  components: {
    Screenfull,
  },
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
  components: { Screenfull },
}
</script>

<style lang="scss" scoped>
.header-info {
  display: flex;
  align-items: center;
  i {
    display: inline-block;
    line-height: 20px;
    font-size: 18px;
    padding-left: 10px;
    margin-left: 10px;
    cursor: pointer;
    border-left: 1px solid #f1f1f1;
    &:hover {
      color: #0095e5;
    }
  }
  .common-change {
    width: 24px;
    height: 24px;
    border-radius: 20px;
    background: #0095e5;
    font-size: 16px;
    color: #fff;
    margin-left: 20px;
    padding-left: 0;
    text-align: center;
    border: 1px solid #0095e5;
    &:hover {
      transition: all 0.3s ease-in-out;
      background: #fff;
    }
  }
  .screenfull {
    margin-left: 10px;
    padding-left: 10px;
    border-left: 1px solid #f1f1f1;
  }
}
</style>
