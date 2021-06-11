<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.examName"
            class="query-input"
          ></el-input>
        </el-form-item>
      </div>
      <el-form-item>
        <el-button @click="query()" icon="el-icon-search" type="primary"
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
            <div class="no-date">
              <span>{{ item.no }}</span>
              <span>{{ item.date }}</span>
            </div>
            <div class="handler">
              <span data-title="编辑">
                <i class="common common-edit"></i>
              </span>
              <span data-title="详情">
                <i class="common common-dingdan"></i>
              </span>
              <span data-title="删除">
                <i class="common common-delete"></i>
              </span>
              <span data-title="权限">
                <i class="common common-role"></i>
              </span>
              <span data-title="更多">
                <i class="common common-more-row"></i>
                <div class="handler-more">
                  <div>更多</div>
                  <div>更多</div>
                  <div>更多</div>
                </div>
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
export default {
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
      examList: []
    }
  },
  methods: {
    // 查询
    query() {},
    // tab切换
    examNext() {
      this.tabActive = Number(this.tabActive) + 1 + ""
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
