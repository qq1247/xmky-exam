<template>
  <div>
    <el-form
      ref="editForm"
      :model="editForm"
      :rules="editForm.rules"
      label-width="120px"
      label-position="left"
      size="small"
      class="exam-setting-box"
    >
      <div>
        <el-form-item label="考试名称：" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="考试时间：" prop="examTime">
          <el-date-picker
            v-model="editForm.examTime"
            type="datetimerange"
            align="right"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            :default-time="['08:00:00', '10:00:00']">
          </el-date-picker>
        </el-form-item>
      </div>
      <div v-if="subjective">
        <el-form-item
          label="阅卷开始时间"
          style="visibility: hidden"
        ></el-form-item>
        <el-form-item label="阅卷开始时间：" prop="markStartTime">
          <el-date-picker
            v-model="editForm.markStartTime"
            type="datetime"
            placeholder="开始日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="阅卷结束时间：" prop="markEndTime">
          <el-date-picker
            v-model="editForm.markEndTime"
            type="datetime"
            placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
      </div>

      <div>更多</div>
      <div :style="{ display: rotate ? 'none' : '' }">
        <el-form-item label="及格（%）：" prop="passScore">
          <el-input
            v-model="editForm.passScore"
            placeholder="请输入及格百分比"
          />
        </el-form-item>
        <el-form-item label="防作弊：" prop="options">
          <el-checkbox-group v-model="editForm.options">
            <el-checkbox
              v-for="option in optionsArr"
              :label="option.value"
              :key="option.value"
              >{{ option.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="展示方式：" prop="showType">
          <el-radio-group v-model="editForm.showType">
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
            v-model="editForm.anonState"
            :true-label="1"
            :false-label="2"
          ></el-checkbox>
        </el-form-item>
        <el-form-item label="成绩公开：" prop="scoreState">
          <el-checkbox
            v-model="editForm.scoreState"
            :true-label="1"
            :false-label="2"
          ></el-checkbox>
        </el-form-item>
        <el-form-item label="排名公开：" prop="rankState">
          <el-checkbox
            v-model="editForm.rankState"
            :true-label="1"
            :false-label="2"
          ></el-checkbox>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script>
import * as dayjs from 'dayjs'
export default {
  data() {
    return {
      editForm: {
        name: '',
        examTime: [new Date(), new Date()],
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
            { required: true, message: '请填写考试名称', trigger: 'blur' },
          ],
          startTime: [{ required: true, validator: validateStartTime }],
          endTime: [{ required: true, validator: validateEndTime }],
          markStartTime: [{ required: true, validator: validateMarkStartTime }],
          markEndTime: [{ required: true, validator: validateMarkEndTime }],
        },
      },
      isSetQuick: false,
    }
  },
  async created() {
    this.editForm.startTime = this.getHour(2)
    this.editForm.endTime = this.getHour(1, this.editForm.startTime)
  },
  watch: {
    editForm: {
      handler() {
        if (this.isSetQuick) {
          this.setQuickInfo()
        }
      },
      immediate: true,
      deep: true,
    },
    'editForm.options': {
      handler(newName, oldName) {
        if (this.isSetQuick) {
          let quickInfo = getQuick()
          quickInfo.options = newName
          setQuick(quickInfo)
        }
      },
      immediate: true,
    },
  },
  methods: {
    // 下一步
    next() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return
        }
        let quickInfo = getQuick()
        let params = {
          name: this.editForm.name,
          startTime: this.editForm.startTime,
          endTime: this.editForm.endTime,
          paperId: quickInfo.id,
          examTypeId: this.examTypeId || quickInfo.examTypeId,
        }

        if (this.editForm.showMarkTime) {
          params = {
            markStartTime: this.editForm.markStartTime,
            markEndTime: this.editForm.markEndTime,
            ...params,
          }
        }

        const res = quickInfo.examId
          ? await examEdit({
              ...params,
              id: quickInfo.examId,
            })
          : await examAdd(params)
        const examId = res.data || quickInfo?.examId
        this.setQuickInfo(examId)
        this.paperSxe({ id: quickInfo.id, options: this.editForm.options })
        if (examId) {
          this.anonymous({ id: examId, state: this.editForm.anonState })
          this.ranking({ id: examId, state: this.editForm.rankState })
          this.score({ id: examId, state: this.editForm.scoreState })
        }
        this.$parent.activeIndex++
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
        examName: this.editForm.name,
        startTime: this.editForm.startTime,
        endTime: this.editForm.endTime,
        markStartTime: this.editForm.markStartTime,
        markEndTime: this.editForm.markEndTime,
        passScore: this.editForm.passScore, // 及格%
        options: this.editForm.options, // 防作弊
        showType: this.editForm.showType, // 展示方式
        anonState: this.editForm.anonState, // 匿名阅卷
        scoreState: this.editForm.scoreState, // 成绩公开
        rankState: this.editForm.rankState, // 排名公开
      }
      setQuick(quickInfo)
    },
  },
}
</script>

<style lang="scss" scoped>
.exam-setting-box {
  display: flex;
  flex-wrap: wrap;
  padding: 15px 150px;
  justify-content: space-between;
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
