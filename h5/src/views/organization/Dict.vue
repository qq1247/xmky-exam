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
          <el-form-item label prop="dictIndex">
            <el-input
              placeholder="请输入索引"
              v-model="queryForm.dictIndex"
            ></el-input>
          </el-form-item>
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
          <el-form-item style="width: 200px">
            <el-button @click="query" icon="el-icon-search" type="primary"
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
          <el-table-column label="索引">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.DICT_INDEX }}</span>
            </template>
          </el-table-column>
          <el-table-column label="键">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.DICT_KEY }}</span>
            </template>
          </el-table-column>
          <el-table-column label="值">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.DICT_VALUE }}</span>
            </template>
          </el-table-column>
          <el-table-column label="排序">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.NO }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="get(scope.row.ID)" size="mini">修改</el-button>
              <el-button @click="del(scope.row.ID)" size="mini" type="danger"
                >删除</el-button
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
        dictIndex: null,
        dictKey: null,
        dictValue: null,
      },
      editForm: {
        //修改表单
        id: null, //主键
        dictIndex: null, //索引
        dictKey: null, //key
        dictValue: null, //值
        no: null, //排序
        show: false, // 是否显示页面
        rules: {
          //校验
          dictIndex: [
            { required: true, message: "请输入排序", trigger: "change" },
          ],
          dictKey: [
            { required: true, message: "请输入排序", trigger: "change" },
          ],
          dictValue: [
            { required: true, message: "请输入排序", trigger: "change" },
          ],
          no: [{ required: true, message: "请输入排序", trigger: "change" }],
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
        data: { rows, total },
      } = await this.$https.dictListpage({
        dictIndex: this.queryForm.dictIndex,
        dictKey: this.queryForm.dictKey,
        dictValue: this.queryForm.dictValue,
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

        let { code, msg } = await this.$https.dictAdd({
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
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

        let { code, msg } = await this.$https.dictEdit({
          id: this.editForm.id,
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
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
      let res = await this.$https.dictGet({ id: id });
      if (res.code != 200) {
        alert(res.msg);
        return;
      }

      this.editForm.show = true;
      this.editForm.id = res.data.id;
      this.editForm.dictIndex = res.data.dictIndex;
      this.editForm.dictKey = res.data.dictKey;
      this.editForm.dictValue = res.data.dictValue;
      this.editForm.no = res.data.no;
    },
    // 删除
    async del(id) {
      this.$confirm("确定要删除？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        let res = await this.$https.dictDel({ id });
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
    width: 1170px;
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
