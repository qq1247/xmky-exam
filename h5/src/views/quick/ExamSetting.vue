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

    <div class="footer">
      <el-button type="primary" @click="$emit('prev', '1')">上一步</el-button>
      <el-button type="primary" @click="next">下一步</el-button>
    </div>
  </el-form>
</template>

<script>
import {
  examTypeAdd,
  examTypeListPage,
  examAdd,
  examGet,
  examEdit
} from 'api/exam'
import { getQuick, setQuick } from '@/utils/storage'
import * as dayjs from 'dayjs'
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'
dayjs.extend(isSameOrBefore)
export default {
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
          examTime: [{ required: true, validator: validateExamTime }],
          markTime: [{ required: true, validator: validateMarkTime }]
        }
      }
    }
  },
  async created() {
    const quickInfo = getQuick()
    if (!Object.keys(quickInfo).length || !quickInfo) return false
    this.examForm.showMarkTime = quickInfo.markType !== 1
    if (quickInfo.examId) {
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
      } = await examGet({ id: quickInfo.examId })
      this.$nextTick(() => {
        this.examForm.name = name
        this.examForm.paperName = paperName
        this.examForm.examTime = [startTime, endTime]
        this.examForm.showMarkTime = paperMarkType !== 1
        const quickInfo = getQuick()
        quickInfo.markType = paperMarkType
        setQuick(quickInfo)
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
      const examTypeList = await examTypeListPage({
        name: '我的考试',
        curPage: 1,
        pageSize: 5
      })

      if (!examTypeList.data.list.length) {
        const res = await examTypeAdd({
          name: '我的考试'
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
      this.$refs['examForm'].validate(async(valid) => {
        if (!valid) {
          return
        }

        let params = {
          name: this.examForm.name,
          startTime: this.examForm.examTime[0],
          endTime: this.examForm.examTime[1],
          paperId: getQuick().id,
          examTypeId: this.examTypeId
        }

        if (this.examForm.markTime.length) {
          params = {
            markStartTime: this.examForm.markTime[0],
            markEndTime: this.examForm.markTime[1],
            ...params
          }
        }

        const res = getQuick().examId
          ? await examEdit({
            ...params,
            id: getQuick().examId
          })
          : await examAdd(params)
        const examId = res.data || getQuick()?.examId
        this.setQuickInfo(examId)
        this.$emit('next', '4')
      })
    },
    // 设置quickInfo
    async setQuickInfo(examId) {
      let quickInfo = getQuick()
      if (quickInfo?.examId) {
        return
      }
      quickInfo = {
        ...quickInfo,
        examId,
        examName: this.examForm.name
      }
      setQuick(quickInfo)
    }
  }
}
</script>

<style lang="scss" scoped>
.footer {
  position: fixed;
  bottom: 0;
  width: calc(100% - 201px);
  right: 0;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(13px);
  display: flex;
  padding: 10px 10px 10px 30px;
  box-shadow: 1px -3px 13px -6px rgba(#000000, 0.15);
}
</style>
