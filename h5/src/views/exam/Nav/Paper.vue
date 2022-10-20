<template>
  <div class="content">
    <!-- 答题卡 -->
    <div class="paper-router">
      <el-divider>答题卡</el-divider>
      <div class="router-content">
        <Draggable
          v-model="examInfo.examQuestions"
          tag="div"
          group="questionGroup"
          chosen-class="drag-question-active"
          animation="300"
          @change="questionSort"
        >
          <div
            v-for="(examQuestion, index) in examInfo.examQuestions"
            :key="index"
            :style="{'display': examQuestion.type === 1 ? 'block' : 'inline-block'}"
          >
            <div v-if="examQuestion.type === 1" class="router-title">
              {{examQuestion.chapterName}}
            </div>
            <div v-else>
              <a :class="['router-index']" @click="toHref(examQuestion.question.no)">
                {{examQuestion.question.no}} 
              </a>
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
          :value="examInfo.exam.paperName"
          @input="value => paperNameUpdate(value)"
          placeholder="请输入试卷名称"
          maxlength="32"
          ></el-input>
          <div class="top-score">
            {{totalScore}}分
            <i class="common common-fenshudixian fenshudixian"></i>
          </div>
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
          <el-button icon="el-icon-plus" size="mini" @click="_chapterAdd" round>章节添加</el-button>
          <el-button icon="el-icon-delete" size="mini" round @click="paperClear">试卷重置</el-button>
        </div>
      </div>
      <div class="content-center"
        v-for="(examQuestion, index) of examInfo.examQuestions"
        :key="index"
      >
        <div v-if="examQuestion.type === 1" class="chapter">
          <el-input
            :value="examQuestion.chapterName"
            @input="(value) => chapterNameUpdate({ index, value})"
            placeholder="请输入名称"
            maxlength="16"
          />
          <el-input
            :value="examQuestion.chapterTxt"
            @input="(value) => chapterTxtUpdate({ index, value})"
            :rows="2"
            placeholder="请输入描述"
            type="textarea"
            :autosize="true"
            maxlength="64"
          />
          <div class="question-opt">
            <el-button
              class="btn"
              style="float:right; padding: 5px 10px; margin-right: 20px;"
              icon="el-icon-close"
              round
              size="mini"
              type="danger"
              @click="chapterDel(index)"
            >删除</el-button>
          </div>
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
                  :value="examQuestion.question.score"
                  :min="0.5" :max="20" 
                  size="small"
                  @change="(value) => _questionScoreUpdate(index, value)"
                ></el-input-number>分
              </template>
              <template v-if="examQuestion.question.type === 2">
                ，漏选
                <el-input-number 
                  :value="examQuestion.question.answerScores[0]"
                  :min="0" :max="20" 
                  size="small"
                  @change="(value) => _questionAnswerScoreUpdate(index, 0, value)"
                ></el-input-number>分
              </template>
              <template v-if="examQuestion.question.type === 3 || (examQuestion.question.type === 5 && examQuestion.question.markType === 1)">
                <div style="display: inline;"
                    v-for="(answerScore, index1) of examQuestion.question.answerScores"
                    :key="`answerScores${index1}`"
                  > 
                    第{{$tools.intToChinese(index1 + 1)}}{{examQuestion.question.type === 3 ? '空' : '关键词'}}
                    <el-input-number 
                      :value="examQuestion.question.answerScores[index1]"
                      :min="0.5" :max="10" 
                      size="small"
                      @change="(value) => _questionAnswerScoreUpdate(index, index1, value)"
                    ></el-input-number>分，
                </div>
              </template>
              <el-button
                class="btn"
                style="float: right;"
                icon="el-icon-close"
                round
                size="mini"
                type="danger"
                @click="questionDel(index)"
              >删除</el-button>
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
import { mapState } from 'vuex'
import { mapGetters } from 'vuex'
import { mapMutations } from 'vuex'
export default {
  components: {
    Draggable,
    Question,
    QuestionList,
  },
  data() {
    return {
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
    ...mapState('exam', [
      'examInfo',
    ]),
    ...mapGetters('exam',[
      'totalScore',
    ])
  },
  methods: {
    ...mapMutations('exam', [
      'paperNameUpdate',
      'paperClear',
      'chapterNameUpdate',
      'chapterTxtUpdate',
      'chapterAdd',
      'chapterDel',
      'questionAdd',
      'questionDel',
      'questionSort',
      'questionScoreUpdate',
      'questionAnswerScoreUpdate',
    ]),
    toEditor() {
      this.$emit('toEditor')
    },
    // 章节添加
    _chapterAdd() {
      this.chapterAdd({'name':'点击这里输入章节名称', 'txt':''})
      this.$nextTick(function() {//等待dom变化后在滚动
        let contentCenters = document.getElementsByClassName('content-center')// 滚动到底部
        contentCenters[contentCenters.length - 1].scrollIntoView(false)
      })
    },
    // 跳转到对应的试题
    toHref(index) {
      document.getElementById(`question-${index}`).parentElement.parentElement.scrollIntoView()
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

      let exQuestionIds = []
      this.examInfo.examQuestions.forEach(examQuestion => {
        if (examQuestion.type === 2 && examQuestion.question.id) {
          exQuestionIds.push(examQuestion.question.id)
        }
      });
      
      let {data: { list, total }} = await questionListpage({
        ...this.queryForm,
        exIds: exQuestionIds.join(),
        curPage: this.queryList.curPage, 
        pageSize: this.queryList.pageSize
      })

      this.queryList.total = total
      this.queryList.list = list
    },
    // 查询命令
    async queryCmd(cmd) {
      // if (cmd === 'randAdd') {
      //   let exQuestionIds = []
      //   this.examQuestions.forEach(examQuestion => {
      //     if (examQuestion.type === 2) {
      //       exQuestionIds.push(examQuestion.question.id)
      //     }
      //   });
        
      //   let {data: { list, total }} = await questionListpage({
      //     ...this.queryForm,
      //     exQuestionIds: exQuestionIds.join(),
      //     rand: true,
      //     curPage: 1, 
      //     pageSize: 10
      //   })

      //   list.forEach(question => {
      //     this.questionAdd(question)
      //   })

      //   this.query(1)
      // }
    },
    // 试题导入
    questionImport(question) {
      this.questionAdd(question)
      this.questionSort()

      this.queryList.list = this.queryList.list.filter(cur => cur != question) // 添加过的从当前列表删除
      if (!this.queryList.list.length) {// 如果当前页都删完了
          this.query(1)// 重新查询
      }
    },
    // 试题分数更新
    async _questionScoreUpdate(index, value) {
      let that = this
      that.questionScoreUpdate({index, value})
      if (!value) {
        value = 1
        await that.$nextTick()
        that.questionScoreUpdate({index, value})
      }

      let question = that.examInfo.examQuestions[index].question
      if (question.type === 2) {// 多选题漏选超出范围，恢复为分数一半
        if (question.score <= question.answerScores[0]) {
          that.questionAnswerScoreUpdate({index, answerIndex : 0, value : value / 2})
        }
      }
    },
    // 试题子分数修改
    async _questionAnswerScoreUpdate(index, answerIndex, value) {
      let that = this
      that.questionAnswerScoreUpdate({index, answerIndex, value})
      if (!value) {
        value = 1
        await that.$nextTick()
        that.questionAnswerScoreUpdate({index, answerIndex, value})
      }

      let question = that.examInfo.examQuestions[index].question
      if (question.type === 2) {// 多选题漏选超出范围，恢复为分数一半
        if (question.score <= question.answerScores[0]) {// bug：分数2，漏选分数1。漏输入2，恢复成1，漏输入2，不变更
          await that.$nextTick() // questionAnswerScoreUpdate同时调用两次，最后一次生效，第二次因为模型还是原值，不触发dom更新
          that.questionAnswerScoreUpdate({index, answerIndex : 0, value : question.score / 2})// 加nextTick处理
        }
      } else if (question.type === 3 // 填空或客观问答题，分数为子分数累加
        || (question.type === 5 && question.markType === 1)) {
        that.questionScoreUpdate({
          index, 
          value: question.answerScores.reduce((pre, cur) => pre + cur)})
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
.btn:hover {
  padding-bottom: 5px;
} 
.question-opt {
  color: #557587;
  height: 32px;
  margin-top: 10px;
  display: none;
  /deep/ .el-input {
    width: 50px;
  }
  /deep/ .el-input-number--small {
    width: 50px;
  }
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
    z-index: 1;// 滚动试题时不能点击
  }
  .top-title {
    flex: 1;
    text-align: center;
    font-size: 20px;
    vertical-align: middle;
    .top-score {
      position: relative;
      display: flex;
      float: right;
      font-size: 20px;
      flex-direction: column;
      top: -26px;
      right: 28px;
      z-index: 1;
      color: #FF5722;
    }
    .fenshudixian {
      font-size: 53px;
      margin-top: -25px;
      margin-right: -4px;
    }
    
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