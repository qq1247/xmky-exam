<template>
  <div class="home-content">
    <!-- <Upload
      type="word"
      ref="templateUpload"
      @success="templateSuccess"
      @remove="templateRemove"
    >
    </Upload> -->
    <div class="home-left">
      <el-card class="box-card" shadow="never">
        <div slot="header">考试概览</div>
        <template v-if="onlyRole[0] === 'user'">
          <div class="data-content" v-if="userInfo.exam">
            <div class="data-item">
              <span>一共缺考：</span>{{ userInfo.exam.missNum }}次
            </div>
            <div class="data-item">
              <span>参加考试：</span>{{ userInfo.exam.num }}场
            </div>
            <div class="data-item">
              <span>一共及格：</span>{{ userInfo.exam.succNum }}次
            </div>
            <div class="data-item">
              <span>最高排名：</span>{{ userInfo.exam.top }}名
            </div>
          </div>
          <div class="data-content" v-if="userInfo.score">
            <div class="data-item">
              <span>平均成绩：</span>{{ userInfo.score.avg }}分
            </div>
            <div class="data-item">
              <span>最低成绩：</span>{{ userInfo.score.min }}分
            </div>
            <div class="data-item">
              <span>最高成绩：</span>{{ userInfo.score.max }}分
            </div>
            <!-- <p><span>标准差：</span>{{ userInfo.score.sd }}</p> -->
          </div>
        </template>
        <template v-if="onlyRole[0] === 'subAdmin'">
          <div
            class="data-content"
            v-if="
              userInfo.exam &&
              userInfo.paper &&
              userInfo.question &&
              userInfo.myMark
            "
          >
            <div class="data-item" @click="$router.push({ name: 'Exam' })">
              <div class="item-icon exam-bg">
                <img src="../../assets/img/index/index-exam.png" alt="" />
              </div>
              <div class="item-info">
                <span class="info-num">{{ userInfo.exam.num }}</span>
                <span class="info-intro">创建考试（场）</span>
              </div>
            </div>
            <div class="data-item" @click="$router.push({ name: 'Paper' })">
              <div class="item-icon paper-bg">
                <img src="../../assets/img/index/index-paper.png" alt="" />
              </div>
              <div class="item-info">
                <span class="info-num">{{ userInfo.paper.num }}</span>
                <span class="info-intro">创建试卷（张）</span>
              </div>
            </div>
            <div class="data-item" @click="$router.push({ name: 'Question' })">
              <div class="item-icon question-bg">
                <img src="../../assets/img/index/index-question.png" alt="" />
              </div>
              <div class="item-info">
                <span class="info-num">{{ userInfo.question.num }}</span>
                <span class="info-intro">创建试题（题）</span>
              </div>
            </div>
            <div class="data-item">
              <div class="item-icon mark-bg">
                <img src="../../assets/img/index/index-mark.png" alt="" />
              </div>
              <div class="item-info">
                <span class="info-num">{{ userInfo.myMark.num }}</span>
                <span class="info-intro">待阅试卷（张）</span>
              </div>
            </div>
          </div>
        </template>
      </el-card>
      <el-card class="box-card" shadow="never">
        <div slot="header">
          {{ onlyRole.includes('user') ? '待考列表' : '待阅列表' }}
        </div>
        <div class="calendar-list">
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
                    <img
                      src="../../assets/img/index/wait-mark.png"
                      class="today-icon"
                    />
                    <div class="item-center">
                      <div class="info-item ellipsis">{{ item.examName }}</div>
                      <div class="info-item">
                        <div class="item-time">
                          {{ item.examStartTime }}
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
                      <img
                        src="../../assets/img/index/index-view.png"
                        alt=""
                      />开始考试
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
                <img
                  src="../../assets/img/index/wait-mark.png"
                  class="today-icon"
                />
                <div class="item-center">
                  <div class="info-item ellipsis">
                    {{ item.questionTypeName }}
                  </div>
                  <div class="info-item">
                    <div class="item-time">
                      {{ item.startTime }} - {{ item.endTime }}
                    </div>
                  </div>
                </div>
                <div class="item-btn" @click="goTest(item)">
                  <img
                    src="../../assets/img/index/index-view.png"
                    alt=""
                  />模拟练习
                </div>
              </div>
            </template>
            <!-- 子管理员 -->
            <template v-if="onlyRole.includes('subAdmin')">
              <template v-if="markList.length">
                <div
                  class="info-item today-item"
                  :style="{
                    display: isMark(
                      item.examMarkStartTime,
                      item.examMarkEndTime
                    )
                      ? 'flex'
                      : 'none',
                  }"
                  :key="item.id"
                  v-for="item in markList"
                >
                  <template
                    v-if="isMark(item.examMarkStartTime, item.examMarkEndTime)"
                  >
                    <img
                      src="../../assets/img/index/wait-mark.png"
                      class="today-icon"
                    />
                    <div class="item-center">
                      <div class="info-item ellipsis">{{ item.examName }}</div>
                      <div class="info-item">
                        <div class="item-time">
                          {{ item.examMarkStartTime }}
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
                      <img
                        src="../../assets/img/index/index-view.png"
                        alt=""
                      />开始阅卷
                    </div>
                  </template>
                </div>
              </template>
              <el-empty
                v-else
                :image="require('../../assets/img/index/mark-null.png')"
                description="暂无待阅"
              ></el-empty>
            </template>
          </div>
        </div>
      </el-card>
    </div>
    <div class="home-right">
      <!-- 快捷导航 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>快捷导航</span>
        </div>
        <div style="margin-bottom: 24px">
          <el-link
            class="quick-nav"
            v-for="nav in navList"
            :key="nav.path"
            :underline="false"
            @click="$router.push(nav.path)"
          >
            <div class="nav-item">
              <div class="nav-img" :style="{ background: nav.background }">
                <img :src="nav.icon" alt="" />
              </div>
              <span class="nav-title">{{ nav.title }}</span>
            </div>
          </el-link>
        </div>
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
        <el-empty
          v-else
          :image="require('../../assets/img/index/notice-null.png')"
          description="暂无公告"
        ></el-empty>
      </el-card>
      <!-- 服务支持 -->
      <el-card class="box-card" shadow="never">
        <div slot="header">
          <span>服务支持</span>
        </div>
        <div class="service">
          <p class="service-item">技术支持：在线考试</p>
          <p class="service-item">
            在线服务：<a
              class="service-qq"
              target="_blank"
              href="https://jq.qq.com/?_wv=1027&k=GXh1hHSy"
            >
              <div class="qq-title">QQ</div>
              <div class="qq-number">811189776</div>
            </a>
          </p>
        </div>
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
import Upload from 'components/Upload'
var mammoth = require('mammoth')
import { mapGetters } from 'vuex'
import { getInfo, setInfo } from '@/utils/storage'
import { getUserInfo, getSubAdminInfo, getAdminInfo } from 'api/report'
import Calendar from 'components/Calendar/index'
import { myExamListPage, myMarkListPage } from 'api/my'
import { bulletinListPage } from 'api/base'
import { questionTypeOpenListPage } from 'api/question'
import * as dayjs from 'dayjs'
export default {
  components: {
    Upload,
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
      userInfo: {},
      timer: null,
      number: 0,
    }
  },
  computed: {
    ...mapGetters(['permission_routes', 'roles', 'onlyRole']),
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
        const userInfo = newValue[0]
        if (userInfo === 'user') this.getUserInfo()
        if (userInfo === 'subAdmin') this.getSubAdminInfo()
        if (userInfo === 'admin') this.getAdminInfo()
        this.setNavBar(userInfo)
        this.getBulletinList()
        this.getQuestionTypeOpenList()
        await this.renderCalendar()
      },
    },
  },
  mounted() {
    window.addEventListener('scroll', this.handleScrollStart)
  },
  methods: {
    /* handleScrollStart() {
      this.timer = setInterval(() => {
        this.number += 1
        this.handleScrollEnd()
      }, 1000)
      this.isTipVisible = false
    },
    handleScrollEnd() {
      console.log(this.number)
      this.isTipVisible = true
      clearInterval(this.timer)
    }, */
    // 上传试题模板成功
    templateSuccess(res, file, fileList) {
      const reader = new FileReader()
      let arrayBuffer
      reader.onload = function (evt) {
        arrayBuffer = evt.target.result
      }
      reader.readAsArrayBuffer(file.raw)
      mammoth.convertToHtml({ arrayBuffer }).then((res) => {
        console.log(res)
      })
    },
    templateRemove() {},
    setNavBar(userInfo) {
      if (userInfo === 'subAdmin') {
        this.navList = [
          {
            path: '/question',
            background: '#0094E5',
            icon: require('../../assets/img/index/question-manage.png'),
            title: '试题管理',
          },
          {
            path: '/paper',
            background: '#FB901B',
            icon: require('../../assets/img/index/paper-manage.png'),
            title: '试卷管理',
          },
          {
            path: '/exam',
            background: '#09C8BD',
            icon: require('../../assets/img/index/exam-manage.png'),
            title: '考试管理',
          },
          {
            path: '/myMark',
            background: '#EB5B5B',
            icon: require('../../assets/img/index/mark-manage.png'),
            title: '阅卷管理',
          },
          {
            path: '/quick',
            background: '#6B77F9',
            icon: require('../../assets/img/index/index-quick.png'),
            title: '快捷考试',
          },
        ]
      }
      if (userInfo === 'user') {
        this.navList = [
          {
            path: '/myExam',
            background: '#0094E5',
            icon: require('../../assets/img/index/question-manage.png'),
            title: '考试管理',
          },
          {
            path: '/simulate',
            background: '#FB901B',
            icon: require('../../assets/img/index/paper-manage.png'),
            title: '模拟练习',
          },
        ]
      }
    },
    async getUserInfo() {
      const userInfo = await getUserInfo()
      this.userInfo = userInfo.data
      this.setUserInfo()
    },
    async getSubAdminInfo() {
      const userInfo = await getSubAdminInfo()
      this.userInfo = userInfo.data
      this.setUserInfo()
    },
    async getAdminInfo() {
      const userInfo = await getAdminInfo()
      this.userInfo = userInfo.data
    },
    setUserInfo() {
      const userInfo = getInfo()
      userInfo.orgName = this.userInfo.org.name
      userInfo.userAvatar = this.userInfo.user.headFileId
      setInfo(userInfo)
      this.$store.commit('user/SET_ORG_NAME', this.userInfo.org.name)
      this.$store.commit('user/SET_USER_AVATAR', this.userInfo.user.headFileId)
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
          userId: null,
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
  display: flex;
  flex-direction: row;
}
.home-left {
  flex: 1;
}
.home-right {
  width: 289px;
  margin-left: 15px;
}
.calendar-list {
  display: flex;
}
.home-calendar {
  width: 300px;
  border-right: 1px solid rgba(#000, 0.1);
}
.home-list {
  flex: 1;
  margin: 0 10px;
}

// 卡片
.box-card {
  margin-bottom: 16px;
  border: none;
  border-radius: 8px;
  /deep/ .el-card__header {
    position: relative;
    padding: 0 0 0 32px;
    height: 40px;
    line-height: 40px;
    &::after {
      content: '';
      display: block;
      position: absolute;
      top: 12px;
      left: 16px;
      width: 4px;
      height: 16px;
      background: #0094e5;
    }
  }
  /deep/.el-card__body {
    padding: 0;
  }
}

.data-content {
  display: flex;
  padding: 24px;
}

.data-item {
  width: 25%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  .item-icon {
    width: 48px;
    height: 48px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 16px;
    transition: all 0.15s ease;
    img {
      width: 24px;
      height: 24px;
      transition: all 0.3s ease-in-out;
    }
  }
  .item-info {
    display: flex;
    flex-direction: column;
    .info-num {
      font-size: 28px;
      font-weight: 600;
      color: #0c2e41;
    }
    .info-intro {
      font-size: 12px;
      color: #537384;
    }
  }
  &:hover {
    .item-icon {
      border-radius: 8px;
      box-shadow: 0 0 8px -3px rgba(#000, 0.13);
      img {
        transform: scale(1.15);
      }
    }
  }
}

.exam-bg {
  background: rgba(#09c8bd, 0.1);
}

.paper-bg {
  background: rgba(#fb901b, 0.1);
}

.question-bg {
  background: rgba(#0094e5, 0.1);
}

.mark-bg {
  background: rgba(#eb5b5b, 0.1);
}

// 日历
/deep/.el-calendar__body {
  padding: 10px;
}

/deep/.el-calendar-table {
  thead {
    th {
      text-align: center;
      width: 40px;
      height: 40px;
      line-height: 40px;
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
        background: #0094e5;
        color: #fff;
        border-radius: 5px;
      }
      &.is-selected {
        background: #0094e5;
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
    height: 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0;
    border-radius: 5px;
    &:hover {
      background: #fff;
      border: 1px solid #0094e5;
      border-radius: 5px;
      color: #000;
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
    border: none;
    cursor: pointer;
    line-height: 30px;
    padding: 0 10px;
    border-radius: 5px;
    background: rgba(#0094e5, 0.1);
    img {
      margin-right: 5px;
    }
    &:hover {
      background: #0094e5;
      color: #fff;
      img {
        content: url('~@/assets/img/index/index-view-active.png');
      }
    }
  }
}

.today-item {
  display: flex;
  align-items: center;
  padding: 10px;
  .today-icon {
    width: 64px;
    height: 64px;
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
  width: 33.33%;
  .nav-item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-top: 24px;
  }
  .nav-img {
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 8px;
  }
  .nav-title {
    font-size: 12px;
    color: #537384;
    margin-top: 8px;
  }
}

.service {
  padding: 16px;
}

.service-item {
  display: flex;
  align-items: center;
  line-height: 30px;
}
.service-qq {
  display: flex;
  .qq-title {
    width: 31px;
    height: 20px;
    line-height: 20px;
    color: #fff;
    text-align: center;
    font-size: 12px;
    background: #0874ae;
    border-radius: 4px 0px 0px 4px;
  }
  .qq-number {
    width: 73px;
    height: 20px;
    line-height: 20px;
    color: #fff;
    text-align: center;
    font-size: 12px;
    background: #0094e5;
    border-radius: 0px 4px 4px 0px;
  }
}
</style>
