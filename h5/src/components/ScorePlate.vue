<template>
  <div :class="['score-plate', minimize ? 'plate-minimize' : '']">
    <div class="plate-header">
      <div class="plate-title">打分板</div>
      <i
        v-if="minimize"
        title="边缘拖拽，点击展开"
        class="common common-score-plate"
        @click="minimize = !minimize"
      />
      <i
        v-else
        title="最小化"
        class="common common-minimize"
        @click="minimize = !minimize"
      />
    </div>
    <!-- 打分间隔和分值 -->
    <div class="plate-body">
      <div class="plate-step">
        <div class="step-title">分数：</div>
        <div class="step-content">
          <div class="step-body">
            <span
              v-for="(item, index) in halfScores"
              :key="item"
              :class="[
                'step-item',
                selectHalfScore === index ? 'select-step' : '',
              ]"
              @click="halfScoreHandler(index)"
            >{{ item }}</span>
          </div>
          <div class="step-body">
            <span
              v-for="(item, index) in scores"
              :key="item"
              :class="['step-item', selectScore === index ? 'select-step' : '']"
              @click="scoreHandler(index)"
            >{{ item }}</span>
          </div>
        </div>
      </div>
      <!-- 上下一题，上下一卷，结束阅卷操作 -->
      <div class="plate-footer">
        <div class="footer-left">
          <el-switch
            v-model="markType"
            active-text="自动"
            inactive-text="人工"
          />
        </div>
        <div class="footer-right">
          <div v-if="markType">
            <el-switch
              v-model="isNextQuestion"
              active-text="下一题"
              inactive-text="同题"
            />
          </div>
          <div v-else>
            <el-button type="primary" size="small" @click="nextQuestion">
              下一题
              <i class="el-icon-arrow-right el-icon--right" />
            </el-button>
            <el-button type="primary" size="small" @click="nextPaper">
              下一卷
              <i class="el-icon-d-arrow-right el-icon--right" />
            </el-button>
          </div>
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
      default: () => {}
    },
    score: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      scores: [],
      halfScores: [],
      selectScore: null,
      selectHalfScore: null,
      markType: true,
      isNextQuestion: true,
      minimize: false
    }
  },
  watch: {
    score: {
      deep: true,
      immediate: true,
      handler(n) {
        if (n) {
          this.scores = []
          this.halfScores = []
          this.selectScore = null
          this.selectHalfScore = null
          for (let index = 0; index < n; index++) {
            this.scores.push(1 + index)
            this.halfScores.push(0.5 + index)
          }
        }
      }
    }
  },
  methods: {
    scoreHandler(index) {
      this.selectScore = index
      this.selectHalfScore = null
      this.$emit('selectScore', Number(this.scores[index]))
    },
    halfScoreHandler(index) {
      this.selectHalfScore = index
      this.selectScore = null
      this.$emit('selectScore', Number(this.halfScores[index]))
    },
    nextQuestion() {
      this.$emit('nextQuestion')
    },
    nextPaper() {
      this.$emit('nextPaper')
    }
  }
}
</script>

<style lang="scss" scoped>
.score-plate {
  position: fixed;
  top: calc(100% / 2);
  z-index: 2000;
  padding: 0 20px;
  line-height: 50px;
  border-radius: 5px;
  background: #fff;
  width: 560px;
  height: auto;
  min-height: 150px;
  box-shadow: 0 0 16px -3px rgba(0, 0, 0, 0.15);
  transition: all 0.1s linear;
  user-select: none;
}

.plate-minimize {
  width: 50px;
  height: 50px;
  min-height: 50px;
  border-radius: 50%;
  padding: 0;
  .plate-header {
    width: 50px;
    height: 50px;
    padding-left: 0;
    &::after {
      display: none;
    }
    .plate-title {
      display: none;
    }
  }
  .plate-body {
    display: none;
  }
}
.plate-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  padding-left: 10px;
  position: relative;
  &::after {
    content: '';
    display: block;
    width: 2px;
    height: 20px;
    position: absolute;
    background: #0094e5;
    top: 15px;
    left: 0;
  }
  .common {
    position: absolute;
    right: 12px;
    z-index: 100;
    font-size: 24px;
    color: #eb5b5b;
    line-height: 24px;
    text-align: center;
    cursor: pointer;
  }
}
.plate-body {
  border-top: 1px solid #dcdfe6;
}
.plate-footer {
  display: flex;
  border-top: 1px solid #dcdfe6;
  .footer-left {
    flex: 1;
  }
}
.plate-step {
  display: flex;
  width: 100%;
  .step-title {
    width: 50px;
  }
  .step-content {
    flex: 1;
  }
  .step-body {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
  }
  .step-item {
    width: 40px;
    height: 30px;
    line-height: 30px;
    font-size: 13px;
    background: #e9f4fa;
    color: #0094e5;
    text-align: center;
    border-radius: 3px;
    cursor: pointer;
    margin: 10px 3px;
    overflow: hidden;
  }
  .select-step {
    position: relative;
    &::before {
      content: '';
      display: block;
      position: absolute;
      right: 0;
      bottom: 0;
      border-width: 5px;
      border-style: solid;
      border-color: transparent #0094e5 #0094e5 transparent;
    }
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
