<!--
 * @Description: 查看 | 评论试题
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-17 17:27:41
 * @LastEditors: Che
 * @LastEditTime: 2021-09-22 13:17:47
-->
<template>
  <div class="container">
    <template v-if="questionList.length">
      <div class="question-details">
        <QuestionDetail
          moreDetail
          :data="questionDetail"
          :type="typeList"
          :difficulty="difficultyList"
        ></QuestionDetail>
        <ReplyText @reply="questionReply"></ReplyText>
        <div class="question-pages">
          <el-button
            round
            type="primary"
            size="small"
            icon="el-icon-arrow-left"
            @click="prevQuestion"
            :disabled="currentIndex === 0"
            >上一题</el-button
          >
          <el-button
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
          @showMore="showMore"
          @childrenReply="childrenReply"
          @getChildrenReply="getChildrenReply"
          v-if="commentList.length"
        ></QuestionComment>
        <el-empty v-else description="暂无评论"> </el-empty>
      </div>
    </template>
    <el-empty v-else description="暂无试卷"> </el-empty>
  </div>
</template>

<script>
import { dictListPage } from 'api/base'
import {
  questionGet,
  questionListPage,
  questionCommentAdd,
  questionCommentListPage,
} from 'api/question'
import QuestionDetail from 'components/QuestionDetail/Index.vue'
import QuestionComment from 'components/QuestionComment/Index.vue'
import ReplyText from '@/components/QuestionComment/ReplyText.vue'
export default {
  components: {
    QuestionDetail,
    QuestionComment,
    ReplyText,
  },
  data() {
    return {
      id: null,
      curPage: 1,
      pageSize: 10,
      tatalPage: 0,
      typeList: [],
      currentIndex: 0,
      questionList: [],
      questionDetail: {},
      difficultyList: [],
      commentList: [],
    }
  },
  mounted() {
    const { id } = this.$route.query
    this.id = id
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
        this.questionComment().then((comment) => {
          this.totalPage =
            comment.data.total % this.pageSize === 0
              ? comment.data.total / this.pageSize
              : Math.ceil(comment.data.total / this.pageSize)
          const moreComment = comment.data.list.reduce((acc, cur) => {
            acc.push({
              id: cur.id,
              name: cur.createUserName || '匿名用户',
              time: cur.createTime,
              avatar:
                'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
              replay: false,
              content: cur.content,
              children: [],
            })
            return acc
          }, [])
          this.commentList = [...this.commentList, ...moreComment]
        })
      })
    },
    // 获取试题列表
    async query() {
      const {
        data: { list },
      } = await questionListPage({
        questionTypeId: this.id,
        curPage: 1,
        pageSize: 100,
      })
      this.questionList = list
    },
    // 获取试题详情
    async showDetails(id) {
      const res = await questionGet({ id })
      if (res?.code != 200) {
        this.$message.error('获取详情失败！请重试')
        this.questionDetail = {}
        return
      }

      this.questionDetail = res.data
    },
    // 获取试题评论
    async questionComment(id) {
      const commentList = await questionCommentListPage({
        questionId: this.questionList[this.currentIndex].id,
        curPage: this.curPage,
        pageSize: this.pageSize,
        parentId: id ? id : null,
      })
      return commentList
    },
    // 试题一级评论
    async oneLevelComment() {
      const commentList = await this.questionComment()
    },
    // 试题一级评论
    async twoLevelComment() {
      const commentList = await this.questionComment()
    },
    // 上一题
    prevQuestion() {
      this.commentList = []
      this.currentIndex -= 1
      this.showDetails(this.questionList[this.currentIndex].id)
    },
    // 下一题
    nextQuestion() {
      this.commentList = []
      this.currentIndex += 1
      this.showDetails(this.questionList[this.currentIndex].id)
    },
    // 获取子级
    getChildrenReply(id) {
      console.log(id)
    },
    // 试题回复
    questionReply(text, anonymity) {
      if (!text) {
        this.$message.warning('请填写评论内容！')
        return
      }

      questionCommentAdd({
        questionId: this.questionList[this.currentIndex].id,
        content: text,
        anonymity: anonymity ? 0 : 1,
      })
    },
    showMore() {},
    // 子级回复
    childrenReply(text, anonymity, id) {
      console.log(text, anonymity, id)
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
