<!--
 * @Description: 开放题库
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:07:54
 * @LastEditors: Che
 * @LastEditTime: 2022-01-06 14:46:02
-->
<template>
  <div class="container">
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
            <el-table-column label="结束时间" prop="endTime"> </el-table-column>
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
      <el-form-item>
        <el-button
          @click="addOpen"
          type="primary"
          v-if="openForm.tabActive == '1'"
        >
          添加
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {
  questionTypeOpenListPage,
  questionTypeOpenAdd,
  questionTypeOpenDel,
} from 'api/question'
import { orgListPage } from 'api/base'
import { userListPage } from 'api/user'
import CustomSelect from 'components/CustomSelect.vue'

export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      id: null,
      userList: [],
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
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    this.getOpenHistory()
  },
  methods: {
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const userList = await userListPage({
        name,
        curPage,
        pageSize: 5,
      })

      if (this.$store.getters.userId == 1) {
        userList.data.list.unshift({
          id: 1,
          name: '管理员',
        })
      }

      this.userList = userList.data.list
      this.openForm.total =
        this.$store.getters.userId == 1
          ? userList.data.total + 1
          : userList.data.total
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(1, name)
    },
    // 获取更多用户
    getMoreUser(curPage, name) {
      this.getUserList(curPage, name)
    },
    // 获取开放历史记录
    async getOpenHistory() {
      const openHistoryList = await questionTypeOpenListPage({
        questionTypeId: this.id,
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
    // 选择阅卷方式
    selectReviewType(e) {
      this.openForm.openReview = e
    },
    // 添加开放记录
    async addOpen() {
      this.$refs['openForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        const res = await questionTypeOpenAdd({
          questionTypeId: this.id,
          startTime: this.openForm.openTime[0],
          endTime: this.openForm.openTime[1],
          userIds: this.openForm.openUser.join(','),
          orgIds: this.openForm.openOrg.join(','),
          commentState: this.openForm.openReview,
        })

        if (res?.code === 200) {
          this.openForm.tabActive = '0'
          this.openForm.curPage = 1
          this.getOpenHistory()
        } else {
          this.$message(res.msg)
        }
      })
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
    // 选择开放用户
    selectOpenUser(e) {
      this.openForm.openUser = e
    },
    // 选择阅读权限用户
    selectWriteUser(e) {
      this.roleForm.writeRoleUser = e
    },
    // 组合回显数据
    compositionRoles(userIds, userNames) {
      const ids = userIds
        .filter((item) => item !== '')
        .map((item) => Number(item))
      const names = userNames
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
  },
}
</script>

<style lang="scss" scoped>
.container {
  width: 1200px;
  margin: 0 auto 20px;
  padding: 10px 20px;
  background: #fff;
  border-radius: 5px;
  border: 1px solid #ececec;
}
.open-history-table /deep/.el-form-item .el-form-item__label {
  color: #99a9bf;
  padding-right: 20px;
}
.open-history-table .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
}
</style>
