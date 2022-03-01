<template>
  <div class="container">
    <template v-if="hashChildren">
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
                  @click="add"
                  icon="el-icon-circle-plus-outline"
                  size="mini"
                  type="primary"
                  >添加</el-button
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
                  active-color="#13ce66"
                  :active-value="2"
                  inactive-color="#ff4949"
                  :inactive-value="1"
                  v-model="scope.row.type"
                  @change="setSubAdmin($event, scope.row.id)"
                ></el-switch>
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
                  <i class="common common-edit" @click="edit(scope.row.id)"></i>
                </el-tooltip>
                <el-tooltip placement="top" content="删除">
                  <i
                    class="common common-delete"
                    @click="del(scope.row.id)"
                  ></i>
                </el-tooltip>
                <el-tooltip placement="top" content="重置密码">
                  <i
                    class="common common-pwd-reset"
                    @click="initPwd(scope.row.id)"
                  ></i>
                </el-tooltip>
                <el-tooltip
                  placement="top"
                  content="强制下线"
                  v-if="scope.row.online"
                >
                  <i
                    class="common common-off-line"
                    @click="offLine(scope.row.id)"
                  ></i>
                </el-tooltip>
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
    </template>
    <router-view v-else></router-view>
  </div>
</template>

<script>
import { userOut, userRole, userListPage } from 'api/user'
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
    }
  },
  computed: {
    hashChildren() {
      return this.$route.matched.length > 2 ? false : true
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
  },
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: center;
  .content {
    width: 1200px;
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
