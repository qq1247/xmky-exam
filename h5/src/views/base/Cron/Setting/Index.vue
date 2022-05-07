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
            >去查看</router-link>
          </div>
        </div>
        <component :is="currentView" />
      </el-card>
    </div>
  </div>
</template>

<script>
import Setting from './Setting.vue'
import Start from './Start.vue'
import Stop from './Stop.vue'
import Once from './Once.vue'
import Delete from './Deletes.vue'
export default {
  components: {
    Setting,
    Start,
    Stop,
    Once,
    Delete
  },
  data() {
    return {
      tab: [
        {
          name: '定时任务信息',
          intro: '添加/修改',
          icon: 'common common-edit',
          contentName: '定时任务信息',
          contentIntro:
            '根据业务需要，可变更定时任务的触发时间。可在列表页面查看最近三次的触发时间。',
          contentUrl: 'CronIndex',
          index: '1'
        }
      ],
      contentName: '',
      contentIntro: '',
      contentUrl: '',
      viewList: [Setting, Start, Stop, Once, Delete],
      currentView: null
    }
  },
  computed: {
    tabIndex: {
      get() {
        return this.$route.params.tab
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
          name: '启动任务',
          intro: '启动定时任务',
          icon: 'common common-start',
          contentName: '启动任务',
          contentIntro: '启动定时任务',
          index: '2'
        },
        {
          name: '停止任务',
          intro: '停止定时任务',
          icon: 'common common-end',
          contentName: '停止任务',
          contentIntro: '停止定时任务',
          index: '3'
        },
        {
          name: '运行一次',
          intro: '运行定时任务一次',
          icon: 'common common-once',
          contentName: '运行一次',
          contentIntro: '运行定时任务一次',
          index: '4'
        },
        {
          name: '删除',
          intro: '删除定时任务',
          icon: 'common common-delete',
          contentName: '删除',
          contentIntro: '删除定时任务',
          index: '5'
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
