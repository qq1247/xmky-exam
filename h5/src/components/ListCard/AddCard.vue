<!--
 * @Description: 添加组件（公共）
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-13 14:36:49
 * @LastEditors: Che
 * @LastEditTime: 2021-10-13 17:22:32
-->
<template>
  <div class="exam-item">
    <div class="exam-content exam-add" @click="$emit('addCard')">
      <i class="common common-plus"></i>
      <span>{{ addTitle }}</span>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    addTitle: {
      type: String,
      default: '',
    },
  },
  data() {
    return {}
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
</style>
