<!--
 * @Description: 设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 15:43:20
-->
<template>
  <div class="container">
    <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
      <el-form-item label="所属机构" label-width="120px" prop="orgId">
        <CustomSelect
          :multiple="false"
          ref="orgSelect"
          placeholder="请选择机构"
          :value="editForm.orgId"
          :total="editForm.orgListpage.total"
          @input="searchOrg"
          @change="selectOrg"
          @currentChange="getMoreOrg"
          @visibleChange="getOrgList"
        >
          <el-option
            v-for="item in editForm.orgListpage.list"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </CustomSelect>
      </el-form-item>
      <el-form-item label="登录账号" label-width="120px" prop="loginName">
        <el-input
          placeholder="请输入登录账号"
          v-model.trim="editForm.loginName"
        ></el-input>
      </el-form-item>
      <el-form-item label="姓名" label-width="120px" prop="name">
        <el-input
          placeholder="请输入姓名"
          v-model.trim="editForm.name"
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="form-footer">
      <el-button @click="add" type="primary" v-if="!id">添加</el-button>
      <el-button @click="edit" type="primary" v-if="id">修改</el-button>
    </div>
  </div>
</template>

<script>
import { userAdd, userEdit, userGet } from 'api/user'
import { orgListPage } from 'api/base'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      id: null,
      editForm: {
        // 修改表单
        name: null, // 名称
        loginName: null, // 登录账号
        orgId: null, // 机构ID
        orgName: null, // 机构名称
        show: false, // 是否显示页面
        orgListpage: {
          total: 0,
          curPage: 1,
          pageSize: 5,
          list: [],
        },
        rules: {
          // 校验
          loginName: [
            { required: true, message: '请输入登录账号', trigger: 'blur' },
          ],
          name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        },
      },
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await userGet({ id: this.id })
      if (res.code != 200) {
        this.$message.error(res.msg)
        return
      }

      await this.getOrgList()
      this.$nextTick(() => {
        this.editForm.name = res.data.name
        this.editForm.loginName = res.data.loginName
        this.editForm.orgId = res.data.orgId
        this.editForm.orgName = res.data.orgName
        this.editForm.roles = res.data.roles[0]
        this.$refs['orgSelect'].$refs['elSelect'].cachedOptions.push({
          currentLabel: res.data.orgName,
          currentValue: res.data.orgId,
          label: res.data.orgName,
          value: res.data.orgId,
        })
      })
    }
  },
  methods: {
    // 添加机构
    add() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await userAdd({
          name: this.editForm.name,
          loginName: this.editForm.loginName,
          orgId: this.editForm.orgId,
          phone: this.editForm.phone,
        })

        if (res.code != 200) {
          this.$message.error(res.msg)
          return
        }
        this.$router.back()
        this.$alert(res.data.initPwd, '重置密码', {
          confirmButtonText: '确定',
        })
      })
    },
    // 修改
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await userEdit({
          id: this.id,
          name: this.editForm.name,
          loginName: this.editForm.loginName,
          orgId: this.editForm.orgId,
        })

        if (res.code != 200) {
          this.$message.error(res.msg)
          return
        }

        if (res.data.initPwd) {
          this.$alert(res.data.initPwd, '重置密码', {
            confirmButtonText: '确定',
          })
        }

        this.$store.dispatch('setting/changeSetting', {
          key: 'tabIndex',
          value: '1',
        })
      })
    },
    // 获取机构列表
    async getOrgList(curPage = 1, name = '') {
      const orgList = await orgListPage({
        name,
        curPage,
        pageSize: this.editForm.orgListpage.pageSize,
      })
      this.editForm.orgListpage.list = orgList.data.list
      this.editForm.orgListpage.total = orgList.data.total
    },
    // 获取更多用户
    getMoreOrg(curPage, name) {
      this.getOrgList(curPage, name)
    },
    // 根据name查询机构
    searchOrg(name) {
      this.getOrgList(1, name)
    },
    // 选择考试用户
    selectOrg(e) {
      this.editForm.orgId = e
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  padding-left: 120px;
}
</style>
