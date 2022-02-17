<template>
  <div class="container">
    <RuleEdit v-if="genType === 1"></RuleEdit>
    <RandomEdit v-if="genType === 2"></RandomEdit>
  </div>
</template>
<script>
import RuleEdit from './RuleEdit.vue'
import RandomEdit from './RandomEdit.vue'
export default {
  components: {
    RuleEdit,
    RandomEdit,
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
