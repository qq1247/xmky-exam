<template>
  <el-form
    ref="userForm"
    :model="exam"
    label-width="100px"
    class="mark-setting-box"
  >
    <el-row v-for="(item, index) in exam.examUsers" :key="item.id">
      <el-col :span="12">
        <el-form-item
          label="考试用户"
          :prop="`examUsers.${index}.examUserIds`"
          :rules="[
            {
              type: 'array',
              required: true,
              message: '请选择考试用户',
              trigger: 'change'
            },
          ]"
        >
          <CustomSelect
            ref="markExamUserSelect"
            placeholder="请选择考试用户"
            :value="item.examUserIds"
            :total="total"
            @input="(keyword) => getUserList(1, 1, keyword)"
            @change="selectExamUser($event, index)"
            @currentChange="(curPage, keyword) => getMoreUser(1, curPage, keyword)"
            @visibleChange="(curPage, keyword) => getUserList(1, curPage, keyword)"
          >
            <el-option
              v-for="user in examUserList"
              :key="user.id"
              :label="user.name"
              :value="user.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item
          v-if="exam.timeType === 2"
          label="阅卷用户"
          :prop="`examUsers.${index}.markUserId`"
          :rules="[
            { required: true, message: '请选择用户', trigger: 'change' },
          ]"
        >
          <CustomSelect
            ref="markUserSelect"
            :multiple="false"
            placeholder="请选择用户"
            :value="item.markUserId"
            :total="total"
            @input="(keyword) => getUserList(2, 1, keyword)"
            @change="selectPerson($event, index)"
            @currentChange="
              (curPage, keyword) => getMoreUser(2, curPage, keyword)
            "
            @visibleChange="
              (curPage, keyword) => getUserList(2, curPage, keyword)
            "
          >
            <el-option
              v-for="markUser in examUserList"
              :key="markUser.id"
              :label="markUser.name"
              :value="markUser.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>
    </el-row>
    <div v-if="exam.timeType === 2" class="remark-buttons">
      <el-form-item>
        <el-button
          type="primary"
          size="mini"
          icon="el-icon-plus"
          @click="remarkAdd"
          plain
          >添加</el-button
        >
        <el-button
          v-if="exam.examUsers.length > 1"
          size="mini"
          icon="el-icon-minus"
          @click="remarkDel"
          type="danger"
          plain
          >删除</el-button
        >
      </el-form-item>
    </div>
  </el-form>
</template>

<script>
import { userListpage } from 'api/user'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      total: 0,
      curPage: 1,
      pageSize: 5,
      examUserList: [],
    }
  },
  computed: {
    examUsers: {
      get: function () {
        return this.$store.state.exam.examUsers
      },
      set: function (newValue) {
        this.$store.state.exam.examUsers = newValue
      }
    },
    exam: {
      get: function () {
        return this.$store.state.exam.exam
      },
      set: function (newValue) {
        this.$store.state.exam.exam = newValue
      }
    },
  },
  async created() {
    // this.getUserList()
    // this.getUserList(2)
  },
  methods: {
    // 获取用户
    async getUserList(type = 1, curPage = 1, name = '') {
      const params = { name, curPage, pageSize: this.pageSize }
      const examUsersRep = await userListpage(
        type === 1 ? params : { type: 2, ...params }
      )

      this.examUserList = examUsersRep.data.list
      this.total = examUsersRep.data.total
    },
    // 获取更多用户
    getMoreUser(type, curPage, name) {
      this.getUserList(type, curPage, name)
    },
    // 选择阅卷用户
    selectPerson(e, index) {
      this.exam.examUsers[index].markUserId = e || []
      this.exam.examUsers.map((item, indexe) => {
        if (
          index !== indexe &&
          this.exam.examUsers[index].markUserId ===
            item.markUserId
        ) {
          this.exam.examUsers[indexe].markUserId = null
        }
      })
    },
    // 选择阅卷考生
    selectExamUser(e, index) {
      this.exam.examUsers[index].examUserIds = e
      this.exam.examUsers.map((item, indexe) => {
        if (index !== indexe) {
          this.exam.examUsers[index].examUserIds.map((user) => {
            const indexa = item.examUserIds.indexOf(user)
            if (indexa !== -1) {
              this.exam.examUsers[indexe].examUserIds.splice(indexa, 1)
            }
          })
        }
      })
    },
    // 下一步
    next() {
      this.$refs['userForm'].validate((valid) => {
        console.log(valid)
        if (!valid) {
          return
        }
        this.$parent.activeIndex++
      })
    },
    remarkAdd() {
      this.exam.examUsers.push({ "examUserIds": [], "markUserId": null })
    },

    remarkDel() {
      this.exam.examUsers.pop()
    }
  },
}
</script>
<style lang="scss" scoped>
.mark-setting-box {
  padding: 15px 150px;
}
</style>
