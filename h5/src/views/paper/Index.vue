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
            <span>添加试卷分类</span>
          </div>
        </div>
        <ListCard
          v-for="(item, index) in typeList"
          :key="index"
          :data="item"
          name="paper"
          @edit="edit"
          @del="del"
          @role="role"
          @detail="goDetail"
        ></ListCard>
      </div>
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

    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      width="40%"
      title="试卷分类"
      ref="examForm"
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
    <!-- 编辑读写权限 -->
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
            ref="readSelect"
            placeholder="请选择授权用户"
            :value="roleForm.readRoleUser"
            :total="roleForm.total"
            @input="searchUser"
            @change="selectReadUser"
            @currentChange="getMoreUser"
            @visibleChange="getUserList"
          >
            <el-option
              v-for="item in roleForm.roleUserList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </CustomSelect>
        </el-form-item>
        <el-form-item label="编辑权限">
          <CustomSelect
            ref="writeSelect"
            placeholder="请选择授权用户"
            :value="roleForm.writeRoleUser"
            :total="roleForm.total"
            @input="searchUser"
            @change="selectWriteUser"
            @currentChange="getMoreUser"
            @visibleChange="getUserList"
          >
            <el-option
              v-for="item in roleForm.roleUserList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
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
import {
  paperTypeListPage,
  paperTypeEdit,
  paperTypeAdd,
  paperTypeDel,
  paperTypeAuth,
} from '@/api/paper'
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
        rules: {
          examName: [
            { required: true, message: '请输入试卷名称', trigger: 'blur' },
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
    // 查询分类信息
    async query() {
      const typeList = await paperTypeListPage({
        name: this.queryForm.queryName,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })
      this.typeList = typeList.data.list
      this.total = typeList.data.total
    },
    // 添加 || 修改试卷名称
    addOrEdit() {
      this.$refs['examForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let res

        if (this.examForm.edit) {
          res = await paperTypeEdit({
            id: this.examForm.id,
            name: this.examForm.examName,
          })
        } else {
          res = await paperTypeAdd({
            name: this.examForm.examName,
          })
        }

        if (res?.code == 200) {
          this.$message.success(
            !this.examForm.edit ? '添加成功！' : '修改成功！'
          )
          this.examForm.show = false
          if (this.examForm.edit) {
            this.pageChange()
          } else {
            this.pageChange(1)
          }
        } else {
          this.$message.error(!this.examForm.edit ? '添加失败！' : '修改失败！')
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
    del({ id, name }) {
      this.$confirm(`确认删除【${name}】吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperTypeDel({ id })
          if (res?.code == 200) {
            this.total -= 1
            if (this.total <= this.pageSize) {
              this.pageChange(1)
              return
            }
            this.$message.success('删除成功！')
            this.total % this.pageSize == 0 && this.total != this.pageSize
              ? ((this.curPage -= 1), this.pageChange(this.curPage))
              : this.pageChange(this.curPage)
          } else {
            this.$message.error(res.msg || '删除失败！')
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const roleUserList = await userListPage({
        name,
        curPage,
        pageSize: this.pageSize,
      })

      if (this.$store.getters.userId == 1) {
        roleUserList.data.list.unshift({
          id: 1,
          name: '管理员',
        })
      }

      this.roleForm.roleUserList = roleUserList.data.list
      this.roleForm.total =
        this.$store.getters.userId == 1
          ? roleUserList.data.total + 1
          : roleUserList.data.total
    },
    // 获取更多用户
    getMoreUser(curPage, name) {
      this.getUserList(curPage, name)
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(1, name)
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
    role({ id, readUserIds, writeUserIds, readUserNames, writeUserNames }) {
      this.examForm.id = id
      const { roleIds: readIds, roleNames: readNames } = this.compositionRoles(
        readUserIds,
        readUserNames
      )
      const { roleIds: writeIds, roleNames: writeNames } =
        this.compositionRoles(writeUserIds, writeUserNames)
      this.roleForm.show = true
      this.$nextTick(() => {
        this.roleForm.readRoleUser.push(...readIds)
        this.roleForm.writeRoleUser.push(...writeIds)
        this.$refs['readSelect'].$refs['elSelect'].cachedOptions.push(
          ...readNames
        )
        this.$refs['writeSelect'].$refs['elSelect'].cachedOptions.push(
          ...writeNames
        )
      })
    },
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
    // 编辑权限
    async editRoleUsers() {
      const res = await paperTypeAuth({
        id: this.examForm.id,
        readUserIds: this.roleForm.readRoleUser.join(','),
        writeUserIds: this.roleForm.writeRoleUser.join(','),
      })
      if (res?.code == 200) {
        this.$message.success('权限编辑成功！')
        this.pageChange()
        this.roleForm.show = false
      } else {
        this.$message.error('权限编辑失败！')
      }
    },
    // 试卷子分类
    goDetail({ id }) {
      this.$router.push({
        path: '/paper/list',
        query: { id },
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
