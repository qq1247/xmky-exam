<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-10 10:13:52
 * @LastEditors: Che
 * @LastEditTime: 2021-09-13 17:53:44
-->
<template>
  <div class="detail-main">
    <div class="detail-title" v-html="`${data.id}、${data.title}`"></div>
    <template v-if="data.options && data.options.length > 0">
      <div
        class="detail-option"
        v-for="(option, index) in data.options"
        :key="index"
        v-html="`${String.fromCharCode(65 + index)}、${option}`"
      ></div>
    </template>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: () => {},
    },
  },
}
</script>

<style lang="scss" scoped>
.detail-main {
  margin-bottom: 40px;
}
.detail-title {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.detail-option {
  display: flex;
  padding-left: 30px;
  line-height: 30px;
  p,
  span {
    display: flex;
    align-items: center;
  }
}
</style>
