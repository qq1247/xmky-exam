<template>
  <div class="content">
    <!-- 试题列表区域 -->
    <el-scrollbar wrap-style="overflow-x:hidden;" class="content-center">
      <div v-for="(question, index) in list.questionList" :key="question.id">
        <Question
          :question="question"
          :no="index + 1"
          :showMode="showMode"
        ></Question>
      </div>
      <el-empty v-if="list.questionList.length === 0" description="暂无试题">
      <img slot="image" src="@/assets/img/data-null.png" alt="" />
    </el-empty>
    </el-scrollbar>
    <!-- 切换样式按钮区域 -->
    <div class="content-right">
      <div :class="['btn', showMode==='simple' ? 'btn-active' : '']" @click="showMode='simple'">
        <i class="el-icon-s-unfold"></i>
      </div>
      <div :class="['btn', showMode==='detail' ? 'btn-active' : '']" @click="showMode='detail'">
        <i class="el-icon-menu"></i>
      </div>
    </div>
    <!-- 插槽，放自定义信息 -->
    <slot name="content-bottom"></slot>
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
      type: Object,
      default: () => {},
    },
    showMode: {
      type: String,
      default: 'detail',
    }
  },
  data() {
    return {

    }
  },
  methods: {

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
.content-right {
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
</style>
