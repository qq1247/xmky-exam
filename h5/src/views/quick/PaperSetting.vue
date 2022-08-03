<template>
    <div class="exam-content-box">
      <div v-for="(item, index) in exanCotent" :key="index">
        <img :src="item.imgSrc" alt="" />
        <span class="title">{{ item.title }}</span>
        <span>{{ item.desc }}</span>
        <el-button
          round
          size="medium"
          :class="`btn-${index + 1}`"
          @click="toPage(index)"
          >创建</el-button
        >
      </div>
    </div>
</template>

<script>
export default {
  data() {
    return {
      exanCotent: [
        {
          imgSrc: require('../../assets/img/mark/mark-time.png'),
          title: '空白试卷',
          desc: '创建一张空白试卷',
        },
        {
          imgSrc: require('../../assets/img/mark/mark-time.png'),
          title: '文本导入试卷',
          desc: '文本导入一张试卷，即时编辑预览',
        },
        {
          imgSrc: require('../../assets/img/mark/mark-time.png'),
          title: '随机',
          desc: '人工录入，创建一张试卷',
        },
      ],
    }
  },
  methods: {
    toPage(index) {
      this.$emit('switchTab', 1, index)
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-content-box {
  display: flex;
  margin: 15px;
  height: auto;
  min-height: 190px;
  div {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 30px 15px 20px;
    border-radius: 8px;
    color: #fff;
    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 10px 16px -10px rgba(95, 101, 105, 0.15);
      z-index: 100;
    }
    &:nth-child(1) {
      background-color: #0094e4;
      flex-grow: 2;
    }
    &:nth-child(2) {
      background: #09c9be;
      flex-grow: 3;
      margin: 0 10px;
    }
    &:nth-child(3) {
      background: #6a77f9;
      flex-grow: 1;
    }
    .title {
      font-size: 18px;
      font-weight: bold;
    }
    /deep/ .el-button--medium.is-round {
      padding: 10px 40px;
    }
    .btn-1 {
      margin-top: 10px;
      color: #0094e4;
    }
    .btn-2 {
      margin-top: 10px;
      color: #09c9be;
    }
    .btn-3 {
      margin-top: 10px;
      color: #6a77f9;
    }
  }
}
</style>