<template>
  <el-form ref="roleForm" :model="roleForm" label-width="100px">
    <el-form-item label="操作用户" prop="writeRoleUser">
      <CustomSelect
        ref="writeSelect"
        placeholder="请选择用户"
        :value="roleForm.writeRoleUser"
        :total="roleForm.total"
        @input="searchUser"
        @change="selectWriteUser"
        @currentChange="getMoreUser"
        @visibleChange="getUserList"
      >
        <el-option
          v-for="item in userList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </CustomSelect>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="editRoleUsers">编辑</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { questionTypeAuth, questionTypeGet } from 'api/question'
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
        total: 0,
        writeRoleUser: []
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    const res = await questionTypeGet({ id: this.id })
    const { roleIds: writeIds, roleNames: writeNames } = this.compositionRoles(
      res.data.writeUserIds,
      res.data.writeUserNames
    )
    this.$nextTick(() => {
      this.roleForm.writeRoleUser.push(...writeIds)
      this.$refs['writeSelect'].$refs['elSelect'].cachedOptions.push(
        ...writeNames
      )
    })
  },
  methods: {
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const userList = await userListPage({
        name,
        curPage,
        type: 2,
        pageSize: 5
      })

      if (this.$store.getters.userId === 1) {
        userList.data.list.unshift({
          id: 1,
          name: '管理员'
        })
      }

      this.userList = userList.data.list
      this.roleForm.total =
        this.$store.getters.userId === 1
          ? userList.data.total + 1
          : userList.data.total
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(1, name)
    },
    // 获取更多用户
    getMoreUser(curPage, name) {
      this.getUserList(curPage, name)
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
      const res = await questionTypeAuth({
        id: this.id,
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
