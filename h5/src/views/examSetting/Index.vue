<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.examName"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            placeholder="请输入发布人"
            v-model="queryForm.examPerson"
          ></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query" icon="el-icon-search" type="primary"
          >查询</el-button
        >
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
        <div v-for="(item, index) in examList" :key="index" class="exam-item">
          <div class="exam-content">
            <div class="title">{{ item.name }}</div>
            <div class="content-info ellipsis">
              <span>发布人：张三</span>
            </div>
            <div class="content-info">
              <span class="space">成绩公开：是</span>
              <span>排名公开：是</span>
            </div>
            <div class="content-info">
              <span class="space">考试人数：50</span>
              <span>阅卷人数：5</span>
            </div>
            <div class="content-info">
              <span style="color: #ff9900">已发布</span>
            </div>
            <div class="content-info"></div>
            <div class="handler">
              <span data-title="编辑" @click="examEdit">
                <i class="common common-edit"></i>
              </span>
              <span data-title="删除" @click="examDel">
                <i class="common common-delete"></i>
              </span>
              <span data-title="权限" @click="examRole">
                <i class="common common-role"></i>
              </span>
              <span data-title="开放" @click="examOpen">
                <i class="common common-share"></i>
              </span>
              <span data-title="试题列表" @click="goDetail">
                <i class="common common-list-row"></i>
              </span>
            </div>
          </div>
        </div>
      </div>
      <el-pagination background layout="prev, pager, next" :total="1000">
      </el-pagination>
    </div>
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      center
      width="40%"
    >
      <el-form
        :model="examForm"
        :rules="examForm.rules"
        ref="examForm"
        label-width="100px"
      >
        <el-tabs v-model="tabActive">
          <el-tab-pane
            v-for="item in examTabs"
            :key="item.name"
            :label="item.title"
            :name="item.name"
          >
          </el-tab-pane>
          <div v-if="tabActive == '0'">
            <el-form-item label="试卷名称" prop="examName">
              <el-input
                placeholder="请输入试卷名称"
                v-model="examForm.examName"
              ></el-input>
            </el-form-item>
            <el-form-item label="选择试卷">
              <el-select placeholder="请选择试卷" v-model="examForm.examType">
                <el-option
                  :key="parseInt(type.DICT_KEY)"
                  :label="type.DICT_VALUE"
                  :value="parseInt(type.DICT_KEY)"
                  v-for="type in examForm.examTypes"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="成绩公开">
              <el-checkbox v-model="examForm.examScore">是</el-checkbox>
            </el-form-item>
            <el-form-item label="排名公开">
              <el-checkbox v-model="examForm.examRanking">是</el-checkbox>
            </el-form-item>
            <el-form-item label="考试方式">
              <el-checkbox v-model="examForm.examLogin">免登录</el-checkbox>
            </el-form-item>
          </div>
          <div v-if="tabActive == '1'">
            <el-form-item label="阅卷方式">
              <el-radio
                v-for="item in examForm.examRadios"
                :key="item.value"
                v-model="examForm.examRadio"
                :label="item.value"
                >{{ item.name }}</el-radio
              >
            </el-form-item>
            <div>
              <div
                v-for="(item, index) in examForm.examRemarks"
                :key="item.id"
                class="exam-remark"
              >
                <el-form-item label="阅卷人">
                  <el-select
                    placeholder="请选择"
                    v-model="examForm.examRemarks[index].examCheckPerson"
                  >
                    <el-option
                      :key="parseInt(type.DICT_KEY)"
                      :label="type.DICT_VALUE"
                      :value="parseInt(type.DICT_KEY)"
                      v-for="type in examForm.examCheckPersons"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="题号" v-if="examForm.examRadio == 0">
                  <el-select
                    placeholder="请选择"
                    v-model="examForm.examRemarks[index].examPaperNum"
                  >
                    <el-option
                      :key="parseInt(type.DICT_KEY)"
                      :label="type.DICT_VALUE"
                      :value="parseInt(type.DICT_KEY)"
                      v-for="type in examForm.examPaperNums"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="试卷" v-if="examForm.examRadio == 1">
                  <el-select
                    multiple
                    placeholder="请选择"
                    v-model="examForm.examRemarks[index].examPaper"
                  >
                    <el-option
                      :key="parseInt(type.DICT_KEY)"
                      :label="type.DICT_VALUE"
                      :value="parseInt(type.DICT_KEY)"
                      v-for="type in examForm.examPapers"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div class="remark-buttons">
                <el-form-item>
                  <el-button
                    @click="remarkAdd"
                    type="primary"
                    size="mini"
                    icon="el-icon-plus"
                    >添加评语</el-button
                  >
                  <el-button
                    v-if="examForm.examRemarks.length > 1"
                    @click="remarkDel"
                    size="mini"
                    icon="el-icon-minus"
                    >添加评语</el-button
                  >
                </el-form-item>
              </div>
            </div>
          </div>
          <div v-if="tabActive == '2'">
            <el-form-item label="考试用户">
              <el-select
                multiple
                placeholder="请选择用户"
                v-model="examForm.examUser"
              >
                <el-option
                  :key="parseInt(type.DICT_KEY)"
                  :label="type.DICT_VALUE"
                  :value="parseInt(type.DICT_KEY)"
                  v-for="type in examForm.examUsers"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-tabs>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button v-if="!(tabActive == '2')" @click="examNext" type="primary"
          >下一步</el-button
        >
        <el-button @click="examAdd">添加</el-button>
        <el-button @click="examForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Editor from "@/components/Editor.vue"
export default {
  components: {
    Editor
  },
  data() {
    return {
      queryForm: {
        examName: ""
      },
      examForm: {
        show: false,
        examName: "",
        examPerson: "",
        examType: "",
        examTypes: [],
        examScore: false,
        examRanking: false,
        examLogin: false,
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
            examCheckPerson: "",
            examPaperNum: "",
            examPaper: []
          }
        ],
        examUser: [],
        examUsers: [],
        rules: {
          examName: [
            { required: true, message: "请填写试卷名称", trigger: "blur" }
          ]
        }
      },
      tabActive: "0",
      examTabs: [
        {
          title: "基础信息",
          name: "0"
        },
        {
          title: "阅卷用户",
          name: "1"
        },
        {
          title: "考试用户",
          name: "2"
        }
      ],
      examList: [
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        }
      ]
    }
  },
  methods: {
    // 查询
    query() {},
    // tab切换
    examNext() {
      this.tabActive = Number(this.tabActive) + 1 + ""
    },
    examEdit() {
      this.examForm.edit = true
      this.examForm.show = true
    },
    examDel() {},
    examRole() {},
    examOpen() {},
    // 试题详情
    goDetail() {
      this.$router.push("/examSetting/classify")
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
    // 添加试卷信息
    examAdd() {
      this.$refs["examForm"].validate(valid => {
        if (!valid) {
          console.log("error")
          return
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../assets/style/index.scss";
</style>
