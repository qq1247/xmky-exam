<template>
  <div class="container">
    <RulePaper v-if="genType === 1"></RulePaper>
    <RandomPaper v-if="genType === 2"></RandomPaper>
  </div>
</template>
<script>
import RulePaper from './RulePaper/Index.vue'
import RandomPaper from './RandomPaper.vue'
export default {
  components: {
    RulePaper,
    RandomPaper,
  },
  data() {
    return {
      genType: 0,
    }
  },
  async created() {
    this.genType = Number(this.$route.params.genType)
  },
}
</script>

<style lang="scss" scoped>
.container {
  margin-top: -20px;
}
</style>
