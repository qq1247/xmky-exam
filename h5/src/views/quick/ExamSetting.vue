<template>
  <div>
    <el-form
      ref="examForm"
      :model="examForm"
      :rules="examForm.rules"
      label-width="120px"
      label-position="left"
      size="small"
      class="exam-setting-box"
    >
      <div>
        <el-form-item label="考试名称：" prop="name">
          <el-input v-model="examForm.name" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="开始时间：" prop="startTime">
          <el-date-picker
            v-model="examForm.startTime"
            type="datetime"
            placeholder="开始日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间：" prop="endTime">
          <el-date-picker
            v-model="examForm.endTime"
            type="datetime"
            placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
      </div>

      <div v-if="examForm.showMarkTime">
        <el-form-item
          label="阅卷开始时间"
          style="visibility: hidden"
        ></el-form-item>
        <el-form-item label="阅卷开始时间：" prop="markStartTime">
          <el-date-picker
            v-model="examForm.markStartTime"
            type="datetime"
            placeholder="开始日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="阅卷结束时间：" prop="markEndTime">
          <el-date-picker
            v-model="examForm.markEndTime"
            type="datetime"
            placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
      </div>

      <div class="cross-box">
        <div class="ope-more" @click="rotate = !rotate">
          <span>更多</span>
          <i
            class="el-icon-arrow-down"
            :style="{ transform: rotate ? 'rotate(180deg)' : '' }"
          ></i>
        </div>
        <div class="inline"></div>
      </div>

      <div :style="{ display: rotate ? 'none' : '' }">
        <el-form-item label="及格（%）：" prop="passScore">
          <el-input
            v-model="examForm.passScore"
            placeholder="请输入及格百分比"
          />
        </el-form-item>
        <el-form-item label="防作弊：" prop="options">
          <el-checkbox-group v-model="examForm.options">
            <el-checkbox
              v-for="option in optionsArr"
              :label="option.value"
              :key="option.value"
            >{{ option.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="展示方式：" prop="showType">
          <el-radio-group v-model="examForm.showType">
            <el-radio
              v-for="item in showTypesArr"
              :key="item.value"
              :label="item.value"
              >{{ item.name }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
      </div>
      <div :style="{ width: '320px', display: rotate ? 'none' : '' }">
        <el-form-item label="匿名阅卷：" prop="anonState">
          <el-checkbox
            v-model="examForm.anonState"
            :true-label="1"
            :false-label="2"
          ></el-checkbox>
        </el-form-item>
        <el-form-item label="成绩公开：" prop="scoreState">
          <el-checkbox
            v-model="examForm.scoreState"
            :true-label="1"
            :false-label="2"
          ></el-checkbox>
        </el-form-item>
        <el-form-item label="排名公开：" prop="rankState">
          <el-checkbox
            v-model="examForm.rankState"
            :true-label="1"
            :false-label="2"
          ></el-checkbox>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script>
import {
  examTypeAdd,
  examTypeListpage,
  examAdd,
  examGet,
  examEdit,
  examAnon,
  examRank,
  examScore
} from 'api/exam'
import { paperSxe } from 'api/paper'
import { getQuick, setQuick } from '@/utils/storage'
import * as dayjs from 'dayjs'
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'
dayjs.extend(isSameOrBefore)
export default {
  props: { 
    showTypes: {
      type: Array,
      default:() => {
        return [{
        name: '整张',
        value: 1
        },
        {
          name: '单题',
          value: 3
        }]
      }
    } 
  },
  data() {
    const validateStartTime = (rule, value, callback) => {
      if (value.length === 0) {
        return callback(new Error('请选择考试时间'))
      }
      if (getQuick().examId) {

      } else if (dayjs(value).isSameOrBefore(dayjs())) {
        return callback(new Error(`请选择当前时间后的时间段`))
      }
      return callback()
    }
    const validateEndTime = (rule, value, callback) => {
      if (value.length === 0) {
        return callback(new Error('请选择考试时间'))
      }
      if (dayjs(value).isSameOrBefore(dayjs(this.examForm.startTime))) {
        return callback(new Error(`请选择开始时间后的时间段`))
      }
      return callback()
    }

    const validateMarkStartTime = (rule, value, callback) => {
      if (value.length === 0) {
        return callback(new Error('请选择阅卷时间'))
      }
      if (
        dayjs(value).isSameOrBefore(dayjs(this.examForm.endTime))
      ) {
        return callback(new Error(`请选择考试时间后的时间段`))
      }
      return callback()
    }
    const validateMarkEndTime = (rule, value, callback) => {
      if (value.length === 0) {
        return callback(new Error('请选择阅卷时间'))
      }
      if (
        dayjs(value).isSameOrBefore(dayjs(this.examForm.markStartTime))
      ) {
        return callback(new Error(`请选择阅卷开始时间后的时间段`))
      }
      return callback()
    }
    return {
      optionsArr: [
        {label: '试题乱序', value: 1 },
        {label: '选项乱序', value: 2 }
      ],
      rotate: false,
      examTypeId: null,
      showTypesArr: this.showTypes,
      examForm: {
        name: '',
        paperId: 0,
        paperName: '',
        showMarkTime: false, // 是否展示阅卷时间
        startTime: '',
        endTime: '',
        markStartTime: '',
        markEndTime: '',
        passScore: null, // 及格% 
        options: [], // 防作弊
        showType: null, // 展示方式
        anonState: 2, // 匿名阅卷
        scoreState: 2, // 成绩公开
        rankState: 2, // 排名公开
        rules: {
          name: [
            { required: true, message: '请填写考试名称', trigger: 'blur' }
          ],
          startTime: [{ required: true, validator: validateStartTime }],
          endTime: [{ required: true, validator: validateEndTime }],
          markStartTime: [{ required: true, validator: validateMarkStartTime }],
          markEndTime: [{ required: true, validator: validateMarkEndTime }]
        }
      },
      isSetQuick: false
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
          paperMarkType,
          anonState,
          scoreState,
          rankState
        }
      } = await examGet({ id: quickInfo.examId })
      this.$nextTick(() => {
        this.examForm.name = name
        this.examForm.paperName = paperName
        this.examForm.startTime = startTime
        this.examForm.endTime = endTime
        this.examForm.showMarkTime = paperMarkType !== 1
        this.examForm.anonState = anonState
        this.examForm.scoreState = scoreState
        this.examForm.rankState = rankState
        const quickInfo = getQuick()
        quickInfo.markType = paperMarkType
        this.examForm.passScore = quickInfo.passScore
        this.examForm.options = quickInfo.options
        this.examForm.showType = quickInfo.showType
        setQuick(quickInfo)
        if (markStartTime || markEndTime) {
           this.examForm.markStartTime =  markStartTime
           this.examForm.markEndTime = markEndTime
        }
      })
    } else {
      const examTypeList = await examTypeListpage({
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
      const quickInfo = getQuick()
      quickInfo.examTypeId = this.examTypeId
      setQuick(quickInfo)
      this.examForm.startTime = this.getHour(2)
      this.examForm.endTime = this.getHour(1, this.examForm.startTime)
      this.examForm.markStartTime = this.getHour(2, this.examForm.endTime)
      this.examForm.markEndTime = this.getHour(1, this.examForm.markStartTime)
      this.examForm.passScore = quickInfo.passScore
      this.examForm.showType = quickInfo.showType
    }
    this.isSetQuick = true
  },
  watch: {
    examForm: {
      handler () {
        if (this.isSetQuick) {
          this.setQuickInfo()
        }
      },
      immediate: true,
      deep: true
    },
    'examForm.options' : {
      handler (newName, oldName) {
        if (this.isSetQuick) {
          let quickInfo = getQuick()
          quickInfo.options = newName
          setQuick(quickInfo)
        }
      },
      immediate: true
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
        let quickInfo = getQuick()
        let params = {
          name: this.examForm.name,
          startTime: this.examForm.startTime,
          endTime: this.examForm.endTime,
          paperId: quickInfo.id,
          examTypeId: this.examTypeId || quickInfo.examTypeId
        }

        if (this.examForm.showMarkTime) {
          params = {
            markStartTime: this.examForm.markStartTime,
            markEndTime: this.examForm.markEndTime,
            ...params
          }
        }

        const res = quickInfo.examId
          ? await examEdit({
            ...params,
            id: quickInfo.examId
          })
          : await examAdd(params)
        const examId = res.data || quickInfo?.examId
        this.setQuickInfo(examId)
        this.paperSxe({id: quickInfo.id, options: this.examForm.options})
        if (examId) {
          this.anonymous({id: examId, state: this.examForm.anonState})
          this.ranking({id: examId, state: this.examForm.rankState})
          this.score({id: examId, state: this.examForm.scoreState})
        }
        this.$parent.activeIndex ++
      })
    },

    // 防作弊
    async paperSxe(params) {
      const res = await paperSxe(params)
      if (res?.code === 200) {
        // this.$message.success('匿名设置成功！')
      } else {
        this.$message.error(res.msg || '开启乱序失败！')
      }
    },

    // 匿名阅卷
    async anonymous(params) {
      const res = await examAnon(params)
      if (res?.code === 200) {
        // this.$message.success('匿名设置成功！')
      } else {
        this.$message.error(res.msg || '匿名设置失败！')
      }
    },

    // 排名公开
    async ranking(params) {
      const res = await examRank(params)
      if (res?.code === 200) {
        // this.$message.success('排名公开设置成功！')
      } else {
        this.$message.error(res.msg || '排名公开设置失败！')
      }
    },

    // 成绩公开
    async score(params) {
      const res = await examScore(params)
      if (res?.code === 200) {
        // this.$message.success('成绩公开设置成功！')
      } else {
        this.$message.error(res.msg || '成绩公开设置失败！')
      }
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
        examName: this.examForm.name,
        startTime: this.examForm.startTime,
        endTime: this.examForm.endTime,
        markStartTime: this.examForm.markStartTime,
        markEndTime: this.examForm.markEndTime,
        passScore: this.examForm.passScore, // 及格% 
        options: this.examForm.options, // 防作弊
        showType: this.examForm.showType, // 展示方式
        anonState: this.examForm.anonState, // 匿名阅卷
        scoreState: this.examForm.scoreState, // 成绩公开
        rankState: this.examForm.rankState, // 排名公开
      }
      setQuick(quickInfo)
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-setting-box {
  display: flex;
  flex-wrap: wrap;
  padding: 15px 150px;
  justify-content: space-between;
  .cross-box {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    margin: 20px 0;
    .ope-more {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 0 10px;
      width: 60px;
      color: #3b92bf;
    }
    .inline {
      width: calc(100% - 80px);
      border-bottom: 1px solid #dbdbdb;
    }
  }
}
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
