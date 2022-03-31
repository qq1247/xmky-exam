<template>
  <div class="home-content">
    <div class="home-calendar">
      <Calendar
        v-model="now"
        :timePopovers="timePopovers"
        @selectDate="selectDate"
      ></Calendar>
    </div>
    <div class="home-list">
      <!-- 普通用户 -->
      <template v-if="onlyRole.includes('user')">
        <!-- 待考列表 -->
        <template v-if="examList.length">
          <div
            class="info-item today-item"
            :style="{
              display: isExam(item.state) ? 'flex' : 'none',
            }"
            :key="item.id"
            v-for="item in examList"
          >
            <template v-if="isExam(item.state)">
              <i class="common common-wait-exam today-icon"></i>
              <div class="item-center">
                <div class="info-item ellipsis">{{ item.examName }}</div>
                <div class="info-item">
                  <div class="item-time">
                    {{ item.examStartTime }}（{{
                      $tools.computeMinute(
                        item.examStartTime,
                        item.examEndTime
                      )
                    }}）
                  </div>
                  <div class="item-score">总分：{{ item.paperTotalScore }}</div>
                  <div class="item-pass">
                    及格：{{
                      (
                        (item.paperPassScore / 100) *
                        item.paperTotalScore
                      ).toFixed(2)
                    }}
                  </div>
                </div>
              </div>
              <div class="item-btn" @click="goExam(item)">
                <i class="common common-count-down"></i>开始考试
              </div>
            </template>
          </div>
        </template>
        <el-empty v-else description="暂无待考"></el-empty>

        <!-- 模拟练习 -->
        <div
          class="info-item today-item"
          :key="item.id"
          v-for="item in questionTypeOpenList"
        >
          <i class="common common-test today-icon"></i>
          <div class="item-center">
            <div class="info-item ellipsis">{{ item.questionTypeName }}</div>
            <div class="info-item">
              <div class="item-time">
                {{ item.startTime }} - {{ item.endTime }}
              </div>
            </div>
          </div>
          <div class="item-btn" @click="goTest(item)">
            <i class="common common-count-down"></i>模拟练习
          </div>
        </div>
      </template>
      <!-- 子管理员 -->
      <template v-if="onlyRole.includes('subAdmin')">
        <template v-if="markList.length">
          <div
            class="info-item today-item"
            :style="{
              display: isMark(item.examMarkStartTime, item.examMarkEndTime)
                ? 'flex'
                : 'none',
            }"
            :key="item.id"
            v-for="item in markList"
          >
            <template
              v-if="isMark(item.examMarkStartTime, item.examMarkEndTime)"
            >
              <i class="common common-wait-mark today-icon"></i>
              <div class="item-center">
                <div class="info-item ellipsis">{{ item.examName }}</div>
                <div class="info-item">
                  <div class="item-time">
                    {{ item.examMarkStartTime }}（{{
                      $tools.computeMinute(
                        item.examMarkStartTime,
                        item.examMarkEndTime
                      )
                    }}）
                  </div>
                  <div class="item-score">总分：{{ item.paperTotalScore }}</div>
                  <div class="item-pass">
                    及格：{{
                      (
                        (item.paperPassScore / 100) *
                        item.paperTotalScore
                      ).toFixed(2)
                    }}
                  </div>
                </div>
              </div>
              <div class="item-btn" @click="goMark(item)">
                <i class="common common-count-down"></i>开始阅卷
              </div>
            </template>
          </div>
        </template>
        <el-empty v-else description="暂无待阅"></el-empty>
      </template>
    </div>
    <div class="home-info">
      <!-- 快捷导航 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>快捷导航</span>
        </div>
        <el-link
          class="quick-nav"
          v-for="nav in navList"
          :key="nav.path"
          @click="$router.push(nav.path)"
          >{{ nav.meta.title }}</el-link
        >
        <el-link class="quick-nav" @click="$router.push({ name: 'Quick' })"
          >快速考试</el-link
        >
      </el-card>
      <!-- 最新公告 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>最新公告</span>
        </div>
        <template v-if="bulletinList.length">
          <div class="info-list">
            <div
              class="info-item"
              v-for="item in bulletinList"
              :key="item.id"
              @click="getBulletin(item)"
            >
              <div class="item-left ellipsis">{{ item.title }}</div>
              <div class="item-right">{{ item.endTime }}</div>
            </div>
          </div>
        </template>
        <el-empty v-else description="暂无公告"></el-empty>
      </el-card>
      <!-- 服务支持 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>服务支持</span>
        </div>
        <p style="line-height: 30px">技术支持：在线考试</p>
        <p style="line-height: 30px">
          在线服务：<a
            target="_blank"
            href="https://jq.qq.com/?_wv=1027&k=GXh1hHSy"
          >
            <img
              src="https://img.shields.io/badge/qq-811189776-blue"
              alt="811189776"
            />
          </a>
        </p>
      </el-card>
    </div>
    <el-dialog
      center
      :title="bulletinDetail.title"
      :visible.sync="showBulletin"
    >
      <div class="bulletin-wrap">
        <div class="bulletin-title">{{ bulletinDetail.startTime }}</div>
        <div class="bulletin-content" v-html="bulletinDetail.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Calendar from 'components/Calendar/index'
import { myExamListPage, myMarkListPage } from 'api/my'
import { bulletinListPage } from 'api/base'
import { questionTypeOpenListPage } from 'api/question'
import * as dayjs from 'dayjs'
export default {
  components: {
    Calendar,
  },
  data() {
    return {
      now: new Date(),
      examList: [],
      markList: [],
      bulletinList: [],
      timePopovers: {},
      questionTypeOpenList: [],
      navList: [],
      showBulletin: false,
      bulletinDetail: {},
    }
  },
  computed: {
    ...mapGetters(['permission_routes', 'onlyRole']),
    // 是否展示考试
    isExam(state) {
      return (state) => {
        return state !== 3
      }
    },
    // 是否展示阅卷
    isMark(markState) {
      return (markState) => {
        return markState !== 3
      }
    },
  },
  watch: {
    '$store.getters.onlyRole': {
      immediate: true,
      async handler(newValue) {
        this.setNavBar()
        this.getBulletinList()
        this.getQuestionTypeOpenList()
        await this.renderCalendar()
      },
    },
  },
  methods: {
    setNavBar() {
      this.navList = this.permission_routes.filter(
        (item) => item?.meta?.layout === this.onlyRole[0] && !item?.meta?.hidden
      )
    },
    // 获取公告列表
    async getBulletinList() {
      const {
        data: { list },
      } = await bulletinListPage({
        curPage: 1,
        pageSize: 100,
      })
      this.bulletinList = list.filter(
        (item) => item.showType === 2 || item.showType === 1
      )
    },
    // 获取考试列表和阅卷列表
    async getExamAndMark(time = undefined) {
      const days = dayjs(time).daysInMonth()
      const startDate = time || dayjs().date(1).format('YYYY-MM-DD')
      const endDate = dayjs(time).date(days).format('YYYY-MM-DD')
      let examList, markList
      if (this.onlyRole.includes('user')) {
        examList = await myExamListPage({
          curPage: 1,
          pageSize: 10,
          startTime: `${startDate} 00:00:00`,
          endTime: `${endDate} 23:59:59`,
        })
      }
      if (this.onlyRole.includes('subAdmin')) {
        markList = await myMarkListPage({
          curPage: 1,
          pageSize: 10,
          startTime: `${startDate} 00:00:00`,
          endTime: `${endDate} 23:59:59`,
        })
      }
      if (dayjs(time).month() === dayjs().month()) {
        this.examList = (examList && examList?.data?.list) || []
        this.markList = (markList && markList?.data?.list) || []
      }
      return {
        examList: (examList && examList?.data?.list) || [],
        markList: (markList && markList?.data?.list) || [],
      }
    },
    // 获取选择月份的时间
    selectDate(time) {
      const _time = dayjs(time).date(1).format('YYYY-MM-DD')
      this.renderCalendar(_time)
    },
    // 渲染日历
    async renderCalendar(time) {
      const { examList, markList } = await this.getExamAndMark(time)
      let timePopovers = {}

      if (examList.length === 0 && markList.length === 0) {
        this.timePopovers = timePopovers
        return
      }

      let examPopovers = examList.reduce((acc, exam) => {
        const examTime = dayjs(exam.examStartTime).format('YYYY-MM-DD')
        if (!acc[examTime]) {
          acc[examTime] = {}
          acc[examTime]['exam'] = []
        }

        acc[examTime]['exam'].push({
          startTime: exam.examStartTime,
          endTime: exam.examEndTime,
          state: exam.state,
        })
        return acc
      }, {})

      timePopovers = markList.reduce((acc, mark) => {
        const markTime = dayjs(mark.examMarkStartTime).format('YYYY-MM-DD')
        if (!acc[markTime]) {
          acc[markTime] = {}
        }

        if (!acc[markTime] || !acc[markTime]['mark']) {
          acc[markTime]['mark'] = []
        }

        acc[markTime]['mark'].push({
          startTime: mark.examMarkStartTime,
          endTime: mark.examMarkEndTime,
          state: mark.examMarkState,
        })
        return acc
      }, examPopovers)
      this.timePopovers = timePopovers
    },
    // 展示公告
    getBulletin(item) {
      this.showBulletin = true
      this.bulletinDetail = item
    },
    // 获取开放题库
    async getQuestionTypeOpenList() {
      const days = dayjs().daysInMonth()
      const startDate = dayjs().date(1).format('YYYY-MM-DD')
      const endDate = dayjs().date(days).format('YYYY-MM-DD')
      const res = await questionTypeOpenListPage({
        state: 1,
        pageSize: 10,
        curPage: 1,
        startTime: `${startDate} 00:00:00`,
        endTime: `${endDate} 23:59:59`,
      })

      if (res?.code !== 200) return
      this.questionTypeOpenList = res.data.list.filter((item) => {
        const $_endTime = new Date(item.endTime).getTime()
        const now = new Date().getTime()
        return now < $_endTime
      })
    },
    // 模拟练习
    goTest({ questionTypeId, commentState }) {
      this.$router.push({
        name: 'SimulateTest',
        params: {
          questionTypeId,
          commentState,
        },
      })
    },
    // 我的考试操作
    goExam({ examId, paperId, paperShowType, examStartTime, examEndTime }) {
      const _examStartTime = new Date(examStartTime).getTime()
      const _examEndTime = new Date(examEndTime).getTime()
      const now = new Date().getTime()
      if (now < _examStartTime) {
        this.$message.warning('考试未开始，请等待...')
        return
      }

      this.$router.push({
        name: 'MyExamDetail',
        params: {
          examId,
          paperId,
          examEndTime,
          showType: paperShowType,
          preview: _examStartTime < now && now > _examEndTime,
        },
      })
    },
    // 我的阅卷操作
    goMark({ examId, paperId, examMarkStartTime, examMarkEndTime }) {
      const _markStartTime = new Date(examMarkStartTime).getTime()
      const _markEndTime = new Date(examMarkEndTime).getTime()
      const now = new Date().getTime()
      if (now < _markStartTime) {
        this.$message.warning('阅卷未开始，请等待...')
        return
      }

      this.$router.push({
        name: 'MyMarkIndexDetail',
        params: {
          examId,
          paperId,
          preview: _markStartTime < now && now > _markEndTime,
        },
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.home-content {
  margin-top: 20px;
  display: flex;
  flex-direction: row;
}
.home-calendar {
  width: 20%;
}
.home-list {
  flex: 1;
  background: #fff;
  margin: 0 10px;
}

.home-info {
  width: 25%;
}

// 卡片
.box-card {
  margin-bottom: 20px;
  /deep/ .el-card__header {
    position: relative;
    padding: 10px 10px 10px 20px;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 14px;
      left: 10px;
      width: 3px;
      height: 14px;
      background: #0094e5;
    }
  }
  /deep/.el-card__body {
    padding: 10px 10px 0;
  }
}

// 日历
/deep/.el-calendar__body {
  padding: 10px;
}

/deep/.el-calendar-table {
  thead {
    th {
      text-align: center;
    }
  }
  tr {
    &:first-child {
      td {
        border: 1px solid #fff;
      }
    }
    td {
      margin-top: 10px;
      &.is-today {
        background: #44cede;
        color: #fff;
        border-radius: 5px;
      }
      &.is-selected {
        background: #44cede;
        border-radius: 5px;
        color: #fff;
      }
      border: 1px solid #fff;
      &:first-child {
        border: 1px solid #fff;
      }
    }
  }
  .el-calendar-day {
    height: 30px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0;
    border-radius: 5px;
    &:hover {
      background: #fff;
      border: 1px solid #44cede;
      border-radius: 5px;
      color: #000;
      box-shadow: 0 0 5px 1px rgba(0, 149, 229, 0.1);
    }
  }
}

.info-item {
  line-height: 45px;
  font-size: 13px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e6ebf5;
  color: #333;
  padding: 0 5px;
  cursor: pointer;
  &:hover {
    color: #0094e5;
  }
  .item-left {
    flex: 1;
  }
  .item-time {
    flex: 1;
  }
  .item-score,
  .item-pass {
    margin-right: 30px;
  }
  .item-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid #e6ebf5;
    cursor: pointer;
    line-height: 30px;
    padding: 0 10px;
    border-radius: 5px;
    .common-count-down {
      margin-right: 5px;
    }
    &:hover {
      background: rgba(#0094e5, 0.8);
      border: 1px solid rgba(#0094e5, 0.8);
      color: #fff;
    }
  }
}

.today-item {
  display: flex;
  align-items: center;
  padding: 10px;
  .today-icon {
    display: inline-block;
    font-size: 50px;
    text-align: center;
    color: #c9c9c9;
  }
  .item-center {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0 20px;
    .info-item {
      line-height: 30px;
      border: none;
    }
  }
}

/deep/ .el-empty {
  padding: 35px 0;
}

/deep/ .el-dialog__header {
  border-bottom: 1px solid #c2c2c2;
  .el-dialog__title {
    font-size: 16px;
  }
}

.bulletin-wrap {
  display: flex;
  flex-direction: column;
  padding: 0 20px;
  .bulletin-title {
    width: 100%;
    text-align: right;
    font-size: 14px;
    color: #666;
    margin-bottom: 15px;
  }
  .bulletin-content {
    text-indent: 2em;
  }
}

.box-more {
  float: right;
  padding: 3px 0;
}

.quick-nav {
  margin: 10px;
}
</style>
