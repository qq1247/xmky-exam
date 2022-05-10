<template>
  <el-form
    ref="examForm"
    :model="examForm"
    :rules="examForm.rules"
    label-width="100px"
  >
    <el-form-item label="时间选项" prop="timeType">
      <el-select
        v-model="examForm.timeType"
        clearable
        placeholder="请选择时间选项"
      >
        <el-option
          v-for="type in examForm.timeTypes"
          :key="type.key"
          :label="type.value"
          :value="type.key"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="操作选项" prop="timeHandler">
      <el-select
        v-model="examForm.timeHandler"
        clearable
        placeholder="请选择操作选项"
      >
        <el-option
          v-for="handler in examForm.timeHandlers"
          :key="handler.key"
          :label="handler.value"
          :value="handler.key"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="分钟数值" prop="timeSecond">
      <el-input v-model="examForm.timeSecond" placeholder="请输入分钟数值" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="setTime">设置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { examTime } from 'api/exam'
export default {
  data() {
    return {
      id: null,
      examForm: {
        paperId: 0,
        timeType: 1,
        timeTypes: [
          {
            key: 1,
            value: '考试开始时间'
          },
          {
            key: 2,
            value: '考试结束时间'
          },
          {
            key: 3,
            value: '阅卷开始时间'
          },
          {
            key: 4,
            value: '阅卷结束时间'
          }
        ],
        timeHandler: 1,
        timeHandlers: [
          {
            key: 1,
            value: '提前'
          },
          {
            key: 2,
            value: '延后'
          }
        ],
        timeSecond: '',
        rules: {
          timeSecond: [
            { required: true, message: '请填写时间数值', trigger: 'blur' }
          ]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    // 变更时间
    async setTime() {
      this.$refs['examForm'].validate(async(valid) => {
        if (!valid) {
          return
        }

        const minute =
          this.examForm.timeHandler === 1
            ? -this.examForm.timeSecond
            : this.examForm.timeSecond
        const res = await examTime({
          id: this.id,
          timeType: this.examForm.timeType,
          minute
        })

        if (res?.code === 200) {
          this.$message.success('设置成功！')
          this.$router.back()
        } else {
          this.$message.error('设置失败！')
        }
      })
    }
  }
}
</script>
