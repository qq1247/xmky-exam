<template>
  <el-popover placement="right" v-model="visible" @hide="hideHandler" trigger="focus">
    <!-- 打分间隔和分值 -->
    <div class="score-body">
      <div class="score-step">
        <span>间隔：</span>
        <span
          :class="['step-item', selectStep == index ? 'select-step' : '']"
          v-for="(item,index) in steps"
          :key="item"
          @click="stepHandler(index)"
        >{{ item }}</span>
        <el-input
          v-model="step"
          type="number"
          class="step-input"
          placeholder="自定义间隔"
          @input="stepInput"
        ></el-input>
      </div>
      <div class="score-step">
        <span>分数：</span>
        <span
          :class="['step-item', selectScore == index ? 'select-step' : '']"
          v-for="(item,index) in scores"
          :key="item"
          @click="scoreHandler(index)"
        >{{ item }}</span>
      </div>
    </div>
    <!-- 上下一题，上下一卷，结束阅卷操作 -->
    <div class="score-footer">
      <el-button-group>
        <el-button type="primary" size="small" icon="el-icon-arrow-left" @click="prevQuestion">上一题</el-button>
        <el-button type="primary" size="small" @click="nextQuestion">
          下一题
          <i class="el-icon-arrow-right el-icon--right"></i>
        </el-button>
        <el-button type="primary" size="small" icon="el-icon-d-arrow-left" @click="prevPaper">上一卷</el-button>
        <el-button type="primary" size="small" @click="nextPaper">
          下一卷
          <i class="el-icon-d-arrow-right el-icon--right"></i>
        </el-button>
        <el-button
          type="primary"
          size="small"
          icon="el-icon-document-checked"
          @click="checkEnd"
        >完成阅卷</el-button>
      </el-button-group>
    </div>
    <el-input
      :id="`i-${data.id}`"
      type="number"
      slot="reference"
      class="score-input"
      placeholder="请点击 | 输入分数"
      :value="data.scorePlate"
      @input="input"
      @blur="blur"
    ></el-input>
  </el-popover>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      visible: false,
      score: '',
      scores: [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1],
      step: '',
      steps: [0.1, 0.5, 1],
      selectStep: 0,
      selectScore: 0
    }
  },
  methods: {
    input(e) {
      this.$emit('input', e)
    },
    blur(e) {
      this.$emit('blur', e)
    },
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
      if (e < 0) this.step = 0
      if (Number(e) > this.data.score) this.step = this.data.score
      this.createScores(this.step)
    },
    hideHandler() {
      Object.assign(this.$data, this.$options.data())
    },
    prevQuestion() {
      this.$emit('prevQuestion', this.data.id)
      this.hideHandler()
    },
    nextQuestion() {
      this.$emit('nextQuestion', this.data.id)
      this.hideHandler()
    },
    prevPaper() {
      this.$emit('prevPaper')
    },
    nextPaper() {
      this.$emit('nextPaper')
    },
    checkEnd() {
      this.$emit('checkEnd')
    }
  },
}
</script>

<style lang="scss" scoped>
.score-input {
  width: 200px;
  /deep/.el-input__inner {
    border-color: #fff #fff #dcdfe6 #fff;
  }
}
/deep/.el-popover {
  padding: 0;
}
.score-footer {
  text-align: center;
  padding: 10px 0 0 30px;
}
.score-step {
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
