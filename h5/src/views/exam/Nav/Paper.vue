<template>
  <div class="content">
    <!-- 答题卡 -->
    <div class="paper-router">
      <el-divider>答题卡</el-divider>
      <div class="router-content">
        <Draggable
          v-model="examQuestions"
          tag="div"
          group="questionGroup"
          chosen-class="drag-question-active"
          animation="300"
        >
          <div
            v-for="(examQuestion, index) in examQuestions"
            :key="`examQuestions${index}`"
            :style="{'display': examQuestion.type === 1 ? 'block' : 'inline-block'}"
          >
            <div v-if="examQuestion.type === 1" class="router-title">
              {{examQuestion.chapterName}}
            </div>
            <div v-else>
              <a :class="['router-index']" @click="toHref(examQuestion)">{{
                examQuestion.question.no
              }}</a>
            </div>
          </div>
        </Draggable>
      </div>
    </div>
    <!-- 试卷 -->
    <el-scrollbar class="paper-content">
      <div class="top">
        <div class="top-title">
          <el-input
            :value="paperName"
            @input="updatePaperName"
            placeholder="请输入试卷名称"
            maxlength="32"
          ></el-input> {{totalScore}}分
        </div>
        <div class="top-handler">
          <el-button
            icon="el-icon-zoom-in"
            size="mini"
            round
            @click="toAdd"
            >题库选择</el-button
          >
          <el-button
            icon="el-icon-edit-outline"
            size="mini"
            round
            @click="toEditor"
            >文本导入</el-button
          >
          <el-button icon="el-icon-plus" size="mini" @click="_addChapter" round>章节添加</el-button>
          <el-button icon="el-icon-delete" size="mini" round @click="questionClear">试卷重置</el-button>
        </div>
      </div>
      <div class="content-center"
        v-for="(examQuestion, index) of examQuestions"
        :key="`examQuestions${index}`"
      >
        <div v-if="examQuestion.type === 1" class="chapter">
          <el-input
            v-model="examQuestion.chapterName"
            placeholder="请输入名称"
            maxlength="16"
          />
          <el-input
              v-model="examQuestion.chapterTxt"
              :rows="2"
              placeholder="请输入描述"
              type="textarea"
              :autosize="true"
            />
        </div>
        <Question
          v-else-if="examQuestion.type === 2"
          :question="examQuestion.question"
          :no="examQuestion.question.no"
        >
          <template #bottom>
            <div class="question-opt">
              <template v-if="examQuestion.question.type === 1 || examQuestion.question.type === 2 
                  || examQuestion.question.type === 4 || (examQuestion.question.type === 5 && examQuestion.question.markType === 2)">
                本题
                <el-input-number 
                  v-model="examQuestion.question.score" 
                  :min="0.5" :max="20" 
                  size="small"
                  @input="updateScore(index)"
                ></el-input-number>分
              </template>
              <template v-if="examQuestion.question.type === 2">
                ，漏选
                <el-input-number 
                   v-model='examQuestion.question.answerScores[0]' 
                  :min="0.5" :max="20" 
                  size="small"
                  @input="updateScore(index)"
                ></el-input-number>分
              </template>
              <template v-if="examQuestion.question.type === 3 || (examQuestion.question.type === 5 && examQuestion.question.markType === 1)">
                <div style="display: inline;"
                    v-for="(answerScore, index1) of examQuestion.question.answerScores"
                    :key="`answerScores${index1}`"
                  > 
                    第{{$tools.intToChinese(index1 + 1)}}{{examQuestion.question.type === 3 ? '空' : '关键词'}}
                    <el-input-number 
                      v-model='examQuestion.question.answerScores[index1]' 
                      :min="0.5" :max="20" 
                      size="small"
                      @input="updateScore(index)"
                    ></el-input-number>分，
                </div>
              </template>
            </div>
          </template>
        </Question>
      </div>
      <div style="height:50px"></div>
    </el-scrollbar>

    <!-- 题库 -->
    <el-drawer
      title="题库"
      :visible.sync="showQuestionList"
      direction="rtl"
      size="700px"
      :modal-append-to-body="false"
      :modal="false"
    >
      <!-- 试题查询 -->
      <el-form :inline="true" :model="queryForm" class="form-inline" size="small">
        <el-row type="flex" justify="space-between">
          <el-col :span="22">
            <el-form-item label>
              <el-input v-model="queryForm.questionTypeName" placeholder="题库名称" />
            </el-form-item>
            <el-form-item label>
              <el-input v-model="queryForm.id" placeholder="编号" />
            </el-form-item>
            <el-form-item label>
              <el-input v-model="queryForm.title" placeholder="题干" />
            </el-form-item>
            <el-form-item label>
              <el-select v-model="queryForm.type" clearable placeholder="类型">
                <el-option
                  v-for="dict in $tools.getDicts('QUESTION_TYPE')"
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                />
              </el-select>
            </el-form-item>
            <el-form-item label>
              <el-select v-model="queryForm.markType" clearable placeholder="阅卷类型">
                <el-option
                  v-for="dict in $tools.getDicts('PAPER_MARK_TYPE')"
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                />
              </el-select>
            </el-form-item>
            <el-form-item label>
              <el-dropdown split-button type="primary" @click="query(1)" size="small" @command="queryCmd">
                查询
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="randAdd">随机添加10道</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 试题列表 -->
      <div class="content">
        <QuestionList :list="queryList.list" :showMode="'simple'">
          <template #question-btn="{ question }">
            <el-button
              class="btn"
              size="mini"
              type="primary"
              icon="el-icon-back"
              @click="questionImport(question)"
            >
              导入
            </el-button>
          </template>
          <template #question-bottom>
            <div class="footer">
              <el-pagination
                background
                layout="prev, pager, next"
                prev-text="上一页"
                next-text="下一页"
                hide-on-single-page
                :total="queryList.total"
                :page-size="queryList.pageSize"
                :current-page="queryList.curPage"
                @current-change="query"
              />
            </div>
          </template>
        </QuestionList>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import Draggable from 'vuedraggable'
import Question from '@/components/Question/Question.vue'
import { questionListpage } from 'api/question'
import QuestionList from '@/components/Question/QuestionList.vue'
import { mapMutations } from 'vuex'
export default {
  components: {
    Draggable,
    Question,
    QuestionList,
  },
  data() {
    return {
      totalScore: 0, //总分数
      showQuestionList: false, // 显示题库
      queryForm: {
        questionTypeName: null, // 题库
        id: null, // 主键
        title: null, // 标题
        type: null, // 试题类型
        markType: null, // 阅卷类型
      },
      queryList: {
        curPage: 1, // 当前第几页
        pageSize: 5, // 每页多少条
        total: 0, // 总条数
        list: [], // 数据列表
      },
      
    }
  },
  computed: {
    examQuestions: {
      get: function () {
        return this.$store.state.exam.examQuestions
      },
      set: function (newValue) {
        this.$store.state.exam.examQuestions = newValue
      }
    },
    paperName: {
      get: function () {
        return this.$store.state.exam.paperName
      },
      set: function (newValue) {
        this.$store.state.exam.paperName = newValue
      }
    },
  },
  watch: {
    // 如果是填空题或客观问答题，分数为各子项的分数总和
    'examQuestions': {
      deep: true,
      handler(n) {
        
      }
    },
  },
  methods: {
    ...mapMutations('exam', [
      'updatePaperName',
      'addChapter',
      'addQuestion',
    ]),
    toEditor() {
      this.$emit('toEditor')
    },
    _addChapter() {
      this.addChapter({'name':'', 'txt':''})
    },
    // 跳转到对应的试题
    toHref(index) {
      document.querySelector(`#p-${index}`).scrollIntoView({ block: 'end', inline: 'nearest' })
    },
    // 
    toAdd() {
      this.showQuestionList = true
      this.query(1)
    },
    // 试题查询
    async query(curPage) {
      if (curPage) {
        this.queryList.curPage = curPage
      }

      let exIds = []
      this.examQuestions.forEach(examQuestions => {
        if (examQuestions.type === 2) {
          exIds.push(paper.question.id)
        }
      });
      
      let {data: { list, total }} = await questionListpage({
        ...this.queryForm,
        exIds: exIds.join(),
        curPage: this.queryList.curPage, 
        pageSize: this.queryList.pageSize
      })

      this.queryList.total = total
      this.queryList.list = list
    },
    // 查询命令
    async queryCmd(cmd) {
      if (cmd === 'randAdd') {
        let exIds = []
        this.examQuestions.forEach(examQuestion => {
          if (examQuestion.type === 2) {
            exIds.push(examQuestion.question.id)
          }
        });
        
        let {data: { list, total }} = await questionListpage({
          ...this.queryForm,
          exIds: exIds.join(),
          rand: true,
          curPage: 1, 
          pageSize: 10
        })

        list.forEach(question => {
          this.addQuestion(question)
        })

        this.query(1)
      }
    },
    questionImport(question) {
      this.addQuestion(question)

      this.queryList.list = this.queryList.list.filter(cur => cur != question) // 添加过的从当前列表删除
      if (!this.queryList.list.length) {// 如果当前页都删完了
          this.query(1)// 重新查询
      }

    },
    // 重置试卷
    questionClear() {
      this.examQuestions.splice(0)
    },
    // 更新分数
    updateScore(index) {
      console.log(index)
      let no = 1
      this.totalScore = 0
      let examQuestion = this.examQuestions[index]
      if (examQuestion.type === 2) {// 如果是试题
        if (!examQuestion.question.score) { // 分数没有初始化成1（分数范围通过组件限制了，这里不用管）
          examQuestion.question.score = 1
        }

        examQuestion.question.answerScores.forEach((answerScore, index1) => {// 子分数没有初始化成1
          if (!answerScore) {
            this.$set(examQuestion.question.answerScores, index1, 1)
          }
        })

        if (examQuestion.question.type === 2) {// 多选题漏选超出范围，恢复为分数一半
          if (examQuestion.question.score <= examQuestion.question.answerScores[0]) {
            console.log('da:',examQuestion.question, examQuestion.question.answerScores,"&&&", examQuestion.question.score / 2)
            this.$set(examQuestion.question.answerScores, 0, examQuestion.question.score / 2)
            console.log(111111111111,examQuestion.question.answerScores)
          }
        }

        if (examQuestion.question.type === 3 // 填空或客观问答题，分数为子分数累加
          || (examQuestion.question.type === 5 && examQuestion.question.markType === 1)) {
            examQuestion.question.score = examQuestion.question.answerScores.reduce((pre, cur) => pre + cur)
        }

        examQuestion.question.no = no++ // 页面显示序号，排除章节
        this.totalScore += examQuestion.question.score

        this.$set(this.examQuestions, index, examQuestion)
        // this.examQuestions[index] = examQuestion
      }
    }
  },
}
</script>

<style lang="scss" scoped>
.content-center :hover{
  padding-bottom: 0px;
  .question-opt {
    display: block;
  }
}
.question-opt {
  color: #557587;
  height: 32px;
  margin-top: 10px;
  display: none;
  /deep/ .el-input-number--small .el-input-number__increase, /deep/ .el-input-number--small .el-input-number__decrease{
    display: none;
    width: 50px;
  }
  /deep/ .el-input--small .el-input__inner {
    border: none;
    border-radius: 0;
    border-bottom: 1px solid #557587;
  }
  /deep/ .el-input--small .el-input__inner {
    width: 50px;
    padding-left: 0;
    padding-right: 0;
  }
}
.paper-router {
  width: 240px;
  background: #fff;
  position: relative;
  padding: 15px;
  /deep/ .el-divider__text.is-center {
    color: #596a76;
  }
  .total-score {
    background: #0094e5;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #fff;
    text-align: center;
    position: absolute;
    top: 40px;
    left: 0;
    z-index: 100;
  }
}
.router-content {
  padding: 10px;
  .router-title {
    line-height: 40px;
    font-size: 14px;
    font-weight: 600;
    color: #0c2e41;
  }

  .router-index {
    position: relative;
    width: 28px;
    height: 28px;
    color: #41baff;
    line-height: 28px;
    font-weight: 600;
    background: #e3f4fe;
    text-align: center;
    display: inline-block;
    margin-bottom: 10px;
    margin-right: 10px;
    border-radius: 3px;
    text-decoration: none;
    border-radius: 6px;
    cursor: pointer;
    user-select: none;
    &:nth-child(10n) {
      margin-right: 0;
    }
  }

  .router-active,
  a:hover {
    background: #0094e4;
    // border: 1px solid #7fc2f6;
    color: #fff;
  }
}
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 240px);
  margin: 0 auto;
}
.paper-handler {
  width: 400px;
  background: #fff;
  position: relative;
  border-radius: 8px;
  .paper-title {
    line-height: 40px;
    background: #fff;
    width: 100%;
    height: 40px;
    font-size: 16px;
    color: #333;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    font-weight: 600;
    padding: 0 20px;
    border-radius: 8px 8px 0 0;
    border-bottom: 1px solid #eee;
    &::before {
      content: '';
      display: inline-block;
      position: relative;
      top: 5px;
      left: -10px;
      width: 2px;
      height: 20px;
      background: #0094e5;
    }
  }
}

.top {
  background: #fff;
  width: calc(1200px - 410px);
  height: 80px;
  padding: 30px 10px 10px 10px;
  color: #333;
  z-index: 100;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  border-radius: 8px 8px 0 0;
  .top-handler {
    display: none;
    position: absolute;
    right: 10px;
    top: 10px;
    /deep/ .el-button {
      padding: 5px 10px;
    }
  }
  .top-title {
    flex: 1;
    text-align: center;
    font-size: 20px;
    vertical-align: middle;
    /deep/ .el-input__inner {
      text-align: center;
      border: 0;
      font-size: 20px;
      font-weight: bold;
    }
  }
}
/deep/ .el-drawer__header {
  margin-bottom: 0px;
}
.handler-content {
  padding: 10px 10px 10px;
}
.chapter {
  padding: 15px 0px 0px 17px;
  /deep/ .el-input__inner {
    border: none;
    font-size: 18px;
  }
  /deep/ .el-textarea__inner {
    border: none;
    color: #557587;
  }
}
</style>
<style lang="scss">
.paper-content {
  background: #fff;
  width: calc(100% - 400px);
  margin-left: 10px;
  border-radius: 8px;
  &:hover {
    .top {
      .top-handler {
        display: inline-block;
      }
    }
  }
}


</style>