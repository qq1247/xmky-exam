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
          <div class="header-intro">
            {{ contentIntro }}
            <router-link
              v-if="contentUrl"
              :to="{ name: contentUrl }"
              class="header-url"
            >去设置</router-link>
          </div>
        </div>
        <component :is="currentView" />
      </el-card>
    </div>
  </div>
</template>

<script>
import Setting from './Setting.vue'
import Role from './Role.vue'
import Move from './Move.vue'
import Delete from './Deletes.vue'
export default {
  data() {
    return {
      tab: [
        {
          name: '题库',
          intro: '添加/修改',
          icon: 'common common-edit',
          contentName: '题库',
          contentIntro:
            '给一组试题起一个名称，可用于组卷时按题库抽题等',
          index: '1'
        }
      ],
      contentName: '',
      contentIntro: '',
      contentUrl: '',
      viewList: [Setting, Role, Move, Delete],
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
          name: '权限',
          intro: '协助添加试题',
          icon: 'common common-role',
          contentName: '权限',
          contentIntro:
            '协助添加试题',
          index: '2'
        },
        {
          name: '合并',
          intro: '合并题库 ',
          icon: 'common common-move',
          contentName: '合并',
          contentIntro: '合并题库',
          index: '3'
        },
        {
          name: '删除',
          intro: '删除题库',
          icon: 'common common-delete',
          contentName: '删除',
          contentIntro: '该题库下有试题，则不允许删除',
          index: '4'
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
