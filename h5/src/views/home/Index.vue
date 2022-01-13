<!--
 * @Description: 首页
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-13 09:28:11
 * @LastEditors: Che
 * @LastEditTime: 2022-01-07 09:57:16
-->
<template>
  <div class="container">
    <Header></Header>
    <UserInfo></UserInfo>
    <UserContent v-if="onlyRole[0] != 'admin'"> </UserContent>
    <AdminContent v-else></AdminContent>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Header from './Header/Index.vue'
import UserInfo from './UserInfo.vue'
import UserContent from './UserContent.vue'
import AdminContent from './AdminContent.vue'
export default {
  components: {
    Header,
    UserInfo,
    UserContent,
    AdminContent,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['onlyRole']),
  },
}
</script>
<style lang="scss" scoped>
.container {
  width: 1200px;
}
</style>
