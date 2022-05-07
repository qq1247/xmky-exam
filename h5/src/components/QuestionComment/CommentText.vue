<template>
  <div :class="['comment-content', showTriangle ? 'comment-triangle' : '']">
    <el-input
      v-model="commentText"
      class="comment-text"
      type="textarea"
      :rows="2"
      resize="none"
      placeholder="您的评论..."
    />
    <div class="comment-btns">
      <el-checkbox v-model="anonymity">匿名</el-checkbox>
      <el-button
        type="warning"
        class="comment-btn"
        @click="comment"
      >评论</el-button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    showTriangle: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      commentText: '',
      anonymity: false
    }
  },
  methods: {
    comment() {
      this.$emit('comment', this.commentText, this.anonymity)
      this.commentText = ''
      this.anonymity = false
    }
  }
}
</script>

<style lang="scss" scoped>
.comment-content {
  background: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin-top: 20px;
  padding: 10px 10px 10px 30px;
  position: relative;
  .comment-text {
    flex: 1;
    & /deep/.el-textarea__inner {
      border: 1px solid #fc9512;
    }
  }
  .comment-btns {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
  }
  .comment-btn {
    padding: 5px 20px;
    font-size: 13px;
  }
}

.comment-triangle {
  padding: 10px;
  &::after {
    content: '';
    display: block;
    position: absolute;
    z-index: 100;
    right: 13px;
    top: -9px;
    transform: translateY(-50%);
    border-width: 10px;
    border-style: solid;
    border-color: transparent transparent #fff transparent;
  }
}

/deep/ .el-checkbox__input.is-checked .el-checkbox__inner,
/deep/ .el-checkbox__input.is-indeterminate .el-checkbox__inner {
  background-color: #fc9512;
  border-color: #fc9512;
}
/deep/ .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #fc9512;
}
/deep/ .el-checkbox.is-bordered.is-checked {
  border-color: #fc9512;
}
/deep/ .el-checkbox__input.is-focus .el-checkbox__inner {
  border-color: #fc9512;
}
</style>
