<template>
  <div class="container setting-container">
    <el-tabs v-model="tabIndex" tab-position="right">
      <el-tab-pane :key="item.index" v-for="item in tab" :name="item.index">
        <div class="pane-label" slot="label">
          <i :class="item.icon"></i>
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
              :to="{ name: contentUrl }"
              v-if="contentUrl"
              class="header-url"
              >去设置</router-link
            >
          </div>
        </div>
        <component :is="currentView"></component>
      </el-card>
    </div>
  </div>
</template>

<script>
import Setting from './Setting.vue'
import Publish from './Publish.vue'
import Delete from './Deletes.vue'
import Anonymous from './Anonymous.vue'
import Ranking from './Ranking.vue'
import Score from './Score.vue'
import Message from './Message.vue'
import Exports from './Exports.vue'
export default {
  data() {
    return {
      tab: [
        {
          name: '编辑',
          intro: '添加/修改',
          icon: 'common common-edit',
          contentName: '考试信息',
          contentIntro:
            '1：试卷是智能阅卷类型，不显示阅卷时间，考试时间结束时自动阅卷；2：试卷是人工阅卷类型，需填阅卷时间，阅卷时间结束时自动结束考试；3：未答题和未阅卷部分自动标记为0分；4：考试（阅卷）时间到到，才允许查看考试统计；5：考试时间范围内，才允许查看在线用户',
          index: '1',
        },
      ],
      viewList: [
        Setting,
        Publish,
        Delete,
        Anonymous,
        Ranking,
        Score,
        Message,
        Exports,
      ],
      currentView: null,
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
      },
    },
  },
  created() {
    if (Number(this.$route.params.id)) {
      this.tab = [
        ...this.tab,
        {
          name: '发布',
          intro: '发布考试',
          icon: 'common common-publish',
          contentName: '发布',
          contentIntro: '1：发布后，考试不可更改；2：发布后才允许设置考试用户',
          index: '2',
        },
        {
          name: '删除',
          intro: '删除考试',
          icon: 'common common-delete',
          contentName: '删除',
          contentIntro: '不影响关联的试题、试卷等，可以正常显示和使用',
          index: '3',
        },
        {
          name: '匿名阅卷',
          intro: '匿名阅卷',
          icon: 'common common-anonymous',
          contentName: '匿名阅卷',
          contentIntro: '匿名阅卷时不显示用户头像,头像等',
          index: '4',
        },
        {
          name: '排名公开',
          intro: '排名公开',
          icon: 'common common-ranking',
          contentName: '排名公开',
          contentIntro: '排名公开时，用户可以查看当前名次',
          index: '5',
        },
        {
          name: '成绩公开',
          intro: '成绩公开',
          icon: 'common common-score',
          contentName: '成绩公开',
          contentIntro: '成绩公开时，用户可以查看考试成绩',
          index: '6',
        },
        {
          name: '邮件通知',
          intro: '邮件通知',
          icon: 'common common-message',
          contentName: '邮件通知',
          contentIntro:
            '1.下发邮件给考试用户、阅卷用户，2.【】内的文字带有特殊含义，发送邮件时会自动替换，如【姓名】替换为张三',
          index: '7',
        },
        {
          name: '试卷导出',
          intro: '试卷导出',
          icon: 'common common-template-down',
          contentName: '试卷导出',
          contentIntro: '选择不同考试人员，导出其试卷',
          index: '8',
        },
      ]
    }
    this.currentView = this.viewList[Number(this.tabIndex) - 1]
    this.contentName = this.tab[Number(this.tabIndex) - 1].contentName
    this.contentIntro = this.tab[Number(this.tabIndex) - 1].contentIntro
    this.contentUrl = this.tab[Number(this.tabIndex) - 1].contentUrl || ''
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/common-setting.scss';
</style>
