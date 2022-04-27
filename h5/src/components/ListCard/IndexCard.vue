<!--
 * @Description: 业务卡片
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-10-13 14:52:40
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:49:25
-->
<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- 标题 -->
      <div class="title ellipsis">
        {{ data.name || data.examName }}
      </div>
      <template v-if="name === 'question'">
        <!-- 创建者 -->
        <div class="content-info ellipsis">
          创建者：{{ data.createUserName || data.userName }}
        </div>
        <!-- 编辑权限 -->
        <div class="content-info ellipsis">
          <span>操作权限：{{ data.writeUserNames.join(',') || '暂无' }}</span>
        </div>
      </template>
      <!-- 使用权限 -->
      <div class="content-info ellipsis" v-if="name == 'paper'">
        <span>共享权限：{{ data.readUserNames.join(',') || '暂无' }}</span>
      </div>

      <div class="handler">
        <!-- 修改 -->
        <span data-title="修改" @click="edit(data)">
          <i class="common common-edit"></i>
        </span>
        <!-- 删除 -->
        <span data-title="删除" @click="del(data)">
          <i class="common common-delete"></i>
        </span>
        <!-- 操作权限 -->
        <span
          data-title="操作权限"
          @click="role(data)"
          v-if="['question'].includes(name)"
        >
          <i class="common common-role"></i>
        </span>
        <!-- 共享权限 -->
        <span
          data-title="共享权限"
          @click="role(data)"
          v-if="['paper'].includes(name)"
        >
          <i class="common common-role"></i>
        </span>
        <template v-if="name == 'question'">
          <!-- 移动 -->
          <span data-title="移动" @click="move(data)">
            <i class="common common-move"></i>
          </span>
          <!-- 开放 -->
          <span data-title="模拟练习" @click="open(data)">
            <i class="common common-share"></i>
          </span>
          <!-- 统计 -->
          <span data-title="试题统计" @click="statistics(data)">
            <i class="common common-statistics"></i>
          </span>
        </template>
        <!-- 详情 | 列表 -->
        <span
          v-if="detailTitles[name]"
          :data-title="detailTitles[name]"
          @click="detail(data)"
        >
          <i class="common common-list-row"></i>
        </span>
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
    return {
      detailTitles: {
        question: '试题列表',
        paper: '试卷列表',
        exam: '考试列表',
      },
    }
  },
  methods: {
    // 是否有权限（只有创建者才有权限）
    isRole(data) {
      // 是否是创建者
      const isCreateUser =
        data.createUserId && this.$store.getters.userId != data.createUserId
      // 是否是分类
      const isParentClassify = ['question', 'paper', 'exam'].includes(this.name)

      if (isParentClassify && isCreateUser) {
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
    // 权限
    role(data) {
      if (this.isRole(data)) return
      this.$emit('role', data)
    },
    // 开放
    open(data) {
      if (this.isRole(data)) return
      this.$emit('open', data)
    },
    // 回收
    recycle(data) {
      this.$emit('recycle', data)
    },
    // 移动
    move(data) {
      if (this.isRole(data)) return
      this.$emit('move', data)
    },
    // 列表 |详情
    detail(data) {
      this.$emit('detail', data)
    },
    statistics(data) {
      this.$emit('statistics', data)
    },
  },
}
</script>
vue
<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
</style>
