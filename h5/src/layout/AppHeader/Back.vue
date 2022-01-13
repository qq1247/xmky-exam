<!--
 * @Description: 返回
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-14 09:40:24
 * @LastEditors: Che
 * @LastEditTime: 2021-12-31 11:05:07
-->
<template>
  <div class="header-back" @click="() => $router.back()">
    <i class="common common-arrow-left"></i>
    <span class="title">{{ title || '返回' }}</span>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
      title: '',
    }
  },
  methods: {},
}
</script>

<style lang="scss" scoped>
.header-back {
  width: 15%;
  height: 100%;
  display: flex;
  align-items: center;
  padding-left: 20px;
  font-weight: 600;
  cursor: pointer;
  .title {
    margin-left: 10px;
    font-weight: normal;
  }
}
</style>
