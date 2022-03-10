<!--
 * @Description: 主体内容
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 18:58:27
 * @LastEditors: Che
 * @LastEditTime: 2022-01-04 13:16:12
-->
<template>
  <main :class="['app-main', height ? '' : 'custom-main']">
    <div class="main-nav" v-if="['user', 'subAdmin', 'admin'].includes(layout)">
      <el-tooltip
        effect="dark"
        placement="right"
        v-for="nav in navs"
        :key="nav.path"
        :content="nav.meta.title"
      >
        <div class="nav-item">
          <i
            :class="[
              `common ${nav.meta.icon}`,
              active === nav.path.split('/')[1] ? 'active' : '',
            ]"
            @click="$router.replace(nav.path)"
          ></i>
        </div>
      </el-tooltip>
    </div>
    <div
      :class="[
        'main-content',
        ['user', 'subAdmin', 'admin'].includes(layout) ? 'main-margin' : '',
      ]"
    >
      <transition name="fade-transform" mode="out-in">
        <router-view :key="key" />
      </transition>
    </div>
  </main>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'AppMain',
  props: {
    height: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      navs: [],
      layout: '',
      active: '',
    }
  },
  computed: {
    key() {
      return this.$route.path
    },
    ...mapGetters(['permission_routes', 'onlyRole']),
  },
  watch: {
    $route: {
      immediate: true,
      handler(e) {
        this.active = e.path.split('/')[1]
        this.layout = e.meta.layout
        if (e.name === 'Login') return //未登录的时候不做路由面包屑校验
        this.setNavBar()
      },
    },
  },
  methods: {
    setNavBar() {
      this.navs = this.permission_routes.filter(
        (item) => item?.meta?.layout === this.onlyRole[0] && !item?.meta?.hidden
      )
    },
  },
}
</script>

<style lang="scss" scoped>
.app-main {
  flex: 1;
  width: 100%;
  height: calc(100vh - 50px);
  padding-top: 50px;
  position: relative;
  overflow: hidden;
  display: flex;
}
.custom-main {
  padding-top: initial;
  height: auto;
}
.main-nav {
  width: 50px;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  border-right: 1px solid #ebeef5;
  position: fixed;
  z-index: 2000;
  top: 50px;
  bottom: 0;
  left: 0;
  .nav-item {
    height: 50px;
    display: flex;
    justify-self: center;
    align-items: center;
    cursor: pointer;
  }
  .common {
    display: block;
    width: 30px;
    height: 30px;
    line-height: 30px;
    text-align: center;
    border-radius: 50px;
    font-size: 16px;
    transition: font-size 0.1s ease-in-out;
    &:hover {
      font-size: 18px;
      background: rgba(#0095e5, 0.8);
      color: #fff;
    }
  }
  .active {
    font-size: 18px;
    background: rgba(#0095e5, 0.8);
    color: #fff;
  }
}

.main-content {
  flex: 1;
}

.main-margin {
  padding-left: 50px;
  padding-top: 20px;
}
</style>
