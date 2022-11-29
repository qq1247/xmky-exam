<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { loginSysTime } from 'api/common.js'
import * as dayjs from 'dayjs'
export default {
  name: 'App',
  data() {
    return {
      times: 0, // 循环次数
      serverTime: null, // 服务器时间
    }
  },
  mounted() {
    this.synServerTime()
  },
  methods: {
    // 每隔30秒同步一次服务器时间；30秒内使用本地浏览器计时；30秒内会有误差，但影响不大
    async synServerTime() {
      if (this.times <= 0) {
        this.times = 30
        let {data} = await loginSysTime({})
        this.serverTime = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
        //console.log('服务器时间：', dayjs(this.serverTime).format('YYYY-MM-DD HH:mm:ss'))
      } else {
        this.serverTime = dayjs(this.serverTime).add(1, 'second').toDate()
        //console.log('本地计时：', dayjs(this.serverTime).format('YYYY-MM-DD HH:mm:ss'))
      }
      
      this.$tools.setServerTime(this.serverTime)
      this.times--
      setTimeout(() => {
        this.synServerTime()
      }, 1000)
    }
  }
}
</script>

<style lang="scss">
@import 'assets/style/iconfont.css';
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  position: relative;
  width: 100%;
}
</style>
