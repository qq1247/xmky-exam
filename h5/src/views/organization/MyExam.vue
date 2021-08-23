<template>
  <div class="container">
    <div class="content">
      <el-form :inline="true" :model="queryForm" ref="queryForm">
        <el-row>
          <el-col :span="17">
            <el-form-item label prop="examName">
              <el-input
                @focus="queryForm.queryShow = true"
                placeholder="请输入考试名称"
                v-model="queryForm.examName"
              ></el-input>
            </el-form-item>
            <el-button
              @click="query"
              class="query-search"
              icon="el-icon-search"
              type="primary"
              >查询</el-button
            >
          </el-col>
          <el-col :span="7">
            <el-form-item style="float: right"></el-form-item>
          </el-col>
        </el-row>
        <div v-if="queryForm.queryShow"></div>
      </el-form>
      <div class="table">
        <el-table :data="listpage.list" style="width: 100%">
          <el-table-column label="考试名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.examName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="考试时间">
            <template slot-scope="scope">
              <span style="margin-left: 10px; display: block">{{
                scope.row.examStartTime
              }}</span>
              <span style="margin-left: 10px">{{ scope.row.examEndTime }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷人">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{
                scope.row.markUserName
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷时间">
            <template slot-scope="scope">
              <span style="margin-left: 10px; display: block">{{
                scope.row.markStartTime
              }}</span>
              <span style="margin-left: 10px">{{ scope.row.markEndTime }}</span>
            </template>
          </el-table-column>
          <el-table-column label="试卷分数">
            <template slot-scope="scope">
              <span style="margin-left: 10px"
                >{{ scope.row.totalScore }}/{{
                  scope.row.paperTotalScore
                }}</span
              >
            </template>
          </el-table-column>
          <el-table-column label="状态">
            <template slot-scope="scope">
              <span style="margin-left: 10px"
                >{{ scope.row.stateName }}/{{ scope.row.markStateName }}</span
              >
            </template>
          </el-table-column>
          <!-- <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="get(scope.row.id)" size="mini">修改</el-button>
              <el-button @click="del(scope.row.id)" size="mini" type="danger"
                >删除</el-button
              >
            </template>
          </el-table-column>-->
        </el-table>
      </div>
      <el-pagination
        :current-page="listpage.curPage"
        :page-size="listpage.pageSize"
        :total="listpage.total"
        @current-change="pageChange"
        background
        hide-on-single-page
        layout="prev, pager, next"
        next-text="下一页"
        prev-text="上一页"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import { myExamListPage } from '@/api/my'
export default {
  data() {
    return {
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        pageSizes: [10, 20, 50, 100], // 每页多少条
        list: [], // 列表数据
      },
      queryForm: {
        // 查询表单
        examName: null,
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 查询
    async query() {
      const {
        data: { list, total },
      } = await myExamListPage({
        examName: this.queryForm.examName,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    sizeChange(val) {
      this.listpage.pageSize = val
      this.query()
    },
    pageChange(val) {
      this.listpage.curPage = val
      this.query()
    },
    // 初始化
    async init() {
      this.query()
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: center;
  .content {
    width: 1170px;
  }
}
.query-search {
  width: 150px;
  height: 40px;
  border-radius: 5px;
}
.el-input {
  width: 300px;
  float: left;
}
.el-input-number {
  float: left;
}

.el-dialog__title {
  float: left;
}
/deep/ .el-table th {
  background-color: #d3dce6;
  color: #475669;
  text-align: center;
  height: 55px;
}
/deep/ .el-table td {
  color: #8392a6;
  font-size: 12px;
  text-align: center;
  border-bottom: 1px solid #ddd;
}
/deep/ .common {
  padding-right: 10px;
  color: #0096e7;
  font-style: inherit;
  font-weight: bold;
}
.el-link {
  padding-right: 20px;
  color: #8392a6;
  font-size: 12px;
}
/deep/ .el-input__inner:focus {
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 8px rgba(102, 175, 233, 0.6);
  border: 1px solid #f2f4f5;
}
.common-arrow-down {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

/deep/.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #0095e5;
  color: #fff;
}

/deep/.el-pagination.is-background .btn-next,
/deep/.el-pagination.is-background .btn-prev,
/deep/.el-pagination.is-background .el-pager li {
  margin: 0 3px;
  min-width: 35px;
  border: 1px solid #d4dfd9;
  background-color: #fff;
  padding: 0 10px;
}
</style>
