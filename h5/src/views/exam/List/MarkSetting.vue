<!--
 * @Description: 设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-06 16:11:45
-->
<template>
  <el-card style="width: 1200px; margin: 0 auto" shadow="never">
    <el-form :model="examForm" ref="userForm" label-width="100px">
      <el-form-item label="阅卷方式" v-if="examForm.paperMarkType === 2">
        <el-radio
          v-for="(item, index) in examForm.examRadios"
          :key="item.value"
          v-model="examForm.examRadio"
          :label="item.value"
          :disabled="index === 0"
          @change="selectPaperType"
          prop="examRadio"
          >{{ item.name }}</el-radio
        >
      </el-form-item>
      <el-row v-for="(item, index) in examForm.examRemarks" :key="item.id">
        <el-col :span="12">
          <el-form-item
            label="考试用户"
            v-if="examForm.examRadio == 1"
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
                v-for="item in examForm.examUsers"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
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
                v-for="item in examForm.examUsers"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </CustomSelect>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="题号" v-if="examForm.examRadio == 0">
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
                v-for="item in examForm.examQuestionNums"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </CustomSelect>
          </el-form-item>
        </el-col>
      </el-row>
      <div class="remark-buttons" v-if="examForm.paperMarkType === 2">
        <el-form-item>
          <el-button
            @click="remarkAdd"
            type="primary"
            size="mini"
            icon="el-icon-plus"
            >添加</el-button
          >
          <el-button
            v-if="examForm.examRemarks.length > 1"
            @click="remarkDel"
            size="mini"
            icon="el-icon-minus"
            >删除</el-button
          >
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="editExamRead">设置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { examGet, examMarkUserList, examUpdateMarkSet } from 'api/exam'
import { userListPage } from 'api/user'
import { questionListPage } from 'api/question'
import CustomSelect from 'components/CustomSelect.vue'

export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      id: null,
      examForm: {
        name: '',
        paperId: 0,
        paperMarkType: 1,
        total: 0,
        curPage: 1,
        pageSize: 5,
        examRadio: 1,
        examRadios: [
          {
            name: '按题阅卷',
            value: 0,
          },
          {
            name: '按人阅卷',
            value: 1,
          },
        ],
        examQuestionNums: [],
        examRemarks: [],
        examUser: [],
        examUsers: [],
        examUserList: [],
      },
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const {
        data: { paperId, endTime, paperMarkType, state },
      } = await examGet({
        id: this.id,
      })
      this.state = state
      this.examForm.paperId = paperId
      this.examForm.paperMarkType = paperMarkType

      const examMarkUser = await examMarkUserList({ id: this.id })

      const num = examMarkUser.data.length > 0 ? examMarkUser.data.length : 1
      this.examForm.examRemarks = []
      for (let index = 0; index < num; index++) {
        this.examForm.examRemarks.push({
          examCheckPerson: '',
          examQuestionNum: [],
          examUser: [],
        })
      }

      this.$nextTick(() => {
        if (examMarkUser.data.length > 0) {
          this.examForm.examRemarks = []
          examMarkUser.data.map((item, index) => {
            this.examForm.examRemarks.push({
              examCheckPerson: item.markUserId,
              examUser: item.examUserList.map((user) => user.id),
              examQuestionNum: [],
            })
          })
        }

        examMarkUser.data.map((item, index) => {
          const cachedOptions = item.examUserList.reduce((acc, cur) => {
            acc.push({
              currentLabel: cur.name,
              currentValue: cur.id,
              label: cur.name,
              value: cur.id,
            })
            return acc
          }, [])

          this.$refs['markExamUserSelect'] &&
            this.$refs['markExamUserSelect'][index].$refs[
              'elSelect'
            ].cachedOptions.push(...cachedOptions)

          this.$refs['markUserSelect'] &&
            this.$refs['markUserSelect'][index].$refs[
              'elSelect'
            ].cachedOptions.push({
              currentLabel: item.markUserName,
              currentValue: item.markUserId,
              label: item.markUserName,
              value: item.markUserId,
            })
        })
      })
    }
  },
  methods: {
    // 添加评语
    remarkAdd() {
      this.examForm.examRemarks.push({
        examCheckPerson: '',
        examQuestionNum: [],
        examUser: [],
      })
    },
    // 删除评语
    remarkDel() {
      this.examForm.examRemarks.pop()
    },
    // 选择阅卷方式
    selectPaperType(e) {
      this.examForm.examRadio = e
    },
    // 获取用户
    async getUserList(type = 1, curPage = 1, name = '') {
      let params = { name, curPage, pageSize: this.examForm.pageSize }
      const examUsers = await userListPage(
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
      this.examForm.examRemarks[index].examCheckPerson = e
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
            if (indexa !== -1)
              this.examForm.examRemarks[indexe].examUser.splice(indexa, 1)
          })
        }
      })
    },
    // 获取题号
    async getQuestionNumList(id = '', curPage = 1) {
      this.examForm.curPage = curPage
      const params = {
        paperId: this.examForm.paperId,
        curPage,
        pageSize: this.examForm.pageSize,
      }
      !id ? params : (params.id = Number(id))
      const questionNumList = await questionListPage(params)
      this.examForm.examQuestionNums = questionNumList.data.list
      this.examForm.total = questionNumList.data.total
    },
    // 选择题号
    selectQuestionNum(e, index) {
      this.examForm.examRemarks[index].examQuestionNum = e
    },
    // id查询题号
    searchQuestionNum(id) {
      this.getQuestionNumList(id)
    },
    // 分页查询题号
    getMoreQuestionNum(curPage) {
      this.getQuestionNumList(curPage)
    },
    // 编辑阅卷
    editExamRead() {
      this.$refs['userForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let dynamic
        const markUserIds = this.examForm.examRemarks.reduce((acc, cur) => {
          acc.push(cur.examCheckPerson)
          return acc
        }, [])

        if (this.examForm.examRadio == 0) {
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
          id: this.id,
          markUserIds,
        }

        this.examForm.examRadio == 0
          ? (params.questionIds = dynamic)
          : (params.examUserIds = dynamic)

        const res = await examUpdateMarkSet(params)

        if (res?.code == 200) {
          this.$message.success('设置成功！')
          this.$router.back()
        } else {
          this.$message.error('设置失败！')
        }
      })
    },
  },
}
</script>
