<template>
  <div class="top">
    <div class="top-title">{{ paperName }}</div>
    <div class="top-handler">
      <!-- 编辑、预览模式 -->
      <div class="type">
        <div
          class="type-item common common-edit"
          :class="!preview ? 'active' : ''"
          @click="setType(false)"
          title="编辑模式"
        ></div>
        <div
          class="type-item common common-preview"
          :class="preview ? 'active' : ''"
          @click="setType(true)"
          title="预览模式"
        ></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      preview: false,
      paperName: '',
    }
  },
  created() {
    this.paperName = this.$parent.paperName
  },
  activated() {
    this.paperName = this.$parent.paperName
  },
  methods: {
    setType(e) {
      this.preview = e
      this.$parent.preview = e
      if (this.$parent.tabIndex === '3') {
        this.$parent.$refs.questionRouter.routerIndex = ''
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.top {
  background: #fff;
  width: calc(100% - 555px);
  height: 40px;
  color: #333;
  position: fixed;
  z-index: 100;
  font-weight: 600;
  padding-left: 30px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  &::before {
    content: '';
    display: inline-block;
    position: relative;
    top: 14px;
    left: -10px;
    width: 2px;
    height: 14px;
    background: #0095e5;
  }
  .top-title {
    flex: 1;
    line-height: 40px;
  }
  .top-handler {
    display: flex;
    align-items: center;
  }
}
.type {
  display: flex;
  justify-content: flex-end;
  background: #fff;
  border-radius: 4px;
  margin-left: 20px;
  box-shadow: -7px 0 13px -5px rgba(0, 0, 0, 0.2);
  .type-item {
    width: 40px;
    height: 40px;
    line-height: 40px;
    text-align: center;
    color: #555;
    cursor: pointer;
    &:first-child {
      border-top-left-radius: 4px;
      border-bottom-left-radius: 4px;
    }
    &:last-child {
      border-top-right-radius: 4px;
      border-bottom-right-radius: 4px;
    }
  }
  .active {
    background: #0095e5;
    color: #fff;
  }
}
</style>
