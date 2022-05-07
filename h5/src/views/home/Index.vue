<template>
  <div class="container">
    <UserContent v-if="onlyRole[0] != 'admin'" />
    <AdminContent v-else />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import UserContent from './UserContent.vue'
import AdminContent from './AdminContent.vue'
export default {
  components: {
    UserContent,
    AdminContent
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['onlyRole'])
  }
}
</script>
<style lang="scss" scoped>
.container {
  width: 1200px;
}
</style>
