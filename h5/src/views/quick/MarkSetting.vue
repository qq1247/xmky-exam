<template>
  <el-form ref="userForm" :model="examForm" label-width="100px" class="mark-setting-box">
    <el-row v-for="(item, index) in examForm.examRemarks" :key="item.id">
      <el-col :span="12">
        <el-form-item
          label="考试用户"
          :prop="`examRemarks.${index}.examUser`"
          :rules="[
            {
              required: true,
              message: '请选择考试用户',
              trigger: 'change',
            },
          ]"
        >
          <CustomSelect
            ref="markExamUserSelect"
            placeholder="请选择考试用户"
            :value="examForm.examRemarks[index].examUser"
            :total="examForm.total"
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
              v-for="user in examForm.examUsers"
              :key="user.id"
              :label="user.name"
              :value="user.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item
          v-if="examForm.paperMarkType === 2"
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
            :value="examForm.examRemarks[index].examCheckPerson"
            :total="examForm.total"
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
              v-for="markUser in examForm.examUsers"
              :key="markUser.id"
              :label="markUser.name"
              :value="markUser.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item v-if="examForm.examRadio === 0" label="题号">
          <CustomSelect
            placeholder="请选择题号"
            :value="examForm.examRemarks[index].examQuestionNum"
            :total="examForm.total"
            @input="searchQuestionNum"
            @change="selectQuestionNum($event, index)"
            @visibleChange="getQuestionNumList"
            @currentChange="getMoreQuestionNum"
          >
            <el-option
              v-for="num in examForm.examQuestionNums"
              :key="num.id"
              :label="num.name"
              :value="num.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-col>
    </el-row>
    <div v-if="examForm.paperMarkType === 2" class="remark-buttons">
      <el-form-item>
        <el-button
          type="primary"
          size="mini"
          icon="el-icon-plus"
          @click="remarkAdd"
          plain
        >添加</el-button>
        <el-button
          v-if="examForm.examRemarks.length > 1"
          size="mini"
          icon="el-icon-minus"
          @click="remarkDel"
          type="danger"
          plain
        >删除</el-button>
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
    CustomSelect
  },
  data() {
    return {
      examForm: {
        name: '',
        paperId: 0,
        paperMarkType: 2,
        total: 0,
        curPage: 1,
        pageSize: 5,
        examQuestionNums: [],
        examRemarks: [
          {
            examCheckPerson: [],
            examQuestionNum: [],
            examUser: []
          }
        ],
        examUser: [],
        examUsers: [],
        examUserList: []
      }
    }
  },
  async created() {
    const quickInfo = getQuick()
    if (!Object.keys(quickInfo).length || !quickInfo) return false
    if (quickInfo.examId) {
      this.examForm.paperMarkType = getQuick().markType

      const examMarkUser = await examMarkUserList({ id: quickInfo.examId })

      const num = examMarkUser.data.length > 0 ? examMarkUser.data.length : 1
      this.examForm.examRemarks = []
      for (let index = 0; index < num; index++) {
        this.examForm.examRemarks.push({
          examCheckPerson: [],
          examQuestionNum: [],
          examUser: []
        })
      }

      this.$nextTick(() => {
        if (examMarkUser.data.length > 0) {
          this.examForm.examRemarks = []
          examMarkUser.data.map((item, index) => {
            this.examForm.examRemarks.push({
              examCheckPerson: item.markUserId,
              examUser: item.examUserList.map((user) => user.id),
              examQuestionNum: []
            })
          })
        }

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
      })
    }
  },
  activated() {
    this.examForm.paperMarkType = getQuick().markType
  },
  methods: {
    // 添加评语
    remarkAdd() {
      this.examForm.examRemarks.push({
        examCheckPerson: [],
        examQuestionNum: [],
        examUser: []
      })
    },
    // 删除评语
    remarkDel() {
      this.examForm.examRemarks.pop()
    },
    // 获取用户
    async getUserList(type = 1, curPage = 1, name = '') {
      const params = { name, curPage, pageSize: this.examForm.pageSize }
      const examUsers = await userListpage(
        type === 1 ? params : { type: 2, ...params }
      )

      this.examForm.examUsers = examUsers.data.list
      this.examForm.total = examUsers.data.total
    },
    // 获取更多用户
    getMoreUser(type, curPage, name) {
      this.getUserList(type, curPage, name)
    },
    // 根据name 查询人员
    searchUser(name, type) {
      this.getUserList(type, 1, name)
    },
    // 选择阅卷用户
    selectPerson(e, index) {
      this.examForm.examRemarks[index].examCheckPerson = e || []
      this.examForm.examRemarks.map((item, indexe) => {
        if (
          index !== indexe &&
          this.examForm.examRemarks[index].examCheckPerson ===
            item.examCheckPerson
        ) {
          this.examForm.examRemarks[indexe].examCheckPerson = null
        }
      })
    },
    // 选择阅卷考生
    selectExamUser(e, index) {
      this.examForm.examRemarks[index].examUser = e
      this.examForm.examRemarks.map((item, indexe) => {
        if (index !== indexe) {
          this.examForm.examRemarks[index].examUser.map((user) => {
            const indexa = item.examUser.indexOf(user)
            if (indexa !== -1) {
              this.examForm.examRemarks[indexe].examUser.splice(indexa, 1)
            }
          })
        }
      })
    },
    // 下一步
    next() {
      this.$refs['userForm'].validate(async(valid) => {
        if (!valid) {
          return
        }

        let dynamic
        const markUserIds = this.examForm.examRemarks.reduce((acc, cur) => {
          acc.push(cur.examCheckPerson)
          return acc
        }, [])

        if (this.examForm.examRadio === 0) {
          dynamic = this.examForm.examRemarks.reduce((acc, cur) => {
            acc.push(cur.examQuestionNum.join(','))
            return acc
          }, [])
        } else {
          dynamic = this.examForm.examRemarks.reduce((acc, cur) => {
            acc.push(cur.examUser.join(','))
            return acc
          }, [])
        }

        const params = {
          id: getQuick().examId,
          markUserIds
        }

        this.examForm.examRadio === 0
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
            markUserName: cur.markUserName
          })
          return acc
        }, [])
        await this.setQuickInfo(userList)
        this.$parent.activeIndex ++ 
      })
    },
    // 设置quickInfo
    async setQuickInfo(userList) {
      let quickInfo = getQuick() || {}
      quickInfo = {
        ...quickInfo,
        userList
      }
      setQuick(quickInfo)
    }
  }
}
</script>
<style lang="scss" scoped>
.mark-setting-box {
  padding: 15px 150px;
}
</style>
