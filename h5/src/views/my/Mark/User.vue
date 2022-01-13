<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2022-01-11 17:08:31
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 11:01:24
-->
<template>
  <div class="container">
    <el-form :inline="true" :model="queryForm" ref="queryForm">
      <el-row>
        <el-col>
          <el-form-item label prop="name">
            <el-input
              placeholder="请输入名称"
              v-model="queryForm.name"
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
      </el-row>
    </el-form>
    <div class="table">
      <el-table :data="userList" style="width: 100%">
        <el-table-column label="姓名" width="120px" prop="userName">
        </el-table-column>
        <el-table-column label="分数" width="70px" prop="totalScore">
        </el-table-column>
        <el-table-column label="考试状态">
          <template slot-scope="scope">
            <span>{{ scope.row.userId | stateName }}</span>
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
              type="primary"
              size="small"
              @click="goMark(scope.row.userId)"
              >阅卷</el-button
            >
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
        name: null,
      },
    }
  },
  filters: {
    stateName(data) {
      return getOneDict('MY_EXAM_STATE').find((item) => item.no === data)
        .dictValue
    },
    markStateName(data) {
      return getOneDict('MY_EXAM_MARK_STATE').find((item) => item.no === data)
        .dictValue
    },
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
        examId: this.examId,
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
          userId,
        },
      })
    },
  },
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
