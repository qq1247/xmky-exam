<template>
  <div class="container">
    <template v-if="hashChildren">
      <!-- 搜索 -->
      <el-form :inline="true" :model="queryForm" class="form-inline search">
        <div>
          <el-form-item>
            <el-input
              v-model="queryForm.name"
              placeholder="请输入名称"
              class="query-input"
            />
          </el-form-item>
        </div>
        <el-form-item>
          <el-button
            icon="el-icon-search"
            type="primary"
            @click="search"
          >查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 内容 -->
      <div class="content">
        <div class="exam-list">
          <AddCard add-title="添加考试" @addCard="add" />
          <div
            v-for="(exam, index) in examList"
            :key="index"
            class="exam-item"
          >
            <div class="exam-content">
              <div class="title ellipsis">
                {{ exam.name }}
              </div>
              <div class="content-info ellipsis"></div>
              <div class="content-info ellipsis">
                <span>开始时间：{{ exam.startTime }}</span>
              </div>
              <div class="handler">
                <span data-title="修改" @click="edit(exam.id)">
                  <i class="common common-edit" />
                </span>
                <span data-title="删除" @click="del(exam.id)">
                  <i class="common common-delete" />
                </span>
                <span data-title="时间变更" @click="timeUpdate(exam.id)">
                  <i class="common common-time" />
                </span>
                <span data-title="邮件通知" @click="email(exam.id)">
                  <i class="el-icon-message" />
                </span>
                <span data-title="试题统计" @click="statistics(exam.id)">
                  <i class="common common-statistics" />
                </span>
                <!-- <span class="last-span">
                  <i class="common common-more-row" />
                  <div class="handler-more">
                    <div class="more-item" @click="exports(exam.id)">
                      <i class="common common-statistics" />导出
                    </div>
                  </div>
                </span> -->
              </div>
            </div>
          </div>
        </div>
        <!-- 分页 -->
        <el-pagination
          background
          layout="prev, pager, next"
          prev-text="上一页"
          next-text="下一页"
          hide-on-single-page
          :total="total"
          :page-size="pageSize"
          :current-page="curPage"
          @current-change="pageChange"
        />
      </div>
    </template>
    <router-view v-else />
  </div>
</template>

<script>
import { examListpage } from 'api/exam'

import ListCard from 'components/ListCard/ListCard.vue'
import AddCard from 'components/ListCard/AddCard.vue'
export default {
  components: {
    AddCard,
    ListCard
  },
  data() {
    return {
      pageSize: 5,
      total: 0,
      curPage: 1,
      queryForm: {
        name: '',
        examTypeId: 0,
        examTypeName: ''
      },
      examList: []
    }
  },
  computed: {
    hashChildren() {
      return !(this.$route.matched.length > 2)
    }
  },
  mounted() {
    this.queryForm.examTypeId = this.$route.params.id
    this.query()
  },
  methods: {
    // 查询
    async query() {
      const examList = await examListpage({
        examTypeId: this.queryForm.examTypeId,
        name: this.queryForm.name,
        curPage: this.curPage,
        pageSize: this.pageSize
      })
      this.examList = examList.data.list
      this.total = examList.data.total
    },
    // 条件查询
    search() {
      this.curPage = 1
      this.query()
    },

    // 考试添加
    add() {
      this.$tools.switchTab('ExamNav', {
        id: 0,
      })
    },
    // 考试修改
    edit(id) {
      this.$tools.switchTab('ExamNav', {
        id,
      })
    },
    // 考试删除
    del(id) {
      this.$tools.switchTab('ExamListSetting', {
        id,
        tab: '2'
      })
    },
    // 邮件通知
    email(id) {
      this.$tools.switchTab('ExamListSetting', {
        id,
        tab: '3'
      })
    },
    // 试卷导出
    exports(id) {
      this.$tools.switchTab('ExamListSetting', {
        id,
        tab: '4'
      })
    },
    // 时间变更
    timeUpdate(id) {
      this.$tools.switchTab('ExamListSetting', {
        id,
        tab: '1'
      })
    },
    // // 考试用户
    // read({ id }) {
    //   this.$tools.switchTab('ExamListMarkSetting', {
    //     id,
    //     examTypeId: this.$route.params.id
    //   })
    // },
    // // 在线用户
    // onLine({ id }) {
    //   this.$tools.switchTab('ExamListLine', {
    //     id,
    //     examTypeId: this.$route.params.id
    //   })
    // },
    // 统计
    statistics(id) {
      this.$tools.switchTab('ExamListStatistics', {
        id,
      })
    },
    // 切换分页
    pageChange(val) {
      val && (this.curPage = val)
      this.query()
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
.custom_select {
  width: 100%;
}

/deep/ .el-dialog__header {
  padding: 0;
}

.exam-type {
  display: flex;
}

.type-item {
  width: 150px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  border: 1px solid #d8d8d8;
  font-size: 14px;
  margin-right: 10px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  i {
    font-size: 24px;
    margin-right: 10px;
  }
  .common-selected {
    position: absolute;
    right: 5px;
    bottom: 8px;
    font-size: 12px;
    margin-right: 0;
    line-height: 0;
    color: #fff;
    &::after {
      content: '';
      display: block;
      position: absolute;
      z-index: 10;
      right: -6px;
      bottom: -9px;
      border-bottom: 25px solid #1e9fff;
      border-left: 25px solid transparent;
    }
    &::before {
      position: absolute;
      z-index: 50;
      right: -3px;
      bottom: -3px;
    }
  }
}

.type-item-active {
  border: 1px solid #1e9fff;
  color: #1e9fff;
}

.exam-remark {
  display: flex;
  margin-top: 15px;
}

.remark-percentage {
  width: 120px;
  margin: 0 5px;
}

.remark-content {
  width: 230px;
}

.remark-buttons {
  margin: 15px 0;
}

.line-user {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999;
  .common-onLine {
    font-size: 70px;
  }
  .line-name {
    font-size: 16px;
  }

  .line-time {
    font-size: 13px;
  }
}
.line {
  color: #1e9fff;
}
.stat-desc {
  margin-top: 30px;
}
</style>
