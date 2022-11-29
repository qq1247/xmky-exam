<template>
  <div class="container">
    <el-form ref="queryForm" :inline="true" :model="queryForm">
      <el-row>
        <el-col>
          <el-form-item label prop="name">
            <el-input
              v-model="queryForm.name"
              placeholder="请输入名称"
            />
          </el-form-item>
          <el-button
            class="query-search"
            icon="el-icon-search"
            type="primary"
            @click="query"
          >查询</el-button>
        </el-col>
      </el-row>
    </el-form>
    <div class="table">
      <el-table :data="userList" style="width: 100%">
        <el-table-column label="姓名" width="120px">
          <template slot-scope="scope">
            <span>{{ scope.row.userName }}-{{scope.row.orgName}}</span>
          </template>
        </el-table-column>
        <el-table-column label="客观分/总分">
          <template slot-scope="scope">
            <span :style="{color: scope.row.answerState === 2 ? '#FF5722' : '#5FB878',}">
              {{ scope.row.objectiveScore }} / {{scope.row.totalScore}}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="答题用时">
          <template slot-scope="scope">
            <span>{{
              $tools.computeMinute(
                scope.row.answerStartTime,
                scope.row.answerEndTime
              )
            }}</span>
          </template>
        </el-table-column>
        <el-table-column label="阅卷用时">
          <template slot-scope="scope">
            <span>{{
              $tools.computeMinute(
                scope.row.markStartTime,
                scope.row.markEndTime
              )
            }}</span>
          </template>
        </el-table-column>
        <el-table-column label="考试状态">
          <template slot-scope="scope">
            <span>{{ $tools.getDictValue('EXAM_STATE', scope.row.state) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="阅卷状态">
          <template slot-scope="scope">
            <span>{{ $tools.getDictValue('MARK_STATE', scope.row.markState) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              :type="scope.row.state === 1 ? 'danger' : 'primary'"
              size="small"
              :disabled="scope.row.state === 1 ? true : false"
              @click="toMark(scope.row.userId)"
            >阅卷</el-button>
          </template>
        </el-table-column>
      </el-table>
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

<script>
import { myMarkUserListpage } from 'api/my'
import { getOneDict } from '@/utils/getDict'
export default {
  data() {
    return {
      examId: null,
      preview: false,
      total: 0, // 总条数
      curPage: 1, // 当前第几页
      pageSize: 10, // 每页多少条
      userList: [], // 列表数据
      queryForm: {
        name: null
      }
    }
  },
  mounted() {
    this.examId = this.$route.params.examId
    this.query()
  },
  methods: {
    // 查询
    async query() {
      const { data } = await myMarkUserListpage({
        userName: this.queryForm.name,
        examId: this.examId
      })
      this.userList = data.list
      this.total = data.total
    },
    // 去阅卷
    toMark(userId) {
      this.$router.push({
        name: 'MyMarkIndexPaper',
        params: {
          examId: this.examId,
          userId
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
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
.common {
  padding: 0 10px;
  color: #0096e7;
  font-size: 18px;
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
</style>
