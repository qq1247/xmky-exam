<template>
  <div class="score-plate">
    <div class="plate-header">打分板</div>
    <!-- 打分间隔和分值 -->
    <div class="plate-body">
      <div class="plate-step">
        <span>间隔：</span>
        <span
          :class="['step-item', selectStep == index ? 'select-step' : '']"
          v-for="(item, index) in steps"
          :key="item"
          @click="stepHandler(index)"
          >{{ item }}</span
        >
        <el-input
          v-model="step"
          type="number"
          class="step-input"
          placeholder="自定义间隔"
          @input="stepInput"
        ></el-input>
      </div>
      <div class="plate-step">
        <span>分数：</span>
        <span
          :class="['step-item', selectScore == index ? 'select-step' : '']"
          v-for="(item, index) in scores"
          :key="item"
          @click="scoreHandler(index)"
          >{{ item }}</span
        >
      </div>
    </div>
    <!-- 上下一题，上下一卷，结束阅卷操作 -->
    <div class="plate-footer">
      <div class="footer-left">
        <el-switch v-model="markType" active-text="自动" inactive-text="人工">
        </el-switch>
      </div>
      <div class="footer-right">
        <div v-if="markType">
          <el-switch
            v-model="isNextQuestion"
            active-text="下一题"
            inactive-text="同题"
          >
          </el-switch>
        </div>
        <div v-else>
          <el-button type="primary" size="small" @click="nextQuestion">
            下一题
            <i class="el-icon-arrow-right el-icon--right"></i>
          </el-button>
          <el-button type="primary" size="small" @click="nextPaper">
            下一卷
            <i class="el-icon-d-arrow-right el-icon--right"></i>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      score: '',
      scores: [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1],
      step: '',
      steps: [0.5, 1],
      selectStep: 0,
      selectScore: 0,
      markType: true,
      isNextQuestion: true,
    }
  },
  methods: {
    createScores(step) {
      this.scores = []
      this.selectScore = 0
      for (let i = 0; i < 10; i++) {
        this.scores.push((step * (i + 1)).toFixed(2))
      }
    },
    stepHandler(index) {
      this.selectStep = index
      this.createScores(Number(this.steps[index]))
    },
    scoreHandler(index) {
      this.selectScore = index
      this.$emit('selectScore', Number(this.scores[index]))
    },
    stepInput(e) {
      this.step = e
      if (e !== '') {
        if (Number(e) <= 0) {
          this.step = 0
          this.scores = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]
          return
        }
        if (Number(e) > this.data.score) this.step = this.data.score
        this.createScores(this.step)
      }
    },
    nextQuestion() {
      this.$emit('nextQuestion')
      this.createScores(0.5)
      this.step = ''
    },
    nextPaper() {
      this.$emit('nextPaper')
    },
  },
}
</script>

<style lang="scss" scoped>
.score-plate {
  flex: 1;
  max-width: 560px;
  border: 1px solid #f2f2f2;
  border-radius: 5px;
  padding: 0 20px;
  line-height: 50px;
  box-shadow: 0 0 16px -3px rgba(0, 0, 0, 0.15);
}
.plate-header {
  font-size: 16px;
  padding-left: 10px;
  position: relative;
  &::after {
    content: '';
    display: block;
    width: 2px;
    height: 20px;
    position: absolute;
    background: #0095e5;
    top: 15px;
    left: 0;
  }
}
.plate-body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
}
.plate-footer {
  display: flex;
  .footer-left {
    flex: 1;
  }
}
.plate-step {
  display: flex;
  align-items: center;
  .step-item {
    width: 40px;
    height: 30px;
    line-height: 30px;
    font-size: 13px;
    border: 1px solid #ccc;
    text-align: center;
    border-radius: 3px;
    cursor: pointer;
    margin: 10px 3px;
  }
  .select-step {
    border: 1px solid #0094e5;
    color: #0094e5;
  }
  .step-input {
    width: 150px;
    line-height: 30px;
    /deep/.el-input__inner {
      line-height: 30px;
      height: 30px;
      border-color: #fff #fff #dcdfe6 #fff;
    }
  }
}
</style>
