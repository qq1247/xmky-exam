<template>
  <div class="container">
    <div class="content">
      <div class="search">
        <el-form
          :inline="true"
          :model="queryForm"
          class="form-inline"
          ref="queryForm"
        >
          <el-form-item label prop="name">
            <el-input
              placeholder="请输入名称"
              v-model="queryForm.name"
            ></el-input>
          </el-form-item>
          <el-form-item style="width: 200px">
            <el-button @click="query" icon="el-icon-search"
type="primary"
              >查询</el-button
            >
            <el-button @click="reset">重置</el-button>
          </el-form-item>
          <el-form-item>
            <el-button
              @click="editForm.show = true"
              icon="el-icon-search"
              type="primary"
              >添加</el-button
            >
          </el-form-item>
        </el-form>
      </div>
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
              <span
                v-for="item in scope.row.recentTriggerTime.split('；')"
                :key="item"
              >
                <el-tag effect="plain" v-if="item" style="margin-bottom: 3px">
                  {{ item }}
                </el-tag>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="get(scope.row.id)" size="mini">修改</el-button>
              <el-button @click="del(scope.row.id)" size="mini"
type="danger"
                >删除</el-button
              >
              <el-button
                @click="startTask(scope.row.id)"
                size="mini"
                type="primary"
                >启动任务</el-button
              >
              <el-button
                @click="stopTask(scope.row.id)"
                size="mini"
                type="danger"
                >停止任务</el-button
              >
              <el-button
                @click="onceTask(scope.row.id)"
                size="mini"
                type="primary"
                >执行一次</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        :current-page="listpage.curPage"
        :page-size="listpage.pageSize"
        :page-sizes="listpage.pageSizes"
        :total="listpage.total"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
        layout="total, sizes, prev, pager, next, jumper"
      ></el-pagination>
    </div>
    <el-dialog :visible.sync="editForm.show" title="定时任务">
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
        <el-button @click="add" type="primary"
v-if="editForm.id == null"
          >添加</el-button
        >
        <el-button @click="edit" type="primary"
v-if="editForm.id != null"
          >修改</el-button
        >
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
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
          name: [{ required: true, message: "请输入名称", trigger: "change" }],
          jobClass: [
            { required: true, message: "请输入实现类", trigger: "change" },
          ],
          cron: [
            { required: true, message: "请输入表达式", trigger: "change" },
          ],
        },
      },
    };
  },
  created() {
    this.init();
  },
  methods: {
    // 查询
    async query() {
      const {
        data: { rows, total },
      } = await this.$https.cronListpage({
        name: this.queryForm.name,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize,
      });
      this.listpage.total = total;
      this.listpage.list = rows;
    },
    // 重置
    async reset() {
      this.listpage.curPage = 1;
      this.$refs["queryForm"].resetFields();
      this.query();
    },
    handleSizeChange(val) {
      this.listpage.pageSize = val;
      this.query();
    },
    handleCurrentChange(val) {
      this.listpage.curPage = val;
      this.query();
    },
    // 初始化
    async init() {
      this.query();
    },
    add() {
      this.$refs["editForm"].validate(async (valid) => {
        if (!valid) {
          return false;
        }

        const { code, msg } = await this.$https.cronAdd({
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron,
        });
        if (code != 200) {
          alert(msg);
          return
        }

        this.editForm.show = false;
        this.query();
      })
    },
    edit() {
      this.$refs["editForm"].validate(async (valid) => {
        if (!valid) {
          return false;
        }

        const { code, msg } = await this.$https.cronEdit({
          id: this.editForm.id,
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron,
        });
        if (code != 200) {
          alert(msg);
          return
        }

        this.editForm.show = false;
        this.query();
      })
    },
    // 获取试题
    async get(id) {
      const res = await this.$https.cronGet({ id: id });
      if (res.code != 200) {
        alert(res.msg);
        return
      }

      this.editForm.show = true;
      this.editForm.id = res.data.id;
      this.editForm.name = res.data.name;
      this.editForm.jobClass = res.data.jobClass;
      this.editForm.cron = res.data.cron;
    },
    // 删除
    async del(id) {
      this.$confirm("确定要删除？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        const res = await this.$https.cronDel({ id });
        if (res.code != 200) {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }

        this.query();
      })
    },
    // 启动任务
    async startTask(id) {
      this.$confirm("确定要启动任务？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        const res = await this.$https.cronStartTask({ id });
        if (res.code != 200) {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }

        this.query();
      })
    },
    // 停止任务
    async stopTask(id) {
      this.$confirm("确定要停止任务？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        const res = await this.$https.cronStopTask({ id });
        if (res.code != 200) {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }

        this.query();
      })
    },
    // 执行一次
    async onceTask(id) {
      this.$confirm("确定要执行一次？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        const res = await this.$https.cronrunOnceTask({ id });
        if (res.code != 200) {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }

        this.query();
      })
    }
  },
};
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: center;
  padding-top: 120px;
  .content {
    width: 1200px;
    .search {
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
    }
  }
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
</style>
