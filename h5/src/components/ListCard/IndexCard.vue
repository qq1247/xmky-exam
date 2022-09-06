<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- 标题 -->
      <div class="title ellipsis">
        {{ data.name || data.examName }}
      </div>
      <template v-if="name === 'question'">
        <div class="content-info ellipsis">
        </div>
        <div class="content-info ellipsis">
          <span>试题数量：{{ data.questionNum }}</span>
        </div>
      </template>
      <!-- 使用权限 -->
      <div v-if="name === 'paper'" class="content-info ellipsis">
        <span>共享权限：{{ data.readUserNames.join(',') || '暂无' }}</span>
      </div>

      <div class="handler">
        <!-- 修改 -->
        <span data-title="修改" @click="edit(data)">
          <i class="common common-edit" />
        </span>
        <!-- 删除 -->
        <span data-title="删除" @click="del(data)">
          <i class="common common-delete" />
        </span>
        <!-- 操作权限 -->
        <span
          v-if="['question'].includes(name)"
          data-title="操作权限"
          @click="role(data)"
        >
          <i class="common common-role" />
        </span>
        <!-- 共享权限 -->
        <span
          v-if="['paper'].includes(name)"
          data-title="共享权限"
          @click="role(data)"
        >
          <i class="common common-role" />
        </span>
        <template v-if="name === 'question'">
          <!-- 合并 -->
          <span data-title="合并" @click="move(data)">
            <i class="common common-move" />
          </span>
          <span data-title="试题列表" @click="detail(data)">
            <i class="common common-list-row" />
          </span>
          <!-- 开放 -->
          <!-- <span data-title="模拟练习" @click="open(data)">
            <i class="common common-simulate" />
          </span> -->
          <!-- 统计 -->
          <span class="last-span">
            <i class="common common-more-row" />
            <div class="handler-more">
              <div class="more-item" @click="txtImport(data)">
                <i class="common common-template-down" />文本导入
              </div>
              <div class="more-item" @click="statistics(data)">
                <i class="common common-statistics" />试题统计
              </div>
            </div>
          </span>
        </template>
        <!-- 详情 | 列表 -->
        <!-- <span
          v-if="detailTitles[name]"
          :data-title="detailTitles[name]"
          @click="detail(data)"
        >
          <i class="common common-list-row" />
        </span> -->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: () => {}
    },
    name: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      detailTitles: {
        paper: '试卷列表',
        exam: '考试列表'
      }
    }
  },
  methods: {
    // 是否有权限（只有创建用户才有权限）
    isRole(data) {
      // 是否是创建用户
      const isCreateUser =
        data.createUserId && this.$store.getters.userId !== data.createUserId
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
    // 合并
    move(data) {
      if (this.isRole(data)) return
      this.$emit('move', data)
    },
    // 列表 |详情
    detail(data) {
      this.$emit('detail', data)
    },
    // 试题统计
    statistics(data) {
      this.$emit('statistics', data)
    },
    // 文本导入
    txtImport(data) {
      this.$emit('txtImport', data)
    }
  }
}
</script>
vue
<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
</style>
