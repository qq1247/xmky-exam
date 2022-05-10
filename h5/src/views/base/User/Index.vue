<template>
  <div class="container">
    <template v-if="hashChildren">
      <el-form ref="queryForm" :inline="true" :model="queryForm">
        <el-row>
          <el-col :span="16">
            <el-form-item label prop="name">
              <el-input
                v-model="queryForm.name"
                placeholder="请输入名称"
                @focus="queryForm.queryShow = true"
              />
            </el-form-item>
            <el-button
              class="query-search"
              icon="el-icon-search"
              type="primary"
              @click="query"
              >查询</el-button
            >
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button
                icon="el-icon-tickets"
                size="mini"
                type="primary"
                @click="userTemplate()"
                >下载模板</el-button
              >
            </el-form-item>
            <el-form-item>
              <el-upload
                :limit="1"
                name="files"
                list-type="text"
                :headers="headers"
                action="/api/file/upload"
                :file-list="queryForm.fileList"
                :on-success="uploadSuccess"
              >
                <el-button
                  icon="el-icon-upload2"
                  size="mini"
                  type="primary"
                  @click="() => userImport"
                  >导入</el-button
                >
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item style="float: right">
              <el-button
                icon="el-icon-circle-plus-outline"
                size="mini"
                type="primary"
                @click="add"
                >添加</el-button
              >
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="queryForm.queryShow">
          <el-form-item label prop="orgName">
            <el-input
              v-model="queryForm.orgName"
              placeholder="请输入机构名称"
            />
          </el-form-item>
        </div>
      </el-form>
      <div class="table">
        <el-table :data="listpage.list" style="width: 100%">
          <el-table-column label="姓名">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="登录账号">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.loginName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="所属机构">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.orgName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="子管理员权限">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.type"
                active-color="#13ce66"
                :active-value="2"
                inactive-color="#ff4949"
                :inactive-value="1"
                @change="setSubAdmin($event, scope.row.id)"
              />
            </template>
          </el-table-column>
          <el-table-column label="权限描述" width="240px">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{
                scope.row.roles.includes('subAdmin')
                  ? '答题 | 出题 | 组织考试 | 阅卷 | 统计'
                  : '答题'
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300">
            <template slot-scope="scope">
              <el-tooltip placement="top" content="修改">
                <i class="common common-edit" @click="edit(scope.row.id)" />
              </el-tooltip>
              <el-tooltip placement="top" content="删除">
                <i class="common common-delete" @click="del(scope.row.id)" />
              </el-tooltip>
              <el-tooltip placement="top" content="重置密码">
                <i
                  class="common common-pwd-reset"
                  @click="initPwd(scope.row.id)"
                />
              </el-tooltip>
              <el-tooltip
                v-if="scope.row.online"
                placement="top"
                content="强制下线"
              >
                <i
                  class="common common-off-line"
                  @click="offLine(scope.row.id)"
                />
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        :current-page="listpage.curPage"
        :page-size="listpage.pageSize"
        :total="listpage.total"
        background
        hide-on-single-page
        layout="prev, pager, next"
        next-text="下一页"
        prev-text="上一页"
        @current-change="pageChange"
      />
    </template>
    <router-view v-else />
  </div>
</template>

<script>
import {
  userOut,
  userRole,
  userImport,
  userListPage,
  userTemplate,
} from 'api/user'

export default {
  data() {
    return {
      headers: {
        Authorization: this.$store.getters.token,
      },
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
        fileList: [],
      },
    }
  },
  computed: {
    hashChildren() {
      return !(this.$route.matched.length > 2)
    },
  },
  created() {
    this.query()
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
    // 设置子管理员
    async setSubAdmin(e, id) {
      const roles = [e === 2 ? 'subAdmin' : 'user']
      await userRole({ id, roles })
    },
    // 重置密码
    async initPwd(id) {
      this.$tools.switchTab('UserIndexSetting', {
        id,
        tab: '2',
      })
    },
    // 添加
    add() {
      this.$tools.switchTab('UserIndexSetting', {
        id: 0,
        tab: '1',
      })
    },
    // 修改
    edit(id) {
      this.$tools.switchTab('UserIndexSetting', {
        id,
        tab: '1',
      })
    },
    // 删除
    del(id) {
      this.$tools.switchTab('UserIndexSetting', {
        id,
        tab: '3',
      })
    },
    // 强制下线
    offLine(id) {
      userOut({ id }).then((res) => {
        this.query()
      })
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
    // 用户模板
    async userTemplate() {
      const template = await userTemplate({}, 'blob')
      const blob = new Blob([template], { type: 'application/msword' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = '用户模板.xlsx'
      a.click()
      window.URL.revokeObjectURL(url)
    },
    // 上传成功
    uploadSuccess(response, file, fileList) {
      this.userImport(response.data.fileIds)
    },
    // 用户导入
    async userImport(fileId) {
      const res = await userImport({
        fileId,
      })
      if (res.code === 200) {
        this.$message.success('导入用户成功！')
        this.query()
      }
    },
  },
}
</script>
<style lang="scss" scoped>
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

/deep/ .el-upload-list {
  display: none;
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
.common {
  padding: 0 10px;
  color: #0096e7;
  font-size: 18px;
}
/deep/ .el-input__inner:focus {
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 8px rgba(102, 175, 233, 0.6);
  border: 1px solid #f2f4f5;
}
</style>
