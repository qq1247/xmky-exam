<template>
  <div class="detail-main">
    <div class="detail-title">
      <span>{{ data.id }}、</span>
      <div v-html="`${data.title}`" />
    </div>
    <template v-if="data.type === 4">
      <div
        v-for="(option, index) in judgeOption"
        :key="index"
        class="detail-option"
        v-html="`${String.fromCharCode(65 + index)}、${option}`"
      />
    </template>
    <template v-if="data.options && data.options.length > 0">
      <div
        v-for="(option, index) in data.options"
        :key="index"
        class="detail-option"
        v-html="`${String.fromCharCode(65 + index)}、${option}`"
      />
    </template>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      judgeOption: ['对', '错']
    }
  }
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
