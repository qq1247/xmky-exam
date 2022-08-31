<template>
  <div>
    <!-- top -->
    <div class="top">
      <div class="top-title">试题列表</div>
      <div class="top-handler">
        <!-- 编辑、预览模式 -->
        <div class="type">
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
        </div>
      </div>
    </div>
    <div 
      v-for="(question, index) in list.questionList"
      :key="question.id">
      <Question :question="question" :no="index + 1" :showType="'simple'"></Question>
    </div>
    <el-empty v-if="list.questionList.length === 0" description="没有更多试题了">
      <img slot="image" src="@/assets/img/data-null.png" alt="">
    </el-empty>
    <slot name="bottom"></slot>
  </div>
</template>

<script>
import Question from './Question.vue'
export default {
  components: {
    Question
  },
  filters: {
    typeName(data) {
      return getOneDict('QUESTION_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    }
  },
  props: {
    list: {
      type: Object,
      default: () => {}
    },
    id: {
      type: Number,
      default: null
    },
    preview: {
      type: Boolean,
      default: false
    },
    showPotic: {
      type: Boolean,
      default: true
    },
    showErr: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // preview: false,
      checkBoxOption: []
    }
  },
  methods: {
    setType(e) {
      this.preview = e
    },
    pageChange(curPage) {
      this.$emit('pageChange', curPage)
    },
    showDetails(id) {
      this.$emit('showDetails', id)
    },
    questionEdit(id, type) {
      this.$emit('questionEdit', { id, type })
    },
    copy(id) {
      this.$emit('copy', id)
    },
    del(id) {
      this.$emit('del', id)
    },
    publish(id, state) {
      this.$emit('publish', id, state)
    },
    updateAnswer(id) {
      
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';

.question-content {
  &:first-child {
    margin-top: 50px;
  }
}
.remove-top {
  &:first-child {
    margin-top: 0;
  }
}

/deep/ .el-textarea__inner {
  border: none;
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

  &:hover {
    border: 1px solid #0094e5;
    background: rgba(#0094e5, 0.06%);

    .card-bottom-right {
      display: block;
    }
  }

  &:nth-of-type(2) {
    margin-top: 50px;
  }
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

  .card-bottom-right {
    display: none;
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
