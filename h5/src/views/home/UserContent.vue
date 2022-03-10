<!--
 * @Description: 用户·子管理员
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-12-13 13:58:07
 * @LastEditors: Che
 * @LastEditTime: 2022-01-21 18:10:07
-->
<template>
  <div class="home-content">
    <!-- 待考列表 -->
    <el-card class="box-card" shadow="never" v-if="onlyRole.includes('user')">
      <div slot="header">
        <span>待考列表</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="$router.push({ name: 'MyExam' })"
          >更多</el-button
        >
      </div>
      <el-row :gutter="30">
        <el-col :span="7">
          <Calendar
            v-model="now"
            :timePopovers="timePopovers"
            @selectDate="selectDate"
          ></Calendar>
        </el-col>
        <el-col :span="17">
          <template v-if="examList.length">
            <div
              class="info-item today-item"
              :style="{
                display: isToday(item.examStartTime, item.examEndTime)
                  ? 'flex'
                  : 'none',
              }"
              :key="item.id"
              v-for="item in examList"
            >
              <template v-if="isToday(item.examStartTime, item.examEndTime)">
                <i class="common common-wait-exam today-icon"></i>
                <div class="item-center">
                  <div class="info-item ellipsis">{{ item.examName }}</div>
                  <div class="info-item">
                    <div class="item-time">
                      {{ item.examStartTime }}（{{
                        computeMinute(item.examStartTime, item.examEndTime)
                      }}）
                    </div>
                    <div class="item-score">
                      总分：{{ item.paperTotalScore }}
                    </div>
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
          <el-empty v-else description="暂无考试"></el-empty>
        </el-col>
      </el-row>
    </el-card>
    <!-- 待阅列表 -->
    <el-card
      class="box-card"
      shadow="never"
      v-if="onlyRole.includes('subAdmin')"
    >
      <div slot="header">
        <span>待阅列表</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="$router.push({ name: 'MyMark' })"
          >更多</el-button
        >
      </div>
      <el-row :gutter="30">
        <el-col :span="8">
          <Calendar
            v-model="now"
            :timePopovers="timePopovers"
            @selectDate="selectDate"
          ></Calendar>
        </el-col>
        <el-col :span="16">
          <template v-if="markList.length">
            <div
              class="info-item today-item"
              :style="{
                display: isToday(item.examMarkStartTime, item.examMarkEndTime)
                  ? 'flex'
                  : 'none',
              }"
              :key="item.id"
              v-for="item in markList"
            >
              <template
                v-if="isToday(item.examMarkStartTime, item.examMarkEndTime)"
              >
                <i class="common common-wait-mark today-icon"></i>
                <div class="item-center">
                  <div class="info-item ellipsis">{{ item.examName }}</div>
                  <div class="info-item">
                    <div class="item-time">
                      {{ item.examMarkStartTime }}（{{
                        computeMinute(
                          item.examMarkStartTime,
                          item.examMarkEndTime
                        )
                      }}）
                    </div>
                    <div class="item-score">
                      总分：{{ item.paperTotalScore }}
                    </div>
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
          <el-empty v-else description="暂无试卷"></el-empty>
        </el-col>
      </el-row>
    </el-card>
    <el-row :gutter="30">
      <el-col :span="12">
        <el-card class="box-card" shadow="never">
          <div slot="header">
            <span>最新公告</span>
            <el-button style="float: right; padding: 3px 0" type="text"
              >更多</el-button
            >
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
      </el-col>
      <el-col :span="12" v-if="onlyRole.includes('user')">
        <el-card class="box-card" shadow="never">
          <div slot="header">
            <span>模拟练习</span>
            <el-button style="float: right; padding: 3px 0" type="text"
              >更多</el-button
            >
          </div>
          <template v-if="questionTypeOpenList.length">
            <div class="info-list">
              <div
                class="info-item"
                :key="item.id"
                v-for="item in questionTypeOpenList"
                @click="goDetail(item)"
              >
                <div class="item-left ellipsis">
                  {{ item.questionTypeName }}
                </div>
                <div class="item-right">
                  {{ item.startTime }} - {{ item.endTime }}
                </div>
              </div>
            </div>
          </template>
          <el-empty v-else description="暂无模拟试卷"></el-empty>
        </el-card>
      </el-col>
      <el-col :span="12" v-if="onlyRole.includes('subAdmin')">
        <el-card class="box-card" shadow="never">
          <div slot="header">
            <span>快捷导航</span>
          </div>
          <div class="nav-box">
            <div
              class="nav-item"
              v-for="nav in navList"
              :key="nav.path"
              @click="$router.push(nav.path)"
            >
              <template v-if="nav.name !== 'Quick'">
                <i :class="[`common ${nav.meta.icon}`]"></i>
                <span>{{ nav.meta.title }}</span>
              </template>
            </div>
            <div class="easy-exam" @click="$router.push({ name: 'Quick' })">
              <i class="common common-quick"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
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
    // 计算分钟数
    computeMinute(startTime, endTime) {
      const diffTime =
        new Date(endTime).getTime() - new Date(startTime).getTime()
      const minutes = diffTime / (60 * 1000)
      return `${minutes.toFixed(2)}分钟`
    },
    // 是否是今天
    isToday(startTime, endTime) {
      const today = new Date().getTime()
      const _startTime = new Date(
        `${dayjs(startTime).format('YYYY-MM-DD')} 00:00:00`
      ).getTime()
      const _endTime = new Date(
        `${dayjs(endTime).format('YYYY-MM-DD')} 23:59:59`
      ).getTime()
      return today > _startTime && today < _endTime
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
      const res = await questionTypeOpenListPage({
        state: 1,
        pageSize: 10,
        curPage: 1,
      })
      res?.code === 200 && (this.questionTypeOpenList = res.data.list)
    },
    // 查看试题和评论
    goDetail({ questionTypeId, startTime, endTime, commentState }) {
      const $_startTime = new Date(startTime).getTime()
      const $_endTime = new Date(endTime).getTime()
      const now = new Date().getTime()
      if (now < $_startTime || now > $_endTime) {
        this.$message.warning('不在开放时间段，请重新确认时间点！')
        return
      }
      this.$router.push({
        name: 'SimulateComment',
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
  margin: 20px 0;
  display: flex;
  flex-direction: column;
}
.content-left {
  width: 150px;
  display: flex;
  flex-direction: column;
}

.content-right {
  flex: 1;
  margin-left: 50px;
}

// 导航
.nav-router {
  line-height: 40px;
  border-bottom: 1px solid #cfcfcf;
  background: rgba(#0094e5, 0.8);
  border-radius: 20px;
  margin-bottom: 10px;
  color: #fff;
  text-align: center;
  &:hover {
    background: rgba(#0094e5, 1);
  }
  .common {
    margin-right: 10px;
  }
}

// 卡片
.box-card {
  margin-bottom: 30px;
  /deep/ .el-card__header {
    position: relative;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 18px;
      left: 10px;
      width: 4px;
      height: 20px;
      background: #0094e5;
    }
  }
  /deep/.el-card__body {
    padding: 13px 13px 0;
    min-height: 300px;
  }
}

// 日历
/deep/.el-calendar__body {
  padding: 12px 16px 16px;
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
      &.is-today {
        background: #44cede;
        color: #fff;
        border-radius: 50%;
      }
      &.is-selected {
        background: #44cede;
        border-radius: 50%;
        color: #fff;
      }
      border: 1px solid #fff;
      &:first-child {
        border: 1px solid #fff;
      }
    }
  }
  .el-calendar-day {
    height: 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0;
    border-radius: 50%;
    &:hover {
      background: #fff;
      border: 1px solid #44cede;
      border-radius: 50%;
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
    font-size: 60px;
    text-align: center;
    color: #c9c9c9;
  }
  .item-center {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0 20px;
    .info-item {
      line-height: 35px;
      border: none;
    }
  }
}

/deep/ .el-empty {
  padding: 35px 0;
}

.nav-box {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  position: relative;
  .nav-item {
    width: 50%;
    min-height: 130px;
    height: calc(100% / 2);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #555;
    cursor: pointer;
    &:nth-child(1) {
      border-top-left-radius: 10px;
    }
    &:nth-child(2) {
      border-top-right-radius: 10px;
    }
    &:nth-child(3) {
      border-bottom-left-radius: 10px;
    }
    &:nth-child(4) {
      border-bottom-right-radius: 10px;
    }
    &:hover {
      color: #fff;
      background: #0094e5;
      & ~ .easy-exam {
        box-shadow: 0 0 0 10px #fff;
        background: #0094e5;
        color: #fff;
        i {
          color: #fff;
        }
      }
    }
    i {
      font-size: 40px;
    }
  }

  .easy-exam {
    position: absolute;
    background: #fff;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
    border-radius: 50%;
    top: calc(50% - 50px);
    left: calc(50% - 50px);
    box-shadow: 0 0 13px 3px rgba(#000, 0.13);
    outline: 3px solid #0094e5;
    cursor: pointer;
    &:hover {
      box-shadow: 0 0 0 10px #fff;
      background: #0094e5;
      color: #fff;
      i {
        color: #fff;
      }
    }
    i {
      font-size: 40px;
      color: #0094e5;
    }
  }
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
</style>
