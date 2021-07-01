<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input placeholder="请输入名称" v-model="queryForm.queryName" class="query-input"></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query()" icon="el-icon-search" type="primary">查询</el-button>
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
            <span>添加试题</span>
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
        :current-page="1"
        @current-change="pageChange"
      ></el-pagination>
    </div>
    <!-- 添加 | 编辑 试题分类 -->
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      width="30%"
      title="试题分类"
      :close-on-click-modal="false"
      @close="resetData('examForm')"
    >
      <el-form :model="examForm" :rules="examForm.rules" ref="examForm" label-width="60px">
        <el-form-item label="名称" prop="examName">
          <el-input placeholder="请输入分类名称" v-model="examForm.examName"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="addOrEdit" type="primary">
          {{
            examForm.edit ? "修改" : "添加"
          }}
        </el-button>
        <el-button @click="examForm.show = false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑读写权限 -->
    <el-dialog
      :visible.sync="roleForm.show"
      :show-close="false"
      width="33%"
      title="权限编辑"
      :close-on-click-modal="false"
      @close="resetData('roleForm')"
    >
      <el-form :model="roleForm" ref="examForm" label-width="100px">
        <el-form-item label="读取权限">
          <CustomSelect
            placeholder="请选择授权人员"
            :multiple="true"
            :value="roleForm.readRoleUser"
            :total="roleForm.total"
            :showPage="true"
            :tags="true"
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
        <el-form-item label="使用权限">
          <CustomSelect
            placeholder="请选择授权人员"
            :multiple="true"
            :value="roleForm.writeRoleUser"
            :total="roleForm.total"
            :showPage="true"
            :tags="true"
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
  </div>
</template>

<script>
import ListCard from "@/components/ListCard.vue"
import CustomSelect from '@/components/CustomSelect.vue';
export default {
  components: {
    ListCard,
    CustomSelect
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      queryForm: {
        queryName: ""
      },
      examForm: {
        show: false,
        edit: false,
        id: 0,
        examName: "",
        rules: {
          examName: [
            { required: true, message: "请输入试题名称", trigger: "blur" }
          ]
        }
      },
      roleForm: {
        show: false,
        curPage: 1,
        pageSize: 5,
        total: 0,
        readRoleUser: [],
        writeRoleUser: [],
        roleUserList: []
      },
      typeList: []
    }
  },
  mounted() {
    this.query()
  },
  methods: {
    // 查询分类数据
    async query(curPage = 1) {
      const typeList = await this.$https.questionTypeListPage({
        name: this.queryForm.queryName,
        curPage,
        pageSize: this.pageSize
      })
      this.typeList = typeList.data.list
      this.total = typeList.data.total
    },
    // 添加 || 修改分类名称
    addOrEdit() {
      this.$refs["examForm"].validate(async valid => {
        if (!valid) {
          return
        }

        let res

        if (this.examForm.edit) {
          res = await this.$https.questionTypeEdit({
            id: this.examForm.id,
            name: this.examForm.examName
          })
        } else {
          res = await this.$https.questionTypeAdd({
            name: this.examForm.examName
          })
        }

        if (res.code == 200) {
          this.$tools.message(!this.examForm.edit ? "添加成功！" : "修改成功！")
          this.examForm.show = false
          this.query()
        } else {
          this.$tools.message(
            !this.examForm.edit ? "添加失败！" : "修改失败！",
            "error"
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
    // 删除分类
    async del({ id }) {
      const res = await this.$https.questionTypeDel({ id }).catch(err => { })
      res.code == 200
        ? (
          this.$tools.message("删除成功！"),
          this.query()
        )
        : this.$tools.message("删除成功！", "error");
    },
    // 获取用户
    async getUserList(name = '', curPage = 1) {
      this.roleForm.curPage = curPage
      const roleUserList = await this.$https.userListpage({
        name,
        curPage,
        pageSize: this.roleForm.pageSize
      })
      this.roleForm.roleUserList = roleUserList.data.list
      this.roleForm.total = roleUserList.data.total
    },
    // 获取更多用户
    getMoreUser(curPage) {
      this.getUserList(curPage)
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(name)
    },
    // 选择考试用户
    selectReadUser(e) {
      this.roleForm.readRoleUser = e
    },
    // 选择考试用户
    selectWriteUser(e) {
      this.roleForm.writeRoleUser = e
    },
    // 权限人员信息
    async role({ readUserIds, writeUserIds, id }) {
      this.examForm.id = id
      this.roleForm.readRoleUser = readUserIds
        .split(",")
        .filter(item => item != "")
      this.roleForm.writeRoleUser = writeUserIds
        .split(",")
        .filter(item => item != "")
      await this.getUserList()
      this.roleForm.show = true
    },
    // 编辑权限
    async editRoleUsers() {
      const res = await this.$https.questionTypeAuth({
        id: this.examForm.id,
        readUserIds: this.roleForm.readRoleUser.join(","),
        writeUserIds: this.roleForm.writeRoleUser.join(",")
      })
      if (res.code == 200) {
        this.$tools.message("权限编辑成功！")
        this.roleForm.show = false
      } else {
        this.$tools.message("权限编辑失败成功！", "error")
      }
    },
    // 试题开放
    async open() {
      this.$tools.message("此功能开发中...", "info")
    },
    // 试题详情
    goDetail({ id, name }) {
      this.$router.push({
        path: "/examLibrary/Edit",
        query: { id, name }
      })
    },
    // 分页切换
    pageChange(val) {
      this.query(val)
    },
    // 清空还原数据
    resetData(name) {
      this.$tools.resetData(this, name)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";
</style>
