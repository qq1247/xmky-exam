<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            v-model="queryForm.examName"
            placeholder="请输入考试名称"
            class="query-input"/>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button icon="el-icon-search" type="primary" @click="search">查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <template v-if="myExamList.length > 0">
        <div class="exam-list">
          <div 
            v-for="(myExam, index) in myExamList"
            :key="index" 
            class="exam-item">
            <div class="exam-content">
              <div class="tag-group">
                <el-tag
                  size="mini"
                  :type="myExam.state === 3 ? '' : 'danger'"
                  >{{ $tools.getDictValue('EXAM_STATE', myExam.state) }}</el-tag>
              </div>
              <!-- 标题 -->
              <div class="title ellipsis">{{ myExam.examName }}</div>
              <el-row>
                <el-col class="content-info">
                  考试时间：{{ myExam.examStartTime }}（{{$tools.computeMinute(myExam.examStartTime, myExam.examEndTime)}}）
                </el-col>
                <el-col>
                  <el-row class="content-info">
                    <el-col :span="8">
                      答题：{{$tools.computeMinute(myExam.answerStartTime,myExam.answerEndTime)}}
                    </el-col>
                    <el-col
                      :span="8"
                      :style="{
                        color: myExam.totalScore !== null && ((myExam.paperPassScore / 100) * myExam.paperTotalScore).toFixed() > myExam.totalScore ? 'red' : '',
                      }"
                      >分数：{{myExam.totalScore !== null ? myExam.totalScore : '-' }} / {{ myExam.paperTotalScore }}
                    </el-col>
                    <el-col :span="8">
                      排名：{{ myExam.no === null ? '-' : myExam.no }} / {{ myExam.userNum === null ? '-' : myExam.userNum }}
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
              <el-row class="content-info">
                <el-col :span="24">
                  <CountDown
                    :expireTime="getTime(myExam.examStartTime)"
                    @endCallback="myExam.disabled = false"
                    :preTxt="'距离考试：'"
                  ></CountDown>
                </el-col>
              </el-row>
              <div class="handler">
                <span
                  :data-title="进入"
                  @click="toExam(myExam)">
                  <i :class="['common', 'common-exam']" />
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-else description="暂无信息" />
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="total"
        :page-size="pageSize"
        :current-page="1"
        @current-change="pageChange"
      />
    </div>
  </div>
</template>

<script>
import { myExamListpage } from 'api/my'
import * as dayjs from 'dayjs'
import CountDown from '@/components/CountDown.vue'
export default {
  components: {
    CountDown,
  },
  data() {
    return {
      pageSize: 6,
      curPage: 1,
      total: 0,
      queryForm: {
        examName: '',
      },
      myExamList: [],
      stateIcon: ['', 'common-wait', 'common-examing', 'common-exam'],
    }
  },
  mounted() {
    this.query()
  },
  methods: {
    // 我的考试列表
    async query() {
      const myExamList = await myExamListpage({
        examName: this.queryForm.examName,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })

      this.myExamList = myExamList.data.list.map(myExam => {
        myExam.disabled = true
        return myExam
      })
      

      this.total = myExamList.data.total
    },
    search() {
      this.curPage = 1
      this.query()
    },
    getTime(date) {
      return dayjs(date, 'YYYY-MM-DD HH:mm:ss').toDate()
    },
    // 去考试
    toExam(myExam) {
      if (myExam.disabled) {
        this.$message.warning('考试未开始，请等待...')
        return
      }

      this.$router.push({
        name: 'MyExamPaper',
        params: {
          examId: myExam.examId,
        },
      })
    },
    // 分页切换
    pageChange(val) {
      this.curPage = val
      this.query()
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
.container {
  margin: 0 auto 20px;
}
</style>
