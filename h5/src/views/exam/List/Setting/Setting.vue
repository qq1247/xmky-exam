<!--
 * @Description: 设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 13:12:53
-->
<template>
  <div class="container">
    <el-form
      :model="examForm"
      :rules="examForm.rules"
      ref="examForm"
      label-width="100px"
    >
      <el-form-item label="试卷名称" prop="name">
        <el-input
          placeholder="请输入试卷名称"
          v-model="examForm.name"
        ></el-input>
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
          ></el-option>
        </CustomSelect>
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
      <el-form-item
        label="阅卷时间"
        prop="markTime"
        v-if="examForm.showMarkTime"
      >
        <el-date-picker
          v-model="examForm.markTime"
          type="datetimerange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
        ></el-date-picker>
      </el-form-item>
      <!-- <el-form-item label="成绩公开">
          <el-checkbox v-model="examForm.scoreState">是</el-checkbox>
        </el-form-item>
        <el-form-item label="排名公开">
          <el-checkbox v-model="examForm.rankState">是</el-checkbox>
        </el-form-item>
        <el-form-item label="考试方式">
          <el-checkbox v-model="examForm.loginType">免登录</el-checkbox>
        </el-form-item> -->
    </el-form>
    <div class="form-footer">
      <el-button @click="addOrEdit" type="primary">{{
        id ? '修改' : '添加'
      }}</el-button>
    </div>
  </div>
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
    CustomSelect,
  },
  data() {
    const validateExamTime = (rule, value, callback) => {
      if (value.length == 0) {
        return callback(new Error('请选择考试时间'))
      }
      if (
        dayjs(value[0]).isSameOrBefore(dayjs()) ||
        dayjs(value[1]).isSameOrBefore(dayjs())
      ) {
        return callback(
          new Error(`请选择 ${dayjs().format('YYYY-MM-DD HH:mm:ss')} 后的时间`)
        )
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
        return callback(
          new Error(
            `请选择 ${dayjs(this.examForm.examTime[1]).format(
              'YYYY-MM-DD HH:mm:ss'
            )} 后的时间`
          )
        )
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
        scoreState: false,
        rankState: false,
        loginType: false,
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
          scoreState,
          rankState,
          loginType,
        },
      } = await examGet({ id: this.id })
      this.$nextTick(() => {
        this.examForm.name = name
        this.examForm.selectPaperId = paperId
        this.examForm.paperName = paperName
        this.examForm.scoreState = scoreState == 1
        this.examForm.rankState = rankState == 1
        this.examForm.loginType = loginType == 2
        this.examForm.examTime = [startTime, endTime]
        const nextDay = dayjs(
          new Date().getTime() + 1000 * 60 * 60 * 24
        ).format('YYYY-MM-DD')
        if (markStartTime || markEndTime) {
          this.examForm.markTime = [
            markStartTime || `${nextDay} 14:00:00`,
            markEndTime || `${nextDay} 18:00:00`,
          ]
        }

        this.$refs['paperSelect'] &&
          this.$refs['paperSelect'].$refs['elSelect'].cachedOptions.push({
            currentLabel: paperName,
            currentValue: paperId,
            label: paperName,
            value: paperId,
          })
      })
    } else {
      const nextDay = dayjs(new Date().getTime() + 1000 * 60 * 60 * 24).format(
        'YYYY-MM-DD'
      )
      this.examForm.examTime = [`${nextDay} 08:00:00`, `${nextDay} 12:00:00`]
      this.examForm.markTime = [`${nextDay} 14:00:00`, `${nextDay} 18:00:00`]
    }
  },
  methods: {
    // 获取试卷列表
    async getPaperList(curPage = 1, name = '') {
      const paperList = await paperListPage({
        name,
        state: 1,
        curPage,
        pageSize: this.examForm.pageSize,
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
      this.examForm.showMarkTime = selectPaper[0].markType === 1 ? false : true
    },
    // 添加试卷信息
    addOrEdit() {
      this.$refs['examForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let params = {
          name: this.examForm.name,
          startTime: this.examForm.examTime[0],
          endTime: this.examForm.examTime[1],
          scoreState: this.examForm.scoreState ? 1 : 2,
          rankState: this.examForm.rankState ? 1 : 2,
          loginType: this.examForm.loginType ? 2 : 1,
          paperId: this.examForm.selectPaperId,
          examTypeId: this.examTypeId,
        }

        if (this.examForm.markTime.length) {
          params = {
            markStartTime: this.examForm.markTime[0],
            markEndTime: this.examForm.markTime[1],
            ...params,
          }
        }

        const res = this.id
          ? await examEdit({
              ...params,
              id: this.id,
            })
          : await examAdd(params)

        if (res?.code == 200) {
          this.$message.success(!this.id ? '添加成功！' : '修改成功！')
          this.$router.back()
        } else {
          this.$message.error(!this.id ? '添加失败！' : '修改失败！')
        }
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  padding-left: 100px;
}
</style>
