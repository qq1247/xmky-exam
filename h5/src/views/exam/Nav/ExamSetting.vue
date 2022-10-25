<template>
  <el-form
    ref="editForm"
    :model="examInfo.exam"
    :rules="editForm.rules"
    label-width="120px"
    label-position="left"
    size="small"
    class="exam-setting-box"
  >
    <div>
      <el-form-item label="考试名称：" prop="name">
        <el-input 
          v-model="name"
          placeholder="请输入考试名称" />
      </el-form-item>
      <el-form-item label="时间类型" prop="timeType">
        <el-switch
          v-model="timeType"
          active-color="#0094e5"
          :active-value="1"
          active-text="限时"
          inactive-color="#ff4949"
          :inactive-value="2"
          inactive-text="不限时"
        />
      </el-form-item>
      <el-form-item v-if="timeType === 1" label="考试时间：" prop="examTimes">
        <el-date-picker
          v-model="examTimes"
          type="datetimerange"
          align="right"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="['08:00:00', '10:00:00']">
        </el-date-picker>
      </el-form-item>
      <el-form-item v-if="timeType === 1 && markQuestions.length" label="阅卷时间：" prop="markTimes">
        <el-date-picker
          v-model="markTimes"
          type="datetimerange"
          align="right"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="['08:00:00', '10:00:00']">
        </el-date-picker>
        <div style="background-color: #fff4e8; color: #fb901b; height: 20px; line-height: 20px;">
        &nbsp;第<template v-for="examQuestion of markQuestions">{{examQuestion.question.no}}、</template>题需要人工阅卷
        </div>
      </el-form-item>
      <el-form-item label="及格分数：" prop="passScore">
        <el-input-number 
          v-model="passScore" 
          controls-position="right" 
          :min="0" 
          :max="totalScore - 1"
          :step="10"
          ></el-input-number> / {{totalScore}}分
      </el-form-item>
      <el-form-item v-if="genType === 1" label="防作弊：" prop="sxes">
        <el-checkbox-group v-model="sxes">
          <el-checkbox
            :label="1"
            key="1"
            >试题乱序
          </el-checkbox>
          <el-checkbox
            :label="2"
            key="2"
            >选项乱序
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="展示方式：" prop="showType">
        <el-radio-group v-model="showType">
          <el-radio
            v-for="dict in $tools.getDicts('PAPER_SHOW_TYPE')"
            :key="`showType${dict.dictKey}`"
            :label="Number(dict.dictKey)"
            >{{ dict.dictValue }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="genType === 1" label="匿名阅卷：" prop="anonState">
        <el-checkbox
          v-model="anonState"
          :true-label="1"
          :false-label="2"
        >是</el-checkbox>
      </el-form-item>
      <el-form-item label="成绩公开：" prop="scoreState">
        <el-checkbox
          v-model="scoreState"
          :true-label="1"
          :false-label="2"
        >是</el-checkbox>
      </el-form-item>
      <el-form-item label="排名公开：" prop="rankState">
        <el-checkbox
          v-model="rankState"
          :true-label="1"
          :false-label="2"
        >是</el-checkbox>
      </el-form-item>
    </div>
  </el-form>
</template>

<script>
import { loginSysTime } from 'api/common.js'
import * as dayjs from 'dayjs'
import { mapState } from 'vuex'
import { mapGetters } from 'vuex'
import { mapMutations } from 'vuex'
export default {
  data() {
    return {
      editForm: {
        rules: {
          name: [
            { required: true, message: '请输入考试名称', trigger: 'blur' },
          ],
          passScore: [
            { required: true, message: '请输入及格分数', trigger: 'blur' },
          ],
          examTimes: [{
            validator: (rule, value, callback) => {
              if (!value || value.length === 0) {
                return callback(new Error('请填写考试时间'))
              }
              
              if (value[0].getTime() >= value[1].getTime()){
                return callback(new Error('开始时间必须小于结束时间'))
              }
              loginSysTime({}).then((res) => {
                if (value[1].getTime() <= dayjs(res.data, 'YYYY-MM-DD HH:mm:ss').toDate().getTime()) {
                  return callback(new Error('结束时间必须大于当前时间'))
                }
                
                return callback()
              })
          }}],
          markTimes: [{
            validator: (rule, value, callback) => {
              if (!value || value.length === 0) {
                return callback(new Error('请填写阅卷时间'))
              }
              if (value[0].getTime() >= value[1].getTime()){
                return callback(new Error('开始时间必须小于结束时间'))
              }
              if (value[0].getTime() <= this.examTimes[1].getTime()) {
                return callback(new Error('开始时间必须大于考试结束时间'))
              }

              return callback()
          }}],
        },
      },
    }
  },
  computed: {
    ...mapState('exam', [
      'examInfo',
    ]),
    ...mapGetters('exam',[
      'totalScore', 
      'markQuestions', 
    ]),
    genType: {
      get () {
        return this.examInfo.exam.genType
      },
    },
    name: {
      get () {
        return this.examInfo.exam.name
      },
      set (value) {
        this.nameUpdate(value)
      }
    },
    timeType: {
      get () {
        return this.examInfo.exam.timeType
      },
      set (value) {
        this.timeTypeUpdate(value)
      }
    },
    examTimes: {
      get () {
        return this.examInfo.exam.examTimes
      },
      set (value) {
        this.examTimesUpdate(value)
      }
    },
    markTimes: {
      get () {
        return this.examInfo.exam.markTimes
      },
      set (value) {
        this.markTimesUpdate(value)
      }
    },
    passScore: {
      get () {
        return this.examInfo.exam.passScore
      },
      set (value) {
        this.passScoreUpdate(value)
      }
    },
    sxes: {
      get () {
        return this.examInfo.exam.sxes
      },
      set (value) {
        this.sxesUpdate(value)
      }
    },
    showType: {
      get () {
        return this.examInfo.exam.showType
      },
      set (value) {
        this.showTypeUpdate(value)
      }
    },
    anonState: {
      get () {
        return this.examInfo.exam.anonState
      },
      set (value) {
        this.anonStateUpdate(value)
      }
    },
    scoreState: {
      get () {
        return this.examInfo.exam.scoreState
      },
      set (value) {
        this.scoreStateUpdate(value)
      }
    },
    rankState: {
      get () {
        return this.examInfo.exam.rankState
      },
      set (value) {
        this.rankStateUpdate(value)
      }
    },
  },
  mounted: function () {
    if (this.genType === 2) {
      console.log('mounted：随机组卷，去掉防作弊')
      this.sxes = []
    }

    if (!this.name) {
      this.name = '考试-' + dayjs().add(1, 'day').format('YYYYMMDD')
    }
    if (this.timeType === 1 && !this.examTimes.length) {
      var examStartTime = dayjs().add(1, 'day').hour(8).minute(0).second(0).toDate()
      var examEndTime = dayjs().add(1, 'day').hour(10).minute(0).second(0).toDate()
      this.examTimes = [examStartTime, examEndTime]
    }
    if (this.timeType === 1 && this.markQuestions.length && !this.markTimes.length) {
      var markStartTime = dayjs().add(1, 'day').hour(14).minute(0).second(0).toDate()
      var markEndTime = dayjs().add(1, 'day').hour(18).minute(0).second(0).toDate()
      this.markTimes = [markStartTime, markEndTime]
    }
    if (!this.passScore || this.passScore >= this.totalScore) {
      this.passScore = Math.round(this.totalScore * 0.6)
    }
  },
  methods: {
    ...mapMutations('exam', [
      'nameUpdate',
      'timeTypeUpdate', 
      'examTimesUpdate', 
      'markTimesUpdate', 
      'passScoreUpdate', 
      'sxesUpdate', 
      'showTypeUpdate', 
      'anonStateUpdate', 
      'scoreStateUpdate', 
      'rankStateUpdate', 
    ]), 
    // 下一步
    next() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return
        }
        this.$parent.activeIndex++
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.exam-setting-box {
  margin: 50px auto auto 100px;
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
