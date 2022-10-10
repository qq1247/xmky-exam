<template>
  <!-- 试题简单样式 -->
  <el-card v-if="showMode === 'simple'" :class="'center-card'" shadow="hover">
    <div class="center-card-top">
      <span>{{ no }}、</span>
      <div class="render-title" v-html="`${question.title}`" />
    </div>
    <div class="center-card-bottom">
      <div class="card-bottom-left">
        <el-tag class="center-tag-danger" size="mini" type="danger">
          {{ $tools.getDictValue('QUESTION_TYPE', question.type) }}
        </el-tag>
        <el-tag effect="plain" size="mini" type="success">
          {{ $tools.getDictValue('PAPER_MARK_TYPE', question.markType) }}
        </el-tag>
        <el-tag
          :type="question.state === 1 ? 'info' : 'primary'"
          effect="plain"
          size="mini"
        >
          {{ $tools.getDictValue('STATE', question.state) }}
        </el-tag>
        <el-tag effect="plain" size="mini" type="danger">
          {{ question.score }}分
        </el-tag>
      </div>
    </div>
  </el-card>
  <!-- 试题详细样式 -->
  <div v-else-if="showMode === 'detail'" class="question-content">
    <el-alert 
      v-if="question.errs && question.errs.length > 0" 
      :title="question.errs" 
      type="error" 
      effect="dark" 
      :closable="false"
      style="margin-bottom: 10px;padding: 4px 16px;"></el-alert>
    <!-- 题干 -->
    <QuestionTitle
      :type="question.type"
      :no="no"
      :title="question.title"
      :score="question.score"
      :answers="question.answers"
      :readOnly="true"
    />
    <!-- 单选题选项 -->
    <el-radio-group
      v-if="question.type === 1"
      v-model="question.answers[0]"
      class="children-option"
      :disabled="true"
    >
      <el-radio
        v-for="(option, index) in question.options"
        :key="`option${optionLabs[index]}`"
        class="option-item"
        :label="`${optionLabs[index]}`"
      >
        <div
          class="flex-items-center"
          v-html="`${optionLabs[index]}、${option}`"
        />
      </el-radio>
    </el-radio-group>
    <!-- 多选题选项 -->
    <el-checkbox-group
      v-else-if="question.type === 2"
      v-model="question.answers"
      class="children-option"
      :disabled="true"
    >
      <el-checkbox
        v-for="(option, index) in question.options"
        :key="`option${optionLabs[index]}`"
        class="option-item"
        :label="`${optionLabs[index]}`"
      >
        <div
          class="flex-items-center"
          v-html="`${optionLabs[index]}、${option}`"
        />
      </el-checkbox>
    </el-checkbox-group>
    <!-- 填空题（题干区域） -->
    <!-- 判断题选项 -->
    <el-radio-group
      v-else-if="question.type === 4"
      v-model="question.answers[0]"
      class="children-option"
      :disabled="true"
    >
      <el-radio
        v-for="(option, index) in ['对', '错']"
        :key="index"
        class="option-item-inline"
        :label="option"
        >{{ option }}</el-radio
      >
    </el-radio-group>
    <!-- 问答题答案 -->
    <el-input
      v-else-if="question.type === 5 && question.markType === 2"
      v-model="question.answers[0]"
      :rows="2"
      class="ask-content"
      placeholder="请输入答案"
      type="textarea"
      :autosize="true"
      :disabled="true"
    />
    <el-input
      v-else-if="question.type === 5 && question.markType === 1"
      :value="qaAnswer"
      :rows="2"
      class="ask-content"
      placeholder="请输入答案"
      type="textarea"
      :autosize="true"
      :disabled="true"
    />
    <slot name="bottom"></slot>
  </div>
</template>

<script>
import QuestionTitle from './QuestionTitle.vue'
export default {
  components: {
    QuestionTitle,
  },
  props: {
    no: {// 显示排序序号
      type: [String, Number],
      default: 0,
    },
    question: { // 试题对象
      type: Object,
      default: () => {},
    },
    showMode: {// 显示样式
      type: String,
      default: 'detail',
    },
  },
  data() {
    return {
      optionLabs: ['A','B','C','D','E','F','G'],
      judgeLabs: ['对','错']
    }
  },
  computed: {
    qaAnswer: function() {
      if (this.question.type === 5 && this.question.markType === 1) {// 如果是客观问答，格式化答案
        let answer = ''
        this.question.answers.forEach((cur, index) => {
          answer += `关键词${index + 1}：`
          cur.forEach(cur => {
            answer += cur + " "
          });
          if (index < this.question.answers.length - 1) {
            answer += '\n'
          }
        });
        return answer
      }

      return null
    }
  },
  methods: {

  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
.remove-top {
  &:first-child {
    margin-top: 0;
  }
}

/deep/ .el-textarea__inner {
  border: none;
  color: #557587;
}

.top {
  background: #fff;
  width: 100%;
  height: 40px;
  color: #333;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  font-weight: 600;
  padding-left: 30px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;

  &::before {
    content: '';
    display: inline-block;
    position: relative;
    top: 14px;
    left: -10px;
    width: 2px;
    height: 14px;
    background: #0094e5;
  }

  .top-title {
    flex: 1;
    line-height: 40px;
  }

  .top-handler {
    display: flex;
    align-items: center;
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

.center-card {
  cursor: pointer;
  margin: 0 10px 10px 10px;
  display: flex;
  flex-direction: column;

  /deep/ .el-card__body {
    padding: 10px 15px;
  }

  &:first-child {
    margin: 10px;
  }
  // &:hover {
  //   border: 1px solid #0094e5;
  //   background: rgba(#0094e5, 0.06%);
  // }
}

.center-card-active {
  border: 1px solid #0094e5;
}

.center-card-top {
  font-size: 14px;
  text-align: left;
  display: flex;

  .render-title {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  p {
    margin: 0;
    padding: 0;
  }
}

.center-card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 30px;
  margin: 10px 0 -5px;

  .el-tag {
    margin-right: 8px;
  }

  .center-tag-danger {
    background-color: #feeddc;
    border-color: #fddbb9;
    color: #fa8718;
    height: 20px;
    line-height: 18px;
  }

  .center-tag-purple {
    border-color: #daddf9;
    color: #838eea;
    height: 20px;
    line-height: 18px;
  }

  .btn {
    padding: 3px 8px;

    img {
      margin-right: 4px;
      vertical-align: -1px;
    }
  }
}
.err-msg {
  background-color: #f00;
  line-height: 22px;
  word-wrap: break-word;
  font-weight: 600;
  color: #fff;
  padding: 0 10px;
  border-radius: 5px;
}
.err-tip {
  display: inline-block;
  text-indent: 2em;
}

</style>
