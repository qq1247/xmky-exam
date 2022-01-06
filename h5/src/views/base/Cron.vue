<template>
  <div class="container">
    <div class="content">
      <el-form :inline="true" :model="queryForm" ref="queryForm">
        <el-row>
          <el-col :span="17">
            <el-form-item label prop="name">
              <el-input
                @focus="queryForm.name = true"
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
                @click=";(editForm.show = true), (editForm.id = null)"
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
          <el-table-column label="实现类">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.jobClass }}</span>
            </template>
          </el-table-column>
          <el-table-column label="表达式" width="120px">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.cron }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="70px">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.stateName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="最近三次运行时间">
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
              <el-tooltip placement="top" content="编辑">
                <i class="common common-edit" @click="get(scope.row.id)"></i>
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
              <el-tooltip placement="top" content="执行一次">
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
    <el-dialog
      :visible.sync="editForm.show"
      :show-close="false"
      width="40%"
      title="定时任务"
      :close-on-click-modal="false"
      @close="resetData('editForm')"
    >
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="名称" label-width="120px" prop="name">
          <el-input placeholder="请输入名称" v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="实现类" label-width="120px" prop="jobClass">
          <el-input
            placeholder="请输入实现类"
            v-model="editForm.jobClass"
          ></el-input>
        </el-form-item>
        <el-form-item label="cron表达式" label-width="120px" prop="cron">
          <el-input
            placeholder="请输入cron表达式"
            v-model="editForm.cron"
          ></el-input>
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
import {
  cronListPage,
  cronAdd,
  cronEdit,
  cronGet,
  cronDel,
  cronStartTask,
  cronStopTask,
  cronrunOnceTask,
} from 'api/base'
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
      editForm: {
        // 修改表单
        id: null, // 主键
        name: null, // 名称
        jobClass: null, // 实现类
        cron: null, // 表达式
        show: false, // 是否显示页面
        rules: {
          // 校验
          name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
          jobClass: [
            { required: true, message: '请输入实现类', trigger: 'blur' },
          ],
          cron: [{ required: true, message: '请输入表达式', trigger: 'blur' }],
        },
      },
    }
  },
  created() {
    this.init()
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
    // 重置
    async reset() {
      this.listpage.curPage = 1
      this.$refs['queryForm'].resetFields()
      this.query()
    },
    handleSizeChange(val) {
      this.listpage.pageSize = val
      this.query()
    },
    handleCurrentChange(val) {
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
        const { code, msg } = await cronAdd({
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron,
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

        const { code, msg } = await cronEdit({
          id: this.editForm.id,
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron,
        })
        if (code != 200) {
          alert(msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    // 获取试题
    async get(id) {
      const res = await cronGet({ id: id })
      if (res.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.show = true
      this.$nextTick(() => {
        this.editForm.id = res.data.id
        this.editForm.name = res.data.name
        this.editForm.jobClass = res.data.jobClass
        this.editForm.cron = res.data.cron
      })
    },
    // 删除
    async del(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await cronDel({ id })
        if (res?.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
    },
    // 启动任务
    async startTask(id) {
      this.$confirm('确定要启动任务？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await cronStartTask({ id })
        if (res?.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
    },
    // 停止任务
    async stopTask(id) {
      this.$confirm('确定要停止任务？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await cronStopTask({ id })
        if (res?.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
    },
    // 执行一次
    async onceTask(id) {
      this.$confirm('确定要执行一次？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await cronrunOnceTask({ id })
        if (res?.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
    resetData(name) {
      this.$refs[name].resetFields()
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
