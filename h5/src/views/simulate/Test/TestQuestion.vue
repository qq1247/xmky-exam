<template>
  <div class="content-center">
    <div class="children-content" v-if="!isRecite">
      <!-- 标题 -->
      <div class="question-title" v-if="questionDetail.type !== 3">
        <div>{{ questionDetail.id }}、</div>
        <div v-html="`${questionDetail.title}`"></div>
      </div>
      <!-- <div class="question-title" v-else>
        <span>{{ questionDetail.id }}、</span>
        <ClozeTitle
          :title="questionDetail.title"
          :finish="finish"
          :paperQuestion="paperQuestion"
          :myExamDetailCache="myExamDetailCache"
          :questionId="routerIndex"
        ></ClozeTitle>
      </div> -->

      <!-- 单选 -->
      <el-radio-group
        class="children-option"
        v-if="questionDetail.type === 1"
        @change="checkAnswer"
        v-model="questionDetail.selected"
      >
        <el-radio
          class="option-item"
          :disabled="questionDetail.finish ? true : false"
          :label="String.fromCharCode(65 + index)"
          v-for="(option, index) in questionDetail.options"
          :key="index"
        >
          <div
            class="flex-items-center"
            v-html="`${String.fromCharCode(65 + index)}、${option}`"
          ></div>
        </el-radio>
      </el-radio-group>

      <!-- 多选 -->
      <el-checkbox-group
        class="children-option"
        v-if="questionDetail.type === 2"
        @change="checkAnswer"
        v-model="questionDetail.selected"
      >
        <el-checkbox
          :key="index"
          class="option-item"
          :label="String.fromCharCode(65 + index)"
          :disabled="questionDetail.finish ? true : false"
          v-for="(option, index) in questionDetail.options"
        >
          <div
            class="flex-items-center"
            v-html="`${String.fromCharCode(65 + index)}、${option}`"
          ></div>
        </el-checkbox>
      </el-checkbox-group>

      <!-- 判断 -->
      <el-radio-group
        class="children-option"
        v-if="questionDetail.type === 4"
        @change="checkAnswer"
        v-model="questionDetail.selected"
      >
        <el-radio
          :key="index"
          :label="option"
          class="option-item"
          v-for="(option, index) in ['对', '错']"
          :disabled="questionDetail.finish ? true : false"
          >{{ option }}</el-radio
        >
      </el-radio-group>

      <!-- 问答 -->
      <!-- <template v-if="questionDetail.type === 5">
        <el-input
          :rows="2"
          type="textarea"
          class="question-text"
          @change="checkAnswer"
          placeholder="请输入内容"
          v-if="myExamDetailCache[questionDetail.id]"
          :disabled="finish === 'true' ? true : false"
          v-model="myExamDetailCache[questionDetail.id].answers[0]"
        ></el-input>
      </template> -->
    </div>

    <div class="children-content" v-else>
      <!-- 标题 -->
      <div class="question-title" v-if="questionDetail.type !== 3">
        <div>{{ questionDetail.id }}、</div>
        <div v-html="`${questionDetail.title}`"></div>
      </div>
      <!-- <div class="question-title" v-else>
        <span>{{ questionDetail.id }}、</span>
        <ClozeTitle
          :title="questionDetail.title"
          :finish="finish"
          :paperQuestion="paperQuestion"
          :myExamDetailCache="myExamDetailCache"
          :questionId="routerIndex"
        ></ClozeTitle>
      </div> -->

      <!-- 单选 -->
      <el-radio-group
        class="children-option"
        v-if="questionDetail.type === 1"
        v-model="questionDetail.answers[0].answer"
      >
        <el-radio
          class="option-item"
          :disabled="isRecite"
          :label="String.fromCharCode(65 + index)"
          v-for="(option, index) in questionDetail.options"
          :key="index"
        >
          <div
            class="flex-items-center"
            v-html="`${String.fromCharCode(65 + index)}、${option}`"
          ></div>
        </el-radio>
      </el-radio-group>

      <!-- 多选 -->
      <el-checkbox-group
        class="children-option"
        v-if="questionDetail.type === 2"
        v-model="questionDetail.answers[0].answer"
      >
        <el-checkbox
          :key="index"
          class="option-item"
          :label="String.fromCharCode(65 + index)"
          :disabled="isRecite"
          v-for="(option, index) in questionDetail.options"
        >
          <div
            class="flex-items-center"
            v-html="`${String.fromCharCode(65 + index)}、${option}`"
          ></div>
        </el-checkbox>
      </el-checkbox-group>

      <!-- 判断 -->
      <el-radio-group
        class="children-option"
        v-if="questionDetail.type === 4"
        v-model="questionDetail.answers[0].answer"
      >
        <el-radio
          :key="index"
          :label="option"
          class="option-item"
          v-for="(option, index) in ['对', '错']"
          :disabled="isRecite"
          >{{ option }}</el-radio
        >
      </el-radio-group>

      <!-- 问答 -->
      <!-- <template v-if="questionDetail.type === 5">
        <el-input
          :rows="2"
          type="textarea"
          class="question-text"
          @change="checkAnswer"
          placeholder="请输入内容"
          v-if="myExamDetailCache[questionDetail.id]"
          :disabled="finish === 'true' ? true : false"
          v-model="myExamDetailCache[questionDetail.id].answers[0]"
        ></el-input>
      </template> -->
    </div>

    <el-row class="test-handler">
      <el-col :span="5">
        <el-button-group>
          <el-button
            size="small"
            type="primary"
            icon="el-icon-arrow-left"
            @click="prevQuestion"
            >上一题</el-button
          >
          <el-button size="small" type="primary" @click="nextQuestion"
            >下一题<i class="el-icon-arrow-right el-icon--right"></i
          ></el-button>
        </el-button-group>
      </el-col>
      <el-col :span="7"
        ><el-checkbox v-model="isRecite"
          ><div class="flex-items-center">背题模式</div>
        </el-checkbox>
        <el-checkbox v-model="isRandom" @change="(e) => $emit('randomTest', e)"
          ><div class="flex-items-center">随机模拟</div></el-checkbox
        ></el-col
      >
      <el-col :span="5" :offset="7"
        ><el-button-group>
          <el-button
            size="small"
            type="primary"
            icon="el-icon-chat-line-round"
            @click="analysisDetail = !analysisDetail"
            >{{ analysisDetail ? '收起' : '显示' }}解析</el-button
          >
          <el-button
            size="small"
            type="primary"
            icon="el-icon-chat-dot-round"
            @click="showComment"
            >{{ commentDetail ? '收起' : '显示' }}评论</el-button
          >
        </el-button-group></el-col
      >
    </el-row>

    <div v-if="analysisDetail" class="question-analysis">
      【解析】：<span v-html="questionDetail.analysis"></span>
    </div>

    <CommentText
      v-if="commentDetail && commentState === 2"
      @comment="commentReply"
      :key="questionDetail.id"
    ></CommentText>
    <div class="question-comments">
      <QuestionComment
        :list="commentList"
        v-if="commentDetail && commentList.length && commentState !== 0"
        :commentState="commentState"
        @showMore="showMore"
        @childrenComment="commentReply"
        @getChildrenComment="getChildrenComment"
      ></QuestionComment>
    </div>
  </div>
</template>

<script>
import { questionCommentAdd, questionCommentListPage } from 'api/question'
import QuestionComment from 'components/QuestionComment/Index.vue'
import CommentText from 'components/QuestionComment/CommentText.vue'
export default {
  components: {
    CommentText,
    QuestionComment,
  },
  props: {
    questionDetail: {
      type: Object,
      default: () => {},
    },
    commentState: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      curPage: 1,
      pageSize: 10,
      totalPage: 0,
      commentList: [],
      isRecite: false,
      isRandom: false,
      commentDetail: false,
      analysisDetail: false,
    }
  },
  methods: {
    // 校验答案
    checkAnswer() {
      let flag

      // 单选、判断
      if ([1, 4].includes(this.questionDetail.type)) {
        flag =
          this.questionDetail.selected === this.questionDetail.answers[0].answer
        this.questionDetail.finish = true
      }

      // 多选
      if (this.questionDetail.type === 2) {
        // 正确答案数组
        const answers = this.questionDetail.answers[0].answer.reduce(
          (acc, cur) => {
            acc.push(cur)
            return acc
          },
          []
        )
        // 所选项与正确答案是否一致
        if (this.questionDetail.selected.length !== answers.length) {
          const _flag = this.questionDetail.selected.some((item) =>
            answers.includes(item)
          )
          if (!_flag) {
            flag = false
            this.questionDetail.finish = true
          }
        } else {
          const _flag = this.questionDetail.selected.every((item) =>
            answers.includes(item)
          )
          flag = _flag ? true : false
          this.questionDetail.finish = true
        }
      }

      if (flag) {
        this.nextQuestion()
      }
    },
    // 上一题
    prevQuestion() {
      this.$emit('prevQuestion')
    },
    // 下一题
    nextQuestion() {
      this.$emit('nextQuestion')
    },
    // 获取试题评论
    async questionComment(id) {
      const commentList = await questionCommentListPage({
        questionId: this.questionDetail.id,
        curPage: this.curPage,
        pageSize: this.pageSize,
        parentId: id || null,
      })
      const totalPage =
        commentList.data.total % this.pageSize === 0
          ? commentList.data.total / this.pageSize
          : Math.ceil(commentList.data.total / this.pageSize)
      const moreComment = commentList.data.list.reduce((acc, cur) => {
        const params = {
          id: cur.id,
          curPage: 1,
          content: cur.content,
          time: cur.createTime,
          name: cur.createUserName || '匿名用户',
          avatar:
            'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        }
        acc.push(id ? params : { ...params, replay: false, children: [] })
        return acc
      }, [])
      return { totalPage, moreComment }
    },
    // 试题一级评论
    async getQuestionComment() {
      const { totalPage, moreComment } = await this.questionComment()
      this.totalPage = totalPage
      this.commentList = [...this.commentList, ...moreComment]
    },
    // 获取二级回复列表
    async getChildrenComment(id) {
      const { totalPage, moreComment } = await this.questionComment(id)
      if (!moreComment.length) {
        this.$message({
          message: '没有更多了',
          duration: 1000,
        })
        return
      }
      this.commentList.filter((item, index) => {
        if (item.id === id) {
          this.$set(this.commentList[index], 'totalPage', totalPage)
          this.commentList[index].children = [
            ...this.commentList[index].children,
            ...moreComment,
          ]
          return true
        }
      })
    },
    // 评论回复
    async commentReply(text, anonymity, id) {
      if (!text.trim()) {
        this.$message.warning('请填写评论内容！')
        return
      }

      const res = await questionCommentAdd({
        questionId: this.questionDetail.id,
        content: text,
        anonymity: anonymity ? 0 : 1,
        parentId: id || null,
      })

      if (res?.code === 200) {
        this.commentList = []
        await this.getQuestionComment()
        id && this.getChildrenComment(id)
      }
    },
    // 查看更多一级回复
    async showMore(id, index) {
      if (index) {
        // 二级回复查看更多
        if (
          this.commentList[index].curPage === this.commentList[index].totalPage
        ) {
          this.$message('没有更多了')
          return
        }
        this.commentList[index].curPage += 1
        const { totalPage, moreComment } = await this.questionComment(id)
        this.$set(this.commentList[index], 'totalPage', totalPage)
        this.commentList[index].children = [
          ...this.commentList[index].children,
          ...moreComment,
        ]
      } else {
        // 一级回复查看更多
        if (this.curPage === this.totalPage) {
          this.$message('没有更多了')
          return
        }
        this.curPage += 1
        const { totalPage, moreComment } = await this.questionComment()
        this.totalPage = totalPage
        this.commentList = [...this.commentList, ...moreComment]
      }
    },
    // 展示评论
    showComment() {
      this.commentDetail = !this.commentDetail
      if (this.commentDetail) {
        this.commentList = []
        this.getQuestionComment()
      }
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
.content-center {
  margin-left: 220px;
  padding: 0;
}
.children-content {
  height: 400px;
}
.test-handler {
  padding: 10px;
  border-top: 10px solid #f7f8f9;
  border-bottom: 10px solid #f7f8f9;
}
.question-analysis {
  display: flex;
  padding: 10px;
  background: #fff;
  border-bottom: 10px solid #f7f8f9;
}
</style>
