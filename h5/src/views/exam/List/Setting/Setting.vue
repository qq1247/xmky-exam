<template>
  <el-form
    ref="examForm"
    :model="examForm"
    :rules="examForm.rules"
    label-width="100px"
  >
    <el-form-item label="考试名称" prop="name">
      <el-input v-model="examForm.name" placeholder="请输入试卷名称" />
    </el-form-item>
    <el-form-item label="选择试卷" prop="selectPaperId">
      <CustomSelect
        ref="paperSelect"
        placeholder="请选择试卷"
        :multiple="false"
        :value="examForm.selectPaperId"
        :total="examForm.total"
        @input="searchPaper"
        @change="selectPaper"
        @currentChange="getMorePaper"
        @visibleChange="getPaperList"
      >
        <el-option
          v-for="item in examForm.paperList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </CustomSelect>
    </el-form-item>
    <el-form-item label="考试时间" prop="examTime">
      <el-date-picker
        v-model="examForm.examTime"
        type="datetimerange"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd HH:mm:ss"
      />
    </el-form-item>
    <el-form-item v-if="examForm.showMarkTime" label="阅卷时间" prop="markTime">
      <el-date-picker
        v-model="examForm.markTime"
        type="datetimerange"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd HH:mm:ss"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="addOrEdit">{{
        id ? '修改' : '添加'
      }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { examAdd, examEdit, examGet } from 'api/exam'
import { paperListPage } from 'api/paper'
import * as dayjs from 'dayjs'
import CustomSelect from 'components/CustomSelect.vue'
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'
dayjs.extend(isSameOrBefore)
export default {
  components: {
    CustomSelect
  },
  data() {
    const validateExamTime = (rule, value, callback) => {
      if (value.length === 0) {
        return callback(new Error('请选择考试时间'))
      }
      if (dayjs(value[1]).isSameOrBefore(dayjs())) {
        return callback(new Error(`请选择当前时间后的时间段`))
      }
      return callback()
    }

    const validateMarkTime = (rule, value, callback) => {
      if (value.length === 0) {
        return callback(new Error('请选择阅卷时间'))
      }
      if (
        dayjs(value[0]).isSameOrBefore(dayjs(this.examForm.examTime[1])) ||
        dayjs(value[1]).isSameOrBefore(dayjs(this.examForm.examTime[1]))
      ) {
        return callback(new Error(`请选择考试时间后的时间段`))
      }
      return callback()
    }
    return {
      id: null,
      examTypeId: null,
      examForm: {
        name: '',
        paperId: 0,
        selectPaperId: null,
        showMarkTime: false,
        paperList: [],
        examTime: [],
        markTime: [],
        examRadio: 1,
        examRadios: [
          {
            name: '按题阅卷',
            value: 0
          },
          {
            name: '按人阅卷',
            value: 1
          }
        ],
        rules: {
          name: [
            { required: true, message: '请填写试卷名称', trigger: 'blur' }
          ],
          selectPaperId: [
            { required: true, message: '请选择试卷', trigger: 'blur' }
          ],
          examTime: [{ required: true, validator: validateExamTime }],
          markTime: [{ required: true, validator: validateMarkTime }]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    this.examTypeId = this.$route.params.examTypeId
    if (Number(this.id)) {
      const {
        data: {
          name,
          paperId,
          paperName,
          startTime,
          endTime,
          markStartTime,
          markEndTime,
          paperMarkType
        }
      } = await examGet({ id: this.id })
      this.$nextTick(() => {
        this.examForm.name = name
        this.examForm.selectPaperId = paperId
        this.examForm.paperName = paperName
        this.examForm.examTime = [startTime, endTime]
        this.examForm.showMarkTime = paperMarkType !== 1
        const _markStartTime = this.getHour(2, endTime)
        const _markEndTime = this.getHour(1, _markStartTime)
        if (markStartTime || markEndTime) {
          this.examForm.markTime = [
            markStartTime || `${_markStartTime}`,
            markEndTime || `${_markEndTime}`
          ]
        }

        this.$refs['paperSelect'] &&
          this.$refs['paperSelect'].$refs['elSelect'].cachedOptions.push({
            currentLabel: paperName,
            currentValue: paperId,
            label: paperName,
            value: paperId
          })
      })
    } else {
      const examStartTime = this.getHour(2)
      const examEndTime = this.getHour(1, examStartTime)
      const markStartTime = this.getHour(2, examEndTime)
      const markEndTime = this.getHour(1, markStartTime)
      this.examForm.examTime = [`${examStartTime}`, `${examEndTime}`]
      this.examForm.markTime = [`${markStartTime}`, `${markEndTime}`]
    }
  },
  methods: {
    // 获取任意时候后的N个小时整点
    getHour(n, day) {
      const millisecond = day ? new Date(day).getTime() : new Date().getTime()
      const nextTime = dayjs(millisecond + 1000 * 60 * 60 * n).format(
        'YYYY-MM-DD HH'
      )
      return `${nextTime}:00:00`
    },
    // 获取试卷列表
    async getPaperList(curPage = 1, name = '') {
      const paperList = await paperListPage({
        name,
        state: 1,
        curPage,
        pageSize: 5
      })
      this.examForm.paperList = paperList.data.list
      this.examForm.total = paperList.data.total
    },
    // 获取更多试卷列表
    getMorePaper(curPage, name) {
      this.getPaperList(curPage, name)
    },
    // 根据name 查询试卷
    searchPaper(name) {
      this.getPaperList(1, name)
    },
    // 选择试卷
    selectPaper(e) {
      this.examForm.selectPaperId = e
      const selectPaper = this.examForm.paperList.filter(
        (item) => item.id === e
      )
      this.examForm.showMarkTime = selectPaper[0].markType !== 1
    },
    // 添加试卷信息
    addOrEdit() {
      this.$refs['examForm'].validate(async(valid) => {
        if (!valid) {
          return
        }

        let params = {
          name: this.examForm.name,
          startTime: this.examForm.examTime[0],
          endTime: this.examForm.examTime[1],
          paperId: this.examForm.selectPaperId,
          examTypeId: this.examTypeId
        }

        if (this.examForm.markTime.length) {
          params = {
            markStartTime: this.examForm.markTime[0],
            markEndTime: this.examForm.markTime[1],
            ...params
          }
        }

        const res = this.id
          ? await examEdit({
            ...params,
            id: this.id
          })
          : await examAdd(params)

        if (res?.code === 200) {
          this.$message.success(!this.id ? '添加成功！' : '修改成功！')
          this.$router.back()
        } else {
          this.$message.error(!this.id ? '添加失败！' : '修改失败！')
        }
      })
    }
  }
}
</script>
