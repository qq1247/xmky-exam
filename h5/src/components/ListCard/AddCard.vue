<template>
  <div class="exam-item">
    <div class="exam-content exam-add" @click="$emit('addCard')">
      <i class="common common-plus" />
      <span>{{ addTitle }}</span>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    addTitle: {
      type: String,
      default: ''
    }
  },
  data() {
    return {}
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
</style>
