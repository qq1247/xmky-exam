<template>
  <div class="container">
    <template v-if="hashChildren">
      <el-form ref="queryForm" :inline="true" :model="queryForm">
        <el-row>
          <el-col :span="17">
            <el-form-item label prop="dictIndex">
              <el-input
                v-model="queryForm.dictIndex"
                placeholder="请输入索引"
                @focus="queryForm.queryShow = true"
              />
            </el-form-item>
            <el-button
              class="query-search"
              icon="el-icon-search"
              type="primary"
              @click="query"
            >查询</el-button>
          </el-col>
          <el-col :span="7">
            <el-form-item style="float: right">
              <el-button
                icon="el-icon-circle-plus-outline"
                size="mini"
                type="primary"
                @click="add"
              >添加</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="queryForm.queryShow">
          <el-form-item label prop="dictKey">
            <el-input v-model="queryForm.dictKey" placeholder="请输入键" />
          </el-form-item>
          <el-form-item label prop="dictValue">
            <el-input v-model="queryForm.dictValue" placeholder="请输入值" />
          </el-form-item>
        </div>
      </el-form>
      <div class="table">
        <el-table :data="listpage.list" style="width: 100%">
          <el-table-column label="索引">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.dictIndex }}</span>
            </template>
          </el-table-column>
          <el-table-column label="键">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.dictKey }}</span>
            </template>
          </el-table-column>
          <el-table-column label="值">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.dictValue }}</span>
            </template>
          </el-table-column>
          <el-table-column label="排序">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.no }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-tooltip placement="top" content="修改">
                <i class="common common-edit" @click="edit(scope.row.id)" />
              </el-tooltip>
              <el-tooltip placement="top" content="删除">
                <i class="common common-delete" @click="del(scope.row.id)" />
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        :current-page="listpage.curPage"
        :page-size="listpage.pageSize"
        :total="listpage.total"
        background
        hide-on-single-page
        layout="prev, pager, next"
        next-text="下一页"
        prev-text="上一页"
        @current-change="pageChange"
      />
    </template>
    <router-view v-else />
  </div>
</template>

<script>
import { dictListPage } from 'api/base'

export default {
  data() {
    return {
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        pageSizes: [10, 20, 50], // 每页多少条
        list: [] // 列表数据
      },
      queryForm: {
        // 查询表单
        dictIndex: null,
        dictKey: null,
        dictValue: null,
        queryShow: false
      }
    }
  },
  computed: {
    hashChildren() {
      return !(this.$route.matched.length > 2)
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 初始化
    async init() {
      this.query()
    },
    // 查询
    async query() {
      this.queryForm.queryShow = false
      const {
        data: { list, total }
      } = await dictListPage({
        dictIndex: this.queryForm.dictIndex,
        dictKey: this.queryForm.dictKey,
        dictValue: this.queryForm.dictValue,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 分页查询
    pageChange(val) {
      this.listpage.curPage = val
      this.query()
    },
    // 添加
    add() {
      this.$tools.switchTab('DictIndexSetting', {
        id: 0,
        tab: '1'
      })
    },
    // 修改
    edit(id) {
      this.$tools.switchTab('DictIndexSetting', {
        id,
        tab: '1'
      })
    },
    // 删除
    del(id) {
      this.$tools.switchTab('DictIndexSetting', {
        id,
        tab: '2'
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
.common-arrow-down {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}
</style>
