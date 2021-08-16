<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-08-03 14:22:34
 * @LastEditors: Che
 * @LastEditTime: 2021-08-16 13:06:53
-->
<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" class="form-inline search"></el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <div class="exam-item">
          <div @click="toMyExam" class="exam-content exam-add">
            <i class="common common-exam"></i>
            <span>我的考试</span>
          </div>
        </div>
        <div class="exam-item">
          <div @click="toMyMarkExam" class="exam-content exam-add">
            <i class="common common-readPaper"></i>
            <span>我的阅卷</span>
          </div>
        </div>
        <div
          class="exam-item"
          v-if="
            $store.getters.roles.includes('subAdmin') ||
            $store.getters.roles.includes('admin')
          "
        >
          <div @click="toBulletin" class="exam-content exam-add">
            <i class="common common-readPaper"></i>
            <span>我的公告</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {}
  },
  methods: {
    toMyExam() {
      this.$router.push({ path: '/my/exam' })
    },
    toMyMarkExam() {
      this.$router.push({ path: '/my/mark' })
    },
    toBulletin() {
      this.$router.push({ path: '/my/bulletin', query: {} })
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
.common-exam,
.common-readPaper {
  font-size: 60px;
}
</style>
