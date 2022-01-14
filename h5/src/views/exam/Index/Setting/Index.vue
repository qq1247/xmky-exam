<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-31 11:19:55
 * @LastEditors: Che
 * @LastEditTime: 2022-01-14 17:06:32
-->
<template>
  <div class="container setting-container">
    <el-tabs v-model="tabIndex" tab-position="left">
      <el-tab-pane
        :key="item.index"
        v-for="item in tab"
        :label="item.name"
        :name="item.index"
      >
      </el-tab-pane>
    </el-tabs>
    <div class="setting-right">
      <el-card class="box-card" shadow="never">
        <component :is="currentView"></component>
      </el-card>
    </div>
  </div>
</template>

<script>
import Setting from './Setting.vue'
import Delete from './Deletes.vue'
export default {
  components: {
    Setting,
    Delete,
  },
  data() {
    return {
      tab: [
        {
          name: '编辑',
          index: '1',
        },
      ],
      viewList: [Setting, Delete],
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
      },
    },
  },
  created() {
    if (Number(this.$route.params.id)) {
      this.tab = [
        ...this.tab,
        {
          name: '删除',
          index: '2',
        },
      ]
    }
    this.currentView = this.viewList[Number(this.tabIndex) - 1]
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
</style>
