<!--
 * @Description: 权限
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:05:04
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 10:03:18
-->

<template>
  <div class="container">
    <el-alert
      show-icon
      type="success"
      effect="dark"
      title="协助编辑"
      description="允许其他子管理员协助添加试题。如自己创建一个分类后，让同事添加试题，自己做审核"
      style="margin-bottom: 20px"
    ></el-alert>
    <el-form :model="roleForm" ref="roleForm" label-width="120px">
      <el-form-item label="编辑权限" prop="writeRoleUser">
        <CustomSelect
          ref="writeSelect"
          placeholder="请选择授权用户"
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
          ></el-option>
        </CustomSelect>
      </el-form-item>
    </el-form>
    <div class="form-footer">
      <el-button @click="editRoleUsers" type="primary">编辑</el-button>
    </div>
  </div>
</template>

<script>
import { questionTypeAuth, questionTypeGet } from 'api/question'
import { userListPage } from 'api/user'
import CustomSelect from 'components/CustomSelect.vue'

export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      id: null,
      userList: [],
      roleForm: {
        total: 0,
        writeRoleUser: [],
      },
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
        pageSize: 5,
      })

      if (this.$store.getters.userId == 1) {
        userList.data.list.unshift({
          id: 1,
          name: '管理员',
        })
      }

      this.userList = userList.data.list
      this.roleForm.total =
        this.$store.getters.userId == 1
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
            value: cur,
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
        writeUserIds: this.roleForm.writeRoleUser.join(','),
      })
      if (res?.code == 200) {
        this.$message.success('权限编辑成功！')
        this.$router.back()
      } else {
        this.$message.error('权限编辑失败！')
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  padding-left: 120px;
}
</style>
