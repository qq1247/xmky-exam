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
import InitPwd from './InitPwd.vue'
import Delete from './Deletes.vue'
import Frozen from './Frozen.vue'
export default {
  data() {
    return {
      tab: [
        {
          name: '用户信息',
          intro: '添加/修改',
          icon: 'common common-edit',
          contentName: '用户信息',
          contentIntro: '',
          index: '1'
        }
      ],
      contentName: '',
      contentIntro: '',
      contentUrl: '',
      viewList: [Setting, InitPwd, Delete, Frozen],
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
          name: '重置密码',
          intro: '恢复默认密码',
          icon: 'common common-lock',
          contentName: '重置密码',
          contentIntro:
            '恢复默认密码，一般在用户忘记密码时使用。可在【系统参数/用户密码】设置默认值。',
          contentUrl: 'ParamIndex',
          index: '2'
        },
        {
          name: '删除',
          intro: '删除用户',
          icon: 'common common-delete',
          contentName: '删除',
          contentIntro: '用户被删除后，之前创建的考试、答题等信息会保留。',
          index: '3'
        },
        {
          name: '冻结',
          intro: '冻结账号',
          icon: 'common common-frozen',
          contentName: '冻结账号',
          contentIntro: '冻结账号',
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
