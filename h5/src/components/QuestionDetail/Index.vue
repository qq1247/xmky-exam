<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-10 10:12:59
 * @LastEditors: Che
 * @LastEditTime: 2021-09-13 14:55:25
-->
<template>
  <div class="question-detail">
    <DetailMain :data="data"></DetailMain>
    <el-divider v-if="!moreStatus" @click="showMore"
      >查看更多<i class="el-icon-arrow-down"></i
    ></el-divider>
    <DetailMore
      v-if="moreStatus"
      :data="data"
      :type="type"
      :difficulty="difficulty"
    ></DetailMore>
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
    type: {
      type: Array,
      default: [],
    },
    difficulty: {
      type: Array,
      default: [],
    },
  },
  watch: {
    data: {
      deep: true,
      handler() {
        this.moreStatus = false
      },
    },
  },
  data() {
    return {
      moreStatus: false,
    }
  },
  methods: {
    showMore() {
      this.moreStatus = !this.moreStatus
    },
  },
}
</script>

<style lang="scss">
.question-detail {
  padding: 20px;
}
</style>
