<template>
  <el-form
    ref="userForm"
    :model="examUsers"
    :rules="editForm.rules"
    label-width="100px"
    class="mark-setting-box"
  >
    <el-row v-for="(examUser, index) in examUsers" :key="`examUser${index}`">
      <el-col :span="12">
        <el-form-item 
          label="考试用户123" 
          :prop="`examUsers.examUser`"
        >
          <CustomSelect
            ref="markExamUserSelect"
            placeholder="请选择考试用户"
            :value="editForm.examRemarks[index].examUser"
            :total="editForm.total"
            @input="(keyword) => searchUser(keyword, 1)"
            @change="selectExamUser($event, index)"
            @currentChange="
              (curPage, keyword) => getMoreUser(1, curPage, keyword)
            "
            @visibleChange="
              (curPage, keyword) => getUserList(1, curPage, keyword)
            "
          >
            <el-option
              v-for="user in editForm.examUsers"
              :key="user.id"
              :label="user.name"
              :value="user.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item
          v-if="editForm.paperMarkType === 2"
          label="阅卷用户"
          :prop="`examRemarks.${index}.examCheckPerson`"
          :rules="[
            { required: true, message: '请选择用户', trigger: 'change' },
          ]"
        >
          <CustomSelect
            ref="markUserSelect"
            :multiple="false"
            placeholder="请选择用户"
            :value="editForm.examRemarks[index].examCheckPerson"
            :total="editForm.total"
            @input="(keyword) => searchUser(keyword, 2)"
            @change="selectPerson($event, index)"
            @currentChange="
              (curPage, keyword) => getMoreUser(2, curPage, keyword)
            "
            @visibleChange="
              (curPage, keyword) => getUserList(2, curPage, keyword)
            "
          >
            <el-option
              v-for="markUser in editForm.examUsers"
              :key="markUser.id"
              :label="markUser.name"
              :value="markUser.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item v-if="editForm.examRadio === 0" label="题号">
          <CustomSelect
            placeholder="请选择题号"
            :value="editForm.examRemarks[index].examQuestionNum"
            :total="editForm.total"
            @input="searchQuestionNum"
            @change="selectQuestionNum($event, index)"
            @visibleChange="getQuestionNumList"
            @currentChange="getMoreQuestionNum"
          >
            <el-option
              v-for="num in editForm.examQuestionNums"
              :key="num.id"
              :label="num.name"
              :value="num.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>
    </el-row>
    <div v-if="editForm.paperMarkType === 2" class="remark-buttons">
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
          v-if="editForm.examRemarks.length > 1"
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
import { examMarkUserList, examUserAdd } from 'api/exam'
import { getQuick, setQuick } from '@/utils/storage'
import { userListpage } from 'api/user'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      editForm: {},
    }
  },
  async created() {
    // const quickInfo = getQuick()
    // if (!Object.keys(quickInfo).length || !quickInfo) return false
    // if (quickInfo.examId) {
    //   this.editForm.paperMarkType = getQuick().markType
    //   const examMarkUser = await examMarkUserList({ id: quickInfo.examId })
    //   const num = examMarkUser.data.length > 0 ? examMarkUser.data.length : 1
    //   this.editForm.examRemarks = []
    //   for (let index = 0; index < num; index++) {
    //     this.editForm.examRemarks.push({
    //       examCheckPerson: [],
    //       examQuestionNum: [],
    //       examUser: []
    //     })
    //   }
    //   this.$nextTick(() => {
    //     if (examMarkUser.data.length > 0) {
    //       this.editForm.examRemarks = []
    //       examMarkUser.data.map((item, index) => {
    //         this.editForm.examRemarks.push({
    //           examCheckPerson: item.markUserId,
    //           examUser: item.examUserList.map((user) => user.id),
    //           examQuestionNum: []
    //         })
    //       })
    //     }
    // examMarkUser.data.map((item, index) => {
    //   const cachedOptions = item.examUserList.reduce((acc, cur) => {
    //     acc.push({
    //       currentLabel: cur.name,
    //       currentValue: cur.id,
    //       label: cur.name,
    //       value: cur.id
    //     })
    //     return acc
    //   }, [])
    //   this.$refs['markExamUserSelect'] &&
    //     this.$refs['markExamUserSelect'][index].$refs[
    //       'elSelect'
    //     ].cachedOptions.push(...cachedOptions)
    //   this.$refs['markUserSelect'] &&
    //     this.$refs['markUserSelect'][index].$refs[
    //       'elSelect'
    //     ].cachedOptions.push({
    //       currentLabel: item.markUserName,
    //       currentValue: item.markUserId,
    //       label: item.markUserName,
    //       value: item.markUserId
    //     })
    // })
    // })
    // }
  },
  computed: {
    examUsers: {
      get: function () {
        return this.$store.state.exam.examUsers
      },
      set: function (newValue) {
        this.$store.state.exam.examUsers = newValue
      },
    },
  },
  methods: {
    // 获取用户
    async getUserList(type = 1, curPage = 1, name = '') {
      const params = { name, curPage, pageSize: this.editForm.pageSize }
      const examUsers = await userListpage(
        type === 1 ? params : { type: 2, ...params }
      )

      this.editForm.examUsers = examUsers.data.list
      this.editForm.total = examUsers.data.total
    },
    // 选择阅卷用户
    selectPerson(e, index) {
      // this.editForm.examRemarks[index].examCheckPerson = e || []
      // this.editForm.examRemarks.map((item, indexe) => {
      //   if (
      //     index !== indexe &&
      //     this.editForm.examRemarks[index].examCheckPerson ===
      //       item.examCheckPerson
      //   ) {
      //     this.editForm.examRemarks[indexe].examCheckPerson = null
      //   }
      // })
    },
    // 选择阅卷考生
    selectExamUser(e, index) {
      // this.editForm.examRemarks[index].examUser = e
      // this.editForm.examRemarks.map((item, indexe) => {
      //   if (index !== indexe) {
      //     this.editForm.examRemarks[index].examUser.map((user) => {
      //       const indexa = item.examUser.indexOf(user)
      //       if (indexa !== -1) {
      //         this.editForm.examRemarks[indexe].examUser.splice(indexa, 1)
      //       }
      //     })
      //   }
      // })
    },
    // 下一步
    next() {
      this.$refs['userForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let dynamic
        const markUserIds = this.editForm.examRemarks.reduce((acc, cur) => {
          acc.push(cur.examCheckPerson)
          return acc
        }, [])

        if (this.editForm.examRadio === 0) {
          dynamic = this.editForm.examRemarks.reduce((acc, cur) => {
            acc.push(cur.examQuestionNum.join(','))
            return acc
          }, [])
        } else {
          dynamic = this.editForm.examRemarks.reduce((acc, cur) => {
            acc.push(cur.examUser.join(','))
            return acc
          }, [])
        }

        const params = {
          id: getQuick().examId,
          markUserIds,
        }

        this.editForm.examRadio === 0
          ? (params.questionIds = dynamic)
          : (params.examUserIds = dynamic)

        await examUserAdd(params)

        const examMarkUser = await examMarkUserList({ id: getQuick().examId })
        const userList = examMarkUser.data.reduce((acc, cur) => {
          const examUserList = cur.examUserList.reduce((accExam, curExam) => {
            accExam.push(curExam.name)
            return accExam
          }, [])
          acc.push({
            examUserList,
            markUserName: cur.markUserName,
          })
          return acc
        }, [])
        await this.setQuickInfo(userList)
        this.$parent.activeIndex++
      })
    },
    // 设置quickInfo
    async setQuickInfo(userList) {
      let quickInfo = getQuick() || {}
      quickInfo = {
        ...quickInfo,
        userList,
      }
      setQuick(quickInfo)
    },
  },
}
</script>
<style lang="scss" scoped>
.mark-setting-box {
  padding: 15px 150px;
}
</style>
