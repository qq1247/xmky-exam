<template>
  <div class="container">
    <!-- 导航 -->
    <div class="head">
      <el-link class="head-left" :underline="false" @click="goBack" icon="el-icon-back">返回</el-link>
      <span>{{ paperName }}</span>
      <el-link class="head-right" :underline="false">
        <i class="common common-explain"></i>
      </el-link>
    </div>
    <!-- 内容 -->
    <div class="content">
      <div class="content-left">
        <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
          <div class="left-top">添加章节</div>
          <el-form
            :model="chapterForm"
            :rules="chapterForm.rules"
            ref="chapterForm"
            label-width="90px"
            class="chapter-form"
            size="mini"
          >
            <el-form-item label="题目类型" prop="name">
              <el-input placeholder="请输入章节名称" v-model="chapterForm.name"></el-input>
            </el-form-item>
            <el-form-item label="描述">
              <el-input placeholder="请输入描述" v-model="chapterForm.description"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="paperChapterAdd">添加</el-button>
              <el-button type="primary" v-if="chapterForm.edit" @click="paperChapterEdit">修改</el-button>
            </el-form-item>
          </el-form>
          <div class="left-top">添加试题</div>
          <el-form
            :model="queryForm"
            label-width="70px"
            class="paper-form"
            :inline="true"
            v-model="labelPosition"
            size="mini"
          >
            <el-form-item>
              <el-input placeholder="请输入试题名称" v-model="queryForm.questionTypeName"></el-input>
            </el-form-item>
            <el-form-item>
              <el-input placeholder="请输入题干" v-model="queryForm.title"></el-input>
            </el-form-item>
            <el-form-item>
              <el-select placeholder="请选择类型" v-model="queryForm.type">
                <el-option
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                  v-for="dict in queryForm.typeDictList"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select placeholder="请选择难度" v-model="queryForm.difficulty">
                <el-option
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                  v-for="dict in queryForm.difficultyDictList"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input placeholder="请输入分值" v-model="queryForm.score"></el-input>
            </el-form-item>
            <el-form-item>
              <el-input placeholder="请输入编号" v-model="queryForm.id"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="randomQueryQuestion()">随机查询</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="queryQuestion()">查询</el-button>
            </el-form-item>
          </el-form>
          <div class="drags">
            <Draggable
              tag="div"
              class="drag-content"
              :list="paperList"
              :sort="false"
              :group="{ name: 'paper', put: false }"
              chosenClass="drag-active"
              animation="300"
              @end="sourceEnd"
              @choose="sourceChoose"
              v-if="paperList.length > 0"
            >
              <transition-group>
                <div
                  class="drag-item"
                  v-for="(item,index) in paperList"
                  :key="item.id"
                  :id="item.id"
                >
                  <div class="item-title">{{ index + 1 }}、{{ item.title }}</div>
                  <el-tag effect="dark" size="mini" type="warning">
                    {{
                      item.typeName
                    }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="danger">
                    {{
                      item.difficultyName
                    }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="warning">{{ item.score }}分</el-tag>
                  <el-tag effect="plain" size="mini" type="info">
                    {{
                      item.updateUserName
                    }}
                  </el-tag>
                </div>
              </transition-group>
            </Draggable>
            <div v-if="paperList.length == 0" class="data-null">
              <img class="data-img" src="../../assets/img/data-null.png" alt />
              <span class="data-tip">暂无此类型题目</span>
            </div>
          </div>
        </el-scrollbar>
      </div>

      <div class="content-center">
        <div class="center-top">
          <div class="paper-title">{{ paperName }}</div>
          <!-- <div class="paper-intro">{{}}</div> -->
        </div>

        <div class="center-drag">
          <Draggable
            tag="div"
            :list="paperQuestion"
            group="paperParent"
            chosenClass="drag-question-active"
            animation="300"
            v-if="paperQuestion.length > 0"
            @update="chapterUpdate"
          >
            <div
              class="drag-item drag-content drag-parent"
              v-for="(item, index) in paperQuestion"
              :key="index"
            >
              <div class="chapter">
                <div class="chapter-item">
                  <div class="item-title">{{ item.chapter.name }}</div>
                  <div>
                    <el-button
                      @click="chapterEdit(item.chapter)"
                      class="btn"
                      icon="common common-edit"
                      round
                      size="mini"
                    >编辑</el-button>
                    <el-button
                      @click="chapterDel(item.chapter)"
                      class="btn"
                      icon="common common-delete"
                      round
                      size="mini"
                    >删除章节</el-button>
                    <el-button
                      @click="chapterClear(item.chapter)"
                      class="btn"
                      icon="common common-clear"
                      round
                      size="mini"
                    >清空试题</el-button>
                    <el-button
                      @click="chapteFold(index)"
                      class="btn"
                      icon="common common-fold"
                      round
                      size="mini"
                    >折叠</el-button>
                  </div>
                </div>
                <div class="chapter-description">{{ item.chapter.description }}</div>
              </div>

              <Draggable
                tag="div"
                v-if="item.chapter.show"
                :class="[
                  'drag-content',
                  item.questionList.length != 0 ? 'drag-children' : '',
                ]"
                :list="item.questionList"
                group="paper"
                chosenClass="drag-question-active"
                animation="300"
                :data-id="item.chapter.id"
              >
                <template v-if="item.questionList.length > 0">
                  <div
                    class="children-content"
                    v-for="(child, index) in item.questionList"
                    :key="child.id"
                    :id="`p-${item.chapter.id}-${index}`"
                  >
                    <p>{{ index + 1 }}、{{ child.title }}</p>

                    <!-- 单选 -->
                    <template v-if="child.type === 1">
                      <el-radio-group class="children-option" v-model="child.answer">
                        <el-radio
                          class="option-item"
                          disabled
                          :key="index"
                          :label="String.fromCharCode(65 + index)"
                          v-for="(option, index) in child.options"
                        >{{ String.fromCharCode(65 + index) }}、{{ option.options }}</el-radio>
                      </el-radio-group>
                    </template>

                    <!-- 多选 -->
                    <template v-if="child.type === 2">
                      <el-checkbox-group class="children-option" v-model="child.answer">
                        <el-checkbox
                          class="option-item"
                          disabled
                          :key="index"
                          :label="String.fromCharCode(65 + index)"
                          v-for="(option, index) in child.options"
                        >{{ String.fromCharCode(65 + index) }}、{{ option.options }}</el-checkbox>
                      </el-checkbox-group>
                    </template>

                    <!-- 填空 -->
                    <template v-if="child.type === 3">
                      <div
                        class="option-item-text"
                        :key="index"
                        v-for="(option, index) in child.answer.split(' ')"
                      >{{ option }}</div>
                    </template>

                    <!-- 判断 -->
                    <template v-if="child.type === 4">
                      <el-radio-group class="children-option" v-model="child.answer">
                        <el-radio
                          class="option-item"
                          disabled
                          :key="index"
                          :label="option"
                          v-for="(option, index) in ['对','错']"
                        >{{ option }}</el-radio>
                      </el-radio-group>
                    </template>

                    <!-- 问答 -->
                    <template v-if="child.type === 5">
                      <div class="option-item-text">{{ child.answer }}</div>
                    </template>

                    <div class="children-analysis">
                      <div>【答案】：{{ child.answer }}</div>
                      <div>【解析】：{{ child.analysis }}</div>
                    </div>
                    <div class="children-footer">
                      <div class="children-tags">
                        <el-tag effect="dark" size="mini" type="warning">
                          {{
                            child.typeName
                          }}
                        </el-tag>
                        <el-tag effect="plain" size="mini" type="danger">
                          {{
                            child.difficultyName
                          }}
                        </el-tag>
                        <el-tag effect="plain" size="mini" type="warning">{{ child.score }}分</el-tag>
                        <el-tag effect="plain" size="mini" type="info">
                          {{
                            child.updateUserName
                          }}
                        </el-tag>
                      </div>
                      <div class="children-buts">
                        <el-button
                          @click="setting()"
                          class="btn"
                          icon="el-icon-setting"
                          round
                          size="mini"
                        >设置</el-button>
                        <el-button
                          @click="del(child.paperquestionId)"
                          class="btn"
                          icon="el-icon-delete"
                          round
                          size="mini"
                        >删除</el-button>
                      </div>
                    </div>
                  </div>
                </template>
                <div v-if="item.questionList.length == 0" class="data-null">
                  <i class="common common-drag data-img"></i>
                  <span class="data-tip">拖拽题目到此处</span>
                </div>
              </Draggable>
            </div>
          </Draggable>
          <div v-if="paperQuestion.length == 0" class="data-null">
            <img class="data-img" src="../../assets/img/data-null.png" alt />
            <span class="data-tip">暂无试卷信息</span>
          </div>
        </div>
      </div>

      <div class="content-right">
        <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
          <el-collapse v-model="collapseShow" v-if="paperQuestion.length > 0">
            <el-collapse-item
              v-for="(item,index) in paperQuestion"
              :key="item.id"
              :title="item.chapter.name"
              :name="index"
            >
              <div class="route-href">
                <div
                  :class="['href-item', hrefPointer === `#p-${item.chapter.id}-${index}` ? 'href-item-active' : '']"
                  v-for="(child, index) in item.questionList"
                  :key="child.id"
                >
                  <span @click="toHref(item.chapter.id, index)">{{ index + 1 }}</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
          <div v-if="paperQuestion.length == 0" class="data-null">
            <img class="data-img" src="../../assets/img/data-null.png" alt />
            <span class="data-tip">暂无题目导航信息</span>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>
<script>
import Draggable from 'vuedraggable';
export default {
  components: {
    Draggable
  },
  data() {
    return {
      labelPosition: 'left',
      hrefPointer: '',
      paperId: 0,
      paperName: '',
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      collapseShow: 0,
      chapterForm: {
        id: 0,
        name: '',
        description: '',
        edit: false,
        rules: {
          name: [
            { required: true, message: '请输入章节名称', trigger: 'blur' }
          ]
        }
      },
      queryForm: {
        id: '', // 编号
        title: '', // 题干
        type: null, // 类型
        difficulty: null, // 难度
        score: '', // 分数
        questionTypeName: '', // 试题分类name
        questionTypeId: 1, // 试题分类id
        difficultyDictList: [], // 难度列表
        typeDictList: [] // 类型列表
      },
      paperList: [],
      paperQuestion: []
    }
  },
  created() {
    const { id, name } = this.$route.query
    this.paperId = id
    this.paperName = name
    this.init()
  },
  methods: {
    // 返回
    goBack() {
      this.$router.push('/examPaper/list');
    },
    // 初始化
    async init() {
      await this.query()
      await this.queryQuestion()
      // 初始化类型下拉框
      const typeDictData = await this.$https.dictListpage({
        dictIndex: 'QUESTION_TYPE',
      })
      this.queryForm.typeDictList = typeDictData.data.list
      // 初始化难度下拉框
      const difficultyDictData = await this.$https.dictListpage({
        dictIndex: 'QUESTION_DIFFICULTY',
      })
      this.queryForm.difficultyDictList = difficultyDictData.data.list
    },
    // 查询试卷信息
    async query() {
      try {
        const res = await this.$https.paperQuestionList({
          id: this.paperId
        })
        res.data.map((item) => {
          item.chapter.show = true
          item.questionList.map((question) => {
            if (question.type === 2) {
              question.answer = question.answer.split(',')
            }
          })
        })
        this.paperQuestion = [...res.data]
      } catch (error) { }
    },
    // 查询试题
    async queryQuestion(curPage) {
      const res = await this.$https.questionListPage({
        id: this.queryForm.id,
        type: this.queryForm.type,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        difficulty: this.queryForm.difficulty,
        scoreStart: this.queryForm.score,
        scoreEnd: this.queryForm.score,
        exPaperId: this.paperId,
        curPage: curPage || this.curPage,
        pageSize: this.pageSize
      }).catch(err => { })
      res.code === 200
        ? (this.paperList = res.data.list, this.pageTotal = res.data.total)
        : this.$tools.message('请刷新重新获取试题', 'error')
    },
    // 随机查询试题
    async randomQueryQuestion() {
      let pageNum = this.pageTotal <= this.pageSize
        ? 1
        : Math.ceil(this.pageTotal / this.pageSize)
      let randomCurPage = Math.floor(Math.random() * (pageNum + 1))
      this.queryQuestion(randomCurPage)
    },
    // 添加章节
    paperChapterAdd() {
      this.$refs['chapterForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        const res = await this.$https.paperChapterAdd({
          name: this.chapterForm.name,
          description: this.chapterForm.description,
          paperId: this.paperId,
          type: 1
        }).catch(err => { })
        this.refreshData(res, '添加章节')
      })
    },
    // 回显章节信息
    chapterEdit({ id, name, description }) {
      this.chapterForm.id = id
      this.chapterForm.name = name
      this.chapterForm.description = description
      this.chapterForm.edit = true
    },
    // 编辑章节
    async paperChapterEdit() {
      const res = await this.$https.paperChapterEdit({
        id: this.chapterForm.id,
        name: this.chapterForm.name,
        description: this.chapterForm.description
      }).catch(err => { })
      res.code === 200
        ? (this.$tools.message('编辑章节成功！'), this.chapterForm.edit = false, this.query())
        : this.$tools.message('编辑章节失败！', 'error')
    },
    // 删除章节
    async chapterDel({ id }) {
      const res = await this.$https.paperChapterDel({
        id
      }).catch(err => { })
      this.refreshData(res, '删除章节')
    },
    // 章节折叠
    chapteFold(index) {
      this.paperQuestion[index].chapter.show = !this.paperQuestion[index].chapter.show
    },
    // 清空试卷试题
    async chapterClear({ id }) {
      const res = await this.$https.paperQuestionClear({
        chapterId: id
      }).catch(err => { })
      this.refreshData(res, '清空试题')
    },
    // 删除试题
    async del(paperQuestionId) {
      const res = await this.$https.paperQuestionDel({
        paperQuestionId
      }).catch(err => { })
      this.refreshData(res, '删除试题')
    },
    // 更新页面数据
    refreshData(res, title) {
      res.code === 200
        ? (this.$tools.message(`${title}成功！`), this.query(), this.queryQuestion())
        : this.$tools.message(`${title}失败！`, 'error')
    },
    // 选择拖拽原题
    sourceChoose(e) {
      if (this.paperQuestion.length == 0) {
        this.$tools.message('请添加章节', 'error')
        return;
      }
    },
    // 拖拽原题结束
    async sourceEnd(e) {
      const chapterId = e.to.dataset.id
      const questionIds = e.item.id
      const res = await this.$https.paperQuestionAdd({
        chapterId,
        questionIds
      }).catch(err => { })
      if (res.code !== 200) return false;
      this.query()
      this.queryQuestion()
    },
    // 章节移动
    chapterUpdate(e) {
      console.log(e);
    },
    // 定位锚点
    toHref(id, index) {
      this.hrefPointer = `#p-${id}-${index}`
      document.querySelector(`#p-${id}-${index}`).scrollIntoView({
        behavior: 'smooth'
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
  padding-bottom: 0;
}

.head {
  width: 100%;
  height: 50px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
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
  width: 100%;
  padding: 0 20px 10px;
  margin: 70px auto 0;
}

.drag-content {
  width: 100%;
  border: 1px solid #d8d8d8;
}

.drag-active {
  transform: scale(0.98);
  border: 1px solid #0094e5;
  border-radius: 5px;
  border-bottom: 1px solid #0094e5 !important;
  transition: all 0.15s ease-in-out;
}

.drag-question-active {
  border: 1px solid #0094e5 !important;
  box-shadow: 0 0 16px 2px rgba(0, 148, 229, 0.15);
  transition: all 0.15s ease-in-out;
}

.content-left {
  width: 300px;
  background: #fff;
  position: fixed;
  top: 70px;
  left: 20px;
  bottom: 10px;
  z-index: 100;
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
  background: #fff;
  margin-left: 310px;
  width: calc(100% - 560px);
  .center-drag {
    width: 100%;
    padding: 10px;
    .drag-item {
      cursor: move;
      margin-bottom: 10px;
    }
  }
  .chapter {
    display: flex;
    flex-direction: column;
    padding: 0 10px;
    .chapter-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .item-title {
        font-size: 16px;
      }
      /deep/.el-button {
        opacity: 0;
        .common {
          font-size: 12px;
          margin-right: 5px;
        }
      }
      &:hover {
        .el-button {
          opacity: 1;
        }
      }
    }
    .chapter-description {
      font-size: 14px;
      color: #999;
      padding-bottom: 10px;
      margin-top: -5px;
    }
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
    font-size: 14px;
    box-sizing: border-box;
    &:not(:last-child) {
      border-bottom: none;
    }
    p {
      margin: 0;
      line-height: 40px;
      padding-left: 10px;
      background: #e5f4fc;
    }
  }
  .children-option {
    padding: 10px 0 0 25px;
    .option-item {
      display: block;
      line-height: 30px;
    }
  }
  .option-item-text {
    border-bottom: 1px solid #d8d8d8;
    padding: 20px 10px 5px;
    color: #333;
    margin: 0 25px 0;
  }
  .children-analysis {
    line-height: 25px;
    padding-left: 20px;
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
  height: calc(100% - 80px);
  background: #fff;
  position: fixed;
  top: 70px;
  right: 20px;
  z-index: 100;
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
    background: #0094e5;
    color: #fff;
    line-height: 14px;
    line-height: 40px;
    padding: 0 10px;
  }
}

/deep/ .el-collapse-item__header {
  background: #f1f1f1;
  color: #333;
  padding: 0 10px;
}

.route-href {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  width: 100%;
  padding: 10px 15px 0;
  .href-item {
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
  }
  .href-item-active {
    span {
      border: 1px solid #0094e5;
      color: #0094e5;
    }
  }
}

.data-null {
  padding-top: 30px;
  .data-img {
    width: 64px;
    height: 64px;
  }
  .data-tip {
    margin: 0 auto 20px;
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
