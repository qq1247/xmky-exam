<template>
  <div class="container">
    <!-- 导航 -->
    <div class="head">
      <el-link :underline="false" @click="goBack" class="head-left" icon="el-icon-back">返回</el-link>
      <span>{{ paperName }}</span>
      <el-link :underline="false" class="head-right">
        <i class="common common-explain"></i>
      </el-link>
    </div>

    <!-- 内容 -->
    <div class="content">
      <div class="content-center">
        <div class="paper-title">{{ paper.name }}</div>
        <div class="paper-intro">{{ paper.id }}</div>

        <template v-if="paperQuestion.length > 0">
          <div :key="index" class="drag-item drag-content" v-for="(item, index) in paperQuestion">
            <div class="chapter">
              <div class="chapter-item">
                <div class="item-title">{{ item.chapter.name }}</div>
                <div></div>
              </div>
              <div class="chapter-description">{{ item.chapter.description }}</div>
            </div>

            <div
              :class="[
                'drag-content',
                item.questionList.length != 0 ? 'drag-children' : '',
              ]"
            >
              <template v-if="item.questionList.length > 0">
                <div
                  :id="`p-${item.chapter.id}-${index}`"
                  :key="child.id"
                  class="children-content"
                  v-for="(child, indexc) in item.questionList"
                >
                  <p v-html="(indexc + 1) + '、' + child.title"></p>

                  <!-- 单选 -->
                  <template v-if="child.type === 1">
                    <el-radio-group class="children-option" v-model="child.answers[0]">
                      <el-radio
                        disabled
                        :key="index"
                        :label="String.fromCharCode(65 + index)"
                        class="option-item"
                        v-for="(option, index) in child.options"
                      >
                        <span v-html="`${String.fromCharCode(65 + index)}、${option}`"></span>
                      </el-radio>
                    </el-radio-group>
                  </template>

                  <!-- 多选 -->
                  <template v-if="child.type === 2">
                    <el-checkbox-group class="children-option" v-model="child.answers">
                      <el-checkbox
                        disabled
                        :key="index"
                        :label="String.fromCharCode(65 + index)"
                        class="option-item"
                        v-for="(option, index) in child.options"
                      >
                        <span v-html="`${String.fromCharCode(65 + index)}、${option}`"></span>
                      </el-checkbox>
                    </el-checkbox-group>
                  </template>

                  <!-- 填空 -->
                  <template v-if="child.type === 3">
                    <el-input
                      disabled
                      class="question-text"
                      placeholder="请输入内容"
                      :key="index"
                      v-for="(answer, index) in child.answers"
                      :value="answer"
                    >
                      <template slot="prepend">第{{ index + 1 }}空</template>
                    </el-input>
                  </template>

                  <!-- 判断 -->
                  <template v-if="child.type === 4">
                    <el-radio-group class="children-option" v-model="child.answers[0]">
                      <el-radio
                        disabled
                        :key="index"
                        :label="option"
                        class="option-item"
                        v-for="(option, index) in ['对','错']"
                      >{{ option }}</el-radio>
                    </el-radio-group>
                  </template>

                  <!-- 问答 -->
                  <template v-if="child.type === 5">
                    <el-input
                      disabled
                      :rows="2"
                      class="question-text"
                      placeholder="请输入内容"
                      type="textarea"
                      v-model="child.answers[0]"
                    ></el-input>
                  </template>

                  <div class="children-analysis">
                    <div>【答案】：{{ child.answers[0] || child.answers.split(',') }}</div>
                    <div v-html="`【解析】：${child.analysis || '暂无解析'}`"></div>
                    <div>【分数】：{{ child.score }}分</div>
                    <div>
                      【得分】：
                      <ScorePlate
                        :key="child.id"
                        :ref="`p-${item.chapter.id}-${index}`"
                        :value="child.scorePlate"
                        :maxScore="child.score"
                        @input="scoreInput($event, index, indexc)"
                        @blur="scoreBlur($event)"
                        @selectScore="selectScore($event, index, indexc)"
                        @prevQuestion="prevQuestion"
                        @nextQuestion="nextQuestion"
                        @prevPaper="prevPaper"
                        @nextPaper="nextPaper"
                        @checkEnd="checkEnd"
                      ></ScorePlate>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </template>

        <div class="data-null" v-if="paperQuestion.length == 0">
          <img alt class="data-img" src="../../assets/img/data-null.png" />
          <span class="data-tip">暂无试卷信息</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import ScorePlate from '@/components/ScorePlate.vue'
import { paperList } from '@/mock/questionList.js'
export default {
  components: {
    ScorePlate
  },
  data() {
    return {
      id: 0,
      labelPosition: 'left',
      paperName: '',
      hrefPointer: '',
      paperId: 0,
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      collapseShow: 0,
      paperList: [],
      paperQuestion: [],
      myExamDetailCache: {},
      selectOption: '',
      paper: {},
    };
  },
  created() {
    const { id, paperId } = this.$route.query;
    this.id = id;
    this.paperId = paperId;
    this.init();
    /* const paperQuestion = paperList.data
    this.paperQuestion = paperQuestion */
  },
  methods: {
    // 返回
    goBack() {
      this.$router.push('/my');
    },
    // 初始化
    async init() {
      await this.queryPaper();
      await this.queryPaperInfo();
      await this.queryMyExamAnswerInfo();
    },
    // 查询试卷
    async queryPaper() {
      try {
        const res = await this.$https.paperGet({
          id: this.paperId,
        });
        console.info(res);
        this.paper = { ...res.data };
      } catch (error) { }
    },
    // 查询试卷信息
    async queryPaperInfo() {
      try {
        const res = await this.$https.paperQuestionList({
          id: this.paperId,
        });
        res.data.map((item, index) => {
          item.questionList.map(question => {
            question.scorePlate = ''
          })
        })
        this.paperQuestion = res.data;
      } catch (error) { }
    },
    // 查询我的答案信息
    async queryMyExamAnswerInfo() {
      try {
        const res = await this.$https.myExamAnswerList({
          id: this.id,
        });

        let paperQuestion = this.paperQuestion.reduce((acc, cur) => {
          acc.push(...cur.questionList)
          return acc
        }, [])

        this.myExamDetailCache = res.data.reduce((acc, cur, index) => {
          if (cur.questionType === 3 && paperQuestion[index].id === cur.questionId) {
            cur.answers.length = paperQuestion[index].answers.length
          }

          acc[cur.questionId] = cur;
          return acc;
        }, {});
      } catch (error) {
        this.$tools.message(error, 'error')
      }
    },
    // 更新答案
    async updateAnswer(questionId, answers) {
      if (!this.myExamDetailCache[questionId]) {
        this.$tools.message('更新答案失败，请联系管理员', 'error')
        return;
      }

      const res = await this.$https.myExamUpdateAnswer({
        myExamDetailId: this.myExamDetailCache[questionId].myExamDetailId,
        answers: answers,
      });
    },
    // 更新填空答案
    async updateFillBlanksAnswer(questionId, val, answers, index) {
      if (!this.myExamDetailCache[questionId]) {
        this.$tools.message('更新答案失败，请联系管理员', 'error')
        return;
      }

      answers[index] = val;
      const res = await this.$https.myExamUpdateAnswer({
        myExamDetailId: this.myExamDetailCache[questionId].myExamDetailId,
        answers: answers,
      });
    },
    // 定位锚点
    toHref(id, index) {
      this.hrefPointer = `#p-${id}-${index}`;
      document.documentElement.scrollTop = (document.querySelector(this.hrefPointer).offsetTop - 50)
    },
    // 设置分数
    setScore(e, idx, idxc) {
      let source = this.paperQuestion[idx].questionList[idxc]
      this.$set(source, 'scorePlate', e)
      if (e < 0) this.$set(source, 'scorePlate', 0)
      if (e > source.score) this.$set(source, 'scorePlate', source.score)
    },
    // 打分输入
    scoreInput(e, idx, idxc) {
      this.setScore(e, idx, idxc)
    },
    // 点击打分板分值
    selectScore(e, idx, idxc) {
      this.setScore(e, idx, idxc)
    },
    // 上一题
    prevQuestion() {

    },
    // 下一题
    nextQuestion() {

    },
    // 上一卷
    prevPaper() {

    },
    // 上一卷
    nextPaper() {

    },
    // 结束阅卷
    checkEnd() {

    },

  }
};
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 0;
  padding-bottom: 0;
  background: #fff;
}

.head {
  width: 100%;
  height: 50px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  .head-left {
    color: #fff;
  }
  .head-right {
    color: #fff;
    .common {
      font-size: 20px;
    }
  }
}

.content {
  width: 100%;
  margin-top: 50px;
}

.content-center {
  background: #fff;
  margin: 0 auto;
  width: 1200px;
  .center-drag {
    width: 100%;
    padding: 10px;
    .drag-item {
      margin-bottom: 10px;
    }
  }
  .chapter {
    display: flex;
    flex-direction: column;
    padding: 0 10px;
    .chapter-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .item-title {
        font-size: 16px;
      }
      /deep/.el-button {
        opacity: 0;
        .common {
          font-size: 12px;
          margin-right: 5px;
        }
      }
      &:hover {
        .el-button {
          opacity: 1;
        }
      }
    }
    .chapter-description {
      font-size: 14px;
      color: #999;
      padding-bottom: 10px;
      margin-top: -5px;
    }
  }
  .paper-title {
    font-size: 16px;
    color: #333;
    padding: 20px 0 10px 10px;
  }
  .paper-intro {
    font-size: 12px;
    color: #666;
    padding: 0 10px 15px;
    border-bottom: 1px solid #d8d8d8;
  }
  .item-title {
    line-height: 40px;
  }
  .children-content {
    border: 1px solid #d8d8d8;
    font-size: 14px;
    box-sizing: border-box;
    margin-bottom: 5px;
    p {
      margin: 0;
      line-height: 40px;
      padding: 0 10px;
      background: #e5f4fc;
    }
  }
  .children-option {
    padding: 10px 0 0 25px;
    .option-item {
      display: block;
      line-height: 30px;
    }
  }
  .option-item-text {
    border-bottom: 1px solid #d8d8d8;
    padding: 20px 10px 5px;
    color: #333;
    margin: 0 25px 0;
  }
  .question-text {
    margin: 4px 1%;
    width: 98%;
  }
  .children-analysis {
    line-height: 25px;
    padding-left: 20px;
    margin: 15px 0;
    font-size: 13px;
    color: #666;
    span {
      font-size: 15px;
      font-weight: bold;
    }
  }
  .el-tag {
    margin-right: 6px;
  }
  .children-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
  }
  .btn {
    padding: 5px 10px;
  }
}

.data-null {
  padding-top: 30px;
  .data-img {
    width: 64px;
    height: 64px;
  }
  .data-tip {
    margin: 0 auto 20px;
  }
}

.el-radio,
.el-checkbox {
  margin-right: 10px;
}

.box-card-no-border {
  border: none;
}

/deep/ .el-card__body {
  padding: 5px;
}

.el-input /deep/.el-input-group__prepend {
  background-color: #fff;
  border: 0px;
  width: 38px;
}
/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}
/deep/ .el-collapse-item__header {
  height: 36px;
  line-height: 36px;
  background-color: #f2f2f2;
  padding-left: 20px;
}
/deep/ .el-collapse-item__content {
  padding: 10px;
  font-size: 14px;
}
.exam-card {
  width: 214px;
  font-family: "Microsoft YaHei";
  border: 1px solid #e6e6e6;
  position: fixed;
  right: 50px;
  top: 70px;
}
.exam-time {
  line-height: 40px;
  text-align: center;
  color: #0094e5;
}
.exam-card .exam-head,
.exam-footer {
  background: #1e9fff;
  color: #fff;
  height: 40px;
  line-height: 40px;
  font-size: 18px;
  text-align: center;
  padding: 0px 18px;
  cursor: pointer;
}
.exam-card a {
  width: 32px;
  height: 25px;
  border: 1px solid #eceaea;
  color: #888;
  text-align: center;
  line-height: 23px;
  margin-left: 1px;
  display: inline-block;
  margin-top: 5px;
  margin-right: 5px;
  border-radius: 3px;
  text-decoration: none;
  cursor: pointer;
}
.exam-card a:hover {
  color: #1e9fff;
  border: 1px solid #1e9fff;
}
/deep/ #app {
  background: #fff;
}
/deep/.el-textarea.is-disabled .el-textarea__inner,
/deep/.el-input.is-disabled .el-input__inner {
  background-color: #fff;
  border-color: #0094e5;
  color: #000;
}
/deep/.el-checkbox__input.is-disabled.is-checked + span.el-checkbox__label,
/deep/.el-radio__input.is-disabled.is-checked + span.el-radio__label {
  color: #0094e5;
}
/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner,
/deep/.el-radio__input.is-disabled.is-checked .el-radio__inner {
  background-color: #0094e5;
  border-color: #0094e5;
}
/deep/.el-checkbox__input.is-disabled + span.el-checkbox__label,
/deep/.el-radio__input.is-disabled + span.el-radio__label {
  color: #000;
}
/deep/.el-checkbox__input.is-disabled .el-checkbox__inner,
/deep/.el-radio__input.is-disabled .el-radio__inner {
  background-color: #fff;
}
/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner::after {
  border-color: #fff;
}
</style>
