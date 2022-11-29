<template>
  <div >
    <span>{{preTxt}}</span>
    <span v-if="d > 0">{{ d }}天</span>
    <span>{{h}}小时{{m}}分{{s}}秒</span>
  </div>
</template>

<script>
import * as dayjs from 'dayjs'
export default {
  props: {
    expireTime: {
      type: Date,
      default: new Date()
    },
    preTxt: {
      type: String,
      default: ''
    },
},
  data() {
    return {
      d:0,
      h:0,
      m:0,
      s:0,
    }
  },
  mounted() {
    this.synTime()
  },
  methods: {
    // 同步时间
    synTime() {
      if (this.$tools.getServerTime()) { // f5刷新页面，导致获取不到时间 22.11.07 zhc
        let second = (this.expireTime.getTime() - this.$tools.getServerTime().getTime()) / 1000
        if (second <= 0) {
          this.$emit('endCallback')
          return
        }
        this.d = Math.floor(second / 86400)
        this.h = Math.floor((second / 3600) % 24)
        this.m = Math.floor((second / 60) % 60)
        this.s = second % 60
      }

      setTimeout(() => {
        this.synTime()
      }, 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
/deep/.el-select-dropdown__list {
  padding-bottom: 35px;
}
.custom-pager {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  line-height: 30px;
  background: #fff;
}
</style>
