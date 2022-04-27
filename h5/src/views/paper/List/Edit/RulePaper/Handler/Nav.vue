<template>
  <div class="nav-bar">
    <el-tabs v-model="tabIndex" @tab-click="$emit('tab', tabIndex)">
      <el-tab-pane
        :key="item.name"
        v-for="item in tab"
        :label="item.label"
        :name="item.name"
      >
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tabIndex: '1',
      tab: [
        {
          label: '拖拽试题',
          name: '1',
        },
        {
          label: '添加试题',
          name: '2',
        },
        {
          label: '批量操作',
          name: '3',
        },
        {
          label: '答题卡',
          name: '4',
        },
      ],
    }
  },
}
</script>

<style lang="scss" scoped>
.nav-bar {
  background: #fff;
  width: 100%;
  height: 40px;
  font-size: 16px;
  color: #333;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 100;
  font-weight: 600;
  padding: 0 20px;
  border-radius: 8px 8px 0 0;
  /deep/ .el-tabs__nav-wrap::after {
    background-color: #fff;
  }

  /deep/ .el-tabs {
    border: none;
    position: inherit;
    margin-right: 0;
  }

  /deep/ .el-tabs__item {
    border-bottom: none;
  }

  /deep/ .el-tabs__item.is-active,
  /deep/ .el-tabs__item:hover {
    background-color: #fff;
  }

  /deep/ .el-tabs__nav {
    height: 40px;
  }

  /deep/ .el-tabs__header {
    background: #fff;
    width: 100%;
  }

  /deep/ .el-tabs__item {
    height: 38px;
    line-height: 40px;
  }
}
</style>
