<template>
  <div class="container">
    <template v-if="hashChildren">
      <!-- 搜索 -->
      <el-form :inline="true" :model="queryForm" class="form-inline search">
        <div>
          <el-form-item>
            <el-input
              v-model="queryForm.examName"
              placeholder="请输入考试名称"
              class="query-input"
            />
          </el-form-item>
        </div>
        <el-form-item>
          <el-button icon="el-icon-search" type="primary" @click="search">查询</el-button>
        </el-form-item>
      </el-form>
      <!-- 内容 -->
      <div class="content">
        <div class="exam-list">
          <div 
            v-for="(myMark, index) in myMarkList"
            :key="index"
            class="exam-item">
            <div class="exam-content">
              <div class="tag-group">
                <el-tag
                  size="mini"
                  :type="myMark.examMarkState === 3 ? '' : 'danger'">
                  {{ $tools.getDictValue('MARK_STATE', myMark.examMarkState) }}
                </el-tag>
              </div>
              <!-- 标题 -->
              <div class="title ellipsis">{{ myMark.examName }}</div>
              <el-row>
                <el-col class="content-info">
                  阅卷时间：{{ myMark.examMarkStartTime }}（{{$tools.computeMinute(myMark.examMarkStartTime, myMark.examMarkEndTime)}}）
                </el-col>
                <el-col class="content-info">
                  分数：{{myMark.examPassScore}} / {{ myMark.examTotalScore }}
                </el-col>
              </el-row>
              <el-row class="content-info">
                <el-col :span="24">
                  <CountDown
                    :expireTime="getTime(myMark.examMarkStartTime)"
                    @endCallback="myMark.disabled = false"
                    :preTxt="'距离阅卷：'"
                  ></CountDown>
                </el-col>
              </el-row>
              <div class="handler">
                <span data-title="进入" @click="toMark(myMark)">
                  <i :class="['common', 'common-exam']" />
                </span>
              </div>
            </div>
          </div>
        </div>
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
    </template>
    <router-view v-else />
  </div>
</template>

<script>
import { myMarkListpage } from 'api/my'
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
      myMarkList: [],
    }
  },
  computed: {
    hashChildren() {
      return !(this.$route.matched.length > 2)
    },
  },
  mounted() {
    this.query()
  },
  methods: {
    getTime(date) {
      return dayjs(date, 'YYYY-MM-DD HH:mm:ss').toDate()
    },
    // 我的考试列表
    async query() {
      const myMarkList = await myMarkListpage({
        examName: this.queryForm.examName,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })

      this.myMarkList = myMarkList.data.list.map(myMark => {
        myMark.disabled = true
        return myMark
      })
      this.total = myMarkList.data.total
    },
    search() {
      this.curPage = 1
      this.query()
    },
    // 去阅卷
    toMark(myMark) {
      if (myMark.disabled) {
        this.$message.warning('阅卷未开始，请等待...')
        return
      }

      this.$router.push({
        name: 'MyMarkIndexUser',
        params: {
          examId: myMark.examId,
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
</style>
