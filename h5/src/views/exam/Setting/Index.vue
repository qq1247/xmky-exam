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
            {{ contentIntro
            }}<router-link
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
import Delete from './Deletes.vue'
import Message from './Message.vue'
import Exports from './Exports.vue'
import SetTime from './SetTime.vue'
export default {
  data() {
    return {
      tab: [
        {
          name: '时间变更',
          intro: '时间变更',
          icon: 'common common-time',
          contentName: '时间变更',
          contentIntro: '允许提前延后考试时间',
          index: '1'
        }
      ],
      viewList: [
        SetTime,
        Delete,
        Message,
        Exports,
      ],
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
          name: '删除',
          intro: '删除考试',
          icon: 'common common-delete',
          contentName: '删除',
          contentIntro: '不影响关联的试题、试卷等，可以正常显示和使用',
          index: '2'
        },
        {
          name: '邮件通知',
          intro: '邮件通知',
          icon: 'common common-email',
          contentName: '邮件通知',
          contentIntro:
            '1.下发邮件给考试用户、阅卷用户，2.【】内的文字带有特殊含义，发送邮件时会自动替换，如【姓名】替换为张三',
          index: '3'
        },
        // {
        //   name: '试卷导出',
        //   intro: '试卷导出',
        //   icon: 'common common-template-down',
        //   contentName: '试卷导出',
        //   contentIntro: '选择不同考试人员，导出其试卷',
        //   index: '4'
        // },
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
