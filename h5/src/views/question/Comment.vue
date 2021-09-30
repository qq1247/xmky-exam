<!--
 * @Description: 查看 | 评论试题
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-17 17:27:41
 * @LastEditors: Che
 * @LastEditTime: 2021-09-29 17:08:59
-->
<template>
  <div class="container">
    <template v-if="questionList.length"
      ><div class="question-details">
        <QuestionDetail
          moreDetail
          :data="questionDetail"
          :type="typeList"
          :difficulty="difficultyList"
        ></QuestionDetail
        ><CommentText
          v-if="commentState === 2"
          @comment="commentReply"
          :key="questionList[currentIndex].id"
        ></CommentText>
        <div class="question-pages">
          <el-button
            round
            type="primary"
            size="small"
            icon="el-icon-arrow-left"
            @click="prevQuestion"
            :disabled="currentIndex === 0"
            >上一题</el-button
          ><el-button
            round
            type="primary"
            size="small"
            @click="nextQuestion"
            :disabled="currentIndex === questionList.length - 1"
            >下一题<i class="el-icon-arrow-right"></i
          ></el-button>
        </div>
      </div>
      <div class="question-comments">
        <QuestionComment
          :list="commentList"
          v-if="commentList.length && commentState !== 0"
          :commentState="commentState"
          @showMore="showMore"
          @childrenComment="commentReply"
          @getChildrenComment="getChildrenComment"
        ></QuestionComment
        ><el-empty v-else description="暂无评论"></el-empty></div></template
    ><el-empty v-else description="暂无试题"></el-empty>
  </div>
</template>

<script>
import { dictListPage } from 'api/base'
import {
  questionTypeOpenQuestionGet,
  questionTypeOpenQuestion,
  questionCommentAdd,
  questionCommentListPage,
} from 'api/question'
import QuestionDetail from 'components/QuestionDetail/Index.vue'
import QuestionComment from 'components/QuestionComment/Index.vue'
import CommentText from '@/components/QuestionComment/CommentText.vue'
export default {
  components: {
    CommentText,
    QuestionDetail,
    QuestionComment,
  },
  data() {
    return {
      id: null,
      curPage: 1,
      pageSize: 10,
      totalPage: 0,
      typeList: [],
      commentState: 0,
      currentIndex: 0,
      questionList: [],
      questionDetail: {},
      difficultyList: [],
      commentList: [],
    }
  },
  mounted() {
    const { id, commentState } = this.$route.query
    this.id = id
    this.commentState = Number(commentState)
    this.init()
  },
  methods: {
    async init() {
      const typeDictData = await dictListPage({
        dictIndex: 'QUESTION_TYPE',
      })

      this.typeList = typeDictData.data.list // 初始化类型下拉框

      const difficultyDictData = await dictListPage({
        dictIndex: 'QUESTION_DIFFICULTY',
      })

      this.difficultyList = difficultyDictData.data.list // 初始化难度下拉框

      this.query().then((res) => {
        this.showDetails(this.questionList[this.currentIndex].id)
        if (this.commentState !== 0) {
          this.getQuestionComment()
        }
      })
    },
    // 获取试题列表
    async query() {
      const {
        data: { list },
      } = await questionTypeOpenQuestion({
        questionTypeId: this.id,
        curPage: 1,
        pageSize: 100,
        state: 1,
      })
      this.questionList = list
    },
    // 获取试题详情
    async showDetails(id) {
      const res = await questionTypeOpenQuestionGet({ questionId: id })
      if (res?.code != 200) {
        this.$message.error('获取详情失败！请重试')
        this.questionDetail = {}
        return
      }

      this.questionDetail = res.data
    },
    // 上一题
    prevQuestion() {
      this.currentIndex -= 1
      this.changeQuestion()
    },
    // 下一题
    nextQuestion() {
      this.currentIndex += 1
      this.changeQuestion()
    },
    changeQuestion() {
      this.curPage = 1
      this.totalPage = 0
      this.commentList = []
      this.showDetails(this.questionList[this.currentIndex].id)
      this.getQuestionComment()
    },
    // 获取试题评论
    async questionComment(id) {
      const commentList = await questionCommentListPage({
        questionId: this.questionList[this.currentIndex].id,
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
          type: 'warning',
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
        questionId: this.questionList[this.currentIndex].id,
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
  },
}
</script>

<style lang="scss" scoped>
.question-details {
  background: #fff;
  padding: 20px 30px;
  position: relative;
  .question-detail {
    padding: 0;
  }
  .question-pages {
    position: absolute;
    top: 20px;
    right: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
.question-comments {
  background: #f2f2f5;
  margin-top: 20px;
  padding: 20px;
}
</style>
