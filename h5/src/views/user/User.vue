<template>
  <div class="container">
    <div class="content">
      <el-form :inline="true" :model="queryForm" ref="queryForm">
        <el-row>
          <el-col :span="17">
            <el-form-item label prop="name">
              <el-input
                @focus="queryForm.queryShow = true"
                placeholder="请输入名称"
                v-model="queryForm.name"
              ></el-input>
            </el-form-item>
            <el-button
              @click="query"
              class="query-search"
              icon="el-icon-search"
              type="primary"
              >查询</el-button
            >
          </el-col>
          <el-col :span="7">
            <el-form-item style="float: right">
              <el-button
                @click=";(editForm.show = true), (editForm.id = null)"
                icon="el-icon-circle-plus-outline"
                size="mini"
                type="primary"
                >添加用户</el-button
              >
              <el-button
                @click="toOrg"
                icon="el-icon-circle-plus-outline"
                size="mini"
                type="primary"
                >添加机构</el-button
              >
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="queryForm.queryShow">
          <el-form-item label prop="orgName">
            <el-input
              placeholder="请输入机构名称"
              v-model="queryForm.orgName"
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
      <div class="table">
        <el-table :data="listpage.list" style="width: 100%">
          <el-table-column label="名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="登录名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.loginName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="角色">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.roleNames }}</span>
            </template>
          </el-table-column>
          <el-table-column label="机构名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.orgName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300">
            <template slot-scope="scope">
              <el-link
                :underline="false"
                @click="get(scope.row.id)"
                icon="common common-edit"
                >修改</el-link
              >
              <el-link
                :underline="false"
                @click="initPwd(scope.row.id)"
                icon="common common-edit"
                >密码重置</el-link
              >
              <el-link
                :underline="false"
                @click="del(scope.row.id)"
                icon="common common-delete"
                >删除</el-link
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        :current-page="listpage.curPage"
        :page-size="listpage.pageSize"
        :total="listpage.total"
        @current-change="pageChange"
        background
        hide-on-single-page
        layout="prev, pager, next"
        next-text="下一页"
        prev-text="上一页"
      ></el-pagination>
    </div>
    <el-dialog
      :visible.sync="editForm.show"
      :show-close="false"
      width="40%"
      title="用户"
      :close-on-click-modal="false"
      @close="resetData('editForm')"
    >
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="机构名称" label-width="120px" prop="orgId">
          <CustomSelect
            :multiple="false"
            ref="addUserSelect"
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
        <el-form-item label="登录名称" label-width="120px" prop="loginName">
          <el-input
            placeholder="请输入登录名称"
            v-model.trim="editForm.loginName"
          ></el-input>
        </el-form-item>
        <el-form-item label="名称" label-width="120px" prop="name">
          <el-input
            placeholder="请输入名称"
            v-model.trim="editForm.name"
          ></el-input>
        </el-form-item>
        <el-form-item label="子管理员" label-width="120px" prop="roles">
          <el-switch
            active-color="#13ce66"
            active-value="subAdmin"
            inactive-color="#ff4949"
            inactive-value="user"
            v-model="editForm.roles"
          ></el-switch>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="add" type="primary" v-if="editForm.id == null"
          >添加</el-button
        >
        <el-button @click="edit" type="primary" v-if="editForm.id != null"
          >修改</el-button
        >
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  userListPage,
  userAdd,
  userEdit,
  userInitPwd,
  userDel,
  userGet,
} from 'api/user'
import { orgListPage } from 'api/base'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        list: [], // 列表数据
      },
      queryForm: {
        // 查询表单
        name: null,
        orgName: null,
        queryShow: false,
      },
      editForm: {
        // 修改表单
        id: null, // 主键
        name: null, // 名称
        loginName: null, // 登录名称
        orgId: null, // 机构ID
        orgName: null, // 机构名称
        roles: 'user', // 角色
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
            { required: true, message: '请输入登录名称', trigger: 'blur' },
          ],
          name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        },
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 查询
    async query(curPage = 1) {
      this.queryForm.queryShow = false

      const {
        data: { list, total },
      } = await userListPage({
        orgName: this.queryForm.orgName,
        name: this.queryForm.name,
        curPage: curPage,
        pageSize: this.listpage.pageSize,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 重置
    async reset() {
      this.$refs['queryForm'].resetFields()
      this.listpage.curPage = 1
      this.query()
    },
    // 初始化
    async init() {
      await this.query()
    },
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
          roles: this.editForm.roles,
        })

        if (res.code != 200) {
          alert(res.msg)
          return
        }

        this.$alert(res.data.initPwd, '初始化密码', {
          confirmButtonText: '确定',
        })

        this.editForm.show = false
        this.query()
      })
    },
    // 修改
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await userEdit({
          id: this.editForm.id,
          name: this.editForm.name,
          loginName: this.editForm.loginName,
          orgId: this.editForm.orgId,
          roles: this.editForm.roles,
        })

        if (res.code != 200) {
          alert(res.msg)
          return
        }

        if (res.data.initPwd) {
          this.$alert(res.data.initPwd, '初始化密码', {
            confirmButtonText: '确定',
          })
        }

        this.editForm.show = false
        this.query()
      })
    },
    // 初始化密码
    async initPwd(id) {
      const res = await userInitPwd({
        id: id,
      })

      if (res.code != 200) {
        alert(res.msg)
        return
      }

      if (res.data.initPwd) {
        this.$alert(res.data.initPwd, '初始化密码', {
          confirmButtonText: '确定',
        })
      }
    },
    // 删除
    async del(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await userDel({ id })
        if (res.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
    },
    // 获取用户
    async get(id) {
      const res = await userGet({ id: id })
      if (res.code != 200) {
        alert(res.msg)
        return
      }

      await this.getOrgList()
      this.editForm.show = true
      this.$nextTick(() => {
        this.editForm.id = res.data.id
        this.editForm.name = res.data.name
        this.editForm.loginName = res.data.loginName
        this.editForm.orgId = res.data.orgId
        this.editForm.orgName = res.data.orgName
        this.editForm.roles = res.data.roles
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
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
    // 清空还原数据
    resetData(name) {
      this.$refs[name].resetFields()
    },
    toOrg() {
      this.$router.push({ path: '/user/org' })
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: center;
  .content {
    width: 1170px;
  }
}
.query-search {
  width: 150px;
  height: 40px;
  border-radius: 5px;
}
.el-input {
  width: 300px;
  float: left;
}
.el-input-number {
  float: left;
}

.el-dialog__title {
  float: left;
}
/deep/ .el-table th {
  background-color: #d3dce6;
  color: #475669;
  text-align: center;
  height: 55px;
}
/deep/ .el-table td {
  color: #8392a6;
  font-size: 12px;
  text-align: center;
  border-bottom: 1px solid #ddd;
}
/deep/ .common {
  padding-right: 10px;
  color: #0096e7;
  font-style: inherit;
  font-weight: bold;
}
.el-link {
  padding-right: 20px;
  color: #8392a6;
  font-size: 12px;
}
/deep/ .el-input__inner:focus {
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 8px rgba(102, 175, 233, 0.6);
  border: 1px solid #f2f4f5;
}
.common-arrow-down {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

/deep/.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #0095e5;
  color: #fff;
}

/deep/.el-pagination.is-background .btn-next,
/deep/.el-pagination.is-background .btn-prev,
/deep/.el-pagination.is-background .el-pager li {
  margin: 0 3px;
  min-width: 35px;
  border: 1px solid #d4dfd9;
  background-color: #fff;
  padding: 0 10px;
}
</style>
