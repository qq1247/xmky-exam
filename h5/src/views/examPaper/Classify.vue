<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <el-form-item>
        <el-input
          placeholder="请输入名称"
          v-model="queryForm.examName"
        ></el-input>
      </el-form-item>
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
            <i class="common common-exam-add"></i>
            <span>添加试卷</span>
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
              <span data-title="编辑" @click="paperEdit">
                <i class="common common-edit"></i>
              </span>
              <span data-title="详情" @click="paperDetail">
                <i class="common common-dingdan"></i>
              </span>
              <span data-title="删除" @click="paperDelete">
                <i class="common common-delete"></i>
              </span>
              <span data-title="权限" @click="paperRole">
                <i class="common common-role"></i>
              </span>
              <span data-title="更多" @click="paperMore">
                <i class="common common-more-row"></i>
              </span>
            </div>
          </div>
        </div>
      </div>
      <el-pagination
        background
        layout="prev, pager, next"
        :total="1000"
        class="exam-pagination"
      >
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
            <el-form-item>
              <div class="exam-type">
                <div
                  :class="
                    typeIndex == index
                      ? 'type-item type-item-active'
                      : 'type-item '
                  "
                  v-for="(item, index) in examType"
                  :key="item.content"
                  @click="setExamType(index)"
                >
                  <i :class="['common', `${item.icon}`]"></i>
                  <i
                    class="common common-selected"
                    v-if="typeIndex == index"
                  ></i>
                  {{ item.content }}
                </div>
              </div>
            </el-form-item>
            <el-form-item label="试卷名称" prop="examName">
              <el-input
                placeholder="请输入试卷名称"
                v-model="examForm.examName"
              ></el-input>
            </el-form-item>
            <el-form-item label="及格分数(%)" prop="examPercentage">
              <el-input
                type="number"
                min="1"
                max="100"
                placeholder="请输入及格分数占总分百分比"
                v-model="examForm.examPercentage"
              ></el-input>
            </el-form-item>
            <el-form-item label="考前阅读"
              ><Editor
                :value="examForm.examTip"
                @editorListener="examTip"
                id="examTip"
              ></Editor>
            </el-form-item>
            <el-form-item label="阅读时长">
              <el-input
                placeholder="请输入阅读时长"
                v-model="examForm.examTipTime"
              ></el-input>
            </el-form-item>
          </div>
          <div v-if="tabActive == '1'">
            <el-form-item label="展示方式">
              <el-radio
                v-for="item in examForm.examRadios"
                :key="item.value"
                v-model="examForm.examRadio"
                :label="item.value"
                >{{ item.name }}</el-radio
              >
            </el-form-item>
          </div>
          <div v-if="tabActive == '2'">
            <el-form-item label="防作弊">
              <el-checkbox-group v-model="examForm.examChecked">
                <el-checkbox
                  v-for="item in examForm.examAntiCheat"
                  :label="item"
                  :key="item"
                  >{{ item }}</el-checkbox
                >
              </el-checkbox-group>
              <el-input
                v-model="examForm.examMiniNum"
                placeholder="最小化次数"
                v-if="examForm.examChecked.includes('最小化')"
              >
                <span slot="append">后交卷</span></el-input
              >
            </el-form-item>
          </div>
          <div v-if="tabActive == '3'">
            <el-form-item label="成绩评语">
              <el-checkbox v-model="examForm.examRemark">备选项</el-checkbox>
              <div v-if="examForm.examRemark">
                <div
                  v-for="item in examForm.examRemarks"
                  :key="item.id"
                  class="exam-remark"
                >
                  大于等于<el-input
                    v-model="item.percentage"
                    placeholder="占总分百分比"
                    class="remark-percentage"
                  ></el-input
                  >%，<el-input
                    v-model="item.content"
                    class="remark-content"
                    placeholder="请输入评语"
                  ></el-input>
                </div>
                <div class="remark-buttons">
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
                </div>
              </div>
            </el-form-item>
          </div>
        </el-tabs>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button v-if="!(tabActive == '3')" @click="examNext" type="primary"
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
        examPercentage: "",
        examTip: "",
        examTipTime: "",
        examRadio: "0",
        examRadios: [
          {
            name: "整张",
            value: "0"
          },
          {
            name: "章节",
            value: "1"
          },
          {
            name: "单题",
            value: "2"
          }
        ],
        examAntiCheat: [
          "试题乱序",
          "选项乱序",
          "禁用右键",
          "禁用复制",
          "最小化"
        ],
        examChecked: [],
        examMiniNum: "",
        examRemark: false,
        examRemarks: [
          {
            id: 1,
            percentage: "",
            content: ""
          }
        ],
        rules: {
          examName: [
            { required: true, message: "请填写试卷名称", trigger: "blur" }
          ],
          examPercentage: [
            {
              required: true,
              message: "请填写及格分数占总分的百分比",
              trigger: "blur"
            }
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
          title: "组卷方式",
          name: "1"
        },
        {
          title: "防作弊",
          name: "2"
        },
        {
          title: "成绩评语",
          name: "3"
        }
      ],
      typeIndex: 0,
      examType: [
        {
          icon: "common-person",
          content: "人工组卷"
        },
        {
          icon: "common-exchange",
          content: "随机组卷"
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
    paperEdit() {
      this.$router.push("/examPaper/edit")
    },
    paperDetail() {
      this.$router.push("/examPaper/add")
    },
    paperDelete() {
      this.$router.push("/examPaper/edit")
    },
    paperRole() {
      this.$router.push("/examPaper/edit")
    },
    paperMore() {
      this.$router.push("/examPaper/edit")
    },
    // tab切换
    examNext() {
      this.tabActive = Number(this.tabActive) + 1 + ""
    },
    // 考试阅读富文本
    examTip(id, value) {
      this.examForm[id] = value
    },
    // 组卷方式
    setExamType(index) {
      this.typeIndex = index
    },
    // 添加评语
    remarkAdd() {
      this.examForm.examRemarks.push({
        id: this.examForm.examRemarks.length + 1,
        percentage: "",
        content: ""
      })
    },
    // 删除评语
    remarkDel() {
      this.examForm.examRemarks.pop()
    },
    // 添加试卷信息
    examAdd() {
      this.$refs["examForm"].validate(valid => {
        /* if (!valid) {
          console.log("error")
          return
        } */
        this.$router.push("/examPaper/add")
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.search {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 10px 0;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin: 0 auto;
}

.exam-list {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
}

.exam-item {
  width: calc(100% / 3);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.exam-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 95%;
  padding: 20px 0;
  background: #fff;
  height: 200px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 16px -10px rgba(95, 101, 105, 0.15);
  }
  .title {
    font-size: 16px;
    color: #000;
    font-weight: bold;
    margin-top: 20px;
  }
  .no-date {
    font-size: 14px;
    color: #9199a1;
    margin-top: 10px;
    span:first-child {
      margin-right: 30px;
    }
  }
  .handler {
    span {
      display: inline-block;
      width: 40px;
      height: 40px;
      line-height: 40px;
      border-radius: 50%;
      border: 1px solid #9199a1;
      text-align: center;
      margin: 20px 10px 0 0;
      position: relative;
      transition: all 0.3s ease-in-out;
      &:not(:last-child)::after {
        content: attr(data-title);
        display: block;
        position: absolute;
        z-index: 100;
        bottom: -30px;
        transform: translateX(-50%);
        left: 50%;
        width: 70px;
        height: 30px;
        line-height: 30px;
        background: #1e9fff;
        color: #fff;
        border-radius: 5px;
        font-size: 13px;
        opacity: 0;
        transition: all 0.3s ease-in-out;
      }
      &:not(:last-child)::before {
        content: "";
        display: block;
        position: absolute;
        z-index: 100;
        bottom: 0;
        transform: translateX(-50%);
        left: 50%;
        border-width: 5px;
        border-style: solid;
        border-color: transparent transparent #1e9fff transparent;
        opacity: 0;
        transition: all 0.3s ease-in-out;
      }
      &:hover {
        border: 1px solid #1e9fff;
        background: #1e9fff;
        color: #fff;
        &:not(:last-child)::after {
          bottom: -37px;
          opacity: 1;
        }
        &:not(:last-child)::before {
          bottom: -8px;
          opacity: 1;
        }
      }
    }
  }
}

.exam-add {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #9199a1;
  .common-exam-add {
    display: inline-block;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
    border-radius: 50%;
    border: 1px solid #9199a1;
    font-size: 45px;
    color: #9199a1;
    margin-bottom: 10px;
    transition: all 0.3s ease-in-out;
  }
  &:hover {
    color: #1e9fff;
    .common-exam-add {
      border: 1px solid #1e9fff;
      background: #1e9fff;
      color: #fff;
    }
  }
}

.exam-pagination {
  margin: 50px auto;
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
  border: 1px solid #9199a1;
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
