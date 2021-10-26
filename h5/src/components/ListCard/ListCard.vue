<!--
 * @Description: 业务卡片
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-13 14:52:40
 * @LastEditors: Che
 * @LastEditTime: 2021-10-22 10:16:41
-->
<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- 标题 -->
      <div class="title ellipsis">{{ data.name || data.examName }}</div>
      <el-row :gutter="20" class="content-info">
        <!-- 创建人 -->
        <el-col :span="12" class="info-left"
          >创建人：{{ data.createUserName }}</el-col
        >
        <!-- 修改人 -->
        <el-col :span="12" class="info-right"
          >修改人：{{ data.updateUserName || '***' }}</el-col
        >
      </el-row>
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
            >组卷方式：{{ ['人工组卷', '自动组卷'][data.genType] }}</el-col
          >
          <el-col :span="12" class="info-right"
            >展示方式：{{ ['', '整张', '章节', '单题'][data.showType] }}</el-col
          >
        </el-row>
      </template>
      <template v-if="name == 'examList'">
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >考试人数：{{ data.userNum }}</el-col
          >
          <el-col :span="12" class="info-right"
            >阅卷人数：{{ data.markNum }}</el-col
          >
        </el-row>
      </template>
      <el-row class="content-info">
        <el-col class="info-state">{{
          data.state == 1 ? '已发布' : '草稿'
        }}</el-col>
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
          <span data-title="阅卷设置" @click="read(data)">
            <i class="common common-setting"></i>
          </span>
          <span data-title="在线人员" @click="onLine(data)">
            <i class="common common-onLine"></i>
          </span>
          <span data-title="统计" @click="statistics(data)">
            <i class="common common-statistics"></i>
          </span>
          <!-- <span data-title="通知" @click="message(data)">
            <i class="common common-messages"></i>
          </span> -->
        </template>
      </div>
    </div>
  </div>
</template>

<script>
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
  methods: {
    // 是否有权限（只有创建者才有权限）
    isRole(data) {
      // 是否已经发布
      const isPublish = data.state == 1
      // 是否是子分类
      const isChildrenClassify = ['paperList', 'examList'].includes(this.name)

      if (isChildrenClassify && isPublish) {
        this.$message.warning('已发布不可修改！')
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
    // 阅卷设置
    read(data) {
      this.$emit('read', data)
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
@import '../../assets/style/list-card.scss';
</style>
