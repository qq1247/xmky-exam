<template>
  <div class="container">
    <!-- 导航 -->
    <div class="head">
      <el-link
        class="head-left"
        :underline="false"
        @click="goBack"
        icon="el-icon-back"
        >返回</el-link
      >
      <span> 2022-01-02 小学教师资格证认证考试 </span>
      <el-link class="head-right" :underline="false"
        ><i class="common common-explain"></i
      ></el-link>
    </div>
    <!-- 内容 -->
    <div class="content">
      <div class="content-left">
        <div class="left-top">添加章节</div>
        <el-form
          :model="chapterForm"
          label-width="70px"
          class="chapter-form"
          v-model="labelPosition"
          size="mini"
        >
          <el-form-item label="题目类型">
            <el-input
              placeholder="请输入章节名称"
              v-model="chapterForm.chapterName"
            ></el-input>
          </el-form-item>
          <el-form-item label="描述">
            <el-input
              placeholder="请输入描述"
              v-model="chapterForm.chapterDescribe"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('chapterForm')"
              >添加</el-button
            >
            <el-button @click="resetForm('chapterForm')"
              >自动生成描述</el-button
            >
          </el-form-item>
        </el-form>
        <div class="left-top">添加试题</div>
        <el-form
          :model="paperForm"
          label-width="70px"
          class="paper-form"
          :inline="true"
          v-model="labelPosition"
          size="mini"
        >
          <el-form-item>
            <el-input
              placeholder="请输入题库"
              v-model="paperForm.paperStore"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              placeholder="请输入题干"
              v-model="paperForm.paperStem"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-select placeholder="请选择类型" v-model="paperForm.paperType">
              <el-option
                :key="index"
                :label="type"
                :value="type"
                v-for="(type, index) in paperForm.paperTypes"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              placeholder="请选择难度"
              v-model="paperForm.paperDifficulty"
            >
              <el-option
                :key="index"
                :label="type"
                :value="type"
                v-for="(type, index) in paperForm.paperDifficultys"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              placeholder="请输入分值"
              v-model="paperForm.paperScore"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              placeholder="请输入编号"
              v-model="paperForm.paperNo"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('chapterForm')"
              >随机查询</el-button
            >
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('chapterForm')"
              >查询</el-button
            >
          </el-form-item>
        </el-form>
        <div class="drags">
          <Draggable
            tag="div"
            class="drag-content"
            :list="paperList"
            :sort="false"
            :group="{ name: 'paper', pull: 'clone', put: false }"
            chosenClass="drag-active"
            animation="300"
          >
            <transition-group>
              <div
                class="drag-item"
                v-for="item in paperList"
                :key="item.questionTypeId"
              >
                <div class="item-title">
                  {{ item.questionTypeId }}、{{ item.title }}
                </div>
                <el-tag effect="dark" size="mini" type="warning"
                  >{{ item.score }}分</el-tag
                >
                <el-tag effect="plain" size="mini" type="danger"
                  >{{ item.score }}分</el-tag
                >
                <el-tag effect="plain" size="mini" type="warning"
                  >{{ item.score }}分</el-tag
                >
                <el-tag effect="plain" size="mini" type="info"
                  >{{ item.score }}分</el-tag
                >
              </div>
            </transition-group>
          </Draggable>
        </div>
      </div>
      <div class="content-center">
        <div class="center-top">
          <div class="paper-title">2022-01-02 小学教师资格证认证考试</div>
          <div class="paper-intro">本试卷100分，考试150分钟</div>
        </div>

        <div class="center-drag">
          <Draggable
            class="drag-content drag-parent"
            tag="div"
            :list="papers"
            group="paper"
            chosenClass="drag-active"
            animation="300"
          >
            <transition-group>
              <div
                class="drag-item"
                v-for="(item, index) in papers"
                :key="index"
              >
                <div class="item-title">{{ item.title }}</div>

                <Draggable
                  tag="div"
                  class="drag-content drag-children"
                  :list="item.children"
                  group="paper"
                  chosenClass="drag-active"
                  animation="300"
                >
                  <transition-group>
                    <div
                      class="children-content"
                      v-for="(child, index) in item.children"
                      :key="child.questionTypeId"
                      :id="`p-${item.id}${index}`"
                    >
                      <p>{{ index + 1 }}、{{ child.title }}</p>
                      <div
                        class="children-option"
                        v-for="(option, index) in child.options"
                        :key="index"
                      >
                        {{ String.fromCharCode(65 + index) }}、{{ option }}
                      </div>
                      <div class="children-analysis">
                        <div>【解析】：{{ child.answer }}</div>
                        <div>{{ child.analysis }}</div>
                      </div>
                      <div class="children-footer">
                        <div class="children-tags">
                          <el-tag effect="plain" size="mini" type="success"
                            >{{ child.score }}分</el-tag
                          >
                          <el-tag effect="plain" size="mini" type="danger"
                            >{{ child.score }}分</el-tag
                          >
                          <el-tag effect="plain" size="mini" type="warning"
                            >{{ child.score }}分</el-tag
                          >
                          <el-tag effect="plain" size="mini" type="info"
                            >{{ child.score }}分</el-tag
                          >
                        </div>
                        <div class="children-buts">
                          <el-button
                            @click="get()"
                            class="btn"
                            icon="el-icon-edit"
                            round
                            size="mini"
                            >修改</el-button
                          >
                          <el-button
                            @click="get()"
                            class="btn"
                            icon="el-icon-delete"
                            round
                            size="mini"
                            >删除</el-button
                          >
                        </div>
                      </div>
                    </div>
                  </transition-group>
                </Draggable>
              </div>
            </transition-group>
          </Draggable>
        </div>
      </div>
      <div class="content-right">
        <!-- <div class="paper-time">
          <div class="time-title">答题卡</div>
          <div class="time-residue"><span>剩余时间</span></div>
        </div> -->
        <div class="paper-route" v-for="item in papers" :key="item.id">
          <div class="route-title">{{ item.title }}</div>
          <div class="route-href">
            <div
              class="herf-item"
              v-for="(child, index) in item.children"
              :key="child.questionTypeId"
            >
              <span @click="toHerf(item.id, index)">{{ index + 1 }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Draggable from "vuedraggable"
export default {
  components: {
    Draggable
  },
  data() {
    return {
      labelPosition: "left",
      chapterForm: {
        chapterName: "",
        chapterDescribe: ""
      },
      paperForm: {
        paperStore: "",
        paperStem: "",
        paperTypes: [],
        paperType: "",
        paperDifficultys: [],
        paperDifficulty: "",
        paperScore: "",
        paperNo: ""
      },
      paperList: [
        {
          type: 1,
          difficulty: 1,
          title: "素质教育理念的核心是()",
          options: ["a", "b", "c", "d"],
          answer: "a",
          analysis:
            "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
          state: 1,
          questionTypeId: 1,
          score: 2.5,
          scoreOptions: 2,
          no: 111
        },
        {
          type: 1,
          difficulty: 1,
          title: "素质教育理念的核心是()",
          options: ["a", "b", "c", "d"],
          answer: "a",
          analysis:
            "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
          state: 1,
          questionTypeId: 2,
          score: 2.5,
          scoreOptions: 2,
          no: 111
        },
        {
          type: 1,
          difficulty: 1,
          title: "素质教育理念的核心是()",
          options: ["a", "b", "c", "d"],
          answer: "a",
          analysis:
            "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
          state: 1,
          questionTypeId: 3,
          score: 2.5,
          scoreOptions: 2,
          no: 111
        },
        {
          type: 1,
          difficulty: 1,
          title: "素质教育理念的核心是()",
          options: ["a", "b", "c", "d"],
          answer: "a",
          analysis:
            "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
          state: 1,
          questionTypeId: 4,
          score: 2.5,
          scoreOptions: 4,
          no: 111
        },
        {
          type: 1,
          difficulty: 1,
          title: "素质教育理念的核心是()",
          options: ["a", "b", "c", "d"],
          answer: "a",
          analysis:
            "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
          state: 1,
          questionTypeId: 5,
          score: 2.5,
          scoreOptions: 2,
          no: 111
        },
        {
          type: 1,
          difficulty: 1,
          title: "素质教育理念的核心是()",
          options: ["a", "b", "c", "d"],
          answer: "a",
          analysis:
            "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
          state: 1,
          questionTypeId: 6,
          score: 2.5,
          scoreOptions: 2,
          no: 111
        }
      ],
      papers: [
        {
          id: 1,
          title: "一、单选题",
          children: [
            {
              type: 1,
              difficulty: 1,
              title: "素质教育理念的核心是()",
              options: ["a", "b", "c", "d"],
              answer: "a",
              analysis:
                "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
              state: 1,
              questionTypeId: 1,
              score: 2.5,
              scoreOptions: 2,
              no: 111
            },
            {
              type: 1,
              difficulty: 1,
              title: "素质教育理念的核心是()",
              options: ["a", "b", "c", "d"],
              answer: "a",
              analysis:
                "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
              state: 1,
              questionTypeId: 2,
              score: 2.5,
              scoreOptions: 2,
              no: 111
            }
          ]
        },
        {
          id: 2,
          title: "二、多选题",
          children: [
            {
              type: 1,
              difficulty: 1,
              title: "2.素质教育理念的核心是()",
              options: ["a", "b", "c", "d"],
              answer: "a",
              analysis:
                "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
              state: 1,
              questionTypeId: 3,
              score: 2.5,
              scoreOptions: 2,
              no: 111
            },
            {
              type: 1,
              difficulty: 1,
              title: "2.素质教育理念的核心是()",
              options: ["a", "b", "c", "d"],
              answer: "a",
              analysis:
                "三长一短选最短，三短一长选最长，长短不一选就选B，参差不齐就选D！",
              state: 1,
              questionTypeId: 4,
              score: 2.5,
              scoreOptions: 2,
              no: 111
            }
          ]
        }
      ]
    }
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    toHerf(id, index) {
      document.querySelector(`#p-${id}${index}`).scrollIntoView({
        behavior: "smooth"
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 0;
}

.head {
  width: 100%;
  height: 50px;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  .head-left {
    color: #fff;
  }
  .head-right {
    color: #fff;
    .common {
      font-size: 20px;
    }
  }
}

.content {
  display: flex;
  width: 100%;
  padding: 20px 20px 10px;
  margin: 0 auto;
}

.drag-content {
  width: 100%;
  border: 1px solid #d8d8d8;
}

.drag-active {
  box-shadow: 0 0 10px 2px rgba(30, 159, 255, 0.15);
}

.content-left {
  width: 300px;
  height: 100%;
  background: #fff;
  .left-top {
    width: 100%;
    height: 45px;
    line-height: 45px;
    text-align: left;
    padding-left: 10px;
    font-size: 14px;
    color: #333;
    position: relative;
    border-bottom: 1px solid #d8d8d8;
    &::before {
      content: "";
      display: block;
      position: absolute;
      top: 15px;
      left: 0;
      width: 2px;
      height: 15px;
      background: rgb(30, 159, 255);
    }
  }
  .chapter-form {
    padding: 15px 10px 0 10px;
    border-bottom: 10px solid #f2f4f5;
    .el-form-item {
      margin-bottom: 15px;
    }
  }
  .paper-form {
    padding: 15px 0 0 10px;
    .el-form-item,
    .el-button {
      width: 135px;
    }
    .el-form-item {
      margin-bottom: 15px;
    }
  }
  .drags {
    padding: 10px;
    .drag-item {
      border-bottom: 1px solid #e8e8e8;
      padding: 15px;
      font-size: 14px;
      cursor: move;
    }
    .item-title {
      margin-bottom: 10px;
    }
    .el-tag {
      margin-left: 10px;
    }
  }
}

.content-center {
  flex: 1;
  background: #fff;
  margin: 0 10px;
  border-radius: 5px;
  .center-drag {
    width: 100%;
    padding: 10px;
  }
  .drag-parent {
    padding: 10px;
  }
  .drag-children {
    border: none;
  }
  .paper-title {
    font-size: 16px;
    color: #333;
    padding: 20px 0 10px 10px;
  }
  .paper-intro {
    font-size: 12px;
    color: #666;
    padding: 0 10px 15px;
    border-bottom: 1px solid #d8d8d8;
  }
  .item-title {
    line-height: 40px;
  }
  .children-content {
    border: 1px solid #d8d8d8;
    margin-bottom: 15px;
    font-size: 14px;
    p {
      margin: 0;
      line-height: 40px;
      padding-left: 10px;
      background: rgba(30, 159, 255, 0.45);
    }
  }
  .children-option,
  .children-analysis {
    padding-left: 20px;
    line-height: 30px;
  }
  .children-analysis {
    line-height: 20px;
  }
  .children-analysis {
    margin: 15px 0;
    font-size: 13px;
    color: #666;
    span {
      font-size: 15px;
      font-weight: bold;
    }
  }
  .el-tag {
    margin-right: 6px;
  }
  .children-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
  }
  .btn {
    padding: 5px 10px;
  }
}

.content-right {
  width: 240px;
  height: 100%;
  background: #fff;
  .time-title {
    background-color: rgb(30, 159, 255);
    width: 100%;
    height: 40px;
    line-height: 40px;
    text-align: center;
    padding-left: 10px;
    font-size: 14px;
    color: #fff;
  }
  .time-residue {
    font-size: 12px;
    font-weight: bold;
    line-height: 50px;
    text-align: center;
  }
  .route-title {
    background: #e8e8e8;
    color: #333;
    line-height: 14px;
    line-height: 40px;
    padding: 0 10px;
  }
  .route-href {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    width: 100%;
    padding: 10px 15px 0;
    .herf-item {
      width: 20%;
      height: 50px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      span {
        display: inline-block;
        width: 30px;
        height: 30px;
        line-height: 30px;
        text-align: center;
        font-size: 14px;
        border: 1px solid #d8d8d8;
      }
      .active {
        border: 1px solid rgb(30, 159, 255);
        color: rgb(30, 159, 255);
      }
    }
  }
}

.pagination {
  font-weight: 400;
  text-align: center;
}

.el-radio,
.el-checkbox {
  margin-right: 10px;
}

.box-card-no-border {
  border: none;
}

/deep/ .el-card__body {
  padding: 5px;
}

.el-input /deep/.el-input-group__prepend {
  background-color: #fff;
  border: 0px;
  width: 38px;
}
/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}
</style>
