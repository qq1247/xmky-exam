<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input placeholder="请输入名称" v-model="queryForm.name" class="query-input"></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query()" icon="el-icon-search" type="primary">查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <div class="exam-item">
          <div class="exam-content exam-add" @click="examForm.show = true">
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
          @publish="publish"
          @message="message"
          @read="read"
          @user="user"
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
        :current-page="1"
        @current-change="pageChange"
      ></el-pagination>
    </div>

    <!-- 新增|修改考试 -->
    <el-dialog :visible.sync="examForm.show" :show-close="false" center width="40%">
      <el-form :model="examForm" :rules="examForm.rules" ref="examForm" label-width="100px">
        <el-form-item label="试卷名称" prop="name">
          <el-input placeholder="请输入试卷名称" v-model="examForm.name"></el-input>
        </el-form-item>
        <el-form-item label="选择试卷">
          <CustomSelect
            placeholder="请选择试卷"
            :value="examForm.paperId"
            :total="examForm.total"
            :showPage="true"
            :pageSize="examForm.pageSize"
            @change="selectPaper"
            @focus="getPaperList()"
            @currentChange="getMorePaper"
          >
            <el-option
              v-for="item in examForm.paperList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </CustomSelect>
        </el-form-item>
        <el-form-item label="考试时间">
          <el-date-picker
            v-model="examForm.examTime"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="阅卷时间">
          <el-date-picker
            v-model="examForm.readPaperTime"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="成绩公开">
          <el-checkbox v-model="examForm.scoreState">是</el-checkbox>
        </el-form-item>
        <el-form-item label="排名公开">
          <el-checkbox v-model="examForm.rankState">是</el-checkbox>
        </el-form-item>
        <el-form-item label="考试方式">
          <el-checkbox v-model="examForm.loginType">免登录</el-checkbox>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="addOrEdit">
          {{
            examForm.edit ? "修改" : "添加"
          }}
        </el-button>
        <el-button @click="examForm.show = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 阅卷方式 -->
    <el-dialog :visible.sync="examForm.readShow" :show-close="false" center width="40%">
      <el-form :model="examForm" :rules="examForm.rules" ref="examForm" label-width="100px">
        <el-form-item label="阅卷方式">
          <el-radio
            v-for="item in examForm.examRadios"
            :key="item.value"
            v-model="examForm.examRadio"
            :label="item.value"
          >{{ item.name }}</el-radio>
        </el-form-item>
        <div>
          <div v-for="(item, index) in examForm.examRemarks" :key="item.id" class="exam-remark">
            <el-form-item label="阅卷人">
              <CustomSelect
                placeholder="请选择阅卷人"
                :multiple="true"
                :value="examForm.examRemarks[index].examCheckPerson"
                :total="examForm.total"
                :showPage="true"
                :remote="true"
                :reserveKeyword="true"
                :filterable="true"
                :pageSize="examForm.pageSize"
                :remoteMethod="searchPerson"
                @change="selectPerson($event, index)"
                @currentChange="getMorePerson"
              >
                <el-option
                  v-for="item in examForm.examCheckPersons"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </CustomSelect>
            </el-form-item>
            <el-form-item label="题号" v-if="examForm.examRadio == 0">
              <!-- <CustomSelect
                placeholder="请选择题号"
                :multiple="true"
                :value="examForm.examRemarks[index].examPaperNum"
                :total="examForm.total"
                :showPage="true"
                :pageSize="examForm.pageSize"
                @change="selectPerson($event, index)"
                @focus="getPersonList()"
                @currentChange="getMorePerson"
              >
                <el-option
                  v-for="item in examForm.examPaperNums"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </CustomSelect>-->
            </el-form-item>
            <el-form-item label="试卷" v-if="examForm.examRadio == 1">
              <!-- <CustomSelect
                placeholder="请选择试卷"
                :multiple="true"
                :value="examForm.examRemarks[index].examPaper"
                :total="examForm.total"
                :showPage="true"
                :pageSize="examForm.pageSize"
                @change="selectPaper"
                @focus="getPaperList()"
                @currentChange="getMorePaper"
              >
                <el-option
                  v-for="item in examForm.examPapers"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </CustomSelect>-->
            </el-form-item>
          </div>
          <div class="remark-buttons">
            <el-form-item>
              <el-button @click="remarkAdd" type="primary" size="mini" icon="el-icon-plus">添加评语</el-button>
              <el-button
                v-if="examForm.examRemarks.length > 1"
                @click="remarkDel"
                size="mini"
                icon="el-icon-minus"
              >添加评语</el-button>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="addOrEdit">编辑</el-button>
        <el-button @click="examForm.readShow = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 考试用户 -->
    <el-dialog :visible.sync="examForm.userShow" :show-close="false" center width="40%">
      <el-form :model="examForm" :rules="examForm.rules" ref="examForm" label-width="100px">
        <el-form-item label="考试用户">
          <CustomSelect
            placeholder="请选择试卷"
            :multiple="true"
            :value="examForm.examUser"
            :total="examForm.total"
            :showPage="true"
            :pageSize="examForm.pageSize"
            @change="selectUser"
            @focus="getUserList()"
            @currentChange="getMoreUser"
          >
            <el-option
              v-for="item in examForm.examUsers"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </CustomSelect>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="editExamUser">编辑</el-button>
        <el-button @click="examForm.userShow = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ListCard from "@/components/ListCard.vue";
import CustomSelect from '@/components/CustomSelect.vue';
import * as dayjs from "dayjs";
export default {
  components: {
    ListCard,
    CustomSelect
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      queryForm: {
        name: "",
        examTypeId: 0,
        examTypeName: ''
      },
      examForm: {
        id: 0,
        show: false,
        readShow: false,
        userShow: false,
        edit: false,
        name: "",
        paperId: "",
        paperList: [],
        curPage: 1,
        pageSize: 10,
        total: 0,
        examTime: '',
        readPaperTime: '',
        scoreState: false,
        rankState: false,
        loginType: false,
        examRadio: 0,
        examRadios: [
          {
            name: "按题阅卷",
            value: 0
          },
          {
            name: "按卷阅卷",
            value: 1
          }
        ],
        examCheckPersons: [],
        examPaperNums: [],
        examPapers: [],
        examRemarks: [
          {
            id: 1,
            examCheckPerson: [],
            examPaperNum: [],
            examPaper: []
          }
        ],
        examUser: [],
        examUsers: [],
        rules: {
          name: [
            { required: true, message: "请填写试卷名称", trigger: "blur" }
          ]
        }
      },
      examList: []
    }
  },
  mounted() {
    const { id } = this.$route.query
    this.queryForm.examTypeId = id
    this.query()
  },
  methods: {
    // 查询
    async query(curPage = 1) {
      const examList = await this.$https.examListPage({
        examTypeId: this.queryForm.examTypeId,
        curPage,
        pageSize: this.pageSize
      })
      this.examList = examList.data.list
      this.total = examList.data.total
    },
    // tab切换
    examNext() {
      this.examForm.tabActive = Number(this.examForm.tabActive) + 1 + "";
    },
    // 添加评语
    remarkAdd() {
      this.examForm.examRemarks.push({
        id: this.examForm.examRemarks.length + 1,
        examCheckPerson: "",
        examPaperNum: "",
        examPaper: []
      })
    },
    // 删除评语
    remarkDel() {
      this.examForm.examRemarks.pop()
    },
    // 获取试卷列表
    async getPaperList(curPage = 1) {
      const paperList = await this.$https.paperListPage({
        curPage,
        pageSize: this.examForm.pageSize
      })
      this.examForm.paperList = paperList.data.list
      this.examForm.total = paperList.data.total
    },
    // 获取更多试卷列表
    getMorePaper(curPage) {
      this.getPaperList(curPage)
    },
    // 选择试卷
    selectPaper(e) {
      this.examForm.paperId = e
    },
    // 添加试卷信息
    addOrEdit() {
      this.$refs["examForm"].validate(async (valid) => {
        if (!valid) {
          return
        }

        let params = {
          name: this.examForm.name,
          startTime: dayjs(this.examForm.examTime[0]).format('YYYY-MM-DD HH:mm:ss'),
          endTime: dayjs(this.examForm.examTime[1]).format('YYYY-MM-DD HH:mm:ss'),
          markStartTime: dayjs(this.examForm.readPaperTime[0]).format('YYYY-MM-DD HH:mm:ss'),
          markEndTime: dayjs(this.examForm.readPaperTime[1]).format('YYYY-MM-DD HH:mm:ss'),
          scoreState: this.examForm.scoreState ? 1 : 2,
          rankState: this.examForm.rankState ? 1 : 2,
          loginType: this.examForm.loginType ? 2 : 1,
          paperId: this.examForm.paperId,
          examTypeId: this.queryForm.examTypeId
        }

        const res = this.examForm.edit
          ? await this.$https.examEdit({
            ...params,
            id: this.queryForm.examTypeId,
          }).catch(err => { })
          : await this.$https.examAdd(params).catch(err => { })

        res.code === 200
          ? (
            this.$tools.message(!this.examForm.edit ? "添加成功！" : "修改成功！"),
            this.examForm.show = false,
            this.resetData('examForm'),
            this.query()
          )
          : this.$tools.message(!this.examForm.edit ? "添加失败！" : "修改失败！", "error")

      })
    },
    // 编辑分类
    edit({ name, paperId, paperName, startTimeStr, endTimeStr, markStartTimeStr, markEndTimeStr, scoreState, rankState, loginType }) {
      this.examForm.edit = true
      this.examForm.show = true
      this.examForm.name = name
      this.examForm.paperId = paperId
      this.examForm.paperName = paperName
      this.examForm.scoreState = scoreState == 1 ? true : false
      this.examForm.rankState = rankState == 1 ? true : false
      this.examForm.loginType = loginType == 2 ? true : false
      this.examForm.examTime = [new Date(startTimeStr), new Date(endTimeStr)]
      this.examForm.readPaperTime = [new Date(markStartTimeStr), new Date(markEndTimeStr)]
    },
    // 删除分类
    async del({ id }) {
      const res = await this.$https.examDel({
        id
      }).catch(err => { })
      res.code == 200
        ? (this.$tools.message("删除成功！"), this.query())
        : this.$tools.message("删除失败！", "error");
    },
    // 通知
    message(item) { },
    // 用户设置
    read({ id }) {
      this.examForm.readShow = true
      this.examForm.id = id
    },
    // 用户设置
    user({ id, state }) {
      if (state == 2) {
        this.$tools.message('请先发布考试！', 'error')
        return;
      }
      this.examForm.userShow = true
      this.examForm.id = id
    },
    // 考试发布
    async publish({ id, state }) {
      if (state == 1) {
        this.$tools.message('考试已发布!', 'warning')
        return;
      }
      const res = await this.$https.examPublish({
        id
      }).catch(err => { })
      res.code == 200
        ? (this.$tools.message("考试发布成功！"), this.query())
        : this.$tools.message("请重新发布考试！", "error");
    },
    // 获取考试用户
    async getUserList(curPage = 1) {
      const examUsers = await this.$https.userListpage({
        id: this.examForm.id,
        curPage,
        pageSize: this.examForm.pageSize
      })

      this.examForm.examUsers = examUsers.data.list
      this.examForm.total = examUsers.data.total
    },
    // 获取更多用户
    async getMoreUser(curPage) {
      this.getUserList(curPage)
    },
    // 选择考试用户
    selectUser(e) {
      this.examForm.examUser = e
    },
    // 获取阅卷人员
    async getPersonList(name = '', curPage = 1) {
      const examUsers = await this.$https.userListpage({
        name,
        curPage,
        pageSize: this.examForm.pageSize
      })

      this.examForm.examCheckPersons = examUsers.data.list
      this.examForm.total = examUsers.data.total
    },
    selectPerson(e, index) {
      this.examForm.examRemarks[index].examCheckPerson = e
    },
    searchPerson(e, index) {
      console.log(e, index);
    },
    // 编辑考试用户
    async editExamUser() {
      const res = await this.$https.examUpdateExamUser({
        id: this.examForm.id,
        userIds: this.examForm.examUser,
      }).catch(err => { })

      res.code == 200
        ? (this.$tools.message("编辑成功！"), this.examForm.userShow = false, this.resetData('examForm'), this.query())
        : this.$tools.message("编辑失败！", "error");

    },
    // 切换分页
    pageChange(val) {
      this.query(val)
    },
    // 清空还原数据
    resetData(name) {
      this.$tools.resetData(this, name)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";

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
      content: "";
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
</style>
