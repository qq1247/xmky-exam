<template>
  <div class="header-nav">
    <router-link to="/" class="nav-logo">
      <el-image :src="orgLogo" lazy>
        <i slot="placeholder" class="common common-more-row" />
        <i slot="error" class="common common-perfect" />
      </el-image>
      <div class="org-name">{{ $store.getters.orgName }}</div>
    </router-link>
    <div class="nav">
      <router-link
        :class="['nav-item', $route.path === '/home' ? 'nav-item-active' : '']"
        to="/"
      >
        首页
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      orgLogo: '/api/login/logo',
      navInfo: {
        user: [
          {
            name: '首页',
            path: 'Home'
          },
          {
            name: '考试',
            path: 'MyExam'
          }
        ],
        subAdmin: [
          {
            name: '首页',
            path: 'Home'
          },
          {
            name: '考试',
            path: 'Question'
          }
        ],
        admin: [
          {
            name: '首页',
            path: 'Home'
          },
          {
            name: '基础管理',
            path: 'User'
          }
        ]
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.header-nav {
  display: flex;
  align-items: center;
}
.nav-logo {
  height: 64px;
  display: flex;
  align-items: center;
  /deep/ .el-image {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    .el-image__inner {
      width: 32px;
      height: 32px;
    }
  }
  .common-perfect {
    font-size: 40px;
    color: #0095e5;
  }
  .org-name {
    font-size: 18px;
    font-weight: 600;
    margin-left: 16px;
  }
}
.nav {
  display: flex;
  margin-left: 80px;
  .nav-item {
    height: 64px;
    line-height: 64px;
    margin: 0 35px;
    position: relative;
    border-bottom: 2px solid #fff;
    &:hover {
      transition: all 0.3s ease-in-out;
      border-bottom: 2px solid #0095e5;
    }
  }

  .nav-item-active {
    border-bottom: 2px solid #0095e5;
  }
}
</style>
