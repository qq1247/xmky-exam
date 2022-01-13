<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-10 10:12:59
 * @LastEditors: Che
 * @LastEditTime: 2021-12-28 14:10:45
-->
<template>
  <div class="question-detail">
    <DetailMain v-if="comment" :data="data"></DetailMain>
    <DetailMore :data="data"></DetailMore>
  </div>
</template>

<script>
import DetailMore from './DetailMore.vue'
import DetailMain from './DetailMain.vue'
export default {
  components: {
    DetailMain,
    DetailMore,
  },
  props: {
    data: {
      type: Object,
      default: () => {},
    },
    comment: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {}
  },
  methods: {},
}
</script>

<style lang="scss" scoped>
.question-detail {
  padding: 20px;
}
</style>
