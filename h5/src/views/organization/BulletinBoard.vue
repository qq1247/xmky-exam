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
          <el-form-item label prop="title">
            <el-input
              placeholder="请输入标题"
              v-model="queryForm.title"
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
          <el-table-column label="标题">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column label="跳转链接">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.url }}</span>
            </template>
          </el-table-column>
          <el-table-column label="排序">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.no }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="get(scope.row.id)" size="mini">修改</el-button>
              <el-button @click="del(scope.row.id)" size="mini"
type="danger"
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
    <el-dialog :visible.sync="editForm.show" title="公告栏">
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="标题" label-width="120px" prop="title">
          <el-input
            placeholder="请输入标题"
            v-model="editForm.title"
          ></el-input>
        </el-form-item>
        <el-form-item label="图片" label-width="120px" prop="imgs">
          <el-input placeholder="请输入图片" v-model="editForm.imgs"></el-input>
        </el-form-item>
        <el-form-item label="视频" label-width="120px" prop="video">
          <el-input
            placeholder="请输入视频"
            v-model="editForm.video"
          ></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <Editor
            :value="editForm.content"
            id="content"
            @editorListener="editorListener"
          ></Editor>
        </el-form-item>
        <el-form-item label="图片高" label-width="120px" prop="imgsHeight">
          <el-input
            placeholder="请输入图片高"
            v-model="editForm.imgsHeight"
          ></el-input>
        </el-form-item>
        <el-form-item label="图片宽" label-width="120px" prop="imgsWidth">
          <el-input
            placeholder="请输入图片宽"
            v-model="editForm.imgsWidth"
          ></el-input>
        </el-form-item>
        <el-form-item label="跳转链接" label-width="120px" prop="url">
          <el-input
            placeholder="请输入跳转链接"
            v-model="editForm.url"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="是否在首页展示"
          label-width="120px"
          prop="topState"
        >
          <el-input
            placeholder="请输入是否在首页展示"
            v-model="editForm.topState"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" label-width="120px" prop="no">
          <el-input-number
            :max="100"
            :min="1"
            v-model.number="editForm.no"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="用户读权限" label-width="120px" prop="readUserIds">
          <el-input
            placeholder="请输入用户读权限"
            v-model="editForm.readUserIds"
          ></el-input>
        </el-form-item>
        <el-form-item label="机构读权限" label-width="120px" prop="readOrgIds">
          <el-input
            placeholder="请输入机构读权限"
            v-model="editForm.readOrgIds"
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
import Editor from '@/components/Editor.vue'
export default {
  components: {
    Editor,
  },
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
        title: null,
      },
      editForm: {
        // 修改表单
        id: null, // 主键
        title: null, // 标题
        imgs: null, // 图片
        video: null, // 视频
        content: null, // 内容
        imgsHeight: null, // 图片高
        imgsWidth: null, // 图片宽
        url: null, // 跳转
        topState: null, // 是否在首页展示
        no: null, // 排序
        readUserIds: null, // 用户读权限
        readOrgIds: null, // 机构读权限
        show: false, // 是否显示页面
        rules: {
          // 校验
          title: [{ required: true, message: '请输入标题', trigger: 'change' }],
          content: [
            { required: true, message: '请输入内容', trigger: 'change' },
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
      const {
        data: { list, total },
      } = await this.$https.bulletinBoardListpage({
        title: this.queryForm.title,
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

        const { code, msg } = await this.$https.bulletinBoardAdd({
          title: this.editForm.title,
          imgs: this.editForm.imgs,
          video: this.editForm.video,
          content: this.editForm.content,
          imgsHeight: this.editForm.imgsHeight,
          imgsWidth: this.editForm.imgsWidth,
          url: this.editForm.url,
          topState: this.editForm.topState,
          no: this.editForm.no,
          readUserIds: this.editForm.readUserIds,
          readOrgIds: this.editForm.readOrgIds,
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

        const { code, msg } = await this.$https.bulletinBoardEdit({
          id: this.editForm.id,
          title: this.editForm.title,
          imgs: this.editForm.imgs,
          video: this.editForm.video,
          content: this.editForm.content,
          imgsHeight: this.editForm.imgsHeight,
          imgsWidth: this.editForm.imgsWidth,
          url: this.editForm.url,
          topState: this.editForm.topState,
          no: this.editForm.no,
          readUserIds: this.editForm.readUserIds,
          readOrgIds: this.editForm.readOrgIds,
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
      const res = await this.$https.bulletinBoardGet({ id: id })
      if (res?.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.show = true
      this.editForm.id = res.data.id
      this.editForm.title = res.data.title
      this.editForm.imgs = res.data.imgs
      this.editForm.video = res.data.video
      this.editForm.content = res.data.content
      this.editForm.imgsHeight = res.data.imgsHeight
      this.editForm.imgsWidth = res.data.imgsWidth
      this.editForm.url = res.data.url
      this.editForm.topState = res.data.topState
      this.editForm.no = res.data.no
      this.editForm.readUserIds = res.data.readUserIds
      this.editForm.readOrgIds = res.data.readOrgIds
    },
    // 删除
    async del(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await this.$https.bulletinBoardDel({ id })
        if (res?.code != 200) {
          this.$message({
            type: 'error',
            message: res.msg,
          })
        }

        this.query()
      })
    },
    // 编辑器监听事件
    editorListener(id, value) {
      this.editForm[id] = value
    },
  },
}
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
