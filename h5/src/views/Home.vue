<template>
  <div class="container">
    <el-carousel :interval="3000" height="350px">
      <el-carousel-item :key="carouse.id" v-for="carouse in carouselList">
        <p>{{carouse.title}}</p>
        <img :src="'/api/file/download?id='+carouse.imgFileId" />
      </el-carousel-item>
    </el-carousel>
    <div class="container-content">
      <el-row :gutter="10">
        <el-col :span="10">
          <div class="box-title">
            <span>考试时间安排</span>
          </div>
          <el-calendar :range="['2021-06-01', '2021-06-30']"></el-calendar>
        </el-col>
        <el-col :span="14">
          <div class="box-title">
            <span>待考列表</span>
            <span>更多&nbsp;&gt;&gt;</span>
          </div>
          <el-col :key="item.id" :span="12" v-for="item in examList">
            <el-card class="box-card" shadow="hover">
              <div class="card-header" slot="header">
                <span class="header-left">{{ item.examName }}</span>
                <div class="header-right">
                  <i class="common common-setting"></i>
                  <el-tag effect="dark" size="mini" type="danger">
                    {{
                    examStatus[item.state]
                    }}
                  </el-tag>
                  <el-tag effect="dark" size="mini" type="warning">
                    {{
                    readPaperStatus[item.markState]
                    }}
                  </el-tag>
                </div>
              </div>
              <el-row class="body-item">
                <el-col :span="6" class="item-title">开始时间：</el-col>
                <el-col :span="18">{{ item.examStartTime }}</el-col>
              </el-row>
              <el-row class="body-item">
                <el-col :span="6" class="item-title">结束时间：</el-col>
                <el-col :span="18">{{ item.examEndTime }}</el-col>
              </el-row>
              <el-row class="body-item">
                <el-col :span="12">
                  <el-col :span="8" class="item-title">及格：</el-col>
                  <el-col :span="16">
                    {{ item.totalScore }}&nbsp;/&nbsp;{{
                    item.paperTotalScore
                    }}
                  </el-col>
                </el-col>
                <el-col :span="12">
                  <el-col :span="11" class="item-title">考试人数：</el-col>
                  <el-col :span="13">{{ item.userNum }}</el-col>
                </el-col>
              </el-row>
              <el-button class="card-btn" icon="el-icon-timer" round size="mini" type="primary">{{ item.markState == 1 ? '开始阅卷' : '开始考试' }}</el-button>
            </el-card>
          </el-col>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="10">
          <div class="box-title">
            <span>考试通知</span>
          </div>
          <el-row :class="['notice', item.hot ? 'notice-hot' : '']" :key="item.id" v-for="item in noticeList">
            <el-col class="notice-left">
              <i class="common common-remind"></i>
              <span>{{ item.title }}</span>
            </el-col>
            <el-col class="notice-right">{{ item.time }}</el-col>
          </el-row>
        </el-col>
        <el-col :span="14">
          <div class="box-title">
            <span>待阅列表</span>
            <span>更多&nbsp;&gt;&gt;</span>
          </div>
          <el-col :key="item.id" :span="12" v-for="item in examList">
            <el-card class="box-card" shadow="hover">
              <div class="card-header" slot="header">
                <span class="header-left">{{ item.examName }}</span>
                <div class="header-right">
                  <i class="common common-setting"></i>
                  <el-tag effect="dark" size="mini" type="danger">
                    {{
                    examStatus[item.state]
                    }}
                  </el-tag>
                  <el-tag effect="dark" size="mini" type="warning">
                    {{
                    readPaperStatus[item.markState]
                    }}
                  </el-tag>
                </div>
              </div>
              <el-row class="body-item">
                <el-col :span="6" class="item-title">开始时间：</el-col>
                <el-col :span="18">{{ item.examStartTime }}</el-col>
              </el-row>
              <el-row class="body-item">
                <el-col :span="6" class="item-title">结束时间：</el-col>
                <el-col :span="18">{{ item.examEndTime }}</el-col>
              </el-row>
              <el-row class="body-item">
                <el-col :span="12">
                  <el-col :span="8" class="item-title">及格：</el-col>
                  <el-col :span="16">
                    {{ item.totalScore }}&nbsp;/&nbsp;{{
                    item.paperTotalScore
                    }}
                  </el-col>
                </el-col>
                <el-col :span="12">
                  <el-col :span="11" class="item-title">考试人数：</el-col>
                  <el-col :span="13">{{ item.userNum }}</el-col>
                </el-col>
              </el-row>
              <el-button class="card-btn" icon="el-icon-timer" round size="mini" type="primary">{{ item.markState == 1 ? '开始阅卷' : '开始考试' }}</el-button>
            </el-card>
          </el-col>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'
export default {
  data() {
    return {
      examList: [],
      examStatus: ['', '未考试', '考试中', '已交卷', '强制交卷'],
      readPaperStatus: ['', '未阅卷', '阅卷中', '已阅卷'],
      noticeList: [],
      carouselList: [],
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      this.getCarouselList()
      this.getExamList()
      this.getNoticeList()
    },
    async getExamList() {
      const examList = await this.$https.myExamListpage({
        curPage: 1,
        pageSize: 10,
      })
      this.examList = examList
    },
    getNoticeList() {
      for (let index = 0; index < 11; index++) {
        this.noticeList.push({
          id: index + 1,
          title: '这是一条最新通知',
          time: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          hot: index == 0,
        })
      }
    },
    // 获取轮播图
    async getCarouselList() {
      const {
        data: { list, total },
      } = await this.$https.bulletinListPage({
        curPage: 1,
        pageSize: 10,
        topState: 1, // 只查询置顶的
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
}

.container-content {
  width: 1200px;
  margin: 20px auto 0;
}

/deep/.el-card {
  border: 1px solid #fff;
  &.is-hover-shadow {
    &:hover,
    &:focus {
      border: 1px solid #0095e5;
      box-shadow: none;
    }
  }
}

.box-title {
  font-size: 14px;
  color: #000;
  line-height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.box-card {
  margin-bottom: 10px;
  /deep/.el-card__header {
    padding: 13px;
    border-bottom: 1px solid #f4f5f7;
  }
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: none;
    .header-left {
      font-size: 13px;
      color: #232425;
      font-weight: 600;
    }
    .header-right {
      .el-tag {
        margin-left: 8px;
        border-radius: 20px;
        padding: 0 8px;
      }
    }
  }
  /deep/.el-card__body {
    padding: 15px;
  }
  .body-item {
    line-height: 30px;
    font-size: 13px;
    color: #777;
    .item-title {
      color: #252627;
    }
  }
  .card-btn {
    margin-top: 20px;
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
        border-radius: 5px;
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
    height: 60px;
    display: flex;
    flex-direction: column;
    align-items: center;
    &:hover {
      background: #fff;
      border: 1px solid #0095e5;
      color: #000;
      border-radius: 5px;
      box-shadow: 0 0 5px 1px rgba(0, 149, 229, 0.1);
    }
  }
}

// 通知列表
.notice {
  display: flex;
  justify-content: space-between;
  align-items: center;
  line-height: 40px;
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
  }
  .notice-right {
    text-align: right;
    color: #858585;
  }
}

.notice-hot {
  color: #0095e5;
  .notice-right {
    color: #0095e5;
  }
}
</style>
