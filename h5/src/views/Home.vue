<template>
  <div class="container">
    <el-carousel :interval="3000" height="350px">
      <el-carousel-item
        :style="{ background: carouse.bg || '#393d42' }"
        :key="carouse.id"
        v-for="carouse in carouselList"
      >
        <div class="banner-list">
          <div class="banner-info">
            <p class="banner-title">{{ carouse.title }}</p>
            <p class="banner-content">{{ carouse.content }}</p>
            <div class="banner-btn">查看详情</div>
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
      <el-row :gutter="40">
        <el-col :span="8">
          <template>
            <div class="box-title">
              <i class="common common-time"></i><span>考试安排</span>
            </div>
            <el-calendar v-model="now">
              <template #dateCell="{ date, data }">
                <div
                  class="date-cell"
                  :class="data.isSelected ? 'is-selected' : ''"
                >
                  <div class="calendar-day">
                    {{ data.day.split('-').slice(2).join('-') }}
                  </div>
                </div>
              </template>
            </el-calendar>
          </template>
          <template>
            <div class="box-title">
              <i class="common common-notice"></i><span>公告</span>
            </div>
            <el-row
              :class="['notice', item.topState === 1 ? 'notice-hot' : '']"
              :key="item.id"
              v-for="item in bulletinList"
            >
              <el-col :span="12" class="notice-left">
                <i></i>
                <span class="ellipsis">{{ item.title }}</span>
              </el-col>
              <el-col :span="12" class="notice-right">{{
                item.updateTime
              }}</el-col>
            </el-row>
          </template>
        </el-col>
        <el-col :span="16">
          <div>
            <div class="box-title box-divider">
              <i class="common common-classify"></i><span>待考列表</span>
            </div>
            <el-row :gutter="10">
              <el-col :span="12" :key="item.id" v-for="item in examList">
                <el-card class="box-card" shadow="hover">
                  <div class="card-header" slot="header">
                    <span class="header-left">{{ item.examName }}</span>
                    <div class="header-right">
                      <div class="exam-status">
                        {{ examStatus[item.state] }}
                      </div>
                    </div>
                  </div>
                  <el-row class="body-item">
                    <span class="start-time">{{ item.examStartTime }}</span
                    >({{
                      computeMinute(item.examStartTime, item.examEndTime)
                    }}分钟)
                  </el-row>
                  <el-row class="body-item">
                    <el-col :span="12">
                      <el-col :span="8" class="item-title"
                        ><i class="common common-good"></i>及格：</el-col
                      >
                      <el-col :span="16" class="item-data">
                        {{
                          (item.totalScore * item.paperTotalScore) / 100
                        }}&nbsp;/&nbsp;{{ item.paperTotalScore }}
                      </el-col>
                    </el-col>
                    <el-col :span="12">
                      <el-col :span="12" class="item-title"
                        ><i class="common common-persons"></i>考试人数：</el-col
                      >
                      <el-col :span="12" class="item-data">{{
                        item.userNum
                      }}</el-col>
                    </el-col>
                  </el-row>
                  <div class="card-btn">
                    <i class="common common-count-down"></i>开始考试
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <div>
            <div class="box-title box-divider">
              <i class="common common-classify"></i><span>待阅列表</span>
            </div>
            <el-row :gutter="10">
              <el-col :span="12" :key="item.id" v-for="item in markList">
                <el-card class="box-card" shadow="hover">
                  <div class="card-header" slot="header">
                    <span class="header-left">{{ item.examName }}</span>
                    <div class="header-right">
                      <div class="mark-status">
                        {{ examStatus[item.state] }}
                      </div>
                    </div>
                  </div>
                  <el-row class="body-item">
                    <span class="start-time">{{ item.markStartTime }}</span
                    >({{
                      computeMinute(item.markStartTime, item.markEndTime)
                    }}分钟)
                  </el-row>
                  <el-row class="body-item">
                    <el-col :span="12">
                      <el-col :span="8" class="item-title"
                        ><i class="common common-good"></i>及格：</el-col
                      >
                      <el-col :span="16" class="item-data">
                        {{
                          (item.paperPassScore * item.paperTotalScore) / 100
                        }}&nbsp;/&nbsp;{{ item.paperTotalScore }}
                      </el-col>
                    </el-col>
                    <el-col :span="12">
                      <el-col :span="12" class="item-title"
                        ><i class="common common-persons"></i>考试人数：</el-col
                      >
                      <el-col :span="12" class="item-data">{{
                        item.userNum
                      }}</el-col>
                    </el-col>
                  </el-row>
                  <div class="card-btn">
                    <i class="common common-count-down"></i>开始阅卷
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { myExamListPage, myMarkListPage } from '@/api/my'
import { bulletinListPage } from '@/api/base'
import getMainColor from '@/utils/getImageColor.js'
import * as dayjs from 'dayjs'
export default {
  data() {
    return {
      examList: [],
      markList: [],
      examStatus: ['', '待考', '考试', '已考', '已考'],
      readPaperStatus: ['', '待阅', '阅卷', '已阅'],
      bulletinList: [],
      carouselList: [],
      now: new Date(),
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 计算分钟数
    computeMinute(startTime, endTime) {
      const timeDiff =
        new Date(endTime).getTime() - new Date(startTime).getTime()
      return Math.ceil(timeDiff / (1000 * 60 * 60))
    },
    // 初始化
    init() {
      this.getCarouselList()
      this.getBulletinList()
      this.getExamList()
      this.getMarkList()
      this.renderExamCalendar()
    },
    // 获取考试列表
    async getExamList() {
      const {
        data: { list },
      } = await myExamListPage({
        curPage: 1,
        pageSize: 10,
        needExam: 1,
      })
      this.examList = list
    },
    // 获取阅卷列表
    async getMarkList() {
      const {
        data: { list },
      } = await myMarkListPage({
        curPage: 1,
        pageSize: 10,
        needMark: 1,
      })
      this.markList = list
    },
    // 获取公告列表
    async getBulletinList() {
      const {
        data: { list },
      } = await bulletinListPage({
        curPage: 1,
        pageSize: 10,
        state: 1,
      })
      this.bulletinList = list
    },
    // 获取轮播图
    async getCarouselList() {
      const {
        data: { list },
      } = await bulletinListPage({
        curPage: 1,
        pageSize: 10,
        state: 2,
      })

      if (list.length > 0) {
        list.map(async (item) => {
          const bg = await getMainColor(
            `/api/file/download?id=${item.imgFileId}`
          )
          item.bg = bg
        })
        this.carouselList = list
      } else {
        this.carouselList = [
          {
            title: '在线考试',
            content:
              '一套适用于中小型企业的在线考试系统，开源免费，支持智能阅卷，权限控制等，持续更新，敬请等待~',
            img: require('../assets/img/banner-img.png'),
          },
        ]
      }
    },
    // 渲染日历
    async renderExamCalendar() {
      const days = dayjs().daysInMonth()
      const startDate = dayjs().date(1).format('YYYY-MM-DD')
      const endDate = dayjs().date(days).format('YYYY-MM-DD')
      const examList = await myExamListPage({
        curPage: 1,
        pageSize: 10,
        startDate: `${startDate} 00:00:00`,
        endDate: `${endDate} 23:59:59`,
      })
      const markList = await myMarkListPage({
        curPage: 1,
        pageSize: 10,
        startDate: `${startDate} 00:00:00`,
        endDate: `${endDate} 23:59:59`,
      })
      console.log(examList)
      console.log(markList)
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
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
      font-size: 36px;
    }
    .banner-content {
      font-size: 14px;
      overflow: hidden;
      text-indent: 2em;
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
        border-radius: 50%;
      }
      &.is-selected {
        background: rgba(255, 0, 0, 0.1);
        border-radius: 5px;
      }
      border: 1px solid #fff;
      &:first-child {
        border: 1px solid #fff;
      }
    }
  }
  .el-calendar-day {
    height: 50px;
    line-height: 50px;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0;
    &:hover {
      background: #fff;
      border: 1px solid #0095e5;
      color: #000;
      box-shadow: 0 0 5px 1px rgba(0, 149, 229, 0.1);
    }
  }
}

// 通知列表
.notice {
  display: flex;
  justify-content: space-between;
  align-items: center;
  line-height: 30px;
  background: #fff;
  padding: 0 10px;
  cursor: pointer;
  &:nth-of-type(2) {
    padding-top: 10px;
    .notice-left {
      i::before {
        display: none;
      }
    }
  }
  &:nth-last-of-type(1) {
    padding-bottom: 10px;
    .notice-left {
      i::after {
        display: none;
      }
    }
  }
  &:hover {
    color: #0095e9;
    .notice-right {
      color: #0095e9;
    }
    .notice-left {
      i {
        background: #0095e5;
        box-shadow: 0 0 10px 1px rgba(0, 149, 233, 0.5);
      }
    }
  }
  .notice-left {
    display: flex;
    align-items: center;
    line-height: 40px;
    i {
      width: 8px;
      height: 8px;
      display: block;
      background: #dfdfdf;
      border-radius: 4px;
      margin-right: 8px;
      position: relative;
      &::after,
      &::before {
        content: '';
        position: absolute;
        box-sizing: border-box;
        width: 1px;
        height: calc((40px - 8px) / 2);
        background: #f7f8f9;
      }
      &::after {
        top: calc((40px - 8px) / 4);
        left: 4px;
      }
      &::before {
        bottom: calc((40px - 8px) / 4);
        left: 4px;
      }
    }
    span {
      flex: 1;
    }
  }
  .notice-right {
    text-align: right;
    color: #999;
  }
}

.notice-hot {
  color: #0095e5;
  .notice-left {
    i {
      background: #0095e5;
      box-shadow: 0 0 10px 1px rgba(0, 149, 233, 0.5);
    }
  }
  .notice-right {
    color: #0095e5;
  }
}
</style>
