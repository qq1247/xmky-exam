<!--
 * @Description: 用户信息
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-13 09:46:35
 * @LastEditors: Che
 * @LastEditTime: 2022-01-12 18:25:08
-->
<template>
  <div class="user-info">
    <div class="info-handler">
      <el-avatar :size="80" v-if="userInfo.user">{{
        (userInfo.user.name && userInfo.user.name.slice(0, 1)) || '头像'
      }}</el-avatar>
      <div class="info-user">
        <p v-if="userInfo.user">{{ userInfo.user.name || '***' }}</p>
        <p v-if="userInfo.org">{{ userInfo.org.name || '***' }}</p>
      </div>
    </div>
    <div class="info-content">
      <template v-if="onlyRole[0] === 'user'">
        <div class="content-item" v-if="userInfo.exam">
          <p><span>考试次数：</span>{{ userInfo.exam.num }}</p>
          <p><span>缺考次数：</span>{{ userInfo.exam.missNum }}</p>
          <p><span>通过次数：</span>{{ userInfo.exam.succNum }}</p>
          <p><span>最高排名：</span>{{ userInfo.exam.top }}</p>
        </div>
        <div class="content-item" v-if="userInfo.score">
          <p><span>平均分：</span>{{ userInfo.score.avg }}</p>
          <p><span>最低分：</span>{{ userInfo.score.min }}</p>
          <p><span>最高分：</span>{{ userInfo.score.max }}</p>
          <p><span>标准差：</span>{{ userInfo.score.sd }}</p>
        </div>
      </template>
      <template v-if="onlyRole[0] === 'subAdmin'">
        <div
          class="content-item"
          v-if="
            userInfo.exam &&
            userInfo.paper &&
            userInfo.question &&
            userInfo.myMark
          "
        >
          <p><span>创建考试次数：</span>{{ userInfo.exam.num }}</p>
          <p><span>创建试卷次数：</span>{{ userInfo.paper.num }}</p>
          <p><span>创建试题次数：</span>{{ userInfo.question.num }}</p>
          <p><span>待阅试卷：</span>{{ userInfo.myMark.num }}</p>
        </div>
      </template>
      <template v-if="onlyRole[0] === 'admin'">
        <div
          class="content-item"
          v-if="
            userInfo.user &&
            userInfo.subAdmin &&
            userInfo.bulletin &&
            userInfo.onlineUser
          "
        >
          <p><span>创建用户数：</span>{{ userInfo.user.num }}</p>
          <p><span>创建子管理数：</span>{{ userInfo.subAdmin.num }}</p>
          <p><span>创建公告数：</span>{{ userInfo.bulletin.num }}</p>
          <p><span>在线用户：</span>{{ userInfo.onlineUser.num }}</p>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getUserInfo, getSubAdminInfo, getAdminInfo } from 'api/report'
export default {
  data() {
    return {
      userInfo: {},
    }
  },
  computed: {
    ...mapGetters(['roles', 'onlyRole']),
  },
  watch: {
    '$store.getters.onlyRole': {
      immediate: true,
      handler(newValue) {
        const userInfo = newValue[0]
        if (userInfo === 'user') this.getUserInfo()
        if (userInfo === 'subAdmin') this.getSubAdminInfo()
        if (userInfo === 'admin') this.getAdminInfo()
      },
    },
  },
  methods: {
    async getUserInfo() {
      const userInfo = await getUserInfo()
      this.userInfo = userInfo.data
    },
    async getSubAdminInfo() {
      const userInfo = await getSubAdminInfo()
      this.userInfo = userInfo.data
    },
    async getAdminInfo() {
      const userInfo = await getAdminInfo()
      this.userInfo = userInfo.data
    },
  },
}
</script>

<style lang="scss" scoped>
.user-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  margin-top: 60px;
  padding: 10px 0;
  background: #8fa4b3;
  border-radius: 10px;
  .info-handler {
    display: flex;
    margin: 0 30px;
    .info-user {
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      margin-left: 20px;
    }
  }
  .info-content {
    flex: 1;
    height: 100%;
    display: flex;
    line-height: 24px;
    span {
      display: inline-block;
      text-align: right;
      width: 130px;
    }
  }
}
</style>
