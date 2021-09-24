<template>
  <div class="container">
    <div class="content">
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
                @click="
                  ;(editForm.show = true),
                    (editForm.id = null),
                    (editForm.imgFileId = []),
                    (editForm.content = '')
                "
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
                scope.row.topStateName
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="读权限">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{
                scope.row.readUserNames
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300">
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
    <el-dialog
      :visible.sync="editForm.show"
      :show-close="false"
      width="40%"
      title="公告"
      :close-on-click-modal="false"
      @close="resetData('editForm')"
    >
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="标题" label-width="120px" prop="title">
          <el-input
            placeholder="请输入标题"
            v-model="editForm.title"
          ></el-input>
        </el-form-item>
        <el-form-item label="内容" label-width="120px" prop="content">
          <Editor
            :value="editForm.content"
            @editorListener="editorListener"
            id="content"
          ></Editor>
        </el-form-item>
        <el-form-item label="阅读人员" label-width="120px" prop="examUser">
          <CustomSelect
            ref="readSelect"
            placeholder="请选择用户"
            :value="editForm.examUser"
            :total="editForm.total"
            @input="searchUser"
            @change="selectExamUser"
            @currentChange="getMoreUser"
            @visibleChange="getUserList"
          >
            <el-option
              v-for="item in editForm.examUsers"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </CustomSelect>
        </el-form-item>
        <el-form-item label="置顶展示" label-width="120px" prop="topState">
          <el-switch
            active-color="#13ce66"
            inactive-color="#ff4949"
            v-model="editForm.topState"
            :active-value="1"
            :inactive-value="2"
          ></el-switch>
        </el-form-item>
        <el-form-item label="轮播展示" label-width="120px" prop="state">
          <el-switch
            active-color="#13ce66"
            inactive-color="#ff4949"
            v-model="editForm.state"
            :active-value="2"
            :inactive-value="1"
          ></el-switch>
        </el-form-item>
        <template v-if="editForm.state == 2">
          <el-form-item label="轮播图片" label-width="120px" prop="imgFileId">
            <Upload
              ref="bannerUpload"
              type="image"
              :files="editForm.imgFileId"
              @success="bannerSucess"
              @remove="bannerRemove"
            />
          </el-form-item>
        </template>
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
  bulletinListPage,
  bulletinAdd,
  bulletinEdit,
  bulletinDel,
  bulletinGet,
} from 'api/base'
import { userListPage } from 'api/user'
import Upload from 'components/Upload'
import Editor from 'components/Editor.vue'
import EditHeader from 'components/EditHeader.vue'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    Editor,
    Upload,
    EditHeader,
    CustomSelect,
  },
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
      // 修改表单
      editForm: {
        id: null, // 主键
        title: '', // 标题
        imgFileId: [], // 图片
        content: '', // 内容
        state: 1, // 状态
        topState: 1, // 置顶状态
        show: false, // 是否显示页面
        examUsers: [],
        examUser: [],
        pageSize: 5, // 每页多少条
        total: 0,
        // 校验
        rules: {
          title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        },
      },
      headers: {
        Authorization: this.$store.getters.token,
      },
    }
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
        site: 1,
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 重置
    async reset() {
      this.$refs['queryForm'].resetFields()
      this.listpage.curPage = 1
      this.query()
    },
    // 初始化
    async init() {
      await this.query()
    },
    // 编辑器监听事件
    editorListener(id, value) {
      this.editForm[id] = value
    },
    // 上传banner成功
    bannerSucess(res, file, fileList) {
      this.editForm.imgFileId = fileList
    },
    // 上传banner失败
    bannerClear(ref) {
      if (this.editForm.imgFileId.length > 0) {
        this.$refs[ref].clear()
        this.$set(this.editForm, 'imgFileId', [])
      }
    },
    // 删除banner
    bannerRemove(file, fileList) {
      this.editForm.imgFileId = fileList
    },
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const examUsers = await userListPage({
        name,
        curPage,
        pageSize: this.editForm.pageSize,
      })

      this.editForm.examUsers = examUsers.data.list
      this.editForm.total = examUsers.data.total
    },
    // 获取更多用户
    getMoreUser(curPage, name) {
      this.getUserList(curPage, name)
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(1, name)
    },
    // 选择阅卷考生
    selectExamUser(e) {
      this.editForm.examUser = e
    },
    // 添加机构
    add() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await bulletinAdd({
          title: this.editForm.title,
          content: this.editForm.content,
          readUserIds: this.editForm.examUser,
          topState: this.editForm.topState,
          state: this.editForm.state,
          imgFileId:
            this.editForm.state === 2 && this.editForm.imgFileId.length > 0
              ? this.editForm.imgFileId[0].response.data.fileIds
              : null,
        })

        if (res.code != 200) {
          alert(res.msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    // 修改
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }
        const res = await bulletinEdit({
          id: this.editForm.id,
          title: this.editForm.title,
          imgFileId:
            this.editForm.state === 2 && this.editForm.imgFileId.length > 0
              ? this.editForm.imgFileId[0]?.response
                ? this.editForm.imgFileId[0].response.data.fileIds
                : this.$tools.getQueryParam(
                    this.editForm.imgFileId[0].url,
                    'id'
                  )
              : null,
          content: this.editForm.content,
          readUserIds: this.editForm.examUser,
          topState: this.editForm.topState,
          state: this.editForm.state,
        })

        if (res.code != 200) {
          alert(res.msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    // 删除
    async del(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await bulletinDel({ id })
        if (res.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
    },
    // 获取用户
    async get(id) {
      const res = await bulletinGet({ id: id })
      if (res.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.show = true
      this.editForm.imgFileId = []
      this.editForm.examUser = []
      this.editForm.content = res.data?.content || ''
      this.$nextTick(() => {
        this.editForm.id = res.data.id
        this.editForm.title = res.data.title
        this.editForm.topState = res.data.topState
        this.editForm.state = res.data.state
        if (res.data.readUserNames !== '') {
          const { roleIds: readIds, roleNames: readNames } =
            this.compositionRoles(res.data.readUserIds, res.data.readUserNames)
          this.editForm.examUser.push(...readIds)
          this.$refs['readSelect'].$refs['elSelect'].cachedOptions.push(
            ...readNames
          )
        }
        if (res.data.imgFileId !== '') {
          this.editForm.imgFileId.push({
            url: `${process.env.VUE_APP_BASE_URL}file/download?id=${Number(
              res.data.imgFileId
            )}`,
          })
        }
      })
    },
    // 组合回显数据
    compositionRoles(userIds, userNames) {
      const ids = userIds
        .split(',')
        .filter((item) => item !== '')
        .map((item) => Number(item))
      const names = userNames.split(',')
      const roles = ids.reduce(
        (acc, cur, index) => {
          acc['roleIds'].push(cur)
          acc['roleNames'].push({
            currentLabel: names[index],
            currentValue: cur,
            label: names[index],
            value: cur,
          })
          return acc
        },
        { roleIds: [], roleNames: [] }
      )
      return roles
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
    // 清空还原数据
    resetData(name) {
      this.bannerClear('bannerUpload')
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
