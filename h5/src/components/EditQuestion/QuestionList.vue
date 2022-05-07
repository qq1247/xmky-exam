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
    <template v-if="list.questionList.length > 0">
      <template v-if="!preview">
        <el-card
          v-for="question in list.questionList"
          :key="question.id"
          :class="[
            'center-card',
            question.id === id ? 'center-card-active' : '',
          ]"
          shadow="hover"
          @click.native="showDetails(question.id)"
        >
          <div class="center-card-top">
            <span>{{ question.id }}、</span>
            <div class="render-title" v-html="`${question.title}`" />
          </div>
          <!-- 编辑模式 -->
          <div class="center-card-bottom">
            <div class="card-bottom-left">
              <el-tag class="center-tag-danger" size="mini" type="danger">{{
                question.type | typeName
              }}</el-tag>

              <el-tag class="center-tag-purple" effect="plain" size="mini">{{
                question.difficulty | difficultyName
              }}</el-tag>

              <el-tag effect="plain" size="mini" type="warning">{{
                ['', '智能', '非智能'][question.ai]
              }}</el-tag>

              <el-tag
                effect="plain"
                size="mini"
                type="danger"
              >{{ question.score }}分</el-tag>

              <el-tag effect="plain" size="mini">{{
                question.createUserName
              }}</el-tag>

              <el-tag
                :type="question.state === 1 ? 'info' : 'danger'"
                effect="plain"
                size="mini"
              >{{ question.state === 1 ? '发布' : '草稿' }}</el-tag>
            </div>
            <div class="card-bottom-right">
              <el-button
                class="btn"
                size="mini"
                type="primary"
                icon="el-icon-document"
                @click.stop="questionEdit(question.id, question.type)"
              >修改</el-button>
              <el-button
                class="btn"
                size="mini"
                type="primary"
                icon="el-icon-document-copy"
                @click.stop="copy(question.id)"
              >复制</el-button>
              <template v-if="question.state !== 0">
                <el-button
                  class="btn"
                  size="mini"
                  type="danger"
                  icon="el-icon-delete"
                  @click.stop="del(question.id)"
                >删除</el-button>
              </template>
              <template v-if="question.state === 2">
                <el-button
                  plain
                  round
                  class="btn"
                  size="mini"
                  type="primary"
                  icon="el-icon-share"
                  @click.stop="publish(question.id, question.state)"
                >发布</el-button>
              </template>
            </div>
          </div>
        </el-card>
      </template>
      <div v-else>
        <div
          v-for="(question, indexQuestion) in list.questionList"
          :id="`p-${question.id}`"
          :key="question.id"
          :class="[
            'question-content',
            question.type === 5 ? 'ask-content' : '',
          ]"
        >
          <div class="question-title">
            <span>{{ indexQuestion + 1 }}、</span>
            <div v-html="`${question.title}`" />
          </div>

          <!-- 单选 -->
          <template v-if="question.type === 1">
            <el-radio-group v-model="question.answers" class="children-option">
              <el-radio
                v-for="(option, indexOption) in question.options"
                :key="indexOption"
                class="option-item"
                :label="String.fromCharCode(65 + index)"
              >
                <div
                  class="flex-items-center"
                  v-html="`${String.fromCharCode(65 + index)}、${option}`"
                />
              </el-radio>
            </el-radio-group>
          </template>

          <!-- 多选 -->
          <template v-if="question.type === 2">
            <el-checkbox-group
              v-model="question.answers"
              class="children-option"
            >
              <el-checkbox
                v-for="(option, indexOption) in question.options"
                :key="indexOption"
                class="option-item"
                :label="String.fromCharCode(65 + index)"
              >
                <div
                  class="flex-items-center"
                  v-html="`${String.fromCharCode(65 + index)}、${option}`"
                />
              </el-checkbox>
            </el-checkbox-group>
          </template>

          <!-- 判断 -->
          <template v-if="question.type === 4">
            <el-radio-group v-model="question.answer" class="children-option">
              <el-radio
                v-for="(option, indexOption) in ['对', '错']"
                :key="indexOption"
                class="option-item"
                :label="option"
              >{{ option }}</el-radio>
            </el-radio-group>
          </template>

          <!-- 问答 -->
          <template v-if="question.type === 5">
            <el-input
              v-model="question.answer"
              :rows="2"
              class="question-text"
              placeholder="请输入内容"
              type="textarea"
            />
          </template>
        </div>
      </div>
    </template>
    <el-empty v-else description="暂无试题">
      <img slot="image" src="@/assets/img/data-null.png" alt="">
    </el-empty>
    <div class="footer">
      <!-- 分页 -->
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="list.total"
        :page-size="list.pageSize"
        :current-page="list.curPage"
        @current-change="pageChange"
      />
    </div>
  </div>
</template>

<script>
import { getOneDict } from '@/utils/getDict'
export default {
  components: {},
  filters: {
    typeName(data) {
      return getOneDict('QUESTION_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    difficultyName(data) {
      return getOneDict('QUESTION_DIFFICULTY').find(
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
    }
  },
  data() {
    return {
      preview: false,
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
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';

.question-content {
  &:first-child {
    padding-top: 50px;
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
    background: #0095e5;
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
    background: #0095e5;
    color: #fff;
    img {
      content: url('~@/assets/img/question/question-edit-active.png');
    }
  }
  .view-active {
    background: #0095e5;
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
    border: 1px solid #0095e5;
    .card-bottom-right {
      display: block;
    }
  }
  &:nth-of-type(2) {
    margin-top: 50px;
  }
}

.center-card-active {
  border: 1px solid #0095e5;
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
    padding: 5px 10px;
  }
}

/deep/ .el-radio__input .el-radio__inner,
/deep/ .el-checkbox__input .el-checkbox__inner {
  margin-top: 8px;
}

.footer {
  background: #fff;
  width: 100%;
  height: 40px;
  color: #333;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding-left: 16px;
  display: flex;
  align-items: center;
}
</style>
