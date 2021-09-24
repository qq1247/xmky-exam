<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.name"
            class="query-input"
          ></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="search" icon="el-icon-search" type="primary"
          >查询</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <div class="exam-item">
          <div
            class="exam-content exam-add"
            @click=";(examForm.show = true), (examForm.edit = false)"
          >
            <i class="common common-plus"></i>
            <span>添加考试库</span>
          </div>
        </div>
        <ListCard
          v-for="(item, index) in examList"
          :key="index"
          :data="item"
          name="examList"
          @edit="edit"
          @del="del"
          @onLine="onLine"
          @publish="publish"
          @statistics="statistics"
          @read="read"
        ></ListCard>
      </div>
      <!-- 分页 -->
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="total"
        :page-size="pageSize"
        :current-page="curPage"
        @current-change="pageChange"
      ></el-pagination>
    </div>

    <!-- 新增 |修改考试 -->
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      center
      width="50%"
      :close-on-click-modal="false"
      @close="resetData('examForm')"
    >
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
        <el-form-item label="考试时间" prop="examTime" required>
          <el-date-picker
            v-model="examForm.examTime"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="阅卷时间" prop="markTime" required>
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
      <div class="dialog-footer" slot="footer">
        <el-button @click="addOrEdit">
          {{ examForm.edit ? '修改' : '添加' }}
        </el-button>
        <el-button @click="examForm.show = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 阅卷设置 -->
    <el-dialog
      :visible.sync="examForm.readShow"
      :show-close="false"
      center
      width="60%"
      :close-on-click-modal="false"
      @close="resetData('userForm')"
    >
      <el-form :model="examForm" ref="userForm" label-width="100px">
        <el-form-item label="阅卷方式">
          <el-radio
            v-for="item in examForm.examRadios"
            :key="item.value"
            v-model="examForm.examRadio"
            :label="item.value"
            @change="selectPaperType"
            prop="examRadio"
            >{{ item.name }}</el-radio
          >
        </el-form-item>
        <el-row v-for="(item, index) in examForm.examRemarks" :key="item.id">
          <el-col :span="12">
            <el-form-item
              label="考试用户"
              v-if="examForm.examRadio == 1"
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
                @input="searchUser"
                @change="selectExamUser($event, index)"
                @currentChange="getMoreUser"
                @visibleChange="getUserList"
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
              label="阅卷人"
              :prop="`examRemarks.${index}.examCheckPerson`"
              :rules="[
                { required: true, message: '请选择阅卷人', trigger: 'change' },
              ]"
            >
              <CustomSelect
                ref="markUserSelect"
                :multiple="false"
                placeholder="请选择阅卷人"
                :value="examForm.examRemarks[index].examCheckPerson"
                :total="examForm.total"
                @input="searchUser"
                @change="selectPerson($event, index)"
                @currentChange="getMoreUser"
                @visibleChange="getUserList"
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
        <div class="remark-buttons">
          <el-form-item>
            <el-button
              @click="remarkAdd"
              type="primary"
              size="mini"
              icon="el-icon-plus"
              >添加阅卷人</el-button
            >
            <el-button
              v-if="examForm.examRemarks.length > 1"
              @click="remarkDel"
              size="mini"
              icon="el-icon-minus"
              >删除阅卷人</el-button
            >
          </el-form-item>
        </div>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="editExamRead">保存</el-button>
        <el-button @click="examForm.readShow = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 考试信息统计 -->
    <el-dialog :visible.sync="examForm.infoShow" width="60%">
      <!-- 考试信息 -->
      <el-descriptions
        class="stat-desc"
        title="考试信息"
        :column="3"
        size="medium"
        border
      >
        <el-descriptions-item label="卷面总分">{{
          examForm.statisticsInfo.paperTotalScore
        }}</el-descriptions-item>
        <el-descriptions-item label="及格分数">{{
          (
            (examForm.statisticsInfo.paperTotalScore *
              examForm.statisticsInfo.paperPassScore) /
            100
          ).toFixed()
        }}</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{
          examForm.statisticsInfo.examStartTime
            | timeDiff(
              examForm.statisticsInfo.examStartTime,
              examForm.statisticsInfo.examEndTime
            )
        }}</el-descriptions-item>
      </el-descriptions>

      <!-- 分数统计 -->
      <el-descriptions
        class="stat-desc"
        title="分数统计"
        :column="3"
        size="medium"
        border
      >
        <el-descriptions-item label="最高分">{{
          examForm.statisticsInfo.max || 0
        }}</el-descriptions-item>
        <el-descriptions-item label="最低分">{{
          examForm.statisticsInfo.min || 0
        }}</el-descriptions-item>
        <el-descriptions-item label="平均分">{{
          examForm.statisticsInfo.avg || 0
        }}</el-descriptions-item>
      </el-descriptions>

      <!-- 用户统计 -->
      <el-descriptions
        class="stat-desc"
        title="用户统计"
        :column="3"
        size="medium"
        border
      >
        <el-descriptions-item label="及格人数">{{
          examForm.statisticsInfo.examUserAnswer || 0
        }}</el-descriptions-item>
        <el-descriptions-item label="实际人数">{{
          examForm.statisticsInfo.examUserSum || 0
        }}</el-descriptions-item>
        <el-descriptions-item label="及格比例"
          >{{
            examForm.statisticsInfo.examUserAnswer ||
            examForm.statisticsInfo.examUserSum
              ? (examForm.statisticsInfo.examUserAnswer /
                  examForm.statisticsInfo.examUserSum) *
                100
              : 0
          }}%</el-descriptions-item
        >
      </el-descriptions>

      <!-- 交卷统计 -->
      <el-descriptions
        class="stat-desc"
        title="交卷统计"
        :column="2"
        size="medium"
        border
      >
        <el-descriptions-item label="最早交卷">{{
          examForm.statisticsInfo.maxExam || '待统计'
        }}</el-descriptions-item>
        <el-descriptions-item label="最迟交卷">{{
          examForm.statisticsInfo.minExam || '待统计'
        }}</el-descriptions-item>
        <el-descriptions-item label="最长耗时">{{
          (examForm.statisticsInfo.maxTime || 0) | formateTime
        }}</el-descriptions-item>
        <el-descriptions-item label="最短耗时">{{
          (examForm.statisticsInfo.minTime || 0) | formateTime
        }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 实时在线人数 -->
    <el-drawer
      title="实时在线考试人员"
      :visible.sync="examForm.lineShow"
      direction="ltr"
      size="30%"
      @closed="lineEnd"
    >
      <el-row>
        <template v-if="examForm.examUserList.length > 0">
          <el-col
            :span="8"
            v-for="item in examForm.examUserList"
            :key="item.id"
          >
            <div
              :class="[
                'line-user',
                examForm.onLineUser.includes(item.id) ? 'line' : '',
              ]"
            >
              <i class="common common-onLine"></i>
              <span class="line-name">{{ item.name }}</span>
            </div>
          </el-col>
        </template>
        <el-empty v-else description="暂无在线人员"> </el-empty>
      </el-row>
    </el-drawer>
  </div>
</template>

<script>
import {
  examAdd,
  examDel,
  examEdit,
  examOnLine,
  examPublish,
  examUserList,
  examListPage,
  examGradeReport,
  examMarkUserList,
  examUpdateMarkSet,
} from 'api/exam'
import { paperListPage } from 'api/paper'
import { userListPage } from 'api/user'
import { questionListPage } from 'api/question'
import ListCard from 'components/ListCard.vue'
import CustomSelect from 'components/CustomSelect.vue'
import * as dayjs from 'dayjs'
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'
dayjs.extend(isSameOrBefore)
export default {
  components: {
    ListCard,
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
      pageSize: 5,
      total: 0,
      curPage: 1,
      timeLine: null,
      queryForm: {
        name: '',
        examTypeId: 0,
        examTypeName: '',
      },
      examForm: {
        id: 0,
        name: '',
        paperId: 0,
        edit: false,
        show: false,
        readShow: false,
        infoShow: false,
        lineShow: false,
        selectPaperId: '',
        total: 0,
        curPage: 1,
        pageSize: 5,
        paperList: [],
        examTime: [],
        markTime: [],
        scoreState: false,
        rankState: false,
        loginType: false,
        statisticsInfo: {},
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
        examCheckPersons: [],
        examQuestionNums: [],
        examRemarks: [],
        examUser: [],
        examUsers: [],
        examUserList: [],
        onLineUser: [],
        rules: {
          name: [
            { required: true, message: '请填写试卷名称', trigger: 'blur' },
          ],
          selectPaperId: [
            { required: true, message: '请选择试卷', trigger: 'blur' },
          ],
          examTime: [{ validator: validateExamTime }],
          markTime: [{ validator: validateMarkTime }],
        },
      },
      examList: [],
    }
  },
  filters: {
    timeDiff(start, end) {
      const hour = dayjs(start).diff(dayjs(end), 'hour')
      const minute = dayjs(start).diff(dayjs(end), 'minute')
      const second = dayjs(start).diff(dayjs(end), 'second')
      return `${hour}'${minute}''${second}'''`
    },
    formateTime(value) {
      let time = value / 1000
      let hour = Math.floor(time / 60 / 60) % 24
      let minute = Math.floor(time / 60) % 60
      let second = Math.floor(time) % 60
      return `${hour}'${minute}''${second}'''`
    },
  },
  mounted() {
    const { id } = this.$route.query
    this.queryForm.examTypeId = id
    this.query()
  },
  methods: {
    // 查询
    async query() {
      const examList = await examListPage({
        examTypeId: this.queryForm.examTypeId,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })
      this.examList = examList.data.list
      this.total = examList.data.total
    },
    search() {
      this.curPage = 1
      this.query()
    },
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
    },
    // 添加试卷信息
    addOrEdit() {
      this.$refs['examForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        const params = {
          name: this.examForm.name,
          startTime: this.examForm.examTime[0],
          endTime: this.examForm.examTime[1],
          markStartTime: this.examForm.markTime[0],
          markEndTime: this.examForm.markTime[1],
          scoreState: this.examForm.scoreState ? 1 : 2,
          rankState: this.examForm.rankState ? 1 : 2,
          loginType: this.examForm.loginType ? 2 : 1,
          paperId: this.examForm.selectPaperId,
          examTypeId: this.queryForm.examTypeId,
        }

        const res = this.examForm.edit
          ? await examEdit({
              ...params,
              id: this.examForm.id,
            })
          : await examAdd(params)

        if (res?.code == 200) {
          this.examForm.show = false
          this.$message.success(
            !this.examForm.edit ? '添加成功！' : '修改成功！'
          )
          if (this.examForm.edit) {
            this.pageChange()
          } else {
            this.pageChange(1)
          }
        } else {
          this.$message.error(!this.examForm.edit ? '添加失败！' : '修改失败！')
        }
      })
    },
    // 编辑分类
    async edit({
      id,
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
    }) {
      this.examForm.show = true
      this.examForm.edit = true
      this.$nextTick(() => {
        this.examForm.id = id
        this.examForm.name = name
        this.examForm.selectPaperId = paperId
        this.examForm.paperName = paperName
        this.examForm.scoreState = scoreState == 1
        this.examForm.rankState = rankState == 1
        this.examForm.loginType = loginType == 2
        this.examForm.examTime = [startTime, endTime]
        this.examForm.markTime = [markStartTime, markEndTime]

        this.$refs['paperSelect'] &&
          this.$refs['paperSelect'].$refs['elSelect'].cachedOptions.push({
            currentLabel: paperName,
            currentValue: paperId,
            label: paperName,
            value: paperId,
          })
      })
    },
    // 删除分类
    del({ id, name }) {
      this.$confirm(`确认删除【${name}】吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await examDel({ id })
          if (res?.code == 200) {
            this.total -= 1
            if (this.total <= this.pageSize) {
              this.pageChange(1)
              return
            }
            this.$message.success('删除成功！')
            this.total % this.pageSize == 0 && this.total != this.pageSize
              ? ((this.curPage -= 1), this.pageChange(this.curPage))
              : this.pageChange(this.curPage)
          } else {
            this.$message.error(res.msg || '删除失败！')
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 在线人员
    async onLine({ id, state }) {
      if (state === 2) {
        this.$message.error('请先发布考试！')
        return
      }
      this.examForm.lineShow = true
      const resList = await examUserList({ id })
      resList?.code === 200 && (this.examForm.examUserList = resList.data)
      if (resList?.code === 200 && resList.data.length > 0) {
        const ids = resList.data.reduce((acc, cur) => {
          acc.push(cur.id)
          return acc
        }, [])
        let resLine = await examOnLine({ ids })
        resLine?.code === 200 && (this.examForm.onLineUser = resLine.data)
        this.timeLine = setInterval(async () => {
          resLine = await examOnLine({ ids })
          resLine?.code === 200 && (this.examForm.onLineUser = resLine.data)
        }, 30 * 1000)
      }
    },
    // 关闭在线人员弹窗
    lineEnd() {
      clearInterval(this.timeLine)
      this.timeLine = null
    },
    // 统计
    async statistics({ id, state, markEndTime }) {
      if (state === 2 || dayjs().isSameOrBefore(dayjs(markEndTime))) {
        this.$message.error('阅卷后可查看')
        return
      }
      const statisticsInfo = await examGradeReport({ examId: id })
      statisticsInfo.code === 200 &&
        ((this.examForm.statisticsInfo = statisticsInfo.data),
        (this.examForm.infoShow = true))
    },
    // 阅卷设置
    async read({ id, paperId, state }) {
      if (state == 2) {
        this.$message.error('请先发布考试！')
        return
      }

      const examMarkUser = await examMarkUserList({ id })

      const num = examMarkUser.data.length > 0 ? examMarkUser.data.length : 1
      this.examForm.examRemarks = []
      for (let index = 0; index < num; index++) {
        this.examForm.examRemarks.push({
          examCheckPerson: '',
          examQuestionNum: [],
          examUser: [],
        })
      }

      this.examForm.readShow = true
      this.examForm.id = id
      this.examForm.paperId = paperId

      this.$nextTick(() => {
        if (examMarkUser.data.length > 0) {
          this.examForm.examRemarks = []
          examMarkUser.data.map((item, index) => {
            this.examForm.examRemarks.push({
              examCheckPerson: item.markUserId,
              examUser: item.examUserList.map((user) => user.id),
              examQuestionNum: [],
            })
          })
        }

        examMarkUser.data.map((item, index) => {
          const cachedOptions = item.examUserList.reduce((acc, cur) => {
            acc.push({
              currentLabel: cur.name,
              currentValue: cur.id,
              label: cur.name,
              value: cur.id,
            })
            return acc
          }, [])

          this.$refs['markExamUserSelect'] &&
            this.$refs['markExamUserSelect'][index].$refs[
              'elSelect'
            ].cachedOptions.push(...cachedOptions)

          this.$refs['markUserSelect'] &&
            this.$refs['markUserSelect'][index].$refs[
              'elSelect'
            ].cachedOptions.push({
              currentLabel: item.markUserName,
              currentValue: item.markUserId,
              label: item.markUserName,
              value: item.markUserId,
            })
        })
      })
    },
    // 考试发布
    async publish({ id, state }) {
      if (state == 1) {
        this.$message.warning('考试已发布!')
        return
      }
      this.$confirm(`确认发布考试吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await examPublish({ id })
          res?.code == 200
            ? (this.$message.success('考试发布成功！'), this.pageChange())
            : this.$message.error('请重新发布考试！')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 选择阅卷方式
    selectPaperType(e) {
      if (e == 0) {
        this.$message.warning('暂未开放')
        this.examForm.examRadio = 1
        return
      }
      this.examForm.examRadio = e
    },
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const examUsers = await userListPage({
        name,
        curPage,
        pageSize: this.examForm.pageSize,
      })

      this.examForm.examUsers = examUsers.data.list
      this.examForm.total = examUsers.data.total
    },
    // 获取更多用户
    getMoreUser(curPage, name) {
      this.getUserList(curPage, name)
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(1, name)
    },
    // 选择阅卷人员
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
    // 获取题号
    async getQuestionNumList(id = '', curPage = 1) {
      this.examForm.curPage = curPage
      const params = {
        paperId: this.examForm.paperId,
        curPage,
        pageSize: this.examForm.pageSize,
      }
      !id ? params : (params.id = Number(id))
      const questionNumList = await questionListPage(params)
      this.examForm.examQuestionNums = questionNumList.data.list
      this.examForm.total = questionNumList.data.total
    },
    // 选择题号
    selectQuestionNum(e, index) {
      this.examForm.examRemarks[index].examQuestionNum = e
    },
    // id查询题号
    searchQuestionNum(id) {
      this.getQuestionNumList(id)
    },
    // 分页查询题号
    getMoreQuestionNum(curPage) {
      this.getQuestionNumList(curPage)
    },
    // 编辑阅卷
    editExamRead() {
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
          id: this.examForm.id,
          markUserIds,
        }

        this.examForm.examRadio == 0
          ? (params.questionIds = dynamic)
          : (params.examUserIds = dynamic)

        const res = await examUpdateMarkSet(params)

        res?.code == 200
          ? (this.$message('设置成功！'),
            (this.examForm.readShow = false),
            this.pageChange())
          : this.$message.error('设置失败！')
      })
    },
    // 切换分页
    pageChange(val) {
      val && (this.curPage = val)
      this.query()
    },
    // 清空还原数据
    resetData(name) {
      this.$refs[name].resetFields()
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';

.custom_select {
  width: 100%;
}

/deep/ .el-dialog__header {
  padding: 0;
}

.exam-type {
  display: flex;
}

.type-item {
  width: 150px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  border: 1px solid #d8d8d8;
  font-size: 14px;
  margin-right: 10px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  i {
    font-size: 24px;
    margin-right: 10px;
  }
  .common-selected {
    position: absolute;
    right: 5px;
    bottom: 8px;
    font-size: 12px;
    margin-right: 0;
    line-height: 0;
    color: #fff;
    &::after {
      content: '';
      display: block;
      position: absolute;
      z-index: 10;
      right: -6px;
      bottom: -9px;
      border-bottom: 25px solid #1e9fff;
      border-left: 25px solid transparent;
    }
    &::before {
      position: absolute;
      z-index: 50;
      right: -3px;
      bottom: -3px;
    }
  }
}

.type-item-active {
  border: 1px solid #1e9fff;
  color: #1e9fff;
}

.exam-remark {
  display: flex;
  margin-top: 15px;
}

.remark-percentage {
  width: 120px;
  margin: 0 5px;
}

.remark-content {
  width: 230px;
}

.remark-buttons {
  margin: 15px 0;
}

.line-user {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999;
  .common-onLine {
    font-size: 70px;
  }
  .line-name {
    font-size: 16px;
  }
}
.line {
  color: #1e9fff;
}
.stat-desc {
  margin-top: 30px;
}
</style>
