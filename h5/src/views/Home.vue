<template>
  <div class="container">
    <el-carousel :interval="3000" height="350px">
      <el-carousel-item
        class="banner-list"
        :key="carouse.id"
        v-for="carouse in carouselList"
      >
        <div class="banner-list">
          <p>{{ carouse.title }}</p>
          <img
            :src="'/api/file/download?id=' + carouse.imgFileId"
            style="width: 400px; height: 350px"
          />
        </div>
      </el-carousel-item>
    </el-carousel>
    <div class="container-content">
      <el-row :gutter="10">
        <el-col :span="8">
          <div class="box-title">
            <i class="common common-time"></i><span>考试安排</span>
          </div>
          <el-calendar :range="['2021-06-01', '2021-06-30']">
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
        </el-col>
        <el-col :span="16">
          <div class="box-title box-divider">
            <i class="common common-classify"></i><span>待考列表</span>
          </div>
          <el-col :key="item.id" :span="12" v-for="item in examList">
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
                >({{ getMinute(item.examStartTime, item.examEndTime) }}分钟)
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
                <i class="common common-count-down"></i>开始阅卷
              </div>
            </el-card>
          </el-col>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="8">
          <div class="box-title">
            <i class="common common-notice"></i><span>公告</span>
          </div>
          <el-row
            :class="['notice', item.hot ? 'notice-hot' : '']"
            :key="item.id"
            v-for="item in bulletinList"
          >
            <el-col class="notice-left">
              <i class="common common-remind"></i>
              <span>{{ item.title }}</span>
            </el-col>
            <el-col class="notice-right">{{ item.updateTime }}</el-col>
          </el-row>
        </el-col>
        <el-col :span="16">
          <div class="box-title box-divider">
            <i class="common common-classify"></i><span>待阅列表</span>
          </div>
          <el-col :key="item.id" :span="12" v-for="item in markList">
            <el-card class="box-card" shadow="hover">
              <div class="card-header" slot="header">
                <span class="header-left">{{ item.name }}</span>
                <div class="header-right">
                  <div class="mark-status">
                    {{ examStatus[item.state] }}
                  </div>
                </div>
              </div>
              <el-row class="body-item">
                <span class="start-time">{{ item.markStartTime }}</span
                >({{ getMinute(item.markStartTime, item.markEndTime) }}分钟)
              </el-row>
              <el-row class="body-item">
                <el-col :span="12">
                  <el-col :span="8" class="item-title"
                    ><i class="common common-good"></i>及格：</el-col
                  >
                  <el-col :span="16" class="item-data">
                    {{
                      (item.paperPassScore * item.paperTotleScore) / 100
                    }}&nbsp;/&nbsp;{{ item.paperTotleScore }}
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
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      examList: [],
      markList: [],
      examStatus: ['', '待考', '考试', '已考', '已考'],
      readPaperStatus: ['', '待阅', '阅卷', '已阅'],
      bulletinList: [],
      carouselList: [],
    }
  },
  created() {
    this.init()
  },
  methods: {
    getMinute(startTime, endTime) {
      const timeDiff =
        new Date(endTime).getTime() - new Date(startTime).getTime()
      return Math.ceil(timeDiff / (1000 * 60 * 60))
    },
    init() {
      this.getCarouselList()
      this.getBulletinList()
      this.getExamList()
      this.getMarkList()
    },
    // 获取考试列表
    async getExamList() {
      const {
        data: { list, total },
      } = await this.$https.myExamListPage({
        curPage: 1,
        pageSize: 10,
        needExam: 1,
      })
      this.examList = list
    },
    // 获取阅卷列表
    async getMarkList() {
      const {
        data: { list, total },
      } = await this.$https.myMarkExamListPage({
        curPage: 1,
        pageSize: 10,
      })
      this.markList = list
    },
    // 获取公告列表
    async getBulletinList() {
      const {
        data: { list, total },
      } = await this.$https.bulletinListPage({
        curPage: 1,
        pageSize: 10,
        state: 1,
      })
      this.bulletinList = list
    },
    // 获取轮播图
    async getCarouselList() {
      const {
        data: { list, total },
      } = await this.$https.bulletinListPage({
        curPage: 1,
        pageSize: 10,
        state: 2,
      })
      this.carouselList = list
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
}
.el-carousel__item {
  border-radius: 3px;
  h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 300px;
    text-align: center;
    margin: 0;
  }
}

.el-carousel__item {
  &:nth-child(2n) {
    background-color: #99a9bf;
  }
  &:nth-child(2n + 1) {
    background-color: #d3dce6;
  }
  .banner-list {
    width: 1200px;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0 auto;
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
  color: #000;
  padding: 20px 0 3px;
  display: flex;
  align-items: center;
  font-weight: 600;
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
      font-weight: 600;
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
      font-weight: 600;
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
      font-weight: 600;
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
  &:hover {
    color: #000;
    .notice-right {
      color: #000;
    }
  }
  .notice-left {
    i {
      font-size: 12px;
      font-weight: 600;
      margin-right: 5px;
    }
    font-weight: 600;
  }
  .notice-right {
    text-align: right;
    color: #999;
  }
}

.notice-hot {
  color: #0095e5;
  .notice-right {
    color: #0095e5;
  }
}
</style>
