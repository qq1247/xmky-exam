<template>
  <div class="setting-container">
    <el-tabs v-model="tabIndex" tab-position="right">
      <el-tab-pane v-for="item in tab" :key="item.index" :name="item.index">
        <div slot="label" class="pane-label">
          <i :class="item.icon" />
          <div>
            <div class="label-name">{{ item.name }}</div>
            <div class="label-intro">{{ item.intro }}</div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="setting-right">
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <div class="header-name">{{ contentName }}</div>
          <div class="header-intro">{{ contentIntro }}</div>
        </div>
        <component :is="currentView" />
      </el-card>
    </div>
  </div>
</template>

<script>
import Setting from './Setting.vue'
import Role from './Role.vue'
import Delete from './Deletes.vue'
export default {
  data() {
    return {
      tab: [
        {
          name: '编辑',
          intro: '添加/修改',
          icon: 'common common-edit',
          contentName: '试卷分类信息',
          contentIntro:
            '为试卷创建一个分类。建议：按类型分开存放，方便管理维护 ',
          index: '1'
        }
      ],
      viewList: [Setting, Role, Delete],
      currentView: null
    }
  },
  computed: {
    tabIndex: {
      get() {
        return this.$route.params.tab || '1'
      },
      set(val) {
        this.currentView = this.viewList[Number(val) - 1]
        this.contentName = this.tab[Number(val) - 1].contentName
        this.contentIntro = this.tab[Number(val) - 1].contentIntro
        this.contentUrl = this.tab[Number(val) - 1].contentUrl || ''
      }
    }
  },
  created() {
    if (Number(this.$route.params.id)) {
      this.tab = [
        ...this.tab,
        {
          name: '共享权限',
          intro: '允许其他子管理员使用该试卷',
          icon: 'common common-role',
          contentName: '共享权限',
          contentIntro:
            '允许其他子管理员使用该试卷。例：招人部门共享入职试卷，由人事组织进行考试',
          index: '2'
        },
        {
          name: '删除',
          intro: '删除分类',
          icon: 'common common-delete',
          contentName: '删除',
          contentIntro: '该分类下有试卷，则不允许删除',
          index: '3'
        }
      ]
    }
    this.currentView = this.viewList[Number(this.tabIndex) - 1]
    this.contentName = this.tab[Number(this.tabIndex) - 1].contentName
    this.contentIntro = this.tab[Number(this.tabIndex) - 1].contentIntro
    this.contentUrl = this.tab[Number(this.tabIndex) - 1].contentUrl || ''
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/common-setting.scss';
</style>
