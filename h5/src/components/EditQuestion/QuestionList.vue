<template>
  <div>
    <!-- top -->
    <div v-if="showPotic" class="top">
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
          @click.native="showDetails(question.id, question.type)"
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

              <el-tag effect="plain" size="mini" type="success">{{
                ['', '客观题', '主观题'][question.markType]
              }}</el-tag>

              <el-tag
                effect="plain"
                size="mini"
                type="danger"
              >{{ question.score }}分</el-tag>

              <el-tag effect="plain" size="mini" type="info">{{
                question.createUserName
              }}</el-tag>

              <el-tag
                :type="question.state === 1 ? 'info' : 'primary'"
                effect="plain"
                size="mini"
              >{{['回收站', '发布', '草稿'][question.state]}}</el-tag>
            </div>
            <div class="card-bottom-right">
              <el-button
                class="btn"
                size="mini"
                type="primary"
                @click.stop="questionEdit(question.id, question.type)"
              >
                <img
                  src="@/assets/img/question/question-update.png"
                  alt=""
                >修改
              </el-button>
              <el-button
                class="btn"
                size="mini"
                type="primary"
                @click.stop="copy(question.id)"
              ><img
                src="@/assets/img/question/question-copy.png"
                alt=""
              >复制</el-button>
              <template v-if="question.state !== 0">
                <el-button
                  class="btn"
                  size="mini"
                  type="danger"
                  @click.stop="del(question.id)"
                ><img
                  src="@/assets/img/question/question-delete.png"
                  alt=""
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
            showErr ? 'question-content remove-top' : 'question-content',
            question.type === 5 ? 'ask-content' : '',
          ]"
        >
          <!-- 错误提示 -->
          <template v-if="showErr && question.errs.length != 0">
            <div class="err-msg">
              <i class="el-icon-warning err-icon"></i>
              <span class="err-tip">{{ question.errs }}</span>
            </div>
          </template>
          <!-- 题目 -->
          <div v-if="question.type !== 3" class="question-title">
            <span>{{ indexQuestion + 1 }}、</span>
            <div v-html="`${question.title}`" />
          </div>
          <ClozeTitle 
            v-else
            :no="indexQuestion + 1"
            :title="question.title"
            :answers="question.answers"
            :id="question.id"
            :err="false"
            :preview="true"
          />
          

          <!-- 单选 -->
          <template v-if="question.type === 1">
            <el-radio-group v-model="question.answers" class="children-option">
              <el-radio
                v-for="(option, indexOption) in question.options"
                :key="indexOption"
                class="option-item"
                :label="String(option.no)"
              >
                <div
                  class="flex-items-center"
                  v-html="
                    `${String.fromCharCode(65 + indexOption)}、${option.option}`
                  "
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
                :label="String(option.no)"
              >
                <div
                  class="flex-items-center"
                  v-html="
                    `${String.fromCharCode(65 + indexOption)}、${option.option}`
                  "
                />
              </el-checkbox>
            </el-checkbox-group>
          </template>

          <!-- 判断 -->
          <template v-if="question.type === 4">
            <el-radio-group v-model="question.answers" class="children-option">
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
              v-model="question.answers"
              :rows="2"
              class="question-text"
              placeholder="请输入答案"
              type="textarea"
              :autosize="true"
            />
          </template>
        </div>
      </div>
    </template>
    <el-empty v-else description="暂无试题">
      <img slot="image" src="@/assets/img/data-null.png" alt="">
    </el-empty>
    <div v-if="showPotic" class="footer">
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
import ClozeTitle from '../ClozeTitle.vue'
export default {
  components: {
    ClozeTitle
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
