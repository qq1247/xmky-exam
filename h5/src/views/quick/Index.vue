<template>
  <div>
    <header class="app-header">
      <div class="header-back">
        <i
          class="common common-home"
          title="首页"
          @click="$router.push('/')"
        ></i>
      </div>
      <div>快速考试</div>
      <div class="header-info">
        <div class="info-name">
          <span class="user-name">{{ name }}</span>
          <i class="common common-login-out" @click="loginOut"></i>
        </div>
      </div>
    </header>
    <main class="app-main">
      <div class="container setting-container">
        <el-tabs v-model="tabIndex" tab-position="right">
          <el-tab-pane
            v-for="item in tab"
            :name="item.index"
            :key="item.index"
            :disabled="item.index !== tabIndex"
          >
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
              <div class="header-name">
                {{ tab[Number(tabIndex) - 1].contentName }}
              </div>
              <div class="header-intro">
                {{ tab[Number(tabIndex) - 1].contentIntro }}
              </div>
            </div>
            <keep-alive>
              <component
                :is="viewList[Number(tabIndex) - 1]"
                :paperType="paperType"
                @next="next"
                @prev="prev"
              ></component>
            </keep-alive>
          </el-card>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { removeQuick } from '@/utils/storage'
import PaperSetting from './PaperSetting/Index.vue'
import ExamSetting from './ExamSetting.vue'
import RemarkSetting from './RemarkSetting.vue'
import ExamPublish from './ExamPublish.vue'
export default {
  data() {
    return {
      tab: [
        {
          name: '设置试卷',
          intro: '添加/修改',
          icon: 'common common-classify',
          contentName: '试题分类信息',
          contentIntro:
            '为试题创建一个分类。建议：按类型分开存放，方便管理维护',
          index: '1',
        },
        {
          name: '设置考试',
          intro: '添加/修改',
          icon: 'common common-exam',
          contentName: '试题分类信息',
          contentIntro:
            '为试题创建一个分类。建议：按类型分开存放，方便管理维护',
          index: '2',
        },
        {
          name: '设置人员',
          intro: '添加/修改',
          icon: 'common common-role',
          contentName: '试题分类信息',
          contentIntro:
            '为试题创建一个分类。建议：按类型分开存放，方便管理维护',
          index: '3',
        },
        {
          name: '发布试卷',
          intro: '添加/修改',
          icon: 'common common-publish',
          contentName: '试题分类信息',
          contentIntro:
            '为试题创建一个分类。建议：按类型分开存放，方便管理维护',
          index: '4',
        },
      ],
      viewList: [PaperSetting, ExamSetting, RemarkSetting, ExamPublish],
      tabIndex: '1',
      tabActive: '1',
      paperType: 0,
      markType: 1,
    }
  },
  computed: {
    ...mapGetters(['name']),
  },
  methods: {
    loginOut() {
      this.$confirm(`确认退出登录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          this.$store.dispatch('user/resetToken').then(() => {
            this.$message('登出成功！')
            this.$router.push('/')
          })
        })
        .catch((err) => {
          console.log(err)
        })
    },
    next(e) {
      this.tabIndex = '' + (Number(this.tabIndex) + 1)
      this.paperType = e.paperType
    },
    prev() {
      this.tabIndex = '' + (Number(this.tabIndex) - 1)
    },
  },
  destroyed() {
    removeQuick()
  },
}
</script>

<style lang="scss" scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 50px;
  z-index: 2000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.header-back {
  width: 20%;
  i {
    display: inline-block;
    width: 25px;
    height: 25px;
    line-height: 24px;
    text-align: center;
    border-radius: 50%;
    font-weight: 600;
    margin-left: 20px;
    cursor: pointer;
    &:hover {
      transition: all 0.15s ease-in-out;
      background-color: #0095e5;
      color: #fff;
    }
  }
}

.header-info {
  width: 20%;
  height: 100%;
  display: flex;
  align-items: center;
  padding-right: 30px;
  justify-content: flex-end;
  .info-name {
    display: flex;
    align-items: center;
    .common-login-out {
      margin-left: 10px;
      &:hover {
        color: #0095e5;
        cursor: pointer;
      }
    }
  }
}

.app-main {
  flex: 1;
  width: 100%;
  min-height: 100vh;
  height: 100%;
  padding-top: 50px;
  position: relative;
  overflow: hidden;
  display: flex;
}

.setting-container {
  flex-direction: row;
}
/deep/ .el-tabs {
  height: 100%;
}
.setting-right {
  flex: 1;
}
/deep/ .el-tabs {
  margin-right: 15px;
  border-right: 1px solid #ebebeb;
  position: fixed;
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
  width: 200px;
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
  margin-left: 200px;
}

/deep/.el-card {
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
