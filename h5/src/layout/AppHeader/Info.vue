<!--
 * @Description: 信息
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-14 09:41:18
 * @LastEditors: Che
 * @LastEditTime: 2021-12-27 13:40:29
-->
<template>
  <div class="header-info">
    <div class="info-name">
      <span class="user-name">{{ name }}</span>
      <i class="common common-login-out" @click="loginOut"></i>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  components: {},
  props: {
    viewShow: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['name']),
  },
  mounted() {},
  methods: {
    view() {
      this.$emit('view')
    },
    loginOut() {
      this.$confirm(`确认退出登录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          this.$store.dispatch('user/resetToken').then(() => {
            this.$message('登出成功！')
            this.$router.push('/')
          })
        })
        .catch((err) => {
          console.log(err)
        })
    },
  },
}
</script>

<style lang="scss" scoped>
.header-info {
  width: 20%;
  height: 100%;
  display: flex;
  align-items: center;
  padding-right: 30px;
  justify-content: flex-end;
  .info-name {
    display: flex;
    align-items: center;
    .common-login-out {
      margin-left: 10px;
      &:hover {
        color: #0095e5;
        cursor: pointer;
      }
    }
  }
}
</style>
