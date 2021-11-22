<!--
 * @Description: 公告栏
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-10-15 10:54:53
 * @LastEditors: Che
 * @LastEditTime: 2021-11-05 17:27:00
-->
<template>
  <div
    class="notice"
    v-if="show"
    :style="{ background: bgColor, color: color }"
  >
    <i
      v-if="leftIcon"
      :class="['notice-left common', 'common-' + leftIcon]"
    ></i>
    <div class="notice-content" :style="{ paddingRight: !model ? '15px' : 0 }">
      <slot>{{ content }}</slot>
    </div>
    <i
      v-if="model"
      :class="[
        'notice-right common',
        model === 'link' ? 'common-arrow-right' : 'common-close',
      ]"
      @click="click"
    ></i>
  </div>
</template>

<script>
export default {
  props: {
    leftIcon: {
      type: String,
      default: '',
    },
    /*
     * closeable 关闭
     * link 链接
     */
    model: {
      type: String,
      default: '',
    },
    content: {
      type: String,
      default: '',
    },
    bgColor: {
      type: String,
      default: '#fff',
    },
    color: {
      type: String,
      default: '#ed6a0c',
    },
  },
  data() {
    return {
      show: true,
    }
  },
  mounted() {},
  methods: {
    click(event) {
      if (this.model === 'closeable') {
        this.show = false
        this.$emit('close', event)
      } else {
        this.$emit('link', event)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.notice {
  display: flex;
  width: 100%;
  height: 40px;
  line-height: 40px;
  cursor: pointer;
  border-width: 1px;
  border-style: solid;
  border-radius: 20px;
  .notice-left,
  .notice-right {
    width: 40px;
    line-height: 40px;
    text-align: center;
  }
  .notice-content {
    flex: 1;
    padding: 0 5px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
</style>
