<!--
 * @Description: Logo
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 17:59:47
 * @LastEditors: Che
 * @LastEditTime: 2022-01-07 15:51:13
-->
<template>
  <div class="header-nav">
    <router-link to="/" class="logo">
      <el-image :src="orgLogo" lazy>
        <i slot="placeholder" class="common common-more-row"></i>
        <i slot="error" class="common common-perfect"></i>
      </el-image>
    </router-link>
    <div class="nav">
      <router-link
        :class="['nav-item', index == 0 ? 'nav-item-active' : '']"
        v-for="(item, index) in navInfo[$store.getters.onlyRole[0]]"
        :key="item.path"
        :to="{ name: item.path }"
        >{{ item.name }}</router-link
      >
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      orgLogo: `/api/login/logo` || require('../../assets/img/logo.png'),
      navInfo: {
        user: [
          {
            name: '首页',
            path: 'Home',
          },
          {
            name: '考试',
            path: 'MyExam',
          },
        ],
        subAdmin: [
          {
            name: '首页',
            path: 'Home',
          },
          {
            name: '考试',
            path: 'Question',
          },
        ],
        admin: [
          {
            name: '首页',
            path: 'Home',
          },
          {
            name: '基础管理',
            path: 'User',
          },
        ],
      },
    }
  },
}
</script>

<style lang="scss" scoped>
.header-nav {
  display: flex;
  align-items: center;
}
.logo {
  width: 50px;
  height: 50px;
  /deep/ .el-image {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    .el-image__inner {
      width: 40px;
      height: 40px;
    }
  }
  .common-perfect {
    font-size: 40px;
    color: #0095e5;
  }
}
.nav {
  display: flex;
  .nav-item {
    height: 50px;
    line-height: 50px;
    margin: 0 20px;
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
