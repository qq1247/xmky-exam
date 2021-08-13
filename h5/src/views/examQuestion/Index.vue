<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.queryName"
            class="query-input"
          ></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query" icon="el-icon-search" type="primary"
          >查询</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <div class="exam-item">
          <div
            class="exam-content exam-add"
            @click="
              examForm.show = true
              examForm.edit = false
            "
          >
            <i class="common common-plus"></i>
            <span>添加试题分类</span>
          </div>
        </div>
        <ListCard
          v-for="(item, index) in typeList"
          :key="index"
          :data="item"
          name="question"
          @edit="edit"
          @del="del"
          @role="role"
          @open="open"
          @detail="goDetail"
          @move="move"
        ></ListCard>
      </div>
      <!-- 分页 -->
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="total"
        :page-size="pageSize"
        :current-page="curPage"
        @current-change="pageChange"
      ></el-pagination>
    </div>
    <!-- 添加 | 编辑 试题分类 -->
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      width="40%"
      title="试题分类"
      :close-on-click-modal="false"
      @close="resetData('examForm')"
    >
      <el-form
        :model="examForm"
        :rules="examForm.rules"
        ref="examForm"
        label-width="60px"
      >
        <el-form-item label="名称" prop="examName">
          <el-input
            placeholder="请输入分类名称"
            v-model="examForm.examName"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="addOrEdit" type="primary">{{
          examForm.edit ? '修改' : '添加'
        }}</el-button>
        <el-button @click="examForm.show = false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑使用权限 -->
    <el-dialog
      :visible.sync="roleForm.show"
      :show-close="false"
      width="40%"
      title="权限编辑"
      :close-on-click-modal="false"
      @close="resetData('roleForm')"
    >
      <el-form :model="roleForm" ref="examForm" label-width="100px">
        <el-form-item label="使用权限">
          <CustomSelect
            placeholder="请选择授权人员"
            :multiple="true"
            :value="roleForm.readRoleUser"
            :total="roleForm.total"
            :showPage="true"
            :currentPage="roleForm.curPage"
            :pageSize="roleForm.pageSize"
            :remote="true"
            :reserveKeyword="true"
            :filterable="true"
            :remoteMethod="searchUser"
            @change="selectReadUser"
            @focus="getUserList()"
            @currentChange="getMoreUser"
          >
            <el-option
              v-for="item in roleForm.roleUserList"
              :key="item.id"
              :label="item.name"
              :value="String(item.id)"
            ></el-option>
          </CustomSelect>
        </el-form-item>
        <el-form-item label="编辑权限">
          <CustomSelect
            placeholder="请选择授权人员"
            :multiple="true"
            :value="roleForm.writeRoleUser"
            :total="roleForm.total"
            :showPage="true"
            :currentPage="roleForm.curPage"
            :pageSize="roleForm.pageSize"
            :remote="true"
            :reserveKeyword="true"
            :filterable="true"
            :remoteMethod="searchUser"
            @change="selectWriteUser"
            @focus="getUserList()"
            @currentChange="getMoreUser"
          >
            <el-option
              v-for="item in roleForm.roleUserList"
              :key="item.id"
              :label="item.name"
              :value="String(item.id)"
            ></el-option>
          </CustomSelect>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="editRoleUsers" type="primary">编辑</el-button>
        <el-button @click="roleForm.show = false">取消</el-button>
      </div>
    </el-dialog>
    <!-- 移动试题分类 -->
    <el-dialog
      :visible.sync="examForm.moveShow"
      :show-close="false"
      width="40%"
      title="移动分类下试题"
      :close-on-click-modal="false"
      @close="resetData('examForm')"
    >
      <el-form :model="examForm" label-width="100px">
        <el-form-item label="选择试题分类" prop="questionType">
          <CustomSelect
            placeholder="请选择试题分类"
            :value="examForm.questionType"
            :total="examForm.total"
            :showPage="true"
            :currentPage="examForm.curPage"
            :pageSize="examForm.pageSize"
            @change="selectQuestionType"
            @focus="getQuestionType()"
            @currentChange="getMoreQuestionType"
          >
            <el-option
              v-for="item in examForm.questionTypes"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </CustomSelect>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="questionMove" type="primary">移动</el-button>
        <el-button @click="examForm.moveShow = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  questionTypeListPage,
  questionTypeEdit,
  questionTypeAdd,
  questionTypeMove,
  questionTypeAuth,
} from '@/api/question'
import { userListPage } from '@/api/user'
import ListCard from '@/components/ListCard.vue'
import CustomSelect from '@/components/CustomSelect.vue'
export default {
  components: {
    ListCard,
    CustomSelect,
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      curPage: 1,
      queryForm: {
        queryName: '',
      },
      examForm: {
        show: false,
        edit: false,
        id: 0,
        examName: '',
        moveShow: false,
        pageSize: 5,
        total: 1,
        curPage: 1,
        questionType: '',
        questionTypes: [],
        rules: {
          examName: [
            { required: true, message: '请输入分类名称', trigger: 'blur' },
          ],
          questionType: [
            { required: true, message: '请选择试题分类', trigger: 'blur' },
          ],
        },
      },
      roleForm: {
        show: false,
        curPage: 1,
        pageSize: 5,
        total: 0,
        readRoleUser: [],
        writeRoleUser: [],
        roleUserList: [],
      },
      typeList: [],
    }
  },
  mounted() {
    this.query()
  },
  methods: {
    // 查询分类数据
    async query() {
      const typeList = await questionTypeListPage({
        name: this.queryForm.queryName,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })
      this.typeList = typeList.data.list
      this.total = typeList.data.total
    },
    // 添加 || 修改分类名称
    addOrEdit() {
      this.$refs['examForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let res

        if (this.examForm.edit) {
          res = await questionTypeEdit({
            id: this.examForm.id,
            name: this.examForm.examName,
          })
        } else {
          res = await questionTypeAdd({
            name: this.examForm.examName,
          })
        }

        if (res?.code == 200) {
          this.examForm.show = false
          this.$tools.message(!this.examForm.edit ? '添加成功！' : '修改成功！')
          if (this.examForm.edit) {
            this.pageChange()
          } else {
            this.pageChange(1)
          }
        } else {
          this.$tools.message(
            !this.examForm.edit ? '添加失败！' : '修改失败！',
            'error'
          )
        }
      })
    },
    // 编辑分类
    edit({ id, name }) {
      this.examForm.edit = true
      this.examForm.id = id
      this.examForm.examName = name
      this.examForm.show = true
    },
    move({ id }) {
      this.examForm.moveShow = true
      this.examForm.id = id
    },
    // 删除分类
    del({ id, name }) {
      this.$confirm(`确认删除【${name}】吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await this.$https
            .questionTypeDel({ id })
            .catch((err) => {})
          if (res?.code == 200) {
            this.total -= 1
            if (this.total <= this.pageSize) {
              this.pageChange(1)
              return
            }
            this.$tools.message('删除成功！')
            this.total % this.pageSize == 0 && this.total != this.pageSize
              ? ((this.curPage -= 1), this.pageChange(this.curPage))
              : this.pageChange(this.curPage)
          } else {
            this.$tools.message(res.msg || '删除失败！', 'error')
          }
        })
        .catch(() => {})
    },
    // 获取试题分类
    async getQuestionType() {
      const typeList = await questionTypeListPage({
        name: '',
        curPage: this.examForm.curPage,
        pageSize: this.examForm.pageSize,
      })
      this.examForm.questionTypes = typeList.data.list
      this.examForm.total = typeList.data.total
    },
    // 获取更多试题分类
    getMoreQuestionType(curPage) {
      this.examForm.curPage = curPage
      this.getQuestionType()
    },
    // 选择试题分类
    selectQuestionType(e) {
      this.examForm.questionType = e
    },
    // 获取用户
    async getUserList(name = '') {
      const roleUserList = await userListPage({
        name,
        curPage: this.roleForm.curPage,
        pageSize: this.roleForm.pageSize,
      })

      if (this.$store.getters.userId == 1) {
        roleUserList.data.list.unshift({
          id: 1,
          name: '管理员',
        })
      }

      this.roleForm.roleUserList = roleUserList.data.list
      this.roleForm.total = roleUserList.data.total + 1
    },
    // 获取更多用户
    getMoreUser(curPage) {
      this.roleForm.curPage = curPage
      this.getUserList()
    },
    // 根据name 查询人员
    searchUser(name) {
      this.roleForm.curPage = 1
      this.getUserList(name)
    },
    // 选择读取权限用户
    selectReadUser(e) {
      this.roleForm.readRoleUser = e
    },
    // 选择阅读权限用户
    selectWriteUser(e) {
      this.roleForm.writeRoleUser = e
    },
    // 权限人员信息
    async role({ readUserIds, writeUserIds, id }) {
      this.examForm.id = id
      this.roleForm.readRoleUser = readUserIds
      this.roleForm.writeRoleUser = writeUserIds
      await this.getUserList()
      this.roleForm.show = true
    },
    // 移动试题分类
    async questionMove() {
      const res = await questionTypeMove({
        sourceId: this.examForm.id,
        targetId: this.examForm.questionType,
      })
      if (res?.code == 200) {
        this.examForm.moveShow = false
        this.$tools.message('移动成功！')
        this.pageChange()
      } else {
        this.$tools.message('移动失败！', 'error')
      }
    },
    // 编辑权限
    async editRoleUsers() {
      const res = await questionTypeAuth({
        id: this.examForm.id,
        readUserIds: this.roleForm.readRoleUser.join(','),
        writeUserIds: this.roleForm.writeRoleUser.join(','),
      })
      if (res?.code == 200) {
        this.$tools.message('权限编辑成功！')
        this.pageChange()
        this.roleForm.show = false
      } else {
        this.$tools.message('权限编辑失败！', 'error')
      }
    },
    // 试题开放
    async open() {
      this.$tools.message('此功能开发中...', 'info')
    },
    // 试题详情
    goDetail({ id, name, writeUserIds }) {
      const edit = writeUserIds.includes(String(this.$store.getters.userId))
      this.$router.push({
        path: '/question/edit',
        query: { id, name, edit },
      })
    },
    // 分页切换
    pageChange(val) {
      val && (this.curPage = val)
      this.query()
    },
    // 清空还原数据
    resetData(name) {
      this.$tools.resetData(this, name)
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
</style>
