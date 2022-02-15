<template>
  <div class="container">
    <div class="content" v-if="hashChildren">
      <el-form :inline="true" :model="queryForm" ref="queryForm">
        <el-row>
          <el-col :span="17">
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
          <el-col :span="7">
            <el-form-item style="float: right">
              <el-button
                @click="add"
                icon="el-icon-circle-plus-outline"
                size="mini"
                type="primary"
                >添加</el-button
              >
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="queryForm.queryShow"></div>
      </el-form>
      <div class="table">
        <el-table :data="listpage.list" style="width: 100%">
          <el-table-column label="名称" width="120px">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="70px">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.stateName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="最近三次触发时间">
            <template slot-scope="scope">
              <span :key="item" v-for="item in scope.row.triggerTimes">
                <el-tag effect="plain" style="margin-bottom: 3px" v-if="item">{{
                  item
                }}</el-tag>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-tooltip placement="top" content="修改">
                <i class="common common-edit" @click="edit(scope.row.id)"></i>
              </el-tooltip>
              <el-tooltip placement="top" content="删除">
                <i class="common common-delete" @click="del(scope.row.id)"></i>
              </el-tooltip>
              <el-tooltip placement="top" content="启动任务">
                <i
                  class="common common-start"
                  @click="startTask(scope.row.id)"
                ></i>
              </el-tooltip>
              <el-tooltip placement="top" content="停止任务">
                <i
                  class="common common-end"
                  @click="stopTask(scope.row.id)"
                ></i>
              </el-tooltip>
              <el-tooltip placement="top" content="运行一次">
                <i
                  class="common common-once"
                  @click="onceTask(scope.row.id)"
                ></i>
              </el-tooltip>
            </template>
          </el-table-column>
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
    <router-view v-else></router-view>
  </div>
</template>

<script>
import { cronListPage } from 'api/base'

export default {
  data() {
    return {
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        pageSizes: [10, 20, 50], // 每页多少条
        list: [], // 列表数据
      },
      queryForm: {
        // 查询表单
        name: null,
      },
    }
  },
  computed: {
    hashChildren() {
      return this.$route.matched.length > 2 ? false : true
    },
  },
  created() {
    this.query()
  },
  methods: {
    // 查询
    async query(curPage = 1) {
      const {
        data: { list, total },
      } = await cronListPage({
        name: this.queryForm.name,
        curPage: curPage,
        pageSize: this.listpage.pageSize,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 添加
    add() {
      this.$tools.switchTab('CronIndexSetting', {
        id: 0,
        tab: '1',
      })
    },
    // 修改
    edit(id) {
      this.$tools.switchTab('CronIndexSetting', {
        id,
        tab: '1',
      })
    },
    // 启动任务
    startTask(id) {
      this.$tools.switchTab('CronIndexSetting', {
        id,
        tab: '2',
      })
    },
    // 停止任务
    stopTask(id) {
      this.$tools.switchTab('CronIndexSetting', {
        id,
        tab: '3',
      })
    },
    // 运行一次
    onceTask(id) {
      this.$tools.switchTab('CronIndexSetting', {
        id,
        tab: '4',
      })
    },
    // 删除
    del(id) {
      this.$tools.switchTab('CronIndexSetting', {
        id,
        tab: '5',
      })
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: center;
  .content {
    width: 1200px;
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
