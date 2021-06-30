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
        <div class="center-top">
          <div class="paper-title">{{ paper.name }}</div>
          <div class="paper-intro">{{paper.id}}</div>
        </div>

        <div class="center-drag">
          <div v-if="paperQuestion.length > 0">
            <div :key="index" class="drag-item drag-content drag-parent" v-for="(item, index) in paperQuestion">
              <div class="chapter">
                <div class="chapter-item">
                  <div class="item-title">{{ item.chapter.name }}</div>
                  <div></div>
                </div>
                <div class="chapter-description">{{ item.chapter.description }}</div>
              </div>

              <div :class="[
                  'drag-content',
                  item.questionList.length != 0 ? 'drag-children' : '',
                ]" v-if="item.chapter.show">
                <template v-if="item.questionList.length > 0">
                  <div :id="`p-${item.chapter.id}-${index}`" :key="child.id" class="children-content" v-for="(child, index) in item.questionList">
                    <p v-html="(index + 1) + '、' + child.title"></p>

                    <!-- 单选 -->
                    <template v-if="child.type === 1">
                      <el-radio-group @change="((val)=>{updateAnswer(child.id, val)})" class="children-option" v-model="myExamDetailCache[child.id].answers[0]">
                        <el-radio :key="index" :label="String.fromCharCode(65 + index)" class="option-item" v-for="(option, index) in child.options">
                          <span v-html="`${String.fromCharCode(65 + index)}、${option}`"></span>
                        </el-radio>
                      </el-radio-group>
                    </template>

                    <!-- 多选 -->
                    <template v-if="child.type === 2">
                      <el-checkbox-group @change="((val)=>{updateAnswer(child.id, val)})" class="children-option" v-model="myExamDetailCache[child.id].answers">
                        <el-checkbox :key="index" :label="String.fromCharCode(65 + index)" class="option-item" v-for="(option, index) in child.options">
                          <span v-html="`${String.fromCharCode(65 + index)}、${option}`"></span>
                        </el-checkbox>
                      </el-checkbox-group>
                    </template>

                    <!-- 填空 -->
                    <template v-if="child.type === 3">
                      <el-input @change="((val)=>{updateFillBlanksAnswer(child.id, val, child.answers, index)})" placeholder="请输入内容" v-for="(answer, index) in myExamDetailCache[child.id].answers" v-model="myExamDetailCache[child.id].answers[index]">
                        <template slot="prepend">第{{index + 1}}空</template>
                      </el-input>
                    </template>

                    <!-- 判断 -->
                    <template v-if="child.type === 4">
                      <el-radio-group @change="((val)=>{updateAnswer(child.id, val)})" class="children-option" v-model="myExamDetailCache[child.id].answers[0]">
                        <el-radio :key="index" :label="option" class="option-item" v-for="(option, index) in ['对','错']">{{ option }}</el-radio>
                      </el-radio-group>
                    </template>

                    <!-- 问答 -->
                    <template v-if="child.type === 5">
                      <el-input :rows="2" @change="((val)=>{updateAnswer(child.id, val)})" placeholder="请输入内容" type="textarea" v-model="myExamDetailCache[child.id].answers[0]"></el-input>
                    </template>

                    <div class="children-footer">
                      <div class="children-buts"></div>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
          <div class="data-null" v-if="paperQuestion.length == 0">
            <img alt class="data-img" src="../../assets/img/data-null.png" />
            <span class="data-tip">暂无试卷信息</span>
          </div>
        </div>
      </div>

          <el-collapse class="exam-card" v-if="paperQuestion.length > 0" v-model="collapseShow">
            <div class="exam-head">答题卡</div>
            <div id="countdown"></div>
            <el-collapse-item :key="item.id" :name="index" :title="item.chapter.name" v-for="(item,index) in paperQuestion">
              <a @click="toHref(item.chapter.id, index )" v-for="(child, index) in item.questionList">{{ index+ 1 }}</a>
            </el-collapse-item>
            <div class="exam-footer">提交</div>
          </el-collapse>

          <!-- <el-collapse v-if="paperQuestion.length > 0" v-model="collapseShow">
            <el-collapse-item :key="item.id" :name="index" :title="item.chapter.name" v-for="(item,index) in paperQuestion">
              <div class="route-href">
                <div :class="['href-item', hrefPointer === `#p-${item.chapter.id}-${index}` ? 'href-item-active' : '']" :key="child.id" v-for="(child, index) in item.questionList">
                  <span @click="toHref(item.chapter.id, index )">{{ index+ 1 }}</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>-->
    </div>
  </div>
</template>
<script>
export default {
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
  },
  methods: {
    // 返回
    goBack() {
      this.$router.push('/examPaper/list');
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
      } catch (error) {}
    },
    // 查询试卷信息
    async queryPaperInfo() {
      try {
        const res = await this.$https.paperQuestionList({
          id: this.paperId,
        });
        res.data.map((item) => {
          item.chapter.show = true;
        });
        this.paperQuestion = [...res.data];
      } catch (error) {}
    },
    // 查询我的答案信息
    async queryMyExamAnswerInfo() {
      try {
        const res = await this.$https.myExamAnswerList({
          id: this.id,
        });

        this.myExamDetailCache = res.data.reduce((acc, cur) => {
          acc[cur.questionId] = cur;
          return acc;
        }, {});
      } catch (error) {
        alert(error);
      }
    },
    // 更新答案
    async updateAnswer(questionId, answers) {
      if (!this.myExamDetailCache[questionId]) {
        alert('更新答案失败，请联系管理员');
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
        alert('更新答案失败，请联系管理员');
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
      document.querySelector(`#p-${id}-${index}`).scrollIntoView({
        behavior: 'smooth',
      });
    },
  },
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
  padding: 0 20px 10px;
  margin: 70px auto 0;
}

.drag-content {
  width: 100%;
  border: 1px solid #d8d8d8;
}

.drag-active {
  transform: scale(0.98);
  border: 1px solid #0094e5;
  border-radius: 5px;
  border-bottom: 1px solid #0094e5 !important;
  transition: all 0.15s ease-in-out;
}

.drag-question-active {
  border: 1px solid #0094e5 !important;
  box-shadow: 0 0 16px 2px rgba(0, 148, 229, 0.15);
  transition: all 0.15s ease-in-out;
}

.content-left {
  width: 300px;
  background: #fff;
  position: fixed;
  top: 70px;
  left: 20px;
  bottom: 10px;
  z-index: 100;
  .left-top {
    width: 100%;
    height: 45px;
    line-height: 45px;
    text-align: left;
    padding-left: 10px;
    font-size: 14px;
    color: #333;
    position: relative;
    border-bottom: 1px solid #d8d8d8;
    &::before {
      content: '';
      display: block;
      position: absolute;
      top: 15px;
      left: 0;
      width: 2px;
      height: 15px;
      background: rgb(30, 159, 255);
    }
  }
  .chapter-form {
    padding: 15px 10px 0 10px;
    border-bottom: 10px solid #f2f4f5;
    .el-form-item {
      margin-bottom: 15px;
    }
  }
  .paper-form {
    padding: 15px 0 0 10px;
    .el-form-item,
    .el-button {
      width: 135px;
    }
    .el-form-item {
      margin-bottom: 15px;
    }
  }
  .drags {
    padding: 10px;
    .drag-item {
      border-bottom: 1px solid #e8e8e8;
      padding: 15px;
      font-size: 14px;
    }
    .item-title {
      margin-bottom: 10px;
    }
    .el-tag {
      margin-left: 10px;
    }
  }
}

.content-center {
  background: #fff;
  margin-left: 310px;
  width: calc(100% - 560px);
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
  .drag-parent {
    padding: 10px;
  }
  .drag-children {
    border: none;
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
    &:not(:last-child) {
      border-bottom: none;
    }
    p {
      margin: 0;
      line-height: 40px;
      padding-left: 10px;
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

.content-right {
  width: 240px;
  height: calc(100% - 80px);
  background: #fff;
  position: fixed;
  top: 70px;
  right: 20px;
  z-index: 100;
  .time-title {
    background-color: rgb(30, 159, 255);
    width: 100%;
    height: 40px;
    line-height: 40px;
    text-align: center;
    padding-left: 10px;
    font-size: 14px;
    color: #fff;
  }
  .time-residue {
    font-size: 12px;
    font-weight: bold;
    line-height: 50px;
    text-align: center;
  }
  .route-title {
    background: #0094e5;
    color: #fff;
    line-height: 14px;
    line-height: 40px;
    padding: 0 10px;
  }
}

.route-href {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  width: 100%;
  padding: 10px 15px 0;
  .href-item {
    width: 20%;
    height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    span {
      display: inline-block;
      width: 30px;
      height: 30px;
      line-height: 30px;
      text-align: center;
      font-size: 14px;
      border: 1px solid #d8d8d8;
    }
  }
  .href-item-active {
    span {
      border: 1px solid #0094e5;
      color: #0094e5;
    }
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

.pagination {
  font-weight: 400;
  text-align: center;
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
  font-family: 'Microsoft YaHei';
  border: 1px solid #e6e6e6;
  position: fixed;
  right: 50px;
  top: 70px;
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
</style>
