<template>
  <el-switch
    v-model="state"
    :active-value="2"
    :inactive-value="1"
    @change="userFrozen"
  />
</template>

<script>
import { userFrozen, userGet } from 'api/user'

export default {
  data() {
    return {
      id: null,
      state: 2
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    const res = await userGet({ id: this.id })
    this.state = res.data.state
  },
  methods: {
    async userFrozen() {
      const res = await userFrozen({ id: this.id })
      res.code === 200 && this.$router.back()
    }
  }
}
</script>
