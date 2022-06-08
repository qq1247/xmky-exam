<template>
  <div class="top">
    <div class="top-title">{{ paperName }}</div>
    <div class="top-handler">
      <!-- 编辑、预览模式 -->
      <div class="type">
        <div
          class="type-item"
          :class="!preview ? 'edit-active' : ''"
          title="编辑模式"
          @click="setType(false)"
        >
          <img src="@/assets/img/question/question-edit.png" alt="">
        </div>
        <div
          class="type-item"
          :class="preview ? 'view-active' : ''"
          title="预览模式"
          @click="setType(true)"
        >
          <img src="@/assets/img/question/question-view.png" alt="">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    paperName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      preview: false
    }
  },
  methods: {
    setType(e) {
      this.preview = e
      this.$parent.$parent.preview = e
    }
  }
}
</script>

<style lang="scss" scoped>
.top {
  background: #fff;
  width: calc(1200px - 410px);
  height: 40px;
  color: #333;
  position: fixed;
  z-index: 100;
  font-weight: 600;
  padding-left: 30px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  border-radius: 8px 8px 0 0;
  &::before {
    content: '';
    display: inline-block;
    position: relative;
    top: 14px;
    left: -10px;
    width: 2px;
    height: 14px;
    background: #0094e5;
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
  border: 1px solid #eee;
  border-radius: 4px;
  margin-right: 16px;
  .type-item {
    width: 20px;
    height: 20px;
    line-height: 20px;
    text-align: center;
    cursor: pointer;
    &:first-child {
      border-radius: 2px 0 0 2px;
    }
    &:last-child {
      border-radius: 0 2px 2px 0;
    }
  }
  .edit-active {
    background: #0094e5;
    color: #fff;
    img {
      content: url('~@/assets/img/question/question-edit-active.png');
    }
  }
  .view-active {
    background: #0094e5;
    color: #fff;
    img {
      content: url('~@/assets/img/question/question-view-active.png');
    }
  }
}
</style>
