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
            <span>{{ scope.row.userName || `匿名${scope.$index + 1}` }}</span>
          </template>
        </el-table-column>
        <el-table-column label="分数">
          <template slot-scope="scope">
            <span
              :style="{
                color:
                  Number(
                    (
                      (scope.row.paperPassScore / 100) *
                      scope.row.paperTotalScore
                    ).toFixed(2)
                  ) > scope.row.totalScore
                    ? '#FF5722'
                    : '#5FB878',
              }"
            >{{ scope.row.totalScore || '--' }}</span>
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
            <span>{{ scope.row.state | stateName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="阅卷状态">
          <template slot-scope="scope">
            <span>{{ scope.row.markState | markStateName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              :type="scope.row.state === 1 ? 'danger' : 'primary'"
              size="small"
              :disabled="scope.row.state === 1 ? true : false"
              @click="goMark(scope.row.userId)"
            >阅卷</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { myMarkUserList } from 'api/my'
import { getOneDict } from '@/utils/getDict'
export default {
  components: {},
  filters: {
    stateName(data) {
      return getOneDict('MY_EXAM_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    markStateName(data) {
      return getOneDict('MY_EXAM_MARK_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    }
  },
  props: {},
  data() {
    return {
      examId: null,
      paperId: null,
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
    const { examId, paperId, preview } = this.$route.params
    this.examId = examId
    this.paperId = paperId
    this.preview = preview
    this.query()
  },
  methods: {
    // 查询
    async query() {
      const { data } = await await myMarkUserList({
        userName: this.queryForm.name,
        examId: this.examId
      })
      this.userList = data
    },
    // 我的阅卷操作
    goMark(userId) {
      this.$router.push({
        name: 'MyMarkIndexDetail',
        params: {
          examId: this.examId,
          paperId: this.paperId,
          preview: this.preview,
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
