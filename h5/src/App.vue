<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-22 18:05:35
 * @LastEditors: Che
 * @LastEditTime: 2021-10-25 15:31:30
-->
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { loginSysTime } from 'api/common.js'
export default {
  name: 'App',
  data() {
    return {
      timer: null,
      timerNum: 1,
    }
  },
  mounted() {
    /* this.$once('hook:beforeDestroy', () => {
      this.clearTime()
    }) */
  },
  watch: {
    '$store.getters.token': {
      immediate: true,
      handler(n, o) {
        if (!n) {
          this.clearTime()
          return false
        } else {
          this.setSysTime()
        }
      },
    },
  },
  methods: {
    interval(func, wait) {
      const _this = this
      let interv = function () {
        func.call(_this || null)
        this.timer = setTimeout(interv, wait)
      }
      this.timer = setTimeout(interv, wait)
    },

    setSysTime() {
      this.interval(function () {
        if (this.timerNum % 30 === 0) {
          loginSysTime({}).then((res) => {})
        }
        this.timerNum++
      }, 1000)
    },

    clearTime() {
      clearTimeout(this.timer)
      this.timer = null
      this.timerNum = 1
    },
  },
  beforeDestroy() {
    this.clearTime()
  },
}
</script>

<style lang="scss">
@import url(//at.alicdn.com/t/font_840312_9l6zvjgo1xb.css);
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;
}
</style>
