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
  beforeDestroy() {
    this.clearTime()
  },
  methods: {
    interval(func, wait) {
      const _this = this
      const interv = function () {
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
}
</script>

<style lang="scss">
@import url('//at.alicdn.com/t/font_840312_0zkk51xnv8kj.css');
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  position: relative;
  width: 100%;
}
</style>
