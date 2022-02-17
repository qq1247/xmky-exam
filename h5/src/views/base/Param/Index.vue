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
          name: '用户密码',
          intro: '添加/修改',
          icon: 'common common-lock',
          contentName: '用户密码',
          contentIntro:
            '阅卷方式选择人工阅卷，发布时检测到所有试题为智能题，则自动变更为智能阅卷。',
          index: '1',
        },
        {
          name: '单位信息',
          intro: '复制试卷',
          icon: 'common common-dingdan',
          contentName: '复制',
          contentIntro: '复制当前试卷为新试卷（草稿状态）',
          index: '2',
        },
        {
          name: '附件目录设置',
          intro: '发布试卷',
          icon: 'common common-archive',
          contentName: '删除',
          contentIntro: '发布后，试卷不可更改',
          index: '3',
        },
        {
          name: 'Email设置',
          intro: '删除试卷',
          icon: 'common common-email',
          contentName: '删除',
          contentIntro: '不影响关联的试题、考试等，可以正常显示和使用',
          index: '4',
        },
        {
          name: '数据库备份目录设置',
          intro: '删除试卷',
          icon: 'common common-box',
          contentName: '删除',
          contentIntro: '不影响关联的试题、考试等，可以正常显示和使用',
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
