<template>
  <el-form
    ref="editForm"
    :model="exam"
    :rules="editForm.rules"
    label-width="120px"
    label-position="left"
    size="small"
    class="exam-setting-box"
  >
    <div>
      <el-form-item label="考试名称：" prop="name">
        <el-input v-model="exam.name" placeholder="请输入考试名称" />
      </el-form-item>
      <el-form-item label="时间类型" prop="timeType">
        <el-switch
          v-model="exam.timeType"
          active-color="#0094e5"
          :active-value="1"
          active-text="限时"
          inactive-color="#ff4949"
          :inactive-value="2"
          inactive-text="不限时"
        />
      </el-form-item>
      <el-form-item v-if="exam.timeType === 1" label="考试时间：" prop="examTimes">
        <el-date-picker
          v-model="exam.examTimes"
          type="datetimerange"
          align="right"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="['08:00:00', '10:00:00']">
        </el-date-picker>
      </el-form-item>
      <el-form-item v-if="exam.timeType === 1 && markExamQuestions.length" label="阅卷时间：" prop="markTimes">
        <el-date-picker
          v-model="exam.markTimes"
          type="datetimerange"
          align="right"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="['08:00:00', '10:00:00']">
        </el-date-picker>
        <div style="background-color: #fff4e8; color: #fb901b; height: 20px; line-height: 20px;">
        &nbsp;第<template v-for="examQuestion of markExamQuestions">{{examQuestion.question.no}}、</template>题需要人工阅卷
        </div>
      </el-form-item>
      <el-form-item label="及格分数：" prop="passScore">
        <el-input-number 
          v-model="exam.passScore" 
          controls-position="right" 
          :min="0" 
          :max="totalScore - 1"
          :step="10"
          ></el-input-number> / {{totalScore}}分
      </el-form-item>
      <el-form-item label="防作弊：" prop="sxes">
        <el-checkbox-group v-model="exam.sxes">
          <el-checkbox
            :label="1"
            :key="sxes1"
            >试题乱序
          </el-checkbox>
          <el-checkbox
            :label="2"
            :key="sxes2"
            >选项乱序
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="展示方式：" prop="showType">
        <el-radio-group v-model="editForm.showType">
          <el-radio
            v-for="dict in $tools.getDicts('PAPER_SHOW_TYPE')"
            :key="`showType${dict.dictKey}`"
            :label="Number(dict.dictKey)"
            >{{ dict.dictValue }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="匿名阅卷：" prop="anonState">
        <el-checkbox
          v-model="exam.anonState"
          :true-label="1"
          :false-label="2"
        >是</el-checkbox>
      </el-form-item>
      <el-form-item label="成绩公开：" prop="scoreState">
        <el-checkbox
          v-model="exam.scoreState"
          :true-label="1"
          :false-label="2"
        >是</el-checkbox>
      </el-form-item>
      <el-form-item label="排名公开：" prop="rankState">
        <el-checkbox
          v-model="exam.rankState"
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
              if (!value) {
                return callback(new Error('请填写考试时间'))
              }
              if (value[0].getTime() >= value[1].getTime()){
                return callback(new Error('开始时间必须小于结束时间'))
              }
              loginSysTime({}).then((res) => {
                if (value[1].getTime() <= dayjs(res.data, 'YYYY-MM-DD HH:mm:ss').$d.getTime()) {
                  return callback(new Error('结束时间必须大于当前时间'))
                }
                
                return callback()
              })
          }}],
          markTimes: [{
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请填写阅卷时间'))
              }
              if (value[0].getTime() >= value[1].getTime()){
                return callback(new Error('开始时间必须小于结束时间'))
              }
              loginSysTime({}).then((res) => {
                if (value[0].getTime() <= this.exam.examTimes[1].getTime()) {
                  return callback(new Error('开始时间必须大于考试结束时间'))
                }

                return callback()
              })
          }}],
        },
      },
    }
  },
  async created() {

  },
  watch: {
  },
  computed: {
    examQuestions: {
      get: function () {
        return this.$store.state.exam.examQuestions
      },
      set: function (newValue) {
        this.$store.state.exam.examQuestions = newValue
      }
    },
    exam: {
      get: function () {
        return this.$store.state.exam.exam
      },
      set: function (newValue) {
        this.$store.state.exam = newValue
      }
    },
    markExamQuestions: function () {// 需要人工阅卷的题数
      return this.examQuestions.filter(function(pre, cur) {
        return (pre.type === 2 && pre.question.markType === 2) 
      })
    },
    totalScore: function() {
      let totalScore = 0
      this.examQuestions.forEach(examQuestion => {
        totalScore += examQuestion.type === 1 ? 0 : examQuestion.question.score
      });
      return totalScore
    }
  },
  methods: {
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
