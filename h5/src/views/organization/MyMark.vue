<template>
  <div class="container">
    <div class="content">
      <div class="search">
        <el-form :inline="true" :model="queryForm" class="form-inline" ref="queryForm">
          <el-form-item label prop="examName">
            <el-input placeholder="请输入考试名称" v-model="queryForm.examName"></el-input>
          </el-form-item>
          <el-form-item style="width: 200px">
            <el-button @click="query" icon="el-icon-search" type="primary">查询</el-button>
            <el-button @click="reset">重置</el-button>
          </el-form-item>
          <!-- <el-form-item>
            <el-button
              @click="editForm.show = true"
              icon="el-icon-search"
              type="primary"
              >添加</el-button
            >
          </el-form-item>-->
        </el-form>
      </div>
      <div class="table">
        <el-table :data="listpage.list" style="width: 100%">
          <el-table-column label="考试名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.examName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="考试开始时间">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.examStartTimeStr }}</span>
            </template>
          </el-table-column>
          <el-table-column label="考试结束时间">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.examEndTimeStr }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷人id">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.markUserId }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷人名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.markUserName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷开始时间">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.markStartTimeStr }}</span>
            </template>
          </el-table-column>
          <el-table-column label="阅卷结束时间">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.markEndTimeStr }}</span>
            </template>
          </el-table-column>
          <el-table-column label="试卷总分">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.paperTotalScore }}</span>
            </template>
          </el-table-column>
          <el-table-column label="考试状态">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.state }}</span>
            </template>
          </el-table-column>
          <el-table-column label="考试状态名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.stateName }}</span>
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
        :page-sizes="listpage.pageSizes"
        :total="listpage.total"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
        layout="total, sizes, prev, pager, next, jumper"
      ></el-pagination>
    </div>
    <el-dialog :visible.sync="editForm.show" title="更新答案">
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="答案" label-width="120px" prop="answer">
          <el-input placeholder="请输入答案" v-model="editForm.answer"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="add" type="primary" v-if="editForm.id == null">添加</el-button>
        <el-button @click="edit" type="primary" v-if="editForm.id != null">修改</el-button>
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
        //列表数据
        total: 0, // 总条数
        curPage: 1, //当前第几页
        pageSize: 10, //每页多少条
        pageSizes: [10, 20, 50, 100], //每页多少条
        list: [], //列表数据
      },
      queryForm: {
        //查询表单
        examName: null,
      },
      editForm: {
        //修改表单
        id: null, //主键
        answer: null, //答案
        show: false, // 是否显示页面
        rules: {
          //校验
          answer: [{ required: true, message: "请输入答案", trigger: "change" }],
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
      let {
        data: { list, total },
      } = await this.$https.myMarkListPage({
        examName: this.queryForm.examName,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize,
      });
      this.listpage.total = total;
      this.listpage.list = list;
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

        let { code, msg } = await this.$https.myMarkAdd({
          no: this.editForm.no,
        });
        if (code != 200) {
          alert(msg);
          return;
        }

        this.editForm.show = false;
        this.query();
      });
    },
    edit() {
      this.$refs["editForm"].validate(async (valid) => {
        if (!valid) {
          return false;
        }

        let { code, msg } = await this.$https.myMarkEdit({
          id: this.editForm.id,
          no: this.editForm.no,
        });
        if (code != 200) {
          alert(msg);
          return;
        }

        this.editForm.show = false;
        this.query();
      });
    },
    // 获取试题
    async get(id) {
      let res = await this.$https.myMarkGet({ id: id });
      if (res.code != 200) {
        alert(res.msg);
        return;
      }

      this.editForm.show = true;
      this.editForm.id = res.data.id;
      this.editForm.no = res.data.no;
    },
    // 删除
    async del(id) {
      this.$confirm("确定要删除？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        let res = await this.$https.myMarkDel({ id });
        if (res.code != 200) {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }

        this.query();
      });
    },
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
