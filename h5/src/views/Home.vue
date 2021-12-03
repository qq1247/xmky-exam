<template>
  <div class="container">
    <!-- 轮播图 -->
    <el-carousel :interval="3000" height="350px">
      <el-carousel-item
        :style="{ background: carouse.bgColor || '#393d42' }"
        :key="carouse.id"
        v-for="carouse in carouselList"
      >
        <div class="banner-list">
          <div class="banner-info">
            <div
              class="banner-title"
              :style="{ color: carouse.textColor || '#fff' }"
            >
              {{ carouse.title }}
            </div>
            <div
              class="banner-content multi-ellipsis"
              :style="{ color: carouse.textColor || '#fff' }"
              v-html="carouse.content"
            ></div>
            <div
              class="banner-btn"
              :style="{
                color: carouse.textColor || '#fff',
                borderColor: carouse.textColor || '#fff',
              }"
            >
              查看详情
            </div>
          </div>
          <el-image
            class="banner-image"
            :src="
              carouse.imgFileId
                ? `/api/file/download?id=${carouse.imgFileId}`
                : carouse.img
            "
          >
            <div slot="placeholder" class="image-slot">加载中...</div>
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </div>
      </el-carousel-item>
    </el-carousel>
    <div class="container-content">
      <!-- 公告 -->
      <NoticeBar
        left-icon="notice"
        @link="noticeDetail(10)"
        v-if="bulletinList.length"
      >
        <el-carousel height="40px" :interval="3000" direction="vertical">
          <el-carousel-item v-for="(item, index) in bulletinList" :key="index">
            <div class="notice-custom-content">
              <div class="content-title ellipsis">{{ item.title }}</div>
              <div>{{ item.updateTime }}</div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </NoticeBar>

      <el-row :gutter="40">
        <el-col
          :span="8"
          v-if="onlyRole.includes('user') || onlyRole.includes('subAdmin')"
        >
          <template>
            <div class="box-title">
              <i class="common common-time"></i>
              <span>考试安排</span>
            </div>
            <Calendar
              v-model="now"
              :timePopovers="timePopovers"
              @selectDate="selectDate"
            ></Calendar>
          </template>
        </el-col>
        <el-col :span="16">
          <div v-if="onlyRole.includes('user')">
            <div class="box-title box-divider">
              <i class="common common-classify"></i>
              <span>待考列表</span>
            </div>
            <template v-if="examList.length">
              <el-row :gutter="10">
                <el-col :span="12" :key="item.id" v-for="item in examList">
                  <el-card class="box-card" shadow="hover">
                    <div class="card-header" slot="header">
                      <span class="header-left">{{ item.examName }}</span>
                      <div class="header-right">
                        <div class="exam-status">
                          {{ item.stateName }}
                        </div>
                      </div>
                    </div>
                    <el-row class="body-item">
                      <span class="start-time">{{ item.examStartTime }}</span>
                      （{{
                        computeMinute(item.examStartTime, item.examEndTime)
                      }}）
                    </el-row>
                    <el-row class="body-item">
                      <el-col :span="12">
                        <el-col :span="8" class="item-title">
                          <i class="common common-good"></i>及格：
                        </el-col>
                        <el-col :span="16" class="item-data">
                          {{
                            (item.totalScore * item.paperTotalScore) / 100
                          }}&nbsp;/&nbsp;{{ item.paperTotalScore }}
                        </el-col>
                      </el-col>
                      <el-col :span="12">
                        <el-col :span="12" class="item-title">
                          <i class="common common-persons"></i>考试人数：
                        </el-col>
                        <el-col :span="12" class="item-data">
                          {{ item.userNum }}
                        </el-col>
                      </el-col>
                    </el-row>
                    <div class="card-btn" @click="goExam(item)">
                      <i class="common common-count-down"></i>开始考试
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </template>
            <el-empty v-else description="暂无考试"></el-empty>
          </div>
          <div v-if="onlyRole.includes('subAdmin')">
            <div class="box-title box-divider">
              <i class="common common-classify"></i>
              <span>待阅列表</span>
            </div>
            <template v-if="markList.length">
              <el-row :gutter="10">
                <el-col :span="12" :key="item.id" v-for="item in markList">
                  <el-card class="box-card" shadow="hover">
                    <div class="card-header" slot="header">
                      <span class="header-left">{{ item.examName }}</span>
                      <div class="header-right">
                        <div class="mark-status">
                          {{ item.markStateName }}
                        </div>
                      </div>
                    </div>
                    <el-row class="body-item">
                      <span class="start-time">{{ item.markStartTime }}</span>
                      （{{
                        computeMinute(item.markStartTime, item.markEndTime)
                      }}）
                    </el-row>
                    <el-row class="body-item">
                      <el-col :span="12">
                        <el-col :span="8" class="item-title">
                          <i class="common common-good"></i>及格：
                        </el-col>
                        <el-col :span="16" class="item-data">
                          {{
                            (item.paperPassScore * item.paperTotalScore) / 100
                          }}&nbsp;/&nbsp;{{ item.paperTotalScore }}
                        </el-col>
                      </el-col>
                      <el-col :span="12">
                        <el-col :span="12" class="item-title">
                          <i class="common common-persons"></i>考试人数：
                        </el-col>
                        <el-col :span="12" class="item-data">
                          {{ item.userNum }}
                        </el-col>
                      </el-col>
                    </el-row>
                    <div class="card-btn" @click="goMark(item)">
                      <i class="common common-count-down"></i>开始阅卷
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </template>
            <el-empty v-else description="暂无阅卷"></el-empty>
          </div>
        </el-col>
      </el-row>
      <div v-if="onlyRole.includes('user')">
        <div class="box-title">
          <i class="common common-data-library"></i>
          <span>开放题库</span>
        </div>
        <template v-if="questionTypeOpenList.length">
          <el-row
            class="library"
            :key="item.id"
            v-for="item in questionTypeOpenList"
            @click.native="goDetail(item)"
          >
            <el-col :span="12" class="library-left ellipsis">{{
              item.questionTypeName
            }}</el-col>
            <el-col :span="12" class="library-right"
              >{{ item.startTime }} - {{ item.endTime }}</el-col
            >
          </el-row>
        </template>
        <el-empty v-else description="暂无开放题库"></el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { bulletinListPage } from 'api/base'
import { myExamListPage } from 'api/my'
import { questionTypeOpenListPage } from 'api/question'
import NoticeBar from 'components/NoticeBar/Index'
import getMainColor from '@/utils/getImageColor.js'
import * as dayjs from 'dayjs'
import Calendar from 'components/Calendar/index'
export default {
  components: {
    Calendar,
    NoticeBar,
  },
  data() {
    return {
      examList: [],
      markList: [],
      bulletinList: [],
      questionTypeOpenList: [],
      examStatus: ['', '待考', '考试', '已考', '已考'],
      carouselList: [
        {
          title: '在线考试',
          content:
            '一套适用于中小型企业的在线考试系统，开源免费，支持智能阅卷，权限控制等，持续更新，敬请等待~',
          img: require('../assets/img/banner-img.png'),
        },
      ],
      now: new Date(),
      timePopovers: {},
    }
  },
  created() {
    this.init()
  },
  computed: {
    ...mapGetters(['onlyRole']),
  },
  methods: {
    // 初始化
    async init() {
      this.getBulletinList()
      if (!this.onlyRole.includes('admin')) {
        await this.renderCalendar()
        this.renderList()
        this.getQuestionTypeOpenList()
      }
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
      const carouselList = list.filter((item) => item.showType === 3)
      this.getCarouselList(carouselList)
    },
    // 获取轮播图
    async getCarouselList(list) {
      if (list.length) {
        this.carouselList = list

        this.$nextTick(async () => {
          for (let index = 0; index < this.carouselList.length; index++) {
            const { colorHex, colorReverse } = await getMainColor(
              `/api/file/download?id=${this.carouselList[index].imgFileId}`
            )
            this.$set(this.carouselList[index], 'bgColor', colorHex)
            this.$set(this.carouselList[index], 'textColor', colorReverse)
          }
        })
      }
    },
    // 公告详情
    noticeDetail(id) {
      this.$router.push(`/my?noticeId=${id}`)
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
          pageSize: 100,
          startTime: `${startDate} 00:00:00`,
          endTime: `${endDate} 23:59:59`,
        })
      }
      if (this.onlyRole.includes('subAdmin')) {
        markList = await {
          curPage: 1,
          pageSize: 100,
          startTime: `${startDate} 00:00:00`,
          endTime: `${endDate} 23:59:59`,
        }
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
    // 渲染考试和阅卷列表
    renderList() {
      this.examList = this.examList.filter((item) => item.state === 1)
      this.markList = this.markList.filter((item) => item.markState === 1)
    },
    // 计算分钟数
    computeMinute(startTime, endTime) {
      const diffTime =
        new Date(endTime).getTime() - new Date(startTime).getTime()
      const minutes = diffTime / (60 * 1000)
      return `${minutes}分钟`
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
          state: exam.stateName,
        })
        return acc
      }, {})

      timePopovers = markList.reduce((acc, mark) => {
        const markTime = dayjs(mark.markStartTime).format('YYYY-MM-DD')

        if (!acc[markTime]) {
          acc[markTime] = {}
        }

        if (!acc[markTime] || !acc[markTime]['mark']) {
          acc[markTime]['mark'] = []
        }

        acc[markTime]['mark'].push({
          startTime: mark.markStartTime,
          endTime: mark.markEndTime,
          state: exam.markStateName,
        })
        return acc
      }, examPopovers)

      this.timePopovers = timePopovers
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
        path: '/question/comment',
        query: {
          id: questionTypeId,
          commentState,
        },
      })
    },
    // 去考试页面
    goExam({ id, state, examId, paperId, paperShowType, examEndTime }) {
      if (state === 1) {
        this.$message.warning('考试未开始，请等待...')
        return
      }

      this.$router.push({
        path: '/my/exam/index',
        query: {
          id,
          examId,
          paperId,
          examEndTime,
          showType: paperShowType,
          preview: state !== 2,
        },
      })
    },
    // 去阅卷页面
    goMark({ id, examId, paperId, markState }) {
      if (markState === 1) {
        this.$message.warning('阅卷未开始，请等待...')
        return
      }

      this.$router.push({
        path: '/my/mark/index',
        query: {
          markId: id,
          examId,
          paperId,
          preview: markState !== 2,
        },
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  margin-top: 0;
}

.el-carousel__item {
  height: 350px;
}

.banner-list {
  width: 1200px;
  height: 100%;
  display: flex;
  align-items: center;
  margin: 0 auto;
  padding: 0 30px;
  .banner-info {
    flex: 1;
    padding-right: 100px;
    color: #fff;
    .banner-title {
      font-size: 30px;
    }
    .banner-content {
      font-size: 16px;
      margin: 5px 0 0 30px;
    }
    .banner-btn {
      width: 100px;
      background: transparent;
      color: #fff;
      border: solid 1px #fff;
      padding: 8px;
      padding-left: 16px;
      padding-right: 16px;
      text-align: center;
      margin: 10px 0 0 30px;
      cursor: pointer;
    }
  }
  .banner-image {
    width: 316px;
    height: 280px;
    .image-slot {
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      background: #c0c4cc;
      font-size: 24px;
    }
  }
}

.container-content {
  width: 1200px;
  margin: 20px auto 0;
}

/deep/.el-card {
  border: 1px solid #fff;
}

.box-title {
  font-size: 14px;
  color: #d9534f;
  padding: 20px 0 3px;
  display: flex;
  align-items: center;
  position: relative;
  border-color: #dcdcdc;
  border-width: 0;
  border-style: solid;
  i {
    margin-right: 8px;
  }
}

.box-divider {
  &::after {
    content: '';
    margin-left: 10px;
    display: block;
    flex: 1;
    box-sizing: border-box;
    height: 1px;
    border-color: inherit;
    border-style: inherit;
    border-width: 1px 0 0;
  }
}

.box-card {
  margin-bottom: 10px;
  /deep/.el-card__header {
    padding: 10px 13px;
    border-bottom: 1px solid #fff;
  }
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: none;
    .header-left {
      font-size: 14px;
      color: #232425;
      width: 50%;
    }
    .header-right {
      .exam-status,
      .mark-status {
        width: 70px;
        line-height: 30px;
        text-align: center;
        border: 1px solid #83849a;
        font-size: 12px;
        color: #83849a;
      }
      .mark-status {
        border: 1px solid #eab7b7;
        color: #eab7b7;
      }
    }
  }
  /deep/.el-card__body {
    padding: 0 13px 15px;
  }
  .body-item {
    line-height: 30px;
    font-size: 13px;
    font-size: 13px;
    .start-time {
      font-size: 15px;
    }
    .item-title {
      color: #999;
      font-size: 13px;
      .common {
        margin-right: 5px;
        font-size: 14px;
      }
    }
    .item-data {
      font-size: 15px;
    }
  }
  .card-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    background: #0095e5;
    width: 125px;
    line-height: 40px;
    font-size: 14px;
    color: #fff;
    margin-top: 10px;
    cursor: pointer;
    .common-count-down {
      margin-right: 5px;
    }
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
        background: #0095e5;
        color: #fff;
        border-radius: 8px;
      }
      &.is-selected {
        background: rgba(#0095e5, 0.3);
        border-radius: 8px;
      }
      border: 1px solid #fff;
      &:first-child {
        border: 1px solid #fff;
      }
    }
  }
  .el-calendar-day {
    height: 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0;
    &:hover {
      background: #fff;
      border: 1px solid #0095e5;
      border-radius: 8px;
      color: #000;
      box-shadow: 0 0 5px 1px rgba(0, 149, 229, 0.1);
    }
  }
}

// 通知列表
.notice-custom-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  .content-title {
    width: 80%;
  }
}

// 开放题库
.library {
  display: flex;
  justify-content: space-between;
  align-items: center;
  line-height: 40px;
  background: #fff;
  padding: 0 10px;
  cursor: pointer;
  width: 100%;
  .library-left {
    width: 70%;
  }
  .library-right {
    flex: 1;
    text-align: right;
    color: #999;
  }
  &:hover {
    color: #0095e9;
    .library-right {
      color: #0095e9;
    }
  }
}
</style>
