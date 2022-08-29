<template>
  <div class="content">
    <div class="paper-router">
      <el-divider >答题卡</el-divider>
      <div class="router-content">
        <div class="router-link">
          <a 
          v-for="(question, index) in questionList"
          :key="index"
          class="router-index router-active" 
          @click="toHref(question.id)"
          >
          {{index + 1}}
          </a>
        </div>
      </div>
      <!-- <div
        v-for="(item, index) in paperQuestion"
        :key="index"
        class="router-content"
      >
        <div v-if="item.questionList" class="router-title">
          第{{ $tools.intToChinese(index + 1) }}章（共{{
            item.questionList.length
          }}题，合计{{ computeChapterScore(item.questionList) }}分）
        </div>
        <div v-if="item.questionList" class="router-link">
          <a
            v-for="(question, indexQuestion) in item.questionList"
            :key="question.id"
            :class="[
              'router-index',
              routerIndex === question.id ? 'router-active' : '',
            ]"
            @click="toHref(question.id)"
          >{{ indexQuestion + 1 }}</a>
        </div>
      </div> -->
    </div> 

    <el-scrollbar wrap-style="overflow-x:hidden;" class="paper-content">
      <div class="top">
        <div class="top-title">
          <el-input v-model="paperName" placeholder="请输入试卷名称" maxlength="32"></el-input>
        </div>
        <div class="top-handler"> 
          <!-- 编辑、预览模式 -->
          <el-tooltip class="item" effect="dark" content="选择题库" placement="top">
            <el-button icon="el-icon-zoom-in" size="mini" round @click="drawer=true">选择题库</el-button>
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="即时编辑试题，快速导入" placement="top">
            <el-button icon="el-icon-edit-outline" size="mini" round @click="toEditor">导入试题</el-button>
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="添加章节" placement="top">
            <el-button icon="el-icon-plus" size="mini" round>添加章节</el-button>
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="清空试题" placement="top">
            <el-button icon="el-icon-delete" size="mini" round>清空试题</el-button>
          </el-tooltip>
          <!-- <el-button icon="el-icon-edit" size="mini" round></el-button>
          <el-button icon="el-icon-edit" size="mini" round>添加章节</el-button>
          <el-button icon="el-icon-edit" size="mini" round>清空试题</el-button> -->
          <!-- <div class="type">
            <div
              class="type-item"
              :class="!preview ? 'edit-active' : ''"
              title="编辑模式"
              @click="setType(false)"
            >
              <img src="@/assets/img/question/question-edit.png" alt="">
            </div>
            <div
              class="type-item"
              :class="preview ? 'view-active' : ''"
              title="预览模式"
              @click="setType(true)"
            >
              <img src="@/assets/img/question/question-view.png" alt="">
            </div>
          </div> -->
        </div>
      </div>
      <Composition
        v-if="!preview"
        ref="paperComposition"
        :questionList="questionViewList"
      />
      <Preview v-if="preview" ref="paperPreview" />
      <slot></slot>
    </el-scrollbar>
    <el-drawer
      title="题库"
      :visible.sync="drawer"
      direction="rtl"
      size="500px"
      :modal-append-to-body="false"
      :modal="false"
      >
      <QuestionList 
        @addQuestion="addQuestion"
        @toHref="toHref"
      />
    </el-drawer>
  </div>
</template>
<script>
// import Preview from '@/views/paper/List/Edit/RulePaper/Content/Preview.vue'
import Composition from './Composition.vue'
import QuestionList from './QuestionList.vue'
import { getQuick } from '@/utils/storage'
import { questionListPage } from 'api/question'
export default {
  components: {
    // Preview,
    Composition,
    QuestionList
  },
  data() {
    return {
      preview: false,
      paperId: 0,
      paperState: 2,
      paperTypeId: 0,
      markType: 1,
      paperName: '这里是试卷名称',
      drawer: false, // 抽题默认不显示
      questionList: []
    }
  },
  async created() {
    // const res = await questionListPage({}) 
    // if (res?.code === 200) {
    //   this.questionList = res.data.list
    // } else {
    //   this.$message.error('请刷新重新获取试题！')
    // }
  },
  computed: {
    questionViewList: function () {
      let _questionList = JSON.parse(JSON.stringify(this.questionList)).map(question => {//answers: [{score: 1, answer: ["B"]}] => answers: B
        if (question.type === 1 || question.type === 4 || (question.type === 5 && question.markType === 2)) {
          question.answers = question.answers[0].answer[0]
        } else if (question.type === 2) {
          question.answers = question.answers[0].answer
        } else if (question.type === 3 || (question.type === 5 && question.markType === 1)) {
            question.answers = question.answers.map(answer => {
            return answer.answer[0]
          })
        }
        return question
      });
      return _questionList
    }
  },
  methods: {
    toEditor() {
      this.$emit('toEditor')
    },
    addQuestion(question) {
      this.questionList.push(question)
    },
    // 跳转到对应的试题
    toHref(index) {
      document.querySelector(`#p-${index}`).scrollIntoView({ block: 'end', inline: 'nearest' })
    }
  }
}
</script>

<style lang="scss" scoped>
.paper-router {
  width: 240px;
  background: #fff;
  position: relative;
  padding: 15px;
  .total-score {
    background: #0094e5;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #fff;
    text-align: center;
    position: absolute;
    top: 40px;
    left: 0;
    z-index: 100;
  }
}
.router-content {
  padding: 10px;
  .router-title {
    line-height: 40px;
    font-size: 14px;
    font-weight: 600;
    color: #0c2e41;
  }

  .router-index {
    position: relative;
    width: 28px;
    height: 28px;
    color: #41baff;
    line-height: 28px;
    font-weight: 600;
    background: #E3F4FE;
    text-align: center;
    display: inline-block;
    margin-bottom: 10px;
    margin-right: 10px;
    border-radius: 3px;
    text-decoration: none;
    border-radius: 6px;
    cursor: pointer;
    user-select: none;
    &:nth-child(10n) {
      margin-right: 0;
    }
  }

  .router-active, a:hover {
    background: #0094E4;
    // border: 1px solid #7fc2f6;
    color: #fff;
  }
}
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 240px);
  margin: 0 auto;
}
.paper-handler {
  width: 400px;
  background: #fff;
  position: relative;
  border-radius: 8px;
  .paper-title {
    line-height: 40px;
    background: #fff;
    width: 100%;
    height: 40px;
    font-size: 16px;
    color: #333;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    font-weight: 600;
    padding: 0 20px;
    border-radius: 8px 8px 0 0;
    border-bottom: 1px solid #eee;
    &::before{
      content: '';
      display: inline-block;
      position: relative;
      top: 5px;
      left: -10px;
      width: 2px;
      height: 20px;
      background: #0094e5;
    }
  }
}

.top {
  background: #fff;
  width: calc(1200px - 410px);
  height: 80px;
  padding: 30px 10px 10px 10px;
  color: #333;
  position: fixed;
  z-index: 100;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  border-radius: 8px 8px 0 0;
  .top-handler {
    display: none;
    position: absolute;
    right: 10px;
    top: 10px;
    /deep/ .el-button {
      padding: 5px 10px;
    }
  }
  .top-title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    vertical-align: middle;
    /deep/ .el-input__inner {
      text-align: center;
      border: 0;
      font-size: 18px;
      font-weight: bold;
    }
  }
  
}
.type {
  display: flex;
  justify-content: flex-end;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-right: 16px;
  .type-item {
    width: 20px;
    height: 20px;
    line-height: 20px;
    text-align: center;
    cursor: pointer;
    &:first-child {
      border-radius: 2px 0 0 2px;
    }
    &:last-child {
      border-radius: 0 2px 2px 0;
    }
  }
  .edit-active {
    background: #0094e5;
    color: #fff;
    img {
      content: url('~@/assets/img/question/question-edit-active.png');
    }
  }
  .view-active {
    background: #0094e5;
    color: #fff;
    img {
      content: url('~@/assets/img/question/question-view-active.png');
    }
  }
}
/deep/ .el-drawer__header {
  margin-bottom: 0px;
}
.el-drawer__wrapper {
  top: 160px;
}
.handler-content {
  padding: 10px 10px 10px;
}
</style>
<style lang="scss">
.paper-content {
  background: #fff;
  width: calc(100% - 400px);
  margin-left: 10px;
  border-radius: 8px;
  &:hover {
    .top {
      .top-handler {
        display: inline-block;
      }
    }
  }
}
</style>