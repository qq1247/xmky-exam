<template>
  <el-form :model="examForm" ref="userForm" label-width="100px">
    <el-form-item label="阅卷方式">
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
      <el-button @click="$emit('prev')" type="primary">上一步</el-button>
      <el-button @click="next" type="primary">下一步</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { examMarkUserList, examUpdateMarkSet } from 'api/exam'
import { getQuick, setQuick } from '@/utils/storage'
import { userListPage } from 'api/user'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
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
        examRemarks: [
          {
            examCheckPerson: '',
            examQuestionNum: [],
            examUser: [],
          },
        ],
        examUser: [],
        examUsers: [],
        examUserList: [],
      },
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
    // 下一步
    next() {
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
          id: getQuick().examId,
          markUserIds,
        }

        this.examForm.examRadio == 0
          ? (params.questionIds = dynamic)
          : (params.examUserIds = dynamic)

        await examUpdateMarkSet(params)

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
        this.$emit('next', {
          paperType: getQuick().paperType,
        })
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
  activated() {
    this.examForm.paperMarkType = getQuick().markType
  },
}
</script>
