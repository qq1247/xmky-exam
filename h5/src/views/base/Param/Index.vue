<template>
  <div class="container setting-container">
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
import Logo from '../Param/Logo.vue'
import File from '../Param/File.vue'
import Email from '../Param/Email.vue'
import DataBase from '../Param/DataBase.vue'
import PassWord from '../Param/PassWord.vue'
export default {
  components: {
    Logo,
    File,
    Email,
    DataBase,
    PassWord
  },
  data() {
    return {
      tab: [
        {
          name: '重置密码',
          intro: '设置密码生成策略',
          icon: 'common common-lock',
          contentName: '重置密码',
          contentIntro: '添加新用户或重置密码时，设置密码生成策略。',
          index: '1'
        },
        {
          name: '企业信息',
          intro: '变更企业信息',
          icon: 'common common-dingdan',
          contentName: '企业信息',
          contentIntro: '变更企业信息',
          index: '2'
        },
        {
          name: '上传目录',
          intro: '上传附件保存位置',
          icon: 'common common-archive',
          contentName: '上传目录',
          contentIntro:
            '上传附件保存位置。如果填写的是相对路径，则相对路径是启动程序目录',
          index: '3'
        },
        {
          name: '邮件设置',
          intro: '邮件设置',
          icon: 'common common-email',
          contentName: '邮件设置',
          contentIntro: '邮件设置',
          index: '4'
        },
        {
          name: '备份目录',
          intro: '数据库备份目录',
          icon: 'common common-box',
          contentName: '备份目录',
          contentIntro:
            '数据库备份位置。如果填写的是相对路径，则相对路径是启动程序目录',
          index: '5'
        }
      ],
      contentName: '',
      contentIntro: '',
      contentUrl: '',
      viewList: [PassWord, Logo, File, Email, DataBase],
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
    this.currentView = this.viewList[Number(this.tabIndex) - 1]
    this.contentName = this.tab[Number(this.tabIndex) - 1].contentName
    this.contentIntro = this.tab[Number(this.tabIndex) - 1].contentIntro
    this.contentUrl = this.tab[Number(this.tabIndex) - 1].contentUrl || ''
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/common-setting.scss';

.setting-container {
  padding-top: 16px;
}
</style>
