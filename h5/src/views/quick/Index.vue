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
          <component
            :is="viewList[Number(tabIndex) - 1]"
            @prev="(e) => (tabIndex = e)"
            @next="(e) => (tabIndex = e)"
          ></component>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { removeQuick } from '@/utils/storage'
import PaperSetting from './PaperSetting.vue'
import PaperComposition from './PaperComposition.vue'
import ExamSetting from './ExamSetting.vue'
import MarkSetting from './MarkSetting.vue'
import ExamPublish from './ExamPublish.vue'
export default {
  data() {
    return {
      tab: [
        {
          name: '设置试卷',
          intro: '添加/修改',
          icon: 'common common-classify',
          index: '1',
        },
        {
          name: '快速组卷',
          intro: '添加/修改',
          icon: 'common common-classify',
          index: '2',
        },
        {
          name: '设置考试',
          intro: '添加/修改',
          icon: 'common common-exam',
          index: '3',
        },
        {
          name: '设置人员',
          intro: '添加/修改',
          icon: 'common common-role',
          index: '4',
        },
        {
          name: '发布试卷',
          intro: '添加/修改',
          icon: 'common common-publish',
          index: '5',
        },
      ],
      viewList: [
        PaperSetting,
        PaperComposition,
        ExamSetting,
        MarkSetting,
        ExamPublish,
      ],
      tabIndex: '1',
    }
  },
  computed: {
    ...mapGetters(['name']),
  },
  created() {
    removeQuick()
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
  margin-left: 201px;
  padding-right: 20px;
}
</style>
