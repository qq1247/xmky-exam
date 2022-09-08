<template>
  <div class="content">
    <!-- 试题列表区域 -->
    <el-scrollbar wrap-style="overflow-x:hidden;" class="content-center">
      <div v-for="(question) in list" :key="question.id" class="question">
        <Question
          :question="question"
          :no="question.id"
          :showMode="switchMode"
        />
        <div class="question-slot">
          <slot name="question-btn" :question="question"></slot>
        </div>
      </div>
      <el-empty v-if="list.length === 0" description="暂无试题">
        <img slot="image" src="@/assets/img/data-null.png" alt="" />
      </el-empty>
      <!-- 试题底部插槽，可用于放分页等信息 -->
      <slot name="question-bottom"></slot>
    </el-scrollbar>

    <!-- 切换样式按钮区域 -->
    <div class="content-center-right">
      <div :class="['btn', switchMode==='simple' ? 'btn-active' : '']" @click="switchMode='simple'">
        <i class="el-icon-s-unfold"></i>
      </div>
      <div :class="['btn', switchMode==='detail' ? 'btn-active' : '']" @click="switchMode='detail'">
        <i class="el-icon-menu"></i>
      </div>
    </div>
  </div>
</template>

<script>
import Question from './Question.vue'
export default {
  components: {
    Question,
  },
  props: {
    list: {
      type: Array,
      default: [],
    },
    showMode: {
      type: String,
      default: 'detail',
    }
  },
  data() {
    return {
      switchMode : this.showMode
    }
  },
}
</script>

<style lang="scss" scoped>
.content {
  display: flex;
  width: 100%;
  margin: 0 auto;
}
.content-center {
  flex: 1;
  background: #fff;
  border-left: 1px solid rgba(0, 0, 0, 0.1);
  // border-right: 1px solid rgba(0, 0, 0, 0.1);
  padding-bottom: 40px;
  overflow: hidden;
}
.content-center-right {
  padding: 10px 10px 10px 0px;
  display: flex;
  flex-direction: column;
  background: #fff;
  .btn {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 40px;
    height: 40px;
    margin-left: 0;
    border: 1px solid rgba(0, 0, 0, 0.1);
    cursor: pointer;
    &:nth-child(1) {
      border-bottom: 0;
    }
  }
  .btn-active {
    background: #0094e5;
    border: 0;
    color: #fff;
  }
}
.question {
  position: relative;
  &:hover {
    .center-card {
      border: 1px solid #0094e5;
      background: rgba(#0094e5, 0.06%);
    }
    .question-slot {
      display: block;
    }
  }
}
.question-slot {
  position: absolute;
  right: 20px;
  bottom: 10px;
  display: none;
  /deep/ .el-button--mini {
    padding: 3px 8px;
  }
}
</style>
