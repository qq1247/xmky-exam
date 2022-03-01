<template>
  <div class="container">
    <div class="content" v-if="hashChildren">
      <el-form :inline="true" :model="queryForm" ref="queryForm">
        <el-row>
          <el-col :span="17">
            <el-form-item label prop="name">
              <el-input
                @focus="queryForm.queryShow = true"
                placeholder="请输入标题"
                v-model="queryForm.title"
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
          <el-table-column label="标题">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column label="置顶">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{
                scope.row.showType === 2 ? '是' : '否'
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="读权限">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{
                scope.row.readUserNames || '所有人'
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300">
            <template slot-scope="scope">
              <el-tooltip placement="top" content="修改">
                <i class="common common-edit" @click="edit(scope.row.id)"></i>
              </el-tooltip>
              <el-tooltip placement="top" content="删除">
                <i class="common common-delete" @click="del(scope.row.id)"></i>
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
import { bulletinListPage } from 'api/base'

export default {
  data() {
    return {
      // 列表数据
      listpage: {
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        list: [], // 列表数据
      },
      // 查询表单
      queryForm: {
        title: null,
        queryShow: false,
      },
    }
  },
  computed: {
    hashChildren() {
      return this.$route.matched.length > 2 ? false : true
    },
  },
  created() {
    this.init()
  },
  methods: {
    // 查询
    async query(curPage = 1) {
      this.queryForm.queryShow = false

      const {
        data: { list, total },
      } = await bulletinListPage({
        title: this.queryForm.title,
        curPage: curPage,
        pageSize: this.listpage.pageSize,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 初始化
    async init() {
      await this.query()
    },

    // 添加
    add() {
      this.$tools.switchTab('BulletinIndexSetting', {
        id: 0,
        tab: '1',
      })
    },
    // 修改
    edit(id) {
      this.$tools.switchTab('BulletinIndexSetting', {
        id,
        tab: '1',
      })
    },
    // 删除
    del(id) {
      this.$tools.switchTab('BulletinIndexSetting', {
        id,
        tab: '2',
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

/deep/.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
/deep/.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
/deep/.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
/deep/.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
