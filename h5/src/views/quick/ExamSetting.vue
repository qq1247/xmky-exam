<template>
  <el-form
    :model="examForm"
    :rules="examForm.rules"
    ref="examForm"
    label-width="100px"
  >
    <el-form-item label="考试名称" prop="name">
      <el-input placeholder="请输入试卷名称" v-model="examForm.name"></el-input>
    </el-form-item>
    <el-form-item label="考试时间" prop="examTime">
      <el-date-picker
        v-model="examForm.examTime"
        type="datetimerange"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd HH:mm:ss"
      ></el-date-picker>
    </el-form-item>
    <el-form-item label="阅卷时间" prop="markTime" v-if="examForm.showMarkTime">
      <el-date-picker
        v-model="examForm.markTime"
        type="datetimerange"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd HH:mm:ss"
      ></el-date-picker>
    </el-form-item>
    <el-form-item>
      <el-button @click="$emit('prev')" type="primary">上一步</el-button>
      <el-button @click="next" type="primary">下一步</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { examTypeAdd, examTypeListPage, examAdd } from 'api/exam'
import { getQuick, setQuick } from '@/utils/storage'
import * as dayjs from 'dayjs'
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'
dayjs.extend(isSameOrBefore)
export default {
  data() {
    const validateExamTime = (rule, value, callback) => {
      if (value.length == 0) {
        return callback(new Error('请选择考试时间'))
      }
      if (dayjs(value[1]).isSameOrBefore(dayjs())) {
        return callback(new Error(`请选择当前时间后的时间段`))
      }
      return callback()
    }

    const validateMarkTime = (rule, value, callback) => {
      if (value.length == 0) {
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
      examTypeId: null,
      examForm: {
        name: '',
        paperId: 0,
        showMarkTime: false,
        paperList: [],
        examTime: [],
        markTime: [],
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
        rules: {
          name: [
            { required: true, message: '请填写试卷名称', trigger: 'blur' },
          ],
          selectPaperId: [
            { required: true, message: '请选择试卷', trigger: 'blur' },
          ],
          examTime: [{ required: true, validator: validateExamTime }],
          markTime: [{ required: true, validator: validateMarkTime }],
        },
      },
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
    // 下一步
    next() {
      this.$refs['examForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let params = {
          name: this.examForm.name,
          startTime: this.examForm.examTime[0],
          endTime: this.examForm.examTime[1],
          paperId: getQuick().paperId,
          examTypeId: this.examTypeId,
        }

        if (this.examForm.markTime.length) {
          params = {
            markStartTime: this.examForm.markTime[0],
            markEndTime: this.examForm.markTime[1],
            ...params,
          }
        }

        const { data: examId } = await examAdd(params)
        this.setQuickInfo(examId)
        this.$emit('next', {
          paperType: getQuick().type,
        })
      })
    },
    // 设置quickInfo
    async setQuickInfo(examId) {
      let quickInfo = getQuick() || {}
      if (quickInfo?.examId) {
        return
      }
      quickInfo = {
        ...quickInfo,
        examId,
        examName: this.examForm.name,
      }
      setQuick(quickInfo)
    },
  },
  async activated() {
    this.examForm.showMarkTime = getQuick().markType === 1 ? false : true
    const examTypeList = await examTypeListPage({
      name: '我的考试',
      curPage: 1,
      pageSize: 5,
    })

    if (!examTypeList.data.list.length) {
      const res = await examTypeAdd({
        name: '我的考试',
      })
      if (res?.code === 200) {
        this.examTypeId = res.data
      }
    } else {
      this.examTypeId = examTypeList.data.list[0].id
    }
    const examStartTime = this.getHour(2)
    const examEndTime = this.getHour(1, examStartTime)
    const markStartTime = this.getHour(2, examEndTime)
    const markEndTime = this.getHour(1, markStartTime)
    this.examForm.examTime = [`${examStartTime}`, `${examEndTime}`]
    this.examForm.markTime = [`${markStartTime}`, `${markEndTime}`]
  },
}
</script>
