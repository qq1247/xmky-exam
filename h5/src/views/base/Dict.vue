<template>
  <div class="container">
    <div class="content">
      <el-form :inline="true" :model="queryForm" ref="queryForm">
        <el-row>
          <el-col :span="17">
            <el-form-item label prop="dictIndex">
              <el-input
                @focus="queryForm.queryShow = true"
                placeholder="请输入索引"
                v-model="queryForm.dictIndex"
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
                @click="editForm.show = true"
                icon="el-icon-circle-plus-outline"
                size="mini"
                type="primary"
                >添加</el-button
              >
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="queryForm.queryShow">
          <el-form-item label prop="dictKey">
            <el-input
              placeholder="请输入键"
              v-model="queryForm.dictKey"
            ></el-input>
          </el-form-item>
          <el-form-item label prop="dictValue">
            <el-input
              placeholder="请输入值"
              v-model="queryForm.dictValue"
            ></el-input>
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
              <el-link
                :underline="false"
                @click="get(scope.row.id)"
                icon="common common-edit"
                >修改</el-link
              >
              <el-link
                :underline="false"
                @click="del(scope.row.id)"
                icon="common common-delete"
                >删除</el-link
              >
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
    <el-dialog :visible.sync="editForm.show" title="数据字典">
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="索引" label-width="120px" prop="dictIndex">
          <el-input
            placeholder="请输入索引"
            v-model="editForm.dictIndex"
          ></el-input>
        </el-form-item>
        <el-form-item label="键" label-width="120px" prop="dictKey">
          <el-input
            placeholder="请输入键"
            v-model="editForm.dictKey"
          ></el-input>
        </el-form-item>
        <el-form-item label="值" label-width="120px" prop="dictValue">
          <el-input
            placeholder="请输入值"
            v-model="editForm.dictValue"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" label-width="120px" prop="no">
          <el-input-number
            :max="100"
            :min="1"
            v-model.number="editForm.no"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="add" type="primary" v-if="editForm.id == null"
          >添加</el-button
        >
        <el-button @click="edit" type="primary" v-if="editForm.id != null"
          >修改</el-button
        >
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { dictListPage, dictAdd, dictEdit, dictGet, dictDel } from 'api/base'
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
        dictIndex: null,
        dictKey: null,
        dictValue: null,
        queryShow: false,
      },
      editForm: {
        // 修改表单
        id: null, // 主键
        dictIndex: null, // 索引
        dictKey: null, // key
        dictValue: null, // 值
        no: null, // 排序
        show: false, // 是否显示页面
        rules: {
          // 校验
          dictIndex: [
            { required: true, message: '请输入排序', trigger: 'change' },
          ],
          dictKey: [
            { required: true, message: '请输入排序', trigger: 'change' },
          ],
          dictValue: [
            { required: true, message: '请输入排序', trigger: 'change' },
          ],
          no: [{ required: true, message: '请输入排序', trigger: 'change' }],
        },
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 查询
    async query() {
      this.queryForm.queryShow = false
      const {
        data: { list, total },
      } = await dictListPage({
        dictIndex: this.queryForm.dictIndex,
        dictKey: this.queryForm.dictKey,
        dictValue: this.queryForm.dictValue,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 重置
    async reset() {
      this.listpage.curPage = 1
      this.$refs['queryForm'].resetFields()
      this.query()
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
    add() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await dictAdd({
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
          no: this.editForm.no,
        })
        if (code != 200) {
          alert(msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await dictEdit({
          id: this.editForm.id,
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
          no: this.editForm.no,
        })
        if (code != 200) {
          alert(msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    async get(id) {
      const res = await dictGet({ id: id })
      if (res.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.show = true
      this.editForm.id = res.data.id
      this.editForm.dictIndex = res.data.dictIndex
      this.editForm.dictKey = res.data.dictKey
      this.editForm.dictValue = res.data.dictValue
      this.editForm.no = res.data.no
    },
    // 删除
    async del(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await dictDel({ id })
        if (res?.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
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
