<template>
  <div class="question-detail">
    <DetailMain v-if="comment" :data="data" />
    <DetailMore :data="data" />
  </div>
</template>

<script>
import DetailMore from './DetailMore.vue'
import DetailMain from './DetailMain.vue'
export default {
  components: {
    DetailMain,
    DetailMore
  },
  props: {
    data: {
      type: Object,
      default: () => {}
    },
    comment: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {}
  },
  methods: {}
}
</script>

<style lang="scss" scoped>
.question-detail {
  padding: 20px;
}
</style>
