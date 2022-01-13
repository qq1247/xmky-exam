<!--
 * @Description: 管理员
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-13 13:58:51
 * @LastEditors: Che
 * @LastEditTime: 2022-01-12 18:13:47
-->
<template>
  <div class="home-content">
    <!-- <div class="content-left">
      <router-link
        class="nav-router"
        v-for="nav in navList"
        :key="nav.path"
        :to="nav.path"
      >
        <i :class="`common ${nav.meta.icon}`"></i>
        <span>{{ nav.meta.title }}</span>
      </router-link>
    </div> -->
    <el-card class="box-card" shadow="never">
      <div slot="header">
        <span>服务器参数</span>
      </div>
      <div class="params-item" v-for="item in serverParams" :key="item.name">
        <span>{{ item.name }}:</span>{{ item.value }}
      </div>
    </el-card>

    <el-card class="box-card" shadow="never">
      <div slot="header">
        <span>慢接口日志</span>
      </div>
      <div class="server-log" v-for="(log, index) in serverLog" :key="index">
        {{ log }}
      </div>
    </el-card>

    <div class="table">
      <el-table :data="listpage.list" stripe style="width: 100%">
        <el-table-column label="姓名">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后在线时间">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.lastLoginTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template slot-scope="scope">
            <span
              v-if="scope.row.online"
              style="cursor: pointer"
              @click="offLine(scope.row.id)"
              >强制下线</span
            >
            <span v-else>离线</span>
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

<script>
import { mapGetters } from 'vuex'
import { userOut, userListPage } from 'api/user'
import { getServerParam, getServerLog } from 'api/report'
export default {
  data() {
    return {
      navList: [],
      serverParams: [],
      serverLog: [],
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        list: [], // 列表数据
      },
    }
  },
  computed: {
    ...mapGetters(['permission_routes', 'onlyRole']),
  },
  mounted() {
    this.setNavBar()
    this.getServerParam()
    this.query()
    this.getServerLog()
  },
  methods: {
    setNavBar() {
      this.navList = this.permission_routes.filter(
        (item) => item?.meta?.layout === this.onlyRole[0]
      )
    },
    async getServerLog() {
      const serverLog = await getServerLog()
      this.serverLog = serverLog.data
    },
    async getServerParam() {
      const serverParams = await getServerParam()
      this.serverParams = serverParams.data
    },
    // 查询用户信息
    async query(curPage = 1) {
      const {
        data: { list, total },
      } = await userListPage({
        orgName: '',
        name: '',
        curPage: curPage,
        pageSize: this.listpage.pageSize,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 强制下线
    offLine(id) {
      userOut({ id }).then((res) => {
        this.query()
        this.$message('下线成功！')
      })
    },
    // 分页查询用户信息
    pageChange(val) {
      this.query(val)
    },
  },
}
</script>

<style lang="scss" scoped>
.home-content {
  margin: 20px 0;
  display: flex;
  flex-direction: column;
}
.content-left {
  width: 150px;
  display: flex;
  flex-direction: column;
}

.content-right {
  flex: 1;
  margin-left: 50px;
}

// 导航
.nav-router {
  line-height: 40px;
  border-bottom: 1px solid #cfcfcf;
  background: rgba(#0095e5, 0.8);
  border-radius: 20px;
  margin-bottom: 10px;
  color: #fff;
  text-align: center;
  &:hover {
    background: rgba(#0095e5, 1);
  }
  .common {
    margin-right: 10px;
  }
}

.content-right {
  flex: 1;
  margin-left: 50px;
}

.box-card {
  margin-bottom: 30px;
  /deep/ .el-card__header {
    position: relative;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 18px;
      left: 10px;
      width: 4px;
      height: 20px;
      background: #0095e5;
    }
  }
  /deep/.el-card__body {
    padding: 13px;
    min-height: 200px;
  }
}

.params-item {
  line-height: 30px;
  span {
    display: inline-block;
    width: 150px;
    text-align: right;
    margin-right: 10px;
  }
}

.server-log {
  padding: 10px 0;
  line-height: 24px;
  border-bottom: 1px solid #cfcfcf;
  &:last-child {
    border-bottom: none;
  }
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

/deep/ .el-pagination {
  margin-top: 20px;
}
</style>
