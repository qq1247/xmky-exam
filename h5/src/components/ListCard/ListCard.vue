<!--
 * @Description: 业务卡片
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-10-13 14:52:40
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:49:33
-->
<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- 标题 -->
      <div class="title ellipsis">{{ data.name || data.examName }}</div>
      <template v-if="name == 'paperList'">
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >及格：{{ (data.passScore * data.totalScore) / 100 }}</el-col
          >
          <el-col :span="12" class="info-right"
            >满分：{{ data.totalScore }}</el-col
          >
        </el-row>
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >组卷方式：{{ data.genType | genType }}</el-col
          >
          <el-col :span="12" class="info-right"
            >展示方式：{{ data.showType | showType }}</el-col
          >
        </el-row>
        <el-row class="content-info">
          <el-col :span="24" class="info-right"
            >阅卷方式：{{ data.markType | markType }}</el-col
          >
        </el-row>
      </template>
      <template v-if="name == 'examList'">
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >及格：{{
              ((data.paperPassScore / 100) * data.paperTotalScore).toFixed(2)
            }}</el-col
          >
          <el-col :span="12" class="info-right"
            >满分：{{ data.paperTotalScore }}</el-col
          >
        </el-row>
        <el-row class="content-info">
          <el-col class="info-left"
            >考试时间：{{ data.startTime }}（{{
              $tools.computeMinute(data.startTime, data.endTime)
            }}）</el-col
          >
        </el-row>
        <el-row class="content-info">
          <el-col class="info-left"
            >阅卷时间：{{ data.markStartTime }}（{{
              $tools.computeMinute(data.markStartTime, data.markEndTime)
            }}）</el-col
          >
        </el-row>
      </template>
      <el-row class="content-info">
        <el-col class="info-state">{{ data.state | state }}</el-col>
      </el-row>
      <div class="handler">
        <!-- 编辑 -->
        <span data-title="编辑" @click="edit(data)">
          <i class="common common-edit"></i>
        </span>
        <!-- 删除 -->
        <span data-title="删除" @click="del(data)">
          <i class="common common-delete"></i>
        </span>
        <!-- 发布 -->
        <span data-title="发布" @click="publish(data)">
          <i class="common common-publish"></i>
        </span>
        <template v-if="name == 'paperList'">
          <span data-title="复制" @click="copy(data)">
            <i class="common common-copy"></i>
          </span>
          <!-- <span
          data-title="归档"
          @click="archive(data)"
        >
          <i class="common common-archive"></i>
        </span> -->
          <span data-title="开始组卷" @click="composition(data)">
            <i class="common common-composition"></i>
          </span>
        </template>
        <template v-if="name == 'examList'">
          <span data-title="考试用户" @click="read(data)">
            <i class="common common-setting"></i>
          </span>
          <span data-title="统计" @click="statistics(data)">
            <i class="common common-statistics"></i>
          </span>
          <span class="last-span">
            <i class="common common-more-row"></i>
            <div class="handler-more">
              <div class="more-item" @click="onLine(data)">
                <i class="common common-onLine"></i>在线用户
              </div>
              <div class="more-item" @click="anonymous(data)">
                <i class="common common-anonymous"></i>匿名阅卷
              </div>
              <div class="more-item" @click="ranking(data)">
                <i class="common common-ranking"></i>排名公开
              </div>
              <div class="more-item" @click="score(data)">
                <i class="common common-score"></i>成绩公开
              </div>
              <div class="more-item" @click="message(data)">
                <i class="common common-messages"></i>邮件通知
              </div>
            </div>
          </span>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { getOneDict } from '@/utils/getDict'
export default {
  props: {
    data: {
      type: Object,
      default: () => {},
    },
    name: {
      type: String,
      default: '',
    },
  },
  data() {
    return {}
  },
  filters: {
    genType(data) {
      return getOneDict('PAPER_GEN_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    markType(data) {
      return getOneDict('PAPER_MARK_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    showType(data) {
      return getOneDict('PAPER_SHOW_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    state(data) {
      return getOneDict('EXAM_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
  },
  create() {},
  methods: {
    // 是否有权限（只有创建者才有权限）
    isRole(data) {
      // 是否已经发布
      const isPublish = data.state == 1

      const now = new Date().getTime()
      const startTime = new Date(data.startTime).getTime()
      const endTime = new Date(data.endTime).getTime()
      // 是否在考试时间段内
      const examTimeRange = now > startTime && now < endTime

      if (this.name === 'examList' && isPublish && examTimeRange) {
        this.$message.warning('暂无此项权限！')
        return true
      }
      return false
    },
    // 编辑
    edit(data) {
      if (this.isRole(data)) return
      this.$emit('edit', data)
    },
    // 删除
    del(data) {
      if (this.isRole(data)) return
      this.$emit('del', data)
    },
    // 复制
    copy(data) {
      this.$emit('copy', data)
    },
    // 归档
    archive(data) {
      this.$emit('archive', data)
    },
    // 组卷
    composition(data) {
      this.$emit('composition', data)
    },
    // 发布
    publish(data) {
      this.$emit('publish', data)
    },
    // 统计
    statistics(data) {
      this.$emit('statistics', data)
    },
    onLine(data) {
      this.$emit('onLine', data)
    },
    // 考试用户
    read(data) {
      this.$emit('read', data)
    },
    // 匿名阅卷
    anonymous(data) {
      this.$emit('anonymous', data)
    },
    // 排名公开
    ranking(data) {
      this.$emit('ranking', data)
    },
    // 成绩公开
    score(data) {
      this.$emit('score', data)
    },
    // 通知
    message(data) {
      this.$emit('message', data)
    },
  },
}
</script>
vue
<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
</style>
