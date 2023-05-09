<template>
  <div class="home-content">
    <div class="home-left">
      <el-card class="box-card" shadow="never">
        <div slot="header">考试概览</div>
        <div
          v-if="
            userInfo.bulletin &&
              userInfo.onlineUser &&
              userInfo.subAdmin &&
              userInfo.user
          "
          class="data-content"
        >
          <div class="data-item" @click="$router.push({ name: 'User' })">
            <div class="item-icon exam-bg">
              <img src="../../assets/img/index/index-exam.png" alt="">
            </div>
            <div class="item-info">
              <span class="info-num">{{ userInfo.user.num }}</span>
              <span class="info-intro">创建用户（名）</span>
            </div>
          </div>
          <div class="data-item" @click="$router.push({ name: 'User' })">
            <div class="item-icon paper-bg">
              <img src="../../assets/img/index/index-paper.png" alt="">
            </div>
            <div class="item-info">
              <span class="info-num">{{ userInfo.onlineUser.num }}</span>
              <span class="info-intro">在线用户（名）</span>
            </div>
          </div>
          <div class="data-item" @click="$router.push({ name: 'Bulletin' })">
            <div class="item-icon question-bg">
              <img src="../../assets/img/index/index-question.png" alt="">
            </div>
            <div class="item-info">
              <span class="info-num">{{ userInfo.bulletin.num }}</span>
              <span class="info-intro">创建公告（条）</span>
            </div>
          </div>
          <div class="data-item" @click="$router.push({ name: 'User' })">
            <div class="item-icon mark-bg">
              <img src="../../assets/img/index/index-mark.png" alt="">
            </div>
            <div class="item-info">
              <span class="info-num">{{ userInfo.subAdmin.num }}</span>
              <span class="info-intro">创建子管理（名）</span>
            </div>
          </div>
        </div>
      </el-card>
      <el-card class="box-card" shadow="never">
        <div slot="header">慢接口日志</div>
        <template v-if="serverLog.length">
          <div
            v-for="(log, index) in serverLog"
            :key="index"
            class="server-log"
          >
            {{ log }}
          </div>
        </template>
        <el-empty
          v-else
          :image="require('assets/img/index/notice-null.png')"
          description="暂无异常"
        />
      </el-card>
      <el-card
        class="box-card box-flex"
        style="margin-bottom: 0"
        shadow="never"
      >
        <div slot="header">用户信息</div>
        <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
          <div class="table" max-height="100">
            <el-table :data="listpage.list" stripe>
              <el-table-column label="姓名" fixed>
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
                  >强制下线</span>
                  <span v-else>离线</span>
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
        </el-scrollbar>
      </el-card>
    </div>
    <div class="home-right">
      <!-- 功能列表 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>功能列表</span>
        </div>
        <div style="margin-bottom: 24px">
          <el-link
            v-for="nav in navList"
            :key="nav.path"
            class="quick-nav"
            :underline="false"
            @click="$router.push(nav.path)"
          >
            <div class="nav-item">
              <div class="nav-img" :style="{ background: nav.background }">
                <img :src="nav.icon" alt="">
              </div>
              <span class="nav-title">{{ nav.title }}</span>
            </div>
          </el-link>
        </div>
      </el-card>
      <!-- 系统参数 -->
      <el-card class="box-card box-flex" shadow="never">
        <div slot="header">
          <span>服务器参数</span>
        </div>
        <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
          <div
            v-for="param in serverParams"
            :key="param.name"
            class="params-item"
          >
            <span class="item-name">{{ param.name }}:</span>
            <div v-if="Array.isArray(param.value)" class="item-value">
              <span v-for="(item, index) in param.value" :key="index">
                {{ item }} <br>
              </span>
            </div>
            <span v-else class="item-value">{{ param.value }}</span>
          </div>
        </el-scrollbar>
      </el-card>
      <!-- 服务支持 -->
      <el-card class="box-card service-box" shadow="never">
        <div slot="header">
          <span>服务支持</span>
        </div>
        <div class="service">
          <p class="service-item"><span>技术支持：</span>在线考试</p>
          <p class="service-item">
            <span>在线服务：</span><a
              class="service-qq"
              target="_blank"
              href="https://jq.qq.com/?_wv=1027&k=GXh1hHSy"
            >
              <div class="qq-title">QQ</div>
              <div class="qq-number">811189776</div>
            </a>
          </p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { userOut, userListPage } from 'api/user'
import { getServerParam, getServerLog, getAdminInfo } from 'api/report'
export default {
  data() {
    return {
      navList: [
        {
          path: '/user',
          background: '#0094E5',
          icon: require('../../assets/img/index/paper-manage.png'),
          title: '用户管理'
        },
        {
          path: '/org',
          background: '#FB901B',
          icon: require('../../assets/img/index/paper-manage.png'),
          title: '组织机构'
        },
        {
          path: '/bulletin',
          background: '#09C8BD',
          icon: require('../../assets/img/index/exam-manage.png'),
          title: '公告管理'
        },
        {
          path: '/dict',
          background: '#EB5B5B',
          icon: require('../../assets/img/index/mark-manage.png'),
          title: '数据字典'
        },
        {
          path: '/cron',
          background: '#6B77F9',
          icon: require('../../assets/img/index/mark-manage.png'),
          title: '定时任务'
        },
        {
          path: '/param',
          background: '#FB901B',
          icon: require('../../assets/img/index/question-manage.png'),
          title: '系统参数'
        },
        {
          path: '/sensitive',
          background: '#09C8BD',
          icon: require('../../assets/img/index/exam-manage.png'),
          title: '敏感词'
        }
      ],
      serverParams: [],
      serverLog: [],
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        list: [], // 列表数据
        navList: []
      },
      userInfo: {}
    }
  },
  computed: {
    ...mapGetters(['permission_routes', 'onlyRole'])
  },
  mounted() {
    this.getAdminInfo()
    this.query()
    this.getServerLog()
    setTimeout(() => {// 延时请求，规避cpu占用率是其他接口造成的问题
      this.getServerParam()
    }, 1000);
  },
  methods: {
    async getAdminInfo() {
      const userInfo = await getAdminInfo()
      this.userInfo = userInfo.data
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
        data: { list, total }
      } = await userListPage({
        orgName: '',
        name: '',
        curPage: curPage,
        pageSize: this.listpage.pageSize
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
    }
  }
}
</script>

<style lang="scss" scoped>
.home-content {
  height: 100%;
  display: flex;
  flex-direction: row;
}

.home-left {
  height: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.home-right {
  width: 289px;
  height: 100%;
  display: flex;
  flex-direction: column;
  margin-left: 15px;
}

.box-flex {
  flex: 1;
  margin-bottom: 0;
  /deep/ .el-card__body {
    height: calc(100% - 40px);
  }
}

.data-content {
  display: flex;
  padding: 24px;
}

.data-item {
  width: 25%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  .item-icon {
    width: 48px;
    height: 48px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 16px;
    transition: all 0.15s ease;
    img {
      width: 24px;
      height: 24px;
      transition: all 0.3s ease-in-out;
    }
  }
  .item-info {
    display: flex;
    flex-direction: column;
    .info-num {
      font-size: 28px;
      font-weight: 600;
      color: #0c2e41;
    }
    .info-intro {
      font-size: 12px;
      color: #537384;
    }
  }
  &:hover {
    .item-icon {
      border-radius: 8px;
      box-shadow: 0 0 8px -3px rgba(#000, 0.13);
      img {
        transform: scale(1.15);
      }
    }
  }
}

.exam-bg {
  background: rgba(#09c8bd, 0.1);
}

.paper-bg {
  background: rgba(#fb901b, 0.1);
}

.question-bg {
  background: rgba(#0094e5, 0.1);
}

.mark-bg {
  background: rgba(#eb5b5b, 0.1);
}

.box-card {
  margin-bottom: 16px;
  border: none;
  border-radius: 8px;
  /deep/ .el-card__header {
    position: relative;
    padding: 0 0 0 32px;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 12px;
      left: 16px;
      width: 4px;
      height: 16px;
      background: #0094e5;
    }
  }
  /deep/.el-card__body {
    padding: 0;
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
  width: 33.33%;
  .nav-item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-top: 24px;
  }
  .nav-img {
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 8px;
  }
  .nav-title {
    font-size: 12px;
    color: #537384;
    margin-top: 8px;
  }
}

.service-box {
  margin-bottom: 0;
}

.service {
  padding: 16px;
}

.service-item {
  display: flex;
  align-items: center;
  line-height: 30px;
  span {
    color: #537384;
  }
}
.service-qq {
  display: flex;
  .qq-title {
    width: 31px;
    height: 20px;
    line-height: 20px;
    color: #fff;
    text-align: center;
    font-size: 12px;
    background: #0874ae;
    border-radius: 4px 0px 0px 4px;
  }
  .qq-number {
    width: 73px;
    height: 20px;
    line-height: 20px;
    color: #fff;
    text-align: center;
    font-size: 12px;
    background: #0094e5;
    border-radius: 0px 4px 4px 0px;
  }
}
</style>
