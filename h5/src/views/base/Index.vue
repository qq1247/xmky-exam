<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-27 17:31:01
 * @LastEditors: Che
 * @LastEditTime: 2021-09-28 10:20:17
-->
<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" class="form-inline search"></el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <div class="exam-item">
          <div @click="toDict" class="exam-content exam-add">
            <i class="common common-data-library"></i>
            <span>数据字典</span>
          </div>
        </div>
        <div class="exam-item">
          <div @click="toCron" class="exam-content exam-add">
            <i class="common common-time"></i>
            <span>定时任务</span>
          </div>
        </div>
        <div class="exam-item">
          <div @click="toParam" class="exam-content exam-add">
            <i class="common common-setting"></i>
            <span>系统参数</span>
          </div>
        </div>
        <div class="exam-item">
          <div @click="toSensitive" class="exam-content exam-add">
            <i class="common common-sensitive"></i>
            <span>敏感词库</span>
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
    toDict() {
      this.$router.push({ path: '/base/dict' })
    },
    toCron() {
      this.$router.push({ path: '/base/cron' })
    },
    toParam() {
      this.$router.push({ path: '/base/param' })
    },
    toSensitive() {
      this.$router.push({ path: '/base/sensitive' })
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
.exam-add {
  i {
    font-size: 60px;
  }
}
</style>
