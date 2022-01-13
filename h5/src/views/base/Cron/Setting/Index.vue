<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-22 09:46:18
 * @LastEditors: Che
 * @LastEditTime: 2022-01-10 13:38:20
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
      <component :is="currentView"></component>
    </div>
  </div>
</template>

<script>
import Setting from './Setting.vue'
import Start from './Start.vue'
import Stop from './Stop.vue'
import Once from './Once.vue'
import Delete from './Deletes.vue'
export default {
  components: {
    Setting,
    Start,
    Stop,
    Once,
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
      viewList: [Setting, Start, Stop, Once, Delete],
      currentView: null,
    }
  },
  created() {
    if (Number(this.$route.params.id)) {
      this.tab = [
        ...this.tab,
        {
          name: '启动',
          index: '2',
        },
        {
          name: '停止',
          index: '3',
        },
        {
          name: '一次',
          index: '4',
        },
        {
          name: '删除',
          index: '5',
        },
      ]
    }
    this.currentView = this.viewList[Number(this.tabIndex) - 1]
  },
  computed: {
    tabIndex: {
      get() {
        return this.$route.params.tab
      },
      set(val) {
        this.currentView = this.viewList[Number(val) - 1]
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
.setting-right {
  flex: 1;
}
</style>
