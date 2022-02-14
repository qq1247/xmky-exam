<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-22 09:46:18
 * @LastEditors: Che
 * @LastEditTime: 2022-01-14 17:07:35
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
          <div class="header-intro">{{ contentIntro }}</div>
        </div>
        <component :is="currentView"></component>
      </el-card>
    </div>
  </div>
</template>

<script>
import Setting from './Setting.vue'
import InitPwd from './InitPwd.vue'
import Delete from './Deletes.vue'
export default {
  components: {
    Setting,
    InitPwd,
    Delete,
  },
  data() {
    return {
      tab: [
        {
          name: '编辑',
          intro: '这是编辑副标题',
          icon: 'common common-edit',
          contentName: '这是编辑内容标题',
          contentIntro: '这是编辑内容副标题',
          index: '1',
        },
      ],
      viewList: [Setting, InitPwd, Delete],
      currentView: null,
    }
  },
  created() {
    if (Number(this.$route.params.id)) {
      this.tab = [
        ...this.tab,
        {
          name: '初始化密码',
          intro: '这是编辑副标题',
          icon: 'common common-lock',
          contentName: '这是密码内容标题',
          contentIntro: '这是密码内容副标题',
          index: '2',
        },
        {
          name: '删除',
          intro: '这是编辑副标题',
          icon: 'common common-delete',
          contentName: '这是内容标题',
          contentIntro: '这是内容副标题',
          index: '3',
        },
      ]
    }
    this.currentView = this.viewList[Number(this.tabIndex) - 1]
    this.contentName = this.tab[Number(this.tabIndex) - 1].contentName
    this.contentIntro = this.tab[Number(this.tabIndex) - 1].contentIntro
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
      },
    },
  },
}
</script>

<style lang="scss" scoped>
.setting-container {
  width: 1200px;
  flex-direction: row;
  align-items: flex-start;
}
/deep/ .el-tabs {
  margin-right: 20px;
}
/deep/ .el-tabs__item {
  height: auto;
  line-height: normal;
  border-bottom: 1px solid #ebebeb;
  &:last-child {
    border-bottom: none;
  }
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
    margin-top: 10px;
  }
}
.setting-right {
  flex: 1;
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
</style>
