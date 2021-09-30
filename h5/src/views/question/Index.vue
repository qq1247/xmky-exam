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
        <el-button @click="search" icon="el-icon-search" type="primary"
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
            @click=";(examForm.show = true), (examForm.edit = false)"
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

    <!-- 编辑读写权限 -->
    <el-dialog
      :visible.sync="roleForm.show"
      :show-close="false"
      width="40%"
      title="权限编辑"
      :close-on-click-modal="false"
      @close="resetData('roleForm')"
    >
      <el-form :model="roleForm" ref="roleForm" label-width="100px">
        <el-form-item label="使用权限" prop="readRoleUser">
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
              v-for="item in userList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </CustomSelect>
        </el-form-item>
        <el-form-item label="编辑权限" prop="writeRoleUser">
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
              v-for="item in userList"
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

    <!-- 移动试题分类 -->
    <el-dialog
      :visible.sync="examForm.moveShow"
      :show-close="false"
      width="40%"
      title="移动分类下试题"
      :close-on-click-modal="false"
      @close="resetData('moveForm')"
    >
      <el-form
        :model="examForm"
        :rules="examForm.rules"
        ref="moveForm"
        label-width="120px"
      >
        <el-form-item label="选择试题分类" prop="questionType">
          <CustomSelect
            :multiple="false"
            placeholder="请选择试题分类"
            :value="examForm.questionType"
            :total="examForm.total"
            @change="selectQuestionType"
            @input="searchQuestionType"
            @currentChange="getMoreQuestionType"
            @visibleChange="getQuestionType"
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

    <!-- 试题开放 -->
    <el-dialog
      :visible.sync="openForm.show"
      title="开放记录"
      :show-close="false"
      center
      width="50%"
      :close-on-click-modal="false"
      @close="resetData('openForm')"
    >
      <el-form
        :model="openForm"
        :rules="openForm.rules"
        ref="openForm"
        label-width="100px"
      >
        <el-tabs v-model="openForm.tabActive">
          <el-tab-pane
            v-for="item in openForm.openTabs"
            :key="item.name"
            :label="item.title"
            :name="item.name"
          ></el-tab-pane>
          <template v-if="openForm.tabActive == '0'">
            <el-table :data="openForm.openHistoryList" style="width: 100%">
              <el-table-column type="expand">
                <template slot-scope="props">
                  <el-form
                    label-position="right"
                    class="open-history-table"
                    label-width="130px"
                  >
                    <el-form-item label="ID">
                      <span>{{ props.row.id }}</span>
                    </el-form-item>
                    <el-form-item label="开始时间">
                      <span>{{ props.row.startTime }}</span>
                    </el-form-item>
                    <el-form-item label="结束时间">
                      <span>{{ props.row.endTime }}</span>
                    </el-form-item>
                    <el-form-item label="开放人员">
                      <span>{{ props.row.userNames }}</span>
                    </el-form-item>
                    <el-form-item label="开放机构">
                      <span>{{ props.row.orgNames }}</span>
                    </el-form-item>
                    <el-form-item label="评论方式">
                      <span>{{
                        ['不显示', '只读', '可编辑'][props.row.commentState]
                      }}</span>
                    </el-form-item>
                    <el-form-item label="状态">
                      <span>{{ ['', '开放', '作废'][props.row.state] }}</span>
                    </el-form-item>
                  </el-form>
                </template>
              </el-table-column>
              <el-table-column label="开始时间" prop="startTime">
              </el-table-column>
              <el-table-column label="结束时间" prop="endTime">
              </el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button
                    @click.native.prevent="delHistory(scope.row.id)"
                    type="text"
                    size="small"
                    :disabled="scope.row.state === 2"
                  >
                    作废
                  </el-button></template
                ></el-table-column
              >
            </el-table>
            <!-- 分页 -->
            <el-pagination
              style="margin-top: 20px"
              background
              layout="prev, pager, next"
              prev-text="上一页"
              next-text="下一页"
              hide-on-single-page
              :total="openForm.pageTotal"
              :page-size="openForm.pageSize"
              :current-page="openForm.curPage"
              @current-change="openPageChange"
            ></el-pagination>
          </template>
          <template v-if="openForm.tabActive == '1'">
            <el-form-item label="开放时间" prop="openTime">
              <el-date-picker
                v-model="openForm.openTime"
                type="datetimerange"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd HH:mm:ss"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="开放用户" prop="openUser">
              <CustomSelect
                ref="openUserSelect"
                placeholder="请选择用户"
                :value="openForm.openUser"
                :total="openForm.total"
                @input="searchUser"
                @change="selectOpenUser"
                @currentChange="getMoreUser"
                @visibleChange="getUserList"
              >
                <el-option
                  v-for="item in userList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </CustomSelect>
            </el-form-item>
            <el-form-item label="开放机构" prop="openOrg">
              <CustomSelect
                ref="writeSelect"
                placeholder="请选择机构"
                :value="openForm.openOrg"
                :total="openForm.total"
                @input="searchOrg"
                @change="selectOpenOrg"
                @currentChange="getMoreOrg"
                @visibleChange="getOrgList"
              >
                <el-option
                  v-for="item in openForm.openOrgList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                >
                  <span style="float: left">{{
                    `${item.name}${
                      item.parentId === 0 ? '' : ` - ${item.parentName}`
                    }`
                  }}</span>
                </el-option>
              </CustomSelect>
            </el-form-item>
            <el-form-item label="评论方式" prop="openReview">
              <el-radio
                v-for="item in openForm.openReviewList"
                :key="item.value"
                v-model="openForm.openReview"
                :label="item.value"
                @change="selectReviewType"
                prop="openReview"
                >{{ item.name }}</el-radio
              >
            </el-form-item>
          </template>
        </el-tabs>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="addOpen" v-if="openForm.tabActive == '1'">
          添加
        </el-button>
        <el-button @click="openForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  questionTypeListPage,
  questionTypeOpenListPage,
  questionTypeOpenAdd,
  questionTypeOpenDel,
  questionTypeEdit,
  questionTypeAdd,
  questionTypeMove,
  questionTypeAuth,
  questionTypeDel,
} from 'api/question'
import { orgListPage } from 'api/base'
import { userListPage } from 'api/user'
import ListCard from 'components/ListCard.vue'
import CustomSelect from 'components/CustomSelect.vue'
import { orgAdd } from '@/api/base'
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
      userList: [],
      queryForm: {
        queryName: '',
      },
      examForm: {
        show: false,
        edit: false,
        id: 0,
        examName: '',
        moveShow: false,
        total: 1,
        curPage: 1,
        questionType: null,
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
        total: 0,
        readRoleUser: [],
        writeRoleUser: [],
      },
      openForm: {
        id: null,
        show: false,
        total: 1,
        pageSize: 5,
        pageTotal: 1,
        curPage: 1,
        openTime: [],
        openUser: [],
        openOrg: [],
        openOrgList: [],
        openReview: 0,
        openReviewList: [
          {
            name: '不显示',
            value: 0,
          },
          {
            name: '只读',
            value: 1,
          },
          {
            name: '可编辑',
            value: 2,
          },
        ],
        tabActive: '0',
        openTabs: [
          {
            title: '历史记录',
            name: '0',
          },
          {
            title: '新增记录',
            name: '1',
          },
        ],
        openHistoryList: [],
        rules: {
          openTime: [
            { required: true, message: '请选择开放时间', trigger: 'change' },
          ],
        },
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
    // 搜索
    search() {
      this.curPage = 1
      this.query()
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
          this.$message.success(
            !this.examForm.edit ? '添加成功！' : '修改成功！'
          )
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
      this.examForm.show = true
      this.examForm.edit = true
      this.$nextTick(() => {
        this.examForm.id = id
        this.examForm.examName = name
      })
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
          const res = await questionTypeDel({ id })
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
    // 获取试题分类
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListPage({
        name,
        curPage,
        pageSize: this.pageSize,
      })
      this.examForm.questionTypes = typeList.data.list
      this.examForm.total = typeList.data.total
    },
    // 根据name 查询试题分类
    searchQuestionType(name) {
      this.getQuestionType(1, name)
    },
    // 获取更多试题分类
    getMoreQuestionType(curPage, name) {
      this.getQuestionType(curPage, name)
    },
    // 选择试题分类
    selectQuestionType(e) {
      this.examForm.questionType = e
    },
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const userList = await userListPage({
        name,
        curPage,
        pageSize: this.pageSize,
      })

      if (this.$store.getters.userId == 1) {
        userList.data.list.unshift({
          id: 1,
          name: '管理员',
        })
      }

      this.userList = userList.data.list
      this.roleForm.total = this.openForm.total =
        this.$store.getters.userId == 1
          ? userList.data.total + 1
          : userList.data.total
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
    // 选择开放用户
    selectOpenUser(e) {
      this.openForm.openUser = e
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
    // 移动试题分类
    async questionMove() {
      if (!this.examForm.questionType) {
        this.$message.warning('请选择试题分类')
        return
      }

      const res = await questionTypeMove({
        sourceId: this.examForm.id,
        targetId: this.examForm.questionType,
      })
      if (res?.code == 200) {
        this.examForm.moveShow = false
        this.$message.success('移动成功！')
        this.pageChange()
      } else {
        this.$message.error('移动失败！')
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
        this.$message.success('权限编辑成功！')
        this.pageChange()
        this.roleForm.show = false
      } else {
        this.$message.error('权限编辑失败！')
      }
    },
    // 获取开放历史记录
    async getOpenHistory() {
      const openHistoryList = await questionTypeOpenListPage({
        questionTypeId: this.openForm.id,
        curPage: this.openForm.curPage,
        pageSize: this.openForm.pageSize,
        list: '1',
      })
      this.openForm.openHistoryList = openHistoryList.data.list
      this.openForm.pageTotal = openHistoryList.data.total
    },
    // 开放历史记录分页查询
    openPageChange(val) {
      val && (this.openForm.curPage = val)
      this.getOpenHistory()
    },
    // 作废开放记录
    async delHistory(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await questionTypeOpenDel({ id })
        res?.code && ((this.openForm.curPage = 1), this.getOpenHistory())
      })
    },
    // 试题开放
    async open({ id }) {
      this.openForm.show = true
      this.openForm.tabActive = '0'
      this.openForm.id = id
      this.openForm.curPage = 1
      this.getOpenHistory()
    },
    // 获取机构
    async getOrgList(curPage = 1, name = '') {
      const orgList = await orgListPage({
        name,
        curPage,
        pageSize: this.pageSize,
      })

      this.openForm.openOrgList = orgList.data.list
      this.openForm.total = orgList.data.total
    },
    // 获取更多机构
    getMoreOrg(curPage, name) {
      this.getOrgList(curPage, name)
    },
    // 根据name 查询机构
    searchOrg(name) {
      this.getOrgList(1, name)
    },
    // 选择读取权限机构
    selectOpenOrg(e) {
      this.openForm.openOrg = e
    },
    // 添加开放记录
    async addOpen() {
      this.$refs['openForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        const res = await questionTypeOpenAdd({
          questionTypeId: this.openForm.id,
          startTime: this.openForm.openTime[0],
          endTime: this.openForm.openTime[1],
          userIds: this.openForm.openUser.join(','),
          orgIds: this.openForm.openOrg.join(','),
          commentState: this.openForm.openReview,
        })

        if (res?.code === 200) {
          this.openForm.tabActive = '0'
          this.openForm.curPage = 1
          this.resetData('openForm')
          this.getOpenHistory()
        } else {
          this.$message(res.msg)
        }
      })
    },
    // 选择阅卷方式
    selectReviewType(e) {
      this.openForm.openReview = e
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
      this.$refs[name].resetFields()
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
.open-history-table /deep/.el-form-item .el-form-item__label {
  color: #99a9bf;
  padding-right: 20px;
}
.open-history-table .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
}
</style>
