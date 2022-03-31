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
    <div class="home-params">
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
              <span style="margin-left: 10px">{{
                scope.row.lastLoginTime
              }}</span>
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
    <div class="home-info">
      <!-- 快捷导航 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>快捷导航</span>
        </div>
        <el-link
          class="quick-nav"
          v-for="nav in navList"
          :key="nav.path"
          @click="$router.push(nav.path)"
          >{{ nav.meta.title }}</el-link
        >
      </el-card>
      <!-- 系统参数 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>服务器参数</span>
        </div>
        <div
          class="params-item"
          v-for="param in serverParams"
          :key="param.name"
        >
          <span class="item-name">{{ param.name }}:</span>
          <div class="item-value" v-if="Array.isArray(param.value)">
            <span v-for="item in param.value" :key="item">
              {{ item }} <br />
            </span>
          </div>
          <span class="item-value" v-else>{{ param.value }}</span>
        </div>
      </el-card>
      <!-- 服务支持 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>服务支持</span>
        </div>
        <p style="line-height: 30px">技术支持：在线考试</p>
        <p style="line-height: 30px">
          在线服务：<a
            target="_blank"
            href="https://jq.qq.com/?_wv=1027&k=GXh1hHSy"
          >
            <img
              src="https://img.shields.io/badge/qq-811189776-blue"
              alt="811189776"
            />
          </a>
        </p>
      </el-card>
    </div>
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
        navList: [],
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
}

.home-params {
  flex: 1;
  background: #fff;
  margin: 0 10px;
}
.home-info {
  width: 30%;
}

.box-card {
  width: 100%;
  margin-bottom: 20px;
  /deep/ .el-card__header {
    position: relative;
    padding: 10px 10px 10px 20px;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 14px;
      left: 10px;
      width: 3px;
      height: 14px;
      background: #0094e5;
    }
  }
  /deep/.el-card__body {
    padding: 10px 10px 0;
  }
}

.params-item {
  line-height: 30px;
  display: flex;
  .item-name {
    display: inline-block;
    width: 110px;
    text-align: right;
    margin-right: 10px;
  }
  .item-value {
    flex: 1;
    word-break: break-word;
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

.quick-nav {
  margin: 10px;
}
</style>
