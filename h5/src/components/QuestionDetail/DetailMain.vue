<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-10 10:13:52
 * @LastEditors: Che
 * @LastEditTime: 2021-12-01 17:58:22
-->
<template>
  <div class="detail-main">
    <div class="detail-title">
      <span>{{ data.id }}、</span>
      <div v-html="`${data.title}`"></div>
    </div>
    <template v-if="data.type === 4">
      <div
        class="detail-option"
        v-for="(option, index) in judgeOption"
        :key="index"
        v-html="`${String.fromCharCode(65 + index)}、${option}`"
      ></div>
    </template>
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
  data() {
    return {
      judgeOption: ['对', '错'],
    }
  },
}
</script>

<style lang="scss" scoped>
.detail-main {
  margin-bottom: 20px;
}
.detail-title {
  font-size: 14px;
  text-align: left;
  display: flex;
  p {
    margin: 0;
    padding: 0;
  }
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
