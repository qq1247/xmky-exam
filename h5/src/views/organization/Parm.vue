<template>
  <div class="container">
    <div class="content">
      <div class="search">
        <el-form
          :inline="true"
          :model="queryForm"
          class="form-inline"
          ref="queryForm"
        >
          <el-form-item label prop="orgName">
            <el-input
              placeholder="请输入单位名称"
              v-model="queryForm.orgName"
            ></el-input>
          </el-form-item>
          <el-form-item style="width: 200px">
            <el-button @click="query" icon="el-icon-search" type="primary"
              >查询</el-button
            >
            <el-button @click="reset">重置</el-button>
          </el-form-item>
          <el-form-item>
            <el-button
              @click="editForm.show = true"
              icon="el-icon-search"
              type="primary"
              >添加</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <div class="table">
        <el-table :data="listpage.list" style="width: 100%">
          <el-table-column label="邮件主机">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.emailHost }}</span>
            </template>
          </el-table-column>
          <el-table-column label="邮件用户名">
            <template slot-scope="scope">
              <span style="margin-left: 10px">
                {{ scope.row.emailUserName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="邮件密码">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.emailPwd }}</span>
            </template>
          </el-table-column>
          <el-table-column label="邮件协议">
            <template slot-scope="scope">
              <span style="margin-left: 10px">
                {{ scope.row.emailProtocol }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="邮件编码">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.emailEncode }}</span>
            </template>
          </el-table-column>
          <el-table-column label="单位商标">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.orgLogo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="单位名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.orgName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="get(scope.row.id)" size="mini">修改</el-button>
              <el-button @click="del(scope.row.id)" size="mini" type="danger"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        :current-page="listpage.curPage"
        :page-size="listpage.pageSize"
        :page-sizes="listpage.pageSizes"
        :total="listpage.total"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
        layout="total, sizes, prev, pager, next, jumper"
      ></el-pagination>
    </div>
    <el-dialog :visible.sync="editForm.show" title="参数">
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="邮件主机" label-width="120px" prop="emailHost">
          <el-input
            placeholder="请输入邮件主机"
            v-model="editForm.emailHost"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="邮件用户名"
          label-width="120px"
          prop="emailUserName"
        >
          <el-input
            placeholder="请输入邮件用户名"
            v-model="editForm.emailUserName"
          ></el-input>
        </el-form-item>
        <el-form-item label="邮件密码" label-width="120px" prop="emailPwd">
          <el-input
            placeholder="请输入邮件密码"
            v-model="editForm.emailPwd"
          ></el-input>
        </el-form-item>
        <el-form-item label="邮件协议" label-width="120px" prop="emailProtocol">
          <el-input
            placeholder="请输入邮件协议"
            v-model="editForm.emailProtocol"
          ></el-input>
        </el-form-item>
        <el-form-item label="邮件编码" label-width="120px" prop="emailEncode">
          <el-input
            placeholder="请输入邮件编码"
            v-model="editForm.emailEncode"
          ></el-input>
        </el-form-item>
        <el-form-item label="单位商标" label-width="120px" prop="orgLogo">
          <el-input
            placeholder="请输入单位商标"
            v-model="editForm.orgLogo"
          ></el-input>
        </el-form-item>
        <el-form-item label="单位名称" label-width="120px" prop="orgName">
          <el-input
            placeholder="请输入单位名称"
            v-model="editForm.orgName"
          ></el-input>
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
import { parmListPage, parmAdd, parmEdit, parmGet, parmDel } from '@/api/base'
export default {
  data() {
    return {
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        pageSizes: [10, 20, 50, 100], // 每页多少条
        list: [], // 列表数据
      },
      queryForm: {
        // 查询表单
        emailHost: null,
        emailEncode: null,
        emailUserName: null,
        emailPwd: null,
        emailProtocol: null,
        orgLogo: null,
        orgName: null,
      },
      editForm: {
        // 修改表单
        id: null, // 主键
        emailHost: null,
        emailEncode: null,
        emailUserName: null,
        emailPwd: null,
        emailProtocol: null,
        orgLogo: null,
        orgName: null,
        show: false, // 是否显示页面
        rules: {
          // 校验
          orgName: [
            { required: true, message: '请输入排序', trigger: 'change' },
          ],
        },
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 查询
    async query() {
      const {
        data: { list, total },
      } = await parmListPage({
        orgName: this.queryForm.orgName,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 重置
    async reset() {
      this.listpage.curPage = 1
      this.$refs['queryForm'].resetFields()
      this.query()
    },
    handleSizeChange(val) {
      this.listpage.pageSize = val
      this.query()
    },
    handleCurrentChange(val) {
      this.listpage.curPage = val
      this.query()
    },
    // 初始化
    async init() {
      this.query()
    },
    add() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await parmAdd({
          emailHost: this.editForm.emailHost,
          emailEncode: this.editForm.emailEncode,
          emailUserName: this.editForm.emailUserName,
          emailPwd: this.editForm.emailPwd,
          emailProtocol: this.editForm.emailProtocol,
          orgLogo: this.editForm.orgLogo,
          orgName: this.editForm.orgName,
        })
        if (code != 200) {
          alert(msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await parmEdit({
          id: this.editForm.id,
          emailHost: this.editForm.emailHost,
          emailEncode: this.editForm.emailEncode,
          emailUserName: this.editForm.emailUserName,
          emailPwd: this.editForm.emailPwd,
          emailProtocol: this.editForm.emailProtocol,
          orgLogo: this.editForm.orgLogo,
          orgName: this.editForm.orgName,
        })
        if (code != 200) {
          alert(msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    // 获取参数
    async get(id) {
      const res = await parmGet({ id: id })
      if (res?.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.show = true
      this.editForm.id = res.data.id
      this.editForm.emailHost = res.data.emailHost
      this.editForm.emailEncode = res.data.emailEncode
      this.editForm.emailUserName = res.data.emailUserName
      this.editForm.emailPwd = res.data.emailPwd
      this.editForm.emailProtocol = res.data.emailProtocol
      this.editForm.orgLogo = res.data.orgLogo
      this.editForm.orgName = res.data.orgName
    },
    // 删除
    async del(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await parmDel({ id })
        if (res?.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
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
    .search {
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
    }
  }
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
</style>
