<template>
  <el-form ref="roleForm" :model="roleForm" label-width="100px">
    <el-form-item label="共享给" prop="readRoleUser">
      <CustomSelect
        ref="readSelect"
        placeholder="请选择用户"
        :value="roleForm.readRoleUser"
        :total="roleForm.total"
        @input="searchUser"
        @change="selectReadUser"
        @currentChange="getMoreUser"
        @visibleChange="getUserList"
      >
        <el-option
          v-for="item in roleForm.roleUserList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </CustomSelect>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="editRoleUsers">共享</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { paperTypeAuth, paperTypeGet } from 'api/paper'
import { userListPage } from 'api/user'
import CustomSelect from 'components/CustomSelect.vue'

export default {
  components: {
    CustomSelect
  },
  data() {
    return {
      id: null,
      userList: [],
      roleForm: {
        show: false,
        curPage: 1,
        pageSize: 5,
        total: 0,
        readRoleUser: [],
        writeRoleUser: [],
        roleUserList: []
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    const res = await paperTypeGet({ id: this.id })
    const { roleIds: readIds, roleNames: readNames } = this.compositionRoles(
      res.data.readUserIds,
      res.data.readUserNames
    )
    this.$nextTick(() => {
      this.roleForm.readRoleUser.push(...readIds)
      this.$refs['readSelect'].$refs['elSelect'].cachedOptions.push(
        ...readNames
      )
    })
  },
  methods: {
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const roleUserList = await userListPage({
        name,
        curPage,
        pageSize: this.pageSize
      })

      if (this.$store.getters.userId === 1) {
        roleUserList.data.list.unshift({
          id: 1,
          name: '管理员'
        })
      }

      this.roleForm.roleUserList = roleUserList.data.list
      this.roleForm.total =
        this.$store.getters.userId === 1
          ? roleUserList.data.total + 1
          : roleUserList.data.total
    },
    // 获取更多用户
    getMoreUser(curPage, name) {
      this.getUserList(curPage, name)
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(1, name)
    },
    // 选择读取权限用户
    selectReadUser(e) {
      this.roleForm.readRoleUser = e
    },
    // 选择阅读权限用户
    selectWriteUser(e) {
      this.roleForm.writeRoleUser = e
    },
    // 组合权限人员信息
    compositionRoles(userIds, userNames) {
      const ids = userIds
        .filter((item) => item !== '')
        .map((item) => Number(item))
      const names = userNames
      const roles = ids.reduce(
        (acc, cur, index) => {
          acc['roleIds'].push(cur)
          acc['roleNames'].push({
            currentLabel: names[index],
            currentValue: cur,
            label: names[index],
            value: cur
          })
          return acc
        },
        { roleIds: [], roleNames: [] }
      )
      return roles
    },
    // 编辑权限
    async editRoleUsers() {
      const res = await paperTypeAuth({
        id: this.id,
        readUserIds: this.roleForm.readRoleUser.join(','),
        writeUserIds: this.roleForm.writeRoleUser.join(',')
      })
      if (res?.code === 200) {
        this.$message.success('权限编辑成功！')
        this.$router.back()
      } else {
        this.$message.error('权限编辑失败！')
      }
    }
  }
}
</script>
