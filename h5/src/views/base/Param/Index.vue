<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 10:58:56
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 14:52:39
-->
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
    PassWord,
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
          index: '1',
        },
        {
          name: '企业信息',
          intro: '变更企业信息',
          icon: 'common common-dingdan',
          contentName: '企业信息',
          contentIntro: '变更企业信息',
          index: '2',
        },
        {
          name: '上传目录',
          intro: '上传附件保存位置',
          icon: 'common common-archive',
          contentName: '上传目录',
          contentIntro: '上传附件保存位置。如果填写的是相对路径，则相对路径是启动程序目录',
          index: '3',
        },
        {
          name: '邮件设置',
          intro: '邮件设置',
          icon: 'common common-email',
          contentName: '邮件设置',
          contentIntro: '邮件设置',
          index: '4',
        },
        {
          name: '备份目录',
          intro: '数据库备份目录',
          icon: 'common common-box',
          contentName: '备份目录',
          contentIntro: '数据库备份位置。如果填写的是相对路径，则相对路径是启动程序目录',
          index: '5',
        },
      ],
      contentName: '',
      contentIntro: '',
      contentUrl: '',
      viewList: [PassWord, Logo, File, Email, DataBase],
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
    this.currentView = this.viewList[Number(this.tabIndex) - 1]
    this.contentName = this.tab[Number(this.tabIndex) - 1].contentName
    this.contentIntro = this.tab[Number(this.tabIndex) - 1].contentIntro
    this.contentUrl = this.tab[Number(this.tabIndex) - 1].contentUrl || ''
  },
}
</script>

<style lang="scss" scoped>
.setting-container {
  width: 1200px;
  flex-direction: row;
}
/deep/ .el-tabs {
  height: 100%;
}
.setting-right {
  flex: 1;
}
/deep/ .el-tabs {
  margin-right: 20px;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.1);
  border: none;
}
/deep/ .el-tabs__item {
  height: auto;
  line-height: normal;
  border-bottom: 1px solid #ebebeb;
  &:last-child {
    border-bottom: none;
  }
}
/deep/ .el-tabs--right .el-tabs__header.is-right {
  margin-left: 0;
}
/deep/ .el-tabs--right .el-tabs__active-bar.is-right {
  width: 3px;
}
/deep/ .el-tabs__header {
  background: #fff;
  width: 300px;
}
/deep/ .el-tabs__nav-wrap::after {
  background-color: #fff;
}
/deep/ .el-tabs__item.is-active,
/deep/ .el-tabs__item:hover {
  color: initial;
  background: #f4f4f4;
}

.pane-label {
  display: flex;
  padding: 10px 0;
  .common {
    font-weight: 600;
    margin-right: 10px;
    padding-top: 2px;
  }
  .label-name {
    font-size: 14px;
    font-weight: 600;
  }
  .label-intro {
    font-size: 12px;
    color: #999;
    margin-top: 2px;
  }
}
.setting-right {
  flex: 1;
}

/deep/.el-card {
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.1);
  border: none;
}

/deep/.el-card__header {
  padding: 20px 20px 0;
  border-bottom: transparent;
}

.header-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 5px;
}
.header-intro {
  font-size: 13px;
  color: #999;
}
.header-url {
  font-size: 13px;
  color: #0094e5;
  text-decoration: underline;
  cursor: pointer;
}
</style>
