<template>
  <div>
    <i
      :class="[
        'common',
        isFullscreen ? 'common-fullscreen-exit' : 'common-fullscreen',
      ]"
      @click="click"
    ></i>
  </div>
</template>

<script>
import screenfull from 'screenfull'

export default {
  name: 'Screenfull',
  data() {
    return {
      isFullscreen: false,
    }
  },
  mounted() {
    this.init()
  },
  beforeDestroy() {
    this.destroy()
  },
  methods: {
    click() {
      if (!screenfull.enabled) {
        this.$message({
          message: 'you browser can not work',
          type: 'warning',
        })
        return false
      }
      screenfull.toggle()
    },
    change() {
      this.isFullscreen = screenfull.isFullscreen
    },
    init() {
      if (screenfull.enabled) {
        screenfull.on('change', this.change)
      }
    },
    destroy() {
      if (screenfull.enabled) {
        screenfull.off('change', this.change)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.common {
  font-size: 19px;
  cursor: pointer;
  &:hover {
    color: #0095e5;
  }
}
</style>
